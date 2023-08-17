/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const Settings = require('../main/settings');
const Chat = require('../chat/chat');
const VideoUtil = require('../settings/video-util');
const VideoSettings = require('../settings/settings');

const OmUtil = require('../main/omutils');
const VideoManager = require('./video-manager');
const UserListUtil = require('./user-list-util');
const QuickPoll = require('./quick-poll');

let options;

function __activityAVIcon(elem, selector, predicate, onfunc, disabledfunc) {
	const icon = elem.find(selector);
	if (predicate()) {
		icon.show();
		const on = onfunc()
			, disabled = disabledfunc();
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
	return icon;
}
function __activityIcon(elem, selector, predicate, action, confirm) {
	let icon = elem.find(selector);
	if (predicate()) {
		if (icon.length === 0) {
			icon = OmUtil.tmpl('#user-actions-stub ' + selector);
			elem.append(icon);
		}
		icon.off();
		if (confirm) {
			icon.confirmation(confirm);
		} else {
			icon.click(action);
		}
	} else {
		icon.hide();
	}
}
function __rightIcon(c, elem, rights, selector, predicate) {
	const self = c.uid === options.uid
		, hasRight = UserListUtil.hasRight(rights, c.rights);
	let icon = elem.find(selector);
	if (predicate() && !UserListUtil.hasRight('SUPER_MODERATOR', c.rights) && (
		(self && options.questions && !hasRight)
		|| (!self && UserListUtil.hasRight('MODERATOR'))
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
	__rightIcon(c, elem, ['SHARE'], '.right.screen-share', () => true);
	__rightIcon(c, elem, ['REMOTE_CONTROL'], '.right.remote-control', () => true);
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


function _getClient(uid) {
	return $('#user' + uid);
}
function _addClient(_clients) {
	if (!options) {
		return; //too early
	}
	const clients = Array.isArray(_clients) ? _clients : [_clients];
	clients.forEach(c => {
		const self = c.uid === options.uid;
		let le = _getClient(c.uid);
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
	if (!options) {
		return; //too early
	}
	const self = c.uid === options.uid
		, le = _getClient(c.uid)
		, selfCamStream = c.streams.find(s => s.type === 'WEBCAM')
		, hasAudio = VideoUtil.hasMic(self && selfCamStream ? selfCamStream : c)
		, hasVideo = VideoUtil.hasCam(self && selfCamStream ? selfCamStream : c)
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
		if(UserListUtil.hasRight('MUTE_OTHERS')) {
			speaks.addClass('clickable').click(function () {
				VideoManager.clickMuteOthers(c.uid);
			}).attr('title', speaks.attr('title') + speaks.data('mute'));
		}
	}
	le.attr('title', c.user.displayName)
		.css('background-image', 'url(' + c.user.pictureUri + ')')
		.find('.user.name').text(c.user.displayName);

	if (c.user.id !== -1) {
		const actions = le.find('.user.actions');
		__rightVideoIcon(c, actions);
		__rightAudioIcon(c, actions);
		__rightOtherIcons(c, actions);
		__activityIcon(actions, '.kick'
			, () => !self && UserListUtil.hasRight('MODERATOR') && !UserListUtil.hasRight('SUPER_MODERATOR', c.rights)
			, null
			, {
				confirmationEvent: 'om-kick'
				, placement: Settings.isRtl ? 'left' : 'right'
				, onConfirm: () => OmUtil.roomAction({action: 'kick', uid: c.uid})
			});
		__activityIcon(actions, '.private-chat'
			, () => options.userId !== c.user.id && $('#chatPanel').is(':visible')
			, function() {
				Chat.addTab('chatTab-u' + c.user.id, c.user.displayName);
				Chat.open();
				$('#chatMessage .wysiwyg-editor').click();
			});
	}
	if (self) {
		options.rights = c.rights;
		QuickPoll.setRights();
		options.activities = c.activities;
		const header = $('#room-sidebar-tab-users .header');
		header.find('.om-icon.settings').off().click(VideoSettings.open);
		__rightVideoIcon(c, header);
		__activityAVIcon(
				header
				, '.activity.cam'
				, () => !options.audioOnly && UserListUtil.hasRight(VideoUtil.CAM_ACTIVITY)
				, () => hasVideo
				, () => Settings.load().video.cam < 0)
			.off().click(function() {
				VideoManager.toggleActivity(VideoUtil.CAM_ACTIVITY);
			});;
		__rightAudioIcon(c, header);
		__activityAVIcon(
				header
				, '.activity.mic', () => UserListUtil.hasRight(VideoUtil.MIC_ACTIVITY)
				, () => hasAudio
				, () => Settings.load().video.mic < 0)
			.off().click(function() {
				VideoManager.toggleActivity(VideoUtil.MIC_ACTIVITY);
			});
		__rightOtherIcons(c, header);
	}
	VideoManager.update(c)
}

module.exports = {
	init: function(opts) {
		options = opts;
		UserListUtil.init(opts);
	}
	, addClient: _addClient
	, updateClient: function(c) {
		_updateClient(c);
		__sortUserList();
	}
	, removeClient: function(uid) {
		_getClient(uid).remove();
		__updateCount();
	}
	, removeOthers: function() {
		const selfUid = options.uid;
		$('.user-list .user.entry').each(function() {
			const c = $(this);
			if (c.data('uid') !== selfUid) {
				c.remove();
			}
		});
		__updateCount();
	}
	, getClient: _getClient
};
