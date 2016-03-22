/*
 * File    : Attachment.java
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
import java.util.*;
import java.text.*;

/**
 * Class that represents the BrowseCriteria object used on the web service operation <I>browse(BrowseCriteria) : [Collection]</I>
 * @author ogalzorriz
 * @version 1.0
 */
public class BrowseCriteria extends ObjectColex{
    
    /**
     * the name of the field to browse by, it can be nameCollection,idUser,tags or description
     */
    private String browseBy = "";
    /**
     * the value to browse
     */
    private String value = "";
    /**
     * the field to order by
     */
    private String orderBy = "nameCollection";
    /**
     * the direction 'asc' or 'desc'
     */
    private String direction = "asc";
    /**
     * the first index
     */
    private int indexBegin = 0;
    /**
     * the last index
     */
    private int indexEnd = 0;
    
    /**
     * Creates a new BrowseCriteria
     */
    public BrowseCriteria ()
    {
    }
    
    /**
     * Creates a new BrowseCriteria from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public BrowseCriteria(SOAPElement se) throws SOAPException
    {
        
        Iterator iAux=se.getChildElements(soapFactory.createName("browseBy"));
        browseBy=((SOAPElement)iAux.next()).getValue();
        
        iAux=se.getChildElements(soapFactory.createName("value"));
        value=((SOAPElement)iAux.next()).getValue();
        
        iAux=se.getChildElements(soapFactory.createName("indexBegin"));
        indexBegin = Integer.parseInt(((SOAPElement)iAux.next()).getValue());
        
        iAux=se.getChildElements(soapFactory.createName("indexEnd"));
        indexEnd = Integer.parseInt(((SOAPElement)iAux.next()).getValue());
        
        iAux=se.getChildElements(soapFactory.createName("orderBy"));
        orderBy=((SOAPElement)iAux.next()).getValue();
        
        iAux=se.getChildElements(soapFactory.createName("direction"));
        direction=((SOAPElement)iAux.next()).getValue();
    }
        
    /**
     * Sets the browseby field
     * @param sBrowseBy the browse by field
     */
    public void setBrowseBy(String sBrowseBy)
    {
        browseBy=sBrowseBy;
    }
    
    /**
     * Returns the browseby field
     * @return the browseby field
     */
    public String getBrowseBy()
    {
        return browseBy;
    }
    
    /**
     * Set the browse value
     * @param sValue the browse value
     */
    public void setValue(String sValue)
    {
        value=sValue;
    }
    
    /**
     * Returns the browse value
     * @return the browse value
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Sets the order by field
     * @param sOrderBy the order by field
     */
    public void setOrderBy(String sOrderBy)
    {
        orderBy=sOrderBy;
    }
    
    /**
     * Returns the order by value
     * @return the order by value
     */
    public String getOrderBy()
    {
        return orderBy;
    }
    
    /**
     * Sets the direction to browse
     * @param sDirection the direction to browse
     */
    public void setDirection(String sDirection)
    {
        direction=sDirection;
    }
    
    /**
     * Returns the direction to browse
     * @return the direction to browse
     */
    public String getDirection()
    {
        return direction;
    }
        
    /**
     * Sets the first index to browse
     * @param iIndexBegin the first index to browse
     */
    public void setIndexBegin(int iIndexBegin)
    {
        indexBegin=iIndexBegin;
    }
    
    /**
     * Returns the first index to browse
     * @return the first index to browse
     */
    public int getIndexBegin()
    {
        return indexBegin;
    }
    
    /**
     * Sets the last index to browse
     * @param iIndexEnd the last index to browse
     */
    public void setIndexEnd(int iIndexEnd)
    {
        indexEnd=iIndexEnd;
    }
    
    /**
     * Returns the last index to browse
     * @return the last index to browse
     */
    public int getIndexEnd()
    {
        return indexEnd;
    }
    
    /**
     * Converts this object into a SOAPElement
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the SOAPElement containing the XML representation of this BrowseCriteria
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seBrowseCriteria=null;
        SOAPElement seBrowseBy,seValue,seIndexBegin,seIndexEnd,seOrderBy,seDirection;
        
        seBrowseCriteria = soapFactory.createElement("browseCriteria");
        
        seBrowseBy = soapFactory.createElement("browseBy");
        seBrowseBy.addTextNode(browseBy);
        seBrowseCriteria.addChildElement(seBrowseBy);
        
        seValue = soapFactory.createElement("value");
        seValue.addTextNode(value);
        seBrowseCriteria.addChildElement(seValue);
        
        seIndexBegin = soapFactory.createElement("indexBegin");
        seIndexBegin.addTextNode(String.valueOf(indexBegin));
        seBrowseCriteria.addChildElement(seIndexBegin);
        
        seIndexEnd = soapFactory.createElement("indexEnd");
        seIndexEnd.addTextNode(String.valueOf(indexEnd));
        seBrowseCriteria.addChildElement(seIndexEnd);
        
        seOrderBy = soapFactory.createElement("orderBy");
        seOrderBy.addTextNode(orderBy);
        seBrowseCriteria.addChildElement(seOrderBy);
        
        seDirection = soapFactory.createElement("direction");
        seDirection.addTextNode(direction);
        seBrowseCriteria.addChildElement(seDirection);
        
        return seBrowseCriteria;
    }
    
    /**
     * Returns a vector with the String options to browseBy [nameCollection,idUser,tags,description]
     * @return a Vector containing the Strings : [nameCollection,idUser,tags,description]
     */
    public static Vector getVBrowseBy()
    {
        Vector vBrowseBy = new Vector();
        
        vBrowseBy.add("nameCollection");
        vBrowseBy.add("idUser");
        vBrowseBy.add("tags");
        vBrowseBy.add("description");
        
        return vBrowseBy;
    }
    
    /**
     * Returns a vector with the String options to orderBy [nameCollection,idUser,numRecords,created]
     * @return a Vector containing the Strings : [nameCollection,idUser,numRecords,created]
     */
    public static Vector getVOrderBy()
    {   
        Vector vOrderBy = new Vector();
        
        vOrderBy.add("nameCollection");
        vOrderBy.add("idUser");
        vOrderBy.add("numRecords");
        vOrderBy.add("created");
        
        return vOrderBy;
    }
}
