/* *****************************************************************************
 * AuthenticationServlet.java
* ****************************************************************************/

/* J_LZ_COPYRIGHT_BEGIN *******************************************************
* Copyright 2001-2004 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* J_LZ_COPYRIGHT_END *********************************************************/

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletConfig.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class AuthenticationServlet extends HttpServlet
{
    //----------------------------------------------------------------------
    // Parameters
    //----------------------------------------------------------------------
    public static final String PARAM_REQUEST  = "rt";
    public static final String PARAM_USER     = "usr";
    public static final String PARAM_PASSWORD = "pwd";

    //----------------------------------------------------------------------
    // Request types
    //----------------------------------------------------------------------
    public static final String REQ_UNKNOWN     = "unknown";
    public static final String REQ_LOGIN       = "login";
    public static final String REQ_LOGINGUEST  = "loginguest";
    public static final String REQ_LOGOUT      = "logout";
    public static final String REQ_GETUSERNAME = "getusername";

    //----------------------------------------------------------------------
    // Status code types
    //----------------------------------------------------------------------
    public static final int STAT_OK              = 0;
    public static final int STAT_UNKNOWN         = 1;
    public static final int STAT_ERROR           = 2;
    public static final int STAT_INVALID         = 3;
    public static final int STAT_INVALID_SESSION = 4;
    public static final int STAT_FORBIDDEN       = 5;

    //----------------------------------------------------------------------
    // XML tag names
    //----------------------------------------------------------------------
    public static final String TAG_ROOT     = "authentication";
    public static final String TAG_STATUS   = "status";
    public static final String TAG_RESPONSE = "response";
    public static final String TAG_USER     = "user";
    public static final String TAG_USERNAME = "username";


    /** Static counter for guest name. */
    private static int sGuestCounter = 0;
    private static Object sLock = new Object();

    /** Attribute session for username. */
    static public String ATTR_USERNAME = "username";

    /** Default authentication initialization file; this file is assumed to
     * exist in the WEB-INF directory. */
    private String DEFAULT_USERS_FILE = "lzusers.xml";

    /** Map to store user information */
    private HashMap mUserMap = new HashMap();

    /** Flag to allow get guest interface. */
    private boolean mAllowLoginGuest = true;

    /** Logger */
    private static Logger mLogger  = Logger.getLogger(AuthenticationServlet.class);


    /** 
     * Initialize servlet.
     * @param config configuration file object 
     */
    public void init(ServletConfig config)
        throws ServletException 
    {
        super.init (config);

        log("Initializing AuthenticationServlet!");
        ServletContext ctxt = config.getServletContext();

        // Sanity check the servlet context version
        if (ctxt.getMajorVersion() < 2 || ctxt.getMinorVersion() < 2)
            throw new ServletException("must be at least servlet 2.2");

        //------------------------------------------------------------
        // Create internal name, password table
        //------------------------------------------------------------
        // Figure out where lps config directory is.
        String ctxtPath = ctxt.getRealPath("/");
        String delim =
            (ctxtPath.lastIndexOf(File.separator) == ctxtPath.length()-1 ? "" : File.separator);
        String lpsConfigDir = getInitParameter("lps.config.dir");
        if (lpsConfigDir == null) {
            lpsConfigDir = System.getProperty("lps.config.dir");
            if (lpsConfigDir == null) {
                lpsConfigDir = "config";
            }
        }
        String configDir = ctxtPath + delim + "WEB-INF" 
            + File.separator + "lps"
            + File.separator + lpsConfigDir;


        // Get connection parameter.
        String usersFile = config.getInitParameter("LZ_USERS_FILE");
        if (usersFile == null)
            usersFile = DEFAULT_USERS_FILE;


        String fullPath = getFullPath(configDir, usersFile);
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(new File(fullPath));
            Element root = document.getRootElement();
            List userList = root.getChildren(TAG_USER);
                
            // Create user information map.
            int userListLen = userList.size();
            for (int i = 0; i < userListLen; i++) {
                Element userTag = (Element)userList.get(i);
                String name = userTag.getAttributeValue("name");
                String password = userTag.getAttributeValue("password");
                if (name!=null && password!=null)
                    mUserMap.put(name, password);
            }
        } catch (IOException e) {
            String info = "init exception: " + e.getMessage(); 
            mLogger.debug(info); 
            throw new ServletException(info);
        } catch (JDOMException e) {
            String info = "init exception: " + e.getMessage(); 
            mLogger.debug(info); 
            throw new ServletException(info);
        }

        //------------------------------------------------------------
        // Flag to allow guest naming interface.
        //------------------------------------------------------------
        String loginGuestStr  = config.getInitParameter("ALLOW_GUEST_LOGIN");
        if (loginGuestStr!=null)
            mAllowLoginGuest = loginGuestStr.equals("true");
    }


    /** 
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        mLogger.debug("doGet(req, res)");

        mLogger.debug("Request: uri " + req.getRequestURI());
        mLogger.debug("Request: query string " + req.getQueryString());

        Enumeration headers = req.getHeaderNames();
        while(headers.hasMoreElements()) {
            String h = (String)headers.nextElement();
            mLogger.debug("    Header: " + h + " : " + req.getHeader(h));
        }

        Element root = new Element(TAG_ROOT);

        // Request type
        String rt = req.getParameter(PARAM_REQUEST);
        boolean isOk = false;
        if (rt == null)
            doUnknown(req, root);
        else if (rt.equals(REQ_LOGIN))
            doLogin(req, res, root);
        else if (rt.equals(REQ_LOGINGUEST))
            if (mAllowLoginGuest)
                doLoginGuest(req, res, root);
            else
                doForbidden(req, rt, root);
        else if (rt.equals(REQ_LOGOUT))
            doLogout(req, res, root);
        else if (rt.equals(REQ_GETUSERNAME))
            doGetUsername(req, root);
        else
            doUnknown(req, root);

        res.setHeader ("Pragma",        "no-cache");
        res.setHeader ("Cache-Control", "no-cache");
        String xml = new XMLOutputter().outputString(new Document(root));
        ServletOutputStream out = res.getOutputStream();
        out.println(xml);
    }

    /** 
     * Handle post request.
     *
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) 
        throws ServletException, IOException
    {
        mLogger.debug("doPost(req, res)");

        doGet(req, res);
    }


    /** 
     * Login user and create session.
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    private void doLogin(HttpServletRequest req, HttpServletResponse res,
                         Element root)
    {
        mLogger.debug("doLogin(req, res, root)");

        String username = req.getParameter(PARAM_USER);
        if ( username != null && username.equals("guest") ) {
            if (mAllowLoginGuest) 
                doLoginGuest(req, res, root);
            else
                doForbidden(req, REQ_LOGIN, root);
            return;
        }

        int code = STAT_INVALID;
        Element eRequest = getRequestElement(REQ_LOGIN);

        Element eUsername = null;
        String password = req.getParameter(PARAM_PASSWORD);
        if ( username != null && ! username.equals("") &&
             password != null && ! password.equals("") ) {

            String checkPassword = (String)mUserMap.get(username);
            if (checkPassword != null && !checkPassword.equals("")) {
                if (password.equals(checkPassword)) {
                    HttpSession sess = req.getSession();
                    sess.setAttribute(ATTR_USERNAME, username);
                    eUsername = new Element(TAG_USERNAME)
                        .addContent(username);
                    code = STAT_OK;
                }
            }
        }

        Element eStatus = getStatusElement(code);
        eRequest.addContent(eStatus);
        if (eUsername != null) 
            eRequest.addContent(eUsername);

        root.addContent(eRequest);
    }


    /** 
     * Fetch a guest name.
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    private void doLoginGuest(HttpServletRequest req, HttpServletResponse res,
                              Element root)
    {
        mLogger.debug("doLoginGuest(req, res, root)");

        String guest;
        synchronized (sLock) {
            guest = "guest" + sGuestCounter++;
        }
        HttpSession sess = req.getSession();
        sess.setAttribute(ATTR_USERNAME, guest);

        Element eRequest = getRequestElement(REQ_LOGINGUEST);
        eRequest.addContent(getStatusElement(STAT_OK));
        Element eUsername = new Element(TAG_USERNAME)
            .addContent(guest);
        eRequest.addContent(eUsername);
        root.addContent(eRequest);
    }


    /** 
     * Remove session.
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    private void doLogout(HttpServletRequest req, HttpServletResponse res, 
                          Element root)
    {
        mLogger.debug("doLogout(req, res, root)");

        Element eRequest = getRequestElement(REQ_LOGOUT);
        HttpSession sess = req.getSession();
        if (sess.getAttribute(ATTR_USERNAME) != null) {
            sess.invalidate();
            eRequest.addContent(getStatusElement(STAT_OK));
        } else {
            eRequest.addContent(getStatusElement(STAT_INVALID_SESSION));
        }

        root.addContent(eRequest);
    }


    /** 
     * Get username of current session, if any.
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    private void doGetUsername(HttpServletRequest req , Element root)
    {
        mLogger.debug("doGetUsername(req, root)");

        Element eRequest = getRequestElement(REQ_GETUSERNAME);
        HttpSession sess = req.getSession();
        // Session is only valid if we have a username
        String username = (String)sess.getAttribute(ATTR_USERNAME);
        if (username != null) {
            eRequest.addContent(getStatusElement(STAT_OK));
            Element eUsername = new Element(TAG_USERNAME)
                .addContent(username);
            eRequest.addContent(eUsername);
        } else {
            eRequest.addContent(getStatusElement(STAT_INVALID_SESSION));
        }

        root.addContent(eRequest);
    }


    /**
     * Return unknown response.
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse
     */
    private void doUnknown(HttpServletRequest req, Element root)
    {
        mLogger.debug("doUnknown(req, root)");

        Element eRequest = getRequestElement(REQ_UNKNOWN);
        eRequest.addContent(getStatusElement(STAT_UNKNOWN));
        root.addContent(eRequest);
    }


    /** 
     * Return forbidden response.
     * @param req @see HttpServletRequest
     * @param res @see HttpServletResponse 
     */
    private void doForbidden(HttpServletRequest req, String rt, Element root)
    {
        mLogger.debug("doForbidden(req, root)");
        Element eRequest = getRequestElement(rt);
        eRequest.addContent(getStatusElement(STAT_FORBIDDEN));
        root.addContent(eRequest);
    }


    /**
     * Get a status element with a particular message attached.
     * @param code code of status
     * @param message status message
     * @return an element with status information
     */
    private Element getStatusElement(int code, String msg)
    {
        mLogger.debug("getStatusElement(code=" + code + ", msg=" + msg + ")");

        return new Element(TAG_STATUS)
            .setAttribute("code", Integer.toString(code))
            .setAttribute("msg", msg);
    }

    /**
     * Get a status element.
     * @param code code of status
     * @return an element with status information 
     */
    private Element getStatusElement(int code)
    {
        mLogger.debug("getStatusElement(code=" + code + ")");

        return getStatusElement(code, getStatusCodeMessage(code));
    }

    /**
     * Get a request element.
     * @param type type of request
     * @return an element with request information 
     */
    private Element getRequestElement(String type)
    {
        mLogger.debug("getRequestElement(type=" + type + ")");

        return new Element(TAG_RESPONSE)
            .setAttribute("type", type);
    }

    /**
     * Get status code of message.
     * @param code integer code
     * @return string representation of code 
     */
    private String getStatusCodeMessage(int code)
    {
        mLogger.debug("getStatusCodeMessage(code=" + code + ")");

        if (code == STAT_INVALID)
            return "invalid";
        if (code == STAT_ERROR)
            return "error";
        if (code == STAT_OK)
            return "ok";
        if (code == STAT_INVALID_SESSION)
            return "invalid session";
        if (code == STAT_FORBIDDEN)
            return "forbidden";

        return "unknown";       // STAT_UNKNOWN
    }


    /**
     * Create a full path based by appending file to dir. File has to be of
     * relative path or null is returned.
     * @param dir directory name
     * @param file file name
     * @return string concatenated with dir and file or null if file is of
     * absolute path
     */
    static public String getFullPath(String dir, String file)
    {
        mLogger.debug("getFullPath(dir=" + dir + ", file=" + file + ")");

        final String fileSeparator = System.getProperty("file.separator");

        String fullPath = "";
        if (dir != null && dir.length() != 0) {
            fullPath = dir;
            boolean dirHasLastSeparator = 
                dir.lastIndexOf(fileSeparator) == dir.length()-1;
            if (! dirHasLastSeparator) 
                fullPath += fileSeparator;
        }
        fullPath += file;

        return fullPath;
    }

    /** 
     * Replace real path forward slash characters to back-slash for Windows.
     * This is to get around a WebSphere problem where calling getRealPath()
     * returns path with mixed file separators.
     * @param ctxt servlet context
     * @param path virtual webapp path to resolve into a real path
     * @return the real path, or null if the translation cannot be performed
     */
    public String getRealPath(ServletContext ctxt, String path)
    {
        String realPath = ctxt.getRealPath(path);
        if ( realPath != null && File.separatorChar == '\\' )
            realPath = realPath.replace('/', '\\');
        return realPath;
    }
}
