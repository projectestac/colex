<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexrb" class="edu.xtec.colex.client.beans.ColexRecordBean" scope="request" /><%
if(!colexrb.init(request, response)){%><jsp:forward page="redirect.jsp">
 <jsp:param name="redirectPage" value="<%=colexrb.getRedirectPage()%>"/>
</jsp:forward>
<%} else if(!response.isCommitted()){%>        
<%String scriptEnd="function resizeImages() {";%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Col·lex : col·leccions en xarxa</title>
        
		<link rel="shortcut icon" href="img/favicon.ico" />
        <link rel="stylesheet" type="text/css" href="styles.css" />
        <link rel="stylesheet" type="text/css" media="print" href="css/print.css" />
        
        <script type="text/javascript" src="js/disableEnter.js"></script>
        
        <script type="text/javascript" src="js/prototype.js"></script>

        <script src="js/scriptAculo/prototype.js" type="text/javascript"></script>
        <script src="js/scriptAculo/scriptaculous.js?load=effects" type="text/javascript"></script>
        
        <script type="text/javascript" src="js/window.js"></script>
        <script type="text/javascript" src="js/effects.js"></script>
        <link rel="stylesheet" type="text/css" href="css/default.css" />    
        <link rel="stylesheet" type="text/css" href="css/alert.css" />
        
        
        <script type="text/javascript" src="js/calendar.js"></script>
        <script type="text/javascript" src="js/calendar-ca.js"></script>
        <link rel="stylesheet" type="text/css" href="css/calendar-pink.css" />
        
        <script src="js//layers.js"> </script>
        <script src="js/links.js"> </script>
        
        <!--[if lt IE 7.]>
            <script defer type="text/javascript" src="js/pngfix.js"></script>
        <![endif]-->
        
        <script type="text/javaScript">
       
        var clicked = 0;
        
        function incBegin()
        {
            if (clicked == 0)
            {
                clicked = 1;
                document.formRecord.begin.value++;
                document.formRecord.operation.value="search";
                document.formRecord.submit(); 
            }
            else 
            {
                document.formRecord.submit();
                Dialog.alert("<%=colexrb.getMessage("PROCESSING_OPERATION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
            }
            
            
        }
    
        function decBegin()
        {
            if (clicked == 0)
            {
                clicked = 1;
                document.formRecord.begin.value--;
                document.formRecord.operation.value="search";
                document.formRecord.submit();  
            }
            else 
            {
                document.formRecord.submit();
                Dialog.alert("<%=colexrb.getMessage("PROCESSING_OPERATION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
            }
        }
        
        var expandDisabled = 0;
        
        function addRecord()
        {
        
            var divRecord = document.getElementById("divRecord");
            if (divRecord!=null) {divRecord.style.display="none";}
            
            var divMessage = document.getElementById("divMessage");
            if (divMessage!=null) {divMessage.style.display="none";}
            
            var divBrowser = document.getElementById("browser");
            if (divBrowser!=null) {divBrowser.style.display="none";}
            
            document.getElementById("itemsAddRecord").style.display="block";
            
            
            disableLink('linkAdd');
            disableLink('linkModify');
            disableLink('linkDelete');
            disableLink('linkPrint');
            
            
           
            document.getElementById("divAddRecord").style.display="block";
            
            
            var divCenter = document.getElementById("center");
            var divR = document.getElementById("divRecord");
            var divAdd = document.getElementById("divAddRecord");
              
            if (expandDisabled == 0 )
            //case button is actived
            {
            if (Element.getDimensions(divAdd).height < 293)
            //Case extended and too little
            {
                    changeSize();
                    disableLink('linkExpand');
                     
                    expandDisabled = 1;
            }
            else if (Element.getDimensions(divAdd).height == 293)
            //Case normal
            {
                changeSize();
                
                divASize = Element.getDimensions(divAdd).height;
                
                changeSize();
                
                if (divASize < 293) 
                //Test if extended is little
                {
                    disableLink('linkExpand');
                    expandDisabled = 1;
                }
            }
            }
        }
        
        function acceptAddRecord()
        {
            document.formAddRecord.submit();
        }
        
        function cancelAddRecord()
        {
            
            document.formAddRecord.reset();
            
            var divRecord = document.getElementById("divRecord");
            if (divRecord!=null) {divRecord.style.display="block";}
            var divBrowser = document.getElementById("browser");
            if (divBrowser!=null) {divBrowser.style.display="block";}
            
            var divMessage = document.getElementById("divMessage");
            if (divMessage!=null) {divMessage.style.display="block";}
            
            document.getElementById("divAddRecord").style.display="none";
            
            document.getElementById("itemsAddRecord").style.display="none";
            
            enableLink('linkAdd');
            
            if (expandDisabled==1)
            //case Normal
            {
                changeSize();
        
                var divR = document.getElementById("divRecord");
                divRSize = Element.getDimensions(divR).height;
        
                changeSize();
        
                if (divRSize > 293)
                {
                    enableLink('linkExpand');
                 
                    linkExp = document.getElementById('linkExpand');
                    linkExp.getElementsByTagName('img')[0].src="img/icons/exp_plus.jpg";
                    
                    expandDisabled=0;
                }
            }
            
            <%if (colexrb.getNumFound() != 0) 
            {%>
                
                enableLink('linkModify');
                enableLink('linkDelete');
                enableLink('linkPrint');
            <%}%>
        }
        
        function modifyRecord()
        {
            var e = document.getElementsByTagName("input");
            
            for(var i=0;i<e.length;i++)
            {
                if (e[i].type=="text")
                {
                    e[i].readOnly=false;
                    e[i].style.borderStyle="solid";
                    e[i].style.borderWidth="2px";
                    e[i].style.borderTopColor="#838183";
                    e[i].style.borderLeftColor="#838183";
                    e[i].style.borderRightColor="#fffaff";
                    e[i].style.borderBottomColor="#fffaff";
                    
                    var name = e[i].name;
                    
                    if (name.indexOf('fdDD_')==0)
                    {
                        e[i].size=2;
                    }
                    else if (name.indexOf('fdMM_')==0)
                    {
                        e[i].size=2;
                    }
                    else if (name.indexOf('fdYYYY_')==0)
                    {
                        e[i].size=4;
                    }
                    else e[i].size=20; 
                    
                    firstDescendant = e[i].parentNode.firstChild;
                    while (firstDescendant && firstDescendant.nodeType != 1) firstDescendant = firstDescendant.nextSibling;
                    
                    if (firstDescendant.tagName=='A')
                    {
                        
                        Element.show(e[i]);
                        Element.hide(firstDescendant);
                        
                    }
                    
                    
                    
                }
                if (e[i].type=="checkbox")
                {
                    e[i].disabled=false;
                }
            }
            
            
            
            e = document.getElementsByTagName("textarea");
            
            for(var i=0;i<e.length;i++)
            {
                e[i].readOnly=false;
                e[i].style.borderStyle="solid";
                e[i].style.borderWidth="2px";
                e[i].style.borderTopColor="#838183";
                e[i].style.borderLeftColor="#838183";
                e[i].style.borderRightColor="#fffaff";
                e[i].style.borderBottomColor="#fffaff";
                
                if (!Element.visible(e[i]))
                {
                    Element.show(e[i]);
                    firstDescendant = e[i].parentNode.firstChild;
                    while (firstDescendant && firstDescendant.nodeType != 1) firstDescendant = firstDescendant.nextSibling;
                    Element.hide(firstDescendant);
                }
                
            }
            
            
            e = document.getElementsByTagName("select");
            
            for(var i=0;i<e.length;i++)
            {
                e[i].disabled=false;
            }

            disableLink('linkAdd');
            disableLink('linkModify');
            disableLink('linkDelete');
            disableLink('linkPrint');
            
            
            document.getElementById("itemsEditRecord").style.display="block";
            document.getElementById("browser").style.display="none";
            
            var objects = document.getElementsByTagName("object");
            
            for(var i=0;i<objects.length;i++)
            {
                objects[i].style.display="none";
            }
            
            
            <% 
            java.util.Vector vFieldsDefs = colexrb.retrieveAttachFields();
            

            for (int i=0;i<vFieldsDefs.size();i++)  
            {
                edu.xtec.colex.domain.FieldDef fd;
                fd = (edu.xtec.colex.domain.FieldDef) vFieldsDefs.get(i);
                

                if (fd.getType().equals("image"))
                {%>
                    disableLinkImage('linkFd_<%=fd.getId()%>'); 
                <%}%>
                      
                var spanEdit_<%=fd.getId()%> = document.getElementById('editFd_<%=fd.getId()%>');
                
                spanEdit_<%=fd.getId()%>.style.display='block';
            <%}

            vFieldsDefs = colexrb.retrieveInfoFields();
            
            for (int i=0;i<vFieldsDefs.size();i++)
            {
                edu.xtec.colex.domain.FieldDef fd;
                fd = (edu.xtec.colex.domain.FieldDef) vFieldsDefs.get(i);%>
                
                var imgInfo = document.getElementById('infoFd_<%=fd.getId()%>');
                imgInfo.style.visibility='visible';
                
                
            <%
                if (fd.getType().equals("date")) 
                {%> 
                var cal = document.getElementById('calFd_<%=fd.getId()%>');
                cal.style.visibility='visible';
                <%}
            }
            
            %>
        }
        
        function acceptModifyRecord()
        {
            document.formRecord.operation.value="modify";
            document.formRecord.submit();       
        }
        
        function cancelModifyRecord()
        {
            document.formRecord.operation.value="search";
            document.formRecord.submit();
        }
       
        function deleteRecord()
        {
            Dialog.confirm("<%=colexrb.getMessage("delete.record")%>", 
            {
                windowParameters: {width:350,height:130}, 
                okLabel: "sí", 
                cancelLabel: "no", 
                buttonClass: "myButtonClass", 
                id: "myDialogId",
                cancel:function(win) {return false},
                ok:function(win) {deleteRecordOK(win)} 
            });
        }
        
        function deleteRecordOK(win)
        {   
            Windows.closeAll();
            document.formRecord.operation.value="delete";
            setTimeout("document.formRecord.submit()",200);
        }
        
        function showTabRecords()
        {
            document.formRecord.action="table.jsp";
            document.formRecord.operation.value="search";
            document.formRecord.submit();
        }
        
        function showAll()
        {
            document.formCollection.operation.value="showAll";
            document.formCollection.submit();
        }
        
        
        
        function exportCollection()
        {
            document.formCollection.operation.value="export";
                document.formCollection.submit();
        }
        
        function changeImport(fullName)
        {
            fileName = fullName.match(/[^\/\\]+$/);
            var extension = fileName[0].substring(fileName[0].indexOf(".")+1);
            
            if (extension != 'zip') 
            {
                Dialog.alert("<%=colexrb.getMessage("NO_ZIP_EXTENSION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                
            }
            else
            {
                link = document.getElementById('linkImport');
                
                imgImport = document.getElementById('navigate');
                imgImport.src='img/buttons/import.jpg';
                
                Event.observe(link, 'click', function(){importRecords()} , false);
                
                divFF = document.getElementById('divFakeFile');
                
                Element.removeClassName(divFF,"fakefile");
                Element.addClassName(divFF,"fakefileImport");
                
            }
        
            document.formCollection.fakefile.value=fullName;
        }
        
        function importRecords()
        {
            fullName = document.formCollection.importFile.value;
            extension ='';
            
            if (fullName != '')
            {
                fileName = fullName.match(/[^\/\\]+$/);
                extension = fileName[0].substring(fileName[0].indexOf(".")+1);
            }
                       
            if (extension != 'zip') Dialog.alert("<%=colexrb.getMessage("NO_ZIP_EXTENSION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
            else
            {
                document.formCollection.operation.value="importRecords";
                document.formCollection.submit();
            }
        }
        
        function sortCollection()
        {
            if (document.formCollection.orderField.value!="null")
            {
                 document.formCollection.operation.value="search";
                 document.formCollection.submit();
            }
        }
        
        function changeFieldSort()
        {
            if (document.formCollection.orderField.value!="null")
            {
            
                document.formCollection.direction[0].checked = true;
                document.formCollection.direction[1].checked = false;
                document.formCollection.operation.value="search";
                document.formCollection.submit();
            }
        }
       
        function changeDirectionSort()
        {
            if (document.formCollection.orderField.value!="null")
            {
                 document.formCollection.operation.value="search";
                 document.formCollection.submit();
            }
        }
        
        
        function changeCheckBox(formName,fieldName)
        {
            
            var checkHidden = eval("document."+formName+"."+fieldName);
            
            if (checkHidden.value=="true")
            {
                checkHidden.value="false";
                
            }
            else checkHidden.value="true";
        }
        
        function deleteImage(fieldId,maxWidth,maxHeight)
        {
            var delHidden = eval("document.formRecord.del_"+fieldId);
            delHidden.value="true";
            
            image = new Image();
            image.src= "img/buttons/noimage.png";
            prop=getProportion(image.width,image.height,maxWidth,maxHeight);
                
            var imgOld = document.getElementById("img_"+fieldId);
            imgOld.width=image.width/prop;
            imgOld.height=image.height/prop;
            imgOld.src = "img/buttons/noimage.png";    
        }

        function newImage(fullName,fieldId,maxWidth,maxHeight)
        {
            if (isImageFile(fullName))
            {
                var delHidden = eval("document.formRecord.del_"+fieldId);
                delHidden.value="false";
            
                image = new Image();
                image.src= "img/buttons/newimage.png";
                
                
                prop=getProportion(image.width,image.height,maxWidth,maxHeight);
                
                var imgOld = $("img_"+fieldId);
                imgOld.src = "img/buttons/noimage.png";
                
                imgOld.width=image.width/prop;
                imgOld.height=image.height/prop;
            }

        }
        
        function deleteSound(fieldId)
        {
            var delHidden = eval("document.formRecord.del_"+fieldId);
            delHidden.value="true";
            
            var sound = $('sound_'+fieldId);
            sound.src = "img/icons/nosound.png";
            

            var soundFile = $('soundFile_'+fieldId);
            
            soundFile.textContent = "Cap";
            

        }

        function newSound(fullName,fieldId)
        {
            
            if (isSoundFile(fullName))
            {
                var delHidden = eval("document.formRecord.del_"+fieldId);
                delHidden.value="false";

                var sound = $('sound_'+fieldId);
                sound.src = "img/icons/newsound.png";

                var soundFile = $('soundFile_'+fieldId);
                
                var fileName = fullName.match(/[^\/\\]+$/);
                soundFile.textContent = fileName;
            }

        }
        
        function isImageFile(fullName)
        {
        
            fileName = fullName.match(/[^\/\\]+$/);
            extension = fileName[0].substring(fileName[0].indexOf(".")+1);
            
            ops='<%=colexrb.getImageExt()%>';
            
            if (ops.indexOf(extension)==-1) 
            {
                Dialog.alert("<%=colexrb.getMessage("NO_IMAGE_EXTENSION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                return false;
            }
            else return true;
        }
        
        function isSoundFile(fullName)
        {
        
            fileName = fullName.match(/[^\/\\]+$/);
            extension = fileName[0].substring(fileName[0].indexOf(".")+1);
            
            ops='<%=colexrb.getSoundExt()%>';
            
            if (ops.indexOf(extension)==-1) 
            {
                Dialog.alert("<%=colexrb.getMessage("NO_SOUND_EXTENSION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                return false;
            }
            else return true;
        }
        
        function printRecord()
        {
                
            var divCenter = document.getElementById("center");
           
            
            if (Element.hasClassName(divCenter, "centerNormal"))
            {
                if (expandDisabled==0) changeSize();                
                window.print();
                
            }
            else
            {
                window.print();
            }
        }
        
        
        function openCalendar(formName,format,id)
        {
            day = eval("document."+formName+"."+format+"DD_"+id);
            month = eval("document."+formName+"."+format+"MM_"+id);
            year = eval("document."+formName+"."+format+"YYYY_"+id);

            var calendar = new Calendar(1, null, 
                                            function onSelect(calendar, date) 
                                            {
                                                day.value=date.substring(0,2);
                                                month.value=date.substring(3,5);
                                                year.value=date.substring(6,10);

                                                if (calendar.dateClicked) 
                                                {
                                                    calendar.callCloseHandler();
                                                }
                                            }, 
                                            function onClose(calendar) 
                                            {
                                                calendar.hide();
                                            }   
                                );  

            calendar.weekNumbers = false;
            calendar.setRange(1,2050);
            calendar.setDateFormat("%d-%m-%Y");

            calendar.create();

            calendar.parseDate(day.value+'-'+month.value+'-'+year.value);
            calendar.showAtElement(year,"BR");

        }
        
        function getProportion(iWidth, iHeight, iMaxWidth, iMaxHeight)
        {
            var propH=1;
            var propW=1;
        
            if(iWidth>iMaxWidth)
            {
                propW=iWidth/iMaxWidth;
            }
        
            if(iHeight>iMaxHeight)
            {
                propH=iHeight/iMaxHeight;
            }
        
            if(propH>propW)
            {
                return propH;
            }
            else return propW;
        }
                
        function changeSize()
        {
                
            var divCenter = document.getElementById("center");
            var divR = document.getElementById("divRecord");
            var divAdd = document.getElementById("divAddRecord");
     
            if (Element.hasClassName(divCenter, "centerExtended"))
            {
            	Element.removeClassName(divCenter,"centerExtended");
		Element.removeClassName(divR,"recordExtended");
		Element.removeClassName(divAdd,"recordExtended");
		
		Element.addClassName(divCenter,"centerNormal");
		Element.addClassName(divR,"recordNormal");
		Element.addClassName(divAdd,"recordNormal");
		
		
		<%if (colexrb.getNumFound()!=0){%> document.formRecord.recordSize.value="Normal";<%}%>
		<%if (colexrb.getOwner()==null){%> document.formAddRecord.recordSize.value="Normal";<%}%>
		document.formCollection.recordSize.value="Normal";
		document.formSearch.recordSize.value="Normal";
		
                if (expandDisabled==0) 
                {
                    document.getElementById("expandIco").src="img/icons/exp_plus.jpg";
                    document.getElementById("expandIco").alt="<%=colexrb.getMessage("sc.title.expandSize")%>";
                    document.getElementById("expandIco").title="<%=colexrb.getMessage("sc.title.expandSize")%>";
                }
		
            }   
            else 
            {
		Element.removeClassName(divCenter,"centerNormal");
		Element.removeClassName(divR,"recordNormal");
		Element.removeClassName(divAdd,"recordNormal");
		
		
		Element.addClassName(divCenter,"centerExtended");
		Element.addClassName(divR,"recordExtended");
		Element.addClassName(divAdd,"recordExtended");
		
		<%if (colexrb.getNumFound()!=0){%> document.formRecord.recordSize.value="Extended";<%}%>
		<%if (colexrb.getOwner()==null){%> document.formAddRecord.recordSize.value="Extended";<%}%>
		document.formCollection.recordSize.value="Extended";
                document.formSearch.recordSize.value="Extended";
                
                if (expandDisabled==0) 
                {
                    document.getElementById("expandIco").src="img/icons/exp_minus.jpg";
                    document.getElementById("expandIco").alt="<%=colexrb.getMessage("sc.title.reduceSize")%>";
                    document.getElementById("expandIco").title="<%=colexrb.getMessage("sc.title.reduceSize")%>";
                }
	    }
        }
        
        function showInfo()
        {
           div = $('divInfo');
           
            if (!Element.visible(div)) 
            {
                Effect.BlindDown(div);
                hideObjects();
            }
            else 
            {
                Effect.BlindUp(div);
                showObjects();
            }   
        }
        
        
        
        </script>
        
    </head>
    <body onload="resizeImages();">
    <div id="RecordScreen" class="content">
    
    <span id="help" >
        <a target="_blank" href="manual/credits.html" style="color:#546376;"><%=colexrb.getMessage("sc.about")%></a> | <a target="_blank" href="manual/intro.html" style="color:#546376;"><%=colexrb.getMessage("sc.help")%></a>
    </span>
    <form id="formCollection" name="formCollection" action="record.jsp" enctype="multipart/form-data" method="POST" method="POST" >
    
    <div id="rightUpRecord" class="rightUp">

            <h3> <%=colexrb.getMessage("sc.import.records")%> </h3>
            
            <div class="fileinputs">
                <input type="file" class="file" name="importFile" id="importFile" onchange="javascript:changeImport(this.value);" alt="<%=colexrb.getMessage("sc.alt.fileName")%>"></input>
                <div id="divFakeFile" name="divFakeFile" class="fakefile">
                    <input id="fakefile" name="fakefile"> </input>
                    <a id="linkImport" name="linkImport"> <img  id="navigate" name="navigate" src="img/buttons/navigate.jpg" alt="<%=colexrb.getMessage("sc.alt.navigate")%>"/> </a>
                </div>
            </div>
            
            <div id="coverRightUpRecord">
            
            </div>
            
            
    </div>    

    <div class="rightMid">
    
        <h3> <%=colexrb.getMessage("sc.sort.by")%> </h3>
            <select name="orderField" id="orderField" <%--onchange="javascript:changeFieldSort();"--%>>
                <option value="null" > <%=colexrb.getMessage("sc.select.field")%> </option>
        <% 
            edu.xtec.colex.domain.Query query = colexrb.getQuery();
            java.util.Vector vSort = colexrb.retrieveSortableFields();
            edu.xtec.colex.domain.FieldDef fd;
            for (int i=0;i<vSort.size();i++)
            {
                fd = (edu.xtec.colex.domain.FieldDef) vSort.get(i);
         %>
                <option value="<%=fd.getName()%>"
                <%if (fd.getName().equals(query.getOrderField())) {%> selected="selected" <%}%>
                > <%=fd.getName()%> 
                
                </option>
        
         <% }%>
            </select>
            
            <div class="divRadio">
            <input type="radio" id ="direction" name="direction" value="asc" checked="checked" alt="<%=colexrb.getMessage("sc.sort.asc")%>"> <%=colexrb.getMessage("sc.sort.asc")%> </input>
            <input type="radio" id ="direction" name="direction" value="desc" alt="<%=colexrb.getMessage("sc.sort.desc")%>"<% if (query.getDirection().equals("desc")){%> checked="checked" <%}%>> <%=colexrb.getMessage("sc.sort.desc")%> </input>
            </div>
            
            
            <div class="items">
                <a href="#" id="linkSort" name="linkSort" onclick="javascript:sortCollection()"> <img src="img/buttons/sort.png" alt="<%=colexrb.getMessage("sc.title.sort")%>"> </img> </a>
            </div>
            
          
            
            
    </div>
    

    
    
    <div class="left">
    
    <div id="header">
        <ul id="navigation">
            <li>
            
            <%if (colexrb.getUserId()==null)
            {%>    
                <a href="portal.jsp">
            <%}
            else
            {%>
                <a href="index.jsp"> 
            <%}%>
            <img alt="<%=colexrb.getMessage("sc.alt.home")%>" src="img/buttons/home.png"> </img> </a>
            </li>
        </ul>
    </div>
    <%
    if (colexrb.getFault("search").equals("NO_EXISTS_COLLECTION"))
    {%>
        <h4 id="divMessage" name="divMessage" class="fault"> <%=colexrb.getMessage(colexrb.getFault("search"))%> </h4>         
        
        
    <%}
    else
    {
    %>
        <h2> <%=colexrb.getCollection()%> </h2> 
        
        <a id="linkShowInfo" href="#" onclick="javascript:showInfo();" style="position:absolute;top:70px;left:22px;" title="<%=colexrb.getMessage("sc.title.details")%>"> <img src="img/icons/showInfo.png" alt="<%=colexrb.getMessage("sc.title.details")%>" /> </a>
        <div id="divInfo" style="display:none;">
        <% String sOwner = colexrb.getOwner() ;
        if (sOwner==null) sOwner = colexrb.getUserId();%>
        
        <h3 style="position:relative;top:0px;left:0px;color:#D1004F;"><%=colexrb.getMessage("sc.details")%></h3>
        
        
        <div> <b><%=colexrb.getMessage("idUser")%></b> <%=sOwner%> </div>
        <div> <b><%=colexrb.getMessage("sc.num.records")%></b> <%=colexrb.getNumRecords()%> </div>
        <div> <b><%=colexrb.getMessage("created")%></b> <%=colexrb.getCreated()%> </div>
        <hr class="hrClass"/>
        <div> <b><%=colexrb.getMessage("sc.description")%></b> <%=colexrb.getDescription()%> </div>
        <div> <b><%=colexrb.getMessage("sc.tags")%></b> <%=colexrb.getTags()%> </div>
        
        <div style="text-align:right;padding-top:5px;">
        <a onclick="javascript:showInfo();" href="#">
            <img alt="<%=colexrb.getMessage("sc.title.accept")%>" src="img/buttons/acceptPink.png"/>
        </a>
        </div>
        
        </div>
        <% if (colexrb.getFault("get").equals("NO_FIELDS_DEFS") || (colexrb.getFault("get").equals("NO_PUBLIC_COLLECTION")))
        {%>
        
        <h4 id="divMessage" name="divMessage" class="fault"> <%=colexrb.getMessage(colexrb.getFault("get"))%> </h4>
        
        <script> 
        disableLink('linkExport');
        document.getElementById('coverRightUpRecord').style.display='block';
        </script>
        
        <%}
        else
        {%>
            
            <%if ((colexrb.getFault("search").equals("EMPTY_COLLECTION")) || (colexrb.getFault("search").equals("NO_RECORD_FOUND")))
            {%>
                <h4 id="divMessage" name="divMessage" class="fault"> <%=colexrb.getMessage(colexrb.getFault("search"))%> </h4>         
            <%}%>

        
                <input type="hidden" name ="operation" id="operation"/>
                <input type="hidden" name="collection" id="colllection" value="<%=colexrb.getCollection()%>"/>
                <input type="hidden" name="numRecords" id="numRecords" value="<%=colexrb.getNumRecords()%>"/>
                
                <input type="hidden" name="recordSize" id="recordSize" value="<%=colexrb.getRecordSize()%>"/>
                <input type="hidden" name="isTabMode" id="isTabMode" value="<%=colexrb.getIsTabMode()%>"/>
                
            <% if (colexrb.getOwner()!=null)
            {%>
                <input type="hidden" name="owner" id="owner" value="<%=colexrb.getOwner()%>"/>
            <%}%>
        
            <% 
            java.util.Vector vConditions = query.getConditions();
            edu.xtec.colex.domain.Condition cond;
            for (int i=0;i<vConditions.size();i++)
            {
                cond = (edu.xtec.colex.domain.Condition) vConditions.get(i);%>
                <input type="hidden" name="op_<%=i%>" value="<%=cond.getOperator()%>"/>
                <input type="hidden" name="name_<%=i%>" value="<%=cond.getFieldName()%>"/>
                <input type="hidden" name="cmp_<%=i%>" value="<%=cond.getComparator()%>"/>
                <input type="hidden" name="value_<%=i%>" value="<%=cond.getValue()%>"/>

            <%}%>
            </form>
    
    
            <div id="itemsRecord" >
                <a id="linkAdd" name="linkAdd" href="#" title="<%=colexrb.getMessage("sc.title.add")%>" onclick="javascript:addRecord()"> <img alt="<%=colexrb.getMessage("sc.title.add")%>" src="img/icons/addRecord.jpg"> </img> </a>
                <a id="linkModify" name="linkModify" href="#" title="<%=colexrb.getMessage("sc.title.modify")%>" onclick="javascript:modifyRecord()"> <img alt="<%=colexrb.getMessage("sc.title.modify")%>" src="img/icons/editRecord.jpg"> </img>  </a>
                <a id="linkDelete" name="linkDelete" href="#" title="<%=colexrb.getMessage("sc.title.delete")%>" onclick="javascript:deleteRecord()"> <img alt="<%=colexrb.getMessage("sc.title.delete")%>" src="img/icons/deleteRecord.jpg"> </img> </a>
                <a id="linkPrint" name="linkPrint" href="#" title="<%=colexrb.getMessage("sc.title.print")%>" onclick="javascript:printRecord()"> <img alt="<%=colexrb.getMessage("sc.title.print")%>" src="img/icons/printRecord.jpg"> </img> </a>
                <a> <img  src="img/backgrounds/line.jpg"> </img> </a>
                <a id="linkShowTable" name="linkShowTable" href="#" title="<%=colexrb.getMessage("sc.title.table")%>" onclick="javascript:showTabRecords();"> <img alt="<%=colexrb.getMessage("sc.title.table")%>" src="img/icons/showTable.jpg"> </img> </a>
                                
                <a id="linkExpand" name="linkExpand" href="#" onclick="javascript:changeSize();"> 
                    <%if (colexrb.getRecordSize().equals("Normal")){%>
                    <img id="expandIco" name="expandIco" src="img/icons/exp_plus.jpg" title="<%=colexrb.getMessage("sc.title.expandSize")%>" alt="<%=colexrb.getMessage("sc.title.expandSize")%>"> </img>
                    <%}
                    else {%><img id="expandIco" name="expandIco" src="img/icons/exp_minus.jpg" title="<%=colexrb.getMessage("sc.title.reduceSize")%>" alt="<%=colexrb.getMessage("sc.title.reduceSize")%>"> </img> <%}%>
                </a>
                
            </div>
        

            <div id="contenidor">
            <div id="center" class="center<%=colexrb.getRecordSize()%>">
        
           
            <% 
            if (colexrb.retrievePermission() == edu.xtec.colex.domain.Guest.PERMISSION_TOTAL)
            {%>
        
            <div id="divAddRecord" class="record<%=colexrb.getRecordSize()%>">
            <form name="formAddRecord" id="formAddRecord" action="record.jsp" method="POST" enctype="multipart/form-data">
                <input type="hidden" name ="operation" id="operation" value="add"/>
                <input type="hidden" name="collection" id="colllection" value="<%=colexrb.getCollection()%>"/>
                <input type="hidden" name="numRecords" id="numRecords" value="<%=colexrb.getNumRecords()%>"/>
                
                <input type="hidden" name="recordSize" id="recordSize" value="<%=colexrb.getRecordSize()%>"/>
        
                <% if (colexrb.getOwner()!=null)
                {%>
                    <input type="hidden" name="owner" id="owner" value="<%=colexrb.getOwner()%>"/>
                <%}%>
                
            <table id="tabAddRecord">
            <%
                java.util.Vector v = colexrb.retrieveFieldDefs();
                   
                for (int i=0;i<v.size();i++)
                {
                    fd = (edu.xtec.colex.domain.FieldDef) v.get(i);
                %>
                    <tr>
                    <td class="rightTD" valign="top"> <h4><%= fd.getName() %></h4> </td>
                    <td class="leftTD">
                    <% String sType = fd.getType();
                
                    boolean bFaultAdd = (colexrb.getFault("add")!="");
                
                    if (sType.equals("image"))
                    {%>
                        <input type="file" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" onchange="isImageFile(this.value)" alt="<%=colexrb.getMessage("sc.alt.fileName")%>"/>
                    <%}
                    else if (sType.equals("sound"))
                    {%>
                        <input type="file" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" onchange="isSoundFile(this.value)" alt="<%=colexrb.getMessage("sc.alt.fileName")%>"/>
                    <%}
                    else if (sType.equals("date"))
                    {%>            
                    <input  size="2" maxlength="2" type="text" id="fdDD_<%=fd.getId()%>" name="fdDD_<%=fd.getId()%>" title="<%=colexrb.getMessage("sc.title.day")%>" alt="<%=colexrb.getMessage("sc.title.day")%>"
                    <%if (bFaultAdd){%> value="<%=colexrb.pmRequest.getParameter("fdDD_"+fd.getId())%>"<%}%>/>
                    - <input  size="2" maxlength="2" type="text" id="fdMM_<%=fd.getId()%>" name="fdMM_<%=fd.getId()%>" title="<%=colexrb.getMessage("sc.title.month")%>" alt="<%=colexrb.getMessage("sc.title.month")%>"
                    <%if (bFaultAdd){%> value="<%=colexrb.pmRequest.getParameter("fdMM_"+fd.getId())%>"<%}%>/>
                    - <input size="4" maxlength="4" type="text" id="fdYYYY_<%=fd.getId()%>" name="fdYYYY_<%=fd.getId()%>" title="<%=colexrb.getMessage("sc.title.year")%>" alt="<%=colexrb.getMessage("sc.title.year")%>" 
                    <%if (bFaultAdd){%> value="<%=colexrb.pmRequest.getParameter("fdYYYY_"+fd.getId())%>"<%}%>/>
                
                    <a title="<%=colexrb.getMessage("sc.title.calendar")%>" href="javascript:openCalendar('formAddRecord','fd','<%=fd.getId()%>')"> <img alt="<%=colexrb.getMessage("sc.title.calendar")%>" src="img/icons/date.gif"/> </a>
                
                    <img  src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%>" alt="<%=colexrb.getMessage(sType)%>">
                 
                    <%}
                    else if (sType.equals("boolean"))
                    {

                        boolean bValue = false;
                
                        if ( (bFaultAdd) && (colexrb.pmRequest.getParameter("fd_"+fd.getId()).equals("true")) ) {bValue=true;}%>   
                        
                        <input type="checkbox" onchange="javascript:changeCheckBox('formAddRecord','fd_<%=fd.getId()%>')" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>" <% if (bValue) {%> checked="true" <%}%>/>
                        <input type="hidden" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" value="<%=bValue%>"/>
                    
                    <%}
                    else if (sType.equals("text"))
                    {
                        edu.xtec.colex.domain.FieldDefText fdText;
                        fdText = (edu.xtec.colex.domain.FieldDefText) fd;
                        if (fdText.getLength()<=20)
                        {%>
                            <input type="text" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>"
                            <%if (bFaultAdd){%> value="<%=colexrb.pmRequest.getParameter("fd_"+fd.getId())%>"<%}%>/>                
                        <%}
                        else
                        {
                            int iRows = fdText.getLength()/35;
                            if (iRows == 0) iRows++;
                            else if (iRows > 5) iRows = 5;
                            
                            %>
                            <textarea cols="35" rows="<%=iRows%>" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>"
                            ><%if (bFaultAdd){%><%=colexrb.pmRequest.getParameter("fd_"+fd.getId())%><%}%></textarea>
                        <%}
                    
                    }
                    else if (sType.equals("select"))
                    {
                        edu.xtec.colex.domain.FieldDefSelect fdSelect;
                        fdSelect = (edu.xtec.colex.domain.FieldDefSelect) fd;
                        %>
                        <select id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" size=1 style="width:150px;">
                        
                            <option value="<%=fd.getDefaultValue()%>"> <%=colexrb.getMessage(fd.getDefaultValue())%> </option>
                        <%
                        edu.xtec.colex.domain.Property p;
                        java.util.Vector vProp = fd.getVProperties();
                        
                        for(int j=0;j<vProp.size();j++)
                        {
                            p = (edu.xtec.colex.domain.Property) vProp.get(j);
                        %>
                            <option value="<%=p.getValue()%>"> <%=p.getValue()%> </option>
                        <%}%>
                        
                        
                        
                        </select>

                    
                    <%}
                    else if (sType.equals("html"))
                    {%>
                            <textarea cols="35" rows="10" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" style="width:90%;"
                            ><%if (bFaultAdd){%><%=colexrb.pmRequest.getParameter("fd_"+fd.getId())%><%}%></textarea>
                    
                    <%}
                    else
                    {%>
                        <input type="text" id="fd_<%=fd.getId()%>" name="fd_<%=fd.getId()%>" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>"
                        <%if (bFaultAdd){%> value="<%=colexrb.pmRequest.getParameter("fd_"+fd.getId())%>"<%}%>/>                
                    
                    <%}
                    //Show FieldDef properties information
                
                    if (sType.equals("text"))
                    {
                        edu.xtec.colex.domain.FieldDefText fdText;
                    
                        fdText = (edu.xtec.colex.domain.FieldDefText) fd;%>
                    
                        <img src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%> (<%=colexrb.getMessage("length")%> = <%=fdText.getLength()%>)" alt="<%=colexrb.getMessage(sType)%> (<%=colexrb.getMessage("length")%> = <%=fdText.getLength()%>)"/>
                    <%}
    
                    else if (sType.equals("integer"))
                    {
                        String sProperties = fd.getProperties();
                        edu.xtec.colex.domain.FieldDefInteger fdI;
                        fdI = (edu.xtec.colex.domain.FieldDefInteger) fd;
                    
                    
                    
                    String sUnit = fdI.getUnit();
                    
                    if (!sUnit.equals("")) {%> (<%=sUnit%>) <%}%>
                    
                    <img src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%> [<%=fdI.getMin()%>,<%=fdI.getMax()%>]" alt="<%=colexrb.getMessage(sType)%> [<%=fdI.getMin()%>,<%=fdI.getMax()%>]"/>
                    
                    <%}
                    else if (sType.equals("decimal"))
                    {
                        String sProperties = fd.getProperties();
                        edu.xtec.colex.domain.FieldDefDecimal fdD;
                        fdD = (edu.xtec.colex.domain.FieldDefDecimal) fd;
                    
                        fdD.setProperties(fd.getProperties());
                    
                        String sUnit = fdD.getUnit();
                    
                        if (!sUnit.equals("")) {%> (<%=sUnit%>) <%}%>
                    
                        <img src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%> [<%=fdD.getMin()%>,<%=fdD.getMax()%>]" alt="<%=colexrb.getMessage(sType)%> [<%=fdD.getMin()%>,<%=fdD.getMax()%>]"/>
                    
                    <%}
                    else if (sType.equals("link"))
                    {%>
                    <img src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%>" alt="<%=colexrb.getMessage(sType)%>"/>
                    
                    <%}
                    else if (sType.equals("html"))
                    {%>
                    <img src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%>" alt="<%=colexrb.getMessage(sType)%>"/>
                    
                    <%}%>                    
                    
                </td>
                </tr>
            <%}%>
        </table>
        
        </form>
        </div>
        
        <%}%>
        
        <div id="divRecord" class="record<%=colexrb.getRecordSize()%>">      
        <%if (colexrb.getNumFound()!=0)
        {%>
        
            
            <table id="tabRecord">
            <form name="formRecord" id="formRecord" action="record.jsp" method="POST" enctype="multipart/form-data">
            <input type="hidden" name ="operation" id="operation"/>
            <input type="hidden" name="collection" id="colllection" value="<%=colexrb.getCollection()%>"/>
            <input type="hidden" name="begin" id="begin" value="<%=query.getBeginIndex()%>"/>
            <input type="hidden" name="numRecords" id="numRecords" value="<%=colexrb.getNumRecords()%>"/>
            <input type="hidden" name="recordSize" id="recordSize" value="<%=colexrb.getRecordSize()%>"/>
            
            <% if (colexrb.getOwner()!=null)
            {%>
            <input type="hidden" name="owner" id="owner" value="<%=colexrb.getOwner()%>"/>
            <%}
            %>
           
            <input type="hidden" name="direction" id="direction" value="<%=query.getDirection()%>"/>
            <input type="hidden" name="orderField" id="orderField" value="<%=query.getOrderField()%>"/>
        
            <% 
            for (int i=0;i<vConditions.size();i++)
            {
                 cond = (edu.xtec.colex.domain.Condition) vConditions.get(i);
            %>
                 <input type="hidden" name="op_<%=i%>" value="<%=cond.getOperator()%>"/>
                 <input type="hidden" name="name_<%=i%>" value="<%=cond.getFieldName()%>"/>
                 <input type="hidden" name="cmp_<%=i%>" value="<%=cond.getComparator()%>"/>
                 <input type="hidden" name="value_<%=i%>" value="<%=cond.getValue()%>"/>

            <%}%>
            
            <%
            edu.xtec.colex.domain.Record r = colexrb.retrieveRecord();
            %>
                <input type="hidden" name="idRecord" id="idRecord" value="<%=r.getId()%>"/>
            <%
            java.util.Vector vFields = r.getFields();
        
            for (int i=0;i<vFields.size();i++)
            {
                edu.xtec.colex.domain.Field f = (edu.xtec.colex.domain.Field) vFields.get(i);%>
                <%  edu.xtec.colex.domain.FieldDef fdAux = colexrb.getFieldDef(f.getName());
            
                String sType = fdAux.getType();
                
                boolean bFaultModify = ((colexrb.getFault("modify")!="") && (!colexrb.getFault("modify").equals("NO_EXISTS_MODIFY_RECORD")));

                if (sType.equals("image"))
                {
                    edu.xtec.colex.domain.FieldDefImage fdImage;
                    fdImage = (edu.xtec.colex.domain.FieldDefImage) fdAux;%>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                
                    <div style="width:<%=fdImage.getWidth()%>px;height:<%=fdImage.getHeight()%>px;" >
            
                    <%if (f.getValue().equals("null"))
                    {%>
                        <a> <img id="img_<%=i%>"  border = "0" width="<%=fdImage.getWidth()%>" height="<%=fdImage.getHeight()%>" id="img_<%=i%>" name="img_<%=i%>" src="img/buttons/noimage.png" alt="<%=colexrb.getMessage("sc.has.no.image")%>" /></a>
                        
                        
                        <%scriptEnd = scriptEnd +                        
                            "var image"+i+" = new Image();"+
                            "image"+i+".src=\"img/buttons/noimage.png\";"+
        
        
                            "prop"+i+"=getProportion(image"+i+".width,image"+i+".height,"+fdImage.getWidth()+","+fdImage.getHeight()+");"+
    
                            "document.getElementById('img_"+i+"').width=image"+i+".width/prop"+i+";"+
                            "document.getElementById('img_"+i+"').height=image"+i+".height/prop"+i+";";%>
                        

                    <%}
                    else
                    {%>
                        <a id="linkFd_<%=i%>" name="linkFd_<%=i%>" href="#" onclick="window.open('<%=colexrb.getFilesURL()%><%=f.getValue()%>','collex','width=600,height=400,left=400,top=150,screenX=400,screenY=150,scrollbars=yes,resizable=yes');" title="<%=colexrb.getMessage("sc.title.open")%>"> <img id="img_<%=i%>" name="img_<%=i%>" border = "0" width="<%=fdImage.getWidth()%>" height="<%=fdImage.getHeight()%>" src="<%=colexrb.getFilesURL()%><%=f.getValue()%>" alt="<%=edu.xtec.colex.utils.Utils.getFileName(f.getValue())%>" /></a>
                                                
                        <%scriptEnd = scriptEnd +                        
                            "var image"+i+" = new Image();"+
                            "image"+i+".src=\""+colexrb.getFilesURL()+f.getValue()+"\";"+
        
        
                            "prop"+i+"=getProportion(image"+i+".width,image"+i+".height,"+fdImage.getWidth()+","+fdImage.getHeight()+");"+
    
                            "document.getElementById('img_"+i+"').width=image"+i+".width/prop"+i+";"+
                            "document.getElementById('img_"+i+"').height=image"+i+".height/prop"+i+";";%>
                        
                        
                    <%}%>    
                    
                    </div>
                    
                    <span id="editFd_<%=i%>" name="editFd_<%=i%>" style="display:none;">
                    <br>
                    <input type="file" onchange="javascript:newImage(this.value,'<%=i%>',<%=fdImage.getWidth()%>,<%=fdImage.getHeight()%>);" id="fd_<%=i%>" name="fd_<%=i%>" value="" alt="<%=colexrb.getMessage("sc.alt.fileName")%>"/>
                    <br>
                    <a href="javascript:deleteImage('<%=i%>',<%=fdImage.getWidth()%>,<%=fdImage.getHeight()%>);"> <%=colexrb.getMessage("sc.title.delete")%> </a>
                    <input type="hidden" id="del_<%=i%>" name="del_<%=i%>" value="false"/>
                    </span>
                 
                <%}
                else if (sType.equals("sound"))
                {%>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                    <%if (f.getValue().equals("null"))
                    {%>
                        <a> <img  border = "0" id="sound_<%=i%>" name="sound_<%=i%>" src="img/icons/nosound.png" alt="<%=colexrb.getMessage("sc.has.no.sound")%>" /></a>
                    <%}
                    else
                    {%>
                    
                        <object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="42" height="42" id="sound_player" align="middle">
                        <param name="allowScriptAccess" value="sameDomain" />
                        <param name="movie" value="sound_player.swf" />
                        <PARAM NAME=FlashVars VALUE="ruta_so=<%=colexrb.getFilesURL()%><%=f.getValue()%>">
                        <param name="quality" value="high" />
                        <param name="bgcolor" value="#ffffff" />
                        <embed src="sound_player.swf" FlashVars="ruta_so=<%=colexrb.getFilesURL()%><%=f.getValue()%>" 
                        quality="high" bgcolor="#ffffff" width="42" height="42" name="sound_player" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
                        <br>
                        <div class="soundFile"> <%=edu.xtec.colex.utils.Utils.getFileName(f.getValue())%> </div>
                        
                        </object>
                    <%}%>
                        
                    
                    <span id="editFd_<%=i%>" name="editFd_<%=i%>" style="display:none;">
                    <%if (!f.getValue().equals("null"))
                    {%>
                        <a> <img  border = "0" id="sound_<%=i%>" name="sound_<%=i%>" src="img/icons/newsound.png" alt="<%=colexrb.getMessage("sc.has.sound")%>" /></a>
                    
                        <div id="soundFile_<%=i%>" name="soundFile_<%=i%>" class="soundFile"> <%=edu.xtec.colex.utils.Utils.getFileName(f.getValue())%> </div>
                    <%}
                    else
                    {%>
                        <div id="soundFile_<%=i%>" name="soundFile_<%=i%>" class="soundFile"> Cap </div>
                    <%}%>
                    
                    <input type="file" onchange="javascript:newSound(this.value,'<%=i%>');" id="fd_<%=i%>" name="fd_<%=i%>" value="" alt="<%=colexrb.getMessage("sc.alt.fileName")%>"/>
                    <br>
                    <a href="javascript:deleteSound('<%=i%>');"> <%=colexrb.getMessage("sc.title.delete")%> </a>
                    <input type="hidden" id="del_<%=i%>" name="del_<%=i%>" value="false"/>
                    </span>
                    
                <%}
                else if (sType.equals("date"))
                {
                    edu.xtec.colex.domain.FieldDefDate fdDate;%>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
            
                    <span>
                    <input size="2" maxlength="2" type="text" id="fdDD_<%=i%>" name="fdDD_<%=i%>" readonly="readonly" title="<%=colexrb.getMessage("sc.title.day")%>" alt="<%=colexrb.getMessage("sc.title.day")%>" 
                    
                    <%if (!bFaultModify) {%> value="<%=edu.xtec.colex.domain.FieldDefDate.getDD(f.getValue())%>" <%}
                    else {%>  value="<%=colexrb.pmRequest.getParameter("fdDD_"+fdAux.getId())%>"<%}%>
                    />                
                    - <input size="2" maxlength="2" type="text" id="fdMM_<%=i%>" name="fdMM_<%=i%>" readonly="readonly" title="<%=colexrb.getMessage("sc.title.month")%>" alt="<%=colexrb.getMessage("sc.title.month")%>" 
                    <%if (!bFaultModify) {%> value="<%=edu.xtec.colex.domain.FieldDefDate.getMM(f.getValue())%>" <%}
                    else {%>  value="<%=colexrb.pmRequest.getParameter("fdMM_"+fdAux.getId())%>"<%}%> 
                    />
                    - <input size="4" maxlength="4" type="text" id="fdYYYY_<%=i%>" name="fdYYYY_<%=i%>" readonly="readonly" title="<%=colexrb.getMessage("sc.title.year")%>" alt="<%=colexrb.getMessage("sc.title.year")%>" 
                    <%if (!bFaultModify) {%> value="<%=edu.xtec.colex.domain.FieldDefDate.getYYYY(f.getValue())%>" <%}
                    else {%>  value="<%=colexrb.pmRequest.getParameter("fdYYYY_"+fdAux.getId())%>"<%}%>  
                    />

                    <a id="calFd_<%=fdAux.getId()%>" href="javascript:openCalendar('formRecord','fd','<%=fdAux.getId()%>')" style="visibility:hidden;" title="<%=colexrb.getMessage("sc.title.calendar")%>"> <img src="img/icons/date.gif" alt="<%=colexrb.getMessage("sc.title.calendar")%>"/> </a>
                    <img id="infoFd_<%=fdAux.getId()%>"  style="visibility:hidden;" src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%>" alt="<%=colexrb.getMessage(sType)%>">
                
                    </span>
                <%}
                //If type is boolean we use a checkbox and an input hidden, then javascript
                //sets the value in the hidden
                else if (sType.equals("boolean"))
                {

                    boolean bValue=false;
                    
                    if (!bFaultModify)
                    {
                        if (f.getValue().equals("true")) bValue=true;
                    }
                    else 
                    {
                        if (colexrb.pmRequest.getParameter("fd_"+fdAux.getId()).equals("true")) bValue=true;
                    }
                            
                    %>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                
                    <input type="checkbox" onchange="javascript:changeCheckBox('formRecord','fd_<%=i%>')" disabled="true" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>" 
                    <% if (bValue) {%> checked="true" <%}%>
                    />
                    <input type="hidden" id="fd_<%=i%>" name="fd_<%=i%>" value="<%=bValue%>"/>
                <%}
                else if (sType.equals("text"))
                {
                    edu.xtec.colex.domain.FieldDefText fdText;
                    fdText = (edu.xtec.colex.domain.FieldDefText) fdAux;%>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                   
                    <%if(fdText.getLength()<=20)
                    {%>
                        <input type="text" id="fd_<%=i%>" name="fd_<%=i%>" size="<%=f.getValue().trim().length()%>" readonly="readonly" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>"
                    <%if (!bFaultModify) {%> value="<%=f.getValue()%>" <%}
                    else {%>  value="<%=colexrb.pmRequest.getParameter("fd_"+fdAux.getId())%>"<%}%>
                    />                
                    <%}
                    else
                    {
                        int iRows = fdText.getLength()/35;
                        if (iRows == 0) iRows++;
                        else if (iRows > 5) iRows = 5;
                    %>
                    
                    <textarea cols="35" rows="<%=iRows%>" id="fd_<%=i%>" name="fd_<%=i%>" size="<%=f.getValue().trim().length()%>" readonly="readonly"
                    ><%if (!bFaultModify) {%><%=f.getValue()%><%}
                    else {%><%=colexrb.pmRequest.getParameter("fd_"+fdAux.getId())%><%}%></textarea>
                    
                    <%}
                
                
                }
                else if (sType.equals("select"))
                {
                    edu.xtec.colex.domain.FieldDefSelect fdSelect;
                    fdSelect = (edu.xtec.colex.domain.FieldDefSelect) fdAux;
                    %>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                    
                    <select id="fd_<%=i%>" name="fd_<%=i%>" size=1 style="width:150px;" disabled>
                        
                        <%
                        edu.xtec.colex.domain.Property p;
                        java.util.Vector vProp = fdAux.getVProperties();
                        
                        for(int j=0;j<vProp.size();j++)
                        {
                            p = (edu.xtec.colex.domain.Property) vProp.get(j);%>
             
                            <option value="<%=p.getValue()%>"
                            <%
                               if (bFaultModify)
                               {
                                    if(p.getValue().equals(colexrb.pmRequest.getParameter("fd_"+fdAux.getId()))){%> selected <%}
                               }
                               else if (p.getValue().equals(f.getValue())){%> selected <%}%>

                               ><%=p.getValue()%> </option>
                        <%}%>
                        
                        <option value="<%=fdAux.getDefaultValue()%>" <%if (f.getValue().equals(fdAux.getDefaultValue())){%> selected <%}%> > <%=colexrb.getMessage(fdAux.getDefaultValue())%> </option>
                        
                    </select>
                <%}
                else if (sType.equals("link"))
                {
                    edu.xtec.colex.domain.FieldDefLink fdLink;
                    fdLink = (edu.xtec.colex.domain.FieldDefLink) fdAux;
                    %>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                
                    <a href="<%=fdLink.addHttp(f.getValue())%>" target="_sblank" style="text-decoration:underline;font-weight:normal;padding:4px 0px 4px 4px;"><%=fdLink.trim(f.getValue(),30)%></a>
                    <input type="text" id="fd_<%=i%>" name="fd_<%=i%>" size="<%=f.getValue().trim().length()%>" readonly="readonly" style="display:none;" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>"
                    <%if (!bFaultModify) {%> value="<%=f.getValue()%>" <%}
                    else {%>  value="<%=colexrb.pmRequest.getParameter("fd_"+fdAux.getId())%>"<%}%>/>
                <%}
                else if (sType.equals("html"))
                {   
                    edu.xtec.colex.domain.FieldDefHtml fdHtml;
                    fdHtml = (edu.xtec.colex.domain.FieldDefHtml) fdAux;
                    %>
                    <tr>
                    <td class="rightTD" style="border-bottom: 0px" valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD" style="border-bottom: 0px">
                    
                    </td>
                    </tr>
            
                    <tr> <td colspan=2>
                    <div class="divHtml">
                        
                    <%=f.getValue()%>
                    </div> 
                    
                    <textarea cols="45" rows="10" id="fd_<%=i%>" name="fd_<%=i%>" style="display:none; width:90%;"
                            ><%if (!bFaultModify) {%><%=f.getValue()%><%}
                    else {%><%=colexrb.pmRequest.getParameter("fd_"+fdAux.getId())%><%}%></textarea>
                <%}
                else
                {%>
                    <tr>
                    <td class="rightTD"  valign="top"> <h4> <%= f.getName() %> </h4> </td>
                    <td class="leftTD">
                    
                    <input type="text" id="fd_<%=i%>" name="fd_<%=i%>" size="<%=f.getValue().trim().length()%>" readonly="readonly" alt="<%=colexrb.getMessage("sc.alt.fieldValue")%>"
                    <%if (!bFaultModify) {%> value="<%=f.getValue()%>" <%}
                    else {%>  value="<%=colexrb.pmRequest.getParameter("fd_"+fdAux.getId())%>"<%}%>
                    />                
                <%}

                
                //To create information of the field
                if (sType.equals("integer"))
                {
                    String sProperties = fdAux.getProperties();
                    edu.xtec.colex.domain.FieldDefInteger fdI;
                    fdI = (edu.xtec.colex.domain.FieldDefInteger) fdAux;
                    
                    String sUnit = fdI.getUnit();
                    
                    if (!sUnit.equals("")) {%> (<%=sUnit%>) <%}
                    %>
                    <img id="infoFd_<%=fdAux.getId()%>" style="visibility:hidden;" src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%> [<%=fdI.getMin()%>,<%=fdI.getMax()%>]" alt="<%=colexrb.getMessage(sType)%> [<%=fdI.getMin()%>,<%=fdI.getMax()%>]"/>
                    
                <%}
                else if (sType.equals("decimal"))
                {
                    String sProperties = fdAux.getProperties();
                    edu.xtec.colex.domain.FieldDefDecimal fdD;
                    fdD = (edu.xtec.colex.domain.FieldDefDecimal) fdAux;
                                        
                    String sUnit = fdD.getUnit();
                    
                    if (!sUnit.equals("")) {%> (<%=sUnit%>) <%}
                    %>
                <img id="infoFd_<%=fdAux.getId()%>" style="visibility:hidden;" src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%> [<%=fdD.getMin()%>,<%=fdD.getMax()%>]" alt="<%=colexrb.getMessage(sType)%> [<%=fdD.getMin()%>,<%=fdD.getMax()%>]"/>
                <%}
                else if (sType.equals("text"))
                {
                    edu.xtec.colex.domain.FieldDefText fdText;
                    fdText = (edu.xtec.colex.domain.FieldDefText) fdAux;

                %>
                
                <img id="infoFd_<%=fdAux.getId()%>" style="visibility:hidden;" src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%> (<%=colexrb.getMessage("length")%> = <%=fdText.getLength()%>)" alt="<%=colexrb.getMessage(sType)%> (<%=colexrb.getMessage("length")%> = <%=fdText.getLength()%>)"/>

                <%}
                else if (sType.equals("link"))
                {%>
                    <img id="infoFd_<%=fdAux.getId()%>" style="visibility:hidden;" src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%>" alt="<%=colexrb.getMessage(sType)%>"/>
                
                <%}
                else if (sType.equals("html"))
                {%>
                    <img id="infoFd_<%=fdAux.getId()%>" style="visibility:hidden;" src="img/icons/info.png" title="<%=colexrb.getMessage(sType)%>" alt="<%=colexrb.getMessage(sType)%>"/>
                
                <%}%>
                
                
            
            
            </td>
            </tr>
            <%}%>
        </form>
        </table>
        
    <%}%>
    </div>
    </div>
    <div id="bottom">
        
        <div id="itemsEditRecord">
        <a href="#" onclick="javascript:cancelModifyRecord();"> <img src="img/icons/cancel.jpg" alt="<%=colexrb.getMessage("sc.title.cancel")%>"> </img> </a></a>
            <a href="#" onclick="javascript:acceptModifyRecord();">  <img src="img/icons/ok.jpg" alt="<%=colexrb.getMessage("sc.title.accept")%>"> </img> </a> </a>
        </div>
        
        <div id="itemsAddRecord">
            <a href="#" onclick="javascript:cancelAddRecord();"> <img src="img/icons/cancel.jpg" alt="<%=colexrb.getMessage("sc.title.cancel")%>"> </img> </a>
            <a href="#" onclick="javascript:acceptAddRecord();">  <img src="img/icons/ok.jpg" alt="<%=colexrb.getMessage("sc.title.accept")%>"> </img> </a>
        </div>
        
        <%if (colexrb.getNumFound()!=0)
        {%>
        
        <div id="browser">
            <table>
            <tr>
                <td id="prev">
                <% if (colexrb.getQuery().getBeginIndex() !=0)
                {%>
                <a id ="linkPrev" href="javascript:decBegin();" title="<%=colexrb.getMessage("sc.title.prev")%>"> <img src="img/icons/prev.png" alt="<%=colexrb.getMessage("sc.title.prev")%>"/></a>
                <%}%>
                </td>                
                <td id="index"> <%=colexrb.getMessage("sc.index.record")%> <%=query.getBeginIndex()+1%> <%=colexrb.getMessage("sc.index.of")%> <%=colexrb.getNumFound()%> <%=colexrb.getMessage("sc.index.found")%> </td>    
                <td id="next">
                <% if ( colexrb.getQuery().getBeginIndex() < colexrb.getNumFound()-1)
                {%>        
                <a id="linkNext" href="javascript:incBegin();" title="<%=colexrb.getMessage("sc.title.next")%>"> <img src="img/icons/next.png" alt="<%=colexrb.getMessage("sc.title.next")%>"/></a>
                <%}%>
                </td>
            </tr>
            
            </table>
        </div>
        <%}%>
    
        
    
    </div>
        </div>
    </div>
    
    <div id="divSearch" class="rightDown">
    <jsp:include page="search.jsp" flush="true"/>
    </div>
    
    <%}%>
    
<%}%> <%-- No exists collection --%>
    </form>
    </div>

        <script type="text/javaScript">
         
        <%if (colexrb.getNumFound() == 0) 
        {%>
            
            disableLink('linkModify');
            disableLink('linkDelete');
            disableLink('linkPrint');
            disableLink('linkShowTable');
        <%}%>
        
        
        var permission =  <%=colexrb.retrievePermission()%>;
        
        switch(permission)
        {
        
            case <%=edu.xtec.colex.domain.Guest.PERMISSION_READ%>:
                disableLink('linkModify');
                disableLink('linkDelete');
                disableLink('linkAdd');
                document.getElementById('coverRightUpRecord').style.display='block';
                break;
            case <%=edu.xtec.colex.domain.Guest.PERMISSION_NONE%>:
                disableLink('linkModify');
                disableLink('linkDelete');
                disableLink('linkAdd');
                document.getElementById('coverRightUpRecord').style.display='block';
                break;
        
        }
        
        <% if (colexrb.getOwner()!=null)
        {%>
            
        <%}%>
        
        
        <% if (colexrb.getRecordSize().equals("Normal"))
        {%>
        // Scirpt to check if expand button is enabled or disabled
        
        if (document.getElementById("divAddRecord")!=null)
        //Check case there is no field definitions
        {
            changeSize();
        
            var divR = document.getElementById("divRecord");
            divRSize = Element.getDimensions(divR).height;
        
            changeSize();
        
            if (divRSize < 293)
            {
                disableLink('linkExpand');
                expandDisabled = 1;
            }
        }
        <%}%>
        

        <%if (colexrb.getFault("add")!="")
        {%>
            addRecord();
            Dialog.alert("<%=colexrb.getMessage(colexrb.getFault("add"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
        <%}
        else if (colexrb.getFault("delete")!="")
        {%>
            Dialog.alert("<%=colexrb.getMessage(colexrb.getFault("delete"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
        <%}
        else if (colexrb.getFault("modify")!="")
        {
            if (!colexrb.getFault("modify").equals("NO_EXISTS_MODIFY_RECORD"))
            {%>
            modifyRecord();
            <%}%>
            Dialog.alert("<%=colexrb.getMessage(colexrb.getFault("modify"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
        <%}
        else if (colexrb.getFault("importRecords")!="")
        {%>
            Dialog.alert("<%=colexrb.getMessage(colexrb.getFault("importRecords"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
        <%}
        else if (colexrb.getFault("search")!="")
        {
            if (colexrb.getFault("search").equalsIgnoreCase("MALFORMED_QUERY"))
            {%>
                editSearch();
                Dialog.alert("<%=colexrb.getMessage(colexrb.getFault("search"))%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"});                 
    
                var s_blank = document.formSearch.op_1;
                s_blank.style.display ='none';
            <%}
            else if (colexrb.getFault("search").equalsIgnoreCase("NO_RECORD_FOUND"))
            {
                if (colexrb.getRecordSize().equals("Extended"))
                {%>
                    changeSize();
                    
                <%}%>
                disableLink('linkExpand');
                expandDisabled = 1;
            <%}
        }%>
        
        <%-- To edit/add records from table.jsp --%>
        
        <%if (colexrb.pmRequest.getParameter("subOperation")!=null)
        {
            if (colexrb.pmRequest.getParameter("subOperation").equals("modify"))
            {%>
                
                modifyRecord();
            <%}
            else if (colexrb.pmRequest.getParameter("subOperation").equals("add"))
            {%>
            
                addRecord();
            <%}
        }%> 
        
        
    </script>
    <script  language="javascript">   <%=scriptEnd+"}"%></script>
    
    </body>

</html>
<%}%>