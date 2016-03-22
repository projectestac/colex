/*
 * File    : ServletStructure.java
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
import edu.xtec.colex.domain.*;
import edu.xtec.colex.exception.*;
import edu.xtec.colex.utils.Utils;
import javax.activation.DataHandler;

/**
 * Servlet class that processes all the web service operation requests for the Structure of a collection: 
 * <ul>
 * <li><I>addFieldDef(User,Collection,FieldDef) : void</I></li>
 * <li><I>deleteFieldDef(User,Collection,FieldDef) : void</I></li>
 * <li><I>modifyFieldDef(User,Collection,FieldDefOld,FieldDefNew) : void</I></li>
 * <li><I>getStructure(User,Owner,Collection) : [FieldDef]</I></li>
 * <li><I>modifyCollection(User,oldName,Collection) : void</I></li>
 * <li><I>getCollectionInfo(User,Owner,Collection) : Collection</I></li>
 * <li><I>increaseFieldDefPos(User,Collection,FieldDef) : void</I></li>
 * </ul>
 * @author ogalzorriz
 * @version 1.0
 */
public class ServletStructure extends ServletMain{
    
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
        out.println("I am ServletStructure");
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
          
            if      (sQuery.equals("addFieldDef")) 
                    {smResponse = addFieldDef(sbeRequest);}
            else if (sQuery.equals("deleteFieldDef")) 
                    {smResponse = deleteFieldDef(sbeRequest);}
            else if (sQuery.equals("modifyFieldDef")) 
                    {smResponse = modifyFieldDef(sbeRequest);}
            else if (sQuery.equals("getStructure")) 
                    {smResponse = getStructure(sbeRequest);}
            else if (sQuery.equals("modifyCollection")) 
                    {smResponse = modifyCollection(sbeRequest);}
            else if (sQuery.equals("getCollectionInfo")) 
                    {smResponse = getCollectionInfo(sbeRequest);}
            else if (sQuery.equals("increaseFieldDefPos")) 
                    {smResponse = increaseFieldDefPos(sbeRequest);}
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
        catch(Exception e)
        {
            e.printStackTrace();
            return internalError(e);
        }
        return smResponse;
    }        

    /**
     * Implements the web service operation <I>addFieldDef(User,Collection,FieldDef) : void</I>, adding the FieldDef to the Collection.
     * @param sbeRequest the SOAPBodyElement request containing the User, the Collection and the FiledDef
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage addFieldDef(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        FieldDef fdRequest = (FieldDef) getParam(sbeRequest, "fieldDef");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: addFieldDef Params: Collection = "+cRequest.getName()+ " FieldName = "+fdRequest.getName());
        
        db.addFieldDef(uRequest,cRequest,fdRequest);
        
        smResponse = createResponse("respAddFieldDef","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>deleteFieldDef(User,Collection,FieldDef) : void</I>, deleting the FieldDef from the Collection.
     * @param sbeRequest the SOAPBodyElement request containing the User, the Collection and the FiledDef
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage deleteFieldDef(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        FieldDef fdRequest = (FieldDef) getParam(sbeRequest, "fieldDef");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: deleteFieldDef Params: Collection = "+cRequest.getName()+ " FieldName = "+fdRequest.getName());
        
        db.deleteFieldDef(uRequest,cRequest,fdRequest);
        
        smResponse = createResponse("respDeleteFieldDef","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>modifyFieldDef(User,Collection,FieldDefOld,FieldDefNew) : void</I>, modfying the FieldDef from the Collection. It is only possible to modify: (1) The name of the FieldDef, (2) The properties of the FieldDef if every Field of the given Collection will satisfy the new properties
     * @param sbeRequest the SOAPBodyElement request containing the User, the Collection, the FiledDefOld and the FieldDefNew
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage modifyFieldDef(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        
        Vector vOldFD = getFieldsDefs(sbeRequest,"fieldsOld");
        Vector vNewFD = getFieldsDefs(sbeRequest,"fieldsNew");
        
        FieldDef fdOld = (FieldDef) vOldFD.get(0); //Only needed fot the logger
        
        logger.info("User: "+uRequest.getUserId()+" Operation: modifyFieldDef Params: Collection = "+cRequest.getName()+ " FieldName = "+fdOld.getName());
         
        db.modifyFieldDef(uRequest,cRequest,vOldFD,vNewFD);
        
        smResponse = createResponse("respModifyFieldDef","Ok");
               
        return smResponse;
    }
    
    /**
     * Implements the web service <I>getStructure(User,Owner,Collection) : [FieldDef]</I>, returning the Structure of the Collection. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Collection and the Owner
     * @return the SOAPMessage response containing a Vector of the FieldDefs
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage getStructure(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        Vector vFieldDefs = null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
              
        String sOwner = "";
        if (oRequest!=null) sOwner = " Owner = "+oRequest.getUserId();
        
        logger.debug("User: "+uRequest.getUserId()+" Operation: getStructure Params: Collection = "+cRequest.getName()+sOwner);
        
        vFieldDefs = db.getStructure(uRequest,cRequest,oRequest);
        
        smResponse = createResponse("respGetStructure",vFieldDefs,"fields");
        
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>modifyCollection(User,oldName,Collection) : void</I>, modifying the name, description,tags and isPublic of the Collection
     * @param sbeRequest the SOAPBodyElement request containing the User, the oldName and the Collection. We use the pair User+oldName to identify the Collection
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage modifyCollection(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        SOAPMessage smResponse=null;
        Vector vFieldDefs = null;
        
        User uRequest = (User) getParam(sbeRequest, "user");
        
        String oldName = getOldName(sbeRequest);
        
        Collection cNewCollection = (Collection) getParam(sbeRequest,"collection");
       
        logger.info("User: "+uRequest.getUserId()+" Operation: modifyCollection Params: Collection = "+oldName);
                
        db.modifyCollection(uRequest, new Collection(oldName), cNewCollection);
     
        smResponse = createResponse("respModifyCollection","Ok");

        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>getCollectionInfo(User,Owner,Collection) : Collection</I>, returning a Collection object filled with all the information: numRecords, isPublic, description, tags. If Owner == null the collection owner is the User
     * @param sbeRequest the SOAPBodyElement request containing the User, the Collection and the Owner
     * @return the SOAPMessage response containing a Collection object with all the information
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage getCollectionInfo(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {        
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Owner oRequest = (Owner) getParam(sbeRequest, "owner");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
                
        logger.debug("User: "+uRequest.getUserId()+" Operation: getCollectionInfo Params: Collection = "+cRequest.getName());
        
        Collection cResponse = db.getCollectionInfo(uRequest,oRequest,cRequest);
                
        smResponse = createResponse("respGetCollectionInfo",cResponse);
               
        return smResponse;
    }
    
    /**
     * Implements the web service operation <I>increaseFieldDefPos(User,Collection,FieldDef) : void</I>, increasing the position of the FieldDef
     * @param sbeRequest the SOAPBodyElement request containing the User, the Collection and the FiledDef
     * @return a SOAPMessage response "Ok" if everything is done
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public SOAPMessage increaseFieldDefPos(SOAPBodyElement sbeRequest) throws DataBaseException,ServerException
    {
        
        SOAPMessage smResponse=null;
               
        User uRequest = (User) getParam(sbeRequest, "user");
        Collection cRequest = (Collection) getParam(sbeRequest,"collection");
        FieldDef fdRequest = (FieldDef) getParam(sbeRequest,"fieldDef");
        
        logger.info("User: "+uRequest.getUserId()+" Operation: increaseFieldDefPos Params: Collection = "+cRequest.getName() +" FieldName = "+fdRequest.getName());
        
        db.increaseFieldDefPos(uRequest,cRequest,fdRequest);
        
        
        smResponse = createResponse("respIncreaseFieldDefPos","Ok");
               
        return smResponse;
    }
    
    /**
     * Auxiliar method to get the oldName of the collection on modifyCollection
     * @param sbeRequest the SOAPBodyElement request containing the oldName
     * @return the oldName String
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    private String getOldName(SOAPBodyElement sbeRequest) throws ServerException
    {
        String sRes="";
        try 
        {
            Iterator iAux=sbeRequest.getChildElements(sf.createName("oldName"));
            
            if (iAux.hasNext()) 
            {
                sRes = ((SOAPElement)iAux.next()).getValue();
            }
        } 
        catch (SOAPException se)
        {
            throw new ServerException(se);
        }
        
        return sRes;
    }
}

