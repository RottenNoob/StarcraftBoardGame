var webSocket = new WebSocket("ws://" + location.host + "/starcraftBoardGame/gameLobbyChat");

            
webSocket.onmessage = function(event){
    var serverAction = JSON.parse(event.data);
    if (serverAction.action === "sendChat") {
    	writeResponse(serverAction);
    }
    if (serverAction.action === "showPlayer") {
    	addPlayer(serverAction);
    }
    if (serverAction.action === "startGame") {
    	startGame(serverAction);
    }
    if (serverAction.action === "clearView") {
    	clearView();
    }
    if (serverAction.action === "deleteGame") {
    	deleteGame(serverAction);
    }
    if (serverAction.action === "quitGame") {
    	quitGame(serverAction);
    }
};
 
webSocket.onclose = function(event){
};

function startGame(serverAction){
	window.location.replace("http://" + location.host + "/starcraftBoardGame/accesMembre/gamePage.jsp");
}

function deleteGame(serverAction){
	window.location.replace("http://" + location.host + "/starcraftBoardGame/accesMembre/gameServerList");
}

function quitGame(serverAction){
	window.location.replace("http://" + location.host + "/starcraftBoardGame/accesMembre/gameServerList");
}

function clearView(){
	var currentGameList = document.getElementsByClassName("player");
    while(currentGameList.length > 0){
    	currentGameList[0].parentNode.removeChild(currentGameList[0]);
    }
}

function addPlayer(serverAction){
	var userName = serverAction.playerName;
	
    var table = document.getElementById("playerList");
    var addedRow = document.createElement("p");
    addedRow.setAttribute("class", "player");
    addedRow.innerHTML = userName;
    table.appendChild(addedRow);
    
}


function sendStartGame(){
	var clientAction = {
			action : "startGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function sendDeleteGame(){
	var clientAction = {
			action : "deleteGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function sendQuitGame(){
	var clientAction = {
			action : "quitGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}