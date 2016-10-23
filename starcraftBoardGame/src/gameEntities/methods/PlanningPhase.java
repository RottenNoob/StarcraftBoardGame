package gameEntities.methods;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.gameMap.Galaxy;
import gameEntities.playerItems.OrderToken;

public class PlanningPhase {

	/**place les unités sur le canvas de choix des unités**/
	public void displayAvailableOrders(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			for (int i:starcraftPlayer.getAvailableOrders().keySet()){
				OrderToken order = starcraftPlayer.getAvailableOrders().get(i);
				try {
					JSONObject displayAvailableOrder = new JSONObject()
							.put("action", "displayAvailableOrder")
							.put("id", order.getId())
							.put("name", order.getName())
							.put("color", starcraftPlayer.getPlayerColor())
							.put("special", order.getSpecial());
					GlobalMethods.sendPlayerAction(player, displayAvailableOrder);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void clearDisplayedOrders(String player, StarcraftGame game){
		if (game.getGalaxy().getOrderList().size() > 0){
			try {
				JSONObject clearDisplayedOrders = new JSONObject()
						.put("action", "clearDisplayedOrders");
				GlobalMethods.sendPlayerAction(player, clearDisplayedOrders);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void displayPlacedOrders(String player, StarcraftGame game){
		if (game.getGalaxy().getOrderList().size() > 0){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			for (String coordinates:game.getGalaxy().getOrderList().keySet()){
				OrderToken order = game.getGalaxy().getOrderList().get(coordinates).get(0);
				JSONObject displayPlacedOrder = order.returnOrderJson("displayPlacedOrder", coordinates, starcraftPlayer, game);
				GlobalMethods.sendPlayerAction(player, displayPlacedOrder);
			}
		}
	}
	
	/**ajoute le bouton de placement des ordres**/
	public void addPlanningPhaseButton(String player, StarcraftGame game){
		try {
			JSONObject addPlanningPhaseButton = new JSONObject()
					.put("action", "addPlanningPhaseButton");
			GlobalMethods.sendPlayerAction(player, addPlanningPhaseButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**active les planètes sur lesquelles le joueur peut agir**/
	public void activateValidPlanetSquares(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			Galaxy galaxy = game.getGalaxy();
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			ArrayList<int[]> validCoordinates = galaxy.getAllPossiblePlanetEvents(starcraftPlayer, game.getTurnPart());
			try {
				JSONArray coordinateArray = new JSONArray();
				for (int[] coordinate:validCoordinates){
					JSONObject coordinateJS = new JSONObject()
							.put("coordinate", String.valueOf(coordinate[0]) + "." + String.valueOf(coordinate[1]));
					coordinateArray.put(coordinateJS);
				}
				JSONObject activateValidPlanetSquares = new JSONObject()
						.put("action", "activateValidPlanetSquares")
						.put("coordinates", coordinateArray);
				GlobalMethods.sendPlayerAction(player, activateValidPlanetSquares);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**actions du joueur**/
	
	/**fin du tour**/
	public void sendPlanningPhaseTurn(String playerName, String coordinates, int orderId, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		OrderToken orderToAdd = starcraftPlayer.getAvailableOrders().get(orderId);
		starcraftPlayer.removeOrder(orderId);
		game.getGalaxy().addOrder(coordinates, orderToAdd);
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	public void askOrderStack(String playerName, String coordinates, StarcraftGame game) {
		ArrayList<OrderToken> orderList = game.getGalaxy().getOrderList().get(coordinates);
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		for (OrderToken orderToken:orderList){
			JSONObject displayOrderStack = orderToken.returnOrderJson("displayOrderStack", coordinates, starcraftPlayer, game);
			GlobalMethods.sendPlayerAction(playerName, displayOrderStack);
		}
	}
}
