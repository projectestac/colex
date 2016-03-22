/*
 * File    : FieldDefLink.java
 * Created : 27-sep-2005 12:24
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
package edu.xtec.colex.domain;

import javax.xml.soap.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class that represents the Field Definition of type 'link'
 * @author ogalzorriz
 * @version 1.0
 */
public class FieldDefLink extends FieldDef{
    
    /**
     * Creates a new FieldDefLink
     */
    public FieldDefLink() 
    {
        type="link";
    }
    
    /**
     * Creates a new FieldDefLink
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     */
    public FieldDefLink(String sName, String sType) 
    {
        super(sName,sType);
    }
    
    /**
     * Calls the parent method to fill the Name and the Type of the FieldDef
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        super.fill(se);
    }

    /**
     * Do nothing because there are no propierties on this FieldDef
     * @param se the given SOAPElement where to add the XML representation of the properties
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void addPropertiesToXml(SOAPElement se) throws SOAPException            
    {
    }
    
    /**
     * Do nothing because there are no propierties on this FieldDef
     * @param sProperties the properties
     */
    public void setProperties(String sProperties)
    {
    }
    
    /**
     * Returns an empty String "" because there are no propierties on this FieldDef
     * @return an empty String ""
     */
    public String getProperties()
    {
        return "";
    }
    
    /**
     * Returns an empty Vector because there are no propierties on this FieldDef
     * @return an empty Vector
     */
    public Vector getVProperties()
    {   
        return new Vector();
    }
    
    /**
     * Returns a string with a space appended " "
     * @return a string with a space appended " "
     */
    public String getDefaultValue()
    {
        return " ";
    }
    
    /**
     * Returns true because we use the method addHttp to put the http protocol
     * @param sValue the given html text to check 
     * @return true
     */
    public boolean checkValue(String sValue)
    {
        return true;
    }
    
    /**
     * Returns true because this fieldDef is not searchable
     * @param sComparator the comparator to check
     * @return true
     */
    public boolean checkComparator(String sComparator)
    {
          return true;
    }
    
    /**
     * Returns an empty Vector because this fieldDef is not searchable
     * @return an empty Vector
     */
    public Vector getComparators()
    {
        return new Vector();
    }
    
    /**
     * If the link has no a protocol adds the http protocol on it
     * @param sLink the link where to add the http
     * @return the link with the http added
     */
    public static String addHttp(String sLink)
    {
        if (sLink.indexOf("://")<0) sLink="http://"+sLink;
        return sLink;
    }
    
    /**
     * Trims a link to a maximum size of iSize to show it on the jsp
     * @param sLink the link to be trimed
     * @param iSize the maximum size
     * @return the link trimed
     */
    public static String trim(String sLink, int iSize)
    {
        if (sLink.length() > iSize) sLink = sLink.substring(0,iSize-3)+"...";
        return sLink;
    }
    
    /**
     * Returns false because this fieldDef is not searchable
     * @return false
     */
    public boolean isSortable()
    {
        return false;
    }
    
    /**
     * Returns false because this fieldDef is not sortable
     * @return false
     */
    public boolean isSearchable()
    {
        return false;
    }      
}
