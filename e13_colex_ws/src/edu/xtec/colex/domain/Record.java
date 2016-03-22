/*
 * File    : Record.java
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
 * Class that represents the Record object. A Record is a set of {@link Field}.
 * @author ogalzorriz
 * @version 1.0
 */
public class Record extends ObjectColex
{
    /**
     * The Vector containing the Fields
     */
    private Vector Fields;
    /**
     * The id of the Record assigned by the system
     */
    private int id=-1;
    
    /**
     * Creates a new empty Record
     */
    public Record() 
    {
        Fields = new Vector();
    }
    
    /**
     * Creates a Record from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Record(SOAPElement se) throws SOAPException
    {
        SOAPElement seId;
        Iterator It=se.getChildElements(soapFactory.createName("id"));

        if (It.hasNext())
        {
            seId = (SOAPElement) It.next();
            id = Integer.parseInt(seId.getValue());
        }
        
        It=se.getChildElements(soapFactory.createName("field"));

        Fields = new Vector();
        
        while (It.hasNext())
        {
            Fields.add(new Field((SOAPElement)It.next()));
        }
    }
    
    /**
     * Sets the id of this Record
     * @param iId the int value of the id
     */
    public void setId(int iId)
    {
        id=iId;
    }
    
    /**
     * Returns the id of this Record
     * @return the int value of the id
     */
    public int getId()
    {
        return id;
    }
    
    /**
     * Sets the Fields of this Record
     * @param vFields a Vector containing the Fields
     */
    public void setFields(Vector vFields)
    {
        Fields = vFields;
    }

    /**
     * Returns the Fields of this Record in a Vector
     * @return a Vector of {@link Field} containing the fields of this Record
     */
    public Vector getFields()
    {
        return Fields;
    }
    
    /**
     * Adds a Field to this Record
     * @param f the Field to add
     */
    public void addField(Field f)
    {
        Fields.add(f);
    }
    
    /**
     * Deletes a Field from the Record
     * @param f the Field to delete
     */
    public void deleteField(Field f)
    {
        Fields.remove(f);
    }

    /**
     * Returns the value of a Field from this Record
     * @param sName the Name of the Field
     * @return the Value of the Field
     */
    public String getFieldValue(String sName)
    {
        String sValue="";
        Field fAux;
        
        boolean bFound = false;
        
        for (int i=0; i<Fields.size() && (!bFound);i++)
        {
            fAux = (Field) Fields.get(i);
            
            if (fAux.getName().equals(sName))
            {
                sValue=fAux.getValue();
                bFound=true;
            }
        
        }
        return sValue;
    
    }
    
    /**
     * Modifies the value of a Field from this Record
     * @param sName the Name of the field
     * @param sValue the new Value of the Field
     */
    public void modifyFieldValue(String sName, String sValue)
    {
        Field fAux;
        boolean bFound = false;
        
        for (int i=0; i<Fields.size() && (!bFound);i++)
        {
            fAux = (Field) Fields.get(i);
            
            if (fAux.getName().equals(sName))
            {
                fAux.setValue(sValue);
                bFound=true;
            }       
        }
    }
  
    /**
     * Converts this object into a SOAPElement
     * @return the SOAPElement containing the XML representation of this Record
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        Field fAux;
        
        SOAPElement seRecord = soapFactory.createElement("record");
            
        if (id!=-1)
        {
            SOAPElement seID = soapFactory.createElement("id");
            seID.addTextNode(Integer.toString(id));
            seRecord.addChildElement(seID);
        }
        
        for (int i=0;i<Fields.size();i++)
        {
            fAux = (Field) Fields.get(i);
            seRecord.addChildElement(fAux.toXml());
        }
            
        return seRecord;
    }
}