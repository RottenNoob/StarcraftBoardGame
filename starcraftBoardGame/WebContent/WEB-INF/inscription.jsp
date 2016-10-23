<%@ page pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Inscription</title>
        <link type="text/css" rel="stylesheet" href="<c:url value="/inc/form.css"/>" />
    </head>
    <body>
    	<c:import url="/inc/menu.jsp" />
        <form method="post" action="inscription">
            <fieldset>
                <legend>Registration</legend>
                <p>You can register a new user with the following form.</p>

                <label for="nom">Username</label>
                <input type="text" id="nom" name="nom" value="<c:out value="${utilisateur.name}"/>" size="20" maxlength="20" />
                <span class="erreur">${form.erreurs['nom']}</span>
                <br />


                <label for="motdepasse">Password <span class="requis">*</span></label>
                <input type="password" id="motdepasse" name="motdepasse" value="" size="20" maxlength="20" />
                <span class="erreur">${form.erreurs['motdepasse']}</span>
                <br />
                
                <label for="confirmation">Confirm your password <span class="requis">*</span></label>
                <input type="password" id="confirmation" name="confirmation" value="" size="20" maxlength="20" />
                <span class="erreur">${form.erreurs['confirmation']}</span>
                <br />

                <input type="submit" value="Inscription" class="sansLabel" />
                <br />
                
            </fieldset>
        </form>
    </body>
</html>