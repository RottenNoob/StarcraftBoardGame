package gameEntities.playerItems;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.UnitIdGenerator;
import gameEntities.gameMap.Planet;
import gameEntities.gameMap.PlanetArea;
import gameEntities.gameMap.RoadLink;
import gameEntities.gameMap.StarcraftBattle;


public class StarcraftUnit extends BuyableItem {
	private static final long serialVersionUID = -474347218964355172L;
	//donne une id unique à chaque unité
    private long id;
    
	private String name;
	private String species;
	private String owner;

	private String type;
	private String image;
	private String moveType;
	private String attackType;
	private String unitCost;
	private int unitCostNumber;
	private int attackSupport = -1;
	private Set<String> abilities = new HashSet<String>();
	//ce sont les coordonnées de l'unité avant leur placement, sert uniquement à déterminer
	//les déplacements valides
	private int[] oldCoordinates = new int[]{0, 0, 0};
	////coordonnées actuelles de l'unité, sert à l'affichage
	private int[] coordinates = new int[]{0, 0, 0};
	// la situation de départ indique dans quelle genre de pool est l'unité ou si elle est sur la carte
	private String startingSituation;
	private String color;
	
	//description unités sous format Json
	public JSONObject returnStarcraftUnitJson(String action){
		JSONObject result = null;
		try {
			result = new JSONObject()
					.put("action", action)
					.put("id", this.id)
					.put("species", this.species)
					.put("image", this.image)
					.put("type", this.type)
					.put("xPosition", this.coordinates[0])
					.put("yPosition", this.coordinates[1])
					.put("areaId", this.coordinates[2])
					.put("color", this.color);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public StarcraftUnit(String unitName, UnitIdGenerator generator){
		this.name = unitName;
		this.id = generator.getNextValue();
		URL resources = getClass().getClassLoader().getResource("../../starcraftResources/unitList.xml");
		try {
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/root/faction/unit[@name =\""+ unitName +"\"]");
			Object o = expr.evaluate(doc, XPathConstants.NODE);
			Node selectedNode = (Node) o;
			NodeList list = selectedNode.getChildNodes();
			Element unit = (Element) selectedNode;
			this.type = unit.getAttribute("type");
			this.image = unit.getAttribute("img");
			
			XPathExpression factionExpr = xpath.compile("/root/faction[count(unit[@name =\""+ unitName +"\"]) > 0]/@species");
			Object factionObject = factionExpr.evaluate(doc, XPathConstants.STRING);
			this.species = (String) factionObject;
			//System.out.println(this.species + " : " + this.name);
			
			for (int i = 0; i < list.getLength(); i++){
				Node property = (Node) list.item(i);
				String propertyName = property.getNodeName();
				NamedNodeMap attributes = property.getAttributes();
				if (propertyName.equals("cost")){
					if (attributes.getNamedItem("mineral")!=null){
						setMineralCost(Integer.parseInt(attributes.getNamedItem("mineral").getNodeValue()));
					}
					if (attributes.getNamedItem("gas")!=null){
						setGasCost(Integer.parseInt(attributes.getNamedItem("gas").getNodeValue()));
					}
				} else if (propertyName.equals("move")){
					this.moveType = attributes.getNamedItem("type").getNodeValue();
				} else if (propertyName.equals("attack")){
					this.attackType = attributes.getNamedItem("type").getNodeValue();
				} else if (propertyName.equals("support")){
					this.attackSupport = Integer.parseInt(attributes.getNamedItem("attack").getNodeValue());
				} else if (propertyName.equals("ability")){
					this.abilities.add(attributes.getNamedItem("name").getNodeValue());
				} else if (propertyName.equals("unitCost")){
					this.unitCost = attributes.getNamedItem("name").getNodeValue();
					this.unitCostNumber = Integer.parseInt(attributes.getNamedItem("number").getNodeValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getImage() {
		return image;
	}

	public String getType() {
		return type;
	}

	public int getAttackSupport() {
		return attackSupport;
	}

	public String getAttackType() {
		return attackType;
	}

	public String getMoveType() {
		return moveType;
	}

	public Set<String> getAbilities() {
		return this.abilities;
	}

	public String getUnitCost() {
		return unitCost;
	}

	public int getUnitCostNumber() {
		return unitCostNumber;
	}


	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getSpecies() {
		return species;
	}

	public int[] getOldCoordinates() {
		return oldCoordinates;
	}

	public void setOldCoordinates(int[] oldCoordinates) {
		this.oldCoordinates = oldCoordinates;
	}

	public int[] getCoordinates() {
		return coordinates;
	}

	/**met à jour les coordonnées de l'unité et met à jour les différentes zones de la galaxie**/
	public void setCoordinates(int[] coordinates, StarcraftGame game){
		//si il s'agit d'un transport, on change les RoadLinks corresondant
		if (this.type.equals("transport")){
			// on vérifie que les coordonnées correspondaient à un lien existant
			RoadLink oldLink = game.getGalaxy().getLinkFromCoordinates(this.coordinates);
			if (oldLink != null){
				if (oldLink.getUnitIdList().contains(this.id)){
					oldLink.removeUnitId(this.id);
				}
			}
			RoadLink newLink = game.getGalaxy().getLinkFromCoordinates(coordinates);
			newLink.addUnitId(this.id);
		}else{
			//sinon, on change les zones correspondantes
			Planet oldPlanet = game.getGalaxy().returnPlanetAt(coordinates[0], coordinates[1]);
			if (oldPlanet != null){
				PlanetArea oldArea = game.getGalaxy().returnPlanetAt(this.coordinates[0], this.coordinates[1]).getArea(this.coordinates[2]);
				if (oldArea.getUnitIdList().contains(this.id)){
					oldArea.removeUnitId(this.id);
				}
			}
			//si l'unité est participe déjà à une bataille, on ne peut pas annuler une bataille
			if (!this.startingSituation.equals("inBattle")){
				//si il y avait une bataille et qu'enlever l'uniter annule la bataille
				if (game.getGalaxy().getStarcraftBattle() != null){
					StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
					if (starcraftBattle.getCoordinates()[0] == this.coordinates[0]
							&& starcraftBattle.getCoordinates()[1] == this.coordinates[1]
									&& starcraftBattle.getCoordinates()[2] == this.coordinates[2]){
						starcraftBattle.removeAttackingUnit(this.id);
						starcraftBattle.removeUnplacedUnit(this.id);
						if (starcraftBattle.getAttackingUnits().size() < 1){
							game.getGalaxy().setStarcraftBattle(null);
						}
					}
				}
			}
			PlanetArea newArea = game.getGalaxy().returnPlanetAt(coordinates[0], coordinates[1]).getArea(coordinates[2]);
			newArea.addUnitId(this.id);
			//si l'unité est participe déjà à une bataille, on ne peut pas créer une bataille
			if (!this.startingSituation.equals("inBattle")){
				StarcraftPlayer starcraftPlayer = game.getPlayer(this.owner);
				//si des unités ennemies sont sur place, on déclenche une bataille ou on rajoute une unité à la bataille en cours
				if (game.getGalaxy().countEnemyUnitsInPlace(coordinates[0], coordinates[1], coordinates[2], "", starcraftPlayer) > 0){
					if (game.getGalaxy().getStarcraftBattle() != null){
						StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
						if (starcraftBattle.getCoordinates()[0] == coordinates[0]
								&& starcraftBattle.getCoordinates()[1] == coordinates[1]
										&& starcraftBattle.getCoordinates()[2] == coordinates[2]){
							starcraftBattle.addUnplacedUnit(this);
							starcraftBattle.addAttackingUnit(this.id);
						}else{
							try {
								throw new Exception("A mistake happened during battle setup");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}else{
						StarcraftBattle starcraftBattle = new StarcraftBattle();
						starcraftBattle.setAttackingPlayer(starcraftPlayer);
						starcraftBattle.addUnplacedUnit(this);
						starcraftBattle.addAttackingUnit(this.id);
						starcraftBattle.setCoordinates(coordinates);
						game.getGalaxy().setStarcraftBattle(starcraftBattle);
					}
				}
			}
		}
		this.coordinates = coordinates;
	}

	public String getStartingSituation() {
		return startingSituation;
	}

	public void setStartingSituation(String startingSituation) {
		this.startingSituation = startingSituation;
	}

	public long getId(){
		return this.id;
	}

	public void updateOldCoordinates(){
		System.arraycopy( this.coordinates, 0, this.oldCoordinates, 0, this.coordinates.length );
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}


}
