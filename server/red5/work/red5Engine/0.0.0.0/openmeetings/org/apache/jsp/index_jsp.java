package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write("\n");
      out.write("<meta http-equiv=\"cache-control\" content=\"no-cache\">\n");
      out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
      out.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\">\n");
      out.write("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"favicon.ico\">\n");
      out.write("<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0;\">\n");
      out.write("<title>OpenLaszlo Application</title>\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("          // If loaded bare into a browser, set the browser size to the canvas size\n");
      out.write("          if (window === top) {\n");
      out.write("            (function (width, height) {\n");
      out.write("              // Cf. http://www.quirksmode.org/viewport/compatibility.html\n");
      out.write("              if (window.innerHeight) {\n");
      out.write("                // Sadly, innerHeight/Width is not r/w on some browsers, and resizeTo is for outerHeight/Width\n");
      out.write("                window.resizeTo(width ? (width + window.outerWidth - window.innerWidth) : window.outerWidth,\n");
      out.write("                                height ? (height + window.outerHeight - window.innerHeight) : window.outerHeight);\n");
      out.write("              } else if (document.documentElement && document.documentElement.clientHeight) {\n");
      out.write("                if (width) {\n");
      out.write("                  document.documentElement.clientWidth = width;\n");
      out.write("                }\n");
      out.write("                if (height) {\n");
      out.write("                  document.documentElement.clientHeight = height;\n");
      out.write("                }\n");
      out.write("              } else {\n");
      out.write("                if (width) {\n");
      out.write("                  document.body.clientWidth = width;\n");
      out.write("                }\n");
      out.write("                if (height) {\n");
      out.write("                  document.body.clientHeight = height;\n");
      out.write("                }\n");
      out.write("              }\n");
      out.write("            })(null, null);\n");
      out.write("          }\n");
      out.write("        </script>\n");
      out.write("<script type=\"text/javascript\"\n");
      out.write("\tsrc=\"embed-compressed.js\"></script>\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("\n");
      out.write("function getBrowserInfo() {\n");
      out.write("    //alert(navigator.userAgent);\n");
      out.write("    return navigator.userAgent;\n");
      out.write("}\n");
      out.write("\n");
      out.write("</script>\n");
      out.write("<style type=\"text/css\">\n");
      out.write("html,body { /* http://www.quirksmode.org/css/100percheight.html */\n");
      out.write("\theight: 100%;\n");
      out.write("\t/* prevent scrollbars */\n");
      out.write("\tmargin: 0;\n");
      out.write("\tpadding: 0;\n");
      out.write("\tborder: 0 none;\n");
      out.write("\toverflow: hidden;\n");
      out.write("}\n");
      out.write("\n");
      out.write("body {\n");
      out.write("\tbackground-color: #ffffff;\n");
      out.write("}\n");
      out.write("\n");
      out.write("img {\n");
      out.write("\tborder: 0 none;\n");
      out.write("}\n");
      out.write("</style>\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("<script type=\"text/javascript\">\n");
      out.write("    lz.embed.swf({url: 'main.swf8.swf?lzproxied=solo', allowfullscreen: 'true', bgcolor: '#ffffff', width: '100%', height: '100%', id: 'lzapp', accessible: 'false'});\n");
      out.write("\n");
      out.write("    lz.embed.lzapp.onloadstatus = function loadstatus(p) {\n");
      out.write("      // called with a percentage (0-100) indicating load progress\n");
      out.write("    }\n");
      out.write("\n");
      out.write("    lz.embed.lzapp.onload = function loaded() {\n");
      out.write("      // called when this application is done loading\n");
      out.write("    }\n");
      out.write("</script>\n");
      out.write("<div style=\"width: 100%; height: 100%;\" id=\"lzappContainer\"><embed\n");
      out.write("\tsrc=\"maindebug.lzx-Dateien/maindebug.lzx\" quality=\"high\"\n");
      out.write("\tbgcolor=\"#ffffff\" wmode=\"window\" allowfullscreen=\"false\" id=\"lzapp\"\n");
      out.write("\tname=\"lzapp\"\n");
      out.write("\tflashvars=\"lzt=swf&amp;lzproxied=solo&amp;lzr=swf8&amp;bgcolor=%23ffffff&amp;width=100%25&amp;height=100%25&amp;__lzurl=maindebug.lzx%3Flzt%3Dswf%26lzproxied%3Dsolo%26lzr%3Dswf8&amp;__lzminimumversion=8&amp;id=lzapp\"\n");
      out.write("\tswliveconnect=\"true\" allowscriptaccess=\"sameDomain\"\n");
      out.write("\ttype=\"application/x-shockwave-flash\"\n");
      out.write("\tpluginspage=\"http://www.macromedia.com/go/getflashplayer\"\n");
      out.write("\talign=\"middle\" height=\"100%\" width=\"100%\"></div>\n");
      out.write("<noscript>Please enable JavaScript in order to use this\n");
      out.write("application.</noscript>\n");
      out.write("</body>\n");
      out.write("</html>");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
