<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,java.text.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,com.randomnoun.dmx.*,com.randomnoun.dmx.show.Show"
%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/common.tld" prefix="r" %>
<% 
   AppConfig appConfig = AppConfig.getAppConfig();
   Controller controller = (Controller) request.getAttribute("controller");
   Universe universe = (Universe) request.getAttribute("universe");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" dir="ltr">
<head>
    <!-- Meta Tags -->
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=utf-8" />
    <meta name="robots" content="index,follow" />
    <meta name="publisher" content="randomnoun" />
    <meta name="copyright" content="&copy; Copyright 2010, randomnoun" />
    <meta name="description" content="Albion comedy DMX web application" />
    <meta name="revisit-after" content="2 days" />
    <meta name="keywords" content="nothing-in-particular" />
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> DMX</title>

    <link rel="shortcut icon" href="image/favicon.png" />
    <link rel="stylesheet" href="css/farbtastic.css" type="text/css" />
    <link rel="stylesheet" href="css/fancyController.css" type="text/css" />

    <!-- JavaScript -->
    <script src="mjs?js=prototype" type="text/javascript"></script>
    <script src="mjs?js=jquery-1.4.4.min,farbtastic"></script> 
    <script>jQuery.noConflict();</script> 
    <script src="mjs?js=scriptaculous,builder,effects,dragdrop,controls,slider,sound,rollover,johnford,browserDetect" type="text/javascript"></script> 
    
    <%-- <script src="mjs?js=prototype,slider" type="text/javascript"></script>  --%>
<script>
<r:setJavascriptVar name="shows" value="${shows}" />
<r:setJavascriptVar name="fixtures" value="${fixtures}" />
<r:setJavascriptVar name="fixtureDefs" value="${fixtureDefs}" />
<r:setJavascriptVar name="dmxValues" value="${dmxValues}" />
<r:setJavascriptVar name="fixValues" value="${fixValues}" />
<r:setJavascriptVar name="version" value="${version}" />
<r:setJavascriptVar name="origPanel" value="${panel}" />
var dmxTimeSourceText = "<%= universe.getTimeSource().getClass().getName() %> / <%= new Date(universe.getTimeSource().getTime()) %>";
<%-- this will be a client-side include eventually --%>
<jsp:include page="js/fancyController.js" />
</script>
</head>
<body onload="initWindow()">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB!</span></div>
<div class="lhsMenuContainer">
  <div id="lhsBlackout" class="lhsMenuItem"><img class="lhsIcon" width="70" height="70" src="image/lhsBlackout.png" title="Blackout"/><div class="lhsMenuText">Blackout</div></div>
  <div id="lhsShows" class="lhsMenuItem"><img class="lhsIcon" width="70" height="70" src="image/lhsShows.png" title="Shows"/><div class="lhsMenuText">Shows</div></div>
  <div id="lhsFixtures" class="lhsMenuItem"><img class="lhsIcon" width="70" height="70" src="image/lhsFixtures.png" title="Fixtures"/><div class="lhsMenuText">Fixtures</div></div>
  <div id="lhsDMX" class="lhsMenuItem"><img class="lhsIcon" width="70" height="70" src="image/lhsDmx.png" title="DMX"/><div class="lhsMenuText">DMX</div></div>
  <div id="lhsLogs" class="lhsMenuItem"><img class="lhsIcon" width="70" height="70" src="image/lhsLogs.png" title="Logs"/><div class="lhsMenuText">Logs</div></div>
  <div id="lhsConfig" class="lhsMenuItem"><img class="lhsIcon" width="70" height="70" src="image/lhsConfig.png" title="Config"/><div class="lhsMenuText">Config</div></div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">

<div id="lgoPanel" >
<img id="lgoImage" src="image/dmx-web.png"/>
<div id="lgoClientImage"></div>
<div id="lgoText1" ></div>
<div id="lgoText2" ></div>
<div>

</div>
</div>

<div id="shwPanel" >
  <div id="shwCancel"><img width="80" height="70" "src="image/cancelWhite.png" title="Cancel all"/></div>
  <div id="shwAudio">
    <div id="shwAudioBMTLegend"></div>
    <div id="shwAudioBassOuter" class="shwAudioOuter" /><div id="shwAudioBassInner" class="shwAudioInner"></div></div>
    <div id="shwAudioMidOuter" class="shwAudioOuter" /><div id="shwAudioMidInner" class="shwAudioInner"></div></div>
    <div id="shwAudioTrebleOuter" class="shwAudioOuter" /><div id="shwAudioTrebleInner" class="shwAudioInner"></div></div>
  </div>
  <div id="shwPageUp"><img width="80" height="70" "src="image/pageUp.png" title="Page up"/></div>
  <div id="shwPageDown"><img width="80" height="70" "src="image/pageDown.png" title="Page down"/></div>
  <div id="shwItemContainer"></div>
</div>


<div id="fixPanel" style="display: none;">
  <div id="fixBlackout" class="fixControl">Blackout</div>
  <div id="fixStandardControls">
  <div id="fixDimScrollArea">
  <div id="fixDim"><div id="fixDimHandle"></div></div>
  </div>
  <div id="fixDimLabel">Dimmer</div>
  <div id="fixStrobeScrollArea">
  <div id="fixStrobe"><div id="fixStrobeHandle"></div></div>
  </div>
  <div id="fixStrobeLabel">Strobe</div>
  <!--  <div id="fixColor" class="fixControl">Colour</div> -->
  <input type="text" id="fixColor" name="fixColor" value="#123456" />
  <div id="fixColorPicker"></div>
  <div id="fixAim" class="fixControl"><div id="fixAimHandle"></div><div id="fixAimActual"></div></div>
  <div id="fixAimLabel">Pan/Tilt control</div>
  <div id="fixAimLeft">&#8592;</div>
  <div id="fixAimRight">&#8594;</div>
  <div id="fixAimTop">&#8593;</div>
  <div id="fixAimBottom">&#8595;</div>
  </div>
  <div id="fixCustomControls"></div>
  
  
  <div id="fixGroup" class="fixControl fixGroup">Select individual</div>
  <div id="fixAllNone" class="fixControl fixAllNone">All/None</div>
  <div id="fixCustom" class="fixControl fixCustom">Custom controls</div>
  
  <div id="fixItemContainer"></div>
  <div id="fixPageUp"><img width="80" height="70" "src="image/pageUp.png" title="Page up"/></div>
  <div id="fixPageDown"><img width="80" height="70" "src="image/pageDown.png" title="Page down"/></div>

</div>

<div id="dmxPanel" style="display: none;">
  <div id="dmxImmediate" class="dmxControl">Immediate ON</div>
  <div id="dmxUpdateAll" class="dmxControl dmxControlDisabled">Update all</div> 
  <div id="dmxTimeSource" class="dmxTimeSource"></div>
  <div id="dmxValues">
  </div>
  <div id="dmxSliderScrollArea">
  <div id="dmxSlider"><div id="dmxSliderHandle"></div></div>
  </div>
  <!-- 
  <div id="dmxHighlight" style="display:none;"></div>
  <div id="dmxHighlight2" style="display:none;"></div>
   -->
</div>

<div id="logPanel" style="display: none;">
  <div id="logExceptionContainer"></div>
  <div id="logPageUp"><img width="80" height="70" "src="image/pageUp.png" title="Page up"/></div>
  <div id="logPageDown"><img width="80" height="70" "src="image/pageDown.png" title="Page down"/></div>
</div>

<div id="cnfPanel" style="display: none;">
  <div id="cnfFixtureDef" class="cnfControl">Fixture definitions</div>
  <div id="cnfFixture" class="cnfControl">Fixtures</div>
  <div id="cnfShowDef" class="cnfControl">Show definitions</div>
  <div id="cnfShow" class="cnfControl">Shows</div>
<%--  <div id="cnfResetDMX" class="cnfControl">Reset DMX device</div>  --%>
  <div id="cnfResetAudio" class="cnfControl">Reset audio controller</div>
  
  <div id="cnfSimple" class="cnfControl">Simple controller</div>
<% 
    if (appConfig.getProperty("dev.vlc.streamingUrl")!=null &&
	  !appConfig.getProperty("dev.vlc.streamingUrl").equals("")) {
%>  
  <div id="cnfVideo" class="cnfControl">Video stream</div>
<%
    }
%>  
</div>



</div>



</body>
</html>
