<%@page session="false" contentType="text/html; charset=iso-8859-1"
%><jsp:useBean id="colexshb" class="edu.xtec.colex.client.beans.ColexShareBean" scope="request" /><%
if(!colexshb.init(request, response)){%><jsp:forward page="redirect.jsp">
 <jsp:param name="redirectPage" value="<%=colexshb.getRedirectPage()%>"/>
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
        
        
        <script src="js/scriptAculo/prototype.js" type="text/javascript"></script>
        <script src="js/scriptAculo/scriptaculous.js?load=effects" type="text/javascript"></script>

        
        
        
        <script type="text/javaScript"></script>
        
        <style>
            .rowSelected
            {
                background-color:red;
            }
            
        </style>
        
        <script>
        function addGuest()
        {
            guest = TrimString(document.formPermissions.guest.value);
                        
            if (guest=="") Dialog.alert("<%=colexshb.getMessage("EMPTY_GUEST_NAME")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
            else 
            {
                document.formPermissions.operation.value='add';
                document.formPermissions.submit();
            }

        }
        
        function deleteGuest(guestId)
        {
            Dialog.confirm("<%=colexshb.getMessage("delete.guest")%>", 
            {
                windowParameters: {width:350,height:130}, 
                okLabel: "sí", 
                cancelLabel: "no", 
                buttonClass: "myButtonClass", 
                id: "myDialogId",
                cancel:function(win) {return false},
                ok:function(win) {deleteGuestOK(win,guestId)} 
            });

        }
        
        function deleteGuestOK(win,guestId)
        {
            document.formPermissions.guestHidden.value=guestId;
            document.formPermissions.operation.value='delete';
            document.formPermissions.submit();
        }
        
        function modifyPermission(userId,permission)
        {
            document.formPermissions.operation.value='modifyPermission';
            document.formPermissions.guestHidden.value=userId;
                
            if (permission == 1) permission = 2;
            else permission = 1;
            
            document.formPermissions.permissionHidden.value=permission;
            document.formPermissions.submit();
        }
            
        function sendMail()
        {
            //Fer tots els checks
            
            mailTo = TrimString(document.formMail.mailTo.value);
                        
            if (mailTo=="") Dialog.alert("<%=colexshb.getMessage("EMPTY_MAIL_ADDRESS")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
            else 
            {
               document.formMail.submit();
            }
        }
            
        </script>
        
    </head>
    <body id='bodyID'>
    
    <div id="ShareScreen" class="content" style="">
    <span id="help" >
        <a target="_blank" href="manual/credits.html" style="color:#546376;"><%=colexshb.getMessage("sc.about")%></a> | <a target="_blank" href="manual/intro.html" style="color:#546376;"><%=colexshb.getMessage("sc.help")%></a>
    </span>
    <div id="leftShare" class="left">
            <div id="header">
                    <ul id="navigation">
                    <li id="home" > 
                    <a href="index.jsp"> <img alt="<%=colexshb.getMessage("sc.alt.home")%>" src="img/buttons/home.png"> </img> </a>
                    
                    </li>
                    </ul>
            </div>
    <%
    if (colexshb.getFault("get").equals("NO_EXISTS_COLLECTION"))
    {%>
        <h4 class="fault"> <%=colexshb.getMessage(colexshb.getFault("get"))%> </h4> 
    
    <%}
    else
    {%>
         <h2><%=colexshb.getCollection()%></h2>
         
         <form name="formPermissions" method="post" action="share.jsp">
         <input type="hidden" name ="collection" id="collection" value="<%=colexshb.getCollection()%>"></input>
         <input type="hidden" name ="operation" id="operation" value=""></input>
         <input type="hidden" name ="guestHidden" id="guestHidden" value=""></input>
         <input type="hidden" name ="permissionHidden" id="permissionHidden" value=""></input>  

            
            <% 
                edu.xtec.colex.domain.Guest gAux;
                java.util.Vector vGuests;
            %>            

           
                
            <h3> <%=colexshb.getMessage("sc.list.guests")%> </h3>

            
            
            <div id="divGuests">
                <table id="tabGuests" summary="<%=colexshb.getMessage("table.share.summary")%>">
                    <caption style="display:none;"><%=colexshb.getMessage("table.share.caption")%></caption> 
                    <thead style="display:none;">
                    <tr>
                    <th scope="col"> <%=colexshb.getMessage("idUser")%> </th>
                    <th scope="col"> <%=colexshb.getMessage("sc.permission")%> </th>
                    <th scope="col"> <%=colexshb.getMessage("sc.title.last.access")%> </th>
                    <th scope="col" abbr=" "> </th>
                    </tr>
                    
                    </thead>
                    <tbody>
                    <%vGuests = colexshb.retrieveGuests();

                    for (int i=0;i<vGuests.size();i++)
                    {
                        gAux = (edu.xtec.colex.domain.Guest) vGuests.get(i);
                    
                    if (i % 2 == 0)
                    {%>
                    <tr id="tr_<%=gAux.getUserId()%>" class="odd"><%
                    }
                    else
                    {%>
                    <tr id="tr_<%=gAux.getUserId()%>" class="even"><%
                    }%>	
                    <td>
                    <%=gAux.getUserId()%>
                    </td>
                    <td>
                            <% if (colexshb.getOwner()==null)
                            {%>
                                <a href="#" style="text-decoration:underline;" title="<%=colexshb.getMessage("sc.title.modify.permission")%>" onclick="javascript:modifyPermission('<%=gAux.getUserId()%>','<%=gAux.getPermission()%>');">
                            <%}
                            else {%><a><%}%>
                            
                            <%
                            if (gAux.getPermission()==gAux.PERMISSION_READ){%><%=colexshb.getMessage("PERMISSION_READ")%><%}
                            else if (gAux.getPermission()==gAux.PERMISSION_TOTAL) {%> <%=colexshb.getMessage("PERMISSION_TOTAL")%><%}%>
                            </a>
                    </td>
                    <td title="<%=colexshb.getMessage("sc.title.last.access")%>">
                    <%
                    java.util.Date dLastAccess = gAux.getLastAccess();
                    
                    if (dLastAccess != null)
                    {
                    java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");%>
                    <%=df.format(gAux.getLastAccess())%>
                    <%}
                    else
                    {%>
                    <%="-"%>
                    <%}%>
                    
                    </td>
                    
                    <% if (colexshb.getOwner()==null)
                    {%>
                    <td>
                        <a href="#" title="<%=colexshb.getMessage("sc.title.delete")%>" onclick="javascript:deleteGuest('<%=gAux.getUserId()%>');"><img alt="<%=colexshb.getMessage("sc.title.delete")%>" src="img/icons/deleteGuest.png"/></a>
                    </td>
                    <%}%>
                    </tr>

                    <%}%>
                    
                    
                    </tbody>
                </table>
            </div>
            
            <br/>
            
            
                
            </div>
            
            <%-- To add a new user --%>    
            <div id="rightUpShare" class="rightUp">
         
            <h3> <%=colexshb.getMessage("sc.add.user")%></h3>
            <div> <%=colexshb.getMessage("sc.identifier")%>  <input name="guest" id="guest" type="text"  size="8" maxlength="30" alt="<%=colexshb.getMessage("sc.alt.identifier")%>"/> </div>
            <div> <%=colexshb.getMessage("sc.permission")%> </div>
            
            <input id="permission" name="permission" type="radio" value="<%=edu.xtec.colex.domain.Guest.PERMISSION_READ%>" checked alt="<%=colexshb.getMessage("PERMISSION_READ")%>"/><span style="font-size:90%;"><%=colexshb.getMessage("PERMISSION_READ")%></span>
            <br/>
            <input id="permission" name="permission" type="radio" value="<%=edu.xtec.colex.domain.Guest.PERMISSION_TOTAL%>" alt="<%=colexshb.getMessage("PERMISSION_TOTAL")%>"/><span style="font-size:90%;"><%=colexshb.getMessage("PERMISSION_TOTAL")%></span>
            
            
            
            <br/>
          
            <div>
                <a onclick="javascript:addGuest()" href="#" style="position: absolute; right: 10px;top: 90px;"> <img alt="<%=colexshb.getMessage("sc.title.accept")%>" src="img/buttons/acceptBlue.png"/> </a>
            </div>
            
            <div id="coverRightUpShare" style="">
            </div>
            
            
            </div>
            
            
         </form>
         
         <div id="rightMidShare" class="rightMid">
            <form name="formMail" id="formMail" method="post" action="share.jsp">
                <input type="hidden" name="operation" id="operation" value="sendMail"/>
                <input type="hidden" name ="collection" id="collection" value="<%=colexshb.getCollection()%>"></input>
         
                <h3><%=colexshb.getMessage("sc.send.mail")%></h3>
                
            <h4><%=colexshb.getMessage("sc.mail.to")%></h4><input name="mailTo" id="mailTo" size="30" alt="<%=colexshb.getMessage("sc.mail.to")%>"/>
            <h4><%=colexshb.getMessage("sc.mail.from")%></h4><input name="mailFrom" id="mailFrom" size="30" readonly value="<%=colexshb.retrieveMailFrom()%>" alt="<%=colexshb.getMessage("sc.mail.from")%>"/>
            <h4><%=colexshb.getMessage("sc.mail.subject")%></h4><input name="mailSubject" id="mailSubject" size="30" value="<%=colexshb.retrieveMailSubject()%>" alt="<%=colexshb.getMessage("sc.mail.subject")%>"/>
            <h4><%=colexshb.getMessage("sc.mail.text")%></h4>
            <textarea name="mailText" id="mailText" cols="30" rows="5"><%=colexshb.retrieveMailText()%></textarea> <br/>
            <a style="display:none;" href="#" onclick="javascript:alert(document.formMail.mailLink.value)">Enllaç</a><input type="hidden" name="mailLink" id="mailLink" size="55" readonly value="<%=colexshb.retrieveMailLink()%>" /> <br/>
            
            <% if (colexshb.getOwner()!=null)
            {%>
                <input type="hidden" name="owner" id="owner" value="<%=colexshb.getOwner()%>"/>
            <%}%>
            
            </form>
            
            <div>
                <a style="position: absolute; right: 10px; top: 302px;" href="#" onclick="javascript:sendMail()"><img alt="<%=colexshb.getMessage("sc.alt.send")%>" src="img/buttons/send.png"/></a>
            </div>
            
            <div id="coverRightMidShare">  </div>
         </div>
         
         
         
         
        <div id="rightDownShare" class="rightDown">
            <form name="formLog" id="formLog" method="post" action="record.jsp">
                <input type="hidden" name="operation" id="operation" value=""/>
                <input type="hidden" name ="collection" id="collection" value="<%=colexshb.getCollection()%>"></input>
         
                <h3><%=colexshb.getMessage("sc.log")%></h3>
                
                <%
                java.util.Vector vLog;
                edu.xtec.colex.domain.LogOperation loAux;
                %>
                <div id="divLog">
                <table id="tabLog" summary="<%=colexshb.getMessage("table.log.summary")%>">
                    <caption style="display:none;"><%=colexshb.getMessage("table.log.caption")%></caption> 
                    <thead style="display:none;">
                    <tr>
                    <th scope="col"> <%=colexshb.getMessage("idUser")%> </th>
                    <th scope="col"> <%=colexshb.getMessage("operation")%> </th>
                    <th scope="col"> <%=colexshb.getMessage("sc.title.record")%> </th>
                    <th scope="col"> <%=colexshb.getMessage("date")%> </th>
                    </tr>
                    
                    </thead>
                    <tbody>
                    <%vLog = colexshb.retrieveLog();

                    for (int i=0;i<vLog.size();i++)
                    {
                        loAux = (edu.xtec.colex.domain.LogOperation) vLog.get(i);
                   
                    if (i % 2 == 0)
                    {%>
                    <tr class="odd"><%
                    }
                    else
                    {%>
                    <tr class="even"><%
                    }%>	
                    <td>
                    <%=loAux.getIdUser()%>
                    </td>
                    <td>
                    <%if (loAux.getOperation() == loAux.ADD_RECORD)
                    {%> <img alt="<%=colexshb.getMessage("sc.title.add")%>" title="<%=colexshb.getMessage("sc.title.add")%>" src="img/icons/addRecordMini.png"/> 
                    <%}
                    else if (loAux.getOperation() == loAux.DELETE_RECORD)
                    {%>
                       <img alt="<%=colexshb.getMessage("sc.title.delete")%>" title="<%=colexshb.getMessage("sc.title.delete")%>" src="img/icons/deleteRecordMini.png"/> 
                    <%}
                    else if (loAux.getOperation() == loAux.MODIFY_RECORD)
                    {%>
                       <img alt="<%=colexshb.getMessage("sc.title.modify")%>" title="<%=colexshb.getMessage("sc.title.modify")%>" src="img/icons/modifyRecordMini.png"/> 
                    <%}%> 
                    </td>
                    <td>
                    <%=colexshb.getMessage("sc.record.with")%> <%=loAux.getText()%>
                    </td>
                    <td>
                    <% java.text.DateFormat df2 = new java.text.SimpleDateFormat("dd/MM/yyyy");%>
                    <%=df2.format(loAux.getDate())%>
                    </td>
                    <td>
                    </tr>

                    <%}%>
                    </tbody>
                </table>
                </div>
                
            </form>
         
         </div>
         
         
          <script>
         <%if (colexshb.getOwner()!=null)
         {%>
            document.getElementById("coverRightUpShare").style.display='block';
         <%}%>
         
         <%if (!colexshb.isPublic())
          {%>
            document.getElementById("coverRightMidShare").style.display='block';
         <%}%>
         
         <%if (colexshb.getFault("add")!="")
         {%>
            Dialog.alert("<%=colexshb.getMessage(colexshb.getFault("add"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
         <%}%>
         
         <%if (colexshb.getFault("sendMail")!="")
         {%>
            Dialog.alert("<%=colexshb.getMessage(colexshb.getFault("sendMail"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
         <%}%>
         </script>
               
    <%}%>
            
    
    </div>
    </body>
    
    <script>
         
    
    divLog = document.getElementById('divLog');
    divLog.scrollTop = divLog.scrollHeight;
        
        
    </script>
    
    
</html>
