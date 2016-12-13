<%@page import="java.io.IOException"%>
<%@page import="edu.xtec.colex.exception.ServerException"%>
<%@page import="edu.xtec.colex.server.DataBase"%>
<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<%
    String msg = "Aplicacio:OK";
    try {
        DataBase oDB = new DataBase();
        oDB.listTagClouds(-1).isEmpty();
        java.net.URL oURL = new java.net.URL("https://integracio.apliense.xtec.cat/e13_colex/");
        oURL.openStream();
    } catch (ServerException se) {
        msg = "ERROR: database connection failed (" + se.toString() + ")";
    } catch (IOException ioe) {
        msg = "ERROR: access to application failed (" + ioe.toString() + ")";
    }
%>

<%=msg%>	
<br>
<%="config.educacio: " + System.getProperty("config.educacio")%>
<br>
<%=System.getProperty("weblogic.Name")%>
<br>
<%="server.environment: " + System.getProperty("server.environment")%>
