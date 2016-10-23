package websockets;

import javax.servlet.http.HttpSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import entities.Utilisateur;
import gameEntities.PreparedGame;
import gameEntities.StarcraftGame;
import gameHandling.GameListHandler;
import servlets.Inscription;
import session.PrincipalWithSession;



@ServerEndpoint("/serverLobbyChat")
public class GameListWebSocket {
	private static final Map<Session, HttpSession> sessions = Collections.synchronizedMap(new HashMap<Session, HttpSession>());
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        HttpSession httpSessionToAdd = ((PrincipalWithSession) session.getUserPrincipal()).getSession();
        /** vérifie que les personnes utilisant ce webSocket sont bien connectées à un compte utilisateur**/
    	if (httpSessionToAdd != null){
    		GameListWebSocket.sessions.put(session, httpSessionToAdd);
    		updateServerList();
    		Utilisateur userSession = (Utilisateur) httpSessionToAdd.getAttribute(Inscription.ATT_SESSION_USER);
    		long gameId = -1;
    		if (httpSessionToAdd.getAttribute("gameId") != null){
    			gameId = (Long) httpSessionToAdd.getAttribute("gameId");
    		}else{
    			gameId = GameListHandler.getStarcraftGameId(userSession.getName());
    		}
    		if (gameId > -1){
    			httpSessionToAdd.setAttribute("gameId", gameId);
    			JsonProvider provider = JsonProvider.provider();
    			JsonObject rejoinGame = provider.createObjectBuilder()
    					.add("action", "rejoinGame")
    					.build();
    			try {
    				session.getBasicRemote().sendText(rejoinGame.toString());
    			} catch (IOException ex) {
    				ex.printStackTrace();
    			}
    		}
    	}
    }
 

    @OnMessage
    public void onMessage(String message, Session session){
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            Utilisateur currentUser = (Utilisateur) sessions.get(session).getAttribute("sessionUtilisateur");
            /**
             * When a user sends a message to the server, this method will intercept the message
             * and allow us to react to it. For now the message is read as a String.
             */
            if ("sendChat".equals(jsonMessage.getString("action"))) {
            	String messageToSend = currentUser.getName()
            			+ " : " + jsonMessage.getString("message");
            	sendMessageToAll(messageToSend);
            }else if ("createGame".equals(jsonMessage.getString("action"))){
            	createGame(currentUser);
            } else if("joinGame".equals(jsonMessage.getString("action"))){
            	joinGame(currentUser, jsonMessage.getString("hostName"));
            } else if("loadGame".equals(jsonMessage.getString("action"))){
            	loadGame(session);
            }
        }
    }
 
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
    }
    
    private void loadGame(Session session){
    	ObjectInputStream ois = null;

		try {
			final FileInputStream fichier = new FileInputStream("saveGame.ser");
			ois = new ObjectInputStream(fichier);
			StarcraftGame game = (StarcraftGame) ois.readObject();
			GameListHandler.addToStarcraftGameList(game);
	    	sessions.get(session).setAttribute("gameId", game.getId());
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonProvider provider = JsonProvider.provider();
		JsonObject rejoinGame = provider.createObjectBuilder()
				.add("action", "rejoinGame")
				.build();
		try {
			session.getBasicRemote().sendText(rejoinGame.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }
    
    private void createGame(Utilisateur user){
    	PreparedGame game = new PreparedGame();
    	game.setHost(user);
    	GameListHandler.addToPreparedGamelist(game);
    	updateServerList();
    }
    
    private void joinGame(Utilisateur user, String hostName){
		GameListHandler.getPreparedGame(hostName).addPlayer(user);
    	updateServerList();
    }
    
    
    /**fonction permettant à tous les joueurs connectés de voir toutes les parties crées**/
    private void updateServerList(){
    	JsonProvider provider = JsonProvider.provider();
    	JsonObject clearView = provider.createObjectBuilder()
                .add("action", "clearView")
                .build();
        for(Session key: GameListWebSocket.sessions.keySet() ){
            try {
            	key.getBasicRemote().sendText(clearView.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    	for (String key : GameListHandler.getPreparedGamelist().keySet()){
    		printGameToAll(GameListHandler.getPreparedGamelist().get(key));
    	}

    }
    
    
    private void printGameToAll(PreparedGame game){
        JsonProvider provider = JsonProvider.provider();
        String playerNumber =  Integer.toString(game.getPlayerList().size());
        JsonObject createServer = provider.createObjectBuilder()
                .add("action", "createGame")
                .add("hostName", game.getHost().getName())
                .add("playerNumber", playerNumber)
                .build();
        for(Session key: GameListWebSocket.sessions.keySet() ){
            try {
            	key.getBasicRemote().sendText(createServer.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void sendMessageToAll(String message){
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "sendChat")
                .add("message", message)
                .build();
        for(Session key: GameListWebSocket.sessions.keySet() ){
            try {
            	key.getBasicRemote().sendText(addMessage.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
