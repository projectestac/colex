<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexvb" class="edu.xtec.colex.admin.ColexValidateBean" scope="request" />
<%String sError = null;
    if (!colexvb.isValid(request, response)) {
        sError = colexvb.getError();
    } %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Departament d'Ensenyament. Identificaci&oacute; d'usuari/&agrave;ria personal </title>
    <link rel="shortcut icon" href="img/favicon_gencat.ico" />
    <link href="css/validate.css" type="text/css" rel="stylesheet">
</head>

<body onload="document.login.user.focus()">

<form action="validate.jsp" method="post" name="login" id="login" autocomplete="off">
    <%String url = request.getParameter("url");
        if (url == null) {
            url = "index.jsp";
        }%>
    <input type="hidden" name="url" value="<%=url%>"/>
    <%if (request.getParameter("logo") != null) {%>
    <input type="hidden" name="logo" value="<%=request.getParameter("logo")%>"/>
    <%} %>

    <!-- contenidor -->
    <table class="contenidor" cellspacing="0">

        <!-- superior -->
        <tbody><tr><td class="contenidorsup">
            <table class="superior" cellspacing="0">
                <tbody><tr class="l1">
                    <td class="sup1">
                        <a href="http://www.gencat.net/educacio/"><img src="img/login/oid_logo_departament.gif" alt="Pàgina principal del Departament d'Ensenyament"></a>
                    </td>
                    <td class="sup2">
                        <img src="img/login/oid_gencat_escut.gif" alt="Departament d'Ensenyament">
                    </td>
                </tr>
                <tr class="l2">
                    <td class="sup3">
                        <a href="http://www.gencat.net/" class="ruta">&nbsp;Inici:&nbsp;</a>
                        <a href="http://www.gencat.net/educacio" class="ruta">Departament d'Ensenyament:&nbsp;</a>
                        <span class="rutaactiu">Identificació</span>
                    </td>
                    <td class="sup4">
                        &nbsp;
                    </td>
                </tr>
                </tbody></table>
        </td></tr>
        <!-- /superior -->

        <!-- central -->
        <tr><td class="contenidormig">

            <center>
                <table class="mig" cellspacing="0">
                    <tbody>
                    <tr>
                        <td colspan="2">
                            <%
                                if (request.getParameter("logo") != null) {%>
                            <p style="float:right;"><img src="<%=request.getParameter("logo")%>"/></p>
                            <%} %>
                        </td>
                    </tr>
                    <tr>
                        <td class="mig1" colspan="2">
                            Identificació d'usuari/ària personal
                        </td>
                    </tr>
                    <tr>
                        <td class="mig2" colspan="2">
                            &nbsp;
                        </td>
                    </tr>

                    <tr>
                        <td class="mig3">
                            <table class="migcamps" cellspacing="0">
                                <tbody>
                                <tr>
                                    <td class="mig3">
                                    </td>
                                    <td class="mig7">
                                        <%if (sError != null) { %>
                                        <% if ("101".equals(sError)) { %>Cal especificar un nom d'usuari/ària
                                        <% } else if ("102".equals(sError)) {%>Cal especificar una contrasenya
                                        <% } else if ("103".equals(sError)) {%>El nom d'usuari/ària especificat és incorrecte
                                        <% } else if ("104".equals(sError)) {%>La contrasenya especificada és incorrecta
                                        <% } else if ("105".equals(sError)) {%>El nom d'usuari/ària i/o la contrasenya especificats són incorrectes
                                        <% } else if ("106".equals(sError)) {%>S'ha produït un error en intentar validar l'usuari/ària especificat. Torna a intentar-ho d'aquí a uns minuts.
                                        <% }
                                        }%>
                                    </td>
                                </tr>

                                <tr>
                                    <td class="mig3">
                                        Usuari/ària *
                                    </td>
                                    <td class="mig4">
                                        <input maxlength="30" id="user" name="user" tabindex="1" type="text">
                                    </td>
                                </tr>
                                <tr>
                                    <td class="mig3">
                                        Contrasenya *
                                    </td>
                                    <td class="mig4">
                                        <input maxlength="30" id="pwd" name="pwd" tabindex="2" type="password">
                                    </td>
                                </tr>
                                </tbody></table>
                        </td>

                    </tr>

                    <tr>
                        <td class="mig2" colspan="2">
                            &nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td class="mig5" colspan="2">
                            <input class="botons" value="Accepta" tabindex="3" type="submit">
                        </td>
                    </tr>
                    <tr>
                        <td class="mig6" colspan="2">
                            <br/>
                            <ul><li type="square">Podeu accedir a aquesta aplicació amb un identificador XTEC o <a href="http://blocs.xtec.cat">XTECBlocs</a>.</li></ul>
                        </td>
                    </tr>
                    </tbody></table>
            </center>
            <!-- central -->
        </td></tr>


        <!-- inferior -->
        <tr><td class="contenidorinf">
            <table class="inferior" cellspacing="0">
                <tbody><tr>
                    <td>
                        © Generalitat de Catalunya
                    </td>
                </tr>
                </tbody></table>
            <!-- /inferior -->
        </td></tr>
        <!-- /contenidor -->
        </tbody></table>
</form>


</body>
</html>
