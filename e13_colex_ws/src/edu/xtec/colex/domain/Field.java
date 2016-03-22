/*
 * File    : Field.java
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

/**
 * Class that represents the Field object. A Field is a Name and a Value, where we store the data of the field definitions for every record on a collection.
 * @author ogalzorriz
 * @version 1.0
 */
public class Field extends ObjectColex
{
    /**
     * the name of the Field, it has to be one of the FieldDefs of the collection where the record belongs to.
     */
    private String name;
    /**
     * the value of this Field
     */
    private String value;
    
    /**
     * Creates a new empty Field
     */
    public Field() 
    {}
    
    /**
     * Creates a new Field with the given Name and Value
     * @param sName the Name of the Field
     * @param sValue the Value of the Field
     */
    public Field(String sName, String sValue) 
    {
        name=sName;
        value=sValue;
    }
    
    /**
     * Creates a new Field from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Field(SOAPElement se) throws SOAPException
    {
        Iterator It=se.getChildElements(soapFactory.createName("nameField"));
        name=((SOAPElement)It.next()).getValue();
        It=se.getChildElements(soapFactory.createName("value"));
        value=((SOAPElement)It.next()).getValue();        
        if (value==null) value="";
    }
    
    /**
     * Sets the name of the Field
     * @param sName the name
     */
    public void setName(String sName)
    {
        name=sName;
    }
    
    /**
     * Returns the name of the Field
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the value of the Field
     * @param sValue the value
     */
    public void setValue(String sValue)
    {
        value=sValue;
    }
    
    /**
     * Returns the value of the Field
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Converts this object into a SOAPElement
     * @return SOAPElement containing the XML representation of this Field
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seField=null,seName,seValue;
 
        seField = soapFactory.createElement("field");
        seName = soapFactory.createElement("nameField");
        seName.addTextNode(name);
        seValue= soapFactory.createElement("value");
        seValue.addTextNode(value);
        seField.addChildElement(seName);
        seField.addChildElement(seValue);
        
        return seField;
    }
}
