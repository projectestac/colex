/*
 * File    : Property.java
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
/**
 * Class that represents the Property object used by the FieldDefinitions objects to store and retrieve the information of its preoperties. An exemple of property is the length on the FieldDefText.
 * @author ogalzorriz
 * @version 1.0
 */
public class Property {
    
    /**
     * the name of the Property
     */
    private String name;
    /**
     * the value of the Property
     */
    private String value;
    
    /**
     * Creates a new Property with the given Name and Value
     * @param sName the Name of the Property
     * @param sValue the Value of the Property
     */
    public Property(String sName, String sValue)    
    {
        name=sName;
        value=sValue;
    }
    
    /**
     * Sets the name of the Property
     * @param sName the name
     */
    public void setName(String sName)
    {
        name=sName;
    }
    
    /**
     * Returns the name of the Property
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the value of the Property
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
}
