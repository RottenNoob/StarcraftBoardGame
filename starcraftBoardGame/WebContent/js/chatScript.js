/**Ces scripts ne peuevent être appelés qu'après le chargment des scripts de page**/

document.getElementById('messageinput').onkeypress = function(e){
    if (!e) e = window.event;
    var keyCode = e.keyCode || e.which;
    if (keyCode == '13'){
      send();
      return false;
    }
}

/**
* Sends the value of the text input to the server
 */
function send(){
	var text = document.getElementById("messageinput").value;
	sendToAll(text);
	document.getElementById("messageinput").value = "";
}

function sendToAll(message){
	var clientAction = {
			action : "sendChat",
			message: message
	};
	webSocket.send(JSON.stringify(clientAction));
}


function writeResponse(serverAction){
	var messages = document.getElementById("messages");
	messages.innerHTML += serverAction.message +"&#13;";
	messages.scrollTop=messages.scrollHeight;
}