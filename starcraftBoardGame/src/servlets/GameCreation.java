package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet( urlPatterns = { "/accesMembre/gameCreation" } )
public class GameCreation extends HttpServlet {

	private static final long serialVersionUID = -3575712034137427904L;
	public static final String VUE      = "/accesMembre/gameCreation.jsp";
	
	
    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
        /* Affichage de la page de connexion */
        this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );
    }
    

}

