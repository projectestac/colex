/*
 * File    : ColexStructureBean.java
 * Created : 13-sep-2005 17:56
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
package edu.xtec.colex.client.beans;

import edu.xtec.colex.domain.*;
import edu.xtec.colex.utils.*;
import javax.xml.soap.*;
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import javax.servlet.http.*;

/**
 * Bean to processes all the requests from the JSP structure.jsp, where is implemented the page for modifyng the structure of a collection
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexStructureBean extends ColexMainBean 
{
    /**
     * the Vector containing the FieldDefs of the Collection
     */
    protected Vector vFieldDefs;
    /**
     * String name of the collection
     */
    protected String collection;
    /**
     * String description of the Collection
     */
    protected String description;
    /**
     * String tags of the Collection
     */
    protected String tags;
    /**
     * Boolean to set if the collection is public
     */
    protected boolean isPublic=false;
    
    /**
     * Method to complete the initialitzation of the bean
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if everything has been initialized, else false
     */
    public boolean initImpl(HttpServletRequest request,HttpServletResponse response)
    {
        boolean bOK = true;
        
        if (bOK)
        {
            collection = request.getParameter("collection");
                                
            if (collection == null)
            {
                bOK=false;
                redirectPage="index.jsp";
            }
            else
            {
                String operation = request.getParameter("operation");
                
                try
                {
                    if (operation!=null)
                    {
                        if (operation.equals("add"))
                        {
                            parseAddFieldDef();
                        }
                        else if(operation.equals("delete"))
                        {
                            parseDeleteFieldDef();
                        }
                        else if(operation.equals("modify"))
                        {
                            getStructure();
                            parseModifyFieldDef();
                        }   
                        else if(operation.equals("modifyCollection"))
                        {
                            modifyCollection(request.getParameter("newName"),request.getParameter("description"),request.getParameter("tags"));
                        }
                        else if(operation.equals("increasePosition"))
                        {
                            parseIncreasePosition();
                        }
                    }
                    
                    getStructure();
                    getCollectionInfo();
                    
                }
                catch (SOAPException se)
                {
                    logger.error("User: "+getUserId()+" Exception: "+se);
                    bOK= false;
                    redirectPage="error.jsp";
                }
                catch (IOException ioe)
                {
                    logger.error("User: "+getUserId()+" Exception: "+ioe);
                    bOK= false;
                    redirectPage="error.jsp";
                }
                catch (Exception e)
                {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String stacktrace = sw.toString();
            
                    //logger.error("User: "+userId+" Exception: "+e); //Just print the exception without the stacktrace
            
                    logger.error("User: "+getUserId()+" StrackTrace: \n"+stacktrace);
                    
                    bOK=false;
                    redirectPage="error.jsp";
                }
            }
        }
        return bOK;
    }
    
    /**
     * Method called from the jsp, it returs the String name of the current Collection
     * @return the String name
     */
    public String getCollection()
    {
        return collection;
    }
    
    /**
     * Method called from the jsp, it returns the String description of the Collection
     * @return the String description
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Method called from the jsp, it returns the String tags of the Collection
     * @return the String tags
     */
    public String getTags()
    {
        return tags;
    }
    
    /**
     * Method called from the jsp, it returs the FieldDefs of the Collection
     * @return a Vector containing the FieldDefs
     */
    public Vector retrieveFieldDefs()
    {
        return vFieldDefs;
    }
    
    /**
     * Method called from the jsp, it returs the all the FieldDefs available to create a FieldDef
     * @return a Vector containing the FieldDefs
     */
    public Vector retriveTypes()
    {
        return FieldDef.getAllTypes();
    }
    
    /**
     * Method called from the jsp, it returns if Collection is public
     * @return true if the Collection is public, else false
     */
    public boolean isPublic()
    {
        return isPublic;
    }
    
    /**
     * Method called from the jsp, it returns the name of the collection or in case of Fault the newName parameter
     * @return the String name of the collection or in case of Fault the newName parameter
     */
    public String getNewName()
    {
        String sNewName = request.getParameter("newName");
        
        if (sNewName != null) return sNewName;
        
        else return collection;
    }
    
    /**
     * Method called from the jsp, when in case of Fault get the addFieldName parameter else returns string empty ""
     * @return the String addFieldName parameter
     */
    public String getAddFieldName()
    {
        String sAddFieldName = request.getParameter("addFieldName");
        
        if ((sAddFieldName != null) && (getFault("add")!="")) return sAddFieldName;
        else return "";
    }

    /**
     * Method called from the jsp, when in case of Fault get the type parameter else returns string empty ""
     * @return the String type parameter
     */
    public String getAddFieldType()
    {
        String sAddFieldType = request.getParameter("type");
        
        if ((sAddFieldType != null) && (getFault("add")!="")) return sAddFieldType;
        else return "";
    }
    
    /**
     * Method called from the jsp, when in case of Fault get the position of the field trying to modify
     * @return the int position of the field trying to modify
     */
    public int getModifyFieldPos()
    {
        String sFieldName = request.getParameter("oldName");
        FieldDef fdOld = null;
                            
        boolean bFound=false;
        int i=0;
                    
        while (i<vFieldDefs.size()&&!bFound)
        {
            fdOld = (FieldDef) vFieldDefs.get(i);
                        
            if (sFieldName.equals(fdOld.getName()))
            {
                bFound=true;
            }
            else i++;
        }
        return i;
    }
    
    /**
     * Calls the web service operation <I>getStructure(User,Owner,Collection) : [FieldDef]</I>
     * @throws javax.xml.soap.SOAPException  when a SOAPException error occurs
     */
    public void getStructure() throws SOAPException
    {
        User u = new User(getUserId());
        vFieldDefs = new Vector();
        
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"getStructure");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest,new Collection(collection));
        
            smRequest.saveChanges();
            
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
    
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"get");
            }
            else
            {
                vFieldDefs = getFieldDefs(smResponse);
            }   
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
         
    /**
     * Calls the web service operation <I>getCollectionInfo(User,Owner,Collection) : Collection</I>
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void getCollectionInfo() throws SOAPException
    {
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"getCollectionInfo");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest,new Collection(collection));
        
            smRequest.saveChanges();
            
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
     
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"get");
            }
            else
            {
                Collection cResponse = getCollection(smResponse);
                
                description = cResponse.getDescription();
                tags = Tags.decode(cResponse.getTags());
                
                int iIsPublic = cResponse.getIsPublic();
                
                if(iIsPublic == 0) isPublic = false;
                else isPublic = true;
            }
        }   
        catch(SOAPException se)
        {
            throw se;
        }
    
    }

    /**
     * Parses the parameters and calls the web service operation
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void parseAddFieldDef() throws SOAPException
    {
        String sType = request.getParameter("type");
                    
        FieldDef fd = FieldDef.createFieldDef(sType);
                    
        fd.setName(request.getParameter("addFieldName"));
        
        if (sType.equals("select"))
        {
            fd.setProperties(request.getParameter("hiddenOptions"));    
        }
        else
        {
            Vector vProperties = fd.getVProperties();
            String sProperties = "";
        
            Property pAux;

            for (int i=0; i<vProperties.size();i++)
            {
                pAux = (Property) vProperties.get(i);
            
                sProperties = sProperties + request.getParameter(pAux.getName())+"|";
            }
            fd.setProperties(sProperties);
        }
        addFieldDef(fd);
    }
   
    /**
     * Calls the web service operation <I>addFieldDef(User,Collection,FieldDef) : void</I></li>
     * @param fd the FieldDef to add
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void addFieldDef(FieldDef fd) throws SOAPException
    {
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"addFieldDef");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest, new Collection(collection));
            addParam(sbeRequest, fd);
        
            smRequest.saveChanges();
        
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
        
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"add");
            }
            else
            {
                
            }
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
    
    /**
     * Parses the parameters and calls the web service operation
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void parseDeleteFieldDef() throws SOAPException
    {
        FieldDef fd = FieldDef.createFieldDef("text");
        fd.setName(request.getParameter("oldName"));
        fd.setProperties("0|");
        //The only important thing is the name of the Field Def
        deleteFieldDef(fd);
    }
    
    /**
     * Calls the web service operation <I>deleteFieldDef(User,Collection,FieldDef) : void</I></li>
     * @param fd the FieldDef to delete
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void deleteFieldDef(FieldDef fd) throws SOAPException
    {
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"deleteFieldDef");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest,new Collection(collection));
            addParam(sbeRequest, fd);
        
            smRequest.saveChanges();
        
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
        
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"delete");
            }
            else {}
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
    
   /**
     * Parses the parameters and calls the web service operation
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void parseModifyFieldDef() throws SOAPException
    {
        String sType = request.getParameter("type");
        
        FieldDef fdOld = FieldDef.createFieldDef(sType);
        fdOld.setName(request.getParameter("oldName"));
                                        
        FieldDef fdNew = FieldDef.createFieldDef(sType);
        fdNew.setName(request.getParameter("modifyFieldName"));
        
        String sProperties = "";
        
        if(sType.equals("select"))
        {
            sProperties = request.getParameter("hiddenOptions");
        }
        else
        {
            Vector vProperties = fdNew.getVProperties();
            Property pAux;
        
            for (int i=0; i<vProperties.size();i++)
            {
                pAux = (Property) vProperties.get(i);
            
                sProperties = sProperties + request.getParameter(pAux.getName())+"|";
            }
        }
        
        fdNew.setProperties(sProperties);
                         
        modifyFieldDef(fdOld,fdNew);
    
    }
    
    /**
     * Calls the web service operation <I>modifyFieldDef(User,Collection,FieldDefOld,FieldDefNew) : void</I></li>
     * @param fdOld the FieldDef with the old name and old properties
     * @param fdNew the FieldDef with the new name and new properties
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void modifyFieldDef(FieldDef fdOld, FieldDef fdNew) throws SOAPException
    {
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"modifyFieldDef");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest,new Collection(collection));
            
            Name nFields = sf.createName("fieldsOld");
            SOAPElement seRequest = sbeRequest.addChildElement(nFields);
            
            seRequest.addChildElement(fdOld.toXml());
            
            nFields = sf.createName("fieldsNew");
            seRequest = sbeRequest.addChildElement(nFields);
            seRequest.addChildElement(fdNew.toXml());
        
            smRequest.saveChanges();
            
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
        
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"modify");
            }
            else
            {
                
            }
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
    
    /**
     * Calls the web service operation <I>modifyCollection(User,oldName,Collection) : void</I></li>
     * @param sNewName the String new name of the collection
     * @param sDescription the String new description of the collection
     * @param sTags the String new tags of the collection
     * @throws javax.xml.soap.SOAPException javax.xml.soap.SOAPException when a SOAPException error occurs
     * @throws java.io.IOException when an IOException error occurs
     */
    private void modifyCollection(String sNewName, String sDescription, String sTags) throws SOAPException,IOException
    {
    
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"modifyCollection");
        
            addParam(sbeRequest,u);
            
            SOAPElement seOldName = sf.createElement("oldName");
            seOldName.addTextNode(collection);
            sbeRequest.addChildElement(seOldName);
            
            Collection cRequest = new Collection(sNewName);
            
            String sIsPublic =request.getParameter("isPublic");
            boolean newPrivacity = false;
            if (sIsPublic!=null)
            {
                if (sIsPublic.equals("true")) newPrivacity = true;
            }
            
            if (newPrivacity) cRequest.setIsPublic(1);
            else cRequest.setIsPublic(0);
            
            cRequest.setDescription(sDescription);
            cRequest.setTags(Tags.encode(sTags));
            
            addParam(sbeRequest,cRequest);
            
            smRequest.saveChanges();
            
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
        
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"modifyCollection");
            }
            else
            {
               String sRedirectPage = buildRedirectURL(request,"structure.jsp?collection="+sNewName/*+"&isPublic="+isPublic*/);
               response.sendRedirect(sRedirectPage);
            }
        }
        catch(SOAPException se)
        {
            throw se;
        }
        catch(IOException ioe)
        {
            throw ioe;
        }
    }
    
    /**
     * Parses the parameters and calls the web service operation
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void parseIncreasePosition() throws SOAPException
    {
        FieldDef fd = FieldDef.createFieldDef("text");
        fd.setName(request.getParameter("oldName"));
        fd.setProperties("0|");
        //The only important thing is the name of the Field Def
        
        increasePosition(fd);
    }
    
    /**
     * Calls the web service operation <I>increaseFieldDefPos(User,Collection,FieldDef) : void</I></li>
     * @param fd the FielDef to increase its position
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void increasePosition(FieldDef fd) throws SOAPException
    {
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"increaseFieldDefPos");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest,new Collection(collection));
            addParam(sbeRequest, fd);
            
            smRequest.saveChanges();
        
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.structure"));
            
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"increase");
            }
            else {}
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
}
