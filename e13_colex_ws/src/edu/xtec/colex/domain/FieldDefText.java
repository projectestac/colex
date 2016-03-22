/*
 * File    : FieldDefText.java
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
import java.util.Vector;
import java.util.Iterator;

/**
 * Class that represents the Field Definition of type 'text'
 * @author ogalzorriz
 * @version 1.0
 */
public class FieldDefText extends FieldDef
{     
    /**
     * the length value property
     */
    protected int length;
    
    /**
     * Creates a new FieldDefText
     */
    public FieldDefText() 
    {
        type="text";
        length=10;
    }
    
    /**
     * Creates a new FieldDefText
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     * @param iLength the Length value property
     */
    public FieldDefText(String sName, String sType, int iLength) 
    {
        super(sName,sType);
        length=iLength;
    }
    
    /**
     * Sets the Length value property
     * @param iLength the Length
     */
    public void setLength(int iLength)
    {
        length = iLength;
    }
    
    /**
     * Returns the Length value property
     * @return the Length
     */
    public int getLength()
    {
        return length;
    }
    
    /**
     * Calls the parent method to fill the Name and the Type of the FieldDef and fills the properties (length)
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        super.fill(se);
        
        Iterator It=null;
        
        It=se.getChildElements(soapFactory.createName("length"));
        String sLength = ((SOAPElement)It.next()).getValue();
        sLength = sLength.trim();
        
        if (sLength.startsWith("+")) sLength = sLength.substring(1);
        
        length=Integer.parseInt(sLength);
    }

    /**
     * Add the propeties (length) to the given SOAPElement containing the XML representation of the FieldDef
     * @param se th SOAPElement containing the XML representation of the FieldDef 
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs 
     */
    public void addPropertiesToXml(SOAPElement se) throws SOAPException            
    {
        SOAPElement seLength;
            
        seLength  = soapFactory.createElement("length");
        seLength.addTextNode(""+length);
        se.addChildElement(seLength);
    }
    
    /**
     * Sets the properties of this FieldDef separated by '|'
     * @param sProperties the properties
     */
    public void setProperties(String sProperties)
    {
        int iBegin = 0;
        int iEnd = sProperties.indexOf("|",iBegin);
        
        String sLength = sProperties.substring(iBegin,iEnd);
        
        if (sLength.startsWith("+")) sLength = sLength.substring(1);
        length = Integer.parseInt(sLength);
    }
    
    /**
     * Returns the properties of this FieldDef length|
     * @return the properties
     */
    public String getProperties()
    {
        return length+"|";
    }
    
    /**
     * Returns the properties of this FieldDef in a Vector
     * @return a Vector of Propertys {@link Property} containing the propiertes of this FieldDef
     */
    public Vector getVProperties()
    {   
        Vector v= new Vector();
        
        v.add(new Property("length",""+length));
        
        return v;
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
     * Checks if the given Value length is less or equal to the length property
     * @param sValue the given text to check
     * @return true if the given Value length is less or equal to the length property, else false
     */    
    public boolean checkValue(String sValue)
    {
        return (sValue.length()<=length);
    }
    
    /**
     * Check if the comparator is '=' or '!=' or 'LIKE'
     * @param sComparator the comparator to check
     * @return true if the comparator is '=' or '!=' or 'LIKE', else false
     */
    public boolean checkComparator(String sComparator)
    {
        return (    (sComparator.equals("=")) || (sComparator.equals("!="))
                ||  (sComparator.equals("LIKE")) ); 
    }
    
    /**
     * Returns the comparators that can be used with this FieldDef '=','!=','LIKE'
     * @return a Vector containing the Strings '=','!=','LIKE'
     */
    public Vector getComparators()
    {
        Vector vRes = new Vector();
        
        vRes.add("=");
        vRes.add("!=");
        vRes.add("LIKE");
        
        return vRes;        
    }       
}
