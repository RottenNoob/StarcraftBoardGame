package listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import dao.DAOFactory;

@WebListener
public class InitialisationDaoFactory implements ServletContextListener {
    private static final String ATT_DAO_FACTORY = "daofactory";

    private static DAOFactory          daoFactory;

    @Override
    public void contextInitialized( ServletContextEvent event ) {
        /* Récupération du ServletContext lors du chargement de l'application */
        ServletContext servletContext = event.getServletContext();
        /* Instanciation de notre DAOFactory */
        daoFactory = DAOFactory.getInstance();
        /* Enregistrement dans un attribut ayant pour portée toute l'application */
        servletContext.setAttribute( ATT_DAO_FACTORY, daoFactory );
    }

    @Override
    public void contextDestroyed( ServletContextEvent event ) {
        /* Rien à réaliser lors de la fermeture de l'application... */
    }
    
    public static DAOFactory getFactory(){
    	return daoFactory;
    }
}