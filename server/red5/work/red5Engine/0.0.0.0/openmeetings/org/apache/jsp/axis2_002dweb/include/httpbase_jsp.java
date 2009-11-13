package org.apache.jsp.axis2_002dweb.include;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import org.apache.axis2.Constants;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.transport.http.AxisServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public final class httpbase_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {


  private String frontendHostUrl;

  public void jspInit() {
    ServletContext context = this.getServletConfig().getServletContext();
    ConfigurationContext configctx = (ConfigurationContext) context.getAttribute(AxisServlet.CONFIGURATION_CONTEXT);
    if (configctx != null){
        Parameter parameter = configctx.getAxisConfiguration().getParameter(Constants.HTTP_FRONTEND_HOST_URL);
        if (parameter != null) {
          frontendHostUrl = (String) parameter.getValue();
        }
    }
  }

  public String calculateHttpBase(HttpServletRequest aRequest) {
    StringBuffer stringBuffer = new StringBuffer();
    if (frontendHostUrl != null) {
      stringBuffer.append(frontendHostUrl);
    } else {
      String scheme = aRequest.getScheme();
      stringBuffer.append(scheme);
      stringBuffer.append("://");
      stringBuffer.append(aRequest.getServerName());
      if (("http".equalsIgnoreCase(scheme) && aRequest.getServerPort() != 80) || "https".equalsIgnoreCase(scheme) && aRequest.getServerPort() != 443)
      {
        stringBuffer.append(":");
        stringBuffer.append(aRequest.getServerPort());
      }
      // I think i saw web containers return null for root web context
      if (aRequest.getContextPath() != null) {
        stringBuffer.append(aRequest.getContextPath());
      }
    }
    // append / char if needed
    if (stringBuffer.charAt(stringBuffer.length() - 1) != '/') {
      stringBuffer.append("/");
    }
    String curentUrl = stringBuffer.toString();
    aRequest.setAttribute("frontendHostUrl", curentUrl);
    return curentUrl;
  }

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

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<base href=\"");
      out.print( calculateHttpBase(request));
      out.write('"');
      out.write('/');
      out.write('>');
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
