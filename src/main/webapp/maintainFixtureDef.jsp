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
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
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
  position: absolute; left: 20px; top: 610px;
  width: 180px; height: 70px; background-image: url("image/button-green.png");
  /*background-color: #AAAAFF; */ ; margin-top: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}

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
    width: 856px;
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
    width: 850px;
    overflow-x: auto; 
    overflow-y: auto; 
    /*height: 550px;*/  
}

</style>
<script>
<r:setJavascriptVar name="fixtureDefErrorLines" value="${fixtureDefErrorLines}" />
<r:setJavascriptVar name="fixtureControllerErrorLines" value="${fixtureControllerErrorLines}" />
<r:setJavascriptVar name="channelMuxerErrorLines" value="${channelMuxerErrorLines}" />
<r:setJavascriptVar name="fixtureDefId" value="${fixtureDef.id}" />
var edtFixtureDefEditor;
var edtFixtureControllerEditor;
var edtTabNames = new Array('fixture', 'controller', 'channelMuxer', 'attachment');

function edtGetFixtureDef() {
	var fixtureDefIdEl = document.getElementById("fixtureDefId");
	var fixtureDefId = fixtureDefIdEl.value;
	if (fixtureDefId != '') {
	    //alert("Retrieving fixtureDef " + fixtureDefId);
	    document.location = "maintainFixtureDef.html?action=getFixtureDef&fixtureDefId=" + fixtureDefId;
	}
}
function edtNewFixtureDef() {
    document.location = "maintainFixtureDef.html?action=newFixtureDef";
}

function edtEditorInitialisationCallback(editor) {
	if (editor==edtFixtureDefEditor) {
	    if (edtFixtureDefEditor && fixtureDefErrorLines > 0) {
	    	edtFixtureDefEditor.jumpToLine(fixtureDefErrorLines[0]);
	        var lineNumberContainerDiv = $("fixtureDef.fixtureDefScript").nextSibling.childNodes[2];
	        setTimeout(edtHighlightRow.curry(new Date().getTime(), lineNumberContainerDiv, fixtureDefErrorLines), 100);
	    }
	} else if (editor==edtFixtureControllerEditor) {
        if (edtFixtureControllerEditor && fixtureControllerErrorLines > 0) {
        	edtFixtureControllerEditor.jumpToLine(fixtureControllerErrorLines[0]);
            var lineNumberContainerDiv = $("fixtureDef.fixtureControllerScript").nextSibling.childNodes[2];
            setTimeout(edtHighlightRow.curry(new Date().getTime(), lineNumberContainerDiv, fixtureControllerErrorLines), 100);
        }
	} else {
        if (edtChannelMuxerEditor && channelMuxerErrorLines > 0) {
            edtChannelMuxerEditor.jumpToLine(channelMuxerErrorLines[0]);
            var lineNumberContainerDiv = $("fixtureDef.channelMuxerScript").nextSibling.childNodes[2];
            setTimeout(edtHighlightRow.curry(new Date().getTime(), lineNumberContainerDiv, channelMuxerErrorLines), 100);
        }
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
    <c:if test="${fixtureDef!=null}" >
    
    // top: 38px;
    var tabContainerEl=$("tabContainer");
    var offsets = Position.positionedOffset(tabContainerEl);
    for (var i=0; i<edtTabNames.length; i++) {
        $(edtTabNames[i] + "TabSheet").style.top = (offsets[1] + 22) + "px";
        $(edtTabNames[i] + "TabSheet").style.height = (572 - offsets[1]) + "px";
    }
    
    edtFixtureDefEditor = CodeMirror.fromTextArea('fixtureDef.fixtureDefScript', {
        lineNumbers: true,
        height: (522-offsets[1]) + "px", width: "750px",
        parserfile: ["tokenizejava.js","parsejava.js"],
        stylesheet: "css/codemirror/javacolors.css",
        path: "js/codemirror/",
        tabMode : "shift",
        initCallback : edtEditorInitialisationCallback
    });
    edtFixtureControllerEditor = CodeMirror.fromTextArea('fixtureDef.fixtureControllerScript', {
        lineNumbers: true,
        height: (522-offsets[1]) + "px", width: "750px",
        parserfile: ["tokenizejava.js","parsejava.js"],
        stylesheet: "css/codemirror/javacolors.css",
        path: "js/codemirror/",
        tabMode : "shift",
        initCallback : edtEditorInitialisationCallback
    });
    edtChannelMuxerEditor = CodeMirror.fromTextArea('fixtureDef.channelMuxerScript', {
        lineNumbers: true,
        height: (522-offsets[1]) + "px", width: "750px",
        parserfile: ["tokenizejava.js","parsejava.js"],
        stylesheet: "css/codemirror/javacolors.css",
        path: "js/codemirror/",
        tabMode : "shift",
        initCallback : edtEditorInitialisationCallback
    });

    var edtSubmitEl = $("edtSubmit");
    edtSubmitEl.update(fixtureDefId==-1 ? "Create" : "Update");
    Event.observe(edtSubmitEl, 'click', edtSubmitClick);

    if ($("attachment")) { $("attachment").value = ""; }
    if ($("addFile")) { $("addFile").disabled = false; }
    </c:if>
    
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
    Event.observe($("addFile"), 'click', edtAddFile);
}

function edtSubmitClick() { edtSubmit(); }
function lhsCancelClick() { document.location = "index.html?panel=cnfPanel"; }
function lhsOKClick() { edtSubmit(); };

function edtSubmit() {
	document.forms[0].elements["fixtureDef.name"].value=$("fixtureDef.name").value;
	document.forms[0].elements["fixtureDef.fixtureControllerScript"].value=edtFixtureControllerEditor.getCode();
	document.forms[0].elements["fixtureDef.channelMuxerScript"].value=edtChannelMuxerEditor.getCode();
	document.forms[0].submit();
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
	new Ajax.Request("maintainFixtureDef.html?action=getProgress", {
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
        "<a href=\"image/fixture/" + fixtureDefId + "/" + name + "\" target=\"_new\">" + name + "</a> (" + sizeInUnits + ") " + description + "<br/>"));
    $("lastImageRow").insert({'before': newRowEl});
    if ($("attachment")) { $("attachment").value = ""; }
    if ($("description")) { $("description").value = ""; }
    $("addFile").disabled = false;
}

function edtDeleteFile(fixtureDefImageId) {
	if (confirm("Are you sure you wish to delete this attachment?")) {
		new Ajax.Request("maintainFixtureDef.html?action=deleteFile&fixtureDefId=<c:out value='${fixtureDef.id}'/>&fileId=" + fixtureDefImageId, {
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
	for (var i=0; i<edtTabNames.length; i++) {
		$(edtTabNames[i] + "TabSheet").style.visibility = (newTab==edtTabNames[i] ? "visible" : "hidden");
		$(edtTabNames[i] + "Tab").className = (newTab==edtTabNames[i] ? "current" : "");
	}
	return false;
}


function initWindow() {
    edtInitPanel();
    edtSetTab('fixture'); // @TODO: check errorList
}


</script>
</head>



<body onload="initWindow()">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Fixture config</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem">Back</div>
  <div id="lhsOK" class="lhsMenuItemGreen">OK</div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">

<div id="edtPanel" >
<jsp:include page="misc/errorHeader.jsp" />


<table class="fixtureDef" width="900px;">
<col width="100px;"/>
<col width="100px;"/>
<col width="700px;"/>

<c:if test="${fixtureDef==null}" >
<form action="maintainFixtureDef.html" method="post">
<tr><td>Select fixture definition:</td>
    <td><r:select name="fixtureDefId" value="${fixtureDefId}" data="${fixtureDefs}" 
  displayColumn="name" valueColumn="id"  /></td>
    <td><input type="button" name="getFixtureDef" value="Get fixture definition" onclick="edtGetFixtureDef()" /></td>
</tr><tr>    
    <td></td>
    <td></td>
    <td><input type="button" name="createFixtureDef" value="Create new fixture definition" onclick="edtNewFixtureDef()" /></td>
</tr>
</form>
</c:if>

<c:if test="${fixtureDef!=null}" >
<tr><td>Name:</td>
    <td colspan="2"><r:input type="text" name="fixtureDef.name" value="${fixtureDef.name}"/></td></tr>
</table>

<div id="tabContainer" class="tabs"><ul>
<li id="fixtureTab" class="current"><a onclick="edtSetTab('fixture')">Fixture</a></li>
<li id="controllerTab"><a onclick="edtSetTab('controller')" >Controller</a></li>
<li id="channelMuxerTab"><a onclick="edtSetTab('channelMuxer')" >Channel Muxer</a></li>
<li id="attachmentTab"><a onclick="edtSetTab('attachment')" >Attachments</a></li>
</ul></div>
<div id="fixtureTabSheet" class="tabSheet">
<form action="maintainFixtureDef.html" method="post">
<table>
<input type="hidden" name="fixtureDef.id" value="<c:out value='${fixtureDef.id}'/>"/>
<input type="hidden" name="updateFixtureDef" value="Y" />
<input type="hidden" name="fixtureDef.name" value="<c:out value='${fixtureDef.name}'/>"/>
<input type="hidden" name="fixtureDef.fixtureControllerScript" value="" />
<input type="hidden" name="fixtureDef.channelMuxerScript" value="" />
<c:if test="${fixtureDef.fixtureDefClassName != null}" >    
<tr><td valign="top">Fixture class:</td>
    <td><c:out value="${fixtureDef.fixtureDefClassName}"/></td></tr>    
</c:if>    
<tr><td valign="top">Fixture script:</td>
    <td colspan="2"><r:input type="textarea" name="fixtureDef.fixtureDefScript" value="${fixtureDef.fixtureDefScript}" rows="25" cols="100"/></td></tr>
</table>
</form>
</div>

<div id="controllerTabSheet" class="tabSheet">
<table>
<c:if test="${fixtureDef.fixtureControllerClassName != null}" >    
<tr><td valign="top">Controller class:</td>
    <td><c:out value="${fixtureDef.fixtureControllerClassName}"/></td></tr>    
</c:if>    
<tr><td valign="top">Controller script:</td>
    <td colspan="2"><r:input type="textarea" name="fixtureDef.fixtureControllerScript" value="${fixtureDef.fixtureControllerScript}" rows="25" cols="100"/></td></tr>
</table>    
</div>

<div id="channelMuxerTabSheet" class="tabSheet">
<table>
<c:if test="${fixtureDef.channelMuxerClassName != null}" >    
<tr><td valign="top">Channel Muxer class:</td>
    <td><c:out value="${fixtureDef.channelMuxerClassName}"/></td></tr>    
</c:if>    
<tr><td valign="top">Channel Muxer script:</td>
    <td colspan="2"><r:input type="textarea" name="fixtureDef.channelMuxerScript" value="${fixtureDef.channelMuxerScript}" rows="25" cols="100"/></td></tr>
</table>    
</div>

<div id="attachmentTabSheet" class="tabSheet">
<form id="uploadForm" action="maintainFixtureDef.html" method="post" enctype="multipart/form-data">
<input type="hidden" name="action" value="submitFile" />
<input type="hidden" name="fixtureDefId" value="${fixtureDef.id}" />
<table>
<tr><td valign="top">Image attachments:</td>
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

<c:if test="${fixtureDefImages!=null}" >
<c:forEach var="fixtureDefImage" items="${fixtureDefImages}" >
<tr id="file[<c:out value='${fixtureDefImage.id}'/>]"><td valign="top"></td> 
    <!--  maintainFixtureDef.html?action=getFile&fileId=<c:out value="${fixtureDefImage.id}"/>"   -->
    <td><input type="button" value="Delete" onclick="edtDeleteFile(<c:out value="${fixtureDefImage.id}"/>)"/> <a href="image/fixture/<c:out value="${fixtureDefImage.fixtureDefId}"/>/<c:out value="${fixtureDefImage.name}"/>" target="_new"><c:out value="${fixtureDefImage.name}"/></a> (<c:out value="${fixtureDefImage.sizeInUnits}"/>) <c:out value="${fixtureDefImage.description}"/><br/>
    </td>    
</tr>
</c:forEach>
</c:if>
<tr id="lastImageRow" />
</table>
</form>
</div>

</div>


<div id="edtSubmit" class="edtSubmit"></div>
</c:if>
</table>
</form>


</div>
</div>
</body>

</html>
