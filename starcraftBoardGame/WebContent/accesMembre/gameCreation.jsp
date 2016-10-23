<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="<c:url value="/inc/form.css"/>" />
	<title>Création d'une partie</title>

</head>
<body>
<c:import url="/inc/menu.jsp" />
<c:import url="/accesMembre/chatApp.jsp" />

	<fieldset>
		<legend>Player list</legend>
			<div id="playerList">
			</div>
	</fieldset>
	<c:choose>
	 <c:when test="${sessionScope.sessionUtilisateur.name == sessionScope.hostName}">
	 	<button type="button" onclick="sendDeleteGame();" id ="deleteGameButton">Delete the game</button>
		<button type="button" onclick="sendStartGame();" id ="startGameButton">Start the game</button>
	</c:when>
	<c:otherwise>
		<button type="button" onclick="sendQuitGame();" id ="quitGameButton">Leave the game</button>
	</c:otherwise>
	</c:choose>
</body>

<script src="<c:url value='/js/gameCreationScript.js'/>"></script>
<script src="<c:url value='/js/chatScript.js'/>"></script>

</html>