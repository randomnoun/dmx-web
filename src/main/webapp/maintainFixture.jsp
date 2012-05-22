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
  width: 800px; height: 450px; 
  padding: 10px; 
  border: solid 2px black; 
  display: none; 
}
#repeatFixtureTable TD { vertical-align: top; }
.rfInput { font-family: Courier New; font-size: 8pt; width: 120px; }
.rfTitle1 { font-size: 14pt; font-weight: bold; padding-bottom: 15px;  }
.rfTitle2 { position: absolute; font-size: 14pt; font-weight: bold; padding-bottom: 15px; left: 420px; top: 10px;}
.rfPreviewContainer { 
  position: absolute; top: 40px; left: 420px; width: 380px; height: 380px;
  overflow: scroll;  
}  
.rfPreview {
  display: inline-block;
  width: 80px; height: 30px; 
  color: #7369b5;
  background-color: #b5a6ef;
  margin: 3px; padding: 2px; 
}
.rfPreviewName {
  color: #000042;
}
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


function edtInitPanel() {
    
    Event.observe($("edtSubmit"), 'click', edtSubmitClick);
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
    Event.observe($("lhsRepeat"), 'click', lhsRepeat);
    Event.observe($("rfOKButton"), 'click', rfOKButtonClick);
    Event.observe($("rfCancelButton"), 'click', rfCancelButtonClick);
    for (var i = 0; i < fixtures_size; i++) {
    	edtUpdateDmxOffset(i);
    }
}
function edtUpdateDmxOffset(rowId) {
	var trEl = document.getElementById("rowid." + rowId);
    var fixtureId = new Number(document.forms[0].elements["fixtures[" + rowId + "].fixtureDefId"].value);
    var dmxOffset = new Number(document.forms[0].elements["fixtures[" + rowId + "].dmxOffset"].value);
    var dmxFinish = fixtureDefMap[fixtureId] + dmxOffset - 1;
    trEl.getElementsByTagName("TD")[4].innerHTML = dmxFinish;
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
        <td colspan="2" class="formHeader" style="background-color: #000052" width="90px">DMX offset <img src="image/help-icon.png" align="right" title="Starting DMX channel for this fixture" /></td>
        <td rowspan="2" class="formHeader" style="background-color: #000052; vertical-align: bottom;">Sort<br/>order <img src="image/help-icon.png" align="right" title="Order in which this fixture will appear on the 'Fixtures' panel" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Fixture panel <img src="image/help-icon.png" align="right" title="Display settings for this fixture on the fixture panel" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Position <img src="image/help-icon.png" align="right" title="The location of the fixture" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Looking at Position <img src="image/help-icon.png" align="right" title="A point that this fixture is looking towards (in it's initial state)" /></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Up vector <img src="image/help-icon.png" align="right" title="The direction of up, taking the fixture as being at co-ordinates (0,0,0)" /></td>
      </tr>
      <tr valign="bottom"> 
       <td class="formHeader">&nbsp;</td>
       <td class="formHeader" style="background-color: #000052">Fixture type <img src="image/help-icon.png" align="right" title="Fixture definition type" /></td>
       <td class="formHeader" style="background-color: #000052">Name <img src="image/help-icon.png" align="right" title="The name that will appear on the 'Fixtures' panel" /></td>
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
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixtureDefId' text='errorBg' />"> 
                                  <r:select data="${form.fixtureDefs}" name="fixtures[${rowStatus.index}].fixtureDefId" value="${rowData.fixtureDefId}" displayColumn="name" valueColumn="id" firstOption="(please select...)" onchange="edtUpdateDmxOffset(${rowStatus.index})"/>
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                  <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="20">
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
                              <td class="<r:onError name='fixtures[${rowStatus.index}].fixtureDefId' text='errorBg' />"> 
                                  <r:select data="${form.fixtureDefs}" name="fixtures[${rowStatus.index}].fixtureDefId" value="${rowData.fixtureDefId}" displayColumn="name" valueColumn="id" firstOption="(please select...)" onchange="edtUpdateDmxOffset(${rowStatus.index})"/>
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                  <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="20">
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
<table id="repeatFixtureTable" style="width:400px;">
<col style="width: 140px;">
<col style="width: 30px;">
<col style="width: 230px;">
<tr><td class="rfTitle1" colspan="3">Repeating fixtures</td></tr>
<tr><td colspan="2">Type of fixture:</td><td><r:select data="${form.fixtureDefs}" name="rfFixtureDefId" value="" displayColumn="name" valueColumn="id" firstOption="(please select...)"/></td></tr>
<tr><td colspan="2">Number of fixtures:</td><td><input class="rfInput" style="width: 30px;" type="text" name="rfCount" value="1" /> x <input class="rfInput" style="width:30px;" type="text" name="repeatFixtureCount" value="1" /> columns (<span style="font-family: Courier New; font-size: 8pt;">x</span>) x rows (<span style="font-family: Courier New; font-size: 8pt;">y</span>)</td></tr>
<tr><td colspan="2">Names of fixtures:</td><td><input class="rfInput" type="text" name="rfName" value="something-{x}-{y}" /></td></tr>
<tr><td colspan="2">Starting DMX offset:</td><td><input class="rfInput" type="text" name="rfDmxOffset" value="15" /></td></tr>
<tr><td colspan="2">DMX offset gap between fixtures:</td><td><input class="rfInput" type="text" name="rfDmxOffsetGap" value="0" /></td></tr>
<tr><td colspan="2">DMX allocation:</td><td><r:select data="${rfDmxAllocations}" name="rfDmxAllocation" value="" displayColumn="name" valueColumn="id" /></td></tr>
<tr><td rowspan="2">Fixture panel <img src="image/help-icon.png" title="Display settings for this fixture on the fixture panel" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfPanelX" value="x * 10" /></td></tr>
<tr><td>Y:</td><td><input class="rfInput" type="text" name="rfPanelY" value="y * 10" /></td></tr>
<tr><td rowspan="3">3D Position <img src="image/help-icon.png" title="The location of the fixture" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfPositionX" value="x * 10" /></td></tr>
<tr><td>Y:</td><td><input class="rfInput" type="text" name="rfPositionY" value="y * 10" /></td></tr>
<tr><td>Z:</td><td><input class="rfInput" type="text" name="rfPositionZ" value="1" /></td></tr>
<tr><td rowspan="3">3D looking at position <img src="image/help-icon.png" title="A point that this fixture is looking towards (in it's initial state)" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfLookingAtX" value="x + 1" /></td></tr>
<tr><td>Y:</td><td><input class="rfInput" type="text" name="rfLookingAtY" value="y" /></td></tr>
<tr><td>Z:</td><td><input class="rfInput" type="text" name="rfLookingAtZ" value="0" /></td></tr>
<tr><td rowspan="3">3D up vector <img src="image/help-icon.png" title="The direction of up, taking the fixture as being at co-ordinates (0,0,0)" /></td>
    <td>X:</td><td><input class="rfInput" type="text" name="rfUpX" value="0" /></td></tr>
<tr><td>Y:</td><td><input class="rfInput" type="text" name="rfUpY" value="0" /></td></tr>
<tr><td>Z:</td><td><input class="rfInput" type="text" name="rfUpZ" value="1" /></td></tr>
<tr><td colspan="2"></td><td><input type="button" id="rfOKButton" name="rfOKButton" value="OK" /> <input type="button" id="rfCancelButton" name="rfCancelButton" value="Cancel" /></td></tr>
</table>

<div class="rfTitle2">Preview</div>
<div class="rfPreviewContainer">
<div class="rfPreview"><div class="rfPreviewName">something-1-1</div>1</div>
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

</div>

</body>
</html>
