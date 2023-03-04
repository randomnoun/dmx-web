var shwState = new Array();
var dmxValues = dmxValues.split(",");
var dmxModified = new Array();
var dmxToFixture = new Array();
var dmxHighlightTimeout = -1;
//var dmxImmediate = true;
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
var lastLogCount = 0;
var disableIframe = true;

function curry( fn /*, ... */ ) {
  var curryArgs = Array.prototype.slice.call( arguments, 1 );
  return function( /* ... */ ) {
    var newArgs    = Array.prototype.slice.call( arguments, 0 ),
        mergedArgs = curryArgs.concat( newArgs );
    return fn.apply( this, mergedArgs );
  }
}

function initLookups() {
    for (var i=0; i<fixtures.length; i++) {
        var f=fixtures[i]; var c=fixtureDefs[f.type]['dmxChannels'];
        if (f.universeIdx==0) {
            for (var d=0; d<c; d++) {
                dmxToFixture[d+f['dmxOffset']]=f;
            }
        }  
    }
}
// return value formatted as string with 2 decimal places
function twoDigits(v) {
  var t = new String(Math.floor(v*100)/100);
  var pos = t.indexOf(".");
  if (pos==-1) { t += ".00"; } else if (pos==t.length-2) { t += "0"; }
  //t = t.replace(/^(.*)\.([0-9])$/, "$1\.$20");
  return t;
}
// @TODO timeout this text or something
// @converted
function setRhsMessageHTML(text) {
    $("#rhsMessage").html(text);
}

// @converted maybe
function sendRequest(url, completedFunction) {
    jQuery.ajax({
        method: 'GET',
        url: url,
        dataType: 'json'
    }).done(function (data) {
        setRhsMessageHTML(data.message);
        if (completedFunction) { completedFunction(data); }
    });
    /*
    new Ajax.Request(url, {
        method:'get', // evalJSON:true,
        onSuccess: function(transport) {
            setRhsMessageHTML(transport.responseJSON.message);
            completedFunction(transport.responseJSON);
        },
        onComplete: function(transport) {
            //completedFunction(transport.responseJSON);
        }});
    */
}

// @converted maybe
function sendPostRequest(url,parameters,completedFunction) {
    jQuery.ajax({
        method: 'POST',
        url: url,
        data: parameters,
        dataType: 'json'
    }).done(function (data) {
        setRhsMessageHTML(data.message);
        completedFunction(data);
    });

    /*
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
    */
}


/******************************* LHS MENU ******************************/
// @converted
function clickFx(el) {
    el.addClass("clickHighlight");
    window.setTimeout(function() { el.removeClass("clickHighlight"); }, 50);
}

// @converted
function noSelect(el) {
    // @converted, @TODO use user-select instead
	$(el).on('selectstart', function() { return false; } ); //ie
	$(el).on('mousedown', function() { return false; } ); //mozilla
}

// @converted
function initLhsMenu() {
    $("#lhsLogo").on('click', lhsLogo); 
    $("#lhsBlackout").on('click', lhsBlackout);
    $("#lhsShows").on('click', lhsShows);
    $("#lhsFixtures").on('click', lhsFixtures);
    $("#lhsDMX").on('click', lhsDMX);
    $("#lhsLogs").on('click', lhsLogs);
    $("#lhsConfig").on('click', lhsConfig);
    
    $(".lhsMenuItem").each( function(i, s) { noSelect(s); } );
    noSelect($("#lhsLogo"));
}

// @converted
function lhsShowPanel(panelName) {
    if (currentPanelName==panelName) { return; }
    
    $(document).off('keypress', dmxSliderKeypress);
    for (var i=0; i<lhsMenuPanels.length; i++) {
        if (panelName!=lhsMenuPanels[i]) {
            $("#" + lhsMenuPanels[i])[0].style.display = "none";
        }
    }
    currentPanelName = panelName;
    if (longPollRequest) { longPollRequest.abort(); }
    var el=$("#" + panelName)[0]; 
    if (el) { 
        el.style.display = "block";
    }
    if (!disableIframe) { reloadCometIframe(); }
    // @TODO update with current state
}

// @converted
function lhsSelect(el) {
    $(".lhsMenuItem").each( function(i, s) { $(s).removeClass("lhsSelect"); } );
    if (el!=null) {
        el.addClass("lhsSelect");
    }
}

// @converted
function lhsBlackout() { 
    clickFx($("#lhsBlackout"));
    sendRequest('fancyController.html?action=blackOut');
}

// @converted it'd be great if copilot or chatcpt could do this, really
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

// @converted
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
	    	$("#cometFrame").attr("src", "fancyController.html?action=iframe&pageId=" + pageId + "&panel=" + currentPanelName);
	    }
    }
}

// @converted
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

// @converted
function reloadCometIframe() {
	$("#cometFrame").attr("src", "fancyController.html?action=iframe&pageId=" + pageId + "&panel=" + currentPanelName);
}

/******************************* CONFIG PANEL ******************************/

// @converted
function lgoInitPanel() {
	$("#lgoText1").html("Client Name");
    $("#lgoText2").html("<b>DMX-WEB</b><br/><br/>" +
      "Release: " + version["release"] + "<br/>" +
      "Build number: " + version["buildNumber"] + "<br/><br/>" +
      "RXTX JAR version: " + version["rxtxJarVersion"] + "<br/>" + 
      "RXTX DLL version: " + version["rxtxDllVersion"] + "<br/>" + 
      "<br/><br/>" +
      "<ul>\n" +
      "<li><a href=\"" + javadocUrl + "\" target=\"_new\">Java API documentation</a>\n" +
      "</li>\n" +
      "</ul>" +
      "<br/><br/>"
      // "Browser: " + BrowserDetect.browser + " " + BrowserDetect.version + " on " + BrowserDetect.OS + "<br/><br/>" +
      // (BrowserDetect.browser=="Chrome" ? "" : "You'd be astonished how much more responsive this user interface appears if you try using <a href=\"www.google.com/chrome\">Chrome</a>")
    );
}


/******************************* SHOW PANEL ******************************/

// @converted
var shwScrollFx=null;  
function shwInitPanel() {
    var x, y, displayOffset=0, lastShowGroupId=-1;
    var sp = $("#shwItemContainer");
    for (var i = 0; i < shows.length; i++) {
    	var show = shows[i];
    	if (i==0 || lastShowGroupId != show["showGroupId"]) {
    		var cancelGroupEl = $("<div>", { 
                "id": "shwCancelGroup[" + show["showGroupId"] + "]", 
                "showGroupId": show["showGroupId"],
                "title" : "Cancel all running shows in this group",
                "class" : "shwCancelGroup" }).html("<img width=\"80\" height=\"70\" src=\"image/stop.png\" title=\"Cancel group\"/>");
    		cancelGroupEl[0].style.left = "10px"; 
    		cancelGroupEl[0].style.top = ((10 + Math.floor(( i + displayOffset - 1) / 4) * 90) + 90) + "px";
            sp.append(cancelGroupEl);
            cancelGroupEl.on('click', curry(shwCancelGroupClick, show["showGroupId"]));
            // Event.observe(cancelGroupEl, 'click', shwCancelGroupClick.curry(show["showGroupId"]));    		
    	}
    	if (lastShowGroupId != show["showGroupId"]) {
    		var separatorEl = $("<div>", { "class" : "shwItemSeparator" });
    		separatorEl[0].style.left = "10px"; 
    		separatorEl[0].style.top = (( 10 + Math.floor((i + displayOffset - 1) / 4) * 90) + 80)+"px";
    		if ((i + displayOffset) % 4 != 0) {
    			displayOffset += (4 - ((i + displayOffset) % 4));
    		}
            sp.append(separatorEl);
    		lastShowGroupId = show["showGroupId"];
    	}
        x = 110 + ((i + displayOffset) % 4) * 200; y = 10 + Math.floor((i + displayOffset) / 4) * 90;
        var shwEl = $("<div>", { 
            "id": "shwItem[" + show["id"] + "]", 
            "showId": show["id"],
            "title" : show["description"],
            "class" : "shwItem" }
            ).html(
                show["name"] + "<div class=\"shwOverlay\"></div>" 
            );
        shwEl[0].style.left = x + "px"; 
        shwEl[0].style.top = y + "px";
        sp.append(shwEl);
        shwState[show["id"]] = "STOPPED";
        shwEl.on('click', shwItemClick);
        // Event.observe(shwEl, 'click', shwItemClick);
    }
    
    
    $("#shwCancel").on('click', shwCancelClick);
    $("#shwPageUp").on('click', shwPageDownClick); noSelect($("#shwPageUp"));
    $("#shwPageDown").on('click', shwPageUpClick); noSelect($("#shwPageDown"));
} 

// @converted
function shwItemClick(event) {
    var shwItemEl = $(event.delegateTarget);
    if (shwItemEl.hasClass('shwOverlay')) { shwItemEl = shwItemEl.parentNode; }
    clickFx(shwItemEl);
    var showId = shwItemEl.attr('showId');
    if (shwState[showId]=='RUNNING') {
        sendRequest('fancyController.html?action=cancelShow&showId=' + showId, startPollRequests);
    } else {
        sendRequest('fancyController.html?action=startShow&showId=' + showId, startPollRequests);      
    }
}

// @converted
function shwCancelClick(event) {
    sendRequest('fancyController.html?action=cancelShow');
}

// @converted
function shwCancelGroupClick(showGroupId) {
	sendRequest('fancyController.html?action=cancelShowGroup&showGroupId=' + showGroupId);
}

// @converted
function shwPageDownClick(event) {
	var el = $("#shwItemContainer");
	$(el).stop();
	$(el).animate( { scrollTop: '-=1000' }, 1000 );
	
	//if (shwScrollFx!=null) { shwScrollFx.cancel(); el.scrollTop=el.scrollTop-1000; shwScrollFx=null; }
	//else { shwScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop-1000, {afterFinish:function(){shwScrollFx=null;}}, 'scrollTop'); }
}

// @converted
function shwPageUpClick(event) {
	var el = $("$shwItemContainer");
    $(el).stop();
    $(el).animate( { scrollTop: '+=1000' }, 1000 );
	//if (shwScrollFx!=null) { shwScrollFx.cancel(); el.scrollTop=el.scrollTop+1000; shwScrollFx=null; }
	//else { shwScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop+1000, {afterFinish:function(){shwScrollFx=null;}}, 'scrollTop'); }
}



// this could be cleaned up a bit

// @converted
function shwUpdatePanel(json) {
    var newShows = json.shows;
    var now = new Date().getTime();
    for (var i = 0; i < newShows.length; i++) {
        var showId = newShows[i]['id'];
        var el = $('#shwItem\\[' + showId + '\\]');
        if (newShows[i]['state']=='SHOW_RUNNING') {
            shwState[showId] = 'RUNNING';
            el.addClass('shwRunning');
            el.removeClass('shwException');
            var overlayEl = $(el.children()[0]);
            var shwTimeEls = $('.shwTime', overlayEl);
            if (shwTimeEls.length == 0) {
                var shwTimeEl = $('<div>',
                   { 'class' : 'shwTime', 
                     'value' : newShows[i]['time'], 
                     'setAt' : now 
                   }).html( twoDigits(newShows[i]['time'] / 1000));
                overlayEl.prepend(shwTimeEl);
            } else {
                shwTimeEls[0].innerHTML = twoDigits(newShows[i]['time']/1000);
            }
            if (newShows[i]['label']) {
                var shwLabelEls = $('.shwLabel', overlayEl);
                if (shwLabelEls.length == 0) {
                    var shwLabelEl = $('<div>',
                        { 'class' : 'shwLabel' }).html(newShows[i]['label']);
                    overlayEl.prepend(shwLabelEl);
                } else {
                    shwLabelEls.html(newShows[i]['label']);
                }
            }
            if (newShows[i]['length'] != MAX_LENGTH) {
                var shwLenEls = $('.shwLenOuter', overlayEl);
                if (shwLenEls.length == 0) {
                    var shwLenEl = $('<div>',
                        { 'class' : 'shwLenOuter' }).html(
                        '<div class="shwLenInner" style="width:' + Math.min((newShows[i]['time'] * 40 / newShows[i]['length']), 40) + 'px;"></div>');
                    overlayEl.prepend(shwLenEl);
                } else {
                    $(shwLenEls.children()[0])[0].style.width = Math.min((newShows[i]['time'] * 40 / newShows[i]['length']), 40) + 'px';
                }
            }
        } else if (newShows[i]['state'] == 'SHOW_STOPPED_WITH_EXCEPTION') {
            shwState[showId] = 'STOPPED_WITH_EXCEPTION';
            el.removeClass('shwRunning');
            el.addClass('shwException');
        } else {            
            shwState[showId] = 'STOPPED';
            el.removeClass('shwRunning');
            el.removeClass('shwException');
            var overlayEl = $(el.children()[0]);
            overlayEl.html('');
        }
    }

    var bmt = json.audio;
    /*
    $('shwAudioBassInner').style.width = Math.min(bmt['b']*40,40) + 'px';    
    $('shwAudioMidInner').style.width = Math.min(bmt['m']*40,40) + 'px';
    $('shwAudioTrebleInner').style.width = Math.min(bmt['t']*40,40) + 'px';
    */
    $('#shwAudioBassInner')[0].style.width = (bmt['b'] * 40) + 'px';    
    $('#shwAudioMidInner')[0].style.width = (bmt['m'] * 40) + 'px';
    $('#shwAudioTrebleInner')[0].style.width = (bmt['t'] * 40) + 'px';
    if (json.logCount!==undefined) { logUpdateNotification(json.logCount); }
    if (json.totalFrames) { recSetFrames(json.currentFrame, json.totalFrames); }
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
    var x,y,fd;
    var fic = $("#fixItemContainer");
    var fixEl = null;
    
    // set background
    if (stage.fixPanelBackgroundImage) {
    	$("#fixItemContainer").attr("style", "background-image: url('" + stage.fixPanelBackgroundImage + "'); background-repeat: no-repeat; ");
    } else {
    	$("#fixItemContainer").removeAttr("style");
    }
    
    // display in sortOrder order
    var fixOrder = new Array(); 
    for (var i = 0; i < fixtures.length; i++) { fixOrder[i] = i; };
    fixOrder.sort(function(a,b) { 
        fa = fixtures[a].sortOrder; 
        fb = fixtures[b].sortOrder; 
        return fa < fb ? -1 : (fa > fb ? 1 : 0); } );
    
    for (var j = 0; j < fixtures.length; j++) {
    	i = fixOrder[j];
        x = 10 + (j % 4) * 200; y = 10 + Math.floor(j / 4) * 90;
        f = fixtures[i]; 
        fd = fixtureDefs[f.type];
        if (f.x != null && f.y != null) { x = f.x; y = f.y; }
        if (f.fpType == "L") {
	        fixEl = $("<div>", { 
	            "id": "fixItem[" + i + "]", "fixtureId": i,
	            "class" : "fixItem",
	            "selectClass" : "fixSelect" }).html(
	            "<div class=\"fixItemRecBack\"></div>" +
	            "<div class=\"fixItemLabel\">" + f.name + "</div>" +
	            "<div class=\"fixOutput\"><div class=\"fixOutputDim\"><div class=\"fixOutputDim2\"></div></div>&nbsp;<div class=\"fixOutputColor\"></div>" +
	            (fd.panRange==0 ? "" : "&nbsp;&#8596;<div class=\"fixOutputPan\">0</div>") +
	            (fd.tiltRange==0 ? "" : "&nbsp;&#8597;<div class=\"fixOutputTilt\">0</div>") +
	            "&nbsp;<div class=\"fixOutputStrobe\"></div>" +
	            "</div>" +
	            "<div class=\"fixItemIcon\"><img src=\"" + fd["img32"] + "\"></div>"
	            );
        } else if (f.fpType == "S") {
        	fixEl = $("<div>", { 
	            "id": "fixItem[" + i + "]", "fixtureId": i,
	            "class" : "fixItemHalf",
	            "selectClass" : "fixSelectHalf"}).html(
	            "<div class=\"fixItemRecBack\"></div>" +
	            "<div class=\"fixItemHalfLabel\">" + f.name + "</div>" +
	            "<div class=\"fixOutputHalf\"><div class=\"fixOutputDim\"><div class=\"fixOutputDim2\"></div></div>&nbsp;<div class=\"fixOutputColor\"></div>" +
	            (fd.panRange==0 ? "" : "&nbsp;&#8596;<div class=\"fixOutputPan\">0</div>") +
	            (fd.tiltRange==0 ? "" : "&nbsp;&#8597;<div class=\"fixOutputTilt\">0</div>") +
	            "&nbsp;<div class=\"fixOutputStrobe\"></div>" +
	            "</div>" +
	            "<div class=\"fixItemHalfIcon\"><img src=\"" + fd["img16"] + "\"></div>"
	            );
        } else if (f.fpType == "M") {
        	fixEl = $("<div>", { 
	            "id": "fixItem[" + i + "]", "fixtureId": i,
	            "class" : "fixItemMatrix",
	            "selectClass" : "fixSelectMatrix" }).html(
	            "<div class=\"fixOutputColorMatrix\"></div>"
	            );
        }
        fixEl[0].style.left = x + "px"; 
        fixEl[0].style.top = y + "px";
        fixItemEls.push(fixEl);
        fic.append(fixEl);
        fixEl.on('click', fixItemClick);
    }
    $("#fixAllNone").on('click', fixAllNoneClick); noSelect($("#fixAllNone"));
    $("#fixGroup").on('click', fixGroupClick); noSelect($("#fixGroup"));
    $("#fixCustom").on('click', fixCustomClick); noSelect($("#fixCustom"));
    $("#fixBlackout").on('click', fixBlackout); noSelect($("#fixBlackout"));
    $("#fixAim").on('click', fixAimClick);

    fixDimSlider = $('#fixDim').slider( {
        orientation: "vertical",
        min: 0, max: 255,
        slide: function(e, ui) { fixDimChange(ui.value); }
        // change: function(e, ui) { fixDimChange(v); }
    }); 

    fixStrobeSlider = $('#fixStrobe').slider( {
        orientation: "vertical",
        min: 0, max: 255,
        slide: function(e, ui) { fixStrobeChange(ui.value); }
        // change: function(e, ui) { fixDimChange(v); }
    }); 
    var fixAimPos = $('#fixAim').offset();
    fixAimDraggable = $("#fixAimHandle").draggable({
        // constraint code modified from http://www.java2s.com/Code/JavaScript/Ajax-Layer/Draganddropsnaptoabox.htm
        // handle: $('#fixAimHandle'),
        containment: [ fixAimPos.left, fixAimPos.top, fixAimPos.left + 160, fixAimPos.top + 160 ], // $('#fixAim'), // ah hang on, this isn't it is it.
        drag: function(e, ui) {
            var handleEl = $('#fixAimHandle');
            var containerEl = $('#fixAim');
            //handleDimensions=Element.getDimensions(draggable.element);
            //parentDimensions=Element.getDimensions(draggable.element.parentNode);
            handlePos=handleEl.position(); // Position.positionedOffset(draggable.element);
            fixAimDrag((handlePos.left + handleEl.width()/2)/(containerEl.width()),
                       (handlePos.top + handleEl.height()/2)/(containerEl.height()));
        }
        /*
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
        */
    });
    
    // eurgh
    //$('fixDimScrollArea').on('DOMMouseScroll', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // mozilla
    //$('fixDimScrollArea').on('mousewheel', fncWheelHandler.bindAsEventListener(fixDimSlider, 0.1));  // IE/Opera

    
    // @TODO jquery this
    /*
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
    */
   
   
    $("#fixPageUp").on('click', fixPageDownClick); noSelect($("#fixPageUp"));
    $("#fixPageDown").on('click', fixPageUpClick); noSelect($("#fixPageDown"));

    //jQuery('#fixColorPicker').farbtastic(/*'#fixColor'*/ fixColorChange);
    fixColorPicker=$.farbtastic($('#fixColorPicker'), fixColorChange);
    if (fixtures.length>0) { fixUpdateControls(0); }
} 

// @converted
function fixPageDownClick(event) {
	var el = $("#fixItemContainer");
	$(el).stop();
    $(el).animate( { scrollTop: '-=1000' }, 1000 );
	// if (fixScrollFx!=null) { fixScrollFx.cancel(); el.scrollTop=el.scrollTop-1000; fixScrollFx=null; }
	// else { fixScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop-1000, {afterFinish:function(){fixScrollFx=null;}}, 'scrollTop'); }
}

// @converted
function fixPageUpClick(event) {
	var el = $("#fixItemContainer");
    $(el).stop();
    $(el).animate( { scrollTop: '+=1000' }, 1000 );
	//if (fixScrollFx!=null) { fixScrollFx.cancel(); el.scrollTop=el.scrollTop+1000; fixScrollFx=null; }
	//else { fixScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop+1000, {afterFinish:function(){fixScrollFx=null;}}, 'scrollTop'); }
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

// @converted
function fixToggleEl(_el) {
    var el = $(_el);
	var selectClass = el.attr("selectClass");
    if (el.hasClass(selectClass)) {
        el.removeClass(selectClass); return false;
    } else {
        el.addClass(selectClass); return true;
    }
}

// @converted
var fixLastFixSelectedEl = null;
var fixSelectIndividual = false;
function fixItemClick(event) {
    var fixItemEl = $(event.delegateTarget);
    // while (fixItemEls.indexOf(fixItemEl) == -1) { fixItemEl = fixItemEl.parent(); }
    if (fixSelectIndividual) {
        if (fixLastFixSelectedEl != null && fixLastFixSelectedEl != fixItemEl) { 
            fixLastFixSelectedEl.removeClass(fixLastFixSelectedEl.attr("selectClass")); 
        }
    }
    fixLastFixSelectedEl = fixToggleEl(fixItemEl) ? fixItemEl : null;

    var fixItems = new Array();
    $.each(fixItemEls, function(i, f) {
            if (f.hasClass(f.attr("selectClass"))) { 
                fixItems.push( f.attr("fixtureId")); 
            };
        });
    if (fixItems.length == 1) {
    	var fd = fixtureDefs[fixtures[fixItems[0]].type];
    	var cn = $("#fixAim").children(); 
    	for (var i = cn.length - 1; i > 1; i--) {
        	// $(cn[i]).parent().removeChild(cn[i]);
        	$(cn[i]).remove();
        }
        fixLabelAim(0, fd["panRange"], 0, fd["tiltRange"]);
    	fixUpdateControls(fixItems[0]);
    	$("#fixAllNone").removeClass("fixSmallSelect");
    } else if (fixItems.length == 0) {
        $("#fixAimLeft").html("");
        $("#fixAimRight").html("");
        $("#fixAimTop").html("");
        $("#fixAimBottom").html("");
        var cn=$("#fixAim").children();
        for (var i = cn.length - 1; i > 1; i--) {
        	// cn.item(i).parentNode.removeChild(cn.item(i));
        	$(cn[i]).remove();
        }
        $("#fixAllNone").removeClass("fixSmallSelect");
    } else if (fixItems.length == fixtures.length) {
    	$("#fixAllNone").addClass("fixSmallSelect");
    } else {
    	$("#fixAllNone").removeClass("fixSmallSelect");
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
}

// @converted
function fixLabelAim(panMin, panMax, tiltMin, tiltMax) {
    $("#fixAimLeft").html("&#8592; " + panMin + "&deg;");
    $("#fixAimRight").html(panMax + "&deg; &#8594;");
    $("#fixAimTop").html("&#8593;<br/>" + tiltMin + "&deg;");
    $("#fixAimBottom").html("&#8595;<br/>" + tiltMax + "&deg;");
    var fixAimEl = $("fixAim");
    for (var x = 0; x < panMax; x += 45) {
    	var gridEl = $("<div>", { 
            "class" : (x % 180 == 0) ? "fixAimVGrid2" : "fixAimVGrid1",
            "style" : "left: " + (x * 160 / panMax) + "px;" });
        fixAimEl.append(gridEl);
    }
    for (var y = 0; y < tiltMax; y += 45) {
        var gridEl = $("<div>", { 
            "class" : (y % 180 == 0) ? "fixAimHGrid2" : "fixAimHGrid1",
            "style" : "top: " + (y * 160 / tiltMax) + "px;" });
        fixAimEl.append(gridEl);
    }
}

// make the main fixture controls reflect the current state of the
// supplied fixture
// @converted maybe, check handle later
function fixUpdateControls(fixtureId) {
    var f = fixtures[fixtureId];
    var fd = fixtureDefs[f.type];
    var v = fixValues[fixtureId];
    fixUIUpdateOnly = true;
    fixColorPicker.setColor(v["c"]);
    // @TODO jQuery
    /* fixDimSlider.val(1 - v["d"]);
    if (fixValues[fixtureId]["s"]) {
    	fixStrobeSlider.setValue(
    		1 - ((v["s"]-fd["minStrobeHertz"])/
    		(fd["maxStrobeHertz"]-fd["minStrobeHertz"])));
    } else {
    	fixStrobeSlider.setValue(1);
    }
    */
    
    var aimHandleEl=$("#fixAimHandle");
    var aimParentEl=aimHandleEl.parent();
    var aimActualEl=$("#fixAimActual");
    // var aimHandleDimensions=Element.getDimensions(aimHandleEl);
    // var aimParentDimensions=Element.getDimensions(aimHandleEl.parentNode);
    aimHandleEl[0].style.left = (v["p"] * aimParentEl.width() / fd["panRange"]) + "px"; //  - aimHandleEl.width() / 2)
    aimHandleEl[0].style.top = (v["t"] * aimParentEl.height() / fd["tiltRange"]) + "px"; //  - aimHandleEl.height() / 2
    aimActualEl[0].style.left = (v["ap"] * aimParentEl.width() / fd["panRange"]) + "px"; //  - aimHandleEl.width() / 2
    aimActualEl[0].style.top = (v["at"] * aimParentEl.height() / fd["tiltRange"]) + "px"; //  - aimHandleEl.height() / 2
    
    var ccs = fd["customControls"];
    var ccEl;
    if (ccs && fixCustomControlsVisible) {
    	for (var i = 0; i < ccs.length; i++) {
    		ccEl = $("#fixCC[" + i + "]");
    		if (ccEl.length > 0) {
	    		if (ccs[i].uiType == "TOGGLE") {
	    			if (v["ccs"][i] == 1) { ccEl.addClass("fixSmallSelect"); }
	    			else { ccEl.removeClass("fixSmallSelect"); }
	    		} else if (ccs[i].uiType == "SLIDER") {
                    /* @TODO jQuery
	    			try {
	    				fixCustomControls[i].setValue(1-(v["ccs"][i]/255));
	    			} catch (e) {
	    				// TypeError can occur if custom control display is still being shown for wrong fixture
	    			} 
	    			*/
	    		} else if (ccs[i].uiType == "GRID") {
	    			var x = Math.floor(v["ccs"][i]/160); 
	    			var y = (v["ccs"][i]%160); 
	    			x=x<0?0:(x>159?159:x); y=y<0?0:(y>159?159:y);
	    			fixCustomControls[i].handle.style.left = (x-10) + "px";
	    			fixCustomControls[i].handle.style.top = (y-10) + "px";
	    		}
    		}
    	}
    }
    
    fixUIUpdateOnly=false;
}

// @converted
function fixSameValue(values) {
	var sameValue = values[0];
	for (var i = 1; i < values.length; i++) {
		if (values[i] == null) { return null; }
		if (values[i] != sameValue) { return null; }
	}
	return sameValue;
}

// same as fixUpdateControls, but only update where all fixtures have the same value
// TODO: set other controls opacity to n% ?

// @converted
function fixUpdateControlsArray(fixtureIds) {
    var values = new Array();
    var value, valuePan, valueTilt;
    fixUIUpdateOnly=true;

    values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixtures[fid].type); } ); 
    fd = fixSameValue(values);
    if (fd) { fd = fixtureDefs[fd]; }
    
    values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["c"]); } ); 
    value = fixSameValue(values);
    if (value != null) { fixColorPicker.setColor(value); }
    
    values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["d"]); } ); 
    value = fixSameValue(values);
    // if (value !=null ) { fixDimSlider.setValue( 1 - value ); } // @TODO jQuery

    if (fd) {  // all fixtures of same type
      values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["s"]); } ); 
      value=fixSameValue(values);
      if (value!=null) {
          /** @TODO jquery 
    	fixStrobeSlider.setValue(1-((value-fd["minStrobeHertz"])/
    	    (fd["maxStrobeHertz"]-fd["minStrobeHertz"])));
    	    */
      } else {
          /** @TODO jquery
    	  fixStrobeSlider.setValue(1);
    	  */
      }
  
      var aimHandleEl = $("#fixAimHandle");
      var aimParentEl = aimHandleEl.parent();
      
      var aimActualEl = $("#fixAimActual");
      // var aimHandleDimensions = Element.getDimensions(aimHandleEl);
      // var aimParentDimensions = Element.getDimensions(aimHandleEl.parentNode);
    
      values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["p"]); } ); 
      valuePan = fixSameValue(values);
      values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["t"]); } ); 
      valueTilt = fixSameValue(values);
      
      if (valuePan!=null && valueTilt!=null) {  // NB: same as above
          aimHandleEl[0].style.left = (valuePan * aimParentEl.width() / fd["panRange"] - aimHandleEl.width() / 2) + "px";
          aimHandleEl[0].style.top = (valueTilt * aimParentEl.height() / fd["tiltRange"] - aimHandleEl.height() / 2) + "px";
      }
      values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["ap"]); } ); 
      valuePan = fixSameValue(values);
      values=[]; $.each(fixtureIds, function(i, fid) { values.push(fixValues[fid]["at"]); } ); 
      valueTilt = fixSameValue(values);
      if (valuePan!=null && valueTilt!=null) {  // NB: same as above
          aimActualEl[0].style.left = ( valuePan * aimParentEl.width() / fd["panRange"] - aimHandleEl.width() / 2) + "px";
          aimActualEl[0].style.top = ( valueTilt * aimParentEl.height() / fd["tiltRange"] - aimHandleEl.height() / 2) + "px";
      }
      var ccs = fd["customControls"];
      var ccEl;
      if (ccs && fixCustomControlsVisible) {
    	for (var i=0; i<ccs.length; i++) {
    		ccEl = $("#fixCC\\[" + i + "\\]");
    		if (ccEl) {
    			values=[]; fixtureIds.each(function(fid){values.push(fixValues[fid]["ccs"][i]);}); value=fixSameValue(values);
    			if (value!=null) {
	    		  if (ccs[i].uiType=="TOGGLE") {
	    			if (value==1) { ccEl.addClass("fixSmallSelect"); }
	    			else { ccEl.removeClass("fixSmallSelect"); }
	    		  } else if (ccs[i].uiType=="SLIDER") {
                      // @TODO jQuery
	    			  // fixCustomControls[i].setValue(1-(value/255));
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
    
    fixUIUpdateOnly=false;
}

// @converted
function fixGroupClick(event) {
    var fixGroupEl = $(event.delegateTarget);
    fixSelectIndividual = !fixSelectIndividual;
    if (fixSelectIndividual) {
    	fixGroupEl.addClass("fixSmallSelect"); 
    } else {
    	fixGroupEl.removeClass("fixSmallSelect"); 
    }
    if (fixSelectIndividual) {
    	$.each(fixItemEls, function(i, f){f.removeClass(f.attr("selectClass"));});
        if (fixLastFixSelectedEl!=null) { fixLastFixSelectedEl.addClass(fixLastFixSelectedEl.attr("selectClass")); }
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
}

// @converted
function fixAllNoneClick(event) {
    var fixAllNoneEl = $(event.delegateTarget);
    var countFixSelected=0;
    $.each(fixItemEls, function(i, f){ if (f.hasClass(f.attr("selectClass"))) { countFixSelected++;}; } );
    if (countFixSelected == fixtures.length) {
    	fixAllNoneEl.removeClass("fixSmallSelect");
    	$.each(fixItemEls, function(i, f) { f.removeClass(f.attr("selectClass"));});
    } else {
    	fixAllNoneEl.addClass("fixSmallSelect");
    	$.each(fixItemEls, function(i, f) { f.addClass(f.attr("selectClass"));});
    	if (fixSelectIndividual) {
    		fixSelectIndividual=false;
    		$("fixGroup").removeClass("fixSmallSelect"); 
    	}
    }
    if (fixCustomControlsVisible) { fixUpdateCustomControls(); }
}

// show/hide the custom controls panel
// @converted
function fixCustomClick(event) {
	fixCustomEl = $(event.delegateTarget);
	fixCustomControlsVisible = !fixCustomControlsVisible;
	if (fixCustomControlsVisible) {
		fixCustomEl.addClass("fixSmallSelect");
		$("#fixStandardControls")[0].style.visibility="hidden";
		$("#fixCustomControls")[0].style.visibility="visible";
		fixUpdateCustomControls();
	} else {
		fixCustomEl.removeClass("fixSmallSelect");
		$("#fixStandardControls")[0].style.visibility="visible";
		$("#fixCustomControls")[0].style.visibility="hidden";
	}
}

// @converted
function fixUpdateCustomControls() {
	var cc,tmft=false,i,fixtureId=null,fd=null;
    var ccEl=$("#fixCustomControls");
    $.each(fixItemEls, function(i, f) {
        if (f.hasClass(f.attr("selectClass"))) { 
            fixtureId=f.attr("fixtureId");
            if (fd==null) { fd=fixtureDefs[fixtures[fixtureId].type]; }
            else if (fd!=fixtureDefs[fixtures[fixtureId].type]) {
                tmft=true;
            }
        };
    });
    if (fd==null) {
    	ccEl.html("Select a fixture or set of fixtures of the same type.");
    	fixCustomControlFixtureDef=null;
    } else if (tmft) {
    	ccEl.html("Too many fixture types selected. Select a fixture or set of fixtures of the same type.");
    	fixCustomControlFixtureDef=null;
    } else {
    	if (fixCustomControlFixtureDef!=fd) {
    		fixCustomControlFixtureDef=null;
    		ccEl.html("");  // have to remove event listeners on removed controls ?
    		if (fd["customControls"]) {
	    		for (i = 0; i < fd["customControls"].length; i++) {
	    			cc=fd["customControls"][i];
	    			var ctrlEl;
	    			if (cc["uiType"]=="TOGGLE") {
	    		        ctrlEl = $("<div>", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomToggle" }).html(
	    		          cc.image ? ("<img src=\"" + cc.image + "\" />") : cc.label
	    		        );
	    		        ccEl.appendChild(ctrlEl);
	    		        if (cc.top) { 
                            // ctrlEl.absolutize(); // @TODO jQuery 
                            ctrlEl[0].style.position='absolute';
                            ctrlEl[0].style.top=cc.top; 
                            ctrlEl[0].style.left=cc.left; 
                        }
	    		        ctrlEl.on('click', curry(fixCustomToggleClick, i));
	    		        noSelect("#" + ctrlEl);
	    			} else if (cc["uiType"]=="SLIDER") {
	    				ctrlEl = $("<div>", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomSlider" });
	    				var handleEl = $("<div>", { "class" : "fixCustomSliderHandle" });
	    				ccEl.appendChild(ctrlEl);
	    				if (cc.top) { 
                            // ctrlEl.absolutize();
                            ctrlEl.style.position='absolute'; 
                            ctrlEl.style.top=cc.top; 
                            ctrlEl.style.left=cc.left; 
                        }
	    				ctrlEl.appendChild(handleEl);
	    				/* @TODO jQuery
	    				fixCustomControls[i] = new Control.Slider(handleEl, ctrlEl, {
	    			        axis: "vertical",
	    			        sliderValue: 1-(fixValues[fixtureId]["ccs"][i]/255),
	    			        onSlide: fixCustomSliderChange.curry(i),  // value is 2nd param
	    			        onChange: fixCustomSliderChange.curry(i) 
	    			    });
	    			    */
	    				var labelEl = $("<div>", {"class": "fixCustomSliderLabel"}).html(cc.label);
	    				labelEl[0].style.left = ctrlEl.position().left+"px"; // was positionedOffset(); maybe offset() here ?
	    				ccEl.appendChild(labelEl);
	    			} else if (cc["uiType"]=="GRID") {
	    				ctrlEl = $("<div>", { 
	    		            "id": "fixCC[" + i + "]", 
	    		            "controlId": i,
	    		            "class" : "fixCustomGrid" });
	    				var handleEl = $("<div>", { "class" : "fixCustomGridHandle" });
	    				ccEl.appendChild(ctrlEl);
	    				if (cc.top) { 
                            ctrlEl.style.position='absolute'; 
                            ctrlEl.style.top=cc.top; 
                            ctrlEl.style.left=cc.left; 
                        }
	    				ctrlEl.appendChild(handleEl);
	    				/* @TODO jQuery
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
	    			    */
	    				var labelEl = $("<div>", {"class": "fixCustomSliderLabel"}).update(cc.label);
	    				labelEl[0].style.left=ctrlEl.position().left+"px"; // offset() ?
	    				ccEl.appendChild(labelEl);	    				
	    			} else {
	    				alert("Unknown control type '" + cc["uiType"] + "'");
	    			}
	    		}
    		} else {
    			ccEl.html("No custom controls for this fixture type");
    		}
    		fixCustomControlFixtureDef=fd;
    	}
    }
}

// @converted
function fixGetItems() {
    var fixItems=new Array();
    $.each(fixItemEls, function(i, f){if (f.hasClass(f.attr("selectClass"))){fixItems.push(f.attr("fixtureId"));};});
    return fixItems;
}

// @converted
function fixGetItemIds() {
	return fixGetItems().join(",");
}

// @converted
function fixBlackout(event) {
    sendRequest('fancyController.html?action=fixtureBlackout&fixtureIds=' + fixGetItemIds());
}

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
// @converted
function ajaxLimitter(_minRequestInterval, _finalRequestInterval) {
    
    return function(minRequestInterval, finalRequestInterval) {
        
        //var minRequestInterval = minRequestInterval;
        //var finalRequestInterval = finalRequestInterval;
        var newValue = null;
        var lastValueSetTime =- 1;
        var newValueTimeoutId = -1;        
        
        function _sendRequest(url) { // could pass in value here to prevent duplicate requests going through
            var now=new Date().getTime();
            if (now - lastValueSetTime > minRequestInterval) {
                lastValueSetTime=now;
                if (newValueTimeoutId != -1) { window.clearTimeout(newValueTimeoutId); }
                newValueTimeoutId=-1;
                sendRequest(url);
            } else {
                if (newValueTimeoutId==-1) {
                    newValueTimeoutId=window.setTimeout(curry(_sendRequest, url), finalRequestInterval);
                } else {
                    window.clearTimeout(newValueTimeoutId);
                    newValueTimeoutId=window.setTimeout(curry(_sendRequest, url), finalRequestInterval);
                }
            }
        }
        
        return {
            sendRequest: _sendRequest
        }
    }(_minRequestInterval, _finalRequestInterval);
    
};

// @converted
var fixDimLimitter = ajaxLimitter(100, 200);
function fixDimChange(v) {
	if (fixUIUpdateOnly) { return; }
    v = Math.floor(255 - v);
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixDimLimitter.sendRequest( 
           'fancyController.html?action=fixtureDim&v=' + v + '&fixtureIds=' + fixItemIds);
        if (isRecording) { fixRecTouch(fixGetItems()); }
    }
}

// @converted
var fixStrobeLimitter = ajaxLimitter(100, 200);
function fixStrobeChange(v) {
	if (fixUIUpdateOnly) { return; }
    v=Math.floor(255 - v);
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixDimLimitter.sendRequest( 
           'fancyController.html?action=fixtureStrobe&v=' + v + '&fixtureIds=' + fixItemIds);
        if (isRecording) { fixRecTouch(fixGetItems()); }
    }
    
}

var fixColorLimitter = ajaxLimitter(100, 200);
function fixColorChange(color) {
	if (fixUIUpdateOnly) { return; }
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
        fixColorLimitter.sendRequest( 
           'fancyController.html?action=fixtureColor&color=' + color.substring(1) + '&fixtureIds=' + fixItemIds);
        if (isRecording) { fixRecTouch(fixGetItems()); }
    }
    
}

// @converted
var fixAimLimitter = ajaxLimitter(100, 200);
function fixAimDrag(x, y) {
	if (fixUIUpdateOnly) { return; }
    var fixItemIds=fixGetItemIds();
    if (fixItemIds!="") {
       fixAimLimitter.sendRequest( 
           'fancyController.html?action=fixtureAim&x=' + (x*100) + '&y=' + (y*100) + '&fixtureIds=' + fixItemIds);
       if (isRecording) { fixRecTouch(fixGetItems()); }
    }
    
}

// @converted
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

// @converted
var fixCustomSliderLimitter = ajaxLimitter(100, 200);
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

// @TODO jQuery
var fixCustomGridLimitter = ajaxLimitter(100,200);
function fixCustomGridChange(controlId, draggable, event) {
	if (fixUIUpdateOnly) { return; }
    var handlePos=Position.positionedOffset(draggable.element); // positions range from -10-150
    //var x = (handlePos[0]+handleDimensions.width/2)/(parentDimensions.width);
    //var y = (handlePos[1]+handleDimensions.height/2)/(parentDimensions.height);
	var fixItemIds=fixGetItemIds();
	if (fixItems.length > 0) {
       fixCustomGridLimitter.sendRequest( 
          'fancyController.html?action=customControl&controlId=' + controlId + '&value=' + ((handlePos[0]+10)*160 + handlePos[1]+10) + '&fixtureIds=' + fixItemIds);
       if (isRecording) { fixRecTouch(fixGetItems()); }     
  	}
}

// @converted
function fixRecTouch(fixtureIds) {
	for (var i=0; i<fixtureIds.length; i++) {
	  var fixItemEl = $("#fixItem[" + fixtureIds[i] + "]").children()[0];
	  var fpType = fixtures[fixtureIds[i]].fpType;
	  if (fpType == "L") { fixItemEl.addClass("fixItemRec"); }
	  else if (fpType == "S") { fixItemEl.addClass("fixItemHalfRec"); }
	  else if (fpType == "M") { /*fixItemEl.addClass("fixItemHalf"); something else probably */ }
	}  
}

// @converted
function fixUpdatePanel(json) {
    fixValues = json.fixValues;
    for (var i=0; i<fixValues.length; i++) {
        var fixValue = fixValues[i];
        var fpType = fixtures[i].fpType;
        var el = $("#fixItem\\[" + i + "\\]");
        if (fpType=="L" || fpType=="S") {
          var fd=fixtureDefs[fixtures[i].type];
          var divEls = el[0].getElementsByTagName("DIV");
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
          el[0].getElementsByTagName("DIV")[0].style.backgroundColor=fixValue["c"];
        }  
    }
    var fixItems=new Array();
    $.each(fixItemEls, function(i, f) { if (f.hasClass(f.attr("selectClass"))){fixItems.push(f.attr("fixtureId"));};});
    if (fixItems.length==1) { fixUpdateControls(fixItems[0]); }
    else if (fixItems.length>1) { fixUpdateControlsArray(fixItems); }
    
    if (json.logCount!==undefined) { logUpdateNotification(json.logCount); }
    if (json.totalFrames) { recSetFrames(json.currentFrame, json.totalFrames); }
}


/******************************* DMX PANEL ******************************/
var dmxSelectedFixture=null;  // fixture being highlighted
var dmxSelectedChannel=null;  // fixture being editted via keyboard
var dmxSelectedValue=null;
var dmxOrigValue=null;
var dmxHighlightedChannel=null; // fixture being editted via slider
var dmxSlider=null;
var dmxUIUpdateOnly = false;

// @converted
function dmxInitPanel() {
	
	// could either create all universes/banks here and then just display the active one
	// or update the elements on a single panel to reflect the current universe/bank 
	
    var x,y,f,dmxBoxEl;
    var dv=$("#dmxValues");
    for (var i=1; i<=256; i++) { 
        x=20+((i-1)%16)*50; y=90+Math.floor((i-1)/16)*30;
        var dmxEl=$("<div>", { "class" : "dmxValue",
          "id" : "dmxBox[" + i + "]", 
          "style" : "left:" + x + "px; top:" + y + "px",
          "dmxChannel" : i}).html(
          "<div class=\"dmxOffset\">" + i + "</div>" +
          "<div id=\"dmxValue[" + i + "]\">" + dmxValues[i-1] + "</div>"
          );
        dv.append(dmxEl);
        $(dmxEl).on('click', dmxValueClick);
        $(dmxEl).on('mouseover', dmxValueOnMouseOver);
        $(dmxEl).on('mouseout', dmxValueOnMouseOut);
    }
    for (var i=0; i<fixtures.length; i++) {
    	f=fixtures[i];
    	if (f.universeIdx==0 && f.dmxOffset<=256) {
    	  var dmxFixtureIconEl=$("<div>", {"class" : "dmxFixtureIcon" }).html(
    		"<img src=\"" + fixtureDefs[f.type]["img16"] + "\">");
    	  var dmxBoxEl=$("dmxBox[" + f["dmxOffset"] + "]");
    	  dmxBoxEl.append( dmxFixtureIconEl );
    	  dmxBoxEl.addClass("dmxValueWithFixture");
    	}  
    }
    dmxSlider = $('#dmxSlider').slider( {
        orientation: "vertical",
        min: 0, max: 255,
        slide: function(e, ui) { dmxSliderChange(ui.value); }
    });
    
    /* @TODO jQuery
    dmxSlider = new Control.Slider("dmxSliderHandle", "dmxSlider", {
        axis: "vertical",
        onSlide: function(v) { dmxSliderChange(v); },
        onChange: function(v) { dmxSliderChange(v); }
    });
    Event.observe('dmxSliderScrollArea', 'DOMMouseScroll', fncWheelHandler.bindAsEventListener(dmxSlider, 0.1));  // mozilla
    Event.observe('dmxSliderScrollArea', 'mousewheel', fncWheelHandler.bindAsEventListener(dmxSlider, 0.1));  // IE/Opera
    Event.observe('dmxSliderScrollArea', 'click', dmxValueClick);
    */
    $("#dmxSliderScrollArea")[0].style.visibility="hidden";
    
    $("#dmxPrevBank").on('click', dmxPrevBank);
    $("#dmxNextBank").on('click', dmxNextBank);

}

// @converted
var dmxSliderLimitter = ajaxLimitter(100, 200);
function dmxSliderChange(v) {
	if (dmxUIUpdateOnly) { return; }
    v = Math.floor(255 - v);
    dmxSliderLimitter.sendRequest( 
       'fancyController.html?action=setDmxValue&channel=' + dmxHighlightedChannel + '&value=' + v);
}


// @converted
function dmxValueClick(event) {
    var dmxValueEl, el, el2, ch, f, off, dc, j;
    dmxValueEl = $(event.delegateTarget);
    if (dmxValueEl.attr('id')=='dmxSliderScrollArea') {   // ==$("#dmxSliderScrollArea")
    	ch = dmxHighlightedChannel; 
    	dmxValueEl = $("#dmxBox[" + ch + "]");
    } else {
    	ch=$(dmxValueEl).attr("dmxChannel");
    	while (!ch && dmxValueEl!=null) {
            dmxValueEl=dmxValueEl.parent(); 
            ch=$(dmxValueEl).attr("dmxChannel"); 
        }
    }
    f=dmxToFixture[ch]; 
    if (dmxSelectedFixture!=f && dmxSelectedFixture!=null) {
        off=dmxSelectedFixture["dmxOffset"];
        dc=fixtureDefs[dmxSelectedFixture.type]["dmxChannels"];
        for (j=off; j<off+dc; j++) {
        	if (j>=1 && j<=256) {  // @XXX: visible universe/bank
	            el = $("#dmxBox[" + j + "]");
	            el.removeClass("dmxSelectGroup");
	            el.removeClass("dmxSelect");
        	}
        }
    }
    el = $("#dmxBox[" + ch + "]");
    el2 = $("#dmxValue[" + ch + "]"); 
    if (dmxSelectedValue) {
    	if (dmxImmediate) {
    		sendRequest("fancyController.html?action=setDmxValue&channel=" + dmxSelectedChannel + "&value=" + dmxSelectedValue.innerHTML);
    	}
    	dmxSelectedValue.removeClass("dmxSelectedValue");
    }
    if (el2 == dmxSelectedValue) {
    	dmxSelectedValue = null;
    	return;
    }
    dmxSelectedChannel = ch;
    dmxSelectedValue = el2;
    dmxOrigValue = dmxValues[ch-1];
	el2.addClass("dmxSelectedValue");
	// capture keystrokes from here on
	$(document).on('keypress', dmxKeypress);
	/* @TODO check if this is still needed
	if (BrowserDetect.browser=="Chrome") { 
		Event.observe(document, 'keydown', dmxKeydown); 
	}
	*/
}

// only required for chrome, which doesn't pass backspaces through to dmxKeypress
// @converted
function dmxKeydown(event) {
	if (event.keyCode==8) { dmxKeypress(event); event.stop(); }
	if (event.keyCode==27) { dmxKeypress(event); event.stop(); }
}

// @converted
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
    	dmxCancelValueUpdate();
    	break;
    case Event.KEY_RETURN:
    	dmxSelectedValue.removeClass("dmxSelectedValue");
    	//if (dmxImmediate) {
    		sendRequest("fancyController.html?action=setDmxValue&channel=" + dmxSelectedChannel + "&value=" + v);
    		dmxUIUpdateOnly=true;
    		dmxSlider.slider('value', (1-v/255) * 100);
    		dmxUIUpdateOnly=false;
    	//}
    	$(document).off('keypress', dmxKeypress);
    	$(document).off('keydown', dmxKeydown);
    	dmxSelectedChannel=null;
    	break;
    	
    case Event.KEY_LEFT:
    case Event.KEY_RIGHT:
    case Event.KEY_UP:
    case Event.KEY_DOWN:
    case Event.TAB:
    }

}

// @converted
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
// @converted
function dmxCancelValueUpdate() {
	v = dmxOrigValue;
	dmxValues[dmxSelectedChannel-1]=v;
	dmxSelectedValue.innerHTML = v;
    dmxSelectedValue.removeClass("dmxSelectedValue");
    $(document).off('keypress', dmxKeypress);
    $(document).off('keydown', dmxKeydown);
    dmxSelectedChannel=null;
    // event.stop();	
}

// @converted
function dmxValueOnMouseOver(event) {
	var dmxValueEl, el, ch, f, off, dc, j, cds, cd = null, dmxSliderEl;
	// if (dmxSlider.dragging) { return; }
    dmxValueEl = event.delegateTarget;
    ch=$(dmxValueEl).attr("dmxChannel");
    while (!ch && dmxValueEl!=null) {dmxValueEl=dmxValueEl.parentNode; ch=$(dmxValueEl).attr("dmxChannel"); }
    f=dmxToFixture[ch]; 
    if (dmxSelectedFixture!=f && dmxSelectedFixture!=null) {
        off=dmxSelectedFixture["dmxOffset"];
        dc=fixtureDefs[dmxSelectedFixture.type]["dmxChannels"];
    	for (j=off; j<off+dc; j++) {
    		if (j>=1 && j<=256) {  // @XXX: visible universe/bank
    		    el = $("#dmxBox[" + j + "]");
    		    el.removeClass("dmxSelectGroup");
    		    el.removeClass("dmxSelect");
    		}
    	}
    }
    el=$("#dmxTimeSource");
    if (!f) { 
    	el.html(dmxTimeSourceText);
    	dmxSelectedFixture=null;
    	$("#dmxSliderScrollArea")[0].style.visibility="hidden";
    	// TODO: possibly do this on panel change as well ?
    	$(document).off('keypress', dmxSliderKeypress);
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
    el.html("Name: <b>" + f["name"] + "</b><br/>" +
    	"<img valign=\"text-bottom\" src=\"" + fd["img16"] + "\"> " + fd["label"] + "<br/>" + 
    	"<img valign=\"text-bottom\" src=\"image/channel/offset_16x16.png\"> Offset: " + f["dmxOffset"] + "<br/>" +
    	(cd==null ? "" : "<img valign=\"text-bottom\" src=\"" + cd["img"] + "\"> " + cd["label"]) +
    	" (Channel: " + (ch-off) + ")");
    //}
    for (j=off; j<off+dc; j++) {
    	if (j>=1 && j<=256) {  // @XXX: visible universe/bank
    	    el = $("#dmxBox[" + j + "]");
    	    if (j==ch) {
    	    	el.removeClass("dmxSelectGroup");
    	    	el.addClass("dmxSelect");
    	    	dmxHighlightedChannel=ch;
    	    } else {
    	    	el.addClass("dmxSelectGroup");
    	    }
    	}
    }
	dmxSliderEl=$("#dmxSliderScrollArea");
	dmxSliderEl[0].style.visibility="visible";
	// var pos=Position.positionedOffset(dmxValueEl);
	var pos = $(dmxValueEl).position();
	dmxSliderEl[0].style.left = (pos.left - 3) + "px";
	dmxSliderEl[0].style.top = (pos.top - 3) + "px";
	dmxUIUpdateOnly=true;
	dmxSlider.slider('value', 255 - dmxValues[dmxHighlightedChannel-1]);
	dmxUIUpdateOnly=false;
    dmxSelectedFixture=f;
    $(document).on('keypress', dmxSliderKeypress);
}

// @converted
function dmxValueOnMouseOut(event) {
    var dmxValueEl = $(event.delegateTarget);
    var ch=$(dmxValueEl).attr("dmxChannel");
    while (!ch && dmxValueEl.legnth > 0) {dmxValueEl=$(dmxValueEl).parent(); if (dmxValueEl) { ch=dmxValueEl.attr("dmxChannel"); } }
    if (dmxValueEl==null) { return; }
    //var f=dmxToFixture[ch];
    //if (!f) { return; }
    f=dmxSelectedFixture;  // current selected fixture
    if (!f) { return; }
    var off=f["dmxOffset"];
    var dc=fixtureDefs[f.type]["dmxChannels"];
    dmxValueEl.removeClass("dmxSelect");
    if (ch>=off && ch<off+dc) {
    	dmxValueEl.addClass("dmxSelectGroup");
    }
}

// @converted
function dmxUpdatePanel(json) {
	dmxSetUniverse(json.currentUniverse, json.currentBank);
	if (json.dmxValues) {
        var dmxValuesNew = json.dmxValues.split(",");
        for (var i=1; i<=255; i++) {
            var el = $("#dmxValue\\[" + i + "\\]");
            if (dmxValues[i-1]!=dmxValuesNew[i-1]) {
            	dmxValues[i-1]=dmxValuesNew[i-1];
            	if (i!=dmxSelectedChannel) {
    	        	el[0].innerHTML=dmxValues[i-1];
    	        	el.addClass("dmxModified");
    	        	dmxModified[i-1] = true;
            	}
            } else if (dmxModified[i-1]) {
            	dmxModified[i-1] = false;
            	el.removeClass("dmxModified");
            }
        }
        if (dmxSelectedFixture==null) {
            $("#dmxTimeSource").html("<div class=\"dmxTime\">" + json.now + "</div>");
        }
        if (dmxHighlightedChannel && 
        	($("#dmxSliderScrollArea")[0].style.visibility=="visible") /* && (!dmxSlider.dragging) */) {
    	    dmxUIUpdateOnly=true;
    	    // @TODO jQuery
    		// dmxSlider.setValue(1-dmxValues[dmxHighlightedChannel-1]/255);
    		dmxUIUpdateOnly=false;
        }
        if (json.logCount!==undefined) { logUpdateNotification(json.logCount); }
        if (json.totalFrames) { recSetFrames(json.currentFrame, json.totalFrames); }
    }
}

// @converted
function dmxSetUniverse(newDmxCurrentUniverse, newDmxCurrentBank) {
	if (dmxCurrentUniverse!=newDmxCurrentUniverse ||
		dmxCurrentBank!=newDmxCurrentBank) {
		// clear any selected offset(s)
		if (dmxSelectedChannel!=null) { cancelDmxValueUpdate(); }
		// update universeContainer
		dmxCurrentUniverse=newDmxCurrentUniverse;
		dmxCurrentBank=newDmxCurrentBank;
		$("#dmxCurrentUniverse").html(dmxCurrentUniverse+1);
		$("#dmxCurrentBank").html(dmxCurrentBank+1);

		// @TODO update all the dmx controls to reflect current universe/bank
		for (var i=1; i<=256; i++) { 
			var dmxEl = $("#dmxBox\\[" + i + "\\]");
			dmxEl.html(
	          "<div class=\"dmxOffset\">" + (i+dmxCurrentBank*256) + "</div>" +
	          "<div id=\"dmxValue[" + i + "]\">" + dmxValues[i+dmxCurrentBank*256-1] + "</div>"
	          );
	    }
	    for (var i=0; i<fixtures.length; i++) {
	    	f=fixtures[i];
	    	if (f.universeIdx==0) {
	    	  var dmxFixtureIconEl=$("<div>", {"class" : "dmxFixtureIcon" }).html(
	    		"<img src=\"" + fixtureDefs[f.type]["img16"] + "\">");
	    	  $("#dmxBox\\[" + f["dmxOffset"] + "\\]").append( dmxFixtureIconEl );
	    	  $("#dmxBox\\[" + f["dmxOffset"] + "\\]").addClass('dmxValueWithFixture');
	    	}  
	    }
	}
}

// @converted
function dmxPrevBank() {
	sendRequest('fancyController.html?action=prevBank', dmxUpdatePanel); // 
}

// @converted
function dmxNextBank() {
	sendRequest('fancyController.html?action=nextBank', dmxUpdatePanel); // 
}


/******************************* LOG PANEL ******************************/
var logScrollFx;
// @converted  
function logInitPanel() {
    $("logPageUp").on('click', logPageDownClick); noSelect($("#logPageUp"));
    $("logPageDown").on('click', logPageUpClick); noSelect($("#logPageDown"));
    $("logClear").on('click', logClearClick); noSelect($("#logClear"));
}

// @converted
function logPageDownClick(event) {
	var el = $("logExceptionContainer");
	$(el).stop();
    $(el).animate( { scrollTop: '-=1000' }, 1000 );

	//if (logScrollFx!=null) { logScrollFx.cancel(); el.scrollTop=el.scrollTop-1000; logScrollFx=null; }
	//else { logScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop-1000, {afterFinish:function(){logScrollFx=null;}}, 'scrollTop'); }
}

// @converted
function logPageUpClick(event) {
	var el = $("logExceptionContainer");
$(el).stop();
    $(el).animate( { scrollTop: '+=1000' }, 1000 );
	
	//if (logScrollFx!=null) { logScrollFx.cancel(); el.scrollTop=el.scrollTop+1000; logScrollFx=null; }
	//else { logScrollFx = new Effect.Tween(el, el.scrollTop, el.scrollTop+1000, {afterFinish:function(){logScrollFx=null;}}, 'scrollTop'); }
}

// @converted
function logClearClick(event) {
    sendRequest('fancyController.html?action=clearLogs', logUpdatePanel);
    //reloadCometIframe();
    //startPollRequests();
}


// @converted
function logToggle(logId) {
	var e = logExceptions[logId];
	var logDetailEl = $("#logDetail\\[" + logId + "\\]");
	if (logDetailEl.length == 0) {
		var logTitleEl = $("#logTitle\\[" + logId + "\\]");
		var text;
		if (e["count"]>1) {
			text = "This exception has occurred " + e["count"] + " times.<br/>" +
			  "First occurrence: " + new Date(e["firstTimestamp"]) + "<br/>" +
			  "Last occurrence: " + new Date(e["timestamp"]) + "<br/>";
		} else {
			text = "Time of exception: " + new Date(e["timestamp"]) + "<br/>";
		}
		logDetailEl = $("<div>",
		  { "class" : "logDetail", "id" : "logDetail[" + logId + "]" }).html(
			"<pre>" + text + e.trace + "</pre>");
		logTitleEl.after(logDetailEl);
	} else {
		logDetailEl[0].style.display = logDetailEl[0].style.display=='none' ? 'block' : 'none';
	}
}

// @converted
function logUpdatePanel(json) {
    var el = $("#logExceptionContainer");
    var text;
    el.innerHTML = ""; // reset any existing DIVs
    logExceptions = json.exceptions;
    for (var i=0; i<logExceptions.length; i++) {
        var e = logExceptions[i];
        text = (e["count"]>1 ? "[" + e["count"] + "] " : "") + e.message;
        var logTitleEl = $("<div>", 
          { "class" : "logTitle", 
        	"id" : "logTitle[" + i + "]" ,
            "onclick" : "logToggle(" + i + ")"}).html(
          "<img class=\"logExpandImg\" src=\"image/logExpand.png\"/> " + 
          "<span class=\"logMessage\">" + text + "</span>");
        el.append(logTitleEl);
    }
    if (json.logCount!==undefined) { logUpdateNotification(json.logCount); }
    if (json.totalFrames) { recSetFrames(json.currentFrame, json.totalFrames); }
}

// @converted
function logUpdateNotification(logCount) {
	if (logCount!=lastLogCount) {
		if (logCount>0) {
			$("#lhsLogNotification")[0].style.display="block";
	    	$("#lhsLogNotificationText").html(logCount);
	    } else {
	    	$("#lhsLogNotification")[0].style.display="none";
	    }
		lastLogCount = logCount;
	}
}

/******************************* CONFIG PANEL ******************************/

// @converted
function cnfInitPanel() {
	$("#cnfStage").on('click', cnfStageClick);
	$("#cnfDevice").on('click', cnfDeviceClick);
	$("#cnfAudioController").on('click', cnfAudioControllerClick);
	$("#cnfAudioSource").on('click', cnfAudioSourceClick);
	$("#cnfRecord").on('click', cnfRecordClick);
	$("#cnfFixtureDef").on('click', cnfFixtureDefClick);
    $("#cnfFixture").on('click', cnfFixtureClick);
    $("#cnfShowDef").on('click', cnfShowDefClick);
    $("#cnfShow").on('click', cnfShowClick);
    $("#cnfResetAudio").on('click', cnfResetAudioClick);
    if ($("#cnfSimple").length > 0) {
    	$("#cnfSimple").on('click', cnfSimpleClick);
    }
    $("cnfImportExport").on('click', cnfImportExportClick);
    $("cnfVideo").on('click', cnfVideoClick);
    
    if (isRecording) {
    	$("#cnfRecordText").html("Stop recording");
    	$("#cnfRecord").addClass("cnfControlSelect");
    	$("#recContainer")[0].style.visibility = "visible";
    }
    
}

// @converted
function cnfStageClick() {
    document.location="maintainStage.html";
}
// @converted
function cnfDeviceClick() {
    document.location="maintainDevice.html?deviceType=D";
}
// @converted
function cnfAudioControllerClick() {
    document.location="maintainDevice.html?deviceType=C";
}
// @converted
function cnfAudioSourceClick() {
    document.location="maintainDevice.html?deviceType=S";
}
// @converted
function cnfRecordClick() {
	// NB: may be called when cnfPanel not visible
	isRecording=!isRecording;
	if (isRecording) {
		recShowDefId = null;
		$("#cnfRecord").addClass("cnfControlSelect");
		sendRequest('fancyController.html?action=startRecording', recCallback);
	} else {
		if (recShowDefId == null) {
			var showName = prompt("Enter a name for this show (or click 'Cancel' to discard this show)", "recordedShow");
			if (showName==null) {
				sendRequest('fancyController.html?action=stopRecording', recCallback);
			} else {
				sendRequest('fancyController.html?action=stopRecording&showName=' + escape(showName), recCallback);
			}
			
		} else {
			var showName = prompt("Enter a name for this show (change default to save as a new show, or click 'Cancel' to discard this show)", recShowDefName);
			if (showName==null) {
				sendRequest('fancyController.html?action=stopRecording', recCallback);
			} else {
				sendRequest('fancyController.html?action=stopRecording&recShowDefId=' + recShowDefId + '&showName=' + escape(showName), recCallback);
			}
		}
	}
	$("#recContainer")[0].style.visibility = isRecording ? "visible" : "hidden";
	$("#cnfRecordText").html(isRecording ? "Stop recording" : "Record a show");
}

// @converted all these
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
function cnfImportExportClick() {
    document.location="importExport.html";
}
function cnfVideoClick() {
    window.open("streaming.html", "streamingWindow");
}


/******************************* RECORDING ******************************/
//var recCurrentFrame = 0; // 0-based
//var recTotalFrames = 0;

// @converted
function recInitPanel() {
	$("#recContainer")[0].style.visibility = isRecording ? "visible" : "hidden";
	$("#cnfRecordText").html(isRecording ? "Stop recording" : "Start recording");
	if (isRecording) {
		$("#cnfRecord").addClass("cnfControlSelect");
		fixRecTouch(recModifiedFixtureIds);
	}
	
	recSetFrames(recCurrentFrame, recTotalFrames);  // 0, 1

    $("recPrevFrame").on('click', recPrevFrame);
    $("recNextFrame").on('click', recNextFrame);
    $("recAddFrame").on('click', recAddFrame);
    $("recDeleteFrame").on('click', recDeleteFrame);
    $("recPlay").on('click', recPlay);
    $("recRecordAnim").on('click', cnfRecordClick);
}

// @converted
function recUpdatePanel(json) {
	
}

// @converted
function recSetFrames(newRecActiveFrame, newRecTotalFrames) {
	recCurrentFrame=newRecActiveFrame;
	recTotalFrames=newRecTotalFrames;
	$("#recCurrentFrame").html(recCurrentFrame+1);
	$("#recTotalFrames").html(recTotalFrames);
}

// @converted
function recPrevFrame() {
	sendRequest('fancyController.html?action=prevFrame', recCallback);
	//recCurrentFrame--;
	recSetFrames(recCurrentFrame, recTotalFrames);
}

// @converted
function recNextFrame() {
	sendRequest('fancyController.html?action=nextFrame', recCallback);
	//recCurrentFrame++; 
	//if (recCurrentFrame+1 > recTotalFrames) { recTotalFrames = recCurrentFrame+1; }
	//recSetFrames(recCurrentFrame, recTotalFrames);
}

// @converted
function recAddFrame() {
	sendRequest('fancyController.html?action=addFrame', recCallback);
	//alert("recAddFrame");
}

// @converted
function recDeleteFrame() {
	sendRequest('fancyController.html?action=deleteFrame', recCallback);
	//alert("recDeleteFrame");
}

// @converted
function recPlay() {
	sendRequest('fancyController.html?action=playRecording', recCallback);
	// alert("recPlay");
}

// call this recUpdatePanel ?
// @converted
function recCallback(json) {
	if (json.totalFrames) { recSetFrames(json.currentFrame, json.totalFrames); }
	// set fixture/dmx value highlights for this frame ?
	if (json.shows) {
		// recreate the show panel. or just load the whole thing again.
		window.location = 'fancyController.html?panel=shwPanel'; // .reload()
	}
	if (json.hideRecFrame) { 
		$("#cnfRecord").removeClass("cnfControlSelect");
		$("#recContainer")[0].style.visibility = "hidden";
		$("#cnfRecordText").html("Start recording");
		recCurrentFrame=0;
		recTotalFrames=1;
		for (var i=0; i<fixtures.length; i++) {
		  var fixItemEl = $($("#fixItem[" + i + "]").children()[0]); // @TODO fix
		  var fpType = fixtures[i].fpType;
		  if (fpType == "L") { fixItemEl.removeClass("fixItemRec"); }
		  else if (fpType == "S") { fixItemEl.removeClass("fixItemHalfRec"); }
		  else if (fpType == "M") { /*fixItemEl.removeClass("fixItemHalf"); something else probably */ }
	    }
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
    lhsDMX(); dmxInitPanel();
    shwInitPanel();
    lhsFixtures(); fixInitPanel(); 
    recInitPanel(); // needs fixtures in place
    logInitPanel();
    cnfInitPanel();
    if (initMessage!=null) { setRhsMessageHTML(initMessage); }
    logUpdateNotification(logCount);
    disableIframe=false;
    if (origPanel=='cnfPanel') {  // from cancel buttons in editor pages
    	lhsConfig();
    } else if (origPanel=='fixPanel' || isRecording) {
    	lhsFixtures(); reloadCometIframe();  // from 'edit recorded show'
    } else if (origPanel=='shwPanel') {
    	lhsShows();               // after show recording 
    } else {
        lhsLogo();
    }

}

