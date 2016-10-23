package gameEntities.methods;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;

public class GalaxyOrderChoice {

	
	/**ajoute le bouton de choix des ordres**/
	public void addGalaxyOrderChoiceButton(String player, StarcraftGame game){
		try {
			JSONObject addGalaxyOrderChoiceButton = new JSONObject()
					.put("action", "addGalaxyOrderChoiceButton");
			GlobalMethods.sendPlayerAction(player, addGalaxyOrderChoiceButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**ajoute le bouton de choix d'activation des ordres**/
	public void addActivationChoiceButton(String player, StarcraftGame game){
		try {
			JSONObject addActivationChoiceButton = new JSONObject()
					.put("action", "addActivationChoiceButton")
					.put("orderName", game.getCurrentOrder().getName())
					.put("planetName", game.getGalaxy().getPlanetEvent());
			GlobalMethods.sendPlayerAction(player, addActivationChoiceButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**active les cases valides**/
	public void activateValidSquareOrders(String player, StarcraftGame game){
		try {
			if (game.getPlayerCurrentlyPlaying().equals(player)){
				ArrayList<String> orderCoordinates = game.getGalaxy().getAllValidOrdersCoordinates(player);
				JSONArray coordinateArray = new JSONArray();
				for (String coordinate:orderCoordinates){
					JSONObject coordinateJS = new JSONObject()
							.put("coordinate", coordinate);
					coordinateArray.put(coordinateJS);
				}
				JSONObject activateValidSquareOrders = new JSONObject()
						.put("action", "activateValidSquareOrders")
						.put("coordinates", coordinateArray);
				GlobalMethods.sendPlayerAction(player, activateValidSquareOrders);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**fin de la première étape**/
	public void endGalaxyOrderChoiceTurn(String playerName, String coordinates, StarcraftGame game) {
		game.executeOrderAt(coordinates);

		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			
			GameTurnHandler.printNextTurnScreen(player, game);
			
		}
	}

	/**annulation de l'ordre**/
	public void cancelOrder(String playerName, StarcraftGame game) {
		//piocher une carte évènement à faire
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	public void executeOrder(String playerName, StarcraftGame game) {
		game.executeCurrentOrder();
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}
	
}
