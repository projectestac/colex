/*
 * File    : ServletRecord.java
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
import edu.xtec.colex.domain.Record;
import edu.xtec.colex.domain.Owner;
import edu.xtec.colex.domain.Query;
import edu.xtec.colex.domain.User;
import edu.xtec.colex.exception.ServerException;
import edu.xtec.colex.exception.DataBaseException;

/**
 * Servlet class that processes all the web service operation requests for a Record: 
 * <ul>
 * <li><I>addRecord(User,Owner,Collection,Record) : void</I></li>
 * <li><I>deleteRecord(User,Owner,Collection,Record) : void</I></li>
 * <li><I>modifyRecord(User,Owner,Collection,Record) : void</I></li>
 * <li><I>searchAll(User,Owner,Collection,Query) : NumRecord,NumFound,[Record]</I></li>
 * <li><I>importRecords(User,Owner,Collection,FILE) : void</I></li>
 * </ul>
 * @author ogalzorriz
 * @version 1.0
 */

public class ServletRecord extends ServletMain{

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
        out.println("I am ServletRecord");
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
          
            if(sQuery.equals("addRecord")) 
            {
                Vector vAttachments = checkAttachmentsSize(smRequest,sbeRequest);                
                smResponse = addRecord(sbeRequest,vAttachments);
            }   
            else if (sQuery.equals("search")) 
                    {smResponse = search (sbeRequest, false);}
            else if (sQuery.equals("searchAll")) 
                    {smResponse = search (sbeRequest, true);}
            else if (sQuery.equals("deleteRecord")) 
                    {smResponse = deleteRecord(sbeRequest);}
            else if (sQuery.equals("modifyRecord")) 
            {
                Vector vAttachments = checkAttachmentsSize(smRequest,sbeRequest);                
                smResponse = modifyRecord(sbeRequest,vAttachments);
            }
            else if (sQuery.equals("importRecords")) 
            {
                smResponse = importRecords(sbeRequest,smRequest);
            }
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
        catch(IOException ioe)
        {   
            return internalError(ioe);
        }    
        return smResponse;
    }        

    /**
     * Implements the web service operation <I>addRecord(User,Owner,Collection,Record) : void</I>, adding the Record to the Collection. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner, the Collection and the Record
     * @param vAttachments a Vector containing the AttachmentParts if there are
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage addRecord(SOAPBodyElement sbeRequest, Vector vAttachments) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Record rRequest = (Record) getParam(sbeRequest,"record");

        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
        logger.info("User: "+uRequest.getUserId()+" Operation: addRecord Params: Collection = "+cRequest.getName()+sOwner);
        
        db.addRecord(uRequest,oRequest,cRequest,rRequest,vAttachments);
       
        smResponse = createResponse("respAddRecord","Ok");               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>deleteRecord(User,Owner,Collection,Record) : void</I>, deleting the Record from the Collection. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner, the Collection and the Record. The Record has the id to identify it on the database
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage deleteRecord(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Record rRequest = (Record) getParam(sbeRequest, "record");
        
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
  
        logger.info("User: "+uRequest.getUserId()+" Operation: deleteRecord Params: Collection = "+cRequest.getName()+" idRecord = "+rRequest.getId()+sOwner);
        
        db.deleteRecord(uRequest,oRequest,cRequest,rRequest);
        
        smResponse = createResponse("respDeleteRecord","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>modifyRecord(User,Owner,Collection,Record) : void</I>, adding the Record to the Collection. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner, the Collection and the Record. The Record has the id to identify it on the database
     * @param vAttachments a Vector containing the modified AttachmentParts if there are
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage modifyRecord(SOAPBodyElement sbeRequest, Vector vAttachments) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Record rRequest = (Record) getParam(sbeRequest,"record");
        
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();

        logger.info("User: "+uRequest.getUserId()+" Operation: modifyRecord Params: Collection = "+cRequest.getName()+" idRecord = "+rRequest.getId()+sOwner);
        
        db.modifyRecord(uRequest,oRequest,cRequest,rRequest,vAttachments);
        
        smResponse = createResponse("respModifyRecord","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>searchAll(User,Owner,Collection,Query) : NumRecord,NumFound,[Record]</I>, returning the Records matching the Query. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner, the Collection and the Query.
     * @param bAllRecords if true return all the records, else return just one record
     * @return the SOAPMessage response containing a Vector of all the Records matching the Query
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage search(SOAPBodyElement sbeRequest, boolean bAllRecords) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Query qRequest = (Query) getParam(sbeRequest,"query");
        
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
        
        logger.debug("User: "+uRequest.getUserId()+" Operation: searchCollection Params: Collection = "+cRequest.getName()+sOwner);
        
        Vector vRecords = db.search(uRequest,oRequest,cRequest,qRequest,bAllRecords);
                
        if (bAllRecords) smResponse = createResponse("respSearchAll",vRecords,"records");
        else smResponse = createResponse("respSearch",vRecords,"records");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>importRecords(User,Owner,Collection,FILE) : void</I>, importing the Records of the FILE into the Collection. The Records of the FILE must have the same Fields Definitions structure as the destination Collection and it has to be created before. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Owner and the Collection
     * @param smRequest the SOAPMessage request containing the zip FILE with the XML definition of the Records and the Attachments if there are
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage importRecords(SOAPBodyElement sbeRequest, SOAPMessage smRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest,"owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
      
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
        
        Iterator it = smRequest.getAttachments();
        
        AttachmentPart zipAtt = (AttachmentPart) it.next();
        
        Vector vFieldsDefs = new Vector();
        Vector vRecords = new Vector();
        Hashtable hTempFiles = new Hashtable();
        
        int iAttachmentsSize = 0;
        
        logger.info("User: "+uRequest.getUserId()+" Operation: importRecords Params: Collection = "+cRequest.getName()+sOwner);
                
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
        
        if (!db.isStructureCompatible(uRequest,oRequest,cRequest,vFieldsDefs)) throw new DataBaseException(DataBaseException.NO_STRUCTURE_COMPATIBLE);
        
        try
        {
            for (int i=0;i<vRecords.size();i=i+2)
            {
                db.addRecord(uRequest, oRequest, cRequest, (Record) vRecords.get(i), (Vector) vRecords.get(i+1));
            }
        }
        catch (DataBaseException dbe)
        {
            throw dbe;
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
    
    /**
     * Checks if the User has enough space to store the AttachmentParts and returns all of them on a Vector
     * @param smRequest the SOAPMessage request containing the AttachmentParts
     * @param sbeRequest the SOAPBodyElement request containing the User
     * @return a Vector containg all the AttachmentParts
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws javax.xml.soap.SOAPException when an internal Exception error occurs
     * @throws java.io.IOException when an IO Exception error occurs
     */
    private Vector checkAttachmentsSize(SOAPMessage smRequest, SOAPBodyElement sbeRequest) throws ServerException,DataBaseException,SOAPException, IOException
    {
        Vector vAttachments = new Vector();
        Iterator iAttachments = smRequest.getAttachments();
        
        int iBytesSize=0;
        
        InputStream is;
                    
        while (iAttachments.hasNext()) 
        {
            AttachmentPart apAux= (AttachmentPart) iAttachments.next();
            
            is = apAux.getDataHandler().getInputStream();
            
            byte[] readbuf = new byte[1024];
            
            int read = 0;
                
            while ((read = is.read(readbuf, 0, readbuf.length)) != -1) 
            {
                iBytesSize = iBytesSize +read;
            } 
            vAttachments.add(apAux);
        }
                
        User uRequest = (User) getParam(sbeRequest,"user");

        int iSize = Utils.getDirSize(ServletMain.getServerProperties().getProperty("dir.files"),uRequest);
        
        int diskQuota = db.getDiskQuota(uRequest);
        
        if ((iSize + iBytesSize) > diskQuota) throw new DataBaseException(DataBaseException.DISK_QUOTA_EXCEDED);
     
        return vAttachments;
    }

}

