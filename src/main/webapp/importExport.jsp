<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<%
	// @TODO the onchange javascript for the empty row for the active field isn't set.
    AppConfig appConfig = AppConfig.getAppConfig();
%>
<html>
<head>

    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=utf-8" />
    <meta name="robots" content="index,follow" />
    <meta name="publisher" content="randomnoun" />
    <meta name="copyright" content="&copy; Copyright 2010, randomnoun" />
    <meta name="description" content="DMX webapp" />
    <meta name="revisit-after" content="2 days" />
    <meta name="keywords" content="nothing-in-particular" />
    
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> Import/Export</title>
     
    <link rel="shortcut icon" href="images/favicon.png" />
    
    <link href="css/table-edit.css" media=all" rel="stylesheet" type="text/css" />

    <script src="mjs?js=prototype" type="text/javascript"></script>

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
  width: 180px; height: 70px; background-image: url("image/button-green.png");
  /*background-color: #AAAAFF; */ ; margin-top: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.rj {  /* right-justified */
  text-align: right;
}
.ir { /* import radio button */ 
  position: absolute; top: 0px;
}


TABLE { width: auto; }
TH, TD { padding: 0px; line-height: 1; }
INPUT { font-size: 8pt; }
TR { line-height: 1; }
SELECT { color: black; margin: 0px; font-size: 8pt; }
.formHeader { color: white; font-weight: bold; padding: 2px; vertical-align: bottom; }
.redrollover:hover { background-color: #FFAAAA; }
.greenrollover:hover { background-color: #AAFFAA; }
.errorBg {  }
.errorBg INPUT { background-color: #FFAAAA; }
.errorBg SELECT { background-color: #FFAAAA; }

#controls .menu {
  width:440px; height: 70px; float:left; background: url(images/ui/bg-menu.png) no-repeat left top; color: #ffffff;
  font-size: 22px;
  font-weight: bold; text-align: center; padding-top:10px;
}
.lhsCategorySelectButton {
  width:158px; height:54px; background: url(images/ui/bg-menu-small.png) no-repeat left top; float:left;
  font-size: 22px; color: white;
  font-weight: bold; text-align: center; padding-top: 16px;
  cursor: pointer
}
.smallInput { width: 40px; }
ul { list-style: none; margin: 0px 5px; padding-left: 16px; }

li {
  margin: 0 0 0px 0;
  position: relative;
}
#exportItemsDiv {
  position: absolute; left: 20px; top: 20px; width: 300px; height: 600px;
  overflow: scroll; 
}
#importItemsDiv {
  position: absolute; left: 20px; top: 20px; width: 300px; height: 600px;
  overflow: scroll; 
}
.importIcon {
  display: inline-block; width: 16px; height: 16px; position: relative; top: 1px; margin-right: 2px; 
}
.edtImage {
  width: 16px; height: 16px; position: relative; top: 1px; margin-right: 2px;
}
.edtImage2 {
  position: absolute; top: 0px; left: 0px; width: 16px; height: 16px;  
}


#exportDiv {
  position: absolute;
  left: 0px; top: 0px;
  width: 400px; height: 700px;
}
#importDiv { 
  position: absolute;
  left: 450px; top: 0px;
  width: 400px; height: 700px;

}
</style>

<script>
<r:setJavascriptVar name="exportItems" value="${exportItems}" />
<r:setJavascriptVar name="importItems" value="${importItems}" />

function edtAddTreeNodes(id, containerEl, items) {
    var ulEl = new Element("ul");
    containerEl.insert({'bottom' : ulEl});
    for (var i=0; i<items.length; i++) {
    	var liEl = new Element("li").update(
    		"<input type=\"checkbox\" name=\"" + (items[i].name ? items[i].name : id + "-" + i) + "\" id=\"" + id + "-" + i + "\">" +
            "<label for=\"exportItems-" + i + "\">" +
            (items[i].image ? '<img src="image/' + items[i].image + '" class="edtImage" />' : '' ) + 
            items[i].text + "</label>");
    	ulEl.insert({'bottom' : liEl});
    	if (items[i].children && items[i].children.length > 0) {
    		edtAddTreeNodes(id + "-" + i, liEl, items[i].children);
    	}
    }
}

function edtAddImportTreeNodes(id, containerEl, items, depth) {
    var ulEl = new Element("ul");
    containerEl.insert({'bottom' : ulEl});
    for (var i=0; i<items.length; i++) {
    	var header = items[i].header;
    	var overlayError = items[i].showError;
    	var overlayWarn = items[i].canReplace;
    	var name = items[i].name ? items[i].name : id + "-" + i;
    	var liEl = new Element("li").update(
    		"<input type=\"checkbox\" name=\"" + name + "\" id=\"" + id + "-" + i + "\">" +
            "<label for=\"" + name + "\">" +
            "<span class=\"importIcon\">" +
            (items[i].image ? '<img src="image/' + items[i].image + '" class="edtImage2" />' : '' ) +
            (overlayError ? '<img src="image/overlay-error.gif" class="edtImage2" title="' + items[i].reason + '"/>' :
            (overlayWarn ? '<img src="image/overlay-warn.gif" class="edtImage2" title="' + items[i].reason + '"/>' : '')) +
            "</span>" + 
            items[i].text + "</label>" /* +
            (header ? "" : 
              (items[i].canAdd ?     "<input class=\"ir\" style=\"left:" + (180-depth*21) + "px;\" value=\"+\" type=\"radio\" name=\"" + items[i].name + ".replace\">" : "") +
              (items[i].canReplace ? "<input class=\"ir\" style=\"left:" + (200-depth*21) + "px;\" value=\"O\" type=\"radio\" name=\"" + items[i].name + ".replace\">" : "") +
              ((items[i].canAddWithRename || items[i].canReplaceWithRename) ? "<input class=\"ir\" style=\"left:" + (220-depth*21) + "px;\" value=\"R\" type=\"radio\" name=\"" + items[i].name + ".replace\">" : "")
            )  */
    	);
    	ulEl.insert({'bottom' : liEl});
    	if (items[i].children && items[i].children.length > 0) {
    		edtAddImportTreeNodes(id + "-" + i, liEl, items[i].children, depth+1);
    	}
    }
}


function edtCheckSiblings(containerEl) {
	var ulEl = $(containerEl.parentNode);  // li->ul
	var ec=0, cc=0; // el count, checked count
	ulEl.select('input[type="checkbox"]').each(function(el) { 
	    ec++; cc+=el.checked?1:0;
	});
	var el = $(ulEl.parentNode).select('input[type="checkbox"]')[0]; 
	if (ec==cc) {
	    el.indeterminate = false;
	    el.checked = "true";
	} else if (cc==0) {
		el.indeterminate = false;
	    el.checked = false;
	} else {
		el.indeterminate = true;
	    el.checked = "true";
	}
	if (ulEl.parentNode.tagName=="LI") {
		edtCheckSiblings(ulEl.parentNode);
	}
}

function edtCheckboxChange(e) {
	var tgtEl = e.findElement();
    var checked = tgtEl.checked;
    containerEl = $(tgtEl.parentNode); // li
    containerEl.select('input[type="checkbox"]').each(function(el) {
      el.writeAttribute("indeterminate", "false");
      el.checked = checked ? "true" : false;
    });
    edtCheckSiblings(containerEl);
}

function edtInitPanel() {
    //var edtSubmitEl = $("edtSubmit");
    //Event.observe(edtSubmitEl, 'click', edtSubmitClick);
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
    if (exportItems) {
	    var exportItemsDivEl = $("exportItemsDiv");
	    edtAddTreeNodes("exportItems", exportItemsDivEl, exportItems);
    }
    if (importItems) {
	    var importItemsDivEl = $("importItemsDiv");
	    edtAddImportTreeNodes("importItems", importItemsDivEl, importItems, 0);
	}
    $$('input[type="checkbox"]').each(function(el) {
    	Event.observe(el, 'change', edtCheckboxChange);
    });

}
function edtSubmitClick() { 
	isSubmitting=true; 
	checkModify('exportForm',tblObj); 
	document.forms[0].submit();
}
function lhsCancelClick() { document.location = "index.html?panel=cnfPanel"; }
function lhsOKClick() { 
    isSubmitting=true; 
	checkModify('exportForm',tblObj); 
	document.forms[0].submit();
};
function initWindow() {
    edtInitPanel();
}



</script>
</head>

<body onload="initWindow()" >
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Import/Export</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/back.png" title="Back"/><div class="lhsMenuText">Back</div></div>
  <div id="lhsOK" class="lhsMenuItemGreen"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="OK"/><div class="lhsMenuText">OK</div></div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">

	<jsp:include page="/misc/errorHeader.jsp" />
	<div id="exportDiv">
	<form id="exportForm" name="exportForm" method="post" action="importExport.html">
	<input type="hidden" name="action" value="export" />
    <div id="exportItemsDiv"></div>
    <input type="submit" name="export" value="Export" />
	</form>
	</div>
	
	<div id="importDiv">
	<form id="importForm" name="importForm" method="post" action="importExport.html" enctype="multipart/form-data">
	<input type="hidden" name="action" value="import" />
	<input type="file" name="importFile" />
    <input type="submit" name="import" value="Import" />
    <div id="importItemsDiv">
    <%--
    (This will be updated to display the items in the imported file)
    <br/>
    i.e. same sort of tree display as export, but with a bunch of
    radio buttons on the right to tell the system what to do
    if there's a name collision.
    'replace', 'rename', 'skip'
    if there's a classname collision, then rename might be a bit 
    more difficult. 
    --%>
    </div>
	</form>
	</div>

</div>

</body>
</html>
