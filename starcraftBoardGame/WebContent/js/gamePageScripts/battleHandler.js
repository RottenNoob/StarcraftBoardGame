var attackingBackGround = "#FFD68B";
var defendingBackGround = "#EEF7FA";
/**liste des objets ayant des events**/
var battleObjectWithEvents = [];
var battleUnitSelected;
var validBattlePlaces = [];
var battleCardSelected;


/*actions joueurs*/
//---------------------------------------------------------------------------------------
function destroyBattleUnit(unitId){
	for (var i = 0; i < battleObjectWithEvents.length; i++){
		battleObjectWithEvents[i].removeAllEventListeners();
	}
	battleObjectWithEvents.length = 0;
	var clientAction = {
			action : "destroyBattleUnit",
			unitId : unitId.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}


function endRetreatTurn(){
	if (currentTurn){
		validAreas.length = 0;
		unitSelection(undefined);
		stage.removeAllEventListeners();
		clearEventAndHighlight();
		var clientAction = {
				action : "endRetreatTurn",
		};
		webSocket.send(JSON.stringify(clientAction));
		var endTurnButton = document.getElementById("endTurnButton");
		endTurnButton.disabled = true;

	}
}

function endBattleCardTurn(){
	validBattlePlaces.length = 0;
	if (battleCardSelected !== undefined){
		battleCardSelected.alpha = 1;
		battleCardSelection(undefined);
	}
	stage.removeAllEventListeners();
	for (var i = 0; i < battleObjectWithEvents.length; i++){
		battleObjectWithEvents[i].removeAllEventListeners();
	}
	battleObjectWithEvents.length = 0;
	var clientAction = {
			action : "endBattleCardTurn",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function endSupportLineTurn(){
	validBattlePlaces.length = 0;
	if (battleUnitSelected !== undefined){
		battleUnitSelected.alpha = 1;
		battleUnitSelection(undefined);
	}
	stage.removeAllEventListeners();
	for (var i = 0; i < battleObjectWithEvents.length; i++){
		battleObjectWithEvents[i].removeAllEventListeners();
	}
	battleObjectWithEvents.length = 0;
	var clientAction = {
			action : "endSupportLineTurn",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function endFrontLineTurn(){
	validBattlePlaces.length = 0;
	if (battleUnitSelected !== undefined){
		battleUnitSelected.alpha = 1;
		battleUnitSelection(undefined);
	}
	stage.removeAllEventListeners();
	for (var i = 0; i < battleObjectWithEvents.length; i++){
		battleObjectWithEvents[i].removeAllEventListeners();
	}
	battleObjectWithEvents.length = 0;
	var clientAction = {
			action : "endFrontLineTurn",
	};
	webSocket.send(JSON.stringify(clientAction));
}

/**demande les emplacements possibles en fonction de l'unité choisie**/
function askValidBattlePlacements(unitIdArg){
	var clientAction = {
			action : "askValidBattlePlacements",
			unitId : unitIdArg.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}

function askValidBattleCardPlacements(cardIdArg){
	var clientAction = {
			action : "askValidBattleCardPlacements",
			cardId : cardIdArg
	};
	webSocket.send(JSON.stringify(clientAction));
}
/**on met à jour le placement de l'unité dans le jeu**/
function sendBattleUnitPlacement(battleRow, battlePlace){
	var clientAction = {
			action : "sendBattleUnitPlacement",
			unitId : battleUnitSelected.name.toString(),
			battleRow : battleRow,
			battlePlace : battlePlace
	};
	webSocket.send(JSON.stringify(clientAction));
}

/**on met à jour le placement de la carte dans le jeu**/
function sendBattleCardPlacement(battleRow, battlePlace){
	
	var clientAction = {
			action : "sendBattleCardPlacement",
			cardId : battleCardSelected.name.toString(),
			battleRow : battleRow,
			battlePlace : battlePlace
	};
	webSocket.send(JSON.stringify(clientAction));
}

function returnUnitToBattlePool(unitIdArg){
	var clientAction = {
			action : "returnUnitToBattlePool",
			unitId : unitIdArg.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}

function returnCardToBattlePool(cardIdArg){
	var clientAction = {
			action : "returnCardToBattlePool",
			cardId : cardIdArg.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
}
//---------------------------------------------------------------------------------------
/*fin actions joueurs*/

//TODO (ajouter highlight)
/*ajout d'évènements*/
//---------------------------------------------------------------------------------------
function addUnitDestructionEvent(unit){
	if (currentTurn &&  !unit.hasEventListener("click")){
		addBattleObjectWithEvents(unit);
		unit.addEventListener("click", function(event) {
			destroyBattleUnit(unit.name);
		})
	}
}

/**ajoute un évènement quand on click sur le canvas, à modifier si on souhaite ajouter l'évènement à un container**/
function addBattleCardToPoolEvent(container){
	if (currentTurn){
		container.on("stagemousedown", function(evt) {
			if (battleCardSelected !== undefined){
				if (battleCardSelected.parent.name === "unitContainer"){
					battleCardSelected.alpha = 1;
					returnCardToBattlePool(battleCardSelected.name);
					battleCardSelection(undefined);
				}
			}
		})
	}
}

/**ajoute un évènement quand on click sur le canvas, à modifier si on souhaite ajouter l'évènement à un container**/
function addBattleUnitToPoolEvent(container){
	if (currentTurn){
		container.on("stagemousedown", function(evt) {
			if (battleUnitSelected !== undefined){
				if (battleUnitSelected.parent.name === "unitContainer"){
					battleUnitSelected.alpha = 1;
					returnUnitToBattlePool(battleUnitSelected.name);
					battleUnitSelection(undefined);
				}
			}
		})
	}
}

function addBattleUnitPlacementEvent(area){
	if (currentTurn &&  !area.hasEventListener("click")){
		addBattleObjectWithEvents(area);
		area.addEventListener("mouseover", function(event) { 
			if (isValidBattlePlace(area)){
				area.alpha = 0.5;
			}
			combatStage.update(event);
		})

		area.addEventListener("mouseout", function(event) { 
			if (isValidBattlePlace(area)){
				area.alpha = 1;
			}
			combatStage.update(event);
		})

		area.addEventListener("click", function(event) { 
			if (isValidBattlePlace(area)){
				sendBattleUnitPlacement(area.parent.name, area.name);
				battleUnitSelected.alpha = 1;
				battleUnitSelection(undefined);
				area.alpha = 1;
				combatStage.update(event);
				stage.update(event);
			}
		})
	}
}

function addBattleCardPlacementEvent(area){
	if (currentTurn &&  !area.hasEventListener("click")){
		addBattleObjectWithEvents(area);
		area.addEventListener("mouseover", function(event) { 
			if (isValidBattlePlace(area)){
				area.alpha = 0.5;
			}
			combatStage.update(event);
		})

		area.addEventListener("mouseout", function(event) { 
			if (isValidBattlePlace(area)){
				area.alpha = 1;
			}
			combatStage.update(event);
		})

		area.addEventListener("click", function(event) { 
			if (isValidBattlePlace(area)){
				sendBattleCardPlacement(area.parent.name, area.name);
				battleCardSelected.alpha = 1;
				battleCardSelection(undefined);
				area.alpha = 1;
				combatStage.update(event);
				stage.update(event);
			}
		})
	}
}


function addBattleUnitSelectionEvent(unit){
	if (currentTurn &&  !unit.hasEventListener("click")){
		addBattleObjectWithEvents(unit);
		unit.addEventListener("click", function(event) {
			if (battleUnitSelected === undefined){
				//sélection de l'unité
				unit.alpha = 0.6;
				battleUnitSelection(unit);
			} else if(battleUnitSelected.name === unit.name){
				//désélection
				battleUnitSelection(undefined);
				unit.alpha =1;
			}
			stage.update(event);
			combatStage.update(event);
		})
	}
}

function addBattleCardSelectionEvent(card){
	if (currentTurn &&  !card.hasEventListener("click")){
		addBattleObjectWithEvents(card);
		card.addEventListener("click", function(event) {
			if (battleCardSelected === undefined){
				//sélection de la carte
				card.alpha = 0.6;
				battleCardSelection(card);
			} else if(battleCardSelected.name === card.name){
				//désélection
				battleCardSelection(undefined);
				card.alpha =1;
			}
			stage.update(event);
			combatStage.update(event);
		})
	}
}
//---------------------------------------------------------------------------------------
/*fin ajout d'évènements*/

//TODO
/*réception des messages du serveur*/
//---------------------------------------------------------------------------------------

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addEndRetreatButton(serverAction){
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endRetreatTurn();");
	endTurnButton.innerHTML = "Next step";
	endTurnButton.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
}

/****/
function clearBattleStage(serverAction){
	combatStage.removeAllChildren();
}

/****/
function activateDeletableUnits(serverAction){
	var unitList = serverAction.unitList;
	for(var i in unitList){
		var unit = combatStage.getChildByName("unitContainer").getChildByName(unitList[i].unitId);
		addUnitDestructionEvent(unit);
	}
}

/****/
function removeUnitDisplay(serverAction){
	var unitContainer = combatStage.getChildByName("unitContainer");
	if (unitContainer.getChildByName(serverAction.unitId)){
		unitContainer.removeChild(unitContainer.getChildByName(serverAction.unitId));
		combatStage.update();
	}
	var unitContainerGalaxy = galaxyStage.getChildByName("unitContainer");
	if (unitContainerGalaxy.getChildByName(serverAction.unitId)){
		unitLeaveArea(serverAction.unitId);
		unitContainerGalaxy.removeChild(unitContainerGalaxy.getChildByName(serverAction.unitId));
		galaxyStage.update();
	}
}

/**affiche le choix des cartes de combat**/
function displayCombatCardChoice(serverAction){
	var cardNumber = 0;
	if (stage.numChildren < 2){
		cardNumber = 1;
	}else{
		cardNumber = stage.numChildren;
	}
	setCanvasPixelHeight((Math.floor(cardNumber/2) + 1) * (0.75 * cardHeight + 10));
	var card = drawCombatCard(serverAction);
	card.scaleX = 0.75;
	card.scaleY = 0.75;
	card.y = 5 + Math.floor((cardNumber-1)/2) * (0.75 * cardHeight + 10);
	card.x = ((cardNumber - 1)%2) * (0.75 * cardWidth + 10) + 5;
	addBattleCardSelectionEvent(card);
	stage.addChild(card);
	stage.update();
}

function sendEndBattleTurn(serverAction){
	var endTurnButton = document.getElementById("endTurnButton");
	endTurnButton.disabled = !serverAction.ending;
}

/**affiche une carte sur le champs de bataille**/
function displayBattleCard(serverAction){
	var cardName = "card" + serverAction.id;
	var card = stage.getChildByName(cardName);
	var place = combatStage
	.getChildByName("checkerContainer").getChildByName(serverAction.battleRow).getChildByName(serverAction.battlePlace);
	
	if (card){
		card.scaleX = 1;
		card.scaleY = 1;
		combatStage.getChildByName("unitContainer").addChild(card);
	}else{
		card = combatStage.getChildByName("unitContainer").getChildByName(cardName);
		if (!card){
			card = drawCombatCard(serverAction);
			combatStage.getChildByName("unitContainer").addChild(card);
		}
	}
	card.x = place.x;
	card.y = place.y + place.parent.y;
	combatStage.update();
	stage.update();
}

/**montre le dos des cartes de combats de l'adversaire**/
function displayBackCard(serverAction){
	var cardName = "cardBack" + serverAction.id;
	var card = combatStage.getChildByName("unitContainer").getChildByName(cardName);
	if (!card){
		card = drawHiddenCard(serverAction);
	}
	card.set({name : cardName});
	
	var place = combatStage
	.getChildByName("checkerContainer").getChildByName(serverAction.battleRow).getChildByName(serverAction.battlePlace);
	combatStage.getChildByName("unitContainer").addChild(card);
	
	card.x =  place.x;
	card.y =  place.y + place.parent.y;
	combatStage.update();
	stage.update();
}

/****/
function removeBackCards(serverAction){
	var unitContainer = combatStage.getChildByName("unitContainer");
	for (var i = 0; i < unitContainer.numChildren; i++){
		if (unitContainer.getChildAt(i).name.toString().indexOf("cardBack") > -1){
			unitContainer.removeChildAt(i);
			i--;
		}
	}
}



function displayBattleUnit(serverAction){
	var unitId = serverAction.name;
	var unitCopy = getBattleUnitCopy(unitId);
	combatStage.getChildByName("unitContainer").addChild(unitCopy);
	var place = combatStage
	.getChildByName("checkerContainer").getChildByName(serverAction.battleRow).getChildByName(serverAction.battlePlace);
	unitCopy.x = place.x + 10;
	unitCopy.y = place.y + 10 + place.parent.y;
	//si la place est déjà occupée par une unitée, on déplace unitCopy sur la gauche
	var occupiedXPlaces = [];
	for (var i = 0; i < combatStage.getChildByName("unitContainer").numChildren; i++){
		var unit = combatStage.getChildByName("unitContainer").getChildAt(i);
		if (unit.name !== unitCopy.name){
			if (unit.y === unitCopy.y){
				occupiedXPlaces.push(unit.x);
			}
		}
	}
	while (occupiedXPlaces.indexOf(unitCopy.x) > -1){
		unitCopy.x += 100;
	}
	combatStage.update();
	stage.update();
}


/**si l'unité est toujours sélectionnée, on mémorise les endroits où l'unité peut aller**/
function sendValidBattlePlacements(serverAction){
	//on vérifie qu'au moment où le client reçoit ces infos, celles-ci sont toujours significatives
	if (battleUnitSelected !== undefined){
		if (battleUnitSelected.name.toString() === serverAction.name.toString()){
			var placeNames = serverAction.placeNames;
			for(var i in placeNames){
				var connexion  = {
						battleRow: placeNames[i].battleRow,
						battlePlace: placeNames[i].battlePlace
						};
				validBattlePlaces.push(connexion);
			}
		}
	}
}

function sendValidBattleCardPlacements(serverAction){
	//on vérifie qu'au moment où le client reçoit ces infos, celles-ci sont toujours significatives
	if (battleCardSelected !== undefined){
		if (battleCardSelected.name.toString() === serverAction.name.toString()){
			var placeNames = serverAction.placeNames;
			for(var i in placeNames){
				var connexion  = {
						battleRow: placeNames[i].battleRow,
						battlePlace: placeNames[i].battlePlace
						};
				validBattlePlaces.push(connexion);
			}
		}
	}
}


/**active les unités manipulables**/
function activateValidBattleUnits(serverAction){
	var unitList = serverAction.unitList;
	for (var i in unitList){
		var unit = getBattleUnitCopy(unitList[i].unitId);
		addBattleUnitSelectionEvent(unit);
	}
}


/**active le canvas où se déroule le combat**/
function setCombatModeOn(serverAction){
	setCombatMode(true);
}

/**désactive le canvas où se déroule le combat**/
function setCombatModeOff(serverAction){
	setCombatMode(false);
}

/**fait apparaitre le champ de bataille**/
function setCardBattleField(serverAction){
	//hauteur correspond à la hauteur d'une carte * le nombre d'escarmouches
	combatStage.canvas.height = serverAction.height * cardHeight;
	//le champs de bataille est égale à la (largeur des unités + 10) * le nombre d'unités possibles dans une seule escarmouche
	var battleWidth = (serverAction.attackWidth + serverAction.defenseWidth) * 100;
	combatStage.canvas.width = battleWidth + 4 * cardWidth;
	var attackStageWidth = serverAction.attackWidth * 100;
	
	//construction de l'échiquier sur le quel on pose cartes et unités
	var checker = drawChecker(serverAction);
	combatStage.addChild(checker);
	
	var unitContainer = new createjs.Container();
	unitContainer.set({name : "unitContainer"});
	combatStage.addChild(unitContainer);
	
	combatStage.update();
}

/**fait apparaitre le champ de bataille**/
function setBattleField(serverAction){
	stage.removeAllChildren();
	
	//hauteur correspond à la hauteur d'une carte * le nombre d'escarmouches
	combatStage.canvas.height = serverAction.height * cardHeight;
	//le champs de bataille est égale à la (largeur des unités + 10) * le nombre d'unités possibles dans une seule escarmouche
	var battleWidth = (serverAction.attackWidth + serverAction.defenseWidth) * 100;
	combatStage.canvas.width = battleWidth + 4 * cardWidth;
	var attackStageWidth = serverAction.attackWidth * 100;
	
	//construction de l'échiquier sur le quel on pose cartes et unités
	var checker = drawChecker(serverAction);
	combatStage.addChild(checker);
	
	var unitContainer = new createjs.Container();
	unitContainer.set({name : "unitContainer"});
	combatStage.addChild(unitContainer);
	
	var attackerRows = Math.floor((serverAction.attackersNumber - 1)/3) + 1;
	var defenderRows = Math.floor((serverAction.defendersNumber - 1)/3) + 1;
	stage.canvas.height = (attackerRows + defenderRows) * 100;
	var attackingUnits = new createjs.Shape();
	attackingUnits.set({name : "attackingUnits"});
	attackingUnits.graphics.beginFill(attackingBackGround).drawRect(0, 0, 300, attackerRows * 100);
	stage.addChild(attackingUnits);
	var defendingUnits = new createjs.Shape();
	defendingUnits.set({name : "defendingUnits"});
	defendingUnits.graphics.beginFill(defendingBackGround).drawRect(0, 0, 300, defenderRows * 100);
	defendingUnits.y = attackerRows * 100;
	stage.addChild(defendingUnits);
	
	stage.update();
	combatStage.update();
}

/**affiche les unitées non placées à l'initialisation**/
function displayUnplacedUnits(serverAction){
	var attackers = serverAction.attackers;
	var defenders = serverAction.defenders;

	for(var i in attackers){
		var unitId = attackers[i].unitId;
		var unitCopy = galaxyStage.getChildByName("unitContainer").getChildByName(unitId).clone(true);
		unitCopy.scaleX = 2;
		unitCopy.scaleY = 2;
		unitCopy.x = (i%3) * 100 + 10;
		unitCopy.y = Math.floor(i/3) * 100 + 10;
		stage.addChild(unitCopy);
	}
	for(var j in defenders){
		var unitId = defenders[j].unitId;
		var unitCopy = galaxyStage.getChildByName("unitContainer").getChildByName(unitId).clone(true);
		unitCopy.scaleX = 2;
		unitCopy.scaleY = 2;
		unitCopy.x = (j%3) * 100 + 10;
		unitCopy.y = Math.floor(j/3) * 100 + 10 + stage.getChildByName("defendingUnits").y;
		stage.addChild(unitCopy);
	}
	stage.update();
}

/**déplace une unité dans le menu d'action**/
function sendUnitToBattlePool(serverAction){
	var unit = getBattleUnitCopy(serverAction.name);
	stage.addChild(unit);
	unit.x = 10;
	if (serverAction.attacker){
		unit.y = 10;
	}else{
		unit.y = 10 + stage.getChildByName("defendingUnits").y;
	}
	reDisplayUnplacedBattleUnits();
	combatStage.update();
}

/**déplace une unité dans le menu d'action**/
function sendCardToBattlePool(serverAction){
	var cardName = "card" + serverAction.name;
	var card = combatStage.getChildByName("unitContainer").getChildByName(cardName);
	stage.addChild(card);
	var j = 0;
	for (var i = 0; i < stage.numChildren; i++){
		var item = stage.getChildAt(i);
		if (item.name !== "backGround"){
			item.scaleX = 0.75;
			item.scaleY = 0.75;
			item.y = 5 + Math.floor(j/2) * (0.75 * cardHeight + 10);
			item.x = (j%2) * (0.75 * cardWidth + 10) + 5;
			j++;
		}
	}
	combatStage.update();
	stage.update();
}

function deleteCardBack(serverAction){
	var cardName = "cardBack" + serverAction.name;
	combatStage.getChildByName("unitContainer").removeChild(combatStage.getChildByName("unitContainer").getChildByName(cardName));
	combatStage.update();
}



/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addEndFrontLineTurnButton(serverAction){
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endFrontLineTurn();");
	endTurnButton.innerHTML = "Next step";
	endTurnButton.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addEndSupportLineTurnButton(serverAction){
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endSupportLineTurn();");
	endTurnButton.innerHTML = "Next step";
	endTurnButton.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addEndBattleCardTurnButton(serverAction){
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endBattleCardTurn();");
	endTurnButton.innerHTML = "Next step";
	if (!currentTurn){
		endTurnButton.disabled = true;
	}	
	
	actionMenu.appendChild(endTurnButton);
}


function addAllCheckerEvents(serverAction){
	addBattleCardToPoolEvent(stage);
	addBattleUnitToPoolEvent(stage);
	var checker = combatStage.getChildByName("checkerContainer");
	for (var i = 0; i < checker.numChildren; i++){
		var skirmish = checker.getChildAt(i);
		for (var j = 0; j < skirmish.numChildren; j++){
			if (!(skirmish.getChildAt(j).name.indexOf("Card") > -1)){
				addBattleUnitPlacementEvent(skirmish.getChildAt(j));
			}else{
				addBattleCardPlacementEvent(skirmish.getChildAt(j));
			}
		}
	}
}
//---------------------------------------------------------------------------------------
/*fin réception des messages du serveur*/

//TODO
/*foctions de dessin*/
//---------------------------------------------------------------------------------------
/**dessine l'échiquier où l'on pose les unités**/
function drawChecker(serverAction){
	//construction de l'échiquier sur le quel on pose cartes et unités
	var checker = new createjs.Container();
	checker.set({name : "checkerContainer"});
	var attackStageWidth = serverAction.attackWidth * 100;
	for (var i =0; i < serverAction.height; i++){
		//ligne d'escarmouche
		var skirmishRow = new createjs.Container();
		var skirmishName = "skirmish" +i.toString();
		skirmishRow.set({name : skirmishName});
		skirmishRow.x = 0;
		skirmishRow.y = i * cardHeight;
		
		//case de carte de support pour l'attaquant
		var supportAttackCardPlace = new createjs.Shape();
		supportAttackCardPlace.set({name : "supportAttackCardPlace"});
		supportAttackCardPlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(attackingBackGround)
		.drawRect(0, 0, cardWidth, cardHeight);
		skirmishRow.addChild(supportAttackCardPlace);
		
		//case de carte de combat pour l'attaquant
		var frontAttackCardPlace = new createjs.Shape();
		frontAttackCardPlace.set({name : "frontAttackCardPlace"});
		frontAttackCardPlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(attackingBackGround)
		.drawRect(0, 0, cardWidth, cardHeight);
		frontAttackCardPlace.x = cardWidth;
		skirmishRow.addChild(frontAttackCardPlace);
		
		if (serverAction.attackWidth > 1){
			//case de renfort pour l'attaquant
			var supportAttackPlace = new createjs.Shape();
			supportAttackPlace.set({name : "supportAttackPlace"});
			supportAttackPlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(attackingBackGround)
			.drawRect(0, 0, 100 * (serverAction.attackWidth - 1), 100);
			supportAttackPlace.x = 2 * cardWidth;
			supportAttackPlace.y =  cardHeight/2 - 50;
			skirmishRow.addChild(supportAttackPlace);
		}
		
		//case de ligne de front pour l'attaquant
		var frontAttackPlace = new createjs.Shape();
		frontAttackPlace.set({name : "frontAttackPlace"});
		frontAttackPlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(attackingBackGround)
		.drawRect(0, 0, 100, 100);
		frontAttackPlace.x = 2 *cardWidth + 100 * (serverAction.attackWidth - 1);
		frontAttackPlace.y = cardHeight/2 - 50;
		skirmishRow.addChild(frontAttackPlace);
		
		//case de ligne de front pour le défenseur
		var frontDefensePlace = new createjs.Shape();
		frontDefensePlace.set({name : "frontDefensePlace"});
		frontDefensePlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(defendingBackGround)
		.drawRect(0, 0, 100, 100);
		frontDefensePlace.x = 2 *cardWidth + 100 * serverAction.attackWidth;
		frontDefensePlace.y = cardHeight/2 - 50;
		skirmishRow.addChild(frontDefensePlace);
		
		if (serverAction.defenseWidth > 1){
			//case de renfort pour le défenseur
			var supportDefensePlace = new createjs.Shape();
			supportDefensePlace.set({name : "supportDefensePlace"});
			supportDefensePlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(defendingBackGround)
			.drawRect(0, 0, 100 * (serverAction.defenseWidth - 1), 100);
			supportDefensePlace.x = 2 *cardWidth + 100 * (serverAction.attackWidth + 1);
			supportDefensePlace.y = cardHeight/2 - 50;
			skirmishRow.addChild(supportDefensePlace);
		}
		
		//case de carte de combat pour le défenseur
		var frontDefenseCardPlace = new createjs.Shape();
		frontDefenseCardPlace.set({name : "frontDefenseCardPlace"});
		frontDefenseCardPlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(defendingBackGround)
		.drawRect(0, 0, cardWidth, cardHeight);
		frontDefenseCardPlace.x = combatStage.canvas.width - 2 * cardWidth;
		skirmishRow.addChild(frontDefenseCardPlace);
		
		//case de carte de support pour le défenseur
		var supportDefenseCardPlace = new createjs.Shape();
		supportDefenseCardPlace.set({name : "supportDefenseCardPlace"});
		supportDefenseCardPlace.graphics.beginStroke("black").setStrokeStyle(4).beginFill(defendingBackGround)
		.drawRect(0, 0, cardWidth, cardHeight);
		supportDefenseCardPlace.x = combatStage.canvas.width - cardWidth;
		skirmishRow.addChild(supportDefenseCardPlace);
		
		checker.addChild(skirmishRow);
	}
	return checker
}

function getBattleUnitCopy(unitId){
	var unitCopy;
	if (stage.getChildByName(unitId)){
		unitCopy = stage.getChildByName(unitId);
	}else if (combatStage.getChildByName("unitContainer").getChildByName(unitId)){
		unitCopy = combatStage.getChildByName("unitContainer").getChildByName(unitId);
	}else{
		unitCopy = galaxyStage.getChildByName("unitContainer").getChildByName(unitId).clone(true);
		unitCopy.scaleX = 2;
		unitCopy.scaleY = 2;
	}
	return unitCopy;
}
//---------------------------------------------------------------------------------------
/*foctions de dessin*/

//TODO
/*fonctions utilitaires*/
//---------------------------------------------------------------------------------------
/**on sélectionne ou déselectionne une unité. Si on sélectionne, on active aussi la
détection du mouseover pour montrer les actions possibles**/
function battleUnitSelection(value){
	if (value !== undefined){
		stage.enableMouseOver(10);
		combatStage.enableMouseOver(10);
		askValidBattlePlacements(value.name);
	} else {
		stage.enableMouseOver(0);
		combatStage.enableMouseOver(0);
		validBattlePlaces.length = 0;
	}
	battleUnitSelected = value;
}

function battleCardSelection(value){
	if (value !== undefined){
		stage.enableMouseOver(10);
		combatStage.enableMouseOver(10);
		askValidBattleCardPlacements(value.name);
	} else {
		stage.enableMouseOver(0);
		combatStage.enableMouseOver(0);
		validBattlePlaces.length = 0;
	}
	battleCardSelected = value;
}


/**fonction pour ajouter un objet à la liste d'objet avec évènements en évitant les doublons**/
function addBattleObjectWithEvents(object){
	if (battleObjectWithEvents.indexOf(object) == -1){
		battleObjectWithEvents.push(object);
	}
}

/**réorganise le menu d'action où les unités sont placées**/
function reDisplayUnplacedBattleUnits(){
	var j = 0;
	var k = 0;
	var yBorder = stage.getChildByName("defendingUnits").y;
	for (var i = 0; i < stage.numChildren; i++){
		var item = stage.getChildAt(i);
		if (item.name !== "attackingUnits" && item.name !== "defendingUnits"){
			if (item.y < yBorder){
				item.x = (j%3) * 100 + 10;
				item.y = Math.floor(j/3) * 100 + 10;
				j++;
			}else{
				item.x = (k%3) * 100 + 10;
				item.y = Math.floor(k/3) * 100 + 10 + yBorder;
				k++;
			}
		}
	}
	stage.update();
}


/**regarde la zone sont des endroits valides pour placer une unité**/
function isValidBattlePlace(area){
	var result = false;
	for (var i = 0; i < validBattlePlaces.length; i++){
		if (validBattlePlaces[i].battlePlace === area.name && validBattlePlaces[i].battleRow === area.parent.name){
			result=true;
			break;
		}
	}
	return result;
}
//---------------------------------------------------------------------------------------
/*fin fonctions utilitaires*/
