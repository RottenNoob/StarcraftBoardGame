<%@page pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Connexion</title>
        <link type="text/css" rel="stylesheet" href="<c:url value="/inc/form.css"/>" />
    </head>
    <body>
    	<c:import url="/inc/menu.jsp" />
        <form method="post" action="<c:url value="/connexion" />">
            <fieldset>
                <legend>Log in</legend>

                <label for="nom">Username</label>
                <input type="text" id="nom" name="nom" value="<c:out value="${utilisateur.name}"/>" size="20" maxlength="20" />
                <span class="erreur">${form.erreurs['nom']}</span>
                <br />


                <label for="motdepasse">Password <span class="requis">*</span></label>
                <input type="password" id="motdepasse" name="motdepasse" value="" size="20" maxlength="20" />
                <span class="erreur">${form.erreurs['motdepasse']}</span>
                <br />
                

                <input type="submit" value="Log in" class="sansLabel" />
                <br />
                
                <p class="${empty form.erreurs ? 'succes' : 'erreur'}">${form.resultat}</p>
            </fieldset>
        </form>
    </body>
</html>