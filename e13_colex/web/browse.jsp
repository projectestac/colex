<%@page session="false" contentType="text/html; charset=iso-8859-1"%>
<jsp:useBean id="colexbb" class="edu.xtec.colex.client.beans.ColexBrowseBean" scope="request"/>
<%if(!colexbb.init(request, response)){%>ERROR<%}%>
<script src="js/scriptAculo/prototype.js" type="text/javascript"></script>
<script src="js/scriptAculo/scriptaculous.js?load=effects" type="text/javascript"></script>
<%int COLLECTIONS_PAGE = 8;%>
<script>
    
    function browse()
    {
        document.formBrowser.orderBy.value = 'nameCollection';
        document.formBrowser.direction.value = 'asc';
        document.formBrowser.indexBegin.value = 1;
        document.formBrowser.indexEnd.value = <%=COLLECTIONS_PAGE%>;
        document.formBrowser.operation.value = 'browse';
        document.formBrowser.submit();
    }
    
    
    function orderBy(orderBy,direction)
    {
        document.formBrowser.orderBy.value = orderBy;
        document.formBrowser.direction.value = direction;
        document.formBrowser.indexBegin.value = 1;
        document.formBrowser.indexEnd.value = <%=COLLECTIONS_PAGE%>;
        document.formBrowser.operation.value = 'browse';
        document.formBrowser.submit();
    }
    
    function setIndexs(indexBegin,indexEnd)
    {
        document.formBrowser.indexBegin.value = indexBegin;
        document.formBrowser.indexEnd.value = indexEnd;
        document.formBrowser.operation.value = 'browse';
        document.formBrowser.submit();
    }
    
    
    function openDescription(id)
    {
        div = document.getElementById('div_'+id);
        link =  document.getElementById('link_'+id);
        
        if (!Element.visible(div)) 
        {
            Effect.BlindDown(div);
            link.innerHTML='&nbsp;-';
        } 
        else 
        {
            Effect.BlindUp(div);
            link.innerHTML='+';
        }
    }
    
    function browseTag(tag)
    {
        document.formBrowser.browseBy.value = 'tags';
        document.formBrowser.value.value = tag;
        document.formBrowser.orderBy.value = 'nameCollection';
        document.formBrowser.direction.value = 'asc';
        document.formBrowser.indexBegin.value = 1;
        document.formBrowser.indexEnd.value = <%=COLLECTIONS_PAGE%>;
        document.formBrowser.operation.value = 'browse';
        document.formBrowser.submit();
    }
    
    function getAllTags()
    {
        document.formBrowser.operation.value = 'allTags';
        document.formBrowser.submit();
    }

</script>



<form name="formBrowser" method="POST">

    <% 
    int iNumFound = colexbb.getNumFound();
    edu.xtec.colex.domain.BrowseCriteria bc;
    bc = colexbb.getBrowseCriteria();
    java.util.Vector vBrowseBy = bc.getVBrowseBy();
    java.util.Vector vOrderBy = bc.getVOrderBy();
    
    java.util.Vector vTags = colexbb.retrieveTagClouds();
    edu.xtec.colex.domain.Tag tag;

    %>
    
    <input type="hidden" name="operation" value="browse"/>
    <input type="hidden" name="indexBegin" value="<%=bc.getIndexBegin()%>"/>
    <input type="hidden" name="indexEnd" value="<%=bc.getIndexEnd()%>"/>
    <input type="hidden" name="orderBy" value="<%=bc.getOrderBy()%>"/>
    <input type="hidden" name="direction" value="<%=bc.getDirection()%>"/>
    
    
<% if (!colexbb.isAllTags())
{%><div> 

    <div id="divBrowse">
    <h3> <%=colexbb.getMessage("sc.search.collections")%> </h3>
    <select name="browseBy" id="browseBy">
        
        <% for (int i=0;i<vBrowseBy.size();i++)
        {
            String sBrowseBy = (String) vBrowseBy.get(i);%>
            
            <option value="<%=sBrowseBy%>" <%if (bc.getBrowseBy().equals(sBrowseBy)){%>selected<%}%>> <%=colexbb.getMessage(sBrowseBy)%> </option>
        <%}%>
        
    </select>
    
    <input name="value" type="text" value="<%=bc.getValue()%>" size="15" alt="<%=colexbb.getMessage("sc.alt.browseValue")%>" />
    
    
    <a title="<%=colexbb.getMessage("sc.title.search")%>" href="#" onclick="javascript:browse()"> <img alt="<%=colexbb.getMessage("sc.title.search")%>" src="img/icons/search.png"/> </a>
    
    <% if (iNumFound == 0) {%> <div style="color:#FF6D96;margin-top:15px;margin-left:10px;"> No s'ha trobat cap col·lecció </div> <%}%>
    
    </div>
    
    <div id="divSeparator"><img src="img/backgrounds/separator.png"/></div>

    
    <div id = "divClouds">
        <h3><%=colexbb.getMessage("sc.tag.clouds")%></h3>
        <%
           
           
        for (int i=0;i<vTags.size();i++)
        {
            tag =(edu.xtec.colex.domain.Tag) vTags.get(i);%>

            <a href="#" style="font-size:<%=tag.getFontSize()%>%;cursor:pointer;" onclick="javascript:browseTag('<%=tag.getValue()%>');" title="<%=colexbb.getMessage("sc.tags.found")%> <%=tag.getTimes()%>"><%=tag.getValue()%></a>

        <%}%>
        
       
            <a href="#" style="color:#FF6D96;" onclick="javascript:getAllTags();"><%=colexbb.getMessage("sc.more")%></a>
        
    </div>

    <%if (iNumFound > 0)
    {%>
    
        
       <div id="browserCenter">
<div id="browserTop">
    <div id="browserIndex">
        
        
        <span>
        <%=iNumFound%> <%=colexbb.getMessage("sc.found.collections")%> 
        </span>
        
         <%if (iNumFound > COLLECTIONS_PAGE)
        {%>
        <span> 

            <%if (bc.getIndexBegin()!=1){
                %><a href="#" onclick="javascript:setIndexs(1,<%=COLLECTIONS_PAGE%>);"> << </a>
            <%}
            else{%> << <%}%>
            
        </span>
        |
        
        <%for (int i=1; i<=iNumFound;i=i+COLLECTIONS_PAGE)
        {
        
            if (i == bc.getIndexBegin())
            {%>
                <span style="color:red;"><%=(i/COLLECTIONS_PAGE)+1%></span>
            <%}
            else
            {%>
                <span> <a href="#" onclick="javascript:setIndexs(<%=i%>,<%=i+COLLECTIONS_PAGE-1%>);"><%=(i/COLLECTIONS_PAGE)+1%></a> </span>
            <%}%>
            |
        <%}%>
                
              
        <span> 

            <%if (( bc.getIndexBegin() + COLLECTIONS_PAGE ) > iNumFound)
            {%>
                >>
            <%}
            else
            {%>
                <a href="#" onclick="javascript:setIndexs(<%=(iNumFound-(iNumFound%COLLECTIONS_PAGE))+1%>,<%=iNumFound%>);"> >> </a>
            <%}%>
        
        </span>
        <%}%> 
        
            
        </div>
    
</div>
        <table summary="<%=colexbb.getMessage("table.browse.summary")%>">
        <caption style="display:none;"><%=colexbb.getMessage("table.browse.caption")%></caption> 
        <thead>
        <tr>
        <% for (int i=0;i<vOrderBy.size();i++)
        {
            String sOrderBy = (String) vOrderBy.get(i);%>
           
            <th scope="col" class="th<%=sOrderBy%>">
            
            <%if (bc.getOrderBy().equals(sOrderBy))
            {

                if (bc.getDirection().equals("asc"))
                {%>
                    <a title="<%=colexbb.getMessage("sc.title.sort")%>" style="cursor:pointer;" onclick="javascript:orderBy('<%=sOrderBy%>','desc');"><%=colexbb.getMessage(sOrderBy)%></a> <img style="display:inline;" src="img/icons/asc.png" alt="<%=colexbb.getMessage("sc.sort.asc")%>" />
                <%}
                else
                {%>
                    <a title="<%=colexbb.getMessage("sc.title.sort")%>" style="cursor:pointer;" onclick="javascript:orderBy('<%=sOrderBy%>','asc');"><%=colexbb.getMessage(sOrderBy)%></a> <img style="display:inline;" src="img/icons/desc.png" alt="<%=colexbb.getMessage("sc.sort.desc")%>" />
                <%}
            }
            else
            {%>
                <a title="<%=colexbb.getMessage("sc.title.sort")%>" style="cursor:pointer;" onclick="javascript:orderBy('<%=sOrderBy%>','asc');"><%=colexbb.getMessage(sOrderBy)%></a>
            
            <%}%>
            
            </th>
           
        <%}%>
        
        <th scope="col" abbr=" ">  </th>

        </tr>
        </thead>
        
       
        <tbody>
    
        <%
           java.util.Vector vCollections = colexbb.retrieveBrowse();
           edu.xtec.colex.domain.Collection collection;
           edu.xtec.colex.domain.User user;
           
        int i=0;
        int j=i;
        for (;j<vCollections.size();j=j+2)
        {
            collection = (edu.xtec.colex.domain.Collection) vCollections.get(j);
            user = (edu.xtec.colex.domain.User) vCollections.get(j+1);
                    
            if (i % 2 == 0)
            {
               %><tr class="odd"><%
            }
            else
            {
                %><tr class="even"><%
            }
            
            %>		
            
            <td class="tdnameCollection"> <a href="#browserBottom" title="<%=colexbb.getMessage("sc.title.details")%>"  id="link_<%=i%>" onclick="javascript:openDescription('<%=i%>');" style="cursor:pointer;">+</a> <%=collection.getName()%> </td>
            <td> <%=user.getUserId()%> </td>
            <td> <%=collection.getNumRecords()%> </td>
            <%java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");%>
            <td> <%=df.format(collection.getCreated())%> </td>
            <td> <a href="<%=colexbb.getLinkCollection(user.getUserId(),collection.getName())%>" target="s_blank" title="<%=colexbb.getMessage("sc.title.open")%>"> <img src="img/icons/openCollectionLittle.png" alt="<%=colexbb.getMessage("sc.title.open")%>"/> </a></td>
            </tr>
            
            <%if (i % 2 == 0)
            {
               %><tr class="odd" ><%
            }
            else
            {
                %><tr class="even"><%
            }
            
            %>
            
            
            <td colspan="5" abbr="<%=colexbb.getMessage("sc.title.details")%>"> 
            <div id="div_<%=i%>" style="display:none;text-align:left;font-weight:normal;font-size:90%;padding-bottom:5px;">
<hr class="hrClass"/>               
<div style="margin-left:30px;"><b><%=colexbb.getMessage("sc.description")%></b> <%=collection.getDescription()%></div>
               <br/>
               <div style="margin-left:30px;"><b><%=colexbb.getMessage("sc.tags")%></b> 
<% 
String sTag;
vTags = edu.xtec.colex.utils.Tags.toVector(collection.getTags());

for (int k=0;k<vTags.size();k++)
{
    sTag=(String)vTags.get(k);%>
    <a title="<%=colexbb.getMessage("sc.title.search")%>" onclick="javascript:browseTag('<%=sTag%>')" href="#"><%=sTag%></a>
<%}%>


<div>
                
            </div>
            </td>
            <%i++;%>
            
            </tr>
                
        <%}%>
        </tbody>
        
        </table>
        
         <div id="browserBottom">
        </div>
        </div>
        
       <script>$('browserBottom').scrollTo()</script>
        
        <%}%>
        
        
</div>

<%
    }else
        {%>
        
        <input name="value" type="hidden" value=""/>
        <input name="browseBy" type="hidden" value=""/>
        
        <div>
            
        <h3><%=colexbb.getMessage("sc.tag.clouds")%></h3>
        
        <div id="allTagsCenter">
         
        <div id="divAllTags">   
        <% 
        for (int i=0;i<vTags.size();i++)
        {
            tag =(edu.xtec.colex.domain.Tag) vTags.get(i);%>

        <a href="#" style="font-size:<%=tag.getFontSize()%>%;cursor:pointer;" onclick="javascript:browseTag('<%=tag.getValue()%>');" title="<%=colexbb.getMessage("sc.tags.found")%> <%=tag.getTimes()%>"><%=tag.getValue()%></a>

        <%}%>

        </div>
         </div>
        
        <div id="allTagsBottom">
            <a href="#" style="color:#FF6D96;position: absolute; right:10px; .right:20px; _right:20px;" onclick="javascript:history.go(-1)"><%=colexbb.getMessage("sc.back")%></a>
        </div>
        
        </div>
        
        <%}%>


</form>