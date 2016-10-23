var webSocket = new WebSocket("ws://" + location.host + "/starcraftBoardGame/gamePageLobby");
var currentTurn = false;
var currentPlayer = "";
var playerColor = "";
var startTimer;
var endTimer;
var unitImages = [];
var modalStage = new createjs.Stage("infoStage");

var cardHeight = 300;
var cardWidth = 200;
//taille de la fenêtre en pixels
var actualWidth = window.innerWidth ||
document.documentElement.clientWidth ||
document.body.clientWidth ||
document.body.offsetWidth;

var actualHeight = window.innerHeight ||
document.documentElement.clientHeight ||
document.body.clientHeight ||
document.body.offsetHeight;

//couleur des textes
var textOutlineColor = "#7A5230";
var textColor = "#BDBDBD";

function cloneImage(object, image) {
    imageClone = new Image();
    imageClone.src = image.src;
    object.appendChild(imageClone);
}

function setBackGround(object, color){
	object.style.background = color;
}

function setModaBackground(width, length){
modalStage.removeChild(modalStage.getChildByName("backGround"));
var backGround;
backGround = new createjs.Shape();
backGround.set({name : "backGround"});
backGround.graphics.beginFill("#CBFDCB").drawRect(0, 0, width,length);
modalStage.addChild(backGround);
modalStage.setChildIndex( backGround, 0);
}

function startTimeMeasure(serverAction){
	startTimer = new Date().getTime();
}

function endTimeMeasure(serverAction){
	endTimer = new Date().getTime();
	var time = endTimer - startTimer;
	console.log(time);
}

function clearModalMenu(){
	for (var i = 0; i < modalStage.numChildren; i++){
		if (modalStage.getChildAt(i).name !== "backGround"){
			modalStage.removeChildAt(i);
			i--;
		}
	}
}

function clearViewByClass(serverAction){
	var currentGameList = document.getElementsByClassName(serverAction.name);
    while(currentGameList.length > 0){
    	currentGameList[0].parentNode.removeChild(currentGameList[0]);
    }
}


function deleteElement(serverAction){
	var element = document.evaluate( serverAction.xpath ,document.body , null, XPathResult.FIRST_ORDERED_NODE_TYPE, null ).singleNodeValue;
	if (element != null) {
	  element.parentNode.removeChild(element);
	}
}

function playerTurnUpdate(serverAction){
	var playerTurnInfo = document.getElementById("playerTurnInfo");
	playerTurnInfo.innerHTML = serverAction.message;
	currentPlayer = serverAction.currentPlayer;
    if (serverAction.isCurrentPlayerTurn === "true") {
    	currentTurn = true;
    } else {
    	currentTurn = false;
    }
}

function setModalMenuSize(width, height){
	if (width !== modalStage.canvas.width || height !== modalStage.canvas.height){
		var infoStage = document.getElementById("infoStageContainer");
		modalStage.canvas.width = width;
		modalStage.canvas.height = height;
		setModaBackground(width, height);
		if (actualWidth*0.8 <width){
			infoStage.style.width = actualWidth*0.8 +"px";
		}else{
			infoStage.style.width = width + "px";
		}
	}
}

/****/
function hideModalMenu(){
	var modalBackGround = document.getElementById("modalBackGround");
	var infoStage = document.getElementById("infoStageContainer");
	modalBackGround.style.display = "none";
	infoStage.style.display = "none";
}

/****/
function showModalMenu(){
	var modalBackGround = document.getElementById("modalBackGround");
	var infoStage = document.getElementById("infoStageContainer");
	modalBackGround.style.display = "block";
	infoStage.style.display = "block";
}

//fonction faisant apparaitre le menu des actions et la le plateau de la galaxie où on place planètes et unités
function updateBoardView(serverAction){
	
	var gameBoard = document.getElementById("gameBoard");
	var boardHeight = parseInt(gameBoard.offsetHeight, 10);
	var boardWidth = parseInt(gameBoard.offsetWidth, 10);
	
	var actionMenu = document.createElement("div");
	actionMenu.setAttribute("id", "actionMenu");
	actionMenu.setAttribute("class", "actionMenu");
	var actionMenuHeight = boardHeight - 20;
	actionMenu.style.height = actionMenuHeight + "px";
	
	gameBoard.appendChild(actionMenu);
	
	var actionMenuWidth = parseInt(actionMenu.offsetWidth, 10);
	
	var galaxyBoard = document.createElement("div");
	galaxyBoard.setAttribute("id", "galaxyBoard");
	galaxyBoard.setAttribute("class", "galaxyBoard");
	var galaxyBoardWidth = boardWidth - 30 - actionMenuWidth;
	var galaxyBoardHeight = boardHeight - 20 ;
	galaxyBoard.style.width = galaxyBoardWidth + "px";
	galaxyBoard.style.height = galaxyBoardHeight + "px";
	
	
	gameBoard.appendChild(galaxyBoard);
}

//ajoute les éléments où l'on place les boutons d'action et le canvas
//cela permet d'avoir une mise en page consistante
function upgradeActionMenu(serverAction){

	//menu d'action
	var actionMenu = document.getElementById("actionMenu");
	var actionMenuHeight = parseInt(actionMenu.offsetHeight, 10);
	var actionMenuWidth = parseInt(actionMenu.offsetWidth, 10);
	
	var buttonContainerHeight = 40;
	var canvasContainerHeight = actionMenuHeight - 50 ;
	var canvasContainerWidth = actionMenuWidth - 10 ;
	
	//bouton de fin de tour
	var buttonContainer =  document.createElement("div");
	buttonContainer.setAttribute("id", "buttonContainer");
	buttonContainer.setAttribute("class", "buttonContainer");
	buttonContainer.style.width = actionMenuWidth + "px";
	buttonContainer.style.height = buttonContainerHeight + "px";

	
	//bouton de rotation de planète
	var canvasContainer =  document.createElement("div");
	canvasContainer.setAttribute("id", "canvasContainer");
	canvasContainer.setAttribute("class", "canvasContainer");
	canvasContainer.style.width = canvasContainerWidth + "px";
	canvasContainer.style.height = canvasContainerHeight + "px";
	
	actionMenu.appendChild(buttonContainer);
	actionMenu.appendChild(canvasContainer);

}

function leaveStarcraftGame(){
	var clientAction = {
			action : "leaveStarcraftGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function saveStarcraftGame(){
	var clientAction = {
			action : "saveStarcraftGame",
	};
	webSocket.send(JSON.stringify(clientAction));
}

function leaveGame(serverAction){
	window.location.replace("http://" + location.host + "/starcraftBoardGame/accesMembre/gameServerList");
}


