<%@ page 
  language="java"
  contentType="text/html; charset=utf-8"
  pageEncoding="utf-8"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<% 
    //@TODO the onchange javascript for the empty row for the dmxOffset field isn't set.
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
    
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> Maintain Fixtures</title>
     
    <link rel="shortcut icon" href="images/favicon.png" />
    
    <link href="css/table-edit.css" media=all" rel="stylesheet" type="text/css" />

    <script src="mjs?js=prototype" type="text/javascript"></script>
    <script language="javascript" src="js/table-edit.js"></script>

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
.lhsMenuText2 {
  padding-top: 6px;
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
.smallInput2 { width: 30px; }
#repeatFixtureDiv { 
  width: 1000px; height: 500px; 
  padding: 10px; 
  border: solid 2px black; 
  display: none; 
}
#repeatFixtureTable TD { vertical-align: top; }
.rfInput { font-family: Courier New; font-size: 8pt; width: 200px; }
.rfTitle1 { font-size: 14pt; font-weight: bold; padding-bottom: 15px;  }
.rfTitle2 { position: absolute; font-size: 14pt; font-weight: bold; padding-bottom: 15px; left: 420px; top: 10px;}
#rfPreviewContainer { 
  position: absolute; top: 40px; left: 420px; width: 580px; height: 580px;
  overflow: scroll;  
}  
.rfPreview {
  display: inline-block;
  width: 140px; height: 90px; 
  color: #7369b5;
  background-color: #b5a6ef;
  margin: 3px; padding: 2px; 
}
.rfPreviewName {
  color: #000042;
}
#rfPreviewUpdateFrame { display: none; }
</style>

<script>
<r:setJavascriptVar name="fixtureDefMap" value="${form.fixtureDefMap}" />
<r:setJavascriptVar name="fixtures_size" value="${form.fixtures_size}" />

/** Create rnTable object for this page */
var tblObj = new rnTable(
  'tblObj',                                   // the name of this variable
  fixtures_size+1,    // index of table row containing 'new record' HTML (i.e. rowcount + header rows - footer rows)
  fixtures_size,    // key of final record (will be incremented when creating new records)
  'fixtures',                                 // serverside table id (included in name attributes) 
  'id',                                    // table key field 
  'entryTable',                               // clientside table id
  'mainForm',                                 // enclosing clientside form id
  'Are you sure you wish to delete this fixture ?',
    new Array(
      'id', 'fixtureDefId', 'name', 'dmxOffset','x', 'y', 'z',
      'lookingAtX', 'lookingAtY', 'lookingAtZ', 'upX', 'upY', 'upZ')
);

function rfInputChange(e) {
	//var tgtEl = e.findElement();
	//alert("changed name='" + tgtEl.getAttribute("name") + "', id='" + tgtEl.getAttribute("id") + "'");
	var formEl = $("rfForm");
	formEl.submit();
}
function rfDmxAllocationChange(e) {
	var formEl = $("rfForm");
	var rfDmxAllocation = Form.getInputs('rfForm','radio','rfDmxAllocation').find(function(radio) { return radio.checked; }).value;
	//alert("I detect a disturbance in the form " + rfDmxAllocation);
	$("rfDmxAllocationLoop").style.display = (rfDmxAllocation=="loop" ? "table-row" : "none");
	$("rfDmxAllocationGrid").style.display = (rfDmxAllocation=="grid" ? "table-row" : "none");
	$("rfDmxAllocationCsv").style.display = (rfDmxAllocation=="csv" ? "table-row" : "none");
	$$('.rfDmxCalcRow').each(function(el){el.style.display = (rfDmxAllocation=="calculated" ? "table-row" : "none"); });

	// if we calculated locations, then we lose the previously set allocation 
	if (rfDmxAllocation=="calculated") {
		$("rfCsv").setAttribute("disabled", "true");
		formEl.removeAttribute("enctype");
		//Form.getInputs('rfForm','radio','rfDmxLoop').each(function(radio) { radio.checked=false; });
		//Form.getInputs('rfForm','radio','rfDmxGrid').each(function(radio) { radio.checked=false; });
	} else if (rfDmxAllocation=="loop") {
		$("rfCsv").setAttribute("disabled", "true");
		formEl.removeAttribute("enctype");
		rfDmxLoopChange();
	} else if (rfDmxAllocation=="grid") {
		$("rfCsv").setAttribute("disabled", "true");
		formEl.removeAttribute("enctype");
		rfDmxGridChange();
	} else if (rfDmxAllocation=="csv") {
		$("rfCsv").removeAttribute("disabled");
		formEl.setAttribute("enctype", "multipart/form-data");
	}
	// $$ this
	// $("rfDmxAllocationCsv").display = (rfDmxAllocation=="csv" ? "table-row" : "none");
}
function rfDmxLoopChange(e) {
	var formEl = $("rfForm");
	var rfFixtureDefId = new Number(formEl["rfFixtureDefId"].value).floor();
	var countX = new Number(formEl["rfCountX"].value).floor();
	var countY = new Number(formEl["rfCountY"].value).floor();
	var dmxOffsetGap = new Number(formEl["rfDmxOffsetGap"].value).floor();
	var numChannels = fixtureDefMap[rfFixtureDefId].dmxChannels + dmxOffsetGap;
	var startUniverse = new Number(formEl["rfDmxUniverseStart"].value).floor();
	var startOffset = new Number(formEl["rfDmxOffsetStart"].value).floor();
	var loopTypeEl = Form.getInputs('rfForm','radio','rfDmxLoop').find(function(radio) { return radio.checked; });
	var loopType = (loopTypeEl==null) ? null : loopTypeEl.value;
	var offsetFunc = "";
	if (loopType=="loop-rd") { // alt left-to-right, down
		offsetFunc = "y * " + countX + " + iif(y % 2 == 0, x, " + countX + " - 1 - x)";
	} else if (loopType=="loop-ld") { // alt right-to-left, down
		offsetFunc = "y * " + countX + " + iif(y % 2 == 1, x, " + countX + " - 1 - x)";
	} else if (loopType=="loop-ru") { // alt left-to-right, up
		offsetFunc = "(" + countY + " - 1 - y) * " + countX + " + iif(y % 2 == (" + countY + "-1) % 2, x, " + countX + " - 1 - x)";
	} else if (loopType=="loop-lu") { // alt right-to-left, up
		offsetFunc = "(" + countY + " - 1 - y) * " + countX + " + iif(y % 2 == 1 - ((" + countY + "-1) % 2), x, " + countX + " - 1 - x)";
	} else {
		alert("Unknown loopType '" + loopType + "'");
	}
	formEl["rfPanelX"].value="x * 10";
	formEl["rfPanelY"].value="y * 10";
	formEl["rfDmxOffsetCalc"].value="fillDmxOffset(" + startUniverse + ", " + startOffset + ", " + numChannels + 
	  ", " + offsetFunc + " )";
	formEl["rfDmxUniverseCalc"].value="fillDmxUniverse(" + startUniverse + ", " + startOffset + ", " + numChannels + 
	  ", " + offsetFunc + " )";
	rfInputChange();
}
function rfDmxGridChange(e) {
	var formEl = $("rfForm");
	var rfFixtureDefId = new Number(formEl["rfFixtureDefId"].value).floor();
	var countX = new Number(formEl["rfCountX"].value).floor();
	var countY = new Number(formEl["rfCountY"].value).floor();
	var dmxOffsetGap = new Number(formEl["rfDmxOffsetGap"].value).floor();
	var numChannels = fixtureDefMap[rfFixtureDefId].dmxChannels + dmxOffsetGap;
	var startUniverse = new Number(formEl["rfDmxUniverseStart"].value).floor();
	var startOffset = new Number(formEl["rfDmxOffsetStart"].value).floor();
	var loopTypeEl = Form.getInputs('rfForm','radio','rfDmxGrid').find(function(radio) { return radio.checked; });
	var loopType = (loopTypeEl==null) ? null : loopTypeEl.value;
	var offsetFunc = "";
	if (loopType=="grid-lr-tb") { // left to right, top to bottom
		offsetFunc = "y * " + countX + " + x"
	} else if (loopType=="grid-lr-bt") { // left to right, bottom to top
		offsetFunc = "(" + countY + " - 1 - y) * " + countX + " + x";
	} else if (loopType=="grid-rl-tb") { // right to left, top to bottom
		offsetFunc = "y * " + countX + " + " + countX + " - 1 - x"
	} else if (loopType=="grid-rl-bt") { // right to left, bottom to top 
		offsetFunc = "(" + countY + " - 1 - y) * " + countX + " + " + countX + " - 1 - x";
	} else {
		alert("Unknown loopType '" + loopType + "'");
	}
	formEl["rfPanelX"].value="x * 10";
	formEl["rfPanelY"].value="y * 10";
	formEl["rfDmxOffsetCalc"].value="fillDmxOffset(" + startUniverse + ", " + startOffset + ", " + numChannels + 
	  ", " + offsetFunc + " )";
	formEl["rfDmxUniverseCalc"].value="fillDmxUniverse(" + startUniverse + ", " + startOffset + ", " + numChannels + 
	  ", " + offsetFunc + " )";
	rfInputChange();
}

function rfUpdatePreview(json) {
	var previewContainerEl = $("rfPreviewContainer");
	var html = "";
	for (var y=0; y<json.rows.length; y++) {
		var row=json.rows[y];
		html += '<div style="width:' + (row.length * 150) + 'px;">';
		for (var x=0;x<row.length; x++) {
			var cell=row[x];
			html += "<div class=\"rfPreview\">" + 
			  "<div class=\"rfPreviewName\">" + cell.name + "</div>" + 
			  cell.offset + "<br/>" + 
			  (cell.panel ? "Panel: " + cell.panel + "<br/>" : "" ) +
			  (cell.position ? "Position: " + cell.position + "<br/>" : "" ) +
			  (cell.lookingAt ? "Looking at: " + cell.lookingAt + "<br/>" : "" ) +
			  (cell.up ? "Up: " + cell.up : "" ) +
			  "</div>";
		}
		html += "</div>";
	}
	previewContainerEl.update(html);
}
function edtInitPanel() {
    Event.observe($("edtSubmit"), 'click', edtSubmitClick);
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
    Event.observe($("lhsRepeat"), 'click', lhsRepeat);
    Event.observe($("rfOKButton"), 'click', rfOKButtonClick);
    Event.observe($("rfCancelButton"), 'click', rfCancelButtonClick);
    $$('.rfInput').each(function(el){Event.observe(el,'change',rfInputChange)});
    $$('input[name="rfDmxAllocation"]').each(function(el){Event.observe(el,'change',rfDmxAllocationChange)});
    $$('input[name="rfDmxLoop"]').each(function(el){Event.observe(el,'change',rfDmxLoopChange)});
    $$('input[name="rfDmxGrid"]').each(function(el){Event.observe(el,'change',rfDmxGridChange)});
    for (var i = 0; i < fixtures_size; i++) {
    	edtUpdateDmxOffset(i);
    }
    rfDmxAllocationChange();
    rfInputChange();
}
function edtUpdateDmxOffset(rowId) {
	var trEl = document.getElementById("rowid." + rowId);
	var imgEl = document.getElementById("fixtures[" + rowId + "].htmlImg16");
    var fixtureId = new Number(document.forms[0].elements["fixtures[" + rowId + "].fixtureDefId"].value).floor();
    var dmxOffset = new Number(document.forms[0].elements["fixtures[" + rowId + "].dmxOffset"].value).floor();
    var dmxFinish = fixtureDefMap[fixtureId].dmxChannels + dmxOffset - 1;
    trEl.getElementsByTagName("TD")[5].innerHTML = dmxFinish;
    imgEl.setAttribute("src", "image/fixture/" + fixtureId + "/" + fixtureDefMap[fixtureId].htmlImg16);
    // alert("Updating row " + rowId + " to " + dmxFinish);    
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
function lhsRepeat() {
	$("repeatFixtureDiv").style.display = "block";
}
function rfOKButtonClick() {
	$("repeatFixtureDiv").style.display = "none";
}
function rfCancelButtonClick() {
	$("repeatFixtureDiv").style.display = "none";
}
function initWindow() {
	initRnTable(tblObj);
    edtInitPanel();
}


</script>
</head>



<body onload="initWindow()" onunload="formUnloadCheck('mainForm')">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Fixture config</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/back.png" title="Back"/><div class="lhsMenuText">Back</div></div>
  <div id="lhsRepeat" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsFixtureRepeat.png" title="Repeat Fixture"/><div class="lhsMenuText2">Repeat Fixture</div></div>
  <div id="lhsOK" class="lhsMenuItemGreen"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="OK"/><div class="lhsMenuText">OK</div></div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">



	<jsp:include page="/misc/errorHeader.jsp" />
	<form id="mainForm" name="mainForm" method="post" action="maintainFixture.html">
	    <input type="hidden" name="action" value="maintain" /> 

  <table border="0" cellpadding="1" cellspacing="1" id="entryTable">
      <tr>
        <td colspan="3"></td>
        <td colspan="3" class="formHeader" style="background-color: #000052" width="90px">DMX offset <img src="image/help-icon.png" align="right" title="Starting DMX channel for this fixture" /></td>
        <td rowspan="2" class="formHeader" style="background-color: #000052; vertical-align: bottom;">Sort<br/>order <img src="image/help-icon.png" align="right" title="Order in which this fixture will appear on the 'Fixtures' panel" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Fixture panel <img src="image/help-icon.png" align="right" title="Display settings for this fixture on the fixture panel (in pixels)" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Position <img src="image/help-icon.png" align="right" title="The location of the fixture" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Looking at Position <img src="image/help-icon.png" align="right" title="A point that this fixture is looking towards (in it's initial state)" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Up vector <img src="image/help-icon.png" align="right" title="The direction of up, with the fixture at local co-ordinates (0,0,0)" /></td>
      </tr>
      <tr valign="bottom"> 
       <td class="formHeader">&nbsp;</td>
       <td class="formHeader" style="background-color: #000052">Fixture type <img src="image/help-icon.png" align="right" title="Fixture definition type" /></td>
       <td class="formHeader" style="background-color: #000052">Name <img src="image/help-icon.png" align="right" title="The name that will appear on the 'Fixtures' panel" /></td>
       <td class="formHeader" style="background-color: #000052">Universe</td>
       <td class="formHeader" style="background-color: #000052">Start</td>
       <td class="formHeader" style="background-color: #000052">Finish</td>
       <!-- sort order -->
       <td class="formHeader" style="background-color: #000052">Display type</td>
       <td class="formHeader" style="background-color: #000052">X</td>
       <td class="formHeader" style="background-color: #000052">Y</td>
       <td class="formHeader" style="background-color: #000052">X</td>
       <td class="formHeader" style="background-color: #000052">Y</td>
       <td class="formHeader" style="background-color: #000052">Z</td>
       <td class="formHeader" style="background-color: #000052">X</td>
       <td class="formHeader" style="background-color: #000052">Y</td>
       <td class="formHeader" style="background-color: #000052">Z</td>
       <td class="formHeader" style="background-color: #000052">X</td>
       <td class="formHeader" style="background-color: #000052">Y</td>
       <td class="formHeader" style="background-color: #000052">Z</td>
   </tr>
      <c:forEach var="rowData" varStatus="rowStatus" items="${form.fixtures}" > 
                   <c:choose> <c:when test="${rowData.cmdDelete == 'Y'}"> 
                       <tr id="rowid.<c:out value='${rowStatus.index}'/>"> 
                           <td height="31"> 
                               <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>">   
                               <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].cmdDelete" value="Y"> 
                           </td>
                           <td colspan="6" bgcolor="#FFAAAA"><div style="margin:5px; color: black;"><i>This row has been marked for deletion. 
                           Correct the other validation errors on this page to delete.</i></div></td>
                       </tr>
                   </c:when>

                   <%-- if the row has validation errors or if the row is a new row (the data hasn't been committed to the database --%>       
                   <c:when test="${rowData.isInvalid == 'Y' || rowData.cmdUpdate == 'N'}"> 
                       <%-- New row with validation errors --%>
                       <tr id="rowid.<c:out value='${rowStatus.index}'/>"> 
                           
                              <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${rowStatus.index}'/>,0); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                                  <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].cmdUpdate" value="<c:out value='${rowData.cmdUpdate}'/>">
                                  <%--  <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>"> --%>
                              </td>
                              <td nowrap class="<r:onError name='fixtures[${rowStatus.index}].fixtureDefId' text='errorBg' />">
                                  <img id="<c:out value='fixtures[${rowStatus.index}].htmlImg16'/>" src=""/> 
                                  <r:select data="${form.fixtureDefs}" name="fixtures[${rowStatus.index}].fixtureDefId" value="${rowData.fixtureDefId}" displayColumn="name" valueColumn="id" firstOption="(please select...)" onchange="edtUpdateDmxOffset(${rowStatus.index})"/>
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                  <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="20">
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].universeNumber' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].universeNumber" value="<c:out value='${rowData.universeNumber}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].dmxOffset' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].dmxOffset" value="<c:out value='${rowData.dmxOffset}'/>" onchange="edtUpdateDmxOffset(<c:out value='${rowStatus.index}'/>)">
                              </td>
                              <td align="right"></td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].sortOrder' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].sortOrder" value="<c:out value='${rowData.sortOrder}'/>">
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixPanelType' text='errorBg' />"> 
                                  <r:select data="${form.fixPanelTypes}" name="fixtures[${rowStatus.index}].fixPanelType" value="${rowData.fixPanelType}" displayColumn="name" valueColumn="id"  />
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixPanelX' text='errorBg' />"> 
                                  <input type="text" class="smallInput2 formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].fixPanelX" value="<c:out value='${rowData.fixPanelX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixPanelY' text='errorBg' />"> 
                                  <input type="text" class="smallInput2 formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].fixPanelY" value="<c:out value='${rowData.fixPanelY}'/>" >
                              </td>

                              <td class="<r:onError name='fixtures[${rowStatus.index}].x' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].x" value="<c:out value='${rowData.x}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].y' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].y" value="<c:out value='${rowData.y}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].z' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].z" value="<c:out value='${rowData.z}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtX" value="<c:out value='${rowData.lookingAtX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtY" value="<c:out value='${rowData.lookingAtY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtZ" value="<c:out value='${rowData.lookingAtZ}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].upX" value="<c:out value='${rowData.upX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].upY" value="<c:out value='${rowData.upY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].upZ" value="<c:out value='${rowData.upZ}'/>" >
                              </td>
                              
                          </tr>
                   </c:when>
                          
                   <c:otherwise> 
                       <!--- prefilled row --->
                       <tr id="rowid.<c:out value='${rowStatus.index}'/>"> 
                           <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${rowStatus.index}'/>,1); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                               <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>">
                               <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].cmdUpdate" value="Y">                
                           </td>
                              <td nowrap class="<r:onError name='fixtures[${rowStatus.index}].fixtureDefId' text='errorBg' />"> 
                                  <img id="<c:out value='fixtures[${rowStatus.index}].htmlImg16'/>" src=""/>
                                  <r:select data="${form.fixtureDefs}" name="fixtures[${rowStatus.index}].fixtureDefId" value="${rowData.fixtureDefId}" displayColumn="name" valueColumn="id" firstOption="(please select...)" onchange="edtUpdateDmxOffset(${rowStatus.index})"/>
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                  <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="20">
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].universeNumber' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].universeNumber" value="<c:out value='${rowData.universeNumber}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].dmxOffset' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].dmxOffset" value="<c:out value='${rowData.dmxOffset}'/>" onchange="edtUpdateDmxOffset(<c:out value='${rowStatus.index}'/>)">
                              </td>
                              <td align="right"></td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].sortOrder' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].sortOrder" value="<c:out value='${rowData.sortOrder}'/>">
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixPanelType' text='errorBg' />"> 
                                  <r:select data="${form.fixPanelTypes}" name="fixtures[${rowStatus.index}].fixPanelType" value="${rowData.fixPanelType}" displayColumn="name" valueColumn="id"  />
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixPanelX' text='errorBg' />"> 
                                  <input type="text" class="smallInput2 formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].fixPanelX" value="<c:out value='${rowData.fixPanelX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixPanelY' text='errorBg' />"> 
                                  <input type="text" class="smallInput2 formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].fixPanelY" value="<c:out value='${rowData.fixPanelY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].x' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].x" value="<c:out value='${rowData.x}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].y' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].y" value="<c:out value='${rowData.y}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].z' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].z" value="<c:out value='${rowData.z}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtX" value="<c:out value='${rowData.lookingAtX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtY" value="<c:out value='${rowData.lookingAtY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtZ" value="<c:out value='${rowData.lookingAtZ}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].upX" value="<c:out value='${rowData.upX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].upY" value="<c:out value='${rowData.upY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${rowStatus.index}'/>].upZ" value="<c:out value='${rowData.upZ}'/>" >
                              </td>
                              
                       </tr>
                   </c:otherwise></c:choose> 
               </c:forEach> 
 
               <%-- Empty row --%>
               <tr id="rowid.<c:out value='${form.fixtures_size}'/>"> 
                   <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${form.fixtures_size}'/>,0); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                       <input type="hidden" name="fixtures[<c:out value='${form.fixtures_size}'/>].cmdUpdate" value="N">
                   </td>
                   <td><r:select data="${form.fixtureDefs}" name="fixtures[${form.fixtures_size}].fixtureDefId" value="" displayColumn="name" valueColumn="id" firstOption="(please select...)"/></td>
                   <td><input type="text" class="formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].name" value="" size="20"></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].universeNumber" value="1" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].dmxOffset" value="" ></td>
                   <td></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].sortOrder" value="" ></td>
                   <td><r:select data="${form.fixPanelTypes}" name="fixtures[${form.fixtures_size}].fixPanelType" value="" displayColumn="name" valueColumn="id" /></td>
                   <td><input type="text" class="smallInput2 formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].fixPanelX" value="" ></td>
                   <td><input type="text" class="smallInput2 formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].fixPanelY" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].x" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].y" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].z" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].lookingAtX" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].lookingAtY" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].lookingAtZ" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].upX" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].upY" value="" ></td>
                   <td><input type="text" class="smallInput formfield rj" name="fixtures[<c:out value='${form.fixtures_size}' />].upZ" value="" ></td>
               </tr>
               <tr> 
                   <td> <div class="greenrollover"> <a href="javascript:void(0)" onclick="fnAddRow(tblObj); return 0;"><img src="image/add-icon.gif" width="18" height="17" border="0"></a></div></td>
                   <td colspan="4">&nbsp;Add row</td>
               </tr>
 
               <tr align="left"> 
                   <td colspan="5">
                       <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="Update"/><div class="lhsMenuText">Update</div></div>
                   </td>
               </tr>
               <tr> 
                   <td colspan="5" align="right">&nbsp; </td>
               </tr>
           </table>
       </form>
</div>

<div id="repeatFixtureDiv" style="position: absolute; z-order:10; top:100px; left:100px; background-color: white;">
<form id="rfForm" method="post" action="maintainFixture2.html" target="rfPreviewUpdateFrame"> 
<input type="hidden" name="action" value="rfPreview" />
<table id="repeatFixtureTable" style="width:400px;">
<col style="width: 140px;">
<col style="width: 30px;">
<col style="width: 230px;">
<tr><td class="rfTitle1" colspan="3">Repeating fixtures</td></tr>
<tr><td colspan="2">Type of fixture:</td><td><r:select data="${form.fixtureDefs}" name="rfFixtureDefId" value="" displayColumn="name" valueColumn="id" /></td></tr>
<tr><td colspan="2">Number of fixtures:</td><td><input class="rfInput" style="width: 30px;" type="text" name="rfCountX" value="1" /> x <input class="rfInput" style="width:30px;" type="text" name="rfCountY" value="1" /> columns (<span style="font-family: Courier New; font-size: 8pt;">x</span>) x rows (<span style="font-family: Courier New; font-size: 8pt;">y</span>)</td></tr>
<tr><td colspan="2">Names of fixtures:</td><td><input class="rfInput" type="text" name="rfName" value="something-{x}-{y}" /></td></tr>
<tr><td colspan="2">Starting universe:</td><td><input class="rfInput" type="text" name="rfDmxUniverseStart" value="1" /></td></tr>
<tr><td colspan="2">Starting DMX offset:</td><td><input class="rfInput" type="text" name="rfDmxOffsetStart" value="1" /></td></tr>
<tr><td colspan="2">DMX offset gap between fixtures:</td><td><input class="rfInput" type="text" name="rfDmxOffsetGap" value="0" /></td></tr>
<tr><td colspan="2">DMX offset allocation:</td><td>
  <input type="radio" name="rfDmxAllocation" value="grid" checked /> Grid<br/>
  <input type="radio" name="rfDmxAllocation" value="loop" /> Looped<br/>
  <input type="radio" name="rfDmxAllocation" value="csv"/> CSV<br/>
  <input type="radio" name="rfDmxAllocation" value="calculated"/> Calculated<br/>
  </td>
<tr id="rfDmxAllocationLoop" style="display: none;"><td colspan="2"></td><td>
  <div style="margin-top: 10px; height: 40px;">
  <span style="float:left;"><input class="" type="radio" name="rfDmxLoop" value="loop-rd" checked/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/loop-rd.png"/></span>Loop alternating left-to-right, then right-to-left, downwards<br/>
  </div><div style="height: 40px; clear:left;">
  <span style="float:left; clear: left;"><input class="" type="radio" name="rfDmxLoop" value="loop-ld"/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/loop-ld.png"/></span>Loop alternating right-to-left, then left-to-right, downwards<br/>
  </div><div style="height: 40px; clear:left;">
  <span style="float:left; clear: left;"><input class="" type="radio" name="rfDmxLoop" value="loop-ru"/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/loop-ru.png"/></span>Loop alternating left-to-right, then right-to-left, upwards<br/>
  </div><div style="height: 40px; clear:left;">
  <span style="float:left; clear: left;"><input class="" type="radio" name="rfDmxLoop" value="loop-ld"/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/loop-lu.png"/></span>Loop alternating right-to-left, then left-to-right, upwards<br/>
  </div>
</td>
</tr>

<tr id="rfDmxAllocationGrid" style="display: none;"><td colspan="2"></td><td>
  <div style="margin-top: 10px; height: 40px;">
  <span style="float:left;"><input class="" type="radio" name="rfDmxGrid" value="grid-lr-tb" checked/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/lr-td2.png"/></span>Grid in rows, left to right, top to bottom<br/>
  </div><div style="height: 40px; clear:left;">
  <span style="float:left; clear: left;"><input class="" type="radio" name="rfDmxGrid" value="grid-lr-bt"/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/lr-bu2.png"/></span>Grid in rows, left to right, bottom to top<br/>
  </div><div style="height: 40px; clear:left;">
  <span style="float:left; clear: left;"><input class="" type="radio" name="rfDmxGrid" value="grid-rl-tb"/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/rl-td2.png"/></span>Grid in rows, right to left, top to bottom<br/>
  </div><div style="height: 40px; clear:left;">
  <span style="float:left; clear: left;"><input class="" type="radio" name="rfDmxGrid" value="grid-rl-bt"/></span> 
  <span style="float:left; margin-right:5px;"><img src="image/config/rl-bu2.png"/></span>Grid in rows, right to left, bottom to top<br/>
  </div>
</td>
</tr>

<tr id="rfDmxAllocationCsv" style="display: none;"><td colspan="2"></td><td>
  <div style="margin-top: 10px; height: 40px;">
  Upload file in CSV format:
  <br/><input class="" type="file" name="rfCsv" id="rfCsv" disabled="true"/>
  <br/><br/>CSV columns are:
  fixtureName, universe, dmxOffset, fixturePanelX, fixturePanelY,
  position3d_x, position3d_y, position3d_z
  lookingAt3d_x, lookingAt3d_y, lookingAt3d_z,
  upVector3d_x, upVector3d_y, upVector3d_z
  </div>
</td>
</tr>
  
<%-- <r:select data="${rfDmxAllocations}" name="rfDmxAllocation" value="" displayColumn="name" valueColumn="id" /></td></tr> --%>
<tr class="rfDmxCalcRow"><td colspan="2">DMX universe</td><td><input class="rfInput" type="text" name="rfDmxUniverseCalc" value="1" /></td></tr>
<tr class="rfDmxCalcRow"><td colspan="2">DMX offset</td><td><input class="rfInput" type="text" name="rfDmxOffsetCalc" value="1" /></td></tr>
<tr class="rfDmxCalcRow"><td rowspan="2">Fixture panel <img src="image/help-icon.png" title="Display settings for this fixture on the fixture panel (in pixels)" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfPanelX" value="x * 10" /></td></tr>
<tr class="rfDmxCalcRow"><td>Y:</td><td><input class="rfInput" type="text" name="rfPanelY" value="y * 10" /></td></tr>
<tr class="rfDmxCalcRow"><td rowspan="3">3D position <img src="image/help-icon.png" title="The location of the fixture" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfPositionX" value="x * 10" /></td></tr>
<tr class="rfDmxCalcRow"><td>Y:</td><td><input class="rfInput" type="text" name="rfPositionY" value="y * 10" /></td></tr>
<tr class="rfDmxCalcRow"><td>Z:</td><td><input class="rfInput" type="text" name="rfPositionZ" value="1" /></td></tr>
<tr class="rfDmxCalcRow"><td rowspan="3">3D looking at position <img src="image/help-icon.png" title="A point that this fixture is looking towards (in it's initial state)" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfLookingAtX" value="x + 1" /></td></tr>
<tr class="rfDmxCalcRow"><td>Y:</td><td><input class="rfInput" type="text" name="rfLookingAtY" value="y" /></td></tr>
<tr class="rfDmxCalcRow"><td>Z:</td><td><input class="rfInput" type="text" name="rfLookingAtZ" value="0" /></td></tr>
<tr class="rfDmxCalcRow"><td rowspan="3">3D up vector <img src="image/help-icon.png" title="The direction of up, with the fixture at local co-ordinates (0,0,0)" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfUpX" value="0" /></td></tr>
<tr class="rfDmxCalcRow"><td>Y:</td><td><input class="rfInput" type="text" name="rfUpY" value="0" /></td></tr>
<tr class="rfDmxCalcRow"><td>Z:</td><td><input class="rfInput" type="text" name="rfUpZ" value="1" /></td></tr>

<tr ><td colspan="2"></td><td><input type="button" id="rfOKButton" name="rfOKButton" value="OK" /> <input type="button" id="rfCancelButton" name="rfCancelButton" value="Cancel" /></td></tr>
</table>

<div class="rfTitle2">Preview</div>
<div id="rfPreviewContainer">
<div class="rfPreview"><div class="rfPreviewName">something-1-1</div>u1-offset 1</div>
<div class="rfPreview"><div class="rfPreviewName">something-1-2</div>5</div>
<div class="rfPreview"><div class="rfPreviewName">something-1-3</div>9</div>
<br/>
<div class="rfPreview"><div class="rfPreviewName">something-2-1</div>13</div>
<div class="rfPreview"><div class="rfPreviewName">something-2-2</div>17</div>
<div class="rfPreview"><div class="rfPreviewName">something-2-3</div>21</div>
<br/>
<div class="rfPreview"><div class="rfPreviewName">something-3-1</div>25</div>
<div class="rfPreview"><div class="rfPreviewName">something-3-2</div>29</div>
<div class="rfPreview"><div class="rfPreviewName">something-3-3</div>33</div>
</div>
</form>
</div>

<iframe id="rfPreviewUpdateFrame" name="rfPreviewUpdateFrame" src="about:blank"></iframe>
</body>
</html>
