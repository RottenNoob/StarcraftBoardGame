package gameEntities.methods;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;

public class GlobalMethods {
	private static final Map<String, Session> playerSessions = Collections.synchronizedMap(new HashMap<String, Session>());
	
	public static Map<String, Session> getPlayerSessions(){
		return playerSessions;
	}
	
	public static void addPlayerSession(String playerName, Session session){
		playerSessions.put(playerName, session);
	}
	
	public static void removePlayerSession(String playerName){
		playerSessions.remove(playerName);
	}
	
	/** Fonction envoyant à l'interface Web les informations nécessaires pour
	 * déterminer le joueur actif **/
	public static void printPlayerTurn(String player, StarcraftGame game){
		String message = "";
		String isCurrentPlayerTurn = "";
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			isCurrentPlayerTurn= "true";
			message = "It\'s your turn";
		}else{
			isCurrentPlayerTurn= "false";
			message = "It\'s " + game.getPlayerCurrentlyPlaying() + "\'s turn";
		}
		JSONObject startGame;
		try {
			startGame = new JSONObject()
					.put("action", "playerTurnUpdate")
					.put("message", message)
					.put("isCurrentPlayerTurn", isCurrentPlayerTurn)
					.put("currentPlayer", game.getPlayerCurrentlyPlaying());
			sendPlayerAction(player, startGame);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void clearByClass(String player, String className){
		JSONObject deleteClassView;
		try {
			deleteClassView = new JSONObject()
					.put("action", "clearViewByClass")
					.put("name", className);
			sendPlayerAction(player, deleteClassView);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearObjectEvents(String player){
		JSONObject clearObjectEvents;
		try {
			clearObjectEvents = new JSONObject()
					.put("action", "clearObjectEvents");
			sendPlayerAction(player, clearObjectEvents);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void startTimeMeasure(String player){
		JSONObject startTimeMeasure;
		try {
			startTimeMeasure = new JSONObject()
					.put("action", "startTimeMeasure");
			sendPlayerAction(player, startTimeMeasure);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void endTimeMeasure(String player){
		JSONObject endTimeMeasure;
		try {
			endTimeMeasure = new JSONObject()
					.put("action", "endTimeMeasure");
			sendPlayerAction(player, endTimeMeasure);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearActionStage(String player){
		JSONObject clearActionStage;
		try {
			clearActionStage = new JSONObject()
					.put("action", "clearActionStage");
			sendPlayerAction(player, clearActionStage);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void deleteElement(String player, String xpath){
		try {
			JSONObject deleteElement = new JSONObject()
					.put("action", "deleteElement")
					.put("xpath", xpath);
			sendPlayerAction(player, deleteElement);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void printGameBoard(String player){
		try {
			JSONObject updateBoardView = new JSONObject()
					.put("action", "updateBoardView");
			sendPlayerAction(player, updateBoardView);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void upgradeActionMenu(String player){	
		try {
			JSONObject addCanvas = new JSONObject()
					.put("action", "upgradeActionMenu");
			sendPlayerAction(player, addCanvas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public static void sendPlayerAction(String player, JSONObject object){
		Session session = playerSessions.get(player);
		if (session!=null){
			try {
				session.getBasicRemote().sendText(object.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
