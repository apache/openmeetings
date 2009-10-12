<%@ page contentType="text/html" %>
<%@ page import="java.net.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.regex.*" %>
<%@ page import="java.util.zip.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.openlaszlo.utils.FileUtils.*" %>
<%@ page import="org.openlaszlo.xml.internal.XMLUtils.*" %>

<%@ page import="org.w3c.dom.*" %>
<%@ page import="javax.xml.transform.*" %>
<%@ page import="javax.xml.transform.dom.*" %>
<%@ page import="javax.xml.transform.stream.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>

    
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2009 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
<!-- @LZX_VERSION@                                                         -->


<html>
    <head>
    <title>SOLO DHTML Application Deployment Wizard</title>
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
int maxZipFileSize = 64000000; // 64MB max
int warnZipFileSize = 10000000; // warn at 10MB of content (before compression)
boolean warned = false;



String zipfilename = "";

String whatpage = request.getParameter("whatpage");
if (whatpage == null) {
    whatpage = "configure";
}

String appUrl = request.getParameter("appurl");
if (appUrl == null) {
    appUrl = "examples/animation/animation.lzx";
}

appUrl = appUrl.trim();


String title = request.getParameter("apptitle");
if (title == null) {  title = ""; }

URL wrapperUrl = null;
URL canvasUrl = null;

String appwidth = request.getParameter("appwidth");
String appheight = request.getParameter("appheight");

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
              Explanation: The SOLO deployment tool creates an
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



        canvasElt = parse(canvasbuf.toString(), out);

        String canvasdebug = canvasElt.getAttribute("debug");
        if ("true".equals(canvasdebug)) {
            %> <h2><font color="red">Note: your app has its canvas debug flag enabled, 
                the Laszlo DHTML debugger curently does not work in a standalone SOLO deployment, continue?</h2>
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
        
        // remove the servlet prefix and leading slash
        //  src="/legals/lps/includes/embed-dhtml.js"
        wrapper = wrapper.replaceAll(request.getContextPath()+"/", "");
        
        // Replace object file URL with SOLO filename
        // Lz.dhtmlEmbedLFC({url: 'animation.lzx?lzt=object&lzproxied=false&lzr=dhtml'
        // Lz.dhtmlEmbed({url: 'animation.lzx?lzt=object&lzr=dhtml&_canvas_debug=false',
        //                 bgcolor: '#eaeaea', width: '800', height: '300', id: 'lzapp'});

        //wrapper = wrapper.replaceAll("[.]lzx[?]lzt=object.*'", ".lzx.js'");
        wrapper = wrapper.replaceAll("[.]lzx[?]lzt=object.*?'", ".lzx.js'");

        // Replace the serverroot with a relative path
        // Lz.dhtmlEmbedLFC('/trunk/lps/includes/lfc/LFCdhtml.js', '/trunk');
        wrapper = wrapper.replaceFirst("lz.embed.lfc(.*?);",
                                       "lz.embed.lfc('lps/includes/lfc/LFCdhtml.js', 'lps/resources/');");

        // debugging print
        if (false) {
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

<font face="helvetica,arial"> <b> <i> Setup SOLO DHTML Application Deployment</i> </b> </font>
<hr align="left" width="420" height="2"/>


<br>
     
<table><tr><td width=600>
     This wizard will generate a zip file containing all the resources you need to deploy a serverless (SOLO) application. For deployments which do not require the Javscript browser integration support files, it  will also generate some simple HTML wrappers which can be cut and pasted into HTML pages.
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
  <tr><td/><td/></tr>

                                        
</table>
<p>
<input type=submit value="Continue...">


</form>
<p>
<font face="helvetica,arial"> <b> <i> Note</i> </b> </font>
<hr align="left" width="420" height="2"/>

                                        
<table><tr><td width=600>

The SOLO deployment tool creates a
Zip archive of <i>all</i> files, recursively, from the <i>directory</i> that
contains your specified application source file. So it is best to use this SOLO tool
on an application which resides in its own directory.                                         
If there are other applications in the same directory, this tool will copy
all of those apps and their assets (and subdirectories) into the Zip file. That may not be what you want.


</td></tr><table>

<%
} else if  (whatpage.equals("preview")){


    

%>
<font face="helvetica,arial"> <b> <i> Preview SOLO Application in Browser</i> </b> </font>
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
<input type="hidden" name="apptitle" value="<%= title %>">

<p>
<input type=submit value="Continue...">

<%

} else if  (whatpage.equals("download")){
%>
<%
     String htmlfile = "";

     // add in all the files in the app directory
     ServletContext ctx = getServletContext();

     // destination to output the zip file, will be the current jsp directory
     File tmpdir = new File(ctx.getRealPath(request.getServletPath().toString())).getParentFile();
     tmpdir = tmpdir.getCanonicalFile();

     // The absolute path to the base directory of the server web root 
     File basedir = new File(ctx.getRealPath(request.getContextPath().toString())).getParentFile();
     basedir = basedir.getCanonicalFile();

     // The absolute path to the application directory we are packaging
     // e.g., demos/amazon
     File appdir = new File(ctx.getRealPath(appUrl)).getParentFile();
     appdir = appdir.getCanonicalFile();

     // Keep track of which files we have output to the zip archive, so we don't
     // write any duplicate entries.
     HashSet zippedfiles = new HashSet();

     // These are the files to include in the ZIP file
     ArrayList filenames = new ArrayList();
     // LPS includes, (originally copied from /lps/includes/*)
     filenames.add("lps/includes/embed-compressed.js");
     filenames.add("lps/includes/blank.gif");
     filenames.add("lps/includes/spinner.gif");
     filenames.add("lps/includes/excanvas.js");	
     filenames.add("lps/includes/laszlo-debugger.css");
     filenames.add("lps/includes/laszlo-debugger.html");

     ArrayList appfiles = new ArrayList();
     listFiles(appfiles, appdir);

    // Create a buffer for reading the files
     byte[] buf = new byte[1024];
     char[] cbuf = new char[1024];
    
     try {
         // Create the ZIP file
         SimpleDateFormat format = 
             new SimpleDateFormat("MMM_dd_yyyy_HH_mm_ss");
         String outFilename = "solo_deploy_" + format.format(new Date()) + ".zip";
         zipfilename = outFilename;
         ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tmpdir+"/"+outFilename));

         // create a byte array from lzhistory wrapper text
         htmlfile = new File(appUrl).getName()+".html";

         byte lbytes[] = wrapper.getBytes();
         //Write out a copy of the lzhistory wrapper as appname.lzx.html
         out.println("<br>copyFileToZipFile dstfixed="+htmlfile+" lookup "+zippedfiles.contains(htmlfile));
         copyByteArrayToZipFile(zout, lbytes, htmlfile, zippedfiles);

         // Compress the include files
         for (int i=0; i<filenames.size(); i++) {
             String srcfile = basedir + "/" + (String) filenames.get(i);
             // Add ZIP entry to output stream.
             String dstfile = (String) filenames.get(i);
             copyFileToZipFile(zout, srcfile, dstfile, zippedfiles);
         }

         // special case for IE7, need to copy lps/includes/blank.gif to lps/resources/lps/includes/blank.gif
         String srcfile = basedir + "/" + "lps/includes/blank.gif";
         String dstfile = "lps/resources/lps/includes/blank.gif";
         copyFileToZipFile(zout, srcfile, dstfile, zippedfiles);

        // Copy the DHTML LFC to lps/includes/LFC-dhtml.js
         ArrayList lfcfiles = new ArrayList();
         listFiles(lfcfiles, new File(basedir + "/lps/includes/lfc"));
         for (int i=0; i<lfcfiles.size(); i++) {
             String fname = (String) lfcfiles.get(i);
             if (!fname.matches(".*LFCdhtml.*.js")) { continue; }
             String stripped = fname.substring(basedir.getCanonicalPath().length()+1);
             copyFileToZipFile(zout, fname, stripped, zippedfiles);
         }

         // track how big the file is, check that we don't write more than some limit
         int contentSize = 0;

         // Compress the app files
     for (int i=0; i<appfiles.size(); i++) {
         String srcname = (String) appfiles.get(i);
         String dstname = srcname.substring(appdir.getPath().length()+1);
         // Add ZIP entry to output stream.
         copyFileToZipFile(zout, srcname, dstname, zippedfiles);

         if (contentSize > maxZipFileSize) {
             throw new IOException("file length exceeds max of "+ (maxZipFileSize/1000000) +"MB");
         }

         if (contentSize > warnZipFileSize && !warned) {

             warned = true;
             %> 
                 <h3><font color="red">The zip file has had more than <%= warnZipFileSize / 1000000 %>MB of content added to it, perhaps this is what you intended, but remember that the SOLO deployment tool creates an
                      archive of all files, recursively, from the directory that
                      contains your specified application source file.  If your application source file
                      is in a directory with other apps, this tool will create a zip that
                      contains all those apps and their assets (and subdirectories) as well. 
                      </h3>


                      <% }

     }



         // Complete the ZIP file
         zout.close();
     } catch (IOException e) {
  %>         
  <h3><font color="red">Error generating zip file: <%= e.toString() %></h3>
  <%
   }
  %>
     <font face="helvetica,arial"> <b> <i> Zip file containing application deployment files</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>



Click here to download zip-archived file <a href="<%=zipfilename%>"><tt><%=zipfilename%></tt></a>.
<p>
In the zip file, a wrapper HTML file named <tt><%= htmlfile %></tt> has been created
to launch your SOLO application.
<p>


Note: the file may take a moment to generate and save to disk, please be patient.

  <p>
<font face="helvetica,arial"> <b> <i> SOLO Application Deployment: Wrapper HTML</i> </b> </font>
<hr align="left" width="420" height="2"/>
<p>
Paste this wrapper into a browser to deploy your app:

<p>
<textarea rows="20" cols="80">
<%= wrapper %>
</textarea>
<p>
<%

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


public String fixSlashes (String path) {
       return(path.replace('\\', '/'));
}

public Element getChild(Element elt, String name) {
    NodeList elts = elt.getChildNodes();
    for (int i=0; i < elts.getLength(); i++) {
        Node child = elts.item(i);
        if (child instanceof Element && ((Element)child).getTagName().equals(name)) {
            return (Element) child;
        }
    }
    return null;
}

public Element parse(String content, javax.servlet.jsp.JspWriter out) throws IOException {
    try {
        // Create a DOM builder and parse the fragment
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        Document d = factory.newDocumentBuilder().parse( new
                                                         org.xml.sax.InputSource(new StringReader(content)) );

        return d.getDocumentElement();

    } catch (java.io.IOException e) {
            out.println("Could not parse XML  '" + content + "'");
            e.printStackTrace();
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            out.println("Could not parse XML  '" + content + "'");
            e.printStackTrace();
        } catch (org.xml.sax.SAXException e) {
            out.println("Could not parse XML  '" + content + "'");
            e.printStackTrace();
        }
    return null;
}


%>
