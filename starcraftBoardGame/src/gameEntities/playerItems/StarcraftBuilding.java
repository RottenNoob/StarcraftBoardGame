package gameEntities.playerItems;

import java.util.HashSet;
import java.util.Set;

public class StarcraftBuilding extends BuyableItem {

	private static final long serialVersionUID = -2475133492521104162L;
	private int number;
	private int level;
	
	private Set<String> unlockedUnits = new HashSet<String>();
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public Set<String> getUnlockedUnits() {
		return unlockedUnits;
	}
}
