/*
 * File    : ColexMainBean.java
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
import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.utils.*;
import org.apache.log4j.*;
import java.io.*;
import javax.servlet.http.*;
import javax.xml.soap.*;
import java.util.*;

/**
 * All the beans of the application used to processes all the requests from the
 * JSP extend from this abstract class
 *
 * @author ogalzorriz
 * @version 1.0
 */
public abstract class ColexMainBean {

    /**
     * String constant with the path to configuration files
     */
    public final static String JSPCONF_PATH = "/edu/xtec/colex/client/beans/";
    /**
     * String constant with the configuration file
     */
    public static String JSPCONF_FILE = "colex_client.properties";
    /**
     * String constant with the configuration logger file
     */
    public final static String JSPCONF_LOG_FILE = "colex_clientLog.properties";

    public static final String ENCODING = "UTF-8";
    protected String redirectPage = "error.jsp";
    protected static Properties pJsp;

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected String sUserId;

    protected static SOAPConnectionFactory scf;
    protected static SOAPConnection con;
    protected static MessageFactory mf;
    protected static SOAPFactory sf;

    protected SOAPMessage smRequest;
    protected SOAPMessage smResponse;

    protected Hashtable faultString;

    protected Locale currentLocale;
    protected static ResourceBundle messages;

    protected static Logger logger;

    static {
        String sEnvironment = System.getProperty("server.environment");
        if (sEnvironment != null) {
            JSPCONF_FILE = "colex_client_" + sEnvironment + ".properties";
        }
    }

    /**
     * Method to initialize the beans
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if the user is validated and everything has been
     * initialized, else false
     */
    public boolean init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        iniLogger();
       // logger.debug("iniLogger");
        iniSoap();
      //  logger.debug("iniSoap");
        iniLanguage();
      //  logger.debug("iniLanguage");

        boolean bOK = true;

        faultString = new Hashtable();

        if (needValidation() && !hasPublicAccess()) {
            if (!isValidated()) {
                StringBuffer sbReqURL = request.getRequestURL();

                String page = sbReqURL.substring(sbReqURL.lastIndexOf("/") + 1);

                String query = request.getQueryString();

                if (query == null) {
                    query = "";
                } else {
                    query = "?" + query;
                }

                String validateURL = getJspProperties().getProperty("url.validate");

                redirectPage = validateURL.substring(0, validateURL.lastIndexOf("/") + 1) + page + query;

                bOK = false;
            }
        }

        if (bOK) {
            bOK = initImpl(request, response);
        }

        return bOK;
    }

    /**
     * Abstract method implemented by each Bean to complete its own
     * initialitzation
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if everything has been initialized, else false
     */
    protected abstract boolean initImpl(HttpServletRequest request, HttpServletResponse response);

    /**
     * Returns if the system needs validation
     *
     * @return true
     */
    public boolean needValidation() {
        return true;
    }

    /**
     * Returns if the user is validated
     *
     * @return true if the user is validated, else false
     */
    public boolean isValidated() {
        return (getUserId() != null && getUserId().length() > 0 && !getUserId().equalsIgnoreCase("null"));
    }

    /**
     * Returns the user String userId stored in a cookie
     *
     * @return the String userId if is validated, else return null
     */
    public String getUserId() {
        if (sUserId == null && request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
            //    logger.debug("Cookie.user_key: " + getJspProperties().getProperty("cookie.user_key"));
                for (int i = 0; i < cookies.length; i++) {
                    Cookie c = cookies[i];
               //     logger.debug("Cookies: " + c.getValue().trim() + " <--> " + i);
              //      logger.debug("Cookies-GETNAME: " + c.getName().trim() + " <--> " + i);
                    if (c.getName().equals(getJspProperties().getProperty("cookie.user_key")) && c.getValue() != null) {
                        sUserId = c.getValue().trim();
               //         logger.debug("sUserId: " + sUserId + " <--> " + i);
                        break;
                    }
                }
            }
        }
        return sUserId;
    }

    /**
     * Returns if the user is a Teacher
     *
     * @return true if the user is a teacher, else false
     */
    public boolean isTeacher() {
        boolean bIsTeacher = false;
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie c = cookies[i];
                    if (c.getName().equals(getJspProperties().getProperty("cookie.portal")) && c.getValue() != null) {
                        bIsTeacher = c.getValue().endsWith("P");
                        break;
                    }
                }
            }
        }
        return bIsTeacher;
    }

    /**
     * Deletes the cookie session and redirects to the portal.jsp
     */
    public void logout() {
        if (request != null) {
            Cookie c = new Cookie(getJspProperties().getProperty("cookie.user_key"), "");
            c.setMaxAge(0);
            c.setPath("/");
            if (getJspProperties().getProperty("cookie.domain") != null) {
                c.setDomain(getJspProperties().getProperty("cookie.domain"));
            }

            response.addCookie(c);
            sUserId = null;
            redirectPage = buildRedirectURL(request, "portal.jsp");
            try {
                response.sendRedirect(redirectPage);
            } catch (IOException e) {
                logger.error("User: " + getUserId() + " Exception: " + e);
            }
        }
    }

    /**
     * Returns the faultString if there has been a fault calling the method
     * sMethod
     *
     * @param sMethod the String name of the method
     * @return the faultString if there has been fault, else en empty string ""
     */
    public String getFault(String sMethod) {
        String sRes = (String) faultString.get(sMethod);

        if (sRes == null) {
            return "";
        } else {
            return sRes;
        }
    }

    /**
     * Returns the page where to redirect the request used when the init method
     * of a Bean returns false
     *
     * @return the String redirect page
     */
    public String getRedirectPage() {
        return redirectPage;
    }

    /**
     * Redirects the request to error.jsp
     */
    protected void redirectToError() {
        try {
            String sRedirectPage = buildRedirectURL(request, "error.jsp");
            response.sendRedirect(sRedirectPage);
        } catch (IOException e) {
            logger.info("User: " + getUserId() + " Exception: " + e);
        }
    }

    /**
     * Initializes the logger
     */
    protected void iniLogger() {

        if (logger == null) {
            Properties pLog = new Properties();
            try {
                InputStream isl = ColexMainBean.class.getResourceAsStream(JSPCONF_PATH + JSPCONF_LOG_FILE);

                if (isl != null) {
                    pLog.load(isl);
                }
                isl.close();

                File f = new File(System.getProperty("user.home"), JSPCONF_LOG_FILE);

                if (f.exists()) {
                    FileInputStream is = new FileInputStream(f);
                    pLog.load(is);
                    is.close();
                }

                PropertyConfigurator.configure(pLog);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            logger = Logger.getRootLogger();
        }
    }

    /**
     * Initializes all the Soap objects needed
     */
    protected void iniSoap() {
        if ((scf == null) || (con == null) || (mf == null) || (sf == null)) {
            try {
             //   logger.debug("iniSOAP IN <<");
                System.setProperty("javax.xml.soap.MessageFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl");
                System.setProperty("javax.xml.soap.SOAPConnectionFactory", "com.sun.xml.messaging.saaj.client.p2p.HttpSOAPConnectionFactory");
                System.setProperty("javax.xml.soap.SOAPFactory", "com.sun.xml.messaging.saaj.soap.ver1_1.SOAPFactory1_1Impl");

             //   logger.debug("Property SET");
                scf = SOAPConnectionFactory.newInstance();
            //    logger.debug("SoapConnectioNFactory New Instance: " + scf);
                con = scf.createConnection();
            //    logger.debug("SCF Connected");

                mf = MessageFactory.newInstance();
            //    logger.debug("MessageFactory INSTANCE");
                sf = SOAPFactory.newInstance();
            //    logger.debug("SOAPFactory INSTANCE");
            //    logger.debug("iniSOAP OUT <<");
            } catch (Exception e) {
                logger.info("User: " + getUserId() + " Exception: " + e);
            }
        }
    }

    /**
     * Sets the name of the web service operation to call
     *
     * @param smRequest the SOAPMessage to make the call
     * @param sName the Name of the web service operation to call
     * @return the SOAPBodyElement with the Name of the web service operation
     * added
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected SOAPBodyElement setRequestName(SOAPMessage smRequest, String sName) throws SOAPException {

        SOAPBody sbRequest = smRequest.getSOAPBody();

        Name n = sf.createName(sName);

        SOAPBodyElement sbeRequest = sbRequest.addBodyElement(n);

        return sbeRequest;
    }

    /**
     * Adds an ObjectColex to a SOAPBodyElement
     *
     * @param sbe the SOAPBodyElement
     * @param ocolex the ObjectColex
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void addParam(SOAPBodyElement sbe, ObjectColex ocolex) throws SOAPException {
        sbe.addChildElement(ocolex.toXml());
    }

    /**
     * Adds a Vector containing ObjectColex elements to a SOAPBodyElement
     *
     * @param sbe the SOAPBodyElement
     * @param vOcolex the Vector
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void addParams(SOAPBodyElement sbe, Vector vOcolex) throws SOAPException {
        ObjectColex ocolex;

        for (int i = 0; i < vOcolex.size(); i++) {

            ocolex = (ObjectColex) vOcolex.get(i);

            sbe.addChildElement(ocolex.toXml());
        }
    }

    /**
     * Sends a SOAPMessage to the given String Url
     *
     * @param smRequest the SOAPMessage
     * @param sURL the String Url
     * @return the SOAPMessage response from the web service operation
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected SOAPMessage sendMessage(SOAPMessage smRequest, String sURL) throws SOAPException {

      //  logger.debug("sURL - sendMessage_ColexMainBean.java: " + sURL);
        
        SOAPMessage ReqMsg = smRequest;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ReqMsg.writeTo(out);
            String strMsg = new String(out.toByteArray());
          //  logger.debug("SoapMessage String 1: " + strMsg);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ColexMainBean.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        SOAPMessage ret = con.call(smRequest, sURL);

        SOAPMessage msg = ret;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            msg.writeTo(out);
            String strMsg = new String(out.toByteArray());
         //   logger.debug("SoapMessage String 2: " + strMsg);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ColexMainBean.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
     //   logger.debug("SOAPMessage sendMessage EXIT");
        return ret;

    }

    /**
     * Method that checks if there is a Fault into a SOAPMessage response, in
     * case of 'Server' fault redirects to error
     *
     * @param sbResponse the SOAPBodyElement where to check if there is a Fault
     * @param sMethod the name of the web service operation called
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void checkFault(SOAPBody sbResponse, String sMethod) throws SOAPException {
        SOAPFault sf = sbResponse.getFault();

        Name n = sf.getFaultCodeAsName();
        String sName = n.getLocalName();

        if (sName.equals("Client")) {
            faultString.put(sMethod, sf.getFaultString());
        } else if (sName.equals("Server")) {
            redirectToError();
        }
    }

    /**
     * Returns the collection stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Collection
     */
    protected Collection getCollection(SOAPMessage sm) throws SOAPException {
        Collection cRes;

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();
        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("collection"));

        se = (SOAPElement) it.next();

        cRes = new Collection(se);

        return cRes;
    }

    /**
     * Returns a Vector containing the Records stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getRecords(SOAPMessage sm) throws SOAPException {
        Record rRes;
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();
        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("records"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("record"));

        int i = 0;

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            rRes = new Record(se);

            vRes.add(rRes);
            i++;
        }

        return vRes;
    }

    /**
     * Returns an int value of a parameter stored into a SOAPMessage
     *
     * @param sm the SOAPMessage
     * @param parameter the String name of the parameter
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the int value of the parameter
     */
    protected int getIntValue(SOAPMessage sm, String parameter) throws SOAPException {
        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();
        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName(parameter));

        se = (SOAPElement) it.next();

        return Integer.parseInt(se.getValue());
    }

    /**
     * Returns a Vector containing the Collections stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getCollections(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("collections"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("collection"));

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            Collection c = new Collection(se);

            vRes.add(c);

        }

        return vRes;
    }

    /**
     * Returns a Vector containing the FieldDefs stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getFieldDefs(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("fields"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("fieldDef"));

        int i = 0;

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            FieldDef fd = FieldDef.createFieldDef(se);

            fd.setId(i);
            vRes.add(fd);
            i++;
        }

        return vRes;
    }

    /**
     * Returns a Vector containing the Guests stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getGuests(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("guests"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("guest"));

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            Guest g = new Guest(se);
            vRes.add(g);
        }

        return vRes;
    }

    /**
     * Returns a Vector containing the LogOperations stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getLogs(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("logs"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("logOperation"));

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            LogOperation lo = new LogOperation(se);

            vRes.add(lo);
        }

        return vRes;
    }

    /**
     * Initializes the language getting the locale from the HttpServletRequest
     */
    protected void iniLanguage() {
        boolean updateLang = false;
        if (request.getParameter("lang") != null) {
            currentLocale = new Locale(request.getParameter("lang"));
            updateLang = true;
        }
        if (currentLocale == null) {
            currentLocale = new Locale("ca", "es");
        }

        if (updateLang || messages == null) {
            messages = ResourceBundle.getBundle("edu.xtec.colex.client.beans.Messages", currentLocale);
        }
    }

    /**
     * Returns the String Message for the String Key stored into
     * Messages.properties files, it is a language function
     *
     * @param sKey the String Key
     * @return the Message
     */
    public String getMessage(String sKey) {
        if (sKey.startsWith("NO_FIELD_CHECK_PTIES")) {
            String sFieldName = sKey.substring(sKey.indexOf("*") + 1);
            String sRes = messages.getString("NO_FIELD_CHECK_PTIES");

            return sRes.replaceAll("01", sFieldName);
        }

        return messages.getString(sKey);
    }

    /**
     * Returns the JSP configuration properties
     *
     * @return the JSP configuration properties
     */
    public Properties getJspProperties() {
        if (pJsp == null) {
            pJsp = new Properties();
            try {
                InputStream isl = ColexMainBean.class.getResourceAsStream(JSPCONF_PATH + JSPCONF_FILE);

                if (isl != null) {
                    pJsp.load(isl);
                }
                isl.close();

                File f = new File(System.getProperty("user.home"), JSPCONF_FILE);
                if (f.exists()) {
                    FileInputStream is = new FileInputStream(f);
                    pJsp.load(is);
                    is.close();
                }
            } catch (FileNotFoundException fnfe) {
                logger.error("User: " + getUserId() + " Exception: " + fnfe);
            } catch (IOException e) {
                logger.error("User: " + getUserId() + " Exception: " + e);
            }
        }

        return pJsp;
    }

    /**
     * Returs the String to the Url where the attached files are accesible
     *
     * @return the String to the files Url
     */
    public String getFilesURL() {
        return getJspProperties().getProperty("url.files");
    }

    /**
     * Returns a String url link to the collection of a user
     *
     * @param idUser the User owner of the collection
     * @param nameCollection the name of the collection
     * @return the url link to the collection
     */
    public String getLinkCollection(String idUser, String nameCollection) {
        String sBasePath = null;
        StringBuffer sbRequestURL = request.getRequestURL();

        if (sbRequestURL != null) {

            int iLast = sbRequestURL.lastIndexOf("/") + 1;
            sBasePath = sbRequestURL.substring(0, iLast);

            String sBP = this.getJspProperties().getProperty("url.server.base.path");

            if (sBP != null) {
                sBasePath = sBP;
            }

        }

        String sUserEnconded = "";
        String sCollectionEncoded = "";

        try {
            sUserEnconded = java.net.URLEncoder.encode(idUser, "UTF-8");
            sCollectionEncoded = java.net.URLEncoder.encode(nameCollection, "UTF-8");
        } catch (UnsupportedEncodingException Uee) {
            sUserEnconded = idUser;
            sCollectionEncoded = nameCollection;
        }

        return sBasePath + "record.jsp?invite=owner=" + sUserEnconded + "$$collection=" + sCollectionEncoded;

    }

    /**
     * Builds a String url from a request and a jsp page
     *
     * @param request the HttpServletRequest
     * @param sRedirectPage the String jsp where to redirect
     * @return the String url builded
     */
    protected String buildRedirectURL(HttpServletRequest request, String sRedirectPage) {

        String sBasePath = null;
        StringBuffer sbRequestURL = request.getRequestURL();

        if (sbRequestURL != null) {
            int iLast = sbRequestURL.lastIndexOf("/") + 1;
            sBasePath = sbRequestURL.substring(0, iLast);
        }

        if (sBasePath != null) {
            String sBP = this.getJspProperties().getProperty("url.server.base.path");

            if (sBP != null) {
                sBasePath = sBP;
            }

            if (sBasePath.indexOf("educacio.intranet") >= 0) {
                sBasePath = sBasePath.replaceAll("educacio.intranet", "edu365.cat");
            }

            if (sRedirectPage.indexOf("://") < 0) {
                sRedirectPage = sBasePath + sRedirectPage;
            }
        }

        return sRedirectPage;
    }

    /**
     * Method used to escape the char ' before showing them into the jsps
     *
     * @param myString the String to escape
     * @return the String with the char ' escaped
     */
    public String escapeChars(String myString) {
        return myString.replaceAll("'", "\\\\'");
    }

    /**
     * Method to permit the acces to the portal.jsp and the public collections
     * on record.jsp and table.jsp when the user is not validated
     *
     * @return false
     */
    public boolean hasPublicAccess() {
        return false;
    }

    /**
     * Auxiliar method useful for debugging. It prints a SOAPMessage into the
     * System.out
     *
     * @param sm the SOAPMessage to print
     */
    protected void printMessage(SOAPMessage sm) {
        System.out.println(Utils.soapMessageToString(sm));
    }
}
