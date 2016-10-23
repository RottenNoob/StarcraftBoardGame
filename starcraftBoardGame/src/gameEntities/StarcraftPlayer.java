package gameEntities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gameEntities.gameMap.Planet;
import gameEntities.gameMap.PlanetArea;
import gameEntities.methods.GameTurnHandler;
import gameEntities.playerItems.BuyableItem;
import gameEntities.playerItems.BuyingOrder;
import gameEntities.playerItems.CombatCard;
import gameEntities.playerItems.CombatCardAbility;
import gameEntities.playerItems.OrderToken;
import gameEntities.playerItems.StarcraftBuilding;
import gameEntities.playerItems.StarcraftUnit;
import gameEntities.playerItems.UnitPool;


public class StarcraftPlayer implements java.io.Serializable {
	private static final long serialVersionUID = -1695540066938569849L;
	// le nom d'une pool contenant les unités à placer au départ
	private String playerName;
	private String species;
	private String faction;
	private String factionImage;
	private String playerColor;
	private Set<String> leadershipcards = new HashSet<String>();
	private Map<String, Planet> planetDeck = new HashMap<String, Planet>();
	private Map<String, UnitPool> unitPools = new HashMap<String, UnitPool>();
	private int availableWorkers = 0;
	private int unavailableWorkers = 0;
	private ArrayList<OrderToken> ownedOrders = new ArrayList<OrderToken>();
	private Map<Integer, OrderToken> availableOrders = new HashMap<Integer, OrderToken>();
	private Boolean activeSpecialOrderBonus = false;
	private ArrayList<CombatCard> combatCardDeck = new ArrayList<CombatCard>();
	private Map<Integer, CombatCard> combatCardsInHand = new HashMap<Integer, CombatCard>();
	private ArrayList<CombatCard> dismissedCombatCards = new ArrayList<CombatCard>();
	private int mineralResources = 4;
	private int gasResources = 2;
	private int mineralToken = 0;
	private int gasToken = 0;
	private Map<List<Integer>, PlanetArea> areaResourceList = new HashMap<List<Integer>, PlanetArea>();
	private Set<String> unlockedUnits = new HashSet<String>();
	private ArrayList<StarcraftBuilding> availableBuildings = new ArrayList<StarcraftBuilding>();
	private ArrayList<StarcraftBuilding> ownedBuildings = new ArrayList<StarcraftBuilding>();
	private BuyingOrder buyingOrder;
	private int workerOnMineral = 0;
	private int workerOnGas = 0;
	private StarcraftUnit worker;
	private int unitBuildLimit = 2;
	private int unitBuilt = 0;
	
	public JSONObject getUnlockedBuildingsJS(StarcraftGame game, String action){
		JSONObject result = null;
		try {
			ArrayList<StarcraftBuilding> unlockedBuildings = new ArrayList<StarcraftBuilding>();
			for (StarcraftBuilding building:this.availableBuildings){
				if (building.getLevel() == 1){
					unlockedBuildings.add(building);
				}else{
					for (StarcraftBuilding builtBuilding:this.ownedBuildings){
						if (builtBuilding.getNumber() == building.getNumber()
								&& builtBuilding.getLevel() + 1 == building.getLevel()){
							unlockedBuildings.add(building);
							break;
						}
					}
				}
			}
			if (unlockedBuildings.size() > 0){
				JSONArray buildingListJS = new JSONArray();
				for (StarcraftBuilding building:unlockedBuildings){
					JSONArray unitListJS = new JSONArray();
					for (String unitName:building.getUnlockedUnits()){
						JSONObject unitJS =  new JSONObject()
								.put("image", GameConstants.getUnitImage(unitName));
						unitListJS.put(unitJS);
					}
					//batiment à rajouter
					JSONObject buildingJS = new JSONObject()
							.put("number", building.getNumber())
							.put("level", building.getLevel())
							.put("species", this.species)
							.put("color", this.playerColor)
							.put("unitList", unitListJS);
					buildingListJS.put(buildingJS);
				}
				result = new JSONObject()
						.put("action", action)
						.put("buildingList", buildingListJS);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void buildBuilding(){
		if (this.buyingOrder.getItem().getClass().getName().endsWith("StarcraftBuilding")){
			StarcraftBuilding building = (StarcraftBuilding) this.buyingOrder.getItem();
			//si le joueur est zerg, on regarde si le batiment permet des constructions supplémentaires d'unités
			if (this.species.equals("Zerg")){
				if (building.getLevel() == 1){
					this.unitBuildLimit +=2;
				}
			}
			for (String unitName:building.getUnlockedUnits()){
				this.unlockedUnits.add(unitName);
			}
			this.ownedBuildings.add(building);
			for (int i = 0; i < this.availableBuildings.size(); i++){
				if (this.availableBuildings.get(i).getNumber() == building.getNumber()
						&& this.availableBuildings.get(i).getLevel() == building.getLevel()){
					this.availableBuildings.remove(i);
					break;
				}
			}
		}
	}
	
	public StarcraftBuilding getBuilding(int number, int level){
		StarcraftBuilding result = null;
		for (StarcraftBuilding building:this.availableBuildings){
			if (building.getNumber() == number && building.getLevel() == level){
				result = building;
				break;
			}
		}
		return result;
	}
	
	/**envoie la liste des unités disponibles pour la construction sous forme JS**/
	public JSONObject getUnlockedUnitsJS(StarcraftGame game, String action){
		JSONObject result = null;
		try {
			JSONArray UnitListJS = new JSONArray();
			for (String unitName:this.unlockedUnits){
				StarcraftUnit unitToAdd = new StarcraftUnit(unitName, game.unitIdGenerator);
				JSONObject unitJS = null;
				if (this.unitBuildLimit > this.unitBuilt){
					if (game.getTurnPart().equals(GameConstants.buildUnitsTurnName)){
						if (!unitToAdd.getType().equals("base")){
							unitJS = new JSONObject()
									.put("name", unitName)
									.put("image", unitToAdd.getImage())
									.put("species", unitToAdd.getSpecies())
									.put("color", this.playerColor);
							UnitListJS.put(unitJS);
						}
					}else if (game.getTurnPart().equals(GameConstants.buildBaseTurnName)){
						if (unitToAdd.getType().equals("base")){
							unitJS = new JSONObject()
									.put("name", unitName)
									.put("image", unitToAdd.getImage())
									.put("species", unitToAdd.getSpecies())
									.put("color", this.playerColor);
							UnitListJS.put(unitJS);
							break;
						}
					}
				}else if (!unitToAdd.getType().equals("base") && !unitToAdd.getType().equals("mobile")
						&& game.getTurnPart().equals(GameConstants.buildUnitsTurnName)){
					unitJS = new JSONObject()
							.put("name", unitName)
							.put("image", unitToAdd.getImage())
							.put("species", unitToAdd.getSpecies())
							.put("color", this.playerColor);
					UnitListJS.put(unitJS);
				}
			}
			result = new JSONObject()
					.put("action", action)
					.put("unitList", UnitListJS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void executeBuyingOrder(){
		if (this.buyingOrder.getItem().getClass().getName().endsWith("StarcraftUnit")){
			StarcraftUnit unit = (StarcraftUnit) this.buyingOrder.getItem();
			if (unit.getType().equals("mobile")){
				this.unitBuilt++;
			}
		}else if (this.buyingOrder.getItem().getClass().getName().endsWith("StarcraftBuilding")){
			this.unitBuilt = this.unitBuildLimit;
		}
		this.buyingOrder = null;
	}
	
	/**annule la commande de construction est récupère les travailleurs déjà utilisés **/
	public void cancelBuyingOrder(StarcraftGame game){
		this.workerOnMineral -= this.buyingOrder.getWorkerOnBaseMineral();
		this.workerOnGas -= this.buyingOrder.getWorkerOnBaseGas();
		//récupération des travailleurs affectés à la base
		this.availableWorkers += this.buyingOrder.getWorkerOnBaseMineral() + this.buyingOrder.getWorkerOnBaseGas();
		GameTurnHandler.callDisplayWorkersOnBaseResources(this);
		for (List<Integer> coordinates:this.buyingOrder.getAffecedWorkers().keySet()){
			PlanetArea area = this.areaResourceList.get(coordinates);
			int returnedWorkers = this.buyingOrder.getAffecedWorkers().get(coordinates);
			area.setWorkerAmount(area.getWorkerAmount() - returnedWorkers);
			//récupération des travailleurs affectés à la zone
			this.availableWorkers += returnedWorkers;
			GameTurnHandler.updateAreaWorkerDisplay(coordinates, this, game);
		}
		String itemType = "";
		if (this.buyingOrder.getItem().getClass().getName().endsWith("StarcraftUnit")){
			this.unitPools.remove(game.getTurnPart());
			itemType = "StarcraftUnit";
		}else if (this.buyingOrder.getItem().getClass().getName().endsWith("StarcraftBuilding")){
			itemType = "StarcraftBuilding";
		}
		this.buyingOrder = null;
		GameTurnHandler.updateBaseWorkerDisplay(this);
		if (itemType.equals("StarcraftUnit")){
			GameTurnHandler.setBuildUnitStage(this.playerName, game);
		}else if (itemType.equals("StarcraftBuilding")){
			GameTurnHandler.setBuildBuildingStage(this.playerName, game);
		}
		
	}
	
	public void sendWorkerOnBaseMineral(){
		this.availableWorkers--;
		this.workerOnMineral++;
		this.buyingOrder.setWorkerOnBaseMineral(this.buyingOrder.getWorkerOnBaseMineral() + 1);
		this.buyingOrder.setSpentMineral(this.buyingOrder.getSpentMineral() + 1);
	}
	
	public void sendWorkerOnBaseGas(){
		this.availableWorkers--;
		this.workerOnGas++;
		this.buyingOrder.setWorkerOnBaseGas(this.buyingOrder.getWorkerOnBaseGas() + 1);
		this.buyingOrder.setSpentGas(this.buyingOrder.getSpentGas() + 1);
	}
	
	public void sendWorkerToArea(int x, int y, int z){
		this.availableWorkers--;
		PlanetArea area = this.areaResourceList.get(Arrays.asList(x, y, z));
		area.setWorkerAmount(area.getWorkerAmount() + 1);
		if (area.getMineralResources() > 0){
			this.buyingOrder.setSpentMineral(this.buyingOrder.getSpentMineral() + 1);
		}else{
			this.buyingOrder.setSpentGas(this.buyingOrder.getSpentGas() + 1);
		}
	}
	
	public Set<List<Integer>> getUsableResourceAreas(){
		Set<List<Integer>> result = new HashSet<List<Integer>>();
		if (this.availableWorkers > 0){
			if (this.buyingOrder.requireMineral()){
				for (List<Integer> coordinates:this.areaResourceList.keySet()){
					PlanetArea area = this.areaResourceList.get(coordinates);
					if (area.getMineralResources() > area.getWorkerAmount()){
						result.add(coordinates);
					}
				}
			}
			if (this.buyingOrder.requireGas()){
				for (List<Integer> coordinates:this.areaResourceList.keySet()){
					PlanetArea area = this.areaResourceList.get(coordinates);
					if (area.getGasResources() > area.getWorkerAmount()){
						result.add(coordinates);
					}
				}
			}
		}
		return result;
	}
	
	/**vérifie si il est possible d'affecter des travailleurs supplémentaires sur le minéral de base**/
	public Boolean canSetWorkerOnBaseMineral(){
		Boolean result = false;
		if (this.buyingOrder.requireMineral()){
			if (this.availableWorkers > 0 && buyingOrder!= null){
				if (this.workerOnMineral < this.mineralResources){
					result = true;
				}
			}
		}
		return result;
	}
	
	/**vérifie si il est possible d'affecter des travailleurs supplémentaires sur le gas de base**/
	public Boolean canSetWorkerOnBaseGas(){
		Boolean result = false;
		if (this.buyingOrder.requireGas()){
			if (this.availableWorkers > 0 && buyingOrder!= null){
				if (this.workerOnGas < this.gasResources){
					result = true;
				}
			}
		}
		return result;
	}
	
	//TODO
	public void setBuyingOrder(BuyableItem item){
		this.buyingOrder = new BuyingOrder();
		System.out.println(item.getClass().getName());
		buyingOrder.setItem(item);
	}
	
	public BuyingOrder getBuyingOrder(){
		return this.buyingOrder;
	}
	
	
	
	/**pioche des cartes de combat**/
	public void drawCombatCards(int number){
		for (int i = 0; i < number; i++){
			if (this.combatCardDeck.size() > 0){
				this.combatCardsInHand.put(this.combatCardDeck.get(0).getId(), this.combatCardDeck.get(0));
				this.combatCardDeck.remove(0);
			}else if (this.dismissedCombatCards.size()> 0){
				this.combatCardDeck.addAll(this.dismissedCombatCards);
				Collections.shuffle(this.combatCardDeck);
				this.dismissedCombatCards.removeAll(this.combatCardDeck);
				i--;
			}else{
				break;
			}
		}
		GameTurnHandler.callUpdateCardNumber(this);
	}
	
	/**initialise la liste des batiments constructibles**/
	public void initializeBuildings(){
		URL resources = getClass().getClassLoader().getResource("../../starcraftResources/buildingList.xml");
		try {
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/root/faction[@species =\""+ this.species +"\"]");
			Object o = expr.evaluate(doc, XPathConstants.NODE);
			Node selectedNode = (Node) o;
			NodeList list = selectedNode.getChildNodes();
			
			for (int i = 0; i < list.getLength(); i++){
				
				Node building = (Node) list.item(i);
				String nodeName = building.getNodeName();
				NamedNodeMap buildingAttributes = building.getAttributes();
				if (nodeName.equals("building")){
					StarcraftBuilding buildingToAdd = new StarcraftBuilding();
					NodeList propertyList = building.getChildNodes();
					buildingToAdd.setNumber(Integer.parseInt(buildingAttributes.getNamedItem("number").getNodeValue()));
					buildingToAdd.setLevel(Integer.parseInt(buildingAttributes.getNamedItem("level").getNodeValue()));
					for (int j = 0; j < propertyList.getLength(); j++){
						Node property = (Node) propertyList.item(j);
						String propertyName = property.getNodeName();
						NamedNodeMap propertyAttributes = property.getAttributes();
						if (propertyName.equals("cost")){
							if (propertyAttributes.getNamedItem("mineral")!=null){
								buildingToAdd.setMineralCost(Integer.parseInt(propertyAttributes.getNamedItem("mineral").getNodeValue()));
							}
							if (propertyAttributes.getNamedItem("gas")!=null){
								buildingToAdd.setGasCost(Integer.parseInt(propertyAttributes.getNamedItem("gas").getNodeValue()));
							}
						} else if (propertyName.equals("unlock")){
							buildingToAdd.getUnlockedUnits().add(propertyAttributes.getNamedItem("name").getNodeValue());
						}
					}
					if (buildingToAdd.getMineralCost() == 0 && buildingToAdd.getGasCost() == 0){
						this.ownedBuildings.add(buildingToAdd);
						this.unlockedUnits.addAll(buildingToAdd.getUnlockedUnits());
					}else{
						this.availableBuildings.add(buildingToAdd);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**met les cartes de combats de base dans le deck du joueur**/
	public void initializeCombatCardDeck(CardIdGenerator generator){
		URL resources = getClass().getClassLoader().getResource("../../starcraftResources/combatCards.xml");
		try {
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/root/faction[@species =\""+ this.species +"\"]");
			Object o = expr.evaluate(doc, XPathConstants.NODE);
			Node selectedNode = (Node) o;
			NodeList list = selectedNode.getChildNodes();
			
			for (int i = 0; i < list.getLength(); i++){
				
				Node card = (Node) list.item(i);
				String nodeName = card.getNodeName();
				if (nodeName.equals("card")){
					CombatCard cardToAdd = new CombatCard(generator);
					cardToAdd.setSpecies(this.species);
					cardToAdd.setColor(this.playerColor);
					cardToAdd.setOwner(this.playerName);
					NodeList propertyList = card.getChildNodes();
					for (int j = 0; j < propertyList.getLength(); j++){
						Node property = (Node) propertyList.item(j);
						String propertyName = property.getNodeName();
						NamedNodeMap propertyAttributes = property.getAttributes();
						if (propertyName.equals("unit")){
							cardToAdd.getUnitNames().add(propertyAttributes.getNamedItem("name").getNodeValue());
						} else if (propertyName.equals("text")){
							cardToAdd.setText(property.getTextContent());
						} else if (propertyName.equals("maxStat")){
							cardToAdd.setMaxAttack(Integer.parseInt(propertyAttributes.getNamedItem("attack").getNodeValue()));
							cardToAdd.setMaxDefense(Integer.parseInt(propertyAttributes.getNamedItem("health").getNodeValue()));
						} else if (propertyName.equals("minStat")){
							cardToAdd.setMinAttack(Integer.parseInt(propertyAttributes.getNamedItem("attack").getNodeValue()));
							cardToAdd.setMinDefense(Integer.parseInt(propertyAttributes.getNamedItem("health").getNodeValue()));
						} else if (propertyName.equals("condition")){
							cardToAdd.getRequirements().add(propertyAttributes.getNamedItem("name").getNodeValue());
						} else if (propertyName.equals("ability")){
							CombatCardAbility abilityToAdd = new CombatCardAbility();
							abilityToAdd.setName(propertyAttributes.getNamedItem("name").getNodeValue());
							if (propertyAttributes.getNamedItem("amount") != null){
								abilityToAdd.setAmount(Integer.parseInt(propertyAttributes.getNamedItem("amount").getNodeValue()));
							}
							NodeList abilityPropertyList = property.getChildNodes();
							for (int k = 0; k < abilityPropertyList.getLength(); k++){
								Node abilityProperty = (Node) abilityPropertyList.item(k);
								String abilityPropertyName = abilityProperty.getNodeName();
								NamedNodeMap abilityPropertyAttributes = abilityProperty.getAttributes();
								if (abilityPropertyName.equals("ally")){
									abilityToAdd.getAlliedUnitNames().add(abilityPropertyAttributes.getNamedItem("name").getNodeValue());
								} else if (abilityPropertyName.equals("enemy")){
									abilityToAdd.getHostileUnitNames().add(abilityPropertyAttributes.getNamedItem("name").getNodeValue());
								}
							}
							cardToAdd.addAbility(abilityToAdd);
							//vérifie que les cartes sont crées correctement
							/*System.out.println(abilityToAdd.getName());
							for (String unitName:abilityToAdd.getAlliedUnitNames()){
								System.out.println(unitName);
							}
							for (String unitName:abilityToAdd.getHostileUnitNames()){
								System.out.println(unitName);
							}*/
						}
					}
					if (cardToAdd.getName().equals("")){
						//seules les cartes du paquet technologie ont un nom
						this.combatCardDeck.add(cardToAdd);
						//System.out.println(cardToAdd.getCardJS("test").toString());
					}
				}
			}
			Collections.shuffle(this.combatCardDeck);
			if (this.species.equals("Terran")){
				this.drawCombatCards(8);
			}else{
				this.drawCombatCards(6);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeUnitFromPools(Long unitId){
		for (String poolName:this.unitPools.keySet()){
			if (this.unitPools.get(poolName).getUnitList().containsKey(unitId)){
				this.unitPools.get(poolName).removeUnit(unitId);
				break;
			}
		}
	}
	
	public String getName(){
		return this.playerName;
	}
	
	public void setName(String newName){
		this.playerName = newName;
	}
	
	public String getSpecies(){
		return this.species;
	}
	
	public void setSpecies(String species){
		this.species = species;
	}
	
	public String getFaction(){
		return this.faction;
	}
	
	public void setFaction(String faction){
		this.faction = faction;
	}
	
	public String getFactionImage(){
		return this.factionImage;
	}
	
	public void setFactionImage(String factionImage){
		this.factionImage = factionImage;
	}

	public Set<String> getLeadershipcards() {
		return leadershipcards;
	}
	
	public void initializeOwnedOrders(){
		this.ownedOrders.add(new OrderToken("move", this.playerName, false));
		this.ownedOrders.add(new OrderToken("move", this.playerName, false));
		this.ownedOrders.add(new OrderToken("move", this.playerName, true));
		this.ownedOrders.add(new OrderToken("build", this.playerName, false));
		this.ownedOrders.add(new OrderToken("build", this.playerName, false));
		this.ownedOrders.add(new OrderToken("build", this.playerName, true));
		this.ownedOrders.add(new OrderToken("research", this.playerName, false));
		this.ownedOrders.add(new OrderToken("research", this.playerName, false));
		this.ownedOrders.add(new OrderToken("research", this.playerName, true));
	}
	
	public void loadAvailableOrders(){
		this.availableOrders = new HashMap<Integer, OrderToken>();
		int i = 0;
		for (OrderToken order:this.ownedOrders){
			order.setId(i);
			i++;
			this.availableOrders.put(order.getId(), order);
		}
	}
	
	public Map<Integer, OrderToken> getAvailableOrders(){
		return this.availableOrders;
	}
	
	public void removeOrder(int id){
		this.availableOrders.remove(id);
	}
	
	/****/
	public void addUnitToPlayerPool(String poolTurn, StarcraftUnit unit){
		if (this.unitPools.containsKey(poolTurn)){
			this.unitPools.get(poolTurn).addUnit(unit);
		}else{
			UnitPool unitPool = new UnitPool();
			unitPool.setTurnPart(poolTurn);
			unitPool.addUnit(unit);
			this.unitPools.put(poolTurn, unitPool);
		}
	}

	/**ajoute la carte de leadership puis met en place ses effets (ajout d'unités et effets spéciaux)**/
	public void addLeadershipcards(String leadershipcard, StarcraftGame game) {
		//TODO mettre les effets spéciaux en place
		//augmentation des ressources de base
		if (leadershipcard.equals("Endless hunger")){
			this.mineralResources += 1;
			this.gasResources += 1;
		}else if (leadershipcard.equals("Orbital platform")){
			this.mineralResources += 2;
		}
		//
		initializeOwnedOrders();
		initializeBuildings();
		this.leadershipcards.add(leadershipcard);
		URL resources = getClass().getClassLoader().getResource("../../starcraftResources/leadershipCards.xml");
		try {
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/root/faction/card[@name =\""+ leadershipcard +"\"]");
			Object o = expr.evaluate(doc, XPathConstants.NODE);
			Node selectedNode = (Node) o;
			NodeList list = selectedNode.getChildNodes();
			
			for (int i = 0; i < list.getLength(); i++){
				Node property = (Node) list.item(i);
				String propertyName = property.getNodeName();
				if (propertyName.equals("itemBatch")){
					int unitNumber = Integer.parseInt(property.getAttributes().getNamedItem("number").getNodeValue());
					String unitName = property.getAttributes().getNamedItem("name").getNodeValue();
					StarcraftUnit starcraftunit = new StarcraftUnit(unitName, game.unitIdGenerator);
					starcraftunit.setOwner(this.playerName);
					starcraftunit.setColor(this.playerColor);
					starcraftunit.setStartingSituation(GameConstants.startingUnitSituation);
					String poolTurn = "";
					if (starcraftunit.getType().equals("base")){
						poolTurn = GameConstants.placeBaseTurnName;
					}else if(starcraftunit.getType().equals("worker")){
						this.availableWorkers += unitNumber;
						this.worker = starcraftunit;
					}else{
						poolTurn = "placeUnit";
					}
					if (!poolTurn.equals("")){
						if (this.unitPools.containsKey(poolTurn)){
							this.unitPools.get(poolTurn).addUnit(starcraftunit);
							for (int j = 1; j < unitNumber; j++){
								StarcraftUnit starcraftunitToAdd = new StarcraftUnit(unitName, game.unitIdGenerator);
								starcraftunitToAdd.setOwner(this.playerName);
								starcraftunitToAdd.setColor(this.playerColor);
								starcraftunitToAdd.setStartingSituation(GameConstants.startingUnitSituation);
								this.unitPools.get(poolTurn).addUnit(starcraftunitToAdd);
							}
						}else{
							UnitPool unitPool = new UnitPool();
							unitPool.setTurnPart(poolTurn);
							unitPool.addUnit(starcraftunit);
							for (int j = 1; j < unitNumber; j++){
								StarcraftUnit starcraftunitToAdd = new StarcraftUnit(unitName, game.unitIdGenerator);
								starcraftunitToAdd.setOwner(this.playerName);
								starcraftunitToAdd.setColor(this.playerColor);
								starcraftunitToAdd.setStartingSituation(GameConstants.startingUnitSituation);
								unitPool.addUnit(starcraftunitToAdd);
							}
							this.unitPools.put(poolTurn, unitPool);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadAvailableOrders();
	}

	public Map<String, UnitPool> getUnitPools() {
		return this.unitPools;
	}
	
	public Map<String, Planet> getPlanetDeck() {
		return this.planetDeck;
	}

	public void addPlanet(Planet planet) {
		this.planetDeck.put(planet.getName(), planet);
	}
	
	public void removePlanet(String planetName){
		this.planetDeck.remove(planetName);
	}

	public int getAvailableWorkers() {
		return availableWorkers;
	}

	public void setAvailableWorkers(int availableWorkers) {
		this.availableWorkers = availableWorkers;
	}

	public int getUnavailableWorkers() {
		return unavailableWorkers;
	}

	public void setUnavailableWorkers(int unavailableWorkers) {
		this.unavailableWorkers = unavailableWorkers;
	}

	public String getPlayerColor() {
		return playerColor;
	}

	public void setPlayerColor(String playerColor) {
		this.playerColor = playerColor;
	}

	public Boolean getActiveSpecialOrderBonus() {
		return activeSpecialOrderBonus;
	}

	public void setActiveSpecialOrderBonus(Boolean activeSpecialOrderBonus) {
		this.activeSpecialOrderBonus = activeSpecialOrderBonus;
	}

	public ArrayList<CombatCard> getDismissedCombatCards() {
		return dismissedCombatCards;
	}

	public Map<Integer, CombatCard> getCombatCardsInHand() {
		return combatCardsInHand;
	}

	public ArrayList<CombatCard> getCombatCardDeck() {
		return combatCardDeck;
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

	public int getMineralToken() {
		return mineralToken;
	}

	public void setMineralToken(int crystalToken) {
		this.mineralToken = crystalToken;
	}

	public int getGasToken() {
		return gasToken;
	}

	public void setGasToken(int gasToken) {
		this.gasToken = gasToken;
	}

	public Map<List<Integer>, PlanetArea> getAreaResourceList() {
		return areaResourceList;
	}

	public void addResourceArea(int x, int y, PlanetArea area){
		List<Integer> coordinates = Arrays.asList(x, y, area.getId());
		this.areaResourceList.put(coordinates, area);
	}

	public Set<String> getUnlockedUnits() {
		return unlockedUnits;
	}

	public ArrayList<StarcraftBuilding> getAvailableBuildings() {
		return availableBuildings;
	}

	public ArrayList<StarcraftBuilding> getOwnedBuildings() {
		return ownedBuildings;
	}

	public int getWorkerOnMineral() {
		return workerOnMineral;
	}

	public void setWorkerOnMineral(int workerOnMineral) {
		this.workerOnMineral = workerOnMineral;
	}

	public int getWorkerOnGas() {
		return workerOnGas;
	}

	public void setWorkerOnGas(int workerOnGas) {
		this.workerOnGas = workerOnGas;
	}

	public StarcraftUnit getWorker() {
		return worker;
	}

	public int getUnitBuildLimit() {
		return unitBuildLimit;
	}

	public int getUnitBuilt() {
		return unitBuilt;
	}

	public void setUnitBuilt(int unitBuilt) {
		this.unitBuilt = unitBuilt;
	}
}
