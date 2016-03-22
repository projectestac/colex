/*
 * File    : FieldDefSound.java
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
 * Class that represents the Field Definition of type 'sound'
 * @author ogalzorriz
 * @version 1.0
 */
public class FieldDefSound extends FieldDef{
    
    /**
     * Creates a new FieldDefSound
     */
    public FieldDefSound() 
    {
        type="sound";
    }
    
    /**
     * Creates a new FieldDefSound
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     */
    public FieldDefSound(String sName, String sType) 
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
     * Returns the string "null", meaning that there is not yet a sound
     * @return the string "null"
     */
    public String getDefaultValue()
    {
        return "null";
    }
    
    /**
     * Returns true because we check the file extension in the jsp level
     * @param sValue the given image to check
     * @return true
     */
    public boolean checkValue(String sValue)
    {
        return true;
    }
    
    /**
     * Check if the comparator is '=' or '!='
     * @param sComparator the comparator to check
     * @return true if the comparator is '=' or '!=', else false
     */
    public boolean checkComparator(String sComparator)
    {
        return ((sComparator.equals("=")) || (sComparator.equals("!="))); 
    }
    
    /**
     * Returns the comparators that can be used with this FieldDef '=','!='
     * @return a Vector containing the Strings '=','!='
     */
    public Vector getComparators()
    {
        Vector vRes = new Vector();
        
        vRes.add("=");
        vRes.add("!=");
                
        return vRes;        
    }
    
    /**
     * Returns false because this fieldDef has not info associated
     * @return false
     */
    public boolean isInfo()
    {
        return false;
    }
    
    /**
     * Returns true because this fieldDef has a file attachment
     * @return true
     */
    public boolean isAttachment()
    {
        return true;
    }
    
    /**
     * Returns false because this fieldDef is not sortable
     * @return false
     */
    public boolean isSortable()
    {
        return false;
    }    
}
