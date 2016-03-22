<%@page session="false" contentType="text/html; charset=iso-8859-1"
%><jsp:useBean id="colexsb" class="edu.xtec.colex.client.beans.ColexStructureBean" scope="request" />
<%
if(!colexsb.init(request, response))
{%>
<jsp:forward page="<%=colexsb.getRedirectPage()%>"/>
<%}%>

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
        
        <script language="javascript" type="text/javascript" src="tiny_mce/tiny_mce.js"></script>
             
        <script src="js/check.js"></script>
        
        <script type="text/javaScript">
        
         Node.prototype.swapNode = function (node) 
        {
            var nextSibling = this.nextSibling;
            var parentNode = this.parentNode;
            node.parentNode.replaceChild(this, node);
            parentNode.insertBefore(node, nextSibling);  
        }
        
        var divModify = null;
        var divField = null;
        
        function openCollection(nameCollection)
        {
            document.formCollection.collection.value=nameCollection;
            document.formCollection.operation.value="showAll";
            document.formCollection.action="record.jsp";
            document.formCollection.submit();
            
        }
                
        function modifyCollection()
        {          
            name = TrimString(document.formCollection.newName.value);
            if (name=="") Dialog.alert("<%=colexsb.getMessage("EMPTY_COLLECTION_NAME")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
            else if (!chkReservedChars(name,'<%=colexsb.getJspProperties().getProperty("reserved.chars")%>')) Dialog.alert('<%=colexsb.getMessage("RESERVED_CHAR")%> (<%=colexsb.getJspProperties().getProperty("reserved.chars")%>)', {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
            else if (document.formCollection.description.value.length > 1000) Dialog.alert("<%=colexsb.getMessage("TOO_LONG_DESCRIPTION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
            else 
            {
                document.formCollection.newName.value=name;
                document.formCollection.operation.value="modifyCollection";
                document.formCollection.action="structure.jsp";
                document.formCollection.submit();
            }
        }
        
        function addField()
        {
            name = TrimString(document.formAdd.addFieldName.value);
        
            if (name=="") 
            {
                Dialog.alert("<%=colexsb.getMessage("EMPTY_FIELD_NAME")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                return;
            }
            
            if (!chkReservedChars(name,'<%=colexsb.getJspProperties().getProperty("reserved.chars")%>'))
            {
                Dialog.alert('<%=colexsb.getMessage("RESERVED_CHAR")%> (<%=colexsb.getJspProperties().getProperty("reserved.chars")%>)', {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
                return;
                
            }
            
            var type = document.formAdd.type.value;
            
            if (!chkProperties(type,document.formAdd))
            {
                    Dialog.alert("<%=colexsb.getMessage("NO_VALID_PROPERTIES")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                    return;
            }
            
            document.formAdd.addFieldName.value=name;
            
            
            if (type=="select")
            {
                var optionsText ="";
                
                for (i=0;i<document.formAdd.listOptions.length;i++)
                {
                    optionsText = optionsText + document.formAdd.listOptions.options[i].value + "|";
                }
                
                document.formAdd.hiddenOptions.value=optionsText
                
                if (i==0) 
                {
                    Dialog.alert("<%=colexsb.getMessage("EMPTY_OPTIONS")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                    return;
                }
            
            }
            
            document.formAdd.submit();
            
        }
        
        function deleteField(formId)
        {
            Dialog.confirm("<%=colexsb.getMessage("delete.fielddef")%>", 
            {
                windowParameters: {width:350, height:130}, 
                okLabel: "sí", 
                cancelLabel: "no", 
                buttonClass: "myButtonClass", 
                id: "myDialogId",
                cancel:function(win) {return false},
                ok:function(win) {deleteFieldOK(win,formId)} 
            });
        }
        
        
        function deleteFieldOK(win,formId)
        {
            Windows.closeAll();
            
            document.getElementById(formId).operation.value="delete";
            form = document.getElementById(formId);
            setTimeout("form.submit()",200);
        }
                
        function editField(fieldId)
        {
            if (divModify !=null)
            {
                divField.swapNode(divModify);
                editFieldId = divModify.id;
                
                editFieldId = editFieldId.substring(editFieldId.indexOf('_'),editFieldId.length);
                
                document.getElementById("incArrow"+editFieldId).style.display="block";
                document.getElementById("decArrow"+editFieldId).style.display="block";
            }
            
        
            divField = document.getElementById("fd_"+fieldId);
         
            divModify = document.getElementById("mod_"+fieldId);
            
            divField.swapNode(divModify);
            
            document.getElementById("incArrow_"+fieldId).style.display="none";
            document.getElementById("decArrow_"+fieldId).style.display="none";
        }
        
        function modifyFieldDef(formId)
        {
            formModify = document.getElementById(formId);
            
            name = TrimString(formModify.modifyFieldName.value);
            
            if (name=="") 
            {
                Dialog.alert("<%=colexsb.getMessage("EMPTY_FIELD_NAME")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                return;
            }
            if (!chkReservedChars(name,'<%=colexsb.getJspProperties().getProperty("reserved.chars")%>'))
            {
                Dialog.alert('<%=colexsb.getMessage("RESERVED_CHAR")%> (<%=colexsb.getJspProperties().getProperty("reserved.chars")%>)', {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
                return;
                
            }
            
            var type = formModify.type.value;
            
            if (!chkProperties(type,formModify))
            {
                    Dialog.alert("<%=colexsb.getMessage("NO_VALID_PROPERTIES")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                    return;
            }
            
            if (type=="select")
            {
                var optionsText ="";
                
                for (i=0;i<formModify.listOptions.length;i++)
                {
                    optionsText = optionsText + formModify.listOptions.options[i].value + "|";
                }
                
                formModify.hiddenOptions.value=optionsText
                
                if (i==0) 
                {
                    Dialog.alert("<%=colexsb.getMessage("EMPTY_OPTIONS")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                    return;
                }
            
            }
            
            formModify.modifyFieldName.value=name;
            formModify.operation.value="modify";
            formModify.submit();
            
        }
        
        function cancelModify(fieldId)
        {
            var e = divModify.getElementsByTagName("form");
           
            if (e[0].type.value=="select")
            //Case to reset the Options on the type Select, we reset all the page
            {
                document.formCollection.operation.value="";
                document.formCollection.submit();
            }
            
            e[0].reset();
            
           
            
            divField.swapNode(divModify);
            
            
            divModify = null;
            divField = null;
            
            document.getElementById("incArrow_"+fieldId).style.display="block";
            document.getElementById("decArrow_"+fieldId).style.display="block";
            
            
        }
        
        function increasePosition(formId)
        {
            document.getElementById(formId).operation.value ="increasePosition";
            document.getElementById(formId).submit();
        }
        
        function cancelAdd()
        {
            document.formAdd.reset();
            document.formAdd.addFieldName.value="";
            document.formAdd.type.value="text";            
            iniAddLayer();
        }
        
        function iniAddLayer()
        {
            setTypeLayer(document.formAdd.type.value);
        }
        
        function setTypeLayer(current)
        {
            hideTypeLayers();
            document.getElementById(current).style.display="block";
        }
        
        function hideTypeLayers()
        {
            <%
            java.util.Vector vTypes = colexsb.retriveTypes();
            edu.xtec.colex.domain.FieldDef fd;
            
            for (int i=0; i<vTypes.size();i++)
            {
                fd = (edu.xtec.colex.domain.FieldDef) vTypes.get(i);
                %>
                document.getElementById("<%=fd.getType()%>").style.display="none";
            <%}%>            
        }
        
        
        function addOption(selectObject,inputObject,isFieldAdd) 
        {
            name = inputObject.value;
        
            if (name=="") 
            {
                Dialog.alert("<%=colexsb.getMessage("EMPTY_OPTION_NAME")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                return;
            }
            
            <%
            edu.xtec.colex.domain.FieldDef fdSelect;
            fdSelect = edu.xtec.colex.domain.FieldDef.createFieldDef("select");%>
            
            defaultValue = '<%=fdSelect.getDefaultValue()%>';
            defaultValueLan = '<%=colexsb.getMessage(fdSelect.getDefaultValue())%>';
            
            
            if ( (defaultValue.toLowerCase() == name.toLowerCase()) || (defaultValueLan.toLowerCase() == name.toLowerCase()))
            {
                Dialog.alert("<%=colexsb.getMessage("RESERVED_WORD")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
                return;
            }
            
            if (!chkReservedChars(name,'<%=colexsb.getJspProperties().getProperty("reserved.chars")%>'))
            {
                Dialog.alert('<%=colexsb.getMessage("RESERVED_CHAR")%> (<%=colexsb.getJspProperties().getProperty("reserved.chars")%>)', {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});
                return;
                
            }
            
            for (i=0; i<selectObject.options.length;i++)
            {
                if (selectObject.options[i].value.toLowerCase() == name.toLowerCase())
                {
                    Dialog.alert("<%=colexsb.getMessage("REPEATED_OPTION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                    return;
                }
            }

            var optionObject = new Option(name,name)
            var optionRank = selectObject.options.length
            selectObject.options[optionRank]=optionObject
            inputObject.value='';
            selectObject.scrollTop = selectObject.scrollHeight;
            
            if (isFieldAdd)
            {   
                fieldAdd = document.getElementById('divFieldAdd');
                fieldAdd.scrollTop = fieldAdd.scrollHeight;
                Element.scrollTo(selectObject);
            }
        }
    
        function deleteOption(selectObject,optionRank) 
        {
            if (optionRank == -1) Dialog.alert("<%=colexsb.getMessage("NO_OPTION_SELECTED")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});        
            if (selectObject.options.length!=0) 
            { 
                selectObject.options[optionRank]=null
            }
        }
        </script>
        
    </head>
    <body>
    
    <div id="StructureScreen" class="content">
    <span id="help" >
        <a target="_blank" href="manual/credits.html" style="color:#546376;"><%=colexsb.getMessage("sc.about")%></a> | <a target="_blank" href="manual/intro.html" style="color:#546376;"><%=colexsb.getMessage("sc.help")%></a>
    </span>
    <div class="left">
            <div id="header">
                    <ul id="navigation">
                    <li id="home" > 
                    <a href="index.jsp"> <img alt="<%=colexsb.getMessage("sc.alt.home")%>" src="img/buttons/home.png"> </img> </a>
                    
                    </li>
                </ul>
            </div>
    <%
    if (colexsb.getFault("get").equals("NO_EXISTS_COLLECTION"))
    {%>
        <h4 class="fault"> <%=colexsb.getMessage(colexsb.getFault("get"))%> </h4> 
    
    <%}
    else
    {
    %>
            <h2><a href="#" title="<%=colexsb.getMessage("sc.title.open")%>" onclick="javascript:openCollection('<%=colexsb.escapeChars(colexsb.getCollection())%>')"><%=colexsb.getCollection()%></a></h2>
            
            <h3> <%=colexsb.getMessage("sc.available.fields")%> </h3>
    
    <%if (colexsb.getFault("get").equals("NO_FIELDS_DEFS"))
        {%>
        
            <h4 class="fault"> <%=colexsb.getMessage(colexsb.getFault("get"))%> </h4> 
        
        <%}
        else
        {%>
           <div id="divFields">
            <table id="tabFields">
                <%
                java.util.Vector vFieldDefs = colexsb.retrieveFieldDefs();

                //Fields Definitions of the collection
                for (int i=0;i<vFieldDefs.size();i++)
                {
                    fd = (edu.xtec.colex.domain.FieldDef) vFieldDefs.get(i);
                    String sClass;
                    
                    if (i % 2==0) sClass="odd";
                    else sClass="even";
                    %>	
                    <tr class="<%=sClass%>">
                    
                    <td valign="top" height="20px" width="5%">
                    

                    <a id="decArrow_<%=fd.getId()%>" name="decArrow_<%=fd.getId()%>" class="decArrow" title="<%=colexsb.getMessage("sc.title.dec")%>"
                    <%if (i!=0)
                    {
                        edu.xtec.colex.domain.FieldDef fdAux;
                        fdAux = (edu.xtec.colex.domain.FieldDef) vFieldDefs.get(i-1);
                    %>href="javascript:increasePosition('fmod_<%=fdAux.getId()%>')" > <img alt="<%=colexsb.getMessage("sc.title.dec")%>" src="img/icons/dec.png">
                    <%}
                    else
                    {%> 
                    > <img src="img/icons/decDis.png" alt="<%=colexsb.getMessage("sc.title.decDis")%>">  
                    <%}%>
                    </img> </a>
                                        
                    </td>
                    <td valign="top" height="20px" width="5%">
                    
                    <a id="incArrow_<%=fd.getId()%>" name="incArrow_<%=fd.getId()%>" class="incArrow" title="<%=colexsb.getMessage("sc.title.inc")%>"
                    <%if (i<vFieldDefs.size()-1){%> 
                    href="javascript:increasePosition('fmod_<%=fd.getId()%>')"> <img alt="<%=colexsb.getMessage("sc.title.inc")%>" src="img/icons/inc.png">
                    <%}
                    else
                    {%>
                    > <img src="img/icons/incDis.png" alt="<%=colexsb.getMessage("sc.title.incDis")%>"> 
                    <%}%>
                    </img> </a>     
                    
                    </td>
                    
                    <td width="90%" id="fd_<%=fd.getId()%>" style="padding-left: 5px;">
                    <a href="javascript:editField('<%=fd.getId()%>')" title="<%=colexsb.getMessage("sc.title.modify")%>"> <%=fd.getName()%> </a> : <%=colexsb.getMessage(fd.getType())%> 
                    </td>
                    </tr>
                    
                    <tr> <td colspan="3"> </td> </tr>

                <%}%>
            </table>
            </div>
            
            
            <table style="display:none;">

            <% 
            //Divs to edit a field
            for (int i=0;i<vFieldDefs.size();i++)
            {
                fd = (edu.xtec.colex.domain.FieldDef) vFieldDefs.get(i);

                %>	
      
                
                
                <tr> <td id="mod_<%=fd.getId()%>" width="90%" rowspan="2">
                
                <div class="divFieldMod">
                <form method="POST" name="fmod_<%=fd.getId()%>" id="fmod_<%=fd.getId()%>" action="structure.jsp">
                <input type="hidden" name ="collection" id="collection" value="<%=colexsb.getCollection()%>"></input>
                <input type="hidden" name ="type" id="type" value="<%=fd.getType()%>"></input>
                <input type="hidden" name ="operation" id="operation" value=""></input>
                
                    <div> 
                    <h4> <%=fd.getName()%>  : <%=colexsb.getMessage(fd.getType())%> </h4>
                    <input type="text"  maxlength="20" id="modifyFieldName" name="modifyFieldName"value="<%=fd.getName()%>" alt="<%=colexsb.getMessage("sc.alt.fieldName")%>"></input> 
                    </div>
                    
                    <input type="hidden" name="oldName" value="<%=fd.getName()%>"></input>
                    <div>
                    <% 
                    java.util.Vector v= fd.getVProperties();

                    edu.xtec.colex.domain.Property p;
                    int j;
                    
                    if (fd.getType().equals("select"))
                    {%>
                        <h4><%=colexsb.getMessage("sc.choose.options")%></h4>
                        
                        <input  type="text" name="optionText" id="" alt="<%=colexsb.getMessage("sc.alt.optionValue")%>"> </input> <a href="#" title="<%=colexsb.getMessage("sc.title.add")%>" onclick="javascript:addOption(fmod_<%=fd.getId()%>.listOptions,fmod_<%=fd.getId()%>.optionText,false);"> <img alt="<%=colexsb.getMessage("sc.title.add")%>" height="20px" width="20px" src="img/icons/addPink.jpg"/> </a>
                        
                        <br/><br/>
                       
                        <select style="width:150px;" name="listOptions" size="3" >
                        <%for(j=0;j<v.size();j++)
                        {
                            p = (edu.xtec.colex.domain.Property) v.get(j);
                        %>
                            <option value="<%=p.getValue()%>"> <%=p.getValue()%> </option>
                        <%}%>
                        </select>
                        
                        <input type="hidden" name="hiddenOptions" id="" value="<%=fd.getProperties()%>"/>

                        <a href="#" title="<%=colexsb.getMessage("sc.title.delete")%>" onclick="javascript:deleteOption(fmod_<%=fd.getId()%>.listOptions,fmod_<%=fd.getId()%>.listOptions.selectedIndex);"> <img alt="<%=colexsb.getMessage("sc.title.delete")%>" height="10px" width="20px" src="img/icons/minusPink.jpg"/> </a>
                    
                    <%}
                    else
                    {
                        if (v.size()>0)%> <h4><%=colexsb.getMessage("sc.properties")%> </h4>
                        <%for (j=0;j<v.size();j++)
                        {

                            p = (edu.xtec.colex.domain.Property) v.get(j);%> 

                            <%=colexsb.getMessage(p.getName())%> : <input id="" size="6" name="<%=p.getName()%>" type="text" value="<%=p.getValue()%>" alt="<%=colexsb.getMessage("sc.alt.propertyValue")%>"></input>
                            <%if(fd.getType().equals("image")) {%> (px) <%}%>
                            
                            
                    
                            <%if (j % 2 == 1) {%> <br/> <%}
                        
                        }
                    }%>
                    </div>
                
                <div class="items" >
                    <span id="delete"> <a href="#" onclick="javascript:deleteField('fmod_<%=fd.getId()%>');" title="<%=colexsb.getMessage("sc.title.delete")%>"> <img alt="<%=colexsb.getMessage("sc.title.delete")%>" src="img/icons/trashPink.jpg"> </img> </a></span>
                    <a href="#" onclick="javascript:cancelModify('<%=fd.getId()%>')" title="<%=colexsb.getMessage("sc.title.cancel")%>"> <img alt="<%=colexsb.getMessage("sc.title.cancel")%>" src="img/icons/cancelPink.jpg"> </img> </a>
                    <a href="#" onclick="javascript:modifyFieldDef('fmod_<%=fd.getId()%>');" title="<%=colexsb.getMessage("sc.title.accept")%>"> <img alt="<%=colexsb.getMessage("sc.title.accept")%>" src="img/icons/okPink.jpg"> </img> </a>
                </div>
                
                </form>
                </div>
                   </td>
                   </tr>
            <%}%>   
            
            </table>
        
        <%}%>
        
        </div>
    
        <form id="formCollection" name="formCollection" action="structure.jsp" method="POST">
        <input type="hidden" name ="operation" id="operation" value="modifyCollection"></input>
        <input type="hidden" name ="collection" id="collection" value="<%=colexsb.getCollection()%>"></input>
        <div id="rightUpStructure" class="rightUp">
        
        <h3><%=colexsb.getMessage("sc.modify.details")%></h3>
        

        <div id="divDetails">
            
        <h4><%=colexsb.getMessage("sc.name")%></h4>
        <input type="text" name="newName" id="newName"  maxlength="20" value="<%=colexsb.getNewName()%>" alt="<%=colexsb.getMessage("sc.alt.collectionName")%>"></input>
        
        <h4><%=colexsb.getMessage("sc.visibility")%></h4>
        <div class="divRadio">
        <input type="radio" onchange="" id ="public" name="isPublic" value="false" alt="<%=colexsb.getMessage("sc.private")%>" <% if (!colexsb.isPublic()){%> checked="checked" <%}%>> <%=colexsb.getMessage("sc.private")%> </input>
        <input type="radio" onchange="" id ="public" name="isPublic" value="true" alt="<%=colexsb.getMessage("sc.public")%>" <% if (colexsb.isPublic()){%> checked="checked" <%}%>> <%=colexsb.getMessage("sc.public")%> </input>
        </div>
        <h4><%=colexsb.getMessage("sc.tags")%></h4> 
        <input type="text" name="tags" size="25" value="<%=colexsb.getTags()%>" alt="<%=colexsb.getMessage("sc.tags")%>"/>
        
        <h4><%=colexsb.getMessage("sc.description")%></h4>
        <textarea id="description" name="description" rows="4" cols="20"><%=colexsb.getDescription()%></textarea>
        
        </div>       
        
        <div class="items"> <a href="#" onclick="javascript:modifyCollection()"> <img alt="<%=colexsb.getMessage("sc.title.accept")%>" src="img/buttons/acceptGreen.png"> </img> </a></div>
       
        </div>
        
        
        </form>
                
        <div class="rightDown">
        <h3> <%=colexsb.getMessage("sc.add.field")%> </h3>
        <form id="formAdd" name="formAdd" action="structure.jsp" method="POST">
            
            <input type="hidden" name ="operation" id="operation" value="add"></input>
            <input type="hidden" name ="collection" id="collection" value="<%=colexsb.getCollection()%>"></input>

            <div id="divFieldAdd">
            <h4> <%=colexsb.getMessage("sc.field.name")%> </h4>
            <input type="text" id="addFieldName" name="addFieldName"  maxlength="20" value="<%=colexsb.getAddFieldName()%>" alt="<%=colexsb.getMessage("sc.alt.fieldName")%>"/>        
             <h4><%=colexsb.getMessage("sc.type")%></h4>
            <select id="type" name="type" onchange="javascript:setTypeLayer(this.value)">

            <%  for (int i=0;i<vTypes.size();i++)
                {

                    fd = (edu.xtec.colex.domain.FieldDef) vTypes.get(i);%>

                <option value="<%=fd.getType()%>" <% if (fd.getType().equals(colexsb.getAddFieldType())) {%> selected="selected" <%}%>> <%=colexsb.getMessage(fd.getType())%> </option>

                <%}%>
            
            </select>

            <%  
            //Divs of the properties to Add a Field
            for (int i=0;i<vTypes.size();i++)
            {
                fd = (edu.xtec.colex.domain.FieldDef) vTypes.get(i);%>
                
                
                <div id="<%=fd.getType()%>" style="display:none;">

                   
                    
                <%  
                    if (fd.getType().equals("select"))
                    {
                        %> <h4><%=colexsb.getMessage("sc.choose.options")%></h4>
                        
                        <input  type="text" name="optionText" id="" maxlength="20" alt="<%=colexsb.getMessage("sc.alt.optionValue")%>"> </input> <a href="#" title="<%=colexsb.getMessage("sc.title.add")%>" onclick="javascript:addOption(formAdd.listOptions,formAdd.optionText,true);"> <img alt="<%=colexsb.getMessage("sc.title.add")%>" height="20px" width="20px" src="img/icons/add.jpg"/> </a>
                        
                        <br/><br/>
                        
                        <select style="width:150px;" name="listOptions" size="3"> </select>

                        <a href="#" title="<%=colexsb.getMessage("sc.title.delete")%>" onclick="javascript:deleteOption(formAdd.listOptions,formAdd.listOptions.selectedIndex);"> <img alt="<%=colexsb.getMessage("sc.title.delete")%>" height="10px" width="20px" src="img/icons/minus.jpg"/> </a>
                        
                        <input type="hidden" name="hiddenOptions" id=""/>
                        
                        <%
                           
                           
                    
                    }
                    else
                    {
                    java.util.Vector vProp = fd.getVProperties(); 
                    edu.xtec.colex.domain.Property prop; 
                    if (vProp.size()>0){%> <h4><%=colexsb.getMessage("sc.properties")%></h4><%}
                    for (int j=0;j<vProp.size();j++)
                    {
                        prop = (edu.xtec.colex.domain.Property) vProp.get(j);
                        if (j!=0){%><br/> <%}
                    %>
                        <%=colexsb.getMessage(prop.getName())%> : <input id="<%=prop.getName()%>" size="6" name="<%=prop.getName()%>" value="<%=prop.getValue()%>" type="text" alt="<%=colexsb.getMessage("sc.alt.propertyValue")%>"></input>
                      <%  
                        if(fd.getType().equals("image")) {%> (px) <%}%>
                        
                    <%}
                    }
                %>

                </div>   

            <%}%>

            </div>

            <div class="items"> 
                <a href="#" onclick="javascript:cancelAdd()"> <img alt="<%=colexsb.getMessage("sc.title.cancel")%>" src="img/buttons/cancelYellow.png"> </img> </a>
                <a href="#" onclick="javascript:addField()"> <img alt="<%=colexsb.getMessage("sc.title.accept")%>" src="img/buttons/acceptYellow.png"> </img> </a>
            </div>
        </form>
        </div>
    <%}%>
        
    
    </div>
    
    <script>
    
       iniAddLayer();
    
        <%if (colexsb.getFault("add")!="")
        {%>        
            <%
            edu.xtec.colex.domain.FieldDef fdAux = null;
            
            fdAux = fdAux.createFieldDef(request.getParameter("type"));
            
            java.util.Vector vProps = fdAux.getVProperties();
            edu.xtec.colex.domain.Property pAux;
            
            for (int i=0;i<vProps.size();i++)
            {
                pAux = (edu.xtec.colex.domain.Property) vProps.get(i);
            %>
                var prop = eval("document.formAdd."+"<%=pAux.getName()%>");
                prop.value="<%=request.getParameter(pAux.getName())%>";
            <%}%>  
        
        
        
            Dialog.alert("<%=colexsb.getMessage(colexsb.getFault("add"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
            
            
            
        <%}
        else if (colexsb.getFault("delete")!="")
        {%>
            Dialog.alert("<%=colexsb.getMessage(colexsb.getFault("delete"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
        <%}
        else if (colexsb.getFault("modify")!="")
        {%>
            
            form = eval("document.fmod_"+"<%=colexsb.getModifyFieldPos()%>");
            form.modifyFieldName.value='<%=request.getParameter("modifyFieldName")%>';
            
            <%
            edu.xtec.colex.domain.FieldDef fdAux = null;
            
            fdAux = fdAux.createFieldDef(request.getParameter("type"));
            
            java.util.Vector vProps = fdAux.getVProperties();
            edu.xtec.colex.domain.Property pAux;
            
            for (int i=0;i<vProps.size();i++)
            {
                pAux = (edu.xtec.colex.domain.Property) vProps.get(i);
            %>
                var prop = eval("document.fmod_"+"<%=colexsb.getModifyFieldPos()%>"+".<%=pAux.getName()%>");
                prop.value="<%=request.getParameter(pAux.getName())%>";
            <%}%>  

            editField(<%=colexsb.getModifyFieldPos()%>);
            
            Dialog.alert("<%=colexsb.getMessage(colexsb.getFault("modify"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                        
        <%}
        else if (colexsb.getFault("modifyCollection")!="")
        {%>
           Dialog.alert("<%=colexsb.getMessage(colexsb.getFault("modifyCollection"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
        <%}%>
    
    </script>

    </body>
</html>
