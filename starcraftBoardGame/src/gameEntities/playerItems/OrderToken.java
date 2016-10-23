package gameEntities.playerItems;

import org.json.JSONException;
import org.json.JSONObject;

import gameEntities.StarcraftGame;
import gameEntities.StarcraftPlayer;

public class OrderToken implements java.io.Serializable {
	private static final long serialVersionUID = 3880267599025102867L;
	private int id;
	private String name;
	private String owner;
	private Boolean special;
	
	public OrderToken(String name, String owner, Boolean special){
		this.name = name;
		this.owner = owner;
		this.special = special;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Boolean getSpecial() {
		return special;
	}
	public void setSpecial(Boolean special) {
		this.special = special;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public JSONObject returnOrderJson(String action, String coordinates ,StarcraftPlayer starcraftPlayer, StarcraftGame game){
		JSONObject result = null;
		if (this.getOwner().equals(starcraftPlayer.getName())){
			try {
				result = new JSONObject()
						.put("action", action)
						.put("coorddinates", coordinates)
						.put("name", this.getName())
						.put("color", starcraftPlayer.getPlayerColor())
						.put("special", this.getSpecial());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			String color = game.getPlayer(this.owner).getPlayerColor();
			try {
				result = new JSONObject()
						.put("action", action)
						.put("coorddinates", coordinates)
						.put("name", "hidden")
						.put("color", color);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
}
