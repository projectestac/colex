<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexib" class="edu.xtec.colex.client.beans.ColexIndexBean" scope="request" /><%if (!colexib.init(request, response)) {%><jsp:forward page="redirect.jsp"><jsp:param name="redirectPage" value="<%=colexib.getRedirectPage()%>"/></jsp:forward><%}%>

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

            <script type="text/javascript">

                function openCollection(nameCollection)
                {
                    document.formCollection.collection.value = nameCollection;
                    document.formCollection.owner = null;
                    document.formCollection.operation.value = "showAll";
                    document.formCollection.action = "record.jsp";
                    document.formCollection.submit();
                }

                function deleteCollection(nameCollection)
                {
                    Dialog.confirm('<%=colexib.getMessage("delete.collection")%>',
                            {
                                windowParameters: {width: 350, height: 130},
                                okLabel: "sí",
                                cancelLabel: "no",
                                buttonClass: "myButtonClass",
                                id: "myDialogId",
                                cancel: function(win) {
                                    return false
                                },
                                ok: function(win) {
                                    deleteCollectionOK(win, nameCollection)
                                }
                            });
                }

                function deleteCollectionOK(win, nameCollection)
                {
                    Windows.closeAll();

                    document.formCollection.collection.value = nameCollection;
                    document.formCollection.operation.value = "delete";
                    document.formCollection.action = "index.jsp";
                    setTimeout("document.formCollection.submit()", 200);
                }

                function modifyCollection(nameCollection, isPublic)
                {
                    document.formCollection.collection.value = nameCollection;
                    document.formCollection.isPublic.value = isPublic;
                    document.formCollection.action = "structure.jsp";
                    document.formCollection.submit();
                }

                function shareCollection(owner, nameCollection)
                {
                    if (owner == null)
                    {
                        document.formCollection.collection.value = nameCollection;
                        document.formCollection.action = "share.jsp";
                        document.formCollection.submit();
                    }
                    else
                    {
                        document.formShare.owner.value = owner;
                        document.formShare.collection.value = nameCollection;
                        document.formShare.action = "share.jsp";
                        document.formShare.submit();

                    }
                }

                function openShareCollection(owner, nameCollection)
                {
                    document.formShare.operation.value = "showAll";
                    document.formShare.owner.value = owner;
                    document.formShare.collection.value = nameCollection;
                    document.formShare.action = "record.jsp";
                    document.formShare.submit();

                }

                function createCollection()
                {
                    name = TrimString(document.formCreate.newCollection.value);

                    if (name == "")
                        Dialog.alert("<%=colexib.getMessage("EMPTY_COLLECTION_NAME")%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
                    else if (!chkReservedChars(name, '<%=colexib.getJspProperties().getProperty("reserved.chars")%>'))
                        Dialog.alert('<%=colexib.getMessage("RESERVED_CHAR")%> (<%=colexib.getJspProperties().getProperty("reserved.chars")%>)', {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
                    else
                    {
                        document.formCreate.newCollection.value = name;
                        document.formCreate.submit();
                    }
                }


                function changeImport(fullName)
                {
                    fileName = fullName.match(/[^\/\\]+$/);
                    var extension = fileName[0].substring(fileName[0].indexOf(".") + 1);

                    if (extension != 'zip')
                    {
                        Dialog.alert("<%=colexib.getMessage("NO_ZIP_EXTENSION")%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});

                    }
                    else
                    {
                        var shortName = fileName[0].substring(0, fileName[0].indexOf("."));
                        document.formImport.importName.value = shortName;
                    }
                    document.formImport.fakefile.value = fullName;
                }

                function importCollection()
                {
                    fullName = document.formImport.importFile.value;
                    extension = '';

                    if (fullName != '')
                    {
                        fileName = fullName.match(/[^\/\\]+$/);
                        extension = fileName[0].substring(fileName[0].indexOf(".") + 1);
                    }

                    name = TrimString(document.formImport.importName.value);
                    if (name == "")
                        Dialog.alert("<%=colexib.getMessage("EMPTY_COLLECTION_NAME")%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
                    else if (extension != 'zip')
                        Dialog.alert("<%=colexib.getMessage("NO_ZIP_EXTENSION")%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
                    else if (!chkReservedChars(name, '<%=colexib.getJspProperties().getProperty("reserved.chars")%>'))
                        Dialog.alert('<%=colexib.getMessage("RESERVED_CHAR")%> (<%=colexib.getJspProperties().getProperty("reserved.chars")%>)', {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
                    else
                    {
                        document.formImport.importName.value = name;
                        document.formImport.submit();
                    }
                }

                function logout()
                {
                    document.formCreate.operation.value = "logout";
                    document.formCreate.action = "index.jsp";
                    document.formCreate.submit();
                }


        </script>

    </head>
    <body>

        <script>
            <%if (colexib.getFault("create") != "") {%>
            Dialog.alert("<%=colexib.getMessage(colexib.getFault("create"))%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});

            <%}%>
            <%if (colexib.getFault("delete") != "") {%>
            Dialog.alert("<%=colexib.getMessage(colexib.getFault("delete"))%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
            <%}%>
            <%if (colexib.getFault("import") != "") {%>
            Dialog.alert("<%=colexib.getMessage(colexib.getFault("import"))%>", {windowParameters: {width: 350, height: 130}, okLabel: "d&rsquo;acord"});
            <%}%>
        </script>       
        <div id="HomeScreen" class="content">

            <span id="help" >
                <a target="_blank" href="manual/credits.html" style="color:#546376;"><%=colexib.getMessage("sc.about")%></a> | <a target="_blank" href="manual/intro.html" style="color:#546376;"><%=colexib.getMessage("sc.help")%></a>
            </span>


            <div id="leftMain" class="left">
                <div id="header" >
                    <ul id="navigation">
                        <li id="register"> <a title="<%=colexib.getMessage("sc.alt.logout")%>" href="#" onclick="javascript:logout();"> <img alt="<%=colexib.getMessage("sc.alt.logout")%>" src="img/buttons/logout.png"> </img> </a></li>
                    </ul>
                </div>

                <h3> <%=colexib.getMessage("sc.my.collections")%> </h3>

                <%
                    if (colexib.retrieveCollections().size() == 0) {%>
                <div id="divWelcomeInfo" name="divWelcomeInfo" align="justify"> <%=colexib.getMessage("sc.welcome.text")%> </div>
                <%} else {
                %>

                <form name="formCollection" id="formCollection" method="POST" action="">
                    <input type="hidden" name ="operation" id="operation"></input>
                    <input type="hidden" name ="collection" id="collection"></input>
                    <input type="hidden" name ="isPublic" id="isPublic"></input>

                    <div id="divCollections">
                        <table id="tabCollections">
                            <%
                                java.util.Vector vCollections = colexib.retrieveCollections();
                                edu.xtec.colex.domain.Collection collection;
                                edu.xtec.colex.domain.Guest guest;

                                int i;
                                for (i = 0; i < colexib.retrieveOwnedCollections(); i++) {
                                    collection = (edu.xtec.colex.domain.Collection) vCollections.get(i);

                                    if (i % 2 == 0) {
                            %><tr class="odd"><%
                            } else {
                                %><tr class="even"><%
                                    }
                                %>		


                                <td class="tdListLeft"> <a title="<%=colexib.getMessage("sc.title.open")%>" href="#" onclick="javascript:openCollection('<%=colexib.escapeChars(collection.getName())%>');"> <%=collection.getName()%> </a>  </td>    
                                <td class="tdListRight">
                                    <a title="<%=colexib.getMessage("sc.title.open")%>" href="#" onclick="javascript:openCollection('<%=colexib.escapeChars(collection.getName())%>');"> <img alt="<%=colexib.getMessage("sc.title.open")%>" height="44" width="46" src="img/icons/openCollection.png"> </img> </a> 
                                    <a title="<%=colexib.getMessage("sc.title.modify")%>" href="#" onclick="javascrpt:modifyCollection('<%=colexib.escapeChars(collection.getName())%>', '<%=collection.getIsPublic()%>');"> <img alt="<%=colexib.getMessage("sc.title.modify")%>" src="img/icons/editCollection.png"> </img> </a>
                                    <a title="<%=colexib.getMessage("sc.title.delete")%>" href="#" onclick="javascript:deleteCollection('<%=colexib.escapeChars(collection.getName())%>');"> <img alt="<%=colexib.getMessage("sc.title.delete")%>" src="img/icons/deleteCollection.png"> </img> </a> 
                                    <a title="<%=colexib.getMessage("sc.title.share")%>" href="#" onclick="javascript:shareCollection(null, '<%=collection.getName()%>');"> <img alt="<%=colexib.getMessage("sc.title.share")%>" src="img/icons/shareCollection.png"> </img> </a>
                                </td>
                            </tr>

                            <%}%>


                            <%
                                int j = i;
                                for (; j < vCollections.size(); j = j + 2) {
                                    collection = (edu.xtec.colex.domain.Collection) vCollections.get(j);
                                    guest = (edu.xtec.colex.domain.Guest) vCollections.get(j + 1);

                                    if (i % 2 == 0) {
                            %><tr class="odd"><%
                            } else {
                                %><tr class="even"><%
                                    }
                                    i++;
                                %>		


                                <td class="tdListLeft">  <a title="<%=colexib.getMessage("sc.title.open")%>" href="#" onclick="javascript:openShareCollection('<%=guest.getUserId()%>', '<%=colexib.escapeChars(collection.getName())%>');"> <%=collection.getName()%> </a></td>    
                                <td class="tdListRight">

                                    <span style="vertical-align:15px;"> - <%=guest.getUserId()%> -  </span>

                                    <a title="<%=colexib.getMessage("sc.title.open")%>" href="#" onclick="javascript:openShareCollection('<%=guest.getUserId()%>', '<%=colexib.escapeChars(collection.getName())%>');"> <img alt="<%=colexib.getMessage("sc.title.open")%>" height="44" width="46" src="img/icons/openCollection.png"> </img> </a> 
                                    <a title="<%=colexib.getMessage("sc.title.share")%>" href="#" onclick="javascript:shareCollection('<%=guest.getUserId()%>', '<%=collection.getName()%>');"> <img alt="<%=colexib.getMessage("sc.title.share")%>" src="img/icons/shareCollection.png"> </img> </a>

                                </td>
                            </tr>

                            <%}%>

                        </table>
                    </div>
                </form>


                <form name="formShare" id="formShare" method="POST" action="">
                    <input type="hidden" name ="operation" id="operation"></input>
                    <input type="hidden" name ="collection" id="collection"></input>
                    <input type="hidden" name ="owner" id="owner"></input>
                </form>
            </div>

            <%}%>


        </div>

        <div id="rightUpMain" class="rightUp">
            <h4 id="welcome"> <%=colexib.getMessage("sc.welcome")%> <%=colexib.getUserId()%></h4><br>
        </div>

        <div id="rightMidMain" class="rightMid">
            <div id="divCreate">
                <form id="formCreate" name="formCreate" action="index.jsp" method="POST">   
                    <input type="hidden" name ="operation" id="operation" value="create"></input>

                    <h3> <%=colexib.getMessage("sc.create.collection")%> </h3>
                    <h4><%=colexib.getMessage("sc.name")%></h4>

                    <div><input type="text" name="newCollection" id="newCollection" maxlength="20" value="<%=colexib.getNewCollection()%>" alt="<%=colexib.getMessage("sc.alt.collectionName")%>">
                        </input>
                        <a href="#" onclick="javascript:createCollection()"> <img alt="<%=colexib.getMessage("sc.alt.create")%>" src="img/buttons/create.png"> </img> </a>
                    </div>
                </form>    
            </div>

            <div id="divImport">

                <form id="formImport" name="formImport" action="index.jsp" enctype="multipart/form-data" method="POST" >   
                    <input type="hidden" name ="operation" id="operation" value="import"></input>
                    <h3> <%=colexib.getMessage("sc.import.collection")%> </h3>
                    <h4><%=colexib.getMessage("sc.file")%></h4>


                    <div class="fileinputs">
                        <input type="file" class="file" name="importFile" id="importFile" onchange="javascript:changeImport(this.value);" alt="<%=colexib.getMessage("sc.alt.fileName")%>"/>
                        <div class="fakefile">
                            <input id="fakefile" name="fakefile">
                            <a> <img alt="<%=colexib.getMessage("sc.alt.navigate")%>" src="img/buttons/navigate.png"> </a>
                        </div>
                    </div>

                    <h4><%=colexib.getMessage("sc.name")%></h4> 
                    <div><input type="text" name="importName" id="importName" maxlength="20" value="<%=colexib.getImportName()%>" alt="<%=colexib.getMessage("sc.alt.collectionName")%>"/>
                        <a href="#" onclick="javascript:importCollection()"> <img alt="<%=colexib.getMessage("sc.alt.import")%>" src="img/buttons/import.png"> </img> </a>
                    </div>
                </form>    

            </div>
        </div>


        <div id="rightDownMain" class="rightDown" >
            <jsp:include page="browse.jsp" flush="true"/>       
        </div>

    </body>
</html>
