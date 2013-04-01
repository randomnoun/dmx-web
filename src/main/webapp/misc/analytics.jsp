<%@ page 
  language="java"
  contentType="text/html; charset=utf-8"
  pageEncoding="utf-8"
  errorPage="misc/errorPage.jsp"
  import="com.randomnoun.dmx.config.*"
%>
<%
  AppConfig appConfig = AppConfig.getAppConfig();
  String analyticsGoogle = appConfig.getProperty("analytics.google");
  String analyticsGoingUp = appConfig.getProperty("analytics.goingup");
  String analyticsClicky = appConfig.getProperty("analytics.clicky");
  
%>
<% 
    if (analyticsGoogle!=null) {
    	if ("2".equals(appConfig.getProperty("analytics.google.version"))) {
    		String analyticsGoogleCreateParameter = appConfig.getProperty("analytics.createParameter");
%>
<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', '<%= analyticsGoogle %>'<%= analyticsGoogleCreateParameter==null?"":","+analyticsGoogleCreateParameter %>');
  ga('send', 'pageview');

</script>
<%
        } else {
        	String analyticsGoogleMultiDomain = appConfig.getProperty("analytics.google.multiDomain");
%>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', '<%= analyticsGoogle %>']);
<%
   if ("true".equals(analyticsGoogleMultiDomain)) {
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
    }
    if (analyticsGoingUp!=null) { 
%>
<script type="text/javascript">
document.write(unescape('%3Cscript type="text/javascript" src="'+
document.location.protocol+'//counter.goingup.com/js/tracker.js?st=<%= analyticsGoingUp %>&amp;b=5"%3E%3C/script%3E'));
</script>
<noscript><a href="http://www.goingup.com" title="web performance"><img src="http://counter.goingup.com/default.php?st=<%= analyticsGoingUp %>&amp;b=5" border="0" alt="web performance" /></a></noscript>
<%
    }
    if (analyticsClicky!=null) {
%>
<script src="http://static.getclicky.com/js" type="text/javascript"></script>
<script type="text/javascript">clicky.init(<%= analyticsClicky %>);</script>
<noscript><p><img alt="Clicky" width="1" height="1" src="http://in.getclicky.com/<%= analyticsClicky %>ns.gif" /></p></noscript>
<%
    }
%>