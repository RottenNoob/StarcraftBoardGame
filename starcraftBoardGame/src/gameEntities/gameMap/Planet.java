package gameEntities.gameMap;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Planet implements java.io.Serializable {

	private static final long serialVersionUID = -4077716656826684330L;
	//donne une id unique à chaque zone de la planète pour pouvoir intéragir avec(ces ids se répètent entre différentes planètes)
	private int areaId = 0;
	private String name;
	private int rotation = 0;
	private int xPosition = 0;
	private int yPosition = 0;
	//tableau décrivant les différentes routes connectées à la planète
	private int[] roads = new int[4];
	private ArrayList<PlanetArea> areaList = new ArrayList<PlanetArea>();


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		int newRotation = (this.rotation + rotation)%4;
		int[] oldRoads =  new int[4];
		System.arraycopy( this.roads, 0, oldRoads, 0, this.roads.length );
		for (int i = 0; i < 4; i++){
			this.roads[i] = oldRoads[(i - rotation)%4];
		}
		this.rotation = newRotation;
	}
	
	public void incrementRotation() {
		int[] oldRoads =  new int[4];
		System.arraycopy( this.roads, 0, oldRoads, 0, this.roads.length );
		for (int i = 0; i < 4; i++){
			// on ajoute 4 pour éviter les nombres négatifs qui ne sont pas gérés par le modulo
			int index = (i - 1 + 4)%4;
			this.roads[i] = oldRoads[index];
		}
		this.rotation++;
	}
	
	/**renvoie les coordonnées de toutes les places auxquelles mènent les routes de la planète**/
	public ArrayList<int[]> neighbourCoordinates(){
		ArrayList<int[]> result = new ArrayList<int[]>();
		for (int i = 0; i < 4; i++){
			if (this.roads[i] == 1){
				if (i == 0){
					result.add(new int[]{xPosition, yPosition - 1, 2});
				}else if (i == 1){
					result.add(new int[]{xPosition + 1, yPosition, 3});
				}else if (i == 2){
					result.add(new int[]{xPosition , yPosition +1, 0});
				}else{
					result.add(new int[]{xPosition - 1, yPosition, 1});
				}
			}
		}
		return result;
	}
	
	
	public void addRoad(int index){
		this.roads[index] = 1;
	}
	
	public int[] returnRoads(){
		return this.roads;
	}
	
	/**renvoie la position des routes présentes sur la planète**/
	public ArrayList<Integer> getRoadPositions(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < 4; i++){
			if (this.roads[i] == 1){
				result.add(i);
			}
		}
		return result;
	}
	
	public ArrayList<PlanetArea> getAreaList(){
		return this.areaList;
	}
	
	public void addArea(PlanetArea area){
		area.setId(this.areaId);
		areaId++;
		this.areaList.add(area);
	}
	
	//description des routes de la planète sous format Json
	public JSONObject returnRoadPositionJson(String action){
		JSONObject result = null;
		try {
			JSONArray roadArray = new JSONArray();
			for (int i = 0; i < 4; i++){
				if (this.roads[i] == 1){
					JSONObject roadJS;

					roadJS = new JSONObject()
							.put("number", i);

					roadArray.put(roadJS);
				}
			}
			result = new JSONObject()
					.put("action", action)
					.put("name", this.name)
					.put("roadList", roadArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	//description de la planète sous format Json
	public JSONObject returnActionJson(String action){
		JSONObject result = null;
		try {
			JSONArray roadArray = new JSONArray();
			for (int i = 0; i < 4; i++){
				if (this.roads[i] == 1){
					JSONObject roadJS = new JSONObject()
							.put("number", i);
					roadArray.put(roadJS);
				}
			}
			JSONArray areaArray = new JSONArray();
			for (PlanetArea area:this.areaList){
				JSONArray resourceArray = new JSONArray();
				for (AreaResource resource:area.getResources()){
					JSONObject resourceJS = new JSONObject()
							.put("resourceType", resource.getResourceType())
							.put("amount", resource.getResourceAmount());
					resourceArray.put(resourceJS);
				}
				JSONObject areaJS = new JSONObject()
						.put("id", area.getId())
						.put("areaType", area.getAreaType())
						.put("unitLimit", area.getUnitLimit())
						.put("resources", resourceArray);
				areaArray.put(areaJS);
			}
			result = new JSONObject()
					.put("action", action)
					.put("name", this.name)
					.put("xPosition", this.xPosition)
					.put("yPosition", this.yPosition)
					.put("areaList", areaArray)
					.put("roadList", roadArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public int getX() {
		return xPosition;
	}

	public void setX(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public void setY(int yPosition) {
		this.yPosition = yPosition;
	}

	public PlanetArea getArea(int areaId){
		PlanetArea result = null;
		for (PlanetArea area:this.areaList){
			if (area.getId() == areaId){
				result = area;
				break;
			}
		}
		return result;
	}
}
