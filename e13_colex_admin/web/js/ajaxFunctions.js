    /**************************************************************************
        Utility Functions
    **************************************************************************/    

    function getParam(responseXML,paramName)
    {        
        if (responseXML.getElementsByTagName(paramName).length>0 && responseXML.getElementsByTagName(paramName)[0].firstChild!=null)
        {
            return responseXML.getElementsByTagName(paramName)[0].firstChild.nodeValue;
        }
        return "";
    }
    

    function getAttachment(responseXML,index)
    {  
        return responseXML.getElementsByTagName('attachment_'+index)[0];
    }

    /**************************************************************************
        Get Quota
    **************************************************************************/    

    function getQuota()
    {
        info = document.getElementById('modifyQuotaInfo');
        
        info.innerHTML = 'Searching...';

        info.style.display='block';
           
        var success = function(req){getQuotaComplete(req);}
        var failure = function(req){getQuotaFailed(req);}
    
        var url = 'ServletAdmin';
        
        var user = document.formModifyQuota.user.value;        
        
        var pars = 'operation=getQuota' + '&user=' + user;
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function getQuotaComplete(req)
    {
        info = document.getElementById('modifyQuotaInfo');
        info.style.display='none';
        
        document.formModifyQuota.oldQuota.value=getParam(req.responseXML,'quota');
        document.formModifyQuota.spaceUsed.value=getParam(req.responseXML,'spaceUsed');
        document.formModifyQuota.newQuota.value='';
    }

    function getQuotaFailed(req)
    {
        info = document.getElementById('modifyQuotaInfo');
        info.style.display='none';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }

    /**************************************************************************
        Modify Quota
    **************************************************************************/    
    
    function modifyQuota()
    {
        info = document.getElementById('modifyQuotaInfo');
        
        info.innerHTML = 'Processing...';
           
        var success = function(req){modifyQuotaComplete(req);}
        var failure = function(req){modifyQuotaFailed(req);}
    
        var url = 'ServletAdmin';
        
        var user = document.formModifyQuota.user.value;
        var newQuota = document.formModifyQuota.newQuota.value;
        
        var pars = 'operation=modifyQuota' + '&user=' + user + '&newQuota=' +newQuota;
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function modifyQuotaComplete(req)
    {
        info = document.getElementById('modifyQuotaInfo');
        info.innerHTML = 'Disk quota modified';/*Cojer el ok del req*/

        info.style.display='block';

        
    }

    function modifyQuotaFailed(req)
    {
        info = document.getElementById('modifyQuotaInfo');
        info.innerHTML = '<br/>';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }

    /**************************************************************************
        List Users
    **************************************************************************/    
    
    function listUsers()
    {
        info = document.getElementById('listUsersInfo');
        info.innerHTML = 'Searching..';
        info.style.display='block';

        var success = function(req){listUsersComplete(req);}
        var failure = function(req){listUsersFailed(req);}
    
        var url = 'ServletAdmin';
                
        var pars = 'operation=listUsers';
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function listUsersComplete(req)
    {
        info = document.getElementById('listUsersInfo');
        info.style.display='none';

            //Montar la taula amb l'xml
        /*info.innerHTML = 'Ok!!!!';/*Cojer el ok del req*/
        
        clearTabListUsers();

        user = getParam(req.responseXML,'user_0');
        i=1;

        
        while (user.length>0)
        {
         
            addUser(user,i);
            
            user = getParam(req.responseXML,'user_'+i);
            i++;

        }
    }

    function listCollectionsFailed(req)
    {
        info = document.getElementById('listCollectionsInfo');
        info.innerHTML = '<br/>';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }

 /**************************************************************************
        List Collections
    **************************************************************************/    
    
    function listCollections()
    {
        info = document.getElementById('listCollectionsInfo');
        info.innerHTML = 'Searching..';
        info.style.display='block';

        var user = document.formListCollections.user.value;   
        

        var success = function(req){listCollectionsComplete(req,user);}
        var failure = function(req){listCollectionsFailed(req);}
    
        var url = 'ServletAdmin';
                
        var pars = 'operation=listCollections' + '&user=' + user;
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function listCollectionsComplete(req,user)
    {
        info = document.getElementById('listCollectionsInfo');
        info.style.display='none';

            //Montar la taula amb l'xml
        /*info.innerHTML = 'Ok!!!!';/*Cojer el ok del req*/
        
        clearTabListCollections();

        collection = getParam(req.responseXML,'collection_0');
        i=1;

        
        while (collection.length>0)
        {
         
            addCollection(user,collection,i);
            
            collection = getParam(req.responseXML,'collection_'+i);
            i++;

        }
    }

    function listCollectionsFailed(req)
    {
        info = document.getElementById('listCollectionsInfo');
        info.innerHTML = '<br/>';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }

    /**************************************************************************
        Delete Collection
    **************************************************************************/    

    function deleteCollection(user,collection)
    {
        if (confirm("Are you sure to delete the collection '" +collection +"' ?"))
        
        {   
            info = document.getElementById('listCollectionsInfo');
        
            info.innerHTML = 'Deleting...';

            info.style.display='block';
           
            var success = function(req){deleteCollectionComplete(req);}
            var failure = function(req){deleteCollectionFailed(req);}
    
            var url = 'ServletAdmin';
        
            var pars = 'operation=deleteCollection' + '&user=' + user + '&collection=' + collection;

            var myAjax = new Ajax.Request(url, {method:'post',
                postBody:pars, onSuccess:success, onFailure:failure});
        }
    }
    
    
    function deleteCollectionComplete(req)
    {
        listCollections();
        /*
        info = document.getElementById('listCollectionsInfo');
        info.style.display='none';
        */  
        
        
    }

    function deleteCollectionFailed(req)
    {
        info = document.getElementById('listCollectionsInfo');
        info.style.display='none';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }

    /**************************************************************************
        List Attachments User
    **************************************************************************/    
    
    function listAttachmentsUser()
    {
        info = document.getElementById('listAttachmentsInfo');
        info.innerHTML = 'Searching..';
        info.style.display='block';

        var user = document.formListAttachments.user.value;   
        

        var success = function(req){listAttachmentsUserComplete(req,user);}
        var failure = function(req){listAttachmentsUserFailed(req);}
    
        var url = 'ServletAdmin';
                
        var pars = 'operation=listAttachmentsUser' + '&user=' + user;
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function listAttachmentsUserComplete(req,user)
    {
        info = document.getElementById('listAttachmentsInfo');
        info.style.display='none';

        clearTabListAttachments();

        att = getAttachment(req.responseXML,0);
        
        i=1;
        
        while (att != null)
        {
            fileName = getParam(att,'fileName');
            nameCollection = getParam(att,'nameCollection');
            url = getParam(att,'url');
            size = getParam(att,'size');    
            created = getParam(att,'created');    

            addAttachment(user,fileName,nameCollection,url,size,created,i);
            
            att = getAttachment(req.responseXML,i);
            i++;
        }


    }

    function listAttachmentsUserFailed(req)
    {
        info = document.getElementById('listCollectionsInfo');
        info.innerHTML = '<br/>';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }

    /**************************************************************************
        List Attachments Date
    **************************************************************************/    
    
    function listAttachmentsDate()
    {
        info = document.getElementById('listAttachmentsInfo');
        info.innerHTML = 'Searching..';
        info.style.display='block';

        var beginDate = document.formListAttachments.beginDate.value;   
        var endDate = document.formListAttachments.endDate.value;   
        

        var success = function(req){listAttachmentsDateComplete(req);}
        var failure = function(req){listAttachmentsDateFailed(req);}
    
        var url = 'ServletAdmin';
                
        var pars = 'operation=listAttachmentsDate' + '&beginDate=' + beginDate+ '&endDate=' + endDate;
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function listAttachmentsDateComplete(req)
    {
        info = document.getElementById('listAttachmentsInfo');
        info.style.display='none';

        clearTabListAttachments();



        att = getAttachment(req.responseXML,0);
        
        i=1;
        


        while (att != null)
        {
            user = getParam(att,'user');
            fileName = getParam(att,'fileName');
            nameCollection = getParam(att,'nameCollection');
            url = getParam(att,'url');
            size = getParam(att,'size');    
            created = getParam(att,'created');    
            

            addAttachment(user,fileName,nameCollection,url,size,created,i);
            
            att = getAttachment(req.responseXML,i);
            i++;
        }


    }

    function listAttachmentsDateFailed(req)
    {
        info = document.getElementById('listCollectionsInfo');
        info.innerHTML = '<br/>';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }



    /**************************************************************************
        Delete Attachment
    **************************************************************************/    

    function deleteAttachment (user,nameCollection,fileName)
    {
        if (confirm("Are you sure to delete the file '" +fileName+"' ?"))
        
        {   
            info = document.getElementById('listAttachmentsInfo');
        
            info.innerHTML = 'Deleting...';

            info.style.display='block';
           
            var success = function(req){deleteAttachmentComplete(req);}
            var failure = function(req){deleteAttachmentFailed(req);}
    
            var url = 'ServletAdmin';
        
            var pars = 'operation=deleteAttachment' + '&user=' + user + '&collection=' + nameCollection+ '&file=' + fileName;

            var myAjax = new Ajax.Request(url, {method:'post',
                postBody:pars, onSuccess:success, onFailure:failure});
        }
    }
    
    
    function deleteAttachmentComplete(req)
    {
        info = document.getElementById('listAttachmentsInfo');
        info.style.display='none';

        divLAU = document.getElementById("divLAUser");
        
        if (divLAU.style.display=="block")
        {
            listAttachmentsUser();
        } 
        else
        {
            listAttachmentsDate();
        }
        
    }

    function deleteCollectionFailed(req)
    {
        info = document.getElementById('listAttachmentsInfo');
        info.style.display='none';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }


    /**************************************************************************
        Delete Files
    **************************************************************************/    

    function deleteFiles()
    {
        info = document.getElementById('deleteFilesInfo');
        
        info.innerHTML = 'Deleting...';

        info.style.display='block';
           
        var success = function(req){deleteFilesComplete(req);}
        var failure = function(req){deleteFilesFailed(req);}
    
        var url = 'ServletAdmin';
        
        var pars = 'operation=deleteFiles';

        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function deleteFilesComplete(req)
    {
        info = document.getElementById('deleteFilesInfo');
        info.innerHTML = 'Delete files completed';
    }

    function deleteFilesFailed(req)
    {
        info = document.getElementById('deleteFilesInfo');
        info.style.display='none';
        
        alert(req.responseText);     
        /*alert('Sorry, the update failed.');*/
    }



/**************************************************************************
        Get SQL
    **************************************************************************/    

    function getSQL()
    {
        info = document.getElementById('getSQLInfo');
        
        info.innerHTML = 'Processing...';

        info.style.display='block';
           
        var success = function(req){getSQLComplete(req);}
        var failure = function(req){getSQLFailed(req);}
    
        var url = 'ServletAdmin';
        
        var sql = document.getElementById('sql').value;        
        
        var pars = 'operation=getSQL'+'&sql=' + sql;
        
        var myAjax = new Ajax.Request(url, {method:'post',
            postBody:pars, onSuccess:success, onFailure:failure});
    }
    
    
    function getSQLComplete(req)
    {
        var res = '<table><tr class=\"head\">';

        info = document.getElementById('getSQLInfo');
        info.style.display='none';

        structure = req.responseXML.getElementsByTagName('structureXML')[0];


        colName = getParam(structure,'col_0');

        i=1;

        while (colName != '')
        {

            res= res +"<td>"+colName+"</td>";
            colName = getParam(structure,'col_'+i);
            i++;
        }

        var res = res+ '</tr>';

        data = req.responseXML.getElementsByTagName('dataXML')[0];
        
        row = data.getElementsByTagName('row_0')[0];

        var iRow = 1;
        
        while (row!=null)
        {
            if (iRow % 2 == 1) res = res + '<tr>';
            else res = res+ '<tr class=\"even\">';    
            
            colName = getParam(row,'col_0');
            
            i=1;
            while (colName != '')
            {

                res= res +"<td>"+colName+"</td>";
                colName = getParam(row,'col_'+i);
                i++;
            }

            res = res+ '</tr>';

            row = data.getElementsByTagName('row_'+iRow)[0];
            iRow++;    
        }

        res = res+ '</table>';
        
        document.getElementById('divSQLResult').innerHTML=res;
    }

    function getSQLFailed(req)
    {
        info = document.getElementById('getSQLInfo');
        info.style.display='none';
        
        alert(req.responseText);     
       
    }
