var shwState = new Array();
var dmxValues = dmxValues.split(",");
var dmxModified = new Array();
var dmxToFixture = new Array();
var dmxHighlightTimeout = -1;
var dmxImmediate = true;
var logExceptions = new Array();
var lhsMenuPanels=new Array("lgoPanel", "shwPanel", "fixPanel", "dmxPanel", "logPanel", "cnfPanel");
var prfRequestId = 0;
var prfEnabled = false;
var prfStartPollRequestTime = null;
var prfEndPollRequestTime = null;
var prfEndPanelUpdateTime = null;
var longPollRequest=null;
var currentPanelName=null;
var MAX_LENGTH=9223372036854775807;
// timestamp of last useful server response (i.e. response for current panel)
var lastServerResponse = 0; // to prevent iframe & polls rolling back changes from the other 
var disableIframe = true;

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
            completedFunction(transport.responseJSON);
        },
        onComplete: function(transport) {
            //completedFunction(transport.responseJSON);
        }});
}

function sendPostRequest(url,parameters,completedFunction) {
    new Ajax.Request(url, {
        method:'post', // evalJSON:true,
        parameters:parameters,
        onSuccess: function(transport) {
            setRhsMessageHTML(transport.responseJSON.message);
            completedFunction(transport.responseJSON);
        },
        onComplete: function(transport) {
            //completedFunction(transport.responseJSON);
        }});
}


/******************************* LHS MENU ******************************/
function clickFx(el) {
    el.addClassName("clickHighlight");
    window.setTimeout(function() { el.removeClassName("clickHighlight") }, 50);
}

function noSelect(el) {
	el.onselectstart = function() { return false; } //ie
	el.onmousedown = function() { return false; } //mozilla
}

function initLhsMenu() {
	var button;
    Event.observe($("lhsLogo"), 'click', lhsLogo); 
    Event.observe($("lhsBlackout"), 'click', lhsBlackout);
    Event.observe($("lhsShows"), 'click', lhsShows);
    Event.observe($("lhsFixtures"), 'click', lhsFixtures);
    Event.observe($("lhsDMX"), 'click', lhsDMX);
    Event.observe($("lhsLogs"), 'click', lhsLogs);
    Event.observe($("lhsConfig"), 'click', lhsConfig);
    
    $$(".lhsMenuItem").each(function(s){noSelect(s);});
    noSelect($("lhsLogo"));
}

function lhsShowPanel(panelName) {
    if (currentPanelName==panelName) { return; }
    //if (currentPanelName=="dmxPanel") { dmxHideHighlight2(); }
    Event.stopObserving(document, 'keypress', dmxSliderKeypress);
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
    if (!disableIframe) { reloadCometIframe(); }
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
	/*
	var pollUrl = "fancyController.html?action=poll&pageId=" + pageId + "&panel=" + currentPanelName; 
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
    */
}

function updatePanel(json) {
    var jsonPanel = json.panel;
    var serverTime = json.serverTime;
    if (jsonPanel==currentPanelName) {
        if (serverTime > lastServerResponse) {
	    	lastServerResponse = serverTime;
	        if (jsonPanel=="shwPanel") { shwUpdatePanel(json); }
	        else if (jsonPanel=="dmxPanel") { dmxUpdatePanel(json); }
	        else if (jsonPanel=="fixPanel") { fixUpdatePanel(json); }
	        else if (jsonPanel=="logPanel") { logUpdatePanel(json); }
	        if (!json.stopPollRequests) {
	            window.setTimeout(startPollRequests, 500);
	        }
	    }
	    if (json.reloadIframe) {
	    	$("cometFrame").setAttribute("src", "fancyController.html?action=iframe&pageId=" + pageId + "&panel=" + currentPanelName);
	    }
    }
}
function updatePanelComet(json) {
	var jsonPanel = json.panel;
	var serverTime = json.serverTime;
    if (jsonPanel==currentPanelName) {
    	if (serverTime > lastServerResponse) {
    		lastServerResponse = serverTime;
	        if (jsonPanel=="shwPanel") { shwUpdatePanel(json); }
	        else if (jsonPanel=="dmxPanel") { dmxUpdatePanel(json); }
	        else if (jsonPanel=="fixPanel") { fixUpdatePanel(json); }
	        else if (jsonPanel=="logPanel") { logUpdatePanel(json); }
    	}
    }
}
function reloadCometIframe() {
	$("cometFrame").setAttribute("src", "fancyController.html?action=iframe&pageId=" + pageId + "&panel=" + currentPanelName);
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
      "<li><a href=\"" + javadocUrl + "\" target=\"_new\">Java API documentation</a>\n" +
      "</li>\n" +
      "</ul>" +
      "<br/><br/>" +
      "Browser: " + BrowserDetect.browser + " " + BrowserDetect.version + " on " + BrowserDetect.OS + "<br/><br/>" +
      (BrowserDetect.browser=="Chrome" ? "" : "You'd be astonished how much more responsive this user interface appears if you try using <a href=\"www.google.com/chrome\">Chrome</a>")
    );
}


/******************************* SHOW PANEL ******************************/
var shwScrollFx=null;  
function shwInitPanel() {
    var x, y, el, displayOffset=0, lastShowGroupId=-1;
    var sp=$("shwItemContainer");
    for (var i=0; i<shows.length; i++) {
    	var show = shows[i];
    	if (i==0 || lastShowGroupId!=show["showGroupId"]) {
    		var cancelGroupEl = new Element("div", { 
                "id": "shwCancelGroup[" + show["showGroupId"] + "]", 
                "showGroupId": show["showGroupId"],
                "title" : "Cancel all running shows in this group",
                "class" : "shwCancelGroup" }).update("<img width=\"80\" height=\"70\" src=\"image/cancel.png\" title=\"Cancel group\"/>");
    		cancelGroupEl.style.left="10px"; 
    		cancelGroupEl.style.top=((10+Math.floor((i+displayOffset-1)/4)*90)+90)+"px";
            sp.appendChild(cancelGroupEl);
            Event.observe(cancelGroupEl, 'click', shwCancelGroupClick.curry(show["showGroupId"]));    		
    	}
    	if (lastShowGroupId!=show["showGroupId"]) {
    		var separatorEl = new Element("div", { "class" : "shwItemSeparator" });
    		separatorEl.style.left="10px"; 
    		separatorEl.style.top=((10+Math.floor((i+displayOffset-1)/4)*90)+80)+"px";
    		if ((i+displayOffset)%4!=0) {
    			displayOffset += (4-((i+displayOffset)%4));
    		}
            sp.appendChild(separatorEl);
    		lastShowGroupId = show["showGroupId"];
    	}
        x=110+((i+displayOffset)%4)*200; y=10+Math.floor((i+displayOffset)/4)*90;
        var shwEl = new Element("div", { 
            "id": "shwItem[" + show["id"] + "]", 
            "showId": show["id"],
            "title" : show["description"],
            "class" : "shwItem" }).update(
            show["name"] + "<div class=\"shwOverlay\"></div>" 
            );
        shwEl.style.left=x+"px"; shwEl.style.top=y+"px";
        sp.appendChild(shwEl);
        shwState[show["id"]] = "STOPPED";
        Event.observe(shwEl, 'click', shwItemClick);
    }
    Event.observe($("shwCancel"), 'click', shwCancelClick);
    Event.observe($("shwPageUp"), 'click', shwPageDownClick); noSelect($("shwPageUp"));
    Event.observe($("shwPageDown"), 'click', shwPageUpClick); noSelect($("shwPageDown"));
} 

function shwItemClick(event) {
    var shwItemEl = event.element();
    if (shwItemEl.readAttribute("class")=="shwOverlay") { shwItemEl = shwItemEl.parentNode; }
    clickFx(shwItemEl);
    var showId = shwItemEl.readAttribute("showId");
    if (shwState[showId]=="RUNNING") {
      sendRequest('fancyController.html?action=cancelShow&showId=' + showId, startPollRequests);
    } else {
      sendRequest('fancyController.html?action=startShow&showId=' + showId, startPollRequests);      
    }
}

function shwCancelClick(event) {
    sendRequest('fancyController.html?action=cancelShow');
}

function shwCancelGroupClick(showGroupId) {
	sendRequest('fancyController.html?action=cancelShowGroup&showGroupId=' + showGroupId);
}

function shwPageDownClick(event) {
	var el = $("shwItemContainer");
	if (shwScrollFx!=null) { shwScrollFx.cancel(); el.scrollTop=el.scrollTop-1000; shwScrollFx=null; }
	else { shwScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop-1000, {afterFinish:function(){shwScrollFx=null;}}, 'scrollTop'); }
}

function shwPageUpClick(event) {
	var el = $("shwItemContainer");
	if (shwScrollFx!=null) { shwScrollFx.cancel(); el.scrollTop=el.scrollTop+1000; shwScrollFx=null; }
	else { shwScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop+1000, {afterFinish:function(){shwScrollFx=null;}}, 'scrollTop'); }
}



// this could be cleaned up a bit
function shwUpdatePanel(json) {
    var newShows = json.shows;
    var now = new Date().getTime();
    for (var i=0; i<newShows.length; i++) {
        var showId = newShows[i]["id"];
        var el = $("shwItem[" + showId + "]");
        if (newShows[i]["state"]=="SHOW_RUNNING") {
        	shwState[showId] = "RUNNING";
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
        	shwState[showId] = "STOPPED_WITH_EXCEPTION";
            el.removeClassName("shwRunning");
            el.addClassName("shwException");
        } else {            
        	shwState[showId] = "STOPPED";
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
var fixScrollFx = null;
var fixItemEls = new Array();
function fixInitPanel() {
    var x,y,i,j,f,fd,fixEl;
    var fp=$("fixPanel");
    var fic=$("fixItemContainer");
    var fixEl;
    
    // display in sortOrder order
    var fixOrder = new Array(); for (var i=0; i<fixtures.length; i++) { fixOrder[i] = i; };
    fixOrder.sort(function(a,b) { fa=fixtures[a].sortOrder; fb=fixtures[b].sortOrder; return fa<fb ? -1 : (fa>fb ? 1 : 0); } );
    for (var j=0; j<fixtures.length; j++) {
    	i=fixOrder[j];
        x=10+(j%4)*200; y=10+Math.floor(j/4)*90;
        f=fixtures[i]; fd=fixtureDefs[f.type];
        if (f.x!=null && f.y!=null) { x=f.x; y=f.y; }
        if (f.fpType=="L") {
	        fixEl = new Element("div", { 
	            "id": "fixItem[" + i + "]", "fixtureId": i,
	            "class" : "fixItem",
	            "selectClass" : "fixSelect" }).update(
	            "<div class=\"fixItemRecBack\"></div>" +
	            "<div class=\"fixItemLabel\">" + f.name + "</div>" +
	            "<div class=\"fixOutput\"><div class=\"fixOutputDim\"><div class=\"fixOutputDim2\"></div></div>&nbsp;<div class=\"fixOutputColor\"></div>" +
	            (fd.panRange==0 ? "" : "&nbsp;&#8596;<div class=\"fixOutputPan\">0</div>") +
	            (fd.tiltRange==0 ? "" : "&nbsp;&#8597;<div class=\"fixOutputTilt\">0</div>") +
	            "&nbsp;<div class=\"fixOutputStrobe\"></div>" +
	            "</div>" +
	            "<div class=\"fixItemIcon\"><img src=\"" + fd["img32"] + "\"></div>"
	            );
        } else if (f.fpType=="S") {
        	fixEl = new Element("div", { 
	            "id": "fixItem[" + i + "]", "fixtureId": i,
	            "class" : "fixItemHalf",
	            "selectClass" : "fixSelectHalf"}).update(
	            "<div class=\"fixItemRecBack\"></div>" +
	            "<div class=\"fixItemHalfLabel\">" + f.name + "</div>" +
	            "<div class=\"fixOutputHalf\"><div class=\"fixOutputDim\"><div class=\"fixOutputDim2\"></div></div>&nbsp;<div class=\"fixOutputColor\"></div>" +
	            (fd.panRange==0 ? "" : "&nbsp;&#8596;<div class=\"fixOutputPan\">0</div>") +
	            (fd.tiltRange==0 ? "" : "&nbsp;&#8597;<div class=\"fixOutputTilt\">0</div>") +
	            "&nbsp;<div class=\"fixOutputStrobe\"></div>" +
	            "</div>" +
	            "<div class=\"fixItemHalfIcon\"><img src=\"" + fd["img16"] + "\"></div>"
	            );
        } else if (f.fpType=="M") {
        	fixEl = new Element("div", { 
	            "id": "fixItem[" + i + "]", "fixtureId": i,
	            "class" : "fixItemMatrix",
	            "selectClass" : "fixSelectMatrix" }).update(
	            "<div class=\"fixOutputColorMatrix\"></div>"
	            );
        }
        fixEl.style.left=x+"px"; fixEl.style.top=y+"px";
        fixItemEls.push(fixEl);
        fic.appendChild(fixEl);
        Event.observe(fixEl, 'click', fixItemClick);
    }
    Event.observe($("fixAllNone"), 'click', fixAllNoneClick); noSelect($("fixAllNone"));
    Event.observe($("fixGroup"), 'click', fixGroupClick); noSelect($("fixGroup"));
    Event.observe($("fixCustom"), 'click', fixCustomClick); noSelect($("fixCustom"));
    Event.observe($("fixBlackout"), 'click', fixBlackout); noSelect($("fixBlackout"));
    Event.observe($("fixAim"), 'click', fixAimClick);
    fixDimSlider = new Control.Slider("fixDimHandle", "fixDim", {
        axis: "vertical",
        onSlide: function(v) { fixDimChange(v); },
        onChange: function(v) { fixDimChange(v); }
    });
    Event.observe('fixDimScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // mozilla
    Event.observe('fixDimScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // IE/Opera
    fixStrobeSlider = new Control.Slider("fixStrobeHandle", "fixStrobe", {
        axis: "vertical",
        onSlide: function(v) { fixStrobeChange(v); },
        onChange: function(v) { fixStrobeChange(v); }
    });
    fixStrobeSlider.setValue(1);
    Event.observe('fixStrobeScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(fixStrobeSlider, 0.1));  // mozilla
    Event.observe('fixStrobeScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(fixStrobeSlider, 0.1));  // IE/Opera
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
    Event.observe($("fixPageUp"), 'click', fixPageDownClick); noSelect($("fixPageUp"));
    Event.observe($("fixPageDown"), 'click', fixPageUpClick); noSelect($("fixPageDown"));

    //jQuery('#fixColorPicker').farbtastic(/*'#fixColor'*/ fixColorChange);
    fixColorPicker=jQuery.farbtastic(jQuery('#fixColorPicker'), fixColorChange);
    
    if (fixtures.length>0) { fixUpdateControls(0); }
} 

function fixPageDownClick(event) {
	var el = $("fixItemContainer");
	if (fixScrollFx!=null) { fixScrollFx.cancel(); el.scrollTop=el.scrollTop-1000; fixScrollFx=null; }
	else { fixScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop-1000, {afterFinish:function(){fixScrollFx=null;}}, 'scrollTop'); }
}

function fixPageUpClick(event) {
	var el = $("fixItemContainer");
	if (fixScrollFx!=null) { fixScrollFx.cancel(); el.scrollTop=el.scrollTop+1000; fixScrollFx=null; }
	else { fixScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop+1000, {afterFinish:function(){fixScrollFx=null;}}, 'scrollTop'); }
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
	var selectClass = el.readAttribute("selectClass");
    if (el.hasClassName(selectClass)) {
        el.removeClassName(selectClass); return false;
    } else {
        el.addClassName(selectClass); return true;
    }
}

var fixLastFixSelectedEl = null;
var fixSelectIndividual = false;
function fixItemClick(event) {
    var fixItemEl = event.element();
    while (fixItemEls.indexOf(fixItemEl)==-1) { fixItemEl=fixItemEl.parentNode; }
    if (fixSelectIndividual) {
        if (fixLastFixSelectedEl!=null && fixLastFixSelectedEl!=fixItemEl) { 
            fixLastFixSelectedEl.removeClassName(fixLastFixSelectedEl.readAttribute("selectClass")); 
        }
    }
    fixLastFixSelectedEl = fixToggleEl(fixItemEl) ? fixItemEl : null;

    var fixItems=new Array();
    fixItemEls.each(function(f){if (f.hasClassName(f.readAttribute("selectClass"))){fixItems.push(f.readAttribute("fixtureId"))};});
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
	    			try {
	    				fixCustomControls[i].setValue(1-(v["ccs"][i]/255));
	    			} catch (e) {
	    				// TypeError can occur if custom control display is still being shown for wrong fixture
	    			} 
	    		} else if (ccs[i].uiType=="GRID") {
	    			var x=Math.floor(v["ccs"][i]/160); var y=(v["ccs"][i]%160); 
	    			x=x<0?0:(x>159?159:x); y=y<0?0:(y>159?159:y);
	    			fixCustomControls[i].handle.style.left=(x-10) + "px";
	    			fixCustomControls[i].handle.style.top=(y-10) + "px";
	    		}
    		}
    	}
    }
    
    fixUIUpdateOnly=false
}

function fixSameValue(values) {
	sameValue=values[0];
	for (var i=1; i<values.length; i++) {
		if (values[i]==null) { return null; }
		if (values[i]!=sameValue) { return null; }
	}
	return sameValue;
}

// same as fixUpdateControls, but only update where all fixtures have the same value
// TODO: set other controls opacity to n% ?
function fixUpdateControlsArray(fixtureIds) {
    //var f=fixtures[fixtureIds[0]];
    //var fd=fixtureDefs[f.type];
    //var v=fixValues[fixtureId];
	
    var values=new Array();
    var value, valuePan, valueTilt;
    fixUIUpdateOnly=true;

    values=[]; fixtureIds.each(function(fid){values.push(fixtures[fid].type);}); 
    fd=fixSameValue(values);
    if (fd) { fd = fixtureDefs[fd]; }
    
    values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["c"]);}); value=fixSameValue(values);
    if (value!=null) { fixColorPicker.setColor(value); }
    
    values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["d"]);}); value=fixSameValue(values);
    if (value!=null) { fixDimSlider.setValue(1-value); }

    if (fd) {  // all fixtures of same type
      values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["s"]);}); value=fixSameValue(values);
      if (value!=null) { 
    	fixStrobeSlider.setValue(1-((value-fd["minStrobeHertz"])/
    	    (fd["maxStrobeHertz"]-fd["minStrobeHertz"])));
      } else {
    	fixStrobeSlider.setValue(1);
      }
  
      var aimHandleEl=$("fixAimHandle");
      var aimActualEl=$("fixAimActual");
      var aimHandleDimensions=Element.getDimensions(aimHandleEl);
      var aimParentDimensions=Element.getDimensions(aimHandleEl.parentNode);
    
      values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["p"]);}); valuePan=fixSameValue(values);
      values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["t"]);}); valueTilt=fixSameValue(values);
      if (valuePan!=null && valueTilt!=null) {  // NB: same as above
        aimHandleEl.style.left=(valuePan*aimParentDimensions.width/fd["panRange"] - aimHandleDimensions.width/2) + "px";
        aimHandleEl.style.top=(valueTilt*aimParentDimensions.height/fd["tiltRange"] - aimHandleDimensions.height/2) + "px";
      }
      values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["ap"]);}); valuePan=fixSameValue(values);
      values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["at"]);}); valueTilt=fixSameValue(values);
      if (valuePan!=null && valueTilt!=null) {  // NB: same as above
        aimActualEl.style.left=(valuePan*aimParentDimensions.width/fd["panRange"] - aimHandleDimensions.width/2) + "px";
        aimActualEl.style.top=(valueTilt*aimParentDimensions.height/fd["tiltRange"] - aimHandleDimensions.height/2) + "px";
      }
      var ccs=fd["customControls"];
      var ccEl;
      if (ccs && fixCustomControlsVisible) {
    	for (var i=0; i<ccs.length; i++) {
    		ccEl = $("fixCC[" + i + "]");
    		if (ccEl) {
    			values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["ccs"][i]);}); value=fixSameValue(values);
    			if (value!=null) {
	    		  if (ccs[i].uiType=="TOGGLE") {
	    			if (value==1) { ccEl.addClassName("fixSmallSelect"); }
	    			else { ccEl.removeClassName("fixSmallSelect"); }
	    		  } else if (ccs[i].uiType=="SLIDER") {
	    			fixCustomControls[i].setValue(1-(value/255));
	    		  } else if (ccs[i].uiType=="GRID") {
	    			var x=Math.floor(value/160); var y=(value%160); 
	    			x=x<0?0:(x>159?159:x); y=y<0?0:(y>159?159:y);
	    			fixCustomControls[i].handle.style.left=(x-10) + "px";
	    			fixCustomControls[i].handle.style.top=(y-10) + "px";
	    		  }
    			}  
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
    	fixItemEls.each(function(f){f.removeClassName(f.readAttribute("selectClass"));});
        if (fixLastFixSelectedEl!=null) { fixLastFixSelectedEl.addClassName(fixLastFixSelectedEl.readAttribute("selectClass")); }
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
}

function fixAllNoneClick(event) {
    var fixAllNoneEl = event.element();
    var countFixSelected=0;
    fixItemEls.each(function(f){if (f.hasClassName(f.readAttribute("selectClass"))){countFixSelected++;};});
    if (countFixSelected==fixtures.length) {
    	fixAllNoneEl.removeClassName("fixSmallSelect");
    	fixItemEls.each(function(f){f.removeClassName(f.readAttribute("selectClass"));});
    } else {
    	fixAllNoneEl.addClassName("fixSmallSelect");
    	fixItemEls.each(function(f){f.addClassName(f.readAttribute("selectClass"));});
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
    fixItemEls.each(function(f){if (f.hasClassName(f.readAttribute("selectClass"))){fixtureId=f.readAttribute("fixtureId");if(fd==null){fd=fixtureDefs[fixtures[fixtureId].type]}else if (fd!=fixtureDefs[fixtures[fixtureId].type]){tmft=true;}};});
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
	    			var ctrlEl;
	    			if (cc["uiType"]=="TOGGLE") {
	    		        ctrlEl = new Element("div", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomToggle" }).update(
	    		          cc.image ? ("<img src=\"" + cc.image + "\" />") : cc.label
	    		        );
	    		        ccEl.appendChild(ctrlEl);
	    		        if (cc.top) { ctrlEl.absolutize(); ctrlEl.style.top=cc.top; ctrlEl.style.left=cc.left; }
	    		        Event.observe(ctrlEl, 'click', fixCustomToggleClick.curry(i));
	    		        noSelect(ctrlEl);
	    			} else if (cc["uiType"]=="SLIDER") {
	    				ctrlEl = new Element("div", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomSlider" });
	    				var handleEl = new Element("div", { "class" : "fixCustomSliderHandle" });
	    				ccEl.appendChild(ctrlEl);
	    				if (cc.top) { ctrlEl.absolutize(); ctrlEl.style.top=cc.top; ctrlEl.style.left=cc.left; }
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
	    			} else if (cc["uiType"]=="GRID") {
	    				ctrlEl = new Element("div", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomGrid" });
	    				var handleEl = new Element("div", { "class" : "fixCustomGridHandle" });
	    				ccEl.appendChild(ctrlEl);
	    				if (cc.top) { ctrlEl.absolutize(); ctrlEl.style.top=cc.top; ctrlEl.style.left=cc.left; }
	    				ctrlEl.appendChild(handleEl);
	    				fixCustomControls[i] = new Draggable(handleEl, {
	    			        // constraint code modified from http://www.java2s.com/Code/JavaScript/Ajax-Layer/Draganddropsnaptoabox.htm
	    			        snap: function(x,y,draggable) {
	    			            function constrain(n, lower, upper) {
	    			                if (n>upper) { return upper; }
	    			                else if (n<lower) { return lower; }
	    			                else return n;
	    			            }
	    			            return[constrain(x, -10, 149),
	    			                   constrain(y, -10, 149)];
	    			        },
	    			        onDrag: fixCustomGridChange.curry(i),
	    			        revert: false
	    			    });
	    				var labelEl = new Element("div", {"class": "fixCustomSliderLabel"}).update(cc.label);
	    				labelEl.style.left=ctrlEl.positionedOffset().left+"px";
	    				ccEl.appendChild(labelEl);	    				
	    			} else {
	    				alert("Unknown control type '" + cc["uiType"] + "'")
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
    fixItemEls.each(function(f){if (f.hasClassName(f.readAttribute("selectClass"))){fixItems.push(f.readAttribute("fixtureId"))};});
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
            } else {
            	window.clearTimeout(this.newValueTimeoutId);
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
        if (isRecording) { fixRecTouch(fixGetItems()); }
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
        if (isRecording) { fixRecTouch(fixGetItems()); }
    }
    
}

var fixColorLimitter = new AjaxLimitter(100, 200);
function fixColorChange(color) {
	if (fixUIUpdateOnly) { return; }
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixColorLimitter.sendRequest( 
           'fancyController.html?action=fixtureColor&color=' + color.substring(1) + '&fixtureIds=' + fixItemIds);
        if (isRecording) { fixRecTouch(fixGetItems()); }
    }
    
}

var fixAimLimitter = new AjaxLimitter(100, 200);
function fixAimDrag(x, y) {
	if (fixUIUpdateOnly) { return; }
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
       fixAimLimitter.sendRequest( 
           'fancyController.html?action=fixtureAim&x=' + (x*100) + '&y=' + (y*100) + '&fixtureIds=' + fixItemIds);
       if (isRecording) { fixRecTouch(fixGetItems()); }
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
    	if (isRecording) { fixRecTouch(fixItems); }
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
    	if (isRecording) { fixRecTouch(fixItems); }
	}
}

var fixCustomGridLimitter = new AjaxLimitter(100,200);
function fixCustomGridChange(controlId, draggable, event) {
	if (fixUIUpdateOnly) { return; }
    var handlePos=Position.positionedOffset(draggable.element); // positions range from -10-150
    //var x = (handlePos[0]+handleDimensions.width/2)/(parentDimensions.width);
    //var y = (handlePos[1]+handleDimensions.height/2)/(parentDimensions.height);
	var fixItemIds=fixGetItemIds();
	if (fixItems.length > 0) {
       fixCustomGridLimitter.sendRequest( 
          'fancyController.html?action=customControl&controlId=' + controlId + '&value=' + ((handlePos[0]+10)*160 + handlePos[1]+10) + '&fixtureIds=' + fixItemIds);
       if (isRecording) { fixRecTouch(fixItems); }     
  	}
}

function fixRecTouch(fixtureIds) {
	var fixItemEl, fpType, i;
	for (i=0; i<fixtureIds.length; i++) {
	  var fixItemEl = $("fixItem[" + fixtureIds[i] + "]").childNodes.item(0);
	  var fpType = fixtures[fixtureIds[i]].fpType;
	  if (fpType == "L") { fixItemEl.addClassName("fixItemRec"); }
	  else if (fpType == "S") { fixItemEl.addClassName("fixItemHalfRec"); }
	  else if (fpType == "M") { /*fixItemEl.addClassName("fixItemHalf"); something else probably */ }
	}  
}

function fixUpdatePanel(json) {
    fixValues = json.fixValues;
    for (var i=0; i<fixValues.length; i++) {
        var fixValue = fixValues[i];
        var fpType = fixtures[i].fpType;
        var el = $("fixItem[" + i + "]");
        if (fpType=="L" || fpType=="S") {
          var fd=fixtureDefs[fixtures[i].type];
          var divEls = el.getElementsByTagName("DIV");
          var divElIdx = 4;
          divEls[divElIdx++].style.height=(1-fixValue["d"])*15 + "px";
          divEls[divElIdx++].style.backgroundColor=fixValue["c"];
          if (fd.panRange!=0) { divEls[divElIdx++].innerHTML=twoDigits(fixValue["p"]); }
          if (fd.tiltRange!=0) { divEls[divElIdx++].innerHTML=twoDigits(fixValue["t"]); }
          if (fixValue["s"]) {
        	divEls[divElIdx++].innerHTML=twoDigits(fixValue["s"]);
          } else {
        	divEls[divElIdx++].innerHTML="";
          }
        } else if (fpType=="M") {
          el.getElementsByTagName("DIV")[0].style.backgroundColor=fixValue["c"];
        }  
    }
    var fixItems=new Array();
    fixItemEls.each(function(f){if (f.hasClassName(f.readAttribute("selectClass"))){fixItems.push(f.readAttribute("fixtureId"))};});
    if (fixItems.length==1) { fixUpdateControls(fixItems[0]); }
    else if (fixItems.length>1) { fixUpdateControlsArray(fixItems); }
}


/******************************* DMX PANEL ******************************/
var dmxSelectedFixture=null;  // fixture being highlighted
var dmxSelectedChannel=null;  // fixture being editted via keyboard
var dmxSelectedValue=null;
var dmxOrigValue=null;
var dmxHighlightedChannel=null; // fixture being editted via slider
var dmxSlider=null;
var dmxUIUpdateOnly = false
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
    	var dmxFixtureIconEl=new Element("div", {"class" : "dmxFixtureIcon" }).update(
    		"<img src=\"" + fixtureDefs[f.type]["img16"] + "\">");
    	//var dmxFixtureIconEl=new Element("div", {"class" : "dmxFixtureIcon" }).update(
    	//	"<img src=\"image/favicon.png\">"	);
    	$("dmxBox[" + f["dmxOffset"] + "]").insert({'top':dmxFixtureIconEl});
    	$("dmxBox[" + f["dmxOffset"] + "]").className="dmxValueWithFixture";
    }
    dmxSlider = new Control.Slider("dmxSliderHandle", "dmxSlider", {
        axis: "vertical",
        onSlide: function(v) { dmxSliderChange(v); },
        onChange: function(v) { dmxSliderChange(v); }
    });
    Event.observe('dmxSliderScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(dmxSlider, 0.1));  // mozilla
    Event.observe('dmxSliderScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(dmxSlider, 0.1));  // IE/Opera
    Event.observe('dmxSliderScrollArea', 'click', dmxValueClick);
    $("dmxSliderScrollArea").style.visibility="hidden";

    Event.observe($("dmxImmediate"), 'click', dmxImmediateClick);
}

var dmxSliderLimitter = new AjaxLimitter(100, 200);
function dmxSliderChange(v) {
	if (dmxUIUpdateOnly) { return; }
    v=Math.floor(255*(1-v));
    dmxSliderLimitter.sendRequest( 
       'fancyController.html?action=setDmxValue&channel=' + dmxHighlightedChannel + '&value=' + v);
}

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
    if (dmxValueEl==$("dmxSliderScrollArea")) {
    	ch=dmxHighlightedChannel; dmxValueEl=$("dmxBox[" + ch + "]");
    } else {
    	ch=$(dmxValueEl).readAttribute("dmxChannel");
    	while (!ch && dmxValueEl!=null) {dmxValueEl=dmxValueEl.parentNode; ch=$(dmxValueEl).readAttribute("dmxChannel"); }
    }
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
    el = $("dmxBox[" + ch + "]");
    el2 = $("dmxValue[" + ch + "]"); 
    if (dmxSelectedValue) {
    	if (dmxImmediate) {
    		sendRequest("fancyController.html?action=setDmxValue&channel=" + dmxSelectedChannel + "&value=" + dmxSelectedValue.innerHTML);
    	}
    	dmxSelectedValue.removeClassName("dmxSelectedValue");
    }
    if (el2 == dmxSelectedValue) {
    	dmxSelectedValue = null;
    	return;
    }
    dmxSelectedChannel = ch;
    dmxSelectedValue = el2;
    dmxOrigValue = dmxValues[ch-1];
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
    	v = dmxOrigValue;
    	dmxValues[dmxSelectedChannel-1]=v;
    	dmxSelectedValue.innerHTML = v;
        dmxSelectedValue.removeClassName("dmxSelectedValue");
        Event.stopObserving(document, 'keypress', dmxKeypress);
        Event.stopObserving(document, 'keydown', dmxKeydown);
        dmxSelectedChannel=null;
        event.stop();
    	break;
    case Event.KEY_RETURN:
    	dmxSelectedValue.removeClassName("dmxSelectedValue");
    	if (dmxImmediate) {
    		sendRequest("fancyController.html?action=setDmxValue&channel=" + dmxSelectedChannel + "&value=" + v);
    		dmxUIUpdateOnly=true;
    		dmxSlider.setValue(1-v/255);
    		dmxUIUpdateOnly=false;
    	}
    	Event.stopObserving(document, 'keypress', dmxKeypress);
    	Event.stopObserving(document, 'keydown', dmxKeydown);
    	dmxSelectedChannel=null;
    	break;
    	
    case Event.KEY_LEFT:
    case Event.KEY_RIGHT:
    case Event.KEY_UP:
    case Event.KEY_DOWN:
    case Event.TAB:
    }

}

function dmxSliderKeypress(event) {
	dmxTest++;
    switch (event.keyCode) {
      case Event.KEY_UP:
    	  // alert("up");
    	  break;
      case Event.KEY_DOWN:
    	  // alert("down");
    	  break;
    }

}

function dmxValueOnMouseOver(event) {
	var dmxValueEl, el, ch, f, off, dc, j, cds, cd = null, dmxSliderEl;
	if (dmxSlider.dragging) { return; }
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
    	$("dmxSliderScrollArea").style.visibility="hidden";
    	// TODO: possibly do this on panel change as well ?
    	Event.stopObserving(document, 'keypress', dmxSliderKeypress);
    	return;
    };
    off=f["dmxOffset"];
    fd=fixtureDefs[f.type];
    dc=fd["dmxChannels"];
    cds=fd["channelDefs"];
    for (var j=0; j<cds.length; j++) {
    	if (cds[j]["dmxOffset"]==ch-off) {
    		cd=cds[j]; break;
    	}
    }
    //if (dmxSelectedFixture!=f) {
    el.update("Name: <b>" + f["name"] + "</b><br/>" +
    	"<img valign=\"text-bottom\" src=\"" + fd["img16"] + "\"> " + fd["label"] + "<br/>" + 
    	"<img valign=\"text-bottom\" src=\"image/channel/offset_16x16.png\"> Offset: " + f["dmxOffset"] + "<br/>" +
    	(cd==null ? "" : "<img valign=\"text-bottom\" src=\"" + cd["img"] + "\"> " + cd["label"]) +
    	" (Channel: " + (ch-off) + ")");
    //}
    for (j=off; j<off+dc; j++) {
    	el = $("dmxBox[" + j + "]");
    	if (j==ch) {
    		el.removeClassName("dmxSelectGroup");
    		el.addClassName("dmxSelect");
    		dmxHighlightedChannel=ch;
    	} else {
    		el.addClassName("dmxSelectGroup");
    	}
    }
	dmxSliderEl=$("dmxSliderScrollArea");
	dmxSliderEl.style.visibility="visible";
	var pos=Position.positionedOffset(dmxValueEl);
	dmxSliderEl.style.left = (pos[0] - 3) + "px";
	dmxSliderEl.style.top = (pos[1] - 3) + "px";
	dmxUIUpdateOnly=true;
	dmxSlider.setValue(1-dmxValues[dmxHighlightedChannel-1]/255);
	dmxUIUpdateOnly=false;
    dmxSelectedFixture=f;
    Event.observe(document, 'keypress', dmxSliderKeypress);
}
function dmxValueOnMouseOut(event) {
    var dmxValueEl = event.element();
    var ch=$(dmxValueEl).readAttribute("dmxChannel");
    while (!ch && dmxValueEl!=null) {dmxValueEl=$(dmxValueEl.parentNode); if (dmxValueEl) { ch=dmxValueEl.readAttribute("dmxChannel"); } }
    if (dmxValueEl==null) { return; }
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

function dmxUpdatePanel(json) {
    var dmxValuesNew = json.dmxValues.split(",");
    for (var i=1; i<=255; i++) {
        var el = $("dmxValue[" + i + "]");
        if (dmxValues[i-1]!=dmxValuesNew[i-1]) {
        	dmxValues[i-1]=dmxValuesNew[i-1];
        	if (i!=dmxSelectedChannel) {
	        	el.innerHTML=dmxValues[i-1];
	        	el.addClassName("dmxModified");
	        	dmxModified[i-1] = true;
        	}
        } else if (dmxModified[i-1]) {
        	dmxModified[i-1] = false;
        	el.removeClassName("dmxModified");
        }
    }
    if (dmxSelectedFixture==null) {
        $("dmxTimeSource").update("<div class=\"dmxTime\">" + json.now + "</div>")
    }
    if (dmxHighlightedChannel && 
    	($("dmxSliderScrollArea").style.visibility=="visible") &&
    	(!dmxSlider.dragging)) {
	    dmxUIUpdateOnly=true;
		dmxSlider.setValue(1-dmxValues[dmxHighlightedChannel-1]/255);
		dmxUIUpdateOnly=false;
    }
}


/******************************* LOG PANEL ******************************/
var logScrollFx;  
function logInitPanel() {
    Event.observe($("logPageUp"), 'click', logPageDownClick); noSelect($("logPageUp"));
    Event.observe($("logPageDown"), 'click', logPageUpClick); noSelect($("logPageDown"));
    Event.observe($("logClear"), 'click', logClearClick); noSelect($("logClear"));
}

function logPageDownClick(event) {
	var el = $("logExceptionContainer");
	if (logScrollFx!=null) { logScrollFx.cancel(); el.scrollTop=el.scrollTop-1000; logScrollFx=null; }
	else { logScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop-1000, {afterFinish:function(){logScrollFx=null;}}, 'scrollTop'); }
}

function logPageUpClick(event) {
	var el = $("logExceptionContainer");
	if (logScrollFx!=null) { logScrollFx.cancel(); el.scrollTop=el.scrollTop+1000; logScrollFx=null; }
	else { logScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop+1000, {afterFinish:function(){logScrollFx=null;}}, 'scrollTop'); }
}

function logClearClick(event) {
    sendRequest('fancyController.html?action=clearLogs');
    startPollRequests();
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
	Event.observe($("cnfStage"), 'click', cnfStageClick);
	Event.observe($("cnfRecord"), 'click', cnfRecordClick);
	Event.observe($("cnfFixtureDef"), 'click', cnfFixtureDefClick);
    Event.observe($("cnfFixture"), 'click', cnfFixtureClick);
    Event.observe($("cnfShowDef"), 'click', cnfShowDefClick);
    Event.observe($("cnfShow"), 'click', cnfShowClick);
    Event.observe($("cnfResetAudio"), 'click', cnfResetAudioClick);
    //Event.observe($("cnfResetDMX"), 'click', cnfResetDMXClick);
    Event.observe($("cnfSimple"), 'click', cnfSimpleClick);
    Event.observe($("cnfVideo"), 'click', cnfVideoClick);
    
    if (isRecording) {
    	$("cnfRecordText").update("Stop recording");
    	$("cnfRecord").addClassName("cnfControlSelect");
    	$("recContainer").style.visibility = "visible";
    }
    
}

function cnfStageClick() {
    document.location="maintainStage.html";
}
function cnfRecordClick() {
	isRecording=!isRecording;
	$("recContainer").style.visibility = isRecording ? "visible" : "hidden";
	$("cnfRecordText").update(isRecording ? "Stop recording" : "Start recording");
	if (isRecording) {
		$("cnfRecord").addClassName("cnfControlSelect");
		sendRequest('fancyController.html?action=startRecording', recCallback);
	} else {
		var showName = prompt("And the showname is?", "something");
		$("cnfRecord").removeClassName("cnfControlSelect");
		sendRequest('fancyController.html?action=stopRecording&showName=' + escape(showName), recCallback);
	}
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


/******************************* RECORDING ******************************/
var recCurrentFrame = 0; // 0-based
var recTotalFrames = 0;

function recInitPanel() {
	$("recContainer").style.visibility = isRecording ? "visible" : "hidden";
	$("cnfRecordText").update(isRecording ? "Stop recording" : "Start recording");
	if (isRecording) {
		$("cnfRecord").addClassName("cnfControlSelect");
	}
	
	recSetFrames(0,1);

    Event.observe($("recPrevFrame"), 'click', recPrevFrame);
    Event.observe($("recNextFrame"), 'click', recNextFrame);
    Event.observe($("recAddFrame"), 'click', recAddFrame);
    Event.observe($("recDeleteFrame"), 'click', recDeleteFrame);
    Event.observe($("recPlay"), 'click', recPlay);
}

function recUpdatePanel(json) {
	
}

function recSetFrames(newRecActiveFrame, newRecTotalFrames) {
	recCurrentFrame=newRecActiveFrame;
	recTotalFrames=newRecTotalFrames;
	$("recCurrentFrame").update(recCurrentFrame+1);
	$("recTotalFrames").update(recTotalFrames);
}

function recPrevFrame() {
	sendRequest('fancyController.html?action=prevFrame', recCallback);
	//recCurrentFrame--;
	recSetFrames(recCurrentFrame, recTotalFrames);
}

function recNextFrame() {
	sendRequest('fancyController.html?action=nextFrame', recCallback);
	//recCurrentFrame++; 
	//if (recCurrentFrame+1 > recTotalFrames) { recTotalFrames = recCurrentFrame+1; }
	//recSetFrames(recCurrentFrame, recTotalFrames);
}

function recAddFrame() {
	sendRequest('fancyController.html?action=addFrame', recCallback);
	//alert("recAddFrame");
}

function recDeleteFrame() {
	sendRequest('fancyController.html?action=deleteFrame', recCallback);
	//alert("recDeleteFrame");
}

function recPlay() {
	sendRequest('fancyController.html?action=playRecording', recCallback);
	// alert("recPlay");
}

// call this recUpdatePanel ?
function recCallback(json) {
	if (json.totalFrames) { recSetFrames(json.currentFrame, json.totalFrames); }
	// set fixture/dmx value highlights for this frame ?
	if (json.shows) {
		// recreate the show panel. or just load the whole thing again.
		window.location.reload()
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
    // lhsFixtures(); // need fixtures to be visible during init, or dimmer slider breaks (?)
	disableIframe=true;
    initLookups();
    initLhsMenu();
    
    lgoInitPanel();
    recInitPanel();
    lhsDMX(); dmxInitPanel();
    shwInitPanel();
    lhsFixtures(); fixInitPanel();
    logInitPanel();
    cnfInitPanel();
    if (origPanel=='cnfPanel') {  // from cancel buttons in editor pages
    	lhsConfig();
    } else {
        lhsLogo();
    }
    disableIframe=false;
}

