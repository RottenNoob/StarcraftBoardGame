package gameEntities.methods;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.GameConstants;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.gameMap.Galaxy;
import gameEntities.gameMap.RoadLink;
import gameEntities.gameMap.Planet;
import gameEntities.playerItems.StarcraftUnit;
import gameEntities.playerItems.UnitPool;

public class PlaceUnits {

	/**active la planète sur laquelle se déroule l'action**/
	public void activateCurrentPlanet(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			Planet currentPlanet = game.getGalaxy().getAllPlanets().get(game.getGalaxy().getPlanetEvent());
			String planetCoordinates = Integer.toString(currentPlanet.getX()) + "." +  Integer.toString(currentPlanet.getY());
			try {
				JSONObject activateCurrentPlanet = new JSONObject()
						.put("action", "activateCurrentPlanet")
						.put("planetCoordinates", planetCoordinates);
				GlobalMethods.sendPlayerAction(player, activateCurrentPlanet);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**active les routes liées sur lesquelles le joueur peut agir**/
	public void activateValidLinks(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			Galaxy galaxy = game.getGalaxy();
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			ArrayList<RoadLink> validCoordinates = galaxy.getAllPossibleLinkEvent(starcraftPlayer, game.getTurnPart());
			try {
				JSONArray coordinateArray = new JSONArray();
				for (RoadLink link:validCoordinates){
					JSONObject coordinateJS = new JSONObject()
							.put("coordinate", String.valueOf(link.getCoordinates1()[0]) + "." + String.valueOf(link.getCoordinates1()[1]))
							.put("roadId", link.getCoordinates1()[2]);
					coordinateArray.put(coordinateJS);
					JSONObject coordinateJS2 = new JSONObject()
							.put("coordinate", String.valueOf(link.getCoordinates2()[0]) + "." + String.valueOf(link.getCoordinates2()[1]))
							.put("roadId", link.getCoordinates2()[2]);
					coordinateArray.put(coordinateJS2);
				}
				JSONObject activateValidLinks = new JSONObject()
						.put("action", "activateValidLinks")
						.put("coordinates", coordinateArray);
				GlobalMethods.sendPlayerAction(player, activateValidLinks);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**active les planètes sur lesquelles le joueur peut agir**/
	public void activateValidPlanets(String player, StarcraftGame game){
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
				JSONObject activateValidPlanets = new JSONObject()
						.put("action", "activateValidPlanets")
						.put("coordinates", coordinateArray);
				GlobalMethods.sendPlayerAction(player, activateValidPlanets);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**ajoute le bouton de placement des unités**/
	public void addUnitPlacementButton(String player, StarcraftGame game){
		try {
			JSONObject addUnitPlacementButton = new JSONObject()
					.put("action", "addUnitPlacementButton");
			GlobalMethods.sendPlayerAction(player, addUnitPlacementButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**place les unités sur le canvas de choix des unités**/
	public void printUnitChoice(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		if (starcraftPlayer.getUnitPools().containsKey(game.getTurnPart())){
			Map<Long, StarcraftUnit> unitsToPlace= starcraftPlayer.getUnitPools().get(game.getTurnPart()).getUnitList();
			//cela évite d'envoyer une action inutile
			if (!unitsToPlace.isEmpty()){
				for (long unitId:unitsToPlace.keySet()){
					StarcraftUnit unit = unitsToPlace.get(unitId);
					JSONObject printUnitChoice = unit.returnStarcraftUnitJson("printUnitChoice");
					GlobalMethods.sendPlayerAction(player, printUnitChoice);
				}
			}
		}
		checkEndTurn(player, game);
	}
	
	/**affiche une unité dans la galaxie**/
	public void printAllGalaxyUnits(String player, StarcraftGame game){
		for (long unitId:game.getGalaxy().getUnitList().keySet()){
			printGalaxyUnit(player, unitId, game);
		}
	}

	/**affiche une unité dans la galaxie**/
	public void printGalaxyUnit(String player, long unitId, StarcraftGame game){
		StarcraftUnit unit = game.getGalaxy().getUnitList().get(unitId);
		JSONObject printUnitChoice = unit.returnStarcraftUnitJson("printGalaxyUnit");
		GlobalMethods.sendPlayerAction(player, printUnitChoice);
	}
	
	/**vérifie si le joueur peut passer au tour suivant**/
	public void checkEndTurn(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			boolean disabledButton = true;
			if (!game.getTurnPart().equals(GameConstants.moveRetreatUnitTurnName)){
				//au cas où la pool n'existerait pas
				if (!starcraftPlayer.getUnitPools().containsKey(game.getTurnPart())){
					disabledButton = false;
				}else{
					UnitPool unitPool = starcraftPlayer.getUnitPools().get(game.getTurnPart());
					if (unitPool.getUnitList().isEmpty()){
						disabledButton = false;
					}else{
						if (unitPool.getUnitList().size() == 1 && game.getGalaxy().getAllPlanets().size() <= game.getPlayerList().size()){
							disabledButton = false;
						}
					}
				}
			}else{
				//TODO si il y a une bataille en cours, on vérifie si il reste des unités à déplacer
				disabledButton = !game.getGalaxy().getStarcraftBattle().canEndTurn(player, game);
			}
			try {
				JSONObject checkEndTurn = new JSONObject()
						.put("action", "checkEndTurn")
						.put("disabledButton", disabledButton);
				GlobalMethods.sendPlayerAction(player, checkEndTurn);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**active les unités sélectionnables par le joueur**/
	public void activateValidUnits(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			ArrayList<Long> activeUnits = game.returnAllActiveUnits();
			try {
				JSONArray unitList = new JSONArray();
				for (long unitId:activeUnits){
					JSONObject coordinateJS = new JSONObject()
							.put("unitId", unitId);
					unitList.put(coordinateJS);
				}
				JSONObject activateValidUnits = new JSONObject()
						.put("action", "activateValidUnits")
						.put("unitList", unitList);
				GlobalMethods.sendPlayerAction(player, activateValidUnits);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**actions du joueur**/
	
	/**indique au joueur quelles places sont possibles pour l'unitée sélectionnée**/
	public void askValidPlacements(String player, long unitId, StarcraftGame game){
		Galaxy galaxy = game.getGalaxy();
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		try {
			ArrayList<int[]> validCoordinates = galaxy.getValidUnitPlacement(starcraftPlayer, unitId);
			JSONArray coordinateArray = new JSONArray();
			for (int[] coordinate:validCoordinates){
				JSONObject coordinateJS = new JSONObject()
						.put("coordinate", String.valueOf(coordinate[0]) + "." + String.valueOf(coordinate[1]))
						.put("areaId", coordinate[2]);
				coordinateArray.put(coordinateJS);
			}
			StarcraftUnit unit = game.getGalaxy().findUnit(starcraftPlayer, unitId);
			JSONObject sendValidPlacements = null;
			if (unit.getType().equals("transport")){
				sendValidPlacements = new JSONObject()
						.put("action", "sendValidTransportPlacements")
						.put("name", unitId)
						.put("coordinates", coordinateArray);
			}else{
				sendValidPlacements = new JSONObject()
						.put("action", "sendValidPlacements")
						.put("name", unitId)
						.put("coordinates", coordinateArray);
			}
			GlobalMethods.sendPlayerAction(player, sendValidPlacements);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void sendUnitPlacement(String playerName, long unitId, String coordinates, int areaId, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		StarcraftUnit unit = game.getGalaxy().findUnit(starcraftPlayer, unitId);
		int separatorIndex = coordinates.indexOf('.');
		int xCoord  = Integer.parseInt(coordinates.substring(0, separatorIndex));
		int yCoord  = Integer.parseInt(coordinates.substring(separatorIndex + 1));
		unit.setCoordinates(new int[]{xCoord, yCoord, areaId}, game);
		game.getGalaxy().addUnit(unit);
		starcraftPlayer.removeUnitFromPools(unitId);
		GameTurnHandler.updateUnitDisplay(unitId, game);
		if (starcraftPlayer.getBuyingOrder() != null){
			GameTurnHandler.endBuyingOrder(playerName, game);
		}
		checkEndTurn(playerName, game);
	}

	public void returnUnitToPool(String playerName, long unitId, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		StarcraftUnit unit = game.getGalaxy().findUnit(starcraftPlayer, unitId); 
		starcraftPlayer.getUnitPools().get(game.getTurnPart()).addUnit(unit);
		game.getGalaxy().removeUnit(unitId, game);
		printUnitChoice(playerName, game);
		checkEndTurn(playerName, game);
	}

	public void endUnitPlacementTurn(String playerName, StarcraftGame game) {
		for (long id:game.getGalaxy().getUnitList().keySet()){
			StarcraftUnit unit = game.getGalaxy().getUnitList().get(id);
			unit.updateOldCoordinates();
			unit.setStartingSituation(GameConstants.inGalaxySituation);
		}
		
		if (game.getGalaxy().getStarcraftBattle() != null && !game.getTurnPart().equals(GameConstants.moveRetreatUnitTurnName)){
			game.getGalaxy().getStarcraftBattle().startBattle(game);
		}else{
			game.nextTurn();
		}
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}
	
}
