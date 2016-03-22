<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexeb" class="edu.xtec.colex.client.beans.ColexErrorBean" scope="request" />
<%colexeb.init(request,response);%>
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
    </head>
    
    
    <body>

    
    
    <div id="divInternalError">
    
    <img src="img/backgrounds/error.png"> </img>
    
    <h3 id="errorMessage"> <%=colexeb.getMessage("sc.internalError")%> </h3>
    
    <a id="linkErrorHome" href="portal.jsp"> <img alt="<%=colexeb.getMessage("sc.alt.home")%>" src="img/buttons/home.png"> </img> </a>
    
    </div>
    
    
    </body>
</html>
