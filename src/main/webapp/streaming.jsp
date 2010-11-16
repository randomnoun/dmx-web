<html><head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<!-- A minimal Flowplayer setup to get you started -->

    <!-- 
        include flowplayer JavaScript file that does  
        Flash embedding and provides the Flowplayer API.
    -->
    <script type="text/javascript" src="js/flowplayer-3.2.4.min.js"></script>
    
    <!-- some minimal styling, can be removed -->
    <link rel="stylesheet" type="text/css" href="style.css">
    
    <!-- page title -->
    <title>Minimal Flowplayer setup</title>

</head><body>

    <div id="page">
        
        <h1>Minimal Flowplayer setup</h1>
    
        <p>View commented source code to get familiar with Flowplayer installation.</p>
        
        <!-- this A tag is where your Flowplayer will be placed. it can be anywhere -->
        <!--  http://e1h13.simplecdn.net/flowplayer/flowplayer.flv -->
        <a  
             href="http://bnealb01.dev.randomnoun:8081/mediaplayer/stream.flv"  
             style="display:block;width:520px;height:330px"  
             id="player"> 
        </a> 
    
        <!-- this will install flowplayer inside previous A- tag. -->

        <script>
            flowplayer("player", "swf/flowplayer-3.2.5.swf");
        </script>
    
        
        
        <!-- 
            after this line is purely informational stuff. 
            does not affect on Flowplayer functionality 
        -->

        <p>     
            If you are running these examples <strong>locally</strong> and not on some webserver you must edit your 
            <a href="http://www.macromedia.com/support/documentation/en/flashplayer/help/settings_manager04.html">
                Flash security settings</a>. 
        </p>

        
        <p class="less">
            Select "Edit locations" &gt; "Add location" &gt; "Browse for files" and select
            flowplayer-x.x.x.swf you just downloaded.
        </p>
        
        
        <h2>Documentation</h2>
        
        <p>
            <a href="http://flowplayer.org/documentation/installation/index.html">Flowplayer installation</a>

        </p>

        <p>
            <a href="http://flowplayer.org/documentation/configuration/index.html">Flowplayer configuration</a>
        </p>

        <p>
            See this identical page on <a href="http://flowplayer.org/demos/example/index.htm">Flowplayer website</a> 
        </p>

        
    </div>
    
    
</body></html>