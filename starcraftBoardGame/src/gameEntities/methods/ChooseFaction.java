package gameEntities.methods;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;

/**méthodes gérant le choix de la faction par les joueurs**/
public class ChooseFaction {
	
	public void updateCardNumber(StarcraftPlayer player){
		try {
			JSONObject updateCardNumber = new JSONObject()
					.put("action", "updateCardNumber")
					.put("cardNumber", player.getCombatCardsInHand().size());
			GlobalMethods.sendPlayerAction(player.getName(), updateCardNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**affiche les choix possible de faction**/
	public void printFactionChoice(String playerName, StarcraftGame game){

		//enlève l'ancien affichage des choix possibles de faction
		GlobalMethods.clearByClass(playerName, "factionChoices");
		// fait la liste de toutes les factions déjà choisies
		Set<String> chosenFactionList = new HashSet<String>();
		for (String player:game.getPlayerList().keySet()){
			if (game.getPlayerList().get(player).getFaction()!=null){
				String factionAdded = game.getPlayerList().get(player).getFaction();
				chosenFactionList.add(factionAdded);
			}
		}
		try {
			URL resources = getClass().getClassLoader().getResource("../../starcraftResources/factionChoices.xml");
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("faction");
			for (int i = 0; i < nList.getLength(); i++){
				Element node = (Element) nList.item(i);
				String factionName = node.getAttribute("name");
				String speciesName = node.getAttribute("species");
				String image = node.getAttribute("image");
				String factionColor = node.getAttribute("color");
				if (!chosenFactionList.contains(factionName)){
					JSONObject factionChoice = new JSONObject()
							.put("action", "printFactionChoice")
							.put("factionName", factionName)
							.put("speciesName", speciesName)
							.put("image", image)
							.put("factionColor", factionColor);
					GlobalMethods.sendPlayerAction(playerName, factionChoice);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**affiche les différentes factions choisies par les joueurs dans la barre indiquant les tours**/
	public void printChosenFactions(String player, StarcraftGame game){
		GlobalMethods.clearByClass(player, "activeFactionChoices");

		for (String playerName : game.getTurnList()){			
			printChosenFactionByPlayer(player, game, playerName);
		}

	}
	

	/**affiche une faction choisie par un joueur dans la barre indiquant les tours**/
	public void printChosenFactionByPlayer(String player, StarcraftGame game, String choosingPlayer){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(choosingPlayer);
		if (starcraftPlayer.getFaction()!=null){
			String factionName = starcraftPlayer.getFaction();
			String speciesName = starcraftPlayer.getSpecies();
			String factionImage = starcraftPlayer.getFactionImage();
			try {
				JSONObject factionChoice = new JSONObject()
						.put("action", "playerFactionChoice")
						.put("playerName", choosingPlayer)
						.put("factionName", factionName)
						.put("speciesName", speciesName)
						.put("factionImage", factionImage);
				GlobalMethods.sendPlayerAction(player, factionChoice);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**affiche la partie 'faction' du champs d'information('your game board') sur la situation du joueur**/
	public void printPlayerFactionInfo(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		if (starcraftPlayer.getFaction()!=null){
			String factionName = starcraftPlayer.getFaction();
			String speciesName = starcraftPlayer.getSpecies();
			String factionImage = starcraftPlayer.getFactionImage();
			String factionColor = starcraftPlayer.getPlayerColor();
			try {
				JSONObject factionChoice = new JSONObject()
						.put("action", "playerFactionInfo")
						.put("playerName", player)
						.put("factionName", factionName)
						.put("speciesName", speciesName)
						.put("factionImage", factionImage)
						.put("factionColor", factionColor);
				GlobalMethods.sendPlayerAction(player, factionChoice);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**Ajoute à la partie info un indicateur des cartes de combats piochées**/
	public void printCombatCardInfo(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		if (starcraftPlayer.getSpecies()!=null){
			try {
				JSONObject factionChoice = new JSONObject()
						.put("action", "displayCardNumber")
						.put("color", starcraftPlayer.getPlayerColor())
						.put("cardNumber", starcraftPlayer.getCombatCardsInHand().size());
				GlobalMethods.sendPlayerAction(player, factionChoice);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**Réception des actions possibles du joueur dans l'étape de sélection des factions**/
	
	/**le joueur choisis la faction**/
	public void choosePlayerFaction(String playerName, String speciesName, String factionName, StarcraftGame game){
		StarcraftPlayer activeplayer= game.getPlayer(playerName);
		activeplayer.setSpecies(speciesName);
		activeplayer.setFaction(factionName);
		//trouve l'image correspondant à la faction
		String image = "";
		String playerColor = "";
		try {
			URL resources = getClass().getClassLoader().getResource("../../starcraftResources/factionChoices.xml");
	    	File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("faction");
			int i = 0;
			while (i < nList.getLength() && image.equals("")){
				Element node = (Element) nList.item(i);
				String nodeFactionName = node.getAttribute("name");
				if (factionName.equals(nodeFactionName)){
					image = node.getAttribute("image");
					playerColor = node.getAttribute("color");
				}else{
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		activeplayer.setFactionImage(image);
		activeplayer.setPlayerColor(playerColor);
		activeplayer.initializeCombatCardDeck(game.cardIdGenerator);
		//les actions du joueurs actifs sont terminées, on passe donc au tout suivant
		game.nextTurn();
		
		// on affiche la faction choisie par le joueur
		printPlayerFactionInfo(playerName, game);
		printCombatCardInfo(playerName, game);
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			if (game.getCurrentTurnNumber() == 0){
				//enlève l'ancien affichage des choix possibles de faction
				GlobalMethods.clearByClass(player, "factionChoices");
			}else{
				//on enlève la faction choisie des choix possibles
				//(les noms des attributs sont en minuscules car ils sont en minuscules dans le html)
				GlobalMethods.deleteElement(player, "//div[@class = 'factionChoices' "
						+ "and @speciesname='"+ speciesName +"' and"
						+ " @factionname='"+ factionName +"']");
			}
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}


	public void displayCombatCards(String playerName, StarcraftGame game) {
		StarcraftPlayer activeplayer= game.getPlayer(playerName);
		for (int combatCardId:activeplayer.getCombatCardsInHand().keySet()){
			JSONObject combatCardJS = activeplayer.getCombatCardsInHand().get(combatCardId).getCardJS("displayCardInHand");
			GlobalMethods.sendPlayerAction(playerName, combatCardJS);
		}
	}

}
