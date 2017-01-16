package edu.xtec.colex.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.domain.User;
import edu.xtec.colex.exception.DataBaseException;
import edu.xtec.colex.exception.ServerException;
import edu.xtec.colex.utils.ParseMultipart;


public class ServletAdmin extends HttpServlet 
{
    static AdminDataBase adminDB = new AdminDataBase();
    
    protected static Logger logger;
    
    public final static String ADMINCONF_PATH = "/edu/xtec/colex/admin/";
    
    public static String ADMINCONF_FILE = "adminColex.properties";
    
    protected static Properties pAdmin;
    
    public final static String ADMINCONF_LOG_FILE = "adminLog.properties";
    
    protected ParseMultipart pmRequest;
    protected HttpServletResponse response;
    
    protected String responseXML;
    
    static{
    	String sEnvironment = System.getProperty("server.environment");
    	if (sEnvironment!=null){
    		ADMINCONF_FILE = "adminColex_"+sEnvironment+".properties";
    	}
    }




    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        pmRequest = new ParseMultipart(request);
        this.response = response;
        
        responseXML="<responseXML></responseXML>";
        
        iniLogger();
        
        String operation = pmRequest.getParameter("operation");
       
        
       
        if (operation!=null)
        {
            try
            {
                if (operation.equals("getQuota"))
                {
                    getQuota();
                }
                else if (operation.equals("modifyQuota"))
                {
                    modifyQuota();
                }
                else if (operation.equals("deleteUsers"))
                    //Segurmanent aqui en el Bean
                {
                    deleteUsers();
                    /*response.setContentType("text/xml;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<holaaaaaaaaaaaa/>");
                    out.close();*/
                    response.sendRedirect("index.jsp");
                    
                    return;
                    
                }
                else if(operation.equals("listUsers"))
                {
                    listUsers();
                }
                else if(operation.equals("listCollections"))
                {
                    listCollections();
                }
                else if(operation.equals("deleteCollection"))
                {
                    deleteCollection();
                }
                else if(operation.equals("listAttachmentsUser"))
                {
                    listAttachmentsUser();
                }
                else if(operation.equals("listAttachmentsDate"))
                {
                    listAttachmentsDate();
                }
                else if(operation.equals("deleteAttachment"))
                {
                    deleteAttachment();
                }
                else if(operation.equals("deleteFiles"))
                {
                    deleteFiles();
                }
                else if(operation.equals("getSQL"))
                {
                    getSQL();
                }
                else
                {
                    System.out.println("Operation not defined");
                }                    
            }
            catch(DataBaseException dbe)
            {
                throw new ServletException();
            }
            catch (ServerException se)
            {
                throw new ServletException();
            }
            
        }
        
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println(responseXML);
        out.close();
    }
    
    
    protected void getQuota() throws DataBaseException,ServerException
    {
        int quota = 0;
        
        User user = new User(pmRequest.getParameter("user"));
     
        quota = adminDB.getQuota(user);
        
        quota = quota /1024/1024;
        
        addParam("quota",""+quota+" Mb");
        
        float spaceUsed = 0;
        String sSpaceUsed="";
        
        
        spaceUsed = (float) edu.xtec.colex.utils.Utils.getDirSize(getAdminProperties().getProperty("FILES_DIR"),user);
        
        spaceUsed = spaceUsed /1024;

        if (spaceUsed >= 1024) 
        {
            spaceUsed = spaceUsed /1024;
            sSpaceUsed = ""+(int)spaceUsed+" Mb";
        }
        else sSpaceUsed = ""+(int)spaceUsed+" Kb";
        
        
        addParam("spaceUsed",sSpaceUsed);
    }
    
    protected void modifyQuota() throws DataBaseException,ServerException
    {
        User user = new User(pmRequest.getParameter("user"));
        int newQuota = Integer.parseInt(pmRequest.getParameter("newQuota"));
     
        adminDB.modifyQuota(user,newQuota*1024*1024);
        
        logger.info("Disk quota modified. User=\""+user.getUserId()+"\"");
        
    }
    
    protected void deleteUsers() throws DataBaseException,ServerException
    {
        FileItem fiUsers = pmRequest.getFileItem("usersFile");
        
        String sCurrentUser = "";
        
        String sAllUsers = fiUsers.getString().trim();
        int nextColon;
        
        int numDeleted = 0;
        
        for(int ini=0;ini<sAllUsers.length();)
        {
            nextColon = sAllUsers.indexOf(',',ini);
            sCurrentUser = sAllUsers.substring(ini,nextColon);
            
            
            try
            {
                Vector vCollections = adminDB.listCollections(new User(sCurrentUser));
                
                int j=0;
                
                for (;j<vCollections.size();j++)
                {
                    adminDB.deleteCollection(new User(sCurrentUser),(Collection)vCollections.get(j));
                    
                }
                
                numDeleted = numDeleted +j;
                
                
            }
            catch (DataBaseException dbe)
            {
                logger.info("No collections for user "+sCurrentUser);
            }
            
            adminDB.deleteUser(new User(sCurrentUser));
            edu.xtec.colex.utils.Utils.deleteDirectory(new File(getAdminProperties().getProperty("FILES_DIR")+sCurrentUser));
            
            ini = nextColon+1;
            
            logger.info("User deleted. User=\""+sCurrentUser+"\"");
        }
        
        
    }
    
    protected void listUsers() throws DataBaseException,ServerException
    {
        /*Vector vCollections = adminDB.listCollections(user);*/
        
        Vector vUsers = adminDB.listUsers();
        
        /*vUsers.add(new User("ogarci10"));
        vUsers.add(new User("ogarci11"));
        vUsers.add(new User("ogarci12"));
        vUsers.add(new User("ogarci13"));
        vUsers.add(new User("ogarci14"));*/
        
        User uAux;
                
        for (int i=0; i<vUsers.size();i++)
        {
            uAux = (User) vUsers.get(i);
            
            addParam("user_"+i,uAux.getUserId());
            
        }
    }
    
    protected void listCollections() throws DataBaseException,ServerException
    {
        
        
        User user = new User(pmRequest.getParameter("user"));
     
        Vector vCollections = adminDB.listCollections(user);
        
        Collection cAux;
        
        
        for (int i=0; i<vCollections.size();i++)
        {
            cAux = (Collection) vCollections.get(i);
            
            addParam("collection_"+i,cAux.getName());
            
        }
    }
    
    protected void deleteCollection() throws DataBaseException,ServerException
    {
        User user = new User(pmRequest.getParameter("user"));
        
        System.out.println("COLLECTION *"+pmRequest.getParameter("collection")+"*");
        
        Collection collection = new Collection(pmRequest.getParameter("collection"));
        
        adminDB.deleteCollection(user, collection);
        logger.info("Collection deleted. User=\""+user.getUserId()+"\" Collection=\""+collection.getName()+"\"");
     }
    
    protected void listAttachmentsUser() throws DataBaseException,ServerException
    { 
        User user = new User(pmRequest.getParameter("user"));
     
        Vector vAttachments = adminDB.listAttachmentsUser(user);
        
        AttachmentInfo aiAux;
        String sAtt;        
        
        for (int i=0; i<vAttachments.size();i++)
        {
            aiAux = (AttachmentInfo) vAttachments.get(i);
            
            sAtt = "<fileName>"+aiAux.getFileName()+"</fileName>";
            sAtt = sAtt + "<nameCollection>"+aiAux.getNameCollection()+"</nameCollection>";
            sAtt = sAtt + "<url>"+getAdminProperties().getProperty("FILES_URL")+aiAux.getUrl()+"</url>";
            sAtt = sAtt + "<size>"+aiAux.getSize()/1024+" Kb</size>";
            sAtt = sAtt + "<created>"+aiAux.getCreated()+"</created>";
            
            addParam("attachment_"+i,sAtt);
         }
    }
    
    protected void listAttachmentsDate() throws DataBaseException,ServerException
    {
        
        
        String beginDate = pmRequest.getParameter("beginDate");
        
        String endDate = pmRequest.getParameter("endDate");
        
        //System.out.println("beginDate "+beginDate+"endDate "+endDate);
     
        Vector vAttachments = adminDB.listAttachmentsDate(beginDate,endDate);
        
        AttachmentInfo aiAux;
        String sAtt;
        
        
        for (int i=0; i<vAttachments.size();i++)
        {
            aiAux = (AttachmentInfo) vAttachments.get(i);
            
            sAtt = "<fileName>"+aiAux.getFileName()+"</fileName>";
            sAtt = sAtt + "<nameCollection>"+aiAux.getNameCollection()+"</nameCollection>";
            sAtt = sAtt + "<user>"+aiAux.getUser().getUserId()+"</user>";            
            sAtt = sAtt + "<url>"+getAdminProperties().getProperty("FILES_URL")+aiAux.getUrl()+"</url>";
            sAtt = sAtt + "<size>"+aiAux.getSize()/1000+" Kb</size>";
            sAtt = sAtt + "<created>"+aiAux.getCreated()+"</created>";
            
            addParam("attachment_"+i,sAtt);
         }
    }
    
    
    
    protected void deleteAttachment() throws DataBaseException,ServerException
    {
        User user = new User(pmRequest.getParameter("user"));
        Collection collection = new Collection(pmRequest.getParameter("collection"));
        String sFile = pmRequest.getParameter("file");
    
        System.out.println("SERVLET "+ user +" "+ collection +"  "+ sFile);
        
        adminDB.deleteAttachment(user,collection,sFile);
        
        logger.info("Attachemnt deleted. User=\""+user.getUserId()+"\" Collection=\""+collection.getName()+"\" File=\""+sFile+"\"");
        
    }
    
    
    protected void deleteFiles() throws DataBaseException,ServerException
    {
        adminDB.checkFiles();
    }
    
    protected void getSQL() throws DataBaseException,ServerException
    {
        responseXML="<responseXML>"+adminDB.getSQL(pmRequest.getParameter("sql"))+"</responseXML>";
        System.out.println(adminDB.getSQL(pmRequest.getParameter("sql")));
    }
        
    protected void addParam(String paramName, String paramValue)
    {
        responseXML = responseXML.replaceAll("</responseXML>","<"+paramName+">"+paramValue+"</"+paramName+"></responseXML>");        
    }
    
    private void iniLogger()
    {
        
        if (logger==null)
        {
            Properties pLog = new Properties();
            try
            {
                InputStream isl = ServletAdmin.class.getResourceAsStream(ADMINCONF_PATH + ADMINCONF_LOG_FILE);
            
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
    
    public static Properties getAdminProperties()
    {        
        if (pAdmin==null)
        {
            pAdmin = new Properties();
            try{
                InputStream isl = ServletAdmin.class.getResourceAsStream(ADMINCONF_PATH + ADMINCONF_FILE);
                
                if (isl!=null){
                    pAdmin.load(isl);
                }
                isl.close();
                
                File f = new File(System.getProperty("user.home"), ADMINCONF_FILE);
                if(f.exists()){
                    FileInputStream is=new FileInputStream(f);
                    pAdmin.load(is);
                    is.close();
                }
            } catch (FileNotFoundException f) {
                f.printStackTrace();
                //logger.error(f);
            } catch (IOException e) {
                e.printStackTrace();
                //logger.error(e);
            }
        }
        return pAdmin;
        
    
    }
}

