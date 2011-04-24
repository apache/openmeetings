<!--
@!@!@!@!@ ATTENTION EDITORS OF THIS FILE @!@!@!@!@

If you edit this file, please validate your work using http://validator.w3.org/
-->
<!-- charset needs to match xsl charset -->
<%@ page contentType="text/html; charset=UTF-8"
      import="java.util.*,
      java.io.*,
      javax.servlet.*,
      javax.xml.transform.*,
      org.jdom.*,
      org.jdom.input.*,
      org.openlaszlo.compiler.FileResolver,
      org.jdom.output.*"%>

<%!
    /**
     * Trims / from directory name.
     */
    public String trim(String dirName) {
        if (dirName.length() == 0) return "";
        StringBuffer buf = new StringBuffer(dirName);
        int len = buf.length();
        for (int i = len-1; 0 <= i && buf.charAt(i) == '/'; --i) {
            buf.deleteCharAt(i);
        }
        return buf.toString();
    }
%>


<%
    String webappHome = getServletConfig().getServletContext().getRealPath("/");
    if (webappHome == null) {
        throw new ServletException("can't get real path for webapp");
    }

    String viewer = new File(request.getServletPath()).getName();
    String ctx = new File(getServletContext().getRealPath(request.getServletPath())).getParent();
    java.net.URL requestURL = new java.net.URL(HttpUtils.getRequestURL(request).toString());

    String xform = request.getParameter("mode");
    String fname = request.getParameter("file");
    String base = request.getParameter("base");
    if (xform == null) xform = "viewer";
    if (fname == null) fname = "/demos";
    if (base == null) base = "";

    boolean found = false;

    if (new File(getServletContext().getRealPath(base), fname).exists()) {
      fname = new File(base, fname).toString();
      found = true;
    } else {
      try {
	String p = getServletContext().getRealPath(fname);
	if (p != null && !new File(p).exists()) {
	    String resolved = FileResolver.DEFAULT_FILE_RESOLVER.resolve(fname, base, false).toString();
	    if (resolved.startsWith(getServletContext().getRealPath(""))) {
	      fname = resolved.substring(getServletContext().getRealPath("").length());
	      found = true;
	    }
	}
      } catch (FileNotFoundException e) {
      } catch (RuntimeException e) {
      }
    }


    if (! found) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND,
        "The requested file or directory (" + fname + ") was not found.");
      return;
    }

    if (request.getParameter("base") != null) {
      response.sendRedirect(viewer + "?file=" + fname.replace(File.separatorChar, '/'));
    }

    // Tomcat 3.3 seems to have problems getting real path for filenames that contain "..".
    fname = new File(fname).getCanonicalPath().toString();
    String comp = fname.replace(File.separatorChar, '/');

    if ( (!comp.endsWith(".lzx")) && (!comp.equals("/")) ) {
        comp += '/';
    }

    // In windows, canonical path will begin with 'C:', 'D:', etc. We want
    // to remove that.
    if (comp.length() > 1) {
        if (comp.charAt(1) == ':' && File.separatorChar == '\\') {
            comp = comp.substring(2);
            fname = fname.substring(2);
        }
    }

    // The test is so that fname="c:" falls through to an exception that
    // the app server reports readably.
    if (new File(fname).getParent() != null)
      base = new File(fname).getParent().replace(File.separatorChar, '/');
    String urlbase = new java.net.URL(requestURL, request.getContextPath() + base + "/").toString();

    boolean isOK = true;
    String tmp = (File.separatorChar == '\\' ? fname.replace('\\', '/') : fname);
    tmp = tmp.toLowerCase();
    if ( ( tmp.startsWith("/web-inf") || tmp.startsWith("/lps/utils/viewer") ) ) {
        isOK = false;
    }

    // =======================================================================
    // AK: Checking at the final stage whether the user is actually allowed
    //     view source here. Everything under the webapp is fair game now.
    //     I've tested this and even if the user tries to enter "../" in the
    //     querystring, it seems to base it down to the start of the webapp.
    // =======================================================================
    String absname = getServletContext().getRealPath(fname).toString();
    if ( ! absname.startsWith( webappHome ) || !isOK ) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission denied: " + comp );
        return;
    }
    boolean isBase = false;
    if ( absname.equals( webappHome ) )
        isBase = true;

    if (new File(absname).isDirectory()) {

      urlbase = new java.net.URL(requestURL, request.getContextPath() + fname + "/").toString();
      urlbase = urlbase.replace(File.separatorChar, '/');

      String dirname = fname.replace(File.separatorChar, '/');
      String title = "Directory Listing for " + dirname;
      out.println("<html><head><title>" + title + "</title></head><body>");
      out.println("<h1>" + title + "</h1>");
      String[] files = new File(absname + "/").list();
      out.println("<ul style='list-style: none; margin-left: 0; padding-left: 0;'>");

      for (int i = -1; i < files.length; i++) {

	// Don't display '..' if we're at a base
	if (i < 0 && (isBase))
	    continue;

        String item = i < 0 ? ".." : files[i];
        String url = "?file=" + dirname + "/" + item;
        String marker = "";
        if (new File(absname, item).isDirectory())
          marker = "/";
        else if (!item.endsWith(".lzx"))
          url = urlbase + item;
        out.println("<li><a href='" + url + "'>" + item + marker + "</a>");
      }
      out.println("</ul></body></html>");
      return;
    }

    java.io.PrintWriter writer = response.getWriter();
    try {
      TransformerFactory tFactory = new com.icl.saxon.TransformerFactoryImpl();
      Source xslSource = new javax.xml.transform.stream.StreamSource(new java.net.URL("file", "", new File(ctx, xform + ".xsl").toString()).openStream());
      Templates template = tFactory.newTemplates(xslSource);
      Source xmlSource = new javax.xml.transform.stream.StreamSource(new java.net.URL("file", "", absname).openStream());
      // Generate the transformer.
      Transformer transformer = template.newTransformer();
      transformer.setParameter("base", urlbase);
      transformer.setParameter("fname", new File(fname).getName());
      transformer.setParameter("url", viewer + /*"?mode=" + xform +*/ "?base=" + base + "&file=");
      // Perform the transformation, sending the output to the response.
      transformer.transform(xmlSource,
                           new javax.xml.transform.stream.StreamResult(writer));
    // If an Exception occurs, return the error to the client.
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      writer.println(e.getMessage());
      e.printStackTrace(writer);
    }
    writer.close();
%>
<!-- * X_LZ_COPYRIGHT_BEGIN ***************************************************
* Copyright 2001-2007 Laszlo Systems, Inc.  All Rights Reserved.              *
* Use is subject to license terms.                                            *
* X_LZ_COPYRIGHT_END ****************************************************** -->
