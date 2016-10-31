/*
 * ColexValidateBean.java
 *
 * Created on 25 de octubre de 2005, 13:03
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package edu.xtec.colex.admin;

import org.apache.log4j.Logger;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 *
 * @author ogarci10
 */
public class ColexValidateBean {
   // protected static Logger logger;
   String sError = null;
    
    /** Creates a new instance of ColexValidateBean */
    public boolean isValid(HttpServletRequest request, HttpServletResponse response)
    {

        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");
        String url = request.getParameter("url");

        if (user != null && pwd != null && !"".equals(user) && !"".equals(pwd)) {

            //if (pwd.equals("colex"))
            Vector vResult = wsValidation(user, pwd);

            String sResult = null;
            if (!vResult.isEmpty()) {
                sResult = (String) vResult.firstElement();
            }

            String sMail = null;
            if ("1".equals(sResult) || "2".equals(sResult)) {
                // Valid USER so get the email
                if (vResult.size() > 1) {
                    sMail = (String) vResult.get(1);
                } else {
                    sMail = user + "@edu365.cat";
                }

            } else {

                sMail = ldapValidation(user, pwd);
                // There is neither an XTEC user
                if (sMail == null) {
                    if (user == null || "".equals(user)) {
                        sError = "101";
                    } else if (pwd == null || "".equals(pwd)) {
                        sError = "102";
                    } else if (sResult == null) {
                        sResult = "105";
                    }
                }
                sError = sResult;
            }

            if (sMail != null) {
                // User key cookie
                response.addCookie(createCookie("cookie.user_key", user));

                // User key cookie
                response.addCookie(createCookie("cookie.email_key", sMail));

                // edu365 key cookie
                String sPortal = (new Date()).getTime() + "A";
                if (sMail.indexOf("@xtec.cat") > 0) {
                    sPortal += "P";
                }
                response.addCookie(createCookie("cookie.portal", sPortal));

                try {

                    response.sendRedirect(url);
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return false;
    }

    private Cookie createCookie(String sPropertyName, String sValue) {
        Cookie c = new Cookie(edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty(sPropertyName), sValue);
        c.setMaxAge(-1);
        c.setPath("/");
        if (edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("cookie.domain") != null) {
            c.setDomain(edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("cookie.domain"));
        }
        return c;
    }



    public String ldapValidation(String userName, String passWord) {
        String sMail = null;
        Hashtable authEnv = new Hashtable(11);
        //String base = "ou=People,dc=xtec,dc=es";
        String base = edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("ldap.base");

        /* Cambio Nadim Transformacio
         Original: String dn = "uid=" + userName + "," + base;
         */
        String dn = "cn=" + userName + "," + base;

        String ldapURL = edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("ldap.server");

        authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        /* Cambio Nadim Transformacio
         Original: authEnv.put(Context.PROVIDER_URL, ldapURL + "/" + base);
         */
        authEnv.put(Context.PROVIDER_URL, ldapURL);
        //Nadim 16/07/2015 end
        authEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        authEnv.put(Context.SECURITY_PRINCIPAL, dn);
        authEnv.put(Context.SECURITY_CREDENTIALS, passWord);

        try {

            DirContext authContext = new InitialDirContext(authEnv);

            // Get the attributes
            Attributes attrs = authContext.getAttributes(dn);

            // Get the email
            sMail = attrs.get("mail").get().toString();

            // Close the context when we're done
            authContext.close();
        } catch (AuthenticationException authEx) {

        } catch (NamingException namEx) {

            namEx.printStackTrace();
        }
        return sMail;
    }



    public Vector wsValidation(String userName, String password) {
        Vector vResult = new Vector();
        //String base = getJspProperties().getProperty("ldap.base");
        //String ldapURL = getJspProperties().getProperty("ldap.server");
        String wsValidationURL = edu.xtec.colex.admin.ServletAdmin.getAdminProperties().getProperty("ws.server");
        try {

            URL url = new URL(wsValidationURL);
            URLConnection urlConn = url.openConnection();

            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Send POST output.
            DataOutputStream wsInput = new DataOutputStream(urlConn.getOutputStream());

            String content = "username=" + URLEncoder.encode(userName, "UTF-8")
                    + "&password=" + URLEncoder.encode(password, "UTF-8");
            //logger.debug("Content: " + content);
            wsInput.writeBytes(content);
            wsInput.flush();
            wsInput.close();

            // reads the CGI response and print it inside the servlet content
            BufferedReader wsOutput = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String sResult = null;

            if (null != (sResult = wsOutput.readLine())) {
                StringTokenizer oTokens = new StringTokenizer(sResult, "$$");

                while (oTokens.hasMoreElements()) {
                    vResult.addElement(oTokens.nextElement());
                }
            }
            wsOutput.close();
        } catch (Exception e) {
            e.printStackTrace();
            vResult.addElement("106");
        }
        return vResult;
    }

    public String getError() {
        return sError;
    }


}
