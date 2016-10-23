<%@ page pageEncoding="UTF-8"%>
<div id="menu">
	<%-- Vérification de la présence d'un objet utilisateur en session --%>
	<c:choose>
		<c:when test="${!empty sessionScope.sessionUtilisateur}">
			<p class="succes">You are logged in as : ${sessionScope.sessionUtilisateur.name}</p>
			<p>
				<a class="menuButton" href="<c:url value="/deconnexion"/>">Log
					out</a>
				<c:if test="${pageContext.request.servletPath == '/accesMembre/gamePage.jsp'}">
					<a class="menuButton" onclick="saveStarcraftGame();">Save the game</a>
					<a class="menuButton" onclick="leaveStarcraftGame();" href="/starcraftBoardGame/accesMembre/gameServerList">Leave the game</a>
				</c:if>
			</p>
		</c:when>
		<c:otherwise>
			<p>
				<a class="menuButton" href="<c:url value="/inscription"/>">Register</a>
				<a class="menuButton" href="<c:url value="/connexion"/>">Log in</a>
			</p>
		</c:otherwise>
	</c:choose>
</div>