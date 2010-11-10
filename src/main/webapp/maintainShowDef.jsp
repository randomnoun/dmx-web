<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,com.randomnoun.dmx.*"
%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="resources.i18n.general" var="generalBundle" />
<% 
   AppConfig appConfig = AppConfig.getAppConfig();
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

<style>
BODY { font-size: 8pt; font-family: Arial; }
.showDef { font-size: 8pt; font-family: Arial;}
.showDef TD { font-size: 8pt; font-family: Arial;}
.showDef INPUT { font-size: 8pt; }
.showDef TEXTAREA { font-family: Lucida Console, Courier New; font-size: 8pt; }
.label { width: 25px; height: 16px; text-align: right; background-color: lightblue; padding-top: 3px; margin-left: 3px; margin-bottom: 1px;}
</style>
<script>
function getShowDef() {
	var showDefIdEl = document.getElementById("showDefId");
	var showDefId = showDefIdEl.value;
	if (showDefId != '') {
	    //alert("Retrieving showDef " + showDefId);
	    document.location = "maintainShowDef.html?action=getShowDef&showDefId=" + showDefId;
	}
}
function newShowDef() {
    document.location = "maintainShowDef.html?action=newShowDef";
}


</script>
</head>

<html>
<body>
<h2>DMX Web</h2>
<h3>Show definitions</h3>
<c:if test="${message!=null}">
<b><c:out value="${message}" /></b><br/>
</c:if>
<jsp:include page="misc/errorHeader.jsp" />
<table>
<tr><td>Select show definition:</td>
    <td><r:select name="showDefId" value="${showDefId}" data="${showDefs}" 
  displayColumn="name" valueColumn="id"  /></td>
    <td><input type="button" name="getShowDef" value="Get show definition" onclick="getShowDef()" /></td>
</tr><tr>    
    <td></td>
    <td></td>
    <td><input type="button" name="createShowDef" value="Create new show definition" onclick="newShowDef()" /></td>
</tr>
<p>
<c:if test="${showDef!=null}" >
<form action="maintainShowDef.html" method="post">
<%-- <r:setForm bundle="${generalBundle}" htmlFormat="${0}" labelFormat="${0}" data="${showDef}" /> --%>
<input type="hidden" name="showDef.id" value="${showDef.id}" />
<table class="showDef">
<tr><td>Name:</td>
    <td><r:input type="text" name="showDef.name" value="${showDef.name}"/></td></tr>
<tr><td valign="top">Script:</td>
    <td><r:input type="textarea" name="showDef.script" value="${showDef.script}" rows="25" cols="100"/></td></tr>
<tr><td></td>
    <td>
    <c:if test="${showDef.id==-1}">
    <input type="submit" name="updateShowDef" value="Create" />
    </c:if>
    <c:if test="${showDef.id!=-1}">
    <input type="submit" name="updateShowDef" value="Update" />
    </c:if>
      
    </td>
</tr>
</table>
</form>
</c:if>
<hr/>
<%--
<jsp:include page="misc/debugAttributes.jsp" />
 --%>

</body>
</html>
