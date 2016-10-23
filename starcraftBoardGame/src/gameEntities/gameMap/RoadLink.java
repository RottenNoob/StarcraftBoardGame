package gameEntities.gameMap;

import java.util.HashSet;
import java.util.Set;

public class RoadLink implements java.io.Serializable {

	private static final long serialVersionUID = 5182438333042530518L;
	private int[] coordinates1;
	private int[] coordinates2;
	/**normal or zLink**/
	private String linkType;
	private Set<Long> unitIdList = new HashSet<Long>();
	

	@Override
	public String toString(){
		String result ="coordinates 1 : ";
		String result2 ="coordinates 2 : ";
		for (int i = 0; i<3; i++){
			result+= Integer.toString(coordinates1[i]) + " , ";
			result2+= Integer.toString(coordinates2[i]) + " , ";
		}
		return result + System.lineSeparator() + result2;
	}
	public int[] getCoordinates1() {
		return coordinates1;
	}
	public void setCoordinates1(int[] coordinates1) {
		this.coordinates1 = coordinates1;
	}
	public int[] getCoordinates2() {
		return coordinates2;
	}
	public void setCoordinates2(int[] coordinates2) {
		this.coordinates2 = coordinates2;
	}
	public String getLinkType() {
		return linkType;
	}
	public void setLinkType(String linkType) {
		this.linkType = linkType;
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
}
