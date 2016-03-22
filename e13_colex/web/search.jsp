<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexrb" class="edu.xtec.colex.client.beans.ColexRecordBean" scope="request" />
    
    <script type="text/javaScript">
    
            //Variables used in the javascript search.js
            sc_has_image='<%=colexrb.getMessage("sc.has.image")%>';
            sc_has_no_image='<%=colexrb.getMessage("sc.has.no.image")%>';
            sc_has_sound='<%=colexrb.getMessage("sc.has.sound")%>';
            sc_has_no_sound='<%=colexrb.getMessage("sc.has.no.sound")%>';
            sc_title_delete='<%=colexrb.getMessage("sc.title.delete")%>';
            sc_title_day='<%=colexrb.getMessage("sc.title.day")%>';
            sc_title_month='<%=colexrb.getMessage("sc.title.month")%>';
            sc_title_year='<%=colexrb.getMessage("sc.title.year")%>';
            sc_alt_fieldValue='<%=colexrb.getMessage("sc.alt.fieldValue")%>';
            

            <%
            java.util.Vector vFds = colexrb.retrievSearchableDefs();                 
            edu.xtec.colex.domain.FieldDef fd;

            String arrayNames ="new Array(";

            for (int i=0;i<vFds.size();i++)
            {
                fd = (edu.xtec.colex.domain.FieldDef) vFds.get(i);
                
                
                arrayNames = arrayNames+"\""+fd.getName()+"\",";
                
                
                java.util.Vector vCmp = colexrb.getComparators(fd.getType());
                String sCmp;
                String array = "new Array('"+fd.getType()+"',";
                                                
                for (int j=0;j<vCmp.size();j++)
                {
                    if (j!=0) array = array +",";
                   
                    sCmp = (String) vCmp.get(j);
                    
                    if (sCmp.equals("!="))
                    //Problem trying to write \u2260 directyle from java, so we make the conversion in javascript @-->\u2260
                    {
                        array = array + "new Array('"+sCmp+"','@')";
                    
                    
                    }
                    else if (sCmp.equals("LIKE"))
                    {
                        array = array + "new Array('"+sCmp+"','"+colexrb.getMessage("sc.like")+"')";
                    }
                    else
                    {
                        array = array + "new Array('"+sCmp+"','"+sCmp+"')";
                    }
                    
                }
                
                array = array+");";
                
                %>
                
                var comparators_<%=i%> = <%=array%>
                     
                for (var i=1;i<comparators_<%=i%>.length;i++)
                {
                    auxArray = comparators_<%=i%>[i];
                    for (j=0;j<auxArray.length;j++)
                    {
                        if (auxArray[j]=='@') auxArray[j]='\u2260';
                    }
                }
                
                
                <%if (fd.getType().equals("select")) 
                {
                    edu.xtec.colex.domain.FieldDefSelect fdSelect;
                    fdSelect = (edu.xtec.colex.domain.FieldDefSelect) fd;
                    %>
                    
                    var prop = '<%=fdSelect.getProperties()%>';
                    
                    var selectOptions_<%=i%> = prop.split('|');
                <%}
                        
                
            }

            arrayNames = arrayNames.substring(0,arrayNames.length()-1) +");";
            %>
            
            
            <%if (vFds.size() > 0) {%>
            var arrayNames = <%=arrayNames%>
            <%}
            else{%>
            var arrayNames = new Array();
            <%}%>
            
            
            
        function addCondition()
        {
            numConditions++;
            idConditions++;
            
            tr = document.createElement("tr");
            tr.setAttribute("id","cnd_"+idConditions);
            
            s_name = document.createElement("select");
            s_name.setAttribute("name","name_"+idConditions);            
            s_name.setAttribute("id","name_"+idConditions);            
                                   
            <%
            for (int i=0;i<vFds.size();i++)
            {
                fd = (edu.xtec.colex.domain.FieldDef) vFds.get(i);
            %>    
                s_name.options[<%=i%>] = new Option("<%=fd.getName()%>","<%=fd.getName()%>");
                            
            <%}%>
            
            s_cmp = document.createElement("select");
            s_cmp.setAttribute("name","cmp_"+idConditions);            
            s_cmp.setAttribute("id","cmp_"+idConditions);
            
            
            i_value = document.createElement("input");
            i_value.setAttribute("type","text");
            i_value.setAttribute("name","value_"+idConditions);
            i_value.setAttribute("id","value_"+idConditions);
            
            td_op = document.createElement("td");
            td_op.setAttribute("id","tdOp_"+idConditions);
            td_op.setAttribute("name","tdOp_"+idConditions);            
            
            if (numConditions!=1)
            {
                s_op = document.createElement("select");
                s_op.setAttribute("id","op_"+idConditions);
                s_op.setAttribute("name","op_"+idConditions);
                s_op.options[0] = new Option("AND","AND");
                s_op.options[1] = new Option("OR","OR");
                
                td_op.appendChild(s_op);
            }
            else
            {
                s_blank = document.createElement("select");
                
                s_blank.setAttribute("id","op_"+idConditions);
                s_blank.setAttribute("name","op_"+idConditions);
               
                s_blank.options[0] = new Option("AND","AND");
                s_blank.options[1] = new Option("OR","OR");
                
                Element.addClassName(s_blank,"s_blank"); 
                td_op.appendChild(s_blank);
            }
            
            tr.appendChild(td_op);
            
            td_name = document.createElement("td");
            td_name.setAttribute("id","tdName_"+idConditions);
            td_name.setAttribute("name","tdName_"+idConditions);            
            td_name.appendChild(s_name);
            tr.appendChild(td_name);
                        
            td_cmp = document.createElement("td");
            td_cmp.setAttribute("id","tdCmp_"+idConditions);
            td_cmp.setAttribute("name","tdCmp_"+idConditions);            
            td_cmp.appendChild(s_cmp); 
            tr.appendChild(td_cmp);
            
            td_value = document.createElement("td");
            td_value.setAttribute("id","tdValue_"+idConditions);
            td_value.setAttribute("name","tdValue_"+idConditions);            
            tr.appendChild(td_value);
            
            td_a = document.createElement("td");
            td_a.setAttribute("id","tdErase_"+idConditions);
            td_a.setAttribute("name","tdErase_"+idConditions);
            tr.appendChild(td_a);
            
            document.getElementById("tabBodyConditions").appendChild(tr);
            
            var aux = idConditions; //to Avoid having a global variable
         
            Event.observe(s_name, 'change', function(){changeCmp(aux)} , false);
            
            
            changeCmp(aux);
            
            con = document.getElementById("conditions");
            con.scrollTop = con.scrollHeight;
            
            
        }
        
        function editSearch()
        {
            disableLink("linkExport");
            disableLink('linkEditSearch');
        
            hideObjects();
 
            var divQuery = document.getElementById("editSearch");            
            divQuery.style.display="block";
             
            var divQueryBackground = document.getElementById("editSearchBackground");            
            divQueryBackground.style.display="block";
        }
        
        function acceptSearchRecord()
        {
            var e = document.getElementsByTagName("input");
            
            for(var i=0;i<e.length;i++)
            {
                if (e[i].type=="hidden")
                {
                    name = e[i].name;
                    
                    if (name.indexOf('valueDate')==0)
                    {
                        pos = name.indexOf('_');
                        
                        iCond = name.substring(pos+1,name.length);
                                                
                        var day = document.getElementById("valueDD_"+iCond).value;
                        var month = document.getElementById("valueMM_"+iCond).value;
                        var year = document.getElementById("valueYYYY_"+iCond).value;
                        
                        e[i].setAttribute("name","value_"+iCond);
                        e[i].setAttribute("id","value_"+iCond);
                        e[i].setAttribute("value",year+"-"+month+"-"+day);
                        
                    }
                }
            }
            

            document.formSearch.submit();
        }
        
        
        function cancelSearchRecord()
        /*Not in use*/        
        {
            
            enableLink('linkExport');
            
            <%if (colexrb.getNumRecords() != 0) 
            {%>
        
                            
            <%}%>
            
            <%if (colexrb.getNumFound() != 0) 
            {%>
                
                enableLink('linkModify');
                enableLink('linkDelete');
            <%}%>
            
            <% if (colexrb.getOwner()!=null)
            {%>
                disableLink('linkModify');
                disableLink('linkDelete');
                disableLink('linkAdd');
            <%}%>
        }
        
        
        function cancelEditSearch()
        {
            enableLink('linkExport');
            enableLink('linkEditSearch');
            
            var divQuery = document.getElementById("editSearch");            
            divQuery.style.display="none";
            
            var divQueryBackground = document.getElementById("editSearchBackground");            
            divQueryBackground.style.display="none";
           
            condicions = document.getElementById("conditions");
            
            numConditions=0;
            idConditions=0;       
            
            condicions.removeChild(document.getElementById("tabConditions"));
            condicions.removeChild(document.getElementById("linkAddCond"));
            
            buildQuery();    
            showObjects();
           
        }
        
        function queryToText()
        /* Not in use */
        {
            var queryStr = document.getElementById("queryString");
            
            divIni = queryStr.firstChild.nextSibling;
                
            divRes = document.createElement("div");
            divRes.setAttribute("id","queryString");
            
            var table = document.getElementById("tabConditions");
            
            var conditions = table.getElementsByTagName("tr");
            
            if (conditions.length==0) 
            {
                span=document.createElement("span");
                span.appendChild(document.createTextNode(" No hi ha consulta "));
                divRes.appendChild(span);
            }
            
            for (var i=0;i<conditions.length;i++)
            {
                tds = conditions[i].getElementsByTagName("td");
                                          
		if (i!=0)
		{
                    br1 = document.createElement("br");
                    divRes.appendChild(br1);
                    
                    span=document.createElement("span");
                    span.appendChild(document.createTextNode(tds[0].firstChild.value));
                    
                    divRes.appendChild(span);

                    br2 = document.createElement("br");
                    divRes.appendChild(br2);
                }
                span=document.createElement("span");
                span.appendChild(document.createTextNode(tds[1].firstChild.value+" "+tds[2].firstChild.value+" "+tds[3].firstChild.value));
                divRes.appendChild(span);
                
            }
            
            var divQuery = document.getElementById("editSearch");            
            divQuery.style.display="none";
            
            document.formSearch.replaceChild(divRes,queryStr);
            
        }
        
        function buildQuery()
        {
            condicions = document.getElementById("conditions");
            table = document.createElement("table");
            table.setAttribute("id","tabConditions");
            
            
            tableBody = document.createElement("tbody");
            tableBody.setAttribute("id","tabBodyConditions");
            table.appendChild(tableBody);
            
            condicions.appendChild(table);
            
            
        
            <% edu.xtec.colex.domain.Query query = colexrb.getQuery();
            java.util.Vector vCond = query.getConditions();
            edu.xtec.colex.domain.Condition cond;
           
            for (int i=0;i<vCond.size();i++)
            {
                cond = (edu.xtec.colex.domain.Condition) vCond.get(i);
            %>
              
            addCondition();
            
            document.formSearch["name_<%=i+1%>"].value="<%=cond.getFieldName()%>";
            
            changeCmp("<%=i+1%>");
            
            document.formSearch["cmp_<%=i+1%>"].value="<%=cond.getComparator()%>";
                
            nameField = "<%=cond.getFieldName()%>";

            for (j=0;j<arrayNames.length;j++)
            {

// XTEC ********** AFEGIT -> Prepare strings to be matched
// 2011.05.12 @mmartinez
            	tmpNameField = nameField.replace("\(", "¡¡");
            	tmpNameField = tmpNameField.replace("\)", "!!");
            	tmpArrayNames = arrayNames[j].replace("\(", "¡¡");
            	tmpArrayNames = tmpArrayNames.replace("\)", "!!");
// ********** FI
                
// XTEC ********** MODIFICAT -> Match prepared strings
// 2011.05.12 @mmartinez
            	if (tmpNameField.match(tmpArrayNames))
// ********* ORIGINAL
            	//if (nameField.match(arrayNames[j]))
// ********* FI 
                {
                	break
                }
            }
            
            var cmp =eval("comparators_"+j);
                     
            if(cmp[0]=="date")
            {
            
                var date = "<%=cond.getValue()%>";
                
                var dateArray = new Array();
                
                dateArray = date.split('-');
                
                var DD = dateArray[2];
                var MM = dateArray[1];
                var YYYY = dateArray[0];
                
                document.formSearch["valueDD_<%=i+1%>"].value = DD;
                document.formSearch["valueMM_<%=i+1%>"].value = MM;
                document.formSearch["valueYYYY_<%=i+1%>"].value = YYYY;
                
            }
            else
            {
                document.formSearch["value_<%=i+1%>"].value="<%=cond.getValue()%>";
            
                
            
                if (cmp[0]=="boolean")
                {
                    <%if (cond.getValue().equals("true"))
                    {%>
                        document.formSearch["check_<%=i+1%>"].checked=true;
                <%}
                    else{%> var a;<%}%>
    
                }
            }
            
            
            <%if (i!=0) 
            {%>
                document.formSearch["op_<%=i+1%>"].value="<%=cond.getOperator()%>";
            <%}%>
            
        <%}%>
        
        <%if (vCond.size()==0)
        {%>
            addCondition();
        <%}%>
        
        addCond = document.createElement("a");
        addCond.setAttribute("href","#");
        addCond.setAttribute("id","linkAddCond");
        addCond.setAttribute("title","<%=colexrb.getMessage("sc.title.add.condition")%>");
        
        imgAdd = document.createElement("img");
        imgAdd.setAttribute("src","img/icons/add.jpg");
        imgAdd.setAttribute("alt","<%=colexrb.getMessage("sc.title.add.condition")%>");
        imgAdd.setAttribute("height","20");
        imgAdd.setAttribute("width","20");
        
        addCond.appendChild(imgAdd);
        
        Event.observe(imgAdd, 'click', function(){addCondition()} , false);
        
        condicions.appendChild(addCond);
    }
   
    function openExportRecords()
    {
        disableLink('linkExport');
        disableLink('linkEditSearch');
        hideObjects();
 
        var divExportRecords = document.getElementById("divExportRecords");            
        divExportRecords.style.display="block";
            
        var divQueryBackground = document.getElementById("exportRecordsBackground");            
        divQueryBackground.style.display="block";
    }
    
    function cancelExportRecords()
    {
        enableLink('linkEditSearch');
        enableLink('linkExport');
        var divExportRecords = document.getElementById("divExportRecords");            
        divExportRecords.style.display="none";
            
        var divQueryBackground = document.getElementById("exportRecordsBackground");            
        divQueryBackground.style.display="none";
        
        showObjects();
    }
    
    function acceptExportRecords()
    {
        for (i=0;i<document.formExportRecords.exportOp.length;i++)
        {
            if (document.formExportRecords.exportOp[i].checked)
            break;
        }
    
        
        
        SelectedOp = document.formExportRecords.exportOp[i].value 
        
        switch(SelectedOp) 
        {
            case 'query':   
                            <%if (colexrb.getNumRecords() == 0) 
                            {%>            
                                Dialog.alert("<%=colexrb.getMessage("EMPTY_COLLECTION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                            <%}
                            else if (colexrb.getNumFound() == 0) 
                            {%>            
                                Dialog.alert("<%=colexrb.getMessage("NO_RECORD_FOUND")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                            <%}
                            else
                            {%>
                                cancelExportRecords();
                                document.formCollection.operation.value="export";
                                document.formCollection.submit();
                            <%}%>
                            break
            case 'all':     <%if (colexrb.getNumRecords() == 0) 
                            {%>            
                                Dialog.alert("<%=colexrb.getMessage("EMPTY_COLLECTION")%>", {windowParameters:{ width:350, height:130},okLabel: "d&rsquo;acord"}); 
                            <%}
                            else
                            {%>
                                cancelExportRecords();
                                document.formCollection.operation.value="exportAll";
                                document.formCollection.submit();
                            <%}%>
                            break
            
            case 'structure':   
                                cancelExportRecords();
                                document.formCollection.operation.value="exportStructure";
                                document.formCollection.submit();
                                
            default:;     
        }
    

    }
    
    
    </script>
        
<script language="JavaScript" charset="iso-8859-1" src="js/search.js" > </script>
        
    <form name="formSearch" id="formSearch" action="record.jsp" method="POST">
        
        <input type="hidden" name="collection" id="colllection" value="<%=colexrb.getCollection()%>"/>
        <input type="hidden" name="operation" id="operation" value="search"/>
        <input type="hidden" name="numRecords" id="numRecords" value="<%=colexrb.getNumRecords()%>"/>
        <input type="hidden" name="recordSize" id="recordSize" value="<%=colexrb.getRecordSize()%>"/>
        
         <% if (colexrb.getOwner()!=null)
            {%>
            <input type="hidden" name="owner" id="owner" value="<%=colexrb.getOwner()%>"/>
            <%}
        %>

        <div>
        <h3> <%=colexrb.getMessage("sc.sort.conditions")%> </h3>
        
        
        </div>
        
        <div id="queryString"><h4>
        <% edu.xtec.colex.domain.Query q = colexrb.getQuery();

            java.util.Vector vConditions = q.getConditions();
            
            edu.xtec.colex.domain.Condition c;
            String sQuery="";
            
            if (vConditions.size()==0){%> <span> <%=colexrb.getMessage("sc.no.query")%> </span>  <%}
           
            for (int i=0;i<vConditions.size();i++)
            {
                c = (edu.xtec.colex.domain.Condition) vConditions.get(i);
                
                if (i!=0){%><br><span><%=c.getOperator().trim()%></span><br><%}%>
                
                <%
                String comparator = "";
                
                fd = colexrb.getFieldDef(c.getFieldName());
                   
                   if (fd.getType().equals("image"))
                   {
                       if (c.getComparator().equals("="))
                       {
                            comparator="'"+colexrb.getMessage("sc.has.no.image")+"'";
                       }
                       else comparator="'"+colexrb.getMessage("sc.has.image")+"'";%>  
                       
                       <span> <%=c.getFieldName()%> <%=comparator%> </span>
                   <%}
                   else if (fd.getType().equals("sound"))
                   {
                       if (c.getComparator().equals("="))
                       {
                            comparator="'"+colexrb.getMessage("sc.has.no.sound")+"'";
                       }
                       else comparator="'"+colexrb.getMessage("sc.has.sound")+"'";%> 
                       
                       <span><%=c.getFieldName()%> <%=comparator%> </span>
                  <%}
                   else
                   {
                        if(c.getComparator().equals("LIKE")) comparator="'"+colexrb.getMessage("sc.like")+"'";
                        else if (c.getComparator().equals("!=")) comparator="&ne;";
                        else comparator=c.getComparator();
                        
                        String sVal=c.getValue();
                        
                        if (fd.getType().equals("date"))
                        //To convert date format YYYY-MM-DD -> DD-MM-YYYY
                        {
                            String[] dateArray = sVal.split("-",3);
                            
                            sVal = dateArray[2]+"-"+dateArray[1]+"-"+dateArray[0];
                        }
                        
                    %>
                        <span><%=c.getFieldName()%> <%=comparator%> <%=sVal%></span>
                   <%}
            }%>
                        
        </h4>
        </div>
        
        <div id="itemsSearch" class="items">
        
        <span id="itemsEditSearch"> 
            <a title="<%=colexrb.getMessage("sc.title.edit.search")%>" style="position:absolute;" id="linkEditSearch" name="linkEditSearch" href="#" onclick="javascript:editSearch();"> <img src="img/icons/editSearch.jpg" alt="<%=colexrb.getMessage("sc.title.edit.search")%>" > </img> </a>       
            <a title="<%=colexrb.getMessage("sc.title.cancel.search")%>" style="position:absolute;left:50px;" id="linkShowAll" name="linkShowAll" href="#" onclick="javascript:showAll();"> <img src="img/icons/showAll.jpg" alt="<%=colexrb.getMessage("sc.title.cancel.search")%>" > </img></a> 
        </span>
            <a id="linkExport" name="linkExport" href="#" onclick="javascript:openExportRecords();"> <img alt="<%=colexrb.getMessage("sc.alt.export")%>" src="img/buttons/exportRecords.jpg"> </img>  </a> 
        </div>
    
        <% if (vConditions.size()==0){%> <script> disableLink('linkShowAll'); </script>  <%}%>
        
        
    <div id="editSearch">
    
    <h3> <%=colexrb.getMessage("sc.edit.search")%> </h3>
            
    <div id="conditions">
   
    
    </div>
    
    <div class="items">
        <a href="#" onclick="javascript:cancelEditSearch()"> <img alt="<%=colexrb.getMessage("sc.alt.cancel.search")%>" src="img/buttons/cancelYellow.jpg"> </img></a>
        <a href="#" onclick="javascript:acceptSearchRecord();"> <img alt="<%=colexrb.getMessage("sc.title.search")%>" src="img/buttons/search.jpg"> </img></a>
    </div>        
    
    </div>
    
    <div id="editSearchBackground">
    <img src="img/backgrounds/searchBack.jpg"> </img>
    </div>
    </form>
    
    <form name="formExportRecords">
    
    
    <div id="divExportRecords" name="divExportRecords">
    
    
        <h3> <%=colexrb.getMessage("sc.export.records")%> </h3>
     
        
        <h4> <%=colexrb.getMessage("sc.export.question")%> </h4>
        <br/>
        <input type="radio"  name="exportOp" value="query" checked="checked" alt="<%=colexrb.getMessage("sc.export.found")%>"> <%=colexrb.getMessage("sc.export.found")%> </input> <br/>
        <input type="radio"  name="exportOp" value="all" alt="<%=colexrb.getMessage("sc.export.all")%>"> <%=colexrb.getMessage("sc.export.all")%> </input> <br/>
        <input type="radio"  name="exportOp" value="structure" alt="<%=colexrb.getMessage("sc.export.structure")%>"> <%=colexrb.getMessage("sc.export.structure")%> </input>
    
        <div class="items">
            <a href="#" onclick="javascript:cancelExportRecords();"> <img alt="<%=colexrb.getMessage("sc.title.cancel")%>" src="img/buttons/cancelYellow.jpg"> </img></a>
            <a href="#" onclick="javascript:acceptExportRecords();"> <img alt="<%=colexrb.getMessage("sc.title.accept")%>" src="img/buttons/export.jpg"> </img></a>
        </div>        
        
    </div>
    
    <div id="exportRecordsBackground">
    <img src="img/backgrounds/exportBack.jpg"> </img>
    </div>
    </form>
    
    
    <script>
       <%if (vFds.size() > 0) {%> buildQuery();<%}
       else{%> disableLink('linkEditSearch');<%}%>
    </script>

