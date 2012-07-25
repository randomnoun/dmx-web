<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" class="hl-en not-logged-in ">
<head>
    <meta charset="UTF-8" />
    <title>DMX-Web Developer Documentation</title>

    <link rel="Shortcut Icon" type="image/x-icon" href="images/favicon.ico" />

    
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    

    
        <script type="text/javascript" src="scripts/html5.js"></script>
        <script type="text/javascript" src="scripts/jquery.js"></script>
        <script type="text/javascript" src="scripts/bluebar.js"></script>
        <script type="text/javascript" src="scripts/bluebar_desktop.js"></script>
    

    
    
    <script type="text/javascript" src="scripts/navigation.js"></script>


    
    <link rel="stylesheet" type="text/css" href="css/developer-main.css"/>


</head>
<body class="sidebar-page">

<div class="root">


    <div class="page">
        
        <header class="top-bar">
            <div class="wrapper">
                <hgroup>
                    <h1 class="logo"><a href="../index.html">DMX-Web</a></h1>
                    
    <h2>Developer Documentation</h2>

                </hgroup>

                
<!-- 
                <div class="account-state">
                    <ul class="actions">
                        
<li class="manage-clients link-settings ">
	<a href="clients/manage/index.html">Manage Clients<i></i></a>
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
    <li class="active">
        <a href="index.jsp">
            
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
    <li >
        <a href="endpoints/index.jsp">
            Local REST API Endpoints
            <i class="disclosure"></i>
        </a>
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
                

    <div class="introduction-wrapper">
        <div class="introduction">
            <h1>Developing for DMX-Web.</h1>

			<p>There are two main ways of developing using DMX-Web</p>

            <p>You can trigger DMX-Web shows, fixture changes and set
            individual DMX values using the <b>REST API</b>. These 
            are called from outside the dmx-web application.
            </p>

            <p>You can create new Show and Fixture definitions using the 
            <b>Java API</b>. These run within the dmx-web application.</p>

            <div class="cta">
                <a class="button" href="clients/manage/index.html">REST API</a>
                <span class="after-register">or
                <a class="button" href="clients/manage/index.html">Java API</a>
                </span>
            </div>
        </div>
    </div>

<!-- 
    <section id="getting_started" class="three-step">
        <h2>Getting Started</h2>

        <ol>
             <li class="step-1">
                 <h3><a href="clients/manage/index.html">Register</a></h3>
                 <p>We'll assign an OAuth client_id and client_secret for each of your applications.</p>
             </li>
             <li class="step-2">
                 <h3><a href="authentication/index.html">Authenticate</a></h3>
                 <p>Have our user <a href="authentication/index.html">authenticate and authorize your application</a> with Instagram.</p>
             </li>
             <li class="step-3">
                 <h3><a href="endpoints/index.html">Start making requests!</a></h3>
                 <p>Make requests to our <a href="endpoints/index.html">API Endpoints</a> with your authenticated OAuth credentials.</p>
              </li>
          </ol>
    </section>
 -->
 
    <section class="terms">
        <h2>WARNING</h2>

        <p>Before you start using the API, we have a few guidelines that we'd like to tell you about. Please make sure to read the full <a href="../about/legal/terms/api/index.html">API Terms of Use.</a> Here's what you'll read about:</p>

        <ol>
            <li>Most things don't work.</li>
            <li>The things that do work will probably be renamed and will therefore stop working.</li>
            <li>It's not very thread-safe.</li>
            <li>It's not particularly good with memory reclamation.</li>
        </ol>
    </section>


            </section>
        </div> <!-- .main -->
    </div> <!-- .main -->


    </div> <!-- .page -->

    
    <footer class="page-footer">
        <div class="wrapper">
            <nav>
                <ul>
                    <li><a href="http://instagram.com/accounts/edit/">Your Account</a></li>
                    <li><a href="../about/us/index.html">About us</a></li>
                    <li><a href="http://help.instagram.com/">Support</a></li>
                    <li><a href="http://blog.instagram.com/">Blog</a></li>
                    <li><a href="index.html">API</a></li>
                    <li><a href="../about/jobs/index.html">Jobs</a></li>
                    <li><a href="../about/legal/privacy/index.html">Privacy</a></li>
                    <li><a href="../about/legal/terms/index.html">Terms</a></li>
                </ul>
            </nav>

            <p class="copyright">&copy; 2012 dmx-web, Inc.</p>
        </div>
    </footer>
    


</div> <!-- .root -->






<!-- 
<script type="text/javascript">
    var _gaq = _gaq || [];
    _gaq.push(['_setAccount', 'UA-18105282-1']);
    _gaq.push(['_setDomainName', 'none']);
    _gaq.push(['_setAllowLinker', true]);
    _gaq.push(['_trackPageview']);
    (function() {
        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
    })();
</script>
 -->
 
</body>
</html>