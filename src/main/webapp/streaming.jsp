<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,java.text.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,com.randomnoun.dmx.*,com.randomnoun.dmx.show.Show"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<% 
   AppConfig appConfig = AppConfig.getAppConfig();
   Controller controller = (Controller) request.getAttribute("controller");
   Universe universe = (Universe) request.getAttribute("universe");  
   String streamingUrl = appConfig.getProperty("dev.vlc.streamingUrl");
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
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> DMX Video Stream</title>

    <link rel="shortcut icon" href="image/favicon.png" />
    <link rel="stylesheet" href="css/farbtastic.css" type="text/css" />

    <!-- JavaScript -->
    <script type="text/javascript" src="js/flowplayer-3.2.4.min.js"></script>
</head>
<body>
    <div id="page">
<%
    if (streamingUrl==null || streamingUrl.equals("")) {
%>
    Video streaming not configured
<%
    } else {
%>
        <!-- this A tag is where your Flowplayer will be placed. it can be anywhere -->
        <!--  http://e1h13.simplecdn.net/flowplayer/flowplayer.flv -->
        <a  
             href="<%= streamingUrl %>"  
             style="display:block;width:520px;height:330px"  
             id="player"> 
        </a> 
    
        <!-- this will install flowplayer inside previous A- tag. -->

        <script>
            flowplayer("player", "swf/flowplayer-3.2.5.swf");
        </script>
<%
    }
%>            
    </div>
<jsp:include page="/misc/analytics.jsp" />    
</body></html>