package gameEntities.methods;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.gameMap.PlanetArea;

public class ResourceHandler {
	
	public void updateBaseWorkerDisplay(StarcraftPlayer player){
		try {
			if (player.getWorker() != null){
				JSONObject updateBaseWorkerDisplay = new JSONObject()
						.put("action", "updateBaseWorkerDisplay")
						.put("availableWorkers", player.getAvailableWorkers())
						.put("unavailableWorkers", player.getUnavailableWorkers());
				GlobalMethods.sendPlayerAction(player.getName(), updateBaseWorkerDisplay);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**affiche le nombre de travailleur dans toutes les zones contenant des travailleurs**/
	public void displayAllWorkersInGalaxy(String player, StarcraftGame game){
		for (String playerName:game.getPlayerList().keySet()){
			StarcraftPlayer workerOwner = game.getPlayerList().get(playerName);
			for (List<Integer> coordinate:workerOwner.getAreaResourceList().keySet()){
				if (workerOwner.getAreaResourceList().get(coordinate).getWorkerAmount() > 0){
					displayWorkersOnArea(player, coordinate, workerOwner, game);
				}
			}
		}
	}
	
	/**affiche le nombre de travailleur dans la zone choisie**/
	public void displayWorkersOnArea(String player, List<Integer> coordinate, StarcraftPlayer workerOwner, StarcraftGame game){
		PlanetArea area = workerOwner.getAreaResourceList().get(coordinate);
		try {
			JSONObject displayWorkersOnArea = new JSONObject()
					.put("action", "displayWorkersOnArea")
					.put("species", workerOwner.getSpecies())
					.put("image", workerOwner.getWorker().getImage())
					.put("number", area.getWorkerAmount())
					.put("coordinate", String.valueOf(coordinate.get(0)) + "." + String.valueOf(coordinate.get(1)))
					.put("areaId", coordinate.get(2))
					.put("color", workerOwner.getPlayerColor());
			GlobalMethods.sendPlayerAction(player, displayWorkersOnArea);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public void displayBaseWorkers(StarcraftPlayer player){
		try {
			if (player.getWorker() != null){
				JSONObject displayBaseWorkers = new JSONObject()
						.put("action", "displayBaseWorkers")
						.put("species", player.getSpecies())
						.put("image", player.getWorker().getImage())
						.put("availableWorkers", player.getAvailableWorkers())
						.put("unavailableWorkers", player.getUnavailableWorkers());
				GlobalMethods.sendPlayerAction(player.getName(), displayBaseWorkers);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void displayWorkersOnBaseResources(StarcraftPlayer player){
		try {
			if (player.getWorker() != null){
				JSONObject displayWorkersOnBaseResources = new JSONObject()
						.put("action", "displayWorkersOnBaseResources")
						.put("species", player.getSpecies())
						.put("image", player.getWorker().getImage())
						.put("workerOnMineral", player.getWorkerOnMineral())
						.put("workerOnGas", player.getWorkerOnGas());
				GlobalMethods.sendPlayerAction(player.getName(), displayWorkersOnBaseResources);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void displayPlayerResourceInfo(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(player);
		if (starcraftPlayer.getLeadershipcards().size() > 0){
			try {
				JSONObject displayPlayerResources = new JSONObject()
						.put("action", "displayPlayerResources")
						.put("mineral", starcraftPlayer.getMineralResources())
						.put("gas", starcraftPlayer.getGasResources());
				GlobalMethods.sendPlayerAction(player, displayPlayerResources);
				JSONObject displayPlayerResourceToken = new JSONObject()
						.put("action", "displayPlayerResourceToken")
						.put("mineral", starcraftPlayer.getMineralToken())
						.put("gas", starcraftPlayer.getGasToken());
				GlobalMethods.sendPlayerAction(player, displayPlayerResourceToken);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
