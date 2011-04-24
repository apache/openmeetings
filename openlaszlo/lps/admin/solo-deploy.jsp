<%@ page contentType="text/html" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.util.zip.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.openlaszlo.utils.FileUtils.*" %>
<%@ page import="org.openlaszlo.server.LPS" %>
<%@ page import="org.openlaszlo.utils.DeployUtils" %>
    
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2010 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->


<html>
    <head>
    <title>OpenLaszlo Widget Application Deployment Wizard</title>
    </head>
    <body>

<%

    /*
      We want an option to deploy an app and it's entire directory.

      So, for an app with is at /foo/bar/baz.lzx

      That should make a zip file which is relative to the web root and has
      /lps/includes/**
      /foo/bar/**   -- will include the SOLO .lzx.swfN.swf file(s)
      /foo/bar/baz.lzx.html  -- the wrapper file
    */


// Set this to make a limit on the size of zip file that is created
int maxZipFileSize = 64000000; // 64MB max
int warnZipFileSize = 10000000; // warn at 10MB
boolean warned = false;
// Directory on server where generated widget files are stored
String WGT_WORKING_DIR = "tmpwgt";



String zipfilename = "";

String whatpage = request.getParameter("whatpage");
if (whatpage == null) {
    whatpage = "configure";
}

String appUrl = request.getParameter("appurl");
if (appUrl == null) {
    appUrl = "";
}

appUrl = appUrl.trim();


// Get the application target runtime, default to swf8
String appRuntime = request.getParameter("runtime");
if (appRuntime == null) {
    appRuntime = "swf8";
}

String title = request.getParameter("apptitle");
if (title == null) {  title = ""; }

URL wrapperUrl = null;
URL lzhistUrl = null;

String appwidth = request.getParameter("appwidth");
String appheight = request.getParameter("appheight");

// The "widgettype" arg controls which flavor of config.xml file we generate.
// We will look for a config file named config.WIDGET_TYPE.xslt,
// fallback to Opera format config file
String widgetType = request.getParameter("widgettype");

// Get app width/height from its canvas wrapper

// download text content of URL
StringBuffer wrapperbuf = new StringBuffer();
StringBuffer lzhistbuf = new StringBuffer();
String lzhistwrapper = "";


/*
request.getContextPath(): /lps-dev
request.getRequestURI(): /lps-dev/hqm/test/solo-deploy.jsp
request.getRequestURL(): http://localhost:8080/lps-dev/hqm/test/solo-deploy.jsp
request.getServletPath(): /hqm/test/solo-deploy.jsp
*/

String sUrl = request.getRequestURL().toString();
String servletPath = request.getServletPath();
String baseUrl = sUrl.substring(0, (sUrl.length() - servletPath.length())+1);

if (appUrl != null && appUrl.length() > 0) {
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
place the file in a subdirectory of the server root directory and try
again with the new path.<p>
              Explanation: The widget deployment tool creates an
archive of all files, recursively, starting in the directory that
contains the application source file.  If the application source file
is in the servlet root container, this tool will create a zip that
contains all the files inside the root directory.  This directory
contains the entire OpenLaszlo binary distribution, so this is almost
certainly not what you want.
       <%        
          return;
        }

        wrapperUrl = new URL(new URL(baseUrl),
                             appUrl + "?lzt=html-object&lzproxied=false&lzr="+appRuntime);

        lzhistUrl = new URL(new URL(baseUrl),
                             appUrl + "?lzt=html&lzproxied=false&lzr="+appRuntime);

        URL swfUrl = new URL(new URL(baseUrl),
                             appUrl + "?lzr="+appRuntime+"&lzproxied=false");

        // Grab a copy of the html-object wrapper
        String str;
        BufferedReader in = new BufferedReader(new InputStreamReader(wrapperUrl.openStream()));
        while ((str = in.readLine()) != null) 
        {
            wrapperbuf.append(str+"\n");
        }
        in.close();

        // load a copy of the lzhistory HTML wrapper
        in = new BufferedReader(new InputStreamReader(lzhistUrl.openStream()));
        while ((str = in.readLine()) != null) 
        {
            lzhistbuf.append(str+"\n");
        }
        in.close();

        // Load a copy of the app url , causing the compiler to run
        in = new BufferedReader(new InputStreamReader(swfUrl.openStream()));
        while ((str = in.readLine()) != null) 
        {
        }
        in.close();

        lzhistwrapper = lzhistbuf.toString();
        // We need to adjust the lzhistory wrapper, to make the path to lps/includes/embed.js
        // be relative rather than absolute.
        
        // remove the servlet prefix and leading slash
        lzhistwrapper = lzhistwrapper.replaceAll(request.getContextPath()+"/", "");
        lzhistwrapper = lzhistwrapper.replaceAll(request.getContextPath(), "");
        lzhistwrapper = lzhistwrapper.replaceAll("[.]lzx[?]lzt=swf", ".lzx."+appRuntime+".swf?");

    } 
    catch (MalformedURLException e) { %>

 <h3><font color="red">Error retrieving URL <%= appUrl %>: <%= e.toString() %></h3>
      <% }
    catch (IOException e) { %>

 <h3><font color="red">Error retrieving URL <%= appUrl %>: <%= e.toString() %></h3>
      <% } 
} else {

    appUrl = "examples/animation/animation.lzx";
}
        
String wrapper = wrapperbuf.toString();

// replace title
wrapper = wrapper.replaceFirst("<title>.*</title>", "<title>"+title+"</title>\n");



// extract width and height with regexp

Pattern pwidth = Pattern.compile("width=\"([0-9]*)\"");
Pattern pheight = Pattern.compile("height=\"([0-9]*)\"");
Matcher mwidth = pwidth.matcher(wrapper);
Matcher mheight = pheight.matcher(wrapper);

if (mwidth.find()) {
    appwidth = mwidth.group(1);
} else {
    appwidth = "640";
}


if (mheight.find()) {
    appheight = mheight.group(1);
} else {
    appheight = "400";
}

int nwidth = 640;
int nheight = 400;

try {
    nwidth = Integer.parseInt(appwidth);
    nheight = Integer.parseInt(appheight);
} catch (Exception e) {
    out.println(e.toString());
}



    // if no form vars, we are at page #0
    if (whatpage.equals("configure")) { 
%>
<font face="helvetica,arial"> <b> <i> Setup Widget Application Deployment</i> </b> </font>
<hr align="left" width="420" height="2"/>


<br>
     
<table><tr><td width=600>
     This wizard will generate a zip file containing all the resources you need to deploy a serverless SOLO widget application. For deployments which do not require browser the Javscript browser integration support files, it  will also generate some simple HTML wrappers which can be cut and pasted into HTML pages.
</td></tr><table>

<form  method="POST" action="<%= sUrl %>">
<input type="hidden" name="runtime" value="<%= appRuntime %>">
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
</select>
</td>
  </tr>
  <tr><td/><td/></tr>
                                        
</table>
<p>
<input type=submit value="Continue...">


</form>
<p>
<%
} else if  (whatpage.equals("preview")){


    

%>
<font face="helvetica,arial"> <b> <i> Preview Widget Application in Browser</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>

<%
String soloURL = (request.getContextPath()+"/" + appUrl + "."+appRuntime+".swf?lzproxied=false");
%>

<tt>Using URL</tt> <a href="<%= soloURL %>"><tt><%= soloURL %></tt></a>

<p>


<tt>Size = <%= appwidth %> x  <%= appheight %></tt>
    <p>
    
<iframe width="<%= nwidth +20 %>" height="<%= nheight +20 %>" src="<%= lzhistUrl %>"></iframe>
     <p>
<form  method="POST">
<input type=radio name="whatpage" value="download" checked> OK, give me the HTML wrapper code
<p> 
<input type=radio name="whatpage" value="configure">Go back to change</td>
<input type="hidden" name="appurl" value="<%= appUrl %>">
<input type="hidden" name="apptitle" value="<%= title %>">
<input type="hidden" name="runtime" value="<%= appRuntime %>">     
<input type="hidden" name="widgettype" value="<%= widgetType %>">     

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


    DeployUtils.buildZipFile(appRuntime, zout, basedir, appdir, new PrintWriter(out), null, lzhistwrapper,  widgetType, appUrl,  title,  appheight,  appwidth);
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
<p>
Note: the file may take a moment to generate and save to disk, please be patient.
  <p>
<font face="helvetica,arial"> <b> <i> Widget Application Deployment: Wrapper HTML</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>
Paste this wrapper into a browser to deploy your app:
<p>
<textarea rows="20" cols="80">
<!DOCTYPE html
  PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
      <link rel="SHORTCUT ICON" href="http://www.laszlosystems.com/favicon.ico">
      <title><%= title %></title>
<style type="text/css">
          html, body { margin: 0; padding: 0; height: 100%; }
          body { background-color: #eaeaea; }
        </style></head>
   <body><object type="application/x-shockwave-flash" data="<%= appUrl %>.<%= appRuntime %>.swf?lzproxied=false" width="<%= appwidth %>" height="<%= appheight %>">
         <param name="movie" value="<%= appUrl %>.<%= appRuntime %>.swf?lzproxied=false">
         <param name="quality" value="high">
         <param name="scale" value="noscale">
         <param name="salign" value="LT">
         <param name="menu" value="false"></object></body>
</html>
</textarea>

<p>



<font face="helvetica,arial"> <b> <i> Browser History/Integration Wrapper HTML</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>
Paste this wrapper into a browser to deploy your app:

<p>
<textarea rows="20" cols="80">
<%= lzhistwrapper %>
</textarea>
<p>



     <font face="helvetica,arial"> <b> <i> HTML code to pop up a new window with the app</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>
     
<textarea rows="20" cols="80">
<a href="<%= appUrl %>" target=_blank
    onClick="
    swin=window.open('', '<%= title %>', 'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=<%= nwidth %>,height=<%= nheight %>');

swin.document.write('<!DOCTYPE html');
swin.document.write('  PUBLIC \'-//W3C//DTD HTML 4.01 Transitional//EN\' \'http://www.w3.org/TR/html4/loose.dtd\'>');
     swin.document.write('<html>');
swin.document.write('   <head>');
swin.document.write('      <meta http-equiv=\'Content-Type\' content=\'text/html; charset=utf-8\'>');
swin.document.write('   ');
swin.document.write('      <link rel=\'SHORTCUT ICON\' href=\'http://www.laszlosystems.com/favicon.ico\'>');
swin.document.write('      <title>Laszlo Application</title>');
swin.document.write('<style type=\'text/css\'>');
swin.document.write('          html, body { margin: 0; padding: 0; height: 100%; }');
swin.document.write('          body { background-color: #eaeaea; }');
swin.document.write('        </style></head>');
swin.document.write('   <body><object type=\'application/x-shockwave-flash\' data=\'<%= appUrl %>.<%= appRuntime %>.swf?lzproxied=false\' width=\'640\' height=\'400\'>');
swin.document.write('         <param name=\'movie\' value=\'<%= appUrl %>.<%= appRuntime %>.swf?lzproxied=false\'>');
swin.document.write('         <param name=\'quality\' value=\'high\'>');
swin.document.write('         <param name=\'scale\' value=\'noscale\'>');
swin.document.write('         <param name=\'salign\' value=\'LT\'>');
swin.document.write('         <param name=\'menu\' value=\'false\'></object></body>');
swin.document.write('</html>');

    swin.focus();
    return false"
    onmouseover="window.status='Open in a new window';return true" onmouseout="window.status='';return true">Launch App Window</a>

</textarea>
<p>

    

<%

}
    }


%>
</body>
    </html>

<%! 
    // utility methods


    public void listFiles(ArrayList fnames, File dir) {
    if (dir.isDirectory()) {   
        if (!(dir.getName().startsWith(".svn"))) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                listFiles(fnames, new File(dir, children[i]));
            }
        }
    } else {
        fnames.add(dir.getPath());
    }
  }

public void copyByteArrayToZipFile (ZipOutputStream zout,
                               byte lbytes[],
                               String dstfile,
                               Set zipped)
  throws java.io.IOException
{
    zout.putNextEntry(new ZipEntry(fixSlashes(dstfile)));
    zout.write(lbytes, 0, lbytes.length);
    zout.closeEntry();
    zipped.add(fixSlashes(dstfile));
}



public void copyFileToZipFile (ZipOutputStream zout,
                               String srcfile,
                               String dstfile,
                               Set zipped)
  throws java.io.IOException, java.io.FileNotFoundException {
    String dstfixed = fixSlashes(dstfile);
    if (zipped.contains(dstfixed)) {
        return;
    }
    FileInputStream in = new FileInputStream(srcfile);
    // Add ZIP entry to output stream.
    zout.putNextEntry(new ZipEntry(dstfixed));
    // Transfer bytes from the file to the ZIP file
    int len;
    byte[] buf = new byte[1024];
    while ((len = in.read(buf)) > 0) {
        zout.write(buf, 0, len);
    }
    // Complete the entry
    zout.closeEntry();
    in.close();
    zipped.add(dstfixed);
}


public String readFile(File file)
  throws IOException
{
    java.io.InputStream istr = new java.io.FileInputStream(file);
    byte bytes[] = new byte[istr.available()];
    istr.read(bytes);
    istr.close();
    return new String(bytes, "UTF-8");
}


public String fixSlashes (String path) {
       return(path.replace('\\', '/'));
}



%>
