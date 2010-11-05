<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*"
%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
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

    <link rel="shortcut icon" href="images/favicon.png" />

    <!-- CSS -->
    <link href="stylesheets/stocktake-site.css" media="all" rel="stylesheet" type="text/css" />
    <link href="stylesheets/stocktake-print.css" media="print" rel="stylesheet" type="text/css" />
    <!--[if IE]><link href="/stylesheets/ie.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    <!--[if lt IE 8]><link href="/stylesheets/ie7.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    <!--[if lt IE 7]><link href="/stylesheets/ie6.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    
    <!-- JavaScript -->
    <script src="../mjs?js=prototype,scriptaculous,builder,effects,dragdrop,controls,slider,sound,rollover,facebox" type="text/javascript"></script>
    <script src="js/dmx.js" type="text/javascript"></script>
    
    <!--[if IE 6]>
    <script type="text/javascript" src="/javascripts/DD_belatedPNG_0.0.8a.js"></script>
    <script type="text/javascript">
      DD_belatedPNG.fix('#button_login, #button_start_an_auction_now,   #button_feedback,   #feedback_panel, #feedback_panel .head, #feedback_panel .body,  .btn_generic_clear, .btn_generic_clear span, .more, #process span, .button, .button span');
    </script>
    <![endif]-->
    <link rel="shortcut icon" href="images/favicon.png" />

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
<li><a href="manualController.html">Manual controller</a></li>
<li><a href="controller.html">The other controller</a></li>
</ul>

</body>
</html>
