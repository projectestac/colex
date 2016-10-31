/*
 * File    : DataBase.java
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
package edu.xtec.colex.admin;

import edu.xtec.colex.domain.*;
import edu.xtec.colex.exception.DataBaseException;
import edu.xtec.colex.exception.ServerException;
import edu.xtec.colex.utils.Utils;
import edu.xtec.util.db.ConnectionBean;
import edu.xtec.util.db.ConnectionBeanProvider;
import org.apache.log4j.Logger;

import javax.xml.soap.AttachmentPart;
import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

/**
 * Database class that interacts with the DataBase application
 * @author ogalzorriz
 * @version 1.0
 */
public class DataBase 
{
    /**
     * Path to database configuration files
     */
    public final static String DBCONF_PATH = "/edu/xtec/colex/admin/";
    /**
     * Database file configuration
     */
    public final static String DBCONF_FILE = "adminColex.properties";
    /**
     * Database configuration properties
     */
    private static Properties pDB;
    /**
     * The ConnectionBeanProvider used to connect to the database
     */
    protected static ConnectionBeanProvider broker;
    /**
     * The ConnectionBean used to connect to the database
     */
    protected ConnectionBean c;
    
    static final Logger logger = Logger.getLogger(edu.xtec.colex.server.DataBase.class);

    /**
     *  Construct an empty DataBase object
     */
    public DataBase() 
    { 
    }
    
    /**
     * Returns the DataBase configuration properties
     * @return the DataBase configuration properties
     * @throws ServerException when an internal Exception error occurs
     */
    public static Properties getDBProperties() throws ServerException
    {
        if (pDB==null){
            pDB = new Properties();
            try{
                InputStream isl = edu.xtec.colex.server.DataBase.class.getResourceAsStream (DBCONF_PATH+DBCONF_FILE);

                if (isl!=null){
                    pDB.load(isl);
                }
                isl.close();

                File f = new File(System.getProperty("user.home"), DBCONF_FILE);
                if(f.exists()){
                    FileInputStream is=new FileInputStream(f);
                    pDB.load(is);
                    is.close();
                }
            }
            catch (FileNotFoundException f) {throw new ServerException(f);}
            catch (IOException e) {throw new ServerException(e);}
        }

        return pDB;
    }

    /**
     * Returns a ConnectionBeanProvider to connect to the DataBase
     * @return the ConnectionBeanProvider
     * @throws ServerException when an internal Exception error occurs
     */
    protected ConnectionBeanProvider getConnectionBeanProvider() throws ServerException
    {
        try
        {
            if(broker == null)
            { // Only created by first servlet to call
                broker = ConnectionBeanProvider.getConnectionBeanProvider(true, getDBProperties());
            }
        }
        catch (Exception e){throw new ServerException(e);}
        return broker;
    }

    /**
     * Returns a ConnectionBean to connect to the DataBase
     * @return the ConnectionBean
     * @throws ServerException when an internal Exception error occurs
     */
    protected ConnectionBean getConnection() throws ServerException
    {
        if (c==null)
        {
            try
            {
                c = getConnectionBeanProvider().getConnectionBean();
                c.getConnection().setAutoCommit(true);
            }
            catch (ServerException e){throw new ServerException(e);}
            catch (SQLException e){throw new ServerException(e);}
        }
        return c;
    }

    /**
     * Free the connection to the database
     */
    public void freeConnection()
    {
        if (c!=null && broker!=null)
        {
            broker.freeConnectionBean(c);
            c=null;
        }
    }

    /**
     * Creates a new empty Collection (it has no Fields Defs)
     * @param uRequest the User owner of the new Collection cRequest
     * @param cRequest the new Collection to create
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void createCollection(User uRequest,Collection cRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;

        try
        {
            conn = getConnection().getConnection();

            if (!existsUser(conn,uRequest))
            {
                boolean bOk = Utils.createDir(getDBProperties().getProperty("dir.files"),uRequest);
                if (!bOk) throw new ServerException(new FileNotFoundException("Could not create the user database folder, check the property \"dir.files\" on colex_db.properties and colex_ws.properties"));
                insertUser(conn,uRequest);
            }

            if (existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.EXISTS_COLLECTION);

            cRequest.setTags(edu.xtec.colex.utils.Tags.encode(cRequest.getTags()));

            iniTransaction(conn);

            try
            {
                insertCollection(conn,uRequest,cRequest);
            }
            catch (SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            Utils.createDir(getDBProperties().getProperty("dir.files"),uRequest,cRequest);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Creates a new empty Collection with the Fields Defs defined
     * @param uRequest the User owner of the new Collection cRequest
     * @param cRequest the new Collection to create
     * @param vFieldsDefs Vector containing the Fields Defs to create the cRequest
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void createCollectionDefined(User uRequest,Collection cRequest,Vector vFieldsDefs) throws DataBaseException,ServerException
    {
        Connection conn = null;

        try
        {
            conn = getConnection().getConnection();

            if (!existsUser(conn,uRequest))
            {
                boolean bOk = Utils.createDir(getDBProperties().getProperty("dir.files"),uRequest);
                if (!bOk) throw new ServerException(new FileNotFoundException("Could not create the user database folder, check the property \"dir.files\" on colex_db.properties and colex_ws.properties"));
                insertUser(conn,uRequest);
            }

            if (existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.EXISTS_COLLECTION);

            String sTags = edu.xtec.colex.utils.Tags.encode(cRequest.getTags());

            cRequest.setTags(sTags);

            Vector vTags = edu.xtec.colex.utils.Tags.toVector(sTags);

            cRequest.setNumRecords(0); //We need to set 0 as numRecords

            iniTransaction(conn);

            try
            {
                insertCollection(conn,uRequest,cRequest);
                insertFieldsDefs(conn,cRequest,vFieldsDefs);
                updateCollection(conn,cRequest);
                if (cRequest.getIsPublic()==1) addTags(conn,vTags);
            }

            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            Utils.createDir(getDBProperties().getProperty("dir.files"),uRequest,cRequest);
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }


    /**
     * Checks if the Fields Defs of the Vector vFiledsDefs are compatible (the same name and the same type)
     * @param uRequest if oRequest is null the User owner of Collection, else the User that interacts with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection
     * @param vFieldsDefs the Vector containing the Fields Defs to check
     * @return true if the fields defs of the Collection are the same of the fields defs of the Vector vFieldsDefs, els returns false
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public boolean isStructureCompatible(User uRequest, Owner oRequest, Collection cRequest,Vector vFieldsDefs) throws DataBaseException,ServerException
    {
        Connection conn = null;

        try
        {
            conn = getConnection().getConnection();

            if (oRequest==null)
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.EXISTS_COLLECTION);
            }
            else
            {
                if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.EXISTS_COLLECTION);
            }

            Vector vFieldsDefsDest = selectFieldsDefs(conn, cRequest);


            if (vFieldsDefs.size()!=vFieldsDefsDest.size()) return false;

            FieldDef fdImport,fdDestination;

            for (int i=0;i<vFieldsDefs.size();i++)
            {
                fdImport = (FieldDef) vFieldsDefs.get(i);
                fdDestination = (FieldDef) vFieldsDefsDest.get(i);

                if (!fdDestination.isCompatible(fdImport)) return false;
            }

        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }

        return true;
    }

    /**
     * Changes the properties (name,isPublic,Description,Tags) of a Collection
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection to change the name
     * @param cNewCollection the Collection containing the new name
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void modifyCollection(User uRequest,Collection cRequest,Collection cNewCollection) throws DataBaseException,ServerException
    {
        Connection conn = null;

        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            if (!(cRequest.getName().equals(cNewCollection.getName())))
            {
                if (existsCollection(conn, uRequest,cNewCollection)) throw new DataBaseException(DataBaseException.EXISTS_COLLECTION);
            }

            iniTransaction(conn);

            int iWasPublic = cRequest.getIsPublic();
            int iIsPublic = cNewCollection.getIsPublic();
            String sOldTags = cRequest.getTags();

            cRequest.setName(cNewCollection.getName());
            cRequest.setIsPublic(cNewCollection.getIsPublic());
            cRequest.setDescription(cNewCollection.getDescription());
            cRequest.setTags(cNewCollection.getTags());

            Vector vAdd = new Vector();
            Vector vDelete = new Vector();

            if ((iWasPublic == 1) && (iIsPublic == 1 ))
            {
                vAdd = edu.xtec.colex.utils.Tags.toAdd(sOldTags,cNewCollection.getTags());
                vDelete = edu.xtec.colex.utils.Tags.toDelete(sOldTags,cNewCollection.getTags());
            }
            else if ((iWasPublic == 1) && (iIsPublic == 0 ))
            {
                vDelete = edu.xtec.colex.utils.Tags.toVector(sOldTags);
            }
            else if ((iWasPublic == 0) && (iIsPublic == 1 ))
            {
                vAdd = edu.xtec.colex.utils.Tags.toVector(cNewCollection.getTags());
            }
            else if ((iWasPublic == 0) && (iIsPublic == 0 ))
            {

            }

            try
            {
                updateCollection(conn,cRequest);
                addTags(conn,vAdd);
                deleteTags(conn,vDelete);
            }
            catch (SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Returns a Collection containing all the information about it
     * @param uRequest if oRequest is null the User owner of Collection, else the User that interacts with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     * @return the Collection containing the information
     */
    public Collection getCollectionInfo(User uRequest, Owner oRequest,Collection cRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;

        Collection cResponse = new Collection("");

        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest))
                {
                    throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
                }

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux)) && (!isPublic(conn,oRequest,cRequest)))
                {
                        throw new DataBaseException(DataBaseException.NO_PUBLIC_COLLECTION);
                }

            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }

            cResponse = cRequest;
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }

        return cResponse;
    }

    /**
     * Returns all the Collections owned by the given User
     * @param uRequest the User to list the Collections
     * @return a Vector containing the Collections of the User uRequest
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public Vector listCollections(User uRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        String sUser = uRequest.getUserId();

        ResultSet rsRes = null;
        Collection cAux;
        Vector vRes = new Vector();

        try
        {
            conn = getConnection().getConnection();

            PreparedStatement psStmt = conn.prepareStatement("SELECT nameCollection,isPublic,description,tags,created from collection c " +
                                    "WHERE c.idUser = ? ORDER BY nameCollection" );

            psStmt.setString(1,sUser);

            rsRes = psStmt.executeQuery();

            if (!rsRes.next())
            {
                if (rsRes!=null) rsRes.close();
                if (psStmt!=null) psStmt.close();

                throw new DataBaseException(DataBaseException.NO_COLLECTIONS);
            }

            int iIsPublic;

            do
            {
                cAux = new Collection(rsRes.getString(1));

                cAux.setIsPublic(rsRes.getInt(2));
                cAux.setDescription(rsRes.getString(3));
                cAux.setTags(rsRes.getString(4));

                if (cAux.getDescription()==null) cAux.setDescription("");
                if (cAux.getTags()==null) cAux.setTags("");
                //Because in Oracle String empty is null

                cAux.setCreated(rsRes.getDate(5));

                vRes.add(cAux);
            }
            while (rsRes.next());

            if (rsRes!=null) rsRes.close();
            if (psStmt!=null) psStmt.close();
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }

        finally
        {
            freeConnection();
        }

        return vRes;
    }

    /**
     * Deletes the given Collection of the given User
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection to delete
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void deleteCollection(User uRequest,Collection cRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFiles;
        int idCollection;

        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn, uRequest, cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            String sTags = cRequest.getTags();
            Vector vTags = edu.xtec.colex.utils.Tags.toVector(sTags);

            iniTransaction(conn);
            try
            {
                deleteFields(conn,cRequest, null);
                vFiles = deleteAttachments(conn,cRequest);
                deleteFieldsDefs(conn,cRequest, null);
                deleteRecords(conn,cRequest);
                deleteCollection(conn,cRequest);
                deleteGuests(conn,cRequest);
                deleteLog(conn,cRequest);
                if (cRequest.getIsPublic()==1) deleteTags(conn,vTags);
            }
            catch (SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            eraseFiles(vFiles);
            Utils.deleteDir(getDBProperties().getProperty("dir.files"),uRequest,cRequest);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Adds a new FieldDef to the given Collection and User
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection to add the FieldDef fdRequest
     * @param fdRequest the FieldDef to add to the Collection
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void addFieldDef(User uRequest,Collection cRequest,FieldDef fdRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;

        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            Vector vFieldsDefs = new Vector();
            vFieldsDefs.add(fdRequest);

            if (existsFieldsDefs(conn,cRequest,vFieldsDefs)) throw new DataBaseException(DataBaseException.EXISTS_FIELD_DEF);
            iniTransaction(conn);

            try
            {
                insertFieldsDefs(conn,cRequest,vFieldsDefs);
                updateFields(conn,cRequest,vFieldsDefs);
                updateCollection(conn,cRequest);
            }

            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
           freeConnection();
        }
    }

    /**
     * Deletes the FieldDef of the given Collection and User
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection containing the FieldDef fdRequest
     * @param fdRequest the FieldDef to delete from the Collection cRequest
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void deleteFieldDef(User uRequest,Collection cRequest,FieldDef fdRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFiles;

        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            Vector vFieldsDefs = new Vector();
            vFieldsDefs.add(fdRequest);

            if (!existsFieldsDefs(conn,cRequest,vFieldsDefs)) throw new DataBaseException(DataBaseException.NO_EXISTS_FIELD_DEF);
            iniTransaction(conn);

            try
            {
                deleteFields(conn,cRequest,vFieldsDefs);
                vFiles = this.deleteAttachments(conn,fdRequest);
                deleteFieldsDefs(conn,cRequest,vFieldsDefs);

                if (cRequest.getNumFieldsDefs() == 0)
                {
                    deleteRecords(conn, cRequest);
                    cRequest.setNumRecords(0);
                }

                updateCollection(conn,cRequest);
            }

            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            eraseFiles(vFiles);
        }

        catch(SQLException sqle)
        {
           throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Modifies the FieldDef of the given Collection. It is only possible to change:
     * - The name of the FieldDef
     * - The properties of the FieldDef if every Field of the given Collection satisfies the new properties
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection containing the OldFieldDef to change
     * @param vOldFD the Vector containing the Field Defs to modify (in this version we only put one element on this Vector)
     * @param vNewFD the Vector containing the Field Defs modified (in this version we only put one element on this Vector)
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void modifyFieldDef(User uRequest, Collection cRequest, Vector vOldFD, Vector vNewFD) throws DataBaseException,ServerException
    {
        Connection conn = null;
        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            if (!existsFieldsDefs(conn,cRequest,vOldFD)) throw new DataBaseException(DataBaseException.NO_EXISTS_FIELD_DEF);

            FieldDef fdOld = (FieldDef) vOldFD.get(0);
            FieldDef fdNew = (FieldDef) vNewFD.get(0);

            if (existsFieldsDefs(conn, cRequest,vNewFD) && !(fdOld.getName().equals(fdNew.getName())))
            {
                throw new DataBaseException(DataBaseException.EXISTS_FIELD_DEF);
            }

            if (!(fdOld.getType().equals(fdNew.getType())))
            {
                throw new DataBaseException(DataBaseException.IMPOSSIBLE_MODITY_TYPE);
            }

            fdNew.setId(fdOld.getId());

            if (!fdNew.getType().equals("select"))
            {
                if (!checkFields(conn,cRequest,fdNew))
                {
                    throw new DataBaseException(DataBaseException.IMPOSSIBLE_MODIFY_PTIES);
                }
            }

            iniTransaction(conn);
            try
            {
                if (fdNew.getType().equals("select")) {updateFieldsSelect(conn,cRequest,fdNew);}
                updateFieldDef(conn,fdNew);
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Move a Field Definition to a higher position
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection containing the FieldDef fdRequest
     * @param fdRequest the FieldDef to modify its position
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void increaseFieldDefPos(User uRequest, Collection cRequest, FieldDef fdRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            Vector vFieldsDefs = new Vector();
            vFieldsDefs.add(fdRequest);

            if (!existsFieldsDefs(conn,cRequest, vFieldsDefs)) throw new DataBaseException(DataBaseException.NO_EXISTS_FIELD_DEF);

            if (!(fdRequest.getPosition()<cRequest.getNumFieldsDefs())) throw new DataBaseException(-1);

            iniTransaction(conn);

            try
            {
                PreparedStatement psStmt = conn.prepareStatement("UPDATE fielddef SET position = ? WHERE idCollection=? AND position=?");

                psStmt.setInt(1,fdRequest.getPosition());
                psStmt.setInt(2,cRequest.getId());
                psStmt.setInt(3,fdRequest.getPosition()+1);
                psStmt.executeUpdate();

                psStmt = conn.prepareStatement("UPDATE fielddef SET position = ? WHERE idFieldDef=?");

                psStmt.setInt(1,fdRequest.getPosition()+1);
                psStmt.setInt(2,fdRequest.getId());
                psStmt.executeUpdate();
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);

        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }


    /**
     * Returns the structure of the Collection into a Vector of FieldDefs
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param cRequest the Collection
     * @param oRequest the Owner of the Collection
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     * @return a Vector containing the Fields Definitions of the Collection
     */
    public Vector getStructure(User uRequest,Collection cRequest,Owner oRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFieldDefs = new Vector();

        String sUser = uRequest.getUserId();
        String sName = cRequest.getName();

        String query="";

        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux)) && (!isPublic(conn,oRequest,cRequest)))
                {
                        throw new DataBaseException(DataBaseException.NO_PUBLIC_COLLECTION);
                }
            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }


            if (cRequest.getNumFieldsDefs()==0) throw new DataBaseException(DataBaseException.NO_FIELDS_DEFS);

            vFieldDefs = selectFieldsDefs(conn,cRequest);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
        return vFieldDefs;
    }


    /**
     * Adds a new Record to a Collection
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection to add the Record rRequest
     * @param rRequest the new Record to add to the Collection cRequest
     * @param vAttachments a Vector containing the AttachmentPart(s) of the Record rReques if there are
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void addRecord(User uRequest, Owner oRequest, Collection cRequest, Record rRequest, Vector vAttachments) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFiles;
        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux))) throw new DataBaseException(DataBaseException.NO_PERMISSION);
                else if (gAux.getPermission() < Guest.PERMISSION_TOTAL) throw new DataBaseException(DataBaseException.NO_PERMISSION);
            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }

            if (cRequest.getNumFieldsDefs()==0) throw new DataBaseException(DataBaseException.NO_FIELDS_DEFS);

            Vector vAttachmentsDefined = checkRecord(conn,cRequest,rRequest);

            if (vAttachmentsDefined.size()!=vAttachments.size()) throw new DataBaseException(DataBaseException.NO_ATTACHMENT_MATCH);

            if (!checkAttachments(vAttachmentsDefined,vAttachments)) throw new DataBaseException(DataBaseException.INVALID_FILE_ATTACHMENT);

            iniTransaction(conn);

            try
            {
                insertRecord(conn,uRequest,oRequest,cRequest,rRequest);

                if (oRequest==null) vFiles = insertAttachments(conn,uRequest,cRequest,vAttachments,rRequest);
                else vFiles = insertAttachments(conn,oRequest,cRequest,vAttachments,rRequest);
                insertFields(conn,cRequest,rRequest);

                updateCollection(conn, cRequest);

                insertLogOperation(conn,cRequest,uRequest,LogOperation.ADD_RECORD,rRequest);
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            saveFiles(vFiles);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Deletes a Record from a Collection. We use an id to locate the record.
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection containing the Record rRequest
     * @param rRequest the Record to delete
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void deleteRecord(User uRequest, Owner oRequest, Collection cRequest, Record rRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;

        Vector vRecords;
        Vector vFiles;

        String query="";

        try
        {
            conn = getConnection().getConnection();

            Statement stmt=conn.createStatement();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux))) throw new DataBaseException(DataBaseException.NO_PERMISSION);
                else if (gAux.getPermission() < Guest.PERMISSION_TOTAL) throw new DataBaseException(DataBaseException.NO_PERMISSION);

            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }
            if (cRequest.getNumFieldsDefs()==0) throw new DataBaseException(DataBaseException.NO_FIELDS_DEFS);

            if (cRequest.getNumRecords()==0) throw new DataBaseException(DataBaseException.EMPTY_COLLECTION);

            if (!existsRecord(conn,rRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_DELETE_RECORD);

            rRequest = selectRecord(conn,rRequest);

            iniTransaction(conn);

            try
            {
                deleteFields(conn, rRequest);
                vFiles = deleteAttachments(conn, rRequest);

                deleteRecord(conn, rRequest);

                cRequest.setNumRecords(cRequest.getNumRecords()-1);

                updateCollection(conn, cRequest);
                insertLogOperation(conn,cRequest,uRequest,LogOperation.DELETE_RECORD,rRequest);
            }

            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            eraseFiles(vFiles);

        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Modifies a Record from a Collection. We use an id to locate the record.
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection containing the Record rRequest
     * @param rRequest the Record to modify
     * @param vAttachments a Vector containing the AttachmentPart(s) of the Record rReques if there are
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void modifyRecord(User uRequest, Owner oRequest, Collection cRequest, Record rRequest, Vector vAttachments) throws DataBaseException,ServerException
    {
        Connection conn = null;

        Vector vRecords;
        Vector vDeleteFiles;
        Vector vAddFiles;

        String query="";

        try
        {
            conn = getConnection().getConnection();

            Statement stmt=conn.createStatement();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux))) throw new DataBaseException(DataBaseException.NO_PERMISSION);
                else if (gAux.getPermission() < Guest.PERMISSION_TOTAL) throw new DataBaseException(DataBaseException.NO_PERMISSION);
            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }

            if (cRequest.getNumFieldsDefs()==0) throw new DataBaseException(DataBaseException.NO_FIELDS_DEFS);

            if (cRequest.getNumRecords()==0) throw new DataBaseException(DataBaseException.EMPTY_COLLECTION);

            if (!existsRecord(conn,rRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_MODIFY_RECORD);

            Record rOld = selectRecord(conn,rRequest);

            Vector vAttachmentsDefined = checkRecord(conn,cRequest,rRequest);

            Vector vFieldsAux = rRequest.getFields();

            for (int i=0;i<vFieldsAux.size();i++)
            {
                Field f = (Field) vFieldsAux.get(i);

                if (f.getValue().equals("null"))
                {
                    String sOldValue = rOld.getFieldValue(f.getName());

                    if (!sOldValue.equals("null")) rRequest.modifyFieldValue(f.getName(),sOldValue);
                }
            }

            Vector vDeleteAttachments = new Vector();
            Vector vAddAttachments = new Vector();

            getAttachmentsModified(rRequest,rOld,vAttachmentsDefined,vAddAttachments,vDeleteAttachments);

            if (!checkAttachments(vAddAttachments,vAttachments)) throw new DataBaseException(DataBaseException.INVALID_FILE_ATTACHMENT);

            iniTransaction(conn);

            try
            {
                vDeleteFiles = deleteAttachments(conn, rOld, vDeleteAttachments);

                if (oRequest==null) vAddFiles = insertAttachments(conn,uRequest,cRequest,vAttachments,rRequest);
                else vAddFiles = insertAttachments(conn,oRequest,cRequest,vAttachments,rRequest);

                updateFields(conn,cRequest,rRequest);
                insertLogOperation(conn,cRequest,uRequest,LogOperation.MODIFY_RECORD,rRequest);
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
            eraseFiles(vDeleteFiles);
            saveFiles(vAddFiles);
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }


    /**
     * Search the Record(s) that satisfy the given Query from the given Collection and User
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection where to search
     * @param qRequest the Query to search into the Collection cRequest
     * @param bAllRecords if true returns all the Records that satisfy the Query, else returns the first one
     * @return a Vector containing all the Records of the Collection cRequest that satisfy the Query qRequest
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public Vector search(User uRequest,Owner oRequest,Collection cRequest,Query qRequest, boolean bAllRecords) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vRecords = new Vector();

        String sUser = uRequest.getUserId();
        String sName = cRequest.getName();

        String query="";

        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux)) && (!isPublic(conn,oRequest,cRequest)))
                {
                        throw new DataBaseException(DataBaseException.NO_PUBLIC_COLLECTION);
                }

                updateLastAccess(conn,cRequest,gAux);
            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }

            if (cRequest.getNumFieldsDefs()==0) throw new DataBaseException(DataBaseException.NO_FIELDS_DEFS);

            if (cRequest.getNumRecords()==0) throw new DataBaseException(DataBaseException.EMPTY_COLLECTION);

            if (!checkQuery(conn,cRequest,qRequest)) throw new DataBaseException(DataBaseException.MALFORMED_QUERY);

            int iNumbers = 1;

            if (bAllRecords) iNumbers = cRequest.getNumRecords();

            int iCount = countRecords(conn, cRequest, qRequest);

            if (iCount == 0) throw new DataBaseException(DataBaseException.NO_RECORD_FOUND);

            vRecords = selectRecords(conn,cRequest,qRequest,iNumbers);

            vRecords.add(0, new Integer(cRequest.getNumRecords()));
            vRecords.add(1, new Integer(iCount));
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
        return vRecords;
    }

    /**
     * Exports the Records selected from Collection into four Vectors of FielsDefs,Records,FieldsDefsAtt and Attachments
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection containing the FieldDef fdRequest
     * @param qRequest the Query to select the Records to export
     * @param vFieldsDefs output parameter that contains the Fields Definitions of the Collection
     * @param vRecords output parameter that contains the Records found
     * @param vFieldsDefsAtt output parameter that contains the Fields Definitions that have files attachments
     * @param vAttachments output parameter that contains the Attachments of the Records
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void exportCollection(User uRequest,Owner oRequest,Collection cRequest, Query qRequest, Vector vFieldsDefs, Vector vRecords, Vector vFieldsDefsAtt, Vector vAttachments) throws DataBaseException,ServerException
    {
        Connection conn = null;

        String query="";

        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                uRequest.setUserId(oRequest.getUserId());

                if (!existsCollection(conn,uRequest,cRequest))
                {
                    throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
                }
                else if (!isPublic(conn,oRequest,cRequest))
                {
                    throw new DataBaseException(DataBaseException.NO_PUBLIC_COLLECTION);
                }
            }

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            if (cRequest.getNumFieldsDefs()==0) throw new DataBaseException(DataBaseException.NO_FIELDS_DEFS);

            vFieldsDefs.addAll(selectFieldsDefs(conn, cRequest));
            vFieldsDefsAtt = selectFieldsDefsAtt(conn, cRequest);

            if (!checkQuery(conn,cRequest,qRequest)) throw new DataBaseException(DataBaseException.MALFORMED_QUERY);

            vRecords.addAll(selectRecords(conn,cRequest,qRequest,cRequest.getNumRecords()));

            if (vRecords.size()!=0) vAttachments.addAll(selectAttachments(conn,cRequest,vRecords));
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Returns the disk quota of a User
     * @param uRequest the User
     * @throws ServerException when an internal Exception error occurs
     * @return the int diskquota in bytes
     */
    public int getDiskQuota(User uRequest) throws ServerException
    {
        Connection conn = getConnection().getConnection();

        try
        {
            PreparedStatement psStmt = conn.prepareStatement("SELECT diskquota FROM t_user WHERE iduser = ?");
            psStmt.setString(1,uRequest.getUserId());

            ResultSet rs = psStmt.executeQuery();

            if (!rs.next()) return Integer.parseInt(pDB.getProperty("default.disk.quota"));
                                            //Default quota returned when you check it
                                            //but the user directory is not yet created

            int quota = rs.getInt(1);

            if (rs!=null) rs.close();

            return quota;
        }
        catch (SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Adds a new Guest user into a Collection. The User will have the permission to view or modify the records of the Collection.
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection where to add the user
     * @param gRequest the Guest user to add to the collection. The Guest object has the permission level.
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void addGuest(User uRequest, Collection cRequest, Guest gRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFiles;
        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            if (existsGuest(conn,uRequest,cRequest,gRequest)) throw new DataBaseException(DataBaseException.EXISTS_GUEST);

            iniTransaction(conn);

            try
            {
                insertGuest(conn,uRequest,cRequest,gRequest);
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);

        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Adds a Guest user of a Collection.
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection where to delete the user
     * @param gRequest the Guest user to delete from the collection
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void deleteGuest(User uRequest, Collection cRequest, Guest gRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFiles;
        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            if (!existsGuest(conn,uRequest,cRequest,gRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_GUEST);

            iniTransaction(conn);

            try
            {
                deleteGuest(conn,uRequest,cRequest,gRequest);
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Modifies the permission level that has a Guest in a Collection
     * @param uRequest the User owner of the Collection cRequest
     * @param cRequest the Collection
     * @param gRequest the Guest with the new permission
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     */
    public void modifyPermission(User uRequest, Collection cRequest, Guest gRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vFiles;
        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            int iNewPermission = gRequest.getPermission();

            if (!existsGuest(conn,uRequest,cRequest,gRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_GUEST);

            gRequest.setPermission(iNewPermission);

            iniTransaction(conn);
            try
            {
                updateGuest(conn,uRequest,cRequest,gRequest);
            }
            catch(SQLException sqle)
            {
                rollbackTransaction(conn);
                throw new ServerException(sqle);
            }

            commitTransaction(conn);
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }
    }

    /**
     * Returns a Vector containing the Guests of the Collection cRequest
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     * @return a Vector containing the Guests of the Collection
     */
    public Vector listGuests(User uRequest,Owner oRequest,Collection cRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vGuests = new Vector();

        String sUser = uRequest.getUserId();
        String sName = cRequest.getName();

        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest))
                {
                    throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
                }

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux))) throw new DataBaseException(DataBaseException.NO_PERMISSION);

            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }

            vGuests = selectGuests(conn,cRequest);
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }

        return vGuests;
    }

    /**
     * Returns a Vector containing the Collections where the User uRequest is guested
     *
     * @param uRequest the User to look for what collections he or she is guest to
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     * @return a Vector of pairs of Collection+Guest. We use a Guest object to return the userId of the Owner of the Collection
     */
    public Vector listGuestCollections(User uRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        ResultSet rsRes = null;
        Vector vRes = new Vector();
        Collection cAux;
        Guest gAux; //We return a Guest to store the lastAccess date

        try
        {

            conn = getConnection().getConnection();

            PreparedStatement psStmt = conn.prepareStatement("SELECT nameCollection, isPublic, description, c.idUser, lastAccess " +
                                                            "FROM collection c, guests g " +
                                                            "WHERE c.idCollection = g.idCollection AND g.idGuest = ? ORDER BY c.idUser,c.nameCollection");
            psStmt.setString(1,uRequest.getUserId());


            rsRes = psStmt.executeQuery();

            while (rsRes.next())
            {
                cAux = new Collection(rsRes.getString(1));

                cAux.setIsPublic(rsRes.getInt(2));
                cAux.setDescription(rsRes.getString(3));

                if (cAux.getDescription()==null) cAux.setDescription("");
                //Because in Oracle String empty is null

                vRes.add(cAux);

                gAux = new Guest(rsRes.getString(4));
                gAux.setLastAcces(rsRes.getDate(5));

                vRes.add(gAux);
            }

            if (rsRes!=null) rsRes.close();
            if (psStmt!=null) psStmt.close();
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }

        finally
        {
           freeConnection();
        }

        return vRes;
    }

    /**
     * Returns the permission level of a User into a Collection
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection
     * @param rRequest the Record of the Collection. In this version we do not use the Record, we just care the permission in a Collection level.
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     * @return the int Permission of the User to the Record of the Collection, it can be PERMISSION_NONE,PERMISSION_READ or PERMISSION_TOTAL
     */
    public int getPermission(User uRequest, Owner oRequest, Collection cRequest, Record rRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        ResultSet rsRes = null;
        Collection cAux;

        try
        {
            conn = getConnection().getConnection();

            if (!existsCollection(conn,oRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);

            if (uRequest.getUserId().equals(oRequest.getUserId())) return Guest.PERMISSION_TOTAL;

            Guest gAux = new Guest(uRequest.getUserId());

            if (!existsGuest(conn,oRequest,cRequest,gAux))
            {
                if (isPublic(conn,oRequest,cRequest)) return Guest.PERMISSION_READ;
                else return Guest.PERMISSION_NONE;
            }
            else return gAux.getPermission();

        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
           freeConnection();
        }
    }

    /**
     * Returns all the LogOperation for the Collection
     * @param uRequest if oRequest is null the User owner of the Collection cRequest, else the User interacting with the Collection
     * @param oRequest the Owner of the Collection
     * @param cRequest the Collection
     * @throws DataBaseException when the given parametres not agree with the requeriments of the system
     * @throws ServerException when an internal Exception error occurs
     * @return a Vector containing the LogOperations
     */
    public Vector getLog(User uRequest,Owner oRequest,Collection cRequest) throws DataBaseException,ServerException
    {
        Connection conn = null;
        Vector vLogOperations = new Vector();

        String sUser = uRequest.getUserId();
        String sName = cRequest.getName();

        try
        {
            conn = getConnection().getConnection();

            if (oRequest!=null)
            {
                if (!existsCollection(conn,oRequest,cRequest))
                {
                    throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
                }

                Guest gAux = new Guest(uRequest.getUserId());

                if (!(existsGuest(conn,oRequest,cRequest,gAux))) throw new DataBaseException(DataBaseException.NO_PERMISSION);
            }
            else
            {
                if (!existsCollection(conn,uRequest,cRequest)) throw new DataBaseException(DataBaseException.NO_EXISTS_COLLECTION);
            }

            vLogOperations = selectLog(conn,cRequest);
        }

        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
            freeConnection();
        }

        return vLogOperations;
    }

    /**
     * Returns all public collections matching the BrowseCriteria
     * @param bcRequest the BrowseCriteria request
     * @throws ServerException when an internal Exception error occurs
     * @return a Vector containing the Collections
     */
    public Vector browse(BrowseCriteria bcRequest) throws ServerException
    {
        Connection conn = null;
        ResultSet rsCount = null;
        ResultSet rsRes = null;
        Vector vRes = new Vector();
        Collection cAux;
        User uAux;
        int iNumFound = 0;

        try
        {
            conn = getConnection().getConnection();

            bcRequest.setValue("%"+bcRequest.getValue()+"%");

            PreparedStatement psStmt =
                    conn.prepareStatement(  "SELECT COUNT(*) " +
                                            "FROM collection c " +
                                            "WHERE c.isPublic = 1 " +
                                            "AND LOWER(c."+bcRequest.getBrowseBy()+") LIKE LOWER(?)");

            psStmt.setString(1,bcRequest.getValue());

            rsCount = psStmt.executeQuery();

            if (rsCount.next())
            {
                iNumFound = rsCount.getInt(1);
            }

            if (iNumFound>0)
            {
                if (pDB.getProperty("dbSystem").equals("mysql"))
                {
                         psStmt = conn.prepareStatement("SELECT nameCollection,idUser,tags,numRecords,created,description " +
                                                        "FROM collection c " +
                                                        "WHERE c.isPublic = 1 " +
                                                        "AND LOWER(c."+bcRequest.getBrowseBy()+") LIKE LOWER(?) "+
                                                        "ORDER BY "+bcRequest.getOrderBy()+" "+bcRequest.getDirection()+" "+
                                                        "LIMIT ?,?");

                    psStmt.setString(1,bcRequest.getValue());
                    psStmt.setInt(2,bcRequest.getIndexBegin()-1);
                    psStmt.setInt(3,bcRequest.getIndexEnd()-bcRequest.getIndexBegin()+1);


                }
                else if (pDB.getProperty("dbSystem").equals("oracle"))
                {
                    psStmt = conn.prepareStatement("SELECT * FROM " +
                    "(SELECT a.*, rownum rnum FROM " +
                        "(SELECT nameCollection,idUser,tags,numRecords,created,description " +
                        "FROM collection c " +
                        "WHERE c.isPublic = 1 " +
                        "AND LOWER(c."+bcRequest.getBrowseBy()+") LIKE LOWER(?) "+
                        "ORDER BY "+bcRequest.getOrderBy()+" "+bcRequest.getDirection()+",rowid) a " +
                    "WHERE rownum <=?) " +
                    "WHERE rnum>=?");

                    psStmt.setString(1,bcRequest.getValue());
                    psStmt.setInt(2,bcRequest.getIndexEnd());
                    psStmt.setInt(3,bcRequest.getIndexBegin());
                }


                rsRes = psStmt.executeQuery();

                while (rsRes.next())
                {
                    cAux = new Collection(rsRes.getString(1));

                    cAux.setTags(rsRes.getString(3));
                    cAux.setNumRecords(rsRes.getInt(4));
                    cAux.setCreated(rsRes.getDate(5));
                    cAux.setDescription(rsRes.getString(6));

                    if (cAux.getTags()==null) cAux.setTags("");
                    if (cAux.getDescription()==null) cAux.setDescription("");
                    //Because in Oracle String empty is null

                    vRes.add(cAux);

                    uAux = new User(rsRes.getString(2));

                    vRes.add(uAux);
                }
            }

            vRes.add(0, new Integer(iNumFound));

            if (rsCount!=null) rsCount.close();
            if (rsRes!=null) rsRes.close();
            if (psStmt!=null) psStmt.close();
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        finally
        {
           freeConnection();
        }

        return vRes;
    }


    /**
     * Returns the number of tagClouds requested. If numTags is negative it returns all the tags used in all public collections.
     * @param numTags number of Tags to return
     * @throws ServerException when an internal Exception error occurs
     * @return a Vector of Tag
     */
    public Vector listTagClouds(int numTags) throws ServerException
    {
        Connection conn = null;
        ResultSet rsTags = null;
        ResultSet rsMaxMin = null;
        int iMaxTimes = 0;
        int iMinTimes = 0;
        //logger.error(pDB.getProperty("dbSystem")+"-- Line 1786- Database.java");
        //logger.debug(pDB.getProperty("dbSystem")+"-- Line 1786- Database.java");
        PreparedStatement psStmt;
        
        Vector vRes = new Vector();
    //logger.info(pDB.getProperty("dbSystem")+"-- Line 1790- Database.java");
        try
        {
            conn = getConnection().getConnection();
            
            String sNumTags = "";
            //ogger.info(pDB.getProperty("dbSystem")+"-- Line 1796- Database.java");
            if (pDB.getProperty("dbSystem").equals("mysql"))
            {
                if (numTags > -1)
                {
                    sNumTags = " LIMIT "+numTags;
                }
                
                psStmt = conn.prepareStatement("SELECT MAX(times) as maxTimes, MIN(times) as minTimes" +
                                                " FROM ( SELECT tag,times FROM tag_counter "+
                                                " ORDER BY times desc"+sNumTags+") AS QUERY");
                
                rsMaxMin = psStmt.executeQuery();

                if (rsMaxMin.next())
                {
                    vRes.add(new Integer(rsMaxMin.getInt("minTimes")));
                    vRes.add(new Integer(rsMaxMin.getInt("maxTimes")));
                }
                
                psStmt = conn.prepareStatement("SELECT * FROM (" +
                        " SELECT tag,times FROM tag_counter" +
                        " ORDER BY times DESC"+sNumTags+") AS QUERY"+
                        " ORDER BY tag");
                
                rsTags = psStmt.executeQuery();
                
                while(rsTags.next())
                {   
                    vRes.add(new Tag(rsTags.getString(1),rsTags.getInt(2)));
                }

            }
            else if (pDB.getProperty("dbSystem").equals("oracle"))
            {
                if (numTags > -1)
                {
                    sNumTags = " WHERE rownum <="+numTags;
                }
                
                psStmt = conn.prepareStatement("SELECT MAX(times) as maxTimes, MIN(times) as minTimes" +
                                                " FROM ( SELECT tag,times FROM tag_counter "+
                                                " ORDER BY times desc)"+
                                                sNumTags);
                
                rsMaxMin = psStmt.executeQuery();

                if (rsMaxMin.next())
                {
                    vRes.add(new Integer(rsMaxMin.getInt("minTimes")));
                    vRes.add(new Integer(rsMaxMin.getInt("maxTimes")));
                }
                
                psStmt = conn.prepareStatement("SELECT * FROM (" +
                        " SELECT tag,times FROM tag_counter" +
                        " ORDER BY times DESC) " +
                        sNumTags+
                        " ORDER BY tag");
                
                rsTags = psStmt.executeQuery();
                
                while(rsTags.next())
                {   
                    vRes.add(new Tag(rsTags.getString(1),rsTags.getInt(2)));
                }
            }
        }
        catch(SQLException sqle)
        {
            throw new ServerException(sqle);
        }
        
        finally
        {
           freeConnection();
        }

        return vRes;
    }
    
    protected boolean existsUser(Connection conn,User uRequest) throws SQLException
    {
        boolean bRes;
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT * FROM t_user WHERE idUser= ?");
        psStmt.setString(1,uRequest.getUserId());
                
        ResultSet rsRes = psStmt.executeQuery();
              
        if (rsRes.next())   bRes=true;
        else bRes = false;
        
        if (rsRes!=null) rsRes.close();
        if (psStmt!=null) psStmt.close();
        
        return bRes;
    }
    
    protected boolean existsRecord(Connection conn,Record rRequest) throws SQLException
    {
        boolean bRes;
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT * FROM record WHERE idRecord= ?");
        psStmt.setInt(1,rRequest.getId());
                
        ResultSet rsRes = psStmt.executeQuery();
              
        if (rsRes.next())   bRes=true;
        else bRes = false;
        
        
        if (rsRes!=null) rsRes.close();
        if (psStmt!=null) psStmt.close();
        
        return bRes;
    }
    
    protected Record selectRecord(Connection conn,Record rRequest) throws SQLException
    {
        boolean bRes;
        
        PreparedStatement psStmt = 
            conn.prepareStatement("SELECT nameField,val from fielddef fd,field f " +
                "WHERE f.idRecord = ? " +
                    "AND fd.idFieldDef = f.idFieldDef ORDER BY fd.position");
            
        Record rAux = new Record();
        Field fAux;
        
        rAux.setId(rRequest.getId());
        
        psStmt.setInt(1,rRequest.getId());
            
        ResultSet rsFields = psStmt.executeQuery();
        
        while (rsFields.next())
        {
            fAux = new Field();
            fAux.setName(rsFields.getString(1));
            String valAux = rsFields.getString(2);
            if (valAux==null) valAux=""; //Because in Orcle empty String is (null)
            fAux.setValue(valAux);
            rAux.addField(fAux);
            
        }
        
        if (rsFields!=null) rsFields.close();
        if (psStmt!=null) psStmt.close();
        
        return rAux;
    }
        
    protected boolean existsCollection(Connection conn,User uRequest,Collection cRequest) throws SQLException
    {
        boolean bRes;
        String sUser = uRequest.getUserId();
        String sName = cRequest.getName();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT idCollection,orderFieldDef,retrieveRecords,numFieldsDefs,numRecords,description,tags,created,isPublic from collection c " +
                                    " WHERE c.nameCollection = ? and c.idUser= ?");
                
        psStmt.setString(1, sName);
        psStmt.setString(2, sUser);
        
        ResultSet rsRes = psStmt.executeQuery();
                        
        if (rsRes.next()) 
        {
            bRes=true;
            cRequest.setId(rsRes.getInt(1));
            cRequest.setOrderFieldDef(rsRes.getInt(2));
            cRequest.setRetrieveRecords(rsRes.getInt(3));
            cRequest.setNumFieldsDefs(rsRes.getInt(4));
            cRequest.setNumRecords(rsRes.getInt(5));
            
            cRequest.setDescription(rsRes.getString(6));
            if (cRequest.getDescription()==null) cRequest.setDescription("");
            //because in Oracle string empty is null
            
            cRequest.setTags(rsRes.getString(7));
            if (cRequest.getTags()==null) cRequest.setTags("");
            //because in Oracle string empty is null
            
            cRequest.setCreated(rsRes.getDate(8));
            cRequest.setIsPublic(rsRes.getInt(9));
            
        }
        else bRes = false;

        if (rsRes!=null) rsRes.close();
        if (psStmt!=null) psStmt.close();
        
        return bRes;
    }
    
    private boolean isPublic(Connection conn,Owner oRequest,Collection cRequest) throws SQLException
    {
        boolean bRes;
        String sOwner = oRequest.getUserId();
        String sName = cRequest.getName();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT idCollection,orderFieldDef,retrieveRecords,numFieldsDefs,numRecords from collection c " +
                                    "WHERE c.nameCollection = ? " +
                                    "and c.idUser = ? "+
                                    " and c.isPublic = 1");
        psStmt.setString(1,sName);
        psStmt.setString(2,sOwner);
        
        ResultSet rsRes = psStmt.executeQuery();
              
        if (rsRes.next()) 
        {
            bRes=true;
            cRequest.setId(rsRes.getInt(1));
            cRequest.setOrderFieldDef(rsRes.getInt(2));
            cRequest.setRetrieveRecords(rsRes.getInt(3));
            cRequest.setNumFieldsDefs(rsRes.getInt(4));
            cRequest.setNumRecords(rsRes.getInt(5));            
        }
        else bRes = false;
        
        if (rsRes!=null) rsRes.close();
        if (psStmt!=null) psStmt.close();
        
        return bRes;
    }
    
    
    private void insertUser(Connection conn,User uRequest) throws SQLException
    {
        String sUser = uRequest.getUserId();
        
        Statement stmt=conn.createStatement();    
        
        stmt.executeUpdate( "INSERT INTO t_user(idUser) values ('"+sUser+"')");
        
        if (stmt!=null) stmt.close();
    }
    
    private void insertCollection(Connection conn,User uRequest,Collection cRequest) throws SQLException
    {
        String sUser = uRequest.getUserId();
        String sName = cRequest.getName();
        String sDescription = cRequest.getDescription();
        String sTags = cRequest.getTags();
        
        Statement stmt=null;
        
        PreparedStatement psStmt = null;
        String sSQL = "INSERT INTO collection(nameCollection,idUser,isPublic,description,tags,created) values (?,?,?,?,?,CURRENT_DATE)";
        if (pDB.getProperty("dbSystem").equals("mysql")){
            psStmt = conn.prepareStatement(sSQL, Statement.RETURN_GENERATED_KEYS);
        }else{
            psStmt = conn.prepareStatement(sSQL);        	
        }
                
        psStmt.setString(1, sName);
        psStmt.setString(2, sUser);
        psStmt.setInt(3, cRequest.getIsPublic());
        psStmt.setString(4,sDescription);
        psStmt.setString(5,sTags); 
        
        psStmt.executeUpdate();
        
        ResultSet  rsIdCollection=null;
 
        if (pDB.getProperty("dbSystem").equals("mysql"))
        {
            rsIdCollection = psStmt.getGeneratedKeys();
        }
        else if (pDB.getProperty("dbSystem").equals("oracle"))
        {
            stmt = conn.createStatement();
            rsIdCollection = stmt.executeQuery("select IDCOLLECTION_seq.currval from dual");
        }
                       
        
        rsIdCollection.next();
        cRequest.setId(rsIdCollection.getInt(1));
        
        if (rsIdCollection!=null) rsIdCollection.close();
        if (psStmt != null) psStmt.close();   
        if (stmt != null) stmt.close();   
            
    }
    
    private void updateCollection(Connection conn,Collection cRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("SELECT idFieldDef FROM fielddef WHERE idCollection = ?");
        
        ResultSet rs = null;
        
        if ((cRequest.getOrderFieldDef()==-1) && (cRequest.getNumFieldsDefs()>0))
        {
            psStmt.setInt(1, cRequest.getId());
            rs = psStmt.executeQuery();
            rs.next();
            cRequest.setOrderFieldDef(rs.getInt(1));
        }
        
        psStmt = conn.prepareStatement("UPDATE collection SET nameCollection = ?, orderFieldDef = ?, retrieveRecords = ?, numFieldsDefs = ?, " +
                                                               "numRecords = ?, isPublic = ?, description = ?, tags = ? WHERE idCollection = ?");
        
        psStmt.setString(1, cRequest.getName());
        psStmt.setInt(2, cRequest.getOrderFieldDef());
        psStmt.setInt(3, cRequest.getRetrieveRecords());
        psStmt.setInt(4, cRequest.getNumFieldsDefs());
        psStmt.setInt(5, cRequest.getNumRecords());
        psStmt.setInt(6, cRequest.getIsPublic());
        psStmt.setString(7, cRequest.getDescription());
        psStmt.setString(8, cRequest.getTags());
        
        psStmt.setInt(9, cRequest.getId());
        
        psStmt.executeUpdate();
        
        if (rs!=null) rs.close();
        if (psStmt!=null) psStmt.close();
    }
    
    private void insertFieldsDefs(Connection conn,Collection cRequest, Vector vFieldsDefs) throws SQLException
    {
        int iIdCollection = cRequest.getId();
            
        PreparedStatement psStmt = null;
        String sSQL = "INSERT INTO fielddef(idFieldDef,idCollection,nameField,nameType,position,properties) VALUES (0,?,?,?,?,?)";
        if (pDB.getProperty("dbSystem").equals("mysql")){
        	psStmt = conn.prepareStatement(sSQL, Statement.RETURN_GENERATED_KEYS);
        }else{
        	psStmt = conn.prepareStatement(sSQL);        	
        }
        
        FieldDef fdAux;
            
        for (int i=0;i<vFieldsDefs.size();i++)
        {
            
            fdAux = (FieldDef) vFieldsDefs.get(i);
            
            psStmt.setInt(1, cRequest.getId());
            psStmt.setString(2,fdAux.getName());
            psStmt.setString(3,fdAux.getType());
            psStmt.setInt(4,cRequest.getNumFieldsDefs()+1);
            psStmt.setString(5,fdAux.getProperties());
                                       
            psStmt.executeUpdate();
            
            
            ResultSet  rsIdFieldDef=null;
 
            if (pDB.getProperty("dbSystem").equals("mysql"))
            {
                rsIdFieldDef = psStmt.getGeneratedKeys();
            }
            else if (pDB.getProperty("dbSystem").equals("oracle"))
            {
                rsIdFieldDef = conn.createStatement().executeQuery("select IDFIELDDEF_seq.currval from dual");
            }
            
            rsIdFieldDef.next();
            
            fdAux.setId(rsIdFieldDef.getInt(1));
           
            if ((i==0) && (cRequest.getOrderFieldDef()==-1))
            {
                cRequest.setOrderFieldDef(rsIdFieldDef.getInt(1));
            }
            
            cRequest.setNumFieldsDefs(cRequest.getNumFieldsDefs()+1);
        }
        
        if (psStmt!=null) psStmt.close();
    }
    
    private Vector selectFieldsDefs(Connection conn, Collection cRequest) throws SQLException
    {
        Vector vFieldsDefs = new Vector();
        
        Statement stmt = conn.createStatement();
        
        ResultSet rsFieldsDefs = stmt.executeQuery("SELECT idFieldDef, nameField, nameType, properties "+
                 " from fielddef WHERE idCollection ="+cRequest.getId()+ " order by position");
        
        FieldDef fdAux;
        
        while(rsFieldsDefs.next())
        {
            String sType = rsFieldsDefs.getString(3);
            fdAux = FieldDef.createFieldDef(sType);
            
            fdAux.setId(rsFieldsDefs.getInt(1));
            fdAux.setName(rsFieldsDefs.getString(2));
            fdAux.setProperties(rsFieldsDefs.getString(4));
            
            vFieldsDefs.add(fdAux);
        }
        
        if (rsFieldsDefs!=null) rsFieldsDefs.close();
        if (stmt!=null) stmt.close();
        
        return vFieldsDefs;
    }
    
    private Vector selectFieldsDefsAtt(Connection conn, Collection cRequest) throws SQLException
    {
        Vector vFieldsDefs = new Vector();
        
        Statement stmt = conn.createStatement();
        
        ResultSet rsFieldsDefs = stmt.executeQuery("SELECT idFieldDef, nameField, nameType, properties "+
                 " from fielddef WHERE idCollection ="+cRequest.getId()+" AND (nameType = 'image' OR nameType ='sound')");
        
        FieldDef fdAux;
        
        while(rsFieldsDefs.next())
        {
            String sType = rsFieldsDefs.getString(3);
            fdAux = FieldDef.createFieldDef(sType);
            
            fdAux.setId(rsFieldsDefs.getInt(1));
            fdAux.setName(rsFieldsDefs.getString(2));
            fdAux.setProperties(rsFieldsDefs.getString(4));
            
            vFieldsDefs.add(fdAux);
        }
        
        if (rsFieldsDefs!=null) rsFieldsDefs.close();
         
        if (stmt!=null) stmt.close();
        
        return vFieldsDefs;
    }

    
    protected boolean existsFieldsDefs(Connection conn,Collection cRequest, Vector vFieldsDefs) throws SQLException
    {
        int iIdCollection = cRequest.getId();
            
        PreparedStatement psStmt = 
                        conn.prepareStatement(  " SELECT idFieldDef,position FROM fielddef " +
                                                " WHERE idCollection = "+iIdCollection+
                                                " AND nameField = ?");
        FieldDef fdAux;
        boolean bContinue = true;
            
        for (int i=0;i<vFieldsDefs.size() && bContinue;i++)
        {
            
            fdAux = (FieldDef) vFieldsDefs.get(i);
            psStmt.setString(1,fdAux.getName());
            
            ResultSet rs = psStmt.executeQuery();
            
            if (!rs.next()) bContinue=false;
            else
            {
                fdAux.setId(rs.getInt(1));
                fdAux.setPosition(rs.getInt(2));
            }
        }
        
        if (psStmt!=null) psStmt.close();
        
        return bContinue;
    }
    
    
    private void updateFields(Connection conn,Collection cRequest, Vector vFieldsDefs) throws SQLException
    {
        int iIdCollection = cRequest.getId();
       
        Statement stmt = conn.createStatement();
        
        ResultSet rsRecords = stmt.executeQuery("SELECT idRecord FROM record WHERE idCollection = "+iIdCollection);
        
        Vector vRecords = new Vector();
        Record rAux = new Record();
        
        while (rsRecords.next())
        {
            rAux = new Record();
            rAux.setId(rsRecords.getInt(1));
            vRecords.add(rAux);
        }
        
        PreparedStatement psStmt = 
                        conn.prepareStatement(  "INSERT INTO field (idFieldDef,idRecord,val) " +
                                            "VALUES (?,?,?)");
        FieldDef fdAux;
            
        for (int i=0;i<vFieldsDefs.size();i++)
        {
            for (int j=0;j<vRecords.size();j++)
            {
                fdAux = (FieldDef) vFieldsDefs.get(i);
                rAux = (Record) vRecords.get(j);
                psStmt.setInt(1,fdAux.getId());
                psStmt.setInt(2,rAux.getId());
                psStmt.setString(3,fdAux.getDefaultValue());
                
                psStmt.executeUpdate();
            }
        }        
        
        if (rsRecords!=null) rsRecords.close();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private void updateFields(Connection conn,Collection cRequest, Record rRequest) throws SQLException
    {
        int iIdCollection = cRequest.getId();
        
        PreparedStatement psStmt = conn.prepareStatement("UPDATE field SET val = ? WHERE idRecord ="+rRequest.getId()+" AND idFieldDef = " +
                                                        "( SELECT idFieldDef FROM fielddef WHERE idCollection ="+iIdCollection+" AND nameField =?)"); 
        
        Field fAux;
        Vector vFields = rRequest.getFields();
        
        for (int i=0;i<vFields.size();i++)
        {
            fAux = (Field) vFields.get(i);
            
            psStmt.setString(1, fAux.getValue());
            psStmt.setString(2, fAux.getName());
            
            psStmt.executeUpdate();
        }
        
        if (psStmt!=null) psStmt.close();
    }
    
    
    private void deleteCollection(Connection conn,Collection cRequest) throws SQLException
    {
        PreparedStatement psStmt=conn.prepareStatement("DELETE from collection WHERE idCollection = ?");
        psStmt.setInt(1,cRequest.getId());
        psStmt.executeUpdate();
        
        if (psStmt != null) psStmt.close();            
    }
    
    private void deleteFieldsDefs(Connection conn,Collection cRequest,Vector vFieldsDefs) throws SQLException
    {
        int iIdCollection = cRequest.getId();
        
        String sQuery= "DELETE FROM fielddef WHERE idCollection="+iIdCollection;
        
        if (vFieldsDefs == null)
        {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sQuery);      
            cRequest.setOrderFieldDef(-1);
            cRequest.setNumFieldsDefs(0);
            if (stmt!=null) stmt.close();
        }
        else
        {
            PreparedStatement psStmt = 
                            conn.prepareStatement(sQuery+" AND idFieldDef=?");
        
            FieldDef fdAux;
            int iOrderAux = cRequest.getOrderFieldDef();
            
            for (int i=0;i<vFieldsDefs.size();i++)
            {
                fdAux = (FieldDef) vFieldsDefs.get(i);
                psStmt.setInt(1,fdAux.getId());
                psStmt.executeUpdate();
                
                if (fdAux.getId()==iOrderAux)
                {
                    cRequest.setOrderFieldDef(-1);
                }
                
                
                
                if (fdAux.getPosition()!=cRequest.getNumFieldsDefs())
                {
                    changeFieldDefsPositions(conn,cRequest,fdAux.getPosition());
                }
                
                cRequest.setNumFieldsDefs(cRequest.getNumFieldsDefs()-1);
                
                
            }
            
            
            
            if (psStmt!=null) psStmt.close();
        }
    }
    
    private void deleteRecords(Connection conn,Collection cRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("DELETE FROM record WHERE idCollection = ?");
        psStmt.setInt(1,cRequest.getId());
        psStmt.executeUpdate();        
        
        if (psStmt!=null) psStmt.close();
    }
    
    private void deleteRecord (Connection conn, Record rRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("DELETE FROM record WHERE idRecord= ?");        
        psStmt.setInt(1,rRequest.getId());
        psStmt.executeUpdate();

        if (psStmt!=null) psStmt.close();
    }
    
    private void deleteFields(Connection conn,Collection cRequest,Vector vFieldsDefs) throws SQLException
    {
        int iIdCollection = cRequest.getId();
        
        FieldDef fdAux;ResultSet rsRes;
        
        if (vFieldsDefs==null)
        //Case delete all collection
        {
            Statement stmt = conn.createStatement();
            
            vFieldsDefs = new Vector();
            
            rsRes=stmt.executeQuery("SELECT idFieldDef,nameType FROM fielddef WHERE idCollection="+iIdCollection);
            
            while(rsRes.next())
            {
                fdAux= FieldDef.createFieldDef(rsRes.getString(2));
                fdAux.setId(rsRes.getInt(1));
                vFieldsDefs.add(fdAux);
            }
            
            if (rsRes!=null) rsRes.close();
            if (stmt!=null) stmt.close();
        }
        
       
        PreparedStatement psStmt = 
                conn.prepareStatement("DELETE FROM field WHERE idFieldDef=?");
     
        
        for (int i=0;i<vFieldsDefs.size();i++)
        {
            fdAux = (FieldDef) vFieldsDefs.get(i);
            psStmt.setInt(1,fdAux.getId());
            psStmt.executeUpdate();
        }
            
        if (psStmt!=null) psStmt.close();
    }
    
    
    private void deleteFields(Connection conn,Record rRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("DELETE FROM field WHERE idRecord = ?");
        psStmt.setInt(1,rRequest.getId());
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private Vector deleteAttachments(Connection conn, Collection cRequest) throws SQLException
    {
        Statement stmt = conn.createStatement();
        Vector vFileNames = new Vector();
        String sFileName;
        String query ="SELECT idAttachment,ext,idUser FROM attachment WHERE idFieldDef IN (SELECT idFieldDef FROM fielddef WHERE idCollection ="+cRequest.getId()+")";      
        
        ResultSet rsAtt = stmt.executeQuery("SELECT idUser,idCollection,origName FROM attachment WHERE idFieldDef IN (SELECT idFieldDef FROM fielddef WHERE idCollection ="+cRequest.getId()+")");
        
        while(rsAtt.next())
        {
            sFileName=rsAtt.getString(1)+"/"+rsAtt.getInt(2)+"/"+rsAtt.getString(3);
            
            vFileNames.add(sFileName);
        }
        
        stmt.executeUpdate("DELETE FROM attachment WHERE idCollection ="+cRequest.getId());
        
        if (rsAtt != null) rsAtt.close();
        if (stmt != null) stmt.close();
    
        return vFileNames;
    }
    
    private Vector deleteAttachments(Connection conn, FieldDef fdRequest) throws SQLException
    {
        Statement stmt = conn.createStatement();
        Vector vFileNames = new Vector();
        String sFileName;
                
        ResultSet rsAtt = stmt.executeQuery("SELECT idUser,idCollection,origName FROM attachment WHERE idFieldDef="+fdRequest.getId());
        
        while(rsAtt.next())
        {
            sFileName=rsAtt.getString(1)+"/"+rsAtt.getInt(2)+"/"+rsAtt.getString(3);
            vFileNames.add(sFileName);
        }
        
        stmt.executeUpdate("DELETE FROM attachment WHERE idFieldDef="+fdRequest.getId());
        
        if (rsAtt != null) rsAtt.close();
        if (stmt != null) stmt.close();
    
        return vFileNames;
    }  
    
    private Vector deleteAttachments(Connection conn, Record rRequest) throws SQLException
    {
        Statement stmt = conn.createStatement();
        Vector vFileNames = new Vector();
        String sFileName;
                
        ResultSet rsAtt = stmt.executeQuery("SELECT idUser,idCollection,origName FROM attachment WHERE idRecord="+rRequest.getId());
        
        while(rsAtt.next())
        {
            sFileName=rsAtt.getString(1)+"/"+rsAtt.getInt(2)+"/"+rsAtt.getString(3);
            vFileNames.add(sFileName);
        }
        
        stmt.executeUpdate("DELETE FROM attachment WHERE idRecord="+rRequest.getId());
        
        if (rsAtt != null) rsAtt.close();
        if (stmt != null) stmt.close();
    
        return vFileNames;
    }
    
    protected boolean existsAttachment(Connection conn, Collection cRequest, Field fRequest) throws SQLException
    {
        boolean bRes;
        
        int idCollection = cRequest.getId();
        String sFileName = fRequest.getValue();
        
        Statement stmt = conn.createStatement();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT * from attachment a "+
                                    "WHERE a.origName = ? AND a.idCollection = ?");
        
        psStmt.setString(1, sFileName);
        psStmt.setInt(2, idCollection);
        
        ResultSet rsRes = psStmt.executeQuery();  
       
        if (rsRes.next()) 
        {
            bRes=true;
        }
        else bRes = false;
        
        
        if (rsRes != null) rsRes.close();
        if (psStmt != null) psStmt.close();
        
        return bRes;
    }
    
    private Vector deleteAttachments(Connection conn, Record rRequest, Vector vDeleteAttachments) throws SQLException
    {
        PreparedStatement psSelect = conn.prepareStatement("SELECT idUser,idCollection,origName FROM attachment WHERE idFieldDef = ? AND idRecord="+rRequest.getId());
        PreparedStatement psDelete = conn.prepareStatement("DELETE FROM attachment WHERE idFieldDef = ? AND idRecord="+rRequest.getId());
        
        ResultSet rsAtt = null;
        FieldDef fdAux;
        
        Vector vFileNames = new Vector();
        String sFileName;
        
        
        for (int i=0;i<vDeleteAttachments.size();i++)
        {
            fdAux = (FieldDef) vDeleteAttachments.get(i);
            psSelect.setInt(1,fdAux.getId());
            rsAtt = psSelect.executeQuery();

            rsAtt.next();
            
            
            sFileName=rsAtt.getString(1)+"/"+rsAtt.getInt(2)+"/"+rsAtt.getString(3);
            vFileNames.add(sFileName);
            
            psDelete.setInt(1, fdAux.getId());
            psDelete.executeUpdate();
        
        }
    
        if (rsAtt != null) rsAtt.close();
        
        if (psSelect != null) psSelect.close();
        if (psDelete != null) psDelete.close();
    
        return vFileNames;
    }
    
    private Vector selectRecords(Connection conn,Collection cRequest, Query qRequest, int iNumber) throws SQLException
    {
        
        int iOrderFieldDef = cRequest.getOrderFieldDef();
        int iBeginIndex = 0;
        
        int iRetrieveRecords = cRequest.getRetrieveRecords();
        
        if (iNumber != 0) iRetrieveRecords=iNumber;
        
        Vector vRecords = new Vector();
        Vector vConditions = new Vector();
        Condition cndAux;
        
        PreparedStatement psStmt;
            
        String sSelect =    " SELECT DISTINCT f0.idRecord, f0.val "; 
        String sFrom =      " FROM field f0 ";
        String sWhere =     " ";
        String sWhereEnd =  " ";
        String sOrder =     " ORDER BY f0.val";
        String sToNumberBegin =  "";
        String sToNumberEnd =  "";
        
        String sNullBegin = ""; //because in Oracle string empty is null
        String sNullEnd = "";   //because in Oracle string empty is null
        
        String sUpper = "";     //because Oracle is case sensitive
        String sUpperEnd = "";  //because Oracle is case sensitive
        
        
        String sDirection = "";

        if (qRequest!=null)
        {
            if (!qRequest.getOrderField().trim().equals("null"))
            {
                iOrderFieldDef=Integer.parseInt(qRequest.getOrderField());
                sDirection = " "+qRequest.getDirection()+" ";
            }
            
            iBeginIndex = qRequest.getBeginIndex();
            
            vConditions = qRequest.getConditions();
            
            if (vConditions.size()!=0)
            {
                sWhere=sWhere+"AND (";
            
                for (int i=0;i<vConditions.size();i++)
                {
                    sToNumberBegin =  "";
                    sToNumberEnd =  "";
                    sNullBegin = "";
                    sNullEnd = "";  
                    sUpper = "";
                    sUpperEnd = "";
                    
                    cndAux = (Condition) vConditions.get(i);
                
                    sFrom = sFrom+", field f"+(i+1)+" ";
                
                    if (i!=0)
                    {
                        sWhere = sWhere + " " + cndAux.getOperator() +" ";
                    }
                
                    psStmt = conn.prepareStatement("SELECT nameType FROM fielddef WHERE idFieldDef = ?");
                    
                    psStmt.setInt(1,cndAux.getIdFieldDef());
                    
                    ResultSet rsFieldType = psStmt.executeQuery();
                    
                    rsFieldType.next();
        
                    String sType = rsFieldType.getString(1).trim();
        
                    if (sType.equals("decimal") || sType.equals("integer"))
                    {
                        if (pDB.getProperty("dbSystem").equals("mysql"))
                        {
                            sToNumberBegin = "";
                            sToNumberEnd = "*1";
                        }
                        else if (pDB.getProperty("dbSystem").equals("oracle"))
                        {
                            sToNumberBegin = "to_number(";
                            sToNumberEnd = ")";
                        }
                    }
                    
                    if (pDB.getProperty("dbSystem").equals("oracle"))
                    {
                        //Case to search a null value in Oracle
                        if ((cndAux.getComparator().equals("=") && (cndAux.getValue().trim().equals(""))))
                        {
                            sNullBegin = "(";
                            sNullEnd=" or f"+(i+1)+".val is null)";   
                        }
                        else if ((cndAux.getComparator().equals("!=") && (cndAux.getValue().trim().equals(""))))
                        {
                            sNullBegin = "(";
                            sNullEnd=" or f"+(i+1)+".val is not null)";   
                        }
                        
                        if (sType.equals("text"))
                        {
                            sUpper=" upper(";
                            sUpperEnd=")";                       
                        }
                    }
                    
                    sWhere=sWhere+"( f"+(i+1)+".idFieldDef= ? " +
                            " AND "+sNullBegin+sToNumberBegin+sUpper+"f"+(i+1)+".val"+sUpperEnd+sToNumberEnd+" "+cndAux.getComparator()+sToNumberBegin+sUpper+"?"+sUpperEnd+sToNumberEnd+sNullEnd+")";
                
                    sWhereEnd = sWhereEnd + "AND f"+i+".idrecord=f"+(i+1)+".idrecord ";
                }
            
                sWhere=sWhere+")";
            }            
        }
        
        psStmt = conn.prepareStatement("SELECT nameType FROM fielddef WHERE idFieldDef = ?");
        
        psStmt.setInt(1,iOrderFieldDef);
        
        ResultSet rsFieldType = psStmt.executeQuery();
                
        rsFieldType.next();
        
        String sType = rsFieldType.getString(1).trim();
        
        if (sType.equals("decimal") || sType.equals("integer"))
        {
            if (pDB.getProperty("dbSystem").equals("mysql"))
            {
                sOrder = "ORDER BY f0.val*1 ";
            }
            else if (pDB.getProperty("dbSystem").equals("oracle"))
            {
                sOrder = "ORDER BY to_number(f0.val)";
            }
        }
        
        sOrder = sOrder + sDirection;
        
        sWhere = "WHERE f0.idFieldDef = "+iOrderFieldDef+" "+sWhere; 
        
        String sNum = "";
        
        String queryTest = sSelect+sFrom+sWhere+sWhereEnd+sOrder+sNum; //Used in DEBUG MODE
        
        psStmt = conn.prepareStatement(sSelect+sFrom+sWhere+sWhereEnd+sOrder+sNum);
       
        for (int i=0;i<vConditions.size();i++)
        {
            cndAux = (Condition) vConditions.get(i);
            
            psStmt.setInt(2*i+1, cndAux.getIdFieldDef());
            psStmt.setString(2*i+2, cndAux.getValue());
        }
        
        ResultSet rsRecords = psStmt.executeQuery();
        
        psStmt = 
            conn.prepareStatement("SELECT nameField,val from fielddef fd,field f " +
                "WHERE f.idRecord = ? " +
                    "AND fd.idFieldDef = f.idFieldDef ORDER BY fd.position");
            
        Record rAux;
        Field fAux;
        
        int index=0;
                
        while (rsRecords.next())
        {
            if ( (iBeginIndex <= index)  && (index < (iBeginIndex+iRetrieveRecords)) )
            {
                rAux = new Record();
                rAux.setId(rsRecords.getInt(1));
                
                psStmt.setInt(1,rAux.getId());
            
                ResultSet rsFields = psStmt.executeQuery();
                
                while (rsFields.next())
                {
                    fAux = new Field();
                    fAux.setName(rsFields.getString(1));
                    String valAux = rsFields.getString(2);
                    if (valAux==null) valAux=""; //Because in Orcle empty String is (null)
                    fAux.setValue(valAux);
                    rAux.addField(fAux);
                    rAux.setId(rsRecords.getInt(1));
                }
                
                vRecords.add(rAux);
            }
            index++;
        }
        
        if (psStmt!=null) psStmt.close();
       
        return vRecords;   
    }
    
    private int countRecords(Connection conn,Collection cRequest, Query qRequest) throws SQLException
    {
        int iOrderFieldDef = cRequest.getOrderFieldDef();
        int iCount=0;
        
        Vector vRecords = new Vector();
        
        PreparedStatement psStmt;
        
        String sSelect =    " SELECT COUNT(DISTINCT f0.idRecord) ";
        String sFrom =      " FROM field f0 ";
        String sWhere =     " ";
        String sWhereEnd =  " ";
        String sToNumberBegin = "";
        String sToNumberEnd = "";
        String sNullBegin = "";
        String sNullEnd = "";  
        
        String sUpper = "";     //because Oracle is case sensitive
        String sUpperEnd = "";  //because Oracle is case sensitive
       
        Condition cndAux;
        
            
            Vector vConditions = qRequest.getConditions();
            
            if (vConditions.size()!=0)
            {
                sWhere=sWhere+"AND (";
            
                for (int i=0;i<vConditions.size();i++)
                {
                    sToNumberBegin =  "";
                    sToNumberEnd =  "";
                    
                    sNullBegin = "";
                    sNullEnd = "";  
                    sUpper= "";
                    sUpperEnd= "";
                    
                    cndAux = (Condition) vConditions.get(i);
                
                    sFrom = sFrom+", field f"+(i+1)+" ";
                
                    if (i!=0)
                    {
                        sWhere = sWhere + " " + cndAux.getOperator() +" ";
                    }
                    
                    psStmt = conn.prepareStatement("SELECT nameType FROM fielddef WHERE idFieldDef = ?");
                    
                    psStmt.setInt(1,cndAux.getIdFieldDef());
                    
                    ResultSet rsFieldType = psStmt.executeQuery();
                    
                    rsFieldType.next();
        
                    String sType = rsFieldType.getString(1).trim();
        
                    if (sType.equals("decimal") || sType.equals("integer"))
                    {
                        if (pDB.getProperty("dbSystem").equals("mysql"))
                        {
                            sToNumberBegin = "";
                            sToNumberEnd = "*1";
                        }
                        else if (pDB.getProperty("dbSystem").equals("oracle"))
                        {
                            sToNumberBegin = "to_number(";
                            sToNumberEnd = ")";
                        }
                    }
                    
                    if (pDB.getProperty("dbSystem").equals("oracle"))
                    {
                        //Case to search a null value in Oracle
                        if ((cndAux.getComparator().equals("=") && (cndAux.getValue().trim().equals(""))))
                        {
                            sNullBegin = "(";
                            sNullEnd=" or f"+(i+1)+".val is null)";   
                        }
                        else if ((cndAux.getComparator().equals("!=") && (cndAux.getValue().trim().equals(""))))
                        {
                            sNullBegin = "(";
                            sNullEnd=" or f"+(i+1)+".val is not null)";   
                        }
                        
                        if (sType.equals("text"))
                        {
                            sUpper=" upper(";
                            sUpperEnd=")";                       
                        }
                        
                    }
                     
                    sWhere=sWhere+"( f"+(i+1)+".idFieldDef= ? " +
                         " AND "+sNullBegin+sToNumberBegin+sUpper+"f"+(i+1)+".val"+sUpperEnd+sToNumberEnd+" "+cndAux.getComparator()+sToNumberBegin+sUpper+"?"+sUpperEnd+sToNumberEnd+sNullEnd+")";
                
                    sWhereEnd = sWhereEnd + "AND f"+i+".idrecord=f"+(i+1)+".idrecord ";
                }
            
                sWhere=sWhere+")";
            }            
        
        
        sWhere = "WHERE f0.idFieldDef = "+iOrderFieldDef+" "+sWhere; 
        
        psStmt = conn.prepareStatement(sSelect+sFrom+sWhere+sWhereEnd);
        
        for (int i=0;i<vConditions.size();i++)
        {
            cndAux = (Condition) vConditions.get(i);
            
            psStmt.setInt(2*i+1, cndAux.getIdFieldDef());
            psStmt.setString(2*i+2, cndAux.getValue());
        }
        
        ResultSet rsCount = psStmt.executeQuery();
        
        rsCount.next();
        
        iCount = rsCount.getInt(1);
        
        if (rsCount !=null) rsCount.close();                 
        if (psStmt!=null) psStmt.close();
                
        return iCount;   
    }
    
    private boolean checkFields(Connection conn, Collection cRequest, FieldDef fdNew) throws SQLException
    {
        boolean bOk = true;
        
        Statement stmt=conn.createStatement();    
        
        ResultSet rsValues = stmt.executeQuery(  "SELECT val FROM field " +
                                                "WHERE idFieldDef = "+fdNew.getId());
        
        while(rsValues.next() && bOk)
        {
            String valAux = rsValues.getString(1);
            if (valAux==null) valAux=""; //Because in Oracle empty String is (null)
            
            if (!fdNew.checkValue(valAux))
            {
                bOk = false;
            }
        }
        
        if (rsValues != null) rsValues.close();
        if (stmt != null) stmt.close();
        
        return bOk;
    }
    
    private void updateFieldsSelect(Connection conn, Collection cRequest,  FieldDef fdNew) throws SQLException
    {
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT val,idFieldDef,idRecord FROM field WHERE idFieldDef = ?");
        PreparedStatement psStmtUp = conn.prepareStatement("UPDATE field SET val = ? WHERE idFieldDef = ? AND idRecord = ?");
       
        psStmt.setInt(1,fdNew.getId());
        
        ResultSet rsValues = psStmt.executeQuery();
        
        while(rsValues.next())
        {
            String valAux = rsValues.getString(1);
            if (valAux==null) valAux=""; //Because in Oracle empty String is (null)
            
            if (!fdNew.checkValue(valAux))
            {
                psStmtUp.setString(1,fdNew.getDefaultValue());
                psStmtUp.setInt(2,rsValues.getInt(2));
                psStmtUp.setInt(3,rsValues.getInt(3));
                psStmtUp.executeUpdate();
            }
        }
        
        if (rsValues != null) rsValues.close();
        if (psStmt != null) psStmt.close();
        if (psStmtUp != null) psStmtUp.close();
    }
    
    private void insertRecord(Connection conn, User uRequest, Owner oRequest, Collection cRequest, Record rRequest) throws SQLException
    {
        Statement stmt = conn.createStatement();
       
        String sSQL = "INSERT INTO record(idRecord,idCollection,idOwner) values(null,"+cRequest.getId()+",'"+uRequest.getUserId()+"')";
        if (pDB.getProperty("dbSystem").equals("mysql")){
            stmt.executeUpdate(sSQL, Statement.RETURN_GENERATED_KEYS);
        }else{
            stmt.executeUpdate(sSQL);
        }
                
        ResultSet  rsIdRecord=null;
 
        if (pDB.getProperty("dbSystem").equals("mysql"))
        {
            rsIdRecord = stmt.getGeneratedKeys();
        }
        else if (pDB.getProperty("dbSystem").equals("oracle"))
        {
            rsIdRecord = stmt.executeQuery("select IDRECORD_seq.currval from dual");
        }
        
        rsIdRecord.next();
        int iIdRecord = rsIdRecord.getInt(1);
        
        rRequest.setId(iIdRecord);
        cRequest.setNumRecords(cRequest.getNumRecords()+1);
        
        if (rsIdRecord != null) rsIdRecord.close();
        if (stmt != null) stmt.close();
    }
    
    private Vector checkRecord(Connection conn, Collection cRequest, Record rRequest) throws SQLException,DataBaseException,ServerException
    {
        Vector vFieldsDefs =  selectFieldsDefs(conn,cRequest);
            
        Vector vFields = rRequest.getFields();
        Vector vAttachmentsDefined = new Vector();
        String sType;
            
        if (cRequest.getNumFieldsDefs() != vFields.size()) throw new DataBaseException(DataBaseException.NO_FIELD_DEF_MATCH);
            
        boolean bExists=true,bCheck=true;
            
        Field fAux=null;
        FieldDef fdAux;
        
        for (int i=0;i<vFields.size() && bCheck && bExists;i++)
        {
            fAux = (Field) vFields.get(i);
                
            bExists=false;
                                
            for (int j=0;j<vFieldsDefs.size() && !bExists;j++)
            {
                fdAux = (FieldDef) vFieldsDefs.get(j);
                    
                if (fAux.getName().equals(fdAux.getName()))
                {
                    bExists = true;
                                          
                    sType = fdAux.getType();
                        
                    if (sType.equals("image") || sType.equals("sound"))
                    {
                        if (fAux.getValue().equals("delete"))
                        {
                            vAttachmentsDefined.add(fdAux);
                        }
                        else if (!fAux.getValue().equals("null"))
                        {
                            String fileName = fAux.getValue();
                            String name = fileName.substring(0,fileName.lastIndexOf("."));
                            String ext = fileName.substring(fileName.lastIndexOf(".")+1);
                            
                            fAux.setValue(Utils.toValidFileName(name)+"."+ext);
                            
                            if (existsAttachment(conn,cRequest,fAux))
                            {
                                
                                fileName = fAux.getValue();
                                
                                name = fileName.substring(0,fileName.lastIndexOf("."));
                                ext = fileName.substring(fileName.lastIndexOf(".")+1);
                                
                                int index = 1;
                                
                                boolean bExistsAttachment = true;
                                                                
                                while(bExistsAttachment)
                                {
                                    fAux.setValue(name+"_"+index+"."+ext);
                                    
                                    bExistsAttachment = existsAttachment(conn,cRequest,fAux);
                                    index++;
                                    
                                }
                                rRequest.modifyFieldValue(fAux.getName(),fAux.getValue());
                            }
                            
                            vAttachmentsDefined.add(fdAux);
                        }
                    }
                    else 
                    {    
                        bCheck = fdAux.checkValue(fAux.getValue());
                    
                        if (sType.equals("decimal"))
                        {
                            rRequest.modifyFieldValue(fAux.getName(), convertToDecimal(fAux.getValue()));
                        }
                    }

                    vFieldsDefs.remove(j);
                }
            }   
        }
            
        if (!bExists) throw new DataBaseException(DataBaseException.NO_FIELD_DEF_MATCH);
        if (!bCheck) throw new DataBaseException(DataBaseException.NO_FIELD_CHECK_PTIES,fAux.getName());
            
        return vAttachmentsDefined;
    }
    
    
    private void insertFields(Connection conn, Collection cRequest, Record rRequest) throws SQLException
    {
        int iIdRecord = rRequest.getId();
                
        PreparedStatement psStmt = conn.prepareStatement("INSERT INTO field VALUES(" +
                                                        "(SELECT idFieldDef FROM fielddef WHERE idCollection="+cRequest.getId()+" " +
                                                        " AND nameField = ?)" +
                                                        ","+iIdRecord+",?)");
        Vector vFields = rRequest.getFields();
        Field fAux;
        
        for (int i=0;i<vFields.size();i++)
        {
            fAux = (Field) vFields.get(i);
            psStmt.setString(1,fAux.getName());
            psStmt.setString(2, fAux.getValue());
            psStmt.execute();
        }
        if (psStmt!=null) psStmt.close();
    }
        
    private boolean checkAttachments(Vector vAttachmentsDefined, Vector vAttachments) throws DataBaseException
    {
        FieldDef fdAux;
        AttachmentPart apAux;
        String sContentId,sFieldName; 
        
        int iAttachmentsSize=0;
    
        for (int i=0; i< vAttachments.size();i++)
        {
            apAux=(AttachmentPart) vAttachments.get(i);
            
            sContentId = apAux.getContentId();
            
            sFieldName = sContentId.substring(0, sContentId.indexOf("/"));
            
            boolean bFound = false;
            
            for (int j=0;j<vAttachmentsDefined.size() && !bFound;j++)
            //Look for the field definiton of the given attachment
            {
                fdAux = (FieldDef) vAttachmentsDefined.get(j);
                
                if (sFieldName.equals(fdAux.getName()))
                {
                    if (!fdAux.checkValue(apAux.getContentType()))
                    //Check field type and file type attached
                    {
                        return false;
                    }

                    apAux.setContentId(apAux.getContentId()+"/"+fdAux.getId());
                    
                    bFound = true;
                }
            }
        }
        return true;
    }
    
    private Vector selectAttachments(Connection conn, Collection cRequest, Vector vRecords) throws SQLException
    {
        Vector vAttachments = new Vector();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT idUser,idCollection,origName FROM attachment WHERE idRecord = ?");
        Record rAux;
        ResultSet rs = null;
        Attachment aAux;
        
        for (int i=0;i<vRecords.size();i++)
        {
            rAux = (Record) vRecords.get(i);
            
            psStmt.setInt(1,rAux.getId());
            
            rs = psStmt.executeQuery();
            
            while (rs.next())
            {
                aAux = new Attachment();
                
                aAux.setUrl(rs.getString(1)+"/"+rs.getInt(2)+"/"+rs.getString(3));
                
                aAux.setOriginalName(rs.getString(3));
            
                vAttachments.add(aAux);            
            }
        }
        
        if (rs!=null) rs.close();
        if (psStmt != null) psStmt.close();
        
        return vAttachments;
    }
    
    private void getAttachmentsModified(Record rRequest,Record rOld,Vector vAttachmentsDefined,Vector vAddAttachments,Vector vDeleteAttachments)
    {
        FieldDef fdAux;
        String sOldValue,sNewValue;
        
        for (int i=0; i<vAttachmentsDefined.size();i++)
        {
            fdAux = (FieldDef) vAttachmentsDefined.get(i);
            
            sOldValue = rOld.getFieldValue(fdAux.getName());
            sNewValue = rRequest.getFieldValue(fdAux.getName());
            
            if (!sOldValue.equals(sNewValue))
            {
                if ( sNewValue.equals("delete") && sOldValue.equals("null") )
                {
                    //No Possible case trying to delete an image that does not exist
                    rRequest.modifyFieldValue(fdAux.getName(), "null");
                }
                else if(sOldValue.equals("null"))
                {
                    vAddAttachments.add(fdAux);
                    
                }
                else if (sNewValue.equals("delete"))
                {
                    rRequest.modifyFieldValue(fdAux.getName(), "null");
                    vDeleteAttachments.add(fdAux);
                }
                else
                {
                    vAddAttachments.add(fdAux);
                    vDeleteAttachments.add(fdAux);
                }            
            }
        }
    }
    
    private Vector insertAttachments(Connection conn, User uRequest, Collection cRequest,  Vector vAttachments, Record rRequest) throws SQLException
    {
        Vector vFiles = new Vector();
        
        AttachmentPart apAux;
        String sContentId,sFieldName,sOriginalName,sExtension,sFileName;
        int iIdFieldDef;
        String[] content;
        String[] extension;
        
        String sUserId = uRequest.getUserId();
                
        PreparedStatement psStmt = conn.prepareStatement("INSERT into attachment values(null,?,?,?,?,?,?,CURRENT_DATE)");
        
        for (int i=0; i<vAttachments.size();i++)
        {
            apAux=(AttachmentPart) vAttachments.get(i);
            
            sContentId = apAux.getContentId();
            
            content = sContentId.split("/");
            sFieldName = content[0];
            sOriginalName = rRequest.getFieldValue(sFieldName);
            iIdFieldDef = Integer.parseInt(content[2]);
            
            extension = apAux.getContentType().split("/");
            sExtension= "UNK";
            if (extension.length > 0)   sExtension = extension[1];
            
            psStmt.setString(1,sExtension);
            psStmt.setString(2,sOriginalName);
            psStmt.setString(3,sUserId);
            psStmt.setInt(4,cRequest.getId());
            psStmt.setInt(5,rRequest.getId());
            psStmt.setInt(6,iIdFieldDef);
            
            psStmt.executeUpdate();
           
            sFileName = sUserId+"/"+cRequest.getId()+"/"+sOriginalName;
            
            rRequest.modifyFieldValue(sFieldName,sFileName);
            
            apAux.setContentId(sFileName);
            
            vFiles.add(apAux);
        }
        
        if (psStmt != null) psStmt.close();
        
        return vFiles;
    }
    
    private void updateFieldDef(Connection conn, FieldDef fdNew) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("UPDATE fielddef SET properties = ? ,nameField = ?" +
                " WHERE idFieldDef = ?");
        
        psStmt.setString(1, fdNew.getProperties());
        psStmt.setString(2, fdNew.getName());
        psStmt.setInt(3,  fdNew.getId());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private void saveFiles(Vector vFiles)
    {
        String sFileName;
        AttachmentPart ap;
        InputStream is;
        FileOutputStream os;
        try
        {   
            for (int i=0;i<vFiles.size();i++)
            {
                ap = (AttachmentPart) vFiles.get(i);
                
                sFileName = ap.getContentId();
                
                is = ap.getDataHandler().getInputStream();
                
                File fFile = new File(getDBProperties().getProperty("dir.files")+sFileName);
                if (!fFile.getParentFile().exists()) fFile.getParentFile().mkdirs();
                
                os = new FileOutputStream(fFile);
                       
                byte[] buff = new byte[1024];
                int read = 0;
                
                while ((read = is.read(buff, 0, buff.length)) != -1) 
                {
                    os.write(buff, 0, read);
                }
        
                os.flush();
                os.close();       
            }  
        }
            
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void eraseFiles(Vector vFiles)
    {
        String sFileName;
        
        try
        {   
            for (int i=0;i<vFiles.size();i++)
            {
                sFileName = (String) vFiles.get(i);
                File f = new File(getDBProperties().getProperty("dir.files")+sFileName);
                f.delete();
            }  
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private boolean checkQuery(Connection conn,Collection cRequest,Query qRequest) throws SQLException,ServerException
    {
        boolean bRes = true;
        
        Vector vConditions = qRequest.getConditions();
        String sOrderField,sDirection;
        Condition cndAux;
        ResultSet rsIdFieldDef = null;
        FieldDef fdAux;
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT idFieldDef,nameType FROM fielddef WHERE nameField=? AND idCollection="+cRequest.getId());
     
        sOrderField = qRequest.getOrderField();
        
        if (!sOrderField.equals("null"))
        {
            psStmt.setString(1, sOrderField);
            rsIdFieldDef = psStmt.executeQuery();
            
            if (!rsIdFieldDef.next()) return false;
            
            qRequest.setOrderField(""+rsIdFieldDef.getInt(1));
        }
        
        for (int i=0; i<vConditions.size() && bRes;i++)
        {
            cndAux = (Condition) vConditions.get(i);
            
            psStmt.setString(1, cndAux.getFieldName());
            
            rsIdFieldDef = psStmt.executeQuery();
            
            if (rsIdFieldDef.next())
            {
                fdAux = FieldDef.createFieldDef(rsIdFieldDef.getString(2));
                
                if (fdAux.checkComparator(cndAux.getComparator().trim()))
                {
                    cndAux.setIdFieldDef(rsIdFieldDef.getInt(1));
                }
                else bRes=false;
                
                if (fdAux.getType().equals("decimal"))
                {
                    String sValueAux = cndAux.getValue();
                    sValueAux = sValueAux.replace(',','.'); 
                    //Now we accept separation in decimals both (. and ,)
                    try
                    {
                        Float.parseFloat(sValueAux);
                    }
                    catch (NumberFormatException nfe)
                    {
                        bRes=false;
                    }
                             
                    sValueAux = convertToDecimal(cndAux.getValue());
                            
                    cndAux.setValue(sValueAux);
                                                    
                }
                else if (fdAux.getType().equals("integer"))
                {
                    String sValueAux = cndAux.getValue();
                    
                    if (sValueAux.startsWith("+")) sValueAux = sValueAux.substring(1);
                    try
                    {
                        Integer.parseInt(sValueAux);
                    }
                    catch (NumberFormatException nfe)
                    {
                        bRes=false;
                    }
                    cndAux.setValue(sValueAux);
                            
                }
                
            }
            else bRes =false;
        }
        
        if (rsIdFieldDef != null) rsIdFieldDef.close();
        
        if (psStmt != null) psStmt.close();
        
        return bRes;
    }
    
    private void changeFieldDefsPositions(Connection conn,Collection cRequest,int deletedPosition) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("UPDATE fielddef SET position=position-1 WHERE idCollection = ? AND position>?");
        psStmt.setInt(1, cRequest.getId());
        psStmt.setInt(2,deletedPosition);
        psStmt.executeUpdate();
        
        if(psStmt != null) psStmt.close();
    }
    
    private String convertToDecimal(String sValue) throws ServerException
    {
        String sConvertedDecimal;
                            
        char DECIMAL_POINT_CHAR = this.getDBProperties().getProperty("decimal.point.char").charAt(0);
                            
        sConvertedDecimal = sValue.replace('.', DECIMAL_POINT_CHAR);
        sConvertedDecimal = sConvertedDecimal.replace(',',DECIMAL_POINT_CHAR);
        return sConvertedDecimal;
    }
    
    protected boolean existsGuest(Connection conn, User uRequest, Collection cRequest, Guest gRequest) throws SQLException
    {
        boolean bRes;
        String sUser = uRequest.getUserId();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT permission,lastaccess FROM guests WHERE idUser = ? AND idCollection = ? AND idGuest = ?");
        
        psStmt.setString(1,uRequest.getUserId());
        psStmt.setInt(2,cRequest.getId());
        psStmt.setString(3,gRequest.getUserId());
        
        ResultSet rsRes = psStmt.executeQuery();
        
        if (rsRes.next())
        {
            gRequest.setPermission(rsRes.getInt(1));
            gRequest.setLastAcces(rsRes.getDate(2));
            
            bRes=true;
        }
        else bRes = false;
        
        if (rsRes!=null) rsRes.close();
        if (psStmt!=null) psStmt.close();
        
        return bRes;
    }
    
    private void insertGuest(Connection conn, User uRequest, Collection cRequest,  Guest gRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("INSERT INTO guests values(?,?,?,?,NULL)");
        
        psStmt.setString(1,uRequest.getUserId());
        psStmt.setInt(2,cRequest.getId());
        psStmt.setString(3,gRequest.getUserId());
        psStmt.setInt(4,gRequest.getPermission());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private void deleteGuest(Connection conn, User uRequest, Collection cRequest,  Guest gRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("DELETE FROM guests WHERE idUser=? AND idCollection = ? AND idGuest = ?");
        
        psStmt.setString(1,uRequest.getUserId());
        psStmt.setInt(2,cRequest.getId());
        psStmt.setString(3,gRequest.getUserId());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
     
    private void updateGuest(Connection conn, User uRequest, Collection cRequest,  Guest gRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("UPDATE guests SET permission = ?, lastAccess = ? WHERE idUser = ? AND idCollection = ? AND idGuest = ?");
        
        System.out.println((java.sql.Date)gRequest.getLastAccess());
        
        psStmt.setInt(1,gRequest.getPermission());
        psStmt.setDate(2,(java.sql.Date)gRequest.getLastAccess());
        psStmt.setString(3,uRequest.getUserId());
        psStmt.setInt(4,cRequest.getId());
        psStmt.setString(5,gRequest.getUserId());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private void updateLastAccess(Connection conn, Collection cRequest,  Guest gRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("UPDATE guests SET lastAccess = CURRENT_DATE WHERE idCollection = ? AND idGuest = ?");
        
        psStmt.setInt(1,cRequest.getId());
        psStmt.setString(2,gRequest.getUserId());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private Vector selectGuests(Connection conn, Collection cRequest) throws SQLException
    {
        Vector vGuests = new Vector();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT idGuest,permission,lastAccess FROM guests WHERE idCollection = ? ORDER BY idGuest");
        
        psStmt.setInt(1,cRequest.getId());
        
        ResultSet rsGuests = psStmt.executeQuery();
     
        Guest gAux;
        String sIdGuest;
        int iPermission;
        java.util.Date dLastAcces;
        
        while(rsGuests.next())
        {
            sIdGuest = rsGuests.getString(1);
            iPermission = rsGuests.getInt(2);
            dLastAcces = rsGuests.getDate(3);
            
            gAux = new Guest(sIdGuest);
            gAux.setPermission(iPermission);
            gAux.setLastAcces(dLastAcces);
            vGuests.add(gAux);
        }
        
        if (rsGuests!=null) rsGuests.close();
        if (psStmt!=null) psStmt.close();
        
        return vGuests;
    }
    
    private void deleteGuests(Connection conn, Collection cRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("DELETE FROM guests WHERE idCollection = ?");
        
        psStmt.setInt(1,cRequest.getId());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private void insertLogOperation(Connection conn, Collection cRequest, User uRequest, int operation, Record rRequest) throws SQLException
    {   
        String sTextAux  = selectTextField(conn,cRequest);
        
        if (sTextAux != null)
        {
            sTextAux = sTextAux +" = "+ rRequest.getFieldValue(sTextAux);
                
            if (sTextAux.length()>=50) sTextAux = sTextAux.substring(0,46) +"..."; //To avoid crash the database
        }
        else
        {
            sTextAux ="idRecord = "+rRequest.getId();
        }
        
        PreparedStatement psStmt = conn.prepareStatement("INSERT INTO t_log(idCollection,idUser,op,idRecord,text,d_date) values(?,?,?,?,?,CURRENT_DATE)");
        
        psStmt.setInt(1,cRequest.getId());
        psStmt.setString(2,uRequest.getUserId());
        psStmt.setInt(3,operation);
        psStmt.setInt(4,rRequest.getId());
        psStmt.setString(5,sTextAux);
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private Vector selectLog(Connection conn, Collection cRequest) throws SQLException
    {
        Vector vLogOperations = new Vector();
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT idUser,op,idRecord,text,d_date FROM t_log WHERE idCollection = ? ORDER BY d_date");
        
        psStmt.setInt(1,cRequest.getId());
        
        ResultSet rsLog = psStmt.executeQuery();
     
        LogOperation loAux;
        Record rAux;
        String sIdGuest;
        String sTextAux,sValueAux;
        
        int iPermission;
        java.util.Date dAux;
        
        while(rsLog.next())
        {
            loAux = new LogOperation();
            
            loAux.setIdUser(rsLog.getString(1));
            loAux.setOperation(rsLog.getInt(2));
            loAux.setIdRecord(rsLog.getInt(3));
            loAux.setText(rsLog.getString(4));
            loAux.setDate(rsLog.getDate(5));
            
            vLogOperations.add(loAux);
        }
        
        if (rsLog!=null) rsLog.close();
        if (psStmt!=null) psStmt.close();
        
        return vLogOperations;
    }
    
    private void deleteLog(Connection conn, Collection cRequest) throws SQLException
    {
        
        PreparedStatement psStmt = conn.prepareStatement("DELETE FROM t_log WHERE idCollection = ?");
        
        psStmt.setInt(1,cRequest.getId());
        
        psStmt.executeUpdate();
        
        if (psStmt!=null) psStmt.close();
    }
    
    private String selectTextField(Connection conn, Collection cRequest) throws SQLException
    {
        String sResTextField = null;
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT nameField FROM fielddef WHERE idCollection = ? AND nameType = ? ORDER BY idFieldDef");
        
        psStmt.setInt(1,cRequest.getId());
        psStmt.setString(2,"text");
        
        ResultSet rsTF = psStmt.executeQuery();
        
        if (rsTF.next())    sResTextField = rsTF.getString(1);
        else
        {
            psStmt.setString(2,"select");
            rsTF = psStmt.executeQuery();
            
            if (rsTF.next())    sResTextField = rsTF.getString(1);
        }
     
        if (rsTF!=null) rsTF.close();
        if (psStmt!=null) psStmt.close();
        
        return sResTextField;
    }
    
    private void addTags(Connection conn, Vector vTags) throws SQLException
    {
        ResultSet rs = null; 
        int iTimes; String sTag="";
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT times FROM tag_counter WHERE tag = ?");
        PreparedStatement psStmtIns = conn.prepareStatement("INSERT INTO tag_counter(tag,times) VALUES(?,1)");
        PreparedStatement psStmtUp = conn.prepareStatement("UPDATE tag_counter SET times = ? WHERE tag = ?");
        
        for (int i=0;i<vTags.size();i++)
        {
            sTag = (String)vTags.get(i);
            psStmt.setString(1,sTag);
            rs = psStmt.executeQuery();
            
            if (rs.next()) 
            {
                iTimes = rs.getInt(1)+1;
                psStmtUp.setInt(1,iTimes);
                psStmtUp.setString(2,sTag);
                psStmtUp.executeUpdate();  
            }
            else
            {
                psStmtIns.setString(1,sTag);
                psStmtIns.executeUpdate();
            }
        }
        
        if (rs!=null) rs.close();
        if (psStmt!=null) psStmt.close();
        if (psStmtIns!=null) psStmtIns.close();
        if (psStmtUp!=null) psStmtUp.close();
    }
    
    private void deleteTags(Connection conn, Vector vTags) throws SQLException
    {
        ResultSet rs = null; 
        int iTimes; String sTag="";
        
        PreparedStatement psStmt = conn.prepareStatement("SELECT times FROM tag_counter WHERE tag = ?");
        PreparedStatement psStmtDel = conn.prepareStatement("DELETE FROM tag_counter WHERE tag = ?");
        PreparedStatement psStmtUp = conn.prepareStatement("UPDATE tag_counter SET times = ? WHERE tag = ?");
        
        for (int i=0;i<vTags.size();i++)
        {
            sTag = (String)vTags.get(i);
            psStmt.setString(1,sTag);
            rs = psStmt.executeQuery();
            
            if (rs.next()) 
            {
                iTimes = rs.getInt(1)-1;
                
                if (iTimes==0)
                {
                    psStmtDel.setString(1,sTag);
                    psStmtDel.executeUpdate();
                }
                else
                {
                    psStmtUp.setInt(1,iTimes);
                    psStmtUp.setString(2,sTag);
                    psStmtUp.executeUpdate();
                }
             }
        }
        
        if (rs!=null) rs.close();
        if (psStmt!=null) psStmt.close();
        if (psStmtDel!=null) psStmtDel.close();
        if (psStmtUp!=null) psStmtUp.close();
    }
    
    private void closeConnection(Connection conn) throws ServerException
    {
        try
        {
            if (conn != null && !conn.isClosed()) 
            {
                conn.close();
            }
        }
        catch (SQLException sqle)
        {
            throw new ServerException(sqle);
        }
    }
    
   
    private void closeStatement(Statement stmt) throws ServerException
    {
        try
        {
            if (stmt != null) 
            {
                stmt.close();
            }
        }
        catch (SQLException sqle)
        {
            throw new ServerException(sqle);
        }
    }
    
    private void iniTransaction(Connection conn) throws ServerException
    {
        try
        {
            conn.setAutoCommit(false);
        }
        catch (SQLException sqle)
        {
            throw new ServerException(sqle);
        }
    }
    
    private void commitTransaction(Connection conn) throws ServerException
    {
        try
        {
            conn.commit();
        }
        catch (SQLException sqle)
        {
            throw new ServerException(sqle);
        }
    }
    
    private void rollbackTransaction(Connection conn) throws ServerException
    {
        try
        {
            conn.rollback();
        }
        catch (SQLException sqle)
        {
            throw new ServerException(sqle);
        }
    }
}