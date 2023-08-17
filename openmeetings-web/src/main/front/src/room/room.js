/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const Settings = require('../main/settings');
const VideoUtil = require('../settings/video-util');
const VideoSettings = require('../settings/settings');
const InterviewWbArea = require('../wb/interview-area');
const DrawWbArea = require('../wb/wb-area')

const NoSleep = require('nosleep.js');
const VideoManager = require('./video-manager');
const Sharer = require('./sharer');
const Activities = require('./activities');
const SipDialer = require('./sip-dialer');
const UserListUtil = require('./user-list-util');
const UserList = require('./user-list');
const QuickPoll = require('./quick-poll');

const sbSide = Settings.isRtl ? 'right' : 'left';
let options, menuHeight, sb, dock, noSleep;

function _init(_options) {
	options = _options;
	window.WbArea = options.interview ? new InterviewWbArea() : new DrawWbArea();
	const menu = $('.room-block .room-container .menu');
	sb = $('.room-block .sidebar');
	sb.width(sb.width()); // this is required to 'fix' the width so it will not depend on CSS var
	dock = sb.find('.btn-dock').click(function() {
		const offset = parseInt(sb.css(sbSide));
		if (offset < 0) {
			sb.removeClass('closed');
		}
		dock.prop('disabled', true);
		const props = {};
		props[sbSide] = offset < 0 ? '0px' : (-sb.width() + 45) + 'px';
		sb.animate(props, 1500
			, function() {
				dock.prop('disabled', false);
				__dockSetMode(offset < 0);
				_setSize();
			});
	});
	__dockSetMode(true);
	menuHeight = menu.length === 0 ? 0 : menu.height();
	VideoManager.init();
	Activities.init();
	Sharer.init();
	UserList.init(options);
	_setSize();
	$(window).on('resize.omwb', window.WbArea.resize);
}
function __dockSetMode(mode) {
	const icon = dock.find('i').removeClass('icon-dock icon-undock');
	if (mode) {
		icon.addClass('icon-undock');
		dock.attr('title', dock.data('ttl-undock'))
			.find('.sr-only').text(dock.data('ttl-undock'));
		_sbAddResizable();
	} else {
		icon.addClass('icon-dock');
		dock.attr('title', dock.data('ttl-dock'))
			.find('.sr-only').text(dock.data('ttl-dock'));
		sb.addClass('closed').resizable('destroy');
	}
}
function _getSelfAudioClient() {
	const vw = $('.video-container[data-client-type=WEBCAM][data-client-uid=' + Room.getOptions().uid + ']');
	if (vw.length > 0) {
		const v = vw.first().data();
		if (VideoUtil.hasMic(v.stream())) {
			return v;
		}
	}
	return null;
}
function _preventKeydown(e) {
	const base = $(e.target);
	if (e.target.isContentEditable === true || base.is('textarea, input:not([readonly]):not([type=radio]):not([type=checkbox])')) {
		return;
	}
	if (e.which === 8) { // backspace
		e.preventDefault();
		e.stopImmediatePropagation();
		return false;
	}
}
function __keyPressed(hotkey, e) {
	const code = e.code;
	return hotkey.alt === e.altKey
		&& hotkey.ctrl === e.ctrlKey
		&& hotkey.shift === e.shiftKey
		&& hotkey.code.toUpperCase() === (code ? code.toUpperCase() : '');
}
function _keyHandler(e) {
	if (__keyPressed(options.keycode.arrange, e)) {
		VideoUtil.arrange();
	} else if (__keyPressed(options.keycode.arrangeresize, e)) {
		VideoUtil.arrangeResize(VideoSettings.load());
	} else if (__keyPressed(options.keycode.muteothers, e)) {
		const v = _getSelfAudioClient();
		if (v !== null) {
			VideoManager.clickMuteOthers(Room.getOptions().uid);
		}
	} else if (__keyPressed(options.keycode.mute, e)) {
		const v = _getSelfAudioClient();
		if (v !== null) {
			v.mute(!v.isMuted());
		}
	} else if (__keyPressed(options.keycode.quickpoll, e)) {
		quickPollAction('open');
	}
	if (e.which === 27) {
		$('#wb-rename-menu').hide();
	}
}
function _mouseHandler(e) {
	if (e.which === 1) {
		$('#wb-rename-menu').hide();
	}
}
function _sbWidth() {
	if (sb === undefined) {
		sb = $('.room-block .sidebar');
	}
	return sb === undefined ? 0 : sb.width() + parseInt(sb.css(sbSide));
}
function _setSize() {
	const sbW = _sbWidth()
		, holder = $('.room-block');
	($('.main.room')[0]).style.setProperty('--sidebar-width', sbW + 'px');
	if (sbW > 236) {
		holder.addClass('big').removeClass('narrow');
	} else {
		holder.removeClass('big').addClass('narrow');
	}
}
function _reload() {
	if (!!options && !!options.reloadUrl) {
		window.location.href = options.reloadUrl;
	} else {
		window.location.reload();
	}
}
function _close() {
	_unload();
	$(".room-block").remove();
	$("#chatPanel").remove();
	$('#disconnected-dlg')
		.modal('show')
		.off('hide.bs.modal')
		.on('hide.bs.modal', _reload);
}
function _sbAddResizable() {
	sb.resizable({
		handles: Settings.isRtl ? 'w' : 'e'
		, stop: function() {
			_setSize();
		}
	});
}
function _load() {
	$('body').addClass('no-header');
	Wicket.Event.subscribe("/websocket/closed", _close);
	Wicket.Event.subscribe("/websocket/error", _close);
	$(window).on('keydown.openmeetings', _preventKeydown);
	$(window).on('keyup.openmeetings', _keyHandler);
	$(window).on('keydown.om-sip', SipDialer.keyDown);
	$(window).on('keyup.om-sip', SipDialer.keyUp);
	$(document).click(_mouseHandler);
	document.addEventListener('click', _addNoSleep, false);
	SipDialer.init();
}
function _addNoSleep() {
	_removeNoSleep();
	noSleep = new NoSleep();
	noSleep.enable();
}
function _removeNoSleep() {
	document.removeEventListener('click', _addNoSleep, false);
	if (noSleep) {
		noSleep.disable();
		noSleep = null;
	}
}
function _unload() {
	$('body').removeClass('no-header');
	Wicket.Event.unsubscribe("/websocket/closed", _close);
	Wicket.Event.unsubscribe("/websocket/error", _close);
	if (typeof(WbArea) === 'object') {
		WbArea.destroy();
		window.WbArea = undefined;
	}
	if (typeof(VideoSettings) === 'object') {
		VideoSettings.close();
	}
	if (typeof(VideoManager) === 'object') {
		VideoManager.destroy();
	}
	$('.ui-dialog.user-video').remove();
	$(window).off('keyup.openmeetings');
	$(window).off('keydown.openmeetings');
	$(window).off('resize.omwb');
	$(window).off('keydown.om-sip');
	$(window).off('keyup.om-sip');
	$(document).off('click', _mouseHandler);
	sb = undefined;
	Sharer.close();
	_removeNoSleep();
}
function _showClipboard(txt) {
	const dlg = $('#clipboard-dialog');
	dlg.find('p .text').text(txt);
	dlg.dialog({
		resizable: false
		, height: "auto"
		, width: 400
		, modal: true
		, buttons: [
			{
				text: dlg.data('btn-ok')
				, click: function() {
					$(this).dialog('close');
				}
			}
		]
	});
}

module.exports = {
	init: _init
	, getMenuHeight: function() {
		return menuHeight;
	}
	, getOptions: function() {
		return typeof(options) === 'object' ? JSON.parse(JSON.stringify(options)) : {};
	}
	, load: _load
	, unload: _unload
	, showClipboard: _showClipboard
	, quickPoll: QuickPoll.update
	, hasRight: UserListUtil.hasRight
	, setCssVar: function(key, val) {
		($('.main.room')[0]).style.setProperty(key, val);
	}
	, addClient: UserList.addClient
	, updateClient: UserList.updateClient
	, removeClient: UserList.removeClient
	, removeOthers: UserList.removeOthers
	, getClient: UserList.getClient
};
