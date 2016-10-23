package gameEntities.methods;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;
import gameEntities.gameMap.RoadLink;

public class PlaceZRoad {
	
	/** affiche toutes les routes Z**/
	public void displayAllLink(String player, StarcraftGame game){
		for (RoadLink link:game.getGalaxy().getAllLinks()){
			if (link.getLinkType().equals("zLink")){
				displayLink(player, game, link);
			}
		}
	}
	
	/** affiche une route Z**/
	public void displayLink(String player, StarcraftGame game, RoadLink link){
		try {
			JSONObject displayLink = new JSONObject()
					.put("action", "displayLink")
					.put("coordinates1", Integer.toString(link.getCoordinates1()[0])+"."+Integer.toString(link.getCoordinates1()[1]))
					.put("roadPosition1", link.getCoordinates1()[2])
					.put("coordinates2", Integer.toString(link.getCoordinates2()[0])+"."+Integer.toString(link.getCoordinates2()[1]))
					.put("roadPosition2", link.getCoordinates2()[2]);
			GlobalMethods.sendPlayerAction(player, displayLink);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/** Ajoute les boutons pour la fin du tour**/
	public void addPlaceZRoadButtons(String player, StarcraftGame game){
		try {
			JSONObject addPlacePlanetButtons = new JSONObject()
					.put("action", "addPlaceZRoadButtons");
			GlobalMethods.sendPlayerAction(player, addPlacePlanetButtons);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**active les routes sélectionnables par le joueur**/
	public void activateAvailableRoads(String player, StarcraftGame game){
		ArrayList<int[]> availableRoads = game.getGalaxy().returnAvailableRoads();
		try {
			JSONArray roadList = new JSONArray();
			for (int[] availableRoad:availableRoads){
				JSONObject coordinateJS = new JSONObject()
						.put("coordinates", Integer.toString(availableRoad[0])+"."+Integer.toString(availableRoad[1]))
						.put("roadPosition", availableRoad[2]);
				roadList.put(coordinateJS);
			}
			JSONObject activateValidRoads = new JSONObject()
					.put("action", "activateValidRoads")
					.put("roadList", roadList);
			GlobalMethods.sendPlayerAction(player, activateValidRoads);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	
	//actions du joueur
	
	/**envoie les routes sélectionnables au joueur**/
	public void sendRoadPlacement(String playerName, String coordinates, int roadPosition, StarcraftGame game) {
		int separatorIndex = coordinates.indexOf('.');
		int xCoord  = Integer.parseInt(coordinates.substring(0, separatorIndex));
		int yCoord  = Integer.parseInt(coordinates.substring(separatorIndex + 1));
		int[] selectedRoad = new int[]{xCoord, yCoord, roadPosition};
		ArrayList<int[]> availableRoads = game.getGalaxy().returnAvailableRoads(selectedRoad);
		availableRoads.add(selectedRoad);
		try {
		JSONArray roadList = new JSONArray();
		for (int[] availableRoad:availableRoads){
			JSONObject coordinateJS = new JSONObject()
					.put("coordinates", Integer.toString(availableRoad[0])+"."+Integer.toString(availableRoad[1]))
					.put("roadPosition", availableRoad[2]);
			roadList.put(coordinateJS);
		}
		JSONObject activateValidRoads = new JSONObject()
				.put("action", "activateValidRoads")
				.put("roadList", roadList);
		GlobalMethods.sendPlayerAction(playerName, activateValidRoads);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**fin du tour et création des liens**/
	public void endRoadPlacementTurn(String playerName, String coordinates1, int roadPosition1, String coordinates2,
			int roadPosition2, StarcraftGame game) {
		int separatorIndex1 = coordinates1.indexOf('.');
		int xCoord1  = Integer.parseInt(coordinates1.substring(0, separatorIndex1));
		int yCoord1  = Integer.parseInt(coordinates1.substring(separatorIndex1 + 1));
		int separatorIndex2 = coordinates2.indexOf('.');
		int xCoord2  = Integer.parseInt(coordinates2.substring(0, separatorIndex2));
		int yCoord2  = Integer.parseInt(coordinates2.substring(separatorIndex2 + 1));
		RoadLink link = new RoadLink();
		link.setCoordinates1(new int[]{xCoord1, yCoord1, roadPosition1});
		link.setCoordinates2(new int[]{xCoord2, yCoord2, roadPosition2});
		link.setLinkType("zLink");
		game.getGalaxy().addLink(link);;
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			displayLink(player, game, link);
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	public void endRoadPlacementTurn2(String playerName, StarcraftGame game) {
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
		
	}
	
}
