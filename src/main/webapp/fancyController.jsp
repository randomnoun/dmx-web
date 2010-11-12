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
  position: absolute; top: 30px; left: 5px; width: 200px; height: 500px;
  background-color: red;
}
.rhsPanel {
  position: absolute; top: 30px; left: 225px; width: 900px; height: 500px;
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
#fixDimHandle {
  background-color: red; width: 90px; height: 30px; cursor: move;
}
#fixGroup {
  position: absolute; top: 110px; left: 20px; width: 180px; height: 70px;
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
#fixAimHandle {
  width: 20px; height: 20px;
  background-color: red;
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

// 
function initFixPanel() {
	var x,y,fixEl;
	var fixDimSlider;
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
	Event.observe($("fixBlackout"), 'click', fixBlackout);
	fixDimSlider = new Control.Slider("fixDimHandle", "fixDim", {
		axis: "vertical",
		onSlide: function(v) { fixDimSlide(v); },
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
			return[constrain(x, 0, parentDimensions.width - handleDimensions.width),
			       constrain(y, 0, parentDimensions.height - handleDimensions.height)];
		},
		onDrag: function(draggable, event) {
			handleDimensions=Element.getDimensions(draggable.element);
            parentDimensions=Element.getDimensions(draggable.element.parentNode);
            handlePos=Position.positionedOffset(draggable.element);
            fixAimDrag(handlePos[0]/(parentDimensions.width - handleDimensions.width),
            	       handlePos[1]/(parentDimensions.height - handleDimensions.height));
		},
		revert: false
	});
    Event.observe('fixDimScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // mozilla
    Event.observe('fixDimScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // IE/Opera
    jQuery('#fixColorPicker').farbtastic(/*'#fixColor'*/ fixColorChange);
    
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
		if (fixLastFixSelectedEl!=null && fixLastFixSelectedEl!=fixItemEl) { 
			fixLastFixSelectedEl.removeClassName("fixSelect"); 
		}
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

function fixGetItemIds() {
	var fixItems=new Array();
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixItems.push(f.readAttribute("fixtureId"))};});
    return fixItems.join(",");
}

function fixBlackout(event) {
	sendRequest('fancyController.html?action=fixtureBlackout&fixtureIds=' + fixGetItemIds());
}
function fixDimSlide(v) {
	
}
function fixDimChange(v) {
	v=Math.floor(255*(1-v));
	var fixItemIds=fixGetItemIds();
	if (fixItemIds!="") {
	   sendRequest('fancyController.html?action=fixtureDim&v=' + v + '&fixtureIds=' + fixItemIds);
    }
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
				this.newValueTimeoutId=window.setTimeout(this.updateValue.curry(url), this.finalRequestInterval);
			}
		}
	}
})

var fixColorLimitter = new AjaxLimitter(100, 200);
function fixColorChange(color) {
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

/*
var newColor=null;
var lastColorSetTime=-1;
var newColorTimeoutId=-1;
function fixColorChange(color) {
    var now = new Date().getTime();
	if (now-lastColorSetTime>100) {
		lastColorSetTime=now;
		if (newColorTimeoutId!=-1) { window.clearTimeout(newColorTimeoutId); }
		newColorTimeoutId=-1;
	    var fixItemIds=fixGetItemIds();
	    if (fixItemIds!="") {
	        sendRequest('fancyController.html?action=fixtureColor&color=' + color.substring(1) + '&fixtureIds=' + fixItemIds);
	    }
	} else {
	    if (newColorTimeoutId==-1) {
	    	newColorTimeoutId=window.setTimeout(fixColorChange.curry(color),200);
	    }
	}
}
*/

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
	lhsFixtures();
    initLookups();
    initLhsMenu();
    initDmxPanel();
    initShwPanel();
    initFixPanel();
    
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
  <div id="fixDimScrollArea">
  <div id="fixDim" class="fixControl"><div id="fixDimHandle"></div></div>
  </div>
  <!--  <div id="fixColor" class="fixControl">Colour</div> -->
  <input type="text" id="fixColor" name="fixColor" value="#123456" /></div>
  <div id="fixColorPicker"></div>
  <div id="fixAim" class="fixControl"><div id="fixAimHandle"></div></div>
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
