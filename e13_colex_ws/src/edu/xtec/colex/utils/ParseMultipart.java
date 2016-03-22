/*
 * File    : ParseMultipart.java
 * Created : 07-jun-2005 17:56
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
package edu.xtec.colex.utils;

import javax.servlet.http.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import java.util.*;
import java.io.File;

/**
 * Auxiliar util class used on the jsp to parse the HttpServletRequests that have a MultipartContent (Html forms with input of type file)
 * @author ogalzorriz
 * @version 1.0
 */
public class ParseMultipart {

    /**
     * the HttpServletRequest to parse
     */
    HttpServletRequest request;
    /**
     * the HashTable where to put all the parameters of the HttpServletRequest
     */
    Hashtable parameters;
    /**
     * true if HttpServletRequest is multipart, else false
     */
    boolean isMultipart = false;

    /**
     * Creates a new instance of ParseMultipart with a given HttpServletRequest
     * @param requestIn the HttpServletRequest to parse
     */
    public ParseMultipart(HttpServletRequest requestIn) 
    {
        parameters = new Hashtable();
        
        request = requestIn;
        
        isMultipart = ServletFileUpload.isMultipartContent(new ServletRequestContext(request));
        
        try
        {
            if (isMultipart) 
            {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                // Configure the factory here, if desired.
                
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setHeaderEncoding("ISO-8859-1");
                // Configure the uploader here, if desired.
                
                Iterator fileItems = upload.parseRequest(request).iterator();
            
                while (fileItems.hasNext())
                {
                    FileItem fi = (FileItem) fileItems.next();
                                               
                    if (!fi.isFormField())
                    {
                        parameters.put(fi.getFieldName(),fi);
                    }   
                    else
                    {
                        parameters.put(fi.getFieldName(),fi.getString("ISO-8859-1").trim());
                    }
                }
            }   
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Returns the String value of the parameter paramName
     * @param paramName the name of the parameter
     * @return the String value of the parameter
     */
    public String getParameter(String paramName)
    {
        if (isMultipart) return (String)parameters.get(paramName);
        else return request.getParameter(paramName);
    }
    
    /**
     * Returns an Enumeration containing all the String Names of the parameters that has the HttpServletRequest to parse
     * @return the Enumeration containing all the String Names of the parameters
     */
    public Enumeration getParameterNames()
    {
        if (isMultipart) return parameters.keys();
        else return request.getParameterNames();
    }
    
     /**
     * Returns the FileItem of the parameter paramName
     * @param paramName the name of the parameter
     * @return the FileItem of the parameter
     */
    public FileItem getFileItem(String paramName)
    {
        if (isMultipart) return (FileItem)parameters.get(paramName);
        else return null;
    }
}
