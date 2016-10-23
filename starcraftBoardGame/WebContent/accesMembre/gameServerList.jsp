<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<link type="text/css" rel="stylesheet" href="<c:url value="/inc/form.css"/>" />
	<title>Game Server Lobby</title>
</head>
<body>
<c:import url="/inc/menu.jsp" />

<c:import url="/accesMembre/chatApp.jsp" />

	<fieldset>
		<legend>Game list</legend>
		<form method="post" action="gameServerList">
		<input class="hidden" name="gameHostName" value="<c:out value='${sessionScope.sessionUtilisateur.name}'/>" />
		<button type="submit" onclick="createGame();" name="gameAction" value="createGame" >Create a game</button>
		</form>
		<button type="button" onclick="loadGame();" >Load a game</button>
			<table id="serverListTable">
                <tr>
                    <th>Host name</th>
                    <th>Number of players</th>
                    <th class="action">Join game</th>                    
                </tr>
			</table>
	</fieldset>
</body>
<script src="<c:url value='/js/gameListScript.js'/>"></script>
<script src="<c:url value='/js/chatScript.js'/>"></script>
</html>