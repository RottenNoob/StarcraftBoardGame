package gameEntities.gameMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gameEntities.GameConstants;
import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;
import gameEntities.methods.GameTurnHandler;
import gameEntities.playerItems.CombatCard;
import gameEntities.playerItems.StarcraftUnit;

public class StarcraftBattle implements java.io.Serializable {
	private final String frontLineTurn = "placeFrontlineUnits";
	private final String supportLineTurn = "placeSupportUnits";
	private final String placeCardTurn = "placeCombatCards";
	private final String destroyUnitTurn = "destroyUnits";
	
	//
	private Map<Long, StarcraftUnit> unplacedUnits = new HashMap<Long, StarcraftUnit>();
	private static final long serialVersionUID = 2895157665886278943L;
	private int[] coordinates = new int[]{0, 0, 0};
	private StarcraftPlayer attackingPlayer;
	private Set<Long> attackingUnits = new HashSet<Long>();
	private StarcraftPlayer defendingPlayer;
	private Set<Long> defendingUnits = new HashSet<Long>();
	private int skirmishNumber;
	private int maxDefenseDepth;
	private int maxAttackDepth;
	private Map<Integer, StarcraftSkirmish> skirmishList = new HashMap<Integer, StarcraftSkirmish>();
	private ArrayList<HashSet<Long>> attackingUnitsToDestroy = new ArrayList<HashSet<Long>>();
	private ArrayList<HashSet<Long>> defendingUnitsToDestroy = new ArrayList<HashSet<Long>>();
	//il s'agit des unités invisibles
	private Set<Long> cloackedUnits = new HashSet<Long>();
	private Set<Long> retreatingUnits = new HashSet<Long>();
	
	public int[] getCoordinates() {
		return coordinates;
	}
	
	public Set<Long> getRetreatedUnits() {
		return retreatingUnits;
	}
	
	public void setCoordinates(int[] newCoordinates) {
		this.coordinates = newCoordinates;
	}

	public StarcraftPlayer getAttackingPlayer() {
		return attackingPlayer;
	}

	public void setAttackingPlayer(StarcraftPlayer attackingPlayer) {
		this.attackingPlayer = attackingPlayer;
	}

	public Set<Long> getAttackingUnits() {
		return this.attackingUnits;
	}

	public Set<Long> getDefendingUnits() {
		return this.defendingUnits;
	}

	public StarcraftPlayer getDefendingPlayer() {
		return defendingPlayer;
	}

	public void setDefendingPlayer(StarcraftPlayer defendingPlayer) {
		this.defendingPlayer = defendingPlayer;
	}
	
	public void addAttackingUnit(long unitId) {
		this.attackingUnits.add(unitId);
	}
	
	public void addDefendingUnit(long unitId) {
		this.defendingUnits.add(unitId);
	}
	
	public void removeAttackingUnit(Long unitId) {
		this.attackingUnits.remove(unitId);
	}
	
	public void removeDefendingUnit(Long unitId) {
		this.defendingUnits.remove(unitId);
	}
	
	public Boolean isSuccessfulAttack(StarcraftGame game){
		Galaxy galaxy = game.getGalaxy();
		Boolean successfulAttack = true;
		if (this.defendingUnits.size() > 0){
			for (long unitId:this.defendingUnits){
				if (!this.retreatingUnits.contains(unitId)){
					StarcraftUnit unit = galaxy.getUnitList().get(unitId);
					if (unit.getAttackType() != null){
						successfulAttack = false;
						break;
					}
				}
			}
		}
		return successfulAttack;
	}
	
	public Boolean canEndTurn(String player, StarcraftGame game){
		Boolean result = true;
		Galaxy galaxy = game.getGalaxy();
		Boolean succesfulAttack = isSuccessfulAttack(game);
		PlanetArea area = galaxy.returnPlanetAt(this.coordinates[0], this.coordinates[1]).getArea(this.coordinates[2]);
		
		if (succesfulAttack){
			//retraite des unités en trop
			if (player.equals(this.attackingPlayer.getName())){
				if (this.attackingUnits.size() > area.getUnitLimit()){
					if (galaxy.countFriendlyUnitsInPlace(this.coordinates[0],
							this.coordinates[1],
							this.coordinates[2],
							"mobile",
							this.attackingPlayer) != area.getUnitLimit()){
						result = false;
					}
				}
			}else{
				if (galaxy.countFriendlyUnitsInPlace(this.coordinates[0],
						this.coordinates[1],
						this.coordinates[2],
						"mobile",
						this.defendingPlayer) > 0){
					result = false;
				}
			}
		}else{
			if (player.equals(this.attackingPlayer.getName())){
				if (galaxy.countFriendlyUnitsInPlace(this.coordinates[0],
						this.coordinates[1],
						this.coordinates[2],
						"mobile",
						this.attackingPlayer) > 0){
					result = false;
				}
			}
		}
		return result;
	}
	
	/**liste des unités pouvant faire retraite, cette fonction renvoie aussi les unité déjà parties pour que le joueur
	 * puisse continuer à les manipuler pour annuler ou modifier leurs actions**/
	public ArrayList<Long> getRetreatingUnits(String player, StarcraftGame game){
		Galaxy galaxy = game.getGalaxy();
		Boolean succesfulAttack = isSuccessfulAttack(game);
		
		ArrayList<Long> result = new ArrayList<Long>();
		PlanetArea area = galaxy.returnPlanetAt(this.coordinates[0], this.coordinates[1]).getArea(this.coordinates[2]);
		if (succesfulAttack){
			//retraite des unités en trop
			if (player.equals(this.attackingPlayer.getName())){
				if (this.attackingUnits.size() > area.getUnitLimit()){
					result.addAll(this.attackingUnits);
				}
			}else{
				result.addAll(this.defendingUnits);
			}
		}else{
			if (player.equals(this.attackingPlayer.getName())){
				result.addAll(this.attackingUnits);
			}
		}
		return result;
	}
	
	/**ajoute une liste d'unité à détruire dans la liste des attaquants**/
	public void addAttackersToDestroy(HashSet<Long> unitSet){
		int i = 0;
		Boolean added = false;
		while (i < this.attackingUnitsToDestroy.size() && !added){
			if (unitSet.size() <= this.attackingUnitsToDestroy.get(i).size()){
				this.attackingUnitsToDestroy.add(i, unitSet);
				added = true;
			}else{
				i++;
			}
		}
		if (i == this.attackingUnitsToDestroy.size()){
			this.attackingUnitsToDestroy.add(unitSet);
		}
	}
	
	/**ajoute une liste d'unité à détruire dans la liste des attaquants**/
	public void addDefendersToDestroy(HashSet<Long> unitSet){
		int i = 0;
		Boolean added = false;
		while (i < this.defendingUnitsToDestroy.size() && !added){
			if (unitSet.size() <= this.defendingUnitsToDestroy.get(i).size()){
				this.defendingUnitsToDestroy.add(i, unitSet);
				added = true;
			}else{
				i++;
			}
		}
		if (i == this.defendingUnitsToDestroy.size()){
			this.defendingUnitsToDestroy.add(unitSet);
		}
	}
	
	/**détruit une unité choisie parmi un ensemble d'unités à détruire puis enlève cet ensemble**/
	public void destroyChosenUnit(long id, StarcraftGame game){
		if (this.cloackedUnits.contains(id)){
			this.cloackedUnits.remove(id);
		}else{
			removeUnitFromOldPlace(id);
			if (this.attackingUnits.contains(id)){
				this.attackingUnits.remove(id);
				this.attackingUnitsToDestroy.remove(0);
			}else{
				this.defendingUnits.remove(id);
				this.defendingUnitsToDestroy.remove(0);
			}
			game.getGalaxy().removeUnit(id, game);
			GameTurnHandler.removeUnitDisplay(id, game);
		}
	}
	
	/**détruit une unité**/
	public void destroyUnit(long id, StarcraftGame game){
		if (this.cloackedUnits.contains(id)){
			this.cloackedUnits.remove(id);
			this.retreatingUnits.add(id);
		}else{
			removeUnitFromOldPlace(id);
			if (this.attackingUnits.contains(id)){
				this.attackingUnits.remove(id);
			}else{
				this.defendingUnits.remove(id);
			}
			game.getGalaxy().removeUnit(id, game);
			GameTurnHandler.removeUnitDisplay(id, game);
		}
	}
	
	/**résout la bataille**/
	public void resolveBattle(StarcraftGame game){
		for (int skirmishId:this.skirmishList.keySet()){
			this.skirmishList.get(skirmishId).resolveSkirmish(this, game);
		}
		applyUnitDestruction(game);
	}
	
	public void drawCombatCards(){
		this.attackingPlayer.drawCombatCards(3);
		if (this.defendingPlayer.getSpecies().equals("Protoss")){
			this.defendingPlayer.drawCombatCards(3);
		}else{
			this.defendingPlayer.drawCombatCards(1);
		}
	}
	
	public ArrayList<CombatCard> getAllCombatCards(){
		ArrayList<CombatCard> result = new ArrayList<CombatCard>();
		for (int skirmishId:this.skirmishList.keySet()){
			StarcraftSkirmish skirmish = this.skirmishList.get(skirmishId);
			if (skirmish.getFrontAttackCard() != null){
				result.add(skirmish.getFrontAttackCard());
			}
			if (skirmish.getFrontDefenseCard() != null){
				result.add(skirmish.getFrontDefenseCard());
			}
			if (skirmish.getSupportAttackCard() != null){
				result.add(skirmish.getSupportAttackCard());
			}
			if (skirmish.getSupportDefenseCard() != null){
				result.add(skirmish.getSupportDefenseCard());
			}
		}
		return result;
	}
	
	public CombatCard getRandomCard(String playerName) throws Exception{
		CombatCard result = null;
		CombatCard randomCard = null;
		Boolean cardUsed = false;
		if (playerName.equals(this.attackingPlayer.getName())){
			while (!cardUsed && this.attackingPlayer.getCombatCardDeck().size() > 0){
				randomCard = this.attackingPlayer.getCombatCardDeck().get(0);
				this.attackingPlayer.getCombatCardDeck().remove(0);
				this.attackingPlayer.getDismissedCombatCards().add(randomCard);
				if (randomCard.getMaxAttack() > -1){
					result = randomCard;
					cardUsed = true;
				}
			}
			if (result == null){
				if (this.attackingPlayer.getDismissedCombatCards().size() > 0){
					this.attackingPlayer.getCombatCardDeck().addAll(this.attackingPlayer.getDismissedCombatCards());
					Collections.shuffle(this.attackingPlayer.getCombatCardDeck());
					this.attackingPlayer.getDismissedCombatCards().removeAll(this.attackingPlayer.getDismissedCombatCards());
					result = getRandomCard(playerName);
				}else{
					throw new Exception("Aucune carte valide n'est disponible dans la pioche du joueur");
				}
			}
		}else{
			while (!cardUsed && this.defendingPlayer.getCombatCardDeck().size() > 0){
				randomCard = this.defendingPlayer.getCombatCardDeck().get(0);
				this.defendingPlayer.getCombatCardDeck().remove(0);
				this.defendingPlayer.getDismissedCombatCards().add(randomCard);
				if (randomCard.getMaxAttack() > -1){
					result = randomCard;
					cardUsed = true;
				}
			}
			if (result == null){
				if (this.defendingPlayer.getDismissedCombatCards().size() > 0){
					this.defendingPlayer.getCombatCardDeck().addAll(this.defendingPlayer.getDismissedCombatCards());
					Collections.shuffle(this.defendingPlayer.getCombatCardDeck());
					this.defendingPlayer.getDismissedCombatCards().removeAll(this.defendingPlayer.getDismissedCombatCards());
					result = getRandomCard(playerName);
				}else{
					throw new Exception("Aucune carte valide n'est disponible dans la pioche du joueur");
				}
			}
		}
		
		return result;
	}
	
	/**enlève les cartes de combats utilisées des mains du joueur**/
	public ArrayList<CombatCard> setEndCardTurn(String playerName){
		ArrayList<CombatCard> addedCardList = new ArrayList<CombatCard>();
		if (playerName.equals(this.attackingPlayer.getName())){
			for (int skirmishId:this.skirmishList.keySet()){
				if (this.skirmishList.get(skirmishId).getFrontAttackCard() != null){
					CombatCard card = this.skirmishList.get(skirmishId).getFrontAttackCard();
					this.attackingPlayer.getDismissedCombatCards().add(card);
					this.attackingPlayer.getCombatCardsInHand().remove(card.getId());
					if (this.skirmishList.get(skirmishId).getSupportAttackCard() != null){
						CombatCard supportCard = this.skirmishList.get(skirmishId).getSupportAttackCard();
						this.attackingPlayer.getDismissedCombatCards().add(supportCard);
						this.attackingPlayer.getCombatCardsInHand().remove(supportCard.getId());
					}
				}else{
					try {
						CombatCard randomCard = getRandomCard(playerName);
						this.skirmishList.get(skirmishId).setFrontAttackCard(randomCard);
						addedCardList.add(randomCard);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (this.attackingPlayer.getSpecies().equals("Zerg")){
				this.attackingPlayer.drawCombatCards(1);
			}
		}else{
			for (int skirmishId:this.skirmishList.keySet()){
				if (this.skirmishList.get(skirmishId).getFrontDefenseCard() != null){
					CombatCard card = this.skirmishList.get(skirmishId).getFrontDefenseCard();
					this.defendingPlayer.getDismissedCombatCards().add(card);
					this.defendingPlayer.getCombatCardsInHand().remove(card.getId());
					if (this.skirmishList.get(skirmishId).getSupportDefenseCard() != null){
						CombatCard supportCard = this.skirmishList.get(skirmishId).getSupportDefenseCard();
						this.defendingPlayer.getDismissedCombatCards().add(supportCard);
						this.defendingPlayer.getCombatCardsInHand().remove(supportCard.getId());
					}
				}else{
					try {
						CombatCard randomCard = getRandomCard(playerName);
						this.skirmishList.get(skirmishId).setFrontDefenseCard(randomCard);
						addedCardList.add(randomCard);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (this.defendingPlayer.getSpecies().equals("Zerg")){
				this.defendingPlayer.drawCombatCards(1);
			}
		}
		GameTurnHandler.callUpdateCardNumber(this.attackingPlayer);
		GameTurnHandler.callUpdateCardNumber(this.defendingPlayer);
		return addedCardList;
	}

	//TODO, vérifier si un des deux camps n'a pas d'unités de front
	/**fonction commençant la bataille**/
	public void startBattle(StarcraftGame game){
		//on met fin aux tours normaux
		game.removeTurnPart();
		
		Galaxy galaxy = game.getGalaxy();
		PlanetArea area = galaxy.returnPlanetAt(this.coordinates[0], this.coordinates[1]).getArea(this.coordinates[2]);
		for (long unitId:area.getUnitIdList()){
			if (!this.getAttackingUnits().contains(unitId)){
				StarcraftUnit defendingUnit = galaxy.getUnitList().get(unitId);
				if (defendingUnit.getType().equals("mobile")){
					addDefendingUnit(defendingUnit.getId());
					this.addUnplacedUnit(defendingUnit);
				}
				if (defendingPlayer == null){
					this.defendingPlayer = game.getPlayer(defendingUnit.getOwner());
				}
			}
		}
		//on n'engage la bataille que si le joueur en défense a des unités capable de défendre
		if (this.defendingUnits.size() > 0){
			game.addSpecialTurn(this.attackingPlayer.getName(), this.frontLineTurn);
			int attackingFrontline = 0;
			int defendingFrontline = 0;
			for (long attackingUnitId:this.attackingUnits){
				if (this.unplacedUnits.get(attackingUnitId).getAttackType() != null){
					attackingFrontline++;
				}
			}
			for (long defendingUnitId:this.defendingUnits){
				if (this.unplacedUnits.get(defendingUnitId).getAttackType() != null){
					defendingFrontline++;
				}
			}
			if (defendingFrontline > attackingFrontline){
				this.skirmishNumber = attackingFrontline;
			}else{
				this.skirmishNumber = defendingFrontline;
			}
			this.maxDefenseDepth = this.defendingUnits.size() - this.skirmishNumber + 1;
			this.maxAttackDepth = this.attackingUnits.size() - this.skirmishNumber + 1;
			for (int i = 0; i < this.skirmishNumber; i++){
				StarcraftSkirmish skirmish = new StarcraftSkirmish();
				skirmish.setId(i);
				this.skirmishList.put(i, skirmish);
			}
			drawCombatCards();
			for (long battleUnitId:this.unplacedUnits.keySet()){
				this.unplacedUnits.get(battleUnitId).setStartingSituation("inBattle");
			}
		}else{
			//TODO faire la retraite si l'attaquant a mis trop d'unités
			game.getGalaxy().setStarcraftBattle(null);
			game.nextTurn();
		}
	}
	
	/**détermine toutes les étapes possible après la fin de premier tour**/
	public void endFrontLineTurn(StarcraftGame game){
		if (this.maxAttackDepth > 1){
			game.addSpecialTurnEnd(this.attackingPlayer.getName(), this.supportLineTurn);
		}
		if (this.maxDefenseDepth > 1){
			game.addSpecialTurnEnd(this.defendingPlayer.getName(), this.supportLineTurn);
		}
		game.addSpecialTurnEnd(this.attackingPlayer.getName(), this.placeCardTurn);
		game.addSpecialTurnEnd(this.defendingPlayer.getName(), this.placeCardTurn);
		game.addSpecialTurnEnd(this.attackingPlayer.getName(), "revealBattleCards");
		game.addSpecialTurnEnd(this.attackingPlayer.getName(), this.destroyUnitTurn);
		game.addSpecialTurnEnd(this.defendingPlayer.getName(), this.destroyUnitTurn);
		game.addSpecialTurnEnd(this.defendingPlayer.getName(), GameConstants.moveRetreatUnitTurnName);
		game.addSpecialTurnEnd(this.attackingPlayer.getName(), GameConstants.moveRetreatUnitTurnName);
	}
	
	public Boolean checkEndBattleTurn(StarcraftGame game){
		Boolean result = false;
		if (game.getTurnPart().equals(this.frontLineTurn)){
			if (this.unplacedUnits.size() + 2 * this.skirmishNumber == this.attackingUnits.size()+ this.defendingUnits.size()){
				result = true;
			}
		}else if (game.getTurnPart().equals(this.supportLineTurn)){
			if (game.getPlayerCurrentlyPlaying().equals(this.attackingPlayer.getName())){
				if (this.unplacedUnits.size() + 2 * this.skirmishNumber + this.maxAttackDepth - 1
						== this.attackingUnits.size()+ this.defendingUnits.size()){
					result = true;
				}
			}else{
				if (this.unplacedUnits.size() ==0){
					result = true;
				}
			}
		}
		return result;
	}

	/**indique quelles unités sont sélectionnables en fonction de l'étape de résolution de bataille**/
	public ArrayList<Long> getActiveUnits(StarcraftGame game){
		ArrayList<Long> activeUnits = new ArrayList<Long>();
		if (game.getTurnPart().equals(this.frontLineTurn)){
			//on récupère toutes les unités pouvant être placées devant
			for (long attackingUnitId:this.attackingUnits){
				if (this.unplacedUnits.containsKey(attackingUnitId)){
					if (isValidFrontLine(this.unplacedUnits.get(attackingUnitId))){
						activeUnits.add(attackingUnitId);
					}
				}else{
					//l'unité a déjà été placée et est donc valide
					activeUnits.add(attackingUnitId);
				}
			}
			for (long defendingUnitId:this.defendingUnits){
				if (this.unplacedUnits.containsKey(defendingUnitId)){
					if (isValidFrontLine(this.unplacedUnits.get(defendingUnitId))){
						activeUnits.add(defendingUnitId);
					}
				}else{
					//l'unité a déjà été placée et est donc valide
					activeUnits.add(defendingUnitId);
				}
			}
		}else if (game.getTurnPart().equals(this.supportLineTurn)){
			//le joueur courant est celui qui attaque
			if (game.getPlayerCurrentlyPlaying().equals(this.attackingPlayer.getName())){
				for (long attackingUnitId:this.attackingUnits){
					if (this.unplacedUnits.containsKey(attackingUnitId)){
						activeUnits.add(attackingUnitId);
					}else{
						for (int skirmishId:this.skirmishList.keySet()){
							if (this.skirmishList.get(skirmishId).getAttackingSupports().containsKey(attackingUnitId)){
								activeUnits.add(attackingUnitId);
								break;
							}
						}
					}
				}
			}else{
				//le joueur courant est celui qui défend
				for (long defendingUnitId:this.defendingUnits){
					if (this.unplacedUnits.containsKey(defendingUnitId)){
						activeUnits.add(defendingUnitId);
					}else{
						for (int skirmishId:this.skirmishList.keySet()){
							if (this.skirmishList.get(skirmishId).getDefendingSupports().containsKey(defendingUnitId)){
								activeUnits.add(defendingUnitId);
								break;
							}
						}
					}
				}
			}
		}
		return activeUnits;
	}
	
	/**ajoute une unité à l'escarmouche choisie**/
	public void addUnitToSkirmish(int skimishId, String place, long unitId){
		StarcraftUnit unitToAdd = getBattleUnit(unitId);
		removeUnitFromOldPlace(unitId);
		if (place.equals("frontAttackPlace")){
			this.skirmishList.get(skimishId).setAttackingUnit(unitToAdd);
		}else if (place.equals("frontDefensePlace")){
			this.skirmishList.get(skimishId).setDefendingUnit(unitToAdd);
		}else if (place.equals("supportAttackPlace")){
			this.skirmishList.get(skimishId).addAttackingSupport(unitToAdd);
		}else if (place.equals("supportDefensePlace")){
			this.skirmishList.get(skimishId).addDefendingSupport(unitToAdd);
		}
	}
	
	/**ajoute une unité à l'escarmouche choisie**/
	public void addCardToSkirmish(int skimishId, String place, int cardId, String playerName){
		CombatCard cardToAdd = getBattleCard(cardId, playerName);
		removeCardFromOldPlace(cardId, playerName);
		if (place.equals("frontAttackCardPlace")){
			this.skirmishList.get(skimishId).setFrontAttackCard(cardToAdd);
		}else if (place.equals("supportAttackCardPlace")){
			this.skirmishList.get(skimishId).setSupportAttackCard(cardToAdd);
		}else if (place.equals("frontDefenseCardPlace")){
			this.skirmishList.get(skimishId).setFrontDefenseCard(cardToAdd);
		}else if (place.equals("supportDefenseCardPlace")){
			this.skirmishList.get(skimishId).setSupportDefenseCard(cardToAdd);
		}
	}
	
	/**transfert une carte du champs de batille au menu de sélection, indique en plus si une carte supportait celle enlevée**/
	public int addBattleCardToMenu(int cardId, String playerName){
		int result = -1;
		String[] cardPosition = this.getBattleCardPosition(cardId, playerName);
		if (cardPosition[1].equals("frontAttackCardPlace")){
			StarcraftSkirmish skirmish = this.skirmishList.get(Integer.parseInt(cardPosition[0].substring(8)));
			if (skirmish.getSupportAttackCard() != null){
				result = skirmish.getSupportAttackCard().getId();
			}
		}else if (cardPosition[1].equals("frontDefensePlace")){
			StarcraftSkirmish skirmish = this.skirmishList.get(Integer.parseInt(cardPosition[0].substring(8)));
			if (skirmish.getSupportDefenseCard() != null){
				result = skirmish.getSupportDefenseCard().getId();
			}
		}
		this.removeCardFromOldPlace(cardId, playerName);
		return result;
	}
	
	/**transfert une unité du champs de batille au menu de sélection**/
	public void addUnitToUnplacedUnits(long unitId){
		StarcraftUnit unitToAdd = getBattleUnit(unitId);
		removeUnitFromOldPlace(unitId);
		this.addUnplacedUnit(unitToAdd);
	}
	
	/**retire l'unité de la place où elle était**/
	public void removeUnitFromOldPlace(long unitId){
		if (this.unplacedUnits.containsKey(unitId)){
			this.unplacedUnits.remove(unitId);
		}else{
			String[] unitPosition = this.getBattlePosition(unitId);
			StarcraftSkirmish skirmish = this.skirmishList.get(Integer.parseInt(unitPosition[0].substring(8)));
			if (unitPosition[1].equals("supportAttackPlace")){
				skirmish.removeAttackingSupport(unitId);
			}else if (unitPosition[1].equals("frontAttackPlace")){
				skirmish.setAttackingUnit(null);
			}else if (unitPosition[1].equals("frontDefensePlace")){
				skirmish.setDefendingUnit(null);
			}else{
				skirmish.removeDefendingSupport(unitId);
			}
		}
	}
	
	/**retire la carte de la place où elle était**/
	public void removeCardFromOldPlace(int cardId, String playerName){
		String[] cardPosition = this.getBattleCardPosition(cardId, playerName);
		if (cardPosition != null){
			StarcraftSkirmish skirmish = this.skirmishList.get(Integer.parseInt(cardPosition[0].substring(8)));
			if (cardPosition[1].equals("supportAttackCardPlace")){
				skirmish.setSupportAttackCard(null);;
			}else if (cardPosition[1].equals("frontAttackCardPlace")){
				skirmish.setFrontAttackCard(null);
			}else if (cardPosition[1].equals("frontDefenseCardPlace")){
				skirmish.setFrontDefenseCard(null);
			}else{
				skirmish.setSupportDefenseCard(null);
			}
		}
	}
	
	/**description des places vides dans lesquelles la carte peut être déplacée**/
	public ArrayList<String[]> getValidCardPlacement(String playerName, int cardId, StarcraftGame game){
		ArrayList<String[]> result = new ArrayList<String[]>();
		StarcraftPlayer activePlayer = game.getPlayer(playerName);
		CombatCard card = activePlayer.getCombatCardsInHand().get(cardId);
		Boolean isAttackingPlayer = false;
		if (playerName.equals(this.attackingPlayer.getName())){
			isAttackingPlayer = true;
		}
		Boolean isSupport = true;
		if (card.getMaxAttack() > -1){
			isSupport = false;
		}
		for (int skimishId:this.skirmishList.keySet()){
			StarcraftSkirmish skirmish = this.skirmishList.get(skimishId);
			if (isAttackingPlayer){
				if (isSupport){
					if (skirmish.getFrontAttackCard() != null && skirmish.getSupportAttackCard() == null){
						if (card.fullfillRequirements(skirmish, isAttackingPlayer)){
							result.add(new String[]{"skirmish" + Integer.toString(skimishId), "supportAttackCardPlace"});
						}
					}
				}else{
					if  (skirmish.getFrontAttackCard() == null){
						result.add(new String[]{"skirmish" + Integer.toString(skimishId), "frontAttackCardPlace"});
					}
				}
			}else{
				if (isSupport){
					if (skirmish.getFrontDefenseCard() != null && skirmish.getSupportDefenseCard() == null){
						if (card.fullfillRequirements(skirmish, isAttackingPlayer)){
							result.add(new String[]{"skirmish" + Integer.toString(skimishId), "supportDefenseCardPlace"});
						}
					}
				}else{
					if  (skirmish.getFrontDefenseCard() == null){
						result.add(new String[]{"skirmish" + Integer.toString(skimishId), "frontDefenseCardPlace"});
					}
				}
			}
		}
		return result;
	}
	
	/**description des places vides dans lesquelles l'unité peut être déplacée**/
	public ArrayList<String[]> getValidPlacement(long unitId, StarcraftGame game){
		ArrayList<String[]> result = new ArrayList<String[]>();
		if (game.getTurnPart().equals(this.frontLineTurn)){
			if (this.attackingUnits.contains(unitId)){
				for (int skimishId:this.skirmishList.keySet()){
					if (this.skirmishList.get(skimishId).getAttackingUnit() == null){
						result.add(new String[]{"skirmish" + Integer.toString(skimishId), "frontAttackPlace"});
					}
				}
			}else if (this.defendingUnits.contains(unitId)){
				for (int skimishId:this.skirmishList.keySet()){
					if (this.skirmishList.get(skimishId).getDefendingUnit() == null){
						result.add(new String[]{"skirmish" + Integer.toString(skimishId), "frontDefensePlace"});
					}
				}
			}
		}else if (game.getTurnPart().equals(this.supportLineTurn)){
			if (this.attackingUnits.contains(unitId)){
				for (int skimishId:this.skirmishList.keySet()){
					if (!this.skirmishList.get(skimishId).getAttackingSupports().containsKey(unitId)){
						result.add(new String[]{"skirmish" + Integer.toString(skimishId), "supportAttackPlace"});
					}
				}
			}else if (this.defendingUnits.contains(unitId)){
				for (int skimishId:this.skirmishList.keySet()){
					if (!this.skirmishList.get(skimishId).getDefendingSupports().containsKey(unitId)){
						result.add(new String[]{"skirmish" + Integer.toString(skimishId), "supportDefensePlace"});
					}
				}
			}
		}
		return result;
	}
	
	/**renvoie l'endroit de la bataille où est située la carte, null si la carte n'est pas placée**/
	public String[] getBattleCardPosition(int cardId, String playerName){
		String[] result = null;
		Boolean isAttacker = false;
		if (playerName.equals(this.attackingPlayer.getName())){
			isAttacker = true;
		}
		if (isAttacker){
			for (int skimishId:this.skirmishList.keySet()){
				StarcraftSkirmish skirmish = this.skirmishList.get(skimishId);
				if (skirmish.getFrontAttackCard() != null){
					if (skirmish.getFrontAttackCard().getId() == cardId){
						result= new String[]{"skirmish" + Integer.toString(skimishId), "frontAttackCardPlace"};
						break;
					}
				}
				if (skirmish.getSupportAttackCard() != null){
					if (skirmish.getSupportAttackCard().getId() == cardId){
						result= new String[]{"skirmish" + Integer.toString(skimishId), "supportAttackCardPlace"};
						break;
					}
				}
			}
		}else{
			for (int skimishId:this.skirmishList.keySet()){
				StarcraftSkirmish skirmish = this.skirmishList.get(skimishId);
				if (skirmish.getFrontDefenseCard() != null){
					if (skirmish.getFrontDefenseCard().getId() == cardId){
						result= new String[]{"skirmish" + Integer.toString(skimishId), "frontDefenseCardPlace"};
						break;
					}
				}
				if (skirmish.getSupportDefenseCard() != null){
					if (skirmish.getSupportDefenseCard().getId() == cardId){
						result= new String[]{"skirmish" + Integer.toString(skimishId), "supportDefenseCardPlace"};
						break;
					}
				}
			}
		}
		return result;
	}
	
	/**renvoie l'endroit de la bataille où est située l'unité, null si l'unité n'est pas placée**/
	public String[] getBattlePosition(long unitId){
		String[] result = null;
		if (!this.unplacedUnits.containsKey(unitId)){
			Boolean attackingUnit = false;
			if (this.attackingUnits.contains(unitId)){
				attackingUnit = true;
			}
			for (int skimishId:this.skirmishList.keySet()){
				StarcraftSkirmish skirmish = this.skirmishList.get(skimishId);
				if (attackingUnit){
					if (skirmish.getAttackingUnit() != null){
						if (skirmish.getAttackingUnit().getId() == unitId){
							result= new String[]{"skirmish" + Integer.toString(skimishId), "frontAttackPlace"};
							break;
						}
					}
					if (skirmish.getAttackingSupports().containsKey(unitId)){
						result= new String[]{"skirmish" + Integer.toString(skimishId), "supportAttackPlace"};
						break;
					}
				}else{
					if (skirmish.getDefendingUnit() != null){
						if (skirmish.getDefendingUnit().getId() == unitId){
							result= new String[]{"skirmish" + Integer.toString(skimishId), "frontDefensePlace"};
							break;
						}
					}
					if (skirmish.getDefendingSupports().containsKey(unitId)){
						result= new String[]{"skirmish" + Integer.toString(skimishId), "supportDefensePlace"};
						break;
					}
				}
			}
		}
		return result;
	}
	
	/**renvoie l'unité ayant l'identifiant choisi**/
	public StarcraftUnit getBattleUnit(long unitId){
		StarcraftUnit result = null;
		if (!this.unplacedUnits.containsKey(unitId)){
			String[] unitPosition = this.getBattlePosition(unitId);
			StarcraftSkirmish skirmish = this.skirmishList.get(Integer.parseInt(unitPosition[0].substring(8)));
			if (unitPosition[1].equals("supportAttackPlace")){
				result= skirmish.getAttackingSupports().get(unitId);
			}else if (unitPosition[1].equals("frontAttackPlace")){
				result= skirmish.getAttackingUnit();
			}else if (unitPosition[1].equals("frontDefensePlace")){
				result= skirmish.getDefendingUnit();
			}else{
				result= skirmish.getDefendingSupports().get(unitId);
			}
		}else{
			result = this.unplacedUnits.get(unitId);
		}
		return result;
	}
	
	/**renvoie la carte du joueur ayant l'identifiant choisi**/
	public CombatCard getBattleCard(int cardId, String playerName){
		CombatCard result = null;
		if (playerName.equals(this.attackingPlayer.getName())){
			if (this.attackingPlayer.getCombatCardsInHand().containsKey(cardId)){
				result = this.attackingPlayer.getCombatCardsInHand().get(cardId);
			}
		}else{
			if (this.defendingPlayer.getCombatCardsInHand().containsKey(cardId)){
				result = this.defendingPlayer.getCombatCardsInHand().get(cardId);
			}
		}
		if (result == null){
			String[] cardPosition = this.getBattleCardPosition(cardId, playerName);
			if (cardPosition != null){
				StarcraftSkirmish skirmish = this.skirmishList.get(Integer.parseInt(cardPosition[0].substring(8)));
				if (cardPosition[1].equals("supportAttackCardPlace")){
					result = skirmish.getSupportAttackCard();
				}else if (cardPosition[1].equals("frontAttackCardPlace")){
					result = skirmish.getFrontAttackCard();
				}else if (cardPosition[1].equals("frontDefenseCardPlace")){
					result = skirmish.getFrontDefenseCard();
				}else{
					result = skirmish.getSupportDefenseCard();
				}
			}
		}
		return result;
	}
	
	/**donne le liste des unités avec lesquelles l'unité sélectionnée peut être échangée (fonctionnalité optionnelle)
	public ArrayList<Long> getValidSwaps(long unitId, StarcraftGame game){
		ArrayList<Long> result = new ArrayList<Long>();
		return result;
	}**/
	
	public int getSkirmishNumber() {
		return skirmishNumber;
	}

	public Map<Integer, StarcraftSkirmish> getSkirmishList() {
		return skirmishList;
	}

	public int getMaxDefenseDepth() {
		return this.maxDefenseDepth;
	}
	
	public int getMaxAttackDepth() {
		return this.maxAttackDepth;
	}

	public Map<Long, StarcraftUnit> getUnplacedUnits() {
		return unplacedUnits;
	}

	public void addUnplacedUnit(StarcraftUnit unit) {
		this.unplacedUnits.put(unit.getId(), unit);
		if (unit.getAbilities().contains("cloaking")){
			this.cloackedUnits.add(unit.getId());
		}
	}
	
	public void removeUnplacedUnit(Long unitId) {
		this.unplacedUnits.remove(unitId);
	}
	
	public Boolean isValidFrontLine(StarcraftUnit unit) {
		Boolean result = false;
		if (unit.getAttackType() != null){
			result = true;
		}
		return result;
	}

	public ArrayList<HashSet<Long>> getAttackingUnitsToDestroy() {
		return attackingUnitsToDestroy;
	}

	public ArrayList<HashSet<Long>> getDefendingUnitsToDestroy() {
		return defendingUnitsToDestroy;
	}

	/**on applique la destruction des unités**/
	public void applyUnitDestruction(StarcraftGame game){
		//destruction automatique des unités attaquantes
		while (this.attackingUnitsToDestroy.size() > 0 && getAttackerSetSize(this.attackingUnitsToDestroy.get(0)) < 2){
			for (long unitId:this.attackingUnitsToDestroy.get(0)){
				if (this.attackingUnits.contains(unitId)){
					this.destroyUnit(unitId, game);
				}
			}
			this.attackingUnitsToDestroy.remove(0);
		}

		//destruction automatique des unités défendantes
		while (this.defendingUnitsToDestroy.size() > 0 && getDefenderSetSize(this.defendingUnitsToDestroy.get(0)) < 2){
			for (long unitId:this.defendingUnitsToDestroy.get(0)){
				if (this.defendingUnits.contains(unitId)){
					this.destroyUnit(unitId, game);
				}
			}
			this.defendingUnitsToDestroy.remove(0);
		}
	}
	
	/**compte le nombre d'unités vivantes dans un set d'unités**/
	private int getAttackerSetSize(HashSet<Long> attackerSet){
		int result = 0;
		if (attackerSet.size() > 0){
			for (long unitId:attackerSet){
				if (this.attackingUnits.contains(unitId)){
					result++;
				}
			}
		}
		return result;
	}
	
	/**compte le nombre d'unités vivantes dans un set d'unités**/
	private int getDefenderSetSize(HashSet<Long> defenderSet){
		int result = 0;
		if (defenderSet.size() > 0){
			for (long unitId:defenderSet){
				if (this.defendingUnits.contains(unitId)){
					result++;
				}
			}
		}
		return result;
	}
}
