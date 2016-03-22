/*
 * File    : Attachment.java
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

/**
 * Auxiliar class to use with the Attachments files
 * @author ogalzorriz
 * @version 1.0
 */
public class Attachment {
    
    
    /**
     * The url of the file attachment
     */
    private String url;
    /**
     * The original name of the file attachment
     */
    private String originalName;
    
    /**
     * Creates a new Attachment
     */
    public Attachment() 
    {
    }
    
    /**
     * Sets the url
     * @param sUrl the Url
     */
    public void setUrl(String sUrl)
    {
        url=sUrl;
    }
    
    /**
     * Return the url
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }
    
    /**
     * Sets the originalName
     * @param sOriginalName the original name
     */
    public void setOriginalName(String sOriginalName)
    {
        originalName=sOriginalName;
    }
    
    /**
     * Returns the originalName
     * @return the originalName
     */
    public String getOriginalName()
    {
        return originalName;
    }
}
