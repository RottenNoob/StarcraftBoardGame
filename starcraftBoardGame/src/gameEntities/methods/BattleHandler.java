package gameEntities.methods;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.gameMap.StarcraftBattle;
import gameEntities.playerItems.CombatCard;

public class BattleHandler {
	
	public void clearBattleStage(String player, StarcraftGame game) {
		try {
			JSONObject clearBattleStage = new JSONObject()
					.put("action", "clearBattleStage");
			GlobalMethods.sendPlayerAction(player, clearBattleStage);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void activateDeletableUnits(String player, StarcraftGame game){
		if (game.getPlayerCurrentlyPlaying().equals(player)){
			StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
			try {
				JSONObject activateDeletableUnits = new JSONObject()
						.put("action", "activateDeletableUnits");
				if (starcraftBattle.getAttackingPlayer().getName().equals(player)){
					if (starcraftBattle.getAttackingUnitsToDestroy().size() > 0){
						JSONArray validUnitsArray = new JSONArray();
						for (long unitId:starcraftBattle.getAttackingUnitsToDestroy().get(0)){
							if (starcraftBattle.getAttackingUnits().contains(unitId)){
								JSONObject validUnitJS = new JSONObject()
										.put("unitId", unitId);
								validUnitsArray.put(validUnitJS);
							}
						}
						activateDeletableUnits.put("unitList", validUnitsArray);
						GlobalMethods.sendPlayerAction(player, activateDeletableUnits);
					}
				}else{
					if (starcraftBattle.getDefendingUnitsToDestroy().size() > 0){
						JSONArray validUnitsArray = new JSONArray();
						for (long unitId:starcraftBattle.getDefendingUnitsToDestroy().get(0)){
							if (starcraftBattle.getDefendingUnits().contains(unitId)){
								JSONObject validUnitJS = new JSONObject()
										.put("unitId", unitId);
								validUnitsArray.put(validUnitJS);
							}
						}
						activateDeletableUnits.put("unitList", validUnitsArray);
						GlobalMethods.sendPlayerAction(player, activateDeletableUnits);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void removeUnitDisplay(long unitId, String player, StarcraftGame game){
		try {
			JSONObject removeUnitDisplay = new JSONObject()
					.put("action", "removeUnitDisplay")
					.put("unitId", unitId);
			GlobalMethods.sendPlayerAction(player, removeUnitDisplay);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**ajoute le bouton de placement des unités**/
	public void addEndFrontLineTurnButton(String player, StarcraftGame game){
		JSONObject addEndFrontLineTurnButton;
		try {
			addEndFrontLineTurnButton = new JSONObject()
					.put("action", "addEndFrontLineTurnButton");
			GlobalMethods.sendPlayerAction(player, addEndFrontLineTurnButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void addEndSupportLineTurnButton(String player, StarcraftGame game){
		JSONObject addEndSupportLineTurnButton;
		try {
			addEndSupportLineTurnButton = new JSONObject()
					.put("action", "addEndSupportLineTurnButton");
			GlobalMethods.sendPlayerAction(player, addEndSupportLineTurnButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void addEndBattleCardTurnButton(String player, StarcraftGame game){
		try {
			JSONObject addEndBattleCardTurnButton = new JSONObject()
					.put("action", "addEndBattleCardTurnButton");
			GlobalMethods.sendPlayerAction(player, addEndBattleCardTurnButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**ajoute le bouton de fin de retraites**/
	public void addEndRetreatButton(String player, StarcraftGame game){
		try {
			JSONObject addEndRetreatButton = new JSONObject()
					.put("action", "addEndRetreatButton");
			GlobalMethods.sendPlayerAction(player, addEndRetreatButton);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public void setCombatModeOn(String player, StarcraftGame game){
		JSONObject setCombatModeOn;
		try {
			setCombatModeOn = new JSONObject()
					.put("action", "setCombatModeOn");
			GlobalMethods.sendPlayerAction(player, setCombatModeOn);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setCombatModeOff(String player, StarcraftGame game){
		JSONObject setCombatModeOff;
		try {
			setCombatModeOff = new JSONObject()
					.put("action", "setCombatModeOff");
			GlobalMethods.sendPlayerAction(player, setCombatModeOff);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**créer le quadrillage où l'on place les éléments de la bataille**/
	public void setCardBattleField(String player, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		StarcraftPlayer activeplayer= game.getPlayer(player);
		JSONObject setBattleField;
		try {
			setBattleField = new JSONObject()
					.put("action", "setCardBattleField")
					.put("height", starcraftBattle.getSkirmishNumber())
					.put("attackWidth", starcraftBattle.getMaxAttackDepth())
					.put("defenseWidth", starcraftBattle.getMaxDefenseDepth())
					.put("cardNumber", activeplayer.getCombatCardsInHand().size());
			GlobalMethods.sendPlayerAction(player, setBattleField);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**créer le quadrillage où l'on place les éléments de la bataille et le menu d'action où l'on place les unités**/
	public void setBattleField(String player, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		JSONObject setBattleField;
		try {
			setBattleField = new JSONObject()
					.put("action", "setBattleField")
					.put("height", starcraftBattle.getSkirmishNumber())
					.put("attackWidth", starcraftBattle.getMaxAttackDepth())
					.put("defenseWidth", starcraftBattle.getMaxDefenseDepth())
					.put("attackersNumber", starcraftBattle.getAttackingUnits().size())
					.put("defendersNumber", starcraftBattle.getDefendingUnits().size());
			GlobalMethods.sendPlayerAction(player, setBattleField);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**affiche les cartes de la main du joueur**/
	public void displayCombatCardChoice(String player, StarcraftGame game){
		StarcraftPlayer activeplayer= game.getPlayer(player);
		for (int combatCardId:activeplayer.getCombatCardsInHand().keySet()){
			JSONObject combatCardJS = activeplayer.getCombatCardsInHand().get(combatCardId).getCardJS("displayCombatCardChoice");
			GlobalMethods.sendPlayerAction(player, combatCardJS);
		}
	}
	
	/**affiche les unités non placées dans le canvas d'action**/
	public void displayUnplacedUnits(String player, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		//liste des unités attaquantes
		JSONArray attackUnitList = new JSONArray();
		for (long attackerId:starcraftBattle.getAttackingUnits()){
			//on ne met dans le menu que les unités non placées
			if (starcraftBattle.getUnplacedUnits().containsKey(attackerId)){
				JSONObject attackersJS;
				try {
					attackersJS = new JSONObject()
							.put("unitId", attackerId);
					attackUnitList.put(attackersJS);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		//liste des unités en défense
		JSONArray defendUnitList = new JSONArray();
		for (long defenderId:starcraftBattle.getDefendingUnits()){
			//on ne met dans le menu que les unités non placées
			if (starcraftBattle.getUnplacedUnits().containsKey(defenderId)){
				JSONObject defendersJS;
				try {
					defendersJS = new JSONObject()
							.put("unitId", defenderId);
					defendUnitList.put(defendersJS);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		JSONObject displayUnplacedUnits;
		try {
			displayUnplacedUnits = new JSONObject()
					.put("action", "displayUnplacedUnits")
					.put("attackers", attackUnitList)
					.put("defenders", defendUnitList);
			GlobalMethods.sendPlayerAction(player, displayUnplacedUnits);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**active les unités à manipuler**/
	public void activateValidBattleUnits(String player, StarcraftGame game){
		if (player.equals(game.getPlayerCurrentlyPlaying())){
			StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
			ArrayList<Long> activeUnits = starcraftBattle.getActiveUnits(game);
			//liste des unités attaquantes
			JSONArray activeBattleUnitList = new JSONArray();
			for (long unitId:activeUnits){
				JSONObject activeUnitsJS;
				try {
					activeUnitsJS = new JSONObject()
							.put("unitId", unitId);
					activeBattleUnitList.put(activeUnitsJS);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			JSONObject activateValidBattleUnits;
			try {
				activateValidBattleUnits = new JSONObject()
						.put("action", "activateValidBattleUnits")
						.put("unitList", activeBattleUnitList);
				GlobalMethods.sendPlayerAction(player, activateValidBattleUnits);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void displayAllBattleUnits(String player, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		for (long unitId:starcraftBattle.getAttackingUnits()){
			if (!starcraftBattle.getUnplacedUnits().containsKey(unitId)){
				displayBattleUnit(player, unitId, game);
			}
		}
		for (long unitId:starcraftBattle.getDefendingUnits()){
			if (!starcraftBattle.getUnplacedUnits().containsKey(unitId)){
				displayBattleUnit(player, unitId, game);
			}
		}
	}
	
	/**affiche une unité sur le champs de bataille**/
	public void displayBattleUnit(String player, long unitId, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		String[] unitPosition = starcraftBattle.getBattlePosition(unitId);

		JSONObject displayBattleUnit;
		try {
			displayBattleUnit = new JSONObject()
					.put("action", "displayBattleUnit")
					.put("name", unitId)
					.put("battleRow", unitPosition[0])
					.put("battlePlace", unitPosition[1]);
			GlobalMethods.sendPlayerAction(player, displayBattleUnit);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void revealBattleCard(String player, StarcraftGame game){
		try {
			JSONObject removeBackCards = new JSONObject()
					.put("action", "removeBackCards");
			GlobalMethods.sendPlayerAction(player, removeBackCards);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		ArrayList<CombatCard> combatCardList = starcraftBattle.getAllCombatCards();
		for (CombatCard card:combatCardList){
			displayBattleCard(player, card.getId(), card.getOwner(), game);
		}
	}
	
	public void displayBattleCard(String player, int cardId, String cardOwner, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		String[] cardPosition = starcraftBattle.getBattleCardPosition(cardId, cardOwner);
		if (cardPosition != null){
			JSONObject displayBattleCard = null;
			try {
				CombatCard card = starcraftBattle.getBattleCard(cardId, cardOwner);
				displayBattleCard = card.getCardJS("displayBattleCard")
						.put("battleRow", cardPosition[0])
						.put("battlePlace", cardPosition[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			GlobalMethods.sendPlayerAction(player, displayBattleCard);
		}
	}
	
	public void displayAllHiddenBattleCard(String player, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		ArrayList<CombatCard> combatCardList = starcraftBattle.getAllCombatCards();
		for (CombatCard card:combatCardList){
			displayHiddenBattleCard(player, card.getId(), card.getOwner(), game);
		}
	}
	
	/**affiche une unité sur le champs de bataille**/
	public void displayHiddenBattleCard(String player, int cardId, String cardOwner, StarcraftGame game){
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		String[] cardPosition = starcraftBattle.getBattleCardPosition(cardId, cardOwner);
		if (cardPosition != null){
			JSONObject displayBattleCard = null;
			try {
				if (player.equals(cardOwner)){
					CombatCard card = starcraftBattle.getBattleCard(cardId, cardOwner);
					displayBattleCard = card.getCardJS("displayBattleCard")
							.put("battleRow", cardPosition[0])
							.put("battlePlace", cardPosition[1]);
				}else{
					displayBattleCard = new JSONObject()
							.put("action", "displayBackCard")
							.put("id", cardId)
							.put("color", game.getPlayerList().get(cardOwner).getPlayerColor())
							.put("battleRow", cardPosition[0])
							.put("battlePlace", cardPosition[1]);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			GlobalMethods.sendPlayerAction(player, displayBattleCard);
		}
	}
	
	public void addAllCheckerEvents(String player, StarcraftGame game){
		try {
			JSONObject addAllCheckerEvents = new JSONObject()
					.put("action", "addAllCheckerEvents");
			GlobalMethods.sendPlayerAction(player, addAllCheckerEvents);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendEndBattleTurn(String player, StarcraftGame game){
		if (player.equals(game.getPlayerCurrentlyPlaying())){
			StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
			Boolean end = starcraftBattle.checkEndBattleTurn(game);
			JSONObject sendEndBattleTurn;
			try {
				sendEndBattleTurn = new JSONObject()
						.put("action", "sendEndBattleTurn")
						.put("ending", end);
				GlobalMethods.sendPlayerAction(player, sendEndBattleTurn);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**rend possible la fin du tour**/
	public void activateEndTurn(String player, StarcraftGame game){
		if (player.equals(game.getPlayerCurrentlyPlaying())){
			JSONObject sendEndBattleTurn;
			try {
				sendEndBattleTurn = new JSONObject()
						.put("action", "sendEndBattleTurn")
						.put("ending", true);
				GlobalMethods.sendPlayerAction(player, sendEndBattleTurn);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*gestion des actions du joueur*/

	/**fonction indiquant au client où il peut placer l'unité sélectionnée**/
	public void askValidBattlePlacements(String playerName, long unitId, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		ArrayList<String[]> validPlaces = starcraftBattle.getValidPlacement(unitId, game);
		try {
			JSONArray validPlacesArray = new JSONArray();
			for (String[] validPlace:validPlaces){
				JSONObject validPlaceJS = new JSONObject()
						.put("battleRow", validPlace[0])
						.put("battlePlace", validPlace[1]);
				validPlacesArray.put(validPlaceJS);
			}
			JSONObject sendValidPlacements = new JSONObject()
					.put("action", "sendValidBattlePlacements")
					.put("name", unitId)
					.put("placeNames", validPlacesArray);
			GlobalMethods.sendPlayerAction(playerName, sendValidPlacements);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void askValidBattleCardPlacements(String playerName, int cardId, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		ArrayList<String[]> validPlaces = starcraftBattle.getValidCardPlacement(playerName, cardId, game);
		try {
			JSONArray validPlacesArray = new JSONArray();
			for (String[] validPlace:validPlaces){
				JSONObject validPlaceJS = new JSONObject()
						.put("battleRow", validPlace[0])
						.put("battlePlace", validPlace[1]);
				validPlacesArray.put(validPlaceJS);
			}
			JSONObject sendValidBattleCardPlacements = new JSONObject()
					.put("action", "sendValidBattleCardPlacements")
					.put("name", "card"+Integer.toString(cardId))
					.put("placeNames", validPlacesArray);
			GlobalMethods.sendPlayerAction(playerName, sendValidBattleCardPlacements);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void sendBattleUnitPlacement
	(String playerName, long unitId, String battleRow, String battlePlace, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		int battleRowId = Integer.parseInt(battleRow.substring(8));
		starcraftBattle.addUnitToSkirmish(battleRowId, battlePlace, unitId);
		for (String player : game.getPlayerList().keySet()){
			displayBattleUnit(player, unitId, game);
		}
		sendEndBattleTurn(playerName, game);
	}
	
	public void sendBattleCardPlacement(String playerName, int cardId, String battleRow, String battlePlace,
			StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		int battleRowId = Integer.parseInt(battleRow.substring(8));
		starcraftBattle.addCardToSkirmish(battleRowId, battlePlace, cardId, playerName);
		for (String player : game.getPlayerList().keySet()){
			displayHiddenBattleCard(player, cardId, playerName, game);
		}
	}

	/**renvoie une carte dans le menu d'action, renvoie sa carte de support si celle-ci existe**/
	public void returnCardToBattlePool(String playerName, int cardId, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		int supportCardId = starcraftBattle.addBattleCardToMenu(cardId, playerName);
		try {
			for (String player : game.getPlayerList().keySet()){
				if (player.equals(playerName)){
					JSONObject sendCardToBattlePool = new JSONObject()
							.put("action", "sendCardToBattlePool")
							.put("name", cardId);
					GlobalMethods.sendPlayerAction(player, sendCardToBattlePool);
				}else{
					JSONObject deleteCardBack = new JSONObject()
							.put("action", "deleteCardBack")
							.put("name", cardId);
					GlobalMethods.sendPlayerAction(player, deleteCardBack);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (supportCardId > -1){
			returnCardToBattlePool(playerName, supportCardId, game);
		}
	}

	/**renvoie une unité dans le menu d'action**/
	public void returnUnitToBattlePool(String playerName, long unitId, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		starcraftBattle.addUnitToUnplacedUnits(unitId);
		Boolean attacker = false;
		if (starcraftBattle.getAttackingUnits().contains(unitId)){
			attacker = true;
		}
		try {
			JSONObject sendUnitToBattlePool = new JSONObject()
					.put("action", "sendUnitToBattlePool")
					.put("name", unitId)
					.put("attacker", attacker);
			for (String player : game.getPlayerList().keySet()){
				GlobalMethods.sendPlayerAction(player, sendUnitToBattlePool);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendEndBattleTurn(playerName, game);
	}

	/**fin du tour**/
	public void endFrontLineTurn(String playerName, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		starcraftBattle.endFrontLineTurn(game);
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}
	
	/**fin du tour**/
	public void endSupportLineTurn(String playerName, StarcraftGame game) {
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	/**fin du tour**/
	public void endBattleCardTurn(String playerName, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		ArrayList<CombatCard> addedCardList = starcraftBattle.setEndCardTurn(playerName);
		if (playerName.equals(starcraftBattle.getDefendingPlayer().getName())){
			starcraftBattle.resolveBattle(game);
		}
		game.nextTurn();
		
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			for (CombatCard addedCard:addedCardList){
				displayHiddenBattleCard(player, addedCard.getId(), addedCard.getOwner(), game);
			}
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	public void destroyBattleUnit(String playerName, long unitId, StarcraftGame game) {
		StarcraftBattle starcraftBattle = game.getGalaxy().getStarcraftBattle();
		starcraftBattle.destroyChosenUnit(unitId, game);
		game.isAutomatedTurn();
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}

	public void endRetreatTurn(String playerName, StarcraftGame game) {
		if (playerName.equals(game.getGalaxy().getStarcraftBattle().getAttackingPlayer().getName())){
			game.endBattle();
		}
		game.nextTurn();
		// on met à jour l'affichage reflétant les changements qui on eu lieu lors du tour précédent
		for (String player : game.getPlayerList().keySet()){
			GameTurnHandler.printNextTurnScreen(player, game);
		}
	}
}