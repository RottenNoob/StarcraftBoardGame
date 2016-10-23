package dao;

import entities.Utilisateur;

public interface UtilisateurDao {

    void creer( Utilisateur utilisateur ) throws DAOException;

    Utilisateur trouver( String nom ) throws DAOException;

}