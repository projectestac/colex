/*
 * File    : ColexRecordBean.java
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
import org.apache.commons.fileupload.*;
import javax.activation.DataHandler;

import java.util.zip.*;

/**
 * Bean to processes all the requests from the JSP record.jsp, where is
 * implemented the page to show a record
 *
 * @author ogalzorriz
 * @version 1.0
 */
public class ColexRecordBean extends ColexMainBean {

    /**
     * the current Record open on record.jsp
     */
    protected Record record;
    /**
     * the Vector containing all the Records matching the Query q
     */
    protected Vector vRecords = new Vector();
    /**
     * the current Query
     */
    protected Query q;
    /**
     * the Vector containing the FieldDefs of the Collection
     */
    protected Vector vFieldDefs = new Vector();
    /**
     * int begin index where to start the Query
     */
    protected int begin = 0;
    /**
     * int number of records that has the collection
     */
    protected int numRecords = 0;
    /**
     * int number of Records matching the Query
     */
    protected int numFound = 0;
    /**
     * String name of the collection
     */
    protected String collection;
    /**
     * String direction to sort the records
     */
    protected String direction;
    /**
     * String name to field to order by
     */
    protected String orderField;
    /**
     * String description of the Collection
     */
    protected String description;
    /**
     * String tags of the Collection
     */
    protected String tags;
    /**
     * String creation date of the Collection
     */
    protected String created;
    /**
     * String current operation from the HttpServletRequest
     */
    protected String operation;
    /**
     * String id owner of the Collection
     */
    protected String owner;
    /**
     * String of the size how the record is showed, it can be "Normal" or
     * "Extended"
     */
    protected String recordSize;
    /**
     * boolean to indicate if all the records are shown in table.jsp
     */
    protected String isTabMode;
    /**
     * the permission level from the User on the current Collection
     */
    protected int permission = Guest.PERMISSION_NONE;
    /**
     * boolean to check if the initialization is ok
     */
    protected boolean bOK = true;
    /**
     * String user id for a visitor
     */
    protected String sUserVisitor = null;
    /**
     * the ParseMultipart object to parse the HttpServletRequests that have a
     * MultipartContent
     */
    public ParseMultipart pmRequest;

    /**
     * Method to complete the initialitzation of the bean
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true if everything has been initialized, else false
     */
    public boolean initImpl(HttpServletRequest request, HttpServletResponse response) {
        if (bOK) {
            pmRequest = new ParseMultipart(request);

            String invite = pmRequest.getParameter("invite");

            if (invite != null) {
                String invite3 = request.getQueryString();

                owner = invite3.substring(invite3.indexOf("owner=") + 6, invite3.indexOf("$$"));
                collection = invite3.substring(invite3.indexOf("collection=") + 11);

                try {
                    owner = java.net.URLDecoder.decode(owner, ENCODING);
                    collection = java.net.URLDecoder.decode(collection, ENCODING);
                } catch (java.io.UnsupportedEncodingException uee) {
                }

                q = new Query();
                q.setOrderField("null");
                q.setDirection("asc");
                q.setBeginIndex(0);

                recordSize = "Normal";
                isTabMode = "false";
                operation = "showAll";

                try {
                    searchCollection();
                    if (owner != null) {
                        getPermission();
                    } else {
                        permission = edu.xtec.colex.domain.Guest.PERMISSION_TOTAL;
                    }
                    getCollectionInfo();
                } catch (Exception e) {
                    logger.info("User: inviteUser operation: invite Exception: " + e);
                    bOK = false;
                    redirectPage = "error.jsp";
                }
            } else {
                collection = pmRequest.getParameter("collection");

                if (collection == null) {
                    bOK = false;
                    redirectPage = "index.jsp";
                } else {
                    operation = pmRequest.getParameter("operation");
                    logger.debug("Operation: " + operation);
                    try {
                        if (operation != null) {                            
                            String sBegin = pmRequest.getParameter("begin");                            
                            logger.debug("sBegin: " + sBegin);
                            if (sBegin == null || sBegin.equals("undefined")) {
                                sBegin = "0";
                            }                            
                            if (sBegin != null) {
                                begin = Integer.parseInt(sBegin);
                            }
                            logger.debug("Begin INT" + begin);

                            direction = pmRequest.getParameter("direction");
                            if (direction == null) {
                                direction = "asc";
                            }

                            orderField = pmRequest.getParameter("orderField");
                            if (orderField == null) {
                                orderField = "null";
                            }

                            q = new Query();

                            q.setOrderField(orderField);
                            q.setDirection(direction);

                            q.setBeginIndex(begin);

                            owner = pmRequest.getParameter("owner");

                            recordSize = pmRequest.getParameter("recordSize");
                            if (recordSize == null) {
                                recordSize = "Normal";
                            }

                            isTabMode = pmRequest.getParameter("isTabMode");
                            if (isTabMode == null) {
                                isTabMode = "false";
                            }

                            if (operation.equals("showAll")) {
                                logger.debug("before- showAll");
                                calculateConditions();
                                logger.debug("before- showAll");
                            } else if (operation.equals("search")) {
                                logger.debug("before");
                                calculateConditions();
                                logger.debug("after");
                            } else if (operation.equals("modify")) {
                                logger.debug("before- modify");
                                calculateConditions();
                                parseModifyRecord();
                            } else if (operation.equals("add")) {
                                logger.debug("before- add");
                                parseAddRecord();
                            } else if (operation.equals("delete")) {
                                logger.debug("before- delete");
                                calculateConditions();
                                deleteRecord();
                            } else if (operation.startsWith("export")) {
                                logger.debug("before- export");
                                calculateConditions();
                                q.setBeginIndex(0);
                                exportCollection(response);
                                logger.debug("after- export");
                            } else if (operation.equals("importRecords")) {
                                importRecords(pmRequest.getFileItem("importFile"));
                            }
                            searchCollection();
                            getCollectionInfo();

                            if (operation.equals("modify")) {
                                updateCurrentRecord();
                            }

                            if (owner != null) {
                                getPermission();
                            } else {
                                permission = edu.xtec.colex.domain.Guest.PERMISSION_TOTAL;
                            }
                        }
                    } catch (SOAPException se) {
                        logger.info("User: " + getUserId() + " Exception: " + se);
                        bOK = false;
                        redirectPage = "error.jsp";
                    } catch (Exception e) {
                        logger.info("User: " + getUserId() + " Exception: " + e);
                        bOK = false;
                        redirectPage = "error.jsp";
                    }
                }
            }
        }
        return bOK;
    }

    /**
     * Method that overrides the method from the MainBean because record.jsp can
     * be viewed by visitors users
     *
     * @return true
     */
    public boolean hasPublicAccess() {
        if (getUserId() == null) {
            sUserVisitor = getJspProperties().getProperty("user.visitor");
        };
        return true;
    }

    /**
     * Method called from the jsp, it returs the String name of the current
     * Collection
     *
     * @return the String name
     */
    public String getCollection() {
        return collection;
    }

    /**
     * Method called from the jsp, it returs the Query
     *
     * @return the Query
     */
    public Query getQuery() {
        return q;

    }

    /**
     * Method called from the jsp, it returs the FieldDefs of the Collection
     *
     * @return a Vector containing the FieldDefs
     */
    public Vector retrieveFieldDefs() {
        return vFieldDefs;
    }

    /**
     * Method called from the jsp, it returs the FieldDefs searchable of the
     * collection
     *
     * @return a Vector containing the FieldDefs searchable
     */
    public Vector retrievSearchableDefs() {
        Vector vRes = new Vector();

        for (int i = 0; i < vFieldDefs.size(); i++) {
            FieldDef fdAux = (FieldDef) vFieldDefs.get(i);

            if (fdAux.isSearchable()) {
                vRes.add(fdAux);
            }
        }
        return vRes;
    }

    /**
     * Method called from the jsp, it returs the FieldDefs sortable of the
     * collection
     *
     * @return a Vector containing the FieldDefs sortable
     */
    public Vector retrieveSortableFields() {
        Vector vRes = new Vector();

        for (int i = 0; i < vFieldDefs.size(); i++) {
            FieldDef fdAux = (FieldDef) vFieldDefs.get(i);

            if (fdAux.isSortable()) {
                vRes.add(fdAux);
            }
        }
        return vRes;
    }

    /**
     * Method called from the jsp, it returs the FieldDefs that can have files
     * attached of the collection
     *
     * @return a Vector containing the FieldDefs that can have files attached
     */
    public Vector retrieveAttachFields() {
        Vector vRes = new Vector();

        for (int i = 0; i < vFieldDefs.size(); i++) {
            FieldDef fdAux = (FieldDef) vFieldDefs.get(i);

            if (fdAux.isAttachment()) {
                vRes.add(fdAux);
            }
        }
        return vRes;
    }

    /**
     * Method called from the jsp, it returs the FieldDefs that have info of the
     * collection
     *
     * @return a Vector containing the FieldDefs that can have files attached
     */
    public Vector retrieveInfoFields() {
        Vector vRes = new Vector();

        for (int i = 0; i < vFieldDefs.size(); i++) {
            FieldDef fdAux = (FieldDef) vFieldDefs.get(i);

            if (fdAux.isInfo()) {
                vRes.add(fdAux);
            }
        }
        return vRes;
    }

    /**
     * Method called from the jsp, it returs the number of records of the
     * collection
     *
     * @return int number of records
     */
    public int getNumRecords() {
        return numRecords;
    }

    /**
     * Method called from the jsp, it returs the number of records matching the
     * query
     *
     * @return int number of records matching the query
     */
    public int getNumFound() {
        return numFound;
    }

    /**
     * Method called from the jsp, it returns the Comparators for the fieldType
     *
     * @param fieldType the String with the type of Field
     * @return a Vector containing the Comparators
     */
    public Vector getComparators(String fieldType) {
        FieldDef fd = FieldDef.createFieldDef(fieldType);

        return fd.getComparators();
    }

    /**
     * Method called from the jsp, it returns the Owner of the Collection
     *
     * @return the String idUser owner of the Collection
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Method called from the jsp, it returns the String description of the
     * Collection
     *
     * @return the String description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Method called from the jsp, it returns the String tags of the Collection
     *
     * @return the String tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * Method called from the jsp, it returns the String creation date of the
     * Collection
     *
     * @return the String creation date
     */
    public String getCreated() {
        return created;
    }

    /**
     * Method called from the jsp, it returs the Records of the collection
     *
     * @return a Vector containing the Records
     */
    public Vector retrieveVRecords() {
        return vRecords;
    }

    /**
     * Method called from the jsp, it returs the current Record
     *
     * @return the current Record
     */
    public Record retrieveRecord() {
        return record;
    }

    /**
     * Method called from the jsp, it returs the size desired to show the Record
     * ("Normal" or "Extended")
     *
     * @return the String recordSize
     */
    public String getRecordSize() {
        return recordSize;
    }

    /**
     * Method called from the jsp, it returs if all the records are shown in
     * table.jsp
     *
     * @return the String isTabMode
     */
    public String getIsTabMode() {
        return isTabMode;
    }

    /**
     * Method called from the jsp, it returs the permission level of the User
     * for the Collection
     *
     * @return the int permission
     */
    public int retrievePermission() {
        return permission;
    }

    /**
     * Calls the web service operation <I>searchAll(User,Owner,Collection,Query)
     * : NumRecord,NumFound,[Record]</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void searchCollection() throws SOAPException {
        User u = new User("");

        if (sUserVisitor != null) {
            u.setUserId(sUserVisitor);
        } else {
            u = new User(getUserId());
        }

        Collection c = new Collection("");

        c.setName(collection);

        getStructure();

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "searchAll");

            addParam(sbeRequest, u);

            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }

            addParam(sbeRequest, c);

            if (operation.equals("showAll")) {
                Query qAux = new Query();
                qAux.setBeginIndex(0);
                qAux.setDirection("asc");
                qAux.setOrderField("null");
                addParam(sbeRequest, qAux);
            } else {
                int iBeginIndexAux = q.getBeginIndex();
                q.setBeginIndex(0);
                //we ask for all the records but we keep the begin index
                addParam(sbeRequest, q);

                q.setBeginIndex(iBeginIndexAux);
            }

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.record"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "search");
                String searchFault = getFault("search");

                if (searchFault.equals("EMPTY_COLLECTION")) {
                    numRecords = 0;
                    numFound = 0;
                } else if (searchFault.equals("NO_RECORD_FOUND")) {
                    numFound = 0;
                    String sNumRecords = pmRequest.getParameter("numRecords");
                    if (sNumRecords != null) {
                        numRecords = Integer.parseInt(sNumRecords);
                    }
                }

            } else {
                vRecords = getRecords(smResponse);

                if (q.getBeginIndex() >= vRecords.size()) {
                    q.setBeginIndex(vRecords.size() - 1);
                }

                record = (Record) vRecords.get(q.getBeginIndex());
                numRecords = getIntValue(smResponse, "numRecords");
                numFound = getIntValue(smResponse, "numFound");
            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Returns the FieldDef of a given fieldName
     *
     * @param sFieldName the String fieldName
     * @return the FieldDef
     */
    public FieldDef getFieldDef(String sFieldName) {
        FieldDef fd = FieldDef.createFieldDef("text");
        boolean bFound = false;

        if (vFieldDefs.size() == 0) {
            try {
                getStructure();
            } catch (SOAPException se) {
                logger.info("User: " + getUserId() + " Exception: " + se);
                return fd;
            }
        }

        for (int i = 0; i < vFieldDefs.size() && !bFound; i++) {
            fd = (FieldDef) vFieldDefs.get(i);

            if (fd.getName().equals(sFieldName)) {
                bFound = true;
            }
        }
        return fd;
    }

    /**
     * Parses the parameters and calls the web service operation
     *
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void parseModifyRecord() throws Exception {
        java.util.Enumeration e = pmRequest.getParameterNames();

        Vector vAttachments = new Vector();

        getStructure();

        Record r = new Record();
        r.setId(Integer.parseInt(pmRequest.getParameter("idRecord")));
        Field fAux;
        String paramName;
        String fieldType;
        String fieldName;
        String fieldId;

        while (e.hasMoreElements()) {
            paramName = (String) e.nextElement();
            fAux = new Field();

            if (paramName.startsWith("fd_")) {
                fieldId = paramName.substring(3);
                fieldName = ((FieldDef) vFieldDefs.get(Integer.parseInt(fieldId))).getName();
                fieldType = getFieldDef(fieldName).getType();

                if (fieldType.equals("image") || fieldType.equals("sound")) {
                    FileItem fi = pmRequest.getFileItem(paramName);
                    String sNomFitxer;

                    if (pmRequest.getParameter("del_" + fieldId).equals("true")) {
                        sNomFitxer = "delete";
                    } else if (fi.getSize() != 0) //case there is no attachment
                    {
                        sNomFitxer = Utils.getFileName(fi.getName());
                        fi.setFieldName(fieldName);

                        vAttachments.add(fi);
                    } else {
                        sNomFitxer = "null";
                    }

                    fAux.setName(fieldName);
                    fAux.setValue(sNomFitxer);

                } else {
                    fAux.setName(fieldName);
                    fAux.setValue(pmRequest.getParameter(paramName));
                }

                r.addField(fAux);

            } else if (paramName.startsWith("fdYYYY_")) {
                fieldId = paramName.substring(7);
                fieldName = ((FieldDef) vFieldDefs.get(Integer.parseInt(fieldId))).getName();

                String sDay = pmRequest.getParameter("fdDD_" + fieldId);
                String sMonth = pmRequest.getParameter("fdMM_" + fieldId);
                String sYear = pmRequest.getParameter("fdYYYY_" + fieldId);
                fAux.setName(fieldName);
                fAux.setValue(sYear + "-" + sMonth + "-" + sDay);
                r.addField(fAux);
            }

        }
        modifyRecord(r, vAttachments);
    }

    /**
     * Calls the web service operation
     * <I>modifyRecord(User,Owner,Collection,Record) : void</I>
     *
     * @param r the Record to modify
     * @param vAttachments a Vector containing the Attachments of the Record
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void modifyRecord(Record r, Vector vAttachments) throws Exception {
        User u = new User(getUserId());

        Collection c = new Collection("");

        c.setName(collection);

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "modifyRecord");

            addParam(sbeRequest, u);
            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }
            addParam(sbeRequest, c);
            addParam(sbeRequest, r);

            for (int i = 0; i < vAttachments.size(); i++) {

                FileItem fi = (FileItem) vAttachments.get(i);

                String sNomFitxer = Utils.getFileName(fi.getName());

                File fTemp = File.createTempFile("attach", null);

                fi.write(fTemp);

                URL urlFile = new URL("file://" + fTemp.getPath());

                AttachmentPart ap = smRequest.createAttachmentPart(new DataHandler(urlFile));

                String fieldName = fi.getFieldName();

                ap.setContentId(fieldName + "/" + sNomFitxer);

                smRequest.addAttachmentPart(ap);
            }

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.record"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "modify");
            } else {

            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Parses the parameters and calls the web service operation
     *
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void parseAddRecord() throws Exception {
        java.util.Enumeration e = pmRequest.getParameterNames();

        Vector vAttachments = new Vector();

        getStructure();

        Record r = new Record();
        Field fAux;
        String paramName;
        String fieldType;
        String fieldName;
        String fieldId;

        while (e.hasMoreElements()) {
            paramName = (String) e.nextElement();
            fAux = new Field();

            if (paramName.startsWith("fd_")) {
                fieldId = paramName.substring(3);
                fieldName = ((FieldDef) vFieldDefs.get(Integer.parseInt(fieldId))).getName();
                fieldType = getFieldDef(fieldName).getType();

                if (fieldType.equals("image") || fieldType.equals("sound")) {
                    FileItem fi = pmRequest.getFileItem(paramName);
                    String sNomFitxer;

                    if (fi.getSize() != 0) //case there is no attachment
                    {
                        sNomFitxer = Utils.getFileName(fi.getName());

                        fi.setFieldName(fieldName);

                        vAttachments.add(fi);
                    } else {
                        sNomFitxer = "null";
                    }

                    fAux.setName(fieldName);
                    fAux.setValue(sNomFitxer);
                } else {
                    fAux.setName(fieldName);
                    fAux.setValue(pmRequest.getParameter(paramName));
                }
                r.addField(fAux);

            } else if (paramName.startsWith("fdYYYY_")) {
                fieldId = paramName.substring(7);
                fieldName = ((FieldDef) vFieldDefs.get(Integer.parseInt(fieldId))).getName();

                String sDay = pmRequest.getParameter("fdDD_" + fieldId);
                String sMonth = pmRequest.getParameter("fdMM_" + fieldId);
                String sYear = pmRequest.getParameter("fdYYYY_" + fieldId);

                fAux.setName(fieldName);
                fAux.setValue(sYear + "-" + sMonth + "-" + sDay);
                r.addField(fAux);
            }
        }
        addRecord(r, vAttachments);
    }

    /**
     * Calls the web service operation
     * <I>addRecord(User,Owner,Collection,Record) : void</I>
     *
     * @param r the Record to add
     * @param vAttachments a Vector containing the Attachments of the Record
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void addRecord(Record r, Vector vAttachments) throws Exception {
        User u = new User(getUserId());
        Collection c = new Collection("");

        Vector vTempFiles = new Vector();

        c.setName(collection);

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "addRecord");

            addParam(sbeRequest, u);
            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }
            addParam(sbeRequest, c);
            addParam(sbeRequest, r);

            for (int i = 0; i < vAttachments.size(); i++) {

                FileItem fi = (FileItem) vAttachments.get(i);

                String sNomFitxer = Utils.getFileName(fi.getName());

                File fTemp = File.createTempFile("attach", null);

                fi.write(fTemp);

                vTempFiles.add(fTemp);

                URL urlFile = new URL("file://" + fTemp.getPath());

                AttachmentPart ap = smRequest.createAttachmentPart(new DataHandler(urlFile));

                String fieldName = fi.getFieldName();

                ap.setContentId(fieldName + "/" + sNomFitxer);

                smRequest.addAttachmentPart(ap);
            }
            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.record"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "add");
            } else {

            }
        } catch (Exception e) {
            throw e;
        } finally {
            File fAux;

            for (int i = 0; i < vTempFiles.size(); i++) {
                fAux = (File) vTempFiles.get(i);
                fAux.delete();
            }
        }
    }

    /**
     * Calls the web service operation
     * <I>deleteRecord(User,Owner,Collection,Record) : void</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void deleteRecord() throws SOAPException {
        User u = new User(getUserId());

        Collection c = new Collection("");
        Record r = new Record();

        r.setId(Integer.parseInt(pmRequest.getParameter("idRecord")));
        c.setName(collection);

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "deleteRecord");

            addParam(sbeRequest, u);
            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }
            addParam(sbeRequest, c);
            addParam(sbeRequest, r);

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.record"));

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
     * Calls the web service operation <I>getStructure(User,Owner,Collection) :
     * [FieldDef]</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void getStructure() throws SOAPException {
        User u = new User("");

        if (sUserVisitor != null) {
            u.setUserId(sUserVisitor);
        } else {
            u = new User(getUserId());
        }

        vFieldDefs = new Vector();

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "getStructure");

            addParam(sbeRequest, u);

            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }
            addParam(sbeRequest, new Collection(collection));

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, getJspProperties().getProperty("url.servlet.structure"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "get");
            } else {
                vFieldDefs = getFieldDefs(smResponse);
            }

        } catch (SOAPException se) {
            throw se;
        }

    }

    /**
     * Calculates all the conditions of the q Query object
     *
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void calculateConditions() throws Exception {
        getStructure();
        logger.debug("1");
        java.util.Enumeration eQuery = pmRequest.getParameterNames();
        String param, op, name, cmp, value, id;
        int index;
        Vector ids = new Vector();
        logger.debug("ids: " + ids);
        while (eQuery.hasMoreElements()) {
            param = (String) eQuery.nextElement();
            logger.debug("param: " + param);
            if (param.startsWith("name_")) {
                id = param.substring(param.indexOf("_") + 1);

                ids.add(new Integer("" + id));
            }
        }

        java.util.Collections.sort(ids);
        logger.debug("ids 2: " + ids.size());
        for (int i = 0; i < ids.size(); i++) {
            id = ((Integer) ids.get(i)).toString();

            op = pmRequest.getParameter("op_" + id);
            logger.debug("2");
            if (op == null) {
                op = "null";
            }

            logger.debug("3");
            name = pmRequest.getParameter("name_" + id);
            logger.debug("4");
            cmp = pmRequest.getParameter("cmp_" + id);
            logger.debug("5");
            value = pmRequest.getParameter("value_" + id);
            logger.debug("6");

            q.addCondition(new Condition(op, name, cmp, value));
            logger.debug("7");
        }
    }

    /**
     * Calls the web service operation
     * <I>exportCollection(User,Owner,Collection,Query) : FILE</I>
     *
     * @param response the HttpServletResponse where to return the File
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void exportCollection(HttpServletResponse response) throws Exception {
        User u = new User("");

        if (sUserVisitor != null) {
            u.setUserId(sUserVisitor);
        } else {
            u = new User(getUserId());
        }

        try {

            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "exportCollection");

            addParam(sbeRequest, u);

            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }

            addParam(sbeRequest, new Collection(collection));

            if (operation.equals("exportAll")) {
                Query qAux = new Query();
                qAux.setBeginIndex(0);
                qAux.setDirection("asc");
                qAux.setOrderField("null");
                addParam(sbeRequest, qAux);

            } else if (operation.equals("exportStructure")) {
                Query qAux = new Query();
                qAux.setBeginIndex(0);
                qAux.setDirection("asc");
                qAux.setOrderField("null");

                Condition cAux = new Condition("null", ((FieldDef) vFieldDefs.get(0)).getName(), "=", "31051983");
                /*We make a query that hopefully :) returns no records, just the structure*/

                qAux.addCondition(cAux);

                addParam(sbeRequest, qAux);
            } else {
                addParam(sbeRequest, q);
            }
            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.collection"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "export");
            } else {
                Iterator iAttachments = smResponse.getAttachments();

                //response.setContentType("application/x-zip-compressed");
                response.setContentType("application/zip");

                String nameOk = Utils.toValidFileName(collection);

                response.setHeader("Content-disposition", "filename=" + nameOk + ".zip");

                if (iAttachments.hasNext()) {
                    AttachmentPart ap = (AttachmentPart) iAttachments.next();

                    InputStream is = ap.getDataHandler().getInputStream();

                    OutputStream os = response.getOutputStream();

                    byte[] buff = new byte[1024];
                    int read = 0;

                    while ((read = is.read(buff, 0, buff.length)) != -1) {
                        os.write(buff, 0, read);
                    }

                    os.flush();
                    //os.close();

                }

            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Calls the web service operation
     * <I>importRecords(User,Owner,Collection,FILE) : void</I>
     *
     * @param fiImport the FileItem Zip of the Records to import
     * @throws java.lang.Exception when an Exception error occurs
     */
    protected void importRecords(FileItem fiImport) throws Exception {
        User uRequest = new User(getUserId());
        Collection cRequest = new Collection(collection);
        File fTemp = null;

        try {
            smRequest = mf.createMessage();

            SOAPBody sbRequest = smRequest.getSOAPBody();

            Name n = sf.createName("importRecords");

            SOAPBodyElement sbeRequest = sbRequest.addBodyElement(n);

            sbeRequest.addChildElement(uRequest.toXml());

            if (owner != null) {
                Owner oRequest = new Owner(owner);
                sbeRequest.addChildElement(oRequest.toXml());
            }

            sbeRequest.addChildElement(cRequest.toXml());

            String sNomFitxer = Utils.getFileName(fiImport.getName());

            fTemp = File.createTempFile("attach", null);

            fiImport.write(fTemp);

            URL urlFile = new URL("file://" + fTemp.getPath());

            AttachmentPart ap = smRequest.createAttachmentPart(new DataHandler(urlFile));

            smRequest.addAttachmentPart(ap);

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.record"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "importRecords");
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
     * Return a String containing the image file extensions permitted separated
     * by ','
     *
     * @return a String
     */
    public String getImageExt() {
        return getJspProperties().getProperty("file.image");
    }

    /**
     * Return a String containing the sound file extensions permitted separated
     * by ','
     *
     * @return a String
     */
    public String getSoundExt() {
        return getJspProperties().getProperty("file.sound");
    }

    /**
     * Calls the web service operation <I>getPermission
     * (User,Owner,Collection,Record) : Guest</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void getPermission() throws SOAPException {
        User u = new User("");

        if (sUserVisitor != null) {
            u.setUserId(sUserVisitor);
        } else {
            u = new User(getUserId());
        }

        Owner o = new Owner(owner);
        Collection c = new Collection(collection);

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "getPermission");

            addParam(sbeRequest, u);
            addParam(sbeRequest, o);
            addParam(sbeRequest, c);
            if (record != null) {
                Record r = new Record();
                r.setId(record.getId());
                addParam(sbeRequest, r);
            }

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.share"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "getPermission");
            } else {
                permission = getIntValue(smResponse, "permission");
            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Calls the web service operation
     * <I>getCollectionInfo(User,Owner,Collection) : Collection</I>
     *
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    protected void getCollectionInfo() throws SOAPException {
        User u = new User("");

        if (sUserVisitor != null) {
            u.setUserId(sUserVisitor);
        } else {
            u = new User(getUserId());
        }

        try {
            smRequest = mf.createMessage();

            SOAPBodyElement sbeRequest = setRequestName(smRequest, "getCollectionInfo");

            addParam(sbeRequest, u);
            if (owner != null) {
                Owner oRequest = new Owner(owner);
                addParam(sbeRequest, oRequest);
            }
            addParam(sbeRequest, new Collection(collection));

            smRequest.saveChanges();

            SOAPMessage smResponse = sendMessage(smRequest, this.getJspProperties().getProperty("url.servlet.structure"));

            SOAPBody sbResponse = smResponse.getSOAPBody();

            if (sbResponse.hasFault()) {
                checkFault(sbResponse, "get");
            } else {
                Collection cResponse = getCollection(smResponse);

                description = cResponse.getDescription();
                tags = Tags.decode(cResponse.getTags());

                java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
                created = df.format(cResponse.getCreated());
            }
        } catch (SOAPException se) {
            throw se;
        }
    }

    /**
     * Updates the postion to the current Record after modifying a Record
     */
    protected void updateCurrentRecord() {
        int idRecordModified = Integer.parseInt(pmRequest.getParameter(("idRecord")));

        boolean bFound = false;

        int i = 0;
        Record rCurrent = null;

        while (!bFound && (i < vRecords.size())) {
            rCurrent = (Record) vRecords.get(i);

            if (rCurrent.getId() == idRecordModified) {
                bFound = true;
            } else {
                i++;
            }
        }

        if (bFound) {
            q.setBeginIndex(i);
            record = rCurrent;
        }
    }
}
