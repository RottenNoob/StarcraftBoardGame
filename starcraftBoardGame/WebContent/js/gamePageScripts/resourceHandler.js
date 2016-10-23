//TODO
/*réception des messages du serveur*/

function displayWorkersOnBaseResources(serverAction){
	var baseMineral = document.getElementById("baseMineral");
	
	var workerOnBaseMineral = document.getElementById("workerOnBaseMineral");
	if (!(serverAction.image in unitImages)){
		var image = new Image();
	    image.src = "../starcraftWebResources/unitImages/"+ serverAction.species + "/"+ serverAction.image;
	    unitImages[serverAction.image]=image;
	}
	
	if (serverAction.workerOnMineral > 0){
		if (!workerOnBaseMineral){
			var workerOnBaseMineral = document.createElement("div");
			workerOnBaseMineral.setAttribute("id", "workerOnBaseMineral");
			
			var workerOnBaseMineralText = document.createElement("div");
			workerOnBaseMineralText.setAttribute("id", "workerOnBaseMineralText");
			workerOnBaseMineralText.innerHTML = serverAction.workerOnMineral;
			workerOnBaseMineral.appendChild(workerOnBaseMineralText);
			
			var workerOnBaseMineralImage = document.createElement("div");
			cloneImage(workerOnBaseMineralImage, unitImages[serverAction.image]);
			workerOnBaseMineral.appendChild(workerOnBaseMineralImage);
			
			baseMineral.appendChild(workerOnBaseMineral);
		}else{
			var workerOnBaseMineralText = document.getElementById("workerOnBaseMineralText");
			workerOnBaseMineralText.innerHTML = serverAction.workerOnMineral;
		}
	}else{
		if (workerOnBaseMineral){
			workerOnBaseMineral.parentElement.removeChild(workerOnBaseMineral);
		}
	}
	
	var baseGas = document.getElementById("baseGas");
	var workerOnBaseGas = document.getElementById("workerOnBaseGas");
	if (serverAction.workerOnGas > 0){
		if (!workerOnBaseGas){
			var workerOnBaseGas = document.createElement("div");
			workerOnBaseGas.setAttribute("id", "workerOnBaseGas");
			
			var workerOnBaseGasText = document.createElement("div");
			workerOnBaseGasText.setAttribute("id", "workerOnBaseGasText");
			workerOnBaseGasText.innerHTML = serverAction.workerOnGas;
			workerOnBaseGas.appendChild(workerOnBaseGasText);
			
			var workerOnBaseGasImage = document.createElement("div");
			cloneImage(workerOnBaseGasImage, unitImages[serverAction.image]);
			workerOnBaseGas.appendChild(workerOnBaseGasImage);
			
			baseGas.appendChild(workerOnBaseGas);
		}else{
			var workerOnBaseGasText = document.getElementById("workerOnBaseGasText");
			workerOnBaseGasText.innerHTML = serverAction.workerOnGas;
		}
	}else{
		if (workerOnBaseGas){
			workerOnBaseGas.parentElement.removeChild(workerOnBaseGas);
		}
	}
}

/**montre le nombre de travailleurs occupant une zone**/
function displayWorkersOnArea(serverAction){
	var workerImageName = "worker." +serverAction.coordinate + "." + serverAction.areaId.toString();
	var workerImage = galaxyStage.getChildByName("unitContainer").getChildByName(workerImageName);
	if (serverAction.number > 0){
		if (!workerImage){
			workerImage = drawWorkerWithText(serverAction);
			workerImage.set({name : workerImageName});
			var square = galaxyStage.getChildByName(serverAction.coordinate);
			var areaCount = square.getChildAt(1).getChildByName("areaSet").numChildren;
			var angle1 = (Math.PI*2/areaCount) * (serverAction.areaId-1);
			var angle2 = (Math.PI*2/areaCount) * serverAction.areaId;
			
			workerImage.x = square.x + 200 + 180 *  Math.cos((2 * angle1 + angle2)/3);
			workerImage.y = square.y + 200 + 180 *  Math.sin((2 * angle1 + angle2)/3);
			galaxyStage.getChildByName("unitContainer").addChild(workerImage);
		}else{
			workerImage.getChildByName("textWorker").text = serverAction.number;
			workerImage.getChildByName("textWorker2").text = serverAction.number;
		}
	}else{
		if (workerImage){
			galaxyStage.getChildByName("unitContainer").removeChild(workerImage);
		}
	}
	galaxyStage.update();
}


function updateBaseWorkerDisplay(serverAction){
	var worker1 = document.getElementById("availableWorkers");
	worker1.innerHTML = "Available workers : " + serverAction.availableWorkers;
	var worker2 = document.getElementById("unavailableWorkers");
	worker2.innerHTML = "Unavailable workers : " + serverAction.unavailableWorkers;
}

/**montre le nombre de travailleurs disponibles**/
function displayBaseWorkers(serverAction){
	var factionInfo = document.getElementById("currentPlayerBoard");
	
	var resourceMenu = document.createElement("resourceMenu");
	resourceMenu.setAttribute("class", "playerInfoBoard");
	
	var resourceMenu1 = document.createElement("resourceMenu1");
	resourceMenu1.innerHTML = "Workers";
	
	var resourceMenu2 = document.createElement("resourceMenu2");
	
	var worker1 = document.createElement("div");
	worker1.innerHTML = "Available workers : " + serverAction.availableWorkers;
	worker1.setAttribute("id", "availableWorkers");
	if (!(serverAction.image in unitImages)){
		var image = new Image();
	    image.src = "../starcraftWebResources/unitImages/"+ serverAction.species + "/"+ serverAction.image;
	    unitImages[serverAction.image]=image;
	}
	resourceMenu2.appendChild(worker1);
	cloneImage(resourceMenu2, unitImages[serverAction.image]);
	
	var resourceMenu3 = document.createElement("resourceMenu3");
	
	var worker2 = document.createElement("div");
	worker2.setAttribute("id", "unavailableWorkers");
	worker2.innerHTML = "Unavailable workers : " + serverAction.unavailableWorkers;
	resourceMenu3.appendChild(worker2);
	cloneImage(resourceMenu3, unitImages[serverAction.image]);
	
	resourceMenu.appendChild(resourceMenu1);
	resourceMenu.appendChild(resourceMenu2);
	resourceMenu.appendChild(resourceMenu3);
	factionInfo.appendChild(resourceMenu);
}

/** remplit la partie "faction" du champs d'information sur le joueur actuel**/
function displayPlayerResources(serverAction){
	var factionInfo = document.getElementById("currentPlayerBoard");
	
	var resourceMenu = document.createElement("resourceMenu");
	resourceMenu.setAttribute("class", "playerInfoBoard");
	
	var resourceMenu1 = document.createElement("resourceMenu1");
	resourceMenu1.innerHTML = "Base resources";
	
	var resourceMenu2 = document.createElement("resourceMenu2");
	resourceMenu2.setAttribute("id", "baseMineral");
	
	var mineral = document.createElement("div");
	mineral.setAttribute("class", "imageMenu");
	var mineralImage = document.createElement("img");
	mineralImage.setAttribute("src", "../starcraftWebResources/mapResources/mineral.png");
	mineralImage.setAttribute("width", 40);
	mineralImage.setAttribute("height", 40);
	mineral.appendChild(mineralImage);
	
	var mineralText = document.createElement("div");
	mineralText.innerHTML = serverAction.mineral;
	mineralText.setAttribute("class", "textMenu");
	
	mineral.appendChild(mineralImage);
	mineral.appendChild(mineralText);
	resourceMenu2.appendChild(mineral);
	
	var resourceMenu3 = document.createElement("resourceMenu3");
	resourceMenu3.setAttribute("id", "baseGas")
	
	var gas = document.createElement("div");
	gas.setAttribute("class", "imageMenu");
	var gasImage = document.createElement("img");
	gasImage.setAttribute("src", "../starcraftWebResources/mapResources/gas.png");
	gasImage.setAttribute("width", 40);
	gasImage.setAttribute("height", 40);
	gas.appendChild(gasImage);
	
	var gasText = document.createElement("div");
	gasText.innerHTML = serverAction.gas;
	gasText.setAttribute("class", "textMenu");
	
	gas.appendChild(gasImage);
	gas.appendChild(gasText);
	resourceMenu3.appendChild(gas);
	
	resourceMenu.appendChild(resourceMenu1);
	resourceMenu.appendChild(resourceMenu2);
	resourceMenu.appendChild(resourceMenu3);
	factionInfo.appendChild(resourceMenu);
}

//remplit la partie "faction" du champs d'information sur le joueur actuel
function displayPlayerResourceToken(serverAction){

}

/*fin réception des messages du serveur*/

//TODO
/*Fonctions de dessins*/
/**dessine l'image d'un travailleur sur une zone**/
function drawWorkerWithText(serverAction){
	var result = new createjs.Container();
	var unit = drawUnitImage(serverAction);
	var textWorker = new createjs.Text(serverAction.number, "30px Arial", textOutlineColor);
	textWorker.set({name : "textWorker"});
	var textWorker2 = new createjs.Text(serverAction.number, "30px Arial", textColor);
	textWorker2.set({name : "textWorker2"});
	textWorker.outline = 1.3;
	result.addChild(unit);
	result.addChild(textWorker);
	result.addChild(textWorker2);
	textWorker.x = - 20;
	textWorker.y = - 20;
	textWorker2.x = - 20;
	textWorker2.y = - 20;
	unit.y = - 20;
	return result;
}

function disactivateBaseResources(){
	var baseMineral = document.getElementById("baseMineral");
	baseMineral.removeEventListener("click", sendSetWorkerOnBaseMineral);
	baseMineral.removeAttribute("onmouseover");
	baseMineral.removeAttribute("onmouseout");
	var baseGas = document.getElementById("baseGas");
	baseGas.removeEventListener("click", sendSetWorkerOnBaseGas);
	baseGas.removeAttribute("onmouseover");
	baseGas.removeAttribute("onmouseout");
}

/*fonctions utilitaires*/
function sendSetWorkerOnBaseMineral(){
	var baseMineral = document.getElementById("baseMineral");
	disactivateBaseResources();
	setBackGround(baseMineral, "white");
	setWorkerOnBaseMineral();
}

function sendSetWorkerOnBaseGas(){
	var baseGas = document.getElementById("baseGas");
	disactivateBaseResources();
	setBackGround(baseGas, "white");
	setWorkerOnBaseGas();
}