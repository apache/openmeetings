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