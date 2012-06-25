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
}
#exportItemsDiv {
  position: absolute; left: 20px; top: 20px; width: 300px; height: 600px;
  overflow: scroll; 
}
.edtImage {
  width: 16px; height: 16px; position: relative; top: 1px; margin-right: 2px;
}

</style>

<script>
<r:setJavascriptVar name="exportItems" value="${exportItems}" />

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
    
    var exportItemsDivEl = $("exportItemsDiv");
    edtAddTreeNodes("exportItems", exportItemsDivEl, exportItems);
    $$('input[type="checkbox"]').each(function(el) {
    	Event.observe(el, 'change', edtCheckboxChange);
    });
    	

}
function edtSubmitClick() { 
	isSubmitting=true; 
	checkModify('mainForm',tblObj); 
	document.forms[0].submit();
}
function lhsCancelClick() { document.location = "index.html?panel=cnfPanel"; }
function lhsOKClick() { 
    isSubmitting=true; 
	checkModify('mainForm',tblObj); 
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
	<form id="mainForm" name="mainForm" method="post" action="importExport.html">
	<input type="hidden" name="action" value="export" />
    <div id="exportItemsDiv"></div>
    <input type="submit" name="export" value="Export" />
	
	</form>

</div>

</body>
</html>
