<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" class="hl-en not-logged-in ">
<head>
    <meta charset="UTF-8" />
    <title>Fixture Endpoints &bull; DMX-web Developer Documentation</title>

    <link rel="Shortcut Icon" type="image/x-icon" href="../../images/favicon.ico" />

    
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    

    
        <script type="text/javascript" src="../../scripts/html5.js"></script>
        <script type="text/javascript" src="../../scripts/jquery-3.6.3.js"></script>
        <script type="text/javascript" src="../../scripts/bluebar.js"></script>
        <script type="text/javascript" src="../../scripts/bluebar_desktop.js"></script>
    

    
    
    <script type="text/javascript" src="../../scripts/navigation.js"></script>


    
    <link rel="stylesheet" type="text/css" href="../../css/developer-main.css"/>


</head>
<body class="sidebar-page">

<div class="root">


    <div class="page">
        
        <header class="top-bar">
            <div class="wrapper">
                <hgroup>
                    <h1 class="logo"><a href="../../../index.jsp">DMX-Web</a></h1>
                    
    <h2>Developer Documentation</h2>

                </hgroup>

                
<!-- 
                <div class="account-state">
                    <ul class="actions">
                        
<li class="manage-clients link-settings ">
	<a href="../../clients/manage/index.jsp">Manage Clients<i></i></a>
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
    <a href="index.jsp#" class="disclosure-down"></a>
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
        <a href="authentication/index.jsp">
            
            Authentication
            
            <i class="disclosure"></i>
            
        </a>
        
    </li>
    <li >
        <a href="realtime/index.jsp">
            
            Real-time
            
            <i class="disclosure"></i>
            
        </a>
        
    </li>
     -->
    <li class="active">
        <a href="../index.jsp">
            Local REST API Endpoints
            <i class="disclosure"></i>
        </a>
            <ul>
                <li >
        <a href="../shows/index.jsp">Shows</a>
    </li>
                <li class="active">
        <a href="../fixtures/index.jsp" class="active">Fixtures</a>
    </li>
                <li >
        <a href="../dmx/index.jsp" >DMX Values</a>
    </li>
            </ul>
        
    </li>
    <li >
        <a href="../embedding/index.jsp">
            Java API
            <i class="disclosure"></i>
        </a>
    </li>
    <li >
        <a href="../libraries/index.jsp">
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
                
    <header>
        <h1>Fixture Endpoints</h1>

        
    </header>

    

    <div class="index-nav index-endpoints">
        <ul>
            <li>
                <a href="index.jsp#fixture_list">
                    <span class="type">GET</span>
                    <b><code>/fixture/list</code></b>
                    <span class="description">Retrieves the list of fixtures in the active stage.</span>
                    <i></i>
                </a>
            </li>
            
            <li>
                <a href="index.jsp#fixture_setColor">
                    <span class="type">POST</span>
                    <b><code>/fixture/setColor?fixtureId=</code><span class="token">fixture-id</span><code>&color=</code><span class="token">color-value</span></b>
                    <span class="description">Sets the color of a fixture.</span>
                    <i></i>
                </a>
            </li>

            <li>
                <a href="index.jsp#fixture_setDimmer">
                    <span class="type">POST</span>
                    <b><code>/fixture/setDimmer?fixtureId=</code><span class="token">fixture-id</span><code>&dimmer=</code><span class="token">dimmer-value</span></b>
                    <span class="description">Sets the dimmer value of a fixture.</span>
                    <i></i>
                </a>
            </li>
            
            <li>
                <a href="index.jsp#fixture_setStrobe">
                    <span class="type">POST</span>
                    <b><code>/fixture/setStrobe?fixtureId=</code><span class="token">fixture-id</span><code>&strobe=</code><span class="token">strobe-value</span></b>
                    <span class="description">Sets the strobe value of a fixture.</span>
                    <i></i>
                </a>
            </li>

            <li>
                <a href="index.jsp#show_stop_byname">
                    <span class="type">POST</span>
                    <b><code>/fixture/setPan?fixtureId=</code><span class="token">fixture-id</span><code>&pan=</code><span class="token">pan-value</span></b>
                    <span class="description">Sets the pan value of a fixture.</span>
                    <i></i>
                </a>
            </li>
            
            <li>
                <a href="index.jsp#show_stop_byname">
                    <span class="type">POST</span>
                    <b><code>/fixture/setTilt?fixtureId=</code><span class="token">fixture-id</span><code>&pan=</code><span class="token">tilt-value</span></b>
                    <span class="description">Sets the tilt value of a fixture.</span>
                    <i></i>
                </a>
            </li>
            
<!--             
            <li>
                <a href="index.jsp#get_media_comments">

                    <span class="type">GET</span>
                    <b><code>/media/</code><span class="token">media-id</span><code>/comments</code></b>
                    <span class="description">Get a full list of comments on a media.</span>
                    <i></i>

                </a>
            </li>
            
            
            <li>
                <a href="index.jsp#post_media_comments">

                    <span class="type">POST</span>
                    <b><code>/media/</code><span class="token">media-id</span><code>/comments</code></b>
                    <span class="description">Create a comment on a media.</span>
                    <i></i>

                </a>
            </li>
            
            <li>
                <a href="index.jsp#delete_media_comments">

                    <span class="type">DEL</span>
                    <b><code>/media/</code><span class="token">media-id</span><code>/comments/</code><span class="token">comment-id</span></b>
                    <span class="description">Remove a comment.</span>
                    <i></i>

                </a>
            </li>
 -->
             
        </ul>
    </div>

    
        

    <section class="card endpoint" id="show_start_byid">
        <header>
            <h2>
                <span class="ep-type">GET</span>
                <code>/show/start?showId=</code><span class="token">show-id</span>
            </h2>
        </header>

        
        
        

        <div class="card-info">
            <ul class="actions">
                
                <li><a class="endpoint button" onclick="$('#show_start_byid_response').slideToggle()"><b>Response</b></a></li>
                
            </ul>
            <code>https://yourhost:8080/dmx-web/api/show/start?showId=SHOW-ID</code>
        </div>

        
        <div class="card-info-more ep-response" id="show_start_byid_response">
            <div class="codehilite"><pre><code> { some javascript here }
</code></pre></div>

        </div>

    
        <div class="card-description ep-description">
            <p>
                Starts the show with the supplied unique show-id identifier.
                
                <p>The show ID can be determined by going into the 
                Configuration panel from the main DMX-web page, then
                selecting "Shows", and reading the number in the 'Show ID' column,
                or by using the <a href="#show_list"><code>/show/list</code></a> API request.  
                
                <p>All other shows within the same show group will be stopped
                (except for currently running shows in show group 0).</p>
                
                <p>Shows that are not configured on an active stage will not be started. 
              <!--   
                <small><a href="../../authentication/index.jsp#scope">Required scope</a>: comments</small>
                 -->
            </p>

            
        </div>
    </section>

    

<!-- 
    <section class="card endpoint" id="get_media_comments">
        <header>
            <h2>
                <span class="ep-type">GET</span>
                <code>/media/</code><span class="token">media-id</span><code>/comments</code>
            </h2>
        </header>

        
        
        

        <div class="card-info">
            <ul class="actions">
                
                <li><a class="endpoint button" onclick="$('#get_media_comments_response').slideToggle()"><b>Response</b></a></li>
                
            </ul>
            <code>https://api.instagram.com/v1/media/555/comments?access_token=ACCESS-TOKEN</code>
        </div>

        
        <div class="card-info-more ep-response" id="get_media_comments_response">
            <div class="codehilite"><pre><code><span class="p">{</span>
    <span class="nt">&quot;meta&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;code&quot;</span><span class="p">:</span> <span class="mi">200</span>
    <span class="p">},</span>
    <span class="nt">&quot;data&quot;</span><span class="p">:</span> <span class="p">[</span>
        <span class="p">{</span>
            <span class="nt">&quot;created_time&quot;</span><span class="p">:</span> <span class="s2">&quot;1280780324&quot;</span><span class="p">,</span>
            <span class="nt">&quot;text&quot;</span><span class="p">:</span> <span class="s2">&quot;Really amazing photo!&quot;</span><span class="p">,</span>
            <span class="nt">&quot;from&quot;</span><span class="p">:</span> <span class="p">{</span>
                <span class="nt">&quot;username&quot;</span><span class="p">:</span> <span class="s2">&quot;snoopdogg&quot;</span><span class="p">,</span>
                <span class="nt">&quot;profile_picture&quot;</span><span class="p">:</span> <span class="s2">&quot;http://images.instagram.com/profiles/profile_16_75sq_1305612434.jpg&quot;</span><span class="p">,</span>
                <span class="nt">&quot;id&quot;</span><span class="p">:</span> <span class="s2">&quot;1574083&quot;</span><span class="p">,</span>
                <span class="nt">&quot;full_name&quot;</span><span class="p">:</span> <span class="s2">&quot;Snoop Dogg&quot;</span>
            <span class="p">},</span>
            <span class="nt">&quot;id&quot;</span><span class="p">:</span> <span class="s2">&quot;420&quot;</span>
        <span class="p">},</span>
        <span class="err">...</span>
    <span class="p">]</span>
<span class="p">}</span>
</code></pre></div>

        </div>
        

        <div class="card-description ep-description">
            <p>
                Get a full list of comments on a media.
                
                <small><a href="../../authentication/index.jsp#scope">Required scope</a>: comments</small>
                
            </p>

            
        </div>
    </section>

    
        
    

    <section class="card endpoint" id="post_media_comments">
        <header>
            <h2>
                <span class="ep-type">POST</span>
                <code>/media/</code><span class="token">media-id</span><code>/comments</code>
            </h2>
        </header>

        
        
        

        <div class="card-info">
            <ul class="actions">
                
                <li><a class="endpoint button" onclick="$('#post_media_comments_response').slideToggle()"><b>Response</b></a></li>
                
            </ul>
            <code>curl -F &#39;access_token=ACCESS-TOKEN&#39; \
    -F &#39;text=This+is+my+comment&#39; \
    https://api.instagram.com/v1/media/{media-id}/comments</code>
        </div>

        
        <div class="card-info-more ep-response" id="post_media_comments_response">
            <div class="codehilite"><pre><code><span class="p">{</span>
    <span class="nt">&quot;meta&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;code&quot;</span><span class="p">:</span> <span class="mi">200</span>
    <span class="p">},</span> 
    <span class="nt">&quot;data&quot;</span><span class="p">:</span> <span class="kc">null</span>
<span class="p">}</span>
</code></pre></div>

        </div>
        

        <div class="card-description ep-description">
            <p>
                Create a comment on a media.
                
                <small><a href="../../authentication/index.jsp#scope">Required scope</a>: comments</small>
                
            </p>

            
            <table>
                <caption><strong>Parameters</strong><i></i></caption>
                <tbody>
                    
                    <tr>
                        <th>access_token</th>
                        <td>A valid access token.</td>
                    </tr>
                    
                    <tr>
                        <th>text</th>
                        <td>Text to post as a comment on the media as specified in media-id.</td>
                    </tr>
                    
                </tbody>
            </table>
            
        </div>
    </section>

    
        
    

    <section class="card endpoint" id="delete_media_comments">
        <header>
            <h2>
                <span class="ep-type">DEL</span>
                <code>/media/</code><span class="token">media-id</span><code>/comments/</code><span class="token">comment-id</span>
            </h2>
        </header>

        
        
        

        <div class="card-info">
            <ul class="actions">
                
                <li><a class="endpoint button" onclick="$('#delete_media_comments_response').slideToggle()"><b>Response</b></a></li>
                
            </ul>
            <code>curl -X DELETE https://api.instagram.com/v1/media/{media-id}/comments?access_token=ACCESS-TOKEN</code>
        </div>

        
        <div class="card-info-more ep-response" id="delete_media_comments_response">
            <div class="codehilite"><pre><code><span class="p">{</span>
    <span class="nt">&quot;meta&quot;</span><span class="p">:</span> <span class="p">{</span>
        <span class="nt">&quot;code&quot;</span><span class="p">:</span> <span class="mi">200</span>
    <span class="p">},</span> 
    <span class="nt">&quot;data&quot;</span><span class="p">:</span> <span class="kc">null</span>
<span class="p">}</span>
</code></pre></div>

        </div>
        

        <div class="card-description ep-description">
            <p>
                Remove a comment either on the authenticated user's media or authored by the authenticated user.
                
                <small><a href="../../authentication/index.jsp#scope">Required scope</a>: comments</small>
                
            </p>

            
            <table>
                <caption><strong>Parameters</strong><i></i></caption>
                <tbody>
                    
                    <tr>
                        <th>access_token</th>
                        <td>A valid access token.</td>
                    </tr>
                    
                </tbody>
            </table>
            
        </div>
    </section>

    
 -->


    

            </section>
        </div> <!-- .main -->
    </div> <!-- .main -->


    </div> <!-- .page -->

    
    <footer class="page-footer">
        <div class="wrapper">
            <nav>
                <ul>
                    <li><a href="http://instagram.com/accounts/edit/">Your Account</a></li>
                    <li><a href="../../../about/us/index.jsp">About us</a></li>
                    <li><a href="http://help.instagram.com/">Support</a></li>
                    <li><a href="http://blog.instagram.com/">Blog</a></li>
                    <li><a href="../../index.jsp">API</a></li>
                    <li><a href="../../../about/jobs/index.jsp">Jobs</a></li>
                    <li><a href="../../../about/legal/privacy/index.jsp">Privacy</a></li>
                    <li><a href="../../../about/legal/terms/index.jsp">Terms</a></li>
                </ul>
            </nav>

            <p class="copyright">&copy; 2012 Instagram, Inc.</p>
        </div>
    </footer>
    


</div> <!-- .root -->


</body>
</html>