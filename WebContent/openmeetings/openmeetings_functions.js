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

function loadingComplete() {
	document.getElementById("loading").style.display = 'none';
	var lzApp = document.getElementById("lzappContainer");
	lzApp.style.width = '100%';
	lzApp.style.height = '100%';
}

function getTimeZoneOffset() {
	var rightNow = new Date(), std_time_offset = -rightNow.getTimezoneOffset() / 60;
	for (var i = 0; i < 12; ++i) {
		var d = new Date(rightNow.getFullYear(), i, 1, 0, 0, 0, 0), offset = -d.getTimezoneOffset() / 60;
		if (offset < std_time_offset) {
			std_time_offset = offset;
			break;
		}
	}
    document.getElementById("lzapp").getTimeZoneOffsetCallback(std_time_offset);
}
