<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="b" class="edu.xtec.colex.admin.beans.ExploraBean" scope="request" /><%
int baseLevel=0;
if(!b.init(request, response)){%><jsp:forward page="redirect.jsp">
 <jsp:param name="redirectPage" value="<%=b.getRedirectPage()%>"/>
</jsp:forward><%}%>
<html>
<head>
<title>Exploració de carpetes - <%=b.getShortPath(baseLevel)%></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<style>

.divMain
{
display: none;
position: absolute;
top: 95px;
left: 260px;
/*height: 750px;*/
width: 700px;
background-color: #B5DFEF;
padding: 10px;

}

</style>
</head>

<body>

<a href="#" style=""> <img src="img/logo.png"> </a>

<br/>

<div id="divExploreFiles" style="display:block;"name="divExploreFiles" class="divMain">
<table width="100%">
<tr><td><h3> Fitxers al servidor </h3></td></tr>
<tr><td width="100%"><img src="img/liniadescar.gif" width="100%" height="2"></td></tr>  
<tr><td>&nbsp;</td></tr>
<tr><td><b>Contingut de la carpeta:&nbsp;<%=b.getShortPath(baseLevel)%></b></td></tr>
<tr><td width="100%"><img src="img/liniadescar.gif" width="100%" height="2"></td></tr>
<tr><td>
<table border="0" cellpadding="2" cellspacing="0">
<%
  // Atencio: Canviat el 0 per un 2 per reduir nivells de visualitzacio
  //int baseLevel=0;

  String[][] levels=b.getPathLevels();
  for(int z=baseLevel; z<levels.length; z++){
%>
<tr>
<% if(z>0){%>
<td class="fileTD" colspan="<%=z%>">&nbsp;</td>
<%}%>
<td class="fileTD"><a href="<%=levels[z][1]%>"><img src="img/openfolder.gif" width="16" height="16" align="absmiddle" hspace="3" border="0"></a></td>
<td class="fileTD" align="left" colspan="<%=levels.length-z%>"><a href="<%=levels[z][1]%>"><b><%=levels[z][0]%></b></a></td>
</tr>
<%
  }
%>
<tr>
<td class="fileTD" colspan="<%=levels.length%>">&nbsp;</td>
<td>
<table border="0" cellpadding="2" cellspacing="0" >
<%
  int k=0;
  for(int i=0; i<b.getFolders().length; i++){
    String bgColor= ((k++&1)==0 ? "#EEEEEE" : "#FFFFFF");
    java.io.File f=b.getFolders()[i];
%>
<!-- <tr bgcolor="<%=bgColor%>"> -->
 <tr>
  <td class="fileTD"><a href="<%=b.JSP_NAME%>?<%=b.P_PATH%>=<%=b.getFilePrefix()%><%=f.getName()%>"><img src="img/folder.gif" width="16" height="16" align="absmiddle" hspace="3" border="0"><%=f.getName()%></a></td>
  <td>&nbsp;&nbsp;</td>
  <td class="fileTD"><div align="right"></div></td>
  <td>&nbsp;&nbsp;</td>
  <td class="fileTD"><div align="right"><%=b.getDate(f)%></div></td>
 </tr>
<%
  } 
  for(int i=0; i<b.getFiles().length; i++){
    String bgColor= ((k++&1)==0 ? "#EEEEEE" : "#FFFFFF");
    java.io.File f=b.getFiles()[i];
%>
<!--  <tr bgcolor="<%=bgColor%>">  -->
  <tr >
    <td class="fileTD"><a href="<%=b.WEB_BASE%><%=java.io.File.separator%><%=b.getFilePrefix()%><%=f.getName()%>"><img src="img/<%=b.getIcon(f)%>" width="16" height="16" align="absmiddle" hspace="3" border="0"><%=f.getName()%></a></td>
    <td>&nbsp;&nbsp;</td>
    <td class="fileTD"><div align="right"><%=b.getFileKb(f)%> Kb</div></td>
    <td>&nbsp;&nbsp;</td>
    <td class="fileTD"><div align="right"><%=b.getDate(f)%></div></td>
  </tr>
<% 
  }
%>
</table>
</td>
</tr>
</table>
</td></tr>
<tr><td>
<table width="100%">
<tr><td colspan="2" width="100%"><img src="img/liniadescar.gif" width="100%" height="2"></td></tr>

<tr><td width="100%">
<%--
  if(b.getFiles().length>0 || b.getFolders().length>0){
--%>

<%--<a href="<%=b.getZipPath(baseLevel)%>"><img src="imatges/zip.gif" border="0" width="16" height="16" hspace="3" align="absmiddle"><b>Descarregar 
  el contingut de la carpeta "<%=b.getFolderName()%>" en un fitxer ZIP</b></a>
  --%>
<%--
  }
--%>
</td>

<%--<td align="right" onClick="javascript:window.close();"><a href="#"><img src="img/tancar.gif" width="14" height="14" border="0"></a></td>--%>
</tr></table>
</td></tr></table>
</div>
</body>
</html>
