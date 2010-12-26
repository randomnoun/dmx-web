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
<%!
  String getChecklistImg(HttpServletRequest request, String attributeName) {
	String img = (String) request.getAttribute(attributeName + ".img"); 
	if (img.equals("ok")) {
      return "<img src=\"image/config/icnOK.gif\" width=\"16\" height=\"16\"/>";
	} else if (img.equals("warning")) {
	  return "<img src=\"image/config/icnWarning.gif\" width=\"16\" height=\"16\"/>";
	} else {
	  return "<img src=\"image/config/icnFail.gif\" width=\"16\" height=\"16\"/>";
	}
  }
%>
<%
  long pageNum = ((Long) request.getAttribute("page")).longValue();
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
    
    <title>Configuration</title>
     
    <link rel="shortcut icon" href="images/favicon.png" />
    
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
.edtContainer {
  width: 1000px; height: 680px; padding: 10px;
  background-color: #EEEEFF; 
  overflow: scroll;
}
.rj {  /* right-justified */
  text-align: right;
}

TABLE { width: auto; }
TH, TD { padding: 5px; line-height: 1; font-size: 10pt; vertical-align: top; }
INPUT { font-size: 8pt; margin-bottom: 3px; }
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
P { font-size: 10pt; }
LI { font-size: 10pt; line-height: 1.5; }
.smallInput { width: 40px; }
.textInput { width: 600px; }
.textInput2 { width: 200px; }
.configHeader { font-weight: bold; font-size: 12pt; }  
</style>

<script>

function edtInitPanel() {
    var edtSubmitEl = $("edtSubmit");
    Event.observe(edtSubmitEl, 'click', edtSubmitClick);
    Event.observe($("lhsOK"), 'click', lhsOKClick);
}

function edtSubmitClick() { 
    isSubmitting=true; 
    document.forms[0].submit();
}
function lhsOKClick() { 
    isSubmitting=true; 
    document.forms[0].submit();
};
function initWindow() {
    edtInitPanel();
}
function edtChangeCreateSchema() {
	var createSchema = document.forms[0].elements['createSchema'][0].checked;
	$$(".newSchema").each(function(s){s.style.display=createSchema?'table-row':'none';});
}
</script>
</head>

<body onload="initWindow()" >
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Application config</span></div>
<div class="lhsMenuContainer">
<% if ("ok".equals(request.getAttribute("submitIcon"))) { %>
  <div id="lhsOK" class="lhsMenuItemGreen"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="OK"/><div class="lhsMenuText">OK</div></div>
<% } else if ("next".equals(request.getAttribute("submitIcon"))) { %>
  <div id="lhsOK" class="lhsMenuItemGreen"><img class="lhsMenuIcon" width="70" height="70" src="image/next.png" title="OK"/><div class="lhsMenuText">Next</div></div>
<% }  %>  
</div>

<div id="rhsMessage"></div>

<div class="rhsPanel">
<div class="edtContainer">
    <jsp:include page="/misc/errorHeader.jsp" />
    <form id="mainForm" name="mainForm" method="post" action="config">
        <input type="hidden" name="action" value="maintain" /> 

  <table border="0" cellpadding="1" cellspacing="1" id="entryTable">
      <col width="300px;" >
      <col width="700px;" >
      <tr>
        <td colspan="2">
        <% if (pageNum == 1 ) { %>
        <h1>Welcome to DMX-web.</h1>
        
        <p>In order to use this application, you must first configure this software.
        You will need to enter details for:</p>
        <ul><li>your database settings,
        <li>where you want to keep uploaded and temporary fixture definition files,
        <li>where you want to keep your audio files,
        <li>where you want to keep your log files,
        <li>your DMX connectivity settings, and
        <li>your WinAMP connection properties.
        </ul>
        
        <p>Most of these properties will already have defaults set up,
        please check to ensure that these are correct before hitting "OK".
        If you want to update these properties later, then they will be 
        contained in a text file in the following location:</p>
        
        <p><%= request.getAttribute("configFileLocation") %></p>
          
        <p>First, a check to ensure that your application has been
        installed correctly:</p>
        
        </td>
      </tr>
      <tr>
        <td colspan="2">Installation checklist</td>
      </tr>
      <tr>
      <tr><td>DMX-web version:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= request.getAttribute("dmxWebVersion") %></td></tr>
      <tr><td>DMX-web build:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= request.getAttribute("dmxWebBuild") %></td></tr>
      <tr><td>Java version:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= request.getAttribute("javaVersion") %></td></tr>
      <tr><td>Tomcat version:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= request.getAttribute("tomcatVersion") %></td></tr>
      <tr><td>RXTX JAR version:</td>
          <td><%= getChecklistImg(request, "rxtxJarVersion") %> <%= request.getAttribute("rxtxJarVersion") %></td></tr>
      <tr><td>RXTX DLL version:</td>
          <td><%= getChecklistImg(request, "rxtxDllVersion") %> <%= request.getAttribute("rxtxDllVersion") %></td></tr>
      <tr><td>Server config file writable:</td>
          <td><%= getChecklistImg(request, "serverConfigFileWritable") %> <%= request.getAttribute("serverConfigFileWritable") %></td></tr>
      <tr><td>DMX config file writable:</td>
          <td><%= getChecklistImg(request, "dmxConfigFileWritable") %> <%= request.getAttribute("dmxConfigFileWritable") %></td></tr>
      <tr><td>JDBC drivers available:</td>
          <td><%= getChecklistImg(request, "jdbcDriversAvailable") %> <%= request.getAttribute("jdbcDriversAvailable") %></td></tr>
      <tr><td>COM ports available:</td>
          <td><%= getChecklistImg(request, "comPortsAvailable") %> <%= request.getAttribute("comPortsAvailable") %></td></tr>
        <!-- TODO: VLC -->
      <% if (request.getAttribute("installOK")==null) { %>
      <tr>
        <td colspan="2">
        <br/>
        <p>Please fix the problems above, restart the server, and then try again.</p>
        <br/>
        </td>
      </tr>
      <% } else { %>
      <tr>
        <td colspan="2">
        <br/>
        <p>That all looks fine. Now for some application configuration options</p>
        <br/>
        </td>
      </tr>
      <tr>
        <td colspan="2">
        <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/next.png" title="Next"/><div class="lhsMenuText">Next</div></div>
        </td>
      </tr>
      <% } %>
    <% } else if (pageNum == 2) { %>      
      <tr>
        <td colspan="2" class="configHeader">Database options</td>
      </tr>
      <tr><td>Database driver:</td>
          <td><r:select name="database_driver" value="${database_driver}" data="${databaseDrivers}"  />
          <br/><p>If you don't see your database here, you may need to install a JDBC JAR into the server's <tt>lib</tt> directory.</p>
          </td></tr>
      <tr><td>Database connection string:</td>
          <td><r:input styleClass="textInput" type="text" name="database_url" value="${database_url}" />
          <p>This is the database string that will normally be used whilst running DMX-web.</p>
          <br/>
          </td></tr>
      <tr><td>Database dmx username:</td>
        <td><r:input styleClass="textInput2" type="text" name="database_username" value="${database_username}" />
        </td></tr>
      <tr><td>Database dmx password:</td>
        <td><r:input styleClass="textInput2" type="text" name="database_password" value="${database_password}" />
        </td></tr>
      <tr><td>Database connection type:</td><td>
        <r:input type="radio" name="database_connectionType" trueValue="simple" value="${database_connectionType}" /> Simple - single connection specified in property file<br/>
        <r:input type="radio" name="database_connectionType" trueValue="dbcp" value="${database_connectionType}" /> Pooled - pooled connection specified in property file<br/>
        <r:input type="radio" name="database_connectionType" trueValue="jndi" value="${database_connectionType}" /> JNDI - pooled connection specified in server.xml file<br/>
      </td></tr>

      <tr><td>Create new schema:</td><td>
        <r:input type="radio" name="createSchema" trueValue="y" value="${createSchema}" onchange="edtChangeCreateSchema()"/> Yes - create new schema<br/>
        <r:input type="radio" name="createSchema" trueValue="n" value="${createSchema}" onchange="edtChangeCreateSchema()"/> No - use existing schema (specified in connection string above)<br/>
      </td></tr>
      <tr>
        <td colspan="2" class="newSchema"><b>New schema settings</b></td>
      </tr>
      <tr class="newSchema"><td>Database admin username:</td>
        <td><r:input styleClass="textInput2" type="text" name="databaseAdminUsername" value="${databaseAdminUsername}" />
        <br/><p>This user should be granted CREATE USER, CREATE DATABASE, CREATE TABLE and CREATE TRIGGER permissions on the database.</p>
        </td></tr>
        </td></tr>
      <tr class="newSchema"><td>Database admin password:</td>
        <td><r:input styleClass="textInput2" type="text" name="databaseAdminPassword" value="${databaseAdminPassword}" />
        </td></tr>
      <tr class="newSchema"><td>Admin connection string:</td>
         <td><r:input styleClass="textInput" type="text" name="databaseAdminUrl" value="${databaseAdminUrl}" />
         <br/><p>This connection string will only be used to create the DMX-web database users, tables and triggers.</p>
         </td></tr>
      <tr class="newSchema"><td>Database dmx schema:</td><td><r:input styleClass="textInput2" type="text" name="databaseSchema" value="${databaseSchema}" /></td></tr>
      <tr class="newSchema"><td>Create dmx user:</td><td>
        <r:input type="radio" name="createUser" trueValue="y" value="${createUser}" /> Yes - create the dmx user specified above<br/>
        <r:input type="radio" name="createUser" trueValue="n" value="${createUser}" /> No - new dmx user specified above already exists<br/>
      </td>
      </tr>
      <tr>
        <td colspan="2">
        <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/next.png" title="Next"/><div class="lhsMenuText">Next</div></div>
        </td>
      </tr>
    <% } else if (pageNum == 3) { %>
      
      <tr>
        <td colspan="2" class="configHeader">File locations</td>
      </tr>  
      <tr><td>Temporary directory:</td><td><r:input styleClass="textInput" type="text" name="webapp.fileUpload.tempDir" value="${webapp_fileUpload_tempDir}" /></td></tr>
      <tr><td>Upload directory:</td><td><r:input styleClass="textInput" type="text" name="webapp.fileUpload.path" value="${webapp_fileUpload_path}" /></td></tr>
      <tr><td>Audio directory:</td><td><r:input styleClass="textInput" type="text" name="audioController.defaultPath" value="${audioController_defaultPath}" /></td></tr>
      <tr><td>Log directory:</td><td><r:input styleClass="textInput" type="text" name="log4j.logDirectory" value="${log4j_logDirectory}" /></td></tr>
      <tr>
        <td colspan="2" class="configHeader">DMX connectivity (Enttec USB Pro)</td>
      </tr>
      <tr><td>Device type:</td><td><r:select name="dmxDevice_class" value="${dmxDevice_class}" data="${dmxDeviceTypes}"  /></td></tr>
      <tr><td>COM port:</td><td><r:select name="dmxDevice_portName" value="${dmxDevice_portName}" data="${dmxDevicePortNames}"  /></td></tr>
      <tr>
        <td colspan="2" class="configHeader">Audio connectivity (WinAMP)</td>
      </tr>
      <tr><td>Audio source type:</td><td><r:select name="audioSource_class" value="${audioSource_class}" data="${audioSourceTypes}"  /></td></tr>
      <tr><td>DMX-web Visualisation hostname:</td><td><r:input styleClass="textInput2" type="text" name="audioSource_host" value="${audioSource_host}" /></td></tr>
      <tr><td>DMX-web Visualisation port:</td><td><r:input styleClass="textInput2" type="text" name="audioSource_port" value="${audioSource_port}" /></td></tr>
      <tr><td>Controller type:</td><td><r:select name="audioController_class" value="${audioController_class}" data="${audioControllerTypes}"  /></td></tr>
      <tr><td>NGWinAmp control hostname:</td><td><r:input styleClass="textInput2" type="text" name="audioController_host" value="${audioController_host}" /></td></tr>
      <tr><td>NGWinAmp control port:</td><td><r:input styleClass="textInput2" type="text" name="audioController_port" value="${audioController_port}" /></td></tr>
      <tr><td>NGWinAmp control password:</td><td><r:input styleClass="textInput2" type="text" name="audioController_password" value="${audioController_password}" /></td></tr>
      <tr>
        <td colspan="2">
        <p>If you're happy with the options above, click the 'OK' button below to verify these settings.</p>
        <p>Once successfully configured, this page will no longer be available.</p>
        <p>Changes can be made by updating the <tt><%= request.getAttribute("configFileLocation") %></tt>
        configuration file manually.</p>
        <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/save.png" title="Update"/><div class="lhsMenuText">OK</div></div>        
        </td>
      </tr> 
    <% } %>
    </table>
  </form>
</div>  
</div>

</body>
</html>
