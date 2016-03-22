/*
 * File    : ServletMain.java
 * Created : 21-jun-2005 17:56
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
import org.apache.log4j.*;
import edu.xtec.colex.domain.*;
import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.exception.*;
/**
 * Main Servlet class that processes all the web service operation request and interacts with the database of the application
 * @author ogalzorriz
 * @version 1.0
 */
public abstract class ServletMain extends HttpServlet {
    
    /**
     * Path to server configuration files
     */
    public final static String SERVERCONF_PATH = "/edu/xtec/colex/server/";
    
    /**
     * Server file configuration
     */
    public final static String SERVERCONF_FILE = "colex_ws.properties";
    /**
     * Server configuration properties
     */
    protected static Properties pServer;
    
    /**
     * Server file to cofigure the logger
     */
    public final static String SERVERCONF_LOG_FILE = "colex_wsLog.properties";
    
        
    /**
     * Database of the application
     */
    protected DataBase db;
    
    static
    {
        iniLogger();
        iniSoap();
    }
    
    /**
     * A factory for creating SOAPMessage objects
     */
    protected static MessageFactory mf;
    /**
     * A factory for creating various objects that exists in the SOAP XML
     */
    protected static SOAPFactory sf;
    
    /**
     * Object to log messages
     */
    protected static Logger logger;
    
    /**
     * Initializes the SOAP objects needed
     */
    private User uRequest;
        
    /**
     * Processes the web service operation request
     * @param req the HttpServletRequest to the web service operation
     * @param resp the HttpServletResponse of the web service operation
     * @throws javax.servlet.ServletException when an internal Exception error occurs
     * @throws java.io.IOException when an IO Exception error occurs
     */
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        
      
        
        db = new DataBase();
    
        try 
        {
            // Get all the headers from the HTTP request
            MimeHeaders headers = getHeaders(req);
            
            // Get the body of the HTTP request
            InputStream is = req.getInputStream();
            
            // Now internalize the contents of the HTTP request
            // and create a SOAPMessage
            
            SOAPMessage msg = mf.createMessage(headers, is);
            
            SOAPMessage reply = null;
            
                //reply = processMessage(msg);
            reply = processRequest(msg);
            
            if (reply != null) {
                /*
                 * Need to call saveChanges because we're
                 * going to use the MimeHeaders to set HTTP
                 * response information. These MimeHeaders
                 * are generated as part of the save.
                 */
                if (reply.saveRequired()) {
                    reply.saveChanges();
                }
                
                resp.setStatus(HttpServletResponse.SC_OK);
                putHeaders(reply.getMimeHeaders(), resp);
                
                // Write out the message on the response stream
                
                
                OutputStream os = resp.getOutputStream();
                reply.writeTo(os);
                os.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } 
        
        catch (SOAPException se) 
        {
            throw new ServletException("SAAJ POST failed: " + se.getMessage());
        }
    }
    
    /**
     * Get Mime headers
     * @param req request
     * @return MimeHeaders
     */
    static MimeHeaders getHeaders(HttpServletRequest req) 
    {
        Enumeration headerNames = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();
        
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            
            StringTokenizer values = new StringTokenizer(headerValue, ",");
            
            while (values.hasMoreTokens()) {
                headers.addHeader(headerName, values.nextToken().trim());
            }
        }
        
        return headers;
    }
    
    /**
     * Put Mime headers
     * @param headers MimeHeaders
     * @param res response
     */
    static void putHeaders(MimeHeaders headers, HttpServletResponse res) 
    {
        Iterator it = headers.getAllHeaders();
        
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader) it.next();
            
            String[] values = headers.getHeader(header.getName());
            
            if (values.length == 1) {
                res.setHeader(header.getName(), header.getValue());
            } else {
                StringBuffer concat = new StringBuffer();
                int i = 0;
                
                while (i < values.length) {
                    if (i != 0) {
                        concat.append(',');
                    }
                    
                    concat.append(values[i++]);
                }
                
                res.setHeader(header.getName(), concat.toString());
            }
        }
    }
    
    /**
     * Check the user before processing the SOAP Message
     * @param smRequest the SOAPMessage request of the web service operation
     * @return the SOAPMessage response of the web service operation
     */
    public SOAPMessage processRequest(SOAPMessage smRequest)
    {
        SOAPMessage smResponse = null;
        
        try
        {
            smRequest.getSOAPBody();
        
            SOAPBody sbRequest = smRequest.getSOAPBody();
            SOAPBodyElement sbeRequest=null;
        
            Iterator it = sbRequest.getChildElements();
        
            if (it.hasNext()) sbeRequest = (SOAPBodyElement) it.next();
        
            uRequest = (User) getParam(sbeRequest,"user");
            
            
            if (validateUser(uRequest)) smResponse = processMessage(smRequest);
            else smResponse = createFault(new DataBaseException(DataBaseException.NO_VALID_USER));
        }
        catch (SOAPException se)
        {
            return internalError(se);
        }
        catch (ServerException sve)
        {
            return internalError(sve);
        }
        
        return smResponse;
    }
    
    /**
     * Processes the SOAPMessage
     * @param smRequest the SOAPMessage request of the web service operation
     * @return the SOAPMessage response of the web service operation
     */
    public abstract SOAPMessage processMessage(SOAPMessage smRequest);
    
    /**
     * Checks if the User is valid
     * @param uRequest the User that requests the web service operation
     * @return true if the User is valid
     */
    public boolean validateUser(User uRequest)
    {
        return true;
    }
    
    /**
     * Searches and returns a parameter inside a SOAPBodyRequest
     * @param sbeRequest the SOAPBodyRequest where to search
     * @param sParam the name of the parameter to search
     * @return the ObjectColex found
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public ObjectColex getParam(SOAPBodyElement sbeRequest,String sParam) throws ServerException
    {
        ObjectColex ocolexRes=null;
        
        SOAPElement seParam=null;
        Iterator iAux=null;
        
        try 
        {
            iAux=sbeRequest.getChildElements(sf.createName(sParam));
            if (iAux.hasNext()) 
            {
                seParam = (SOAPElement) iAux.next();
                if (sParam.equals("user")) 
                {
                    ocolexRes= new User(seParam);
                } 
                else if (sParam.equals("collection")) 
                {
                    ocolexRes= new Collection(seParam);
                }
                else if (sParam.equals("fieldDef"))
                {
                    ocolexRes = FieldDef.createFieldDef(seParam);
                }
                else if (sParam.equals("record"))
                {
                    ocolexRes = new Record(seParam);
                }
                else if (sParam.equals("query"))
                {
                    ocolexRes = new Query(seParam);
                }
                else if (sParam.equals("owner"))
                {
                    ocolexRes = new Owner(seParam);
                }
                else if (sParam.equals("guest"))
                {
                    ocolexRes = new Guest(seParam);
                }
                else if (sParam.equals("browseCriteria"))
                {
                    ocolexRes = new BrowseCriteria(seParam);
                }
            }
        } 
        catch (SOAPException se)
        {
            if (sParam.equals("owner")) return null;
            //Owner is an optional parameter 
            //(listCollection,openCollection,searchCollection,export,getStructure)
            else throw new ServerException(se);
        }
        
        return ocolexRes;
    }
    
    
    /**
     * Searches and returns the Fields Definitions inside a SOAPBodyRequest
     * @param sbeRequest the SOAPBodyRequest where to search
     * @param sName the Name of the Fields Definitions, it can be "fieldsOld" or "fieldsNew"
     * @return the Vector with the Fields Definitions found
     * @throws edu.xtec.colex.exception.DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws edu.xtec.colex.exception.ServerException when an internal Exception error occurs
     */
    public Vector getFieldsDefs(SOAPBodyElement sbeRequest, String sName) throws DataBaseException,ServerException
    {
        Vector vRes = new Vector();
        Iterator iAux=null;
        FieldDef fdAux;
        SOAPElement seAux;
        
        try
        {
            iAux = sbeRequest.getChildElements(sf.createName(sName));
            
            seAux = (SOAPElement) iAux.next();
            
            iAux = seAux.getChildElements(sf.createName("fieldDef"));
            
            while (iAux.hasNext())
            {
                seAux = (SOAPElement) iAux.next();
                fdAux = FieldDef.createFieldDef(seAux);
                
                for (int i=0;i<vRes.size();i++)
                {
                    if ( fdAux.getName().equals(((FieldDef)vRes.get(i)).getName()) )
                        throw new DataBaseException(DataBaseException.REPEATED_FIELD_NAME);
                }
                
                vRes.add(fdAux);
            }
        }
        catch (SOAPException se)
        {
            throw new ServerException(se);
        }
                        
        return vRes;
    }
    
    
    /**
     * Creates a String response in a SOAPMessage
     * @param sResponse the name of the web service operation response
     * @param sMessage the text message response
     * @return the SOAPMessage response
     */
    public SOAPMessage createResponse(String sResponse,String sMessage) 
    {
        SOAPMessage smResponse=null;
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
            Name n = sf.createName(sResponse);
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
        
            sbeResponse.addTextNode(sMessage);  
        }
        catch(SOAPException se)
        {
            return internalError(se);
        }
        
        return smResponse;
    }
    
    /**
     * Creates a ObjectColex response in a SOAPMessage
     * @param sResponse the name of the web service operation response
     * @param ocolexRes the ObjectColex response
     * @return the SOAPMessage response
     */
    public SOAPMessage createResponse(String sResponse,ObjectColex ocolexRes)
    {
        SOAPMessage smResponse=null;
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
            Name n = sf.createName(sResponse);
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
        
            sbeResponse.addChildElement(ocolexRes.toXml());
        }
        catch(SOAPException se)
        {
            return internalError(se);
        }
        return smResponse;
    }    
    
    /**
     * Creates a ObjectColex Vector response in a SOAPMessage
     * @param sResponse the name of the web service operation response
     * @param vParams the ObjectColex Vector response
     * @param sParam the name of the param to set in the SOAPMessage response
     * @return the SOAPMessage response
     */
    public SOAPMessage createResponse(String sResponse,Vector vParams,String sParam)
    {
        SOAPMessage smResponse=null;
        try
        {
            smResponse = mf.createMessage();
            SOAPBody sbResponse = smResponse.getSOAPBody();
        
        
            Name n = sf.createName(sResponse);
        
            SOAPBodyElement sbeResponse = sbResponse.addBodyElement(n);
            SOAPElement seResponse;
            
            int i=0;
            
            if (sResponse.indexOf("respSearch")!=-1)
            {
                n = sf.createName("numRecords");
                seResponse = sbeResponse.addChildElement(n);
                Integer iCount = (Integer)vParams.get(i);
                seResponse.addTextNode(iCount.toString());            
                i++;
                
                n = sf.createName("numFound");
                seResponse = sbeResponse.addChildElement(n);
                iCount = (Integer)vParams.get(i);
                seResponse.addTextNode(iCount.toString());            
                i++;
            }
            
            n = sf.createName(sParam);
        
            seResponse = sbeResponse.addChildElement(n);
        
            ObjectColex ocolexAux;
            
            
            while(i< vParams.size()) 
            {
                ocolexAux = (ObjectColex) vParams.get(i);
                seResponse.addChildElement(ocolexAux.toXml());
                i++;
            }
        }
        catch(Exception se)
        {
            se.printStackTrace();
            return internalError(se);
        }
            
        return smResponse;
    }
    
    /**
     * Creates a SOAPMessage fault with the given exception called when a DataBaseException is thrown
     * @param e the Exception to put in the message
     * @return the SOAPMessage Fault response
     */
    public SOAPMessage createFault(Exception e)
    {
        SOAPMessage smRes=null;
        try
        {
            smRes = mf.createMessage();
            SOAPBody sbResponse = smRes.getSOAPBody();
            SOAPFault sfResponse = sbResponse.addFault();
            
            Name nFault = sf.createName("Client","",SOAPConstants.URI_NS_SOAP_ENVELOPE);
            sfResponse.setFaultCode(nFault);
            sfResponse.setFaultString(e.getMessage());
            
            String userId="";
            
            if (uRequest!=null) userId=uRequest.getUserId();
            
            logger.info("User: "+userId+" Exception: "+e);
        }
        catch (SOAPException se)
        {
            return internalError(se);
        }
        return smRes;
    }
    
    /**
     * Creates a SOAPMessage fault with the given exception called when a ServerException is thrown
     * @param e the Exception to put in the message
     * @return the SOAPMessage Fault response
     */
    public SOAPMessage internalError(Exception e)
    {
        SOAPMessage smRes=null;
        
        try
        {
            smRes = mf.createMessage();
            SOAPBody sbResponse = smRes.getSOAPBody();
            SOAPFault sfResponse = sbResponse.addFault();
            
            Name nFault = sf.createName("Server","",SOAPConstants.URI_NS_SOAP_ENVELOPE);
            sfResponse.setFaultCode(nFault);
            sfResponse.setFaultString("Server Error "+e);
            
            String userId="";
            
            if (uRequest!=null) userId=uRequest.getUserId();
            
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();
            
            logger.error("User: "+userId+" StrackTrace: \n"+stacktrace);
        }
        catch (SOAPException se)
        {
           logger.error("¡¡¡PANIC!!!"+e);
        }
        return smRes;
    }
    
    /**
     * Returns the Server configuration properties
     * @return the Server configuration properties
     */
    public static Properties getServerProperties()
    {
        if (pServer==null)
        {
            pServer = new Properties();
            try{
                InputStream isl = ServletMain.class.getResourceAsStream(SERVERCONF_PATH+SERVERCONF_FILE);
                
                if (isl!=null){
                    pServer.load(isl);
                }
                isl.close();
                
                File f = new File(System.getProperty("user.home"), SERVERCONF_FILE);
                if(f.exists()){
                    FileInputStream is=new FileInputStream(f);
                    pServer.load(is);
                    is.close();
                }
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pServer;
    }
      
    /**
     * Initializes the Logger
     */
    private static void iniLogger()
    {
        
        if (logger==null)
        {
            Properties pLog = new Properties();
            try
            {
                InputStream isl = ServletMain.class.getResourceAsStream(SERVERCONF_PATH+SERVERCONF_LOG_FILE);
            
                if (isl!=null)
                {   
                    pLog.load(isl);
                }
                isl.close();
            
                File f = new File(System.getProperty("user.home"), SERVERCONF_LOG_FILE);

                if(f.exists())
                {
                    FileInputStream is=new FileInputStream(f);
                    pLog.load(is);
                    is.close();
                }
            
                PropertyConfigurator.configure(pLog);            
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        
            logger = Logger.getRootLogger();

        }        
    }
    
    /**
     * Initializes the SOAP objects needed
     */
    private static void iniSoap()
    {
        if ((mf==null) || (sf==null))
        {
            try
            {
            	//System.setProperty("javax.xml.soap.MessageFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl");
            	//System.setProperty("javax.xml.soap.MessageFactory", "com.sun.xml.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl"); 
                mf = MessageFactory.newInstance();
                sf = SOAPFactory.newInstance();
            }
   
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
