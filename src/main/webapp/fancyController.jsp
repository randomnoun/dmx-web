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

    <!-- JavaScript -->
    <script src="mjs?js=prototype,scriptaculous,builder,effects,dragdrop,controls,slider,sound,rollover" type="text/javascript"></script>
<style>
BODY { font-size: 8pt; font-family: Arial; }
.lhsMenuContainer {
  position: absolute; top: 30px; left: 5px; width: 200px; height: 1000px;
  background-color: red;
}
.rhsPanel {
  position: absolute; top: 30px; left: 225px; width: 900px; height: 1000px;
  background-color: blue;
}
#rhsMessage {
  position: absolute; top: 5px; left: 225px; width: 900px; height: 20px;
  text-align: left; color: white; font-size: 10pt; font-weight: bold;
  background-color: blue;
}
.lhsMenuItem {
  width: 180px; height: 70px; background-color: green; margin: 10px;
  text-align: center; color: white; font-size: 18pt; 
}
.lhsSelect {
  background-color: #DDFFDD;
}
.clickHighlight {
  background-color: white;
}

/*** SHOW panel ***/
#shwCancel {
  position: absolute; top: 20px; left: 20px; width: 180px; height: 70px;
  background-color: red;
  text-align: center; color: white; font-size: 18pt;
}
.shwItem {
  position: absolute; width: 180px; height: 70px; background-color: green; 
  text-align: center; color: white; font-size: 18pt; 
}

/*** FIXTURE panel ***/
.fixItem {
  position: absolute; width: 180px; height: 70px; background-color: green; 
  text-align: center; color: white; font-size: 18pt; 
}
#fixBlackout {
  position: absolute; top: 20px; left: 20px; width: 180px; height: 70px;
  background-color: red;
}
#fixDim {
  position: absolute; top: 20px; left: 220px; width: 90px; height: 160px;
}
#fixGroup {
  position: absolute; top: 110px; left: 20px; width: 180px; height: 70px;
}
#fixColor {
  position: absolute; top: 20px; left: 330px; width: 240px; height: 160px;
}
#fixAim {
  position: absolute; top: 20px; left: 590px; width: 240px; height: 160px;
}
.fixControl {
  text-align: center; color: white; font-size: 18pt;
  background-color: green;
}
.fixSelect {
  background-color: #DDFFDD;
}

/*** DMX panel ***/
#dmxImmediate {
  position: absolute; top: 5px; left: 20px; width: 180px; height: 70px;
  background-color: green;
}
#dmxUpdateAll {
  position: absolute; top: 5px; left: 220px; width: 180px; height: 70px;
  background-color: green;
}
#dmxTimeSource {
  position: absolute; top: 5px; left: 420px; width: 300px; height: 70px;
  background-color: green;
}
.dmxValue {
  position: absolute; width: 44px; height: 26px; background-color: green;
  text-align: right; color: white; font-weight: bold; font-size: 14pt; padding-top: 2px; padding-right: 4px;
}
#dmxHighlight, #dmxHighlight2 {
  position: absolute; width: 0px; height: 60px;
  border: 5px solid white;
  background-color: transparent;
}
.dmxHighlightFooter {
  position: absolute; top: 40px; height: 20px; width: 100%; 
  color: black; background-color: white;
}


/*** LOG panel ***/
.logException {
  position: absolute; width: 800px; height: 30px; 
  color: white; background-color: red; font-size:14pt;
}



#controller { font-size: 8pt; font-family: Arial;}
#controller TD { font-size: 8pt; font-family: Arial;}
#controller INPUT { font-size: 8pt; }
#config { font-size: 8pt; font-family: Arial;}
#config TD { font-size: 8pt; font-family: Arial;}
.label { width: 25px; height: 16px; text-align: right; background-color: lightblue; padding-top: 3px; margin-left: 3px; margin-bottom: 1px;}
</style>
<script>
<r:setJavascriptVar name="shows" value="${shows}" />
<r:setJavascriptVar name="fixtures" value="${fixtures}" />
<r:setJavascriptVar name="fixtureDefs" value="${fixtureDefs}" />
var dmxToFixture=new Array();
var dmxHighlightTimeout=-1;
var lhsMenuPanels=new Array("shwPanel", "fixPanel", "dmxPanel", "logPanel");
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

// @TODO timeout this text or something
function setRhsMessageHTML(text) {
	$("rhsMessage").innerHTML = text;
}

function sendRequest(url) {
	new Ajax.Request(url, {
	    method:'get', // evalJSON:true,
	    onSuccess: function(transport) {
	        setRhsMessageHTML(transport.responseJSON.message);
	    } });
}


/******************************* LHS MENU ******************************/

function initLhsMenu() {
	Event.observe($("lhsBlackout"), 'click', lhsBlackout);
	Event.observe($("lhsShows"), 'click', lhsShows);
	Event.observe($("lhsFixtures"), 'click', lhsFixtures);
	Event.observe($("lhsDMX"), 'click', lhsDMX);
	Event.observe($("lhsLogs"), 'click', lhsLogs);
	Event.observe($("lhsConfig"), 'click', lhsConfig);
	$$(".lhsMenuItem").each(function(s){Event.observe(s, 'mousedown', function(){return false});});
}

function clickFx(el) {
	el.addClassName("clickHighlight");
	window.setTimeout(function() { el.removeClassName("clickHighlight") }, 50);
}

function lhsShowPanel(panelName) {
	if (currentPanelName==panelName) { return; }
	for (var i=0; i<lhsMenuPanels.length; i++) {
		if (panelName!=lhsMenuPanels[i]) {
			$(lhsMenuPanels[i]).style.display = "none";
		}
	}
	currentPanelName = panelName;
	if (longPollRequest) { longPollRequest.abort(); }
	var el=$(panelName); if (el) { el.style.display = "block"; }
	// @TODO update with current state
}
function lhsSelect(el) {
	$$(".lhsMenuItem").each(function(s){s.removeClassName("lhsSelect");});
	el.addClassName("lhsSelect");
}
function lhsBlackout() { 
	clickFx($("lhsBlackout"));
    sendRequest('fancyController.html?action=blackOut');
}
function lhsShows() { lhsSelect($("lhsShows")); lhsShowPanel("shwPanel"); startRequests(); }
function lhsFixtures() { lhsSelect($("lhsFixtures"));lhsShowPanel("fixPanel"); }
function lhsDMX() { lhsSelect($("lhsDMX")); lhsShowPanel("dmxPanel"); }
function lhsLogs() { lhsSelect($("lhsLogs")); lhsShowPanel("logPanel"); startRequests(); }
function lhsConfig() { lhsSelect($("lhsConfig")); lhsShowPanel("cnfPanel"); }


function startRequests() {
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
	*/
	if (currentPanelName=="logPanel") {
		new Ajax.Request('fancyController.html?action=getExceptions', {
	        method:'get', // evalJSON:true,
	        onSuccess: function(transport) {
	            logSetExceptions(transport.responseJSON);
	        } });
	}
}

/******************************* SHOW PANEL ******************************/
  
function initShwPanel() {
	var x, y, el;
	for (var i=0; i<shows.length; i++) {
		x=20+(i%4)*200; y=110+Math.floor(i/4)*90;
		el=$("shwItem[" + i + "]");
		el.style.left=x+"px"; el.style.top=y+"px";
		el.setAttribute("showId", i);
		Event.observe(el, 'click', shwItemClick);
	}
	Event.observe($("shwCancel"), 'click', shwCancel);
} 

function shwItemClick(event) {
	var shwItemEl = event.element();
	clickFx(shwItemEl);
	var showId = shwItemEl.readAttribute("showId");
	sendRequest('fancyController.html?action=startShow&showId=' + showId);
}

function shwCancel(event) {
	sendRequest('fancyController.html?action=cancelShow');
}

/******************************* FIXTURE PANEL ******************************/

function initFixPanel() {
	var x,y,fixEl;
	var fp=$("fixPanel");
	for (var i=0; i<fixtures.length; i++) {
		x=20+(i%4)*200; y=200+Math.floor(i/4)*90;
    	f=fixtures[i]; fd=fixtureDefs[f.type];
        var fixEl = new Element("div", { 
            "id": "fixItem[" + i + "]", "fixtureId": i,
            "class" : "fixItem" }).update(f.name);
        fixEl.style.left=x+"px"; fixEl.style.top=y+"px";
        fp.appendChild(fixEl);
        Event.observe(fixEl, 'click', fixItemClick);
    }
	Event.observe($("fixGroup"), 'click', fixGroupClick);
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
	if (fixSelectIndividual) {
		if (fixLastFixSelectedEl!=null) { fixLastFixSelectedEl.removeClassName("fixSelect"); }
	}
	fixLastFixSelectedEl = fixToggleEl(fixItemEl) ? fixItemEl : null;
}

function fixGroupClick(event) {
	var fixGroupEl = event.element();
	fixSelectIndividual=fixToggleEl(fixGroupEl);
	if (fixSelectIndividual) {
		$$(".fixItem").each(function(f){f.removeClassName("fixSelect");});
		if (fixLastFixSelectedEl!=null) { fixLastFixSelectedEl.addClassName("fixSelect"); }
	}
}

function fixSetState(json) {
	/* eventually 
    var el = $("logExceptionContainer");
    el.innerHTML = ""; // reset any existing DIVs
    for (var i=0; i<json.exceptions.length; i++) {
        var e = json.exceptions[i];
        var exEl = new Element("div", { "class" : "logException" }).update(e.message);
        exEl.style.left=20+"px"; exEl.style.top=(20+(i*30))+"px";
        el.appendChild(exEl);
    }
    */
}



/******************************* DMX PANEL ******************************/

function initDmxPanel() {
	var x,y,el;
	for (var i=1; i<=255; i++) { 
		x=20+((i-1)%16)*50; y=90+Math.floor((i-1)/16)*30;
		el=$("dmxValue["+i+"]"); 
		el.style.left=x+"px"; el.style.top=y+"px";
		el.setAttribute("dmxChannel", i);
		Event.observe(el, 'click', dmxValueClick);
		Event.observe(el, 'mouseover', dmxValueOnMouseOver);
	}
	Event.observe($("dmxHighlight"), 'mouseover', dmxShowHighlight);
	Event.observe($("dmxHighlight2"), 'mouseover', dmxShowHighlight);
	Event.observe($("dmxHighlight"), 'mouseout', dmxHideHighlight);
	Event.observe($("dmxHighlight2"), 'mouseout', dmxHideHighlight);
}
function dmxShowHighlight(event) {
	if (dmxHighlightTimeout!=-1) { window.clearTimeout(dmxHighlightTimeout); }
    dmxHighlightTimeout=-1;
}
function dmxHideHighlight(event) {
	if (dmxHighlightTimeout==-1) { dmxHighlightTimeout=window.setTimeout(dmxHideHighlight2, 1000); }
}
function dmxHideHighlight2() {
    $("dmxHighlight").style.visibility="hidden";
    $("dmxHighlight2").style.visibility="hidden";
    if (dmxHighlightTimeout!=-1) { window.clearTimeout(dmxHighlightTimeout); }
    dmxHighlightTimeout=-1;
}
function dmxValueClick(event) {
}
function dmxValueOnMouseOver(event) {
    var dmxValueEl = event.element();
    var h1=$("dmxHighlight"), h2=$("dmxHighlight2");
    //alert(el.id + " - " + $(el).readAttribute("dmxChannel"));
    //alert(dmxToFixture[$(el).readAttribute("dmxChannel")]['name']);
    var i=$(dmxValueEl).readAttribute("dmxChannel");
    var f=dmxToFixture[i]; 
    if (!f) { return; }
    var i=f["dmxOffset"];
    var fd=fixtureDefs[f.type];
    var dc=fd["dmxChannels"];
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
    h1.style.width=(h1w+20)+"px"; h1.style.visibility="visible";
    if (h2x) {
        h2.style.left=(h2x-10)+"px"; h2.style.top=(h2y-10)+"px";
        h2.style.width=(h2w+20)+"px"; h2.style.visibility="visible";
    }
    h1.innerHTML="<div class=\"dmxHighlightFooter\">" + f["name"] + "</div>";
    h2.innerHTML="<div class=\"dmxHighlightFooter\">" + f["name"] + "</div>";
}

/******************************* LOG PANEL ******************************/
  
function initLogPanel() {
	
} 

function logSetExceptions(json) {
	var el = $("logExceptionContainer");
	el.innerHTML = ""; // reset any existing DIVs
	for (var i=0; i<json.exceptions.length; i++) {
		var e = json.exceptions[i];
		var exEl = new Element("div", { "class" : "logException" }).update(e.message);
		exEl.style.left=20+"px"; exEl.style.top=(20+(i*30))+"px";
		el.appendChild(exEl);
	}
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
    initLookups();
    initLhsMenu();
    initDmxPanel();
    initShwPanel();
    initFixPanel();
    lhsShows();
}

</script>
</head>
<body onload="initWindow()">

<div class="lhsMenuContainer">
  <div id="lhsBlackout" class="lhsMenuItem">Blackout</div>
  <div id="lhsShows" class="lhsMenuItem">Shows</div>
  <div id="lhsFixtures" class="lhsMenuItem">Fixtures</div>
  <div id="lhsDMX" class="lhsMenuItem">DMX</div>
  <div id="lhsLogs" class="lhsMenuItem">Logs</div>
  <div id="lhsConfig" class="lhsMenuItem">Config</div>
</div>

<div id="rhsMessage">RHS message text</div>
<div class="rhsPanel">
<div id="shwPanel" >
  <div id="shwCancel">Cancel</div>
<% 
    List shows = (List) request.getAttribute("shows");
    for (int i=0; i<shows.size(); i++) {
    	Map show = (Map) shows.get(i);
%>
  <div id="shwItem[<%= i %>]" class="shwItem"><%= show.get("name") %></div>
<%
    }
%>
</div>


<div id="fixPanel" style="display: none;">
  <div id="fixBlackout" class="fixControl">Blackout</div>
  <div id="fixDim" class="fixControl">Dim</div>
  <div id="fixColor" class="fixControl">Colour</div>
  <div id="fixAim" class="fixControl">Aim</div>
  <div id="fixGroup" class="fixControl">Select individual</div>
  
</div>



<div id="dmxPanel" style="display: none;">
  <div id="dmxImmediate">Immediate ON</div>
  <div id="dmxUpdateAll">Update all</div> 
  <div id="dmxTimeSource"><%= universe.getTimeSource().getClass().getName() %> / <%= new Date(universe.getTimeSource().getTime()) %></div>
  <div id="dmxValues">
<% 
    for (int i=1; i<=255; i++) {
%>
  <div class="dmxValueContainer">
    <div class="dmxValue" id="dmxValue[<%= i %>]"><%= i %></div>
  </div>
<%
    }
%>
  </div>
  <div id="dmxHighlight" ></div>
  <div id="dmxHighlight2" ></div>
</div>

<div id="logPanel" style="display: none;">
  <div id="logExceptionContainer"></div>
</div>


</div>



</body>
</html>
