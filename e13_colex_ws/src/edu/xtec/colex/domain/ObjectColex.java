/*
 * File    : ObjectColex.java
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

/**
 * All the domain of the application extends from this abstract class
 * @author ogalzorriz
 * @version 1.0
 */
public abstract class ObjectColex {
    
    /**
     * A factory for creating various objects that exists in the SOAP XML
     */
    
    protected static SOAPFactory soapFactory;
    
    static
    {
        try
        {
            soapFactory = SOAPFactory.newInstance();
        }
        catch(SOAPException e) 
        {
            e.printStackTrace();
        }
    }
 
    /**
     * Converts this object into a SOAPElement. We use this method to put all the domain elements into a SOAPMessage
     * @return the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    abstract public SOAPElement toXml() throws SOAPException;
    
}
