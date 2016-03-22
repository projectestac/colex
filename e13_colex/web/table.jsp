<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colextb" class="edu.xtec.colex.client.beans.ColexTableBean" scope="request" /><%
    if (!colextb.init(request, response)) {%><jsp:forward page="redirect.jsp">
    <jsp:param name="redirectPage" value="<%=colextb.getRedirectPage()%>"/>
</jsp:forward><%}%>

<%String scriptEnd = "function moveImages() {";%>

<%--<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Col·lex : col·leccions en xarxa</title>

        <link rel="shortcut icon" href="img/favicon.ico" />
        <link rel="stylesheet" type="text/css" href="styles.css" />
        <link rel="stylesheet" type="text/css" media="print" href="css/print.css" />

        <script type="text/javascript" src="js/disableEnter.js"></script>

        <script type="text/javascript" src="js/prototype.js"></script>
        <script type="text/javascript" src="js/window.js"></script>
        <script type="text/javascript" src="js/effects.js"></script>
        <link rel="stylesheet" type="text/css" href="css/default.css" />    
        <link rel="stylesheet" type="text/css" href="css/alert.css" />

        <link type="text/css" rel="stylesheet" href="css/columnlist.css" />
        <script type="text/javascript" src="js/sortabletable.js"></script>
        <script type="text/javascript" src="js/columnlist.js"></script>

        <script src="js/links.js"></script>


        <!--[if lt IE 7.]>
            <script defer type="text/javascript" src="js/pngfix.js"></script>
        <![endif]-->

        <script>



            function closeTabRecords()
            {

            <%if (colextb.getNumFound() == 0) {%>document.formCollection.begin.value = 0;<%} else {%>document.formCollection.begin.value = table.selectedRows[0];<%}%>

                    document.formCollection.operation.value = "search";
                    document.formCollection.action = "record.jsp"
                    document.formCollection.submit();
                }

                function openRecord(index)
                {
                    document.formCollection.begin.value = index;
                    document.formCollection.operation.value = "search";
                    document.formCollection.action = "record.jsp"
                    document.formCollection.submit();
                }

                function sortTable(fieldName, direction)
                {
                    document.formCollection.begin.value = 0;
                    document.formCollection.orderField.value = fieldName;
                    document.formCollection.direction.value = direction;
                    document.formCollection.operation.value = "search";
                    document.formCollection.submit();
                }

                var divOpened = null;

                function showImage(e, imageId)
                {
                    if (divOpened != null)
                    {
                        divOpened.style.display = "none";
                    }
                    img = document.getElementById(imageId);

                    divShow = document.getElementById('divShow_' + imageId);


                    divShow.style.left = (Event.pointerX(e) + 5) + 'px';
                    divShow.style.top = (Event.pointerY(e) - 5) + 'px';
                    divShow.style.display = "block";

                    divOpened = divShow;
                }

                function hideImage(imageId)
                {
                    divShow = document.getElementById('divShow_' + imageId);
                    divShow.style.display = "none";
                    divOpened = null;
                }
                function getProportion(iWidth, iHeight, iMaxWidth, iMaxHeight)
                {
                    var propH = 1;
                    var propW = 1;

                    if (iWidth > iMaxWidth)
                    {
                        propW = iWidth / iMaxWidth;
                    }

                    if (iHeight > iMaxHeight)
                    {
                        propH = iHeight / iMaxHeight;
                    }

                    if (propH > propW)
                    {
                        return propH;
                    }
                    else
                        return propW;
                }

                vIdRecords = new Array(<%=colextb.getNumFound()%>);
                //Vector to translate the position of the Record to the real idRecord



                function deleteRecord()
                {
                    Dialog.confirm("<%=colextb.getMessage("delete.record")%>",
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
                                    deleteRecordOK(win)
                                }
                            });
                }

                function deleteRecordOK(win)
                {
                    document.formCollection.idRecord.value = vIdRecords[table.selectedRows[0]];
                    document.formCollection.operation.value = "delete";
                    document.formCollection.action = "table.jsp"
                    document.formCollection.submit();
                }

                function modifyRecord()
                {

                    document.formCollection.begin.value = table.selectedRows[0];
                    document.formCollection.operation.value = "search";
                    document.formCollection.subOperation.value = "modify";
                    document.formCollection.action = "record.jsp"
                    document.formCollection.submit();
                }

                function addRecord()
                {
                    var begin = 0;

                    if (table.selectedRows[0] != undefined)
                        begin = table.selectedRows[0];

                    document.formCollection.begin.value = begin;
                    document.formCollection.operation.value = "search";
                    document.formCollection.subOperation.value = "add";
                    document.formCollection.action = "record.jsp"
                    document.formCollection.submit();
                }




        </script>
    </head>
    <body onload="moveImages();">

        <div id="RecordTable" class="content" >
            <span id="help" >
                <a target="_blank" href="manual/credits.html" style="color:#546376;"><%=colextb.getMessage("sc.about")%></a> | <a target="_blank" href="manual/intro.html" style="color:#546376;"><%=colextb.getMessage("sc.help")%></a>
            </span>
            <form id="formCollection" name="formCollection" action="table.jsp" enctype="multipart/form-data" method="post" >


                <div id="leftRecordTable" class="left">

                    <div id="header">
                        <ul id="navigation">
                            <li id="home"> 

                                <%if (colextb.getUserId() == null) {%>    
                                <a href="portal.jsp">
                                    <%} else {%>
                                    <a href="index.jsp"> 
                                        <%}%>
                                        <img alt="<%=colextb.getMessage("sc.alt.home")%>" src="img/buttons/home.png"> </img> </a>
                            </li>
                        </ul>
                    </div>
                    <%
        if (colextb.getFault("search").equals("NO_EXISTS_COLLECTION")) {%>
                    <h4 id="divMessage" name="divMessage" class="fault"> <%=colextb.getMessage(colextb.getFault("search"))%> </h4>    
                    <%} else {
                    %>
                    <h2> <%=colextb.getCollection()%> </h2>
                    <% if (colextb.getOwner() != null) {%><span style="color:#8A268A;font-size:12px;"> - <%=colextb.getOwner()%> -</span><br/><%}%>
                    <span id="numRecords"> <%=colextb.getMessage("sc.num.records")%> <%=colextb.getNumRecords()%> </span>

                    <% if (colextb.getFault("get").equals("NO_FIELDS_DEFS") || (colextb.getFault("get").equals("NO_PUBLIC_COLLECTION"))) {%>
                    <h4 id="divMessage" name="divMessage" class="fault"> <%=colextb.getMessage(colextb.getFault("get"))%> </h4>
                    <%} else {%>
                    <input type="hidden" name ="operation" id="operation"/>
                    <input type="hidden" name ="subOperation" id="subOperation"/>
                    <input type="hidden" name="collection" id="colllection" value="<%=colextb.getCollection()%>"/>
                    <input type="hidden" name="numRecords" id="numRecords" value="<%=colextb.getNumRecords()%>"/>
                    <%edu.xtec.colex.domain.Query query = colextb.getQuery();%>
                    <input type="hidden" name="begin" id="begin" value="<%=query.getBeginIndex()%>"/>
                    <input type="hidden" name="orderField" id="orderField" value="<%=query.getOrderField()%>">
                    <input type="hidden" name="direction" id="direction" value="<%=query.getDirection()%>">
                    <input type="hidden" name="idRecord" id="idRecord"/>

                    <% if (colextb.getOwner() != null) {%>
                    <input type="hidden" name="owner" id="owner" value="<%=colextb.getOwner()%>"/>
                    <%}%>

                    <%
                        java.util.Vector vConditions = query.getConditions();
                        edu.xtec.colex.domain.Condition cond;
                        for (int i = 0; i < vConditions.size(); i++) {
                    cond = (edu.xtec.colex.domain.Condition) vConditions.get(i);%>
                    <input type="hidden" name="op_<%=i%>" value="<%=cond.getOperator()%>"/>
                    <input type="hidden" name="name_<%=i%>" value="<%=cond.getFieldName()%>"/>
                    <input type="hidden" name="cmp_<%=i%>" value="<%=cond.getComparator()%>"/>
                    <input type="hidden" name="value_<%=i%>" value="<%=cond.getValue()%>"/>

                    <%}%>
            </form>


            <div id="itemsRecord" >
                <a id="linkAdd" name="linkAdd" href="#" title="<%=colextb.getMessage("sc.title.add")%>" onclick="javascript:addRecord()"> <img alt="<%=colextb.getMessage("sc.title.add")%>" src="img/icons/addRecord.jpg"> </img> </a>
                <a id="linkModify" name="linkModify" href="#" title="<%=colextb.getMessage("sc.title.modify")%>" onclick="javascript:modifyRecord()"> <img alt="<%=colextb.getMessage("sc.title.modify")%>" src="img/icons/editRecord.jpg"> </img>  </a>
                <a id="linkDelete" name="linkDelete" href="#" title="<%=colextb.getMessage("sc.title.delete")%>" onclick="javascript:deleteRecord()"> <img alt="<%=colextb.getMessage("sc.title.delete")%>" src="img/icons/deleteRecord.jpg"> </img> </a>
                <a> <img  src="img/backgrounds/line.jpg"> </img> </a>
                <a id="linkShowRecord" name="linkShowRecord" href="#" title="<%=colextb.getMessage("sc.title.record")%>" onclick="javascript:closeTabRecords();"> <img alt="<%=colextb.getMessage("sc.title.record")%>" src="img/icons/showRecord.jpg"> </img> </a>

            </div>


            <%if ((colextb.getFault("search").equals("EMPTY_COLLECTION")) || (colextb.getFault("search").equals("NO_RECORD_FOUND"))) {%>
            <h4 id="divMessage" name="divMessage" class="fault"> <%=colextb.getMessage(colextb.getFault("search"))%> </h4>         
            <%
            } else {
                edu.xtec.colex.domain.FieldDef fdAux;
                java.util.Vector vFD = colextb.retrieveFieldDefs();
            %>

            <div id="divTableRecords" class="webfx-columnlist">

                <div id="divTableRecordsHead" class="webfx-columnlist-head">


                    <table cellspacing="0" cellpadding="0"  style="width: <%=100 * vFD.size()%>px;" summary="<%=colextb.getMessage("table.header.summary")%>">
                        <caption style="display:none;"><%=colextb.getMessage("table.header.caption")%></caption>
                        <tr>



                            <%int j;
                                for (j = 0; j < vFD.size(); j++) {
                                    fdAux = (edu.xtec.colex.domain.FieldDef) vFD.get(j);
                            %>
                            <%if (fdAux.isSortable()) {

                                    if (fdAux.getName().equals(query.getOrderField())) {
                                        if (query.getDirection().equals("asc")) {%>
                            <td scope="col" stlye="width: 100px;" title="<%=fdAux.getName()%>" onclick="javascript:sortTable('<%=fdAux.getName()%>', 'desc');"><img alt="<%=colextb.getMessage("sc.sort.asc")%>" style="display:inline;" src="img/icons/asc.png"/>
                                <%} else {%> 
                            <td scope="col" stlye="width: 100px;" title="<%=fdAux.getName()%>" onclick="javascript:sortTable('<%=fdAux.getName()%>', 'asc');"><img alt="<%=colextb.getMessage("sc.sort.desc")%>" style="display:inline;" src="img/icons/desc.png"/>
                                <%}
                            } else {%>
                            <td scope="col" stlye="width: 100px;" title="<%=fdAux.getName()%>" onclick="javascript:sortTable('<%=fdAux.getName()%>', 'asc');">
                                <%}
                        } else {%>
                            <td scope="col" stlye="width: 100px;" title="<%=fdAux.getName()%>">
                                <%}%>
                                <%=fdAux.getName()%></td>

                            <%}%>
                        </tr>
                    </table>




                </div>

                <div id="divTableRecordsBody" class="webfx-columnlist-body">  

                    <table cellspacing="0" cellpadding="0" style="width: <%=100 * vFD.size()%>px;" summary="<%=colextb.getMessage("table.body.summary")%>">
                        <caption style="display:none;"><%=colextb.getMessage("table.body.caption")%></caption>
                        <colgroup span="<%=vFD.size()%>">
                            <%for (j = 0; j < vFD.size(); j++) {%>
                            <col style="width: 100px;"/>
                            <%}%>
                        </colgroup>


                        <%java.util.Vector vRecords = colextb.retrieveVRecords();

                            edu.xtec.colex.domain.Field fAux;
                            edu.xtec.colex.domain.Record rAux;

                            for (int i = 0; i < vRecords.size(); i++) {
                                rAux = (edu.xtec.colex.domain.Record) vRecords.get(i);
                        java.util.Vector vFields = rAux.getFields();%>

                        <script>

                            vIdRecords[<%=i%>] =<%=rAux.getId()%>;

                        </script>




                        <tr scope="row" ondblclick="javascript:openRecord('<%=i%>');" <% if (colextb.getQuery().getBeginIndex() == i) {%> <%}%>>   


                            <%
                                for (j = 0; j < vFields.size(); j++) {
                                    fAux = (edu.xtec.colex.domain.Field) vFields.get(j);

                                    fdAux = colextb.getFieldDef(fAux.getName());

                                    if (fdAux.getType().equals("date")) {%>
                            <td><%=edu.xtec.colex.domain.FieldDefDate.getDD(fAux.getValue())%>-<%=edu.xtec.colex.domain.FieldDefDate.getMM(fAux.getValue())%>-<%=edu.xtec.colex.domain.FieldDefDate.getYYYY(fAux.getValue())%>

                                <%} else if (fdAux.getType().equals("sound")) {
                                    if (!fAux.getValue().equals("null")) {
                                        String idImg = "img_" + edu.xtec.colex.utils.Utils.getFileName(fAux.getValue());
                                %>    
                            <td> 
                                <img alt="<%=colextb.getMessage("sc.has.sound")%>" id="<%=idImg%>" src="img/icons/newsound.png" height="20" width="20"/>                    
                                <%} else {%>
                            <td> 
                                <img alt="<%=colextb.getMessage("sc.has.no.sound")%>" src="img/icons/nosound.png" height="20" width="20"/>

                                <%}
                                } else if (fdAux.getType().equals("image")) {

                                    if (!fAux.getValue().equals("null")) {
                                        String idImg = "img_" + edu.xtec.colex.utils.Utils.getFileName(fAux.getValue());
                                %>    
                            <td> 
                                <img alt="<%=edu.xtec.colex.utils.Utils.getFileName(fAux.getValue())%>" id="<%=idImg%>" src="<%=colextb.getFilesURL() + fAux.getValue()%>" height="20" width="20"/>


                                <%scriptEnd = scriptEnd
                                            + "imgSource = document.getElementById('" + idImg + "');"
                                            + "divImage = document.createElement('div');"
                                            + "divImage.setAttribute('id','divShow_" + idImg + "');"
                                            + "divImage.setAttribute('style','display:none;');"
                                            + "Element.addClassName(divImage,'divShow');"
                                            + "image = document.createElement('img');"
                                            + "image.setAttribute('id','divShow_" + idImg + "');"
                                            + "image.src='" + colextb.getFilesURL() + fAux.getValue() + "';"
                                            + "imageAux = new Image();"
                                            + "imageAux.src=\"" + colextb.getFilesURL() + fAux.getValue() + "\";"
                                            + "prop=getProportion(imageAux.width,imageAux.height,150,150);"
                                            + "image.width=Math.round(imageAux.width/prop);"
                                            + "image.height=Math.round(imageAux.height/prop);"
                                            + "divImage.appendChild(image);"
                                            + "divRT = document.getElementById('RecordTable');"
                                            + "divRT.appendChild(divImage);"
                                            + "imgSource = document.getElementById('" + idImg + "');"
                                            + "Event.observe(imgSource, 'mouseover', function(event){showImage(event,'" + idImg + "')});"
                                             + "Event.observe(imgSource, 'mouseout', function(){hideImage('" + idImg + "')});";%> 


                                </script>
                                <%} else {%>
                            <td>&nbsp;

                                <%}%>
                                <%} else if (fdAux.getType().equals("select")) {%>
                            <td>
                                <%if (fAux.getValue().equals(fdAux.getDefaultValue())) {%>
                                <%=colextb.getMessage(fdAux.getDefaultValue())%>
                                <%} else {%>
                                <%=fAux.getValue()%>
                                <%}
                            } else if (fdAux.getType().equals("html")) {%>

                            <td> 
                                <i>Html</i>
                                <%} else if (fdAux.getType().equals("text")) {%>

                            <td> 
                                <%if (fAux.getValue().length() > 25) {
                                        fAux.setValue(fAux.getValue().substring(0, 22) + "...");
                                    }%>
                                <%=fAux.getValue()%>
                                <%} else {%>
                            <td><%if (fAux.getValue().trim().equals("")) {%>&nbsp;
                                <%} else {%><%=fAux.getValue()%><%}%>
                                <%}%>
                            </td>
                            <%}%>


                        </tr>
                        <%}%>

                    </table>
                </div>


            </div>

            <%}%>

            <div id="browserTable"> <%=colextb.getNumFound()%> fitxes trobades </td>    
            </div>


        </div>


        <%}%>

        <%}%> <%-- No exists collection --%>
    </div>

    <script type="text/javaScript">
        <%if (colextb.getNumFound() == 0) {%>

        disableLink('linkModify');
        disableLink('linkDelete');
        <%}%>

        var permission =  <%=colextb.retrievePermission()%>;

        switch(permission)
        {

        case <%=edu.xtec.colex.domain.Guest.PERMISSION_READ%>:
        disableLink('linkModify');
        disableLink('linkDelete');
        disableLink('linkAdd');
        break;
        case <%=edu.xtec.colex.domain.Guest.PERMISSION_NONE%>:
        disableLink('linkModify');
        disableLink('linkDelete');
        disableLink('linkAdd');
        break;
        }


    </script>
    <script>

        var table = new WebFXColumnList();

        table.columnSorting = false;
        table.moveColumns = false;
        table.multiple = false;
        table.resizeColumns = false;

        var rc2 = table.bind(document.getElementById('divTableRecords'), document.getElementById('divTableRecordsHead'), document.getElementById('divTableRecordsBody'));



        table._sizeBodyAccordingToHeader();
        table.selectRow(<%=colextb.getQuery().getBeginIndex()%>, false);


    </script>
    <script  language="javascript">   <%=scriptEnd + "}"%></script>
</body>

</html>
