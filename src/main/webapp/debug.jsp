<?xml version="1.0" encoding="ISO-8859-1" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<%@ page 
  errorPage="../misc/errorPage.jsp"
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  import="java.util.*,com.randomnoun.common.security.*,com.randomnoun.dmx.config.AppConfig"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common" prefix="r" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<!-- $Id$ -->
<%
  // method-scoped variables
  
  AppConfig    appConfig = null;
  Properties   systemProps = null;
  Map          userConfig = null;
  User         user = null;
  Enumeration  keys = null;	
  String       name = null, value=null;
  Iterator     i = null, j=null;

  user = (User) session.getAttribute("user");
  appConfig = AppConfig.getAppConfig();

%>
<head>
<title>DMX debug</title>
<link href="css/tabs.css" rel="stylesheet" type="text/css" />
<style>
<!--
.userServiceIcon {
  position: relative; 
  top: 2px;
}
  BODY   { font-family: Arial; font-size: 10pt; }
  H2     { font-family: Arial; font-size: 12pt; font-style: normal; font-weight: bold; margin-bottom: 0; margin-top: 8pt; }
  H3     { font-family: Arial; font-size: 11pt; font-style: normal; font-weight: bold; margin-bottom: 0; margin-top: 8pt; }
  TH     { text-align: left; }
  .error { font-family: Verdana; font-size: 8pt; font-style: normal; font-weight: bold; color: #FF0000; }
  .benchTD { width: 500px; overflow: hidden; }
  .key   { font-family: Verdana; font-size: 8pt; font-style: normal; font-weight: bold; }
  .value { font-family: Verdana; font-size: 8pt; font-style: normal; }
  .tabsheet {
    height: 500;
    width: 100%;
    overflow: scroll;
  }
  .code {
    margin: 5px 5px 5px 20px;  /* top right bottom left */
    padding: 5px;
    width: 100%;
    background-color: #EEEEEE;
    border: dotted 1px black;
    font-size: 9pt;
    font-family: "Lucida Console";
  }  
  .xmlOutput {
    margin: 5px 5px 5px 20px;  /* top right bottom left */
    padding: 5px;
    width: 100%;
    background-color: #EEEEFF;
    border: dotted 1px black;
    font-size: 8pt;
    font-family: "Lucida Console";
  }
  .red {
    color: red;
  }
  .mqMessage {
    width: 100%;
    margin: 5px;
    padding: 5px;
    background-color: #ffffe1;
    border: 1px dotted #808000;
    font-family: Verdana;
    font-size: 11px;
    color: black;  
  }
-->
</style>
	<%-- <script src="mjs?js=prototype" type="text/javascript"></script>  --%>
	<script src="mjs?js=jquery-3.6.3.min"></script>
</head>
<body>
<div style="height:5px;"></div>
<r:tabgroup currentTabId="${debugTab}" layout="round" hideTabsheet="true">
  <r:tab id="attributes" label="Attributes" href="debug.html?debugTab=attributes" >
  <jsp:include page="misc/errorHeader.jsp" />
  <jsp:include page="misc/debugAttributes.jsp" />
</r:tab>



  <r:tab id="jmx" label="JMX" href="debug.html?debugTab=jmx" >
  <h1>JMX Browser</h1>
  <div id="errorDiv"></div>
  <ul id="topNode"></ul>
  <script>
  var getTopNode = true;

  if (getTopNode) {
    getNode("topNode");
  }
  
  function getNode(parentId) {
    var ulNode = document.getElementById(parentId);
    if (ulNode==null) {
      alert("Cannot find node '" + parentId + "'");
    }
    if (ulNode.childNodes.length>0) {
      // already populated; hide this instead
      ulNode.innerHTML = "";
    } else {
    	
        jQuery.ajax({
            method: 'GET',
            url: "debug.html?debugTab=jmx&action=getNodes&path=" + escape(parentId),
            dataType: 'xml'
        }).done(function (data) {
        	var nodesDoc = data; 
            if (nodesDoc!=null) {
                var nodes = nodesDoc.documentElement.childNodes;
                for (var i = 0; i < nodes.length; i++) {
                  var node = nodes.item(i);
                  var name = node.getAttribute("name");
                  newLi = document.createElement("LI");
                  newLi.innerHTML = "<a href=\"javascript:getNode('" + parentId + ";" + name + "');\">"
                    + name + "</a><ul id=\"" + parentId + ";" + name + "\"></ul>";
                  ulNode.appendChild(newLi);
                }
              };
                        // setRhsMessageHTML(data.message);
            // if (completedFunction) { completedFunction(data); }
            
            
        });
    	
    }
   
    	/*
      new Ajax.Request("debug.html?debugTab=jmx&action=getNodes&path=" + escape(parentId), {
          method:'get', // evalJSON:true,
          onSuccess: function(transport) {
                var nodesDoc = transport.responseXML;
                if (nodesDoc!=null) {
                    var nodes = nodesDoc.documentElement.childNodes;
                    for (var i = 0; i < nodes.length; i++) {
                      var node = nodes.item(i);
                      var name = node.getAttribute("name");
                      newLi = document.createElement("LI");
                      newLi.innerHTML = "<a href=\"javascript:getNode('" + parentId + ";" + name + "');\">"
                        + name + "</a><ul id=\"" + parentId + ";" + name + "\"></ul>";
                      ulNode.appendChild(newLi);
                    }
                  }
            },
            onFailure: function(transport) {
            	$("errorDiv").update(transport.responseText);
            } }); 	
    } */
    
  }
  </script>
  <hr/>
  <c:out value="${result}"/>
  </pre>
  </r:tab>

  <r:tab id="jndi" label="JNDI" href="debug.html?debugTab=jndi" >
  <h1>JNDI Browser</h1>
  <div id="errorDiv"></div>
  <ul id="topNode" ></ul>
  <script src="../js/xmlhttp.js"></script>
  <script>
  var getTopNode = true;

  if (getTopNode) {
    getNode("topNode");
  }
  
  function getNode(parentId) {
    var ulNode = document.getElementById(parentId);
    if (ulNode==null) {
      alert("Cannot find node '" + parentId + "'");
    }
    if (ulNode.childNodes.length>0) {
      // already populated; hide this instead
      ulNode.innerHTML = "";
    } else {
    	/*
      var nodesDoc = getRemoteXml("debug.html?debugTab=jndi&action=getNodes&path=" + escape(parentId), "errorDiv", "_new");
    	*/
    	
        jQuery.ajax({
            method: 'GET',
            url: "debug.html?debugTab=jndi&action=getNodes&path=" + escape(parentId),
            dataType: 'xml'
        }).done(function (data) {
        	var nodesDoc = data;
        	
      
        	if (nodesDoc!=null) {
                var nodes = nodesDoc.documentElement.childNodes;
                for (var i = 0; i < nodes.length; i++) {
                  var node = nodes.item(i);
                  var name = node.getAttribute("name");
                  var className = node.getAttribute("className");
                  newLi = document.createElement("LI");
                  newLi.innerHTML = "<a href=\"javascript:getNode('" + parentId + "/" + name + "');\">"
                    + name + " (" + className + ")</a><ul id=\"" + parentId + "/" + name + "\"></ul>";
                  ulNode.appendChild(newLi);
                }
              }
        	
        });
    	
    	
      
    }
  }
  </script>
  <hr/>
  
  </r:tab>
  

  <r:tab id="logging" label="Logging" href="debug.html?debugTab=logging" >
  <jsp:include page="misc/errorHeader.jsp" />
  <form action="debug.html" method="post">
  <input type="hidden" name="debugTab" value="logging" />
  <input type="hidden" name="action" value="setLevel" />

  <h2>Appenders</h2>
  <table class="helptext" style="vertical-align: top;" >
  <col width="100" />
  <col width="100" />
  <col width="100" />
  <tr>
    <th>name</th>
    <th>type</th>
    <th>threshold</th>
  </tr>
  <c:forEach var="appender" items="${appenders}" >
  <tr>
    <td><c:out value="${appender.name}" /></td>
    <td><c:out value="${appender.type}" /></td>
    <td><c:out value="${appender.threshold}" /></td>
  </tr>  
  </c:forEach>
  </table>

  <h2>Loggers</h2>
  <table class="helptext" style="vertical-align: top;" >
  <col width="100" />
  <col width="100" />
  <col width="100" />
  <tr>
    <th>name</th>
    <th>level</th>
    <th>(effective)</th>
    <th>additive</th>
    <th>parent</th>
    <th>appenders</th>
  </tr>
  <tr>
    <td><r:input type="text" name="className" value="${className}" size="80" maxlength="200"/></td>
    <td><r:select name="level" data="${levels}" value="${level}" firstOption="(please select...)"/></td>
    <td><r:input type="submit" name="setLevel" value="Set level"/></td>
    <td></td>
    <td></td>
    <td></td>
  </tr>
  <c:forEach var="logger" items="${loggers}" >
  <tr>
    <td><a href="javascript:setClassName('<c:out value="${logger.name}" />')"><c:out value="${logger.name}" /></a></td>
    <td><c:out value="${logger.level}" /></td>
    <td><c:out value="${logger.effectiveLevel}" /></td>
    <td><c:out value="${logger.additive}" /></td>
    <td><a href="javascript:setClassName('<c:out value="${logger.parent}" />')"><c:out value="${logger.parent}" /></a></td>
    <td><c:out value="${logger.appenders}" /></td>
  </tr>  
  </c:forEach>
  </table>
  </form>

  </r:tab>

  <r:tab id="eventLog" label="Event Log" href="debug.html?debugTab=eventLog" >
<form action="debug.html" method="post">
  <div class="helptext">
  <input type="hidden" name="debugTab" value="eventLog" />
  <input type="submit" name="clearEventLog" value="Clear event log" />
  <b>Event Log</b>
  <input type="hidden" name="action" value="setLevel" />
  </div>
</form>  
  <table class="helptext" style="vertical-align: top;" >
  <col width="120" />
  <col width="150" />
  <col width="100" />
  <col width="100%" />
  <col width="200" />
  <c:forEach var="event" items="${events}" varStatus="status">
    <tr>
    <td nowrap valign="top"><fmt:formatDate value="${event.timestamp}" type="BOTH" dateStyle="SHORT" timeStyle="DEFAULT" /></td>
    <td valign="top"><c:out value="${event.username}" /></td>
    <td valign="top"><c:out value="${event.level}" /></td>
    <td valign="top"><c:out value="${event.message}" />
      <c:if test="${! empty event.stackTrace}">
        <br/><a href="javascript:void();" onclick="x = document.getElementById('E_<c:out value='${status.index}'/>').style; x.display = (x.display=='block' ? 'none' : 'block');">Stacktrace</a>
        <span id="E_<c:out value='${status.index}'/>" style="display:none">
        <br/><pre><c:out value="${event.stackTrace}" /></pre>
        </span>  
      </c:if>
    </td>
    <td valign="top"></td>
  </c:forEach>
  </table>
  </r:tab>


  <r:tab id="test" label="Test" href="debug.html?debugTab=test" >
  <h1>Audit</h1>
  <a href="../audit/installationAudit.do">Installation Audit</a><br/>
  <a href="../audit/resourceAudit.do">Resource Audit</a><br/>
  <a href="../audit/databaseAudit.do">Database audit</a><br/>

  <h1>Testing</h1>
  <a href="../test/testException.do">Test exception handling</a><br/>
  <a href="../test/testClassLoader.do">Test classloader</a><br/>
  
  <p>

  <h1>Tools</h1>
  <a href="../misc/initialisationFailure.do">Re-initialise application</a><br/>
  <a href="../misc/htmlTidy.do">HTML Tidy</a><br/>
  
  <%--
  <a href="../misc/performanceMonitor.do">Performance monitor</a><br/>
  <a href="../misc/sessionDump.do">Session dump</a><br/>
  --%>
  
  
  </r:tab>
  
</r:tabgroup>

</body>
</html>
