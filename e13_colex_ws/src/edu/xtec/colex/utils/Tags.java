/*
 * File    : Tags.java
 * Created : 15-jun-2007 17:56
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

import java.util.Vector;

/**
 * Auxiliar util class with static methods to process the String list of Tags used to describe a Collection
 * @author ogalzorriz
 * @version 1.0
 */
public class Tags {

    /**
     * int constant for MAX_SIZE, the maximum length that can have a String list of Tags for a Collection
     */
    public static int MAX_SIZE = 500;
    /**
     * int constant for MAX_TAG_SIZE, the maximum length that can have a Tag
     */
    public static int MAX_TAG_SIZE = 20;

    /**
     * Encodes a String list of Tags removing the acccents on the chars and putting between every tag the chars '##'. If a tag is repeated it only encodes it one time.
     * @param tagsNoCoded A String list of Tags separated by blank spaces or the char ','
     * @return a String list of Tags encoded, it starts and ends with the char '#', and between every tag the chars '##'
     */
    public static String encode(String tagsNoCoded)
    {
        String sRes = "#";
        String sTag = "";
        Vector vRes = new Vector();
        char c;

        tagsNoCoded = tagsNoCoded.toLowerCase();

        for (int i=0; i<tagsNoCoded.length();i++)
        {
            c = tagsNoCoded.charAt(i);

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

                case '#': break;

                default:    if (c == ',' || c==' ')
                {
                    if (sTag.length()>0)
                    {
                        if (sTag.length()>MAX_SIZE) sTag = sTag.substring(0,MAX_SIZE);
                        if (sRes.indexOf("#"+sTag+"#")==-1) sRes = sRes +"#"+ sTag +"#";
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
            if (sRes.indexOf("#"+sTag+"#")==-1) sRes = sRes + "#" + sTag +"#";
        }

        if (sRes.length()>MAX_SIZE)
        {
            sRes = sRes.substring(0,500);
            sRes = sRes.substring(0,sRes.lastIndexOf('#'));
        }

        return sRes;
    }

    /**
     * Decodes a String list of Tags removing the first and last char and replacing every '##' for a blank space
     * @param tagsEncoded a String list of Tags encoded, it starts and ends with the char '#', and between every tag the chars '##'
     * @return a String list of Tags separated by blank spaces
     */
    public static String decode(String tagsEncoded)
    {
        String sTags;

        if (tagsEncoded.length()<=1)
        {
            return "";
        }
        else
        {
            sTags = tagsEncoded.substring(2,tagsEncoded.length()-1);
            return sTags.replaceAll("##"," ");
        }
    }

    /**
     * Coverts a String list of Tags Encoded into a Vector containing every String Tag
     * @param tagsEncoded a String list of Tags encoded, it starts and ends with the char '#', and between every tag the chars '##'
     * @return a Vector containing String Tags
     */
    public static Vector toVector(String tagsEncoded)
    {
        String[] sTags = tagsEncoded.substring(0,tagsEncoded.length()-1).split("##");

        Vector vTags = new Vector();

        for (int i=1;i<sTags.length;i++)
        {
            vTags.add(sTags[i]);
        }

        return vTags;
    }

    /**
     * Compares two String list of Tags and creates a new Vector of String Tags containing the Tags that appear on the newTags String and not in the srcTags
     * @param srcTags the actual String list of Tags on a collection, encoded by chars '##'
     * @param newTags the new String list of Tags to replace the srcTags, encoded by chars '##'
     * @return a Vector of String Tags containing the Tags that appear on the newTags String and not in the srcTags
     */
    public static Vector toAdd(String srcTags, String newTags)
    {
        String sTag;

        Vector vTags = new Vector();

        Vector vNew = toVector(newTags);

        for (int i=0; i<vNew.size();i++)
        {
            sTag = (String)vNew.get(i);

            if (srcTags.indexOf("#"+sTag+"#")==-1)
            {
                vTags.add(sTag);
            }
        }

        return vTags;
    }

    /**
     * Compares two String list of Tags and creates a new Vector of String Tags containing the Tags that appear on the srcTags String and not in the newTags
     * @param srcTags the actual String list of Tags on a collection, encoded by chars '##'
     * @param newTags the new String list of Tags to replace the srcTags, encoded by chars '##'
     * @return a Vector of String Tags containing the Tags that appear on the srcTags String and not in the newTags
     */
    public static Vector toDelete(String srcTags, String newTags)
    {
        String sTag;

        Vector vTags = new Vector();

        Vector vSrc = toVector(srcTags);

        for (int i=0; i<vSrc.size();i++)
        {
            sTag = (String)vSrc.get(i);

            if (newTags.indexOf("#"+sTag+"#")==-1)
            {
                vTags.add(sTag);
            }
        }

        return vTags;
    }

    /**
     * Replaces in a String list of Tags a Tag for a new list of Tags
     * @param srcTags the actual String list of Tags on a collection, encoded by chars '##'
     * @param oldTag the String Tag to replace
     * @param newTags the new String list of Tags to replace by, encoded by chars '##'
     * @return a String list of Tags with the tag replaced
     */
    public static String replace(String srcTags, String oldTag, String newTags)
    {
        String sTag;

        String sRes = srcTags;

        sRes = sRes.replaceFirst(oldTag,"");

        Vector vNew = toVector(newTags);

        for (int i=0; i<vNew.size();i++)
        {
            sTag = (String)vNew.get(i);

            if (srcTags.indexOf("#"+sTag+"#")==-1)
            {
                sRes = sRes +"#"+sTag+"#";
            }
        }

        return sRes;
    }
}