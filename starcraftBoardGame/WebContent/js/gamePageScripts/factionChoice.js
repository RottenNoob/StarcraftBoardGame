var imageWidth = actualWidth * 0.2;
var imageHeight = actualWidth * 0.15;

var imageWidth2 = actualWidth * 0.1;
var imageHeight2 = actualWidth * 0.075;

//affiche les différentes factions que le joueur peut choisir
function printFactionChoice(serverAction){

	var gameBoard = document.getElementById("gameBoard");
	
	var addedFactionChoice = document.createElement("div");
	addedFactionChoice.setAttribute("class", "factionChoices");
	addedFactionChoice.setAttribute("speciesName", serverAction.speciesName);
	addedFactionChoice.setAttribute("factionName", serverAction.factionName);
	addedFactionChoice.setAttribute("onclick", "sendFactionChoice(this)");
	
	var addedSpeciesInfo = document.createElement("p");
	addedSpeciesInfo.innerHTML = "Species : " + serverAction.speciesName;
	
	var addedFactionInfo = document.createElement("p");
	addedFactionInfo.innerHTML = "Faction : " + serverAction.factionName;
	
	var image = document.createElement("img");
	

	
	image.setAttribute("src", "../starcraftWebResources/factionChoices/" + serverAction.image);
	image.setAttribute("width", imageWidth);
	image.setAttribute("height", imageHeight);
	
	addedFactionChoice.appendChild(addedSpeciesInfo);
	addedFactionChoice.appendChild(addedFactionInfo);
	addedFactionChoice.appendChild(image);
	addedFactionChoice.style.backgroundColor = serverAction.factionColor;
	
	gameBoard.appendChild(addedFactionChoice);
}

// affiche les différentes factions déjà choisies
function playerFactionChoice(serverAction){
	var factionInfo = document.getElementById("factionInfo");
	
	var playerFaction = document.createElement("div");
	playerFaction.setAttribute("class", "activeFactionChoices");
	playerFaction.setAttribute("speciesName", serverAction.speciesName);
	playerFaction.setAttribute("factionName", serverAction.factionName);
	
	var playerNameInfo = document.createElement("p");
	playerNameInfo.innerHTML = "Player name : " + serverAction.playerName;
	
	var addedSpeciesInfo = document.createElement("p");
	addedSpeciesInfo.innerHTML = "Species : " + serverAction.speciesName;
	
	var addedFactionInfo = document.createElement("p");
	addedFactionInfo.innerHTML = "Faction : " + serverAction.factionName;
	
	var image = document.createElement("img");
	image.setAttribute("src", "../starcraftWebResources/factionChoices/" + serverAction.factionImage);
	image.setAttribute("width", imageWidth2);
	image.setAttribute("height", imageHeight2);
	
	playerFaction.appendChild(playerNameInfo);
	playerFaction.appendChild(addedSpeciesInfo);
	playerFaction.appendChild(addedFactionInfo);
	playerFaction.appendChild(image);
	
	
	if (serverAction.playerName === currentPlayer){
		var activeFaction = document.createElement("p");
		if (currentTurn){
			activeFaction.innerHTML = "It\'s your turn";
			playerFaction.style.background = 'green';
		}else{
			activeFaction.innerHTML = "Currently playing faction";
			playerFaction.style.background = 'red';
		}
		playerFaction.appendChild(activeFaction);
		
	}else{
		playerFaction.style.background = 'white';
	}
	
	factionInfo.appendChild(playerFaction);
}

// remplit la partie "faction" du champs d'information sur le joueur actuel
function playerFactionInfo(serverAction){
	var factionInfo = document.getElementById("currentPlayerBoard");
	
	var playerFaction = document.createElement("div");
	playerFaction.setAttribute("class", "playerInfoBoard");
	playerFaction.setAttribute("speciesName", serverAction.speciesName);
	playerFaction.setAttribute("factionName", serverAction.factionName);
	
	var addedSpeciesInfo = document.createElement("p");
	addedSpeciesInfo.innerHTML = "Species : " + serverAction.speciesName;
	
	var addedFactionInfo = document.createElement("p");
	addedFactionInfo.innerHTML = "Faction : " + serverAction.factionName;
	
	var image = document.createElement("img");
	image.setAttribute("src", "../starcraftWebResources/factionChoices/" + serverAction.factionImage);
	image.setAttribute("width", imageWidth2);
	image.setAttribute("height", imageHeight2);
	
	playerFaction.appendChild(addedSpeciesInfo);
	playerFaction.appendChild(addedFactionInfo);
	playerFaction.appendChild(image);
	
	playerFaction.style.backgroundColor = serverAction.factionColor;
	playerColor = serverAction.factionColor;
	
	factionInfo.appendChild(playerFaction);
}

/**indique le nombre de carte en mains pour le joueur et lui permet d'afficher ces cartes**/
function displayCardNumber(serverAction){
	var factionInfo = document.getElementById("currentPlayerBoard");
	
	var combatCardInfo = document.createElement("div");
	combatCardInfo.setAttribute("class", "playerInfoBoard");

	
	combatCardInfo.style.backgroundColor = serverAction.color;
	combatCardInfo.style.height = "300px";
	combatCardInfo.style.width ="200px";
	combatCardInfo.style.border = "8px #000000 solid";
	combatCardInfo.style.margin = "5px";
	combatCardInfo.style.borderRadius = "20px";
	
	combatCardInfo.setAttribute("onclick", "askCombatCardHand()");
	
	var cardText = document.createElement("div");
	cardText.style.position = "relative";
	cardText.style.top = "20%";
	cardText.style.textAlign = "center";
	cardText.style.font = "italic bold 30px arial,serif";
	cardText.innerHTML = "Combat\n Cards";
	
	var cardNumber = document.createElement("div");
	cardNumber.setAttribute("id", "combatCardNumber");
	cardNumber.style.position = "relative";
	cardNumber.style.top = "100px";
	cardNumber.style.textAlign = "center";
	cardNumber.style.font = "italic bold 60px arial,serif";
	cardNumber.innerHTML = serverAction.cardNumber;
	
	combatCardInfo.appendChild(cardText);
	combatCardInfo.appendChild(cardNumber);
	factionInfo.appendChild(combatCardInfo);
}

function updateCardNumber(serverAction){
	var cardNumber = document.getElementById("combatCardNumber");
	if (cardNumber){
		cardNumber.innerHTML = serverAction.cardNumber;
	}
}


function displayCardInHand(serverAction){
	var cardNumber = 0;
	if (modalStage.numChildren < 2){
		cardNumber = 1;
	}else{
		cardNumber = modalStage.numChildren;
	}
	var card = drawCombatCard(serverAction);
	card.y = 5;
	card.x = (cardNumber - 1) * 210 + 5;
	setModalMenuSize(cardNumber * 210, 320);
	modalStage.addChild(card);
	modalStage.update();
}

/**partie gérant les actions faites par le joueur pendant le tour**/
function sendFactionChoice(element){
	if (currentTurn){
		var clientAction = {
				action : "chooseFaction",
				speciesName: element.getAttribute("speciesName"),
				factionName: element.getAttribute("factionName")
		};
		webSocket.send(JSON.stringify(clientAction));
	}
}

function askCombatCardHand(){
	clearModalMenu();
	var clientAction = {
			action : "askCombatCardHand",
	};
	webSocket.send(JSON.stringify(clientAction));
	showModalMenu();
}

/*dessins*/
function drawHiddenCard(serverAction){
	var card = new createjs.Container();
	var backGround = new createjs.Shape();
	backGround.graphics.beginFill(serverAction.color).beginStroke("black").setStrokeStyle(8).drawRect(0, 0, cardWidth, cardHeight);
	backGround.graphics.endStroke().endFill();
	card.addChild(backGround);
	return card;
}

function drawCombatCard(serverAction){

	var card = new createjs.Container();
	card.set({name : "card"+serverAction.id});
	//image d'un point d'ordre spécial
	var backGround = new createjs.Shape();
	backGround.graphics.beginFill("#F5F5DC").beginStroke(serverAction.color).setStrokeStyle(8).drawRect(0, 0, cardWidth, cardHeight);
	backGround.graphics.endStroke().endFill();
	
	card.addChild(backGround);
	
	var imageList = serverAction.images;
	
	for(var i in imageList){
		var imageName = imageList[i].image;
		if (imageName !== ""){
			if (!(imageName in unitImages)){
				var image = new Image();
			    image.src = "../starcraftWebResources/unitImages/"+ serverAction.species + "/"+ imageName;
			    image.onload = function() {
			    	//console.log("image loaded");
			    	modalStage.update();
			    	if (stage){
			    		stage.update();
			    	}
				}
			    unitImages[imageName]=image;
			}
			var unitImage = new createjs.Bitmap(unitImages[imageName]);
			unitImage.x = 25 + i%3 * 50;
			unitImage.y = 50 + Math.floor(i/3) * 50;
			card.addChild(unitImage);
		}
		if (serverAction.maxAttack){
			//affichage des statistiques de combats si elles sont présentes
			var maxAttack = new createjs.Text(serverAction.maxAttack, "40px Arial", "black");
			maxAttack.set({
			    textAlign: 'center',
			    x: 20,
			    y: 0 
			});
			card.addChild(maxAttack);
			
			var maxDefense = new createjs.Text(serverAction.maxDefense, "40px Arial", "black");
			maxDefense.set({
			    textAlign: 'center',
			    x: 180,
			    y: 0 
			});
			card.addChild(maxDefense);
			
			var minAttack = new createjs.Text(serverAction.minAttack, "30px Arial", "black");
			minAttack.set({
			    textAlign: 'center',
			    x: 50,
			    y: 10 
			});
			card.addChild(minAttack);
			
			var minDefense = new createjs.Text(serverAction.minDefense, "30px Arial", "black");
			minDefense.set({
			    textAlign: 'center',
			    x: 150,
			    y: 10 
			});
			card.addChild(minDefense);
		}
		var cardText = new createjs.Text(serverAction.text, "20px Arial", "black");
		cardText.set({
		    textAlign: 'center',
		    x: 100,
		    y: 150,
		    lineWidth : 180
		});
		card.addChild(cardText);
		
	}
	
	
	return card;
}