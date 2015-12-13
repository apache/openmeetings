<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at
  
      http://www.apache.org/licenses/LICENSE-2.0
    	  
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  
-->
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
	<link rel="shortcut icon" href="favicon.ico"> 
	<script type="text/javascript">
		function reMatch(str, reg){
			var s = unescape(str);
			var re = new RegExp(eval(unescape(reg)));
			return s.replace(re,unescape(rep));
		}
	</script>
</head>
<body align="center" valign="middle" align="center" onLoad="focusSWF()">


	<div id="flashcontent">
		<strong>You need to upgrade your Flash Player</strong>
		This is replaced by the Flash content. 
		Place your alternate content here and users without the Flash plugin or with 
		Javascript turned off will see this. Content here allows you to leave out <code>noscript</code> 
		tags.
	</div>
	
	<script type="text/javascript">
		// <![CDATA[
		
		var so = new SWFObject("test-regExp2.lzx?lzt=swf&lzr=swf8", "lzapp", "100%", "100%", "8", "#ffffff");
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