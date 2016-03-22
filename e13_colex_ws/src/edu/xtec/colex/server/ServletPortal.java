/*
 * File    : ServletPortal.java
 * Created : 21-jun-2007 17:56
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
import edu.xtec.colex.domain.*;
import edu.xtec.colex.exception.*;
/**
 * Servlet class that processes all the web service operation requests of the Portal: 
 * <ul>
 * <li><I>browse(BrowseCriteria) : [Collection,User]</I></li>
 * <li><I>listTagClouds(numTags) : [Tag]</I></li>
 * </ul>
 * @author oriol
 * @version 1.0
 */
public class ServletPortal extends ServletMain{
    
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
        out.println("I am ServletPortal");
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
          
            if (sQuery.equals("browse")) 
                    {smResponse = browse(sbeRequest);}
            else if (sQuery.equals("listTagClouds"))
                    {smResponse = listTagClouds(getIntValue(smRequest,"numTags"));}
            else 
            {   
                smResponse = createFault(new DataBaseException(DataBaseException.NO_FOUND_SERVICE));
            }
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
     * Implements the web service operation <I>browse(BrowseCriteria) : [Collection,User]</I>, returning all public collections matching the BrowseCriteria
     * @param smRequest the SOAPMessage request containing the BrowseCriteria
     * @return the SOAPMessage response containing a Vector of Collection
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage browse(SOAPBodyElement smRequest) throws ServerException
    {
        SOAPMessage smResponse=null;
        Vector vCollections = null;
               
        BrowseCriteria bcRequest = (BrowseCriteria) getParam(smRequest, "browseCriteria");
        
        logger.info("Operation: browse");
        
        vCollections = db.browse(bcRequest);
        
        smResponse = createResponseBrowse(vCollections);
               
        return smResponse;
        
    }
    
    /**
     * Implements the web service operation <I>listTagClouds(numTags) : [Tag]</I>, returning the number of tagClouds requested. If numTags is negative it returns all the tags used in all public collections.
     * @param numTags number of Tags to return
     * @return the SOAPMessage response containing a Vector of Tag
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage listTagClouds(int numTags) throws ServerException
    {
        
        SOAPMessage smResponse=null;
        Vector vCollections = new Vector();
               
        logger.info("Operation: listTagClouds");
        
        Vector vTagClouds = db.listTagClouds(numTags);
        
        smResponse = createResponseListTagClous(vTagClouds);
               
        return smResponse;
    }
    
    /**
     * Creates the response for the web service operation <I>browse</I>
     * @param vParams a Vector containing the Collections returned
     * @return the SOAPMessage response
     */
    protected SOAPMessage createResponseBrowse(Vector vParams)
    {
        SOAPMessage smResponse=null;
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
        
            Name n = sf.createName("respBrowse");
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
            SOAPElement seResponse,seResponseAux;
            
            int i=0;
            
            n = sf.createName("numFound");
            seResponse = sbeResponse.addChildElement(n);
            Integer iCount = (Integer)vParams.get(i);
            seResponse.addTextNode(iCount.toString());            
            i++;
            
            n = sf.createName("browseCollections");
        
            seResponse = sbeResponse.addChildElement(n);
 
            ObjectColex ocolexAux;
            
            while(i<vParams.size()) 
            {
                
                n = sf.createName("browseCollection");
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
    
    /**
     * Creates the response for the web service operation <I>listTagClouds</I>
     * @param vParams a Vector containing the Tags returned
     * @return the SOAPMessage response
     */
    protected SOAPMessage createResponseListTagClous(Vector vParams)
    {
        SOAPMessage smResponse=null;
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
        
            Name n = sf.createName("respListTagClouds");
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
            SOAPElement seResponse,seResponseAux;
            
            int i=0;
            
            n = sf.createName("minTimes");
            seResponse = sbeResponse.addChildElement(n);
            Integer iMinTimes = (Integer)vParams.get(i);
            seResponse.addTextNode(iMinTimes.toString());            
            i++;
            
            n = sf.createName("maxTimes");
            seResponse = sbeResponse.addChildElement(n);
            Integer iMaxTimes = (Integer)vParams.get(i);
            seResponse.addTextNode(iMaxTimes.toString());            
            i++;
            
            n = sf.createName("tags");
        
            seResponse = sbeResponse.addChildElement(n);
        
            
            ObjectColex ocolexAux;
            
            while(i<vParams.size()) 
            {
                ocolexAux = (ObjectColex) vParams.get(i);
                seResponse.addChildElement(ocolexAux.toXml());
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

    /**
     * Searches and returns an int parameter inside a SOAPMessage
     * @param sm the SOAPMessage where to search
     * @param parameter the name of the parameter
     * @return the int value found
     * @throws javax.xml.soap.SOAPException when an internal Exception error occurs
     */
    private int getIntValue(SOAPMessage sm, String parameter) throws SOAPException
    {
        SOAPBody sb = sm.getSOAPBody();
       
        Iterator it = sb.getChildElements();
        SOAPElement se = (SOAPElement) it.next();
       
        it = se.getChildElements(sf.createName(parameter));
        
        se = (SOAPElement) it.next();
        
        return Integer.parseInt(se.getValue());
    }
}
