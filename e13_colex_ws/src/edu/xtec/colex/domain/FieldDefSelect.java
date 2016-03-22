/*
 * File    : FieldDefSelect.java
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
 * Class that represents the Field Definition of type 'select'. When a user creates a 'select' field definition defines a list of options, and then in every record choses one.
 * @author ogalzorriz
 * @version 1.0
 */
public class FieldDefSelect extends FieldDef {
    
    /**
     * The options defined by the user. They are treated as the properties of the other field definitions.
     */
    protected Vector options = new Vector();
    
    /**
     * Creates a new FieldDefSelect
     */
    public FieldDefSelect() 
    {
        type="select";
        options = new Vector();
    }
    
    /**
     * Creates a new FieldDefSelect
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     */
    public FieldDefSelect(String sName, String sType) 
    {
        super(sName,sType);
    }
   
    /**
     * Calls the parent method to fill the Name and the Type of the FieldDef and fills the options
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        super.fill(se);
        
        Iterator It=se.getChildElements(soapFactory.createName("option"));
        String option;
        
        while (It.hasNext())
        {
            option = ((SOAPElement)It.next()).getValue();
            options.add(option);
        }
    }

    /**
     * Add the options to the given SOAPElement containing the XML representation of the FieldDef
     * @param se th SOAPElement containing the XML representation of the FieldDef 
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs 
     */
    public void addPropertiesToXml(SOAPElement se) throws SOAPException            
    {
        SOAPElement seOption;
          
        for (int i=0; i<options.size();i++)
        {
            seOption = soapFactory.createElement("option");
            seOption.addTextNode((String)options.get(i));
            se.addChildElement(seOption);
        }
    }
    
    /**
     * Sets the options of this FieldDef separated by '|'
     * @param sProperties the properties
     */
    public void setProperties(String sProperties)
    {
        int iBegin = 0;
        int iEnd = sProperties.indexOf("|",iBegin);
        
        String[] optionsAux = sProperties.split("\\|");
        
        for (int i=0; i< optionsAux.length;i++)
        {
            options.add(new String(optionsAux[i]));
        }
    }
    
    /**
     * Returns the properties of this FieldDef option0|option1|...optionN|
     * @return the properties
     */
    public String getProperties()
    {
        String sRes ="";
        
        for (int i=0; i< options.size();i++)
        {
            sRes = sRes + (String)options.get(i)+"|";
        }
    
        return sRes;
    }
    
    /**
     * Returns the options of this FieldDef in a Vector
     * @return a Vector of {@link Property} containing the options of this FieldDef
     */
    public Vector getVProperties()
    {   
        Vector v= new Vector();
        
        for (int i=0; i<options.size();i++)
        {
            v.add(new Property("option",(String)options.get(i)));
        }
        
        return v;
    }
    
    /**
     * Returns the string "undefined" option
     * @return the string "undefined"
     */
    public String getDefaultValue()
    {
        return "undefined";
    }
    
    /**
     * Checks if the given Value is contained on the list of options defined by the user or the "undefined" value
     * @param sValue the given option to check
     * @return true if the value is on the list of options
     */    
    public boolean checkValue(String sValue)
    {
        return (options.contains(sValue) || sValue.equals(getDefaultValue()));
    }
    
    /**
     * Check if the comparator is '=' or '!='
     * @param sComparator the comparator to check
     * @return true if the comparator is '=' or '!='
     */
    public boolean checkComparator(String sComparator)
    {
        return ( (sComparator.equals("=")) || (sComparator.equals("!=")) ); 
    }
    
    /**
     * Returns the comparators that can be used with this FieldDef '=','!=','<','>'
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
     * Returns false, because this FieldDef do not show information about it
     * @return false
     */
    public boolean isInfo()
    {
        return false;
    }  
}
