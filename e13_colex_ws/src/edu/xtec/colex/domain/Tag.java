/*
 * File    : Tag.java
 * Created : 27-jun-2007 12:24
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
 * Class that represents the Tag object. A Tag object is formed by its value, the number of times used to describe a public Collection and the size used to show it on the browse.jsp.
 * @author ogalzorriz
 * @version 1.0
 */
public class Tag extends ObjectColex
{
    /**
     * the value of the Tag
     */
    private String value ="";
    /**
     * the times that the Tag has used to describe a public Collection
     */
    private int times=-1;
    /**
     * the size that we will use to show the Tag
     */
    private int fontSize = -1;
    
    /**
     * Creates a new Tag with the given Value and Times
     * @param sValue the Value of the Tag
     * @param iTimes the Times of the Tag
     */
    public Tag(String sValue, int iTimes)
    {
        value= sValue;
        times= iTimes;
    }
    
    /**
     * Creates a new Tag from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Tag(SOAPElement se) throws SOAPException
    {
        Iterator It=se.getChildElements(soapFactory.createName("value"));
        value=((SOAPElement)It.next()).getValue();
        
        It=se.getChildElements(soapFactory.createName("times"));
        times = Integer.parseInt(((SOAPElement) It.next()).getValue());  
    }
    
    /**
     * Sets the value of the Tag
     * @param sValue the value
     */
    public void setValue(String sValue)
    {
        value=sValue;
    }
    
    /**
     * Returns the value of the Tag
     * @return the value
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Sets the times of the Tag
     * @param iTimes the times
     */
    public void setTimes(int iTimes)
    {
        times=iTimes;
    }
    
    /**
     * Returns the times of the Tag
     * @return the times
     */
    public int getTimes()
    {
        return times;
    }
    
    /**
     * Sets the fontSize of the Tag
     * @param iFontSize the fontSize
     */
    public void setFontSize(int iFontSize)
    {
        fontSize=iFontSize;
    }
    
    /**
     * Returns the fontSize of the Tag
     * @return the fontSize
     */
    public int getFontSize()
    {
        return fontSize;
    }
    
    /**
     * Converts this object into a SOAPElement
     * @return SOAPElement containing the XML representation of this Tag
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seTag=null,seValue,seTimes;
  
        seTag= soapFactory.createElement("tag");
        seValue = soapFactory.createElement("value");
        seValue.addTextNode(value);
        seTag.addChildElement(seValue);
        
        seTimes = soapFactory.createElement("times");
        seTimes.addTextNode(Integer.toString(times));
        seTag.addChildElement(seTimes);  
        
        return seTag;
    }
}
