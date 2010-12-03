var dmxValues = dmxValues.split(",");
var dmxModified = new Array();
var dmxToFixture = new Array();
var dmxHighlightTimeout = -1;
var dmxImmediate = true;
var logExceptions = new Array();
var lhsMenuPanels=new Array("lgoPanel", "shwPanel", "fixPanel", "dmxPanel", "logPanel", "cnfPanel");
var prfRequestId = 0;
var prfEnabled = true;
var prfStartPollRequestTime = null;
var prfEndPollRequestTime = null;
var prfEndPanelUpdateTime = null;
var longPollRequest=null;
var currentPanelName=null;
var MAX_LENGTH=9223372036854775807;

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

function sendPostRequest(url,parameters,completedFunction) {
    new Ajax.Request(url, {
        method:'post', // evalJSON:true,
        parameters:parameters,
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
	var pollUrl = 'fancyController.html?action=poll&panel=' + currentPanelName; 
	if (prfEnabled) {
	    pollUrl += "&lrid=" + prfRequestId + "&lrd=" + (prfEndPollRequestTime-prfStartPollRequestTime) + "&lru=" + (prfEndPanelUpdateTime-prfEndPollRequestTime)
		prfRequestId++;
		prfStartPollRequestTime=new Date().getTime();
		pollUrl += "&trst=" + prfStartPollRequestTime; 
    }
    new Ajax.Request(pollUrl, {
        onSuccess: function(transport) {
        	prfEndPollRequestTime=new Date().getTime();
            updatePanel(transport.responseJSON);
            prfEndPanelUpdateTime=new Date().getTime();
        },
        onException: function(request, exception) {
        	prfEndPollRequestTime=new Date().getTime();
        	prfEndPanelUpdateTime=0;
        }
    })
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
	$("lgoText1").update("Albion Comedy Bar<br/>and Restaurant");
    $("lgoText2").update("<b>DMX-WEB</b><br/><br/>" +
      "Release: " + version["release"] + "<br/>" +
      "Build number: " + version["buildNumber"] + "<br/><br/>" +
      "RXTX JAR version: " + version["rxtxJarVersion"] + "<br/>" + 
      "RXTX DLL version: " + version["rxtxDllVersion"] + "<br/>" + 
      "<br/><br/>" +
      "<ul>\n" +
      "<li><a href=\"javadoc/dmx/index.html\" target=\"_new\">Java API documentation</a>\n" +
      "</li>\n" +
      "</ul>" +
      "<br/><br/>" +
      "Browser: " + BrowserDetect.browser + " " + BrowserDetect.version + " on " + BrowserDetect.OS + "<br/><br/>" +
      (BrowserDetect.browser=="Chrome" ? "" : "You'd be astonished how much more responsive this user interface appears if you try using <a href=\"www.google.com/chrome\">Chrome</a>")
    );
}


/******************************* SHOW PANEL ******************************/
  
function shwInitPanel() {
    var x, y, el, displayOffset=0, lastShowGroupId=-1;
    var sp=$("shwPanel");
    for (var i=0; i<shows.length; i++) {
    	var show = shows[i];
    	if (lastShowGroupId!=show["showGroupId"]) {
    		var separatorEl = new Element("div", { "class" : "shwItemSeparator" });
    		separatorEl.style.left="20px"; 
    		separatorEl.style.top=((110+Math.floor((i+displayOffset-1)/4)*90)+80)+"px";
    		if ((i+displayOffset)%4!=0) {
    			displayOffset += (4-((i+displayOffset)%4));
    		}
            sp.appendChild(separatorEl);
    		lastShowGroupId = show["showGroupId"];
    	}
        
        x=20+((i+displayOffset)%4)*200; y=110+Math.floor((i+displayOffset)/4)*90;
        var shwEl = new Element("div", { 
            "id": "shwItem[" + show["id"] + "]", 
            "showId": show["id"],
            "title" : show["description"],
            "class" : "shwItem" }).update(
            show["name"] + "<div class=\"shwOverlay\"></div>" 
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

// this could be cleaned up a bit
function shwUpdatePanel(json) {
    var newShows = json.shows;
    var now = new Date().getTime();
    for (var i=0; i<newShows.length; i++) {
        var showId = newShows[i]["id"];
        var el = $("shwItem[" + showId + "]");
        if (newShows[i]["state"]=="SHOW_RUNNING") {
            el.addClassName("shwRunning");
            el.removeClassName("shwException");
            var overlayEl = el.firstDescendant();
            var shwTimeEls = overlayEl.select(".shwTime");
            if (shwTimeEls.length==0) {
                var shwTimeEl = new Element("div",
	               { "class" : "shwTime", 
                	 "value" : newShows[i]["time"], "setAt" : now }).update(
	                 twoDigits(newShows[i]["time"]/1000));
                overlayEl.insert({'top' : shwTimeEl});
            } else {
            	shwTimeEls[0].innerHTML = twoDigits(newShows[i]["time"]/1000);
            }
            if (newShows[i]["label"]) {
            	var shwLabelEls = overlayEl.select(".shwLabel");
            	if (shwLabelEls==0) {
            		var shwLabelEl = new Element("div",
                            { "class" : "shwLabel" }).update(newShows[i]["label"]);
                    overlayEl.insert({'top' : shwLabelEl});
            	} else {
            		shwLabelEls[0].innerHTML = newShows[i]["label"];
            	}
            }
            if (newShows[i]["length"]!=MAX_LENGTH) {
            	var shwLenEls = overlayEl.select(".shwLenOuter");
            	if (shwLenEls==0) {
                    var shwLenEl = new Element("div",
                        { "class" : "shwLenOuter" }).update(
                        "<div class=\"shwLenInner\" style=\"width:" + Math.min((newShows[i]["time"]*40/newShows[i]["length"]),40) + "px;\"></div>");
                    overlayEl.insert({'top' : shwLenEl});
            	} else {
            		shwLenEls[0].firstDescendant().style.width = Math.min((newShows[i]["time"]*40/newShows[i]["length"]),40) + "px";
            	}
            }
        } else if (newShows[i]["state"]=="SHOW_STOPPED_WITH_EXCEPTION") {
            el.removeClassName("shwRunning");
            el.addClassName("shwException");
        } else {            
            el.removeClassName("shwRunning");
            el.removeClassName("shwException");
            var overlayEl = el.firstDescendant();
            overlayEl.innerHTML="";
        }
    }

    var bmt = json.audio;
    /*
    $("shwAudioBassInner").style.width = Math.min(bmt["b"]*40,40) + "px";    
    $("shwAudioMidInner").style.width = Math.min(bmt["m"]*40,40) + "px";
    $("shwAudioTrebleInner").style.width = Math.min(bmt["t"]*40,40) + "px";
    */
    $("shwAudioBassInner").style.width = (bmt["b"]*40) + "px";    
    $("shwAudioMidInner").style.width = (bmt["m"]*40) + "px";
    $("shwAudioTrebleInner").style.width = (bmt["t"]*40) + "px";
    
}

/******************************* FIXTURE PANEL ******************************/

var fixColorPicker = null;
var fixDimSlider = null;
var fixStrobeSlider = null;
var fixUIUpdateOnly = false; // if true, only update UI, don't send AJAX requests
var fixAimDraggable = null;
var fixCustomControlsVisible = false;
var fixCustomControlFixtureDef = null;
var fixCustomControls = new Array();
function fixInitPanel() {
    var x,y,fixEl;
    var fp=$("fixPanel");
    for (var i=0; i<fixtures.length; i++) {
        x=20+(i%4)*200; y=210+Math.floor(i/4)*90;
        f=fixtures[i]; fd=fixtureDefs[f.type];
        var fixEl = new Element("div", { 
            "id": "fixItem[" + i + "]", "fixtureId": i,
            "class" : "fixItem" }).update(
            f.name + "<div class=\"fixOutput\"><div class=\"fixOutputDim\"><div class=\"fixOutputDim2\"></div></div>&nbsp;<div class=\"fixOutputColor\"></div>" +
            (fd.panRange==0 ? "" : "&nbsp;&#8596;<div class=\"fixOutputPan\">0</div>") +
            (fd.tiltRange==0 ? "" : "&nbsp;&#8597;<div class=\"fixOutputTilt\">0</div>") +
            "&nbsp;<div class=\"fixOutputStrobe\"></div>" + 
            "</div>" 
            );
        fixEl.style.left=x+"px"; fixEl.style.top=y+"px";
        fp.appendChild(fixEl);
        
        Event.observe(fixEl, 'click', fixItemClick);
    }
    Event.observe($("fixAllNone"), 'click', fixAllNoneClick);
    Event.observe($("fixGroup"), 'click', fixGroupClick);
    Event.observe($("fixCustom"), 'click', fixCustomClick);
    Event.observe($("fixBlackout"), 'click', fixBlackout);
    Event.observe($("fixAim"), 'click', fixAimClick);
    fixDimSlider = new Control.Slider("fixDimHandle", "fixDim", {
        axis: "vertical",
        onSlide: function(v) { fixDimChange(v); },
        onChange: function(v) { fixDimChange(v); }
    });
    fixStrobeSlider = new Control.Slider("fixStrobeHandle", "fixStrobe", {
        axis: "vertical",
        onSlide: function(v) { fixStrobeChange(v); },
        onChange: function(v) { fixStrobeChange(v); }
    });
    fixStrobeSlider.setValue(1);
    
    $("fixAimActual").absolutize();
    fixAimDraggable = new Draggable("fixAimHandle", {
        // constraint code modified from http://www.java2s.com/Code/JavaScript/Ajax-Layer/Draganddropsnaptoabox.htm
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
    Event.observe('fixStrobeScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(fixStrobeSlider, 0.1));  // mozilla
    Event.observe('fixStrobeScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(fixStrobeSlider, 0.1));  // IE/Opera
    //jQuery('#fixColorPicker').farbtastic(/*'#fixColor'*/ fixColorChange);
    fixColorPicker=jQuery.farbtastic(jQuery('#fixColorPicker'), fixColorChange);
    fixUpdateControls(0);
} 

// would be nice if this also started the Draggable stuff
function fixAimClick(event) {
    /* another day ...
	var aimHolderEl=$("fixAim");
	var aimHandleEl=$("fixAimHandle");
	var pointer  = [Event.pointerX(event), Event.pointerY(event)];
    var offsets  = Position.cumulativeOffset(aimHolderEl);
    handleDimensions=Element.getDimensions(aimHandleEl);
    parentDimensions=Element.getDimensions(aimHolderEl);
    aimHandleEl.style.left=(pointer[0]-offsets[0]-handleDimensions.width/2) + "px";
    aimHandleEl.style.top=(pointer[1]-offsets[1]-handleDimensions.ehight/2) + "px";
    handlePos=Position.positionedOffset(aimHolderEl);
    fixAimDrag((handlePos[0]+handleDimensions.width/2)/(parentDimensions.width),
               (handlePos[1]+handleDimensions.height/2)/(parentDimensions.height));
    */
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
    	var cn=$("fixAim").childNodes;
    	for (var i=cn.length-1; i>1; i--) {
        	cn.item(i).parentNode.removeChild(cn.item(i));
        }
        fixLabelAim(0, fd["panRange"], 0, fd["tiltRange"]);
    	fixUpdateControls(fixItems[0]);
    	$("fixAllNone").removeClassName("fixSmallSelect");
    } else if (fixItems.length==0) {
        $("fixAimLeft").update("");
        $("fixAimRight").update("");
        $("fixAimTop").update("");
        $("fixAimBottom").update("");
        var cn=$("fixAim").childNodes;
        for (var i=cn.length-1; i>1; i--) {
        	cn.item(i).parentNode.removeChild(cn.item(i));
        }
        $("fixAllNone").removeClassName("fixSmallSelect");
    } else if (fixItems.length==fixtures.length) {
    	$("fixAllNone").addClassName("fixSmallSelect");
    } else {
    	$("fixAllNone").removeClassName("fixSmallSelect");
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
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
    var v=fixValues[fixtureId];
    fixUIUpdateOnly=true;
    fixColorPicker.setColor(v["c"]);
    fixDimSlider.setValue(1-v["d"]);
    if (fixValues[fixtureId]["s"]) {
    	fixStrobeSlider.setValue(
    		1-((v["s"]-fd["minStrobeHertz"])/
    		(fd["maxStrobeHertz"]-fd["minStrobeHertz"])));
    } else {
    	fixStrobeSlider.setValue(1);
    }
    fixDimSlider.setValue(1-v["d"]);
    var aimHandleEl=$("fixAimHandle");
    var aimActualEl=$("fixAimActual");
    var aimHandleDimensions=Element.getDimensions(aimHandleEl);
    var aimParentDimensions=Element.getDimensions(aimHandleEl.parentNode);
    aimHandleEl.style.left=(v["p"]*aimParentDimensions.width/fd["panRange"] - aimHandleDimensions.width/2) + "px";
    aimHandleEl.style.top=(v["t"]*aimParentDimensions.height/fd["tiltRange"] - aimHandleDimensions.height/2) + "px";
    aimActualEl.style.left=(v["ap"]*aimParentDimensions.width/fd["panRange"] - aimHandleDimensions.width/2) + "px";
    aimActualEl.style.top=(v["at"]*aimParentDimensions.height/fd["tiltRange"] - aimHandleDimensions.height/2) + "px";
    var ccs=fd["customControls"];
    var ccEl;
    if (ccs && fixCustomControlsVisible) {
    	for (var i=0; i<ccs.length; i++) {
    		ccEl = $("fixCC[" + i + "]");
    		if (ccEl) {
	    		if (ccs[i].uiType=="TOGGLE") {
	    			if (v["ccs"][i]==1) { ccEl.addClassName("fixSmallSelect"); }
	    			else { ccEl.removeClassName("fixSmallSelect"); }
	    		} else if (ccs[i].uiType=="SLIDER") {
	    			fixCustomControls[i].setValue(1-(v["ccs"][i]/255));
	    		}
    		}
    	}
    }
    
    fixUIUpdateOnly=false
}

function fixGroupClick(event) {
    var fixGroupEl = event.element();
    fixSelectIndividual=!fixSelectIndividual;
    if (fixSelectIndividual) {
    	fixGroupEl.addClassName("fixSmallSelect"); 
    } else {
    	fixGroupEl.removeClassName("fixSmallSelect"); 
    }
    if (fixSelectIndividual) {
        $$(".fixItem").each(function(f){f.removeClassName("fixSelect");});
        if (fixLastFixSelectedEl!=null) { fixLastFixSelectedEl.addClassName("fixSelect"); }
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
}

function fixAllNoneClick(event) {
    var fixAllNoneEl = event.element();
    var countFixSelected=0;
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){countFixSelected++;};});
    if (countFixSelected==fixtures.length) {
    	fixAllNoneEl.removeClassName("fixSmallSelect");
    	$$(".fixItem").each(function(f){f.removeClassName("fixSelect");});
    } else {
    	fixAllNoneEl.addClassName("fixSmallSelect");
    	$$(".fixItem").each(function(f){f.addClassName("fixSelect");});
    	if (fixSelectIndividual) {
    		fixSelectIndividual=false;
    		$("fixGroup").removeClassName("fixSmallSelect"); 
    	}
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
}

// show/hide the custom controls panel
function fixCustomClick(event) {
	fixCustomEl = event.element();
	fixCustomControlsVisible=!fixCustomControlsVisible;
	if (fixCustomControlsVisible) {
		fixCustomEl.addClassName("fixSmallSelect");
		$("fixStandardControls").style.visibility="hidden";
		$("fixCustomControls").style.visibility="visible";
		fixUpdateCustomControls();
	} else {
		fixCustomEl.removeClassName("fixSmallSelect");
		$("fixStandardControls").style.visibility="visible";
		$("fixCustomControls").style.visibility="hidden";
	}
}

function fixUpdateCustomControls() {
	var cc,tmft=false,i,fixtureId,fd=null;
    var fixItems=new Array();
    var ccEl=$("fixCustomControls");
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixtureId=f.readAttribute("fixtureId");if(fd==null){fd=fixtureDefs[fixtures[fixtureId].type]}else if (fd!=fixtureDefs[fixtures[fixtureId].type]){tmft=true;}};});
    if (fd==null) {
    	ccEl.update("Select a fixture or set of fixtures of the same type.");
    	fixCustomControlFixtureDef=null;
    } else if (tmft) {
    	ccEl.update("Too many fixture types selected. Select a fixture or set of fixtures of the same type.")
    	fixCustomControlFixtureDef=null;
    } else {
    	if (fixCustomControlFixtureDef!=fd) {
    		fixCustomControlFixtureDef=null;
    		ccEl.update("");  // have to remove event listeners on removed controls ?
    		if (fd["customControls"]) {
	    		for (i=0; i<fd["customControls"].length; i++) {
	    			cc=fd["customControls"][i];
	    			if (cc["uiType"]=="TOGGLE") {
	    		        var ctrlEl = new Element("div", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomToggle" }).update(cc.label);
	    		        ccEl.appendChild(ctrlEl);
	    		        Event.observe(ctrlEl, 'click', fixCustomToggleClick.curry(i));
	    			} else if (cc["uiType"]=="SLIDER") {
	    				var ctrlEl = new Element("div", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomSlider" });
	    				var handleEl = new Element("div", { "class" : "fixCustomSliderHandle" });
	    				ccEl.appendChild(ctrlEl);
	    				ctrlEl.appendChild(handleEl);
	    				fixCustomControls[i] = new Control.Slider(handleEl, ctrlEl, {
	    			        axis: "vertical",
	    			        sliderValue: 1-(fixValues[fixtureId]["ccs"][i]/255),
	    			        onSlide: fixCustomSliderChange.curry(i),  // value is 2nd param
	    			        onChange: fixCustomSliderChange.curry(i) 
	    			    });
	    				var labelEl = new Element("div", {"class": "fixCustomSliderLabel"}).update(cc.label);
	    				labelEl.style.left=ctrlEl.positionedOffset().left+"px";
	    				ccEl.appendChild(labelEl);
	    				
	    				
	    			}
	    		}
    		} else {
    			ccEl.update("No custom controls for this fixture type");
    		}
    		fixCustomControlFixtureDef=fd;
    	}
    }
}

function fixGetItems() {
    var fixItems=new Array();
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixItems.push(f.readAttribute("fixtureId"))};});
    return fixItems;
}

function fixGetItemIds() {
	return fixGetItems().join(",");
}

function fixBlackout(event) {
    sendRequest('fancyController.html?action=fixtureBlackout&fixtureIds=' + fixGetItemIds());
}

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

var fixStrobeLimitter = new AjaxLimitter(100, 200);
function fixStrobeChange(v) {
	if (fixUIUpdateOnly) { return; }
    v=Math.floor(255*(1-v));
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixDimLimitter.sendRequest( 
           'fancyController.html?action=fixtureStrobe&v=' + v + '&fixtureIds=' + fixItemIds);
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
	if (fixUIUpdateOnly) { return; }
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
       fixAimLimitter.sendRequest( 
           'fancyController.html?action=fixtureAim&x=' + (x*100) + '&y=' + (y*100) + '&fixtureIds=' + fixItemIds);
    }
}

function fixCustomToggleClick(controlId) {
	// should toggle requested value here
	// controlId should be defined here, but doesn't appear to be...
	//var controlId = event.element().getAttribute("controlId");
	var fixItems=fixGetItems();
    if (fixItems.length > 0) {
    	var newValue = 1-fixValues[fixItems[0]]["ccs"][controlId];
    	sendRequest('fancyController.html?action=customControl&controlId=' + controlId + '&value=' + newValue + '&fixtureIds=' + fixItems.join(","));
	}
}

var fixCustomSliderLimitter = new AjaxLimitter(100, 200);
function fixCustomSliderChange(controlId, value) {
	// alert("updating slider " + controlId + " to " + value);
	if (fixUIUpdateOnly) { return; }
	var newValue = Math.floor(255*(1-value));
	var fixItems=fixGetItems();
    if (fixItems.length > 0) {
    	fixCustomSliderLimitter.sendRequest('fancyController.html?action=customControl&controlId=' + controlId + '&value=' + newValue + '&fixtureIds=' + fixItems.join(","));
	}
}


function fixUpdatePanel(json) {
    fixValues = json.fixValues;
    for (var i=0; i<fixValues.length; i++) {
        var fixValue = fixValues[i];
        var fd=fixtureDefs[fixtures[i].type];
        var el = $("fixItem[" + i + "]");
        var divEls = el.getElementsByTagName("DIV");
        var divElIdx = 2;
        divEls[divElIdx++].style.height=(1-fixValue["d"])*15 + "px";
        divEls[divElIdx++].style.backgroundColor=fixValue["c"];
        if (fd.panRange!=0) { divEls[divElIdx++].innerHTML=twoDigits(fixValue["p"]); }
        if (fd.tiltRange!=0) { divEls[divElIdx++].innerHTML=twoDigits(fixValue["t"]); }
        if (fixValue["s"]) {
        	divEls[divElIdx++].innerHTML=twoDigits(fixValue["s"]);
        } else {
        	divEls[divElIdx++].innerHTML="";
        }
    }
    var fixItems=new Array();
    $$(".fixItem").each(function(f){if (f.hasClassName("fixSelect")){fixItems.push(f.readAttribute("fixtureId"))};});
    if (fixItems.length==1) { fixUpdateControls(fixItems[0]); }
}


/******************************* DMX PANEL ******************************/
var dmxSelectedFixture=null;  // fixture being highlighted
var dmxSelectedChannel=null;  // fixture being editted
var dmxSelectedValue=null;
function dmxInitPanel() {
    var x,y,el,f;
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
    for (var i=0; i<fixtures.length; i++) {
    	f=fixtures[i];
    	//var dmxFixtureIconEl=new Element("div", {"class" : "dmxFixtureIcon" }).update(
    	//	"<img src=\"" + fixtureDefs[f.type]["img"] + "\">"	);
    	var dmxFixtureIconEl=new Element("div", {"class" : "dmxFixtureIcon" }).update(
    		"<img src=\"image/favicon.png\">"	);
    	$("dmxBox[" + f["dmxOffset"] + "]").insert({'top':dmxFixtureIconEl});
    }
    Event.observe($("dmxImmediate"), 'click', dmxImmediateClick);
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

function dmxImmediateClick(event) {
	dmxImmediate = !dmxImmediate;
	if (dmxImmediate) {
		$("dmxImmediate").update("Immediate ON");
		$("dmxUpdateAll").addClassName("dmxControlDisabled");
		Event.stopObserving($("dmxUpdateAll"), 'click', dmxUpdateAllClick);
		dmxUpdateAllClick
	} else {
		$("dmxImmediate").update("Immediate OFF");
		$("dmxUpdateAll").removeClassName("dmxControlDisabled");
		Event.observe($("dmxUpdateAll"), 'click', dmxUpdateAllClick);
	}
}

function dmxUpdateAllClick(event) {
	var dmxValueString="";
	for (var i=1; i<=255; i++) {
		dmxValueString += dmxValues[i-1] + ","; 
	}
	// might be hitting some GET limits here
	sendPostRequest("fancyController.html?action=setDmxValues2", { "values" : dmxValueString } );
	
}

function dmxValueClick(event) {
    var dmxValueEl, el, el2, ch, f, off, dc, j, cds, cd = null;
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
    if (dmxSelectedValue) {
    	if (dmxImmediate) {
    		sendRequest("fancyController.html?action=setDmxValue&channel=" + dmxSelectedChannel + "&value=" + dmxSelectedValue.innerHTML);
    	}
    	dmxSelectedValue.removeClassName("dmxSelectedValue");
    }
    el = $("dmxBox[" + ch + "]"); dmxSelectedChannel = ch;
    el2 = $("dmxValue[" + ch + "]"); dmxSelectedValue = el2;
	el2.addClassName("dmxSelectedValue");
	// capture keystrokes from here on
	Event.observe(document, 'keypress', dmxKeypress);
	if (BrowserDetect.browser=="Chrome") { 
		Event.observe(document, 'keydown', dmxKeydown); 
	}
}

// only required for chrome, which doesn't pass backspaces through to dmxKeypress
function dmxKeydown(event) {
	if (event.keyCode==8) { dmxKeypress(event); event.stop(); }
	if (event.keyCode==27) { dmxKeypress(event); event.stop(); }
}

var dmxTest=0;
function dmxKeypress(event) {
	dmxTest++;
	//alert(event.keyCode);
	//window.status="keystroke received " + dmxTest;
	var v = dmxSelectedValue.innerHTML;
	switch (event.charCode) {
	case 8:
		event.keyCode = Event.KEY_BACKSPACE;
		break;
	case 27:
		event.keyCode = Event.KEY_ESC;
		break;
    case 48:
    case 49:
    case 50:
    case 51:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
        v = (v=="0"?"":v) + (event.charCode-48);
        if (v.length>3) { v = v.substring(1); }; // TODO: check not >255
        dmxSelectedValue.innerHTML = v;
        event.stop();
        break;
	};
	
    switch (event.keyCode) {
    case Event.KEY_BACKSPACE:
        v = v.substring(0, v.length-1); if (v=="") { v = "0"; }
        dmxSelectedValue.innerHTML = v;
        event.stop();
        break;
    case Event.KEY_ESC:
    	//dmxSelectedValue.innerHTML = "0";
    	dmxValues[dmxSelectedChannel]=v;
        dmxSelectedValue.removeClassName("dmxSelectedValue");
        Event.stopObserving(document, 'keypress', dmxKeypress);
        Event.stopObserving(document, 'keydown', dmxKeydown);
        event.stop();
    	break;
    case Event.KEY_RETURN:
    	dmxSelectedValue.removeClassName("dmxSelectedValue");
    	if (dmxImmediate) {
    		sendRequest("fancyController.html?action=setDmxValue&channel=" + dmxSelectedChannel + "&value=" + v);
    	}
    	Event.stopObserving(document, 'keypress', dmxKeypress);
    	Event.stopObserving(document, 'keydown', dmxKeydown);
    	break;
    	
    case Event.KEY_LEFT:
    case Event.KEY_RIGHT:
    case Event.KEY_UP:
    case Event.KEY_DOWN:
    case Event.TAB:
    }

}

function dmxValueOnMouseOver(event) {
	var dmxValueEl, el, ch, f, off, dc, j, cds, cd = null;
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
    	el.update(dmxTimeSourceText);
    	dmxSelectedFixture=null;
    	return;
    };
    off=f["dmxOffset"];
    fd=fixtureDefs[f.type];
    dc=fd["dmxChannels"];
    cds=fd["channelDefs"];
    for (var j=0; j<cds.length; j++) {
    	if (cds[j]["dmxOffset"]==ch-off) {
    		cd=cds[j];
    	}
    }
    //if (dmxSelectedFixture!=f) {
    el.update("Name: <b>" + f["name"] + "</b><br/>" +
    	"Type: <img valign=\"text-bottom\" src=\"" + fd["img"] + "\">" + fd["label"] + "<br/>" + 
    	"Offset: " + f["dmxOffset"] + "<br/>" +
    	"Channel: " + (ch-off) + (cd==null ? "" : " (<img valign=\"text-bottom\" src=\"" + cd["img"] + "\">" + cd["label"] + ")"));
    //}
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
		var text;
		if (e["count"]>1) {
			text = "This exception has occurred " + e["count"] + " times.<br/>" +
			  "First occurrence: " + new Date(e["firstTimestamp"]) + "<br/>" +
			  "Last occurrence: " + new Date(e["timestamp"]) + "<br/>";
		} else {
			text = "Time of exception: " + new Date(e["timestamp"]) + "<br/>";
		}
		logDetailEl = new Element("div",
		  { "class" : "logDetail", "id" : "logDetail[" + logId + "]" }).update(
			"<pre>" + text + e.trace + "</pre>");
		logTitleEl.insert({'after' : logDetailEl});
	} else {
		logDetailEl.style.display = logDetailEl.style.display=='none' ? 'block' : 'none';
	}
}

function logUpdatePanel(json) {
    var el = $("logExceptionContainer");
    var text;
    el.innerHTML = ""; // reset any existing DIVs
    logExceptions = json.exceptions;
    for (var i=0; i<logExceptions.length; i++) {
        var e = logExceptions[i];
        text = (e["count"]>1 ? "[" + e["count"] + "] " : "") + e.message;
        var logTitleEl = new Element("div", 
          { "class" : "logTitle", 
        	"id" : "logTitle[" + i + "]" ,
            "onclick" : "logToggle(" + i + ")"}).update(
          "<img class=\"logExpandImg\" src=\"image/logExpand.png\"/> " + 
          "<span class=\"logMessage\">" + text + "</span>");
        el.insert({'bottom' : logTitleEl});
    }
}

/******************************* CONFIG PANEL ******************************/

function cnfInitPanel() {
    Event.observe($("cnfFixtureDef"), 'click', cnfFixtureDefClick);
    Event.observe($("cnfFixture"), 'click', cnfFixtureClick);
    Event.observe($("cnfShowDef"), 'click', cnfShowDefClick);
    Event.observe($("cnfShow"), 'click', cnfShowClick);
    Event.observe($("cnfResetAudio"), 'click', cnfResetAudioClick);
    //Event.observe($("cnfResetDMX"), 'click', cnfResetDMXClick);
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
function cnfResetAudioClick() {
	sendRequest("fancyController.html?action=resetAudio");
}
function cnfResetDMXClick() {
	sendRequest("fancyController.html?action=resetDMX");
}
function cnfSimpleClick() {
    document.location="controller.html";
}
function cnfVideoClick() {
    window.open("streaming.html", "streamingWindow");
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
    if (origPanel=='cnfPanel') {  // from cancel buttons in editor pages
    	lhsConfig();
    } else {
        lhsLogo();
    }
}

