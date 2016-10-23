package entities;
import static java.lang.Math.toIntExact;


public class Utilisateur {
    private Long      id;
    private String    motDePasse;
    private String nomUtilisateur;
    
    public Long getId() {
        return id;
    }
    public void setId( Long id ) {
        this.id = id;
    }
    
    public void setName( String nom ) {
        this.nomUtilisateur = nom;
    }
    public String getName() {
        return this.nomUtilisateur;
    }
    
    public void setPassword( String motDePasse ) {
        this.motDePasse = motDePasse;
    }
    public String getPassword() {
        return motDePasse;
    }
    
    @Override
    public int hashCode() {
    	int newHash = nomUtilisateur.hashCode() * 37 + toIntExact(id);
    	return newHash;
    	
    }

    
    @Override
    public boolean equals(Object user) {
    	boolean equality = true;
    	if (!(user instanceof Utilisateur)){
    		equality = false;
    	}else{
        	Utilisateur compareUser = (Utilisateur) user;
        	if (!(this.id == compareUser.getId() && this.nomUtilisateur.equals(compareUser.getName()))){
        		equality = false;
        	}
    	}

    	return equality;
    }

}
