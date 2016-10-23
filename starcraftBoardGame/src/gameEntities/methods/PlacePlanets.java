package gameEntities.methods;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.GameConstants;
import gameEntities.SpecialTurnEvent;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.gameMap.Galaxy;
import gameEntities.gameMap.Planet;

public class PlacePlanets {
	private final int planetDrawing = 3;
	
	/**chaque joueur pioche ses planètes**/
	public void drawPlanets(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		if (starcraftPlayer.getPlanetDeck().isEmpty()){
			for (int i = 0; i < planetDrawing; i++){
				starcraftPlayer.addPlanet(game.drawPlanet());
			}
		}
	}
	
	public void addActionCanvas(String player, StarcraftGame game){
		try {
			JSONObject addCanvas = new JSONObject()
					.put("action", "addActionCanvas");
			GlobalMethods.sendPlayerAction(player, addCanvas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void addPlacePlanetButtons(String player, StarcraftGame game){
		try {
			JSONObject addPlacePlanetButtons = new JSONObject()
					.put("action", "addPlacePlanetButtons");
			GlobalMethods.sendPlayerAction(player, addPlacePlanetButtons);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public void addGalaxyCanvas(String player, StarcraftGame game){
		try {
			JSONObject addCanvas = new JSONObject()
					.put("action", "addGalaxyCanvas")
					.put("name", "galaxyCanvas");
			GlobalMethods.sendPlayerAction(player, addCanvas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void resizeGalaxy(String player, StarcraftGame game){
		Galaxy galaxy = game.getGalaxy();
		galaxy.updateGalaxySizes();
		try {
			JSONObject resizeGalaxy = new JSONObject()
					.put("action", "resizeGalaxy")
					.put("width", galaxy.getWidth())
					.put("length", galaxy.getLength())
					.put("minX",galaxy.getMinX())
					.put("minY", galaxy.getMinY());
			GlobalMethods.sendPlayerAction(player, resizeGalaxy);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void updateGalaxySize(String player, StarcraftGame game){
		Galaxy galaxy = game.getGalaxy();
		galaxy.updateGalaxySizes();
		try {
			JSONObject updateGalaxySize = new JSONObject()
					.put("action", "updateGalaxySize")
					.put("width", galaxy.getWidth())
					.put("length", galaxy.getLength())
					.put("minX",galaxy.getMinX())
					.put("minY", galaxy.getMinY());
			GlobalMethods.sendPlayerAction(player, updateGalaxySize);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setValidPlacements(String player, StarcraftGame game){
		Galaxy galaxy = game.getGalaxy();
		ArrayList<int[]> validCoordinates = galaxy.getValidCoordinates();
		try {
			JSONArray coordinateArray = new JSONArray();
			for (int[] coordinate:validCoordinates){
				JSONObject coordinateJS = new JSONObject()
						.put("coordinate", String.valueOf(coordinate[0]) + "." + String.valueOf(coordinate[1]))
						.put("road", coordinate[2]);
				coordinateArray.put(coordinateJS);
			}

			JSONObject setValidPlacements = new JSONObject()
					.put("action", "setValidPlacements")
					.put("coordinates", coordinateArray);
			GlobalMethods.sendPlayerAction(player, setValidPlacements);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**affiche les planètess que le joueur peut placer**/
	public void printPlanetChoice(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		for (String planetName :starcraftPlayer.getPlanetDeck().keySet()){
			JSONObject printPlanetChoice = starcraftPlayer.getPlanetDeck().get(planetName).returnActionJson("printPlanetChoice");
			GlobalMethods.sendPlayerAction(player, printPlanetChoice);
		}
	}
	
	/**affiche une planète de la galaxie**/
	public void printGalaxyPlanet(String player, StarcraftGame game, String planetName){
		Galaxy galaxy = game.getGalaxy();
		if (galaxy.getAllPlanets().containsKey(planetName)){
			JSONObject printPlanetGalaxy = galaxy.getAllPlanets().get(planetName).returnActionJson("printPlanetGalaxy");
			GlobalMethods.sendPlayerAction(player, printPlanetGalaxy);
		}
	}
	
	/**affiche toutes les planètes de la galaxie**/
	public void printAllGalaxyPlanets(String player, StarcraftGame game){
		for (String planeNamet:game.getGalaxy().getAllPlanets().keySet()){
			printGalaxyPlanet(player, game, planeNamet);
		}
	}
	
	//Actions du joueur
	/**Le joueur place une planète**/
	public void placePlanet(String playerName, String planetName, String coordinates, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		Planet addedPlanet = starcraftPlayer.getPlanetDeck().get(planetName);
		int separatorIndex = coordinates.indexOf('.');
		int xCoord  = Integer.parseInt(coordinates.substring(0, separatorIndex));
		int yCoord  = Integer.parseInt(coordinates.substring(separatorIndex + 1));
		addedPlanet.setX(xCoord);
		addedPlanet.setY(yCoord);
		game.getGalaxy().addPlanet(addedPlanet);
		game.getGalaxy().setPlanetEvent(planetName);
		starcraftPlayer.removePlanet(planetName);
		//si c'est la fin du tour du premier joueur, on rajoute les tours correspondant au placement des secondes planètes
		if (game.getCurrentTurnNumber() == 0){
			SpecialTurnEvent specialTurnEvent = new SpecialTurnEvent();
			specialTurnEvent.setTriggeringPlayer(playerName);
			specialTurnEvent.setTriggeringTurn(GameConstants.placeZRoadTurnName);
			specialTurnEvent.setSpecialTurnName("planetChoice");
			for (String playerNameTurn:game.getPlayerTurns()){
				specialTurnEvent.addNewPlayerTurn(0, playerNameTurn);
			}
			game.addSpecialEvent(specialTurnEvent);
		}
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			printGalaxyPlanet(player, game, planetName);
			// à faire, enlever les actions possibles au moment du placement des planètes
			/*if (game.getCurrentTurnNumber() == 0){
				GlobalMethods.clearByClass(player, "actionCanvas");
			}*/
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}
	
	/**cette fonction change la rotation d'une planète en cours de placement et met à jour
	l'affichage de ces routes pour le joueur actif**/
	public void rotatePlanet(String playerName, String planetName, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		Planet planet = starcraftPlayer.getPlanetDeck().get(planetName);
		planet.incrementRotation();
		JSONObject rotatePlanetRoads = planet.returnRoadPositionJson("rotatePlanetRoads");
		GlobalMethods.sendPlayerAction(playerName, rotatePlanetRoads);
	}

}
