/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var labels, config;
function initSwf(_options) {
	var options = $.extend({
		allowfullscreen : 'true',
		bgcolor : '#ffffff',
		width : '100%',
		height : '100%',
		id : 'lzapp',
		__lzminimumversion : 8
	}, _options);
	$('div[id="contents"], div[id="contents"] > div').css('height', '100%');
	var embed = $('<embed>')
		.attr('quality', 'high')
		.attr('bgcolor', options.bgcolor)
		.attr('src', "public/" + options.src)
		.attr('wmode', options.wmode)
		.attr('allowfullscreen', true)
		.attr('width', options.width).attr('height', options.height)
		.attr('id', 'lzapp')
		.attr('name', 'lzapp')
		//.attr('flashvars', $.param(options))
		.attr('swliveconnect', true)
		.attr('align', 'middle')
		.attr('allowscriptaccess', 'sameDomain')
		.attr('type', 'application/x-shockwave-flash')
		.attr('pluginspage', 'http://www.macromedia.com/go/getflashplayer');
	$('#swfloading').after($('<div id="lzappContainer">').append(embed)).width('1px').height('1px');
}
function loadingComplete() {
	document.getElementById("swfloading").style.display = 'none';
	var lzApp = document.getElementById("lzappContainer");
	lzApp.style.width = '100%';
	lzApp.style.height = '100%';
}
function getStringLabels() {
	return labels;
}
function getConfig() {
	return config;
}
