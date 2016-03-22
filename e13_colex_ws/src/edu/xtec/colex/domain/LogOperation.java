/*
 * File    : LogOperation.java
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
 * Class that represents the LogOperation object. A LogOperation is used to store information about the modifications that are done into a Collection that is shared by users. A LogOperation is formed by the user who does the operation, the operation (ADD_RECORD,DELETE_RECORD,MODIFY_RECORD), the id of the record and the date when has done.
 * @author ogalzorriz
 * @version 1.0
 */

public class LogOperation extends ObjectColex 
{
    /**
     * the idUser of who does the operation
     */
    protected String idUser;
    /**
     * the operation done
     */
    protected int operation;
    /**
     * the idRecord of the record where the operation is done
     */
    protected int idRecord;
    /**
     * the date when the operation is done
     */
    protected Date date;
    /**
     * the textual representation of the operation used in the share.jsp
     */
    protected String text;
    /**
     * Auxiliar attribute to format the Date
     */
    protected DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    /**
     * int constant for ADD_RECORD operation
     */
    public static int ADD_RECORD = 1;
    /**
     * int constant for DELETE_RECORD operation
     */
    public static int DELETE_RECORD = 2;
    /**
     * int constant for MODIFY_RECORD operation
     */
    public static int MODIFY_RECORD = 3;
    
    /**
     * Creates a new empty LogOperation
     */
    public LogOperation()
    {}
    
    /**
     * Creates a new LogOperation from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the object
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public LogOperation(SOAPElement se) throws SOAPException
    {
        Iterator It;
        It=se.getChildElements(soapFactory.createName("idUser"));
        idUser = ((SOAPElement)It.next()).getValue();
        
        It=se.getChildElements(soapFactory.createName("operation"));
        operation = Integer.parseInt(((SOAPElement)It.next()).getValue());
        
        It=se.getChildElements(soapFactory.createName("idRecord"));
        idRecord = Integer.parseInt(((SOAPElement)It.next()).getValue());
        
        It=se.getChildElements(soapFactory.createName("date"));
        String sDate = ((SOAPElement)It.next()).getValue();

        try
        {
            date = df.parse(sDate);
        }
        catch(ParseException pe)
        {
            date = null;
        }

        It=se.getChildElements(soapFactory.createName("text"));
        text = ((SOAPElement)It.next()).getValue();
    }
    
    /**
     * Sets the idUser
     * @param sIdUser the idUser
     */
    public void setIdUser(String sIdUser)
    {
        idUser = sIdUser;
    }
    
    /**
     * Returns the idUser
     * @return the idUser
     */
    public String getIdUser()
    {
        return idUser;
    }
    
    /**
     * Sets the operation
     * @param iOperation the operation (ADD_RECORD|DELETE_RECORD|MODIFY_RECORD)
     */
    public void setOperation(int iOperation)
    {
        operation = iOperation;
    }
    
    /**
     * Returns the operation (ADD_RECORD|DELETE_RECORD|MODIFY_RECORD)
     * @return the operation 
     */
    public int getOperation()
    {
        return operation;
    }
    
    /**
     * Sets the idRecord
     * @param iIdRecord the idRecord
     */
    public void setIdRecord(int iIdRecord)
    {
        idRecord = iIdRecord;
    }
    
    /**
     * Returns the idRecord
     * @return the idRecord 
     */
    public int getIdRecord()
    {
        return idRecord;
    }
    
    /**
     * Sets the date
     * @param dDate the date
     */
    public void setDate (Date dDate)
    {
        date = dDate;
    }
    
    /**
     * Returns the date
     * @return the date 
     */
    public Date getDate()
    {
        return date;
    }
    
    /**
     * Sets the textual representation of this operation
     * @param sText the text
     */
    public void setText (String sText)
    {
        text = sText;
    }
    
    /**
     * Returns the textual representation of this operation
     * @return the text 
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Converts this object into a SOAPElement
     * @return SOAPElement containing the XML representation of this LogOperation
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seLogOperation=null,seIdUser,seOperation,seIdRecord,seDate,seIdText;
  
        seLogOperation = soapFactory.createElement("logOperation");
        
        seIdUser = soapFactory.createElement("idUser");
        seIdUser.addTextNode(idUser);
        seLogOperation.addChildElement(seIdUser);
        
        seOperation = soapFactory.createElement("operation");
        seOperation.addTextNode(Integer.toString(operation));
        seLogOperation.addChildElement(seOperation);
        
        seIdRecord = soapFactory.createElement("idRecord");
        seIdRecord.addTextNode(Integer.toString(idRecord));
        seLogOperation.addChildElement(seIdRecord);
        
        seDate = soapFactory.createElement("date");
        seDate.addTextNode(df.format(date));
        seLogOperation.addChildElement(seDate);
               
        seIdText = soapFactory.createElement("text");
        seIdText.addTextNode(text);
        seLogOperation.addChildElement(seIdText);

        return seLogOperation;
    }
}
