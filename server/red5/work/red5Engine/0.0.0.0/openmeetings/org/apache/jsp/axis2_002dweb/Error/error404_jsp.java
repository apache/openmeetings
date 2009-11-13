package org.apache.jsp.axis2_002dweb.Error;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class error404_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      response.setContentType("text/html;charset=iso-8859-1");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
      out.write("<html>\n");
      out.write("  <head>\n");
      out.write("    ");
      org.apache.jasper.runtime.JspRuntimeLibrary.include(request, response, "../include/httpbase.jsp", out, false);
      out.write("\n");
      out.write("    <title>Axis2 :: Resource not found!</title>\n");
      out.write("    <link href=\"axis2-web/css/axis-style.css\" rel=\"stylesheet\" type=\"text/css\"/>\n");
      out.write("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"/>\n");
      out.write("  </head>\n");
      out.write("\n");
      out.write("  <body>\n");
      out.write("    <table width=\"100%\">\n");
      out.write("      <tr>\n");
      out.write("        <td align=\"left\"><img src=\"axis2-web/images/asf-logo.gif\" alt=\"\"/></td>\n");
      out.write("        <td align=\"right\"><img src=\"axis2-web/images/axis_l.jpg\" alt=\"\"/></td>\n");
      out.write("      </tr>\n");
      out.write("    </table>\n");
      out.write("    <table width=\"100%\">\n");
      out.write("      <tr>\n");
      out.write("        <td>\n");
      out.write("          <h1>Requested resource not found!</h1>\n");
      out.write("          <br/>\n");
      out.write("          <br/>\n");
      out.write("          <br/>\n");
      out.write("          <br/>\n");
      out.write("        </td>\n");
      out.write("      </tr>\n");
      out.write("      <tr><td align=\"center\"><a href=\"axis2-web/index.jsp\">home</a></td></tr>\n");
      out.write("    </table>\n");
      out.write("    <table width=\"100%\">\n");
      out.write("      <tr><td>\n");
      out.write("        <table width=\"950px\">\n");
      out.write("          <tr><td><hr size=\"1\" noshade=\"\"/></td></tr>\n");
      out.write("          <tr>\n");
      out.write("            <td align=\"center\">Copyright &#169; 1999-2006, The Apache Software Foundation<br/>Licensed under the <a\n");
      out.write("              href=\"http://www.apache.org/licenses/LICENSE-2.0\">Apache License, Version 2.0</a>.</td>\n");
      out.write("          </tr>\n");
      out.write("        </table>\n");
      out.write("      </td>\n");
      out.write("        <td>&nbsp;</td>\n");
      out.write("      </tr>\n");
      out.write("    </table>\n");
      out.write("  </body>\n");
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
