<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="misc/errorPage.jsp"
  import="java.util.*,java.text.*,org.springframework.jdbc.core.*,org.springframework.dao.support.DataAccessUtils,com.randomnoun.common.spring.*,com.randomnoun.common.*,com.randomnoun.dmx.config.*,com.randomnoun.dmx.*,com.randomnoun.dmx.show.Show"
%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.randomnoun.com/taglib/common" prefix="r" %>
<% 
   AppConfig appConfig = AppConfig.getAppConfig();
   Controller controller = (Controller) request.getAttribute("controller");
   // Universe universe = (Universe) request.getAttribute("universe");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" dir="ltr">
<head>
    <!-- Meta Tags -->
    <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=utf-8" />
    <meta name="robots" content="index,follow" />
    <meta name="publisher" content="randomnoun" />
    <meta name="copyright" content="&copy; Copyright 2010, randomnoun" />
    <meta name="revisit-after" content="2 days" />
    <meta name="keywords" content="nothing-in-particular" />
    <title><%= appConfig.getProperty("webapp.titlePrefix") %> DMX</title>

    <link rel="shortcut icon" href="image/favicon.png" />
    <link rel="stylesheet" href="css/farbtastic.css" type="text/css" />
    <link rel="stylesheet" href="css/fancyController.css" type="text/css" />

    <!-- JavaScript -->
    <script src="mjs?js=jquery-3.6.3.min,jquery-ui-1.13.2.min,farbtastic"></script> 
    <script>jQuery.noConflict(); $ = jQuery;</script>
    
<script>
<r:setJavascriptVar name="stage" value="${stage}" />
<r:setJavascriptVar name="shows" value="${shows}" />
<r:setJavascriptVar name="fixtures" value="${fixtures}" />
<r:setJavascriptVar name="fixtureDefs" value="${fixtureDefs}" />
<r:setJavascriptVar name="dmxCurrentUniverse" value="${dmxCurrentUniverse}" />
<r:setJavascriptVar name="dmxCurrentBank" value="${dmxCurrentBank}" />
<r:setJavascriptVar name="dmxValues" value="${dmxValues}" />
<r:setJavascriptVar name="fixValues" value="${fixValues}" />
<r:setJavascriptVar name="version" value="${version}" />
<r:setJavascriptVar name="origPanel" value="${panel}" />
<r:setJavascriptVar name="javadocUrl" value="${javadocUrl}" />
<r:setJavascriptVar name="pageId" value="${pageId}" />
<r:setJavascriptVar name="logCount" value="${logCount}" />
<r:setJavascriptVar name="isRecording" value="${recRecording!=null}" />
<c:if test="${recRecording!=null}">
<r:setJavascriptVar name="recCurrentFrame" value="${recCurrentFrame}" />
<r:setJavascriptVar name="recTotalFrames" value="${recTotalFrames}" />
<r:setJavascriptVar name="recShowDefId" value="${recShowDefId}" />
<r:setJavascriptVar name="recShowDefName" value="${recShowDefName}" />
<r:setJavascriptVar name="recModifiedFixtureIds" value="${recModifiedFixtureIds}" />
<r:setJavascriptVar name="recModifiedDmxChannels" value="${recModifiedDmxChannels}" />
</c:if>
<c:if test="${recRecording==null}">
var recCurrentFrame = 0;
var recTotalFrames = 1;
</c:if>
<r:setJavascriptVar name="initMessage" value="${initMessage}" />
var dmxTimeSourceText = "-";
<%-- this will be a client-side include eventually --%>
<jsp:include page="js/fancyController.js" />
</script>
</head>
<body onload="initWindow()">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB</span></div>
<div class="lhsMenuContainer">
  <div id="lhsBlackout" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsBlackout.png" title="Blackout"/><div class="lhsMenuText">Blackout</div></div>
  <div id="lhsShows" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsShows.png" title="Shows"/><div class="lhsMenuText">Shows</div></div>
  <div id="lhsFixtures" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsFixtures.png" title="Fixtures"/><div class="lhsMenuText">Fixtures</div></div>
  <div id="lhsDMX" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsDmx.png" title="DMX"/><div class="lhsMenuText">DMX</div></div>
  <div id="lhsLogs" class="lhsMenuItem" style="position:relative;"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsLogs.png" title="Logs"/>
  <style>
  </style>
  <div id="lhsLogNotification" class="popNotification"><div class="popNotificationLeft"></div><div id="lhsLogNotificationText" class="popNotificationMiddle"></div><div class="popNotificationRight"></div></div>
  <div class="lhsMenuText">Logs</div></div>
  <div id="lhsConfig" class="lhsMenuItem"><img class="lhsMenuIcon" width="70" height="70" src="image/lhsConfig.png" title="Config"/><div class="lhsMenuText">Config</div></div>
  
  <div id="recContainer">
    <div class="recContainerLabel"><img id="recRecordAnim" class="recContainerIcon" width="70" height="29" src="image/recRecordAnim.gif"/></div>
    <div class="recFrameContainer"><div id="recPrevFrame" class="recButtonL" style="margin-left: 0px; "><img src="image/rbutton-prev.png" width="53" height="45"/></div><div id="recCurrentFrame"></div><div id="recTotalFrames"></div><div id="recNextFrame" class="recButtonR"><img src="image/rbutton-next.png" width="53" height="45"/></div></div>
    <div id="recDeleteFrame" class="recButtonDel" title="Delete current frame"><img src="image/rbutton-del.png" width="53" height="45"/></div><div id="recPlay" class="recButtonPlay" title="Replay the current recording"><img src="image/rbutton-play.png" width="53" height="45"/></div><div id="recAddFrame" class="recButtonAdd" title="Create a new frame after the current frame"><img src="image/rbutton-add.png" width="53" height="45"/></div>
  </div>
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
  <div id="shwCancel"><img width="80" height="70" src="image/stopWhite.png" title="Stop all"/></div>
  <div id="shwAudio">
    <div id="shwAudioBMTLegend"></div>
    <div id="shwAudioBassOuter" class="shwAudioOuter" /><div id="shwAudioBassInner" class="shwAudioInner"></div></div>
    <div id="shwAudioMidOuter" class="shwAudioOuter" /><div id="shwAudioMidInner" class="shwAudioInner"></div></div>
    <div id="shwAudioTrebleOuter" class="shwAudioOuter" /><div id="shwAudioTrebleInner" class="shwAudioInner"></div></div>
  </div>
  <div id="shwPageUp"><img width="80" height="70" src="image/pageUp.png" title="Page up"/></div>
  <div id="shwPageDown"><img width="80" height="70" src="image/pageDown.png" title="Page down"/></div>
  <div id="shwItemContainer"></div>
</div>


<div id="fixPanel" style="display: none;">
  <div id="fixBlackout" class="fixControl">Blackout</div>
  <div id="fixStandardControls">
  <div id="fixDimScrollArea">
  <div id="fixDim"><div id="fixDimHandle" class="ui-slider-handle"></div></div>
  </div>
  <div id="fixDimLabel">Dimmer</div>
  <div id="fixStrobeScrollArea">
  <div id="fixStrobe"><div id="fixStrobeHandle" class="ui-slider-handle"></div></div>
  </div>
  <div id="fixStrobeLabel">Strobe</div>
  <!--  <div id="fixColor" class="fixControl">Colour</div> -->
  <input type="text" id="fixColor" name="fixColor" value="#123456" />
  <div id="fixColorPicker"></div>
  <div id="fixAimDragArea" class="fixControl"><div id="fixAim"><div id="fixAimHandle"></div><div id="fixAimActual"></div></div></div>
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
  <div id="fixPageUp"><img width="80" height="70" src="image/pageUp.png" title="Page up"/></div>
  <div id="fixPageDown"><img width="80" height="70" src="image/pageDown.png" title="Page down"/></div>

</div>

<div id="dmxPanel" style="display: none;">
  <div id="dmxUniverse">
    <div class="dmxUniverseContainer"><div id="dmxPrevBank" class="dmxButtonL" style="margin-left: 0px; "><img src="image/rbutton-prev.png" width="53" height="45"/></div><div id="dmxCurrentUniverse">1</div><div id="dmxCurrentBank">2</div><div id="dmxNextBank" class="recButtonR"><img src="image/rbutton-next.png" width="53" height="45"/></div></div>
  </div>
<%--
  <div id="dmxImmediate" class="dmxControl">Immediate ON</div>
  <div id="dmxUpdateAll" class="dmxControl dmxControlDisabled">Update all</div>
--%> 
  <div id="dmxTimeSource" class="dmxTimeSource"></div>
  <div id="dmxValues">
  </div>
  <div id="dmxSliderScrollArea">
  <div id="dmxSlider"><div id="dmxSliderHandle" class="ui-slider-handle"></div></div>
  </div>
  <!-- 
  <div id="dmxHighlight" style="display:none;"></div>
  <div id="dmxHighlight2" style="display:none;"></div>
   -->
</div>

<div id="logPanel" style="display: none;">
  <div id="logExceptionContainer"></div>
  <div id="logPageUp"><img width="80" height="70" src="image/pageUp.png" title="Page up"/></div>
  <div id="logPageDown"><img width="80" height="70" src="image/pageDown.png" title="Page down"/></div>
  <div id="logClear"><img width="80" height="70" src="image/cancel.png" title="Clear"/></div>
</div>

<div id="cnfPanel" style="display: none;">
  <div id="cnfStage" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfStage.png" title="Stages"/><div class="cnfMenuText">Stages</div></div>
  <div id="cnfDevice" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfDevice.png" title="DMX interfaces"/><div class="cnfMenuText">DMX interfaces</div></div>
  <div id="cnfAudioController" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfAudioController.png" title="Audio controllers"/><div class="cnfMenuText">Audio controllers</div></div>
  <div id="cnfAudioSource" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfAudioSource.png" title="Audio sources"/><div class="cnfMenuText">Audio sources</div></div>
  <div id="cnfFixtureDef" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfFixtures.png" title="Fixture definitions"/><div class="cnfMenuText">Fixture definitions</div></div>
  <div id="cnfFixture" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/lhsFixtures.png" title="Fixtures"/><div class="cnfMenuText">Fixtures</div></div>
  <div id="cnfShowDef" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfShows.png" title="Show definitions"/><div class="cnfMenuText">Show definitions</div></div>
  <div id="cnfShow" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/lhsShows.png" title="Shows"/><div class="cnfMenuText">Shows</div></div>
  <div id="cnfRecord" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfRecord2.png" title="Stages" style="margin-left:3px;"/><div id="cnfRecordText" class="cnfMenuText">Record a show</div></div>
  <div id="cnfResetAudio" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfResetAudio.png" title="Reset audio controller"/><div class="cnfMenuText">Reset audio controller</div></div>
<%--  <div id="cnfResetDMX" class="cnfControl">Reset DMX device</div>  --%>
  
  
<% 
    if (appConfig.getProperty("dev.vlc.streamingUrl")!=null &&
	  !appConfig.getProperty("dev.vlc.streamingUrl").equals("")) {
%>  
  <div id="cnfVideo" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfVLC.png" title="Video stream"/><div class="cnfMenuText">Video stream</div></div>
<%
    }
%>
  <div id="cnfImportExport" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfImportExport.png" title="Import / Export"/><div class="cnfMenuText">Import / Export</div></div>
<%--
  <div id="cnfSimple" class="cnfControl"><img class="cnfMenuIcon" width="70" height="70" src="image/cnfSimpleController.png" title="Simple controller"/><div class="cnfMenuText">Simple controller</div></div>
--%>
    
</div>
</div>
<iframe id="cometFrame" src="about:blank"></iframe>

<jsp:include page="/misc/analytics.jsp" />
</body>
</html>
