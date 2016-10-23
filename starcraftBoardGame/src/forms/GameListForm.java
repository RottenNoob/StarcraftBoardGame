package forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import entities.Utilisateur;
import gameEntities.PreparedGame;
import gameHandling.GameListHandler;

public class GameListForm {

	
	public PreparedGame newGame(HttpServletRequest request ){
		
		PreparedGame game = new PreparedGame();
        /* Récupération de la session depuis la requête */
        HttpSession session = request.getSession();
        Utilisateur currentUser = (Utilisateur) session.getAttribute("sessionUtilisateur");
        try{
        	verifyActiongame(game, currentUser, request);
        } catch (Exception e){
        	e.printStackTrace();
        }
        
		return game;
	}
	
	
	public void verifyActiongame(PreparedGame game, Utilisateur user, HttpServletRequest request) throws Exception{
		String gameAction = getValeurChamp( request, "gameAction" );
		String hostName = getValeurChamp( request, "gameHostName" );
		
		if (gameAction.equals("createGame")){
			game.setHost(user);
		}else if (gameAction.equals("joinGame")){
			game = GameListHandler.getPreparedGame(hostName);
		} else{
			throw new Exception("action inconnue");
		}
		
		 HttpSession session = request.getSession();
		 session.setAttribute("hostName", hostName);
	}
	
    private static String getValeurChamp( HttpServletRequest request, String nomChamp ) {
        String valeur = request.getParameter( nomChamp );
        if ( valeur == null || valeur.trim().length() == 0 ) {
            return null;
        } else {
            return valeur.trim();
        }
    }
}
