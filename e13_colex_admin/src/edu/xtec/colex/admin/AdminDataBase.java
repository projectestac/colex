/*
 * AdminDataBase.java
 *
 */

package edu.xtec.colex.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.domain.Field;
import edu.xtec.colex.domain.User;
import edu.xtec.colex.exception.DataBaseException;
import edu.xtec.colex.exception.ServerException;


/**
 *
 * @author ogarci10
 */
public class AdminDataBase extends DataBase
{
    public final static String ADMINCONF_PATH = "/edu/xtec/colex/admin/";
    public final static String ADMINCONF_LOG_FILE = "adminLog.properties";
    
    protected static Logger logger;
    
    /** Creates a new instance of AdminDataBase */
    public AdminDataBase() 
    {
        super();
        iniLogger();
    }

    
    public boolean isAdmin(String sUserId)
    {
        Connection conn = null;
        
        ResultSet rs = null;
        PreparedStatement psStmt = null;

        int quota = 0;
        logger.debug("Admindatabase is Admin " + sUserId);
        try
        {
            conn = getConnection().getConnection();
            psStmt = conn.prepareStatement("SELECT * FROM t_admin WHERE iduser = ?");
            
            psStmt.setString(1,sUserId);
            logger.debug("Admindatabase is Admin  select " + psStmt);
                
            rs = psStmt.executeQuery();
            
            if (rs.next()) 
            {   
                if (rs!=null) rs.close();
                if (psStmt!=null) psStmt.close();
                logger.debug("Admindatabase is Admin  OK ");
                return true;
            }
        }
        catch(Exception e)
        {
            logger.error("Admindatabase is Admin  Error " + e);
            e.printStackTrace();
            return false;
        }
        finally
        {
            freeConnection();
        }
        
        
        return false;
    }
    
    
    
    public int getQuota(User user) throws DataBaseException,ServerException
    {
        Connection conn = null;
        
        int quota = 0;
                
        try
        {
            conn = getConnection().getConnection();
            PreparedStatement psStmt = conn.prepareStatement("SELECT diskquota FROM t_user WHERE iduser = ?");
            
            psStmt.setString(1,user.getUserId());
                
            ResultSet rs = psStmt.executeQuery();
            
            if (rs.next()) 
            {
                quota = rs.getInt(1);
            }
            else throw new DataBaseException(DataBaseException.NO_VALID_USER);
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        finally
        {
            freeConnection();
        }
        
        return quota;
    }
    
    public void modifyQuota(User user, int newQuota) throws DataBaseException,ServerException
    //Caldra fer lex Tx
    {
        Connection conn = null;
        
        try
        {
            conn = getConnection().getConnection();
            PreparedStatement psStmt = conn.prepareStatement("UPDATE t_user SET diskquota = ?  WHERE iduser = ?");
            
            psStmt.setInt(1, newQuota);
            psStmt.setString(2,user.getUserId());
                
            psStmt.executeUpdate();
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        finally
        {
            freeConnection();
        }
        
    } 
    
    public Vector listUsers() throws DataBaseException,ServerException
    {
        Vector vUsers = new Vector();
    
        Connection conn = null;
        
        try
        {
            conn = getConnection().getConnection();
            PreparedStatement psStmt = conn.prepareStatement("SELECT idUser FROM t_user ORDER BY idUser");
            
                
            ResultSet rsUsers = psStmt.executeQuery();
            
            User uAux;
            
            while (rsUsers.next())
            {
                uAux = new User("");
                
                uAux.setUserId(rsUsers.getString(1));
                
                vUsers.add(uAux);
                
            }
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        finally
        {
            freeConnection();
        }
    
        return vUsers;
    }
    
    
    
    public Vector listAttachmentsUser(User user) throws DataBaseException,ServerException
    {
        Vector vAttachments = new Vector();
    
        Connection conn = null;
        
        try
        {
            conn = getConnection().getConnection();
            PreparedStatement psStmt = conn.prepareStatement("SELECT origname,nameCollection, col.idCollection,created FROM attachment att, collection col " +
                                                            "WHERE att.idUser = ?" +
                                                            "AND att.idCollection = col.idCollection ORDER BY nameCollection");
            psStmt.setString(1,user.getUserId());
                
            ResultSet rsAttachments = psStmt.executeQuery();
            
            AttachmentInfo aiAux;
            
            while (rsAttachments.next())
            {
                aiAux = new AttachmentInfo();
                
                aiAux.setFileName(rsAttachments.getString(1));
                aiAux.setNameCollection(rsAttachments.getString(2));
                aiAux.setUrl(user.getUserId()+"/"+rsAttachments.getString(3)+"/"+rsAttachments.getString(1));
                
                File fAux = new File(getDBProperties().getProperty("FILES_DIR")+aiAux.getUrl());
                
                aiAux.setSize(fAux.length());                
                
                aiAux.setCreated(rsAttachments.getDate(4));
                
                vAttachments.add(aiAux);
                
            }
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        finally
        {
            freeConnection();
        }
    
        return vAttachments;
    }
    
    
    public Vector listAttachmentsDate(String beginDate, String endDate) throws DataBaseException,ServerException
    //NOT YET TESTED ON MYSQL !!!!!!!!!!!!!!!
    {
        Vector vAttachments = new Vector();
    
        Connection conn = null;
        
        try
        {
            conn = getConnection().getConnection();
            PreparedStatement psStmt = conn.prepareStatement("SELECT origname,nameCollection, col.idCollection,created, col.idUser FROM attachment att, collection col " +
                                                            "WHERE to_date(?,'DD/MM/YYYY HH24:MI:SS') <= created " +
                                                            "AND created <= to_date(?,'DD/MM/YYYY HH24:MI:SS') " +
                                                            "AND att.idCollection = col.idCollection " +
                                                            "ORDER BY created ASC");
            
            psStmt.setString(1,beginDate+" 00:00");
            psStmt.setString(2,endDate+" 23:59");
            
                
            ResultSet rsAttachments = psStmt.executeQuery();
            
            AttachmentInfo aiAux;
            User user;
            
            while (rsAttachments.next())
            {
                aiAux = new AttachmentInfo();
                
                user = new User(rsAttachments.getString(5));
                aiAux.setUser(user);                
                
                aiAux.setFileName(rsAttachments.getString(1));
                aiAux.setNameCollection(rsAttachments.getString(2));
                aiAux.setUrl(user.getUserId()+"/"+rsAttachments.getString(3)+"/"+rsAttachments.getString(1));
                
                File fAux = new File(getDBProperties().getProperty("FILES_DIR")+aiAux.getUrl());
                
                aiAux.setSize(fAux.length());                
                
                aiAux.setCreated(rsAttachments.getDate(4));
                
                vAttachments.add(aiAux);
            }
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        finally
        {
            freeConnection();
        }
    
        return vAttachments;
    }
    
    public Vector deleteAttachment(User user, Collection collection, String sFile) throws DataBaseException,ServerException
    {
        Vector vAttachments = new Vector();
    
        Connection conn = null;
        
        try
        {
            conn = getConnection().getConnection();
            PreparedStatement psStmt = conn.prepareStatement("SELECT col.idCollection, att.idRecord, att.idFieldDef FROM attachment att, collection col " +
                                                            "WHERE col.idUser = ? " +
                                                            "AND col.nameCollection = ? " +
                                                            "AND att.idCollection = col.idCollection "+
                                                            "AND att.origName = ?");
            
            /*  where col.NAMECOLLECTION = 'Test'
                AND att.IDCOLLECTION = col.IDCOLLECTION
                AND att.ORIGNAME='pumba.jpg'    */
            
            psStmt.setString(1,user.getUserId());
            psStmt.setString(2, collection.getName());
            psStmt.setString(3, sFile);
                
            ResultSet rsAttachments = psStmt.executeQuery();
            
            
            
            if (rsAttachments.next())
            {
                
                collection.setId(rsAttachments.getInt(1));
                
                int idRecord = rsAttachments.getInt(2);
                int idFieldDef = rsAttachments.getInt(3);
                
                psStmt = conn.prepareStatement( "DELETE FROM attachment "+
                                                "WHERE idFieldDef = ?" +
                                                "AND idRecord = ?");
                psStmt.setInt(1, idFieldDef);
                psStmt.setInt(2, idRecord);
                
                psStmt.executeUpdate();
                
                psStmt = conn.prepareStatement( "UPDATE  field SET val = 'null' " +
                                                "WHERE idFieldDef = ?" +
                                                "AND idRecord = ?");
                psStmt.setInt(1, idFieldDef);
                psStmt.setInt(2, idRecord);
                
                psStmt.executeUpdate();
                                
                File fAux = new File(this.getDBProperties().getProperty("FILES_DIR")+user.getUserId()+"/"+collection.getId()+"/"+sFile);
                
                fAux.delete();
                    
                    
            }
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        finally
        {
            freeConnection();
        }
    
        return vAttachments;
    }
    
            
    public void checkFiles() throws ServerException
    {
        File fFiles = new File(getDBProperties().getProperty("FILES_DIR"));
        
        File[] users = fFiles.listFiles();
        File fAux = null;
        
        for (int i=0;i<users.length;i++)
        {
            fAux = (File)users[i]; 
            
            if (fAux.isDirectory())
            {
                
                checkFilesUser(new User(fAux.getName()));
            }
        }
    
    }
    
    public void checkFilesUser(User user) throws ServerException
    {
        Connection conn = null;
        try
        {
            conn = getConnection().getConnection();
            
            File fFiles = new File(getDBProperties().getProperty("FILES_DIR")+"/"+user.getUserId());
            
            if (!existsUser(conn,user)) edu.xtec.colex.utils.Utils.deleteDirectory(fFiles);
            
            else
            {
                String[] collections = fFiles.list();
                Collection cAux;
                
                for (int i=0;i<collections.length;i++)
                {
                    cAux = new Collection("");
                    
                    try
                    {
                        cAux.setId(Integer.parseInt(collections[i]));
                        checkFilesCollection(user,cAux);
                    }
                    catch (NumberFormatException nfe)
                    {
                        File fAux = new File(getDBProperties().getProperty("FILES_DIR")+"/"+user.getUserId()+"/"+collections[i]);
                        
                        fAux.delete();
                            
                    }
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
    }
    
    public void checkFilesCollection(User user, Collection collection) throws ServerException
    {
        Connection conn = null;
        try
        {
            conn = getConnection().getConnection();
            
            File fFiles = new File(getDBProperties().getProperty("FILES_DIR")+"/"+user.getUserId()+"/"+collection.getId());
            
            if (!existsCollection(conn,collection)) edu.xtec.colex.utils.Utils.deleteDirectory(fFiles);
            
            else
            {
                String[] attachments = fFiles.list();
                Field fAux;
                
                for (int i=0;i<attachments.length;i++)
                {
                    fAux = new Field();
                    fAux.setValue(attachments[i]);
                    
                    checkFilesAttachment(user,collection,fAux);
                    
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
    }
    
    public void checkFilesAttachment(User user, Collection collection, Field field) throws ServerException
    {
        Connection conn = null;
        try
        {
            conn = getConnection().getConnection();
            
            File fFiles = new File(getDBProperties().getProperty("FILES_DIR")+"/"+user.getUserId()+"/"+collection.getId()+"/"+field.getValue());
            
            if (!existsAttachment(conn,collection,field)) edu.xtec.colex.utils.Utils.deleteDirectory(fFiles);

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
    
    public void deleteUser(User user) throws ServerException
    {
        Connection conn = null;
        try
        {
            conn = getConnection().getConnection();
            
            PreparedStatement psStmt = conn.prepareStatement("DELETE FROM t_user WHERE idUser = ?");
            
            psStmt.setString(1,user.getUserId());
            
            psStmt.executeUpdate();
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
    
    protected boolean existsCollection(Connection conn,Collection cRequest) throws SQLException
    {
        PreparedStatement psStmt = conn.prepareStatement("SELECT * from collection WHERE idCollection = ?");
                
        psStmt.setInt(1, cRequest.getId());
                
        ResultSet rsRes = psStmt.executeQuery();
                        
        if (rsRes.next()) return true;
        else return false;
    }

    public String getSQL(String sql) 
    {
        ResultSet rs = null;
        Statement stmt = null;
        Connection conn = null;
        
        String res="";
                
        try
        {
            /*
             *  Original Builder
             *
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder =factory.newDocumentBuilder();
                Document doc = builder.newDocument();
                Element results = doc.createElement("Results");
                doc.appendChild(results);
             */
      
      
        
            conn = getConnection().getConnection();
        
            
            
            
            stmt = conn.createStatement();
            
            if ((sql.toLowerCase().indexOf("update")!= -1) && (sql.toLowerCase().indexOf("where")!= -1))
            {
                stmt.executeUpdate(sql);
            
                logger.info("UPDATE "+sql);
                
                return "";
            
            }
            
            else
            {
           
            
            rs = stmt.executeQuery(sql);

            
            
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int colCount = rsmd.getColumnCount();

            int iCol = 1;
      
            String structureXML="<structureXML></structureXML>";
            String colName="";
      
            while (iCol<=colCount)
            {
                colName=rsmd.getColumnName(iCol);
          
                structureXML = structureXML.replaceAll("</structureXML>","<col_"+(iCol-1)+">"+colName+"</col_"+(iCol-1)+"></structureXML>");
          
                iCol++;
            }
      
            String dataXML="<dataXML></dataXML>";
            String sValue="";
      
            int iRow =0;
            while (rs.next()) 
            {
                String row="<row_"+iRow+"></row_"+iRow+">";
        
                for (iCol = 1; iCol <= colCount; iCol++) 
                {
           
                    Object value = rs.getObject(iCol);
           
                    if (value!=null) 
                    {
                        sValue = value.toString();
                        
                        colName = rsmd.getColumnName(iCol);
                        
                        if (colName.equalsIgnoreCase("DESCRIPTION"))
                        {
                            sValue = removeTags(sValue);
                        }
                    
                        sValue ="<![CDATA["+sValue+"]]>";
                        
                        
                    }
                    
                    else sValue="NULL";
           
                    row = row.replaceAll("</row_"+iRow+">","<col_"+(iCol-1)+">"+sValue+"</col_"+(iCol-1)+">"+"</row_"+iRow+">");
                }
        
                dataXML = dataXML.replaceAll("</dataXML>",row+"</dataXML>");
         
                iRow++;   
            }    
      
            res=structureXML+dataXML;
      
      //ORIGINAL WHILE
      /*
      while (rs.next()) 
      {
        Element row = doc.createElement("Row");
        results.appendChild(row);
        for (int ii = 1; ii <= colCount; ii++) 
        {
           String columnName = rsmd.getColumnName(ii);
           Object value = rs.getObject(ii);
           Element node = doc.createElement(columnName);
           if (value!=null)
           {
           node.appendChild(doc.createTextNode(value.toString()));
           }
           else node.appendChild(doc.createTextNode("NULL"));
           row.appendChild(node);
        }
            
      }    
      res=getDocumentAsXml(doc);
      System.out.println(res);
       */
            }
  
        }
        catch(SQLException sqle)
        {
            System.out.println(sqle);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            freeConnection();
        }
    return res;
    }
  
    /*
     * Original Builder
     *
    public static String getDocumentAsXml(Document doc) throws TransformerConfigurationException, TransformerException 
    {
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
        // we want to pretty format the XML output
        // note : this is broken in jdk1.5 beta!
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //
        java.io.StringWriter sw = new java.io.StringWriter();
        StreamResult sr = new StreamResult(sw);
        transformer.transform(domSource, sr);
        
        return sw.toString();
    }
     */
    
    
    private void iniLogger()
    {
        if (logger==null)
        {
            Properties pLog = new Properties();
            try
            {
                InputStream isl = ServletAdmin.class.getResourceAsStream(ADMINCONF_PATH+ADMINCONF_LOG_FILE);
            
                if (isl!=null)
                {   
                    pLog.load(isl);
                }
                isl.close();
            
                File f = new File(System.getProperty("user.home"), ADMINCONF_LOG_FILE);

                if(f.exists())
                {
                    FileInputStream is=new FileInputStream(f);
                    pLog.load(is);
                    is.close();
                }
            
                PropertyConfigurator.configure(pLog);            
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        
            logger = Logger.getRootLogger();
        }        
    }
    
    private String removeTags(String string) 
    {
        StringBuffer sb = new StringBuffer("");

        boolean tag = false;

        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
	    if (ch == '<') {
                tag = true;
            } else if (ch == '>') {
                tag = false;
            } else {
                if (!tag) sb.append(string.charAt(i));
            }
        }

        return sb.toString();
    }
    
}
