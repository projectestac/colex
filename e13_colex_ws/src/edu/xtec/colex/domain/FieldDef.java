/*
 * File    : FieldDef.java
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
 * Class that represents the FieldDef object. A Field Definition is a Name and a Type. A set of FieldDefs is the structure of a Collection used by all the Records of the Collection
 * @author ogalzorriz
 * @version 1.0
 */
public abstract class FieldDef extends ObjectColex
{   
    /**
     * the name of the FieldDef
     */
    protected String name;
    /**
     * The type of the FieldDef. It can be [text,integer,decimal,select,image,sound,date,boolean,link,html]
     */
    protected String type;
    /**
     * The id of the FieldDef assigned by the system
     */
    protected int id;
    /**
     * the position of the FieldDef in the structure of a Collection
     */
    protected int position;
    
    /**
     * Creates a new empty FieldDef
     */
    public FieldDef() 
    {}
    
    /**
     * Creates a new FieldDef with the given Name and Type
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     */
    public FieldDef(String sName, String sType) 
    {
        name=sName;
        type=sType;
    }
    
    /**
     * Crates a new FieldDef of the given type
     * @return the FieldDef
     * @param sType the Type of the FieldDef to create
     */
    public static FieldDef createFieldDef(String sType)
    {
        FieldDef fdRes = null;
        
        if (sType.equals("text"))
        {
            fdRes = new FieldDefText();
        }
        if (sType.equals("integer"))
        {
            fdRes = new FieldDefInteger();
        }
        if (sType.equals("decimal"))
        {
            fdRes = new FieldDefDecimal();
        }
        if (sType.equals("select"))
        {
            fdRes = new FieldDefSelect();
        }
        if (sType.equals("image"))
        {
            fdRes = new FieldDefImage();
        }
        if (sType.equals("sound"))
        {
            fdRes = new FieldDefSound();
        }
        if (sType.equals("date"))
        {
            fdRes = new FieldDefDate();
        }
        if (sType.equals("boolean"))
        {
            fdRes = new FieldDefBoolean();
        }
        if (sType.equals("link"))
        {
            fdRes = new FieldDefLink();
        }
        if (sType.equals("html"))
        {
            fdRes = new FieldDefHtml();
        }
        return fdRes;
    }
    
    /**
     * Creates and returns a new FieldDef from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @return the FieldDef created
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public static FieldDef createFieldDef(SOAPElement se) throws SOAPException
    {
        FieldDef fdRes = null;
        Iterator It=null;
        
        It=se.getChildElements(soapFactory.createName("type"));
        String sType=((SOAPElement)It.next()).getValue();
        
        if (sType.equals("text"))
        {
            fdRes = new FieldDefText();
        }
        else if (sType.equals("decimal"))
        {
            fdRes = new FieldDefDecimal();
        }
        else if (sType.equals("integer"))
        {
            fdRes = new FieldDefInteger();
        }
        else if (sType.equals("select"))
        {
            fdRes = new FieldDefSelect();
        }
        else if (sType.equals("image"))
        {
            fdRes = new FieldDefImage();
        }
        else if (sType.equals("sound"))
        {
            fdRes = new FieldDefSound();
        }
        else if (sType.equals("date"))
        {
            fdRes = new FieldDefDate();
        }
        else if (sType.equals("boolean"))
        {
            fdRes = new FieldDefBoolean();
        }
        else if (sType.equals("link"))
        {
            fdRes = new FieldDefLink();
        }
        else if (sType.equals("html"))
        {
            fdRes = new FieldDefHtml();
        }
        
        if (fdRes !=null) fdRes.fill(se);
        return fdRes;
    }
    
    /**
     * Method to fill the name and the type of the FieldDef when creating a new FieldDef
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        Iterator It=null;
        
        It=se.getChildElements(soapFactory.createName("name"));
        name=((SOAPElement)It.next()).getValue();
        
        It=se.getChildElements(soapFactory.createName("type"));
        type=((SOAPElement)It.next()).getValue();
    }
    
    /**
     * Returns all the types to choose when creating a FieldDef
     * @return a Vector containing all the types
     */
    public static Vector getAllTypes()
    {
        Vector types = new Vector();
        
        FieldDef fdText = createFieldDef("text");
        FieldDef fdInteger = createFieldDef("integer");
        FieldDef fdDecimal = createFieldDef("decimal");
        FieldDef fdImage = createFieldDef("image");
        FieldDef fdSelect = createFieldDef("select");
        FieldDef fdSound = createFieldDef("sound");
        FieldDef fdDate = createFieldDef("date");
        FieldDef fdBoolean = createFieldDef("boolean");
        FieldDef fdLink = createFieldDef("link");
        FieldDef fdHtml = createFieldDef("html");
     
        types.add(fdText);
        types.add(fdInteger);
        types.add(fdDecimal);
        types.add(fdSelect);
        types.add(fdImage);
        types.add(fdSound);
        types.add(fdDate);
        types.add(fdBoolean);
        types.add(fdLink);
        types.add(fdHtml);
        
        return types;
    }
    
    /**
     * Sets the name of the FieldDef
     * @param sName the name
     */
    public void setName(String sName)
    {
        name=sName;
    }
    
    /**
     * Returns the name of the FieldDef
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the type of the FielDef
     * @param sType the type
     */
    public void setType(String sType)
    {
        type=sType;
    }
    
    /**
     * Returns the type of the FieldDef
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Sets the id of the FieldDef
     * @param iId the id
     */
    public void setId(int iId)
    {
        id=iId;
    }
    
    /**
     * Returns the id of the FieldDef
     * @return the id
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * Sets the position of the FieldDef in the structure of a Collection
     * @param iPosition the position
     */
    public void setPosition(int iPosition)
    {
        position=iPosition;
    }
    
    /**
     * Returns the position of the FieldDef in the structure of a Collection
     * @return the position
     */
    public int getPosition()
    {
        return position;
    }
    
    /**
     * Converts this object into a SOAPElement
     * @return SOAPElement containing the XML representation of this FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seFieldDef = soapFactory.createElement("fieldDef");
        SOAPElement seName  = soapFactory.createElement("name");
        seName.addTextNode(name);
        SOAPElement seType= soapFactory.createElement("type");
        seType.addTextNode(type);
        seFieldDef.addChildElement(seName);
        seFieldDef.addChildElement(seType);
        addPropertiesToXml(seFieldDef);
        
        return seFieldDef;
    }
    
    /**
     * Returns if a FieldDef has information to show about it
     * @return returns true if the FieldDef has information to show in the icon-info of the JSP, else returns false
     */
    public boolean isInfo()
    {
        return true;
    }
    
    /**
     * Returns if the FieldDef has an external file, case of types 'image' and 'sound'
     * @return returns true if the FieldDef has an external file, else returns false
     */
    public boolean isAttachment()
    {
        return false;
    }
    
    /**
     * Returns if the Records of a Collection can be sorted by this FieldDef
     * @return returns true if a the Records of a Collection can be sorted by this FieldDef, else returns false
     */
    public boolean isSortable()
    {
        return true;
    }
    
    /**
     * Returns if the Records of a Collection can be searched by this FieldDef
     * @return returns true if a the Records of a Collection can be searched by this FieldDef, else returns false
     */
    public boolean isSearchable()
    {
        return true;
    }
    
    /**
     * Returns if this FieldDef is compatible with the given FieldDef. Two FieldDefs are compatible when they have equals Name, Type and Properties
     * @param fdImport the FieldDef to check if is compatible
     * @return true if both FieldDefs have equals Name,Type and Properties
     */
    public boolean isCompatible(FieldDef fdImport)
    {
        return (type.equals(fdImport.getType()) && name.equals(fdImport.getName()) && this.isCompatibleProperties(fdImport));
    }
    
    /**
     * Adds the XML representation of the properties of this FieldDef into the given SOAPElement
     * @param se the given SOAPElement where to add the XML representation of the properties
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public abstract void addPropertiesToXml(SOAPElement se) throws SOAPException;
    
    /**
     * Sets the properties of this FieldDef separated by '|'
     * @param sProperties the properties
     */
    public abstract void setProperties(String sProperties);
    
    /**
     * Returns the properties of this FieldDef separated by '|'
     * @return the properties
     */
    public abstract String getProperties();
    
    /**
     * Returns the properties of this FieldDef in a Vector
     * @return a Vector of {@link Property} containing the propiertes of this FieldDef
     */
    public abstract Vector getVProperties();
    
    /**
     * Returns the default value to put on the Fields of a Record when we add a new FieldDef into a Collection
     * @return the default value
     */
    public abstract String getDefaultValue(); 
    
    /**
     * Checks if a the given value satisfy the propierties of the FieldDef
     * @param sValue the value to check
     * @return returns true if the value satisfy the properties of the FieldDef, else returns false
     */
    public abstract boolean checkValue(String sValue);
    
    /**
     * Checks if a given comparator can be used on this FieldDef to search for a value
     * @param sComparator the comparator to check
     * @return returns true if the comparator can be used on this FieldDef, else returns false
     */
    public abstract boolean checkComparator(String sComparator);
    
    /**
     * Returns the comparators that can be used on this FieldDef to search for a value
     * @return a Vector of Strings containing the comparators that can be used to search on this FieldDef
     */
    public abstract Vector getComparators();
    
    
    /**
     * Returns if the properties of the this FieldDef are compatible with a given FieldDef to import. For the moment, two properties are compatible only when they are equals
     * @param fdImport the FieldDef to check if properties are compatible
     * @return returns true if both FieldDefs have equals properties, else returns false
     */
    public boolean isCompatibleProperties(FieldDef fdImport)
    {
        return (getProperties().equals(fdImport.getProperties()));
        //In the fututre can be abstract to specify better the compatibilities between two fielddefs
    }
    
}

