//imprime une carte de leadership
function printLeadershipCard(serverAction, className){
	var addedLeadershipChoice = document.createElement("card");
	addedLeadershipChoice.setAttribute("class", className);
	addedLeadershipChoice.setAttribute("cardName", serverAction.cardName);
	
	var title = document.createElement("cardTitle");
	title.innerHTML = serverAction.cardName;
	
	var mainText = document.createElement("cardText");
	mainText.innerHTML = serverAction.text;
	
	
	if (serverAction.hasOwnProperty("itemList")){
		var itemList = serverAction.itemList;
		var unitText = document.createElement("bottomText");
		unitText.innerHTML = "Starting units :";
		for(var i in itemList)
		{
			unitText.innerHTML +=  ' '+ itemList[i].number + ' ' + itemList[i].name ;
			if (i!=itemList.length - 1){
				unitText.innerHTML += ',';
			}
		}
		addedLeadershipChoice.appendChild(unitText);
	}
	addedLeadershipChoice.appendChild(title);
	addedLeadershipChoice.appendChild(mainText);
	
	return addedLeadershipChoice;
}

//affiche les différentes cartes de leadership que le joueur pourrait choisir
function printLeadershipChoice(serverAction){
	var gameBoard = document.getElementById("actionMenu");
	var leadershipCard = printLeadershipCard(serverAction, "leadershipChoice");
	leadershipCard.setAttribute("onclick", "sendLeadershipChoice(this)");
	gameBoard.appendChild(leadershipCard);
}

//affiche la carte de leadership choisie
function playerLeadershipInfo(serverAction){
	var gameBoard = document.getElementById("currentPlayerBoard");
	var leadershipCard = printLeadershipCard(serverAction, "playerInfoBoard");
	gameBoard.appendChild(leadershipCard);
}

/**partie gérant les actions faites par le joueur pendant le tour**/
//envoie le choix de carte de leadership fait par le joueur au serveur
function sendLeadershipChoice(leadershipCard){
	if (currentTurn){
		var clientAction = {
				action : "chooseLeadership",
				cardName : leadershipCard.getAttribute("cardName")
		};
		webSocket.send(JSON.stringify(clientAction));
	}
}