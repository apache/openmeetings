<%@ page contentType="text/html" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.util.zip.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.openlaszlo.utils.FileUtils.*" %>
<%@ page import="org.openlaszlo.xml.internal.XMLUtils.*" %>
<%@ page import="org.openlaszlo.server.LPS" %>
<%@ page import="org.openlaszlo.utils.DeployUtils" %>


<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.transform.*" %>
<%@ page import="javax.xml.transform.dom.*" %>
<%@ page import="javax.xml.transform.stream.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>

    
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2010 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->


<html>
    <head>
    <title>W3C Widget HTML5 Application Deployment Wizard</title>
    </head>
    <body>

<%

    /*
      We want an option to deploy an app and it's entire directory.

      So, for an app with is at /foo/bar/baz.lzx

      + /lps/includes/** ==> lps/includes/**

      + /foo/bar/**   -- will include the SOLO .lzx.js file(s)

      + /foo/bar/baz.lzx.html  -- the wrapper file

      + A copy of the LFC will be placed in lps/includes/LFC-dhtml.js

      + All resources which are external to /foo/bar will be copied into
      a subdir named /foo/bar/lps/resources/**
      

    */


// Set this to make a limit on the size of zip file that is created

String whatpage = request.getParameter("whatpage");
if (whatpage == null) {
    whatpage = "configure";
}

String appUrl = request.getParameter("appurl");
if (appUrl == null) {
    appUrl = "examples/animation/animation.lzx";
}

appUrl = appUrl.trim();

// Directory on server where generated widget files are stored
String WGT_WORKING_DIR = "tmpwgt";


int maxZipFileSize = 64000000; // 64MB max
int warnZipFileSize = 10000000; // warn at 10MB of content (before compression)
boolean warned = false;
String zipfilename = "";


String title = request.getParameter("apptitle");
if (title == null) {  title = ""; }

URL wrapperUrl = null;
URL canvasUrl = null;

String appwidth = request.getParameter("appwidth");
String appheight = request.getParameter("appheight");

// The "widgettype" arg controls which flavor of config.xml file we generate.
// We will look for a config file named config.WIDGET_TYPE.xslt,
// fallback to Opera format config file
String widgetType = request.getParameter("widgettype");


// Get app width/height from its canvas wrapper

// download text content of URL
StringBuffer wrapperbuf = new StringBuffer();
StringBuffer canvasbuf = new StringBuffer();
Element canvasElt = null;

String wrapper = null;


/*
request.getContextPath(): /lps-dev
request.getRequestURI(): /lps-dev/hqm/test/solo-deploy.jsp
request.getRequestURL(): http://localhost:8080/lps-dev/hqm/test/solo-deploy.jsp
request.getServletPath(): /hqm/test/solo-deploy.jsp
*/

String sUrl = request.getRequestURL().toString();
String servletPath = request.getServletPath();
String baseUrl = sUrl.substring(0, (sUrl.length() - servletPath.length())+1);

//LPS.initialize();

// remove dangerous pathname components, "..", and "//"

if (appUrl.indexOf("..") != -1 || appUrl.indexOf("//") != -1) {
    %>
<h3><font color="red">Error, do not use '..' or '//' in your app pathname:  <%= appUrl %></font></h3>
                                    <%
                                    }
try 
    {
        // okee dokee. Now we have to adjust the app path to be relative to
        // the server document root. 

        // say that appUrl == demos/vacation-survey/vacation-survey.lzx

        // trim leading slash
        if (appUrl.charAt(0) == '/') {
            appUrl = appUrl.substring(1);
        }


        // If there are no "/" separators in the non-zero position in the
        // url string , then the app url is at the LPS_HOME root, and that
        // is bad thing to try to zip up recursively, so issue a warning
        // and refuse to do it.
        if (appUrl.indexOf("/") == -1) {
        %>
        <h3><font color="red">Error</font> bad location for app file</h3>
You entered <tt><i><%= appUrl %></i></tt>, which names a file in the server document root directory. Please
place the file in a subdirectory of the server root directory and run this deployment tool
again with the new path.<p>
              Explanation: The W3C Widget deployment tool creates an
archive of all files, recursively, starting in the directory that
contains the application source file.  If the application source file
is in the servlet root directory, this tool will create a zip that
contains all the files inside the root directory.  This directory
contains the entire OpenLaszlo binary distribution, so this is 
probably not what you want. 

       <%        
          return;
        }

        wrapperUrl = new URL(new URL(baseUrl),
                             appUrl + "?lzr=dhtml&lzt=html&lzproxied=false&lzcopyresources=true");

        canvasUrl = new URL(new URL(baseUrl),
                             appUrl + "?lzr=dhtml&lzt=canvas&lzproxied=false&lzcopyresources=true");


        // load the app's canvas descriptor from the compiler
        String str;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(canvasUrl.openStream()));
        } catch (IOException urlex) {
            %> Error occurred when trying to fetch url <a href="<%=canvasUrl%>"><%=canvasUrl%></a> <%
         return;
        }
        // Need to check for HTTP 404 or other error
        while ((str = in.readLine()) != null) 
        {
            canvasbuf.append(str+"\n");
        }
        in.close();



        canvasElt = DeployUtils.parse(canvasbuf.toString());

        String canvasdebug = canvasElt.getAttribute("debug");
        if ("true".equals(canvasdebug)) {
            %> <h2><font color="red">Note: your app has its canvas debug flag enabled, 
                the Laszlo DHTML debugger curently does not work in a standalone widget deployment, continue?</h2>
                <%
        }
        
        // load a copy of the HTML wrapper
        in = new BufferedReader(new InputStreamReader(wrapperUrl.openStream()));
        while ((str = in.readLine()) != null) 
        {
            wrapperbuf.append(str+"\n");
        }
        in.close();

        wrapper = wrapperbuf.toString();

        // We need to adjust the  wrapper, to make the path to lps/includes/dhtml-embed.js
        // be relative rather than absolute.
        wrapper = DeployUtils.adjustDHTMLWrapper(wrapper, request.getContextPath());

        // debugging print
        if (false) {
            out.println("lpspath="+request.getContextPath()+"<br>");

        %><verbatim><pre><%= org.openlaszlo.xml.internal.XMLUtils.escapeXml(wrapper)%></pre></verbatim><%
        }
    } 
    catch (MalformedURLException e) { %>

 <h3><font color="red">Error retrieving URL <%= appUrl %>: <%= e.toString() %></h3>
      <% }
    catch (IOException e) { %>

 <h3><font color="red">Error retrieving URL <%= appUrl %>: <%= e.toString() %></h3>
      <% } 

        
// replace title

// wrapper = wrapper.replaceFirst("<title>.*</title>", "<title>"+title+"</title>\n");

// extract width and height with regexp

appwidth = canvasElt.getAttribute("width");
appheight = canvasElt.getAttribute("height");

int nwidth = 640;
int nheight = 400;

try {
    nwidth = Integer.parseInt(appwidth);
} catch (Exception e) {
    if (!appwidth.matches("\\d+%")) {
          out.println("<br/><font color='red'>Error: can't parse app canvas width string "+appwidth+". "+e.toString()+
                      "</font>");
    }
}

try {
    nheight = Integer.parseInt(appheight);
} catch (Exception e) {
    if (!appheight.matches("\\d+%")) {
          out.println("<br/><font color='red'>Error: can't parse app canvas height string "+appheight+". "+e.toString()+
                      "</font>");
    }
}






// if no form vars, we are at page #0
if (whatpage.equals("configure")) { 
%>

<font face="helvetica,arial"> <b> <i> Setup W3C Widget HTML5 Application Deployment</i> </b> </font>
<hr align="left" width="420" height="2"/>


<br>
     
<table><tr><td width=600>
     This wizard will generate a zip file containing all the resources you need to deploy a serverless (Widget) application. For deployments which do not require the Javscript browser integration support files, it  will also generate some simple HTML wrappers which can be cut and pasted into HTML pages.
</td></tr><table>

<form  method="POST" action="<%= sUrl %>">
<input type="hidden" name="whatpage" value="preview">
<table border=0 width=800>
  <tr>
     <tr><td/><td align="left"><i>Use a pathname relative to the LPS server root, e.g. if the LPS server is mapped to</i> <tt>http://localhost:8080/lps</tt><i>, and your application is accessed at </i><tt>http://localhost:8080/lps/examples/animation/animation.lzx</tt><i> then enter </i><tt><b>examples/animation/animation.lzx</b></tt></td>
</tr>
    <td align="right">Enter pathname of your application:</td><td><input name="appurl" size="64" type="text" value="<%= appUrl %>"/></td>
  </tr>
  <tr>
    <td align="right">Title for web page:</td><td><input name="apptitle" size="40" type="text" value="Laszlo Application"/></td>
  </tr>
  <tr>
     <td align="right">Widget Type (config.xml file):</td><td>
<select name="widgettype">
<option value="opera" selected>Opera</option>
<option value="jil">Android</option>
<option value="osx">Apple Dashboard Widget</option>
<option value="w3c" selected>W3C Widget</option>
</select>
</td>
  </tr>
  <tr><td/><td/></tr>

                                        
</table>
<p>
<input type=submit value="Continue...">


</form>
<p>
<font face="helvetica,arial"> <b> <i> Note</i> </b> </font>
<hr align="left" width="420" height="2"/>

                                        
<table><tr><td width=600>

The W3C Widget deployment tool creates a
Zip archive of <i>all</i> files, recursively, from the <i>directory</i> that
contains your specified application source file. So it is best to use this tool
on an application which resides in its own directory.                                         
If there are other applications in the same directory, this tool will copy
all of those apps and their assets (and subdirectories) into the Zip file. That may not be what you want.


</td></tr><table>

<%
} else if  (whatpage.equals("preview")){


    

%>
<font face="helvetica,arial"> <b> <i> Preview W3C Widget Application in Browser</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>

<%
String soloURL = (request.getContextPath()+"/" + appUrl + ".html?lzr=dhtml&lzproxied=false");
String exampleURL = (request.getContextPath()+"/" + appUrl + "?lzr=dhtml&lzproxied=false");

%>

<tt>Using URL</tt> <a href="<%= exampleURL %>"><tt><%= exampleURL %></tt></a>

<p>


<tt>Size = <%= appwidth %> x  <%= appheight %></tt>
    <p>
    
<iframe width="<%= nwidth +20 %>" height="<%= nheight +20 %>" src="<%= wrapperUrl %>"></iframe>
     <p>
<form  method="POST">
<input type=radio name="whatpage" value="download" checked> OK, give me the HTML wrapper code
<p> 
<input type=radio name="whatpage" value="configure">Go back to change</td>
<input type="hidden" name="appurl" value="<%= appUrl %>">
<input type="hidden" name="widgettype" value="<%= widgetType %>">     
<input type="hidden" name="apptitle" value="<%= title %>">

<p>
<input type=submit value="Continue...">

<%

     } else if (whatpage.equals("emulator") || whatpage.equals("download")) {

    org.openlaszlo.server.LPS.initialize();

    //whatpage=download&appurl=examples%2Fcontactlist%2Fcontactlist.lzx&widgettype=jil&apptitle=Laszlo+Application
    // Create widget working directory, and clear any previously existing files
    File workdir = new File(LPS.HOME()+ File.separator + WGT_WORKING_DIR);
    workdir.mkdirs();
    DeployUtils.deleteDirectoryFiles(workdir);

    // destination to output the zip file
    String outdir = workdir.getCanonicalPath();
    File tmpdir = new File(outdir);

    // Create the ZIP file
    SimpleDateFormat format = 
        new SimpleDateFormat("MMM_dd_yyyy_HH_mm_ss");
    String datestamp = format.format(new Date());
    String outFilename = "solo_deploy_" + datestamp + ".wgt";
    zipfilename = outFilename;
    ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tmpdir+"/"+outFilename));

    // add in all the files in the app directory
    ServletContext ctx = getServletContext();
    // The absolute path to the base directory of the server web root 
    File basedir = new File(ctx.getRealPath(request.getContextPath().toString())).getParentFile();
    basedir = basedir.getCanonicalFile();

    // The absolute path to the application directory we are packaging
    // e.g., demos/amazon
    File appdir = new File(ctx.getRealPath(appUrl)).getParentFile();
    appdir = appdir.getCanonicalFile();

    DeployUtils.buildZipFile("dhtml", zout, basedir, appdir, new PrintWriter(out), null, wrapper,  widgetType, appUrl,  title,  appheight,  appwidth);
    if (whatpage.equals("emulator")) {
        // unpack to working dir
        // redirect to config.xml in working dir
        //out.println("<br>zipfilename: "+zipfilename);
        DeployUtils.unpackZipfile(workdir + File.separator + zipfilename, workdir.getCanonicalPath(), new PrintWriter(out));
        String redirectURL = (request.getContextPath()+ "/" + WGT_WORKING_DIR);
        response.sendRedirect(redirectURL);
    } else {

%>
   <font face="helvetica,arial"> <b> <i> Zip file containing application deployment files</i> </b> </font>
   <hr align="left" width="420" height="2"/>
   <p>
Click here to download zip-archived file <a href="<%= request.getContextPath() + File.separator + WGT_WORKING_DIR + File.separator + zipfilename%>"><tt><%=zipfilename%></tt></a>.

   <p/>
   Note: the file may take a moment to generate and save to disk, please be patient.
  <p/>
  <hr align="left" width="420" height="2"/>
  <p>
<%
            }
}


%>
</body>
    </html>

