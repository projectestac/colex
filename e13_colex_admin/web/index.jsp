<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexab" class="edu.xtec.colex.admin.beans.AdminBean" scope="request" />
<%if(!colexab.init(request, response)){%><jsp:forward page="redirect.jsp">
 <jsp:param name="redirectPage" value="<%=colexab.getRedirectPage()%>"/>
</jsp:forward><%}%>
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
        <title> Col·lex - Administration </title>
    </head>
    
    <link rel="stylesheet" type="text/css" href="css/styles.css" />
    
    <script type="text/javascript" src="js/prototype-1.4.0.js"></script>
    <script type="text/javascript" src="js/ajaxFunctions.js"></script>
    
    <script type="text/javascript" src="js/calendar.js"></script>
    <script type="text/javascript" src="js/calendar-setup.js"></script>
    <script type="text/javascript" src="js/calendar-ca.js"></script>
    <link rel="stylesheet" type="text/css" href="css/calendar-blue.css" />
    
    
    <script type="text/javaScript">
    
    divOpened = null;
    
    function openModifyQuota()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formModifyQuota.reset();
        
        info = document.getElementById('modifyQuotaInfo');
        info.style.display='none';
        
        divMQ = document.getElementById("divModifyQuota");
        divMQ.style.display="block"; 
        
        divOpened = divMQ;
    }
    
    function openListUsers()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formListUsers.reset();
        clearTabListUsers();
        
        info = document.getElementById('listUsersInfo');
        info.style.display='none';
        
        divLU = document.getElementById("divListUsers");
        divLU.style.display="block"; 
        
        divOpened = divLU;
    }
    
    function openDeleteUsers()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formDeleteUsers.reset();
        
        divDC = document.getElementById("divDeleteUsers");
        divDC.style.display="block";    
        
        divOpened = divDC;
    }
    
    function deleteUsers()
    {
        document.formDeleteUsers.operation.value="deleteUsers";
        document.formDeleteUsers.submit();
    }
    
    function openListCollections()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formListCollections.reset();
        clearTabListCollections();
        
        divLC = document.getElementById("divListCollections");
        divLC.style.display="block";    
        
        divOpened = divLC;
    }
    
    function addCollection(user,collection,index)
    {
        tr = document.createElement("tr");
        
        if (index % 2 == 0) tr.setAttribute("class","even");
                    
        tdCollection = document.createElement("td");
        
        nameCollection = document.createTextNode(collection);
        
        tdCollection.appendChild(nameCollection);
        
        tdExport = document.createElement("td");
        
        linkExport = document.createElement("a");
        linkExport.setAttribute("href","#");
        linkExport.appendChild(document.createTextNode('Export'));
        
        tdExport.appendChild(linkExport);
        
        Event.observe(linkExport, 'click', function(){exportCollection(user,collection)} , false);
        
        tdDelete = document.createElement("td");
        
        linkDelete = document.createElement("a");
        linkDelete.setAttribute("href","#");
        linkDelete.appendChild(document.createTextNode('Delete'));
        
        tdDelete.appendChild(linkDelete);
        
        Event.observe(linkDelete, 'click', function(){deleteCollection(user,collection)} , false);
        
        tr.appendChild(tdCollection);
        tr.appendChild(tdExport);
        tr.appendChild(tdDelete);
        
        document.getElementById("tabBodyListCollections").appendChild(tr);
    }
    
    function addUser(user,index)
    {
        tr = document.createElement("tr");
        
        if (index % 2 == 0) tr.setAttribute("class","even");
        
        tdUser = document.createElement("td");
        
        nameUser = document.createTextNode(user);
        
        tdUser.appendChild(nameUser);
        
        tr.appendChild(tdUser);
        
        document.getElementById("tabBodyListUsers").appendChild(tr);
    }
    
    function exportCollection(user,collection)
    {
        document.formListCollections.user.value=user;
        document.formListCollections.collection.value=collection;
        document.formListCollections.operation.value="exportCollection";
        
        document.formListCollections.submit();
    }
    
    function clearTabListCollections()
    {
        tabLC = document.getElementById('tabListCollections');
        
        tabLC.removeChild(document.getElementById('tabBodyListCollections'));
        
        tbody = document.createElement('tbody');
        tbody.setAttribute('id','tabBodyListCollections');
        tbody.setAttribute('name','tabBodyListCollections');
        
        tabLC.appendChild(tbody);
    }
    
    function clearTabListUsers()
    {
        tabLU = document.getElementById('tabListUsers');
        
        tabLU.removeChild(document.getElementById('tabBodyListUsers'));
        
        tbody = document.createElement('tbody');
        tbody.setAttribute('id','tabBodyListUsers');
        tbody.setAttribute('name','tabBodyListUsers');
        
        tabLU.appendChild(tbody);
    }
    
    function openListAttachments()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formListAttachments.reset();
        clearTabListAttachments();
        
        divLAD = document.getElementById("divLADate");
        divLAD.style.display="none";  
        divLAU = document.getElementById("divLAUser");
        divLAU.style.display="none";  
        
        divLF = document.getElementById("divListAttachments");
        divLF.style.display="block";    
        
        divOpened = divLF;
    }
    
    function openListAttachmentsUser()
    {
        document.formListAttachments.reset();
        clearTabListAttachments();
        
        divLAD = document.getElementById("divLADate");
        divLAD.style.display="none";  
        
        divLAU = document.getElementById("divLAUser");
        divLAU.style.display="block";  
    }
    
    function openListAttachmentsDate()
    {
        document.formListAttachments.reset();
        clearTabListAttachments();
        
        divLAU = document.getElementById("divLAUser");
        divLAU.style.display="none";  
        
        divLAD = document.getElementById("divLADate");
        divLAD.style.display="block";  
    }
    
    function addAttachment(user,fileName, nameCollection, url, size, created,index)
    {
   
        tr = document.createElement("tr");
        if (index % 2 == 0) tr.setAttribute("class","even");
        
        tdDelete = document.createElement("td");
        
        linkDelete = document.createElement("a");
        linkDelete.setAttribute("href","#");
        
        imgDelete = document.createElement("img");
        imgDelete.setAttribute("src","img/delete.gif");

        linkDelete.appendChild(imgDelete);
        
        tdDelete.appendChild(linkDelete);
        
        Event.observe(linkDelete, 'click', function(){deleteAttachment(user,nameCollection,fileName)} , false);
        
        
        tdFile = document.createElement("td");
        
        linkFile = document.createElement("a");
        linkFile.setAttribute("href","#");
        linkFile.appendChild(document.createTextNode(fileName));
        
        
        Event.observe(linkFile, 'click', function(){window.open(url,'collex','width=600,height=400,left=600,top=300,screenX=600,screenY=300,scrollbars=yes,resizable=yes');} , false);
        
        tdFile.appendChild(linkFile);
        
        //tdSize = document.createElement("td");
        //tdSize.appendChild(document.createTextNode(size));
        
        
        tdUser = document.createElement("td");
        tdUser.appendChild(document.createTextNode(user));
        
        tdCollection = document.createElement("td");
        tdCollection.appendChild(document.createTextNode(nameCollection));
        
        tdCreated = document.createElement("td");
        tdCreated.appendChild(document.createTextNode(created));
        
        tdExport = document.createElement("td");
        
        linkExport = document.createElement("a");
        linkExport.setAttribute("href","#");
        linkExport.appendChild(document.createTextNode('Export'));
        
        tdExport.appendChild(linkExport);
        
        Event.observe(linkExport, 'click', function(){exportCollection(user,nameCollection)} , false);
        
        tr.appendChild(tdDelete);
        tr.appendChild(tdFile);
        //tr.appendChild(tdSize);
        tr.appendChild(tdCreated);
        tr.appendChild(tdUser);
        tr.appendChild(tdCollection);
        tr.appendChild(tdExport);
        
        document.getElementById("tabBodyListAttachments").appendChild(tr);       
    }
    
    function clearTabListAttachments()
    {
        tabLF = document.getElementById('tabListAttachments');
        
        tabLF.removeChild(document.getElementById('tabBodyListAttachments'));
        
        tbody = document.createElement('tbody');
        tbody.setAttribute('id','tabBodyListAttachments');
        tbody.setAttribute('name','tabBodyListAttachments');
        
        tabLF.appendChild(tbody);
    }
    
    function openDeleteFiles()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formDeleteFiles.reset();
        
        info = document.getElementById('deleteFilesInfo');
        info.style.display='none';
                
        divDF = document.getElementById("divDeleteFiles");
        divDF.style.display="block";    
        
        divOpened = divDF;
    }
    
    
    function openGetSQL()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
        
        document.formGetSQL.reset();
        
        document.getElementById('divSQLResult').innerHTML='';
        
        info = document.getElementById('getSQLInfo');
        info.style.display='none';
                
        divGS = document.getElementById("divGetSQL");
        divGS.style.display="block";    
        
        divOpened = divGS;
    }
    
    function addSQLText(text)
    {
        document.getElementById('sql').value = document.getElementById('sql').value + ' '+text+' ';
    }
    
    
    
    function closeWindow()
    {
        if (divOpened != null)
        {
            divOpened.style.display="none";
        }
    }
    
    function logout()
    {
        document.formLogout.submit();
    }
    
    </script>
    <body>

    
    <%--<h1>Col·lex Administration </h1>--%>
    
    <a href="#" onclick="javascript:closeWindow();"> <img src="img/logo.png"/> </a>
    
    <div>
        <ul>
            <li> <a href="#" onclick="javascript:openModifyQuota();"> Modify Quota </a> </li>
            <li> <a href="#" onclick="javascript:openListUsers();"> List Users </a> </li>
            <li> <a href="#" onclick="javascript:openListCollections();"> List Collections </a> </li>
            <li> <a href="#" onclick="javascript:openListAttachments();"> List Attachments </a> </li>
            <li> <a href="#" onclick="javascript:openDeleteUsers();"> Delete Users </a> </li>
            <li> <a href="#" onclick="javascript:openDeleteFiles();"> Delete Files </a> </li>
            <li> <a href="#" onclick="javascript:openGetSQL();"> SQL </a> </li>
            <li> <a href="#" onclick="javascript:openListTagClouds();"> Manage tags </a> </li>
            <li> <a href="explora.jsp" target="s_blank" > Explore files </a> </li>
            <li> <a href="#" onclick="javascript:logout();"> Logout </a> </li>
            
        </ul>
    </div>

    <div id="divWelcome" name="divWelcome" style="display:block;"class="divMain">
    

    <h4> Welcome to Col·lex Administration </h4>
    
    
    
    
    </div>
    
    
    <div id="divModifyQuota" name="divModifyQuota" class="divMain">
    <form id="formModifyQuota" name="formModifyQuota" method="post">
    <input type="hidden" id="operation" name="operation"/>
    

    <h3> Modify Quota </h3> <div id="modifyQuotaInfo" name="modifyQuotaInfo" class="divInfo"> </div>
    
    User : <input id="user" name="user" type="text" /> <a href="#" onclick="javascript:getQuota()"> Search </a>
    <br/>
    Quota : <input id="oldQuota" name="oldQuota" type="text" size="6" maxlength="6" readonly />
    <br/>
    Space Used : <input id="spaceUsed" name="spaceUsed" type="text" size="6" maxlength="6" readonly/>
    <br/>
    New Quota : <input id="newQuota" name="newQuota" type="text" size="3" maxlength="3" /> (Mb) <a href="#" style="padding-left: 100px;" onclick="javascript:modifyQuota()"> Modify Quota </a>
    <br/>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    
    </form>
    </div>
    
    
    <div id="divListUsers" name="divListUsers" class="divMain">
    <form id="formListUsers" name="formListUsers">
    <input type="hidden" id="operation" name="operation"/>
    

    <h3> List Users</h3> <div id="listUsersInfo" name="listUsersInfo" class="divInfo"> <br/> </div>
    
    
    <a href="#" onclick="javascript:listUsers()"> List </a>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    
    <table id="tabListUsers" name="tabListUsers">
    
    <tbody id="tabBodyListUsers" name="tabBodyListUsers">
    </tbody>
    
    </table>
    
    
    </form>
    </div>
    
    
    <div id="divDeleteUsers" name="divDeleteUsers" class="divMain">
    <form id="formDeleteUsers" name="formDeleteUsers" enctype="multipart/form-data" method="post" action="ServletAdmin">
    <input type="hidden" id="operation" name="operation"/>
    

    <h3> Delete Users </h3> <div id="deleteUsersInfo" name="deleteUsersInfo" class="divInfo"> <br/> </div>
    
    <h4> Introdueix el fitxer amb els usuaris a eliminar </h4>
    
    <input id="usersFile" name="usersFile" type="file"/>
    <a href="#" onclick="javascript:deleteUsers()"> Delete </a>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    
    </form>
    </div>
    
    
    <div id="divListCollections" name="divListCollections" class="divMain">
    <form id="formListCollections" name="formListCollections" method="post">
    <input type="hidden" id="operation" name="operation"/>
    <input type="hidden" id="collection" name="collection"/>
    

    <h3> List Collections </h3> <div id="listCollectionsInfo" name="listCollectionsInfo" class="divInfo"> <br/> </div>
    
        User : <input id="user" name="user" type="text" /> <a href="#" onclick="javascript:listCollections()"> Search </a>
    <br/>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    
    <table id="tabListCollections" name="tabListCollections">
    
    <tbody id="tabBodyListCollections" name="tabBodyListCollections">
    </tbody>
    
    </table>
    
    </form>
    </div>
    
    
    <div id="divListAttachments" name="divListAttachments" class="divMain">
    <form id="formListAttachments" name="formListAttachments" method="post">
    <input type="hidden" id="operation" name="operation"/>
    <input type="hidden" id="collection" name="collection"/>
    

    <h3> List Attachments </h3> <div id="listAttachmentsInfo" name="listAttachmentsInfo" class="divInfo"> <br/> </div>
    
    Search by <a href="#" onclick="javascript:openListAttachmentsUser();"> User </a> or <a href="#" onclick="javascript:openListAttachmentsDate();"> Date </a>
    <hr/>
    
    <div id="divLAUser" name="divLAUser" style="display:none;">
        User : <input id="user" name="user" type="text" /> <a href="#" onclick="javascript:listAttachmentsUser()"> Search </a>
    </div>
    <div id="divLADate" name="divLADate"  style="display:none;">
        Begin date : <input id="beginDate" name="beginDate" type="text" size="10" maxlength="10"/> <a><img id="trigBegin" name="trigBegin" src="img/date.gif"/> </a>
    
        
        


    <script type="text/javascript">
        Calendar.setup({
            inputField     :    "beginDate",      // id of the input field
            ifFormat       :    "%d/%m/%Y",       // format of the input field
            showsTime      :    true,            // will display a time selector
            button         :    "trigBegin",   // trigger for the calendar (button ID)
            singleClick    :    true,           // double-click mode
            step           :    1                // show all years in drop-down boxes (instead of every other year as default)
        });
    </script>

<br/>

    End date : <input id="endDate" name="endDate" type="text" size="10" maxlength="10"/> <a><img id="trigEnd" name="trigEnd" img src="img/date.gif"/> </a> <a href="#" onclick="javascript:listAttachmentsDate()"> Search </a>
    
    <script type="text/javascript">
        Calendar.setup({
            inputField     :    "endDate",      // id of the input field
            ifFormat       :    "%d/%m/%Y",       // format of the input field
            showsTime      :    true,            // will display a time selector
            button         :    "trigEnd",   // trigger for the calendar (button ID)
            singleClick    :    true,           // double-click mode
            step           :    1                // show all years in drop-down boxes (instead of every other year as default)
        });
    </script>
    </div>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    
    <table id="tabListAttachments" name="tabListAttachments">
    
    <tbody id="tabBodyListAttachments" name="tabBodyListAttachments">
    </tbody>
    
    </table>
    
    </form>
    </div>
    
    <div id="divDeleteFiles" name="divDeleteFiles" class="divMain">
    <form id="formDeleteFiles" name="formDeleteFiles">
    <input type="hidden" id="operation" name="operation"/>
    

    <h3> Delete Files </h3> <div id="deleteFilesInfo" name="deleteFilesInfo" class="divInfo"> <br/> </div>
    
    <h4> This action will delete all files and/or directories that have no reference in the database</h4>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    <a href="#" onclick="javascript:deleteFiles()" style="clear: both;"> Continue </a>
    
    </form>
    </div>
    
    
    
    
    <div id="divGetSQL" name="divGetSQL" class="divMain">
    <form id="formGetSQL" name="formGetSQL">
    <input type="hidden" id="operation" name="operation"/>
    

    <h3> SQL </h3> <div id="getSQLInfo" name="getSQLInfo" class="divInfo"> <br/> </div>
    
    <h4>  Enter a select query to execute on the database </h4>
    
    <a href="#"> <img class="close" onclick="javascript:closeWindow()" src="img/close.gif"/> </a>
    
    <table style="border: none; background-color: #B5DFEF;">
    <tr>
    <td>
    <textarea id="sql" name="sql" type="text" cols="70" rows="10"> </textarea> <br/>
    <a href="#" onclick="javascript:document.formGetSQL.reset();"> Clear </a>
    </td>
    
    <td>
        <ul>
            <li> <a href="#" onclick="javascript:addSQLText('select * from');"> select </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('t_user');"> t_user </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('collection');"> collection </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('fielddef');"> fileddef </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('record');"> record </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('field');"> field </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('attachment');"> attachment </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('t_admin');"> t_admin </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('guests');"> guests </a> </li>
            <li> <a href="#" onclick="javascript:addSQLText('t_log');"> t_log </a> </li>
        </ul>
    </td>
    
    </tr>
    
    </table>
<a href="#" onclick="javascript:getSQL()" style="clear: both;"> Execute query </a> <br/>
    
    <div id="divSQLResult" name="divSQLResult"> </div>
    
    </form>
    </div>
    
   
    
    <form id="formLogout" name="formLogout" action="index.jsp">
    <input type="hidden" id="operation" name="operation" value="logout"> </input>
    </form>
    
    
    </body>
        
</html>
