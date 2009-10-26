<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>OpenMeetings</title>
	<script type="text/javascript" src="swfobject.js"></script>
	<style type="text/css">
		
		/* hide from ie on mac \*/
		html {
			height: 100%;
			overflow: hidden;
		}
		
		#flashcontent {
			height: 100%;
		}
		/* end hide */
	
		body {
			height: 100%;
			margin: 0;
			padding: 0;
			background-color: #ffffff;
		}
	
	</style>
</head>
<body align="center" valign="middle" align="center" onLoad="focusSWF()">


	<div id="flashcontent">
		<strong>You need to upgrade your Flash Player</strong>
		<p><a href="http://www.macromedia.com/go/getflashplayer/">Get the latest Flash Player</a></p>
	</div>
	
	<script type="text/javascript">
		// <![CDATA[
		
        var d = new Date();
        
        var so = new SWFObject("main.lzx.swf8.swf?r="+d.getTime(), "lzapp", "100%", "100%", "8", "#ffffff");
        so.addParam("quality", "high");
        so.addParam("id", "lzapp");
        so.addParam("allowScriptAccess", "always");
        so.addParam("scale", "noscale");
        so.write("flashcontent");
		
		function focusSWF(){
		
		    if(navigator.plugins&&navigator.mimeTypes&&navigator.mimeTypes.length){
		    }else {
		        document.getElementById('lzapp').focus();
		    }
		}   		
		
		// ]]>
	</script>	


</body>
</html>
