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
    <script src="js/codemirror/codemirror.js" type="text/javascript"></script>
    
<style>
BODY { font-size: 8pt; font-family: Arial; }
.lhsMenuContainer {
  position: absolute; top: 30px; left: 5px; width: 200px; height: 700px;
  background-color: #EEEEFF; border: solid 1px blue;
}
#lhsLogo {
  position: absolute; top: 5px; left: 5px; width: 202px; height: 22px;
  text-align: left; color: white; font-size: 10pt; font-weight: bold;
  /*background-color: blue; */
  background-image: url("image/lhsLogo-back.png"); 
  padding: 0px 0px;
  cursor: pointer;
}
.rhsPanel {
  position: absolute; top: 30px; left: 210px; width: 900px; height: 700px;
  background-color: #EEEEFF; border: solid 1px blue; 
}
#rhsMessage {
  position: absolute; top: 5px; left: 210px; width: 896px; height: 16px;
  text-align: left; color: #000044; font-size: 10pt; font-weight: bold;
  background-color: #EEEEFF; border: solid 1px blue; padding: 2px;
}
.lhsMenuItem {
  width: 180px; height: 70px; background-image: url("image/button-blue.png");
  /*background-color: #AAAAFF; */ ; margin: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.lhsMenuItemGreen {
  width: 180px; height: 70px; background-image: url("image/button-green.png");
  /*background-color: #AAAAFF; */ ; margin: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.edtSubmit {
  width: 180px; height: 70px; background-image: url("image/button-green.png");
  /*background-color: #AAAAFF; */ ; margin-top: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}


.showDef { font-size: 8pt; font-family: Arial;}
.showDef TD { font-size: 8pt; font-family: Arial;}
.showDef INPUT { font-size: 8pt; }
.showDef TEXTAREA { font-family: Lucida Console, Courier New; font-size: 8pt; }
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
.CodeMirror-wrapping {
    background-color: #fff;
    border: solid #7b9ebd 1px; 
}
.lineNumberError {
    background-image: url("image/lineError.png");
    background-repeat: no-repeat;
}

</style>
<script>
<r:setJavascriptVar name="errorLines" value="${errorLines}" />
<r:setJavascriptVar name="showDefId" value="${showDef.id}" />
var edtScriptEditor;

function edtGetShowDef() {
	var showDefIdEl = document.getElementById("showDefId");
	var showDefId = showDefIdEl.value;
	if (showDefId != '') {
	    //alert("Retrieving showDef " + showDefId);
	    document.location = "maintainShowDef.html?action=getShowDef&showDefId=" + showDefId;
	}
}
function edtNewShowDef() {
    document.location = "maintainShowDef.html?action=newShowDef";
}


function edtEditorInitialisationCallback(editor) {
	if (errorLines && errorLines.length > 0) {
		edtScriptEditor.jumpToLine(errorLines[0]);
	    var lineNumberContainerDiv = $("showDef.script").nextSibling.childNodes[2];
	    setTimeout(edtHighlightRow.curry(new Date().getTime(), lineNumberContainerDiv, errorLines), 100);
	}
}

// line numbers aren't visible at editor initialisation time, try for 2 seconds...
function edtHighlightRow(startTime, lineNumberContainerDiv, errorLines) {
	var allFound = true;
	var divTags = lineNumberContainerDiv.getElementsByTagName("DIV");
	var offset = 0; // for gaps in line numbering DIVs due to word-wrap 
	for (var i=0; allFound && i<errorLines.length; i++) {
	    var lineNumberEl = divTags[errorLines[i]+offset];
	    if (lineNumberEl) {
	    	while (lineNumberEl && lineNumberEl.innerHTML!=errorLines[i]) {
	    		offset++; lineNumberEl = divTags[errorLines[i]+offset];
	    	}
	    	if (lineNumberEl) {
	    	   lineNumberEl.addClassName("lineNumberError");
	        }
	    } else {
	    	allFound = false;
	        if (new Date().getTime()-startTime < 2000) {
	          setTimeout(edtHighlightRow.curry(startTime, lineNumberContainerDiv, errorLines), 100);
	        }
	    }
	}
}

function edtInitPanel() {
	<c:if test="${showDef!=null}" >
    edtScriptEditor = CodeMirror.fromTextArea('showDef.script', {
        lineNumbers: true,
        disableSpellcheck: true,
        height: "570px", width: "750px",
        parserfile: ["tokenizejava.js","parsejava.js"],
        stylesheet: "css/codemirror/javacolors.css",
        path: "js/codemirror/",
        tabMode : "shift",
        initCallback : edtEditorInitialisationCallback
    });
    var edtSubmitEl = $("edtSubmit");
    edtSubmitEl.update(showDefId==-1 ? "Create" : "Update");
    Event.observe(edtSubmitEl, 'click', edtSubmitClick);
    </c:if>
    
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
}

function edtSubmitClick() { document.forms[0].submit(); }
function lhsCancelClick() { document.location = "index.html?panel=cnfPanel"; }
function lhsOKClick() { document.forms[0].submit(); };

function initWindow() {
	edtInitPanel();
}

</script>
</head>
<html>


<body onload="initWindow()">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Show config</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem">Back</div>
  <div id="lhsOK" class="lhsMenuItemGreen">OK</div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">

<div id="edtPanel" >
<jsp:include page="misc/errorHeader.jsp" />

<form action="maintainShowDef.html" method="post">
<table class="showDef" width="900px;">
<col width="100px;"/>
<col width="100px;"/>
<col width="700px;"/>
<c:if test="${showDef==null}" >
<tr><td>Select show definition:</td>
    <td><r:select name="showDefId" value="${showDefId}" data="${showDefs}" 
  displayColumn="name" valueColumn="id"  /></td>
    <td><input type="button" name="getShowDef" value="Get show definition" onclick="edtGetShowDef()" /></td>
</tr><tr>    
    <td></td>
    <td></td>
    <td><input type="button" name="createShowDef" value="Create new show definition" onclick="edtNewShowDef()" /></td>
</tr>
</c:if>
<c:if test="${showDef!=null}" >
<input type="hidden" name="showDef.id" value="${showDef.id}" />
<input type="hidden" name="updateShowDef" value="Y" />
<tr><td>Name:</td>
    <td colspan="2"><r:input type="text" name="showDef.name" value="${showDef.name}"/></td></tr>
<tr><td valign="top">Script:</td>
    <td colspan="2"><r:input type="textarea" name="showDef.script" value="${showDef.script}" rows="25" cols="100"/></td></tr>
<tr><td></td>
    <td>
    <div id="edtSubmit" class="edtSubmit"></div>
    </td>
</tr>
</c:if>
</table>
</form>


</div>
</div>
</body>

</html>
