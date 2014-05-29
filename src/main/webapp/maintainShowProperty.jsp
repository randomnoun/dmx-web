<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common" prefix="r" %>
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
    
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> Maintain Show Properties</title>
     
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
</style>

<script>
/** Create rnTable object for this page */
var tblObj = new rnTable(
  'tblObj',                                   // the name of this variable
  <c:out value="${form.showProperties_size}" />,    // index of table row containing 'new record' HTML (i.e. rowcount + header rows - footer rows)
  <c:out value="${form.showProperties_size}" />,    // key of final record (will be incremented when creating new records)
  'showProperties',                                 // serverside table id (included in name attributes) 
  'id',                                    // table key field 
  'entryTable',                               // clientside table id
  'mainForm',                                 // enclosing clientside form id
  'Are you sure you wish to delete this show property ?',
    new Array(
      'id', 'key', 'value')
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
function lhsCancelClick() { document.location = "maintainShow.html"; }
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
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Show properties</span></div>
<div class="lhsMenuContainer">
  <div id="lhsCancel" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/back.png" title="Back"/><div class="lhsMenuText">Back</div></div>
  <div id="lhsOK" class="lhsMenuItemGreen"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="OK"/><div class="lhsMenuText">OK</div></div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">



<jsp:include page="/misc/errorHeader.jsp" />
<form id="mainForm" name="mainForm" method="post" action="maintainShowProperty.html">
    <input type="hidden" name="action" value="maintain" />
    <input type="hidden" name="showId" value="<c:out value='${showId}'/>" /> 
<table border="0" cellpadding="1" cellspacing="1" id="entryTable">
    <tr valign="bottom"> 
    <td class="formHeader">&nbsp;</td>
    <td class="formHeader" style="background-color: #000052">Key</td>
    <td class="formHeader" style="background-color: #000052">Value</td>
</tr>
   <c:forEach var="rowData" varStatus="rowStatus" items="${form.showProperties}" > 
                <c:choose> <c:when test="${rowData.cmdDelete == 'Y'}"> 
                    <tr id="rowid.<c:out value='${rowStatus.index}'/>"> 
                        <td height="31"> 
                            <input type="hidden" name="showProperties[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>">   
                            <input type="hidden" name="showProperties[<c:out value='${rowStatus.index}'/>].cmdDelete" value="Y"> 
                        </td>
                        <td colspan="3" bgcolor="#FFAAAA"><div style="margin:5px; color: black;"><i>This row has been marked for deletion. 
                        Correct the other validation errors on this page to delete.</i></div></td>
                    </tr>
                </c:when>

                <%-- if the row has validation errors or if the row is a new row (the data hasn't been committed to the database --%>       
                <c:when test="${rowData.isInvalid == 'Y' || rowData.cmdUpdate == 'N'}"> 
                    <%-- New row with validation errors --%>
                    <tr id="rowid.<c:out value='${rowStatus.index}'/>"> 
                        
                           <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${rowStatus.index}'/>,0); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                               <input type="hidden" name="showProperties[<c:out value='${rowStatus.index}'/>].cmdUpdate" value="<c:out value='${rowData.cmdUpdate}'/>">
                               <%--  <input type="hidden" name="showProperties[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>"> --%>
                           </td>
                           <td class="<r:onError name='showProperties[${rowStatus.index}].key' text='errorBg' />"> 
                               <input type="text" class="formfield" name="showProperties[<c:out value='${rowStatus.index}'/>].key" value="<c:out value='${rowData.key}'/>" size="30" />
                               <c:if test="${rowData.description!=null}">
                               <img src="image/help-icon.png" align="right" title="<c:out value='${rowData.description}'/>"/>
                               </c:if>
                           </td>    
                           <td class="<r:onError name='showProperties[${rowStatus.index}].value' text='errorBg' />"> 
                               <input type="text" class="formfield" name="showProperties[<c:out value='${rowStatus.index}'/>].value" value="<c:out value='${rowData.value}'/>" size="50" />
                           </td>    
                       </tr>
                </c:when>
                       
                <c:otherwise> 
                    <!--- prefilled row --->
                    <tr id="rowid.<c:out value='${rowStatus.index}'/>"> 
                        <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${rowStatus.index}'/>,1); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                            <input type="hidden" name="showProperties[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>">
                            <input type="hidden" name="showProperties[<c:out value='${rowStatus.index}'/>].cmdUpdate" value="Y">                
                        </td>
                           <td class="<r:onError name='showProperties[${rowStatus.index}].key' text='errorBg' />"> 
                               <input type="text" class="formfield" name="showProperties[<c:out value='${rowStatus.index}'/>].key" value="<c:out value='${rowData.key}'/>" size="30"/>
                           <c:if test="${rowData.description!=null}">
                           <img src="image/help-icon.png" align="right" title="<c:out value='${rowData.description}'/>"/>
                           </c:if>
                           </td>
                           <td class="<r:onError name='showProperties[${rowStatus.index}].value' text='errorBg' />"> 
                               <input type="text" class="formfield" name="showProperties[<c:out value='${rowStatus.index}'/>].value" value="<c:out value='${rowData.value}'/>" size="50"/>
                           </td>
                    </tr>
                </c:otherwise></c:choose> 
            </c:forEach> 

            <%-- Empty row --%>
            <tr id="rowid.<c:out value='${form.showProperties_size}'/>"> 
                <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${form.showProperties_size}'/>,0); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                    <input type="hidden" name="showProperties[<c:out value='${form.showProperties_size}'/>].cmdUpdate" value="N">
                </td>
                <td><input type="text" class="formfield" name="showProperties[<c:out value='${form.showProperties_size}' />].key" value="" size="30"></td>
                <td><input type="text" class="formfield" name="showProperties[<c:out value='${form.showProperties_size}' />].value" value="" size="50"></td>
            </tr>
            <tr> 
                <td> <div class="greenrollover"> <a href="javascript:void(0)" onclick="fnAddRow(tblObj); return 0;"><img src="image/add-icon.gif" width="18" height="17" border="0"></a></div></td>
                <td colspan="2">&nbsp;Add row</td>
            </tr>

            <tr align="left"> 
                <td colspan="5" >
                       <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="Update"/><div class="lhsMenuText">Update</div></div>
                </td>
            </tr>
            <tr> 
                <td colspan="5" align="right">&nbsp; </td>
            </tr>
        </table>
</form>
<jsp:include page="/misc/analytics.jsp" />
</body>
</html>
