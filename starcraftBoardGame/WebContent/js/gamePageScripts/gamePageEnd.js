/**Récupération des évènements et appel des fonctions**/
webSocket.onmessage = function(event){
    var serverAction = JSON.parse(event.data);
    if (serverAction.action === "sendChat") {
    	writeResponse(serverAction);
    } else {
    	//console.log(serverAction.action);
    	window[serverAction.action](serverAction);
    }
};

webSocket.onclose = function(event){
};

window.onload = function(e){ 
	/** ajuste la taille du plateau de jeu à la taille de l'écran**/
	/*
    var actualWidth = window.innerWidth ||
    document.documentElement.clientWidth ||
    document.body.clientWidth ||
    document.body.offsetWidth;*/
	
    /*var ratio = 16/10;
    var desiredHeight = actualWidth / ratio;*/
    document.getElementById("gameBoard").style.height = actualHeight+"px";
}