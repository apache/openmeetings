/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const VideoUtil = require('../settings/video-util');
const VideoSettings = require('../settings/settings');

let share, inited = false;
const VideoMgrUtil = require('./video-manager-util');
const Video = require('./video');
const Sharer = require('./sharer');

function _onVideoResponse(m) {
	const w = $('#' + VideoUtil.getVid(m.uid))
		, v = w.data();
	if (v) {
		v.processSdpAnswer(m.sdpAnswer);
	}
}
function _onBroadcast(msg) {
	const sd = msg.stream
		, uid = sd.uid;
	if (Array.isArray(msg.cleanup)) {
		msg.cleanup.forEach(function(_cuid) {
			VideoMgrUtil.close(_cuid);
		});
	}
	$('#' + VideoUtil.getVid(uid)).remove();
	new Video(msg);
	OmUtil.log(uid + ' registered in room');
}
function _onShareUpdated(msg) {
	const sd = msg.stream
		, uid = sd.uid
		, w = $('#' + VideoUtil.getVid(uid))
		, v = w.data();
	if (v && (VideoUtil.isSharing(sd) || VideoUtil.isRecording(sd))) {
		// Update activities in the current data object
		v.stream().activities = sd.activities;
	}
	Sharer.setShareState(VideoUtil.isSharing(sd) ? Sharer.SHARE_STARTED : Sharer.SHARE_STOPPED);
	Sharer.setRecState(VideoUtil.isRecording(sd) ? Sharer.SHARE_STARTED : Sharer.SHARE_STOPPED);
}
function _onReceive(msg) {
	const uid = msg.stream.uid;
	VideoMgrUtil.closeV($('#' + VideoUtil.getVid(uid)));
	new Video(msg);
	OmUtil.log(uid + ' receiving video');
}
function _onKMessage(m) {
	switch (m.id) {
		case 'clientLeave':
			$(`${VideoUtil.VID_SEL}[data-client-uid="${m.uid}"]`).each(function() {
				VideoMgrUtil.closeV($(this));
			});
			if (share.data('cuid') === m.uid) {
				share.off().hide();
			}
			break;
		case 'broadcastStopped':
			VideoMgrUtil.close(m.uid, false);
			break;
		case 'broadcast':
			_onBroadcast(m);
			break;
		case 'videoResponse':
			_onVideoResponse(m);
			break;
		case 'iceCandidate':
			{
				const w = $('#' + VideoUtil.getVid(m.uid))
					, v = w.data();
				if (v) {
					v.processIceCandidate(m.candidate);
				}
			}
			break;
		case 'newStream':
			_play([m.stream], m.iceServers);
			break;
		case 'shareUpdated':
			_onShareUpdated(m);
			break;
		case 'error':
			OmUtil.error(m.message);
			break;
		default:
			//no-op
	}
}
function _onWsMessage(_, msg) {
	try {
		if (msg instanceof Blob) {
			return; //ping
		}
		const m = JSON.parse(msg);
		if (!m) {
			return;
		}
		if ('kurento' === m.type && 'test' !== m.mode) {
			OmUtil.info('Received message: ' + msg);
			_onKMessage(m);
		} else if ('mic' === m.type) {
			switch (m.id) {
				case 'activity':
					_userSpeaks(m.uid, m.active);
					break;
				default:
					//no-op
			}
		}
	} catch (err) {
		OmUtil.error(err);
	}
}
function _init() {
	Wicket.Event.subscribe('/websocket/message', _onWsMessage);
	VideoSettings.init(Room.getOptions());
	share = $('.room-block .room-container').find('.btn.shared');
	VideoMgrUtil.init(share);
	inited = true;
	$(window).on('keydown.push-to-talk', {enable: true}, _onPtt);
	$(window).on('keyup.push-to-talk', {enable: false}, _onPtt);
}
function _update(c) {
	if (!inited) {
		return;
	}
	const streamMap = {};
	c.streams.forEach(function(sd) {
		streamMap[sd.uid] = sd.uid;
		sd.self = c.self;
		sd.cam = c.cam;
		sd.mic = c.mic;
		if (VideoUtil.isSharing(sd) || VideoUtil.isRecording(sd)) {
			return;
		}
		const _id = VideoUtil.getVid(sd.uid)
			, av = VideoUtil.hasMic(sd) || VideoUtil.hasCam(sd)
			, v = $('#' + _id);
		if (av && v.length === 1) {
			v.data().update(sd);
		} else if (!av && v.length === 1) {
			VideoMgrUtil.closeV(v);
		}
	});
	if (c.uid === Room.getOptions().uid) {
		$(VideoUtil.VID_SEL).each(function() {
			$(this).data().setRights(c.rights);
		});
	}
	$(`[data-client-uid="${c.cuid}"]`).each(function() {
		const sd = $(this).data().stream();
		if (!streamMap[sd.uid]) {
			//not-inited/invalid video window
			VideoMgrUtil.closeV($(this));
		}
	});
}
function _playSharing(sd, iceServers) {
	const m = {stream: sd, iceServers: iceServers}
		, v = $('#' + VideoUtil.getVid(sd.uid))
	if (v.length === 1) {
		v.remove();
	}
	new Video(m);
	VideoUtil.setPos(v, {left: 0, top: 35});
}
function _play(streams, iceServers) {
	if (!inited) {
		return;
	}
	streams.forEach(function(sd) {
		const m = {stream: sd, iceServers: iceServers};
		if (VideoUtil.isSharing(sd)) {
			VideoUtil.highlight(share
					.attr('title', share.data('user') + ' ' + sd.user.firstName + ' ' + sd.user.lastName + ' ' + share.data('text'))
					.data('uid', sd.uid)
					.data('cuid', sd.cuid)
					.show()
				, 'btn-outline-warning', 10);
			share.tooltip().off().click(function() {
				_playSharing(sd, iceServers);
			});
			if (Room.getOptions().autoOpenSharing === true) {
				_playSharing(sd, iceServers);
			}
		} else if (VideoUtil.isRecording(sd)) {
			return;
		} else {
			_onReceive(m);
		}
	});
}
function _find(uid) {
	return $(`${VideoUtil.VID_SEL}[data-client-uid="${uid}"][data-client-type="WEBCAM"]`);
}
function _userSpeaks(uid, active) {
	const u = $(`#user${uid} .audio-activity`)
		, v = _find(uid).parent();
	if (active) {
		u.addClass('speaking');
		v.addClass('user-speaks')
	} else {
		u.removeClass('speaking');
		v.removeClass('user-speaks')
	}
}
function _refresh(uid, opts) {
	const v = _find(uid);
	if (v.length > 0) {
		v.data().refresh(opts);
	}
}
function _mute(uid, mute) {
	const v = _find(uid);
	if (v.length > 0) {
		v.data().mute(mute);
	}
}
function _muteOthers(uid) {
	$(VideoUtil.VID_SEL).each(function() {
		const w = $(this), v = w.data(), v2 = w.data('client-uid');
		if (v && v2) {
			v.mute(uid !== v2);
		}
	});
}
function _toggleActivity(activity) {
	VideoMgrUtil.sendMessage({
		id: 'toggleActivity'
		, activity: activity
	});
}
function _onPtt(e) {
	if ((e.ctrlKey || e.metaKey) && 'Space' === e.code) {
		const v = _find(Room.getOptions().uid);
		if (v.length > 0 && v.data()) {
			v.data().pushToTalk(e.data.enable);
		}
	}
}

module.exports = {
	init: _init
	, update: _update
	, play: _play
	, refresh: _refresh
	, mute: _mute
	, muteOthers: _muteOthers
	, toggleActivity: _toggleActivity
	, destroy: function() {
		$(window).off('keydown.push-to-talk');
		$(window).off('keyup.push-to-talk');
		Wicket.Event.unsubscribe('/websocket/message', _onWsMessage);
	}
};
