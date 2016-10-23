package gameEntities.gameMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PlanetArea implements java.io.Serializable {

	private static final long serialVersionUID = -5120708783976978825L;
	private int id;
	/**indique le type d'unités pouvant y aller**/
	private String areaType;
	private int unitLimit;
	private ArrayList<AreaResource> resourceList = new ArrayList<AreaResource>();
	private Set<Long> unitIdList = new HashSet<Long>();
	private int mineralResources = 0;
	private int gasResources = 0;
	private int conquestPoints = 0;
	private Boolean specialOrderResource = false;
	private int workerAmount = 0;
	
	public int getUnitLimit() {
		return unitLimit;
	}
	public void setUnitLimit(int unitLimit) {
		this.unitLimit = unitLimit;
	}
	
	/**indique le type d'unités pouvant y aller**/
	public String getAreaType() {
		return areaType;
	}
	public void setAreaType(String areaType) {
		this.areaType = areaType;
	}

	public void addResource(AreaResource resource){
		this.resourceList.add(resource);
		if (resource.getResourceType().equals("mineral")){
			this.mineralResources += resource.getResourceAmount();
		}else if (resource.getResourceType().equals("gas")){
			this.gasResources += resource.getResourceAmount();
		}else if (resource.getResourceType().equals("conquest")){
			this.conquestPoints += resource.getResourceAmount();
		}else{
			this.specialOrderResource = true;
		}
	}
	
	public ArrayList<AreaResource> getResources(){
		return this.resourceList;
	}
	
	public int getResourceAmount(String resourceType){
		int result = 0;
		if (!resourceList.isEmpty()){
			for (AreaResource resource:this.resourceList){
				if (resource.getResourceType().equals(resourceType) && resource.getExhaustion() < 2){
					result += resource.getResourceAmount();
				}
			}
		}
		return result;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Set<Long> getUnitIdList() {
		return unitIdList;
	}
	
	public void addUnitId(long unitId){
		this.unitIdList.add(unitId);
	}
	
	public void removeUnitId(long unitId){
		this.unitIdList.remove(unitId);
	}
	public int getMineralResources() {
		return mineralResources;
	}
	public void setMineralResources(int crystalResources) {
		this.mineralResources = crystalResources;
	}
	public int getGasResources() {
		return gasResources;
	}
	public void setGasResources(int gasResources) {
		this.gasResources = gasResources;
	}
	public int getConquestPoints() {
		return conquestPoints;
	}
	public void setConquestPoints(int conquestPoints) {
		this.conquestPoints = conquestPoints;
	}
	public Boolean getSpecialOrderResource() {
		return specialOrderResource;
	}
	public int getWorkerAmount() {
		return workerAmount;
	}
	public void setWorkerAmount(int workerAmount) {
		this.workerAmount = workerAmount;
	}
}
