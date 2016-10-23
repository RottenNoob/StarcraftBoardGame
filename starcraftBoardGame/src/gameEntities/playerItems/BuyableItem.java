package gameEntities.playerItems;

public class BuyableItem implements java.io.Serializable {
	private static final long serialVersionUID = -3965770092884265767L;
	private int mineralCost = 0;
	private int gasCost = 0;
	
	public int getMineralCost() {
		return mineralCost;
	}
	public void setMineralCost(int mineralCost) {
		this.mineralCost = mineralCost;
	}
	public int getGasCost() {
		return gasCost;
	}
	public void setGasCost(int gasCost) {
		this.gasCost = gasCost;
	}
}
