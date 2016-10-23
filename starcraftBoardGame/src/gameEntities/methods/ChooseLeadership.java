package gameEntities.methods;

import java.io.File;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;

public class ChooseLeadership {


	//Affiche les choix possibles de cartes de leadership
	public void printLeadershipChoice(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);

		try {
			URL resources = getClass().getClassLoader().getResource("../../starcraftResources/leadershipCards.xml");
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/root/faction[@name =\'"+ starcraftPlayer.getFaction() +"\']"
					+ "/card[@age = \'"+ Integer.toString(game.getAge()) +"\']");
			Object o = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList cardlist = (NodeList) o;


			for (int j = 0; j < cardlist.getLength(); j++){
				Element card = (Element) cardlist.item(j);
				String cardName = card.getAttribute("name");
				String text = card.getElementsByTagName("text").item(0).getTextContent();
				JSONObject leaderShipChoice;
				//envoie les informations nécessaires pour afficher les choix de cartes
				if (game.getAge() == 1){
					// si on est au premier âge, on donne aussi les unités de départ
					NodeList itemList = card.getElementsByTagName("itemBatch");
					JSONArray itemArray = new JSONArray();
					for (int k = 0; k < itemList.getLength(); k++){
						Element item = (Element) itemList.item(k);
						JSONObject itemJS = new JSONObject()
								.put("number", item.getAttribute("number"))
								.put("name", item.getAttribute("name"));
						itemArray.put(itemJS);
					}

					leaderShipChoice = new JSONObject()
							.put("action", "printLeadershipChoice")
							.put("cardName", cardName)
							.put("text", text)
							.put("itemList", itemArray);
				}else{
					leaderShipChoice = new JSONObject()
							.put("action", "printLeadershipChoice")
							.put("cardName", cardName)
							.put("text", text);
				}
				GlobalMethods.sendPlayerAction(player, leaderShipChoice);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//affiche la partie 'carte de leadership' du champs d'information('your game board') sur la situation du joueur
	public void printPlayerLeadershipInfo(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		if (!starcraftPlayer.getLeadershipcards().isEmpty()){
			for (String leadershipCardName : starcraftPlayer.getLeadershipcards()){
				URL resources = getClass().getClassLoader().getResource("../../starcraftResources/leadershipCards.xml");
				try {
					File fXmlFile = new File(resources.toURI());
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(fXmlFile);
					doc.getDocumentElement().normalize();

					XPath xpath = XPathFactory.newInstance().newXPath();
					XPathExpression expr = xpath.compile("/root/faction/card[@name =\""+ leadershipCardName +"\"]");
					Object o = expr.evaluate(doc, XPathConstants.NODESET);
					NodeList list = (NodeList) o;
					Element card = (Element) list.item(0);
					String cardName = card.getAttribute("name");
					String text = card.getElementsByTagName("text").item(0).getTextContent();
					JSONObject leaderShipChoice;
					//envoie les informations nécessaires pour afficher les choix de cartes
					if (game.getAge() == 1){
						// si on est au premier âge, on donne aussi les unités de départ
						NodeList itemList = card.getElementsByTagName("itemBatch");
						JSONArray itemArray = new JSONArray();
						for (int k = 0; k < itemList.getLength(); k++){
							Element item = (Element) itemList.item(k);
							JSONObject itemJS = new JSONObject()
									.put("number", item.getAttribute("number"))
									.put("name", item.getAttribute("name"));
							itemArray.put(itemJS);
						}

						leaderShipChoice = new JSONObject()
								.put("action", "playerLeadershipInfo")
								.put("cardName", cardName)
								.put("text", text)
								.put("itemList", itemArray);
					}else{
						leaderShipChoice = new JSONObject()
								.put("action", "playerLeadershipInfo")
								.put("cardName", cardName)
								.put("text", text);
					}
					GlobalMethods.sendPlayerAction(player, leaderShipChoice);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/**Réception des actions possibles du joueur dans l'étape de sélection des cartes de leadership**/
	// Un joueur choisit une carte de leadership
	public void chooseLeadershipCard(String playerName, String leadershipCardName, StarcraftGame game) {
		StarcraftPlayer activePlayer= game.getPlayer(playerName);
		activePlayer.addLeadershipcards(leadershipCardName, game);
		
		//les actions du joueurs actifs sont terminées, on passe donc au tout suivant
		game.nextTurn();
		
		//on met à jour les informations du joueur
		printPlayerLeadershipInfo(playerName, game);
		GameTurnHandler.callDisplayPlayerResourceInfo(playerName, game);
		GameTurnHandler.callDisplayBaseWorkers(playerName, game);
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			if (game.getCurrentTurnNumber() == 0){
				//enlève l'ancien affichage des choix possibles de leadership
				GlobalMethods.clearByClass(player, "leadershipChoice");
			}
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

}
