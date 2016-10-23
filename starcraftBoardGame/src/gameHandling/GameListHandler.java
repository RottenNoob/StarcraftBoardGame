package gameHandling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gameEntities.PreparedGame;
import gameEntities.StarcraftGame;

public class GameListHandler {

	private static final Map<String, PreparedGame> preparedGameList = Collections.synchronizedMap(new HashMap<String, PreparedGame>());
	private static final Map<Long, StarcraftGame> starcraftGameList = Collections.synchronizedMap(new HashMap<Long, StarcraftGame>());
	
	/**gestion des parties en préparation **/
	public static Map<String, PreparedGame> getPreparedGamelist(){
		return preparedGameList;
	}
	
	public static void addToPreparedGamelist(PreparedGame game){
		preparedGameList.put(game.getHost().getName(), game);
	}
	
	public static void removeFromPreparedGamelist(String hostName){
		preparedGameList.remove(hostName);
	}
	
	public static PreparedGame getPreparedGame(String hostName){
		return preparedGameList.get(hostName);
	}
	
	
	/**gestion des parties du jeu starcraft **/
	public static Map<Long, StarcraftGame> getStarcraftGameList(){
		return starcraftGameList;
	}
	
	public static void addToStarcraftGameList(StarcraftGame game){
		starcraftGameList.put(game.getId(), game);
	}
	
	public static void removeToStarcraftGameList(Long id){
		starcraftGameList.remove(id);
	}
	
	public static StarcraftGame getStarcraftGame(Long id){
		if (!starcraftGameList.containsKey(id)){
			try {
				throw new Exception("Couldn't find the game with id : " + Long.toString(id));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return starcraftGameList.get(id);
	}
	
	public static long getStarcraftGameId(String playerName){
		long gameId = -1;
		for (Long starcraftGameId:starcraftGameList.keySet()){
			if (starcraftGameList.get(starcraftGameId).getPlayerList().containsKey(playerName)){
				gameId = starcraftGameId;
			}
		}
		return gameId;
	}


}
