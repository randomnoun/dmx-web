<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common-public" prefix="r" %>
<% 
   AppConfig appConfig = AppConfig.getAppConfig();
   List dmx = (List) request.getAttribute("dmx");
   String startCode = (String) request.getAttribute("startCode");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" dir="ltr">
<head>
    
    <!-- Meta Tags -->
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=utf-8" />
    <meta name="robots" content="index,follow" />
    <meta name="publisher" content="randomnoun" />
    <meta name="copyright" content="&copy; Copyright 2010, randomnoun" />
    <meta name="description" content="Albion comedy DMX web application" />
    <meta name="revisit-after" content="2 days" />
    <meta name="keywords" content="nothing-in-particular" />
    
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> DMX</title>

    <link rel="shortcut icon" href="image/favicon.png" />

    <!-- CSS -->
    <!-- JavaScript -->
    

<style>
#controller { font-size: 8pt; font-family: Arial;}
#controller TD { font-size: 8pt; font-family: Arial;}
#controller INPUT { font-size: 8pt; }
#config { font-size: 8pt; font-family: Arial;}
#config TD { font-size: 8pt; font-family: Arial;}
.label { width: 25px; height: 16px; text-align: right; background-color: lightblue; padding-top: 3px; margin-left: 3px; margin-bottom: 1px;}
</style>

</head>

<html>
<body>
<h2>DMX Web</h2>
Things to try:

<ul>
<li><a href="maintainFixtureDef.html">Maintain fixture types</a></li>
<li><a href="maintainFixture.html">Maintain fixtures</a></li>
<li><a href="maintainShowDef.html">Maintain show definitions</a></li>
<li><a href="maintainShow.html">Maintain shows</a></li>
<li><a href="controller.html">Controller</a></li>
<li><a href="fancyController.html">Fancy Controller</a></li>
<li><strike><a href="manualController.html">Manual controller</a></strike> (don't use this)</li>
<li><a href="debug.html">Debug page</a></li>
<li><a href="streaming.html">Streaming video</a></li>
</ul>
<jsp:include page="/misc/analytics.jsp" />
</body>
</html>
