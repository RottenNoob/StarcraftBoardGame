package gameEntities.gameMap;

public class AreaResource implements java.io.Serializable {

	private static final long serialVersionUID = -4537940926132762234L;
	private String resourceType;
	private int resourceAmount;
	private int exhaustion= 0;
	
	public int getResourceAmount() {
		return resourceAmount;
	}
	public void setResourceAmount(int resourceAmount) {
		this.resourceAmount = resourceAmount;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public int getExhaustion() {
		return exhaustion;
	}
	public void increaseExhaustion() {
		this.exhaustion++;
	}
	
	public void decreaseExhaustion() {
		this.exhaustion--;
	}
}
