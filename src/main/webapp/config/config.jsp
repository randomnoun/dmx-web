<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,org.apache.log4j.Logger"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common-public" prefix="r" %>
<%!
  String getChecklistImg(Map<String, String> form, String attributeName) {
	String img = form.get(attributeName + ".img"); 
	if (img==null) {
	    Logger.getLogger("config.jsp").warn("Missing request attribute '" + attributeName + ".img'");
	    return "";
	} else {
		if (img.equals("ok")) {
	      return "<img src=\"image/config/icnOK.gif\" width=\"16\" height=\"16\"/>";
		} else if (img.equals("warning") || img.equals("warn")) {
		  return "<img src=\"image/config/icnWarning.gif\" width=\"16\" height=\"16\"/>";
		} else {
		  return "<img src=\"image/config/icnFail.gif\" width=\"16\" height=\"16\"/>";
		}
	}
  }
%>
<%
try {
  long pageNum = ((Long) request.getAttribute("pageNumber")).longValue();
  // probably doesn't belong in session, but this should only execute once
  Map<String, String> objects = (Map<String, String>) session.getAttribute("com.randomnoun.dmx.objects");
  Map<String, String> form = (Map<String, String>) session.getAttribute("com.randomnoun.dmx.config");

  request.setAttribute("form", form);
  request.setAttribute("objects", objects);
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
H1 { margin-top: 0px; }
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
P { font-size: 10pt; line-height: 1.2; }
LI { font-size: 10pt; line-height: 1.5; }
.smallInput { width: 40px; }
.textInput { width: 600px; }
.textInput2 { width: 200px; }
.configHeader { font-weight: bold; font-size: 12pt; }  
</style>
<script>
<r:setJavascriptVar name="pageNumber" value="${pageNumber}"/>
<r:setJavascriptVar name="exampleConnectionStrings" value="${objects.exampleConnectionStrings}"/>
<r:setJavascriptVar name="exampleAdminConnectionStrings" value="${objects.exampleAdminConnectionStrings}"/>

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
function edtChangeCreateSchema() {
	var createSchema = document.forms[0].elements['createSchema'][0].checked;
	$$(".newSchema").each(function(s){s.style.display=createSchema?'table-row':'none';});
}
function edtChangeDatabaseConfig() {
	var databaseConfig = document.forms[0].elements['database_config'][0].checked;
	$$(".standardDb").each(function(s){s.style.display=databaseConfig?'table-row':'none';});
	$$(".customDb").each(function(s){s.style.display=databaseConfig?'none':'table-row';});
}
function edtChangeDatabaseDriver() {
	var databaseDriver = document.forms[0].elements['database_driver'].value;
	var exampleConnectionString = exampleConnectionStrings[databaseDriver];
	var exampleAdminConnectionString = exampleAdminConnectionStrings[databaseDriver];
	
	$("exampleDatabaseUrl").update("<br/>Example " + databaseDriver + " connection string:<br/><br/><tt>" + exampleConnectionString + "</tt>");
	$("exampleAdminUrl").update("<br/>Example " + databaseDriver + " admin connection string:<br/><br/><tt>" + exampleAdminConnectionString + "</tt>");
}
function initWindow() {
    edtInitPanel();
    if (pageNumber==1) {
    	document.forms[0].elements['jsCheck'].value="Y";
    	document.forms[0].elements['screenResolution'].value=window.screen.width + "x" + window.screen.height;
    	document.forms[0].elements['browserResolution'].value=document.viewport.getWidth() + "x" + document.viewport.getHeight();
    } else if (pageNumber==3) {
        edtChangeCreateSchema();
    	edtChangeDatabaseConfig();
    	edtChangeDatabaseDriver();
    }
}

</script>
</head>

<body onload="initWindow()" >
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB Application config</span></div>
<div class="lhsMenuContainer">
<% if ("back".equals(request.getAttribute("backIcon"))) { %>
  <div id="lhsBack" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/back.png" title="Back"/><div class="lhsMenuText">Back</div></div>
<% } %>  
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
      <col width="200px;" >
      <col width="800px;" >
      <% if (pageNum == 1 ) { %>
      <tr>
        <td colspan="2">
        <input type="hidden" name="jsCheck" value="" />
        <input type="hidden" name="screenResolution" value="" />
        <input type="hidden" name="browserResolution" value="" />
        
        <h1>Welcome to DMX-web.</h1>
        <input type="hidden" name="pageNumber" value="1" />
        
        <p>Hi there ! This appears to be the first time you've run this application. 
        Before you can use DMX-web, we need to configure your software and hardware.</p>  
        
        <p>The following pages will step through the following details:</p>
        <ul><li>your database settings,
        <li>where you want to keep uploaded and temporary fixture definition files,
        <li>where you want to keep your audio files,
        <li>where you want to keep your log files,
        <li>your DMX connectivity settings, and
        <li>your WinAMP connection properties.
        </ul>
        
        <p>Most of these properties will already have defaults set up,
        please check to ensure that these are correct before hitting "Next".</p>
        
        <p>If you want to update these properties later, then they will be 
        contained in a text file in the following location:</p>
        
        <p><tt><%= form.get("configFileLocation") %></tt></p>
        
        <p>Click 'Next' to continue the installation process</p>
        </td>
      </tr>
      
      <tr>
        <td colspan="2">
        <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/next.png" title="Next"/><div class="lhsMenuText">Next</div></div>
        </td>
      </tr>
      
    
    <% } else if (pageNum == 2) { %>
      <tr>
        <td colspan="2">
        <input type="hidden" name="pageNumber" value="2" />
        <p>First, a check to ensure that all DMX-Web components are available:</p>
        </td>
      </tr>
      <tr>
        <td colspan="2">Installation checklist</td>
      </tr>
      <tr>
      <tr><td>DMX-web version:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= form.get("dmxWebVersion") %></td></tr>
      <tr><td>DMX-web build:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= form.get("dmxWebBuild") %></td></tr>

      <tr><td>Java version:</td>
          <td><%= getChecklistImg(form, "javaVersion") %> <%= form.get("javaVersion") %></td></tr>
      <% if (form.get("javaVersion.text")!=null) { %>
      <tr><td></td><td><%= form.get("javaVersion.text") %></td></tr>
      <% } %>

      <tr><td>Tomcat version:</td>
          <td><%= getChecklistImg(form, "tomcatVersion") %> <%= form.get("tomcatVersion") %></td></tr>
      <% if (form.get("tomcatVersion.text")!=null) { %>
      <tr><td></td><td><%= form.get("tomcatVersion.text") %></td></tr>
      <% } %>
          
      <tr><td>RXTX JAR version:</td>
          <td><%= getChecklistImg(form, "rxtxJarVersion") %> <%= form.get("rxtxJarVersion") %></td></tr>
      <% if (form.get("rxtxJarVersion.text")!=null) { %>
      <tr><td></td><td><%= form.get("rxtxJarVersion.text") %></td></tr>
      <% } %>
          
      <tr><td>RXTX DLL version:</td>
          <td><%= getChecklistImg(form, "rxtxDllVersion") %> <%= form.get("rxtxDllVersion") %></td></tr>
      <% if (form.get("rxtxDllVersion.text")!=null) { %>
      <tr><td></td><td><%= form.get("rxtxDllVersion.text") %></td></tr>
      <% } %>
          
      <tr><td>Server config file is writable:</td>
          <td><%= getChecklistImg(form, "serverConfigFileWritable") %> <%= form.get("serverConfigFileWritable") %></td></tr>
      <% if (form.get("serverConfigFileWritable.text")!=null) { %>
      <tr><td></td><td><%= form.get("serverConfigFileWritable.text") %></td></tr>
      <% } %>    
      
      <tr><td>DMX config file is writable:</td>
          <td><%= getChecklistImg(form, "dmxConfigFileWritable") %> <%= form.get("dmxConfigFileWritable") %></td></tr>
      <% if (form.get("dmxConfigFileWritable.text")!=null) { %>
      <tr><td></td><td><%= form.get("dmxConfigFileWritable.text") %></td></tr>
      <% } %>    

      <tr><td>JDBC drivers available:</td>
          <td><%= getChecklistImg(form, "jdbcDriversAvailable") %> <%= form.get("jdbcDriversAvailable") %></td></tr>
      <% if (form.get("jdbcDriversAvailable.text")!=null) { %>
      <tr><td></td><td><%= form.get("jdbcDriversAvailable.text") %></td></tr>
      <% } %>    
          
      <tr><td>COM ports available:</td>
          <td><%= getChecklistImg(form, "comPortsAvailable") %> <%= form.get("comPortsAvailable") %></td></tr>
      <% if (form.get("comPortsAvailable.text")!=null) { %>
      <tr><td></td><td><%= form.get("comPortsAvailable.text") %></td></tr>
      <% } %>    

      <tr><td>Web browser:</td>
          <td><%= getChecklistImg(form, "userAgent") %> <%= form.get("userAgent") %></td></tr>
      <% if (form.get("userAgent.text")!=null) { %>
      <tr><td></td><td><%= form.get("userAgent.text") %></td></tr>
      <% } %>    

      <tr><td>Javascript enabled:</td>
          <td><%= getChecklistImg(form, "javascriptEnabled") %> <%= form.get("javascriptEnabled") %></td></tr>
      <% if (form.get("javascriptEnabled.text")!=null) { %>
      <tr><td></td><td><%= form.get("javascriptEnabled.text") %></td></tr>
      <% } %>    

      <tr><td>Cookies enabled:</td>
          <td><%= getChecklistImg(form, "cookiesEnabled") %> <%= form.get("cookiesEnabled") %></td></tr>
      <% if (form.get("cookiesEnabled.text")!=null) { %>
      <tr><td></td><td><%= form.get("cookiesEnabled.text") %></td></tr>
      <% } %>    

      <tr><td>Browser resolution:</td>
          <td><%= getChecklistImg(form, "browserResolution") %> <%= form.get("browserResolution") %></td></tr>
      <% if (form.get("browserResolution.text")!=null) { %>
      <tr><td></td><td><%= form.get("browserResolution.text") %></td></tr>
      <% } %>    

        <!-- TODO: VLC -->
      <% if (form.get("installOK")==null) { %>
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
    <% } else if (pageNum == 3) { %>      
      <tr>
        <td colspan="2" class="configHeader">
        <input type="hidden" name="pageNumber" value="3" />
        Database options
        </td>
      </tr>
      <tr><td>Database configuration:</td><td>
        <r:input type="radio" name="database_config" trueValue="standard" value="${form.database_config}" onchange="edtChangeDatabaseConfig()"/> Standard configuration<br/>
        <r:input type="radio" name="database_config" trueValue="custom" value="${form.database_config}" onchange="edtChangeDatabaseConfig()"/> Custom configuration<br/>
      </td></tr>
      </tr>
      <tr class="standardDb"><td></td><td>
        <p>All show, fixture and DMX patch data contained within DMX-Web is stored in a MySQL database.</p>
        
        <p>If you have an existing MySQL database, or you want to specify any custom
        database settings, you can select the "Custom" option above, otherwise
        just select "Standard" and then click Next.</p>
        
        <p>You will be able to review any database changes on the next page before they take place.</p>
      </td>

      <tr class="customDb">
        <td colspan="2"><b>Custom configuration</b></td>
      </tr>
      <tr class="customDb"><td>Database driver:</td>
          <td><r:select name="database_driver" value="${database_driver}" data="${objects.databaseDrivers}" onchange="edtChangeDatabaseDriver()" />
          <br/><p>If you don't see your database here, you may need to install a JDBC JAR into the server's <tt>lib</tt> directory.</p>
          </td></tr>
      <tr class="customDb"><td>Database connection string:</td>
          <td><r:input styleClass="textInput" type="text" name="database_url" value="${form.database_url}" />
          <p>This is the database string that will normally be used whilst running DMX-web.
          <br/><span id="exampleDatabaseUrl"></span></p>
          <br/>
          </td></tr>
      <tr class="customDb"><td>Database dmx username:</td>
        <td><r:input styleClass="textInput2" type="text" name="database_username" value="${form.database_username}" />
        </td></tr>
      <tr class="customDb"><td>Database dmx password:</td>
        <td><r:input styleClass="textInput2" type="text" name="database_password" value="${form.database_password}" />
        </td></tr>
      <%--
      <tr><td>Database connection type:</td><td>
        <r:input type="radio" name="database_connectionType" trueValue="simple" value="${database_connectionType}" /> Simple - single connection specified in property file<br/>
        <r:input type="radio" name="database_connectionType" trueValue="dbcp" value="${database_connectionType}" /> Pooled - pooled connection specified in property file<br/>
        <r:input type="radio" name="database_connectionType" trueValue="jndi" value="${database_connectionType}" /> JNDI - pooled connection specified in server.xml file<br/>
      </td></tr>
       --%>
       
      <tr class="customDb"><td>Create new schema:</td><td>
        <r:input type="radio" name="createSchema" trueValue="y" value="${form.createSchema}" onchange="edtChangeCreateSchema()"/> Yes - create new schema<br/>
        <r:input type="radio" name="createSchema" trueValue="n" value="${form.createSchema}" onchange="edtChangeCreateSchema()"/> No - use existing schema (specified in connection string above)<br/>
      </td></tr>
      <tr>
        <td colspan="2" class="customDb newSchema"><b>New schema settings</b></td>
      </tr>
      <tr class="customDb newSchema"><td>Database admin username:</td>
        <td><r:input styleClass="textInput2" type="text" name="databaseAdminUsername" value="${form.databaseAdminUsername}" />
        <br/><p>This user will be used to create the new schema. It should be granted CREATE USER, CREATE DATABASE, CREATE TABLE and CREATE TRIGGER permissions on the database.</p>
        </td></tr>
        </td></tr>
      <tr class="customDb newSchema"><td>Database admin password:</td>
        <td><r:input styleClass="textInput2" type="text" name="databaseAdminPassword" value="${form.databaseAdminPassword}" />
        </td></tr>
      <tr class="customDb newSchema"><td>Admin connection string:</td>
         <td><r:input styleClass="textInput" type="text" name="databaseAdminUrl" value="${form.databaseAdminUrl}" />
         <p>This connection string will only be used to create the DMX-web database users, tables and triggers.<br/>
         <span id="exampleAdminUrl"></span></p>
         </td></tr>
      <tr class="customDb newSchema"><td>Database dmx schema:</td><td><r:input styleClass="textInput2" type="text" name="databaseSchema" value="${form.databaseSchema}" /></td></tr>
      <tr class="customDb newSchema"><td>Create dmx user:</td><td>
        <r:input type="radio" name="createUser" trueValue="y" value="${form.createUser}" /> Yes - create the dmx user specified above<br/>
        <r:input type="radio" name="createUser" trueValue="n" value="${form.createUser}" /> No - new dmx user specified above already exists<br/>
      </td>
      </tr>
      <tr>
        <td colspan="2">
        <div id="edtSubmit" class="edtSubmit"><img class="lhsMenuIcon" width="70" height="70" src="image/next.png" title="Next"/><div class="lhsMenuText">Next</div></div>
        </td>
      </tr>
    <% } else if (pageNum == 4) { %>
      
      <tr>
        <td colspan="2" class="configHeader">File locations</td>
      </tr>  
      <tr><td>Temporary directory:</td><td><r:input styleClass="textInput" type="text" name="webapp.fileUpload.tempDir" value="${form.webapp_fileUpload_tempDir}" /></td></tr>
      <tr><td>Upload directory:</td><td><r:input styleClass="textInput" type="text" name="webapp.fileUpload.path" value="${form.webapp_fileUpload_path}" /></td></tr>
      <tr><td>Audio directory:</td><td><r:input styleClass="textInput" type="text" name="audioController.defaultPath" value="${form.audioController_defaultPath}" /></td></tr>
      <tr><td>Log directory:</td><td><r:input styleClass="textInput" type="text" name="log4j.logDirectory" value="${form.log4j_logDirectory}" /></td></tr>
      <tr>
        <td colspan="2" class="configHeader">DMX connectivity (Enttec USB Pro)</td>
      </tr>
      <tr><td>Device type:</td><td><r:select name="dmxDevice_class" value="${form.dmxDevice_class}" data="${objects.dmxDeviceTypes}"  /></td></tr>
      <tr><td>COM port:</td><td><r:select name="dmxDevice_portName" value="${form.dmxDevice_portName}" data="${objects.dmxDevicePortNames}"  /></td></tr>
      <tr>
        <td colspan="2" class="configHeader">Audio connectivity (WinAMP)</td>
      </tr>
      <tr><td>Audio source type:</td><td><r:select name="audioSource_class" value="${form.audioSource_class}" data="${objects.audioSourceTypes}"  /></td></tr>
      <tr><td>DMX-web Visualisation hostname:</td><td><r:input styleClass="textInput2" type="text" name="audioSource_host" value="${form.audioSource_host}" /></td></tr>
      <tr><td>DMX-web Visualisation port:</td><td><r:input styleClass="textInput2" type="text" name="audioSource_port" value="${form.audioSource_port}" /></td></tr>
      <tr><td>Controller type:</td><td><r:select name="audioController_class" value="${form.audioController_class}" data="${objects.audioControllerTypes}"  /></td></tr>
      <tr><td>NGWinAmp control hostname:</td><td><r:input styleClass="textInput2" type="text" name="audioController_host" value="${form.audioController_host}" /></td></tr>
      <tr><td>NGWinAmp control port:</td><td><r:input styleClass="textInput2" type="text" name="audioController_port" value="${form.audioController_port}" /></td></tr>
      <tr><td>NGWinAmp control password:</td><td><r:input styleClass="textInput2" type="text" name="audioController_password" value="${form.audioController_password}" /></td></tr>
      <tr>
        <td colspan="2">
        <p>If you're happy with the options above, click the 'OK' button below to verify these settings.</p>
        <p>Once successfully configured, this page will no longer be available.</p>
        <p>Changes can be made by updating the <tt><%= form.get("configFileLocation") %></tt>
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
<%
  } catch (Exception e) {
    org.apache.log4j.Logger.getLogger("config.jsp").error("Exception in config.jsp", e);
    throw(e);
  }
%>