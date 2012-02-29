see<%@ page 
  errorPage="/misc/errorPage.jsp"
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  import="java.util.*,com.randomnoun.dmx.config.AppConfig,com.randomnoun.common.*,com.randomnoun.common.security.*"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%-- NB: authcheck is disabled for this page --%>
<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="resources.i18n.message" var="queryBundle" scope="request"/>
<fmt:setBundle basename="resources.i18n.general" var="generalBundle" scope="request"/>
<!-- $Id$ -->
<%!
  // method-scoped variables

  /** Converts an unsorted enumeration into a sorted iterator */ 
  Iterator sortedIterator(Enumeration e) {
    if (e==null) {
    return new TreeSet().iterator();
  }
  String key;
  TreeSet sortedSet = new TreeSet();
  while (e.hasMoreElements()) {
    key = (String) e.nextElement();
    sortedSet.add(key);
  }
  return sortedSet.iterator();
  }

  /** Returns a sorted iterator for an unsorted set */  
  Iterator sortedIterator(Set set) {
  TreeSet sortedSet = (set==null) ? new TreeSet() : new TreeSet(set);
  return sortedSet.iterator();
  }

  /** Returns a HTML rendering of an object that can be used for displaying
   *  attribute values 
   */
  String toString(String name, Object obj) {
    if (obj==null) {
      return "null";
    }
    if (obj instanceof Map) {
      return "<pre>" + Text.escapeHtml(Struct.structuredMapToString("object", (Map) obj)) + "</pre>";
    } else if (obj instanceof List) {
      return "<pre>" + Text.escapeHtml(Struct.structuredListToString("object", (List) obj)) + "</pre>";
    } else if (obj instanceof Exception) {
      return "<pre>" + Text.escapeHtml(ExceptionUtils.getStackTraceWithRevisions((Exception)obj, this.getClass().getClassLoader(), 0, "")) + "</pre>";
    } else if ("exception".equals(name)) {
      return "<pre>" + Text.escapeHtml(obj.toString()) + "</pre>";
    };
    return Text.escapeHtml(Text.getDisplayString(name, obj.toString()));  
  
  }
  
%>
<%
  // method-scoped variables
  
  AppConfig    appConfig = null;
  Map          ejbProperties = null;
  Properties   systemProps = null;
  Map          userConfig = null;
  User         user = null;
  String       name = null, value=null;
  Iterator     i = null, j=null;

  user = (User) session.getAttribute("user");
  appConfig = AppConfig.getAppConfig();
  ejbProperties = (Map) request.getAttribute("ejbConfigProperties");
%>
<style type="text/css">
<!--
  H2     { font-family: Arial; font-size: 12pt; font-style: normal; font-weight: bold; margin-bottom: 0; margin-top: 8pt; }
  H3     { font-family: Arial; font-size: 11pt; font-style: normal; font-weight: bold; margin-bottom: 0; margin-top: 8pt; }
  .error { font-family: Verdana; font-size: 8pt; font-style: normal; font-weight: bold; color: #FF0000; }
  .benchTD { width: 500px; overflow: hidden; }
  .key   { font-family: Verdana; font-size: 8pt; font-style: normal; font-weight: bold; }
  .value { font-family: Verdana; font-size: 8pt; font-style: normal; }
  .tabsheet {
    height: 500;
    width: 100%;
    overflow: scroll;
  }
-->
</style>
<h2>Quick links</h2>

<ul>
<li><a href="#requestParameters">Request parameters</a></li>
<li><a href="#requestAttributes">Request attributes</a></li>
<li><a href="#requestHeaders">Request headers</a></li>
<li><a href="#sessionAttributes">Session attributes</a></li>
<li><a href="#userConfigAttributes">UserConfig attributes</a></li>
<li><a href="#appConfigAttributes">AppConfig attributes</a></li>
<li><a href="#siteCachedData">Site cached data</a></li>
<li><a href="#systemProperties">System properties</a></li>
</ul>


<!------ Request Parameters -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="requestParameters">Request Parameters</a></h2></td></tr>
<%
  i=sortedIterator(request.getParameterNames());
  while (i.hasNext()) {
    name = (String) i.next();
%>
  <tr>
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString(name, request.getParameter(name)) %></span></td>
  </tr>
<%
  }
%> 
</table>

<!------ Request Attributes -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="requestAttributes">Request Attributes</a></h2></td></tr>
<%
  i=sortedIterator(request.getAttributeNames());
  while (i.hasNext()) {
    name = (String) i.next();
    if (name.equals("appConfig")) {
%>
  <tr>  
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><i>(see below)</i></span></td>
  </tr>
<%
    } else {
%>
  <tr>
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString(name, request.getAttribute(name)) %></span></td>
  </tr>
<%
    }
  }
%> 
</table>

<!------ Request Headers -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="requestHeaders">Request Headers</a></h2></td></tr>
<%
  i=sortedIterator(request.getHeaderNames());
  while (i.hasNext()) {
  name = (String) i.next();
    j=sortedIterator(request.getHeaders(name));
    while (j.hasNext()) {
      value = (String) j.next();
%>
  <tr>
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString(name, value) %></span></td>
  </tr>
<%
    }
  }
%> 
</table>

<!------ Session Attributes -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="sessionAttributes">Session Attributes</a></h2></td></tr>
  <tr>  
    <td width="30%" valign="top"><span class="key"><i>Session Identifier</i></span></td>
    <td width="70%"><span class="value"><%= session.getId() %></span></td>
  </tr>

<%
  i=sortedIterator(session.getAttributeNames());
  while (i.hasNext()) {
    name = (String) i.next();
    if (name.equals("appConfig") || name.equals("userConfig")) {
%>
  <tr>  
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><i>(see below)</i></span></td>
  </tr>
<%
    } else {
%>
  <tr>  
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString( name, session.getAttribute(name)) %> 
    </span></td>
  </tr>
<%
    }
  }
%>
</table>

<!------ UserConfig Attributes -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="userConfigAttributes">UserConfig Attributes</a></h2></td></tr>
<%
    userConfig = (Map) session.getAttribute("userConfig");
  i=sortedIterator(userConfig.keySet());
  while (i.hasNext()) {
    name = (String) i.next();
%>
  <tr>  
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString( name, userConfig.get(name)) %> 
    </span></td>
  </tr>
<%
  }
%>
</table>


<!------ Site Properties  -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="appConfigAttributes">AppConfig Properties (Customer <%= user.getCustomerId() %>)</a></h2></td></tr>
<%
  if (appConfig != null) {
  i=sortedIterator(appConfig.keySet());
  while (i.hasNext()) {
    name = (String) i.next();
    String appProp = toString(name, appConfig.get(name));
%>
  <tr>
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= appProp %></span></td>
  </tr>
<%  
  }
  }
%>
</table>

<!------ Site Cached Data -->
<%--
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="siteCachedData">Site cached data (Customer <%= user.getCustomerId() %>)</a></h2></td></tr>
<%
  if (appConfig.getCachedDataMap() != null) {
  i=sortedIterator(appConfig.getCachedDataMap().keySet());
  while (i.hasNext()) {
    name = (String) i.next();
%>
  <tr>
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString(name, appConfig.getCachedData(name)) %></span></td>
  </tr>
<%
    }
  }
%>
</table>
--%>

<!------ System Properties  -->
<table width="100%" border="0">
<tr><td colspan=2><h2><a name="systemProperties">System Properties</a></h2></td></tr>
<%
  systemProps = System.getProperties();
  if (systemProps != null) {
  i=sortedIterator(systemProps.keySet());
  while (i.hasNext()) {
    name = (String) i.next();
%>
  <tr>
    <td width="30%" valign="top"><span class="key"><%= name %></span></td>
    <td width="70%"><span class="value"><%= toString(name, systemProps.get(name)) %></span></td>
  </tr>
<%
    }
  }
%>
</table>
