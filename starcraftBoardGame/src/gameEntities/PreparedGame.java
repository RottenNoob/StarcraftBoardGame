package gameEntities;

import java.util.HashSet;
import java.util.Set;

import entities.Utilisateur;

public class PreparedGame {
	private Utilisateur host;
	private Set<Utilisateur> playerList = new HashSet<Utilisateur>();
	
	
    public void setHost( Utilisateur host ) {
        this.host = host;
        addPlayer(host);
    }
    public Utilisateur getHost() {
        return this.host;
    }
    
    public void addPlayer(Utilisateur player){
    	this.playerList.add(player);
    }
    
    public Set<Utilisateur> getPlayerList(){
    	return this.playerList;
    }
    
    public void remove(Utilisateur player){
    	this.playerList.remove(player);
    }
}
