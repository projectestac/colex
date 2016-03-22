<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Colex WS</title>
    </head>
    <body>

    <h2> Welcome to colex_ws </h2>
    
    <h3>In this URL there are the Servlets: </h3>
    <ul>
        <li> <a href="ServletCollection"> ServletCollection </a></li>
        <li> <a href="ServletRecord"> ServletRecord </a></li>
        <li> <a href="ServletStructure"> ServletStructure </a></li> 
        <li> <a href="ServletShare"> ServletShare </a></li> 
        <li> <a href="ServletPortal"> ServletPortal </a></li> 
    </ul>
    
    <span> Click on the links to check if they are deployed correctly </span>
    
    </body>
</html>
