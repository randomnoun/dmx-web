<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,com.randomnoun.dmx.*"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common-public" prefix="r" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
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
    
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> Maintain show definitions</title>

    <link rel="shortcut icon" href="image/favicon.png" />
    <link rel="stylesheet" href="css/tabs.css" type="text/css" />

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
  position: absolute; top: 30px; left: 210px; width: 1020px; height: 700px;
  background-color: #EEEEFF; border: solid 1px blue; 
}
#rhsMessage {
  position: absolute; top: 5px; left: 210px; width: 1016px; height: 16px;
  text-align: left; color: #000044; font-size: 10pt; font-weight: bold;
  background-color: #EEEEFF; border: solid 1px blue; padding: 2px;
}
.lhsMenuItem {
  width: 180px; height: 70px; background-image: url("image/button-blue.png");
  /*background-color: #AAAAFF; */ ; margin: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.lhsMenuIcon {
  float: left;
}
.lhsMenuText {
  padding-top: 18px;
}
.lhsMenuItemGreen {
  width: 180px; height: 70px; background-image: url("image/button-green.png");
  /*background-color: #AAAAFF; */ ; margin: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.edtSubmit {
  position: absolute; left: 20px; top: 610px;
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


#progressBar { 
  padding-top: 5px; 
}
#progressBarBox { 
  width: 200px; 
  height: 6px; 
  background: #eee;
  border: solid 1px black;
}
#progressBarBoxContent { 
  width: 0; 
  height: 6px; 
  border-right: 1px solid #9ACB34; 
  background: #9ACB34; 
}

#tabContainer {
    position: absolute;
    left: 20px; 
    width: 956px;
}
.tabSheet {
    position:absolute;
    left:20px; /*top:60px;*/
    margin-top: 0px;
    border-style: none solid solid solid;
    border-width: 1px;
    border-color: threedshadow;
    background-color: white;
    padding: 2px;
    width: 950px;
    overflow-x: auto; 
    overflow-y: auto; 
    /*height: 550px;*/  
}

</style>
<script>
<r:setJavascriptVar name="errorLines" value="${errorLines}" />
<r:setJavascriptVar name="showDefId" value="${showDef.id}" />
<r:setJavascriptVar name="recordedShowDefIds" value="${recordedShowDefIds}" />

var edtScriptEditor;
var edtTabNames = new Array('show', 'attachment');

function edtGetShowDef() {
	var showDefIdEl = document.getElementById("showDefId");
	var showDefId = showDefIdEl.value;
	if (showDefId != '') {
	    //alert("Retrieving showDef " + showDefId);
	    document.location = "maintainShowDef.html?action=getShowDef&showDefId=" + showDefId;
	}
}
function edtGetShowRecording() {
	var showDefIdEl = document.getElementById("showDefId");
	var showDefId = showDefIdEl.value;
	if (showDefId != '') {
	    //alert("Retrieving showDef " + showDefId);
	    document.location = "fancyController.html?action=editRecording&showDefId=" + showDefId;
	}
}
function edtNewShowDef() {
    document.location = "maintainShowDef.html?action=newShowDef";
}

function edtChangeShowDef() {
	var showDefIdEl = document.getElementById("showDefId");
	var showDefId = new Number(showDefIdEl.value).valueOf();
	$("getShowRecording").style.display = 
		(recordedShowDefIds.indexOf(showDefId)==-1) ? "none" : "inline";
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
	
	// top: 38px;
    var tabContainerEl=$("tabContainer");
    var offsets = Position.positionedOffset(tabContainerEl);
    for (var i=0; i<edtTabNames.length; i++) {
        $(edtTabNames[i] + "TabSheet").style.top = (offsets[1] + 22) + "px";
        $(edtTabNames[i] + "TabSheet").style.height = (572 - offsets[1]) + "px";
    }
	
    edtScriptEditor = CodeMirror.fromTextArea('showDef.script', {
        lineNumbers: true,
        disableSpellcheck: true,
        height: (522-offsets[1]) + "px", width: "850px",
        width: "850px",
        parserfile: ["tokenizejava.js","parsejava.js"],
        stylesheet: "css/codemirror/javacolors.css",
        path: "js/codemirror/",
        tabSize : 4,
        tabMode : "shift",
        initCallback : edtEditorInitialisationCallback
    });
    var edtSubmitEl = $("edtSubmit");
    edtSubmitEl.update(showDefId==-1 ? 
        "<img class=\"lhsMenuIcon\" width=\"70\" height=\"70\" src=\"image/save.png\" title=\"Create\"/><div class=\"lhsMenuText\">Create</div>" : 
        "<img class=\"lhsMenuIcon\" width=\"70\" height=\"70\" src=\"image/save.png\" title=\"Update\"/><div class=\"lhsMenuText\">Update</div>"
    );		
    Event.observe(edtSubmitEl, 'click', edtSubmitClick);
    
    if ($("attachment")) { $("attachment").value = ""; }
    if ($("addFile")) { 
    	$("addFile").disabled = false; 
        Event.observe($("addFile"), 'click', edtAddFile);
    }
    
    </c:if>
    <c:if test="${showDef==null}" >
    edtChangeShowDef();
    </c:if>
    
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
}

function edtAddFile() {
	$("uploadForm").target="uploadTarget"
    document.forms[1].submit();
    $("progressBar").style.display = 'block';
    $("progressBarText").update("upload in progress: 0%");
    $("addFile").disabled = true;
    window.setTimeout(edtRefreshProgress, 1500);
}

function edtRefreshProgress() {
	new Ajax.Request("maintainShowDef.html?action=getProgress", {
        method:'get', // evalJSON:true,
        onSuccess: function(transport) {
            edtUpdateProgress(transport.responseJSON);
        }
    });	
}

function edtUpdateProgress(json) {
    $("progressBarText").update("upload in progress: " + json.percentDone + "%");
    $("progressBarBoxContent").style.width = parseInt(json.percentDone * 2) + "px";
    window.setTimeout(edtRefreshProgress, 1000);
} 

//invoked by iframe script
function edtCompletedUploadError(text) {
    alert(text);
    $("addFile").disabled = false;
}

function edtCompletedUploadOK(id, sizeInUnits, name, description) {
    var newRowEl = new Element("tr", { "id" : "file[" + id + "]" });
    newRowEl.appendChild(new Element("td"));
    newRowEl.appendChild(new Element("td").update(
        "<input type=\"button\" name=\"image" + id + "\" value=\"Delete\" onclick=\"edtDeleteFile(" + id + ")\"/> " + 
        "<a href=\"image/show/" + showDefId + "/" + name + "\" target=\"_new\">" + name + "</a> (" + sizeInUnits + ") " + description + "<br/>"));
    $("lastImageRow").insert({'before': newRowEl});
    if ($("attachment")) { $("attachment").value = ""; }
    if ($("description")) { $("description").value = ""; }
    $("addFile").disabled = false;
}

function edtDeleteFile(showDefAttachmentId) {
	if (confirm("Are you sure you wish to delete this attachment?")) {
		new Ajax.Request("maintainShowDef.html?action=deleteFile&showDefId=<c:out value='${showDef.id}'/>&fileId=" + showDefAttachmentId, {
	        method:'get', // evalJSON:true,
	        onSuccess: function(transport) {
	            edtDeleteFileComplete(transport.responseJSON);
	        }
	    }); 		
	}
}

function edtDeleteFileComplete(json) {
	var result = json["result"];
	if (result=="success") {
		var fileId = json["fileId"];
		$("file[" + fileId + "]").remove();
	} else {
		var message = json["message"];
		alert(message);
	}
}

function edtSetTab(newTab) {
	<c:if test="${showDef!=null}" >
	for (var i=0; i<edtTabNames.length; i++) {
		$(edtTabNames[i] + "TabSheet").style.visibility = (newTab==edtTabNames[i] ? "visible" : "hidden");
		$(edtTabNames[i] + "Tab").className = (newTab==edtTabNames[i] ? "current" : "");
	}
	</c:if>
	return false;
}


function edtDeleteShowDef() {
    if (confirm("Are you sure you want to delete this show definition?")) {
        document.location = "maintainShowDef.html?action=deleteShowDef&showDefId=" + showDefId;     
    }
}

function edtSubmitClick() { edtSubmit(); }
function lhsCancelClick() { document.location = "index.html?panel=cnfPanel"; }
function lhsOKClick() { edtSubmit(); };

function edtSubmit() {
	document.forms[0].elements["showDef.name"].value=$("showDef.name").value;
	document.forms[0].submit();
}


function initWindow() {
	edtInitPanel();
	edtSetTab('show'); // @TODO: check errorList
}

</script>
</head>
<body onload="initWindow()">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Show definitions</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/back.png" title="Back"/><div class="lhsMenuText">Back</div></div>
  <div id="lhsOK" class="lhsMenuItemGreen"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="OK"/><div class="lhsMenuText">OK</div></div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">

<div id="edtPanel" >
<jsp:include page="misc/errorHeader.jsp" />

<table class="showDef" width="900px;">
<col width="100px;"/>
<col width="100px;"/>
<col width="700px;"/>
<c:if test="${showDef==null}" >
<form action="maintainShowDef.html" method="post">
<tr><td>Select show definition:</td>
    <td><r:select name="showDefId" value="${showDefId}" data="${showDefs}" 
  displayColumn="name" valueColumn="id"  onchange="edtChangeShowDef()" /></td>
    <td>
    <input type="button" id="getShowRecording" name="getShowRecording" value="Get show recording" onclick="edtGetShowRecording()" />
    <input type="button" name="getShowDef" value="Get show definition" onclick="edtGetShowDef()" />
    </td>
</tr><tr>    
    <td></td>
    <td></td>
    <td><input type="button" name="createShowDef" value="Create new show definition" onclick="edtNewShowDef()" /></td>
</tr>
</form>
</c:if>


<c:if test="${showDef!=null}" >
<tr><td>Name:</td>
    <td colspan="2"><r:input type="text" name="showDef.name" value="${showDef.name}"/>
    <c:if test="${showDef.id!=-1}" >
    <input type="button" name="deleteShowDef" value="Delete this show definition" onclick="edtDeleteShowDef()" /></td>
    </c:if>
    </td>
</tr>
</table>

<div id="tabContainer" class="tabs"><ul>
<li id="showTab" class="current"><a onclick="edtSetTab('show')">Show</a></li>
<li id="attachmentTab"><a onclick="edtSetTab('attachment')" >Attachments</a></li>
</ul></div>

<div id="showTabSheet" class="tabSheet">
<form action="maintainShowDef.html" method="post">
<table>   
<input type="hidden" name="showDef.id" value="${showDef.id}" />
<input type="hidden" name="updateShowDef" value="Y" />
<input type="hidden" name="showDef.name" value="<c:out value='${showDef.name}'/>"/>
<c:if test="${showDef.className != null}" >    
<tr><td valign="top">Classname:</td>
    <td><c:out value="${showDef.className}"/></td></tr>    
</c:if> 
<tr><td valign="top">Script:</td>
    <td colspan="2"><r:input type="textarea" name="showDef.script" value="${showDef.script}" rows="25" cols="100"/></td>
</tr>
</table>
</form>
</div>

<div id="attachmentTabSheet" class="tabSheet">
<c:choose><c:when test="${showDef.id==-1}">
You must create a show before you can attach documents
</c:when>
<c:otherwise>
<form id="uploadForm" action="maintainShowDef.html" method="post" enctype="multipart/form-data">
<input type="hidden" name="action" value="submitFile" />
<input type="hidden" name="showDefId" value="${showDef.id}" />
<table>
<tr><td valign="top">Show attachments:</td>
    <td><input id="attachment" type="file" name="attachment" style="width: 400px;"/>
    </td>
<tr><td>Description:</td>
    <td><input id="description" type="text" name="description" size="30" />
        <input id="addFile", type="button" name="addFile" value="Add" />
    </td>
</tr>
<tr><td></td>
    <td>
    <div id="progressBar" style="display: block;">
      <div id="theMeter">
        <div id="progressBarBox">
           <div id="progressBarBoxContent"></div>
        </div>
        <div id="progressBarText"></div>            
      </div>
    </div>
    </td>
</tr>    
<tr><td></td>
    <td><iframe name="uploadTarget" id="uploadTarget" style="width:100px; height:1px; border:0px solid #fff;"/></iframe>
    </td>
</tr>    

<c:if test="${showDefAttachments!=null}" >
<c:forEach var="showDefAttachment" items="${showDefAttachments}" >
<tr id="file[<c:out value='${showDefAttachment.id}'/>]"><td valign="top"></td> 
    <td><input type="button" value="Delete" onclick="edtDeleteFile(<c:out value="${showDefAttachment.id}"/>)"/> <a href="image/show/<c:out value="${showDefAttachment.showDefId}"/>/<c:out value="${showDefAttachment.name}"/>" target="_new"><c:out value="${showDefAttachment.name}"/></a> (<c:out value="${showDefAttachment.sizeInUnits}"/>) <c:out value="${showDefAttachment.description}"/><br/>
    </td>    
</tr>
</c:forEach>
</c:if>
<tr id="lastImageRow" />
</table>
</form>
</c:otherwise>
</c:choose>
</div>

</div>

<div id="edtSubmit" class="edtSubmit"></div>

</c:if>
</table>



</div>
</div>
<jsp:include page="/misc/analytics.jsp" />
</body>
</html>
