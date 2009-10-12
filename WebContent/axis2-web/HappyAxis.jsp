<html>
<%@ page import="org.apache.axiom.om.OMAbstractFactory,
                 org.apache.axiom.om.OMElement,
                 org.apache.axiom.om.OMFactory,
                 org.apache.axiom.om.OMNamespace,
                 org.apache.axis2.AxisFault,
                 org.apache.axis2.Constants,
                 org.apache.axis2.addressing.EndpointReference,
                 org.apache.axis2.client.Options,
                 org.apache.axis2.client.ServiceClient,
                 org.apache.axis2.context.ConfigurationContext,
                 org.apache.axis2.context.ConfigurationContextFactory,
                 javax.servlet.ServletContext,
                 javax.servlet.http.HttpServletRequest,
                 javax.servlet.http.HttpServletResponse,
                 javax.servlet.jsp.JspWriter,
                 javax.xml.parsers.SAXParser,
                 javax.xml.parsers.SAXParserFactory"
         session="false" %>
<%@ page import="javax.xml.stream.XMLOutputFactory" %>
<%@ page import="javax.xml.stream.XMLStreamException" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.lang.Class" %>
<%@ page import="java.lang.ClassNotFoundException"%>
<%@ page import="java.lang.Exception" %>
<%@ page import="java.lang.Integer" %>
<%@ page import="java.lang.NoClassDefFoundError" %>
<%@ page import="java.lang.SecurityException" %>
<%@ page import="java.lang.String" %>
<%@ page import="java.lang.System" %>
<%@ page import="java.lang.Throwable" %>

<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>
<head>
    <jsp:include page="include/httpbase.jsp"/>
    <title>Axis2 Happiness Page</title>
    <link href="axis2-web/css/axis-style.css" rel="stylesheet" type="text/css">
</head>

<body>
<jsp:include page="include/header.inc"/>
<jsp:include page="include/link-footer.jsp"/>
<%IP = request.getRequestURL().toString();%>
<%!
    /*
    * Happiness tests for axis2. These look at the classpath and warn if things
    * are missing. Normally addng this much code in a JSP page is mad
    * but here we want to validate JSP compilation too, and have a drop-in
    * page for easy re-use
    */
    String IP;

    /**
     * Get a string providing install information.
     * TODO: make this platform aware and give specific hints
     */
    public String getInstallHints(HttpServletRequest request) {

        return "<B><I>Note:</I></B> On Tomcat 4.x and Java1.4, you may need to put libraries that contain "
                + "java.* or javax.* packages into CATALINA_HOME/common/lib"
                + "<br>jaxrpc.jar and saaj.jar are two such libraries.";
    }

    /**
     * test for a class existing
     * @param classname
     * @return class iff present
     */
    Class classExists(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * test for resource on the classpath
     * @param resource
     * @return true iff present
     */
    boolean resourceExists(String resource) {
        boolean found;
        InputStream instream = this.getClass().getResourceAsStream(resource);
        found = instream != null;
        if (instream != null) {
            try {
                instream.close();
            } catch (IOException e) {
            }
        }
        return found;
    }

    /**
     * probe for a class, print an error message is missing
     * @param out stream to print stuff
     * @param category text like "warning" or "error"
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @return the number of missing classes
     * @throws IOException
     */
    int probeClass(JspWriter out,
                   String category,
                   String classname,
                   String jarFile,
                   String axisOperation,
                   String errorText,
                   String homePage) throws IOException {
        try {
            Class clazz = classExists(classname);
            if (clazz == null) {
                String url = "";
                if (homePage != null) {
                    url = "<br>  See <a href=" + homePage + ">" + homePage + "</a>";
                }
                out.write("<p>" + category + ": could not find class " + classname
                        + " from file <b>" + jarFile
                        + "</b><br>  " + errorText
                        + url
                        + "<p>");
                return 1;
            } else {
                String location = getLocation(out, clazz);
                if (location == null) {
                    out.write("Found " + axisOperation + " (" + classname + ")<br/>");
                } else {
                    out.write("Found " + axisOperation + " (" + classname + ") <br/> &nbsp;&nbsp;at " + location + "<br/>");
                }
                return 0;
            }
        } catch (NoClassDefFoundError ncdfe) {
            String url = "";
            if (homePage != null) {
                url = "<br>  See <a href=" + homePage + ">" + homePage + "</a>";
            }
            out.write("<p>" + category + ": could not find a dependency"
                    + " of class " + classname
                    + " from file <b>" + jarFile
                    + "</b><br> " + errorText
                    + url
                    + "<br>The root cause was: " + ncdfe.getMessage()
                    + "<br>This can happen e.g. if " + classname + " is in"
                    + " the 'common' classpath, but a dependency like "
                    + " activation.jar is only in the webapp classpath."
                    + "<p>");
            return 1;
        }
    }

    /**
     * get the location of a class
     * @param out
     * @param clazz
     * @return the jar file or path where a class was found
     */

    String getLocation(JspWriter out,
                       Class clazz) {
        try {
            java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            String location = url.toString();
            if (location.startsWith("jar")) {
                url = ((java.net.JarURLConnection) url.openConnection()).getJarFileURL();
                location = url.toString();
            }

            if (location.startsWith("file")) {
                java.io.File file = new java.io.File(url.getFile());
                return file.getAbsolutePath();
            } else {
                return url.toString();
            }
        } catch (Throwable t) {
        }
        return "an unknown location";
    }

    /**
     * a class we need if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int needClass(JspWriter out,
                  String classname,
                  String jarFile,
                  String axisOperation,
                  String errorText,
                  String homePage) throws IOException {
        return probeClass(out,
                "<b>Error</b>",
                classname,
                jarFile,
                axisOperation,
                errorText,
                homePage);
    }

    /**
     * print warning message if a class is missing
     * @param out stream to print stuff
     * @param classname class to look for
     * @param jarFile where this class comes from
     * @param errorText extra error text
     * @param homePage where to d/l the library
     * @throws IOException when needed
     * @return the number of missing libraries (0 or 1)
     */
    int wantClass(JspWriter out,
                  String classname,
                  String jarFile,
                  String axisOperation,
                  String errorText,
                  String homePage) throws IOException {
        return probeClass(out,
                "<b>Warning</b>",
                classname,
                jarFile,
                axisOperation,
                errorText,
                homePage);
    }

    /**
     * probe for a resource existing,
     * @param out
     * @param resource
     * @param errorText
     * @throws Exception
     */
    int wantResource(JspWriter out,
                     String resource,
                     String errorText) throws Exception {
        if (!resourceExists(resource)) {
            out.write("<p><b>Warning</b>: could not find resource " + resource
                    + "<br>"
                    + errorText);
            return 0;
        } else {
            out.write("found " + resource + "<br>");
            return 1;
        }
    }


    /**
     *  get servlet version string
     *
     */

    public String getServletVersion() {
        ServletContext context = getServletConfig().getServletContext();
        int major = context.getMajorVersion();
        int minor = context.getMinorVersion();
        return Integer.toString(major) + '.' + Integer.toString(minor);
    }


    /**
     * what parser are we using.
     * @return the classname of the parser
     */
    private String getParserName() {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return "Could not create an XML Parser";
        }

        // check to what is in the classname
        return saxParser.getClass().getName();
    }

    /**
     * Create a JAXP SAXParser
     * @return parser or null for trouble
     */
    private SAXParser getSAXParser() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        if (saxParserFactory == null) {
            return null;
        }
        SAXParser saxParser = null;
        try {
            saxParser = saxParserFactory.newSAXParser();
        } catch (Exception e) {
        }
        return saxParser;
    }

    /**
     * get the location of the parser
     * @return path or null for trouble in tracking it down
     */

    private String getParserLocation(JspWriter out) {
        SAXParser saxParser = getSAXParser();
        if (saxParser == null) {
            return null;
        }
        return getLocation(out, saxParser.getClass());
    }

    private String value;

    private OMElement createEnvelope() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace omNs = fac.createOMNamespace("http://axisversion.sample/xsd", "ns1");
        OMElement method = fac.createOMElement("getVersion", omNs);
        OMElement value = fac.createOMElement("myValue", omNs);
        method.addChild(value);
        return method;
    }

    public boolean invokeTheService() {
        try {
            // since this one is an internal request we do not use public frontendHostUrl
            // for it
            int lastindex = IP.lastIndexOf('/');
            IP = IP.substring(0, lastindex);
            ///axis2/axis2-web/services/version
            IP = IP.replaceAll("axis2-web", "");

            OMElement payload = createEnvelope();
            ConfigurationContext configctx =
                    ConfigurationContextFactory.createConfigurationContextFromFileSystem(null, null);
            ServiceClient client = new ServiceClient(configctx, null);
            EndpointReference targetEPR = new EndpointReference(IP + configctx.getServicePath() + "/Version");
            Options options = new Options();
            client.setOptions(options);
            options.setTo(targetEPR);
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);

            OMElement result = client.sendReceive(payload);
            StringWriter writer = new StringWriter();
            result.serialize(XMLOutputFactory.newInstance().createXMLStreamWriter(writer));
            writer.flush();
            value = writer.toString();
            return true;
        } catch (AxisFault axisFault) {
            System.out.println(value);
            return false;
        } catch (XMLStreamException e) {
            value = e.getMessage();
            return false;
        }
    }
    
    public String getFormatedSystemProperty(String systemProperty){
    	return  systemProperty.replaceAll(":", ": ");
    }
%>

<h1>Axis2 Happiness Page</h1>

<h2>Examining webapp configuration</h2>

<blockquote>

<h4>Essential Components</h4>

<%
    int needed = 0,wanted = 0;

    /**
     * the essentials, without these Axis is not going to work
     */
    needed = needClass(out, "org.apache.axis2.transport.http.AxisServlet",
            "axis2-1.0.jar",
            "Apache-Axis",
            "Axis2 will not work",
            "http://xml.apache.org/axis2/");
    needed += needClass(out, "org.apache.commons.logging.Log",
            "commons-logging.jar",
            "Jakarta-Commons Logging",
            "Axis2 will not work",
            "http://jakarta.apache.org/commons/logging.html");
    needed += needClass(out, "javax.xml.stream.XMLStreamReader",
            "stax-api-1.0.1.jar",
            "Streaming API for XML",
            "Axis2 will not work",
            "http://dist.codehaus.org/stax/jars/");
    needed += needClass(out, "org.codehaus.stax2.XMLStreamWriter2",
            "wstx-asl-3.0.1.jar",
            "Streaming API for XML implementation",
            "Axis2 will not work",
            "http://dist.codehaus.org/stax/jars/");

%>
<%
    /*
    * resources on the classpath path
    */
    /* broken; this is a file, not a resource
    wantResource(out,"/server-config.wsdd",
    "There is no server configuration file;"
    +"run AdminClient to create one");
    */
    /* add more libraries here */

    //is everything we need here
    if (needed == 0) {
        //yes, be happy
        out.write("<p><font color='green'><strong>The core axis2 libraries are present.</strong></font></p>");
    } else {
        //no, be very unhappy
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.write("<font color='red'><i>"
                + needed
                + " core axis2 librar"
                + (needed == 1 ? "y is" : "ies are")
                + " missing</i></font>");
    }
    //now look at wanted stuff
%>
<p>
    <B><I>Note:</I></B> Even if everything this page probes for is present,
    there is no guarantee your Axis Service will work, because there are many configuration options
    that we do not check for. These tests are <i>necessary</i> but not <i>sufficient</i>
</p>
</blockquote>
<h2>Examining Version Service</h2>
<%
    boolean serviceStatus = invokeTheService();
    if (serviceStatus) {
%>
<blockquote>
    <font color="green"><strong>
        Found Axis2 default Version service and Axis2 is working
        properly.</strong></font>
    <p>Now you can drop a service archive in axis2/WEB-INF/services.
        Following output was produced while invoking Axis2 version service
        </p>
        <p><%= value%></p>
</blockquote>

<%
} else {
%>
<p>
    <font color="brown"> There was a problem in Axis2 version service , may be
        the service not available or some thing has gone wrong. But this does
        not mean system is not working !
        Try to upload some other service and check to see whether it is
        working.
        <br>
    </font>
</p>

<%
    }
%>
<h2>Examining Application Server</h2>
<blockquote>
<table>
    <tr><td>Servlet version</td><td><%=getServletVersion()%></td></tr>
    <tr><td>Platform</td>
        <td><%=getServletConfig().getServletContext().getServerInfo()%></td>
    </tr>
</table>
</blockquote>
<h2>Examining System Properties</h2>
<%
    /**
     * Dump the system properties
     */
    java.util.Enumeration e = null;
    try {
        e = System.getProperties().propertyNames();
    } catch (SecurityException se) {
    }
    if (e != null) {
        out.write("<pre>");
        out.write("<table cellpadding='5px' cellspacing='0px' style='border: .5px blue solid;'>");
        for (; e.hasMoreElements();) {
            out.write("<tr>");
            String key = (String) e.nextElement();
            out.write("<th style='border: .5px #A3BBFF solid;'>" + key + "</th>");
            out.write("<td style='border: .5px #A3BBFF solid;'>" + getFormatedSystemProperty(System.getProperty(key)) + "&nbsp;</td>");
            out.write("<tr>");
        }
        out.write("</table>");
        out.write("</pre><p>");
    } else {
        out.write("System properties are not accessible<p>");
    }
%>

<jsp:include page="include/footer.inc"/>
</body>
</html>


