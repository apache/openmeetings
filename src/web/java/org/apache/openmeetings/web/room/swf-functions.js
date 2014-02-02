/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
function getAppletStatus() {
	return false;
}

function initSwf(swfurl) {
	var general = {
			url : swfurl,
			bgcolor : '#ffffff',
			width : '100%',
			height : '100%',
			id : 'lzapp',
			__lzminimumversion : 8
		};
	var options = $.extend({}, general, {allowfullscreen : 'true'});
	$('#header, #topControls, #chatPanel').hide();
	$('div[id="contents"], div[id="contents"] > div').css('height', '100%');
	var embed = $('<embed>').attr('quality', 'high').attr('bgcolor', options.bgcolor)
		.attr('src', "public/" + options.url)
		.attr('wmode', 'window').attr('allowfullscreen', true)
		.attr('width', options.width).attr('height', options.height)
		.attr('id', 'lzapp').attr('name', 'lzapp')
		.attr('flashvars', escape($.param(general)))
		.attr('swliveconnect', true).attr('align', 'middle')
		.attr('allowscriptaccess', 'sameDomain').attr('type', 'application/x-shockwave-flash')
		.attr('pluginspage', 'http://www.macromedia.com/go/getflashplayer');
	$('#swfloading').after($('<div id="lzappContainer">').append(embed)).width('1px').height('1px');
}

function roomExit() {
	$('#header, #topControls, #chatPanel').show();
	$('div[id="contents"], div[id="contents"] > div').css('height', 'auto');
	window.location.hash = "#rooms/public";
	$('#lzappContainer').remove();
}