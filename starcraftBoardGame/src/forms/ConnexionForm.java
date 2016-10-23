package forms;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;

import dao.DAOException;
import dao.UtilisateurDao;
import entities.Utilisateur;
import listener.ConnectedUserSessions;

public class ConnexionForm {
    private static final String CHAMP_PASS   = "motdepasse";
    private static final String CHAMP_NOM    = "nom";
    private static final String ALGO_CHIFFREMENT = "SHA-256";
    
    private String resultat;
    private Map<String, String> erreurs = new HashMap<String, String>();

    private UtilisateurDao      utilisateurDao;
    
    public ConnexionForm(UtilisateurDao utilisateurDao){
    	this.utilisateurDao = utilisateurDao;
    }
    
    public String getResultat() {
        return resultat;
    }

    public Map<String, String> getErreurs() {
        return erreurs;
    }
    
    public Utilisateur connexion(HttpServletRequest request ) throws UnsupportedEncodingException{
        String motDePasse = getValeurChamp( request, CHAMP_PASS );
        String nom = getValeurChamp( request, CHAMP_NOM );
        
        Utilisateur utilisateur = new Utilisateur();
        
        try {
        	traiterNom(nom, utilisateur);
        	traiterMotsDePasse(motDePasse, utilisateur);
            if ( erreurs.isEmpty() ) {
                resultat = "Succès de la connexion.";
            } else {
                resultat = "Échec de la connexion.";
            }
        } catch ( DAOException e ) {
            resultat = "Échec de connexion.";
            e.printStackTrace();
        }
        
        return utilisateur;
    }
    
    /**              fonctions de validations                    **/
    private void validationMotsDePasse( String motDePasse, String mdpBase ) throws FormValidationException  {
        if ( motDePasse != null ) {
            if ( motDePasse.length() < 3 ) {
                throw new FormValidationException ( "Les mots de passe doivent contenir au moins 3 caractères." );
            } else {
                ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
                passwordEncryptor.setAlgorithm( ALGO_CHIFFREMENT );
                passwordEncryptor.setPlainDigest( false );
            	if ( !passwordEncryptor.checkPassword(motDePasse, mdpBase) ) {
            		throw new FormValidationException ( "Le mot de passe n'est pas le bon" );
            	}
            }
        } else {
            throw new FormValidationException ( "Merci de saisir votre mot de passe." );
        }
    }
    
    
    private Utilisateur validationNom( String nom ) throws FormValidationException  {
        if ( nom != null && nom.length() < 3 ) {
            throw new FormValidationException ( "Le nom d'utilisateur doit contenir au moins 3 caractères." );
        }else{
        	if (this.utilisateurDao.trouver(nom) == null){
        	throw new FormValidationException ( "Ce nom ne correspond pas à un utilisateur connu" );
        	}else if(ConnectedUserSessions.getAllUserSessions().containsKey(nom)){
        		throw new FormValidationException ( "Cet utilisateur est déjà connecté."
        				+ " Si votre session s'est mal fermée, veuillez attendre 30 minutes pour vous reconnecter" );
        		}else{
        			return this.utilisateurDao.trouver(nom);
        		}
        }
    }
    
    /**           fonctions de traitement des champs            **/
    private void traiterNom( String nom, Utilisateur utilisateur) {
    	Utilisateur baseUtilisateur = null;
        try {
        	baseUtilisateur = validationNom( nom );
        	utilisateur.setPassword(baseUtilisateur.getPassword());
        	utilisateur.setId(baseUtilisateur.getId());
        } catch ( FormValidationException e ) {
            setErreur( CHAMP_NOM, e.getMessage() );
        }
        utilisateur.setName(nom);
    }
    
    private void traiterMotsDePasse( String motDePasse, Utilisateur utilisateur ) {
    	if (this.erreurs.isEmpty()){
	        try {
	        	validationMotsDePasse( motDePasse,  utilisateur.getPassword());
	        } catch ( FormValidationException e ) {
	            setErreur( CHAMP_PASS, e.getMessage() );
	        }
    	}

    }
    
    
    /*
     * Ajoute un message correspondant au champ spécifié à la map des erreurs.
     */
    private void setErreur( String champ, String message ) {
        erreurs.put( champ, message );
    }

    /*
     * Méthode utilitaire qui retourne null si un champ est vide, et son contenu
     * sinon.
     */
    private static String getValeurChamp( HttpServletRequest request, String nomChamp ) {
        String valeur = request.getParameter( nomChamp );
        if ( valeur == null || valeur.trim().length() == 0 ) {
            return null;
        } else {
            return valeur.trim();
        }
    }
}
