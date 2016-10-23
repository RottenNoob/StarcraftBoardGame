package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static dao.DAOUtilitaire.*;

import entities.Utilisateur;

public class UtilisateurDaoImpl implements UtilisateurDao {
	
    private DAOFactory          daoFactory;
    private static final String SQL_SELECT_PAR_NOM = "SELECT id, nomUtilisateur, mot_de_passe FROM Utilisateur WHERE nomUtilisateur = ?";
    private static final String SQL_INSERT = "INSERT INTO Utilisateur (nomUtilisateur, mot_de_passe) VALUES (?, ?)";

    
    UtilisateurDaoImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }
	
    /* Implémentation de la méthode trouver() définie dans l'interface UtilisateurDao */
    @Override
    public Utilisateur trouver( String nom ) throws DAOException {
        return trouver( SQL_SELECT_PAR_NOM, nom );
    }

    /* Implémentation de la méthode définie dans l'interface UtilisateurDao */
    @Override
    public void creer( Utilisateur utilisateur ) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, utilisateur.getName(), utilisateur.getPassword() );
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourné par la requête d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
            /* Récupération de l'id auto-généré par la requête d'insertion */
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                /* Puis initialisation de la propriété id du bean Utilisateur avec sa valeur */
                utilisateur.setId( valeursAutoGenerees.getLong( 1 ) );
            } else {
                throw new DAOException( "échec de la création de l'utilisateur en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }
    }
    
    /*
     * Méthode générique utilisée pour retourner un utilisateur depuis la base
     * de données, correspondant à la requête SQL donnée prenant en paramﾃｨtres
     * les objets passés en argument.
     */
    private Utilisateur trouver( String sql, Object... objets ) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Utilisateur utilisateur = null;

        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            /*
             * Préparation de la requête avec les objets passés en arguments
             * (ici, uniquement une adresse email) et exécution.
             */
            preparedStatement = initialisationRequetePreparee( connexion, sql, false, objets );
            resultSet = preparedStatement.executeQuery();
            /* Parcours de la ligne de données retournée dans le ResultSet */
            if ( resultSet.next() ) {
                utilisateur = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return utilisateur;
    }
    
    
    /*
     * Simple méthode utilitaire permettant de faire la correspondance (le
     * mapping) entre une ligne issue de la table des utilisateurs (un
     * ResultSet) et un bean Utilisateur.
     */
    private static Utilisateur map( ResultSet resultSet ) throws SQLException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId( resultSet.getLong( "id" ) );
        utilisateur.setPassword( resultSet.getString( "mot_de_passe" ) );
        utilisateur.setName( resultSet.getString( "nomUtilisateur" ) );
        return utilisateur;
    }
}
