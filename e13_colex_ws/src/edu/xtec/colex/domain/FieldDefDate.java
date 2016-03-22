/*
 * File    : FieldDefDate.java
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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
     
/**
 * Class that represents the Field Definition of type 'date'
 * @author ogalzorriz
 * @version 1.0
 */
public class FieldDefDate extends FieldDef{
    
    /**
     * Creates a new FieldDefDate
     */
    public FieldDefDate() 
    {
        type="date";
    }

    /**
     * Creates a new FieldDefDate 
     * @param sName the name of the Field
     * @param sType the type of the Field
     */
    public FieldDefDate(String sName, String sType) 
    {
        super(sName,sType);
    }
    
    /**
     * Calls the parent method to fill the Name and the Type of the FieldDef
     * @param se the SOAPElement containing the XML representation of the FieldDef
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void fill(SOAPElement se) throws SOAPException
    {
        super.fill(se);
    }
    
    /**
     * Do nothing because there are no propierties on this FieldDef
     * @param se the given SOAPElement where to add the XML representation of the properties
     * @throws javax.xml.soap.SOAPException when a SOAPException error occurs
     */
    public void addPropertiesToXml(SOAPElement se) throws SOAPException            
    {
    }
    
    /**
     * Do nothing because there are no propierties on this FieldDef
     * @param sProperties the properties
     */
    public void setProperties(String sProperties)
    {
    }
    
    /**
     * Returns an empty String "" because there are no propierties on this FieldDef
     * @return an empty String ""
     */
    public String getProperties()
    {
        return "";
    }
    
    /**
     * Returns an empty Vector because there are no propierties on this FieldDef
     * @return an empty Vector
     */
    public Vector getVProperties()
    {   
        return new Vector();
    }
    
    /**
     * Returns the String "1900-01-01"
     * @return the String "1900-01-01"
     */
    public String getDefaultValue()
    {
        return "1900-01-01";
    }
    
    /**
     * Checks if the given Value is a valid date
     * @param sValue the given date to check
     * @return true if the value is a valid date, else false
     */
    public boolean checkValue(String sValue)
    {    
        String dt = getDD(sValue)+"/"+getMM(sValue)+"/"+getYYYY(sValue);
        
        try 
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
            df.setLenient(false);
            Date dt2 = df.parse(dt);
            return true;
        }
        catch (ParseException e) 
        {
            return false;
        }
        catch (IllegalArgumentException e) 
        {
            return false;
        }
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
    
    /**
     * Extract the day from the given Date
     * @param sValue the Date
     * @return a String containing the day of the date
     */
    public static String getDD(String sValue)
    {
        int iPos = sValue.lastIndexOf("-");
        
        if (iPos==-1) return "";
        else return sValue.substring(iPos+1,sValue.length());
    }
    
    /**
     * Extract the month from the given Date
     * @param sValue the Date
     * @return a String containing the month of the date
     */
    public static String getMM(String sValue)
    {
        int firstPos = sValue.indexOf("-");
        
        if (firstPos==-1) return "";
        else
        {
            int secondPos = sValue.indexOf("-", firstPos+1);
            
            if (secondPos==-1) return "";
            else return sValue.substring(firstPos+1,secondPos);
        }
    }
    
    /**
     * Extract the year from the given Date
     * @param sValue the Date
     * @return a String containing the year of the date
     */
    public static String getYYYY(String sValue)
    {
        int iPos = sValue.indexOf("-");
        
        if (iPos==-1) return "";
        else return sValue.substring(0,iPos);
    }
}
