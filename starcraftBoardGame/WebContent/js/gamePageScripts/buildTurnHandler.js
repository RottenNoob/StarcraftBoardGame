
/*actions joueurs*/
//---------------------------------------------------------------------------------------

function cancelBuyOrder(){
	placedUnitChoice = 0;
	var clientAction = {
			action : "cancelBuyOrder",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function endBuildingUnitTurn(){
	var clientAction = {
			action : "endBuildingUnitTurn",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function setWorkerOnArea(area){
	validAreas.length = 0;
	var clientAction = {
			action : "setWorkerOnArea",
			areaId : area.name.toString(),
			coordinates : area.parent.parent.parent.name
	};
	webSocket.send(JSON.stringify(clientAction));
}

function setWorkerOnBaseMineral(){
	validAreas.length = 0;
	disactivateBaseResources();
	var clientAction = {
			action : "setWorkerBaseMineral"
	};
	webSocket.send(JSON.stringify(clientAction));
}

function setWorkerOnBaseGas(){
	validAreas.length = 0;
	var clientAction = {
			action : "setWorkerBaseGas"
	};
	webSocket.send(JSON.stringify(clientAction));
}


function askBuyingOrder(unit){
	clearEventAndHighlight();
	var clientAction = {
			action : "askBuyingOrder",
			name : unit.name
	};
	webSocket.send(JSON.stringify(clientAction));
}

function askBuyingBuildingOrder(building){
	clearEventAndHighlight();
	var clientAction = {
			action : "askBuyingBuildingOrder",
			name : building.name
	};
	webSocket.send(JSON.stringify(clientAction));
}



//TODO
/*ajout d'évènements*/
//---------------------------------------------------------------------------------------

/**sélectionne une zone où l'on va chercher des ressources**/
function addGetResource(area){
	addObjectWithEvents(area);
	if (currentTurn &&  !area.hasEventListener("click")){
		area.addEventListener("mouseover", function(event) {
			if (isValidResourcePlace(area)){
				area.alpha = 0.5;
				galaxyStage.update(event);
			}
		})

		area.addEventListener("mouseout", function(event) { 
			if (isValidResourcePlace(area)){
				area.alpha = 1;
				galaxyStage.update(event);
			}
		})

		area.addEventListener("click", function(event) { 
			if (isValidResourcePlace(area)){
				setWorkerOnArea(area);
				area.alpha = 1;
			}
		})
	}
}

/**sélectionne une unité à acheter**/
function addBuyUnitSelectionEvent(unit){
	if (currentTurn &&  !unit.hasEventListener("click")){
		addObjectWithEvents(unit);
		unit.addEventListener("click", function(event) {
			askBuyingOrder(unit);
		})
	}
}

/**sélectionne une unité à acheter**/
function addBuyBuildingEvent(building){
	if (currentTurn &&  !building.hasEventListener("click")){
		addObjectWithEvents(building);
		building.addEventListener("click", function(event) {
			askBuyingBuildingOrder(building);
		})
	}
}


//TODO
/*réception des messages du serveur*/
//---------------------------------------------------------------------------------------

function displayUnlockedBuildings(serverAction){
	//mise en place du fond du canvas
	var canvasHeight = 3;
	stage.canvas.height = canvasHeight * 90 + 10;
	var backGround = new createjs.Shape();
	backGround.set({name : "backGround"});
	backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, 300, stage.canvas.height);
	stage.addChild(backGround);
	//affichage des batiments
	var buildingList = serverAction.buildingList;
	for (var i in buildingList){
		var building = displayBuilding(buildingList[i]);
		building.x = 5 + 100 * i;
		building.y = 5;
		stage.addChild(building);
		addBuyBuildingEvent(building);
	}
	stage.update();
}


function setOnOffCancelBuyOrder(serverAction){
	var cancelBuyOrder = document.getElementById("cancelBuyOrder");
	if (cancelBuyOrder){
		cancelBuyOrder.disabled = serverAction.disabledButton;
	}
}


/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addBuildingUnitTurnButton(serverAction){
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endBuildingUnitTurn();");
	endTurnButton.innerHTML = "Next step";
	if (!currentTurn){
		endTurnButton.disabled = true;
	}
	actionMenu.appendChild(endTurnButton);
	
	var cancelBuyOrder =  document.createElement("button");
	cancelBuyOrder.setAttribute("id", "cancelBuyOrder");
	cancelBuyOrder.setAttribute("class", "gameButton");
	cancelBuyOrder.setAttribute("type", "button");
	cancelBuyOrder.setAttribute("onclick", "cancelBuyOrder();");
	cancelBuyOrder.innerHTML = "Cancel order";
	cancelBuyOrder.disabled = true;
	actionMenu.appendChild(cancelBuyOrder);
}

function setUnitAlpha(serverAction){
	var unit = stage.getChildByName(serverAction.name);
	unit.alpha = serverAction.alpha;
	stage.update();
}

function activateBaseMineral(serverAction){
	var baseMineral = document.getElementById("baseMineral");
	baseMineral.addEventListener("click", sendSetWorkerOnBaseMineral);
	baseMineral.setAttribute("onmouseover", "setBackGround(this, 'green')");
	baseMineral.setAttribute("onmouseout", "setBackGround(this, 'white')");
}

function activateBaseGas(serverAction){
	var baseGas = document.getElementById("baseGas");
	baseGas.addEventListener("click", sendSetWorkerOnBaseGas);
	baseGas.setAttribute("onmouseover", "setBackGround(this, 'green')");
	baseGas.setAttribute("onmouseout", "setBackGround(this, 'white')");
}

function addResourcePlacesEvent(serverAction){
	var coordinates = serverAction.coordinates;
	for (var i in coordinates){
		var area = galaxyStage.getChildByName(coordinates[i].coordinate)
		.getChildAt(1).getChildByName("areaSet").getChildByName(coordinates[i].areaId);
		addGetResource(area);
	}
}


function activateResourceAreas(serverAction){
	galaxyStage.enableMouseOver(10);
	var coordinates = serverAction.coordinates;
	for(var i in coordinates){
		var connexion  = {
				coordinates: coordinates[i].coordinate,
				areaId: coordinates[i].areaId
				};
		validAreas.push(connexion);
	}
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function displayUnlockedUnits(serverAction){
	console.log(serverAction);
	var unitList = serverAction.unitList;
	stage.canvas.height = (Math.floor((unitList.length-1) / 3) + 2) * 100;
	var backGround = new createjs.Shape();
	backGround.set({name : "backGround"});
	backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, 300, stage.canvas.height - 100);
	stage.addChild(backGround);
	for (var i in unitList){
		var unit = drawUnitImage(unitList[i]);
		unit.scaleX = 2;
		unit.scaleY = 2;
		unit.x = (i%3) * 100 + 10;
		unit.y = Math.floor(i / 3) * 100 + 10;
		stage.addChild(unit);
	}
	stage.update();
}

function activateUnlockedUnits(serverAction){
	var unitList = serverAction.unitList;
	for (var i in unitList){
		var unit = stage.getChildByName(unitList[i].name);
		addBuyUnitSelectionEvent(unit);
	}
}


//TODO
/*fonctions de dessin*/
//---------------------------------------------------------------------------------------
/**dessine l'image d'une unité**/
function drawUnitImage(serverAction){
	if (!(serverAction.image in unitImages)){
		var image = new Image();
	    image.src = "../starcraftWebResources/unitImages/"+ serverAction.species + "/"+ serverAction.image;
	    image.onload = function() {
	    	//console.log("image loaded");
			stage.update();
			galaxyStage.update();
		}
	    unitImages[serverAction.image]=image;
	}
	var unit = new createjs.Container();
    
	var unitImage = new createjs.Bitmap(unitImages[serverAction.image]);

	unit.set({name : serverAction.name});
	//position dans une zone ou route
	
	var circle = new createjs.Shape();
	circle.graphics.beginFill(serverAction.color).drawCircle(20, 20, 20);
	unit.addChild(circle);
	unit.addChild(unitImage);
	return unit;
}

function displayBuilding(building){
	var buildingImage = new createjs.Container();
	buildingImage.set({name : building.number.toString() + "." +  building.level.toString()});
	var unitList = building.unitList;
	var height = unitList.length;
	//fond d'écran
	var square = new createjs.Shape();
	square.graphics.beginFill(building.color).drawRect(0, 0, 90, 90 * height);
	buildingImage.addChild(square);
	//unités débloquées
	for (var i in unitList){
		if (!(unitList[i].image in unitImages)){
			var image = new Image();
		    image.src = "../starcraftWebResources/unitImages/"+ building.species + "/"+ unitList[i].image;
		    image.onload = function() {
		    	//console.log("image loaded");
				stage.update();
			}
		    unitImages[unitList[i].image]=image;
		}
	    var unitImage = new createjs.Bitmap(unitImages[unitList[i].image]);
	    unitImage.x = 5;
	    unitImage.y = 5 + 90* i;
	    unitImage.scaleX = 2;
	    unitImage.scaleY = 2;
	    buildingImage.addChild(unitImage);
	}
	return buildingImage;
}

//TODO
/**regarde si la route ou la zone sont des endroits valides pour placer une unité**/
function isValidResourcePlace(area){
	var result = false;
	for (var i = 0; i < validAreas.length; i++){
		if (validAreas[i].coordinates === area.parent.parent.parent.name && validAreas[i].areaId === area.name){
			result=true;
			break;
		}
	}
	return result;
}