/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Room = (function() {
	const self = {}, sbSide = Settings.isRtl ? 'right' : 'left';
	let options, menuHeight, sb, dock, activities, noSleep;

	function _init(_options) {
		options = _options;
		window.WbArea = options.interview ? InterviewWbArea() : DrawWbArea();
		const menu = $('.room-block .room-container .menu');
		activities = $('#activities');
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
		const header = $('#room-sidebar-tab-users .header');
		header.find('.om-icon.settings').off().click(VideoSettings.open);
		header.find('.om-icon.activity.cam').off().click(function() {
			VideoManager.toggleActivity('VIDEO');
		});
		header.find('.om-icon.activity.mic').off().click(function() {
			VideoManager.toggleActivity('AUDIO');
		});
		menuHeight = menu.length === 0 ? 0 : menu.height();
		VideoManager.init();
		if (typeof(Activities) !== 'undefined') {
			Activities.init();
		}
		Sharer.init();
		_setSize();
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
		const code = OmUtil.getKeyCode(e);
		return hotkey.alt === e.altKey
			&& hotkey.ctrl === e.ctrlKey
			&& hotkey.shift === e.shiftKey
			&& hotkey.code.toUpperCase() === (code ? code.toUpperCase() : '');
	}
	function _keyHandler(e) {
		if (__keyPressed(options.keycode.arrange, e)) {
			VideoUtil.arrange();
		} else if (__keyPressed(options.keycode.arrangeresize, e)) {
			VideoUtil.arrangeResize();
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
		if (sbW > 285) {
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
		$(document).click(_mouseHandler);
		_addNoSleep();
	}
	function _addNoSleep() {
		_removeNoSleep();
		noSleep = new NoSleep();
		noSleep.enable();
	}
	function _removeNoSleep() {
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
	function _hasRight(_inRights, _ref) {
		const ref = _ref || options.rights;
		let _rights;
		if (Array.isArray(_inRights)) {
			_rights = _inRights;
		} else {
			if ('SUPER_MODERATOR' === _inRights) {
				return ref.includes(_inRights);
			}
			_rights = [_inRights];
		}
		const rights = ['SUPER_MODERATOR', 'MODERATOR', ..._rights];
		for (let i = 0; i < rights.length; ++i) {
			if (ref.includes(rights[i])) {
				return true;
			}
		}
		return false;
	}
	function _setQuickPollRights() {
		const close = $('#quick-vote .close-btn');
		if (close.length === 1) {
			if (_hasRight(['PRESENTER'])) {
				close.show();
				if (typeof(close.data('bs.confirmation')) === 'object') {
					return;
				}
				close
					.confirmation({
						confirmationEvent: 'bla'
						, onConfirm: function() {
							quickPollAction('close');
						}
					});
			} else {
				close.hide();
			}
		}
	}
	function _quickPoll(obj) {
		if (obj.started) {
			let qv = $('#quick-vote');
			if (qv.length === 0) {
				const wbArea = $('.room-block .wb-block');
				qv = OmUtil.tmpl('#quick-vote-template', 'quick-vote');
				wbArea.append(qv);
			}
			const pro = qv.find('.control.pro')
				, con = qv.find('.control.con');
			if (obj.voted) {
				pro.removeClass('clickable').off();
				con.removeClass('clickable').off();
			} else {
				pro.addClass('clickable').off().click(function() {
					quickPollAction('vote', true);
				});
				con.addClass('clickable').off().click(function() {
					quickPollAction('vote', false);
				});
			}
			pro.find('.badge').text(obj.pros);
			con.find('.badge').text(obj.cons);
			_setQuickPollRights();
		} else {
			const qv = $('#quick-vote');
			if (qv.length === 1) {
				qv.remove();
			}
		}
		OmUtil.tmpl('#quick-vote-template', 'quick-vote');
	}
	function __activityAVIcon(elem, selector, predicate, onfunc, disabledfunc) {
		let icon = elem.find(selector);
		if (predicate()) {
			icon.show();
			const on = onfunc()
				, disabled = disabledfunc();;
			if (disabled) {
				icon.addClass('disabled');
			} else {
				icon.removeClass('disabled');
				if (on) {
					icon.addClass('enabled');
				} else {
					icon.removeClass('enabled');
				}
			}
			icon.attr('title', icon.data(on ? 'on' :'off'));
		} else {
			icon.hide();
		}
	}
	function __activityIcon(elem, selector, predicate, action) {
		let icon = elem.find(selector);
		if (predicate()) {
			if (icon.length === 0) {
				icon = OmUtil.tmpl('#user-actions-stub ' + selector);
				elem.append(icon);
			}
			icon.off().click(action);
		} else {
			icon.hide();
		}
	}
	function __rightIcon(c, elem, rights, selector, predicate) {
		const self = c.uid === options.uid
			, hasRight = _hasRight(rights, c.rights);
		let icon = elem.find(selector);
		if (predicate() && !_hasRight('SUPER_MODERATOR', c.rights) && (
			(self && options.questions && !hasRight)
			|| (!self && _hasRight('MODERATOR'))
		)) {
			if (icon.length === 0) {
				icon = OmUtil.tmpl('#user-actions-stub ' + selector);
				elem.append(icon);
			}
			if (hasRight) {
				icon.addClass('granted');
			} else {
				icon.removeClass('granted')
			}
			icon.attr('title', icon.data(self ? 'request' : (hasRight ? 'revoke' : 'grant')));
			icon.off().click(function() {
				OmUtil.roomAction({action: 'toggleRight', right: rights[0], uid: c.uid});
			});
		} else {
			icon.remove();
		}
	}
	function __rightAudioIcon(c, elem) {
		__rightIcon(c, elem, ['AUDIO'], '.right.audio', () => true);
	}
	function __rightVideoIcon(c, elem) {
		__rightIcon(c, elem, ['VIDEO'], '.right.camera', () => !options.audioOnly);
	}
	function __rightOtherIcons(c, elem) {
		__rightIcon(c, elem, ['PRESENTER'], '.right.presenter', () => !options.interview && $('.wb-area').is(':visible'));
		__rightIcon(c, elem, ['WHITEBOARD', 'PRESENTER'], '.right.wb', () => !options.interview && $('.wb-area').is(':visible'));
		__rightIcon(c, elem, ['SHARE'], '.right.screen-share', () => true); //FIXME TODO getRoomPanel().screenShareAllowed()
		__rightIcon(c, elem, ['REMOTE_CONTROL'], '.right.remote-control', () => true); //FIXME TODO getRoomPanel().screenShareAllowed()
		__rightIcon(c, elem, ['MODERATOR'], '.right.moderator', () => true);
	}
	function __setStatus(c, le) {
		const status = le.find('.user-status')
			, mode = c.level == 5 ? 'mod' : (c.level == 3 ? 'wb' : 'user');
		status.removeClass('mod wb user');
		status.attr('title', status.data(mode)).addClass(mode);
		le.data('level', c.level);
	}
	function __updateCount() {
		$('#room-sidebar-users-tab .user-count').text($('#room-sidebar-tab-users .user-list .users .user.entry').length);
	}
	function __sortUserList() {
		const container = $('#room-sidebar-tab-users .user-list .users');
		container.find('.user.entry').sort((_u1, _u2) => {
			const u1 = $(_u1)
				, u2 = $(_u2);
			return u2.data('level') - u1.data('level') || u1.attr('title').localeCompare(u2.attr('title'));
		}).appendTo(container);
	}
	function _addClient(_clients) {
		const clients = Array.isArray(_clients) ? _clients : [_clients];
		clients.forEach(c => {
			const self = c.uid === options.uid;
			let le = Room.getClient(c.uid);
			if (le.length === 0) {
				le = OmUtil.tmpl('#user-entry-stub', 'user' + c.uid);
				le.attr('id', 'user' + c.uid)
					.attr('data-userid', c.user.id)
					.attr('data-uid', c.uid);
				if (self) {
					le.addClass('current');
				}
				$('#room-sidebar-tab-users .user-list .users').append(le);
			}
			_updateClient(c);
		});
		__updateCount();
		__sortUserList();
	}
	function _updateClient(c) {
		const self = c.uid === options.uid
			, le = Room.getClient(c.uid)
			, hasAudio = VideoUtil.hasMic(c)
			, hasVideo = VideoUtil.hasCam(c)
			, speaks = le.find('.audio-activity');
		if (le.length === 0) {
			return;
		}
		__setStatus(c, le);
		if (hasVideo || hasAudio) {
			if (le.find('.restart').length === 0) {
				le.prepend(OmUtil.tmpl('#user-av-restart').click(function () {
					VideoManager.refresh(c.uid);
				}));
			}
		} else {
			le.find('.restart').remove();
		}
		speaks.hide().removeClass('clickable').attr('title', speaks.data('speaks')).off();
		if (hasAudio) {
			speaks.show();
			if(_hasRight('MUTE_OTHERS')) {
				speaks.addClass('clickable').click(function () {
					VideoManager.clickMuteOthers(c.uid);
				}).attr('title', speaks.attr('title') + speaks.data('mute'));
			}
		}
		le.attr('title', c.user.displayName)
			.css('background-image', 'url(' + c.user.pictureUri + ')')
			.find('.user.name').text(c.user.displayName);

		const actions = le.find('.user.actions');
		__rightVideoIcon(c, actions);
		__rightAudioIcon(c, actions);
		__rightOtherIcons(c, actions);
		__activityIcon(actions, '.kick'
			, () => !self && _hasRight('MODERATOR') && !_hasRight('SUPER_MODERATOR', c.rights)
			, function() { OmUtil.roomAction({action: 'kick', uid: c.uid}); });
		__activityIcon(actions, '.private-chat'
				, () => options.userId !== c.user.id && $('#chatPanel').is(':visible')
				, function() {
					Chat.addTab('chatTab-u' + c.user.id, c.user.displayName);
					Chat.open();
					$('#chatMessage .wysiwyg-editor').click();
				});
		if (self) {
			options.rights = c.rights;
			_setQuickPollRights();
			options.activities = c.activities;
			const header = $('#room-sidebar-tab-users .header');
			__rightVideoIcon(c, header);
			__activityAVIcon(header, '.activity.cam', () => !options.audioOnly && _hasRight('VIDEO')
				, () => hasVideo
				, () => Settings.load().video.cam < 0);
			__rightAudioIcon(c, header);
			__activityAVIcon(header, '.activity.mic', () => _hasRight('AUDIO')
				, () => hasAudio
				, () => Settings.load().video.mic < 0);
			__rightOtherIcons(c, header);
		}
		VideoManager.update(c)
	}

	self.init = _init;
	self.getMenuHeight = function() { return menuHeight; };
	self.getOptions = function() { return typeof(options) === 'object' ? JSON.parse(JSON.stringify(options)) : {}; };
	self.load = _load;
	self.unload = _unload;
	self.showClipboard = _showClipboard;
	self.quickPoll = _quickPoll;
	self.hasRight = _hasRight;
	self.setCssVar = function(key, val) {
		($('.main.room')[0]).style.setProperty(key, val);
	};
	self.addClient = _addClient;
	self.updateClient = function(c) {
		_updateClient(c);
		__sortUserList();
	};
	self.removeClient = function(uid) {
		Room.getClient(uid).remove();
		__updateCount();
	};
	self.removeOthers = function() {
		const selfUid = Room.getOptions().uid;
		$('.user-list .user.entry').each(function() {
			const c = $(this);
			if (c.data('uid') !== selfUid) {
				c.remove();
			}
		});
		__updateCount();
	};
	self.getClient = function(uid) {
		return $('#user' + uid);
	};
	return self;
})();
/***** functions required by SIP   ******/
function sipBtnClick() {
	const txt = $('.sip-number');
	txt.val(txt.val() + $(this).data('value'));
}
function sipBtnEraseClick() {
	const txt = $('.sip-number')
		, t = txt.val();
	if (!!t) {
		txt.val(t.substring(0, t.length - 1));
	}
}
function sipGetKey(evt) {
	let k = -1;
	if (evt.keyCode > 47 && evt.keyCode < 58) {
		k = evt.keyCode - 48;
	}
	if (evt.keyCode > 95 && evt.keyCode < 106) {
		k = evt.keyCode - 96;
	}
	return k;
}
function sipKeyDown(evt) {
	const k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).addClass('ui-state-active');
	}
}
function sipKeyUp(evt) {
	const k = sipGetKey(evt);
	if (k > 0) {
		$('#sip-dialer-btn-' + k).removeClass('ui-state-active');
	}
}
function typingActivity(uid, active) {
	const u = Room.getClient(uid).find('.typing-activity');
	if (active) {
		u.addClass("typing");
	} else {
		u.removeClass("typing");
	}
}
$(function() {
	$('.sip').on('keydown', sipKeyDown).on('keyup', sipKeyUp);
	$('.sip .button-row button').button().click(sipBtnClick);
	$('#sip-dialer-btn-erase').button().click(sipBtnEraseClick);
});
