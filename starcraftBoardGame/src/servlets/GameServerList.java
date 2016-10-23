package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import forms.GameListForm;
import gameEntities.PreparedGame;


@WebServlet( urlPatterns = { "/accesMembre/gameServerList" } )
public class GameServerList extends HttpServlet {

	private static final long serialVersionUID = 2216014604411081747L;
	public static final String VUE      = "/accesMembre/gameServerList.jsp";
	public static final String VUE2      = "/starcraftBoardGame/accesMembre/gameCreation";
	
	
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        /* Affichage de la page de connexion */
        this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );
    }
    
    public void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        
    	GameListForm form = new GameListForm();
    	

		@SuppressWarnings("unused")
		PreparedGame game = form.newGame(request);
        
        response.sendRedirect( VUE2 );
        
    }
}
