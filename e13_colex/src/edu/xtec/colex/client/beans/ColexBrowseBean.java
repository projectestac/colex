/*
 * File    : ColexBrowseBean.java
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
import javax.xml.soap.*;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import javax.servlet.http.*;

/**
 * Bean to processes all the requests from the JSP browse.jsp, where is
 * implemented the Collection browser
 *
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexBrowseBean extends ColexMainBean {

    /**
     * the BrowseCriteria used to browse the collections
     */
    protected BrowseCriteria bc = new BrowseCriteria();
    /**
     * the Vector containing the browsed collections
     */
    protected Vector vBrowsedCollections = new Vector();
    /**
     * the Vector containing the most used Tags
     */
    protected Vector vTags = new Vector();
    /**
     * int constant for NUM_TAGS, the tags that is going to be shown on the
     * portal.jsp
     */
    protected int NUM_TAGS = 12;
    /**
     * int number of collections found
     */
    protected int numFound = -1;
    /**
     * boolean to control if we want to show all the tags or just NUM_TAGS
     */
    protected boolean isAllTags = false;

    /**
     * Method to initializes the Bean, overrides the method from the MainBean
     * because we do not want to check the user validation
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if everything has been initialized, else false
     */
    public boolean init(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        iniLogger();
        iniSoap();
        iniLanguage();
        logger.debug("iniLogger, iniSOAP, iniLanguage");
        boolean bOK = true;
        String operation = request.getParameter("operation");
        logger.debug("Operation: " + operation);
        try {
            if (operation != null) {
                if (operation.equals("browse")) {
                    logger.debug("Browse IN");
                    browse();
                    logger.debug("Browse OUT");
                } else if (operation.equals("allTags")) {
                    logger.debug("AllTags IN");
                    isAllTags = true;
                    NUM_TAGS = -1;
                }
            }
            listTagClouds(NUM_TAGS);
        } catch (SOAPException se) {
            logger.error("User: " + getUserId() + " Exception: " + se);
            se.printStackTrace();
            bOK = false;
            redirectPage = "error.jsp";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stacktrace = sw.toString();

            logger.error("User: " + getUserId() + " StrackTrace: \n" + stacktrace);
            e.printStackTrace();

            bOK = false;
            redirectPage = "error.jsp";
        }

        return bOK;
    }

    /**
     * Does nothing because everything is in init method and returns true
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true
     */
    public boolean initImpl(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    /**
     * Returns the BrowseCriteria
     *
     * @return the BrowseCriteria
     */
    public BrowseCriteria getBrowseCriteria() {
        return bc;
    }

    /**
     * Returns the numFound collections
     *
     * @return the numFound collections
     */
    public int getNumFound() {
        return numFound;
    }

    /**
     * Returns if isAllTags mode
     *
     * @return if isAllTags mode
     */
    public boolean isAllTags() {
        return isAllTags;
    }

    /**
     * Method called from the jsp, it returs the browsed Collections
     *
     * @return a Vector containing the browsed Collections
     * {Collection0,User0,Collection1,User1....}
     */
    public Vector retrieveBrowse() {
        return vBrowsedCollections;
    }

    /**
     * Method called from the jsp, it returs the TagClouds most used
     *
     * @return a Vector of Tag
     */
    public Vector retrieveTagClouds() {
        return vTags;
    }

    /**
     * Calls the web service operation <I>browse(BrowseCriteria) :
     * [Collection,User]</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void browse() throws SOAPException {
        bc.setBrowseBy(request.getParameter("browseBy"));
        bc.setValue(request.getParameter("value"));
        bc.setOrderBy(request.getParameter("orderBy"));
        bc.setDirection(request.getParameter("direction"));
        bc.setIndexBegin(Integer.parseInt(request.getParameter("indexBegin")));
        bc.setIndexEnd(Integer.parseInt(request.getParameter("indexEnd")));

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "browse");

            addParam(sbeRequest, bc);

            smRequest.saveChanges();
            logger.debug("Browse: " + smRequest.getSOAPBody().toString());
            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.portal"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "browse");
            } else {
                vBrowsedCollections = getBrowsedCollections(smResponse);

                numFound = getIntValue(smResponse, "numFound");

                if (vBrowsedCollections == null) {
                    vBrowsedCollections = new Vector();
                }
            }
        } catch (SOAPException se) {
            logger.debug("browse");
            throw se;
        }
    }

    /**
     * Calls the web service operation <I>listTagClouds(numTags) : [Tag]</I>
     *
     * @param numTags the numTags to browse
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    private void listTagClouds(int numTags) throws SOAPException {
        try {
            smRequest = mf.createMessage();
            logger.debug("smRequest: " + smRequest);
            SOAPBodyElement sbeRequest = setRequestName(smRequest, "listTagClouds");
            logger.debug("sbeRequest: " + sbeRequest);
            Name n = sf.createName("numTags");
            logger.debug("Name: " + n);
            SOAPElement seRequest = sbeRequest.addChildElement(n);
            logger.debug("seRequest: " + seRequest);
            Integer iNumTags = new Integer(numTags);
            logger.debug("iNumTags: " + iNumTags);
            seRequest.addTextNode(iNumTags.toString());
            logger.debug("Before SaveChanges");
            smRequest.saveChanges();
            logger.debug("Changes Saved");
            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.portal"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "browse");
            } else {
                vTags = getTagClouds(smResponse);

                int iMaxTimes = getIntValue(smResponse, "maxTimes");
                int iMinTimes = getIntValue(smResponse, "minTimes");

                int MAX_FONT_SIZE = 150;
                int MIN_FONT_SIZE = 80;

                int times, prop, result;

                if (vTags == null) {
                    vTags = new Vector();
                } else {
                    Tag tag;

                    for (int i = 0; i < vTags.size(); i++) {
                        tag = (Tag) vTags.get(i);
                        times = tag.getTimes();

                        if (iMaxTimes == iMinTimes) {
                            prop = 50;
                        } else {
                            prop = (100 * (times - iMinTimes)) / (iMaxTimes - iMinTimes);
                        }

                        result = MIN_FONT_SIZE + (((MAX_FONT_SIZE - MIN_FONT_SIZE) * prop) / 100);

                        tag.setFontSize(result);
                    }
                }
            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Returns a Vector containing the browsed collections (pairs of
     * Collection,User) stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getBrowsedCollections(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();
        Iterator it2 = null;

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("browseCollections"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("browseCollection"));

        while (it.hasNext()) {
            se = (SOAPElement) it.next();

            it2 = se.getChildElements(sf.createName("collection"));

            Collection c = new Collection((SOAPElement) it2.next());
            vRes.add(c);

            it2 = se.getChildElements(sf.createName("user"));

            Guest g = new Guest((SOAPElement) it2.next());
            vRes.add(g);
        }

        return vRes;
    }

    /**
     * Returns a Vector containing the tagClouds stored into SOAPMessage
     *
     * @param sm the SOAPMessage
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     * @return the Vector
     */
    protected Vector getTagClouds(SOAPMessage sm) throws SOAPException {
        Vector vRes = new Vector();

        SOAPBody sb = sm.getSOAPBody();

        Iterator it = sb.getChildElements();
        Iterator it2 = null;

        SOAPElement se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("tags"));

        se = (SOAPElement) it.next();

        it = se.getChildElements(sf.createName("tag"));

        while (it.hasNext()) {
            Tag t = new Tag((SOAPElement) it.next());
            vRes.add(t);
        }

        return vRes;
    }
}
