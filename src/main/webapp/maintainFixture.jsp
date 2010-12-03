<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*"
%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<% 
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
</style>



<script>
/** Create rnTable object for this page */
var tblObj = new rnTable(
  'tblObj',                                   // the name of this variable
  <c:out value="${form.fixtures_size}" />,    // index of table row containing 'new record' HTML (i.e. rowcount + header rows - footer rows)
  <c:out value="${form.fixtures_size}" />,    // key of final record (will be incremented when creating new records)
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
    var edtSubmitEl = $("edtSubmit");
    Event.observe(edtSubmitEl, 'click', edtSubmitClick);
    Event.observe($("lhsCancel"), 'click', lhsCancelClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
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
	initRnTable(tblObj);
    edtInitPanel();
}


</script>
</head>



<body onload="initWindow()" onunload="formUnloadCheck('mainForm')">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Fixture config</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem">Back</div>
  <div id="lhsOK" class="lhsMenuItemGreen">OK</div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">



	<jsp:include page="/misc/errorHeader.jsp" />
	<form id="mainForm" name="mainForm" method="post" action="maintainFixture.html">
	    <input type="hidden" name="action" value="maintain" /> 

  <table border="0" cellpadding="1" cellspacing="1" id="entryTable">
      <tr>
        <td colspan="4"></td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Position</td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Looking at Position</td>
        <td colspan="3" class="formHeader" style="background-color: #000052">Up vector</td>
      </tr>
      <tr valign="bottom"> 
       <td class="formHeader">&nbsp;</td>
       <td class="formHeader" style="background-color: #000052">Fixture type</td>
       <td class="formHeader" style="background-color: #000052">Name</td>
       <td class="formHeader" style="background-color: #000052">DMX offset</td>
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
                                  <r:select data="${form.fixtureDefs}" name="fixtures[${rowStatus.index}].fixtureDefId" value="${rowData.fixtureDefId}" displayColumn="name" valueColumn="id" firstOption="(please select...)" />
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                  <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="20">
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].dmxOffset' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].dmxOffset" value="<c:out value='${rowData.dmxOffset}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].x' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].x" value="<c:out value='${rowData.x}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].y' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].y" value="<c:out value='${rowData.y}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].z' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].z" value="<c:out value='${rowData.z}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtX" value="<c:out value='${rowData.lookingAtX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtY' text='errorBg' />"> 
                                  <input type="text" class="fsmallInput ormfield" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtY" value="<c:out value='${rowData.lookingAtY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtZ" value="<c:out value='${rowData.lookingAtZ}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].upX" value="<c:out value='${rowData.upX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].upY" value="<c:out value='${rowData.upY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].upZ" value="<c:out value='${rowData.upZ}'/>" >
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
                                  <r:select data="${form.fixtureDefs}" name="fixtures[${rowStatus.index}].fixtureDefId" value="${rowData.fixtureDefId}" displayColumn="name" valueColumn="id" firstOption="(please select...)"/>
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                  <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="20">
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].dmxOffset' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].dmxOffset" value="<c:out value='${rowData.dmxOffset}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].x' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].x" value="<c:out value='${rowData.x}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].y' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].y" value="<c:out value='${rowData.y}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].z' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].z" value="<c:out value='${rowData.z}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtX" value="<c:out value='${rowData.lookingAtX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtY" value="<c:out value='${rowData.lookingAtY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].lookingAtZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].lookingAtZ" value="<c:out value='${rowData.lookingAtZ}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upX' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].upX" value="<c:out value='${rowData.upX}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upY' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].upY" value="<c:out value='${rowData.upY}'/>" >
                              </td>
                              <td class="<r:onError name='fixtures[${rowStatus.index}].upZ' text='errorBg' />"> 
                                  <input type="text" class="smallInput formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].upZ" value="<c:out value='${rowData.upZ}'/>" >
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
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].dmxOffset" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].x" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].y" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].z" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].lookingAtX" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].lookingAtY" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].lookingAtZ" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].upX" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].upY" value="" ></td>
                   <td><input type="text" class="smallInput formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].upZ" value="" ></td>
               </tr>
               <tr> 
                   <td> <div class="greenrollover"> <a href="javascript:void(0)" onclick="fnAddRow(tblObj); return 0;"><img src="image/add-icon.gif" width="18" height="17" border="0"></a></div></td>
                   <td colspan="4">&nbsp;Add row</td>
               </tr>
 
               <tr align="left"> 
                   <td colspan="5">
                       <div id="edtSubmit" class="edtSubmit">Update</div>
                   </td>
               </tr>
               <tr> 
                   <td colspan="5" align="right">&nbsp; </td>
               </tr>
           </table>
       </form>

</div>

</body>
</html>
