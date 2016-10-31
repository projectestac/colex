/*
 * File    : ColexValidateBean.java
 * Created : 13-sep-2005 17:56
 * By      : ogalzorriz
 *
 * Col·lex - Web-based educational application for design collections
 * of records, store information, make queries and share them.
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
package edu.xtec.colex.client.beans;

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

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DEMO Bean that extends from ColexMainBean and processes the validation cookie
 * method
 *
 * @author sarjona
 * @version 1.2
 */
public class ColexValidateBean extends ColexMainBean {

    String sError = null;

    /**
     * Method that checks a password 'colex' for a user and initializes the
     * cookie to validate the user and redirects the request to the url page
     * indicated
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return boolean. Also save in sError the code error (if it's necessary)
     * 101 // Empty username 102 // Empty password 103 // Incorrect username 104
     * // Incorrect password 105 // Incorrect username or password 106 // Error
     * accessing to WS URL
     */
    public boolean isValid(HttpServletRequest request, HttpServletResponse response) {
        iniLogger();

        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");
        String url = request.getParameter("url");

        if (user != null && pwd != null && !"".equals(user) && !"".equals(pwd)) {
            logger.debug("user: " + user + "<<>> pwd: don't show <<>> URL: " + url);
            //if (pwd.equals("colex"))	
            Vector vResult = wsValidation(user, pwd);
            logger.debug("vResult: " + vResult);
            String sResult = null;
            if (!vResult.isEmpty()) {
                sResult = (String) vResult.firstElement();
            }
            logger.debug("sResult: " + sResult);
            String sMail = null;
            if ("1".equals(sResult) || "2".equals(sResult)) {
                // Valid USER so get the email
                if (vResult.size() > 1) {
                    sMail = (String) vResult.get(1);
                } else {
                    sMail = user + "@edu365.cat";
                }
                logger.debug("isValid sMail: " + sMail);
            } else {
                logger.debug("ELSE go to ldap validation");
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
                    logger.debug("sendRedirect(url): " + url);
                    response.sendRedirect(url);
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return false;
    }

    public String getError() {
        return sError;
    }

    private Cookie createCookie(String sPropertyName, String sValue) {
        Cookie c = new Cookie(getJspProperties().getProperty(sPropertyName), sValue);
        c.setMaxAge(-1);
        c.setPath("/");
        if (getJspProperties().getProperty("cookie.domain") != null) {
            c.setDomain(getJspProperties().getProperty("cookie.domain"));
        }
        return c;
    }

    /**
     *
     * @param userName
     * @param passWord
     * @return mail or null if user is not valid
     */
    public String ldapValidation(String userName, String passWord) {
        String sMail = null;
        Hashtable authEnv = new Hashtable(11);
        //String base = "ou=People,dc=xtec,dc=es";
        String base = getJspProperties().getProperty("ldap.base");
        logger.debug("Base: " + base);
        /* Cambio Nadim Transformacio 
         Original: String dn = "uid=" + userName + "," + base;
         */
        String dn = "cn=" + userName + "," + base;
        //Nadim 16/07/2015 END
        logger.debug("DN: " + dn);
        //String ldapURL = "ldap://ldap.xtec.cat";
        //String ldapURL = "ldap://localhost.edu365.cat";
        String ldapURL = getJspProperties().getProperty("ldap.server");
        logger.debug("ldapURL: " + ldapURL);
        authEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        /* Cambio Nadim Transformacio 
         Original: authEnv.put(Context.PROVIDER_URL, ldapURL + "/" + base);
         */
        authEnv.put(Context.PROVIDER_URL, ldapURL);
        //Nadim 16/07/2015 end
        authEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        authEnv.put(Context.SECURITY_PRINCIPAL, dn);
        authEnv.put(Context.SECURITY_CREDENTIALS, passWord);
        logger.debug("AuthENV: " + authEnv);
        try {
            logger.debug("Trying to validate dn " + dn);
            DirContext authContext = new InitialDirContext(authEnv);
            logger.debug("AuthContext: " + authContext);
            // Get the attributes
            Attributes attrs = authContext.getAttributes(dn);
            logger.debug("attrs: " + attrs);
            // Get the email
            sMail = attrs.get("mail").get().toString();
            logger.debug("Mail: " + sMail);
            // Close the context when we're done
            authContext.close();
        } catch (AuthenticationException authEx) {
            logger.error("Authentication failed!>> " + authEx);
        } catch (NamingException namEx) {
            logger.error("Something went wrong! >>" + namEx);
            namEx.printStackTrace();
        }
        return sMail;
    }

    /**
     *
     * @param userName
     * @param password
     * @return result of the ws invocation
     */
    public Vector wsValidation(String userName, String password) {
        Vector vResult = new Vector();
        //String base = getJspProperties().getProperty("ldap.base");
        //String ldapURL = getJspProperties().getProperty("ldap.server");
        String wsValidationURL = getJspProperties().getProperty("ws.server");
        logger.debug("wsValidationURL: " + wsValidationURL);
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
            logger.debug("wsInput: " + wsInput);
            // reads the CGI response and print it inside the servlet content
            BufferedReader wsOutput = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            logger.debug("wsOutput: " + wsOutput);
            String sResult = null;
            logger.debug("sResult: " + sResult);
            if (null != (sResult = wsOutput.readLine())) {
                StringTokenizer oTokens = new StringTokenizer(sResult, "$$");
                logger.debug("oTokens: " + oTokens);
                while (oTokens.hasMoreElements()) {
                    vResult.addElement(oTokens.nextElement());
                }
            }
            wsOutput.close();
        } catch (Exception e) {
            logger.error("Something went wrong while user validation for " + userName);
            logger.error("ERROR: " + e);
            e.printStackTrace();
            vResult.addElement("106");
        }
        return vResult;
    }

    /**
     * Does nothing is needed to implement the abstract method from
     * ColexMainBean
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @return true
     */
    public boolean initImpl(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }
;
}
