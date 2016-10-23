package gameEntities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import entities.SaveGame;
import gameEntities.gameMap.PlanetArea;
import gameEntities.gameMap.StarcraftBattle;
import gameEntities.methods.GameTurnHandler;
import gameEntities.playerItems.OrderToken;
import gameEntities.playerItems.StarcraftUnit;
import gameEntities.playerItems.UnitPool;
import gameEntities.gameMap.AreaResource;
import gameEntities.gameMap.Galaxy;
import gameEntities.gameMap.Planet;

public class StarcraftGame implements java.io.Serializable {

	private static final long serialVersionUID = 4640965522133792796L;
	private SaveGame      saveGame;
	private Map<String, StarcraftPlayer> playerList = new HashMap<String, StarcraftPlayer>();
	private ArrayList<String> playerTurns = new ArrayList<String>();
	/**liste des joueurs ayant des trours spéciaux**/
	private ArrayList<String> playerSpecialTurns = new ArrayList<String>();
	private int currentTurn = 0;
	private String turnName;
	private ArrayList<String> specialTurnNames = new ArrayList<String>();
	// indique l'époque du jeu
	private int age = 1;
	private Galaxy galaxy = new Galaxy();
	private List<Planet> planetDeck = Collections.synchronizedList(new ArrayList<Planet>());
	private ArrayList<SpecialTurnEvent> speciaTurnEvents = new ArrayList<SpecialTurnEvent>();
	private ArrayList<String> turnPart = new ArrayList<String>();
	private OrderToken currentOrder;
	private Map<List<Integer>, PlanetArea> alreadyOwnedResourceAreas = new HashMap<List<Integer>, PlanetArea>();
	public UnitIdGenerator unitIdGenerator = new UnitIdGenerator();
	public CardIdGenerator cardIdGenerator = new CardIdGenerator();
	
	/**distribue les zones de ressources aux différents joueurs**/
	public void setPlayerResources(){
		for (String playerName:this.playerList.keySet()){
			StarcraftPlayer starcraftPlayer = this.playerList.get(playerName);
			for (String planetName:this.galaxy.getAllPlanets().keySet()){
				Planet currentPlanet = this.galaxy.getAllPlanets().get(planetName);
				if (this.galaxy.unitOnPlanet(starcraftPlayer, currentPlanet, "base")){
					for (PlanetArea area:currentPlanet.getAreaList()){
						List<Integer> coordinates = Arrays.asList(currentPlanet.getX(), currentPlanet.getY(), area.getId());
						if (!this.alreadyOwnedResourceAreas.containsKey(coordinates)){
							this.alreadyOwnedResourceAreas.put(coordinates, area);
							starcraftPlayer.addResourceArea(currentPlanet.getX(), currentPlanet.getY(), area);
						}
					}
				}
			}
		}
	}
	
	public void removeTurnPart(){
		if (this.turnPart.size() > 0){
			this.turnPart.remove(0);
		}
	}
	
	public void addSpecialTurn(String playerName, String playerTurn){
		this.playerSpecialTurns.add(0, playerName);
		this.specialTurnNames.add(0, playerTurn);
	}
	
	public void addSpecialTurnEnd(String playerName, String playerTurn){
		this.playerSpecialTurns.add(playerName);
		this.specialTurnNames.add(playerTurn);
	}
	
    // cette fonction initialise la partie et
    // est appelée une unique fois : au moment de la création de la partie
    public void initializeGame(){
    	setRandomTurns();
    	setPlanetDeck();
    	turnName = "factionChoice";
    }
    
    private void setRandomTurns(){
    	for (String playerNames : playerList.keySet()){
    		playerTurns.add(playerNames);
    	}
    	Collections.shuffle(playerTurns);
    }
    
    /**exécuter l'ordre en cours**/
    public void executeCurrentOrder() {
    	StarcraftPlayer starcraftPlayer = this.playerList.get(this.getPlayerCurrentlyPlaying());
    	if (this.currentOrder.getSpecial()){
    		starcraftPlayer.setActiveSpecialOrderBonus(true);
    	}else{
    		starcraftPlayer.setActiveSpecialOrderBonus(false);
    	}
    	if (this.currentOrder.getName().equals("move")){
    		this.turnPart.add(GameConstants.moveUnitTurnName);
    	}else if (this.currentOrder.getName().equals("build")){
    		Planet currentPlanet = this.galaxy.getAllPlanets().get(this.galaxy.getPlanetEvent());
    		if (this.galaxy.unitOnPlanet(starcraftPlayer, currentPlanet, "base")){
    			this.turnPart.add(GameConstants.buildUnitsTurnName);
    			this.turnPart.add(GameConstants.buildBuildingsTurnName);
    		}else{
    			this.turnPart.add(GameConstants.buildBuildingsTurnName);
    			this.turnPart.add(GameConstants.buildBaseTurnName);
    		}
    	}
	}
      
    /**exécute la pile d'ordre situé sur les coordonnées choisies, si l'ordre ne peut être exécuté, on ne fait rien ce qui entraine
     * le passage au tour suivant**/
    public void executeOrderAt(String coordinates){
    	int separatorIndex = coordinates.indexOf('.');
		int xCoord  = Integer.parseInt(coordinates.substring(0, separatorIndex));
		int yCoord  = Integer.parseInt(coordinates.substring(separatorIndex + 1));
		Planet currentPlanet = this.galaxy.returnPlanetAt(xCoord, yCoord);
    	this.galaxy.setPlanetEvent(currentPlanet.getName());
    	OrderToken order = this.galaxy.getOrderList().get(coordinates).get(0);
    	StarcraftPlayer starcraftPlayer = this.playerList.get(this.getPlayerCurrentlyPlaying());
    	if (order.getName().equals("research")){
    		this.currentOrder = order;
    		this.turnPart.add(GameConstants.executeChoiceTurnName);
    	}else if (order.getName().equals("build")){
    		if (this.galaxy.unitOnPlanet(starcraftPlayer, currentPlanet, "base")
    				|| this.galaxy.unitOnPlanet(starcraftPlayer, currentPlanet, "mobile")){
    			this.turnPart.add(GameConstants.executeChoiceTurnName);
    			this.currentOrder = order;
    		}else{
    			//TODO faire pioche de carte évènement
    		}
    	}else if (order.getName().equals("move")){
    		Boolean hasValidUnit = false;
    		if (this.galaxy.unitOnPlanet(starcraftPlayer, currentPlanet, "mobile")){
    			hasValidUnit = true;
    		}else{
    			for (Planet planet:this.galaxy.getLinkedPlanets(currentPlanet, this.getPlayerCurrentlyPlaying())){
    				if (this.galaxy.unitOnPlanet(starcraftPlayer, planet, "mobile")){
    	    			hasValidUnit = true;
    	    			break;
    	    		}
        		}
    		}
    		if (hasValidUnit){
    			this.currentOrder = order;
    			this.turnPart.add(GameConstants.executeChoiceTurnName);
    		}else{
    			//TODO faire pioche de carte évènement
    		}
    	}
    	try {
			this.galaxy.removeOrder(coordinates);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //TODO prendre en compte les déplacements des bases terran
    /**renvoie toutes les unités sélectionnables par le joueur**/
    public ArrayList<Long> returnAllActiveUnits(){
    	ArrayList<Long> result = new ArrayList<Long>();
    	StarcraftPlayer activePlayer = this.playerList.get(getPlayerCurrentlyPlaying());
    	if (activePlayer.getUnitPools().containsKey(this.getTurnPart())){
    		UnitPool activePool = activePlayer.getUnitPools().get(this.getTurnPart());
    		for (long unitIdPool:activePool.getUnitList().keySet()){
    			result.add(unitIdPool);
        	}
    	}
    	if (this.getTurnPart().equals(GameConstants.placeBaseTurnName) || this.getTurnPart().equals("placeUnit")){
    		for (long unitId:this.galaxy.getUnitList().keySet()){
        		if (this.galaxy.getUnitList().get(unitId).getStartingSituation().equals(GameConstants.startingUnitSituation)){
        			result.add(unitId);
        		}
        	}
    	}else if (this.getTurnPart().equals(GameConstants.moveUnitTurnName)){
    		Planet currentPlanet = this.galaxy.getAllPlanets().get(this.galaxy.getPlanetEvent());
    		for (PlanetArea area:currentPlanet.getAreaList()){
    			for (long unitId:area.getUnitIdList()){
    				//récupération de toutes les unités mobiles amies
    				StarcraftUnit unit = this.galaxy.getUnitList().get(unitId);
    				if (unit.getOwner().equals(this.getPlayerCurrentlyPlaying())){
    					if (unit.getType().equals("mobile")){
    						result.add(unitId);
    					}
    				}
    			}
    		}
    		for (Planet planet:this.galaxy.getLinkedPlanets(currentPlanet, this.getPlayerCurrentlyPlaying())){
    			for (PlanetArea area:planet.getAreaList()){
    				for (long unitId:area.getUnitIdList()){
        				StarcraftUnit unit = this.galaxy.getUnitList().get(unitId);
        				if (unit.getOwner().equals(this.getPlayerCurrentlyPlaying())){
        					if (unit.getType().equals("mobile")){
        						result.add(unitId);
        					}
        				}
        			}
    			}
    		}
    	}else if (this.getTurnPart().equals(GameConstants.moveRetreatUnitTurnName)){
    		StarcraftBattle battle = this.galaxy.getStarcraftBattle();
    		result = battle.getRetreatingUnits(this.getPlayerCurrentlyPlaying(), this);
    	}
    	return result;
    }
    
    /**récupère toutes les planètes à partir du fichier planetList.xml**/
	private void setPlanetDeck(){
		try {
			URL resources = getClass().getClassLoader().getResource("../../starcraftResources/planetList.xml");
			File fXmlFile = new File(resources.toURI());
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList planetList = doc.getElementsByTagName("planet");
			//tous les noeuds planet
			for (int i = 0; i < planetList.getLength(); i++){
				Element planetNode = (Element) planetList.item(i);
				Planet planet = new Planet();
				String planetName = planetNode.getAttribute("name");
				planet.setName(planetName);
				NodeList roadList = planetNode.getElementsByTagName("road");
				//tous les noeuds road
				for (int j = 0; j < roadList.getLength(); j++){
					Element roadNode = (Element) roadList.item(j);
					String roadPosition = roadNode.getAttribute("position");
					planet.addRoad(Integer.parseInt(roadPosition));
				}
				//tous les noeuds area
				NodeList areaList = planetNode.getElementsByTagName("area");
				for (int k = 0; k < areaList.getLength(); k++){
					Element areaNode = (Element) areaList.item(k);
					PlanetArea area = new PlanetArea();
					area.setAreaType(areaNode.getAttribute("unit"));
					area.setUnitLimit(Integer.parseInt(areaNode.getAttribute("unitLimit")));
					NodeList resourceList = areaNode.getElementsByTagName("resource");
					//tous les noeuds resource
					for (int j = 0; j < resourceList.getLength(); j++){
						Element resourceNode = (Element) resourceList.item(j);
						AreaResource resource = new AreaResource();
						resource.setResourceAmount(Integer.parseInt(resourceNode.getAttribute("amount")));
						resource.setResourceType(resourceNode.getAttribute("type"));
						area.addResource(resource);
					}
					//ajout d'une zone à la planète
					planet.addArea(area);
				}
				planetDeck.add(planet);
			}
			Collections.shuffle(planetDeck);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void discardPlanetDeck(){
		this.planetDeck = null;
	}
	
    public SaveGame getSaveGame() {
        return saveGame;
    }
    
	public Planet drawPlanet(){
		Planet result = planetDeck.get(0);
		planetDeck.remove(0);
		return result;
	}
	
	public List<Planet> getAllPlanets(){
		return this.planetDeck;
	}
	
    public void setSaveGame( SaveGame saveGame ) {
        this.saveGame = saveGame;
    }
    
    public long getId() {
        return saveGame.getId();
    }
	
    public void addPlayer(StarcraftPlayer player){
    	this.playerList.put(player.getName(), player);
    }
    
    public Map<String, StarcraftPlayer> getPlayerList(){
    	return this.playerList;
    }
    
    public StarcraftPlayer getPlayer(String playerName){
    	return this.playerList.get(playerName);
    }
    
    public void remove(String player){
    	this.playerList.remove(player);
    }
    
    /**fini la bataille en cours puis passe au tour suivant (faire attention à ce qu'aucuns tours spéciaux ne restent)**/
    public void endBattle(){
    	for (long id:this.galaxy.getUnitList().keySet()){
			StarcraftUnit unit = this.galaxy.getUnitList().get(id);
			unit.updateOldCoordinates();
			unit.setStartingSituation(GameConstants.inGalaxySituation);
		}
    	this.galaxy.setStarcraftBattle(null);
    	GameTurnHandler.callClearBattleStage(this);
		if (currentTurn == playerTurns.size() - 1){
			currentTurn = 0;
			setTurnName();
		}else{
			currentTurn++;
		}
	}
    
    /** regarde si le tour actuel doit être automatisé (le joueur n'a pas de choix d'action)
     * si le tour est automatisé, on accompli les tâches à accomplir puis on passe ou tour suivant**/
    public void isAutomatedTurn(){
    	Boolean isAutomated = false;
    	if (this.getTurnPart().equals(GameConstants.placeBaseTurnName)){
    		StarcraftPlayer starcraftPlayer = this.playerList.get(getPlayerCurrentlyPlaying());
    		if (starcraftPlayer.getUnitPools().get(GameConstants.placeBaseTurnName).getUnitList().isEmpty()){
    			isAutomated = true;
    		}
    	}else if (this.getTurnPart().equals(GameConstants.placeZRoadTurnName)){
    		if (this.galaxy.returnAvailableRoads().size() == 0){
    			isAutomated = true;
    		}
    	}else if (this.getTurnPart().equals(GameConstants.galaxyOrderChoiceTurnName)){
    		if (this.galaxy.getAllValidOrdersCoordinates(getPlayerCurrentlyPlaying()).size() == 0){
    			//ajout de la pioche d'évènement à faire
    			isAutomated = true;
    		}
    	}else if (this.getTurnPart().equals("destroyUnits")){
    		StarcraftBattle battle = this.galaxy.getStarcraftBattle();
    		if (battle.getAttackingPlayer().getName().equals(getPlayerCurrentlyPlaying())
    				&& battle.getAttackingUnitsToDestroy().size() == 0){
    			isAutomated = true;
    		}else if (battle.getDefendingPlayer().getName().equals(getPlayerCurrentlyPlaying())
    				&& battle.getDefendingUnitsToDestroy().size() == 0){
    			isAutomated = true;  			
    		}
    	}else if (this.getTurnPart().equals(GameConstants.moveRetreatUnitTurnName)){
    		StarcraftBattle battle = this.galaxy.getStarcraftBattle();
    		ArrayList<Long> retreatingUnits = battle.getRetreatingUnits(getPlayerCurrentlyPlaying(), this);
    		if (retreatingUnits.size() == 0){
    			isAutomated = true;
    			if (battle.getAttackingPlayer().getName().equals(getPlayerCurrentlyPlaying())){
    				endBattle();
    			}
    		}
    	}
    	
    	//si le tour est automatisé, on passe directement au tour suivant
    	if (isAutomated){
    		nextTurn();
    	}
    }
    
    public void setTurnParts(){
    	if (this.getTurnName().equals("planetChoice")){
    		this.turnPart.add("placePlanet");
    		this.turnPart.add(GameConstants.placeBaseTurnName);
    	} else if (this.getTurnName().equals("executeOrder")){
    		this.turnPart.add(GameConstants.galaxyOrderChoiceTurnName);
    	}
    }
    
    /** renvoie dans quelle partie du tour on est si le tour est décomposé en plusieurs parties
    renvoie le nom du tour sinon**/
    public String getTurnPart(){
    	String result = "";
    	if (this.turnPart.size() == 0){
    		result = this.getTurnName();
    	}else{
    		result = this.turnPart.get(0);
    	}
    	//System.out.println("getTurnPart : " + result);
    	return result;
    }

    
    /**passage au joueur suivant ou au prochain tour**/
    public void nextTurn(){
    	Boolean changedTurn = false;
    	if (this.turnPart.size() < 1){
    		changedTurn = true;
    		if (this.playerSpecialTurns.isEmpty()){
    			//déroulement normal du tour
    			if (currentTurn == playerTurns.size() - 1){
    				currentTurn = 0;
    				setTurnName();
    			}else{
    				currentTurn++;
    			}
    		}else{
    			this.playerSpecialTurns.remove(0);
    			this.specialTurnNames.remove(0);
    		}
    	}else{
    		this.turnPart.remove(0);
    		if (this.turnPart.size() < 1){
    			nextTurn();
    		}
    	}
    	//gestion des tours spéciaux donnés par les cartes ou autres
    	ArrayList<SpecialTurnEvent> keptEvents = new ArrayList<SpecialTurnEvent>();
    	for (SpecialTurnEvent specialTurnEvent:this.speciaTurnEvents){
    		if (specialTurnEvent.triggerSpecialTurn(this.getPlayerCurrentlyPlaying(), this.getTurnName())){
    			ArrayList<String> specialTurns = specialTurnEvent.getNewPlayerTurns();
    			for (String specialPlayerTurn:specialTurns){
    				this.playerSpecialTurns.add(specialPlayerTurn);
    				this.specialTurnNames.add(specialTurnEvent.getSpecialTurnName());
    			}
    		}else{
    			keptEvents.add(specialTurnEvent);
    		}
    	}
    	this.speciaTurnEvents = keptEvents;
    	if (changedTurn){
    		setTurnParts();
    	}
    	isAutomatedTurn();
    }
    
    /**passage des différents types de tours (choix des faction, phase de planification, phase d'exécution, etc...)**/
    public void setTurnName(){
    	if (turnName.equals("factionChoice")){
    		turnName = "leadershipChoice";
    	}else if (turnName.equals("leadershipChoice")){
    		turnName = "planetChoice";
    	}else if (turnName.equals("planetChoice")){
    		turnName = GameConstants.placeZRoadTurnName;
    	}else if (turnName.equals(GameConstants.placeZRoadTurnName)){
    		turnName = "placeUnit";
    	}else if (turnName.equals("placeUnit")){
    		//à la fin du tour de placement des unités, on distribue les ressources
    		setPlayerResources();
    		turnName = GameConstants.planningPhaseTurnName + "1";
    	}else if (turnName.equals(GameConstants.planningPhaseTurnName + "1")){
    		turnName = GameConstants.planningPhaseTurnName + "2";
    	}else if (turnName.equals(GameConstants.planningPhaseTurnName + "2")){
    		turnName = GameConstants.planningPhaseTurnName + "3";
    	}else if (turnName.equals(GameConstants.planningPhaseTurnName + "3")){
    		turnName = GameConstants.planningPhaseTurnName + "4";
    	}else if (turnName.equals(GameConstants.planningPhaseTurnName + "4")){
    		turnName = "executeOrder";
    	}else if (turnName.equals("executeOrder")){
    		if (this.galaxy.getOrderList().size() == 0){
    			System.out.println("full turn ended");
    			//à faire, vérifier si on a changé d'âge et faire toutes les actions de mises à jour du plateau
    			//destruction de base, mettre les ressources, etc...
    			turnName = "eventCardChoice";
    		}
    	}
    }
    
    /**renvoie le nom tu tour actuel**/
    public String getTurnName(){
    	String currentTurnName = "";
    	if (this.playerSpecialTurns.isEmpty()){
    		currentTurnName = this.turnName;
    	}else{
    		currentTurnName = this.specialTurnNames.get(0);
    	}
    	return currentTurnName;
    }
    
    /**renvoie le nom du joueur en train de jouer**/
    public String getPlayerCurrentlyPlaying(){
    	String currentPlayer ="";
    	if (this.playerSpecialTurns.isEmpty()){
    		currentPlayer = playerTurns.get(currentTurn);
    	}else{
    		currentPlayer = this.playerSpecialTurns.get(0);
    	}
    	return currentPlayer;
    }
    
    public ArrayList<String> getPlayerTurns(){
    	return this.playerTurns;
    }
    
    public int getCurrentTurnNumber(){
    	int currentTurnNumber = -1;
    	if (playerSpecialTurns.isEmpty()){
    		currentTurnNumber = this.currentTurn;
    	}
    	return currentTurnNumber;
    }
    
    public ArrayList<String> getTurnList(){
    	return playerTurns;
    }

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Galaxy getGalaxy() {
		return galaxy;
	}

	public void setGalaxy(Galaxy galaxy) {
		this.galaxy = galaxy;
	}
	
	public void addSpecialEvent(SpecialTurnEvent specialTurnEvent){
		this.speciaTurnEvents.add(specialTurnEvent);
	}

	public void addTurnPart(String partName){
		this.turnPart.add(partName);
	}

	public OrderToken getCurrentOrder() {
		return currentOrder;
	}

	public void setCurrentOrder(OrderToken currentOrder) {
		this.currentOrder = currentOrder;
	}

	

}
