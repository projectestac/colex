<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexvb" class="edu.xtec.colex.admin.beans.ColexValidateBean" scope="request" />
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<%colexvb.isValid(request,response); %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%--

<c:if test="${param.sayHello}">

<!-- Let's welcome the user ${param.name} -->
        Hello ${param.name}!
    </c:if>
    --%>
   
   
   
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>

    <h1>JSP Page</h1>
    <form action="validate.jsp" method="post">
    
    
        <input name="user" id="user" type="text">
        <input name="pwd" id="pwd" type="password">
        
        <input type="submit">
        
    
    </form>

    
    
    
    </body>
</html>
