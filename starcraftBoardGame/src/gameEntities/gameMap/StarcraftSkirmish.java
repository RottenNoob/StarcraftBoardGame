package gameEntities.gameMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gameEntities.StarcraftGame;
import gameEntities.playerItems.CombatCard;
import gameEntities.playerItems.CombatCardAbility;
import gameEntities.playerItems.StarcraftUnit;

public class StarcraftSkirmish implements java.io.Serializable {

	private static final long serialVersionUID = 1659853579887155779L;
	private int id;
	private StarcraftUnit defendingUnit;
	private StarcraftUnit attackingUnit;
	private Map<Long, StarcraftUnit> attackingSupports = new HashMap<Long, StarcraftUnit>();
	private Map<Long, StarcraftUnit> defendingSupports = new HashMap<Long, StarcraftUnit>();
	private CombatCard frontAttackCard;
	private CombatCard frontDefenseCard;
	private CombatCard supportAttackCard;
	private CombatCard supportDefenseCard;
	private int attackerPower = 0;
	private int attackerHealth = 0;
	private int defenderPower = 0;
	private int defenderHealth = 0;
	private ArrayList<CombatCardAbility> attackerAbilities = new ArrayList<CombatCardAbility>();
	private ArrayList<CombatCardAbility> defenderAbilities = new ArrayList<CombatCardAbility>();
	private final Set<String> enemyBattleScope = new HashSet<String>(Arrays.asList("splash"));
	private final Set<String> enemyFrontScope = new HashSet<String>(Arrays.asList("attackBonus"));
	private final Set<String> enemyAllScope = new HashSet<String>(Arrays.asList());
	
	
	
	public void resolveSkirmish(StarcraftBattle battle, StarcraftGame game){
		//ajout des capacirés de l'attaquant
		addAttackerAbility(battle);
		
		if (this.attackingUnit.getAttackType()!= null){
			if (this.frontAttackCard.getUnitNames().contains(this.attackingUnit.getName())){
				this.attackerPower = this.frontAttackCard.getMaxAttack();
				this.attackerHealth = this.frontAttackCard.getMaxDefense();
			}else{
				this.attackerPower = this.frontAttackCard.getMinAttack();
				this.attackerHealth = this.frontAttackCard.getMinDefense();
			}
			for (long attackSupportId:this.attackingSupports.keySet()){
				if (this.attackingSupports.get(attackSupportId).getAttackSupport() > 0){
					this.attackerPower +=  this.attackingSupports.get(attackSupportId).getAttackSupport();
				}
			}
			//on active les augmentations d'attaques de l'attaquant
			for (CombatCardAbility battleAbility:this.attackerAbilities){
				if (battleAbility.getName().equals("attackBonus")){
					this.attackerPower += battleAbility.getAmount();
				}else if (battleAbility.getName().equals("healthBonus")){
					this.attackerHealth += battleAbility.getAmount();
				}
			}
		}else{
			this.attackerHealth = this.frontAttackCard.getMinDefense();
			for (CombatCardAbility battleAbility:this.attackerAbilities){
				if (battleAbility.getName().equals("healthBonus")){
					this.attackerHealth += battleAbility.getAmount();
				}
			}
		}
		
		//ajout des capacirés de l'attaquant
		addDefenderAbility(battle);
		
		if (this.defendingUnit.getAttackType()!= null){
			if (this.frontDefenseCard.getUnitNames().contains(this.defendingUnit.getName())){
				this.defenderPower = this.frontDefenseCard.getMaxAttack();
				this.defenderHealth = this.frontDefenseCard.getMaxDefense();
			}else{
				this.defenderPower = this.frontDefenseCard.getMinAttack();
				this.defenderHealth = this.frontDefenseCard.getMinDefense();
			}
			for (long defenderSupportId:this.defendingSupports.keySet()){
				if (this.defendingSupports.get(defenderSupportId).getAttackSupport() > 0){
					this.defenderPower +=  this.defendingSupports.get(defenderSupportId).getAttackSupport();
				}
			}
			//on active les augmentations d'attaques de l'attaquant
			for (CombatCardAbility battleAbility:this.defenderAbilities){
				if (battleAbility.getName().equals("attackBonus")){
					this.defenderPower += battleAbility.getAmount();
				}else if (battleAbility.getName().equals("healthBonus")){
					this.defenderHealth += battleAbility.getAmount();
				}
			}
		}else{
			this.defenderHealth = this.frontDefenseCard.getMinDefense();
			for (CombatCardAbility battleAbility:this.defenderAbilities){
				if (battleAbility.getName().equals("healthBonus")){
					this.defenderHealth += battleAbility.getAmount();
				}
			}
		}
		//on ajoute les unités en défense que l'on doit détruire
		HashSet<Long> defenderSet = new HashSet<Long>();
		Boolean destroyDefender = false;
		if (this.attackerPower >= this.defenderHealth){
			if (this.attackingUnit.getAttackType().equals("all")
					|| this.attackingUnit.getAttackType().equals(this.defendingUnit.getMoveType())){
				destroyDefender = true;
				defenderSet.add(this.defendingUnit.getId());
			}else if (this.defendingSupports.size() > 0){
				//si l'attaquant ne peut détruire l'unité devant, on vérifie si il peut attaquer une unité de support
				for (long defenderID:this.defendingSupports.keySet()){
					if (this.attackingUnit.getAttackType().equals(this.defendingSupports.get(defenderID).getMoveType())){
						destroyDefender = true;
						defenderSet.add(defenderID);
					}
				}
			}
			if (defenderSet.size() > 0){
				battle.addDefendersToDestroy(defenderSet);
			}
		}
		//on ajoute les unités en attaque que l'on doit détruire
		HashSet<Long> attackerSet = new HashSet<Long>();
		Boolean destroyAttacker = false;
		if (this.defenderPower >= this.attackerHealth){
			if (this.defendingUnit.getAttackType().equals("all")
					|| this.defendingUnit.getAttackType().equals(this.attackingUnit.getMoveType())){
				destroyAttacker = true;
				attackerSet.add(this.attackingUnit.getId());
			}else if (this.attackingSupports.size() > 0){
				//si le défenseur ne peut détruire l'unité devant, on vérifie si il peut attaquer une unité de support
				for (long attackerId:this.attackingSupports.keySet()){
					if (this.defendingUnit.getAttackType().equals(this.attackingSupports.get(attackerId).getMoveType())){
						destroyAttacker = true;
						attackerSet.add(attackerId);
					}
				}
			}
			if (attackerSet.size() > 0){
				battle.addAttackersToDestroy(attackerSet);
			}
		}
		//si une unité est détruite de manière directe, on active les capacités dépendantes
		if (destroyDefender){
			for (CombatCardAbility ability:this.attackerAbilities){
				if (ability.getName().equals("splash")){
					this.applyAttackerAbility(ability, battle);
				}
			}
		}
		if (destroyAttacker){
			for (CombatCardAbility ability:this.defenderAbilities){
				if (ability.getName().equals("splash")){
					this.applyDefenderAbility(ability, battle);
				}
			}
		}
		/*System.out.println("skirmish" + Integer.toString(this.id));
		System.out.println("attacker : power = " + Integer.toString(this.attackerPower)
		+ " ; " + "health = " + Integer.toString(this.attackerHealth));
		System.out.println("defender : power = " + Integer.toString(this.defenderPower)
		+ " ; " + "health = " + Integer.toString(this.defenderHealth));*/
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public StarcraftUnit getDefendingUnit() {
		return defendingUnit;
	}
	
	public void setDefendingUnit(StarcraftUnit defendingUnit) {
		this.defendingUnit = defendingUnit;
	}
	
	public StarcraftUnit getAttackingUnit() {
		return attackingUnit;
	}
	
	public void setAttackingUnit(StarcraftUnit attackingUnit) {
		this.attackingUnit = attackingUnit;
	}
	
	public Map<Long, StarcraftUnit> getAttackingSupports() {
		return attackingSupports;
	}
	
	public void addAttackingSupport(StarcraftUnit unit) {
		this.attackingSupports.put(unit.getId(), unit);
	}
	
	public void removeAttackingSupport(Long unitId) {
		this.attackingSupports.remove(unitId);
	}
	
	public Map<Long, StarcraftUnit> getDefendingSupports() {
		return defendingSupports;
	}
	
	public void addDefendingSupport(StarcraftUnit unit) {
		this.defendingSupports.put(unit.getId(), unit);
	}
	
	public void removeDefendingSupport(Long unitId) {
		this.defendingSupports.remove(unitId);
	}

	public CombatCard getFrontAttackCard() {
		return frontAttackCard;
	}

	public void setFrontAttackCard(CombatCard frontAttackCard) {
		this.frontAttackCard = frontAttackCard;
	}

	public CombatCard getFrontDefenseCard() {
		return frontDefenseCard;
	}

	public void setFrontDefenseCard(CombatCard frontDefenseCard) {
		this.frontDefenseCard = frontDefenseCard;
	}

	public CombatCard getSupportDefenseCard() {
		return supportDefenseCard;
	}

	public void setSupportDefenseCard(CombatCard supportDefenseCard) {
		this.supportDefenseCard = supportDefenseCard;
	}

	public CombatCard getSupportAttackCard() {
		return supportAttackCard;
	}

	public void setSupportAttackCard(CombatCard supportAttackCard) {
		this.supportAttackCard = supportAttackCard;
	}

	public int getAttackerPower() {
		return attackerPower;
	}

	public int getAttackerHealth() {
		return attackerHealth;
	}

	public int getDefenderHealth() {
		return defenderHealth;
	}

	public int getDefenderPower() {
		return defenderPower;
	}
	
	/*fonctions utilitaires*/
	/**vérifie si une capacité de l'attaquant peut s'appliquer**/
	private Boolean abilityAgainstDefenderScope(CombatCardAbility ability, StarcraftBattle battle){
		Boolean result = false;
		if (this.enemyBattleScope.contains(ability.getName())){
			if (ability.getHostileUnitNames().size() == 0){
				result = true;
			}else{
				for (long unitId:battle.getDefendingUnits()){
					StarcraftUnit unit = battle.getBattleUnit(unitId);
					if (ability.getHostileUnitNames().contains("ground") && unit.getMoveType().equals("ground")){
						result = true;
						break;
					}else if (ability.getHostileUnitNames().contains("flying") && unit.getMoveType().equals("flying")){
						result = true;
						break;
					}else if (ability.getHostileUnitNames().contains(unit.getName())){
						result = true;
						break;
					}
				}
			}
		}else if (this.enemyFrontScope.contains(ability.getName())){
			if (ability.getHostileUnitNames().size() == 0){
				result = true;
			}else{
				StarcraftUnit unit = this.defendingUnit;
				if (ability.getHostileUnitNames().contains("ground") && unit.getMoveType().equals("ground")){
					result = true;
				}else if (ability.getHostileUnitNames().contains("flying") && unit.getMoveType().equals("flying")){
					result = true;
				}else if (ability.getHostileUnitNames().contains(unit.getName())){
					result = true;
				}
			}
		}else if (this.enemyAllScope.contains(ability.getName())){
			//TODO regarder si des cartes sont concernées
		}else{
			result = true;
		}
		return result;
	}
	
	/**vérifie si une capacité du défenseur peut s'appliquer**/
	private Boolean abilityAgainstAttackerScope(CombatCardAbility ability, StarcraftBattle battle){
		Boolean result = false;
		if (this.enemyBattleScope.contains(ability.getName())){
			if (ability.getHostileUnitNames().size() == 0){
				result = true;
			}else{
				for (long unitId:battle.getAttackingUnits()){
					StarcraftUnit unit = battle.getBattleUnit(unitId);
					if (ability.getHostileUnitNames().contains("ground") && unit.getMoveType().equals("ground")){
						result = true;
						break;
					}else if (ability.getHostileUnitNames().contains("flying") && unit.getMoveType().equals("flying")){
						result = true;
						break;
					}else if (ability.getHostileUnitNames().contains(unit.getName())){
						result = true;
						break;
					}
				}
			}
		}else if (this.enemyFrontScope.contains(ability.getName())){
			if (ability.getHostileUnitNames().size() == 0){
				result = true;
			}else{
				StarcraftUnit unit = this.attackingUnit;
				if (ability.getHostileUnitNames().contains("ground") && unit.getMoveType().equals("ground")){
					result = true;
				}else if (ability.getHostileUnitNames().contains("flying") && unit.getMoveType().equals("flying")){
					result = true;
				}else if (ability.getHostileUnitNames().contains(unit.getName())){
					result = true;
				}
			}
		}else if (this.enemyAllScope.contains(ability.getName())){
			//TODO regarder si des cartes sont concernées
		}else{
			result = true;
		}
		return result;
	}
	
	
	private void addAttackerAbility(StarcraftBattle battle){
		CombatCard card = this.frontAttackCard;
		for (String abilityName : card.getAbilitiesList().keySet()){
			CombatCardAbility ability = card.getAbilitiesList().get(abilityName);
			if (ability.getAlliedUnitNames().size() == 0){
				if (card.getUnitNames().contains(this.attackingUnit.getName())
						|| card.getUnitNames().contains("all")){
					if (abilityAgainstDefenderScope(ability, battle)){
						this.attackerAbilities.add(ability);
					}
				}
			}else if (ability.getAlliedUnitNames().contains(this.attackingUnit.getName())){
				if (abilityAgainstDefenderScope(ability, battle)){
					this.attackerAbilities.add(ability);
				}
			}
		}
		if (this.supportAttackCard != null){
			CombatCard supportCard = this.supportAttackCard;
			if (supportCard.getScope().equals("front")){
				for (String abilityName : supportCard.getAbilitiesList().keySet()){
					CombatCardAbility ability = supportCard.getAbilitiesList().get(abilityName);
					if (ability.getAlliedUnitNames().size() == 0){
						if (supportCard.getUnitNames().contains(this.attackingUnit.getName())
								|| supportCard.getUnitNames().contains("all")){
							if (abilityAgainstDefenderScope(ability, battle)){
								this.attackerAbilities.add(ability);
							}
						}
					}else if (ability.getAlliedUnitNames().contains(this.attackingUnit.getName())){
						if (abilityAgainstDefenderScope(ability, battle)){
							this.attackerAbilities.add(ability);
						}
					}
				}
			}//TODO à faire pour les cartes aux scopes différents
		}
	}
	
	private void addDefenderAbility(StarcraftBattle battle){
		CombatCard card = this.frontDefenseCard;
		for (String abilityName : card.getAbilitiesList().keySet()){
			CombatCardAbility ability = card.getAbilitiesList().get(abilityName);
			if (ability.getAlliedUnitNames().size() == 0){
				if (card.getUnitNames().contains(this.defendingUnit.getName())
						|| card.getUnitNames().contains("all")){
					if (abilityAgainstAttackerScope(ability, battle)){
						this.defenderAbilities.add(ability);
					}
				}
			}else if (ability.getAlliedUnitNames().contains(this.defendingUnit.getName())){
				if (abilityAgainstAttackerScope(ability, battle)){
					this.defenderAbilities.add(ability);
				}
			}
		}
		if (this.supportDefenseCard != null){
			CombatCard supportCard = this.supportDefenseCard;
			if (supportCard.getScope().equals("front")){
				for (String abilityName : supportCard.getAbilitiesList().keySet()){
					CombatCardAbility ability = supportCard.getAbilitiesList().get(abilityName);
					if (ability.getAlliedUnitNames().size() == 0){
						if (supportCard.getUnitNames().contains(this.defendingUnit.getName())
								|| supportCard.getUnitNames().contains("all")){
							if (abilityAgainstAttackerScope(ability, battle)){
								this.defenderAbilities.add(ability);
							}
						}
					}else if (ability.getAlliedUnitNames().contains(this.defendingUnit.getName())){
						if (abilityAgainstAttackerScope(ability, battle)){
							this.defenderAbilities.add(ability);
						}
					}
				}
			}//TODO à faire pour les cartes aux scopes différents
		}
	}
	
	public void applyAttackerAbility(CombatCardAbility ability, StarcraftBattle battle){
		if (ability.getName().equals("splash")){
			HashSet<Long> unitsToDestroy = new HashSet<Long>();
			if (ability.getHostileUnitNames().size() == 0){
				for (long unitId:battle.getDefendingUnits()){
					unitsToDestroy.add(unitId);
				}
			}else{
				for (long unitId:battle.getDefendingUnits()){
					StarcraftUnit unit = battle.getBattleUnit(unitId);
					if (ability.getHostileUnitNames().contains("ground") && unit.getMoveType().equals("ground")){
						unitsToDestroy.add(unitId);
					}else if (ability.getHostileUnitNames().contains("flying") && unit.getMoveType().equals("flying")){
						unitsToDestroy.add(unitId);
					}else if (ability.getHostileUnitNames().contains(unit.getName())){
						unitsToDestroy.add(unitId);
					}
				}
			}
			if (unitsToDestroy.size() > 0){
				battle.addDefendersToDestroy(unitsToDestroy);
			}
		}
	}
	//TODO appliquer une capacité de défenseur
	
	public void applyDefenderAbility(CombatCardAbility ability, StarcraftBattle battle){
		if (ability.getName().equals("splash")){
			HashSet<Long> unitsToDestroy = new HashSet<Long>();
			if (ability.getHostileUnitNames().size() == 0){
				for (long unitId:battle.getAttackingUnits()){
					unitsToDestroy.add(unitId);
				}
			}else{
				for (long unitId:battle.getAttackingUnits()){
					StarcraftUnit unit = battle.getBattleUnit(unitId);
					if (ability.getHostileUnitNames().contains("ground") && unit.getMoveType().equals("ground")){
						unitsToDestroy.add(unitId);
					}else if (ability.getHostileUnitNames().contains("flying") && unit.getMoveType().equals("flying")){
						unitsToDestroy.add(unitId);
					}else if (ability.getHostileUnitNames().contains(unit.getName())){
						unitsToDestroy.add(unitId);
					}
				}
			}
			if (unitsToDestroy.size() > 0){
				battle.addAttackersToDestroy(unitsToDestroy);
			}
		}
	}
}
