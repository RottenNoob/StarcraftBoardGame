package gameEntities.playerItems;

import java.util.ArrayList;

public class CombatCardAbility implements java.io.Serializable {

	private static final long serialVersionUID = -7466986278296050475L;
	private String name;
	private int amount = 0;
	private ArrayList<String> alliedUnitNames = new ArrayList<String>();
	private ArrayList<String> hostileUnitNames = new ArrayList<String>();
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public ArrayList<String> getAlliedUnitNames() {
		return alliedUnitNames;
	}
	public void setAlliedUnitNames(ArrayList<String> alliedUnitNames) {
		this.alliedUnitNames = alliedUnitNames;
	}
	public ArrayList<String> getHostileUnitNames() {
		return hostileUnitNames;
	}
	public void setHostileUnitNames(ArrayList<String> hostileUnitNames) {
		this.hostileUnitNames = hostileUnitNames;
	}
	

}
