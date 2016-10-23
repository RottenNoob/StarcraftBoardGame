<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="<c:url value="/inc/form.css"/>" />
	<link type="text/css" rel="stylesheet" href="<c:url value="/inc/gameStyle.css"/>" />
	<title>Starcraft : the boardgame</title>
</head>
<body>
	<c:import url="/inc/menu.jsp" />
	<c:import url="/accesMembre/chatApp.jsp" />
	<p id = "playerTurnInfo"></p>
	<fieldset>
		<legend>Faction List</legend>
		<div id="factionInfo" class="xSlider">
		</div>
	</fieldset>
	<fieldset>
		<legend>Your game board</legend>
		<div id="currentPlayerBoard" class="xSlider">
		</div>
	</fieldset>
	<div id="gameBoard" class="gameBoard">
	</div>
	<div id="modalBackGround" class="modalBackGround" onClick="hideModalMenu();">
	</div>
	<div id ="infoStageContainer" class="infoStage">
	<canvas id="infoStage">
	</canvas>
	</div>
	
	
</body>




<script src="<c:url value='/js/lib/easeljs.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/gamePageStart.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/resourceHandler.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/factionChoice.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/leadershipChoice.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/galaxyScripts.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/placePlanet.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/placeZRoad.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/placeUnit.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/buildTurnHandler.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/planningPhase.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/galaxyOrderChoice.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/battleHandler.js'/>"></script>
<script src="<c:url value='/js/chatScript.js'/>"></script>
<script src="<c:url value='/js/gamePageScripts/gamePageEnd.js'/>"></script>

</html>