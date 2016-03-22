/*
 * File    : ServletShare.java
 * Created : 18-oct-2005 17:56
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
package edu.xtec.colex.server;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.soap.*;
import java.util.*;
import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.utils.Utils;
import edu.xtec.colex.domain.*;
import edu.xtec.colex.exception.*;

/**
 * Servlet class that processes all the web service operation requests related to Share collections and records: 
 * <ul>
 * <li><I>addGuest (User,Collection,Guest) : void</I></li>
 * <li><I>deleteGuest (User,Collection,Guest) : void</I></li>
 * <li><I>modifyPermission (User,Collection,Guest) : void</I></li>
 * <li><I>listGuests (User,Owner,Collection) : [Guest]</I></li>
 * <li><I>listGuestCollections (User) : [Collection+Guest]</I></li>
 * <li><I>getPermission (User,Owner,Collection,Record) : Guest</I></li>
 * <li><I>getLog (User,Owner,Collection) : [LogOperation]</I></li>
 * </ul>
 * @author ogalzorriz
 * @version 1.0
 */

public class ServletShare extends ServletMain{

    /**
     * Auxiliar method to check with a internet browser if the Servlet is running
     * @param req request
     * @param resp response
     * @throws javax.servlet.ServletException when an internal Exception error occurs
     * @throws java.io.IOException when an IO Exception error occurs
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        
        PrintWriter out = resp.getWriter();
        out.println("I am ServletShare");
        out.flush();        
        out.close();
    }
    
    /**
     * Processes the SOAPMessage
     * @param smRequest the SOAPMessage request of the web service operation
     * @return the SOAPMessage response of the web service operation
     */
    public SOAPMessage processMessage(SOAPMessage smRequest)
    {
        SOAPMessage smResponse=null;
        try
        {
            SOAPBody sbRequest = smRequest.getSOAPBody();
            SOAPBodyElement sbeRequest=null;
        
            Iterator it = sbRequest.getChildElements();
        
            if (it.hasNext()) sbeRequest = (SOAPBodyElement) it.next();
        
            String sQuery=sbeRequest.getElementName().getLocalName();
          
            if (sQuery.equals("addGuest")) 
                    {smResponse = addGuest(sbeRequest);}
            else if (sQuery.equals("deleteGuest")) 
                    {smResponse = deleteGuest(sbeRequest);}
            else if (sQuery.equals("modifyPermission")) 
                    {smResponse = modifyPermission(sbeRequest);}
            else if (sQuery.equals("listGuests")) 
                    {smResponse = listGuests(sbeRequest);}
            else if (sQuery.equals("listGuestCollections")) 
                    {smResponse = listGuestCollections(sbeRequest);}
            else if (sQuery.equals("getPermission")) 
                    {smResponse = getPermission(sbeRequest);}
            else if (sQuery.equals("getLog")) 
                    {smResponse = getLog(sbeRequest);}
            else 
            {   
                smResponse = createFault(new DataBaseException(DataBaseException.NO_FOUND_SERVICE));
            }
        }
        catch(DataBaseException dbe)
        { 
            return createFault(dbe);
        }
        catch(ServerException sve)
        {
            return internalError(sve);
        }
        catch(SOAPException se)
        {
            return internalError(se);
        }
        return smResponse;
    }        
    
    /**
     * Implements the web service operation <I>addGuest (User,Collection,Guest) : void</I>, adding the Guest to the Collection.
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner and the Guest
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage addGuest(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Guest gRequest = (Guest) getParam(sbeRequest, "guest");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: addGuest Params: Collection = "+cRequest.getName()+ " Guest = "+gRequest.getUserId());
        
        db.addGuest(uRequest,cRequest,gRequest);
        
        smResponse = createResponse("respAddGuest","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>deleteGuest (User,Collection,Guest) : void</I>, deleteing the Guest of the Collection.
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner and the Guest
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage deleteGuest(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Guest gRequest = (Guest) getParam(sbeRequest, "guest");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: deleteGuest Params: Collection = "+cRequest.getName()+ " Guest = "+gRequest.getUserId());
        
        db.deleteGuest(uRequest,cRequest,gRequest);
        
        smResponse = createResponse("respDeleteGuest","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>modifyPermission (User,Collection,Guest) : void</I>, setting the permission of the Guest to READ or TOTAL
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner and the Guest
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage modifyPermission(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Guest gRequest = (Guest) getParam(sbeRequest, "guest");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: modifyGuest Params: Collection = "+cRequest.getName()+ " Guest = "+gRequest.getUserId());
        
        db.modifyPermission(uRequest,cRequest,gRequest);
        
        smResponse = createResponse("respModifyPermission","Ok");
               
        return smResponse;
    }

    /**
     * Implements the web service operation <I>listGuests (User,Owner,Collection) : [Guest]</I>, returnig the Guests of the Collection. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner and the Collection
     * @return the SOAPMessage response containing a Vector of Guests of the Collection
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage listGuests(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        Vector vGuests = null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        
              
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
        
        logger.debug("User: "+uRequest.getUserId()+" Operation: listGuests Params: Collection = "+cRequest.getName()+sOwner);
        
        vGuests = db.listGuests(uRequest,oRequest,cRequest);
        
        smResponse = createResponse("respListGuests",vGuests,"guests");
        
        return smResponse;
    }
 
    /**
     * Implements the web service operation <I>listGuestCollections (User) : [Collection+Guest]</I>, returnig the Collections where the User is Guest
     * @param sbeRequest the SOAPBodyElement request containing the User
     * @return the SOAPMessage response containing a Vector of pairs of Collection+Guest. We use a Guest object to return the userId of the Owner of the Collection
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage listGuestCollections(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        Vector vFieldDefs = null;
        Vector vGuestCollections = null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
          
        logger.debug("User: "+uRequest.getUserId()+" Operation: listGuestCollections");
        
        vGuestCollections = db.listGuestCollections(uRequest);
        
        smResponse = createResponseLGC(vGuestCollections);
        
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>getPermission (User,Owner,Collection,Record) : Guest</I>, returnig the permission of the User for the Collection. If Owner == null the collection owner is the User. For the moment we do not consider the Record, we just check the permission in a Collection level
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner, the Collection and the Record
     * @return the SOAPMessage response containing the Guest object that we use to return the permission
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage getPermission(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Record rRequest = (Record) getParam(sbeRequest,"record");
        
        logger.debug("User: "+uRequest.getUserId()+" Operation: getPermission Params: Collection = "+cRequest.getName()+" Owner = "+oRequest.getUserId());
        
        int iPermission = db.getPermission(uRequest,oRequest,cRequest,rRequest);
        
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
            Name n = sf.createName("respGetPermission");
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
            SOAPElement seResponse;
            
            n = sf.createName("permission");
        
            seResponse = sbeResponse.addChildElement(n);
            seResponse.addTextNode(Integer.toString(iPermission));
            smResponse.saveChanges();
        }
        catch(Exception se)
        {
            return internalError(se);
        }
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>getLog (User,Owner,Collection) : [LogOperation]</I>, returning all the LogOperation for the Collection. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner and the Collection
     * @return the SOAPMessage response containing a Vector of the LogOperations
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage getLog(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        Vector vLogOperations = null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        
              
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
        
        logger.debug("User: "+uRequest.getUserId()+" Operation: getLog Params: Collection = "+cRequest.getName()+sOwner);
        
        vLogOperations = db.getLog(uRequest,oRequest,cRequest);
        
        smResponse = createResponse("respGetLog",vLogOperations,"logs");
        
        return smResponse;
    }

    /**
     * Creates the response for the web service operation <I>listGuestCollections</I>
     * @param vParams a Vector containing the pairs Collection+Guest
     * @return the SOAPMessage response
     */
    protected SOAPMessage createResponseLGC(Vector vParams)
    {
        SOAPMessage smResponse=null;
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
            Name n = sf.createName("respListGuestCollections");
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
            SOAPElement seResponse,seResponseAux;
            
            int i=0;
           
            n = sf.createName("guestCollections");
        
            seResponse = sbeResponse.addChildElement(n);
        
            ObjectColex ocolexAux;
            
            while(i<vParams.size()) 
            {
                
                n = sf.createName("guestCollection");
                seResponseAux = seResponse.addChildElement(n);
                
                ocolexAux = (ObjectColex) vParams.get(i);
                seResponseAux.addChildElement(ocolexAux.toXml());
                i++;
                
                ocolexAux = (ObjectColex) vParams.get(i);
                seResponseAux.addChildElement(ocolexAux.toXml());
                i++;
            }
        
            smResponse.saveChanges();
        }
        catch(Exception se)
        {
            se.printStackTrace();
            return internalError(se);
        }
        return smResponse;
    }    
}

