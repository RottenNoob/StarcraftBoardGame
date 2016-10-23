package gameEntities.playerItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BuyingOrder implements java.io.Serializable {

	private static final long serialVersionUID = 8964381431299922946L;
	
	private BuyableItem item;
	private int spentMineral = 0;
	private int spentGas = 0;
	private Map<List<Integer>, Integer> affecedWorkers = new HashMap<List<Integer>, Integer>();
	private int workerOnBaseMineral = 0;
	private int workerOnBaseGas = 0;

	public Boolean requireGas(){
		Boolean result = false;
		if (this.item.getGasCost() > this.spentGas){
			result = true;
		}
		return result;
	}
	
	public Boolean requireMineral(){
		Boolean result = false;
		if  (this.item.getMineralCost() > this.spentMineral){
			result = true;
		}
		return result;
	}
	
	public Boolean isReady(){
		Boolean result = false;
		if (this.item.getGasCost() == this.spentGas && this.item.getMineralCost() == this.spentMineral){
			result = true;
		}
		return result;
	}
	
	public BuyableItem getItem() {
		return item;
	}

	public void setItem(BuyableItem item) {
		this.item = item;
	}

	public int getSpentMineral() {
		return spentMineral;
	}

	public void setSpentMineral(int spentMineral) {
		this.spentMineral = spentMineral;
	}

	public int getSpentGas() {
		return spentGas;
	}

	public void setSpentGas(int spentGas) {
		this.spentGas = spentGas;
	}

	public Map<List<Integer>, Integer> getAffecedWorkers() {
		return affecedWorkers;
	}

	public int getWorkerOnBaseMineral() {
		return workerOnBaseMineral;
	}

	public void setWorkerOnBaseMineral(int workerOnBaseMineral) {
		this.workerOnBaseMineral = workerOnBaseMineral;
	}

	public int getWorkerOnBaseGas() {
		return workerOnBaseGas;
	}

	public void setWorkerOnBaseGas(int workerOnBaseGas) {
		this.workerOnBaseGas = workerOnBaseGas;
	}

}
