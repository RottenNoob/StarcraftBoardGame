package gameEntities.methods;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;

import java.util.List;

import gameEntities.GameConstants;

// classe intermédiare entre le websocket et les méthodes modifiant l'état de la partie
public class GameTurnHandler {
    private static ChooseFaction factionChoice =  new ChooseFaction();
    private static ChooseLeadership leadershipChoice =  new ChooseLeadership();
    private static PlacePlanets placePlanets =  new PlacePlanets();
    private static PlaceUnits placeUnits =  new PlaceUnits();
    private static PlaceZRoad placeZRoad =  new PlaceZRoad();
    private static PlanningPhase planningPhase = new PlanningPhase();
    private static GalaxyOrderChoice galaxyOrderChoice = new GalaxyOrderChoice();
    private static BattleHandler battleHandler = new BattleHandler();
    private static ResourceHandler resourceHandler = new ResourceHandler();
    private static BuildTurnHandler buildUnits = new BuildTurnHandler();
	
	// fonction gérant l'affichage du prochain tour, cette fonction se contente d'envoyer les modifications apportées au plateau
	public static void printNextTurnScreen(String player, StarcraftGame game){
		GlobalMethods.printPlayerTurn(player, game);
		String turnName = game.getTurnPart();
		//System.out.println(turnName);
		int currentTurnNumber = game.getCurrentTurnNumber();
		//cette fonction est appelée à chaque fois car le tour des joueurs change fréquement
		factionChoice.printChosenFactions(player, game);
		if (turnName.equals("factionChoice")){
			//correspond au passage d'une étape à une autre
			if (currentTurnNumber == 0){
				factionChoice.printFactionChoice(player, game);
			}
		}else if (turnName.equals("leadershipChoice")){
			if (currentTurnNumber == 0){
				GlobalMethods.printGameBoard(player);
				leadershipChoice.printLeadershipChoice(player, game);
			}
		}else if (turnName.equals("placePlanet")){
			if (currentTurnNumber == 0){
				GlobalMethods.upgradeActionMenu(player);
				//le joueur pioche des planètes aléatoirement
				placePlanets.drawPlanets(player, game);
				placePlanets.addActionCanvas(player, game);
				placePlanets.addGalaxyCanvas(player, game);
			}
			GlobalMethods.clearByClass(player, "gameButton");
			GlobalMethods.clearActionStage(player);
			placePlanets.addPlacePlanetButtons(player, game);
			placePlanets.printPlanetChoice(player, game);
			placePlanets.updateGalaxySize(player, game);
			placeUnits.printAllGalaxyUnits(player, game);
			placePlanets.setValidPlacements(player, game);
		}else if (turnName.equals(GameConstants.placeBaseTurnName)){
			GlobalMethods.clearActionStage(player);
			GlobalMethods.clearByClass(player, "gameButton");
			placePlanets.resizeGalaxy(player, game);
			placeUnits.printAllGalaxyUnits(player, game);
			placeUnits.addUnitPlacementButton(player, game);
			placeUnits.printUnitChoice(player, game);
			placeUnits.activateCurrentPlanet(player, game);
			placeUnits.activateValidUnits(player, game);
		}else if (turnName.equals(GameConstants.placeZRoadTurnName)){
			if (currentTurnNumber == 0){
				//le joueur pioche des planètes aléatoirement
				placePlanets.resizeGalaxy(player, game);
				placeUnits.printAllGalaxyUnits(player, game);
				GlobalMethods.clearActionStage(player);
				GlobalMethods.clearByClass(player, "gameButton");
				placeZRoad.addPlaceZRoadButtons(player, game);
			}
			placeZRoad.activateAvailableRoads(player, game);
		}else {
			// on peut à partir de ce moment appeler les fonctions d'affichage des
			//éléments sur la galaxie car sa taille ne change plus
			//les unités sont déplacées pendant la partie car il y en a beaucoup,
			//on ne veut pas devoir les réafficher à chaque fois
			//placeUnits.printAllGalaxyUnits(player, game);
			planningPhase.clearDisplayedOrders(player, game);
			planningPhase.displayPlacedOrders(player, game);
			if (turnName.equals("placeUnit")){
				if (currentTurnNumber == 0){
					GlobalMethods.clearActionStage(player);
					GlobalMethods.clearByClass(player, "gameButton");
					placeUnits.addUnitPlacementButton(player, game);
					placeUnits.printUnitChoice(player, game);
				}
				placeUnits.activateValidUnits(player, game);
				placeUnits.activateValidPlanets(player, game);
				placeUnits.activateValidLinks(player, game);
			}else if (turnName.startsWith(GameConstants.planningPhaseTurnName)){
				if (currentTurnNumber == 0){
					GlobalMethods.clearByClass(player, "gameButton");
					planningPhase.addPlanningPhaseButton(player, game);
				}
				GlobalMethods.clearActionStage(player);
				planningPhase.displayAvailableOrders(player, game);
				planningPhase.activateValidPlanetSquares(player, game);
			}else if (turnName.equals(GameConstants.galaxyOrderChoiceTurnName)){
				// on doit mettre à jour les actions possibles à chaque tour à cause des tours sautés
				// on ne sait pas à quel moment chaque écran est utilisé pour la première fois
				battleHandler.setCombatModeOff(player, game);
				GlobalMethods.clearActionStage(player);
				GlobalMethods.clearByClass(player, "gameButton");
				galaxyOrderChoice.addGalaxyOrderChoiceButton(player, game);
				galaxyOrderChoice.activateValidSquareOrders(player, game);
			}else if (turnName.equals(GameConstants.executeChoiceTurnName)){
				GlobalMethods.clearActionStage(player);
				GlobalMethods.clearByClass(player, "gameButton");
				galaxyOrderChoice.addActivationChoiceButton(player, game);
			} else if (turnName.equals(GameConstants.moveUnitTurnName)){
				GlobalMethods.clearActionStage(player);
				GlobalMethods.clearByClass(player, "gameButton");
				placeUnits.addUnitPlacementButton(player, game);
				placeUnits.printUnitChoice(player, game);
				placeUnits.activateValidUnits(player, game);
				placeUnits.activateValidPlanets(player, game);
			} else if (turnName.equals("placeFrontlineUnits")){
					GlobalMethods.clearActionStage(player);
					GlobalMethods.clearByClass(player, "gameButton");
					battleHandler.setCombatModeOn(player, game);
					battleHandler.setBattleField(player, game);
					battleHandler.displayUnplacedUnits(player, game);
					battleHandler.activateValidBattleUnits(player, game);
					battleHandler.addEndFrontLineTurnButton(player, game);
					battleHandler.addAllCheckerEvents(player, game);
			} else if (turnName.equals("placeSupportUnits")){
				GlobalMethods.clearByClass(player, "gameButton");
				battleHandler.addEndSupportLineTurnButton(player, game);
				battleHandler.activateValidBattleUnits(player, game);
				battleHandler.addAllCheckerEvents(player, game);
			} else if (turnName.equals("placeCombatCards")){
				GlobalMethods.clearByClass(player, "gameButton");
				GlobalMethods.clearActionStage(player);
				battleHandler.displayCombatCardChoice(player, game);
				battleHandler.addAllCheckerEvents(player, game);
				battleHandler.addEndBattleCardTurnButton(player, game);
			} else if (turnName.equals("revealBattleCards")){
				GlobalMethods.clearByClass(player, "gameButton");
				GlobalMethods.clearActionStage(player);
				battleHandler.addEndSupportLineTurnButton(player, game);
				battleHandler.revealBattleCard(player, game);
				battleHandler.activateEndTurn(player, game);
			} else if (turnName.equals("destroyUnits")){
				GlobalMethods.clearByClass(player, "gameButton");
				battleHandler.revealBattleCard(player, game);
				battleHandler.activateDeletableUnits(player, game);
			} else if (turnName.equals(GameConstants.moveRetreatUnitTurnName)){
				GlobalMethods.clearByClass(player, "gameButton");
				battleHandler.setCombatModeOff(player, game);
				placeUnits.activateValidUnits(player, game);
				placeUnits.activateValidPlanets(player, game);
				battleHandler.addEndRetreatButton(player, game);
				placeUnits.checkEndTurn(player, game);
			} else if (turnName.equals(GameConstants.buildUnitsTurnName)){
				GlobalMethods.clearByClass(player, "gameButton");
				buildUnits.displayUnlockedUnits(player, game);
				buildUnits.activateUnlockedUnits(player, game);
				buildUnits.addBuildingUnitTurnButton(player, game);
				buildUnits.checkEndTurn(player, game);
			} else if (turnName.equals(GameConstants.buildBuildingsTurnName)){
				GlobalMethods.clearByClass(player, "gameButton");
				GlobalMethods.clearActionStage(player);
				buildUnits.displayUnlockedBuildings(player, game);
				buildUnits.addBuildingUnitTurnButton(player, game);
				buildUnits.checkEndTurn(player, game);
			} else if (turnName.equals(GameConstants.buildBaseTurnName)){
				GlobalMethods.clearByClass(player, "gameButton");
				GlobalMethods.clearActionStage(player);
				buildUnits.displayUnlockedUnits(player, game);
				buildUnits.activateUnlockedUnits(player, game);
				buildUnits.addBuildingUnitTurnButton(player, game);
				buildUnits.checkEndTurn(player, game);
			}
		}
	}
	
	//TODO
	// renvoit l'affichage correspondant à l'état du jeu pour un joueur
	// Cette fonction doit pouvoir tout réafficher à partir d'un page vierge
	// Cela permet d'afficher correctement la partie si une personne rafraichit sa page
	// ou se reconnecte, cette fonction peut aussi servir à charger une partie sauvegardée
	public static void printCurrentTurnScreen(String player, StarcraftGame game){
		String turnName = game.getTurnPart();
		//System.out.println(turnName);
		//fonctions affichant les informations nécessaires aux joueurs quelque soit l'étape courante du jeu.
		GlobalMethods.printPlayerTurn(player, game);
		factionChoice.printChosenFactions(player, game);
		printPlayerBaordInfo(player, game);
		if (turnName.equals("factionChoice")){
			factionChoice.printFactionChoice(player, game);
		}else{
			GlobalMethods.printGameBoard(player);
			if (turnName.equals("leadershipChoice")){
				leadershipChoice.printLeadershipChoice(player, game);
			}else {
				GlobalMethods.upgradeActionMenu(player);
				placePlanets.addGalaxyCanvas(player, game);
				placePlanets.addActionCanvas(player, game);
				if (turnName.equals("placePlanet")){
					placePlanets.addPlacePlanetButtons(player, game);
					placePlanets.updateGalaxySize(player, game);
					placePlanets.printAllGalaxyPlanets(player, game);
					placeUnits.printAllGalaxyUnits(player, game);
					placePlanets.setValidPlacements(player, game);
					placePlanets.printPlanetChoice(player, game);
				}else{
					placePlanets.resizeGalaxy(player, game);
					placePlanets.printAllGalaxyPlanets(player, game);
					placeUnits.printAllGalaxyUnits(player, game);
					resourceHandler.displayAllWorkersInGalaxy(player, game);
					placeZRoad.displayAllLink(player, game);
					planningPhase.displayPlacedOrders(player, game);
					if (turnName.equals(GameConstants.placeBaseTurnName)){
						placeUnits.addUnitPlacementButton(player, game);
						placeUnits.printUnitChoice(player, game);
						placeUnits.activateCurrentPlanet(player, game);
						placeUnits.activateValidUnits(player, game);
					}else if (turnName.equals(GameConstants.placeZRoadTurnName)){
						placeZRoad.addPlaceZRoadButtons(player, game);
						placeZRoad.activateAvailableRoads(player, game);
					}else if (turnName.equals("placeUnit")){
						placeUnits.addUnitPlacementButton(player, game);
						placeUnits.printUnitChoice(player, game);
						placeUnits.activateValidUnits(player, game);
						placeUnits.activateValidPlanets(player, game);
						placeUnits.activateValidLinks(player, game);
					}else if (turnName.startsWith(GameConstants.planningPhaseTurnName)){
						planningPhase.displayAvailableOrders(player, game);
						planningPhase.addPlanningPhaseButton(player, game);
						planningPhase.activateValidPlanetSquares(player, game);
					}else if (turnName.startsWith(GameConstants.galaxyOrderChoiceTurnName)){
						galaxyOrderChoice.addGalaxyOrderChoiceButton(player, game);
						galaxyOrderChoice.activateValidSquareOrders(player, game);
					}else if (turnName.equals(GameConstants.executeChoiceTurnName)){
						galaxyOrderChoice.addActivationChoiceButton(player, game);
					} else if (turnName.equals(GameConstants.moveUnitTurnName)){
						placeUnits.addUnitPlacementButton(player, game);
						placeUnits.printUnitChoice(player, game);
						placeUnits.activateValidUnits(player, game);
						placeUnits.activateValidPlanets(player, game);
					} else if (turnName.equals("placeFrontlineUnits")){
						battleHandler.setCombatModeOn(player, game);
						battleHandler.setBattleField(player, game);
						battleHandler.displayUnplacedUnits(player, game);
						battleHandler.displayAllBattleUnits(player, game);
						battleHandler.activateValidBattleUnits(player, game);
						battleHandler.addEndFrontLineTurnButton(player, game);
						battleHandler.sendEndBattleTurn(player, game);
						battleHandler.addAllCheckerEvents(player, game);
					} else if (turnName.equals("placeSupportUnits")){
						battleHandler.addEndSupportLineTurnButton(player, game);
						battleHandler.setCombatModeOn(player, game);
						battleHandler.setBattleField(player, game);
						battleHandler.displayUnplacedUnits(player, game);
						battleHandler.displayAllBattleUnits(player, game);
						battleHandler.activateValidBattleUnits(player, game);
						battleHandler.addAllCheckerEvents(player, game);
						battleHandler.sendEndBattleTurn(player, game);
					} else if (turnName.equals("placeCombatCards")){
						battleHandler.setCombatModeOn(player, game);
						battleHandler.displayCombatCardChoice(player, game);
						battleHandler.setCardBattleField(player, game);
						battleHandler.displayAllBattleUnits(player, game);
						battleHandler.addAllCheckerEvents(player, game);
						battleHandler.displayAllHiddenBattleCard(player, game);
						battleHandler.addEndBattleCardTurnButton(player, game);
					} else if (turnName.equals("revealBattleCards")){
						battleHandler.setCombatModeOn(player, game);
						battleHandler.setCardBattleField(player, game);
						battleHandler.displayAllBattleUnits(player, game);
						battleHandler.revealBattleCard(player, game);
						battleHandler.addEndSupportLineTurnButton(player, game);
						battleHandler.activateEndTurn(player, game);
					} else if (turnName.equals("destroyUnits")){
						battleHandler.setCombatModeOn(player, game);
						battleHandler.setCardBattleField(player, game);
						battleHandler.displayAllBattleUnits(player, game);
						battleHandler.revealBattleCard(player, game);
						battleHandler.activateDeletableUnits(player, game);
					} else if (turnName.equals(GameConstants.moveRetreatUnitTurnName)){
						placeUnits.activateValidUnits(player, game);
						placeUnits.activateValidPlanets(player, game);
						battleHandler.addEndRetreatButton(player, game);
						placeUnits.checkEndTurn(player, game);
					} else if (turnName.equals(GameConstants.buildUnitsTurnName)){
						buildUnits.displayUnlockedUnits(player, game);
						buildUnits.activateUnlockedUnits(player, game);
						buildUnits.activateResourcePlaces(player, game);
						buildUnits.addResourcePlacesEvent(player, game);
						placeBuiltUnit(player, game);
						buildUnits.addBuildingUnitTurnButton(player, game);
						buildUnits.checkEndTurn(player, game);
					} else if (turnName.equals(GameConstants.buildBuildingsTurnName)){
						buildUnits.displayUnlockedBuildings(player, game);
						buildUnits.addBuildingUnitTurnButton(player, game);
						buildUnits.checkEndTurn(player, game);
						buildUnits.activateResourcePlaces(player, game);
						buildUnits.addResourcePlacesEvent(player, game);
					} else if (turnName.equals(GameConstants.buildBaseTurnName)){
						buildUnits.displayUnlockedUnits(player, game);
						buildUnits.activateUnlockedUnits(player, game);
						buildUnits.activateResourcePlaces(player, game);
						buildUnits.addResourcePlacesEvent(player, game);
						placeBuiltUnit(player, game);
						buildUnits.addBuildingUnitTurnButton(player, game);
						buildUnits.checkEndTurn(player, game);
					}
				}
			}
		}
	}
	
	/**met à jour la position des unités de la galaxie pour tous les joueurs**/
	public static void updateUnitDisplay(long unitId, StarcraftGame game){
		for (String player : game.getPlayerList().keySet()){
			placeUnits.printGalaxyUnit(player, unitId, game);
		}
	}

	public static void callChoosePlayerFaction(String player, String speciesName, String factionName, StarcraftGame game){
    	factionChoice.choosePlayerFaction(player, speciesName, factionName, game);
	}
	
	public static void callChooseLeadershipCard(String player, String leadershipCardName, StarcraftGame game){
		leadershipChoice.chooseLeadershipCard(player, leadershipCardName, game);
	}
	
	public static void callPlacePlanet(String player, String planetName, String coordinates, StarcraftGame game){
		placePlanets.placePlanet(player, planetName, coordinates, game);
	}
	
	public static void callRotatePlanet(String player, String planetName, StarcraftGame game) {
		placePlanets.rotatePlanet(player, planetName, game);
	}
	
	
	// fonction affichant les informations sur un joueur (faction, ressources, batiments construits, etc...)
	// cette fonction doit envoyer des informations différentes à chaque joueur selon leur situation
	public static void printPlayerBaordInfo(String player, StarcraftGame game){
		GlobalMethods.clearByClass(player, "playerInfoBoard");
		factionChoice.printPlayerFactionInfo(player, game);
		leadershipChoice.printPlayerLeadershipInfo(player, game);
		factionChoice.printCombatCardInfo(player, game);
		resourceHandler.displayPlayerResourceInfo(player, game);
		resourceHandler.displayBaseWorkers(game.getPlayer(player));
		displayWorkersOnBaseResources(player, game);
	}
	
	public static void callDisplayBaseWorkers(String player, StarcraftGame game){
		resourceHandler.displayBaseWorkers(game.getPlayer(player));
	}

	public static void callAskValidPlacements(String playerName, String unitId, StarcraftGame game) {
		placeUnits.askValidPlacements(playerName, Long.parseLong(unitId), game);
	}

	public static void callSendUnitPlacement(String playerName, String unitId, String coordinates, String areaId, StarcraftGame game) {
		placeUnits.sendUnitPlacement(playerName, Long.parseLong(unitId), coordinates, Integer.parseInt(areaId), game);
		
	}

	public static void callReturnUnitToPool(String playerName, String unitId, StarcraftGame game) {
		placeUnits.returnUnitToPool(playerName, Long.parseLong(unitId), game);
		
	}

	public static void callEndUnitPlacementTurn(String playerName, StarcraftGame game) {
		placeUnits.endUnitPlacementTurn(playerName, game);
		
	}

	public static void callAskAllRoadPlacements(String playerName, StarcraftGame game) {
		placeZRoad.activateAvailableRoads(playerName, game);
	}

	public static void callSendRoadPlacement(String playerName, String coordinates, String roadPosition, StarcraftGame game) {
		placeZRoad.sendRoadPlacement(playerName, coordinates, Integer.parseInt(roadPosition), game);
		
	}

	public static void callEndRoadPlacementTurn(String playerName,
			String coordinates1,
			String road1,
			String coordinates2,
			String road2,
			StarcraftGame game) {
		placeZRoad.endRoadPlacementTurn(playerName, coordinates1, Integer.parseInt(road1), coordinates2, Integer.parseInt(road2), game);
	}

	public static void callEndRoadPlacementTurn2(String playerName, StarcraftGame game) {
		placeZRoad.endRoadPlacementTurn2(playerName, game);
	}

	public static void callSendPlanningPhaseTurn(String playerName, String coordinates, String orderId, StarcraftGame game) {
		planningPhase.sendPlanningPhaseTurn(playerName, coordinates, Integer.parseInt(orderId), game);
	}

	public static void callAskOrderStack(String playerName, String coordinates, StarcraftGame game) {
		planningPhase.askOrderStack(playerName, coordinates, game);
		
	}

	public static void callEndGalaxyOrderChoiceTurn(String playerName, String coordinates, StarcraftGame game) {
		galaxyOrderChoice.endGalaxyOrderChoiceTurn(playerName, coordinates, game);
	}

	public static void callCancelOrder(String playerName, StarcraftGame game) {
		galaxyOrderChoice.cancelOrder(playerName, game);
	}

	public static void callExecuteOrder(String playerName, StarcraftGame game) {
		galaxyOrderChoice.executeOrder(playerName, game);
	}

	public static void callAskValidBattlePlacements(String playerName, String unitId, StarcraftGame game) {
		battleHandler.askValidBattlePlacements(playerName, Long.parseLong(unitId), game);
		
	}

	public static void callSendBattleUnitPlacement
	(String playerName, String unitId, String battleRow, String battlePlace, StarcraftGame game) {
		battleHandler.sendBattleUnitPlacement(playerName, Long.parseLong(unitId), battleRow, battlePlace, game);
	}

	public static void callReturnUnitToBattlePool(String playerName, String unitId, StarcraftGame game) {
		battleHandler.returnUnitToBattlePool(playerName, Long.parseLong(unitId), game);
	}

	public static void callEndFrontLineTurn(String playerName, StarcraftGame game) {
		battleHandler.endFrontLineTurn(playerName, game);
		
	}

	public static void callAskCombatCardHand(String playerName, StarcraftGame game) {
		factionChoice.displayCombatCards(playerName, game);
	}

	public static void callEndSupportLineTurn(String playerName, StarcraftGame game) {
		battleHandler.endSupportLineTurn(playerName, game);
	}

	public static void callAskValidBattleCardPlacements(String playerName, String cardId, StarcraftGame game) {
		battleHandler.askValidBattleCardPlacements(playerName, Integer.parseInt(cardId.substring(4)), game);
		
	}

	public static void callSendBattleCardPlacement(String playerName, String cardId, String battleRow, String battlePlace,
			StarcraftGame game) {
		battleHandler.sendBattleCardPlacement(playerName, Integer.parseInt(cardId.substring(4)), battleRow, battlePlace, game);
	}

	public static void callReturnCardToBattlePool(String playerName, String cardId, StarcraftGame game) {
		battleHandler.returnCardToBattlePool(playerName, Integer.parseInt(cardId.substring(4)), game);
	}

	public static void callEndBattleCardTurn(String playerName, StarcraftGame game) {
		battleHandler.endBattleCardTurn(playerName, game);
	}

	/**fonction particulière supprimant l'affichage d'une unité pour tous les joueurs**/
	public static void removeUnitDisplay(long unitId, StarcraftGame game){
		for (String playerName:game.getPlayerList().keySet()){
			battleHandler.removeUnitDisplay(unitId, playerName, game);
		}
	}
	
	/**fonction particulière supprimant l'affichage d'une unité pour tous les joueurs**/
	public static void callClearBattleStage(StarcraftGame game){
		for (String playerName:game.getPlayerList().keySet()){
			battleHandler.clearBattleStage(playerName, game);
		}
	}

	public static void callDestroyBattleUnit(String playerName, String unitId, StarcraftGame game) {
		battleHandler.destroyBattleUnit(playerName, Long.parseLong(unitId), game);
	}

	public static void callEndRetreatTurn(String playerName, StarcraftGame game) {
		battleHandler.endRetreatTurn(playerName, game);
	}
	
	public static void callDisplayPlayerResourceInfo(String player, StarcraftGame game) {
		resourceHandler.displayPlayerResourceInfo(player, game);
	}

	public static void callAskBuyingOrder(String playerName, String unitName, StarcraftGame game) {
		buildUnits.askBuyingUnitOrder(playerName, unitName, game);
	}
	
	public static void callUpdateCardNumber(StarcraftPlayer player) {
		factionChoice.updateCardNumber(player);
	}

	public static void callSetWorkerOnArea(String playerName, String coordinates, String areaId, StarcraftGame game) {
		buildUnits.setWorkerOnArea(playerName, coordinates, Integer.parseInt(areaId), game);
	}
	
	public static void updateAreaWorkerDisplay(List<Integer> coordinate, StarcraftPlayer workerOwner, StarcraftGame game){
		for (String player:game.getPlayerList().keySet()){
			resourceHandler.displayWorkersOnArea(player, coordinate, workerOwner, game);
		}
	}
	
	/**montre les travailleurs disponibles et en réserve**/
	public static void updateBaseWorkerDisplay(StarcraftPlayer player){
		resourceHandler.updateBaseWorkerDisplay(player);
	}
	
	public static void placeBuiltUnit(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayer(player);
		if (starcraftPlayer.getBuyingOrder() != null){
			if (starcraftPlayer.getBuyingOrder().isReady()){
				GlobalMethods.clearActionStage(player);
				GlobalMethods.clearObjectEvents(player);
				placeUnits.printUnitChoice(player, game);
				placeUnits.activateValidUnits(player, game);
				placeUnits.activateCurrentPlanet(player, game);
				placeUnits.activateValidLinks(player, game);
			}
		}
	}
	
	public static void endBuyingOrder(String player, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayer(player);
		starcraftPlayer.executeBuyingOrder();
		setBuildUnitStage(player, game);
	}
	
	public static void setBuildUnitStage(String player, StarcraftGame game){
		buildUnits.checkEndTurn(player, game);
		GlobalMethods.clearActionStage(player);
		GlobalMethods.clearObjectEvents(player);
		buildUnits.displayUnlockedUnits(player, game);
		buildUnits.activateUnlockedUnits(player, game);
	}
	
	public static void setBuildBuildingStage(String player, StarcraftGame game){
		buildUnits.checkEndTurn(player, game);
		GlobalMethods.clearActionStage(player);
		GlobalMethods.clearObjectEvents(player);
		buildUnits.displayUnlockedBuildings(player, game);
	}

	public static void callSetWorkerBaseMineral(String playerName, StarcraftGame game) {
		buildUnits.setWorkerBaseMineral(playerName, game);
	}

	public static void callSetWorkerBaseGas(String playerName, StarcraftGame game) {
		buildUnits.setWorkerBaseGas(playerName, game);
	}
	
	private static void displayWorkersOnBaseResources(String playerName, StarcraftGame game){
		StarcraftPlayer starcraftPlayer = game.getPlayerList().get(playerName);
		callDisplayWorkersOnBaseResources(starcraftPlayer);
	}
	
	public static void callDisplayWorkersOnBaseResources(StarcraftPlayer player){
		resourceHandler.displayWorkersOnBaseResources(player);
	}

	public static void callEndBuildingUnitTurn(String playerName, StarcraftGame game) {
		buildUnits.endBuildingUnitTurn(playerName, game);
	}

	public static void callCancelBuyOrder(String playerName, StarcraftGame game) {
		buildUnits.cancelBuyOrder(playerName, game);
	}

	public static void callAskBuyingBuildingOrder(String playerName, String buildingName, StarcraftGame game) {
		int separatorIndex = buildingName.indexOf('.');
		int number  = Integer.parseInt(buildingName.substring(0, separatorIndex));
		int level  = Integer.parseInt(buildingName.substring(separatorIndex + 1));
		buildUnits.askBuyingBuildingOrder(playerName, number, level, game);
	}
}
