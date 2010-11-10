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

    <!-- CSS -->
    <link href="stylesheets/stocktake-site.css" media="all" rel="stylesheet" type="text/css" />
    <link href="stylesheets/stocktake-print.css" media="print" rel="stylesheet" type="text/css" />
    <!--[if IE]><link href="/stylesheets/ie.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    <!--[if lt IE 8]><link href="/stylesheets/ie7.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    <!--[if lt IE 7]><link href="/stylesheets/ie6.css" rel="stylesheet" type="text/css" media="screen" /><![endif]-->
    
    <!-- JavaScript -->
    <script src="../mjs?js=prototype,scriptaculous,builder,effects,dragdrop,controls,slider,sound,rollover,facebox" type="text/javascript"></script>
    <script src="js/dmx.js" type="text/javascript"></script>
    
    <!--[if IE 6]>
    <script type="text/javascript" src="/javascripts/DD_belatedPNG_0.0.8a.js"></script>
    <script type="text/javascript">
      DD_belatedPNG.fix('#button_login, #button_start_an_auction_now,   #button_feedback,   #feedback_panel, #feedback_panel .head, #feedback_panel .body,  .btn_generic_clear, .btn_generic_clear span, .more, #process span, .button, .button span');
    </script>
    <![endif]-->

<style>
#controller { font-size: 8pt; font-family: Arial;}
#controller TD { font-size: 8pt; font-family: Arial;}
#controller INPUT { font-size: 8pt; }
#config { font-size: 8pt; font-family: Arial;}
#config TD { font-size: 8pt; font-family: Arial;}
.label { width: 25px; height: 16px; text-align: right; background-color: lightblue; padding-top: 3px; margin-left: 3px; margin-bottom: 1px;}
</style>
<script>
function startShow(showId) {
	document.location = "controller.html?action=startShow&showId=" + showId;
}
function cancelShow(showId) {
    document.location = "controller.html?action=cancelShow&showId=" + showId;
}

</script>
</head>

<html>
<body>
<h2><a href="controller.html">DMX Web</a></h2>
<c:if test="${message!=null}" >
<b><c:out value="${message}" /></b><br/>
</c:if>
<h3>Configuration:</h3>
<table id="config">
<col width="100px">
<col />
<tr><td>RXTX JAR version</td><td><%= request.getAttribute("rxtx.jarVersion") %></td></tr>
<tr><td>RXTX DLL version</td><td><%= request.getAttribute("rxtx.dllVersion") %></td></tr>
<tr><td valign="top">exception</td><td><pre><%= request.getAttribute("sent4") %></pre></td></tr>
</table>

<h2>Controller</h2>
<ul>
  <li><a href="controller.html?action=blackOut">blackOut</a></li>
  <li>Shows</li>
  <ul>
<%
    List<Show> shows = (List<Show>) request.getAttribute("shows");
    for (int i=0; i<shows.size(); i++) {
    	  Show show = shows.get(i);
%><li>Show <%= i %>: <%= show.getName() %> 
  <input type="button" value="Start" onclick="startShow(<%= i %>)"/>
  <input type="button" value="Cancel" onclick="cancelShow(<%= i %>)"/>
</li>
<%
    }
%>
  </ul>
  <li>Exceptions</li>
  <ul><li>Audio exceptions</li>
  <ul>
<%
    SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy HH:mm:ss");
    List<ExceptionContainer.TimestampedException> exceptions = controller.getAudioController().getExceptions();
    synchronized(exceptions) {
      for (int i=0; i<exceptions.size(); i++) {
    	ExceptionContainer.TimestampedException te = exceptions.get(i);
    	Date d = new Date(te.getTimestamp());
%><li><%= sdf.format(d) %> <%= te.getException().getMessage() %> <pre><%= ExceptionUtils.getStackTrace(te.getException()) %></pre>
<%
      }
    }  
%>
  </ul>
  <li>DMX exceptions</li>
  <ul>
<%  
    exceptions = appConfig.getDmxDeviceExceptions();
    synchronized(exceptions) {
      for (int i=0; i<exceptions.size(); i++) {
        ExceptionContainer.TimestampedException te = exceptions.get(i);
        Date d = new Date(te.getTimestamp());
%><li><%= sdf.format(d) %> <%= te.getException().getMessage() %> <pre><%= ExceptionUtils.getStackTrace(te.getException()) %></pre>
<%
      }
    }  
%>
  </ul>
  <li>Show exceptions</li>
  <ul>
<%  
    List<AppConfig.TimestampedShowException> showExceptions = appConfig.getShowExceptions();
    synchronized(showExceptions) {
      for (int i=0; i<showExceptions.size(); i++) {
    	AppConfig.TimestampedShowException te = showExceptions.get(i);
        Date d = new Date(te.getTimestamp());
%><li><%= sdf.format(d) %> <%= te.getShow().getName() %> <%= te.getException().getMessage() %> <pre><%= ExceptionUtils.getStackTrace(te.getException()) %></pre>
<%
      }
    }  
%>
  
  </ul>

  </ul>
  
  <li>Universe</li>
  <ul>
    <li>Timesource: <%= universe.getTimeSource().getClass().getName() %> / <%= new Date(universe.getTimeSource().getTime()) %> </li>
    <li>DMX values
    <form action="controller.html" method="post" >
    <input type="hidden" name="action" value="setDmxValues" />
<table id="controller" cellspacing=0 cellpadding=0>
<% 
    for (int i=0; i<16; i++) {
%>
    <tr>
<% 
        for (int j=0; j<16; j++) {
%>
        <td><div class="label"><%= (i*16+j+1) %></div></td>
        <td><input name="dmx[<%= i*16+j+1 %>]" value="<%= universe.getDmxChannelValue(i*16+j+1) %>" size="2"/></td>
<%
        }
%>
    </tr>
<% 
    } 
%>
    <tr>
        <td colspan="32"><input type="submit" value="Update..."/></td>
    </tr>
</table>
</form>
    
    </li>
    <li>Fixtures</li>
  <ul>
<%
  List<Fixture> fixtures = controller.getFixtures();
  for (int i = 0; i < fixtures.size(); i++) {
	  Fixture fixture = fixtures.get(i);
	  FixtureOutput output = fixture.getChannelMuxer().getOutput();
%>  
  <li>Fixture <%= i %></li>
  <ul>
    <li>Name: <%= fixture.getName() %></li>
    <li>Type: <%= fixture.getFixtureDef().getClass().getName() %></li>
    <li>Controller: <%= fixture.getFixtureController().getClass().getName() %></li>
    <li>Output: <%= fixture.getChannelMuxer().getOutput() %></li>
    <ul>
      <li><a href="controller.html?action=blackOut&fixtureId=<%= i %>">blackOut</a></li>
      <li>
        <form action="controller.html" method="get"/>
        <input type="hidden" name="action" value="setColor" />
        <input type="hidden" name="fixtureId" value="<%= i %>" />
        <table>
        <tr>
          <td><div class="label">R</div></td><td><input name="r" value="<%= output.getColor().getRed() %>" size="2"/></td>
          <td><div class="label">G</div></td><td><input name="g" value="<%= output.getColor().getGreen() %>" size="2"/></td>
          <td><div class="label">B</div></td><td><input name="b" value="<%= output.getColor().getBlue() %>" size="2"/></td>
          <td><input type="submit" value="Set color"/></td>
        </tr>
        </table>
        </form>
      </li>
      <li>
        <form action="controller.html" method="get"/>
        <input type="hidden" name="action" value="setTilt" />
        <input type="hidden" name="fixtureId" value="<%= i %>" />
        <table>
        <tr>
          <td><div class="label">T</div></td><td><input name="t" value="<%= output.getTilt() %>" size="4"/></td>
          <td><input type="submit" value="Set tilt"/></td>
        </tr>
        </table>
        </form>
      </li>
      <li>
        <form action="controller.html" method="get"/>
        <input type="hidden" name="action" value="setPan" />
        <input type="hidden" name="fixtureId" value="<%= i %>" />
        <table>
        <tr>
          <td><div class="label">P</div></td><td><input name="p" value="<%= output.getPan() %>" size="4"/></td>
          <td><input type="submit" value="Set pan"/></td>
        </tr>
        </table>
        </form>
      </li>
      <li>setStrobe</li>
      <li>setMacro</li>
    </ul> <!-- end fixture output -->
  </ul> <!-- end fixture -->
<%
    }
%>  
</ul>

</body>
</html>
