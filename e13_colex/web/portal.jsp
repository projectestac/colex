<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexpb" class="edu.xtec.colex.client.beans.ColexPortalBean" scope="request" />
<%if (!colexpb.init(request, response)) {%>
<jsp:forward page="redirect.jsp"> 
    <jsp:param name="redirectPage" value="<%=colexpb.getRedirectPage()%>"/>
</jsp:forward><%}%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Col·lex : col·leccions en xarxa</title>

        <link rel="shortcut icon" href="img/favicon.ico" />
        <link rel="stylesheet" type="text/css" href="styles.css" />

        <!--[if lt IE 7.]>
            <script defer type="text/javascript" src="js/pngfix.js"></script>
        <![endif]-->

        <script type="text/javascript" src="js/disableEnter.js"></script>

        <script type="text/javascript" src="js/prototype.js"></script>
        <script type="text/javascript" src="js/window.js"></script>
        <script type="text/javascript" src="js/effects.js"></script>
        <link rel="stylesheet" type="text/css" href="css/default.css"/>    
        <link rel="stylesheet" type="text/css" href="css/alert.css"/>

        <script src="js/check.js"></script>

    </head>
    <body>

        <div id="HomeScreen" class="content" style="background: url(img/backgrounds/bg5.png);background-repeat:no-repeat;">

            <span id="help" >
                <a target="_blank" href="manual/credits.html" style="color:#546376;"><%=colexpb.getMessage("sc.about")%></a> | <a target="_blank" href="manual/intro.html" style="color:#546376;"><%=colexpb.getMessage("sc.help")%></a>
            </span>

            <div id="leftMain" class="left">
                <div id="header" >
                    <ul id="navigation">
                        <li id="register"> <a href="index.jsp"> <img src="img/buttons/login.png"> </img> </a></li>
                    </ul>
                </div>

                <h3> <%=colexpb.getMessage("sc.welcome.colex")%> </h3>

                <div id="divWelcomeInfo" name="divWelcomeInfo"> 
                    <p align=""> <%=colexpb.getMessage("sc.welcome.text.1")%> </p>
                    <p align="">  <%=colexpb.getMessage("sc.welcome.text.2")%> </p>

                </div>


            </div>

            <div id="rightMidMain" class="rightMid" style="height: 260px; top: 48px;">

                <h3 style="margin-left:5px"> <%=colexpb.getMessage("sc.example.collections")%> </h3>

                <div id="divExamples">

                    <table id="tabExamples">
                        <tr>
                            <td class="tdListLeft"><a href="record.jsp?invite=owner=example$$collection=Animals" title="<%=colexpb.getMessage("sc.title.open")%>"> Animals </a></td>
                            <td class="tdListRight"><a href="record.jsp?invite=owner=example$$collection=Animals" title="<%=colexpb.getMessage("sc.title.open")%>"> <img style="position:relative;" src="img/examples/animals.png"></img> </a></td>
                        </tr>
                        <tr>
                            <td class="tdListLeft"><a href="record.jsp?invite=owner=example$$collection=Els%20Planetes" title="<%=colexpb.getMessage("sc.title.open")%>"> Els Planetes </a></td>
                            <td class="tdListRight"><a href="record.jsp?invite=owner=example$$collection=Els%20Planetes" title="<%=colexpb.getMessage("sc.title.open")%>"> <img style="position:relative;" src="img/examples/planets.png"></img> </a></td>
                        </tr>
                        <tr>
                            <td class="tdListLeft"><a href="record.jsp?invite=owner=example$$collection=M%C3%BAsics" title="<%=colexpb.getMessage("sc.title.open")%>"> M&uacute;sics </a></td>
                            <td class="tdListRight"><a href="record.jsp?invite=owner=example$$collection=M%C3%BAsics" title="<%=colexpb.getMessage("sc.title.open")%>"> <img style="position:relative;" src="img/examples/musicians.png"></img> </a></td>
                        </tr>

                    </table>   
                </div>
            </div>

            <div id="rightDownMain" class="rightDown" >
                <jsp:include page="browse.jsp" flush="true"/>   
            </div>

    </body>


</html>
