package websockets;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import dao.SaveGameDao;
import entities.SaveGame;
import entities.Utilisateur;
import gameEntities.PreparedGame;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameHandling.GameListHandler;
import listener.InitialisationDaoFactory;
import servlets.Inscription;
import session.PrincipalWithSession;

@ServerEndpoint(value = "/gameLobbyChat")
public class GameCreationSocket {
	
	private SaveGameDao  saveGameDao = InitialisationDaoFactory.getFactory().getSaveGameDao();
	private static final Map<Session, HttpSession> sessions = Collections.synchronizedMap(new HashMap<Session, HttpSession>());
    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     */
    @OnOpen
    public void onOpen(Session session){
        HttpSession httpSessionToAdd = ((PrincipalWithSession) session.getUserPrincipal()).getSession();
        /** vérifie que les personnes utilisant ce webSocket sont bien connectées à un compte utilisateur**/
    	if (httpSessionToAdd != null){
    		sessions.put(session, httpSessionToAdd);
    		updatePlayerList(session);
    	}
    }
    
    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
    }
    
    @OnMessage
    public void onMessage(String message, Session session){
    	Set<Session> sessionSet = getGameSessions(session);
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            Utilisateur currentUser = (Utilisateur) sessions.get(session).getAttribute(Inscription.ATT_SESSION_USER);
            /**Sends a message to all players in the game  **/
            String actionType = jsonMessage.getString("action");
            if ("sendChat".equals(actionType)) {
            	String messageToSend = currentUser.getName()
            			+ " : " + jsonMessage.getString("message");
            	sendMessageToAll(messageToSend, sessionSet);
            }else if ("startGame".equals(actionType)){
            	createStarcraftGame((String) sessions.get(session).getAttribute("hostName"), sessionSet);
            	deletePreparedGame(session, sessionSet);
            	startGame(sessionSet);
            }else if ("deleteGame".equals(actionType)){
            	deletePreparedGame(session, sessionSet);
            	deleteGame(sessionSet);
            }
            else if ("quitGame".equals(actionType)){
            	quitGame(session);
            }
        }
    }
    
    private void createStarcraftGame(String hostName, Set<Session> sessionSet){
    	SaveGame saveGame = new SaveGame();
    	saveGame.setName(hostName);
    	saveGameDao.creer(saveGame);
    	PreparedGame preparedGame = GameListHandler.getPreparedGame(hostName);
    	StarcraftGame starcraftGame = new StarcraftGame();
    	starcraftGame.setSaveGame(saveGame);
    	for (Utilisateur user: preparedGame.getPlayerList()){
    		StarcraftPlayer player = new StarcraftPlayer();
    		player.setName(user.getName());
    		starcraftGame.addPlayer(player);
    	}
    	starcraftGame.initializeGame();
    	GameListHandler.addToStarcraftGameList(starcraftGame);
    	for (Session session: sessionSet){
    		sessions.get(session).setAttribute("gameId", starcraftGame.getId());
    	}
    }
    
    private void quitGame(Session session){
    	String hostname = (String) sessions.get(session).getAttribute("hostName");
    	Utilisateur currentUser = (Utilisateur) sessions.get(session).getAttribute("sessionUtilisateur");
    	GameListHandler.getPreparedGame(hostname).remove(currentUser);
    	updatePlayerList(session);
    	sessions.get(session).removeAttribute("hostName");
        JsonProvider provider = JsonProvider.provider();
        JsonObject startGame = provider.createObjectBuilder()
                .add("action", "quitGame")
                .build();
        try {
        	session.getBasicRemote().sendText(startGame.toString());
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }
    
    private void deleteGame(Set<Session> sessionSet){
        JsonProvider provider = JsonProvider.provider();
        JsonObject startGame = provider.createObjectBuilder()
                .add("action", "deleteGame")
                .build();
        for(Session session: sessionSet ){
            try {
            	session.getBasicRemote().sendText(startGame.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    private void startGame(Set<Session> sessionSet){
        JsonProvider provider = JsonProvider.provider();
        JsonObject startGame = provider.createObjectBuilder()
                .add("action", "startGame")
                .build();
        for(Session session: sessionSet ){
            try {
            	session.getBasicRemote().sendText(startGame.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void deletePreparedGame(Session session, Set<Session> sessionSet){
    	String hostname = (String) sessions.get(session).getAttribute("hostName");
    	GameListHandler.removeFromPreparedGamelist(hostname);
        for(Session gameSession: sessionSet ){
        	sessions.get(gameSession).removeAttribute("hostName");

        }
    }
    
    private void sendMessageToAll(String message, Set<Session> sessionSet){
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "sendChat")
                .add("message", message)
                .build();
        for(Session session: sessionSet ){
            try {
            	session.getBasicRemote().sendText(addMessage.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /** met à jour l'affichage des joueurs présents dans la partie**/
    private void updatePlayerList(Session session){

    	String hostname = (String) sessions.get(session).getAttribute("hostName");
    	PreparedGame game = GameListHandler.getPreparedGame(hostname);
    	
    	Set<Session> sessionSet = getGameSessions(session);
        
    	JsonProvider provider = JsonProvider.provider();
    	JsonObject clearView = provider.createObjectBuilder()
                .add("action", "clearView")
                .build();
        for(Session gameSession: sessionSet ){
            try {
            	gameSession.getBasicRemote().sendText(clearView.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    	for (Utilisateur user:game.getPlayerList()){
    		showPlayer(user, sessionSet);
    	}
    }
    
    /** affiche la présence d'un joueur dans la partie**/
    private void showPlayer(Utilisateur user, Set<Session> sessionSet){
        JsonProvider provider = JsonProvider.provider();
        String userName =  user.getName();
        JsonObject showPlayer = provider.createObjectBuilder()
                .add("action", "showPlayer")
                .add("playerName", userName)
                .build();
        for(Session session : sessionSet ){
        	try {
        		session.getBasicRemote().sendText(showPlayer.toString());
        	} catch (IOException ex) {
        		ex.printStackTrace();
        	}
        }
    }
    
    /** récupère toutes les sessions qui sont dans la même partie que le joueur actuel**/
    private Set<Session> getGameSessions(Session session){
    	String hostname = (String) sessions.get(session).getAttribute("hostName");
    	PreparedGame game = GameListHandler.getPreparedGame(hostname);
    	
    	Set<Session> sessionSet = new HashSet<Session>();
        for(Session key: sessions.keySet() ){
        	Utilisateur userSession = (Utilisateur) sessions.get(key).getAttribute("sessionUtilisateur");
        	Set<Utilisateur> playerList = game.getPlayerList();
        	if (playerList.contains(userSession)){
        		sessionSet.add(key);
        	}
        }
        return sessionSet;
    }
}
