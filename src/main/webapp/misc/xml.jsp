<%
response.setContentType("text/xml");
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", -1); //prevents caching in the proxy server
%><%@ page 
  language="java"
  contentType="text/xml; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  import="java.util.*" 
  errorPage="../misc/errorPage.jsp"
%><%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %><%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %><%@ taglib uri="/WEB-INF/common.tld" prefix="mm" %><mm:authCheck/><c:out value="${xml}" escapeXml="false" />