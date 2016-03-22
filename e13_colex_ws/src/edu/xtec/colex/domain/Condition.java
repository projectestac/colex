/*
 * File    : Condition.java
 * Created : 27-jun-2005 12:24
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
import org.w3c.dom.Element;

/**
 * Class that represents the Condition object used by the Query object to search in the Records of a Collection
 * @author ogalzorriz
 * @version 1.0
 */
public class Condition extends ObjectColex{

    /**
     * The operator between this Condition and the previous one inside a {@link Query}. It can be 'AND' or 'OR'
     */
    private String operator;
    /**
     * The fieldName where to search
     */
    private String fieldName;
    /**
     * The comparator between the fieldName and the value. It can be '=','!=','<','>','LIKE'
     */
    private String comparator;
    /**
     * The value to search for
     */
    private String value;
    
    /**
     * The id of the FieldDef assigned by the system
     */
    private int idFieldDef;
    
    /**
     * Creates a new instance of Condition
     * @param sOperator the Operator between this and the previous condition
     * @param sFieldName the FieldName where to search
     * @param sComparator the comparator between the fieldName and the value
     * @param sValue the value to search for
     */
    public Condition(String sOperator, String sFieldName, String sComparator, String sValue)
    {
        operator = sOperator;
        fieldName = sFieldName;
        comparator = sComparator;
        value = sValue;   
    }
    
    /**
     * Creates a new Condition from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Condition(SOAPElement se) throws SOAPException
    {
        Iterator iAux;
        
        iAux=se.getChildElements(soapFactory.createName("operator"));
        operator=((SOAPElement)iAux.next()).getValue();
        iAux=se.getChildElements(soapFactory.createName("fieldName"));
        fieldName=((SOAPElement)iAux.next()).getValue();
        iAux=se.getChildElements(soapFactory.createName("comparator"));
        comparator=((SOAPElement)iAux.next()).getValue();
        iAux=se.getChildElements(soapFactory.createName("value"));
        value=((SOAPElement)iAux.next()).getValue();
        if (value==null) value="";
    }
    
    /**
     * Sets the operator
     * @param sOperator the operator
     */
    public void setOperator(String sOperator)
    {
        operator=sOperator;
    }
    
    /**
     * Returns the operator
     * @return the operator
     */
    public String getOperator()
    {
        return operator;
    }
    
    /**
     * Sets the fieldName
     * @param sFieldName the fieldName
     */
    public void setFieldName(String sFieldName)
    {
        fieldName=sFieldName;
    }
    
    /**
     * Returns the fieldName
     * @return the fieldName
     */
    public String getFieldName()
    {
        return fieldName;
    }
    
    /**
     * Sets the comparator
     * @param sComparator the comparator
     */
    public void setComparator(String sComparator)
    {
        comparator=sComparator;
    }
    
    /**
     * Returns the comparator
     * @return the comparator
     */
    public String getComparator()
    {
        return comparator;
    }
    
    /**
     * Sets the value
     * @param sValue the value
     */
    public void setValue(String sValue)
    {
        value=sValue;
    }
    
    /**
     * Returns the value
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Sets the idFieldDef
     * @param iIdFieldDef the idFieldDef
     */
    public void setIdFieldDef(int iIdFieldDef)
    {
        idFieldDef=iIdFieldDef;
    }
    
    /**
     * Returns the idFieldDef
     * @return the idFieldDef
     */
    public int getIdFieldDef()
    {
        return idFieldDef;
    }

    /**
     * Converts this object into a SOAPElement
     * @return the SOAPElement containing the XML representation of this Condition
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seCondition=null,seOperator,seFieldName,seComparator,seValue;
        
        seCondition = soapFactory.createElement("condition");
        
        seOperator = soapFactory.createElement("operator");
        seOperator.addTextNode(operator);
        seFieldName = soapFactory.createElement("fieldName");
        seFieldName.addTextNode(fieldName);
        seComparator = soapFactory.createElement("comparator");
        seComparator.addTextNode(comparator);
        seValue = soapFactory.createElement("value");
        seValue.addTextNode(value);

        seCondition.addChildElement(seOperator);
        seCondition.addChildElement(seFieldName);
        seCondition.addChildElement(seComparator);
        seCondition.addChildElement(seValue);
        
        return seCondition;
    }
}
