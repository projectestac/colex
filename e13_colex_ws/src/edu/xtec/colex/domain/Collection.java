/*
 * File    : Collection.java
 * Created : 27-jun-2005 12:24
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
package edu.xtec.colex.domain;

import javax.xml.soap.*;
import java.util.*;
import java.text.*;
/**
 * A collection is a set of field definitions (the structure) and a set of records (the content).
 * In this class we store all the attributes of a collection.
 * @author ogalzorriz
 * @version 1.0
 */
public class Collection extends ObjectColex{
    /**
     * The name of the Collection
     */
    private String name;
    /**
     * The id of the Collection assigned by the system
     */
    private int id = 0;
    /**
     * The id of the FieldDef to sort by default
     */
    private int orderFieldDef = -1;
    /**
     * The number of records to retrieve by default
     */
    private int retrieveRecords = 1;
    /**
     * The number of FieldDefs of the Collection
     */
    private int numFieldsDefs = 0;
    /**
     * The number of Records of the Collection
     */
    private int numRecords = -1;
    /**
     * If == 1 the Collection is public, else is private
     */
    private int isPublic = -1;
    /**
     * A long textual description of the collection
     */
    private String description = null;
    /**
     * The tags to describe the Collection
     */
    private String tags = null;
    /**
     * The Date when the Collection was created
     */
    private Date created = null;
    /**
     * Auxiliar attribute to format the Date
     */
    protected DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Creates a new Collection object with the given Name
     * @param sName the name for the Collection
     */
    public Collection (String sName)
    {
        name=sName;
    }

    /**
     * Creates a new Collection from a SOAPElement
     * @param se the SOAPElement containing the XML representation of the Collection
     * @throws javax.xml.soap.SOAPException if a SOAP error occurs
     */
    public Collection(SOAPElement se) throws SOAPException
    {

        Iterator iAux=se.getChildElements(soapFactory.createName("name"));
        name=((SOAPElement)iAux.next()).getValue();

        iAux=se.getChildElements(soapFactory.createName("isPublic"));

        if (iAux.hasNext())
        {
            String sIsPublic = ((SOAPElement)iAux.next()).getValue();
            if (sIsPublic.equals("true")) isPublic = 1;
            else isPublic = 0;
        }

        iAux=se.getChildElements(soapFactory.createName("description"));

        if (iAux.hasNext())
        {
            description = ((SOAPElement)iAux.next()).getValue();
            if (description==null) description="";
        }

        iAux=se.getChildElements(soapFactory.createName("tags"));

        if (iAux.hasNext())
        {
            tags = ((SOAPElement)iAux.next()).getValue();
            if (tags==null) tags="";
        }

        iAux=se.getChildElements(soapFactory.createName("numRecords"));

        if (iAux.hasNext())
        {
            numRecords = Integer.parseInt(((SOAPElement)iAux.next()).getValue());
        }

        iAux=se.getChildElements(soapFactory.createName("created"));

        if (iAux.hasNext())
        {
            String sDate = ((SOAPElement)iAux.next()).getValue();

            try
            {
                created = df.parse(sDate);
            }
            catch(ParseException pe)
            {
                created = null;
            }
        }
    }

    /**
     * Sets the name of this Collection
     * @param sName the String containing the name
     */
    public void setName(String sName)
    {
        name=sName;
    }

    /**
     * Returns the name of this Collection
     * @return the String containing the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the description of this Collection
     * @param sDescription the description
     */
    public void setDescription(String sDescription)
    {
        description=sDescription;
    }

    /**
     * Returns the description of this Collection
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the id of this Collection
     * @param iId the int value of the id
     */
    public void setId(int iId)
    {
        id=iId;
    }

    /**
     * Returns the id of this Collection
     * @return the int value of the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set if a collection is public  or not
     * @param iIsPublic If == 1 the Collection is public, else is private
     */
    public void setIsPublic(int iIsPublic)
    {
        isPublic=iIsPublic;
    }

    /**
     * Returns if a collection is public or not
     * @return  If == 1 the Collection is public, else is private
     */
    public int getIsPublic()
    {
        return isPublic;
    }

    /**
     * Sets the id of the FieldDef to sort the Collection by default of this Collection
     * @param iOrderFieldDef the int value of the FieldDef
     */
    public void setOrderFieldDef(int iOrderFieldDef)
    {
        orderFieldDef=iOrderFieldDef;
    }

    /**
     * Returns the id of the FieldDef to sort the Collection by default of this Collection
     * @return the int value of the FieldDef
     */
    public int getOrderFieldDef()
    {
        return orderFieldDef;
    }

    /**
     * Sets the number of Records to retrive from the Collection by default of this
     * Collection
     * @param iRetrieveRecords the int value of number of Records
     */
    public void setRetrieveRecords(int iRetrieveRecords)
    {
        retrieveRecords=iRetrieveRecords;
    }

    /**
     * Returns the number of Records to retrive from the Collection by default of
     * this Collection
     * @return the int value of number of Records
     */
    public int getRetrieveRecords()
    {
        return retrieveRecords;
    }

    /**
     * Sets the number of FieldDefs of this Collection
     * @param iNumFieldsDefs the int value of number of FieldDef(s)
     */
    public void setNumFieldsDefs(int iNumFieldsDefs)
    {
        numFieldsDefs=iNumFieldsDefs;
    }

    /**
     * Returns the number of FieldDefs of this Collection
     * @return the int value of number of FieldDefs
     */
    public int getNumFieldsDefs()
    {
        return numFieldsDefs;
    }

    /**
     * Sets the number of Records of this Collection
     * @param iNumRecords the int value of the numbers of Records
     */
    public void setNumRecords(int iNumRecords)
    {
        numRecords=iNumRecords;
    }

    /**
     * Returns the number of Records of this Collection
     * @return the int value of the numbers
     */
    public int getNumRecords()
    {
        return numRecords;
    }

    /**
     * Sets the tags of this Collection
     * @param sTags the tags
     */
    public void setTags(String sTags)
    {
        tags=sTags;
    }

    /**
     * Returns the tags of the Collection
     * @return the Tags
     */
    public String getTags()
    {
        return tags;
    }

    /**
     * Sets the creation date of the Collection
     * @param dCreated the creation date
     */
    public void setCreated(Date dCreated)
    {
        created = dCreated;
    }

    /**
     * Returns the creation date of the Collection
     * @return the creation date
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * Returns an XML representation of this Collection
     * @return the SOAPElement containing the XML representation of this Collection
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public SOAPElement toXml() throws SOAPException
    {
        SOAPElement seCollection=null;SOAPElement seName,seIsPublic,seDescription,seTags,seNumRecords,seCreated;

        seCollection = soapFactory.createElement("collection");

        seName = soapFactory.createElement("name");

        seName.addTextNode(name);
        seCollection.addChildElement(seName);

        if (isPublic!=-1)
        {
            seIsPublic = soapFactory.createElement("isPublic");

            if (isPublic == 0)   seIsPublic.addTextNode("false");
            else    seIsPublic.addTextNode("true");
            seCollection.addChildElement(seIsPublic);
        }
        if (description != null)
        {
            seDescription = soapFactory.createElement("description");
            seDescription.addTextNode(description);
            seCollection.addChildElement(seDescription);
        }
        if (tags != null)
        {
            seTags = soapFactory.createElement("tags");
            seTags.addTextNode(tags);
            seCollection.addChildElement(seTags);
        }
        if (numRecords != -1)
        {
            seNumRecords = soapFactory.createElement("numRecords");
            seNumRecords.addTextNode(Integer.toString(numRecords));
            seCollection.addChildElement(seNumRecords);
        }
        if (created != null)
        {
            seCreated = soapFactory.createElement("created");
            seCreated.addTextNode(df.format(created));
            seCollection.addChildElement(seCreated);
        }

        return seCollection;
    }

    /**
     * Transform a String into a set of tags separated by '$'. All the chars are converted to lowercase and the accents and dieresis are replaced
     * @param tags The original String of tags
     * @return the String with the tags encoded
     */
    public static String encodeTags(String tags)
    {
        String sRes = "$";
        String sTag = "";

        Vector vRes = new Vector();

        char c;

        tags = tags.toLowerCase();

        for (int i=0; i<tags.length();i++)
        {
            c = tags.charAt(i);

            switch (c)
            {
                case 'à': sTag = sTag + 'a'; break;
                case 'á': sTag = sTag + 'a'; break;
                case 'ä': sTag = sTag + 'a'; break;

                case 'è': sTag = sTag + 'e'; break;
                case 'é': sTag = sTag + 'e'; break;
                case 'ë': sTag = sTag + 'e'; break;

                case 'ì': sTag = sTag + 'i'; break;
                case 'í': sTag = sTag + 'i'; break;
                case 'ï': sTag = sTag + 'i'; break;

                case 'ò': sTag = sTag + 'o'; break;
                case 'ó': sTag = sTag + 'o'; break;
                case 'ö': sTag = sTag + 'o'; break;

                case 'ù': sTag = sTag + 'u'; break;
                case 'ú': sTag = sTag + 'u'; break;
                case 'ü': sTag = sTag + 'u'; break;


                case '$': break;


                default:    if (c == ',' || c==' ')
                {
                    if (sTag.length()>0)
                    {
                        if (sRes.indexOf("$"+sTag+"$")==-1) sRes = sRes +"$"+ sTag +"$";
                    }
                    sTag="";
                }
                else
                {
                    sTag = sTag+c;
                }
            }
        }

        if (sTag.length()>0)
        {
            if (sRes.indexOf("$"+sTag+"$")==-1) sRes = sRes + "$" + sTag +"$";
        }

        return sRes;
    }
}
