package gameEntities.playerItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.CardIdGenerator;
import gameEntities.GameConstants;
import gameEntities.gameMap.StarcraftSkirmish;


public class CombatCard extends BuyableItem {

	private static final long serialVersionUID = -8581554652217829244L;
	private int id;
	
	private String name = "";
	private ArrayList<String> unitNames = new ArrayList<String>();
	private Map<String, CombatCardAbility> abilitiesList = new HashMap<String, CombatCardAbility>();
	private int maxAttack = -1;
	private int maxDefense = -1;
	private int minAttack = -1;
	private int minDefense = -1;
	private String text = "";
	private Set<String> requirements = new HashSet<String>();
	private String species;
	private String color;
	private String owner;
	/**scope peut être égal à front, all ou support**/
	private String scope = "front";
	
	public CombatCard(CardIdGenerator generator){
		this.id = generator.getNextValue();
	}
	
	/**détermine si une carte peut être jouée dans l'escarmouche choisie**/
	public Boolean fullfillRequirements(StarcraftSkirmish skirmish, Boolean isAttackingPlayer){
		Boolean result = false;
		Boolean meetRequirements = true;
		for (String requirement:this.requirements){
			if (requirement.equals("hasSupport")){
				if (isAttackingPlayer){
					//on regarde si le joueur attaquant a des unités de renfort
					if (skirmish.getAttackingSupports().size() < 1){
						meetRequirements = false;
						break;
					}
				}else{
					//on regarde si le joueur défenseur a des unités de renfort
					if (skirmish.getDefendingSupports().size() < 1){
						meetRequirements = false;
						break;
					}
				}
			}
		}
		if (meetRequirements){
			if (this.unitNames.contains("all")){
				result = true;
			}else{
				if (isAttackingPlayer){
					if (this.scope.equals("all") || this.scope.equals("front")){
						if (this.unitNames.contains(skirmish.getAttackingUnit().getName())){
							result = true;
						}
					}
					if ((this.scope.equals("all") || this.scope.equals("support")) && !result){
						for (long unitId:skirmish.getAttackingSupports().keySet()){
							if (this.unitNames.contains(skirmish.getAttackingSupports().get(unitId).getName())){
								result = true;
								break;
							}
						}
					}
				}else{
					if (this.scope.equals("all") || this.scope.equals("front")){
						if (this.unitNames.contains(skirmish.getDefendingUnit().getName())){
							result = true;
						}
					}
					if ((this.scope.equals("all") || this.scope.equals("support")) && !result){
						for (long unitId:skirmish.getDefendingSupports().keySet()){
							if (this.unitNames.contains(skirmish.getDefendingSupports().get(unitId).getName())){
								result = true;
								break;
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	
	public JSONObject getCardJS(String action){
		JSONObject result = null;
		
		JSONArray abilitiesListJS = new JSONArray();
		for (String ability:this.abilitiesList.keySet()){
			try {
				JSONObject abilityJS = new JSONObject()
						.put("name", ability);
				abilitiesListJS.put(abilityJS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		JSONArray imageListArray = new JSONArray();
		for (String unitName:this.unitNames){
			try {
				JSONObject abilityJS = new JSONObject()
						.put("image", GameConstants.getUnitImage(unitName));
						imageListArray.put(abilityJS);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		if (this.name.equals("")){
			if (this.maxAttack > -1){
				try {
					result = new JSONObject()
							.put("action", action)
							.put("species", this.species)
							.put("color", this.color)
							.put("id", this.id)
							.put("maxAttack", this.maxAttack)
							.put("maxDefense", this.maxDefense)
							.put("minAttack", this.minAttack)
							.put("minDefense", this.minDefense)
							.put("text", this.text)
							.put("abilities", abilitiesListJS)
							.put("images", imageListArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					result = new JSONObject()
							.put("action", action)
							.put("species", this.species)
							.put("color", this.color)
							.put("id", this.id)
							.put("text", this.text)
							.put("abilities", abilitiesListJS)
							.put("images", imageListArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getUnitNames() {
		return unitNames;
	}
	public void setUnitNames(ArrayList<String> unitNames) {
		this.unitNames = unitNames;
	}
	public Map<String, CombatCardAbility> getAbilitiesList() {
		return abilitiesList;
	}
	public void setAbilitiesList(Map<String, CombatCardAbility> abilitiesList) {
		this.abilitiesList = abilitiesList;
	}
	public int getMaxAttack() {
		return maxAttack;
	}
	public void setMaxAttack(int maxAttack) {
		this.maxAttack = maxAttack;
	}
	public int getMinAttack() {
		return minAttack;
	}
	public void setMinAttack(int minAttack) {
		this.minAttack = minAttack;
	}
	public int getMaxDefense() {
		return maxDefense;
	}
	public void setMaxDefense(int maxDefense) {
		this.maxDefense = maxDefense;
	}
	public int getMinDefense() {
		return minDefense;
	}
	public void setMinDefense(int minDefense) {
		this.minDefense = minDefense;
	}
	public Set<String> getRequirements() {
		return requirements;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void addAbility(CombatCardAbility ability){
		this.abilitiesList.put(ability.getName(), ability);
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getScope() {
		return scope;
	}


	public void setScope(String scope) {
		this.scope = scope;
	}




}
