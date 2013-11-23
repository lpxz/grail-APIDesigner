package org.apache.jsp.jsp.jsp2.jspattribute;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class jspattribute_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  static {
    _jspx_dependants = new java.util.ArrayList(1);
    _jspx_dependants.add("/WEB-INF/jsp2/jsp2-example-taglib.tld");
  }

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
      out.write("    <title>JSP 2.0 Examples - jsp:attribute and jsp:body</title>\r\n");
      out.write("  </head>\r\n");
      out.write("  <body>\r\n");
      out.write("    <h1>JSP 2.0 Examples - jsp:attribute and jsp:body</h1>\r\n");
      out.write("    <hr>\r\n");
      out.write("    <p>The new &lt;jsp:attribute&gt; and &lt;jsp:body&gt; \r\n");
      out.write("    standard actions can be used to specify the value of any standard\r\n");
      out.write("    action or custom action attribute.</p>\r\n");
      out.write("    <p>This example uses the &lt;jsp:attribute&gt;\r\n");
      out.write("    standard action to use the output of a custom action invocation\r\n");
      out.write("    (one that simply outputs \"Hello, World!\") to set the value of a\r\n");
      out.write("    bean property.  This would normally require an intermediary\r\n");
      out.write("    step, such as using JSTL's &lt;c:set&gt; action.</p>\r\n");
      out.write("    <br>\r\n");
      out.write("    ");
      jsp2.examples.FooBean foo = null;
      synchronized (_jspx_page_context) {
        foo = (jsp2.examples.FooBean) _jspx_page_context.getAttribute("foo", PageContext.PAGE_SCOPE);
        if (foo == null){
          foo = new jsp2.examples.FooBean();
          _jspx_page_context.setAttribute("foo", foo, PageContext.PAGE_SCOPE);
          out.write("\r\n");
          out.write("      Bean created!  Setting foo.bar...<br>\r\n");
          out.write("      ");
          out = _jspx_page_context.pushBody();
          //  my:helloWorld
          jsp2.examples.simpletag.HelloWorldSimpleTag _jspx_th_my_005fhelloWorld_005f0 = new jsp2.examples.simpletag.HelloWorldSimpleTag();
          org.apache.jasper.runtime.AnnotationHelper.postConstruct(_jsp_annotationprocessor, _jspx_th_my_005fhelloWorld_005f0);
          _jspx_th_my_005fhelloWorld_005f0.setJspContext(_jspx_page_context);
          _jspx_th_my_005fhelloWorld_005f0.doTag();
          org.apache.jasper.runtime.AnnotationHelper.preDestroy(_jsp_annotationprocessor, _jspx_th_my_005fhelloWorld_005f0);
          String _jspx_temp0 = ((javax.servlet.jsp.tagext.BodyContent)out).getString();
          out = _jspx_page_context.popBody();
          org.apache.jasper.runtime.JspRuntimeLibrary.introspecthelper(_jspx_page_context.findAttribute("foo"), "bar", _jspx_temp0, null, null, false);
          out.write("\r\n");
          out.write("    ");
        }
      }
      out.write("\r\n");
      out.write("    <br>\r\n");
      out.write("    Result: ");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${foo.bar}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write("\r\n");
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
