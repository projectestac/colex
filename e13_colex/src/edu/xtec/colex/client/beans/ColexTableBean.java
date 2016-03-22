/*
 * File    : ColexTableBean.java
 * Created : 13-sep-2005 17:56
 * By      : ogalzorriz
 *
 * Col·lex - Web-based educational application for design collections
 * of records, store information, make queries and share them.
 *
 * Copyright (C) 2006 - 2008 Oriol Garcia-Alzorriz & Departament
 * d'Educacio de la Generalitat de Catalunya
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details (see the LICENSE file).
 */
package edu.xtec.colex.client.beans;

import edu.xtec.colex.domain.*;
import javax.servlet.http.*;
import edu.xtec.colex.utils.*;
import javax.xml.soap.*;

/**
 * Bean that extends from ColexRecordBean and processes all the requests from the JSP table.jsp, where is implemented the page to show all the records in a table
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexTableBean extends ColexRecordBean 
{
    /**
     * Method to complete the initialitzation of the bean
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if everything has been initialized, else false
     */
    public boolean initImpl(HttpServletRequest request,HttpServletResponse response)
    {
        boolean bOK = true;
        
        if (bOK)
        {
            pmRequest = new ParseMultipart(request);
            collection =  pmRequest.getParameter("collection");

            if (collection == null)
            {
                bOK=false;
                redirectPage="index.jsp";
            }
            else
            {
                operation = pmRequest.getParameter("operation");
                
                try
                {
                    if (operation!=null)                
                    {
                        String sBegin = pmRequest.getParameter("begin");
                        if (sBegin!=null) begin = Integer.parseInt(sBegin);

                        direction = pmRequest.getParameter("direction");
                        if (direction==null) direction ="asc";

                        orderField = pmRequest.getParameter("orderField");
                        if (orderField==null) orderField = "null";

                        q = new Query();

                        q.setOrderField(orderField);
                        q.setDirection(direction);
                        q.setBeginIndex(begin);
   
                        owner = pmRequest.getParameter("owner");
                        
                        recordSize = pmRequest.getParameter("recordSize");
                        if (recordSize==null) recordSize = "Normal";
                            
                        isTabMode = pmRequest.getParameter("isTabMode");
                        if (isTabMode==null) isTabMode = "false";

                        if (operation.equals("showAll"))
                        {
                            
                        }                    
                        else if (operation.equals("search"))
                        {
                            calculateConditions();
                        }
                        else if (operation.equals("delete"))
                        {
                            calculateConditions();
                            deleteRecord();
                        }
                            
                        searchCollection();
                            
                        if (owner!=null) getPermission();
                        else permission = edu.xtec.colex.domain.Guest.PERMISSION_TOTAL;
                    } 
                }
                catch (SOAPException se)
                {
                    logger.info("User: "+getUserId()+" Exception: "+se);
                    bOK= false;
                    redirectPage="error.jsp";
                }
                catch (Exception e)
                {
                    logger.info("User: "+getUserId()+" Exception: "+e);
                    bOK=false;
                    redirectPage="error.jsp";
                }
            }
        }
        return bOK;
    }  
}
