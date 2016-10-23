package listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import entities.Utilisateur;
import servlets.Inscription;

@WebListener
public class ConnectedUserSessions implements HttpSessionListener {

	private static final Map<String, Utilisateur> userSessions = new HashMap<String, Utilisateur>();

	public static Map<String, Utilisateur> getAllUserSessions(){
		  return userSessions;
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		Utilisateur addedUser = (Utilisateur) event.getSession().getAttribute(Inscription.ATT_SESSION_USER);
		addUser(addedUser);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		removeUser(event.getSession());
	}
	
	public static void addUser(Utilisateur utilisateur){
		if (utilisateur != null){
			userSessions.put(utilisateur.getName(), utilisateur);
		}
	}
	
	public static void removeUser(HttpSession session){
		Utilisateur addedUser = (Utilisateur) session.getAttribute(Inscription.ATT_SESSION_USER);

		if (addedUser != null){
			userSessions.remove(addedUser.getName());
		}
	}

}
