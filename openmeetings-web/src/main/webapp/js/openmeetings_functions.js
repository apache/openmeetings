/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
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
	document.getElementById("swfloading").style.display = 'none';
	var lzApp = document.getElementById("lzappContainer");
	lzApp.style.width = '100%';
	lzApp.style.height = '100%';
}
function getTimeZoneOffsetMinutes() {
	var rightNow = new Date(), std_time_offset = -rightNow.getTimezoneOffset();
	for (var i = 0; i < 12; ++i) {
		var d = new Date(rightNow.getFullYear(), i, 1, 0, 0, 0, 0), offset = -d.getTimezoneOffset();
		if (offset < std_time_offset) {
			std_time_offset = offset;
			break;
		}
	}
	return std_time_offset;
}
function getTimeZoneOffset() {
	document.getElementById("lzapp").getTimeZoneOffsetCallback(getTimeZoneOffsetMinutes()/60);
}
