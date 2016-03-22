/*
 * File    : FieldDefDecimal.java
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
 * Class that represents the Field Definition of type 'decimal'
 * @author ogalzorriz
 * @version 1.0
 */
public class FieldDefDecimal extends FieldDef{
    
    /**
     * the Minimum value property
     */
    protected float min;
    /**
     * the Maximum value property
     */
    protected float max;
    /**
     * The Default value property
     */
    protected float defaultValue;
    /**
     * The Unit value property
     */
    protected String unit;
       
    /**
     * Creates a new FieldDefDecimal
     */
    public FieldDefDecimal() 
    {
        type="decimal";
        min=(float)0.0;
        max=(float)10.0;
        defaultValue=0;
        unit="";
    }
    
    /**
     * Creates a new FieldDefDecimal
     * @param sName the Name of the FieldDef
     * @param sType the Type of the FieldDef
     * @param fMin the Minium value property
     * @param fMax the Max value property
     * @param fDefaultValue the Default value property
     * @param sUnit the Unit value property
     */
    public FieldDefDecimal(String sName, String sType, float fMin, float fMax, float fDefaultValue, String sUnit) 
    {
        super(sName,sType);
        min=fMin;
        max=fMax;
        defaultValue=fDefaultValue;
        unit=sUnit;
    }
        
    /**
     * Sets the Unit property
     * @param sUnit the Unit
     */
    public void setUnit(String sUnit)
    {
        unit = sUnit;
    }
    
    /**
     * Returns the Unit property
     * @return the Unit
     */
    public String getUnit()
    {
        return unit;
    }
    
    /**
     * Sets the Minmium value property
     * @param fMin the Minmium
     */
    public void setMin(float fMin)
    {
        min = fMin;
    }
    
    /**
     * Returns the Minimum value property
     * @return the Minimum
     */
    public float getMin()
    {
        return min;
    }
    
    /**
     * Sets the Maximum value property
     * @param fMax the Maximum
     */
    public void setMax(float fMax)
    {
        max = fMax;
    }
    
    /**
     * Returns the Maximum value property
     * @return the Maximum
     */
    public float getMax()
    {
        return max;
    }
    
    /**
     * Sets the Default value property
     * @param fDefaultValue the Default
     */
    public void setDefaultValue(float fDefaultValue)
    {
        defaultValue = fDefaultValue;
    }
    
    /**
     * Returns the Default value property
     * @return the Default
     */
    public String getDefaultValue()
    {
        return ""+defaultValue;
    }
    
    /**
     * Calls the parent method to fill the Name and the Type of the FieldDef and fills the properties (min,max,defaultValue,unit)
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        super.fill(se);
        
        Iterator It=null;
        
        It=se.getChildElements(soapFactory.createName("min"));
        String sMin = ((SOAPElement)It.next()).getValue();
        sMin = sMin.trim();
        min=Float.parseFloat(sMin);
        
        It=se.getChildElements(soapFactory.createName("max"));
        String sMax = ((SOAPElement)It.next()).getValue();
        sMax = sMax.trim();
        max=Float.parseFloat(sMax);
        
        It=se.getChildElements(soapFactory.createName("defaultValue"));
        String sDefaultValue = ((SOAPElement)It.next()).getValue();
        sDefaultValue = sDefaultValue.trim();
        defaultValue=Float.parseFloat(sDefaultValue);
        
        It=se.getChildElements(soapFactory.createName("unit"));
        unit = ((SOAPElement)It.next()).getValue();
        if (unit == null) unit="";
    }

    /**
     * Add the propeties (min,max,defaultValue,unit) to the given SOAPElement containing the XML representation of the FieldDef
     * @param se th SOAPElement containing the XML representation of the FieldDef 
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs 
     */
    public void addPropertiesToXml(SOAPElement se) throws SOAPException            
    {
        SOAPElement seMin,seMax,seDefaultValue,seUnit;
            
        seMin  = soapFactory.createElement("min");
        seMin.addTextNode(""+min);
        se.addChildElement(seMin);
        
        seMax  = soapFactory.createElement("max");
        seMax.addTextNode(""+max);
        se.addChildElement(seMax);
        
        seDefaultValue  = soapFactory.createElement("defaultValue");
        seDefaultValue.addTextNode(""+defaultValue);
        se.addChildElement(seDefaultValue);
        
        seUnit  = soapFactory.createElement("unit");
        seUnit.addTextNode(unit);
        se.addChildElement(seUnit);
        if (unit == null) unit="";
    }
    
    /**
     * Sets the properties of this FieldDef separated by '|'
     * @param sProperties the properties
     */
    public void setProperties(String sProperties)
    {
        int iBegin = 0;
        
        int iEnd = sProperties.indexOf("|",iBegin);
        min = Float.parseFloat(sProperties.substring(iBegin,iEnd));
        
        iBegin = iEnd+1;
        
        iEnd = sProperties.indexOf("|", iBegin);
        max = Float.parseFloat(sProperties.substring(iBegin,iEnd));
        
        iBegin = iEnd+1;
        
        iEnd = sProperties.indexOf("|", iBegin);
        defaultValue = Float.parseFloat(sProperties.substring(iBegin,iEnd));
        
        iBegin = iEnd+1;
        
        iEnd = sProperties.indexOf("|", iBegin);
        unit = sProperties.substring(iBegin,iEnd);
    }
    
    /**
     * Returns the properties of this FieldDef min|max|defaultValue|unit|
     * @return the properties
     */
    public String getProperties()
    {
        
        return min+"|"+max+"|"+defaultValue+"|"+unit+"|";
    }
    
    /**
     * Returns the properties of this FieldDef in a Vector
     * @return a Vector of {@link Property} containing the propiertes of this FieldDef
     */
    public Vector getVProperties()
    {   
        Vector v= new Vector();
        
        v.add(new Property("minDec",""+min));
        v.add(new Property("maxDec",""+max));
        v.add(new Property("defaultValueDec",""+defaultValue));
        v.add(new Property("unitDec",unit));
    
        return v;
    }
    
    /**
     * Checks if the given Value is a valid decimal and satisfy the properties
     * @param sValue the given decimal to check
     * @return true if the value is a valid decimal and satisfy the properties, else false
     */    
    public boolean checkValue(String sValue)
    {
        float fValue = 0;
        
        sValue = sValue.replace(',','.'); 
        //We accept separation in decimals both (. and ,)
        try
        {
            fValue = Float.parseFloat(sValue);
        }
        catch (NumberFormatException nfe)
        {
            if  (sValue.equals("null")) return true;
            else return false;
        }
        
        return (min<=fValue) && (fValue<=max);
    }
    
    /**
     * Check if the comparator is '=' or '!=' or '<' or '>'
     * @param sComparator the comparator to check
     * @return true if the comparator is '=' or '!=' or '<' or '>', else false
     */
    public boolean checkComparator(String sComparator)
    {
        return (    (sComparator.equals("=")) || (sComparator.equals("!="))
                ||  (sComparator.equals("<")) || (sComparator.equals(">")) ); 
    }
    
    /**
     * Returns the comparators that can be used with this FieldDef '=','!=','<','>'
     * @return a Vector containing the Strings '=','!=','<','>'
     */
    public Vector getComparators()
    {
        Vector vRes = new Vector();
        
        vRes.add("=");
        vRes.add("!=");
        vRes.add("<");
        vRes.add(">");
        
        return vRes;        
    }    
   
}
