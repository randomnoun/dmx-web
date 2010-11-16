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
    <title>Video stream</title>

</head><body>

    <div id="page">
        
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
    
    </div>
    
    
</body></html>