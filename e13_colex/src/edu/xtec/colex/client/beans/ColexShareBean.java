/*
 * File    : ColexShareBean.java
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
import cat.xtec.ws.proxies.correu.utils.ServiceLocator;
import cat.xtec.ws.proxies.correu.CorreuSender;
import cat.xtec.ws.proxies.correu.types.CorreuInfo;
import cat.xtec.ws.proxies.correu.types.EnviamentResponse;
import cat.xtec.ws.proxies.correu.types.CorreuBody;
import cat.xtec.ws.proxies.correu.types.CorreuResponse;
import cat.xtec.ws.proxies.correu.types.CorreuException;
import javax.xml.soap.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Bean to processes all the requests from the JSP share.jsp, where is implemented the page for sharing a collection
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexShareBean extends ColexMainBean 
{
    /**
     * String name of the current collection
     */
    protected String collection;
    /**
     * Boolean to set if the collection is public
     */
    protected boolean isPublic=false;
    /**
     * the Vector containing all the Guests of the collection
     */
    protected Vector vGuests = new Vector();
    /**
     * the Vector containing all the LogOperation of the collection
     */
    protected Vector vLog = new Vector();
    /**
     * String id owner of the Collection
     */
    protected String owner;
 
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
                owner = request.getParameter("owner");
                String operation = request.getParameter("operation");
                
                try
                {
                    if (operation!=null)
                    {
                        if (operation.equals("add"))
                        {
                            addGuest();
                        }
                        else if (operation.equals("delete"))
                        {
                            deleteGuest();
                        }
                        else if (operation.equals("modifyPermission"))
                        {
                            modifyPermission();
                        }
                        else if (operation.equals("sendMail"))
                        {
                            sendMail();
                        }
                    }
                    getCollectionInfo();
                    listGuests();
                    getLog();
                }
                catch (SOAPException se)
                {
                    logger.error("User: "+getUserId()+" Exception: "+se);
                    bOK= false;
                    redirectPage="error.jsp";
                }
                catch (Exception e)
                {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String stacktrace = sw.toString();
            
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
     * Method called from the jsp, it returns if Collection is public
     * @return true if the Collection is public, else false
     */
    public boolean isPublic()
    {
        return isPublic;
    }
    
    /**
     * Method called from the jsp, it returns the Owner of the Collection
     * @return the String idUser owner of the Collection
     */
    public String getOwner()
    {
        return owner;
    }
    
    /**
     * Method called from the jsp, it returs the Guests of the Collection
     * @return a Vector containing the Guests
     */
    public Vector retrieveGuests()
    {
        return vGuests;
    }
    
    /**
     * Method called from the jsp, it returs the LogOperations of the Collection
     * @return a Vector containing the LogOperations
     */
    public Vector retrieveLog()
    {
        return vLog;
    }
    
    /**
     * Method called from the jsp, it returs the From to send the mail
     * @return a Vector containing the LogOperations
     */
    public String retrieveMailFrom()
    {
        if (isTeacher()) return getUserId()+"@"+getJspProperties().getProperty("mail.domain.teacher");
        else return getUserId()+"@"+getJspProperties().getProperty("mail.domain.student");
    }
    
    /**
     * Method called from the jsp, it returs the Subject to send the mail
     * @return a Vector containing the LogOperations
     */
    public String retrieveMailSubject()
    {
        return getMessage("mail.subject");
    }
    
    /**
     * Method called from the jsp, it returs the Text to send the mail
     * @return a Vector containing the LogOperations
     */
    public String retrieveMailText()
    {
        String sRes="";
        
        if (vGuests.size()==0) sRes = getMessage("mail.text.singular");
        else sRes = getMessage("mail.text.plural");
        
        return sRes.replaceAll("01",collection);
    }
    
    /**
     * Method called from the jsp, it returs the Url linkt to the Collection added in the Text to send the mail
     * @return a Vector containing the LogOperations
     */
    public String retrieveMailLink()
    {
        String sUserId = getUserId();
        if (owner!=null) sUserId = owner;
        
        return getLinkCollection(sUserId,collection);
    }
       
    /**
     * Calls the web service operation <I>listGuests (User,Owner,Collection) : [Guest]</I>
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void listGuests() throws SOAPException
    {
        User u = new User(getUserId());
        
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"listGuests");
        
            addParam(sbeRequest,u);
            if (owner != null)
            {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest,oRequest);
            }
            addParam(sbeRequest,new Collection(collection));
        
            smRequest.saveChanges();
            
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.share"));
    
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"listGuests");
            }
            else
            {
                vGuests = getGuests(smResponse);
            }
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
    
    /**
     * Calls the web service operation <I>getLog (User,Owner,Collection) : [LogOperation]</I>
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void getLog() throws SOAPException
    {
        User u = new User(getUserId());
        
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"getLog");
        
            addParam(sbeRequest,u);
            if (owner != null)
            {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest,oRequest);
            }
            addParam(sbeRequest,new Collection(collection));
        
            smRequest.saveChanges();
            
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.share"));
    
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"getLog");
            }
            else
            {
                vLog = getLogs(smResponse);
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
    protected void getCollectionInfo() throws SOAPException
    {
        User u = new User(getUserId());
                
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"getCollectionInfo");
        
            addParam(sbeRequest,u);
            if (owner != null)
            {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest,oRequest);
            }
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
     * Calls the web service operation <I>addGuest (User,Collection,Guest) : void</I>
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void addGuest() throws SOAPException
    {
        User u = new User(getUserId());

        String sGuest = request.getParameter("guest");
        Guest g= new Guest(sGuest);
        
        g.setPermission(Integer.parseInt(request.getParameter("permission")));
        
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"addGuest");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest, new Collection(collection));
            addParam(sbeRequest,g);
        
            smRequest.saveChanges();
        
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.share"));
        
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
     * Calls the web service operation <I>deleteGuest (User,Collection,Guest) : void</I>
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void deleteGuest() throws SOAPException
    {
        User u = new User(getUserId());

        String sGuest = request.getParameter("guestHidden");
        Guest g= new Guest(sGuest);
       
        try
        {
    
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"deleteGuest");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest, new Collection(collection));
            addParam(sbeRequest,g);
        
            smRequest.saveChanges();
        
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.share"));
        
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"delete");
            }
        }
        catch(SOAPException se)
        {
            throw se;
        }
    }
    
    /**
     * Calls the web service operation <I>modifyPermission (User,Collection,Guest) : void</I>
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void modifyPermission() throws SOAPException
    {
        User u = new User(getUserId());

        String sGuest = request.getParameter("guestHidden");
        Guest g= new Guest(sGuest);
        g.setPermission(Integer.parseInt(request.getParameter("permissionHidden")));
            
        try
        {
            smRequest = mf.createMessage();
        
            SOAPBodyElement sbeRequest = setRequestName(smRequest,"modifyPermission");
        
            addParam(sbeRequest,u);
            addParam(sbeRequest, new Collection(collection));
            addParam(sbeRequest,g);
            
            smRequest.saveChanges();
        
            SOAPMessage smResponse = sendMessage(smRequest,this.getJspProperties().getProperty("url.servlet.share"));
        
            SOAPBody sbResponse = smResponse.getSOAPBody();
                        
            if (sbResponse.hasFault())
            {
                checkFault(sbResponse,"modifyPermission");
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
     * Sends the email inviting to visit the collection
     */
    protected void sendMail()
    {
        try
        {
// XTEC ********** MODIFICAT -> Change send mail method to use the web services for send mails developed by the Departament d'Ensenyament de la Generalitat de Catalunya
// 2011.05.10 @mmartinez        	
        	//get values from properties file
        	String idEnvironment = getJspProperties().getProperty("mail.ws.environment");
        	String idApp = getJspProperties().getProperty("mail.ws.app");
            String idSender = getJspProperties().getProperty("mail.ws.sender");
        	
            //check the gotten values
            // XTEC NADIM ********* Modificat --> Change to send mail... configuration modified for the transformation of CPD. added ENTORN_PRE
        	//if(!ServiceLocator.ENTORN_INT.equals(idEnvironment) && !ServiceLocator.ENTORN_ACC.equals(idEnvironment) && !ServiceLocator.ENTORN_PROD.equals(idEnvironment)) 
            if(!ServiceLocator.ENTORN_INT.equals(idEnvironment) && !ServiceLocator.ENTORN_ACC.equals(idEnvironment) && !ServiceLocator.ENTORN_PROD.equals(idEnvironment) && !ServiceLocator.ENTORN_PRE.equals(idEnvironment)) 
            {
        	  logger.error("User: "+getUserId()+" Exception: " + idEnvironment + " value for parameter 'entorn' is incorrect. It must be: " + ServiceLocator.ENTORN_INT + ", " + ServiceLocator.ENTORN_PRE + " o " + ServiceLocator.ENTORN_PROD);
        	  faultString.put("sendMail","MAIL_ERROR");
              return;
            }           
            if(!CorreuSender.CORREU_GENCAT.equals(idSender) && !CorreuSender.CORREU_XTEC.equals(idSender) && !CorreuSender.CORREU_EDUCACIO.equals(idSender)) 
            {
              logger.error("User: "+getUserId()+" Exception: " + idSender + " value for parameter 'correu origen' is incorrect. It must be: " + CorreuSender.CORREU_EDUCACIO + ", " + CorreuSender.CORREU_XTEC + " o " + CorreuSender.CORREU_GENCAT);
              faultString.put("sendMail","MAIL_ERROR");
              return;
            }
            
            //get values from the form
            //String from    = request.getParameter("mailFrom");
            String to      = request.getParameter("mailTo");
            String from    = request.getParameter("mailFrom");
            String subject = request.getParameter("mailSubject");
            String body    = request.getParameter("mailText")+"\n\n"+request.getParameter("mailLink");
            
            //send message
            CorreuSender sender = new CorreuSender(idApp,idEnvironment);            
            String sAvailability = sender.consultaDisponibilitat(idSender);            
            if("OK".equals(sAvailability))
            {
            	CorreuInfo correu = new CorreuInfo();
                correu.setFrom(idSender);
                correu.getReplyAddresses().add(from);
                correu.addTo(to);
                correu.setSubject(subject);
                correu.setBodyInfo(0,body);
                
                EnviamentResponse sResponse = sender.enviaCorreus(new CorreuInfo[] {correu});
                if(!sResponse.isOk()){
                  if(sResponse.unsendedMessages().size() > 0) 
                  {
                    CorreuResponse response = (CorreuResponse)sResponse.unsendedMessages().get(0);
                    logger.error("User: "+getUserId()+" Exception: Error with the sended message: " + response.getErrorMessage());
                  }
                  else
                     logger.error("User: "+getUserId()+" Exception: General error: " + sResponse.getMessage());
                  
                  faultString.put("sendMail","MAIL_ERROR");
                  return;
                }
            }
            else 
            {
              logger.error("User: "+getUserId()+" Exception: There are problems with the services availability: " + sAvailability);
              faultString.put("sendMail","MAIL_ERROR");
              return;
            }
            
            
//********** ORIGINAL
            /*java.util.Properties props = System.getProperties();
            
            props.put("mail.smtp.user", getJspProperties().getProperty("mail.smtp.user"));
            props.put("mail.smtp.host",getJspProperties().getProperty("mail.smtp.host"));
            props.put("mail.smtp.port", getJspProperties().getProperty("mail.smtp.port"));//port your mail Server
            props.put("mail.smtp.starttls.enable",getJspProperties().getProperty("mail.smtp.starttls.enable"));
            props.put("mail.smtp.auth",getJspProperties().getProperty("mail.smtp.auth"));
            props.put("mail.smtp.socketFactory.port", getJspProperties().getProperty("mail.smtp.socketFactory.port"));
            
            // Get session
            Authenticator auth = new SMTPAuthenticator(getJspProperties().getProperty("mail.smtp.user"),getJspProperties().getProperty("mail.smtp.password"));
            Session mailSession = Session.getInstance(props,auth);

            // Define message
            String from = request.getParameter("mailFrom");
            String to = request.getParameter("mailTo");

            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, 
            new InternetAddress(to));
            message.setSubject(request.getParameter("mailSubject"));
            message.setText(request.getParameter("mailText")+"\n\n"+request.getParameter("mailLink"));

            // Send message
            Transport.send(message);*/
//********** END
            
            faultString.put("sendMail","MAIL_SENDED");
            //We use the fault method to inform about the email
            logger.debug("User: "+getUserId()+" Operation: sendMail");
        }
        catch (Exception e)
        {
            logger.error("User: "+getUserId()+" Exception: "+e);
            //We use the fault method to inform about the email
            faultString.put("sendMail","MAIL_ERROR");
        }
    }
    
    /**
     * Auxiliar class to do the SMTP Authentication
     */
    class SMTPAuthenticator extends javax.mail.Authenticator 
    {
        private String User;
        private String Password;

        public SMTPAuthenticator(String user, String password) {
            User = user;
            Password = password;
        }
        
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(User, Password);
        }
    }
}