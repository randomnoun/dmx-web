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

    <!-- JavaScript -->
    <script src="mjs?js=prototype" type="text/javascript"></script>
    <!--  <script src="mjs?js=prototype,scriptaculous,builder,effects,dragdrop,controls,slider,sound,rollover" type="text/javascript"></script> -->
    <script src="js/dmx.js" type="text/javascript"></script>
    <script src="js/codemirror/codemirror.js" type="text/javascript"></script>
    <!--  <link href="css/codemirror/docs.css" media="all" rel="stylesheet" type="text/css" /> --> 

<style>
BODY { font-size: 8pt; font-family: Arial; }
.fixtureDef { font-size: 8pt; font-family: Arial;}
.fixtureDef TD { font-size: 8pt; font-family: Arial;}
.fixtureDef INPUT { font-size: 8pt; }
.fixtureDef TEXTAREA { font-family: Lucida Console, Courier New; font-size: 8pt; }
.label { width: 25px; height: 16px; text-align: right; background-color: lightblue; padding-top: 3px; margin-left: 3px; margin-bottom: 1px;}
      .CodeMirror-line-numbers {
        width: 2.2em;
        color: #aaa;
        background-color: #eee;
        text-align: right;
        padding-right: .3em;
        font-size: 10pt;
        font-family: monospace;
        padding-top: .4em;
        line-height: normal;
      }
.lineNumberError {
    background-image: url("image/lineError.png");
    background-repeat: no-repeat;
}
</style>
<script>
function getFixtureDef() {
	var fixtureDefIdEl = document.getElementById("fixtureDefId");
	var fixtureDefId = fixtureDefIdEl.value;
	if (fixtureDefId != '') {
	    //alert("Retrieving fixtureDef " + fixtureDefId);
	    document.location = "maintainFixtureDef.html?action=getFixtureDef&fixtureDefId=" + fixtureDefId;
	}
}
function newFixtureDef() {
    document.location = "maintainFixtureDef.html?action=newFixtureDef";
}


</script>
</head>

<html>
<body>
<h2>DMX Web</h2>
<h3>Fixture definitions</h3>
<c:if test="${message!=null}">
<b><c:out value="${message}" /></b><br/>
</c:if>
<jsp:include page="misc/errorHeader.jsp" />
<table>
<tr><td>Select fixture definition:</td>
    <td><r:select name="fixtureDefId" value="${fixtureDefId}" data="${fixtureDefs}" 
  displayColumn="name" valueColumn="id"  /></td>
    <td><input type="button" name="getFixtureDef" value="Get fixture definition" onclick="getFixtureDef()" /></td>
</tr><tr>    
    <td></td>
    <td></td>
    <td><input type="button" name="createFixtureDef" value="Create new fixture definition" onclick="newFixtureDef()" /></td>
</tr>
<p>
<c:if test="${fixtureDef!=null}" >
<form action="maintainFixtureDef.html" method="post">
<%-- <r:setForm bundle="${generalBundle}" htmlFormat="${0}" labelFormat="${0}" data="${fixtureDef}" /> --%>
<input type="hidden" name="fixtureDef.id" value="${fixtureDef.id}" />
<table class="fixtureDef">
<tr><td>Name:</td>
    <td><r:input type="text" name="fixtureDef.name" value="${fixtureDef.name}"/></td></tr>
<c:if test="${fixtureDef.fixtureDefClassName != null}" >    
<tr><td valign="top">Fixture class:</td>
    <td><c:out value="${fixtureDef.fixtureDefClassName}"/></td></tr>    
</c:if>    
<tr><td valign="top">Fixture script:</td>
    <td><r:input type="textarea" name="fixtureDef.fixtureDefScript" value="${fixtureDef.fixtureDefScript}" rows="25" cols="100"/></td></tr>
<c:if test="${fixtureDef.fixtureControllerClassName != null}" >    
<tr><td valign="top">Controller class:</td>
    <td><c:out value="${fixtureDef.fixtureControllerClassName}"/></td></tr>    
</c:if>    
<tr><td valign="top">Controller script:</td>
    <td><r:input type="textarea" name="fixtureDef.fixtureControllerScript" value="${fixtureDef.fixtureControllerScript}" rows="25" cols="100"/></td></tr>
<tr><td></td>
    <td>
    <c:if test="${fixtureDef.id==-1}">
    <input type="submit" name="updateFixtureDef" value="Create" />
    </c:if>
    <c:if test="${fixtureDef.id!=-1}">
    <input type="submit" name="updateFixtureDef" value="Update" />
    </c:if>
      
    </td>
</tr>
</table>
</form>

<script type="text/javascript">
function initEditorFunc(editor) {
    editor.jumpToLine(34);
    var textAreaEl = document.getElementById("fixtureDef.fixtureDefScript");
    var codeMirrorWrappingEl = textAreaEl.nextSibling;
    lineNumberContainerDiv = codeMirrorWrappingEl.childNodes[2];
    setTimeout(highlightRow.curry(new Date().getTime(), lineNumberContainerDiv, 34), 100);
    // these DIVs don't exist yet....
    //var div34 = lineNumberContainerDiv.getElementsByTagName("DIV")[34];
    //div34.innerHTML="***";
    // alert("style is " + lineNumberContainerDiv.style);
}

function highlightRow(startTime, lineNumberContainerDiv, lineNumber) {
	var div34 = lineNumberContainerDiv.getElementsByTagName("DIV")[lineNumber];
	if (div34) {
		div34.addClassName("lineNumberError");
	} else {
		// give the editor 2 seconds to create line numbers
		if (new Date().getTime()-startTime < 2000) {
		  setTimeout(highlightRow.curry(startTime, lineNumberContainerDiv, 34), 100);
		}
	}
}

  var editor = CodeMirror.fromTextArea('fixtureDef.fixtureDefScript', {
    	lineNumbers: true,
        height: "340px",
        parserfile: ["tokenizejava.js","parsejava.js"],
        stylesheet: "css/codemirror/javacolors.css",
        path: "js/codemirror/",
        tabMode : "shift",
        initCallback : initEditorFunc
    });
    
    
</script>


</c:if>
<hr/>
<%--
<jsp:include page="misc/debugAttributes.jsp" />
 --%>

</body>
</html>
