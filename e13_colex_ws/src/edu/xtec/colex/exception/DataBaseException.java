/*
 * File    : DataBaseException.java
 * Created : 18-oct-2005 17:56
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
package edu.xtec.colex.exception;

/**
 * Class that processes all the DataBaseExceptions that occurs on the system. A DataBaseException occurs the parameters used calling a Web Service operation do not agree with the requeriments of the system. For example, when a user whants to delete a collection and it does not exists (NO_EXISTS_COLLECTION)
 * @author ogalzorriz
 * @version 1.0
 */
public class DataBaseException extends Exception{
    
    /**
     * when trying to create a collection and exists another with the same name
     */
    public static final int EXISTS_COLLECTION = 1;
    /**
     * when trying to access to a collection that does not exist
     */
    public static final int NO_EXISTS_COLLECTION = 2;
    /**
     * when a user has no collections
     */
    public static final int NO_COLLECTIONS = 3;
    /**
     * when a collection is empty
     */
    public static final int EMPTY_COLLECTION = 4;
    /**
     * when a collection has no field definitions
     */
    public static final int NO_FIELDS_DEFS = 5;
    /**
     * when trying to add a new field definition and exists another one with the same name
     */
    public static final int EXISTS_FIELD_DEF = 6;
    /**
     * when trying to access to a field definition and it does not exist
     */
    public static final int NO_EXISTS_FIELD_DEF = 7;
    /**
     * when trying to modify the properties of a field definition and it is not possible because there would be fields that would not satisfy the new properties
     */
    public static final int IMPOSSIBLE_MODIFY_PTIES = 8;
    /**
     * for the moment it is not possible to modify the type of a field
     */
    public static final int IMPOSSIBLE_MODITY_TYPE = 9;
    /**
     * when trying to add or modify a record and its structure does not equal as the structure of its collection
     */
    public static final int NO_FIELD_DEF_MATCH = 10;
    /**
     * when a field of a record does not satisfy the properties ot its field definition
     */
    public static final int NO_FIELD_CHECK_PTIES = 11;
    /**
     * when trying to add a record with more attachments than are on its definition
     */
    public static final int NO_ATTACHMENT_MATCH = 12;
    /**
     * when the attachment of a record is not valid
     */
    public static final int INVALID_FILE_ATTACHMENT = 13;
    /**
     * when a query to search on a collection is not well formed : a field does not exists, a comparator not well used...
     */
    public static final int MALFORMED_QUERY = 14;
    /**
     * when seraching on a collection and no records have found
     */
    public static final int NO_RECORD_FOUND = 15;
    /**
     * when trying to add an attachment and the user has not enough disk quota to store it
     */
    public static final int DISK_QUOTA_EXCEDED = 16;

    /**
     * when trying to import a collection that has a fieldDefinition name repeated
     */
    public static final int REPEATED_FIELD_NAME = 17;
    /**
     * when the User is not validated
     */
    public static final int NO_VALID_USER = 18;
    /**
     * when calling to a web service operation that does not exists on the server
     */
    public static final int NO_FOUND_SERVICE = 19;
    /**
     * when trying to access to a collection that is not public
     */
    public static final int NO_PUBLIC_COLLECTION = 20;
    /**
     * when trying to import a collection and the file is not valid
     */
    public static final int NO_VALID_IMPORT_FILE = 21;
    /**
     * when trying to import records into a collection with not same field definitions
     */
    public static final int NO_STRUCTURE_COMPATIBLE = 22;
    /**
     * when trying to add a guest to a collection that has been added before
     */
    public static final int EXISTS_GUEST = 23;
    /**
     * when trying to access to a guest and it does not exist
     */
    public static final int NO_EXISTS_GUEST = 24;
    /**
     * when trying to access to a collection and the user has a lower permission level
     */
    public static final int NO_PERMISSION = 25;
    /**
     * when trying to delete a record and it does not exist
     */
    public static final int NO_EXISTS_DELETE_RECORD = 26;
    /**
     * when trying to modify a record and it does not exist
     */
    public static final int NO_EXISTS_MODIFY_RECORD = 27;
    
    /**
     * the code of the DataBaseException
     */
    int code;
    /**
     * the fieldName to store wich one is the Field in case of "NO_FIELD_CHECK_PTIES"
     */
    String fieldName;
    
    /**
     * Creates a new DataBaseException with the given Code
     * @param iCode the Code of the DataBaseException
     */
    public DataBaseException(int iCode)
    {
        code=iCode;
    }
    
    /**
     * Creates a new DataBaseException with the given Code and FieldName. We use that method to create a DataBaseExeption of "NO_FIELD_CHECK_PTIES" to store wich one is the Field that not satisfies the properties of its FieldDefinition
     * @param iCode the Code of the DataBaseException
     * @param sFieldName the FieldName where the
     */
    public DataBaseException(int iCode, String sFieldName)
    {
        code=iCode;
        fieldName=sFieldName;
    }
    
    /**
     * Returns the String textual representation of this DataBaseException
     * @return the String text of this DataBaseException
     */
    public String getMessage()
    {
        String sRes;
        
        switch(code)
        {
            case EXISTS_COLLECTION:         sRes="EXISTS_COLLECTION";break;
            case NO_EXISTS_COLLECTION:      sRes="NO_EXISTS_COLLECTION";break;
            case NO_COLLECTIONS:            sRes="NO_COLLECTIONS";break;
            case EMPTY_COLLECTION:          sRes="EMPTY_COLLECTION";break;
            case NO_FIELDS_DEFS:            sRes="NO_FIELDS_DEFS";break;
            case EXISTS_FIELD_DEF:          sRes="EXISTS_FIELD_DEF";break;
            case NO_EXISTS_FIELD_DEF:       sRes="NO_EXISTS_FIELD_DEF";break;
            case IMPOSSIBLE_MODIFY_PTIES:   sRes="IMPOSSIBLE_MODIFY_PTIES";break;
            case IMPOSSIBLE_MODITY_TYPE:    sRes="IMPOSSIBLE_MODITY_TYPE";break;
            case NO_FIELD_DEF_MATCH:        sRes="NO_FIELD_DEF_MATCH";break;
            case NO_FIELD_CHECK_PTIES:      sRes="NO_FIELD_CHECK_PTIES*"+fieldName;break;
            case NO_ATTACHMENT_MATCH:       sRes="NO_ATTACHMENT_MATCH";break;
            case INVALID_FILE_ATTACHMENT:   sRes="INVALID_FILE_ATTACHMENT";break;
            case MALFORMED_QUERY:           sRes="MALFORMED_QUERY";break;
            case NO_RECORD_FOUND:           sRes="NO_RECORD_FOUND";break;
            case DISK_QUOTA_EXCEDED:        sRes="DISK_QUOTA_EXCEDED";break;
            case REPEATED_FIELD_NAME:       sRes="REPEATED_FIELD_NAME";break;
            case NO_VALID_USER:             sRes="NO_VALID_USER";break;
            case NO_FOUND_SERVICE:          sRes="NO_FOUND_SERVICE";break;
            case NO_PUBLIC_COLLECTION:      sRes="NO_PUBLIC_COLLECTION";break;
            case NO_VALID_IMPORT_FILE:      sRes="NO_VALID_IMPORT_FILE";break;
            case NO_STRUCTURE_COMPATIBLE:   sRes="NO_STRUCTURE_COMPATIBLE";break;
            /* Related to share collections */
            case EXISTS_GUEST:              sRes="EXISTS_GUEST";break;
            case NO_EXISTS_GUEST:           sRes="NO_EXISTS_GUEST";break;
            case NO_PERMISSION:             sRes="NO_PERMISSION";break;
            case NO_EXISTS_DELETE_RECORD:   sRes="NO_EXISTS_DELETE_RECORD";break;
            case NO_EXISTS_MODIFY_RECORD:   sRes="NO_EXISTS_MODIFY_RECORD";break;
            
            default :                       sRes="NO_DEFINED_ERROR";break;
        }
        return sRes;
    }
}
