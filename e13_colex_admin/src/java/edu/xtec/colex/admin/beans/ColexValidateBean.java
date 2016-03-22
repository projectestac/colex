/*
 * ColexValidateBean.java
 *
 * Created on 25 de octubre de 2005, 13:03
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.xtec.colex.admin.beans;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author ogarci10
 */
public class ColexValidateBean {
    
    /** Creates a new instance of ColexValidateBean */
    public boolean isValid(HttpServletRequest request, HttpServletResponse response) 
    {
        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");
        
        
        if (user!=null && pwd!=null)
        {
            if (pwd.equals("ola"))
            {
                response.addCookie(new Cookie("usuari-edu365",user));
                try
                {
                    response.sendRedirect("index.jsp");
                }
                catch (Exception ioe)
                {}
            }
        }
        return false;
    }
    
}
