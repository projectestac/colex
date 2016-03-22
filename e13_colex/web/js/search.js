

        function changeCheckBoxSearch(FieldName)
        {
            if (document.formSearch[FieldName].value=="true")
            {
                document.formSearch[FieldName].value="false";
                
            }
            else document.formSearch[FieldName].value="true";
        }
            
        var numConditions=0;
        var idConditions=0;       
            
            
        function changeCmp(idCond)
        { 
            for (j=0;j<arrayNames.length;j++)
            {
                nameField = document.formSearch["name_"+idCond].value;
                
                
                
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
            
            var cmp = eval("comparators_"+j);

            document.formSearch["cmp_"+idCond].options.length=0;
            
            
            for(i=1;i<cmp.length;i++)
            {
                document.formSearch["cmp_"+idCond].options[i-1] = new Option(cmp[i][1],cmp[i][0]);
            }
            
            document.getElementById("cnd_"+idCond).removeChild(document.getElementById("tdValue_"+idCond));
            document.getElementById("cnd_"+idCond).removeChild(document.getElementById("tdErase_"+idCond));
    
            td_value = document.createElement("td");
            td_value.setAttribute("id","tdValue_"+idCond);
            td_value.setAttribute("name","tdValue_"+idCond);
            
            if (cmp[0]=="date")
            {
                i_DD = document.createElement("input");
                i_DD .setAttribute("type","text");
                i_DD.setAttribute("name","valueDD_"+idCond);
                i_DD.setAttribute("id","valueDD_"+idCond);
                i_DD.setAttribute("size","2");
                i_DD.setAttribute("maxlength","2");
                i_DD.setAttribute("title",sc_title_day);
                i_DD.setAttribute("alt",sc_title_day);
                
                
                i_MM = document.createElement("input");
                i_MM.setAttribute("type","text");
                i_MM.setAttribute("name","valueMM_"+idCond);
                i_MM.setAttribute("id","valueMM_"+idCond);
                i_MM.setAttribute("size","2");
                i_MM.setAttribute("maxlength","2");
                i_MM.setAttribute("title",sc_title_month);
                i_DD.setAttribute("alt",sc_title_month);
                
                i_YYYY = document.createElement("input");
                i_YYYY.setAttribute("type","text");
                i_YYYY.setAttribute("name","valueYYYY_"+idCond);
                i_YYYY.setAttribute("id","valueYYYY_"+idCond);
                i_YYYY.setAttribute("size","4");
                i_YYYY.setAttribute("maxlength","4");
                i_YYYY.setAttribute("title",sc_title_year);
                i_DD.setAttribute("alt",sc_title_year);
                
                td_value.appendChild(i_DD);
                td_value.appendChild(document.createTextNode("-"));
                td_value.appendChild(i_MM);
                td_value.appendChild(document.createTextNode("-"));
                td_value.appendChild(i_YYYY);
                
                hidden = document.createElement("input");
                hidden.setAttribute("type","hidden");
                hidden.setAttribute("name","valueDate_"+idCond);
                hidden.setAttribute("id","valueDate_"+idCond);
                td_value.appendChild(hidden);

                var ref = document.createElement("a");
                ref.setAttribute("href","javascript:openCalendar('formSearch','value',"+idCond+");");
        
                var img = document.createElement("img");
                img.setAttribute("src","img/icons/date.gif");
        
                ref.appendChild(img);     
                td_value.appendChild(ref);
            }
            
            else if (cmp[0]=="boolean")
            {
            
                check = document.createElement("input");
                check.setAttribute("id","check_"+idCond);
                check.setAttribute("name","check_"+idCond);
                check.setAttribute("type","checkbox");
                check.setAttribute("alt",sc_alt_fieldValue);  
               

                
                
                td_value.appendChild(check);
                
                
                hidden = document.createElement("input");
                hidden.setAttribute("type","hidden");
                hidden.setAttribute("name","value_"+idCond);
                hidden.setAttribute("id","value_"+idCond);
                hidden.setAttribute("value","false");
                
                td_value.appendChild(hidden);
                
            }
            
            else if (cmp[0]=="image")
            {
                
                document.formSearch["cmp_"+idCond].firstChild.text=sc_has_image;
                document.formSearch["cmp_"+idCond].firstChild.value="!=";
                
                document.formSearch["cmp_"+idCond].lastChild.text=sc_has_no_image;
                document.formSearch["cmp_"+idCond].lastChild.value="=";
                
                hidden = document.createElement("input");
                hidden.setAttribute("type","hidden");
                hidden.setAttribute("name","value_"+idCond);
                hidden.setAttribute("id","value_"+idCond);
                hidden.setAttribute("value","null");
                
                td_value.appendChild(hidden);

            }
            
            else if (cmp[0]=="sound")
            {
                
                document.formSearch["cmp_"+idCond].firstChild.text=sc_has_sound;
                document.formSearch["cmp_"+idCond].firstChild.value="!=";
                
                document.formSearch["cmp_"+idCond].lastChild.text=sc_has_no_sound;
                document.formSearch["cmp_"+idCond].lastChild.value="=";
                
                hidden = document.createElement("input");
                hidden.setAttribute("type","hidden");
                hidden.setAttribute("name","value_"+idCond);
                hidden.setAttribute("id","value_"+idCond);
                hidden.setAttribute("value","null");
                
                td_value.appendChild(hidden);
                
            }
            else if(cmp[0]=="text")
            {
                i_value = document.createElement("input");
                i_value.setAttribute("type","text");
                i_value.setAttribute("name","value_"+idCond);
                i_value.setAttribute("id","value_"+idCond);
                i_value.setAttribute("alt",sc_alt_fieldValue);  
            
                td_value.appendChild(i_value);
            }
            else if(cmp[0]=="select")
            {
                
                i_value = document.createElement("select");
                i_value.setAttribute("name","value_"+idCond);
                i_value.setAttribute("id","value_"+idCond);

                var sOptions = eval("selectOptions_"+j);

                for(i=0;i<sOptions.length-1;i++)
                {
                    option = document.createElement("option");
                    option.setAttribute("value",sOptions[i]);
                    option.appendChild(document.createTextNode(sOptions[i]));
                    i_value.appendChild(option);
                }
            
                td_value.appendChild(i_value);
            }
            else
            {
            
                i_value = document.createElement("input");
                i_value.setAttribute("type","text");
                i_value.setAttribute("name","value_"+idCond);
                i_value.setAttribute("id","value_"+idCond);
                i_value.setAttribute("alt",sc_alt_fieldValue);  
            
                td_value.appendChild(i_value);
            }
            
            document.getElementById("cnd_"+idCond).appendChild(td_value);
            
            a = document.createElement("a");
            a.setAttribute("id","erase_"+idCond);
            a.setAttribute("title",sc_title_delete);
            a.setAttribute("href","#");

            trash =document.createElement("img");
            trash.setAttribute("src","img/icons/trash.jpg");
            trash.setAttribute("alt",sc_title_delete);

            a.appendChild(trash);
            
            td_a = document.createElement("td");
            td_a.setAttribute("id","tdErase_"+idCond);
            td_a.setAttribute("name","tdErase_"+idCond);
            td_a.appendChild(a);
              
            document.getElementById("cnd_"+idCond).appendChild(td_a);

            Event.observe(a, 'click', function(){erase('cnd_'+idCond)} , false);

            if (cmp[0]=="boolean")
            {
                var aux = 'value_'+idCond;
                
                Event.observe(check, 'change', function(){changeCheckBoxSearch(aux)} , false);

                
            }

            if (cmp[0]=="image")
            {
                
            }
        }   
        
        function erase(idCondition) 
        {
            
            numConditions--;
            tr = document.getElementById(idCondition);
            
            if (tr.previousSibling == null)
            {
                trNext = tr.nextSibling;

                if ( trNext !=null)
                {
                    tdNext = trNext.firstChild;
                    //tdNext.removeChild(tdNext.firstChild);
                    Element.addClassName(tdNext.firstChild,"s_blank");
                }
            }
        
            document.getElementById("tabBodyConditions").removeChild(tr);

        }
            
        function doSearch()
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
            document.getElementById("formSearch").submit();
            
        }


   






