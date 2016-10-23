var webSocket = new WebSocket("ws://" + location.host + "/starcraftBoardGame/serverLobbyChat");;

  
webSocket.onmessage = function(event){
    var serverAction = JSON.parse(event.data);
    if (serverAction.action === "sendChat") {
    	writeResponse(serverAction);
    } else if (serverAction.action === "createGame") {
    	addGame(serverAction);
    } else if (serverAction.action === "clearView") {
    	clearView();
    }
    else if (serverAction.action === "rejoinGame") {
    	rejoinGame();
    }
};
 
webSocket.onclose = function(event){
};

function rejoinGame(serverAction){
	window.location.replace("http://" + location.host + "/starcraftBoardGame/accesMembre/gamePage.jsp");
}

function createGame(){
	var clientAction = {
			action : "createGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function loadGame(){
	var clientAction = {
			action : "loadGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function joinGame(host){
	var clientAction = {
			action : "joinGame",
			hostName : host,
	};
	webSocket.send(JSON.stringify(clientAction));
}

function addGame(serverAction){
	var userName = serverAction.hostName;
	
    var table = document.getElementById("serverListTable");
    var addedRow = document.createElement("tr");
    addedRow.setAttribute("class", "game");
    table.appendChild(addedRow);
    
    
    var hostName = document.createElement("td");
    hostName.innerHTML = userName;
    addedRow.appendChild(hostName);
    
    var playerNumber = document.createElement("td");
    playerNumber.innerHTML = serverAction.playerNumber;
    addedRow.appendChild(playerNumber);

    /** création du bouton pour joindre une partie**/
    var joinAction = document.createElement("td");
    
    
    var joinForm = document.createElement("form");
    joinForm.setAttribute("method", "post");
    joinForm.setAttribute("action", "gameServerList");
    
    var inputInfo = document.createElement("input");
    inputInfo.setAttribute("class", "hidden");
    inputInfo.setAttribute("name", "gameHostName");
    inputInfo.setAttribute("value", userName);
    
    var submitButton = document.createElement("button");
    submitButton.setAttribute("type", "submit");
    submitButton.setAttribute("name", "gameAction");
    submitButton.setAttribute("value", "joinGame");
    submitButton.setAttribute("onclick", "joinGame(\"" + userName + "\")");
    
    submitButton.innerHTML = "Join the game";
    
    joinForm.appendChild(inputInfo);
    joinForm.appendChild(submitButton);
    joinAction.appendChild(joinForm);
    /** création du bouton pour joindre une partie**/
    addedRow.appendChild(joinAction);
}

function clearView(){
	var currentGameList = document.getElementsByClassName("game");
    while(currentGameList.length > 0){
    	currentGameList[0].parentNode.removeChild(currentGameList[0]);
    }
}