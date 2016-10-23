package gameEntities.gameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gameEntities.GameConstants;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.playerItems.OrderToken;
import gameEntities.playerItems.StarcraftUnit;


public class Galaxy implements java.io.Serializable {

	private static final long serialVersionUID = -1483779316403739140L;
	private int width = 1;
	private int length = 1;
	private int minX = 0;
	private int minY = 0;
	private Map<String, Planet> planetList = new HashMap<String, Planet>();
	private Map<Long, StarcraftUnit> unitList = new HashMap<Long, StarcraftUnit>();
	private ArrayList<RoadLink> roadLinks= new ArrayList<RoadLink>();
	//cette planète correspond à la planète venant d'être placée ou bien celle où un ordre vient d'être révélé
	private String planetEvent = "";
	private Map<String, ArrayList<OrderToken>> orderList = new HashMap<String, ArrayList<OrderToken>>();
	private StarcraftBattle starcraftBattle = null;
	
	/**compte le nombre d'unités amies d'un type donné à l'endroit donné en excluant l'unité sélectionnée**/
	public int countFriendlyUnitsInPlace(int x, int y, int areaId, String unitType, StarcraftPlayer player, long id){
		int result = 0;
		String playerName = player.getName();
		Set<Long> unitList = null;
		if (unitType.equals("transport")){
			RoadLink link = this.getLinkFromCoordinates(new int[]{x, y, areaId});
			unitList = link.getUnitIdList();
		}else{
			PlanetArea area = this.returnPlanetAt(x, y).getArea(areaId);
			unitList = area.getUnitIdList();
		}
		for (Long unitId:unitList){
			StarcraftUnit unit = this.unitList.get(unitId);
			if (unitType.equals(unit.getType()) && playerName.equals(unit.getOwner()) && id!=unitId){
				result++;
			}
		}
		return result;
	}
	
	/**compte le nombre d'unités amies d'un type donné à l'endroit donné**/
	public int countFriendlyUnitsInPlace(int x, int y, int areaId, String unitType, StarcraftPlayer player){
		int result = 0;
		String playerName = player.getName();
		Set<Long> unitList = null;
		if (unitType.equals("transport")){
			RoadLink link = this.getLinkFromCoordinates(new int[]{x, y, areaId});
			unitList = link.getUnitIdList();
		}else{
			PlanetArea area = this.returnPlanetAt(x, y).getArea(areaId);
			unitList = area.getUnitIdList();
		}
		for (Long unitId:unitList){
			StarcraftUnit unit = this.unitList.get(unitId);
			if (unitType.equals(unit.getType()) && playerName.equals(unit.getOwner())){
				result++;
			}
		}
		return result;
	}
	
	/**compte le nombre d'unités ennemies à l'endroit donné**/
	public int countEnemyUnitsInPlace(int x, int y, int areaId, String unitType, StarcraftPlayer player){
		int result = 0;
		String playerName = player.getName();
		Set<Long> unitList = null;
		if (unitType.equals("transport")){
			RoadLink link = this.getLinkFromCoordinates(new int[]{x, y, areaId});
			unitList = link.getUnitIdList();
		}else{
			PlanetArea area = this.returnPlanetAt(x, y).getArea(areaId);
			unitList = area.getUnitIdList();
		}
		for (Long unitId:unitList){
			if (this.unitList.containsKey(unitId)){
				StarcraftUnit unit = this.unitList.get(unitId);
				if ((unitType.equals(unit.getType()) || unitType.equals(""))&& !playerName.equals(unit.getOwner())){
					result++;
				}
			}
		}
		return result;
	}

	/**renvoie les routes sélectionnables quand aucune route n'est sélectionnée**/
	public ArrayList<int[]> returnAvailableRoads(){
		ArrayList<int[]> result = new ArrayList<int[]>();
		ArrayList<int[]> unlinkedRoads = new ArrayList<int[]>();
		for (String planetName : this.planetList.keySet()){
			Planet planet = this.planetList.get(planetName);
			ArrayList<Integer> roadPositionList = planet.getRoadPositions();
			for (int roadPosition:roadPositionList){
				int[] evaluatedRoad = new int[]{planet.getX(), planet.getY(), roadPosition};
				Boolean linked = false;
				//on cherche si un lien utilise déjà cette route
				for (RoadLink link:this.roadLinks){
					if (evaluatedRoad[0] == link.getCoordinates1()[0]
							&& evaluatedRoad[1] == link.getCoordinates1()[1]
									&& evaluatedRoad[2] == link.getCoordinates1()[2]){
						linked = true;
						break;
					}else if (evaluatedRoad[0] == link.getCoordinates2()[0]
							&& evaluatedRoad[1] == link.getCoordinates2()[1]
									&& evaluatedRoad[2] == link.getCoordinates2()[2]){
						linked = true;
						break;
					}
				}
				if (!linked){
					unlinkedRoads.add(evaluatedRoad);
				}
			}
		}
		//pour chaque routes non liées, on regarde si il est possible de la lier à une autre route
		for (int[] unlinkedRoad1:unlinkedRoads){
			for (int[] unlinkedRoad2:unlinkedRoads){
				if (unlinkedRoad1[0] != unlinkedRoad2[0] || unlinkedRoad1[1] != unlinkedRoad2[1]){
					result.add(unlinkedRoad1);
					break;
				}
			}
		}
		return result;
	}
	
	/**renvoie les routes sélectionnables quand une route est déjà sélectionnée**/
	public ArrayList<int[]> returnAvailableRoads(int[] selectedRoad){
		ArrayList<int[]> result = new ArrayList<int[]>();
		for (String planetName : this.planetList.keySet()){
			Planet planet = this.planetList.get(planetName);
			ArrayList<Integer> roadPositionList = planet.getRoadPositions();
			for (int roadPosition:roadPositionList){
				if (selectedRoad[0] != planet.getX() || selectedRoad[1] != planet.getY()){
					int[] evaluatedRoad = new int[]{planet.getX(), planet.getY(), roadPosition};
					Boolean linked = false;
					for (RoadLink link:this.roadLinks){
						if (evaluatedRoad[0] == link.getCoordinates1()[0]
								&& evaluatedRoad[1] == link.getCoordinates1()[1]
										&& evaluatedRoad[2] == link.getCoordinates1()[2]){
							linked = true;
							break;
						}else if (evaluatedRoad[0] == link.getCoordinates2()[0]
								&& evaluatedRoad[1] == link.getCoordinates2()[1]
										&& evaluatedRoad[2] == link.getCoordinates2()[2]){
							linked = true;
							break;
						}
					}
					if (!linked){
						result.add(evaluatedRoad);
					}
				}

			}
		}
		return result;
	}
	
	
	/**trouve l'unité sur la carte ou bien dans le pool d'unités du joueur**/
	public StarcraftUnit findUnit(StarcraftPlayer starcraftPlayer, long id){
		StarcraftUnit result = null;
		if (this.unitList.containsKey(id)){
			result = this.unitList.get(id);
		}else{
			for (String poolName:starcraftPlayer.getUnitPools().keySet()){
				if (starcraftPlayer.getUnitPools().get(poolName).getUnitList().containsKey(id)){
					result = starcraftPlayer.getUnitPools().get(poolName).getUnitList().get(id);
					break;
				}
			}
		}
		return result;
	}
	
	/**vérifie si il y a une unité du type donné sur la planète autre que l'unité sélectionnée**/
	public boolean unitOnPlanet(StarcraftPlayer starcraftPlayer, Planet planet, String unitType, long id){
		boolean result = false;
		for (PlanetArea area:planet.getAreaList()){
			for(long unitId:area.getUnitIdList()){
				StarcraftUnit unit = this.unitList.get(unitId);
				if (unitId!=id){
					if (unit.getOwner().equals(starcraftPlayer.getName()) && unit.getType().equals(unitType)){
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}
	
	/**vérifie si il y a une unité du type donné sur la planète **/
	public boolean unitOnPlanet(StarcraftPlayer starcraftPlayer, Planet planet, String unitType){
		boolean result = false;
		for (PlanetArea area:planet.getAreaList()){
			for(long unitId:area.getUnitIdList()){
				StarcraftUnit unit = this.unitList.get(unitId);
				if (unit.getOwner().equals(starcraftPlayer.getName()) && unit.getType().equals(unitType)){
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	/**on renvoit les endroits(planètes) où il est possible que des évènements se produisent**/
	public ArrayList<int[]> getAllPossiblePlanetEvents(StarcraftPlayer starcraftPlayer, String turnName){
		ArrayList<int[]> result = new ArrayList<int[]>();
		if (turnName.equals("placeUnit")){
			//dans la phase de départ, on cherche toutes les planètes contenant une base amie
			for (String planetName:this.planetList.keySet()){
				Planet planet = this.planetList.get(planetName);
				if (unitOnPlanet(starcraftPlayer, planet, "base")){
					result.add(new int[]{planet.getX(), planet.getY()});
				}
			}
		}else if (turnName.startsWith(GameConstants.planningPhaseTurnName)){
			//si il s'agit de la phase de plannification, on cherche toutes les planètes contenant au moins
			//une unité quelconque du joueur et les voisines de ses planètes
			ArrayList<String> validPlanetList = new ArrayList<String>();
			for (String planetName:this.planetList.keySet()){
				Planet planet = this.planetList.get(planetName);
				if (unitOnPlanet(starcraftPlayer, planet, "base")){
					result.add(new int[]{planet.getX(), planet.getY()});
					validPlanetList.add(planetName);
				}else if (unitOnPlanet(starcraftPlayer, planet, "mobile")){
					validPlanetList.add(planetName);
					result.add(new int[]{planet.getX(), planet.getY()});
				}else if (unitOnPlanet(starcraftPlayer, planet, "installation")){
					validPlanetList.add(planetName);
					result.add(new int[]{planet.getX(), planet.getY()});
				}
			}
			
			//on cherche ici les planètes voisines des planètes précédentes
			ArrayList<String> neighbourPlanetList = new ArrayList<String>();
			for (String validPlanetName:validPlanetList){
				ArrayList<Planet> neighbourPlanets = this.getLinkedPlanets(this.planetList.get(validPlanetName));
				for (Planet neighbourPlanet:neighbourPlanets){
					if (!validPlanetList.contains(neighbourPlanet.getName()) && !neighbourPlanetList.contains(neighbourPlanet.getName())){
						neighbourPlanetList.add(neighbourPlanet.getName());
						result.add(new int[]{neighbourPlanet.getX(), neighbourPlanet.getY()});
					}
				}
			}
		}else if (turnName.equals(GameConstants.moveUnitTurnName) || turnName.equals(GameConstants.moveRetreatUnitTurnName)){
			// dans le cas d'un mouvement d'unités, les planètes actives sont 
			// celles où l'ordre a été placé puis les voisines qui sont connectées
			Planet currentPlanet = this.planetList.get(this.planetEvent);
			result.add(new int[]{currentPlanet.getX(), currentPlanet.getY()});
			for (Planet planet:getLinkedPlanets(currentPlanet, starcraftPlayer.getName())){
				result.add(new int[]{planet.getX(), planet.getY()});
			}
		}
		return result;
	}
	
	/**on renvoit les endroits(liens) où il est possible que des évènements se produisent**/
	public ArrayList<RoadLink> getAllPossibleLinkEvent(StarcraftPlayer starcraftPlayer, String turnName){
		ArrayList<RoadLink> result = new ArrayList<RoadLink>();
		ArrayList<int[]> validPlanetCoordinates = new ArrayList<int[]>();
		if (turnName.equals("placeUnit")){
			for (String planetName:this.planetList.keySet()){
				Planet planet = this.planetList.get(planetName);
				if (unitOnPlanet(starcraftPlayer, planet, "base")){
					//on regarde si la route est liée à une autre route
					validPlanetCoordinates.add(new int[]{planet.getX(), planet.getY()});
				}
			}
		}else if (turnName.equals(GameConstants.buildUnitsTurnName)){
			validPlanetCoordinates.add(new int[]{this.planetList.get(this.planetEvent).getX(),
					this.planetList.get(this.planetEvent).getY()});
		}
		for (RoadLink link:this.roadLinks){
			for (int[] planetCoordinate:validPlanetCoordinates){
				if (link.getCoordinates1()[0] == planetCoordinate[0] && link.getCoordinates1()[1] == planetCoordinate[1]){
					result.add(link);
					break;
				}else if (link.getCoordinates2()[0] == planetCoordinate[0] && link.getCoordinates2()[1] == planetCoordinate[1]){
					result.add(link);
					break;
				}
			}
		}
		return result;
	}
	
	//TODO compléter la fonction décrivant toutes les possibilités de placement
	/**on renvoit les endroits où l'unité peut être placée**/
	public ArrayList<int[]> getValidUnitPlacement(StarcraftPlayer starcraftPlayer, long unitId){
		//on utilise le joueur car l'unité pourrait se trouver dans son pool d'unités au lieu d'être dans la galaxie
		StarcraftUnit starcraftunit = findUnit(starcraftPlayer, unitId);
		ArrayList<int[]> result = new ArrayList<int[]>();
		String unitType = starcraftunit.getType();
		// si l'unité à placer est une base, on regarde où les unités mobiles requises sont placées
		// si il n'y a pas d'unités, alors le joueur ne peut que placer sa base dans la dernière planète placée
		if (unitType.equals("base")){
			//les places disponibles au début de partie
			if (starcraftunit.getStartingSituation().equals(GameConstants.startingUnitSituation)){
				Planet placedPlanet = this.planetList.get(this.planetEvent);
				if (!unitOnPlanet(starcraftPlayer, placedPlanet, "base", unitId)){
					for (PlanetArea area:placedPlanet.getAreaList()){
						if (area.getAreaType().equals("ground") || area.getAreaType().equals("all")){
							result.add(new int[]{placedPlanet.getX(), placedPlanet.getY(), area.getId()});
						}
					}
				}
				//le joueur peut construire si il n'a pas de bases sur la
				//zone est que la case est occupée par une unité amie
			}else if (starcraftunit.getStartingSituation().equals("builtUnit")){
				Planet placedPlanet = this.planetList.get(this.planetEvent);
				for (PlanetArea area:placedPlanet.getAreaList()){
					if (area.getAreaType().equals("ground") || area.getAreaType().equals("all")){
						if (this.countFriendlyUnitsInPlace
								(placedPlanet.getX(), placedPlanet.getY(), area.getId(), "mobile", starcraftPlayer) > 0){
							result.add(new int[]{placedPlanet.getX(), placedPlanet.getY(), area.getId()});
						}
					}
				}
			}
		}else if (unitType.equals("mobile")){
			//on regarde les zones des planètes où sont les bases
			if (starcraftunit.getStartingSituation().equals(GameConstants.startingUnitSituation)){
				for (String planetName:this.planetList.keySet()){
					Planet planet = this.planetList.get(planetName);
					if (unitOnPlanet(starcraftPlayer, planet, "base")){
						for (PlanetArea area:planet.getAreaList()){
							if (area.getAreaType().equals(starcraftunit.getMoveType()) || area.getAreaType().equals("all")){
								if (countFriendlyUnitsInPlace(planet.getX(),
										planet.getY(),
										area.getId(),
										"mobile",
										starcraftPlayer,
										unitId)
										< area.getUnitLimit()){
									result.add(new int[]{planet.getX(), planet.getY(), area.getId()});
								}
							}
						}
					}
				}
				
			}else if (starcraftunit.getStartingSituation().equals(GameConstants.inGalaxySituation)){
				//on ajoute les coordonnées de départ de l'unité
				result.add(new int[]{starcraftunit.getOldCoordinates()[0],
						starcraftunit.getOldCoordinates()[1],
						starcraftunit.getOldCoordinates()[2]});
				Planet currentPlanet = this.planetList.get(this.planetEvent);
				for (PlanetArea area:currentPlanet.getAreaList()){
					if (area.getAreaType().equals(starcraftunit.getMoveType()) || area.getAreaType().equals("all")){
						// si des unités enemies sont sur la zone, on regarde si on peut rajouter des unité dans la batille
						if (countEnemyUnitsInPlace(currentPlanet.getX(), currentPlanet.getY(), area.getId(), "", starcraftPlayer) > 0){
							if (this.starcraftBattle == null 
									|| (this.starcraftBattle.getCoordinates()[0] == currentPlanet.getX()
									&& this.starcraftBattle.getCoordinates()[1] == currentPlanet.getY()
									&& this.starcraftBattle.getCoordinates()[2] ==area.getId())){
								if (countFriendlyUnitsInPlace(currentPlanet.getX(),
										currentPlanet.getY(),
										area.getId(),
										"mobile",
										starcraftPlayer,
										unitId)
										< area.getUnitLimit() + 2){
									result.add(new int[]{currentPlanet.getX(), currentPlanet.getY(), area.getId()});
								}
							}
						}else{
							if (countFriendlyUnitsInPlace(currentPlanet.getX(),
									currentPlanet.getY(),
									area.getId(),
									"mobile",
									starcraftPlayer,
									unitId)
									< area.getUnitLimit()){
								result.add(new int[]{currentPlanet.getX(), currentPlanet.getY(), area.getId()});
							}
						}
					}
				}
			}else if (starcraftunit.getStartingSituation().equals("inBattle")){
				Planet currentPlanet = this.planetList.get(this.planetEvent);
				for (PlanetArea area:currentPlanet.getAreaList()){
					if (area.getAreaType().equals(starcraftunit.getMoveType()) || area.getAreaType().equals("all")){
						if (countEnemyUnitsInPlace(currentPlanet.getX(), currentPlanet.getY(), area.getId(), "", starcraftPlayer) == 0
								|| (currentPlanet.getX() == starcraftunit.getOldCoordinates()[0]
										&& currentPlanet.getY() == starcraftunit.getOldCoordinates()[1]
												&& area.getId() == starcraftunit.getOldCoordinates()[2])){
							if (countFriendlyUnitsInPlace(currentPlanet.getX(),
									currentPlanet.getY(),
									area.getId(),
									"mobile",
									starcraftPlayer,
									unitId)
									< area.getUnitLimit()){
								result.add(new int[]{currentPlanet.getX(), currentPlanet.getY(), area.getId()});
							}
						}
					}
				}
				for (Planet planet:this.getLinkedPlanets(currentPlanet, starcraftunit.getOwner())){
					for (PlanetArea area:planet.getAreaList()){
						if (countEnemyUnitsInPlace(planet.getX(), planet.getY(), area.getId(), "", starcraftPlayer) == 0
								|| (planet.getX() == starcraftunit.getOldCoordinates()[0]
										&& planet.getY() == starcraftunit.getOldCoordinates()[1]
												&& area.getId() == starcraftunit.getOldCoordinates()[2])){
							if (area.getAreaType().equals(starcraftunit.getMoveType()) || area.getAreaType().equals("all")){
								if (countFriendlyUnitsInPlace(planet.getX(),
										planet.getY(),
										area.getId(),
										"mobile",
										starcraftPlayer,
										unitId)
										< area.getUnitLimit()){
									
									result.add(new int[]{planet.getX(), planet.getY(), area.getId()});
								}
							}
						}
					}
				}
			}else if (starcraftunit.getStartingSituation().equals("builtUnit")){
				Planet currentPlanet = this.planetList.get(this.planetEvent);
				for (PlanetArea area:currentPlanet.getAreaList()){
					if (area.getAreaType().equals(starcraftunit.getMoveType()) || area.getAreaType().equals("all")){
						if (countEnemyUnitsInPlace(currentPlanet.getX(), currentPlanet.getY(), area.getId(), "", starcraftPlayer) == 0){
							if (countFriendlyUnitsInPlace(currentPlanet.getX(),
									currentPlanet.getY(),
									area.getId(),
									"mobile",
									starcraftPlayer,
									unitId)
									< area.getUnitLimit()){
								result.add(new int[]{currentPlanet.getX(), currentPlanet.getY(), area.getId()});
							}
						}
					}
				}
			}
		}else if (unitType.equals("transport")){
			//positions possibles pour les transports
			if (starcraftunit.getStartingSituation().equals(GameConstants.startingUnitSituation)){
				ArrayList<RoadLink> validLinks = this.getAllPossibleLinkEvent(starcraftPlayer, "placeUnit");
				for (RoadLink link:validLinks){
					if (countFriendlyUnitsInPlace(link.getCoordinates1()[0], link.getCoordinates1()[1], link.getCoordinates1()[2],
							"transport", starcraftPlayer,	unitId)	< 1
							&& countFriendlyUnitsInPlace(link.getCoordinates2()[0], link.getCoordinates2()[1], link.getCoordinates2()[2],
									"transport", starcraftPlayer, unitId) < 1){
						if (countEnemyUnitsInPlace(link.getCoordinates1()[0], link.getCoordinates1()[1], link.getCoordinates1()[2],
								"transport", starcraftPlayer) < 3){
							result.add(new int[]{link.getCoordinates1()[0], link.getCoordinates1()[1], link.getCoordinates1()[2]});
						}
						if (countEnemyUnitsInPlace(link.getCoordinates2()[0], link.getCoordinates2()[1], link.getCoordinates2()[2],
								"transport", starcraftPlayer) < 3){
							result.add(new int[]{link.getCoordinates2()[0], link.getCoordinates2()[1], link.getCoordinates2()[2]});
						}
					}
				}
			}else if (starcraftunit.getStartingSituation().equals("builtUnit")){
				//TODO
				ArrayList<RoadLink> validLinks = this.getAllPossibleLinkEvent(starcraftPlayer, GameConstants.buildUnitsTurnName);
				for (RoadLink link:validLinks){
					if (countFriendlyUnitsInPlace(link.getCoordinates1()[0], link.getCoordinates1()[1], link.getCoordinates1()[2],
							"transport", starcraftPlayer,	unitId)	< 1
							&& countFriendlyUnitsInPlace(link.getCoordinates2()[0], link.getCoordinates2()[1], link.getCoordinates2()[2],
									"transport", starcraftPlayer, unitId) < 1){
						if (countEnemyUnitsInPlace(link.getCoordinates1()[0], link.getCoordinates1()[1], link.getCoordinates1()[2],
								"transport", starcraftPlayer) < 3){
							result.add(new int[]{link.getCoordinates1()[0], link.getCoordinates1()[1], link.getCoordinates1()[2]});
						}
						if (countEnemyUnitsInPlace(link.getCoordinates2()[0], link.getCoordinates2()[1], link.getCoordinates2()[2],
								"transport", starcraftPlayer) < 3){
							result.add(new int[]{link.getCoordinates2()[0], link.getCoordinates2()[1], link.getCoordinates2()[2]});
						}
					}
				}
			}
		}
		return result;
	}
	
	/** renvoie toutes les unités d'un joueur ayant le type donné**/
	public ArrayList<Long> returnPlayerUnitsType(String playerName, String unitType){
		ArrayList<Long> result = new ArrayList<Long>();
		for (long unitId:unitList.keySet()){
			if (this.unitList.get(unitId).getType().equals(unitType) && this.unitList.get(unitId).getOwner().equals(playerName)){
				result.add(unitId);
			}
		}
		return result;
	}
	
	/** renvoie toutes les unités dans la galaxie d'un joueur**/
	public ArrayList<Long> returnPlayerUnitsId(String playerName){
		ArrayList<Long> result = new ArrayList<Long>();
		for (long unitId:unitList.keySet()){
			if (this.unitList.get(unitId).getOwner().equals(playerName)){
				result.add(unitId);
			}
		}
		return result;
	}
	
	/**rajoute une planète à la galaxie et créer les liens entre celle-ci et ses voisines**/
	public void addPlanet(Planet planet){
		for (int[] coordinates:planet.neighbourCoordinates()){
			for (String planetName:this.planetList.keySet()){
				Planet neighbour = this.planetList.get(planetName);
				if (coordinates[0] == neighbour.getX()
						&& coordinates[1] == neighbour.getY()
						&& 1 == neighbour.returnRoads()[coordinates[2]]){
					RoadLink link = new RoadLink();
					link.setCoordinates1(new int[]{planet.getX(), planet.getY(), (coordinates[2] + 2)%4});
					link.setCoordinates2(new int[]{neighbour.getX(), neighbour.getY(), coordinates[2]});
					link.setLinkType("normal");
					this.roadLinks.add(link);
				}
			}
		}
		
		planetList.put(planet.getName(), planet);
	}
	
	public ArrayList<RoadLink> returnRoadLinks(){
		return this.roadLinks;
	}
	
	public Map<String, Planet> getAllPlanets(){
		return this.planetList;
	}
	
	public void updateGalaxySizes(){
		if (planetList.size() == 0){
			this.width = 1;
			this.length = 1;
			this.minX = 0;
			this.minY = 0;
		}else{
			int maxX = 0;
			int maxY = 0;
			for (String planetName:this.planetList.keySet()){
				//explore la longueur de la carte
				int planetX = this.planetList.get(planetName).getX();
				if ( planetX< this.minX){
					this.minX = planetX;
				}else if (planetX > maxX){
					maxX = planetX;
				}
				//explore la hauteur de la carte
				int planetY = this.planetList.get(planetName).getY();
				if ( planetY< this.minY){
					this.minY = planetY;
				}else if (planetY > maxY){
					maxY = planetY;
				}
			}
			this.width = maxX - this.minX + 1;
			this.length = maxY - this.minY + 1;
		}
	}
	
	public ArrayList<int[]> getValidCoordinates(){
		ArrayList<int[]> result = new ArrayList<int[]>();
		ArrayList<int[]> occupiedPlaces = new ArrayList<int[]>();
		if (this.planetList.size() == 0){
			result.add(new int[]{0, 0, 0});
			result.add(new int[]{0, 0, 1});
			result.add(new int[]{0, 0, 2});
			result.add(new int[]{0, 0, 3});
		}else{
			//liste toutes les places déjà occupées par une planète
			for (String planetName:this.planetList.keySet()){
				Planet currentPlanet = this.planetList.get(planetName);
				occupiedPlaces.add(new int[]{currentPlanet.getX(), currentPlanet.getY()});
			}
			for (String planetName2:this.planetList.keySet()){
				ArrayList<int[]> possibleCoordinates = this.planetList.get(planetName2).neighbourCoordinates();
				for (int[] coord:possibleCoordinates){
					Boolean coordCountainsPlanet = false;
					for (int[] takenCoord:occupiedPlaces){
						if (takenCoord[0] == coord[0] && takenCoord[1] == coord[1]){
							coordCountainsPlanet = true;
						}
					}
					if (!coordCountainsPlanet){
						result.add(coord);
					}
				}
			}
		}
		return result;
	}
	
	
	public int getWidth() {
		return width;
	}

	public int getLength() {
		return length;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public String getPlanetEvent() {
		return planetEvent;
	}

	public void setPlanetEvent(String lastPlacedPlanet) {
		this.planetEvent = lastPlacedPlanet;
	}
	
	public void addUnit(StarcraftUnit unit) {
		this.unitList.put(unit.getId(), unit);
		if (unit.getType().equals("transport")){
			
		}else{
			
		}
	}
	
	public void addLink(RoadLink link) {
		this.roadLinks.add(link);
	}
	
	public ArrayList<RoadLink> getAllLinks() {
		return this.roadLinks;
	}
	
	/**renvoie la liste des unités de la galaxie, à utiliser en lecture seule**/
	public Map<Long, StarcraftUnit> getUnitList(){
		return this.unitList;
	}
	
	/**enlève une unité à la galaxie**/
	public void removeUnit(Long id, StarcraftGame game) {
		if (this.unitList.containsKey(id)){
			StarcraftUnit unit = this.unitList.get(id);
			if (unit.getType().equals("transport")){
				// on vérifie que les coordonnées correspondaient à un lien existant
				RoadLink oldLink = game.getGalaxy().getLinkFromCoordinates(unit.getCoordinates());
				if (oldLink != null){
					if (oldLink.getUnitIdList().contains(id)){
						oldLink.removeUnitId(id);
					}
				}
			}else{
				Planet oldPlanet = game.getGalaxy().returnPlanetAt(unit.getCoordinates()[0], unit.getCoordinates()[1]);
				if (oldPlanet != null){
					PlanetArea oldArea = game.getGalaxy().returnPlanetAt(unit.getCoordinates()[0], unit.getCoordinates()[1])
							.getArea(unit.getCoordinates()[2]);
					if (oldArea.getUnitIdList().contains(id)){
						oldArea.removeUnitId(id);
					}
				}
			}
		}
		
		this.unitList.remove(id);
	}

	/*fonctions permettant de retrouver des éléments de la galaxie*/
	
	/**fonction renvoyant toutes les planètes liées à la planète choisie**/
	public ArrayList<Planet> getLinkedPlanets(Planet planet){
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (RoadLink link:this.roadLinks){
			if (link.getCoordinates1()[0] == planet.getX()
					&& link.getCoordinates1()[1] == planet.getY()){
				result.add(this.returnPlanetAt(link.getCoordinates2()[0], link.getCoordinates2()[1]));
			}else if (link.getCoordinates2()[0] == planet.getX()
					&& link.getCoordinates2()[1] == planet.getY()){
				result.add(this.returnPlanetAt(link.getCoordinates1()[0], link.getCoordinates1()[1]));
			}
		}
		return result;
	}
	
	/**fonction renvoyant toutes les planètes liées à la planète choisie avec des transports**/
	public ArrayList<Planet> getLinkedPlanets(Planet planet, String playerName){
		ArrayList<Planet> result = new ArrayList<Planet>();
		for (RoadLink link:this.roadLinks){
			Boolean hasTransport = false;
			for (long id:link.getUnitIdList()){
				if (this.unitList.get(id).getOwner().equals(playerName)){
					hasTransport = true;
					break;
				}
			}
			if (hasTransport){
				if (link.getCoordinates1()[0] == planet.getX()
						&& link.getCoordinates1()[1] == planet.getY()){
					result.add(this.returnPlanetAt(link.getCoordinates2()[0], link.getCoordinates2()[1]));
				}else if (link.getCoordinates2()[0] == planet.getX()
						&& link.getCoordinates2()[1] == planet.getY()){
					result.add(this.returnPlanetAt(link.getCoordinates1()[0], link.getCoordinates1()[1]));
				}
			}
		}
		return result;
	}
	
	/**renvoie le lien en fonction d'une de ses coordonnées**/
	public RoadLink getLinkFromCoordinates(int[] coordinates){
		RoadLink result = null;
		for (RoadLink link:this.roadLinks){
			if (link.getCoordinates1()[0] == coordinates[0]
					&& link.getCoordinates1()[1] == coordinates[1]
							&& link.getCoordinates1()[2] == coordinates[2]){
				result = link;
				break;
			}else if (link.getCoordinates2()[0] == coordinates[0]
					&& link.getCoordinates2()[1] == coordinates[1]
							&& link.getCoordinates2()[2] == coordinates[2]){
				result = link;
				break;
			}
		}
		return result;
	}
	
	/** on donne le nom de la planète se trouvant au point x, y**/
	public String returnPlanetNameAt(int x, int y){
		String result = "";
		for (String planetName:this.planetList.keySet()){
			if (this.planetList.get(planetName).getX() == x && this.planetList.get(planetName).getY() == y){
				result = planetName;
				break;
			}
		}
		return result;
	}
	
	/** on donne la planète se trouvant au point x, y**/
	public Planet returnPlanetAt(int x, int y){
		Planet result = null;
		for (String planetName:this.planetList.keySet()){
			if (this.planetList.get(planetName).getX() == x && this.planetList.get(planetName).getY() == y){
				result = this.planetList.get(planetName);
				break;
			}
		}
		return result;
	}
	
	public Map<String, ArrayList<OrderToken>> getOrderList() {
		return orderList;
	}
	
	/**Rajoute un ordre à la galaxie**/
	public void addOrder(String coordinates, OrderToken order){
		if (this.orderList.containsKey(coordinates)){
			this.orderList.get(coordinates).add(0, order);
		}else{
			ArrayList<OrderToken> arrayToAdd = new ArrayList<OrderToken>();
			arrayToAdd.add(order);
			this.orderList.put(coordinates, arrayToAdd);
		}
	}
	
	/**on enlève un ordre de la pile, puis si celle-ci est vide, on supprime la pile d'ordre**/
	public void removeOrder(String coordinates) throws Exception{
		if (this.orderList.containsKey(coordinates)){
			this.orderList.get(coordinates).remove(0);
			if (this.orderList.get(coordinates).size() == 0){
				this.orderList.remove(coordinates);
			}
		}else{
			//cela ne devrait pas arriver si le programme est fait correctement
			throw new Exception("no orders were put at this place");
		}
	}
	
	/**récupère toutes les coordonnées des planètes contenant des ordres exécutables par le joueur actuel**/
	public ArrayList<String> getAllValidOrdersCoordinates(String playerName){
		ArrayList<String> result = new ArrayList<String>();
		for (String coordinates:this.orderList.keySet()){
			if (this.orderList.get(coordinates).get(0).getOwner().equals(playerName)){
				result.add(coordinates);
			}
		}
		return result;
	}

	public StarcraftBattle getStarcraftBattle() {
		return starcraftBattle;
	}

	public void setStarcraftBattle(StarcraftBattle starcraftBattle) {
		this.starcraftBattle = starcraftBattle;
	}
}
