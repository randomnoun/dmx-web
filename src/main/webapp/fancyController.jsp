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

    <!-- JavaScript -->
    <script src="mjs?js=prototype" type="text/javascript"></script>
    <script src="mjs?js=jquery-1.4.4.min,farbtastic"></script> 
    <script>jQuery.noConflict();</script> 
    <script src="mjs?js=scriptaculous,builder,effects,dragdrop,controls,slider,sound,rollover,johnford" type="text/javascript"></script> 
    
    <%-- <script src="mjs?js=prototype,slider" type="text/javascript"></script>  --%>
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
  position: absolute; top: 30px; left: 225px; width: 900px; height: 700px;
  background-color: #EEEEFF; border: solid 1px blue; 
}
#rhsMessage {
  position: absolute; top: 5px; left: 225px; width: 896px; height: 16px;
  text-align: left; color: #000044; font-size: 10pt; font-weight: bold;
  background-color: #EEEEFF; border: solid 1px blue; padding: 2px;
}
.lhsMenuItem {
  width: 180px; height: 70px; background-image: url("image/button-blue.png");
  /*background-color: #AAAAFF; */ ; margin: 10px;
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.lhsSelect {
  /*background-color: #6666FF; */ background-image: url("image/button-blue2.png");
}
.clickHighlight {
  background-color: white;
}

/**** LOGO panel ***/
#lgoPanel {
  position: relative; width: 900px; height: 700px; 
}
#lgoImage {
  position: absolute; left: 20px; top:20px;
}
#lgoText1 {
  position: absolute; left:370px; top:20px;
}
#lgoText2 {
  position: absolute; left:370px; top:200px; height: 50px;
}
.lgoBrand {
  font-family: Georgia,"Times New Roman",serif;
  font-size: 12pt;
  color: black;
  
}


/*** SHOW panel ***/
#shwCancel {
  position: absolute; top: 20px; left: 20px; width: 180px; height: 70px;
  /*background-color: red; */
  background-image: url("image/button-red.png");
  text-align: center; color: white; font-size: 18pt;
}
.shwItem {
  position: absolute; width: 180px; height: 70px; /*background-color: #AAAAFF; */
  background-image: url("image/button-blue.png"); 
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
.shwRunning {
  /* background-color: #AAFFAA; */
  background-image: url("image/button-green.png");
}
.shwException {
  /* background-color: red; */
  background-image: url("image/button-red.png");
}


/*** FIXTURE panel ***/
.fixItem {
  position: absolute; width: 180px; height: 70px; /*background-color: #AAAAFF; */
  background-image: url("image/button-blue.png");  
  text-align: center; color: #000044; font-size: 18pt;
  cursor: pointer; 
}
#fixBlackout {
  position: absolute; top: 20px; left: 20px; width: 180px; height: 70px;
  /*background-color: red; */
  background-image: url("image/button-red.png");
  color: white;
}
#fixDim {
  position: absolute; top: 20px; left: 220px; width: 90px; height: 160px;
  background-image: url("image/dimBack.png");
}
#fixDimHandle {
  /*background-color: blue; */ width: 90px; height: 30px; cursor: move;
  background-image: url("image/dimHandleBlack.png");
}
#fixDimLabel {
  position: absolute; top: 185px; left: 220px; width: 90px; height: 20px;
  text-align: center; font-family: Lucida Console; font-size: 8pt; color: black;
}
#fixGroup {
  position: absolute; top: 110px; left: 20px; width: 180px; height: 70px;
}
.fixGroup {
  background-image: url("image/button-blue.png");
}
#fixColorPicker {
  /* 195x195 pixels */
  position: absolute; top: 0px; left: 330px; /*width: 160px; height: 160px;*/
}
#fixColor {
  display: none;
}
#fixAim {
  position: absolute; top: 20px; left: 550px; width: 160px; height: 160px; cursor: crosshair;
}
.fixAimVGrid1 { position: absolute; top: 0px; height:160px; width:0px; border-right: 1px dotted #7777FF; } 
.fixAimVGrid2 { position: absolute; top: 0px; height:160px; width:0px; border-right: 1px solid #7777FF; }
.fixAimHGrid1 { position: absolute; top: 0px; height:0px; width:160px; border-bottom: 1px dotted #7777FF; }
.fixAimHGrid2 { position: absolute; top: 0px; height:0px; width:160px; border-bottom: 1px solid #7777FF; }
#fixAimHandle {
  width: 20px; height: 20px;
  /*background-color: blue; */
  background-image: url("image/aimHandleBlack.png");
  z-index: 10;
}
#fixAimLabel {
  position: absolute; top: 185px; left: 550px; width: 160px; height: 20px;
  text-align: center; font-family: Lucida Console; font-size: 8pt; color: black;
}
#fixAimLeft, #fixAimRight, #fixAimTop, #fixAimBottom {
  position: absolute; font-family: Lucida Console; font-size: 8pt; color: black;
}
#fixAimLeft { top:5px; left:550px; height: 15px; width:60px; }
#fixAimRight { top:5px; left:650px; height: 15px; width:60px; text-align: right; }
#fixAimTop { top:20px; left:715px; height: 30px; width:20px; vertical-align: top; }
#fixAimBottom { top:150px; left:715px; height: 30px; width:20px; vertical-align: bottom; }
.fixControl {
  text-align: center; color: #000044; font-size: 18pt;
  background-color: #AAAAFF;
  cursor: pointer; 
}
.fixSelect {
  /*background-color: #6666FF; */
  background-image: url("image/button-blue2.png");
}
.fixOutput {
  position: absolute; top:40px; left: 5px; height: 20px; width: 170px;
  font-size: 16pt;
}
.fixOutputColor {
  display: inline-block;
  background-color: black; width: 15px; height: 15px;
  border: solid 1px black;
}
.fixOutputDim {
  display: inline-block;
  background-color: white; width: 15px; height: 15px;
  border: solid 1px white;
}
.fixOutputDim2 {
  display: absolute; top:0px; left:0px;
  background-color: black; width: 15px; height: 15px;
}
.fixOutputPan, .fixOutputTilt {
  display: inline-block;
  color: #000044; font-family: Arial; font-size: 10pt; 
}


/*** DMX panel ***/
#dmxPanel { position: relative; }
#dmxImmediate {
  position: absolute; top: 5px; left: 20px; width: 180px; height: 70px;
}
#dmxUpdateAll {
  position: absolute; top: 5px; left: 220px; width: 180px; height: 70px;
}
#dmxTimeSource {
  position: absolute; top: 5px; left: 420px; width: 300px; height: 70px;
}
.dmxControl {
  text-align: center; color: #000044; font-size: 18pt;
  background-image: url("image/button-blue.png");
  /*background-color: #AAAAFF;*/
  cursor: pointer; 
}
.dmxTimeSource {
  text-align: left; color: #000044; font-size: 10pt;
  background-color: #AAAAFF; 
}
.dmxOffset {
  position: absolute; width: 20px; height: 11px; /*background-color: #DDDDFF;*/
  text-align: right; color: #000044; font-weight: normal; font-size: 7pt; padding: 0px; 
}
.dmxValueContainer {
  position: absolute;
}
.dmxValue {
  position: absolute; width: 48px; height: 26px; /*background-color: #AAAAFF;*/
  background-image: url("image/dmx-back.png");
  text-align: right; color: #000044; font-weight: bold; font-size: 14pt;
}
.dmxSelect {
  background-image: url("image/dmx-back-green.png");
}
.dmxModified {
  background-image: url("image/dmx-back-lit.png");
}
.dmxSelectGroup {
  background-image: url("image/dmx-back-lit.png");
}

#dmxHighlight, #dmxHighlight2 {
  position: absolute; width: 0px; height: 60px;
  border: 5px solid #000044;
  background-color: transparent;
}
.dmxHighlightFooter {
  position: absolute; top: 40px; height: 20px; width: 100%; 
  color: white; background-color: #000044;
}


/*** LOG panel ***/
.logTitle {
  width: 800px; height: 30px; 
  color: white; background-color: red; font-size:14pt;
  margin: 5px;
}
.logDetail {
  width: 800px; color: black; border: solid 1px red; font-size:7pt;
  margin: 5px; background-color: #FFFEBB; padding-left: 10px;
}
.logDetail PRE {
  font-family: Lucida Console, Courier New;
}

.logExpandImg {
  position: absolute; top: 10px; left: 10px; cursor: pointer;
}
.logMessage {
  position: absolute; top: 5px; left: 30px;
}


/*** CONFIG panel ***/
.cnfControl {
  width: 360px; height: 70px; /*background-color: #AAAAFF; */
  background-image: url("image/cnfControlBack.png");   
  text-align: center; color: #000044; font-size: 18pt; 
  margin: 10px;
  cursor: pointer;
}


</style>
<script>
<r:setJavascriptVar name="shows" value="${shows}" />
<r:setJavascriptVar name="fixtures" value="${fixtures}" />
<r:setJavascriptVar name="fixtureDefs" value="${fixtureDefs}" />
<r:setJavascriptVar name="dmxValues" value="${dmxValues}" />
<r:setJavascriptVar name="fixValues" value="${fixValues}" />
<r:setJavascriptVar name="version" value="${version}" />
var dmxValues = dmxValues.split(",");
var dmxModified = new Array();
var dmxToFixture = new Array();
var dmxHighlightTimeout = -1;
var logExceptions = new Array();
var lhsMenuPanels=new Array("lgoPanel", "shwPanel", "fixPanel", "dmxPanel", "logPanel", "cnfPanel");
var longPollRequest=null;
var currentPanelName=null;
function startShow(showId) {
    document.location = "controller.html?action=startShow&showId=" + showId;
}
function cancelShow(showId) {
    document.location = "controller.html?action=cancelShow&showId=" + showId;
}

function initLookups() {
    for (var i=0; i<fixtures.length; i++) {
        var f=fixtures[i]; var c=fixtureDefs[f.type]['dmxChannels'];
        for (var d=0; d<c; d++) {
            dmxToFixture[d+f['dmxOffset']]=f;
        }
    }
}
// return value formatted as string with 2 decimal places
function twoDigits(v) {
  var t = Math.floor(v*100)/100;
  //t.replace(/^(.*)\.([0-9]$/, "${1}\.${2}0");
  return t;
}
// @TODO timeout this text or something
function setRhsMessageHTML(text) {
    $("rhsMessage").innerHTML = text;
}

// @TODO what's the bet that completedFunction isn't scoped right here
function sendRequest(url,completedFunction) {
    new Ajax.Request(url, {
        method:'get', // evalJSON:true,
        onSuccess: function(transport) {
            setRhsMessageHTML(transport.responseJSON.message);
        },
        onComplete: function(transport) {
            completedFunction();
        }});
}


/******************************* LHS MENU ******************************/

function initLhsMenu() {
    Event.observe($("lhsLogo"), 'click', lhsLogo);
    Event.observe($("lhsBlackout"), 'click', lhsBlackout);
    Event.observe($("lhsShows"), 'click', lhsShows);
    Event.observe($("lhsFixtures"), 'click', lhsFixtures);
    Event.observe($("lhsDMX"), 'click', lhsDMX);
    Event.observe($("lhsLogs"), 'click', lhsLogs);
    Event.observe($("lhsConfig"), 'click', lhsConfig);
    $$(".lhsMenuItem").each(function(s){Event.observe(s, 'mousedown', function(){return false});});
    Event.observe($("lhsLogo"), 'mousedown', function(){return false});
}

function clickFx(el) {
    el.addClassName("clickHighlight");
    window.setTimeout(function() { el.removeClassName("clickHighlight") }, 50);
}

function lhsShowPanel(panelName) {
    if (currentPanelName==panelName) { return; }
    //if (currentPanelName=="dmxPanel") { dmxHideHighlight2(); }
    for (var i=0; i<lhsMenuPanels.length; i++) {
        if (panelName!=lhsMenuPanels[i]) {
            $(lhsMenuPanels[i]).style.display = "none";
        }
    }
    currentPanelName = panelName;
    if (longPollRequest) { longPollRequest.abort(); }
    var el=$(panelName); if (el) { 
        el.style.display = "block";
    }
    
    // @TODO update with current state
}
function lhsSelect(el) {
    $$(".lhsMenuItem").each(function(s){s.removeClassName("lhsSelect");});
    if (el!=null) {
        el.addClassName("lhsSelect");
    }
}
function lhsBlackout() { 
    clickFx($("lhsBlackout"));
    sendRequest('fancyController.html?action=blackOut');
}

function lhsLogo() { lhsSelect(null); lhsShowPanel("lgoPanel"); }
function lhsShows() { lhsSelect($("lhsShows")); lhsShowPanel("shwPanel"); startPollRequests(); }
function lhsFixtures() { lhsSelect($("lhsFixtures"));lhsShowPanel("fixPanel"); startPollRequests(); }
function lhsDMX() { lhsSelect($("lhsDMX")); lhsShowPanel("dmxPanel"); startPollRequests(); }
function lhsLogs() { lhsSelect($("lhsLogs")); lhsShowPanel("logPanel"); startPollRequests(); }
function lhsConfig() { lhsSelect($("lhsConfig")); lhsShowPanel("cnfPanel"); }


function startPollRequests() {
    new Ajax.Request('fancyController.html?action=poll&panel=' + currentPanelName, {
        onSuccess: function(transport) {
            updatePanel(transport.responseJSON);
        },
        onException: function(request, exception) {
        	alert(exception);
        }
    })
    /*
    if (currentPanelName=="shwPanel") {
        new Ajax.Request('fancyController.html?action=poll&panel=show', {
            method:'post',
            onSuccess: function(transport) {
                shwUpdatePanel(transport.responseJSON);
            },
            onFailure: function(transport) {
                // mark comms as down ?        
            },
            onComplete: function(transport) {
                // catch-all called after all other event lifecycle handlers
            } });
    }
    if (currentPanelName=="logPanel") {
        new Ajax.Request('fancyController.html?action=getExceptions', {
            method:'get', // evalJSON:true,
            onSuccess: function(transport) {
                logSetExceptions(transport.responseJSON);
            } });
    }
    */
}

function updatePanel(json) {
    var jsonPanel = json.panel;
    if (jsonPanel==currentPanelName) {
        if (jsonPanel=="shwPanel") { shwUpdatePanel(json); }
        else if (jsonPanel=="dmxPanel") { dmxUpdatePanel(json); }
        else if (jsonPanel=="fixPanel") { fixUpdatePanel(json); }
        else if (jsonPanel=="logPanel") { logUpdatePanel(json); }
        if (!json.stopPollRequests) {
            window.setTimeout(startPollRequests, 500);
        }
    }
}

/******************************* CONFIG PANEL ******************************/

function lgoInitPanel() {
	$("lgoText1").update("<img src=\"image/albionComedy.png\"/><br/>" +
		"<div class=\"lgoBrand\">Albion Comedy Bar and Restaurant</div>"
		);
			
    $("lgoText2").update("<b>DMX-WEB</b><br/><br/>" +
      "Release: " + version["release"] + "<br/>" +
      "Build number: " + version["buildNumber"] + "<br/><br/>" +
      "<ul>\n" +
      "<li><a href=\"javadoc/dmx/index.html\" target=\"_new\">Java API documentation</a>\n" +
      "</li>\n" +
      "</ul>");
}


/******************************* SHOW PANEL ******************************/
  
function shwInitPanel() {
    var x, y, el;
    var sp=$("shwPanel");
    for (var i=0; i<shows.length; i++) {
        var show = shows[i];
        x=20+(i%4)*200; y=110+Math.floor(i/4)*90;
        var shwEl = new Element("div", { 
            "id": "shwItem[" + show["id"] + "]", "showId": show["id"],
            "class" : "shwItem" }).update(
            show["name"] 
            );
        shwEl.style.left=x+"px"; shwEl.style.top=y+"px";
        sp.appendChild(shwEl);
        Event.observe(shwEl, 'click', shwItemClick);
    }
    Event.observe($("shwCancel"), 'click', shwCancel);
} 

function shwItemClick(event) {
    var shwItemEl = event.element();
    clickFx(shwItemEl);
    var showId = shwItemEl.readAttribute("showId");
    sendRequest('fancyController.html?action=startShow&showId=' + showId, startPollRequests);
}

function shwCancel(event) {
    sendRequest('fancyController.html?action=cancelShow');
}

function shwUpdatePanel(json) {
    var newShows = json.shows;
    for (var i=0; i<newShows.length; i++) {
        var showId = newShows[i]["id"];
        var el = $("shwItem[" + showId + "]");
        if (newShows[i]["state"]=="SHOW_RUNNING") {
            el.addClassName("shwRunning");
            el.removeClassName("shwException");
        } else if (newShows[i]["state"]=="SHOW_STOPPED_WITH_EXCEPTION") {
            el.removeClassName("shwRunning");
            el.addClassName("shwException");
        } else {            
            el.removeClassName("shwRunning");
            el.removeClassName("shwException");
        }
    }
}

/******************************* FIXTURE PANEL ******************************/

var fixColorPicker = null;
var fixDimSlider = null;
var fixUIUpdateOnly = false; // if true, only update UI, don't send AJAX requests
function fixInitPanel() {
    var x,y,fixEl;
    var fp=$("fixPanel");
    for (var i=0; i<fixtures.length; i++) {
        x=20+(i%4)*200; y=210+Math.floor(i/4)*90;
        f=fixtures[i]; fd=fixtureDefs[f.type];
        var fixEl = new Element("div", { 
            "id": "fixItem[" + i + "]", "fixtureId": i,
            "class" : "fixItem" }).update(
            f.name + "<div class=\"fixOutput\"><div class=\"fixOutputDim\"><div class=\"fixOutputDim2\"></div></div>&nbsp;<div class=\"fixOutputColor\"></div>&nbsp;&#8596;<div class=\"fixOutputPan\">0</div>&nbsp;&#8597;<div class=\"fixOutputTilt\">0</div></div>" 
            );
        fixEl.style.left=x+"px"; fixEl.style.top=y+"px";
        fp.appendChild(fixEl);
        
        Event.observe(fixEl, 'click', fixItemClick);
    }
    Event.observe($("fixGroup"), 'click', fixGroupClick);
    Event.observe($("fixBlackout"), 'click', fixBlackout);
    fixDimSlider = new Control.Slider("fixDimHandle", "fixDim", {
        axis: "vertical",
        onSlide: function(v) { fixDimChange(v); },
        onChange: function(v) { fixDimChange(v); }
    });
    new Draggable("fixAimHandle", {
        // constain code modified from http://www.java2s.com/Code/JavaScript/Ajax-Layer/Draganddropsnaptoabox.htm
        snap: function(x,y,draggable) {
            function constrain(n, lower, upper) {
                if (n>upper) { return upper; }
                else if (n<lower) { return lower; }
                else return n;
            }
            handleDimensions=Element.getDimensions(draggable.element);
            parentDimensions=Element.getDimensions(draggable.element.parentNode);
            return[constrain(x, - handleDimensions.width/2, parentDimensions.width - handleDimensions.width/2),
                   constrain(y, - handleDimensions.height/2, parentDimensions.height - handleDimensions.height/2)];
        },
        onDrag: function(draggable, event) {
            handleDimensions=Element.getDimensions(draggable.element);
            parentDimensions=Element.getDimensions(draggable.element.parentNode);
            handlePos=Position.positionedOffset(draggable.element);
            fixAimDrag((handlePos[0]+handleDimensions.width/2)/(parentDimensions.width),
                       (handlePos[1]+handleDimensions.height/2)/(parentDimensions.height));
        },
        revert: false
    });
    Event.observe('fixDimScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // mozilla
    Event.observe('fixDimScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // IE/Opera
    //jQuery('#fixColorPicker').farbtastic(/*'#fixColor'*/ fixColorChange);
    fixColorPicker=jQuery.farbtastic(jQuery('#fixColorPicker'), fixColorChange);
    fixUpdateControls(0);
} 

function fixToggleEl(el) {
    if (el.hasClassName("fixSelect")) {
        el.removeClassName("fixSelect"); return false;
    } else {
        el.addClassName("fixSelect"); return true;
    }
}

var fixLastFixSelectedEl = null;
var fixSelectIndividual = false;
function fixItemClick(event) {
    var fixItemEl = event.element();
    while (!fixItemEl.hasClassName("fixItem")) { fixItemEl=fixItemEl.parentNode; }
    if (fixSelectIndividual) {
        if (fixLastFixSelectedEl!=null && fixLastFixSelectedEl!=fixItemEl) { 
            fixLastFixSelectedEl.removeClassName("fixSelect"); 
        }
    }
    fixLastFixSelectedEl = fixToggleEl(fixItemEl) ? fixItemEl : null;

    var fixItems=new Array();
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixItems.push(f.readAttribute("fixtureId"))};});
    if (fixItems.length==1) {
    	var fd = fixtureDefs[fixtures[fixItems[0]].type];
        fixLabelAim(0, fd["panRange"], 0, fd["tiltRange"]);
    	fixUpdateControls(fixItems[0]); 
    } else if (fixItems.length==0) {
        $("fixAimLeft").update("");
        $("fixAimRight").update("");
        $("fixAimTop").update("");
        $("fixAimBottom").update("");
        var cn=$("fixAim").childNodes;
        for (var i=cn.length-1; i>1; i--) {
        	cn.item(i).parentNode.removeChild(cn.item(i));
        }
    }
}

function fixLabelAim(panMin, panMax, tiltMin, tiltMax) {
    $("fixAimLeft").update("&#8592; " + panMin + "&deg;");
    $("fixAimRight").update(panMax + "&deg; &#8594;");
    $("fixAimTop").update("&#8593;<br/>" + tiltMin + "&deg;");
    $("fixAimBottom").update("&#8595;<br/>" + tiltMax + "&deg;");
    var fixAimEl = $("fixAim");
    for (var x=0; x<panMax; x+=45) {
    	var gridEl = new Element("div", { 
            "class" : (x%180==0) ? "fixAimVGrid2" : "fixAimVGrid1",
            "style" : "left: " + (x*160/panMax) + "px;" });
        fixAimEl.appendChild(gridEl);
    }
    for (var y=0; y<tiltMax; y+=45) {
        var gridEl = new Element("div", { 
            "class" : (y%180==0) ? "fixAimHGrid2" : "fixAimHGrid1",
            "style" : "top: " + (y*160/tiltMax) + "px;" });
        fixAimEl.appendChild(gridEl);
    }
    
}

// make the main fixture controls reflect the current state of the
// supplied fixture
function fixUpdateControls(fixtureId) {
    var f=fixtures[fixtureId];
    var fd=fixtureDefs[f.type];
    fixUIUpdateOnly=true;
    fixColorPicker.setColor(fixValues[fixtureId]["c"]);
    var aimHandleEl=$("fixAimHandle");
    var aimHandleDimensions=Element.getDimensions(aimHandleEl);
    var aimParentDimensions=Element.getDimensions(aimHandleEl.parentNode);
    aimHandleEl.style.left=(fixValues[fixtureId]["p"]*aimParentDimensions.width/fd["panRange"] - aimHandleDimensions.width/2) + "px";
    aimHandleEl.style.top=(fixValues[fixtureId]["t"]*aimParentDimensions.height/fd["tiltRange"] - aimHandleDimensions.height/2) + "px";
    fixDimSlider.setValue(1-fixValues[fixtureId]["d"]);
    fixUIUpdateOnly=false
}

function fixGroupClick(event) {
    var fixGroupEl = event.element();
    fixSelectIndividual=fixToggleEl(fixGroupEl);
    if (fixSelectIndividual) {
        $$(".fixItem").each(function(f){f.removeClassName("fixSelect");});
        if (fixLastFixSelectedEl!=null) { fixLastFixSelectedEl.addClassName("fixSelect"); }
    }
}

function fixGetItemIds() {
    var fixItems=new Array();
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixItems.push(f.readAttribute("fixtureId"))};});
    return fixItems.join(",");
}

function fixBlackout(event) {
    sendRequest('fancyController.html?action=fixtureBlackout&fixtureIds=' + fixGetItemIds());
}

// this is triggered far too many times
var AjaxLimitter = Class.create({
    // This limiter will limit submitted Ajax requests so that they 
    // do not occur less than minRequestInterval milliseconds between the start of
    // one request and the start of the next request. This is handy for
    // controls that update frequently (the dimmer slider, the color control).
    // 
    // The finalRequestInterval is the timeout value used to send the
    // final update through to the server. It should be greater than the
    // minRequestInterval. This should be the maximum allowable time between
    // the start of AJAX requests submitted by the browser.
    //
    // NB: this limitter does not ensure that one request finishes before
    // the next is generated
    initialize: function(minRequestInterval,finalRequestInterval) {
        this.minRequestInterval=minRequestInterval;
        this.finalRequestInterval=finalRequestInterval;
        this.newValue=null;
        this.lastValueSetTime=-1;
        this.newValueTimeoutId=-1;
    },
    sendRequest: function(url) {  // could pass in value here to prevent duplicate requests going through
        var now=new Date().getTime();
        if (now-this.lastValueSetTime>this.minRequestInterval) {
            this.lastValueSetTime=now;
            if (this.newValueTimeoutId!=-1) { window.clearTimeout(this.newValueTimeoutId); }
            this.newValueTimeoutId=-1;
            sendRequest(url);
        } else {
            if (this.newValueTimeoutId==-1) {
                this.newValueTimeoutId=window.setTimeout(this.sendRequest.curry(url), this.finalRequestInterval);
            }
        }
    }
})

var fixDimLimitter = new AjaxLimitter(100, 200);
function fixDimChange(v) {
	if (fixUIUpdateOnly) { return; }
    v=Math.floor(255*(1-v));
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixDimLimitter.sendRequest( 
           'fancyController.html?action=fixtureDim&v=' + v + '&fixtureIds=' + fixItemIds);
    }
}

var fixColorLimitter = new AjaxLimitter(100, 200);
function fixColorChange(color) {
	if (fixUIUpdateOnly) { return; }
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixColorLimitter.sendRequest( 
           'fancyController.html?action=fixtureColor&color=' + color.substring(1) + '&fixtureIds=' + fixItemIds);
    }
}

var fixAimLimitter = new AjaxLimitter(100, 200);
function fixAimDrag(x, y) {
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
       fixAimLimitter.sendRequest( 
           'fancyController.html?action=fixtureAim&x=' + (x*100) + '&y=' + (y*100) + '&fixtureIds=' + fixItemIds);
    }
}


function fixUpdatePanel(json) {
    fixValues = json.fixValues;
    for (var i=0; i<fixValues.length; i++) {
        var fixValue = fixValues[i];
        var el = $("fixItem[" + i + "]");
        var divEls = el.getElementsByTagName("DIV");
        divEls[2].style.height=(1-fixValue["d"])*15 + "px";
        divEls[3].style.backgroundColor=fixValue["c"];
        divEls[4].innerHTML=twoDigits(fixValue["p"]);
        divEls[5].innerHTML=twoDigits(fixValue["t"]);
    }
    var fixItems=new Array();
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixItems.push(f.readAttribute("fixtureId"))};});
    if (fixItems.length==1) { fixUpdateControls(fixItems[0]); }
}


/******************************* DMX PANEL ******************************/
var dmxSelectedFixture=null;
function dmxInitPanel() {
    var x,y,el;
    var dv=$("dmxValues");
    for (var i=1; i<=255; i++) { 
        x=20+((i-1)%16)*50; y=90+Math.floor((i-1)/16)*30;
        var dmxEl=new Element("div", { "class" : "dmxValue",
          "id" : "dmxBox[" + i + "]", 
          "style" : "left:" + x + "px; top:" + y + "px",
          "dmxChannel" : i}).update(
          "<div class=\"dmxOffset\">" + i + "</div>" +
          "<div id=\"dmxValue[" + i + "]\">" + dmxValues[i-1] + "</div>"
          );
        dv.appendChild(dmxEl);
        Event.observe(dmxEl, 'click', dmxValueClick);
        Event.observe(dmxEl, 'mouseover', dmxValueOnMouseOver);
        Event.observe(dmxEl, 'mouseout', dmxValueOnMouseOut);
    }
    /*
    Event.observe($("dmxHighlight"), 'mouseover', dmxShowHighlight);
    Event.observe($("dmxHighlight2"), 'mouseover', dmxShowHighlight);
    Event.observe($("dmxHighlight"), 'mouseout', dmxHideHighlight);
    Event.observe($("dmxHighlight2"), 'mouseout', dmxHideHighlight);
    */
}
/*
function dmxShowHighlight(event) {
    if (dmxHighlightTimeout!=-1) { window.clearTimeout(dmxHighlightTimeout); }
    dmxHighlightTimeout=-1;
}
function dmxHideHighlight(event) {
    if (dmxHighlightTimeout==-1) { dmxHighlightTimeout=window.setTimeout(dmxHideHighlight2, 1000); }
}
function dmxHideHighlight2() {
    $("dmxHighlight").style.display="none";
    $("dmxHighlight2").style.display="none";
    if (dmxHighlightTimeout!=-1) { window.clearTimeout(dmxHighlightTimeout); }
    dmxHighlightTimeout=-1;
}
*/

function dmxValueClick(event) {
}
function dmxValueOnMouseOver(event) {
	var dmxValueEl, el, ch, f, off, dc, j;
    dmxValueEl = event.element();
    ch=$(dmxValueEl).readAttribute("dmxChannel");
    while (!ch && dmxValueEl!=null) {dmxValueEl=dmxValueEl.parentNode; ch=$(dmxValueEl).readAttribute("dmxChannel"); }
    f=dmxToFixture[ch]; 
    if (dmxSelectedFixture!=f && dmxSelectedFixture!=null) {
        off=dmxSelectedFixture["dmxOffset"];
        dc=fixtureDefs[dmxSelectedFixture.type]["dmxChannels"];
    	for (j=off; j<off+dc; j++) {
    		el = $("dmxBox[" + j + "]");
    		el.removeClassName("dmxSelectGroup");
    		el.removeClassName("dmxSelect");
    	}
    }
    el=$("dmxTimeSource");
    if (!f) { 
    	el.update("<%= universe.getTimeSource().getClass().getName() %> / <%= new Date(universe.getTimeSource().getTime()) %>");
    	dmxSelectedFixture=null;
    	return;
    };
    off=f["dmxOffset"];
    fd=fixtureDefs[f.type];
    dc=fd["dmxChannels"];
    if (dmxSelectedFixture!=f) {
        el.update("Name: <b>" + f["name"] + "</b><br/>" +
        	"Type:" + f["type"] + "<br/>" + 
        	"Offset: " + f["dmxOffset"] + "<br/>");
    }
    for (j=off; j<off+dc; j++) {
    	el = $("dmxBox[" + j + "]");
    	if (j==ch) {
    		el.removeClassName("dmxSelectGroup");
    		el.addClassName("dmxSelect");
    	} else {
    		el.addClassName("dmxSelectGroup");
    	}
    }
    dmxSelectedFixture=f;
}
function dmxValueOnMouseOut(event) {
    var dmxValueEl = event.element();
    var ch=$(dmxValueEl).readAttribute("dmxChannel");
    while (!ch && dmxValueEl!=null) {dmxValueEl=dmxValueEl.parentNode; ch=$(dmxValueEl).readAttribute("dmxChannel"); }
    //var f=dmxToFixture[ch];
    //if (!f) { return; }
    f=dmxSelectedFixture;  // current selected fixture
    if (!f) { return; }
    var off=f["dmxOffset"];
    var dc=fixtureDefs[f.type]["dmxChannels"];
    dmxValueEl.removeClassName("dmxSelect");
    if (ch>=off && ch<off+dc) {
    	dmxValueEl.addClassName("dmxSelectGroup");
    }
}


    /*
    var h1=$("dmxHighlight"), h2=$("dmxHighlight2");
    var h1x, h1y, h1w, h2x, h2y, h2w;
    if (Math.floor((i-1)/16)==Math.floor(((i+dc-2)/16))) {
        h1x=20+((i-1)%16)*50; h1y=90+Math.floor((i-1)/16)*30;
        h1w=dc*50;
    } else {
        h1x=20+((i-1)%16)*50; h1y=90+Math.floor((i-1)/16)*30;
        h1w=(16-((i-1)%16))*50;
        h2x=20; h2y=90+(Math.floor((i-1)/16)+1)*30;
        h2w=(dc-16+((i-1)%16))*50;
    }
    
    dmxHideHighlight2(); 
    h1.style.left=(h1x-10)+"px"; h1.style.top=(h1y-10)+"px";
    h1.style.width=(h1w+20)+"px"; h1.style.display="block";
    if (h2x) {
        h2.style.left=(h2x-10)+"px"; h2.style.top=(h2y-10)+"px";
        h2.style.width=(h2w+20)+"px"; h2.style.display="block";
    }
    h1.innerHTML="<div class=\"dmxHighlightFooter\">" + f["name"] + "</div>";
    h2.innerHTML="<div class=\"dmxHighlightFooter\">" + f["name"] + "</div>";
    */


function dmxUpdatePanel(json) {
    var dmxValuesNew = json.dmxValues.split(",");
    for (var i=1; i<=255; i++) {
        var el = $("dmxValue[" + i + "]");
        if (dmxValues[i-1]!=dmxValuesNew[i-1]) {
        	dmxValues[i-1]=dmxValuesNew[i-1];
        	el.innerHTML=dmxValues[i-1];
        	el.addClassName("dmxModified");
        	dmxModified[i-1] = true;
        } else if (dmxModified[i-1]) {
        	dmxModified[i-1] = false;
        	el.removeClassName("dmxModified");
        }
    }
}


/******************************* LOG PANEL ******************************/
  
function logInitPanel() {
    
} 

function logToggle(logId) {
	var e = logExceptions[logId];
	var logDetailEl = $("logDetail[" + logId + "]");
	if (logDetailEl==null) {
		var logTitleEl = $("logTitle[" + logId + "]");
		logDetailEl = new Element("div",
		  { "class" : "logDetail", "id" : "logDetail[" + logId + "]" }).update(
			"<pre>" + e.trace + "</pre>");
		logTitleEl.insert({'after' : logDetailEl});
	} else {
		logDetailEl.style.display = logDetailEl.style.display=='none' ? 'block' : 'none';
	}
}

function logUpdatePanel(json) {
    var el = $("logExceptionContainer");
    el.innerHTML = ""; // reset any existing DIVs
    logExceptions = json.exceptions;
    for (var i=0; i<logExceptions.length; i++) {
        var e = logExceptions[i];
        var logTitleEl = new Element("div", 
          { "class" : "logTitle", 
        	"id" : "logTitle[" + i + "]" ,
            "onclick" : "logToggle(" + i + ")"}).update(
          "<img class=\"logExpandImg\" src=\"image/logExpand.png\"/> " + 
          "<span class=\"logMessage\">" + e.message + "</span>");
        el.insert({'bottom' : logTitleEl});
    }
}

/******************************* CONFIG PANEL ******************************/

function cnfInitPanel() {
    Event.observe($("cnfFixtureDef"), 'click', cnfFixtureDefClick);
    Event.observe($("cnfFixture"), 'click', cnfFixtureClick);
    Event.observe($("cnfShowDef"), 'click', cnfShowDefClick);
    Event.observe($("cnfShow"), 'click', cnfShowClick);
    Event.observe($("cnfSimple"), 'click', cnfSimpleClick);
    Event.observe($("cnfVideo"), 'click', cnfVideoClick);
}

function cnfFixtureDefClick() {
    document.location="maintainFixtureDef.html";
}
function cnfFixtureClick() {
    document.location="maintainFixture.html";
}
function cnfShowDefClick() {
    document.location="maintainShowDef.html";
}
function cnfShowClick() {
    document.location="maintainShow.html";
}
function cnfSimpleClick() {
    document.location="controller.html";
}
function cnfVideoClick() {
    window.open("streaming.html", "streamingWindow");
}


/******************************* LONG POLLING ******************************/

/*
function initLongPolling() {
    // this will block until there is UI data to send to the browser
    longPollRequest=Ajax.Request('fancyController.html?action=longPoll', {
        method:'get', // evalJSON:true,
        onSuccess: function(transport) {
            if (transport.responseJSON.result=="success") {
                // something
            }
        }
    });
}
*/

 
/******************************* INIT ******************************/

function initWindow() {
    lhsFixtures(); // need fixtures to be visible during init, or dimmer slider breaks (?)
    initLookups();
    initLhsMenu();

    lgoInitPanel();
    dmxInitPanel();
    shwInitPanel();
    fixInitPanel();
    logInitPanel();
    cnfInitPanel();
    lhsLogo();
}

</script>
</head>
<body onload="initWindow()">
<div id="lhsLogo"><span style="position: relative; top: 3px; left: 8px;">DMX-WEB</span></div>
<div class="lhsMenuContainer">
  <div id="lhsBlackout" class="lhsMenuItem">Blackout</div>
  <div id="lhsShows" class="lhsMenuItem">Shows</div>
  <div id="lhsFixtures" class="lhsMenuItem">Fixtures</div>
  <div id="lhsDMX" class="lhsMenuItem">DMX</div>
  <div id="lhsLogs" class="lhsMenuItem">Logs</div>
  <div id="lhsConfig" class="lhsMenuItem">Config</div>
</div>

<div id="rhsMessage">Messages</div>

<div class="rhsPanel">

<div id="lgoPanel" >
<img id="lgoImage" src="image/dmx-web.png"/>
<div id="lgoText1" ></div>
<div id="lgoText2" ></div>
<div>

</div>
</div>

<div id="shwPanel" >
  <div id="shwCancel">Cancel</div>
</div>


<div id="fixPanel" style="display: none;">
  <div id="fixBlackout" class="fixControl">Blackout</div>
  <div id="fixDimScrollArea">
  <div id="fixDim"><div id="fixDimHandle"></div></div>
  </div>
  <div id="fixDimLabel">Dimmer</div>
  <!--  <div id="fixColor" class="fixControl">Colour</div> -->
  <input type="text" id="fixColor" name="fixColor" value="#123456" />
  <div id="fixColorPicker"></div>
  <div id="fixAim" class="fixControl"><div id="fixAimHandle"></div></div>
  <div id="fixAimLabel">Pan/Tilt control</div>
  <div id="fixAimLeft">Left</div>
  <div id="fixAimRight">Right</div>
  <div id="fixAimTop">Top</div>
  <div id="fixAimBottom">Bottom</div>
  
  <div id="fixGroup" class="fixControl fixGroup">Select individual</div>
  
</div>

<div id="dmxPanel" style="display: none;">
  <div id="dmxImmediate" class="dmxControl">Immediate ON</div>
  <div id="dmxUpdateAll" class="dmxControl">Update all</div> 
  <div id="dmxTimeSource" class="dmxTimeSource"></div>
  <div id="dmxValues">
  </div>
  <!-- 
  <div id="dmxHighlight" style="display:none;"></div>
  <div id="dmxHighlight2" style="display:none;"></div>
   -->
</div>

<div id="logPanel" style="display: none;">
  <div id="logExceptionContainer"></div>
</div>

<div id="cnfPanel" style="display: none;">
  <div id="cnfFixtureDef" class="cnfControl">Fixture definitions</div>
  <div id="cnfFixture" class="cnfControl">Fixtures</div>
  <div id="cnfShowDef" class="cnfControl">Show definitions</div>
  <div id="cnfShow" class="cnfControl">Shows</div>
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
