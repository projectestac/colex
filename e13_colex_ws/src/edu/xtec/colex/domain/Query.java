/*
 * File    : Query.java
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
import java.util.Vector;

/**
 * Class that represents the Query object used to search in the Records of a Collection. A query is formed by the orderField to order by, the direction (asc|desc), the beginIndex and a set of conditions. 
 * @author ogalzorriz
 * @version 1.0
 */
public class Query extends ObjectColex
{
    /**
     * The orderField to order by
     */
    private String orderField;
    /**
     * The direction to order by
     */
    private String direction;
    /**
     * The beginIndex where to start to show results
     */
    private int beginIndex;
    /**
     * The set of conditions to filter on the search
     */
    private Vector Conditions;
    
    /**
     * Creates a new empty Query
     */
    public Query() 
    {
        Conditions = new Vector();
    }
    
    /**
     * Creates a Query from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Query(SOAPElement se) throws SOAPException
    {
        Iterator iAux;
        
        iAux=se.getChildElements(soapFactory.createName("orderField"));
        orderField = ((SOAPElement)iAux.next()).getValue();
        
        iAux=se.getChildElements(soapFactory.createName("direction"));
        direction = ((SOAPElement)iAux.next()).getValue();
        
        iAux=se.getChildElements(soapFactory.createName("beginIndex"));
        beginIndex = Integer.parseInt(((SOAPElement)iAux.next()).getValue());
        
        iAux=se.getChildElements(soapFactory.createName("condition"));
        
        Conditions = new Vector();
        
        while (iAux.hasNext())
        {
            Conditions.add(new Condition((SOAPElement)iAux.next()));
        }
    }
    
    /**
     * Sets the orderField
     * @param sOrderField the orderField
     */
    public void setOrderField(String sOrderField)
    {
        orderField=sOrderField;
    }
    
    /**
     * Returns the orderField
     * @return the orderField
     */
    public String getOrderField()
    {
        return orderField;
    }
    
    /**
     * Sets the direction
     * @param sDirection the direction
     */
    public void setDirection(String sDirection)
    {
        direction=sDirection;
    }
    
    /**
     * Returns the direction
     * @return the direction
     */
    public String getDirection()
    {
        return direction;
    }
    
    /**
     * Sets the beginIndex
     * @param iBeginIndex the beginIndex
     */
    public void setBeginIndex(int iBeginIndex)
    {
        beginIndex=iBeginIndex;
    }
    
    /**
     * Returns the beginIndex
     * @return the beginIndex
     */
    public int getBeginIndex()
    {
        return beginIndex;
    }
    
    /**
     * Returns the Conditions of this Query in a Vector
     * @return a Vector of {@link Condition} containing the conditions of this Query
     */
    public Vector getConditions()
    {
        return Conditions;
    }
    
    /**
     * Adds a Condition to this Query
     * @param cnd the Condition to add
     */
    public void addCondition(Condition cnd)
    {
        Conditions.add(cnd);
    }
    
    /**
     * Deletes a Condition from the Query
     * @param cnd the Condition to delete
     */
    public void deleteCondition(Condition cnd)
    {
        Conditions.remove(cnd);
    }
      
    /**
     * Converts this object into a SOAPElement
     * @return SOAPElement containing the XML representation of this Query
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        Condition cndAux;
        SOAPElement seQuery,seOrderField,seBeginIndex,seDirection;
        
        seQuery = soapFactory.createElement("query");
   
        seOrderField = soapFactory.createElement("orderField");
        seOrderField.addTextNode(orderField);
        seDirection = soapFactory.createElement("direction");
        seDirection.addTextNode(direction);
        seBeginIndex = soapFactory.createElement("beginIndex");
        seBeginIndex.addTextNode(String.valueOf(beginIndex));
        
        seQuery.addChildElement(seOrderField);
        seQuery.addChildElement(seDirection);
        seQuery.addChildElement(seBeginIndex);

        for (int i =0;i<Conditions.size();i++)
        {
            cndAux = (Condition) Conditions.get(i);
            seQuery.addChildElement(cndAux.toXml());
        }
            
        return seQuery;
    }
    
    /**
     * Returns the number of conditions that has the query
     * @return the number of Conditions
     */
    public int getSize()
    {
        return Conditions.size();
    }
}
