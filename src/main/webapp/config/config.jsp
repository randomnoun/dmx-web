<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,org.apache.log4j.Logger"
%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<%!
  String getChecklistImg(HttpServletRequest request, String attributeName) {
	String img = (String) request.getAttribute(attributeName + ".img"); 
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
function initWindow() {
    edtInitPanel();
    if (pageNumber==3) {
        edtChangeCreateSchema();
    	edtChangeDatabaseConfig();
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
      <col width="300px;" >
      <col width="700px;" >
      <% if (pageNum == 1 ) { %>
      <tr>
        <td colspan="2">
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
        
        <p><tt><%= request.getAttribute("configFileLocation") %></tt></p>
        
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
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= request.getAttribute("dmxWebVersion") %></td></tr>
      <tr><td>DMX-web build:</td>
          <td><img src="image/config/icnOK.gif" width="16" height="16"/> <%= request.getAttribute("dmxWebBuild") %></td></tr>

      <tr><td>Java version:</td>
          <td><%= getChecklistImg(request, "javaVersion") %> <%= request.getAttribute("javaVersion") %></td></tr>
      <% if (request.getAttribute("javaVersion.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("javaVersion.text") %></td></tr>
      <% } %>

      <tr><td>Tomcat version:</td>
          <td><%= getChecklistImg(request, "tomcatVersion") %> <%= request.getAttribute("tomcatVersion") %></td></tr>
      <% if (request.getAttribute("tomcatVersion.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("tomcatVersion.text") %></td></tr>
      <% } %>
          
      <tr><td>RXTX JAR version:</td>
          <td><%= getChecklistImg(request, "rxtxJarVersion") %> <%= request.getAttribute("rxtxJarVersion") %></td></tr>
      <% if (request.getAttribute("rxtxJarVersion.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("rxtxJarVersion.text") %></td></tr>
      <% } %>
          
      <tr><td>RXTX DLL version:</td>
          <td><%= getChecklistImg(request, "rxtxDllVersion") %> <%= request.getAttribute("rxtxDllVersion") %></td></tr>
      <% if (request.getAttribute("rxtxDllVersion.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("rxtxDllVersion.text") %></td></tr>
      <% } %>
          
      <tr><td>Server config file is writable:</td>
          <td><%= getChecklistImg(request, "serverConfigFileWritable") %> <%= request.getAttribute("serverConfigFileWritable") %></td></tr>
      <% if (request.getAttribute("serverConfigFileWritable.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("serverConfigFileWritable.text") %></td></tr>
      <% } %>    
      
      <tr><td>DMX config file is writable:</td>
          <td><%= getChecklistImg(request, "dmxConfigFileWritable") %> <%= request.getAttribute("dmxConfigFileWritable") %></td></tr>
      <% if (request.getAttribute("dmxConfigFileWritable.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("dmxConfigFileWritable.text") %></td></tr>
      <% } %>    

      <tr><td>JDBC drivers available:</td>
          <td><%= getChecklistImg(request, "jdbcDriversAvailable") %> <%= request.getAttribute("jdbcDriversAvailable") %></td></tr>
      <% if (request.getAttribute("jdbcDriversAvailable.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("jdbcDriversAvailable.text") %></td></tr>
      <% } %>    
          
      <tr><td>COM ports available:</td>
          <td><%= getChecklistImg(request, "comPortsAvailable") %> <%= request.getAttribute("comPortsAvailable") %></td></tr>
      <% if (request.getAttribute("comPortsAvailable.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("comPortsAvailable.text") %></td></tr>
      <% } %>    

      <tr><td>Web browser:</td>
          <td><%= getChecklistImg(request, "userAgent") %> <%= request.getAttribute("userAgent") %></td></tr>
      <% if (request.getAttribute("userAgent.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("userAgent.text") %></td></tr>
      <% } %>    

      <tr><td>Javascript enabled:</td>
          <td><%= getChecklistImg(request, "javascriptEnabled") %> <%= request.getAttribute("javascriptEnabled") %></td></tr>
      <% if (request.getAttribute("javascriptEnabled.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("javascriptEnabled.text") %></td></tr>
      <% } %>    

      <tr><td>Cookies enabled:</td>
          <td><%= getChecklistImg(request, "cookiesEnabled") %> <%= request.getAttribute("cookiesEnabled") %></td></tr>
      <% if (request.getAttribute("cookiesEnabled.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("cookiesEnabled.text") %></td></tr>
      <% } %>    

      <tr><td>Browser resolution:</td>
          <td><%= getChecklistImg(request, "browserResolution") %> <%= request.getAttribute("browserResolution") %></td></tr>
      <% if (request.getAttribute("browserResolution.text")!=null) { %>
      <tr><td></td><td><%= request.getAttribute("browserResolution.text") %></td></tr>
      <% } %>    

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
    <% } else if (pageNum == 3) { %>      
      <tr>
        <td colspan="2" class="configHeader">
        <input type="hidden" name="pageNumber" value="3" />
        Database options
        </td>
      </tr>
      <tr><td>Database configuration:</td><td>
        <r:input type="radio" name="database_config" trueValue="standard" value="${database_config}" onchange="edtChangeDatabaseConfig()"/> Standard configuration<br/>
        <r:input type="radio" name="database_config" trueValue="custom" value="${database_config}" onchange="edtChangeDatabaseConfig()"/> Custom configuration<br/>
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
      <%--
      <tr><td>Database driver:</td>
          <td><r:select name="database_driver" value="${database_driver}" data="${databaseDrivers}"  />
          <br/><p>If you don't see your database here, you may need to install a JDBC JAR into the server's <tt>lib</tt> directory.</p>
          </td></tr>
       --%>
      <tr class="customDb"><td>Database connection string:</td>
          <td><r:input styleClass="textInput" type="text" name="database_url" value="${database_url}" />
          <p>This is the database string that will normally be used whilst running DMX-web.</p>
          <br/>
          </td></tr>
      <tr class="customDb"><td>Database dmx username:</td>
        <td><r:input styleClass="textInput2" type="text" name="database_username" value="${database_username}" />
        </td></tr>
      <tr class="customDb"><td>Database dmx password:</td>
        <td><r:input styleClass="textInput2" type="text" name="database_password" value="${database_password}" />
        </td></tr>
      <%--
      <tr><td>Database connection type:</td><td>
        <r:input type="radio" name="database_connectionType" trueValue="simple" value="${database_connectionType}" /> Simple - single connection specified in property file<br/>
        <r:input type="radio" name="database_connectionType" trueValue="dbcp" value="${database_connectionType}" /> Pooled - pooled connection specified in property file<br/>
        <r:input type="radio" name="database_connectionType" trueValue="jndi" value="${database_connectionType}" /> JNDI - pooled connection specified in server.xml file<br/>
      </td></tr>
       --%>
       
      <tr class="customDb"><td>Create new schema:</td><td>
        <r:input type="radio" name="createSchema" trueValue="y" value="${createSchema}" onchange="edtChangeCreateSchema()"/> Yes - create new schema<br/>
        <r:input type="radio" name="createSchema" trueValue="n" value="${createSchema}" onchange="edtChangeCreateSchema()"/> No - use existing schema (specified in connection string above)<br/>
      </td></tr>
      <tr>
        <td colspan="2" class="customDb newSchema"><b>New schema settings</b></td>
      </tr>
      <tr class="customDb newSchema"><td>Database admin username:</td>
        <td><r:input styleClass="textInput2" type="text" name="databaseAdminUsername" value="${databaseAdminUsername}" />
        <br/><p>This user should be granted CREATE USER, CREATE DATABASE, CREATE TABLE and CREATE TRIGGER permissions on the database.</p>
        </td></tr>
        </td></tr>
      <tr class="customDb newSchema"><td>Database admin password:</td>
        <td><r:input styleClass="textInput2" type="text" name="databaseAdminPassword" value="${databaseAdminPassword}" />
        </td></tr>
      <tr class="customDb newSchema"><td>Admin connection string:</td>
         <td><r:input styleClass="textInput" type="text" name="databaseAdminUrl" value="${databaseAdminUrl}" />
         <br/><p>This connection string will only be used to create the DMX-web database users, tables and triggers.</p>
         </td></tr>
      <tr class="customDb newSchema"><td>Database dmx schema:</td><td><r:input styleClass="textInput2" type="text" name="databaseSchema" value="${databaseSchema}" /></td></tr>
      <tr class="customDb newSchema"><td>Create dmx user:</td><td>
        <r:input type="radio" name="createUser" trueValue="y" value="${createUser}" /> Yes - create the dmx user specified above<br/>
        <r:input type="radio" name="createUser" trueValue="n" value="${createUser}" /> No - new dmx user specified above already exists<br/>
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
<%
  } catch (Exception e) {
    org.apache.log4j.Logger.getLogger("config.jsp").error("Exception in config.jsp", e);
    throw(e);
  }
%>