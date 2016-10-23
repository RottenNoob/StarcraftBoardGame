// on compte le nombre de planètes déjà placées dans le menu d'action
var placedPlanet = 0;

// ajout des boutons de placement de planète
function addPlacePlanetButtons(serverAction){
	placedPlanet = 0;
	//menu auquel on rajoute les actions possible du joueur
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "sendPlanetPositioning();");
	endTurnButton.innerHTML = "Next step";
	endTurnButton.disabled = true;
	
	//bouton de rotation de planète
	var rotateSelectedPlanet =  document.createElement("button");
	rotateSelectedPlanet.setAttribute("id", "rotateSelectedPlanet");
	rotateSelectedPlanet.setAttribute("class", "gameButton");
	rotateSelectedPlanet.setAttribute("type", "button");
	rotateSelectedPlanet.setAttribute("onclick", "rotateSelectedPlanet();");
	rotateSelectedPlanet.innerHTML = "Rotate the selected planet\'s roads";
	rotateSelectedPlanet.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
	actionMenu.appendChild(rotateSelectedPlanet);

}

function swapPlanetPossibility(planet1, planet2){
	var possible = true;
	var planet1Square = planet1.parent;
	var planet2Square = planet2.parent;
	if (planet1Square.name !== "actionMenu"){
		if (!checkValidPlanet(planet1Square.name, planet2)){
			possible = false;
		}
	}
	if (possible){
		if (planet2Square.name !== "actionMenu"){
			if (!checkValidPlanet(planet2Square.name, planet1)){
				possible = false;
			}
		}
	}
	
	return possible;
}

//affiche le choix des planètes
function printPlanetChoice(serverAction){
	setCanvasPixelHeight(15 + (310 * (placedPlanet + 1)));
	var square = new createjs.Container();
	square.set({name : "actionMenu"});
	square.set({occupied : true});
	square.x = 15;
	square.y = 15 + 310 * placedPlanet;
	var planet = drawPlanet(serverAction, 0, 0);
	//on réduit la taille des images de planètes pour améliorer la visibilité du menu d'action
	planet.scaleX = 0.75;
	planet.scaleY = 0.75;
	var squareBackground = new createjs.Shape();
	squareBackground.graphics.beginFill("#CCCCFF").beginStroke("black").drawRect(0, 0, 300, 300);
	square.addChild(squareBackground);
	square.addChild(planet);
	stage.addChild(square);
	stage.update();
	placedPlanet++;
	if (currentTurn){
		//évènement possible si on clique sur l'emplacement de planète
		square.addEventListener("click", function(event) {
			//on met la planète sélectionnée à l'emplacement si celui-ci est vide
			if (selectedPlanet !== undefined && !square.occupied){
				selectedPlanet.parent.set({occupied : false});
				if (selectedPlanet.parent.name === "actionMenu"){
					square.addChild(selectedPlanet);
					selectedPlanet.alpha =1;
					planetSelection(undefined);
				}else{
					selectedPlanet.scaleX = 0.75;
					selectedPlanet.scaleY = 0.75;
					selectedPlanet.alpha =1;
					placedGalaxyPlanetNumber = 0;
					square.addChild(selectedPlanet);
					setGalaxyPlanet(undefined);
					planetSelection(undefined);

					galaxyStage.update(event);
				}
				square.set({occupied : true});
				stage.update(event);
			}
		})
		//évènements possibles au moment d'un click sur la planète
		planet.addEventListener("click", function(event) {
			if (selectedPlanet === undefined){
				//sélection de la planète
				planet.alpha = 0.6;
				planetSelection(planet);
			} else if(selectedPlanet.name === planet.name){
				//désélection
				planetSelection(undefined);
				planet.alpha =1;
			} else if (swapPlanetPossibility(planet, selectedPlanet)){
				//change les planètes de place
				var planetSquare = planet.parent;
				var selectedPlanetSquare = selectedPlanet.parent;
				planet.alpha=1;
				selectedPlanet.alpha =1;
				if (planetSquare.name === "actionMenu"){
					selectedPlanet.scaleX = 0.75;
					selectedPlanet.scaleY = 0.75;
				}else{
					selectedPlanet.scaleX = 1;
					selectedPlanet.scaleY = 1;
					setGalaxyPlanet(selectedPlanet);
				}
				if (selectedPlanetSquare.name === "actionMenu"){
					planet.scaleX = 0.75;
					planet.scaleY = 0.75;
				}else{
					planet.scaleX = 1;
					planet.scaleY = 1;
					setGalaxyPlanet(planet);
				}
				planetSquare.addChild(selectedPlanet);
				selectedPlanetSquare.addChild(planet);
				planetSelection(undefined);
			}
			stage.update(event);
			galaxyStage.update(event);
		})

		planet.addEventListener("mouseover", function(event) {
			if (planet.name !== selectedPlanet.name){
				if (swapPlanetPossibility(planet, selectedPlanet)){
					planet.alpha = 0.5;
				}
			}
			stage.update(event);
			galaxyStage.update(event);
		})

		planet.addEventListener("mouseout", function(event) { 
			if (planet.name !== selectedPlanet.name){
				if (swapPlanetPossibility(planet, selectedPlanet)){
					planet.alpha = 1;
				}
			}
			stage.update(event);
			galaxyStage.update(event);
		})
	}
}

/**Actions du joueur**/
function sendPlanetPositioning(){
	var clientAction = {
			action : "placePlanet",
			planetName : placedGalaxyPlanet.name,
			coordinates : placedGalaxyPlanet.parent.name
	};
	webSocket.send(JSON.stringify(clientAction));
	//on enlève tous les évènements sur la planète placée pour que l'on ne puisse plus la déplacer
	resetPlacePlanetActions();
}

function rotateSelectedPlanet(){
	var clientAction = {
			action : "rotatePlanet",
			planetName : selectedPlanet.name,
	};
	webSocket.send(JSON.stringify(clientAction));
}

//on fait tourner les routes d'une planète
function rotatePlanetRoads(serverAction){
	var planetToRotate;
	for (var i = 0; i < stage.numChildren; i++){
		if (stage.getChildAt(i).name === "actionMenu"){
			if (stage.getChildAt(i).getChildByName(serverAction.name)){
				planetToRotate = stage.getChildAt(i).getChildByName(serverAction.name);
				break;
			}
		}
	}
	planetToRotate.removeChild(planetToRotate.getChildByName("roadSet"));
	//construction des routes
	var roadList = serverAction.roadList;
	buildRoads(planetToRotate, roadList);
	stage.update();
}
