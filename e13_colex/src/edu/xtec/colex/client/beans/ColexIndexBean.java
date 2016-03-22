/*
 * File    : ColexIndexBean.java
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
import javax.xml.soap.*;
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import javax.servlet.http.*;
import org.apache.commons.fileupload.*;
import javax.activation.DataHandler;
import org.apache.log4j.Logger;

/**
 * Bean to processes all the requests from the JSP index.jsp, where is
 * implemented the home page of a user
 *
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexIndexBean extends ColexMainBean {

    /**
     * the Vector containing the Collections of the User and the Collections
     * that has been guested (pairs of Collection,Guest)
     */
    protected Vector vCollections;
    /**
     * int number of collections that the user is the owner
     */
    protected int iOwnedCollections = 0;
    /**
     * the ParseMultipart object to parse the HttpServletRequests that have a
     * MultipartContent
     *
     */
    protected ParseMultipart pmRequest;

    static final Logger logger = Logger.getLogger(edu.xtec.colex.client.beans.ColexIndexBean.class);
    /**
     * Method to complete the initialitzation of the bean
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if everything has been initialized, else false
     */
    public boolean initImpl(HttpServletRequest request, HttpServletResponse response) {
        boolean bOK = true;

        if (bOK) {
            pmRequest = new ParseMultipart(request);
            logger.debug("pmRequest: " + pmRequest);
            try {
                String operation = pmRequest.getParameter("operation");
                logger.debug("Operation: " + operation);
                if (operation != null) {
                    if (operation.equals("create")) {
                        logger.debug("initImpl CREATE IN >> ");
                        createCollection(pmRequest.getParameter("newCollection"));
                        logger.debug("initImpl CREATE OUT >> ");
                    } else if (operation.equals("delete")) {
                        logger.debug("initImpl DELETE IN >> ");
                        deleteCollection(pmRequest.getParameter("collection"));
                        logger.debug("initImpl DELETE OUT >> ");
                    } else if (operation.equals("import")) {
                        logger.debug("initImpl IMPORT IN >> ");
                        importCollection(pmRequest.getParameter("importName"), pmRequest.getFileItem("importFile"));
                        logger.debug("initImpl IMPORT OUT >> ");
                    } else if (operation.equals("logout")) {
                        logger.debug("initImpl LOGOUT IN >> ");
                        logout();
                        logger.debug("initImpl LOGOUT OUT >> ");
                    }
                }

                listCollections();
                listGuestCollections();
            } catch (SOAPException se) {
                logger.info("User: " + getUserId() + " Exception: " + se);
                redirectPage = "error.jsp";
                bOK = false;

            } catch (IOException ioe) {
                logger.info("User: " + getUserId() + " Exception: " + ioe);
                redirectPage = "error.jsp";
                bOK = false;

            } catch (Exception e) {
                e.printStackTrace();
                logger.info("User: " + getUserId() + " Exception: " + e);
                redirectPage = "error.jsp";
                bOK = false;
            }
        }

        logger.info("User: " + getUserId() + " Operation: Access to homepage");

        return bOK;
    }

    /**
     * Method called from the jsp, it returs the Collections of the User and the
     * Collections that has been guested (pairs of Collection,Guest)
     *
     * @return a Vector containing the Collections
     */
    public Vector retrieveCollections() {
        return vCollections;
    }

    /**
     * Method called from the jsp, it returs the number of collections that the
     * user is the owner
     *
     * @return the int ownedCollections
     */
    public int retrieveOwnedCollections() {
        return iOwnedCollections;
    }

    /**
     * Method called from the jsp, when in case of Fault get the newCollection
     * parameter else returns string empty ""
     *
     * @return the String newCollection parameter
     */
    public String getNewCollection() {
        String sNewCollection = pmRequest.getParameter("newCollection");
        if ((sNewCollection != null) && (getFault("create") != "")) {
            return sNewCollection;
        } else {
            return "";
        }
    }

    /**
     * Method called from the jsp, when in case of Fault get the importName
     * parameter else returns string empty ""
     *
     * @return the String importName parameter
     */
    public String getImportName() {
        String sImportName = pmRequest.getParameter("importName");
        if ((sImportName != null) && (getFault("import") != "")) {
            return sImportName;
        } else {
            return "";
        }
    }

    /**
     * Calls the web service operation <I>listCollections(User) :
     * [Collection]</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void listCollections() throws SOAPException {
        logger.debug("ListCollections IN << ");
        User uRequest = new User(getUserId());
        logger.debug("uRequest: " + uRequest);

        vCollections = new Vector();
        
        try {

            smRequest = mf.createMessage();
            logger.debug("smRequest: " + smRequest);

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "listCollections");

            addParam(sbeRequest, uRequest);

            smRequest.saveChanges();
            logger.debug("sbeRequest: " + sbeRequest);

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.collection"));
            logger.debug("smResponse: " + smResponse);

            SOAPBody sbResponse = smResponse.getSOAPBody();
            logger.debug("sbResponse: " + sbResponse);

            if (sbResponse.hasFault()) {
                logger.debug("sbResponse.hasFault() IN ");
                checkFault(sbResponse, "list");
                logger.debug("sbResponse.hasFault() OUT");
            } else {
                vCollections = getCollections(smResponse);
                iOwnedCollections = vCollections.size();
            }
            logger.debug("ListCollections OUT >> " + vCollections);
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Calls the web service operation <I>createCollection(User,Collection) :
     * void</I>
     *
     * @param sCollection the String name of the Collection to create
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @throws java.io.IOException when an IOException error occurs
     */
    private void createCollection(String sCollection) throws SOAPException, IOException {
        User uRequest = new User(getUserId());
        Collection cRequest = new Collection(sCollection);

        cRequest.setDescription("");
        cRequest.setTags("");
        cRequest.setIsPublic(0);

        try {

            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "createCollection");

            addParam(sbeRequest, uRequest);
            addParam(sbeRequest, cRequest);

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.collection"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "create");
            } else {
                try {
                    String sRedirectPage = buildRedirectURL(request, "structure.jsp?collection=" + sCollection);
                    response.sendRedirect(sRedirectPage);
                } catch (IOException ioe) {
                    throw ioe;
                }
            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Calls the web service operation <I>deleteCollection(User,Collection) :
     * void</I>
     *
     * @param sCollection the String name of the Collection to delete
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void deleteCollection(String sCollection) throws SOAPException {
        User uRequest = new User(getUserId());

        try {

            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "deleteCollection");

            addParam(sbeRequest, uRequest);
            addParam(sbeRequest, new Collection(sCollection));

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.collection"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "delete");
            } else {
            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Calls the web service operation <I>importCollection(User,Collection,FILE)
     * : void</I>
     *
     * @param importName the String name of the Collection to import
     * @param fiImport the FileItem Zip of the Collection to import
     * @throws java.lang.Exception when an Exception error occurs
     */
    private void importCollection(String importName, FileItem fiImport) throws Exception {
        User uRequest = new User(getUserId());
        Collection cRequest = new Collection(importName);
        File fTemp = null;

        try {
            smRequest = mf.createMessage();

            SOAPBody sbRequest = smRequest.getSOAPBody();

            Name n = sf.createName("importCollection");

            SOAPBodyElement sbeRequest = sbRequest.addBodyElement(n);

            sbeRequest.addChildElement(uRequest.toXml());
            sbeRequest.addChildElement(cRequest.toXml());

            String sNomFitxer = Utils.getFileName(fiImport.getName());

            fTemp = File.createTempFile("attach", null);

            fiImport.write(fTemp);

            URL urlFile = new URL("file://" + fTemp.getPath());

            AttachmentPart ap = smRequest.createAttachmentPart(new DataHandler(urlFile));

            smRequest.addAttachmentPart(ap);

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.collection"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "import");
            } else {

            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (fTemp != null) {
                fTemp.delete();
            }
        }
    }

    /**
     * Calls the web service operation <I>listGuestCollections (User) :
     * [Collection+Guest]</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void listGuestCollections() throws SOAPException {
        logger.debug("listGuestCollections IN <<");
        User uRequest = new User(getUserId());
        Vector vGuestCollections = new Vector();

        try {

            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "listGuestCollections");

            addParam(sbeRequest, uRequest);

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.share"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "listGuestCollections");
            } else {
                vGuestCollections = getListGuestCollections(smResponse);

                if (vGuestCollections != null) {
                    vCollections.addAll(vGuestCollections);
                }
            }
            logger.debug("listGuestCollections OUT >> " + vCollections);
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Returns a Vector containing the guest collections (Collection,Guest)
     * stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getListGuestCollections(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();
        Iterator it2 = null;

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("guestCollections"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("guestCollection"));

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            it2 = se.getChildElements(sf.createName("collection"));

            Collection c = new Collection((SOAPElement) it2.next());
            vRes.add(c);

            it2 = se.getChildElements(sf.createName("guest"));

            Guest g = new Guest((SOAPElement) it2.next());
            vRes.add(g);
        }
        return vRes;
    }

}
