/*
 * AdminBean.java
 *
 * Created on 31 / juliol / 2006, 12:55
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.xtec.colex.admin;

import edu.xtec.colex.admin.AdminDataBase;
import edu.xtec.colex.utils.ParseMultipart;
import edu.xtec.colex.domain.*;
import edu.xtec.colex.domain.Collection;
import edu.xtec.colex.exception.*;

import javax.xml.soap.*;

import java.net.*;
import java.io.*;

import java.util.Properties;
import java.util.Vector;
import java.util.Iterator;

import javax.servlet.http.*;
import org.apache.log4j.*;

import org.apache.commons.fileupload.*;



/**
 *
 * @author ogarci10
 */
public class AdminBean 
{
    
    protected static String USER_KEY="usuari-xtec";
    protected String sUserId;
    
    protected static AdminDataBase adminDB = new AdminDataBase();
    
    public final static String ADMINCONF_PATH = "/edu/xtec/colex/admin/";

    public final static String ADMINCONF_LOG_FILE = "adminLog.properties";
    
    protected static Properties pAdmin;
    
    public ParseMultipart pmRequest;   
    
    protected HttpServletRequest request;
    protected HttpServletResponse response;

    protected static Logger logger;
    protected String redirectPage="";
  //  protected static Logger logger;
    
    /** Creates a new instance of AdminBean */
    public AdminBean() 
    {
        iniLogger();
    }
    
    
    public boolean init(HttpServletRequest request, HttpServletResponse response)
    {

        logger.debug("AdminBean init Request= "+request.toString()+  "Response=" +response.toString());

        this.request = request;
        this.response = response;
        
        boolean bOK = true;
        bOK = verifyUser(request,response);
        
        if (bOK)
        {
            pmRequest = new ParseMultipart(request);

            String operation = pmRequest.getParameter("operation");
                
            try
            {
                if (operation!=null)                
                {
                    if (operation.equals("exportCollection"))
                    {
                        exportCollection();

                    }
                    else if (operation.equals("logout"))
                    {
                        logout();
                    }

                        
                } 
            }
            
            catch (Exception e)
            {
             //   e.printStackTrace();
            }
                  
        }
        
        return bOK;
        
    }
    
    public boolean verifyUser(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;


        boolean bOK = true;
                        
        if (needValidation())
        {
            if(!isValidated())
            {
                
                StringBuffer sbReqURL = request.getRequestURL();
                
                String page = sbReqURL.substring(sbReqURL.lastIndexOf("/")+1);
                
                String validateURL = edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("VALIDATE_URL");
                                
                redirectPage= validateURL.substring(0,validateURL.lastIndexOf("/")+1) + page;
            
                bOK= false;
            }
        }
	
        return bOK;
    }   
    
    public boolean needValidation()
    {
        return true;
    }

    public boolean isValidated()
    {
        return (getUserId()!=null && getUserId().length()>0 && !getUserId().equalsIgnoreCase("null") && isAdmin());
    }
    
    public boolean isAdmin()
    {
        //return true;
        return adminDB.isAdmin(getUserId());
    }


    public String getUserId() {
        if (sUserId == null && request != null) {
            Cookie[] cookies = request.getCookies();

            if (cookies != null) {
                logger.debug("Cookie.user_key: " + edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("cookie.user_key"));

                for (int i = 0; i < cookies.length; i++) {
                    Cookie c = cookies[i];
                    logger.debug("Cookies: " + c.getValue().trim() + " <--> " + i);
                    logger.debug("Cookies-GETNAME: " + c.getName().trim() + " <--> " + i);
                    if (c.getName().equals(edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("cookie.user_key")) && c.getValue() != null) {
                        sUserId = c.getValue().trim();
                        logger.debug("sUserId: " + sUserId + " <--> " + i);
                        break;
                    }
                }
            }
        }
        return sUserId;
    }






    
    public String getRedirectPage()
    {
        return redirectPage;
    }
    
    public void logout()
    {
        if(request!=null)
        {
            Cookie c = new Cookie(USER_KEY,"");
            
            c.setMaxAge(0);
            c.setPath("/");
            c.setDomain("xtec.cat");
            
            response.addCookie(c);
            sUserId=null;
            redirectPage = buildRedirectURL(request,"index.jsp");
            
            try 
            {
                response.sendRedirect(redirectPage );
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    protected String buildRedirectURL(HttpServletRequest request,String sRedirectPage)
    {
        
        String sBasePath = null;
        StringBuffer sbRequestURL = request.getRequestURL();   
                    
                    
        if (sbRequestURL!=null)
        {
            int iLast = sbRequestURL.lastIndexOf("/")+1;
            sBasePath = sbRequestURL.substring (0, iLast);
        }
            
        if (sBasePath!=null)
        {
            if (sBasePath.indexOf("educacio.intranet")>=0)
            {
                //sBasePath=StringUtil.replace(sBasePath, "educacio.intranet", "edu365.com");
                
                sBasePath=sBasePath.replaceAll("educacio.intranet","edu365.cat");
            }
                        
            if (sRedirectPage.indexOf("://")<0)
            {
                sRedirectPage=sBasePath+sRedirectPage;
            }
       }
                    
                    
       return sRedirectPage;
    }
    
    
    protected void exportCollection() throws Exception
    {
        User user = new User(request.getParameter("user"));
        Collection collection = new Collection(request.getParameter("collection"));
        
        Query query = new Query();
        
        query.setOrderField("null");query.setDirection("asc");query.setBeginIndex(0);
                
        
        
        Vector vFieldsDefs,vRecords,vFieldsDefsAtt,vAttachments;
        
        vRecords = new Vector();
        vFieldsDefs = new Vector();
        vFieldsDefsAtt = new Vector();
        vAttachments= new Vector();
        
        adminDB.exportCollection(user,null, collection,query,vFieldsDefs,vRecords,vFieldsDefsAtt,vAttachments);
              
        Record rAux;
        FieldDef fdAux;
        String sValue;
        int iPos;
      
        //CAL TREURE LA / DE LA URL DELS ARXIUS QUE AFEGIM
        
        for (int i=0;i<vRecords.size();i++)
        {       
            rAux = (Record) vRecords.get(i);
            
            for (int j=0; j<vFieldsDefs.size();j++)
            {
                fdAux = (FieldDef) vFieldsDefs.get(j);
                
                if (fdAux.getType().equals("image")||fdAux.getType().equals("sound")||fdAux.getType().equals("video"))
                {               
                    sValue = rAux.getFieldValue(fdAux.getName());
                    
                    if (!sValue.equals("null"))
                    {
                        iPos = sValue.lastIndexOf("/");                              
                        sValue = sValue.substring(iPos+1, sValue.length());
                        rAux.modifyFieldValue(fdAux.getName(), sValue);
                    }
                }
            }
        }
        
        
        
        //File fileZip = edu.xtec.colex.Utils.toZip(user,collection,vFieldsDefs,vRecords,vAttachments,adminDB.getDBProperties().getProperty("FILES_DIR")); 
        File fileZip = edu.xtec.colex.utils.Utils.toZip(collection,vFieldsDefs,vRecords,vAttachments,adminDB.getDBProperties().getProperty("FILES_DIR")); 
        
        
        response.setContentType("application/x-zip-compressed");
                
        String nameOk = replaceSpaces(collection.getName());
                
        response.setHeader("Content-disposition", "filename="+nameOk+".zip");
                
                
        InputStream is = new FileInputStream(fileZip);
        
                
        OutputStream os = response.getOutputStream();
                       
        byte[] buff = new byte[1024];
        int read = 0;
            
        while ((read = is.read(buff, 0, buff.length)) != -1) 
        {
            os.write(buff, 0, read);
        }
        
        os.flush();
        os.close();       
        
        
    }
    
    
    private String replaceSpaces(String in)
    {
        return trim(in);
    }
    
     /* remove leading whitespace */
    private String ltrim(String source) {
        return source.replaceAll("^\\s+", "");
    }

    /* remove trailing whitespace */
    private String rtrim(String source) {
        return source.replaceAll("\\s+$", "_");
    }

    /* replace multiple whitespaces between words with single blank */
    private String itrim(String source) {
        return source.replaceAll("\\b\\s{1,}\\b", "");
    }

    /* remove all superfluous whitespaces in source string */
    private String trim(String source) {
        return itrim(ltrim(rtrim(source)));
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
}
