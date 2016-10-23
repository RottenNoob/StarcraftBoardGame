var placedUnitChoice = 0;
var selectedUnit;
var validAreas = [];
var validRoads = [];

/*envoi des actions du joueur*/
function endUnitPlacementTurn(){
	if (currentTurn){
		validAreas.length = 0;
		unitSelection(undefined);
		stage.removeAllEventListeners();
		clearEventAndHighlight();
		var clientAction = {
				action : "endUnitPlacementTurn",
		};
		webSocket.send(JSON.stringify(clientAction));
		var endTurnButton = document.getElementById("endTurnButton");
		endTurnButton.disabled = true;

	}
}

function sendUnitPlacement(coordinates, areaId){
	var clientAction = {
			action : "sendUnitPlacement",
			unitId : selectedUnit.name.toString(),
			coordinates : coordinates,
			areaId : areaId.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}

function askValidPlacements(unitIdArg){
	var clientAction = {
			action : "askValidPlacements",
			unitId : unitIdArg.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}

function returnUnitToPool(unitIdArg){
	var clientAction = {
			action : "returnUnitToPool",
			unitId : unitIdArg.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}
/*envoi des actions du joueur*/

/**on sélectionne ou déselectionne une unité. Si on sélectionne, on active aussi la
détection du mouseover pour montrer les actions possibles**/
function unitSelection(value){
	if (value !== undefined){
		stage.enableMouseOver(10);
		galaxyStage.enableMouseOver(10);
		askValidPlacements(value.name);
	} else {
		stage.enableMouseOver(0);
		galaxyStage.enableMouseOver(0);
		validAreas.length = 0;
	}
	selectedUnit = value;
}

/**regarde si la route ou la zone sont des endroits valides pour placer une unité**/
function isValidPlace(area){
	var result = false;
	if (selectedUnit !== undefined){
		if (selectedUnit.type === "transport" && area.parent.name==="roadSet"){
			for (var i = 0; i < validRoads.length; i++){
				if (validRoads[i].coordinates === area.parent.parent.parent.name && validRoads[i].areaId === area.name){
					result=true;
					break;
				}
			}
		}else if (area.parent.name==="areaSet"){
			for (var i = 0; i < validAreas.length; i++){
				if (validAreas[i].coordinates === area.parent.parent.parent.name && validAreas[i].areaId === area.name){
					result=true;
					break;
				}
			}
		}
	}
	return result;
}

//TODO
/*ajout d'évènements*/
function addUnitSelectionEvent(unit){
	if (currentTurn &&  !unit.hasEventListener("click")){
		addObjectWithEvents(unit);
		unit.addEventListener("click", function(event) {
			if (selectedUnit === undefined){
				//sélection de l'unité
				unit.alpha = 0.6;
				unitSelection(unit);
			} else {
				if(selectedUnit.name === unit.name){
				//désélection
				unitSelection(undefined);
				unit.alpha =1;
			}}
			stage.update(event);
			galaxyStage.update(event);
		})
	}
}

function addUnitPlacementEvent(area){
	if (currentTurn &&  !area.hasEventListener("click")){
		addObjectWithEvents(area);
		area.addEventListener("mouseover", function(event) { 
			if (isValidPlace(area)){
				area.alpha = 0.5;
			}
			galaxyStage.update(event);
		})

		area.addEventListener("mouseout", function(event) { 
			if (isValidPlace(area)){
				area.alpha = 1;
			}
			galaxyStage.update(event);
		})

		area.addEventListener("click", function(event) { 
			if (isValidPlace(area)){
				var stringCoordinates = area.parent.parent.parent.name;
				placedUnitChoice = 0;
				sendUnitPlacement(stringCoordinates, area.name);
				selectedUnit.alpha = 1;
				selectedUnit.scaleX = 1;
				selectedUnit.scaleY = 1;
				unitSelection(undefined);
				area.alpha = 1;
			}
		})
	}
}

/**ajoute un évènement quand on click sur le canvas, à modifier si on souhaite ajouter l'évènement à un container**/
function addUnitToPoolEvent(container){
	if (currentTurn){
		container.on("stagemousedown", function(evt) {
			if (selectedUnit !== undefined){
				if (selectedUnit.parent.name === "unitContainer"){
					selectedUnit.alpha = 1;
					selectedUnit.scaleX = 2;
					selectedUnit.scaleY = 2;
					placedUnitChoice = 0;
					returnUnitToPool(selectedUnit.name);
					unitSelection(undefined);
				}
			}
		})
	}
}
/*ajout d'évènements*/

//TODO
/*réception des messages du serveur*/

function checkEndTurn(serverAction){
	var endTurnButton = document.getElementById("endTurnButton");
	if (endTurnButton){
		endTurnButton.disabled = serverAction.disabledButton;
	}
}

/**active les routes sélectionnables**/
function activateValidLinks(serverAction){
	var coordinates = serverAction.coordinates;
	for(var i in coordinates){
		var currentPlanetCoordinates = coordinates[i].coordinate;
		var currentPlanet = galaxyStage.getChildByName(currentPlanetCoordinates).getChildAt(1);
		addUnitPlacementEvent(currentPlanet.getChildByName("roadSet").getChildByName(coordinates[i].roadId));
	}
}

/**active les zones des planètes actives**/
function activateValidPlanets(serverAction){
	var coordinates = serverAction.coordinates;
	for(var i in coordinates){
		var currentPlanetCoordinates = coordinates[i].coordinate;
		var currentPlanet = galaxyStage.getChildByName(currentPlanetCoordinates).getChildAt(1);
		var areaSet = currentPlanet.getChildByName("areaSet");
		for (var i = 0; i < areaSet.numChildren; i++){
			addUnitPlacementEvent(areaSet.getChildAt(i));
		}
	}
}

/**si l'unité est toujours sélectionnée, on mémorise les endroits où l'unité peut aller**/
function sendValidPlacements(serverAction){
	//on vérifie qu'au moment où le client reçoit ces infos, celles-ci sont toujours significatives
	if (selectedUnit !== undefined){
		if (selectedUnit.name.toString() === serverAction.name.toString()){
			validAreas.length = 0;
			var coordinates = serverAction.coordinates;
			for(var i in coordinates){
				var connexion  = {
						coordinates: coordinates[i].coordinate,
						areaId: coordinates[i].areaId
						};
				validAreas.push(connexion);
			}
		}
	}
}

/**si l'unité est toujours sélectionnée, on mémorise les endroits où l'unité peut aller (pour les transports)**/
function sendValidTransportPlacements(serverAction){
	//on vérifie qu'au moment où le client reçoit ces infos, celles-ci sont toujours significatives
	if (selectedUnit !== undefined){
		if (selectedUnit.name.toString() === serverAction.name.toString()){
			validRoads.length = 0;
			var coordinates = serverAction.coordinates;
			for(var i in coordinates){
				var connexion  = {
						coordinates: coordinates[i].coordinate,
						areaId: coordinates[i].areaId
						};
				validRoads.push(connexion);
			}
		}
	}
}

/**active les zones de la planète active pour autoriser leur sélection**/
function activateCurrentPlanet(serverAction){
	var currentPlanetCoordinates = serverAction.planetCoordinates;
	var currentPlanet = galaxyStage.getChildByName(currentPlanetCoordinates).getChildAt(1);
	var areaSet = currentPlanet.getChildByName("areaSet");
	for (var i = 0; i < areaSet.numChildren; i++){
		addUnitPlacementEvent(areaSet.getChildAt(i));
	}
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addUnitPlacementButton(serverAction){
	//le nombre d'unité placées dans la barre d'action
	placedUnitChoice = 0;

	addUnitToPoolEvent(stage);
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endUnitPlacementTurn();");
	endTurnButton.innerHTML = "Next step";
	if (!currentTurn){
		endTurnButton.disabled = true;
	}
	actionMenu.appendChild(endTurnButton);
}

/**affiche une unité dans la barre des choix**/
function printUnitChoice(serverAction){
	changeCanvasHeight(placedUnitChoice);
	var xPlace = placedUnitChoice%3;
	var yPlace = Math.floor(placedUnitChoice/3);
	var unit = getUnit(serverAction.id);
	if (!unit){
		unit = drawUnit(serverAction, 10 + 100 * xPlace, 10 + 100 * yPlace);
	}else if (unit.area){
		//si on a retiré l'unité d'une zone de jeu, on met à jour les informations de la zone
		var index = unit.area.placedUnits.indexOf(unit.position);
		if (index > -1) {
			unit.area.placedUnits.splice(index, 1);
		}
		unit.set({area : undefined});
	}
	unit.x = 10 + 100 * xPlace;
	unit.y = 10 + 100 * yPlace;
	unit.scaleX = 2;
	unit.scaleY = 2;
	stage.addChild(unit);
    stage.update();
    galaxyStage.update();

	placedUnitChoice++;
}

/**une unité quitte sa place**/
function unitLeaveArea(unitId){
	var unit = getUnit(unitId);
	if (unit.area){
		var index = unit.area.placedUnits.indexOf(unit.position);
		if (index > -1) {
			unit.area.placedUnits.splice(index, 1);
		}
	}
}

/**on affiche une unité dans la galaxie**/
function printGalaxyUnit(serverAction){
	var unit = getUnit(serverAction.id);
	var areaSet = galaxyStage.getChildByName(serverAction.xPosition + "." +serverAction.yPosition)
	.getChildAt(1).getChildByName("areaSet");
	if (!unit){
		unit = drawUnit(serverAction, 0, 0);
	}
	
	var unitContainer = galaxyStage.getChildByName("unitContainer");
	var planetCenterX = (serverAction.xPosition - galaxyCanvasSizes.xMin) * 400 + 200;
	var planetCenterY = (serverAction.yPosition - galaxyCanvasSizes.yMin) * 400 + 200;
	
	var areaCount = areaSet.numChildren;
	//nouvelle zone où l'unité sera placée
	var area = areaSet.getChildByName(serverAction.areaId);
	//détermine les angles qui contiennent la zone où sont placées les unités
	var angle1 = (Math.PI*2/areaCount) * (serverAction.areaId-1);
	var angle2 = (Math.PI*2/areaCount) * serverAction.areaId;
	if (serverAction.type === "base"){
		unit.x = planetCenterX + 120 *  Math.cos((angle1 + angle2)/2) +  45 * Math.cos(angle2) - 20;
		unit.y = planetCenterY + 120 *  Math.sin((angle1 + angle2)/2) +  45 * Math.sin(angle2) - 20;
	}else if (serverAction.type === "mobile"){
		var alreadyPlaced = false;
		//ancienne zone où l'unité était
		if (unit.area){
			if (area != unit.area){
				var index = unit.area.placedUnits.indexOf(unit.position);
				if (index > -1) {
					unit.area.placedUnits.splice(index, 1);
				}
			}else{
				alreadyPlaced = true;
			}
		}
		//pour les unités mobiles, on n'a pas besoin de se préoccuper des resizes
		//car il n'ont jamais lieu après les placements d'unités mobiles
		if (!alreadyPlaced){
			unit.set({area : area});
			var k = 0;
			while (unit.area.placedUnits.indexOf(k) > -1){
				k++;
			}
			if (k < 3){
				unit.x =planetCenterX + 50 *  Math.cos((angle1 + angle2)/2) + k * 50 * Math.cos(angle1) - 20;
				unit.y = planetCenterY + 50 *  Math.sin((angle1 + angle2)/2) + k * 50 * Math.sin(angle1) - 20;
			} else  if (k < 5){
				unit.x =planetCenterX + 50 *  Math.cos((angle1 + angle2)/2) + (k - 2) * 50 * Math.cos(angle2) - 20;
				unit.y = planetCenterY + 50 *  Math.sin((angle1 + angle2)/2) + (k - 2) * 50 * Math.sin(angle2) - 20;
			} else if (k < 9){
				unit.x =planetCenterX + 10 *  Math.cos((angle1 + angle2)/2) + (k - 5) * 40 * Math.cos(angle1) - 20;
				unit.y = planetCenterY + 10 *  Math.sin((angle1 + angle2)/2) + (k - 5) * 40 * Math.sin(angle1) - 20;
			} else if (k < 12){
				unit.x =planetCenterX + 10 *  Math.cos((angle1 + angle2)/2) + (k - 8) * 40 * Math.cos(angle2) - 20;
				unit.y = planetCenterY + 10 *  Math.sin((angle1 + angle2)/2) + (k - 8) * 40 * Math.sin(angle2) - 20;
			}else{
				unit.x = planetCenterX + 120 *  Math.cos((angle1 + angle2)/2);
				unit.y = planetCenterY + 120 *  Math.sin((angle1 + angle2)/2);
			}
			unit.set({position : k});
			unit.area.placedUnits.push(k);
		}
	}else{
		//ancienne zone où l'unité était
		if (unit.area){
			var index = unit.area.placedUnits.indexOf(unit.position);
			if (index > -1) {
				unit.area.placedUnits.splice(index, 1);
			}
		}
		//trouve la route où l'unité est placée
		var roadSet = galaxyStage.getChildByName(serverAction.xPosition + "." +serverAction.yPosition)
		.getChildAt(1).getChildByName("roadSet");
		var road = roadSet.getChildByName(serverAction.areaId);
		unit.set({area : road});
		var l = 0;
		while (unit.area.placedUnits.indexOf(l) > -1){
			l++;
		}
		unit.x = planetCenterX + (220 - roadSize/2) * Math.cos((Math.PI/2) * (road.name - 1)) - 20
		+ 40 * (l - 1) * Math.cos((Math.PI/2) * road.name);
		unit.y = planetCenterY + (220 - roadSize/2) * Math.sin((Math.PI/2) * (road.name - 1)) - 20
		+ 40 * (l - 1) * Math.sin((Math.PI/2) * road.name);
		unit.set({position : l});
		unit.area.placedUnits.push(l);
	}
	
	
	unitContainer.addChild(unit);
	galaxyStage.setChildIndex( unitContainer, galaxyStage.getNumChildren()-1);
	galaxyStage.update();
	stage.update();
	
}

function activateValidUnits(serverAction){
	var unitList = serverAction.unitList;
	for(var i in unitList){
		addUnitSelectionEvent(getUnit(unitList[i].unitId));
	}
}

/*fin réception des messages du serveur*/

/*fonctions utilitaires*/
/**renvoie l'unité ayant l'id en paramètre**/
function getUnit(unitId){
	var unit;
	var unitContainer = galaxyStage.getChildByName("unitContainer");
	if (stage.getChildByName(unitId)){
		unit = stage.getChildByName(unitId);
	}else if (unitContainer.getChildByName(unitId)){
		unit = unitContainer.getChildByName(unitId);
	}
	return unit;
}

/**dessine une unité**/
function drawUnit(serverAction, x, y){
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

	unit.set({name : serverAction.id});
	unit.set({type : serverAction.type});
	//position dans une zone ou route
	unit.set({position : 0});
	
	var circle = new createjs.Shape();
	circle.graphics.beginFill(serverAction.color).drawCircle(20, 20, 20);
	unit.addChild(circle);
	unit.addChild(unitImage);
	unit.x = x;
	unit.y = y;
	return unit;
}