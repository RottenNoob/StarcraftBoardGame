var selectedRoad1;
var selectedRoad2;
var roadColors = ["#9A32CD", "#9CCB19", "#FC1501", "#67E6EC", "#FFFF00", "#FFA500"];
var placedRoads = 0;

/*envoi des actions du joueur*/
function endRoadPlacementTurn(){
	clearEventAndHighlight();
	if (selectedRoad1 === undefined || selectedRoad2 === undefined){
		if (currentTurn){
			var clientAction = {
					action : "endRoadPlacementTurn2",
			};
			webSocket.send(JSON.stringify(clientAction));
			document.getElementById("endTurnButton").disabled = true;
			galaxyStage.enableMouseOver(0);
			selectedRoad1 = undefined;
			selectedRoad2 = undefined;
		}
	}else{
		selectedRoad1.alpha = 1;
		selectedRoad2.alpha = 1;
		galaxyStage.update();
		if (currentTurn){
			var clientAction = {
					action : "endRoadPlacementTurn",
					coordinates1 : selectedRoad1.parent.parent.parent.name,
					roadPosition1 : selectedRoad1.name.toString(),
					coordinates2 : selectedRoad2.parent.parent.parent.name,
					roadPosition2 : selectedRoad2.name.toString()
			};
			webSocket.send(JSON.stringify(clientAction));
			document.getElementById("endTurnButton").disabled = true;
			galaxyStage.enableMouseOver(0);
			selectedRoad1 = undefined;
			selectedRoad2 = undefined;
		}
	}
	
}

/**envoie le positionnement d'une des routes quand une seule route est sélectionnée**/
function sendRoadPlacement(road){
	clearEventAndHighlight();
	var clientAction = {
			action : "sendRoadPlacement",
			coordinates : road.parent.parent.parent.name,
			roadPosition : road.name.toString()
	};
	webSocket.send(JSON.stringify(clientAction));
	document.getElementById("endTurnButton").disabled = true;
}

/**envoie le positionnement d'une des routes quand aucune route n'est sélectionnée**/
function askAllRoadPlacements(){
	clearEventAndHighlight();
	var clientAction = {
			action : "askAllRoadPlacements",
	};
	webSocket.send(JSON.stringify(clientAction));
}


/*fin envoi des actions du joueur*/


/*ajout d'évènements*/

function addRoadEvent(road){
	// il n'est possible d'interagir avec une case que que si celle-ci est valide et si c'est le tour du joueur
	if (currentTurn){
		addObjectWithEvents(road);
		var roadPosition = road.name;
		var xRoad = 200 + (200 - roadSize/2) * Math.cos((Math.PI/2) * (roadPosition - 1));
		var yRoad = 200 + (200 - roadSize/2) * Math.sin((Math.PI/2) * (roadPosition - 1));
		var highlight = new createjs.Shape();
		highlight.set({name : "highlight"});
		highlight.graphics.beginFill("green").drawRect(xRoad - roadSize/2, yRoad - roadSize/2, roadSize, roadSize);
		highlight.alpha = 0.7;
		road.parent.addChild(highlight);
		highlightList.push(highlight);
		galaxyStage.update();
		road.addEventListener("mouseover", function(event) { 
			if (road !== selectedRoad1 && road !== selectedRoad2){
				road.alpha = 0.5;
				galaxyStage.update(event);
			}
		})
		road.addEventListener("mouseout", function(event) { 
			if (road !== selectedRoad1 && road !== selectedRoad2){
				road.alpha = 1;
				galaxyStage.update(event);
			}
		})
		road.addEventListener("click", function(event){
			selectRoad(road);
		})
	}
}

/*fin ajout d'évènements*/

/*réception des messages du serveur*/
/****/
function displayLink(serverAction){
	var galaxyElementName1 = serverAction.coordinates1;
	var road1 = galaxyStage.getChildByName(galaxyElementName1).getChildAt(1)
	.getChildByName("roadSet").getChildByName(serverAction.roadPosition1);
	
	var galaxyElementName2 = serverAction.coordinates2;
	var road2 = galaxyStage.getChildByName(galaxyElementName2).getChildAt(1)
	.getChildByName("roadSet").getChildByName(serverAction.roadPosition2);
	
	drawLink(road1, road2);
	
}


/**ajoute les évènements aux routes à activer, si il n'y en n'a pas, on peut passer au tour suivant**/
function activateValidRoads(serverAction){
	galaxyStage.enableMouseOver(10);
	var roadList = serverAction.roadList;
	if (roadList.length > 0){
		for(var i in roadList){
			var galaxyElementName = roadList[i].coordinates;
			var road = galaxyStage.getChildByName(galaxyElementName).getChildAt(1)
			.getChildByName("roadSet").getChildByName(roadList[i].roadPosition);
			addRoadEvent(road);
		}
	}else{
		document.getElementById("endTurnButton").disabled = false;
	}
	
}

/**ajoute les boutons correspondant aux actions possibles pendant cette étape**/
function addPlaceZRoadButtons(serverAction){
	
	var actionMenu = document.getElementById("buttonContainer");
	
	//bouton de fin de tour
	var endTurnButton =  document.createElement("button");
	endTurnButton.setAttribute("id", "endTurnButton");
	endTurnButton.setAttribute("class", "gameButton");
	endTurnButton.setAttribute("type", "button");
	endTurnButton.setAttribute("onclick", "endRoadPlacementTurn();");
	endTurnButton.innerHTML = "Next step";
	endTurnButton.disabled = true;
	
	actionMenu.appendChild(endTurnButton);
}

/*fin réception des messages du serveur*/

/*fonctions utilitaires*/
/**les seules routes avec lesquelles on peut intéragir quand deux routes sont sélectionnées sont ces dernières**/
function restrictEventToSelectedRoads(){
	clearEventAndHighlight();
	document.getElementById("endTurnButton").disabled = false;
	addRoadEvent(selectedRoad1);
	addRoadEvent(selectedRoad2);
}

function selectRoad(road){
	if (selectedRoad1 === undefined){
		if (selectedRoad2 === undefined){
			selectedRoad1 = road;
			road.alpha = 0.5;
			sendRoadPlacement(road);
		}else{
			if (selectedRoad2 === road){
				selectedRoad2.alpha = 1;
				selectedRoad2 = undefined;
				askAllRoadPlacements();
			}else{
				selectedRoad1 = road;
				road.alpha = 0.5;
				restrictEventToSelectedRoads();
			}
		}
	}else{
		if (selectedRoad1 === road){
			selectedRoad1.alpha = 1;
			selectedRoad1 = undefined;
			if (selectedRoad2 === undefined){
				askAllRoadPlacements();
			}else{
				sendRoadPlacement(selectedRoad2);
			}
		}else{
			if (selectedRoad2 === undefined){
				selectedRoad2 = road;
				road.alpha = 0.5;
				restrictEventToSelectedRoads();
			}else{
				if (selectedRoad2 === road){
					selectedRoad2.alpha = 1;
					selectedRoad2 = undefined;
					sendRoadPlacement(selectedRoad1);
				}else{
					console.log("erreur");
				}
			}
		}
	}
}

/*fonction de dessin*/
/**dessine le lien entre 2 routes**/
function drawLink(road1, road2){
	var roadPosition1 = road1.name;
	var xRoad1 = 200 + (200 - roadSize/2) * Math.cos((Math.PI/2) * (roadPosition1 - 1));
	var yRoad1 = 200 + (200 - roadSize/2) * Math.sin((Math.PI/2) * (roadPosition1 - 1));
	road1.graphics.beginStroke(roadColors[placedRoads]).setStrokeStyle(12)
	.drawRect(xRoad1 - roadSize/2, yRoad1 - roadSize/2, roadSize, roadSize);
	
	var roadPosition2 = road2.name;
	var xRoad2 = 200 + (200 - roadSize/2) * Math.cos((Math.PI/2) * (roadPosition2 - 1));
	var yRoad2 = 200 + (200 - roadSize/2) * Math.sin((Math.PI/2) * (roadPosition2 - 1));
	road2.graphics.beginStroke(roadColors[placedRoads]).setStrokeStyle(12)
	.drawRect(xRoad2 - roadSize/2, yRoad2 - roadSize/2, roadSize, roadSize);
	galaxyStage.update();
	placedRoads++;
}
