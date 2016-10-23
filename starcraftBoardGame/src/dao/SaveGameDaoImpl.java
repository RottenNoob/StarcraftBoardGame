package dao;

import static dao.DAOUtilitaire.fermeturesSilencieuses;
import static dao.DAOUtilitaire.initialisationRequetePreparee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import entities.SaveGame;

public class SaveGameDaoImpl implements SaveGameDao {

    private DAOFactory          daoFactory;
    private static final String SQL_SELECT_PAR_ID = "SELECT id, gameName FROM SaveGame WHERE id = ?";
    private static final String SQL_INSERT = "INSERT INTO SaveGame (gameName) VALUES (?)";

    
    SaveGameDaoImpl( DAOFactory daoFactory ) {
        this.daoFactory = daoFactory;
    }
	
	@Override
	public void creer(SaveGame saveGame) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet valeursAutoGenerees = null;
        
        try {
            /* Récupération d'une connexion depuis la Factory */
            connexion = daoFactory.getConnection();
            preparedStatement = initialisationRequetePreparee( connexion, SQL_INSERT, true, saveGame.getName());
            int statut = preparedStatement.executeUpdate();
            /* Analyse du statut retourné par la requête d'insertion */
            if ( statut == 0 ) {
                throw new DAOException( "échec de la création de l'utilisateur, aucune ligne ajoutée dans la table." );
            }
            /* Récupération de l'id auto-généré par la requête d'insertion */
            valeursAutoGenerees = preparedStatement.getGeneratedKeys();
            if ( valeursAutoGenerees.next() ) {
                /* Puis initialisation de la propriété id du bean Utilisateur avec sa valeur */
            	saveGame.setId( valeursAutoGenerees.getLong( 1 ) );
            } else {
                throw new DAOException( "échec de la création de l'utilisateur en base, aucun ID auto-généré retourné." );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( valeursAutoGenerees, preparedStatement, connexion );
        }

	}

	@Override
	public void supprimer(SaveGame saveGame) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Override
	public SaveGame trouver(long id) throws DAOException {
		return trouver( SQL_SELECT_PAR_ID, id );
	}

    /*
     * Méthode générique utilisée pour retourner un utilisateur depuis la base
     * de données, correspondant à la requête SQL donnée prenant en paramﾃｨtres
     * les objets passés en argument.
     */
    private SaveGame trouver( String sql, Object... objets ) throws DAOException {
        Connection connexion = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        SaveGame saveGame = null;

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
            	saveGame = map( resultSet );
            }
        } catch ( SQLException e ) {
            throw new DAOException( e );
        } finally {
            fermeturesSilencieuses( resultSet, preparedStatement, connexion );
        }

        return saveGame;
    }
	
	
    /*
     * Simple méthode utilitaire permettant de faire la correspondance (le
     * mapping) entre une ligne issue de la table des utilisateurs (un
     * ResultSet) et un bean Utilisateur.
     */
    private static SaveGame map( ResultSet resultSet ) throws SQLException {
    	SaveGame saveGame = new SaveGame();
        saveGame.setId( resultSet.getLong( "id" ) );
        saveGame.setName( resultSet.getString( "saveName" ) );
        return saveGame;
    }
}
