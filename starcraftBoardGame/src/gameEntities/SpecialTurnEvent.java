package gameEntities;

import java.util.ArrayList;

//classe gérant les tour irréguliers (généralement dus à des bonus donnant des actions supplémentaires
//à certains joueurs). Les évènements ne se déclenchent qu'une fois par partie
public class SpecialTurnEvent implements java.io.Serializable {

	private static final long serialVersionUID = -2812725133269889271L;
	private String triggeringPlayer;
	private String triggeringTurn;
	private ArrayList<String> newPlayerTurns = new ArrayList<String>();
	private String specialTurnName;
	
	public Boolean triggerSpecialTurn(String currentPlayer, String currentTurn){
		Boolean triggered = false;
		if (currentPlayer.equals(this.triggeringPlayer) && currentTurn.equals(this.triggeringTurn)){
			triggered = true;
		}
		return triggered;
	}


	public void setTriggeringPlayer(String triggeringPlayer) {
		this.triggeringPlayer = triggeringPlayer;
	}


	public void addNewPlayerTurn(String playerTurn) {
		this.newPlayerTurns.add(playerTurn);
	}
	
	public void addNewPlayerTurn(int index, String playerTurn) {
		this.newPlayerTurns.add(index, playerTurn);
	}

	public void setSpecialTurnName(String specialTurnName) {
		this.specialTurnName = specialTurnName;
	}


	public void setTriggeringTurn(String triggeringTurn) {
		this.triggeringTurn = triggeringTurn;
	}
	
	public ArrayList<String> getNewPlayerTurns(){
		return this.newPlayerTurns;
	}
	
	public String getSpecialTurnName(){
		return this.specialTurnName;
	}
	
}
