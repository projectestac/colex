/*
 * File    : ColexErrorBean.java
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
import javax.xml.soap.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import javax.servlet.http.*;

/**
 * Bean to processes all the requests from the JSP error.jsp, where is implemented the error page of the application
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexErrorBean extends ColexMainBean 
{
    /**
     * Method that overrides the method from the MainBean
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if the user is validated and everything has been initialized, else false
     */
    public boolean init(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
        iniLanguage();
        return true;
    }
    
    /**
     * Does nothing because no implementation is needed
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true
     */
    public boolean initImpl(HttpServletRequest request, HttpServletResponse response)
    {
        return true;
    }
}

