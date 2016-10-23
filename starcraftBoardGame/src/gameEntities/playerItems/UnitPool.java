package gameEntities.playerItems;

import java.util.HashMap;
import java.util.Map;

public class UnitPool implements java.io.Serializable {
	private static final long serialVersionUID = 7359020153013939381L;
	private String turnPart;
	private Map<Long, StarcraftUnit> unitList = new HashMap<Long, StarcraftUnit>();

	public String getTurnPartPool() {
		return turnPart;
	}

	public void setTurnPart(String placingTurn) {
		this.turnPart = placingTurn;
	}

	public Map<Long, StarcraftUnit> getUnitList() {
		return unitList;
	}

	public void addUnit(StarcraftUnit unit) {
		this.unitList.put(unit.getId(), unit);
	}
	
	public void removeUnit(long unitId) {
		if (this.unitList.containsKey(unitId)){
			this.unitList.remove(unitId);
		}
	}
	
}
