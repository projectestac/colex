/*
 * File    : ServletCollection.java
 * Created : 07-jun-2005 17:56
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
import edu.xtec.colex.exception.*;
import javax.activation.DataHandler;
import edu.xtec.colex.utils.Utils;
import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.domain.*;

/**
 * Servlet class that processes all the web service operation requests for the Structure of a collection: 
 * <ul>
 * <li><I>createCollection(User,Collection) : void</I></li>
 * <li><I>listCollections(User) : [Collection]</I></li>
 * <li><I>deleteCollection(User,Collection) : void</I></li>
 * <li><I>exportCollection(User,Owner,Collection,Query) : FILE</I></li>
 * <li><I>importCollection(User,Collection,FILE) : void</I></li>
 * </ul>
 * @author ogalzorriz
 * @version 1.0
 */
public class ServletCollection extends ServletMain{
    
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
        out.println("I am ServletCollection");
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
        
            String sQuery = sbeRequest.getElementName().getLocalName();
          
       
            if      (sQuery.equals("createCollection")) 
                    {smResponse = createCollection(sbeRequest);}
            else if (sQuery.equals("listCollections")) 
                    {smResponse = listCollections(sbeRequest);}
            else if (sQuery.equals("deleteCollection")) 
                    {smResponse = deleteCollection(sbeRequest);}
            else if (sQuery.equals("exportCollection"))
                    {smResponse = exportCollection(sbeRequest);}
            else if (sQuery.equals("importCollection"))
                    {smResponse = importCollection(sbeRequest,smRequest);}
            else 
            {   
                smResponse = createFault(new DataBaseException(DataBaseException.NO_FOUND_SERVICE));
            }
        }
        catch(DataBaseException dbe)
        {
            smResponse = createFault(dbe);
            return smResponse;
        }
        catch(ServerException sve)
        {
            smResponse = internalError(sve);
            return smResponse;
        }
        catch(SOAPException se)
        {
            return internalError(se);
        }

        return smResponse;
    }        

    /**
     * Implements the web service operation <I>listCollections(User) : [Collection]</I>, returning the Collections of the User
     * @param sbeRequest the SOAPBodyElement request containing the User
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     * @return the SOAPMessage response containing a Vector of Collections
     */
    public SOAPMessage listCollections(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {    
        SOAPMessage smResponse=null;
        Vector vCollections = null;
            
        User uRequest = (User) getParam(sbeRequest, "user");
    
        logger.debug("User: "+uRequest.getUserId()+" Operation: listCollections");
        
        vCollections = db.listCollections(uRequest);
    
        smResponse = createResponse("respListCollections",vCollections,"collections");
            
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>createCollection(User,Collection) : void</I>, creating a new empty Collection
     * @param sbeRequest the SOAPBodyElement request containing the User and the Collection
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage createCollection(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");

        logger.info("User: "+uRequest.getUserId()+" Operation: createCollection Params: Collection = "+cRequest.getName());
                
        db.createCollection(uRequest,cRequest);

        smResponse = createResponse("respCreateCollection","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>deleteCollection(User,Collection) : void</I>, deleting the Collection
     * @param sbeRequest the SOAPBodyElement request containing the User and the Collection
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage deleteCollection(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: deleteCollection Params: Collection = "+cRequest.getName());
       
        db.deleteCollection(uRequest,cRequest);
                
        smResponse = createResponse("respDeleteCollection","Ok");
   
        return smResponse;
    }
    
     /**
     * Implements the web service operation <I>exportCollection(User,Owner,Collection,Query) : FILE</I>, returning a FILE containing the FieldDefs and the Records of the Collection matching the Query. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner, the Collection and the Query
     * @return the SOAPMessage response containing a ZIP FILE. The file contains an XML with the information of Collection, the FieldDefs and all the Records. If there are attachments (images or sounds) they are also included in the ZIP.
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    
    public SOAPMessage exportCollection(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Query qRequest = (Query) getParam(sbeRequest,"query");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        
        Vector vFieldsDefs,vRecords,vFieldsDefsAtt,vAttachments;
        
        vRecords = new Vector();
        vFieldsDefs = new Vector();
        vFieldsDefsAtt = new Vector();
        vAttachments= new Vector();
        
        String sOwner = "";
        if (oRequest!=null) sOwner = "Owner = "+oRequest.getUserId();
        
        logger.debug("User: "+uRequest.getUserId()+" Operation: exportCollection Params: Collection = "+cRequest.getName()+sOwner);
        
        db.exportCollection(uRequest,oRequest,cRequest,qRequest,vFieldsDefs,vRecords,vFieldsDefsAtt,vAttachments);
              
        Record rAux;
        FieldDef fdAux;
        String sValue;
        int iPos;
 
        cRequest.setTags(edu.xtec.colex.utils.Tags.decode(cRequest.getTags()));
        
        for (int i=0;i<vRecords.size();i++)
        {       
            rAux = (Record) vRecords.get(i);
            
            rAux.setId(-1); //Set the id to -1 because we do not want to show it on the XML
            
            for (int j=0; j<vFieldsDefs.size();j++)
            {
                fdAux = (FieldDef) vFieldsDefs.get(j);
                
                if (fdAux.getType().equals("image")||fdAux.getType().equals("sound"))
                {               
                    sValue = rAux.getFieldValue(fdAux.getName());
                    
                    if (!sValue.equals("null"))
                    {
                        iPos = sValue.lastIndexOf("/"); // Remove the path to the files                            
                        sValue = sValue.substring(iPos+1, sValue.length());
                        rAux.modifyFieldValue(fdAux.getName(), sValue);
                    }
                }
            }
        }
        
        
        smResponse = createResponse("respExportCollection","Ok");
        
        if (oRequest != null)
        {
            uRequest.setUserId(oRequest.getUserId());
        }
        
        File fileZip = Utils.toZip(cRequest,vFieldsDefs,vRecords,vAttachments, getServerProperties().getProperty("dir.files"));        
        
        URL url;
        try
        {
            url = fileZip.toURI().toURL();
            
        }
        catch (MalformedURLException murle){logger.error("EXCEPTION exporting collection: "+fileZip);murle.printStackTrace();throw new ServerException(murle);}
          
        DataHandler dataHandler = new DataHandler(url);
            
        
       
        
        
        
        AttachmentPart att = smResponse.createAttachmentPart(dataHandler);
        
        att.setContentType("application/x-zip-compressed");
        att.setContentId("zipFile");
        
        smResponse.addAttachmentPart(att);
        
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>importCollection(User,Collection,FILE) : void</I>, creating a new empty Collection from the FILE
     * @param sbeRequest the SOAPBodyElement request containing the User and the Collection
     * @param smRequest the SOAPMessage request containing the zip FILE with the XML definition of the Collection, the Records and the Attachments if there are
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage importCollection(SOAPBodyElement sbeRequest, SOAPMessage smRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        
        Iterator it = smRequest.getAttachments();
        
        AttachmentPart zipAtt = (AttachmentPart) it.next();
        
        Vector vFieldsDefs = new Vector();
        Vector vRecords = new Vector();
        Hashtable hTempFiles = new Hashtable();
        
        int iAttachmentsSize = 0;
        
        logger.info("User: "+uRequest.getUserId()+" Operation: importCollection Params: Collection = "+cRequest.getName());
                
        try
        {
            iAttachmentsSize = Utils.unZip(cRequest,zipAtt,vFieldsDefs,vRecords,hTempFiles);
        }
        catch (DataBaseException dbe)
        {
            throw new DataBaseException(DataBaseException.NO_VALID_IMPORT_FILE);
        }
        
        int iSize = Utils.getDirSize(ServletMain.getServerProperties().getProperty("dir.files"),uRequest);
        int diskQuota = db.getDiskQuota(uRequest);
        
        if ((iSize + iAttachmentsSize) > diskQuota) throw new DataBaseException(DataBaseException.DISK_QUOTA_EXCEDED);
        
        db.createCollectionDefined(uRequest,cRequest,vFieldsDefs);

        try
        {
            for (int i=0;i<vRecords.size();i=i+2)
            {
                db.addRecord(uRequest, null, cRequest, (Record) vRecords.get(i), (Vector) vRecords.get(i+1));
            }
        
        }
        catch (DataBaseException dbe)
        {
            db.deleteCollection(uRequest,cRequest);
            throw new DataBaseException(DataBaseException.NO_VALID_IMPORT_FILE);
        }
        //Delete all temporal files included in the zipfile
        
        Enumeration e = hTempFiles.elements();
                
        while (e.hasMoreElements())
        {
            ((File)e.nextElement()).delete();
        }
     
        smResponse = createResponse("respImportCollection","Ok");
    
        return smResponse;
    }

}
