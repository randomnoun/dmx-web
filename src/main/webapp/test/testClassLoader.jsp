<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="../misc/errorPage.jsp"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common" prefix="mm" %>
<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="resources.i18n.test" var="testBundle" scope="request"/>
<mm:authCheck/>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- $Id$ -->
<head>
<title>ClassLoader</title>
<link href="../css/Master.css" rel="stylesheet" type="text/css" />
<style type="text/css">
  H2     { font-family: Arial; font-size: 12pt; font-style: normal; font-weight: bold; margin-bottom: 0; margin-top: 8pt; }
  H3     { font-family: Arial; font-size: 11pt; font-style: normal; font-weight: bold; margin-bottom: 0; margin-top: 8pt; }
  PRE {
    margin-left: 0px;
    margin-right: 20px;
    padding: 5px;
    width: 100%;
    font-size: 9pt;
    font-family: "Lucida Console";
  }
  .stackTrace {
    background-color: #eeeeee;
    border: dotted 1px #777777;
  }  

</style>
</head>
<body>
<jsp:include page="../misc/errorHeader.jsp"/>
<form action="../test/testClassLoader.do">
Enter class name: <mm:input name="classname" value="${classname}" type="text" size="100" />
<input type="submit" name="submit" value="Retrieve classloader details" /> 
</form>
<h3>Classloader for specified class</h3>
<pre class="stackTrace">
  <c:out value="${classLoader}" escapeXml="false" />
</pre>

<h3>System classpath</h3>
<pre class="stackTrace">
  <c:out value="${systemClasspath}" escapeXml="false" />
</pre>

<h3>Webapp classloader hierarchy</h3>
<pre class="stackTrace">
<c:out value="${webappClassLoader}" escapeXml="false" />
</pre>

<%--
<h3>EJB classloader hierarchy</h3>
<pre class="stackTrace">
<c:out value="${ejbClassLoader}" escapeXml="false" />
</pre>
--%>

</body>
</html>
