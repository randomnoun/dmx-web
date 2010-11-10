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
    
    <!-- CSS -->
    <link href="css/dmx-site.css" media="all" rel="stylesheet" type="text/css" />
    <link href="css/dmx-print.css" media="print" rel="stylesheet" type="text/css" />
    <!--[if IE]><link href="/stylesheets/ie.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    <!--[if lt IE 8]><link href="/stylesheets/ie7.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    <!--[if lt IE 7]><link href="/stylesheets/ie6.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
     
<link href="css/table-edit.css" media=all" rel="stylesheet" type="text/css" />
<script language="javascript" src="js/table-edit.js"></script>
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
  'Are you sure you wish to delete this user ?',
    new Array(
      'id', 'fixtureDefId', 'name', 'dmxOffset')
);
</script>
<style>
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
</head>
<body bgcolor=#FFFFFF text="#000000" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" 
 onload="initRnTable(tblObj);" onunload="formUnloadCheck('mainForm')">
 
<div class="wrapper">
    <div class="page" style="margin-top:0px;"> 
        <div id="controls">
            <div style="height:80px; width:880px;">
    
				<!-- TODO: save data before leaving page -->
				<div style="width:158px; height:54px; background: url(images/ui/bg-menu-small.png) no-repeat left top; float:left; background-color: blue;
				  font-size: 22px; color: white;
				  font-weight: bold; text-align: center; padding-top: 16px; margin-right:10px; cursor: pointer;"
				  onclick="document.location='index.html'"
				  >
				&lt; Back
				</div>
				<div style="width:663px; height:76px; background: url(images/ui/button_under-wide2.png) no-repeat left top; float:left;
				  font-size: 30px; color: #000052;
				  font-weight: bold; text-align: left; margin-bottom:10px; padding-left: 10px; padding-top: 10px;" 
				  id="dataEntryDiv"
				  >
				Maintain fixtures           
				</div>
			</div>
		</div>

		<div style="clear: left;">
    		<jsp:include page="/misc/errorHeader.jsp" />
			<form id="mainForm" name="mainForm" method="post" action="maintainFixture.html">
			    <input type="hidden" name="action" value="maintain" /> 
				<div align="center" style="padding-left: 5px">
				    <table border="0" cellpadding="1" cellspacing="1" id="entryTable">
				        <tr valign="bottom"> 
					        <td class="formHeader">&nbsp;</td>
					        <td class="formHeader" style="background-color: #000052">Fixture type</td>
					        <td class="formHeader" style="background-color: #000052">Name</td>
					        <td class="formHeader" style="background-color: #000052">DMX offset</td>
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
	                                
                                    <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${rowStatus.index}'/>,1); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
                                        <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].cmdUpdate" value="<c:out value='${rowData.cmdUpdate}'/>">
                                        <%--  <input type="hidden" name="fixtures[<c:out value='${rowStatus.index}'/>].id" value="<c:out value='${rowData.id}'/>"> --%>
                                    </td>
                                    <td class="<r:onError name='fixtures[${rowStatus.index}].fixtureDefId' text='errorBg' />"> 
                                        <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].fixtureDefId" value="<c:out value='${rowData.fixtureDefId}'/>" size="10">
                                    </td>
                                    <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                        <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="55">
                                    </td>
                                    <td class="<r:onError name='fixtures[${rowStatus.index}].dmxOffset' text='errorBg' />"> 
                                        <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].dmxOffset" value="<c:out value='${rowData.dmxOffset}'/>" size="30">
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
                                        <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].fixtureDefId" value="<c:out value='${rowData.fixtureDefId}'/>" size="10">
                                    </td>
                                    <td class="<r:onError name='fixtures[${rowStatus.index}].name' text='errorBg' />"> 
                                        <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].name" value="<c:out value='${rowData.name}'/>" size="55">
                                    </td>
                                    <td class="<r:onError name='fixtures[${rowStatus.index}].dmxOffset' text='errorBg' />"> 
                                        <input type="text" class="formfield" name="fixtures[<c:out value='${rowStatus.index}'/>].dmxOffset" value="<c:out value='${rowData.dmxOffset}'/>" size="30">
                                    </td>
	                            </tr>
	                        </c:otherwise></c:choose> 
	                    </c:forEach> 
	      
	                    <%-- Empty row --%>
	                    <tr id="rowid.<c:out value='${form.fixtures_size}'/>"> 
	                        <td> <div class="redrollover"> <a href="javascript:void(0)" onclick="fnDeleteRow(tblObj,<c:out value='${form.fixtures_size}'/>,0); return 0;"><img src="image/delete-icon.gif" width="18" height="17" border="0"></a></div>
	                            <input type="hidden" name="fixtures[<c:out value='${form.fixtures_size}'/>].cmdUpdate" value="N">
	                        </td>
	                        <td><input type="text" class="formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].fixtureDefId" value="" size="55"></td>
	                        <td><input type="text" class="formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].name" value="" size="30"></td>
	                        <td><input type="text" class="formfield" name="fixtures[<c:out value='${form.fixtures_size}' />].dmxOffset" value="" size="30"></td>
	                    </tr>
	                    <tr> 
	                        <td> <div class="greenrollover"> <a href="javascript:void(0)" onclick="fnAddRow(tblObj); return 0;"><img src="image/add-icon.gif" width="18" height="17" border="0"></a></div></td>
	                        <td colspan="4">&nbsp;Add row</td>
	                    </tr>
	      
	                    <tr align="right"> 
	                        <td colspan="5" class="formHeader">
						        <div style="width:158px; height:54px; background: url(images/ui/bg-menu-small.png) no-repeat left top; background-color: blue; float:left;
								  font-size: 22px; color: white;
								  font-weight: bold; text-align: center; padding-top: 21px; margin-right:10px; cursor: pointer;"
								  onclick="isSubmitting=true; checkModify('mainForm',tblObj); document.forms[0].submit()"
								  >Update</div>
	                        </td>
	                    </tr>
	                    <tr> 
	                        <td colspan="5" align="right">&nbsp; </td>
	                    </tr>
	                </table>
	            </div>
	        </form>
	    </div>
    </div>
</div>
</body>
</html>
