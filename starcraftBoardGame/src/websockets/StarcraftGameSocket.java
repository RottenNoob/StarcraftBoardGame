package websockets;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

import entities.Utilisateur;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.methods.GameTurnHandler;
import gameEntities.methods.GlobalMethods;
import gameHandling.GameListHandler;
import servlets.Inscription;
import session.PrincipalWithSession;


@ServerEndpoint(value = "/gamePageLobby")
public class StarcraftGameSocket {
//il faut rajouter à une httpsession l'id de la game à laquelle un joueur était connecté en cas de reconnection
	
	private static final Map<Session, HttpSession> sessions = Collections.synchronizedMap(new HashMap<Session, HttpSession>());
    /**
     * @throws InterruptedException 
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     */
    @OnOpen
    public void onOpen(Session session) throws InterruptedException{
        HttpSession httpSessionToAdd = ((PrincipalWithSession) session.getUserPrincipal()).getSession();
        /** vérifie que les personnes utilisant ce webSocket sont bien connectées à un compte utilisateur**/
    	if (httpSessionToAdd != null){
    		sessions.put(session, httpSessionToAdd);
    		/** On attend 1 seconde car les sessions ne sont pas forcément accessibles tout de suite quand plusieurs sont crées en même temps**/
    		TimeUnit.SECONDS.sleep(1);
    		try{
    			GlobalMethods.addPlayerSession(getPlayerName(session), session);
    			GameTurnHandler.printCurrentTurnScreen(getPlayerName(session), getCurrentGame(session));
    		} catch(Exception e){
        		e.printStackTrace();
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
    	GlobalMethods.removePlayerSession(getPlayerName(session));
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
            StarcraftGame game = getCurrentGame(session);
        	String playerName = getPlayerName(session);
            if ("sendChat".equals(actionType)) {
            	String messageToSend = currentUser.getName()
            			+ " : " + jsonMessage.getString("message");
            	sendMessageToAll(messageToSend, sessionSet);
            } else if ("chooseFaction".equals(actionType)){
            	// affecte le choix de faction au joueur correspondant
            	// passe le tour
            	// envoie à tous les joueurs une mise à jour de l'affichage
            	GameTurnHandler.callChoosePlayerFaction(playerName,
            			jsonMessage.getString("speciesName"),
            			jsonMessage.getString("factionName"),
            			game);
            } else if ("chooseLeadership".equals(actionType)){
            	//  affecte le choix de leadership au joueur correspondant
            	GameTurnHandler.callChooseLeadershipCard(playerName, jsonMessage.getString("cardName"), game);
            } else if ("placePlanet".equals(actionType)){
            	//  positionne une planète dans l'univers
            	GameTurnHandler.callPlacePlanet(playerName, jsonMessage.getString("planetName"), jsonMessage.getString("coordinates"), game);
            } else if ("rotatePlanet".equals(actionType)){
            	//  fait tourner la planète et met à jour les endroits où celle-ci peut être placée
            	GameTurnHandler.callRotatePlanet(playerName, jsonMessage.getString("planetName"), game);
            } else if ("askValidPlacements".equals(actionType)){
            	// demande au serveur les possibilités de placement pour l'unité sélectionnée
            	GameTurnHandler.callAskValidPlacements(playerName, jsonMessage.getString("unitId"), game);
            } else if ("sendUnitPlacement".equals(actionType)){
            	//  met à jour le placement d'une unité
            	GameTurnHandler.callSendUnitPlacement(playerName,
            			jsonMessage.getString("unitId"),
            			jsonMessage.getString("coordinates"), 
            			jsonMessage.getString("areaId"), 
            			game);
            } else if ("returnUnitToPool".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callReturnUnitToPool(playerName, jsonMessage.getString("unitId"), game);
            } else if ("endUnitPlacementTurn".equals(actionType)){
            	//  met fin au tour de placement des unités
            	GameTurnHandler.callEndUnitPlacementTurn(playerName, game);
            } else if ("askAllRoadPlacements".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callAskAllRoadPlacements(playerName, game);
            } else if ("sendRoadPlacement".equals(actionType)){
            	//  envoie le placement d'une route et demande quelles routes restantes sont valides
            	GameTurnHandler.callSendRoadPlacement(playerName,
            			jsonMessage.getString("coordinates"),
            			jsonMessage.getString("roadPosition"), 
            			game);
            } else if ("endRoadPlacementTurn".equals(actionType)){
            	//  met fin au tour de place de route
            	GameTurnHandler.callEndRoadPlacementTurn(playerName,
            			jsonMessage.getString("coordinates1"),
            			jsonMessage.getString("roadPosition1"),
            			jsonMessage.getString("coordinates2"),
            			jsonMessage.getString("roadPosition2"),
            			game);
            } else if ("endRoadPlacementTurn2".equals(actionType)){
            	// fini le tour sans placer aucune route
            	GameTurnHandler.callEndRoadPlacementTurn2(playerName, game);
            } else if ("endPlanningPhaseTurn".equals(actionType)){
            	//  termine le tour et indique où l'ordre a été placé sur la carte
            	GameTurnHandler.callSendPlanningPhaseTurn(playerName,
            			jsonMessage.getString("coordinates"),
            			jsonMessage.getString("id"), 
            			game);
            } else if ("askOrderStack".equals(actionType)){
            	//  termine le tour et indique où l'ordre a été placé sur la carte
            	GameTurnHandler.callAskOrderStack(playerName,
            			jsonMessage.getString("coordinates"),
            			game);
            } else if ("endGalaxyOrderChoiceTurn".equals(actionType)){
            	//  termine le tour et indique où l'ordre a été placé sur la carte
            	GameTurnHandler.callEndGalaxyOrderChoiceTurn(playerName,
            			jsonMessage.getString("coordinates"),
            			game);
            } else if ("cancelOrder".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callCancelOrder(playerName, game);
            } else if ("executeOrder".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callExecuteOrder(playerName, game);
            } else if ("askValidBattlePlacements".equals(actionType)){
            	// demande au serveur les possibilités de placement pour l'unité sélectionnée
            	GameTurnHandler.callAskValidBattlePlacements(playerName, jsonMessage.getString("unitId"), game);
            } else if ("sendBattleUnitPlacement".equals(actionType)){
            //  met à jour le placement d'une unité
            	GameTurnHandler.callSendBattleUnitPlacement(playerName,
            			jsonMessage.getString("unitId"),
            			jsonMessage.getString("battleRow"), 
            			jsonMessage.getString("battlePlace"), 
            			game);
            } else if ("returnUnitToBattlePool".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callReturnUnitToBattlePool(playerName, jsonMessage.getString("unitId"), game);
            } else if ("endFrontLineTurn".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callEndFrontLineTurn(playerName, game);
            } else if ("endSupportLineTurn".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callEndSupportLineTurn(playerName, game);
            } else if ("askCombatCardHand".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callAskCombatCardHand(playerName, game);
            } else if ("askValidBattleCardPlacements".equals(actionType)){
            	// demande au serveur les possibilités de placement pour l'unité sélectionnée
            	GameTurnHandler.callAskValidBattleCardPlacements(playerName, jsonMessage.getString("cardId"), game);
            } else if ("sendBattleCardPlacement".equals(actionType)){
            //  met à jour le placement d'une unité
            	GameTurnHandler.callSendBattleCardPlacement(playerName,
            			jsonMessage.getString("cardId"),
            			jsonMessage.getString("battleRow"), 
            			jsonMessage.getString("battlePlace"), 
            			game);
            } else if ("returnCardToBattlePool".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callReturnCardToBattlePool(playerName, jsonMessage.getString("cardId"), game);
            }else if ("endBattleCardTurn".equals(actionType)){
            	//  renvoie l'unité dans le canvas de sélection des unités à placer
            	GameTurnHandler.callEndBattleCardTurn(playerName, game);
            } else if ("destroyBattleUnit".equals(actionType)){
            	//  détruit une unité de la bataille
            	GameTurnHandler.callDestroyBattleUnit(playerName, jsonMessage.getString("unitId"), game);
            } else if ("endRetreatTurn".equals(actionType)){
            	//  met fin au tour de retraite des unités
            	GameTurnHandler.callEndRetreatTurn(playerName, game);
            } else if ("askBuyingOrder".equals(actionType)){
            	//  détruit une unité de la bataille
            	GameTurnHandler.callAskBuyingOrder(playerName, jsonMessage.getString("name"), game);
            } else if ("setWorkerOnArea".equals(actionType)){
            	//  met à jour le placement d'une unité
            	GameTurnHandler.callSetWorkerOnArea(playerName,
            			jsonMessage.getString("coordinates"), 
            			jsonMessage.getString("areaId"), 
            			game);
            } else if ("setWorkerBaseMineral".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callSetWorkerBaseMineral(playerName, game);
            } else if ("setWorkerBaseGas".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callSetWorkerBaseGas(playerName, game);
            } else if ("endBuildingUnitTurn".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callEndBuildingUnitTurn(playerName, game);
            } else if ("cancelBuyOrder".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callCancelBuyOrder(playerName, game);
            } else if ("askBuyingBuildingOrder".equals(actionType)){
            	//  demande quelles routes sont valides
            	GameTurnHandler.callAskBuyingBuildingOrder(playerName, jsonMessage.getString("name"), game);
            } else if ("leaveStarcraftGame".equals(actionType)){
            	//  affecte le choix de leadership au joueur correspondant
            	leaveStarcraftGame(sessionSet, game);
            } else if ("saveStarcraftGame".equals(actionType)){
            	//  affecte le choix de leadership au joueur correspondant
            	saveStarcraftGame(sessionSet, game);
            }
        }
    }
    
    
    private void saveStarcraftGame(Set<Session> sessionSet, StarcraftGame game) {
    	ObjectOutputStream oos = null;

		try {
			final FileOutputStream fichier = new FileOutputStream("saveGame.ser");
			oos = new ObjectOutputStream(fichier);
			oos.writeObject(game);
			oos.flush();
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
    }
    
    private void leaveStarcraftGame(Set<Session> sessionSet, StarcraftGame game) {
		for (Session session:sessionSet){
			sessions.get(session).removeAttribute("gameId");
			JsonProvider providerChoice = JsonProvider.provider();
			JsonObject leaveGame = providerChoice.createObjectBuilder()
					.add("action", "leaveGame")
					.build();
			if (session!=null){
				try {
					session.getBasicRemote().sendText(leaveGame.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		GameListHandler.removeToStarcraftGameList(game.getId());
	}

	private String getPlayerName(Session session){
    	Utilisateur userSession = (Utilisateur) sessions.get(session).getAttribute(Inscription.ATT_SESSION_USER);
    	return userSession.getName();
    }
    
    private StarcraftGame getCurrentGame(Session session){
    	Long id = (Long) sessions.get(session).getAttribute("gameId");
    	return GameListHandler.getStarcraftGame(id);
    }
    
    /** récupère toutes les sessions qui sont dans la même partie que le joueur actuel**/
    private Set<Session> getGameSessions(Session session){
    	StarcraftGame game = getCurrentGame(session);
    	
    	Set<Session> sessionSet = new HashSet<Session>();
        for(Session key: sessions.keySet() ){
        	Map<String, StarcraftPlayer> playerList = game.getPlayerList();
        	if (playerList.containsKey(getPlayerName(key))){
        		sessionSet.add(key);
        	}
        }
        return sessionSet;
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
}
