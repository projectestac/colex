/*
 * AttachmentInfo.java
 *
 * Created on 16 / agost / 2006, 13:01
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.xtec.colex.admin;

import java.util.Date;
import edu.xtec.colex.domain.User;
        

/**
 *
 * @author ogarci10
 */
public class AttachmentInfo extends edu.xtec.colex.domain.Attachment{
    
    protected String fileName="";
    protected String nameCollection="";
    protected long size=0;
    protected Date created = new Date();
    protected User user = new User("");    
        
    public AttachmentInfo() 
    { 
    }
    
    public void setFileName(String sFileName)
    {
        fileName = sFileName;
    }
    
    public String getFileName()
    {
        return fileName;
    }
        
    public void setNameCollection(String sNameCollection)
    {
        nameCollection=sNameCollection;
    }
    
    public String getNameCollection()
    {
        return nameCollection;
    }
    
    public void setSize(long lSize)
    {
        size = lSize;
    }
    
    public long getSize()
    {
        return size;
    }
    
    public void setCreated(Date dCreated)
    {
        created = dCreated;
    }
    
    public Date getCreated()
    {
        return created;
    }
    
    public void setUser(User uUser)
    {
        user = uUser;
    }
    
    public User getUser()
    {
        return user;
    }
}
