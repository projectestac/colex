/*
 * File    : FieldDefImage.java
 * Created : 27-sep-2005 12:24
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
import java.util.Iterator;
import java.util.Vector;
/**
 * Class that represents the Field Definition of type 'image'
 * @author ogalzorriz
 * @version 1.0
 */

public class FieldDefImage extends FieldDef{
    
    /**
     * the Height value property. It is the height that is going to be used to show the images on the record.jsp
     */
    protected int height;
    
    /**
     * the Height value property. It is the width that is going to be used to show the images on the record.jsp
     */
    protected int width;
    
   /**
     * Creates a new FieldDefImage
     */
    public FieldDefImage() 
    {
        type="image";
        height=150;
        width=150;
    }
    
    /**
     * Creates a new FieldDefImage
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     * @param iHeight the Height value property
     * @param iWidth the Width value property
     */
    public FieldDefImage(String sName, String sType, int iHeight, int iWidth) 
    {
        super(sName,sType);
        height=iHeight;
        width=iWidth;
    }
    
    /**
     * Sets the Height property
     * @param iHeight the Height
     */
    public void setHeight(int iHeight)
    {
        height = iHeight;
    }
        
    /**
     * Returns the Height property
     * @return the Height
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * Sets the Width property
     * @param iWidth the Width
     */
    public void setWidth(int iWidth)
    {
        width = iWidth;
    }
    
    /**
     * Returns the Width property
     * @return the Width
     */
    public int getWidth()
    {
        return width;
    }
        
    /**
     * Calls the parent method to fill the Name and the Type of the FieldDef and fills the properties (height,width)
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        super.fill(se);
        
        Iterator It=null;
        
        It=se.getChildElements(soapFactory.createName("height"));
        String sHeight = ((SOAPElement)It.next()).getValue();
        sHeight = sHeight.trim();
        
        if (sHeight.startsWith("+")) sHeight = sHeight.substring(1);
        height=Integer.parseInt(sHeight);
        
        It=se.getChildElements(soapFactory.createName("width"));
        String sWidth = ((SOAPElement)It.next()).getValue();
        sWidth = sWidth.trim();
        if (sWidth.startsWith("+")) sWidth = sWidth.substring(1);
        width=Integer.parseInt(sWidth);
    }

    /**
     * Add the propeties (height,width) to the given SOAPElement containing the XML representation of the FieldDef
     * @param se th SOAPElement containing the XML representation of the FieldDef 
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs 
     */
    public void addPropertiesToXml(SOAPElement se) throws SOAPException            
    {
        SOAPElement seHeight,seWidth;
            
        seHeight  = soapFactory.createElement("height");
        seHeight.addTextNode(""+height);
        se.addChildElement(seHeight);
        
        seWidth  = soapFactory.createElement("width");
        seWidth.addTextNode(""+width);
        se.addChildElement(seWidth);
    }
    
    /**
     * Sets the properties of this FieldDef separated by '|'
     * @param sProperties the properties
     */
    public void setProperties(String sProperties)
    {
        int iBegin = 0;
        
        int iEnd = sProperties.indexOf("|",iBegin);
        
        String sHeight = sProperties.substring(iBegin,iEnd);
        if (sHeight.startsWith("+")) sHeight = sHeight.substring(1);                
        height = Integer.parseInt(sHeight);
        
        iBegin = iEnd+1;
        iEnd = sProperties.indexOf("|", iBegin);
        
        String sWidth = sProperties.substring(iBegin,iEnd);
        if (sWidth.startsWith("+")) sWidth = sWidth.substring(1);
        width = Integer.parseInt(sWidth);
        
    }
    
    /**
     * Returns the properties of this FieldDef height|width|
     * @return the properties
     */
    public String getProperties()
    {
        return height+"|"+width+"|";
    }
    
    /**
     * Returns the properties of this FieldDef in a Vector
     * @return a Vector of {@link Property} containing the propiertes of this FieldDef
     */
    public Vector getVProperties()
    {   
        Vector v= new Vector();
        
        v.add(new Property("height",""+height));
        v.add(new Property("width",""+width));
    
        return v;
    }  
    
    /**
     * Returns the string "null", meaning that there is not yet an image
     * @return the string "null"
     */
    public String getDefaultValue()
    {
        return "null";
    }
    
    /**
     * Returns true because we check the file extension in the jsp level
     * @param sValue the given image to check
     * @return true
     */
    public boolean checkValue(String sValue)
    {
        return true;
    }
    
    /**
     * Check if the comparator is '=' or '!='
     * @param sComparator the comparator to check
     * @return true if the comparator is '=' or '!=', else false
     */
    public boolean checkComparator(String sComparator)
    {
        return ((sComparator.equals("=")) || (sComparator.equals("!="))); 
    }
    
    /**
     * Returns the comparators that can be used with this FieldDef '=','!='
     * @return a Vector containing the Strings '=','!='
     */
    public Vector getComparators()
    {
        Vector vRes = new Vector();
        
        vRes.add("=");
        vRes.add("!=");
                
        return vRes;        
    }
    
    /**
     * Returns false because this fieldDef has not info associated
     * @return false
     */
    public boolean isInfo()
    {
        return false;
    }
    
    /**
     * Returns true because this fieldDef has a file attachment
     * @return true
     */
    public boolean isAttachment()
    {
        return true;
    }
    
    /**
     * Returns false because this fieldDef is not sortable
     * @return false
     */
    public boolean isSortable()
    {
        return false;
    } 
}
