<%@ page 
  language="java"
  contentType="text/html; charset=utf-8"
  pageEncoding="utf-8"
  errorPage="misc/errorPage.jsp"
  import="com.randomnoun.dmx.config.*"
%>
<%
  AppConfig appConfig = AppConfig.getAppConfig();
%>
<% 
    if (appConfig.getProperty("analytics.google")!=null) { 
%>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '<%= appConfig.getProperty("analytics.google") %>']);
<%
   if ("true".equals(appConfig.getProperty("analytics.google.multiDomain"))) {
%>     
  _gaq.push(['_setDomainName', 'none']);
  _gaq.push(['_setAllowLinker', true]);
<%
   }
%>   
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
<% 
    }
    if (appConfig.getProperty("analytics.goingup")!=null) { 
%>
<script type="text/javascript">
document.write(unescape('%3Cscript type="text/javascript" src="'+
document.location.protocol+'//counter.goingup.com/js/tracker.js?st=mdfd3wy&amp;b=5"%3E%3C/script%3E'));
</script>
<noscript><a href="http://www.goingup.com" title="web performance"><img src="http://counter.goingup.com/default.php?st=mdfd3wy&amp;b=5" border="0" alt="web performance" /></a></noscript>
<%
    }
    if (appConfig.getProperty("analytics.clicky")!=null) {
%>
<script src="http://static.getclicky.com/js" type="text/javascript"></script>
<script type="text/javascript">clicky.init(253570);</script>
<noscript><p><img alt="Clicky" width="1" height="1" src="http://in.getclicky.com/253570ns.gif" /></p></noscript>
<%
    }
%>