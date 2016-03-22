/*
 * File    : Guest.java
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
import java.util.*;
import java.text.*;

/**
 * Class that represents a User when it is working as Guest of a Collection. A Guest object stores the permission level (PERMISSION_NONE,PERMISSION_READ,PERMISSION_TOTAL) that the users has on a collection and when was the last access to it.
 * @author ogalzorriz
 * @version 1.0
 */
public class Guest extends User 
{
    /**
     * The permission level that has this user on a collection
     */
    private int permission=-1;
    /**
     * When was the last access from this user to a collection
     */
    private Date lastAccess = null;
    /**
     * int constant for PERMISSION_NONE level
     */
    public static int PERMISSION_NONE = 0;
    /**
     * int constant for PERMISSION_READ level
     */
    public static int PERMISSION_READ = 1;
    /**
     * int constant for PERMISSION_TOTAL level
     */
    public static int PERMISSION_TOTAL = 2;
    
    /**
     * Creates a new Guest with the given userId
     * @param sUserId the id of the Guest
     */
    public Guest(String sUserId)
    {
        super.userId=sUserId;
    }
    
    /**
     * Creates a new Guest from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public Guest(SOAPElement se) throws SOAPException
    {
        Iterator It=se.getChildElements(soapFactory.createName("userId"));
        super.userId=((SOAPElement)It.next()).getValue();
        
        It=se.getChildElements(soapFactory.createName("permission"));
        
        if (It.hasNext()) 
        {
            String sPermission = ((SOAPElement)It.next()).getValue();
            permission=Integer.parseInt(sPermission);
        }
        
        It=se.getChildElements(soapFactory.createName("lastAccess"));
        
        if (It.hasNext()) 
        {
            String sLastDate = ((SOAPElement)It.next()).getValue();
            
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            try
            {
                lastAccess = df.parse(sLastDate);
            }
            catch(ParseException pe)
            {
                lastAccess = null;
            }
        }
    }
    
    /**
     * Sets the permission of this Guest on a collection
     * @param iPermission the permission level (PERMISSION_NONE,PERMISSION_READ,PERMISSION_TOTAL)
     */
    public void setPermission(int iPermission)
    {
        permission=iPermission;
    }
    
    /**
     * Returns the permission level to a collection
     * @return the permission
     */
    public int getPermission()
    {
        return permission;
    }
    
    /**
     * Sets the last access of this Guest to a collection
     * @param dLastAccess the lastAccess
     */
    public void setLastAcces(Date dLastAccess)
    {
        lastAccess=dLastAccess;
    }
    
    /**
     * Returns the lastAcces Date to a collection
     * @return the lastAccess
     */
    public Date getLastAccess()
    {
        return lastAccess;
    }

    /**
     * Returns in a Vector the permissions level that we can use to add a user into a collection
     * @return a Vector containing the strings "PERMISSION_NONE","PERMISSION_READ","PERMISSION_TOTAL"
     */
    public static Vector getVPermissions()
    {
        Vector vPermissions = new Vector();
        
        vPermissions.add("PERMISSION_NONE");
        vPermissions.add("PERMISSION_READ");
        vPermissions.add("PERMISSION_TOTAL");
        
        return vPermissions;
    }
    
    /**
     * Converts this object into a SOAPElement
     * @return the SOAPElement containing the XML representation of this Guest
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seGuest=null,seUserId,sePermission,seLastAccess;
  
        seGuest = soapFactory.createElement("guest");
        seUserId = soapFactory.createElement("userId");
        seUserId.addTextNode(userId);
        seGuest.addChildElement(seUserId);
        
        if (permission!=-1)
        {
            sePermission = soapFactory.createElement("permission");
            sePermission.addTextNode(Integer.toString(permission));
            seGuest.addChildElement(sePermission);
        }
        
        if (lastAccess != null)
        {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            
            seLastAccess = soapFactory.createElement("lastAccess");
            seLastAccess.addTextNode(df.format(lastAccess));
            
            seGuest.addChildElement(seLastAccess);
        }
        return seGuest;
    }
}
