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
function setHeight() {
	var h = $(window).height() - $('#menu').height();
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
	setHeight();
}
function startPrivateChat(el) {
	addChatTab('chatTab-u' + el.parent().parent().data("userid"), el.parent().parent().find('.user.name').text());
	openChat();
	$('#chatMessage .wysiwyg-editor').click();
}
/***** functions required by SIP   ******/
function sipBtnClick() {
	var txt = $('.sip-number');
	txt.val(txt.val() + $(this).data('value'));
}
function sipBtnEraseClick() {
	var txt = $('.sip-number');
	var t = txt.val();
	if (!!t) {
		txt.val(t.substring(0, t.length -1));
	}
}
function sipGetKey(evt) {
	var k = -1;
	if (evt.keyCode > 47 && evt.keyCode < 58) {
		k = evt.keyCode - 48;
	}
	if (evt.keyCode > 95 && evt.keyCode < 106) {
		k = evt.keyCode - 96;
	}
	return k;
}
function sipKeyDown(evt) {
	var k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).addClass('ui-state-active');
	}
}
function sipKeyUp(evt) {
	var k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).removeClass('ui-state-active');
	}
}

/***** functions required by SWF   ******/
function audioActivity(uid, active) {
	var u = $('#user' + uid + ' .audio-activity.ui-icon');
	if (active) {
		u.addClass("speaking");
	} else {
		u.removeClass("speaking");
	}
}
function typingActivity(uid, active) {
	var u = $('#user' + uid + ' .typing-activity.ui-icon');
	if (active) {
		u.addClass("typing");
	} else {
		u.removeClass("typing");
	}
}
