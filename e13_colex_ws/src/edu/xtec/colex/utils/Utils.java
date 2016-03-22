/*
 * File    : Utils.java
 * Created : 15-sep-2005 17:56
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
package edu.xtec.colex.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import edu.xtec.colex.domain.Attachment;
import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.domain.FieldDef;
import edu.xtec.colex.domain.Record;
import edu.xtec.colex.domain.User;
import edu.xtec.colex.exception.DataBaseException;
import edu.xtec.colex.exception.ServerException;
import static edu.xtec.colex.server.DataBase.getDBProperties;
import org.apache.log4j.Logger;

/**
 * Auxiliar utils class with static methods used for managing files
 *
 * @author ogalzorriz
 * @version 1.0
 */
public class Utils {

    /**
     * String constant with validFileChars used in the method toValidFileName
     */
    private static final String validFileChars = "_!~0123456789abcdefghijklmnopqrstuvwxyz";
    /**
     * String constant with convertibleChars used in the method toValidFileName
     */
    private static final String convertibleChars = "\u00e1\u00e0\u00e4\u00e2\u00e3\u00e9\u00e8\u00eb\u00ea\u00ed\u00ec\u00ef\u00ee\u00f3\u00f2\u00f6\u00f4\u00f5\u00fa\u00f9\u00fc\u00fb\u00f1\u00e7\u20ac\u00ba\u00aa\u00e5\u00e6\u00f8\u00fd\u00fe\u00ff";
    /**
     * String constant with equivalentChars used in the method toValidFileName
     */
    private static final String equivalentChars = "aaaaaeeeeiiiiooooouuuunceoaaaoypy";

    // logger
    static final Logger logger = Logger.getLogger(Utils.class);

    /**
     * Creates a Zip File containing an XML with the information of Collection,
     * the FieldDefs and all the Records. If there are attachments (images or
     * sounds) they are also included in the ZIP.
     *
     * @param cRequest the Collection to Zip
     * @param vFieldsDefs a Vector containing the FieldDefinitions to Zip
     * @param vRecords a Vector containing the Records of the Collection to Zip
     * @param vAttachments a Vector containing the Attachments (images or
     * sounds) to Zip
     * @param sFielsDir the String Path where the file Attachments are stored
     * @return A Zip File with the Collection zipped
     * @throws edu.xtec.colex.exception.ServerException when an internal
     * Exception error occurs
     */
    public static File toZip(Collection cRequest, Vector vFieldsDefs, Vector vRecords, Vector vAttachments, String sFielsDir) throws ServerException {
        
        File fileZip = null;
        
        String sFileZip;
        
        try {
            TransformerFactory factory = new com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl();
            Transformer transformer = factory.newTransformer();
            
            SOAPElement seCollection = cRequest.toXml();
            FieldDef fdAux;
            Record rAux;
            logger.debug("seCollection: " + seCollection);
            for (int i = 0; i < vFieldsDefs.size(); i++) {
                fdAux = (FieldDef) vFieldsDefs.get(i);
                seCollection.addChildElement(fdAux.toXml());
            }
            
            for (int i = 0; i < vRecords.size(); i++) {
                rAux = (Record) vRecords.get(i);
                seCollection.addChildElement(rAux.toXml());
            }
            
            File fileXml = File.createTempFile("xml", null);
            logger.debug("XMLFile: " + fileXml);
            StreamResult sr = new StreamResult(fileXml.getPath());
            logger.debug("StreamResult: " + sr);
            //http://www.rgagnon.com/javadetails/java-0481.html
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            // we want to pretty format the XML output
            // note : this is broken in jdk1.5 beta!
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            logger.debug("DOMSource");
            transformer.transform(new DOMSource(seCollection), sr);
            logger.debug("transformed");
            int BUFFER = 2048;
            
            BufferedInputStream origin = null;
            
            fileZip = File.createTempFile("exp", "zip");
            logger.debug("fileZip:" + fileZip);
            FileOutputStream dest = new FileOutputStream(fileZip);
            
            ZipOutputStream out = new ZipOutputStream(dest);
            
            byte data[] = new byte[BUFFER];
            
            FileInputStream fi = new FileInputStream(fileXml);
            origin = new BufferedInputStream(fi, BUFFER);
            String name = Utils.toValidFileName(cRequest.getName());
            ZipEntry entry = new ZipEntry(name + ".xml");
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            
            fileXml.delete();
            
            Attachment aAux;
            for (int i = 0; i < vAttachments.size(); i++) {
                aAux = (Attachment) vAttachments.get(i);
                
                fi = new FileInputStream(sFielsDir + aAux.getUrl());
                origin = new BufferedInputStream(fi, BUFFER);
                entry = new ZipEntry(aAux.getOriginalName());
                out.putNextEntry(entry);
                
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.flush();
            out.close();
            
            dest.flush();
            dest.close();
            
        } catch (SOAPException se) {
            throw new ServerException(se);
        } catch (FileNotFoundException fnfe) {
            throw new ServerException(fnfe);
        } catch (MalformedURLException murle) {
            throw new ServerException(murle);
        } catch (IOException ioe) {
            throw new ServerException(ioe);
        } catch (TransformerException tfe) {
            throw new ServerException(tfe);
        }
        
        return fileZip;
    }

    /**
     * Unzip a Zip File of a Collection
     *
     * @param cRequest input/output parameter that contains the name of the
     * collection and will be filled with its information
     * @param zipAttachment the AttachmentPart containing the Zip File of the
     * collection
     * @param vFieldsDefs output parameter, a Vector containing the FieldsDefs
     * of the Collection
     * @param vRecords output parameter, a Vector containing the Records of the
     * Collection
     * @param hTempFiles output parameter, a Hashtable containing the reference
     * to Attachments files of a Record
     * @return an int with the Attachments size in bytes
     * @throws edu.xtec.colex.exception.ServerException when an internal
     * Exception error occurs
     * @throws edu.xtec.colex.exception.DataBaseException when the given
     * parametres not agree with the requeriments of the system
     */
    public static int unZip(Collection cRequest, AttachmentPart zipAttachment, Vector vFieldsDefs, Vector vRecords, Hashtable hTempFiles) throws ServerException, DataBaseException {
        int iSize = 0;
        
        try {
            SOAPFactory sf = SOAPFactory.newInstance();
            DataHandler dh = zipAttachment.getDataHandler();
            ZipInputStream zis = new ZipInputStream(dh.getInputStream());
            ZipEntry ze;
            
            SOAPElement seCollection = null;
            
            while ((ze = zis.getNextEntry()) != null) {
                //First we create the the tempFile and then we take the URL to it 
                //for creating the attachment

                File fTemp = File.createTempFile("imp", null);
                
                FileOutputStream out = new FileOutputStream(fTemp);
                byte[] b = new byte[512];
                int len = 0;
                
                while ((len = zis.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                
                out.close();
                
                if (ze.getName().endsWith(".xml")) {
                    seCollection = xmlFileToSOAP(fTemp);
                    fTemp.delete();
                } else {
                    hTempFiles.put(ze.getName(), fTemp);
                    iSize += ze.getSize();
                }
                
                zis.closeEntry();
                
            }
            
            Collection cNewCollection = new Collection(seCollection);
            
            cRequest.setIsPublic(cNewCollection.getIsPublic());
            cRequest.setDescription(cNewCollection.getDescription());
            cRequest.setTags(cNewCollection.getTags());
            cRequest.setCreated(cNewCollection.getCreated());
            
            Vector vFieldsDefsAtt = new Vector();
            
            SOAPElement seAux;
            FieldDef fdAux;
            Iterator iAux;
            
            if (seCollection == null) {
                throw new DataBaseException(DataBaseException.NO_VALID_IMPORT_FILE);
            }
            //Case there is a problem reading the XML Collection

            iAux = seCollection.getChildElements(sf.createName("fieldDef"));
            
            while (iAux.hasNext()) {
                seAux = (SOAPElement) iAux.next();
                fdAux = FieldDef.createFieldDef(seAux);
                
                for (int i = 0; i < vFieldsDefs.size(); i++) {
                    if (fdAux.getName().equals(((FieldDef) vFieldsDefs.get(i)).getName())) {
                        throw new DataBaseException(DataBaseException.REPEATED_FIELD_NAME);
                    }
                }
                
                vFieldsDefs.add(fdAux);
                
                if (fdAux.getType().equals("image") || fdAux.getType().equals("sound")) {
                    vFieldsDefsAtt.add(fdAux);
                }
            }
            
            Vector vAttachments;
            Record rAux;
            String value;
            
            iAux = seCollection.getChildElements(sf.createName("record"));
            
            while (iAux.hasNext()) {
                seAux = (SOAPElement) iAux.next();
                rAux = new Record(seAux);
                
                vAttachments = new Vector();
                for (int i = 0; i < vFieldsDefsAtt.size(); i++) {
                    fdAux = (FieldDef) vFieldsDefsAtt.get(i);
                    
                    value = rAux.getFieldValue(fdAux.getName());
                    
                    if (!(value.equals("null"))) {
                        File fTemp = (File) hTempFiles.get(value);
                        URL urlFile = fTemp.toURI().toURL();
                        
                        AttachmentPart ap = MessageFactory.newInstance().createMessage().createAttachmentPart(new DataHandler(urlFile));
                        ap.setContentId(fdAux.getName() + "/" + value);
                        vAttachments.add(ap);
                    }
                }
                vRecords.add(rAux);
                vRecords.add(vAttachments);
                
            }
        } catch (SOAPException se) {
            throw new ServerException(se);
        } catch (FileNotFoundException fnfe) {
            throw new ServerException(fnfe);
        } catch (MalformedURLException murle) {
            throw new ServerException(murle);
        } catch (IOException ioe) {
            throw new ServerException(ioe);
        }
        
        return iSize;
    }

    /**
     * Converts a File into a SOAPElement
     *
     * @param fTemp the XML File
     * @throws edu.xtec.colex.exception.ServerException when an internal
     * Exception error occurs
     * @return a SOAPElement with the contents of the XML File
     */
    public static SOAPElement xmlFileToSOAP(File fTemp) throws ServerException {
        SOAPElement element = null;
        
        try {
            DocumentBuilderFactory DBfactory = DocumentBuilderFactory.newInstance();
            DBfactory.setValidating(false);
            DBfactory.setNamespaceAware(true);
            DocumentBuilder parser;
            
            parser = DBfactory.newDocumentBuilder();
            
            SOAPMessage message = MessageFactory.newInstance().createMessage();
            SOAPPart soap;
            SOAPEnvelope envelope;
            SOAPBody body;
            
            InputStream in = new FileInputStream(fTemp);
            Document xml = parser.parse(in);

            // Create the SOAPMessage, as the factory for SOAPElement.
            soap = message.getSOAPPart();
            envelope = soap.getEnvelope();
            body = envelope.getBody();

            // Adopt the content of the original document.
            element = body.addDocument(xml);
            message.saveChanges();
        } catch (SOAPException se) {
            throw new ServerException(se);
        } catch (ParserConfigurationException pce) {
            throw new ServerException(pce);
        } catch (FileNotFoundException fnfe) {
            throw new ServerException(fnfe);
        } catch (SAXException saxe) {
            throw new ServerException(saxe);
        } catch (IOException ioe) {
            throw new ServerException(ioe);
        }
        
        return element;
    }

    /**
     * Extracts just the file name and its extension of a String including the
     * path to the file
     *
     * @param sPath a String file + path
     * @return a String file name and its extension
     */
    public static String getFileName(String sPath) {
        String sFilename = null;
        if (sPath != null) {
            sFilename = sPath;
            int iIndex = sPath.lastIndexOf("\\");
            if (iIndex < 0) {
                iIndex = sPath.lastIndexOf("/");
            }
            if (iIndex >= 0) {
                sFilename = sPath.substring(iIndex + 1);
            }
        }
        return sFilename;
    }

    /**
     * Converts the chars of a file to valid chars for saving it on the file
     * system
     *
     * @param fn the String file name
     * @return the String file name with valid chars
     */
    static public String toValidFileName(String fn) {
        String result = null;
        if (fn != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fn.length(); i++) {
                char ch = Character.toLowerCase(fn.charAt(i));
                if (validFileChars.indexOf(ch) < 0) {
                    int p = convertibleChars.indexOf(ch);
                    if (p >= 0) {
                        ch = equivalentChars.charAt(p);
                    } else {
                        ch = '_';
                    }
                }
                sb.append(ch);
            }
            
            result = sb.substring(0);
        }
        return result;
    }

    /**
     * Returns the disk quota used by a User in bytes
     *
     * @param sFielsDir the String path where all the attachments files are
     * saved
     * @param uRequest the User to get disk quota used
     * @return an int with de disk quota used in bytes
     */
    static public int getDirSize(String sFielsDir, User uRequest) {
        File folder = new File(sFielsDir + uRequest.getUserId());
        
        if (!folder.exists()) {
            return 0;
        }
        
        File[] fileList = folder.listFiles();
        
        int iSize = 0;
        
        for (int i = 0; i < fileList.length; i++) {
            iSize += fileList[i].length();
            
            if (fileList[i].isDirectory()) {
                File[] subFiles = fileList[i].listFiles();
                
                for (int j = 0; j < subFiles.length; j++) {
                    iSize += subFiles[j].length();
                }
            }
        }
        return iSize;
    }

    /**
     * Creates the directory to store all the attachments files of a user
     *
     * @param sFilesDir the String path where all the attachments files are
     * saved
     * @param uRequest the User to create the directory
     * @return true if the directory was created or it exists before; false
     * otherwise
     */
    static public boolean createDir(String sFilesDir, User uRequest) {
        
        File f = new File(sFilesDir + uRequest.getUserId() + "/");
        if (f.exists()) {
            return true;
        } else {
            return f.mkdir();
        }
        
    }

    /**
     * Creates the directory to store all the attachments files of a collection
     *
     * @param sFielsDir the String path where all the attachments files are
     * saved
     * @param uRequest the User owner of the Collection
     * @param cRequest the Collection to create the directory
     */
    static public void createDir(String sFielsDir, User uRequest, Collection cRequest) {
        File f = new File(sFielsDir + uRequest.getUserId() + "/" + cRequest.getId() + "/");
        f.mkdir();
    }

    /**
     * Deletes the directory to store all the attachments files of a collection
     *
     * @param sFielsDir the String path where all the attachments files are
     * saved
     * @param uRequest uRequest the User owner of the Collection
     * @param cRequest the Collection to create the directory
     */
    static public void deleteDir(String sFielsDir, User uRequest, Collection cRequest) {
        File f = new File(sFielsDir + uRequest.getUserId() + "/" + cRequest.getId() + "/");
        deleteDirectory(f);
    }

    /**
     * Deletes a directory from the file system
     *
     * @param path the File path to delete
     * @return true if the path has been deleted, else false
     */
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Auxiliar method useful for debugging. It converts a SOAPMessage into a
     * String
     *
     * @param sm the SOAPMessage to convert
     * @return the String containing the SOAPMessage
     */
    public static String soapMessageToString(SOAPMessage sm) //throws ServerException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String sRes = "";
        int tab = 0;
        
        try {
            sm.writeTo(baos);
            
            String s = baos.toString();
            
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == '<') {
                    sRes = sRes + "\n";
                }
                sRes = sRes + s.charAt(i);
            }
        } catch (SOAPException se) {/*throw new ServerException();*/
            
        } catch (IOException ioe) {/*throw new ServerException();*/
            
        }
        
        return sRes;
    }
}
