function cancelLink () {
  return false;
}
function disableLink (linkId) {
    link = document.getElementById(linkId);
    if (link!=null)
    {
        if (link.onclick)
        link.oldOnClick = link.onclick;
        
        link.onclick = cancelLink;
        
        if (link.style)
        link.style.cursor = 'default';

        
        var e = link.getElementsByTagName('img');
            
        

        if (link.getElementsByTagName('img')[0] != null)
        {
            oldSrc = link.getElementsByTagName('img')[0].src;
            

            if (oldSrc.indexOf("_null")==-1)
            {
                position = oldSrc.lastIndexOf(".");
            
                newSrc="";
                newSrc=newSrc.concat(oldSrc.substring(0,position),'_null',oldSrc.substring(position,oldSrc.length));
            
                link.getElementsByTagName('img')[0].src = newSrc;
            }
        }
    }
}


function disableLinkImage (linkId) {
    link = document.getElementById(linkId);
    if (link!=null)
    {
        if (link.onclick)
        link.oldOnClick = link.onclick;
        
        link.onclick = cancelLink;
        
        if (link.style)
        link.style.cursor = 'default';

    }
}



function enableLink (linkId) {
    link = document.getElementById(linkId);
    if (link!=null)
    {
        link.onclick = link.oldOnClick ? link.oldOnClick : null;
        if (link.style)
        link.style.cursor = 
        document.all ? 'hand' : 'pointer';

        if (link.getElementsByTagName('img')[0] != null)
        {
            oldSrc = link.getElementsByTagName('img')[0].src;
            

            if (oldSrc.indexOf("_null")!=-1)
            {
                position = oldSrc.lastIndexOf(".");
            
                newSrc="";
                newSrc=newSrc.concat(oldSrc.substring(0,position-5),oldSrc.substring(position,oldSrc.length));
            
                link.getElementsByTagName('img')[0].src = newSrc;
            }
        }
    }
}
function toggleLink (linkId) {
  if (link.disabled) 
    enableLink (link)
  else 
    disableLink (link);
  link.disabled = !link.disabled;
}

        function hideObjects()
        {
            var objects = document.getElementsByTagName("object");
            
            for(var i=0;i<objects.length;i++)
            {
                objects[i].style.visibility="hidden";
            }
            
            if (document.formRecord)
            {
                selects = document.formRecord.elements;
            
                for(j=0;j<selects.length;j++)
                {
                    if (selects[j].tagName=="SELECT") Element.hide(selects[j]);
                }
            }
            
            if (document.formAddRecord)
            {
                selects = document.formAddRecord.elements;
            
                for(j=0;j<selects.length;j++)
                {
                    if (selects[j].tagName=="SELECT") Element.hide(selects[j]);
                }
            }
            
        }
        
        function showObjects()
        {
            var objects = document.getElementsByTagName("object");
            
            for(var i=0;i<objects.length;i++)
            {
                objects[i].style.visibility="visible";
            }
            
            if (document.formRecord)
            {
                selects = document.formRecord.elements;
      
                for(j=0;j<selects.length;j++)
                {
                    if (selects[j].tagName=="SELECT") Element.show(selects[j]);
                }
            }
            
            if (document.formAddRecord)
            {
                selects = document.formAddRecord.elements;
            
                for(j=0;j<selects.length;j++)
                {
                    if (selects[j].tagName=="SELECT") Element.show(selects[j]);
                }
            }
        }