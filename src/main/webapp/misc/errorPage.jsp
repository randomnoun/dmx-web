<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  import="java.util.*,java.text.*,com.randomnoun.common.ExceptionUtils"
%>
<%
	response.setStatus(500);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- $Id$ -->
<head>
<title>Error</title>
</head>
<body style="font-family: Arial;">
<%
	/*
  for (Enumeration e = request.getAttributeNames(); e.hasMoreElements(); ) {
	  String attributeName = (String) e.nextElement();
	  System.out.println(attributeName + ": " + request.getAttribute(attributeName));
  }
  */

  Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
  String message = (String) request.getAttribute("javax.servlet.error.message");
  if (exception==null) {
    exception = (Throwable) request.getAttribute("javax.servlet.jsp.jspException");
    if (exception!=null) {
      message = exception.getMessage();
    }
  }
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
  String timeString = sdf.format(new Date());
  /* if (exception instanceof RemoteException) {
    exception = ((RemoteException) exception).detail;
  } */
%>
<table style="border: red solid 1px; background-color: #FFFEBB; margin:10px; font-size: 8pt">
<tr>
<td valign="top"><img align="left" src="image/information.gif" /></td>
<td valign="top">
  <b>An error has occurred in the application</b>
  <br/><br/>
  <b>Message:</b> <%=message%>
  <br/><br/>
  <b>Request URI:</b> <%=request.getAttribute("javax.servlet.error.request_uri")%> 
  <br/><b>Request Time:</b> <%=timeString%>
  <%-- should only display this in development mode, otherwise log or email it --%>
  <br/><br/>
  <%
  	if (exception!=null) {
  %>
  <b>Stack trace:</b> <a href="javascript:void();" onclick="x = document.getElementById('stackTrace').style; x.display = (x.display=='block' ? 'none' : 'block');">Click to display</a>
  <div id="stackTrace" style="display:none"><pre><%=ExceptionUtils.getStackTraceWithRevisions(exception, this.getClass().getClassLoader(),
    ExceptionUtils.HIGHLIGHT_HTML, "com.randomnoun.,org.apache.jsp.")%></pre></div>
  <% } %>
</td>
</tr>
</table>  
</body>
</html>
<%
    out.flush();
    /*if (request.getAttribute("sentAlarm")==null) {
      	ExceptionHandler.sendApplicationAlarm(request, exception, "JSP");
    }*/
%>
