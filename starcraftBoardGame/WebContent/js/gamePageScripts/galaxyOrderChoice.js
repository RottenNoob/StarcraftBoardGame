var selectedSquareOrder;

/*actions du joueur*/
function endGalaxyOrderChoiceTurn(){
	if (currentTurn){
		galaxyStage.enableMouseOver(0);
		clearEventAndHighlight();
		var clientAction = {
				action : "endGalaxyOrderChoiceTurn",
				coordinates : selectedSquareOrder.name
		};
		webSocket.send(JSON.stringify(clientAction));
		var endTurnButton = document.getElementById("endTurnButton");
		endTurnButton.disabled = true;
		selectedSquareOrder.alpha = 1;
		selectSquareOrder(undefined);
		galaxyStage.update();
	}
}

function executeOrder(){
	if (currentTurn){
		
		var clientAction = {
				action : "executeOrder",
		};
		webSocket.send(JSON.stringify(clientAction));
	}
}

function cancelOrder(){
	if (currentTurn){
		var clientAction = {
				action : "cancelOrder",
		};
		webSocket.send(JSON.stringify(clientAction));
	}
}

/*ajout d'évènements*/
function addOrderExecutionEvent(square){
	if (currentTurn && !square.hasEventListener("click")){
		addObjectWithEvents(square);
		square.addEventListener("mouseover", function(event) { 
			if (selectedSquareOrder === undefined){
				square.alpha = 0.5;
			}
			galaxyStage.update(event);
		})

		square.addEventListener("mouseout", function(event) { 
			if (selectedSquareOrder === undefined){
				square.alpha = 1;
			}
			galaxyStage.update(event);
		})

		square.addEventListener("click", function(event) { 
			if (selectedSquareOrder === undefined){
				selectSquareOrder(square);
				square.alpha = 0.5;
				galaxyStage.update(event);
			}else{
				selectedSquareOrder.alpha = 1;
				selectSquareOrder(undefined);
				galaxyStage.update(event);
			}
		})
	}
}

/*réception des messages du serveur*/

/**active les différentes planètes où je joueur peut exécuter un ordre**/
function activateValidSquareOrders(serverAction){
	galaxyStage.enableMouseOver(10);
	var coordinates = serverAction.coordinates;
	for(var i in coordinates){
		var currentPlanetCoordinates = coordinates[i].coordinate;
		var currentSquare = galaxyStage.getChildByName(currentPlanetCoordinates);
		addOrderExecutionEvent(currentSquare);
	}
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addGalaxyOrderChoiceButton(serverAction){

	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endGalaxyOrderChoiceTurn();");
	endTurnButton.innerHTML = "Execute selected order";
	endTurnButton.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addActivationChoiceButton(serverAction){
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var executeOrder =  document.createElement("button");
	executeOrder.setAttribute("id", "endTurnButton");
	executeOrder.setAttribute("class", "gameButton");
	executeOrder.setAttribute("type", "button");
	executeOrder.setAttribute("onclick", "executeOrder();");
	executeOrder.innerHTML = "Execute "+ serverAction.orderName +" order on " + serverAction.planetName;
	
	var cancelOrder =  document.createElement("button");
	cancelOrder.setAttribute("id", "endTurnButton");
	cancelOrder.setAttribute("class", "gameButton");
	cancelOrder.setAttribute("type", "button");
	cancelOrder.setAttribute("onclick", "cancelOrder();");
	cancelOrder.innerHTML = "Cancel the order";
	
	actionMenu.appendChild(executeOrder);
	actionMenu.appendChild(cancelOrder);
}


/* fin réception des messages du serveur*/

/*fonctions utilitaires*/

function selectSquareOrder(value){
	if (value !== undefined){
		galaxyStage.enableMouseOver(0);
		endTurnButton.disabled = false;
	} else {
		galaxyStage.enableMouseOver(10);
		endTurnButton.disabled = true;
	}
	selectedSquareOrder = value;
}