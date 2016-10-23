var placedOrderChoice = 0;
var stackOrderNumber = 0;
var selectedOrder;
var placedOrder;

/*actions du joueur*/
function endPlanningPhaseTurn(){
	if (currentTurn){
		stage.enableMouseOver(0);
		galaxyStage.enableMouseOver(0);
		clearEventAndHighlight();
		var clientAction = {
				action : "endPlanningPhaseTurn",
				id : placedOrder.orderId.toString(),
				coordinates : placedOrder.parent.name
		};
		webSocket.send(JSON.stringify(clientAction));
		var endTurnButton = document.getElementById("endTurnButton");
		endTurnButton.disabled = true;
		orderSelection(undefined);
		setPlacedOrder(undefined);
		placedOrderChoice = 0;
	}
}

function returnOrderToPool(){
	stage.addChild(selectedOrder);
	selectedOrder.x = selectedOrder.xMenu;
	selectedOrder.y = selectedOrder.yMenu;
	stage.update();
	galaxyStage.update();
}

function askOrderStack(orderCoordinates){
	stackOrderNumber = 0;
	clearModalMenu();
	var clientAction = {
			action : "askOrderStack",
			coordinates : orderCoordinates
	};
	webSocket.send(JSON.stringify(clientAction));
	showModalMenu();
}


/*ajout d'évènements*/
function addDisplayOrderEvent(order){
	order.addEventListener("click", function(event) {
		askOrderStack(order.parent.name);
	})
}

function addOrderEvent(order){
	if (currentTurn){
		addObjectWithEvents(order);
		order.addEventListener("click", function(event) {
			if (selectedOrder === undefined){
				//sélection de l'ordre
				order.alpha = 0.6;
				orderSelection(order);
			} else if(selectedOrder.orderId === order.orderId){
				//désélection
				orderSelection(undefined);
				order.alpha =1;
			} else{
				selectedOrder.alpha =1;
				order.alpha = 0.6;
				orderSelection(order);
			}
			stage.update(event);
			galaxyStage.update(event);
		})
	}
}

/**ajoute un évènement à la case**/
function addOrderPlacementEvent(square){
	if (currentTurn && !square.hasEventListener("click")){
		addObjectWithEvents(square);
		square.addEventListener("mouseover", function(event) { 
			if (isValidSquareOrder(square)){
				square.alpha = 0.5;
			}
			galaxyStage.update(event);
		})

		square.addEventListener("mouseout", function(event) { 
			if (isValidSquareOrder(square)){
				square.alpha = 1;
			}
			galaxyStage.update(event);
		})

		square.addEventListener("click", function(event) { 
			if (isValidSquareOrder(square)){
				square.alpha = 1;
				selectedOrder.alpha = 1;
				selectedOrder.scaleX = 1;
				selectedOrder.scaleY = 1;
				selectedOrder.x = 375;
				selectedOrder.y = 375;
				square.addChild(selectedOrder);
				setPlacedOrder(selectedOrder);
				orderSelection(undefined);
				square.alpha = 1;
				galaxyStage.update(event);
				stage.update(event);
			}
		})
	}
}

/*fin ajout d'évènements*/

/*ajoute un évènement quand on click sur le canvas, à modifier si on souhaite ajouter l'évènement à un container*/
function addOrderToPoolEvent(container){
	if (currentTurn){
		container.on("stagemousedown", function(evt) {
			if (selectedOrder !== undefined){
				if (selectedOrder.parent.parent){
					selectedOrder.alpha = 1;
					selectedOrder.scaleX = 2;
					selectedOrder.scaleY = 2;
					returnOrderToPool();
					orderSelection(undefined);
					setPlacedOrder(undefined);
				}
			}
		})
	}
}

/* fin ajout d'évènements*/

/*réception des messages du serveur*/

/**active les différentes planètes où je joueur peut poser un ordre**/
function activateValidPlanetSquares(serverAction){
	var coordinates = serverAction.coordinates;
	for(var i in coordinates){
		var currentPlanetCoordinates = coordinates[i].coordinate;
		var currentSquare = galaxyStage.getChildByName(currentPlanetCoordinates);
		addOrderPlacementEvent(currentSquare);
	}
}

function clearDisplayedOrders(serverAction){
	hideModalMenu();
	for (var i = 0; i < galaxyStage.numChildren; i++){
		var square = galaxyStage.getChildAt(i);
		if (square.name !== "unitContainer"){
			for (var j = 0; j < square.numChildren; j++){
				if (square.getChildAt(j).type){
					if (square.getChildAt(j).type === "order"){
						square.removeChildAt(j);
					}
				}
			}
		}
	}
}


/**montre les différents ordres posés sur la galaxie**/
function displayPlacedOrder(serverAction){
	var order = drawOrder(serverAction);
	galaxyStage.getChildByName(serverAction.coorddinates).addChild(order);
	order.x = 375;
	order.y = 375;
	addDisplayOrderEvent(order);
	galaxyStage.update();
}

/**montre les différents ordres utilisables par le joueur**/
function displayAvailableOrder(serverAction){
	changeCanvasHeight(placedOrderChoice);
	var order = drawOrder(serverAction);
	
	var xPlace = placedOrderChoice%3;
	var yPlace = Math.floor(placedOrderChoice/3);
	
	order.x = 50 + 100 * xPlace;
	order.y = 50 + 100 * yPlace;
	order.scaleX = 2;
	order.scaleY = 2;
	order.set({xMenu : order.x});
	order.set({yMenu : order.y});
	
	addOrderEvent(order);
	stage.addChild(order);
    stage.update();
    galaxyStage.update();

    placedOrderChoice++;
}

/**montre les différents ordres situés sur la pile**/
function displayOrderStack(serverAction){
	setModalMenuSize(300, 500);
	var order = drawOrder(serverAction);
	
	var xPlace = stackOrderNumber%3;
	var yPlace = Math.floor(stackOrderNumber/3);
	order.x = 50 + 100 * xPlace;
	order.y = 50 + 100 * yPlace;
	order.scaleX = 2;
	order.scaleY = 2;
	
	modalStage.addChild(order);
	modalStage.update();

    stackOrderNumber++;
}


/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addPlanningPhaseButton(serverAction){

	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endPlanningPhaseTurn();");
	endTurnButton.innerHTML = "Next step";
	endTurnButton.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
}

/*fin réception des messages du serveur*/

/*fonctions de dessin*/
function drawOrder(serverAction){
	var orderColor;
	if (serverAction.special){
		orderColor = "#999900"
	}else{
		orderColor = "#BDBDBD"
	}
	//image d'un point d'ordre spécial
	var order = new createjs.Shape();
	if (serverAction.id !== undefined){
		order.set({orderId : serverAction.id});
	}
	order.set({type : "order"});
	order.graphics.beginFill(serverAction.color).beginStroke(orderColor).setStrokeStyle(4).drawRegularPolygon(0, 0, 20, 6);
	order.graphics.endStroke().endFill();
	if (serverAction.name === "move"){
		order.graphics.beginStroke(orderColor).setStrokeStyle(6);
		order.graphics.moveTo(-15, 0);
		order.graphics.lineTo(15, 0);
		order.graphics.endStroke();
		order.graphics.beginStroke(orderColor).setStrokeStyle(6);
		order.graphics.moveTo(0, -15);
		order.graphics.lineTo(0, 15);
		order.graphics.endStroke();
	} else if (serverAction.name === "build"){
		order.graphics.beginStroke(orderColor).setStrokeStyle(6).drawRect(-8, -8, 16, 16);
		order.graphics.endStroke();
	} else if (serverAction.name === "research"){
		order.graphics.beginStroke(orderColor).setStrokeStyle(6).drawCircle(0, 0, 10);
		order.graphics.endStroke();
	} else if (serverAction.name === "hidden"){
		//on ne dessine rien de plus
	}
	
	return order;
}


/*fonction utilitaire*/
//on sélectionne ou d"selectionne une planète. Si on sélectionne, on active aussi la
//détection du mouseover pour montrer les actions possibles
function orderSelection(value){
	if (value !== undefined){
		stage.enableMouseOver(10);
		galaxyStage.enableMouseOver(10);	
	} else {
		stage.enableMouseOver(0);
		galaxyStage.enableMouseOver(0);
	}
	selectedOrder = value;
}

function setPlacedOrder(value){
	var endTurnButton = document.getElementById("endTurnButton");
	if (value !== undefined){
		endTurnButton.disabled = false;
		if (!stage.hasEventListener("stagemousedown")){
			addOrderToPoolEvent(stage);
		}
	} else {
		endTurnButton.disabled = true;
	}
	placedOrder = value;
}

function isValidSquareOrder(square){
	var result = false;
	if (selectedOrder !== undefined){
		if (placedOrder === undefined){
			result = true;
		}else if (placedOrder === selectedOrder && placedOrder.parent.name !== square.name){
			result = true;
		}
	}
	return result;
}
