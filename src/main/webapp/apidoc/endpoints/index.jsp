<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" class="hl-en not-logged-in ">
<head>
    <meta charset="UTF-8" />
    <title>DMX-Web REST API Endpoints &bull; DMX-Web Developer Documentation</title>

    <link rel="Shortcut Icon" type="image/x-icon" href="../images/favicon.ico" />

    
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    

    
        <script type="text/javascript" src="../scripts/html5.js"></script>
        <script type="text/javascript" src="../scripts/jquery.js"></script>
        <script type="text/javascript" src="../scripts/bluebar.js"></script>
        <script type="text/javascript" src="../scripts/bluebar_desktop.js"></script>
    

    
    
    <script type="text/javascript" src="../scripts/navigation.js"></script>


    
    <link rel="stylesheet" type="text/css" href="../css/developer-main.css"/>


</head>
<body class="sidebar-page">

<div class="root">


    <div class="page">
        
        <header class="top-bar">
            <div class="wrapper">
                <hgroup>
                    <h1 class="logo"><a href="../../index.html">DMX-Web</a></h1>
                    
    <h2>Developer Documentation</h2>

                </hgroup>

                
<!-- 
                <div class="account-state">
                    <ul class="actions">
                        
<li class="manage-clients link-settings ">
	<a href="../clients/manage/index.html">Manage Clients<i></i></a>
</li>

                        
                        <li id="link_profile" class="link-signin">
                                <a href="http://instagram.com/accounts/login/?next=">
                                <i></i>
                                <strong>Login</strong>
                            </a>
                        </li>
                        
                    </ul>
                </div>
 -->                
            </div>
        </header> <!-- .top-bar -->
        

        
    <div class="sidebar">
        <div class="wrapper">
            <nav class="sidebar-nav">
                <div class="sidebar-content">
                
    

<form class="quick-search" action="http://instagram.com/developer/search/" method="GET">
    <a href="index.html#" class="disclosure-down"></a>
    <input name="q" type="text" placeholder="Search Documentation"  />
    <input type="submit" />
</form>

<ul>
    <li >
        <a href="../index.jsp">
            
            Overview
            
            <i class="disclosure"></i>
            
        </a>
        
    </li>
    <!-- 
    <li >
        <a href="authentication/index.html">
            
            Authentication
            
            <i class="disclosure"></i>
            
        </a>
        
    </li>
    <li >
        <a href="realtime/index.html">
            
            Real-time
            
            <i class="disclosure"></i>
            
        </a>
        
    </li>
     -->
    <li class="active">
        <a href="endpoints/index.jsp">
            Local REST API Endpoints
            <i class="disclosure"></i>
        </a>
            <ul>
                <li >
        <a href="shows/index.jsp" >Shows</a>
    </li>
                <li >
        <a href="fixtures/index.jsp" >Fixtures</a>
    </li>
                <li >
        <a href="dmx/index.jsp" >DMX Values</a>
    </li>
            </ul>
        
    </li>
    <li >
        <a href="embedding/index.jsp">
            Java API
            <i class="disclosure"></i>
        </a>
    </li>
    <li >
        <a href="libraries/index.jsp">
            Libraries
            <i class="disclosure"></i>
        </a>
    </li>
    <li >
        <a href="http://groups.google.com/group/instagram-api-developers">
            
            Forum
            
            <i class="disclosure"></i>
            
        </a>
        
    </li>
</ul>



                </div>
            </nav>
        </div>
    </div>
    <div class="main">
        <div class="wrapper">
            <section class="nav-page-content" role="main">
                <h1 id="instagram-api-endpoints">DMX-Web Local REST API Endpoints</h1>
                
<p>The Local REST API allows you to do things to your local DMX-Web application.

<p>(As opposed to the remote one I'll eventually create to store libraries of 
shows, fixtures etc)

<p>All local endpoints are unsecured (i.e. run over HTTP, not HTTPS) and require no authenticaiton. 
The API is located at <strong>yourhost:8080/dmx-web/api</strong>. 
For instance: you can start the show with the name 'Act 1 Scene 1' by accessing the following URL 
with your browser or other HTTP user agent 
(replace <b>yourhost</b> with the name of the machine running dmx-web):</p>
<div class="codehilite"><pre><code><span class="n">http</span><span class="p">:</span><span class="o">//</span><span class="n">yourhost:8080</span><span class="o">/</span><span class="n">dmx-web</span><span class="o">/</span><span class="n">api</span><span class="o">/</span><span class="n">startShow</span>?<span class="n">showName</span><span class="p">=</span><span class="n">Act+1+Scene+1</span>
</code></pre></div>

<p>Something fascinating about REST here.

<blockquote>
<h2 id="important-note">Important Note</h2>
<p>May want to ensure that you firewall things properly.</p>
</blockquote>

<h2 id="structure">Structure</h2>
<h3 id="the-envelope">The Envelope</h3>
<p>Every response is contained by an envelope. That is, each response has a predictable set of keys with which you can expect to interact:</p>
<div class="codehilite"><pre><code><span class="p">{</span>
    <span class="nt">&quot;meta&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;code&quot;</span><span class="p">:</span> <span class="mi">200</span>
    <span class="p">},</span>
    <span class="nt">&quot;data&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="err">...</span>
    <span class="p">}<!-- ,</span>
    <span class="nt">&quot;pagination&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;next_url&quot;</span><span class="p">:</span> <span class="s2">&quot;...&quot;</span><span class="p">,</span>
        <span class="nt">&quot;next_max_id&quot;</span><span class="p">:</span> <span class="s2">&quot;13872296&quot;</span>
    <span class="p">}</span> -->
<span class="p">}</span>
</code></pre></div>


<h4 id="meta">meta</h4>
<p>The meta key is used to communicate extra information about the response to the developer. If all goes well, you'll only ever see a code key with value 200. However, sometimes things go wrong, and in that case you might see a response like:</p>
<div class="codehilite"><pre><code><span class="p">{</span>
    <span class="nt">&quot;meta&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;error_type&quot;</span><span class="p">:</span> <span class="s2">&quot;OAuthException&quot;</span><span class="p">,</span>
        <span class="nt">&quot;code&quot;</span><span class="p">:</span> <span class="mi">400</span><span class="p">,</span>
        <span class="nt">&quot;error_message&quot;</span><span class="p">:</span> <span class="s2">&quot;...&quot;</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>


<h4 id="data">data</h4>
<p>The data key is the meat of the response. It may be a list or dictionary, but either way this is where you'll find the data you requested.</p>

<!-- 
<h4 id="pagination">pagination</h4>
<p>Sometimes you just can't get enough. For this reason, we've provided a convenient way to access more data in any request for sequential data. Simply call the url in the next_url parameter and we'll respond with the next set of data.</p>
<div class="codehilite"><pre><code><span class="p">{</span>
    <span class="err">...</span>
    <span class="nt">&quot;pagination&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;next_url&quot;</span><span class="p">:</span> <span class="s2">&quot;https://api.instagram.com/v1/tags/puppy/media/recent?access_token=fb2e77d.47a0479900504cb3ab4a1f626d174d2d&amp;max_id=13872296&quot;</span><span class="p">,</span>
        <span class="nt">&quot;next_max_id&quot;</span><span class="p">:</span> <span class="s2">&quot;13872296&quot;</span>
    <span class="p">}</span>
<span class="p">}</span>
</code></pre></div>
 
<p>On views where pagination is present, we also support the "count" parameter. Simply set this to the number of items you'd like to receive. Note that the default values should be fine for most applications - but if you decide to increase this number there is a maximum value defined on each endpoint.</p>
 -->
 
<h2 id="jsonp">JSONP</h2>
<p>If you're writing an AJAX application, and you'd like to wrap our response with a callback, all you have to do is specify a callback parameter with any API call:</p>
<div class="codehilite">
<pre><code><span class="n">http</span><span class="p">:</span><span class="o">//</span><span class="n">yourhost:8080</span><span class="o">/</span><span class="n">dmx-web</span><span class="o">/</span><span class="n">api</span><span class="o">/</span><span class="n">startShow</span>?<span class="n">showName</span><span class="p">=</span><span class="n">Act+1+Scene+1</span><span class="o">&amp;</span><span class="n">callback</span><span class="p">=</span><span class="n">callbackFunction</span>
</code></pre></div>


<p>Would respond with:</p>
<div class="codehilite"><pre><code><span class="nx">callbackFunction</span><span class="p">({</span>
    <span class="p">...</span>
<span class="p">});</span>
</code></pre></div>
            </section>
        </div> <!-- .main -->
    </div> <!-- .main -->


    </div> <!-- .page -->

    
    <footer class="page-footer">
        <div class="wrapper">
            <nav>
                <ul>
                    <li><a href="http://instagram.com/accounts/edit/">Your Account</a></li>
                    <li><a href="../../about/us/index.html">About us</a></li>
                    <li><a href="http://help.instagram.com/">Support</a></li>
                    <li><a href="http://blog.instagram.com/">Blog</a></li>
                    <li><a href="../index.html">API</a></li>
                    <li><a href="../../about/jobs/index.html">Jobs</a></li>
                    <li><a href="../../about/legal/privacy/index.html">Privacy</a></li>
                    <li><a href="../../about/legal/terms/index.html">Terms</a></li>
                </ul>
            </nav>

            <p class="copyright">&copy; 2012 DMX-Web, Inc.</p>
        </div>
    </footer>
    


</div> <!-- .root -->



</body>
</html>