package gameEntities.methods;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.GameConstants;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.playerItems.BuyableItem;
import gameEntities.playerItems.StarcraftBuilding;
import gameEntities.playerItems.StarcraftUnit;

/**classe gérant la construction**/
public class BuildTurnHandler {
	
	/**vérifie si le joueur peut passer au tour suivant**/
	public void checkEndTurn(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			boolean disabledButton = true;
			if (starcraftPlayer.getBuyingOrder()==null){
				disabledButton = false;
			}
			try {
				//setOnOffCancelBuyOrder
				JSONObject checkEndTurn = new JSONObject()
						.put("action", "checkEndTurn")
						.put("disabledButton", disabledButton);
				GlobalMethods.sendPlayerAction(player, checkEndTurn);
				JSONObject setOnOffCancelBuyOrder = new JSONObject()
						.put("action", "setOnOffCancelBuyOrder")
						.put("disabledButton", !disabledButton);
				GlobalMethods.sendPlayerAction(player, setOnOffCancelBuyOrder);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addBuildingUnitTurnButton(String player, StarcraftGame game){
		try {
			JSONObject addBuildingUnitTurnButton = new JSONObject()
					.put("action", "addBuildingUnitTurnButton");
			GlobalMethods.sendPlayerAction(player, addBuildingUnitTurnButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**modifie l'alpha d'une unité dans la barre de construction d'unité**/
	public void setUnitAlpha(String player, String unitName, Double alpha){
		try {
		JSONObject setUnitAlpha = new JSONObject()
				.put("action", "setUnitAlpha")
				.put("name", unitName)
				.put("alpha", alpha);
		GlobalMethods.sendPlayerAction(player, setUnitAlpha);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void displayUnlockedBuildings(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			JSONObject displayUnlockedBuildings = starcraftPlayer.getUnlockedBuildingsJS(game, "displayUnlockedBuildings");
			if (displayUnlockedBuildings != null){
				GlobalMethods.sendPlayerAction(player, displayUnlockedBuildings);
			}
		}
	}
	
	/**affiche les unités que l'on peut construire**/
	public void displayUnlockedUnits(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			Boolean showUnits = true;
			if (starcraftPlayer.getBuyingOrder() != null){
				if (starcraftPlayer.getBuyingOrder().isReady()){
					showUnits = false;
				}
			}
			if (showUnits){
				JSONObject displayUnlockedUnits = starcraftPlayer.getUnlockedUnitsJS(game, "displayUnlockedUnits");
				GlobalMethods.sendPlayerAction(player, displayUnlockedUnits);
			}
		}
	}
	
	/**affiche les unités que l'on peut construire**/
	public void activateUnlockedUnits(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			if (starcraftPlayer.getBuyingOrder() == null){
				JSONObject activateUnlockedUnits = starcraftPlayer.getUnlockedUnitsJS(game, "activateUnlockedUnits");
				GlobalMethods.sendPlayerAction(player, activateUnlockedUnits);
			}
		}
	}
	
	/**fonction mettant en place les évènements de récupération de ressource sur la galaxie**/
	public void addResourcePlacesEvent(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			if (starcraftPlayer.getBuyingOrder() != null){
				if (!starcraftPlayer.getBuyingOrder().isReady()){
					try {
						if (starcraftPlayer.getAreaResourceList().size() > 0){
							JSONObject addResourcePlacesEvent = new JSONObject()
									.put("action", "addResourcePlacesEvent");
							JSONArray coordinateArray = new JSONArray();
							for (List<Integer> coordinate:starcraftPlayer.getAreaResourceList().keySet()){
								JSONObject coordinateJS = new JSONObject()
										.put("coordinate", String.valueOf(coordinate.get(0)) + "." + String.valueOf(coordinate.get(1)))
										.put("areaId", coordinate.get(2));
								coordinateArray.put(coordinateJS);
							}
							addResourcePlacesEvent.put("coordinates", coordinateArray);
							GlobalMethods.sendPlayerAction(player, addResourcePlacesEvent);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**fonction activant les différentes zones de ressources**/
	public void activateResourcePlaces(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
			if (starcraftPlayer.getBuyingOrder() != null){
				if (!starcraftPlayer.getBuyingOrder().isReady()){
					try {
						BuyableItem item = starcraftPlayer.getBuyingOrder().getItem();
						if (item.getClass().getName().endsWith("StarcraftUnit")){
							StarcraftUnit unit = (StarcraftUnit) item;
							setUnitAlpha(player, unit.getName(), 0.6);
						}else if (item.getClass().getName().endsWith("StarcraftBuilding")){
							StarcraftBuilding building = (StarcraftBuilding) item;
							setUnitAlpha(player,
									Integer.toString(building.getNumber()) + "." + Integer.toString(building.getLevel()), 0.6);
						}
						
						if (starcraftPlayer.canSetWorkerOnBaseMineral()){
							JSONObject activateBaseMineral = new JSONObject()
									.put("action", "activateBaseMineral");
							GlobalMethods.sendPlayerAction(player, activateBaseMineral);
						}
						if (starcraftPlayer.canSetWorkerOnBaseGas()){
							JSONObject activateBaseGas = new JSONObject()
									.put("action", "activateBaseGas");
							GlobalMethods.sendPlayerAction(player, activateBaseGas);
						}
						Set<List<Integer>> coordinateList = starcraftPlayer.getUsableResourceAreas();
						
						if (coordinateList.size() > 0){
							JSONObject activateResourceAreas = new JSONObject()
									.put("action", "activateResourceAreas");
							JSONArray coordinateArray = new JSONArray();
							for (List<Integer> coordinate:coordinateList){
								JSONObject coordinateJS = new JSONObject()
										.put("coordinate", String.valueOf(coordinate.get(0))
												+ "." + String.valueOf(coordinate.get(1)))
										.put("areaId", coordinate.get(2));
								coordinateArray.put(coordinateJS);
							}
							activateResourceAreas.put("coordinates", coordinateArray);
							GlobalMethods.sendPlayerAction(player, activateResourceAreas);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	//TODO
	/*gestion des actions du joueur*/
	
	/**création d'une commande d'unité**/
	public void askBuyingUnitOrder(String playerName, String unitName, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		StarcraftUnit unit = new StarcraftUnit(unitName, game.unitIdGenerator);
		unit.setStartingSituation("builtUnit");
		unit.setOwner(playerName);
		unit.setColor(starcraftPlayer.getPlayerColor());
		starcraftPlayer.setBuyingOrder(unit);
		//désactivation du bouton de fin de tour
		checkEndTurn(playerName, game);
		activateResourcePlaces(playerName, game);
		addResourcePlacesEvent(playerName, game);
	}
	
	/**création d'une commande d'unité**/
	public void askBuyingBuildingOrder(String playerName, int number, int level, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		StarcraftBuilding building = starcraftPlayer.getBuilding(number, level);
		starcraftPlayer.setBuyingOrder(building);
		//désactivation du bouton de fin de tour
		checkEndTurn(playerName, game);
		activateResourcePlaces(playerName, game);
		addResourcePlacesEvent(playerName, game);
	}

	/**affecte un travailleur à une zone en vue de l'achat d'une unité**/
	public void setWorkerOnArea(String playerName, String coordinates, int areaId, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		int separatorIndex = coordinates.indexOf('.');
		int xCoord  = Integer.parseInt(coordinates.substring(0, separatorIndex));
		int yCoord  = Integer.parseInt(coordinates.substring(separatorIndex + 1));
		starcraftPlayer.sendWorkerToArea(xCoord, yCoord, areaId);
		GameTurnHandler.updateAreaWorkerDisplay(Arrays.asList(xCoord, yCoord, areaId), starcraftPlayer, game);
		validateResourceSpending(playerName, game);
	}

	public void setWorkerBaseMineral(String playerName, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		starcraftPlayer.sendWorkerOnBaseMineral();
		GameTurnHandler.callDisplayWorkersOnBaseResources(starcraftPlayer);
		validateResourceSpending(playerName, game);
	}
	
	public void setWorkerBaseGas(String playerName, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		starcraftPlayer.sendWorkerOnBaseGas();
		GameTurnHandler.callDisplayWorkersOnBaseResources(starcraftPlayer);
		validateResourceSpending(playerName, game);
	}
	
	/*fonctions utilitaires*/
	/****/
	public void validateResourceSpending(String playerName, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		if (starcraftPlayer.getBuyingOrder().isReady()){
			//l'achat est complété
			//cas où le joueur a acheté une unité
			if (starcraftPlayer.getBuyingOrder().getItem().getClass().getName().endsWith("StarcraftUnit")){
				StarcraftUnit unit = (StarcraftUnit) starcraftPlayer.getBuyingOrder().getItem();
				//dans le cas d'un travailleur
				if (unit.getType().equals("worker")){
					starcraftPlayer.setUnavailableWorkers(starcraftPlayer.getUnavailableWorkers() + 1);
					setUnitAlpha(playerName, unit.getName(), 1.0);
					starcraftPlayer.executeBuyingOrder();
					checkEndTurn(playerName, game);
					activateUnlockedUnits(playerName, game);
				}else{
					starcraftPlayer.addUnitToPlayerPool(game.getTurnPart(), unit);
					GameTurnHandler.placeBuiltUnit(playerName, game);
				}
			}else if (starcraftPlayer.getBuyingOrder().getItem().getClass().getName().endsWith("StarcraftBuilding")){
				starcraftPlayer.buildBuilding();
				starcraftPlayer.executeBuyingOrder();
				checkEndTurn(playerName, game);
				GlobalMethods.clearActionStage(playerName);
			}
		}else{
			//on cherche les autres ressources nécessaires à la complétion de l'achat
			activateResourcePlaces(playerName, game);
		}
		GameTurnHandler.updateBaseWorkerDisplay(starcraftPlayer);
	}

	public void endBuildingUnitTurn(String playerName, StarcraftGame game) {
		for (long id:game.getGalaxy().getUnitList().keySet()){
			StarcraftUnit unit = game.getGalaxy().getUnitList().get(id);
			unit.updateOldCoordinates();
			unit.setStartingSituation(GameConstants.inGalaxySituation);
		}
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		starcraftPlayer.setUnitBuilt(0);
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	public void cancelBuyOrder(String playerName, StarcraftGame game) {
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		if (starcraftPlayer.getBuyingOrder() != null){
			if (starcraftPlayer.getBuyingOrder().getItem().getClass().getName().endsWith("StarcraftUnit")){
				StarcraftUnit unit = (StarcraftUnit) starcraftPlayer.getBuyingOrder().getItem();
				setUnitAlpha(playerName, unit.getName(), 1.0);
			}
			starcraftPlayer.cancelBuyingOrder(game);
		}
	}


}
