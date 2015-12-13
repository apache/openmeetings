/*
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
  
*/
/*
 * Functions to be included in the HTML wrapper,
 * see the templates dir (*.vm) for the include statements
 *  
 */ 

function getBrowserInfo() {
	//alert(navigator.userAgent);
	document.getElementById("lzapp").getBrowserInfoCallback(navigator.userAgent);
}

function getBrowserLang() {
	//alert(navigator.userAgent);
	document.getElementById("lzapp").getBrowserLangCallback(navigator.language);
}

function redirectToUrl(url) {
	//alert(navigator.userAgent);
	window.location = url;
	
	document.getElementById("lzapp").redirectToUrlCallback("ok");
}

function getTimeZoneOffset(){
	var rightNow = new Date();
            var jan1 = new Date(rightNow.getFullYear(), 0, 1, 0, 0, 0, 0);
            var temp = jan1.toGMTString();
            var jan2 = new Date(temp.substring(0, temp.lastIndexOf(" ")-1));
            var std_time_offset = (jan1 - jan2) / (1000 * 60 * 60);
            
    document.getElementById("lzapp").getTimeZoneOffsetCallback(std_time_offset);
}