package org.apache.jsp.jsp.jsp2.el;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class implicit_002dobjects_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

static private org.apache.jasper.runtime.ProtectedFunctionMapper _jspx_fnmap_0;

static {
  _jspx_fnmap_0= org.apache.jasper.runtime.ProtectedFunctionMapper.getMapForFunction("fn:escapeXml", org.apache.taglibs.standard.functions.Functions.class, "escapeXml", new Class[] {java.lang.String.class});
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

      out.write("<!--\r\n");
      out.write(" Licensed to the Apache Software Foundation (ASF) under one or more\r\n");
      out.write("  contributor license agreements.  See the NOTICE file distributed with\r\n");
      out.write("  this work for additional information regarding copyright ownership.\r\n");
      out.write("  The ASF licenses this file to You under the Apache License, Version 2.0\r\n");
      out.write("  (the \"License\"); you may not use this file except in compliance with\r\n");
      out.write("  the License.  You may obtain a copy of the License at\r\n");
      out.write("\r\n");
      out.write("      http://www.apache.org/licenses/LICENSE-2.0\r\n");
      out.write("\r\n");
      out.write("  Unless required by applicable law or agreed to in writing, software\r\n");
      out.write("  distributed under the License is distributed on an \"AS IS\" BASIS,\r\n");
      out.write("  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\r\n");
      out.write("  See the License for the specific language governing permissions and\r\n");
      out.write("  limitations under the License.\r\n");
      out.write("-->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("  <head>\r\n");
      out.write("    <title>JSP 2.0 Expression Language - Implicit Objects</title>\r\n");
      out.write("  </head>\r\n");
      out.write("  <body>\r\n");
      out.write("    <h1>JSP 2.0 Expression Language - Implicit Objects</h1>\r\n");
      out.write("    <hr>\r\n");
      out.write("    This example illustrates some of the implicit objects available \r\n");
      out.write("    in the Expression Lanaguage.  The following implicit objects are \r\n");
      out.write("    available (not all illustrated here):\r\n");
      out.write("    <ul>\r\n");
      out.write("      <li>pageContext - the PageContext object</li>\r\n");
      out.write("      <li>pageScope - a Map that maps page-scoped attribute names to \r\n");
      out.write("          their values</li>\r\n");
      out.write("      <li>requestScope - a Map that maps request-scoped attribute names \r\n");
      out.write("          to their values</li>\r\n");
      out.write("      <li>sessionScope - a Map that maps session-scoped attribute names \r\n");
      out.write("          to their values</li>\r\n");
      out.write("      <li>applicationScope - a Map that maps application-scoped attribute \r\n");
      out.write("          names to their values</li>\r\n");
      out.write("      <li>param - a Map that maps parameter names to a single String \r\n");
      out.write("          parameter value</li>\r\n");
      out.write("      <li>paramValues - a Map that maps parameter names to a String[] of \r\n");
      out.write("          all values for that parameter</li>\r\n");
      out.write("      <li>header - a Map that maps header names to a single String \r\n");
      out.write("          header value</li>\r\n");
      out.write("      <li>headerValues - a Map that maps header names to a String[] of \r\n");
      out.write("          all values for that header</li>\r\n");
      out.write("      <li>initParam - a Map that maps context initialization parameter \r\n");
      out.write("          names to their String parameter value</li>\r\n");
      out.write("      <li>cookie - a Map that maps cookie names to a single Cookie object.</li>\r\n");
      out.write("    </ul>\r\n");
      out.write("\r\n");
      out.write("    <blockquote>\r\n");
      out.write("      <u><b>Change Parameter</b></u>\r\n");
      out.write("      <form action=\"implicit-objects.jsp\" method=\"GET\">\r\n");
      out.write("\t  foo = <input type=\"text\" name=\"foo\" value=\"");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${fn:escapeXml(param[\"foo\"])}", java.lang.String.class, (PageContext)_jspx_page_context, _jspx_fnmap_0, false));
      out.write("\">\r\n");
      out.write("          <input type=\"submit\">\r\n");
      out.write("      </form>\r\n");
      out.write("      <br>\r\n");
      out.write("      <code>\r\n");
      out.write("        <table border=\"1\">\r\n");
      out.write("          <thead>\r\n");
      out.write("\t    <td><b>EL Expression</b></td>\r\n");
      out.write("\t    <td><b>Result</b></td>\r\n");
      out.write("\t  </thead>\r\n");
      out.write("\t  <tr>\r\n");
      out.write("\t    <td>${param.foo}</td>\r\n");
      out.write("\t    <td>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${fn:escapeXml(param[\"foo\"])}", java.lang.String.class, (PageContext)_jspx_page_context, _jspx_fnmap_0, false));
      out.write("&nbsp;</td>\r\n");
      out.write("\t  </tr>\r\n");
      out.write("\t  <tr>\r\n");
      out.write("\t    <td>${param[\"foo\"]}</td>\r\n");
      out.write("\t    <td>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${fn:escapeXml(param[\"foo\"])}", java.lang.String.class, (PageContext)_jspx_page_context, _jspx_fnmap_0, false));
      out.write("&nbsp;</td>\r\n");
      out.write("\t  </tr>\r\n");
      out.write("\t  <tr>\r\n");
      out.write("\t    <td>${header[\"host\"]}</td>\r\n");
      out.write("\t    <td>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${fn:escapeXml(header[\"host\"])}", java.lang.String.class, (PageContext)_jspx_page_context, _jspx_fnmap_0, false));
      out.write("&nbsp;</td>\r\n");
      out.write("\t  </tr>\r\n");
      out.write("\t  <tr>\r\n");
      out.write("\t    <td>${header[\"accept\"]}</td>\r\n");
      out.write("\t    <td>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${fn:escapeXml(header[\"accept\"])}", java.lang.String.class, (PageContext)_jspx_page_context, _jspx_fnmap_0, false));
      out.write("&nbsp;</td>\r\n");
      out.write("\t  </tr>\r\n");
      out.write("\t  <tr>\r\n");
      out.write("\t    <td>${header[\"user-agent\"]}</td>\r\n");
      out.write("\t    <td>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${fn:escapeXml(header[\"user-agent\"])}", java.lang.String.class, (PageContext)_jspx_page_context, _jspx_fnmap_0, false));
      out.write("&nbsp;</td>\r\n");
      out.write("\t  </tr>\r\n");
      out.write("\t</table>\r\n");
      out.write("      </code>\r\n");
      out.write("    </blockquote>\r\n");
      out.write("  </body>\r\n");
      out.write("</html>\r\n");
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
