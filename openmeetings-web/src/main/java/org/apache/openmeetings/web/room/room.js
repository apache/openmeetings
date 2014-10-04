/**
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
function initVideo(_options) {
	var options = $.extend({bgcolor: "#ffffff", width: 570, height: 900
		, resolutions: JSON.stringify([{label: "4:3 (~6 KByte/sec)", width: 40, height: 30}
			, {label: "4:3 (~12 KByte/sec)", width: 80, height: 60}
			, {label: "4:3 (~20 KByte/sec)", width: 120, height: 90, default: true}
			, {label: "QQVGA 4:3 (~36 KByte/sec)", width: 160, height: 120}
			, {label: "4:3 (~40 KByte/sec)", width: 240, height: 180}
			, {label: "HVGA 4:3 (~56 KByte/sec)", width: 320, height: 240}
			, {label: "4:3  (~60 KByte/sec)", width: 480, height: 360}
			, {label: "4:3 (~68 KByte/sec)", width: 640, height: 480}
			, {label: "XGA 4:3", width: 1024, height: 768}
			, {label: "16:9", width: 256, height: 150}
			, {label: "WQVGA 9:5", width: 432, height: 240}
			, {label: "pseudo 16:9", width: 480, height: 234}
			, {label: "16:9", width: 512, height: 300}
			, {label: "nHD 16:9", width: 640, height: 360}
			, {label: "16:9", width: 1024, height: 600}])
		}, _options);
	var type = 'application/x-shockwave-flash';
	var src = 'public/main.swf?cache' + new Date().getTime();
	var r = $('<div class="room video">');
	var o = $('<object>').attr('type', type).attr('data', src).attr('width', options.width).attr('height', options.height);
	o.append($('<param>').attr('name', 'quality').attr('value', 'best'));
	o.append($('<param>').attr('name', 'wmode').attr('value', 'transparent'));
	o.append($('<param>').attr('name', 'allowscriptaccess').attr('value', 'sameDomain'));
	o.append($('<param>').attr('name', 'allowfullscreen').attr('value', 'false'));
	o.append($('<param>').attr('name', 'flashvars').attr('value', $.param(options)));
	$('#roomMenu').parent().append(r.append(o));
	/*
			.attr('wmode', 'window').attr('allowfullscreen', true)
			.attr('width', options.width).attr('height', options.height)
			.attr('id', 'lzapp').attr('name', 'lzapp')
			.attr('flashvars', escape($.param(options)))
			.attr('swliveconnect', true).attr('align', 'middle')
			.attr('allowscriptaccess', 'sameDomain').attr('type', 'application/x-shockwave-flash')
			.attr('pluginspage', 'http://www.macromedia.com/go/getflashplayer')
	*/
	r.dialog({width: options.width, height: options.height, dialogClass: "video"});
}

function setHeight() {
	var h = $(window).height() - $('#roomMenu').height();
	$(".room.sidebar.left").height(h);
	var p = $(".room.sidebar.left .tabs");
	p.height(h - 5); //FIXME hacks
	$(".user.list", p).height(h - $("ul", p).height() - 15); //FIXME hacks
	$(".room.wb.area").height(h);
	$(".room.wb.area .wb").height(h);
}

$(document).ready(function() {
	$(window).on('resize.openmeetings', function() {
		roomWidth = $(window).width();
		setHeight();
	});
});

var roomWidth = $(window).width();
function roomLoad() {
	$(".room.sidebar.left").resizable({
		handles: "e"
		, stop: function(event, ui) {
			//TODO not really works, need to be investigated
			var w = roomWidth - $(this).width() - 5;
			$(".room.wb.area").width(w);
			$(".room.wb.area .wb").width(w);
		}
	});
}
function startPrivateChat(el) {
	addChatTab('chatTab-u' + el.parent().parent().data("userid"), el.parent().parent().find('.user.name').text());
	openChat();
	$('#chatMessage .wysiwyg-editor').click();
}
