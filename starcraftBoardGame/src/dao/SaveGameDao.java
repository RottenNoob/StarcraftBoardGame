package dao;

import entities.SaveGame;

public interface SaveGameDao {

    void creer( SaveGame saveGame ) throws DAOException;
    
    void supprimer( SaveGame saveGame ) throws DAOException;

    SaveGame trouver( long id ) throws DAOException;

}
