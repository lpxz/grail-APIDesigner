package org.apache.jsp.jsp.jsp2.simpletag;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class repeat_jsp extends org.apache.jasper.runtime.HttpJspBase
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
      out.write("<html>\r\n");
      out.write("  <head>\r\n");
      out.write("    <title>JSP 2.0 Examples - Repeat SimpleTag Handler</title>\r\n");
      out.write("  </head>\r\n");
      out.write("  <body>\r\n");
      out.write("    <h1>JSP 2.0 Examples - Repeat SimpleTag Handler</h1>\r\n");
      out.write("    <hr>\r\n");
      out.write("    <p>This tag handler accepts a \"num\" parameter and repeats the body of the\r\n");
      out.write("    tag \"num\" times.  It's a simple example, but the implementation of \r\n");
      out.write("    such a tag in JSP 2.0 is substantially simpler than the equivalent \r\n");
      out.write("    JSP 1.2-style classic tag handler.</p>\r\n");
      out.write("    <p>The body of the tag is encapsulated in a \"JSP Fragment\" and passed\r\n");
      out.write("    to the tag handler, which then executes it five times, inside a \r\n");
      out.write("    for loop.  The tag handler passes in the current invocation in a\r\n");
      out.write("    scoped variable called count, which can be accessed using the EL.</p>\r\n");
      out.write("    <br>\r\n");
      out.write("    <b><u>Result:</u></b><br>\r\n");
      out.write("    ");
      //  mytag:repeat
      jsp2.examples.simpletag.RepeatSimpleTag _jspx_th_mytag_005frepeat_005f0 = new jsp2.examples.simpletag.RepeatSimpleTag();
      org.apache.jasper.runtime.AnnotationHelper.postConstruct(_jsp_annotationprocessor, _jspx_th_mytag_005frepeat_005f0);
      _jspx_th_mytag_005frepeat_005f0.setJspContext(_jspx_page_context);
      // /jsp/jsp2/simpletag/repeat.jsp(35,4) name = num type = null reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
      _jspx_th_mytag_005frepeat_005f0.setNum(5);
      _jspx_th_mytag_005frepeat_005f0.setJspBody(new Helper( 0, _jspx_page_context, _jspx_th_mytag_005frepeat_005f0, null));
      _jspx_th_mytag_005frepeat_005f0.doTag();
      org.apache.jasper.runtime.AnnotationHelper.preDestroy(_jsp_annotationprocessor, _jspx_th_mytag_005frepeat_005f0);
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

  private class Helper
      extends org.apache.jasper.runtime.JspFragmentHelper
  {
    private javax.servlet.jsp.tagext.JspTag _jspx_parent;
    private int[] _jspx_push_body_count;

    public Helper( int discriminator, JspContext jspContext, javax.servlet.jsp.tagext.JspTag _jspx_parent, int[] _jspx_push_body_count ) {
      super( discriminator, jspContext, _jspx_parent );
      this._jspx_parent = _jspx_parent;
      this._jspx_push_body_count = _jspx_push_body_count;
    }
    public void invoke0( JspWriter out ) 
      throws Throwable
    {
      out.write("\r\n");
      out.write("      Invocation ");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${count}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
      out.write(" of 5<br>\r\n");
      out.write("    ");
      return;
    }
    public void invoke( java.io.Writer writer )
      throws JspException
    {
      JspWriter out = null;
      if( writer != null ) {
        out = this.jspContext.pushBody(writer);
      } else {
        out = this.jspContext.getOut();
      }
      try {
        this.jspContext.getELContext().putContext(JspContext.class,this.jspContext);
        switch( this.discriminator ) {
          case 0:
            invoke0( out );
            break;
        }
      }
      catch( Throwable e ) {
        if (e instanceof SkipPageException)
            throw (SkipPageException) e;
        throw new JspException( e );
      }
      finally {
        if( writer != null ) {
          this.jspContext.popBody();
        }
      }
    }
  }
}
