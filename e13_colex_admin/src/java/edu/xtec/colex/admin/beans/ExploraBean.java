/*
 * ExploraBean.java
 *
 * Created on 29 de diciembre de 2003, 12:48
 */

package edu.xtec.colex.admin.beans;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;
import java.text.DateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author  fbusquet
 */
public class ExploraBean extends AdminBean {
    
    public static final String P_PATH="path"; //Name of the parameter that contains the path
    public static final String JSP_NAME="explora.jsp";
    
    public static final String WEB_BASE=edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("WEB_BASE");
    public static final String BASE_PATH=edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("BASE_PATH");
    
    
    
    
    private String path;
    //private String pathWithLinks;
    private String[][] pathLevels;
    private File fPath;
    private File[] folders;
    private File[] files;    
    private String filePrefix;
    private String folderName;
    
    /** Creates a new instance of ExploraBean */
    public ExploraBean() 
    
    {
    }
    
    public boolean init(HttpServletRequest request,HttpServletResponse response) {
        
        boolean result=verifyUser(request,response);
        
        if (result)
        {
        String carpeta=request.getParameter(P_PATH);
        
        if(carpeta==null)
            carpeta="";
        else if(carpeta.startsWith(File.separator))
            carpeta=carpeta.substring(File.separator.length());
        
        try{
            fPath=new File(BASE_PATH, carpeta).getCanonicalFile();
            if(fPath.exists() && fPath.isDirectory() && fPath.getAbsolutePath().indexOf(BASE_PATH)==0){
                path=fPath.getAbsolutePath().substring(BASE_PATH.length());
                if(path.startsWith(File.separator)){
                    path=path.substring(File.separator.length());
                }
                

                Vector vLevels=new Vector();
                //StringBuffer sbpl=new StringBuffer();
                
                
                
                
                folderName=WEB_BASE.length()>0 ? WEB_BASE : "root";





                //sbpl.append("<a href=\"").append(JSP_NAME).append("\">").append(root).append("</a>");
                vLevels.add(new String[]{folderName, JSP_NAME});
                if(path.length()>0){
                    StringTokenizer st=new StringTokenizer(path, File.separator);
                    StringBuffer sb=new StringBuffer();
                    while(st.hasMoreTokens()){
                        folderName=st.nextToken();
                        if(sb.length()>0)
                            sb.append(java.io.File.separator);
                        sb.append(folderName);
                        //sbpl.append(File.separator).append("<a href=\"").append(JSP_NAME).append("?").append(P_PATH).append("=").append(sb.substring(0)).append("\">");
                        //sbpl.append(tk).append("</a>");
                        vLevels.add(new String[]{folderName, JSP_NAME+"?"+P_PATH+"="+sb.substring(0)});
                    }
                }
                //pathWithLinks=sbpl.substring(0);
                pathLevels=(String[][])vLevels.toArray(new String[vLevels.size()][]);
                
                File[] fileList=fPath.listFiles();
                Vector vFolders=new Vector();
                Vector vFiles=new Vector();
                filePrefix="";                
                if(path.length()>0){
                    //vFolders.add(new File(fPath, ".."));
                    filePrefix=path+File.separator;
                }
                for(int i=0; i<fileList.length; i++){
                    
                        if(fileList[i].isDirectory())
                            vFolders.add(fileList[i]);
                        else
                            vFiles.add(fileList[i]);
                    
                }
                folders=(File[])vFolders.toArray(new File[vFolders.size()]);
                files=(File[])vFiles.toArray(new File[vFiles.size()]);                
                result=true;
            }
        } catch(java.io.IOException ex){
            System.err.println("Error: "+ex.getMessage());        
            result=false;
        }
        }
        return result;
    }
    
    public static String getFileKb(File file){
        return NumberFormat.getInstance().format(Math.max(1, file.length()/1024));
    }
    
    public static String getIcon(File file){
        String result="default.gif";
        
        if(file!=null){
            String name=file.getName();
            int p=name.lastIndexOf('.');
            if(p>0 && p<name.length()-1)
            {
                String ext = name.substring(p+1);
                               
                if (edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("file.audio").indexOf(ext)!=-1)
                {
                    result="audio.gif";
                }
                else if (edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("file.image").indexOf(ext)!=-1)
                {
                    result="img.gif";
                }
                    
            }
        }
        return result;
    }
        
    public static String getDate(File file)
    {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return df.format(new Date(file.lastModified()));
    }
    
    /** Getter for property path.
     * @return Value of property path.
     *
     */
    public String getPath() {
        return path;
    }
    
    public String getShortPath(int cutLevels){
        return cutPaths(path, cutLevels);
    }
    
    public static String cutPaths(String p, int cutLevels){
        String result=p;
        if(cutLevels>0){
            int k=0;
            int i=0;
            while(k>=0 && i<cutLevels){
                k=p.indexOf(File.separatorChar, k)+1;
                i++;
            }
            if(k>0)
                result=p.substring(k);
            else
                result="/";
        }
        return result;
    }
   /* 
    public String getZipPath(int cut){
        return getZipPath(path, cut);
    }*/
    
   /* public static String getZipPath(String path, int cut){
        StringBuffer sb=new StringBuffer(ZipFolder.SERVLET_NAME);
       // StringBuffer sb=new StringBuffer("servlet/CDpaquet.ZipFolder");
        sb.append("?").append(ZipFolder.P_PATH).append("=").append(path);
        if(cut>0)
            path=cutPaths(path, cut);
        String name="root";
        if(path!=null && path.length()>0){
            name=path.toLowerCase().replace(File.separatorChar, '_').replace('/', '_');
        }
        sb.append("&").append(ZipFolder.P_FNAME).append("=").append(name).append(".zip");
        return sb.substring(0);
    }*/
    
    
    /** Getter for property pathWithLinks.
     * @return Value of property pathWithLinks.
     *
     */
    //public java.lang.String getPathWithLinks() {
    //    return pathWithLinks;
    //}
    
    /** Getter for property folders.
     * @return Value of property folders.
     *
     */
    public java.io.File[] getFolders() {
        return this.folders;
    }
    
    /** Getter for property files.
     * @return Value of property files.
     *
     */
    public java.io.File[] getFiles() {
        return this.files;
    }
    
    /** Getter for property filePrefix.
     * @return Value of property filePrefix.
     *
     */
    public java.lang.String getFilePrefix() {
        return filePrefix;
    }    
        
    /** Getter for property pathLevels.
     * @return Value of property pathLevels.
     *
     */
    public java.lang.String[][] getPathLevels() {
        return this.pathLevels;
    }
    
    /** Getter for property folderName.
     * @return Value of property folderName.
     *
     */
    public java.lang.String getFolderName() {
        return folderName;
    }    
    
}
