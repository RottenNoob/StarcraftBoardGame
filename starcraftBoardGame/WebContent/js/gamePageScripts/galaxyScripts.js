//Ajout du dessin d'un polygone régulier
(createjs.Graphics.RegularPolygon = function(x, y, radius, edgeNumber) {
    this.x = x;
    this.y = y;
    this.radius = radius;
    this.edgeNumber = edgeNumber;
}).prototype.exec = function(ctx) {
	ctx.moveTo(this.x + this.radius, this.y);

	for (i = 1; i < this.edgeNumber + 1; i++){
		var angle = (Math.PI*2/this.edgeNumber) * i;
		ctx.lineTo(this.x + this.radius*Math.cos(angle), this.y+this.radius*Math.sin(angle));
	}
}

createjs.Graphics.prototype.drawRegularPolygon = function(x, y, radius, edgeNumber){
	return this.append(new createjs.Graphics.RegularPolygon(x, y, radius, edgeNumber));
}

/** description des graphismes utilisés**/
//largeur des routes
var roadSize = 80;
//image de la ressource minérale de starcraft
var mineralImage = new createjs.Bitmap("../starcraftWebResources/mapResources/mineral.png");
//image de la ressource gaz de starcraft
var gasImage = new createjs.Bitmap("../starcraftWebResources/mapResources/gas.png");
//image d'un point de conquête
var conquestImage = new createjs.Shape();
conquestImage.graphics.beginFill("Red").drawCircle(0, 0, 20);
//image d'un point d'ordre spécial
var specialImage = new createjs.Shape();
specialImage.graphics.beginFill("yellow").beginStroke("black").drawRegularPolygon(0, 0, 20, 6);
//couleur de tous les textes
var flyingBackgroundColor = "#68838B";
var allBackgroundColor = "#556B2F";
var groundBackgroundColor = "#943E0F";

//informations sur le plateau
/**liste des objets ayant des events**/
var objectWithEvents = [];
/**liste des objets avec des highlight**/
var highlightList = [];
var selectedPlanet;
var galaxyBackground = new createjs.Bitmap("../starcraftWebResources/mapResources/starBackground.jpg");
var galaxyStage;
var stage;
var combatStage;
var placedGalaxyPlanetNumber = 0;
var placedGalaxyPlanet;
var validSquares = [];
var galaxyCanvasSizes = {
		xMin : -1,
		yMin : -1,
		width : 3,
		height : 3
};

/**fonction pour ajouter un objet à la liste d'objet avec évènements en évitant les doublons**/
function addObjectWithEvents(object){
	if (objectWithEvents.indexOf(object) == -1){
		objectWithEvents.push(object);
	}
}

function clearObjectEvents(serverAction){
	clearEventAndHighlight();
}

/**enlève les évènements et les indications correspondantes**/
function clearEventAndHighlight(){
	for (var i = 0; i < objectWithEvents.length; i++){
		objectWithEvents[i].removeAllEventListeners();
	}
	objectWithEvents.length = 0;
	for (var j = 0; j < highlightList.length; j++){
		var roadSet = highlightList[j].parent
		highlightList[j].parent.removeChild(highlightList[j]);
		
	}
	highlightList.length = 0;
}

/**on enlève tous éléments du canvas montrant les action possibles**/
function clearActionStage(){
	stage.removeAllChildren();
	stage.update();
}

//on indique quelle planète est actuellement placée
function setGalaxyPlanet(value){
	if (value !== undefined){
		document.getElementById("endTurnButton").disabled = false;
	} else {
		document.getElementById("endTurnButton").disabled = true;
	}
	placedGalaxyPlanet = value;
}

//on sélectionne ou d"selectionne une planète. Si on sélectionne, on active aussi la
//détection du mouseover pour montrer les actions possibles
function planetSelection(value){
	if (value !== undefined){
		stage.enableMouseOver(10);
		galaxyStage.enableMouseOver(10);
		if (value.parent.name === "actionMenu"){
			document.getElementById("rotateSelectedPlanet").disabled = false;
		}
		
	} else {
		stage.enableMouseOver(0);
		galaxyStage.enableMouseOver(0);
		document.getElementById("rotateSelectedPlanet").disabled = true;
	}
	selectedPlanet = value;
}

//on réinitialise les variables servant au placement des planètes
//on elève aussi les évènements permettant au joueur d'agir
function resetPlacePlanetActions(){
	stage.enableMouseOver(0);
	galaxyStage.enableMouseOver(0);
	placedGalaxyPlanet.removeAllEventListeners();
	for (var i = 0; i < galaxyStage.numChildren; i++){
		galaxyStage.getChildAt(i).removeAllEventListeners();
		if (galaxyStage.getChildAt(i).getChildByName("highlight")){
			galaxyStage.getChildAt(i).removeChild(galaxyStage.getChildAt(i).getChildByName("highlight"));
		}
	}
	placedGalaxyPlanetNumber = 0;
	setGalaxyPlanet(undefined);
}

/**modification de la hateur du canvas en fonction du nombre d'éléments standards (de taille 80*80)à placer**/
function changeCanvasHeight(nbElements){
	setCanvasPixelHeight((Math.floor(nbElements/3) + 1)* 100);
}

function changeCanvasHeight2(nbElements1, nbElements2){
	stage.canvas.height = Math.floor((nbElements1+2)/3) * 100 + (Math.floor(nbElements2/3) + 1)* 100;
	var backGround;
	if (stage.getChildByName("backGround")){
		backGround = stage.getChildByName("backGround");
		backGround.graphics.clear();
	}else{
		backGround = new createjs.Shape();
		backGround.set({name : "backGround"});
	}
	backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, 330, Math.floor((nbElements1+2)/3) * 100);
	stage.addChild(backGround);
	stage.setChildIndex( backGround, 0);
	
	var backGround2;
	if (stage.getChildByName("backGround2")){
		backGround2 = stage.getChildByName("backGround2");
		backGround2.graphics.clear();
	}else{
		backGround2 = new createjs.Shape();
		backGround2.set({name : "backGround2"});
	}
	backGround2.graphics.beginFill("#CDE7F0").drawRect(0, Math.floor((nbElements1+2)/3) * 100, 330, (Math.floor(nbElements2/3) + 1)* 100);
	stage.addChild(backGround2);
	stage.setChildIndex( backGround2, 0);
}

function setCanvasPixelHeight(pixels){
	stage.canvas.height = pixels;
	var backGround;
	if (stage.getChildByName("backGround")){
		backGround = stage.getChildByName("backGround");
		backGround.graphics.clear();
		backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, 330, pixels);
	}else{
		backGround = new createjs.Shape();
		backGround.set({name : "backGround"});
		backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, 330,pixels);
		
	}
	stage.addChild(backGround);
	stage.setChildIndex( backGround, 0);
}


//on ajoute le canvas du menus d'action
function addActionCanvas(serverAction){
	placedPlanet = 0;
	//menu auquel on rajoute les actions possible du joueur
	var actionMenu = document.getElementById("canvasContainer");
	
	//canvas
	var canvas = document.createElement("canvas");
	canvas.setAttribute("id", "actionCanvas");
	canvas.setAttribute("class", "actionCanvas");
	
	actionMenu.appendChild(canvas);
	
	stage = new createjs.Stage("actionCanvas");
	
	stage.canvas.width = 330;
	stage.canvas.height = 100;
	var backGround = new createjs.Shape();
	backGround.set({name : "backGround"});
	backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, 330, 1000);
	stage.addChild(backGround);
}

// on ajoute le canvas de la galaxie ainsi que les autres canvas statiques (modal canvas et combat canvas)
function addGalaxyCanvas(serverAction){	
	
	var gameBoard = document.getElementById("galaxyBoard");
	
	var canvas = document.createElement("canvas");
	canvas.setAttribute("id", serverAction.name);
	canvas.setAttribute("class", "galaxysCanvas");	
	gameBoard.appendChild(canvas);
	
	galaxyStage = new createjs.Stage(serverAction.name);
	var unitContainer = new createjs.Container();
	unitContainer.set({name : "unitContainer"});
	galaxyStage.addChild(unitContainer);
	galaxyStage.update();
	
	var combatCanvas = document.createElement("canvas");
	combatCanvas.setAttribute("id", "combatCanvas");
	combatCanvas.setAttribute("class", "galaxysCanvas");
	combatCanvas.style.display = "none";
	gameBoard.appendChild(combatCanvas);
	combatStage = new createjs.Stage("combatCanvas");
}

/**fonction alternant entre l'écran de combat et l'écran de galaxie**/
function setCombatMode(isTrue){
	var combatCanvas = document.getElementById("combatCanvas");
	var galaxyCanvas = document.getElementById("galaxyCanvas");
	if (isTrue){
		combatCanvas.style.display = "block";
		galaxyCanvas.style.display = "none";
	}else{
		combatCanvas.style.display = "none";
		galaxyCanvas.style.display = "block";
	}
}

//on vérifie si la planète sélectionnée a les routes nécessaires pour être placée sur une case
function checkValidPlanet(squareName, planet){
	var valid = false;
	var roads = planet.getChildByName("roadSet");
	var i = 0;
	while  ( i < validSquares.length &&  !valid) {
	    if (validSquares[i].coordinates === squareName){
	    	if (roads.getChildByName(validSquares[i].road)){
	    		valid = true;
	    	}
	    }
	    i++
	}
	return valid;
}

//vérifie si la case fait partie des cases où l'on peut potentiellement placer une planète
function checkValidSquare(squareName){
	var valid = false;
	var i = 0;
	while  ( i < validSquares.length &&  !valid) {
	    if (validSquares[i].coordinates === squareName){
	    	valid = true;
	    }
	    i++
	}
	return valid;
}

//vérifie si on peut placer une planète en particulier sur la case
function isValidSquare(square, planet){
	var result;
	//vérifie qu'il y a bien une planète sélectionnée et que cette planète n'a pas été placée aux tours précédents
	if (planet !== undefined && !square.occupied){
		if (placedGalaxyPlanetNumber < 1){
			if (checkValidPlanet(square.name, planet)){
				result = true;
			}else{
				result = false;
			}
		}else if(planet.name === placedGalaxyPlanet.name  && checkValidPlanet(square.name, planet)){
			result = true;
		}else{
			result = false;
		}
	}else{
		result = false;
	}
	return result;
}

//(re)dessine la galaxie, il faut penser à enlever les cases non utilisées
function updateGalaxyDrawing(){
	var j = 0;
	var end = galaxyStage.numChildren;
	while (j < end){
		var galaxyElementName = galaxyStage.getChildAt(j).name;
		if (galaxyElementName.indexOf(".") !== -1){
			var index = galaxyElementName.indexOf(".");
			var x =  parseInt(galaxyElementName.substring(0, index));
			var y =  parseInt(galaxyElementName.substring(index+1));
			if (x < galaxyCanvasSizes.xMin || x > galaxyCanvasSizes.xMin + galaxyCanvasSizes.width - 1
					|| (y < galaxyCanvasSizes.yMin) || (y > galaxyCanvasSizes.yMin + galaxyCanvasSizes.height - 1)){
				galaxyStage.removeChildAt(j);
				j--;
				end--;
			}
		}
		j++;
	}

	for (var i = 0; i < galaxyCanvasSizes.width; i++){
		for (j = 0; j < galaxyCanvasSizes.height; j++){
			var xCoord = i + galaxyCanvasSizes.xMin;
			var yCoord = j + galaxyCanvasSizes.yMin;
			var square = drawSquare(i, j, xCoord, yCoord);
			if (square){
				galaxyStage.addChild(square);
			}else{
				// si la case existe existe déjà, on la déplace où il faut pour gérer les agrandissement de canvas
				var coordName = xCoord + "." + yCoord;
				
				var currentSquare = galaxyStage.getChildByName(coordName);
				currentSquare.x = i * 400;
				currentSquare.y = j * 400;
			}
		}
	}
	galaxyStage.update();
}


//ajoute les cases manquantes et les évènements correspondants
function setValidPlacements(serverAction){
	validSquares.length = 0;
	var coordinates = serverAction.coordinates;
	for(var k in coordinates){
		var connexion  = {
				coordinates: coordinates[k].coordinate,
				road: coordinates[k].road
				};
		validSquares.push(connexion);
	}
	for (var i = 0; i < galaxyCanvasSizes.width; i++){
		for (var j = 0; j < galaxyCanvasSizes.height; j++){
			var xCoord = i + galaxyCanvasSizes.xMin;
			var yCoord = j + galaxyCanvasSizes.yMin;
			var coordName = xCoord + "." + yCoord;
			var currentSquare = galaxyStage.getChildByName(coordName);
			currentSquare.x = i * 400;
			currentSquare.y = j * 400;
			addSquareEvent(currentSquare);
			galaxyStage.update();
		}
	}
}


//on affiche aussi les cases à l'extérieur des planètes, donc les xMin et yMin sont donc inférieurs de 1 aux valeurs
//de la galaxie correspondante et les largeur et longueur sont augmentées de 2
function updateGalaxySize(serverAction){
	galaxyCanvasSizes.xMin = serverAction.minX - 1;
	galaxyCanvasSizes.yMin = serverAction.minY - 1;
	galaxyCanvasSizes.width = serverAction.width + 2;
	galaxyCanvasSizes.height = serverAction.length + 2;
	galaxyStage.canvas.width = 400 * galaxyCanvasSizes.width;
	galaxyStage.canvas.height = 400 * galaxyCanvasSizes.height;
	updateGalaxyDrawing();
}

//on dessine la galaxie à la fin de la période de placement des galaxies
function resizeGalaxy(serverAction){
	galaxyCanvasSizes.xMin = serverAction.minX;
	galaxyCanvasSizes.yMin = serverAction.minY;
	galaxyCanvasSizes.width = serverAction.width;
	galaxyCanvasSizes.height = serverAction.length;
	galaxyStage.canvas.width = 400 * galaxyCanvasSizes.width;
	galaxyStage.canvas.height = 400 * galaxyCanvasSizes.height;
	updateGalaxyDrawing();
}



function addSquareEvent(square){
	// il n'est possible d'interagir avec une case que que si celle-ci est valide et si c'est le tour du joueur
	if (checkValidSquare(square.name) && currentTurn){
		var highlight = new createjs.Shape();
		highlight.set({name : "highlight"});
		highlight.graphics.beginStroke("green").setStrokeStyle(3).drawRect(0, 0, 400, 400);
		square.addChild(highlight);
		square.addEventListener("mouseover", function(event) { 
			if (isValidSquare(square, selectedPlanet)){
				square.alpha = 0.5;
			}
			galaxyStage.update(event);
		})

		square.addEventListener("mouseout", function(event) { 
			if (isValidSquare(square, selectedPlanet)){
				square.alpha = 1;
			}
			galaxyStage.update(event);
		})

		square.addEventListener("click", function(event) { 
			if (isValidSquare(square, selectedPlanet)){
				setGalaxyPlanet(selectedPlanet);
				
				square.set({occupied : true});
				//sélection de la planète
				selectedPlanet.alpha = 1;
				selectedPlanet.scaleX = 1;
				selectedPlanet.scaleY = 1;
				selectedPlanet.x = 0;
				selectedPlanet.y = 0;
				selectedPlanet.parent.set({occupied : false});
				square.addChild(selectedPlanet);
				planetSelection(undefined);
				square.alpha = 1;
				placedGalaxyPlanetNumber++;
				stage.update(event);
				galaxyStage.update(event);
			}
		})

	}
}

function drawSquare(x, y, xCoord, yCoord){
	var coordName = xCoord + "." + yCoord;

	//ne dessine la case que si elle n'existe pas encore
	if (!galaxyStage.getChildByName(coordName)){
		var square = new createjs.Container();
		square.set({name : coordName});

		square.x = x * 400;
		square.y = y * 400;
		var resourceImage = galaxyBackground.clone();
		square.addChild(resourceImage);

		
		return square;
	}
}

//construit les routes
function buildRoads(planet, roadList){
	//construction des routes
	//on met les routes et les zones dans leur propre container pour faliciter leur récupération
	var roadSet = new createjs.Container();
	roadSet.set({name : "roadSet"});
	for(var i2 in roadList){
		var roadPosition = roadList[i2].number;
		var xRoad = 200 + (200 - roadSize/2) * Math.cos((Math.PI/2) * (roadPosition - 1));//on commence par le nord
		var yRoad = 200 + (200 - roadSize/2) * Math.sin((Math.PI/2) * (roadPosition - 1));
		var road = new createjs.Shape();
		road.set({name : roadPosition});
		road.set({placedUnits : []});
		road.graphics.beginFill("#236B8E").beginStroke("black").drawRect(xRoad - roadSize/2, yRoad - roadSize/2, roadSize, roadSize);
		roadSet.addChild(road);
	}
	planet.addChildAt(roadSet, 0);
}

//dessine une planète
function drawPlanet(serverAction, x, y){
	var unitPlaceColor = "#b7b700";
	var planet = new createjs.Container();
	planet.set({name : serverAction.name});
	planet.set({type : "planet"});
	planet.x = x;
	planet.y = y;
	//ajout du texte du nom de la planète
	var planetNameText = new createjs.Text(planet.name, "30px Arial", textOutlineColor);
	var planetNameText2 = new createjs.Text(planet.name, "30px Arial", textColor);
	planetNameText.set({
	    textAlign: 'center',
	    x: 200,
	    y: 350 
	});
	planetNameText2.set({
	    textAlign: planetNameText.textAlign,
	    x: planetNameText.x,
	    y: planetNameText.y 
	});
	planetNameText.outline = 1.3;
	
	//construction des routes
	var roadList = serverAction.roadList;
	buildRoads(planet, roadList);

	//pour chaque zones de la planète
	var areaSet = new createjs.Container();
	areaSet.set({name : "areaSet"});
	var areaList = serverAction.areaList;
	var areaCount = areaList.length;
	planet.addChild(areaSet);
	for(var i in areaList)
	{
		var angle1 = (Math.PI*2/areaCount) * (i-1);
		var angle2 = (Math.PI*2/areaCount) * i;
		var areaNode = areaList[i];
		//création des bordures de la zone
		var area = new createjs.Shape();
		
		var areaType = areaNode.areaType;
		var unitLimit = areaNode.unitLimit;
		//image d'une place occupée par une unitée
		var unitPlaceImage = new createjs.Shape();
		var areaBackground;
		if (areaType === "flying"){
			//unités volantes
			areaBackground = flyingBackgroundColor;
			unitPlaceImage.graphics.beginFill('black').setStrokeStyle(4).beginStroke(unitPlaceColor).drawCircle(0, 0, 20);
		} else if (areaType==="ground"){
			//unités terrestres
			areaBackground = groundBackgroundColor;
			unitPlaceImage.graphics.beginFill(unitPlaceColor).setStrokeStyle(4).beginStroke('black').drawCircle(0, 0, 20);
		} else {
			//toutes les unités
			areaBackground = allBackgroundColor;
			unitPlaceImage.graphics.beginFill('black').setStrokeStyle(4).beginStroke(unitPlaceColor).drawCircle(0, 0, 20);
			unitPlaceImage.graphics.setStrokeStyle(4).beginStroke(unitPlaceColor).drawCircle(0, 0, 13);
			unitPlaceImage.graphics.setStrokeStyle(4).beginStroke(unitPlaceColor).drawCircle(0, 0, 7);
		}
		
		area.graphics.beginStroke("black").beginFill(areaBackground);
		var xAreaCenter = 200 + 120 *  Math.cos((angle1 + angle2)/2);
		var yAreaCenter = 200 + 120 *  Math.sin((angle1 + angle2)/2);
		area.graphics.moveTo(200 + 175 * Math.cos(angle2), 200+ 175 * Math.sin(angle2));
		area.graphics.lineTo(200, 200);
		area.graphics.lineTo(200 + 175 * Math.cos(angle1), 200+ 175 * Math.sin(angle1));
		area.graphics.arc(200, 200, 175, angle1, angle2);
		area.graphics.endStroke().endFill();
		area.set({name : areaNode.id});
		area.set({position : i});
		area.set({type : areaType});
		//tableau des places d'unités occupées
		area.set({placedUnits : []});
		areaSet.addChild(area);
		
		
		for(var k = 0; k < unitLimit; k++){
			var unitPlace = unitPlaceImage.clone();
			if (k < 3){
				unitPlace.x =200 + 50 *  Math.cos((angle1 + angle2)/2) + k * 50 * Math.cos(angle1);
				unitPlace.y = 200 + 50 *  Math.sin((angle1 + angle2)/2) + k * 50 * Math.sin(angle1);
			} else {
				unitPlace.x =200 + 50 *  Math.cos((angle1 + angle2)/2) + (k - 2) * 50 * Math.cos(angle2);
				unitPlace.y = 200 + 50 *  Math.sin((angle1 + angle2)/2) + (k - 2) * 50 * Math.sin(angle2);
			}
			planet.addChild(unitPlace);
		}
		
		//remplissage de la zone
		var resourceList = serverAction.areaList[i].resources;
		for (var j in resourceList){
			var resourceType = resourceList[j].resourceType;
			if (resourceType !== "special"){
				var textResource = new createjs.Text(resourceList[j].amount, "30px Arial", textOutlineColor);
				var textResource2 = new createjs.Text(resourceList[j].amount, "30px Arial", textColor);
				textResource.outline = 1.3;
			}
			var resourceImage;
			//ajout des ressources de la zone
			var xPlaceTranslation = 45 * j * Math.cos(angle1);
			var yPlaceTranslation = 45 * j * Math.sin(angle1);
			if (resourceType === "mineral"){
				resourceImage = mineralImage.clone();
				resourceImage.x = xAreaCenter + xPlaceTranslation - 20;
				resourceImage.y = yAreaCenter + yPlaceTranslation - 20;
				textResource.x = textResource2.x =resourceImage.x + 10;
				textResource.y = textResource2.y =resourceImage.y;
			}else if (resourceType === "gas"){
				resourceImage = gasImage.clone();
				resourceImage.x = xAreaCenter + xPlaceTranslation - 20;
				resourceImage.y = yAreaCenter + yPlaceTranslation - 20;
				textResource.x = textResource2.x =resourceImage.x + 10;
				textResource.y = textResource2.y =resourceImage.y;
			} else if (resourceType === "conquest") {
				resourceImage = conquestImage.clone();
				resourceImage.x = xAreaCenter + xPlaceTranslation;
				resourceImage.y = yAreaCenter + yPlaceTranslation;
				textResource.x = textResource2.x =resourceImage.x - 7;
				textResource.y = textResource2.y =resourceImage.y - 15;
			} else {
				resourceImage = specialImage.clone();
				resourceImage.x = xAreaCenter + xPlaceTranslation;
				resourceImage.y = yAreaCenter + yPlaceTranslation;
			}
			
			planet.addChild(resourceImage);

			
			if (resourceType !== "special"){
				planet.addChild(textResource2);
				planet.addChild(textResource);
			}
			
		}
	}
	planet.addChild(planetNameText2);
	planet.addChild(planetNameText);
	return planet;
}

function printPlanetGalaxy(serverAction){
	var coordName = serverAction.xPosition + "." + serverAction.yPosition;
	var square = galaxyStage.getChildByName(coordName);
	
	if (!square.occupied){
		var planet = drawPlanet(serverAction, 0, 0);
		square.set({occupied : true});
		square.addChild(planet);
		galaxyStage.update();
	}
}