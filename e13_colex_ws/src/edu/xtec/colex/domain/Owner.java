/*
 * File    : Owner.java
 * Created : 27-sep-2007 12:24
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
 * Class that represents a User when it is working as Owner of a Collection
 * @author ogalzorriz
 * @version 1.0
 */
public class Owner extends User
{
    /**
     * Creates a new Owner with the given userId
     * @param sUserId the id of the Owner
     */
    public Owner(String sUserId)
    {
        super.userId=sUserId;       
    }
    
    /**
     * Creates a new Owner from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Owner(SOAPElement se) throws SOAPException
    {
        Iterator It=se.getChildElements(soapFactory.createName("userId"));
        super.userId=((SOAPElement)It.next()).getValue();
    }
    
    /**
     * Converts this object into a SOAPElement
     * @return the SOAPElement containing the XML representation of this Owner
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seUser=null,seUserId;
        
        seUser = soapFactory.createElement("owner");
        seUserId = soapFactory.createElement("userId");
        seUserId.addTextNode(userId);
        seUser.addChildElement(seUserId);
        
        return seUser;
    }
}
