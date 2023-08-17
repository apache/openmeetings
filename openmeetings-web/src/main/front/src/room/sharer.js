/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const VideoUtil = require('../settings/video-util');
const VideoMgrUtil = require('./video-manager-util');

const SHARE_STARTING = 'starting'
	, SHARE_STARTED = 'started'
	, SHARE_STOPPED = 'stopped';

let sharer, type, fps, sbtn, rbtn
	, shareState = SHARE_STOPPED, recState = SHARE_STOPPED;

/**
 * Re-entering the room should reset settings.
 */
function reset() {
	shareState = SHARE_STOPPED;
	recState = SHARE_STOPPED;
}

function _init() {
	reset();
	sharer = $('#sharer').dialog({
		appendTo: '.room-block .room-container'
		, classes: {
			'ui-dialog': 'sharer'
			, 'ui-dialog-titlebar': ''
		}
		, width: 450
		, autoOpen: false
		, resizable: false
	});
	const ui = sharer.closest('.ui-dialog');
	const parent = $('.room-block .room-container .sb-wb');
	ui.draggable('option', 'containment', parent);
	fixJQueryUIDialogTouch(sharer);

	if (!VideoUtil.sharingSupported()) {
		sharer.find('.container').remove();
		sharer.find('.alert').show();
	} else {
		type = sharer.find('select.type');
		fps = sharer.find('select.fps');
		_disable(fps, VideoUtil.isEdge());
		sbtn = sharer.find('.share-start-stop').off().click(function() {
			if (shareState === SHARE_STOPPED) {
				_setShareState(SHARE_STARTING);
				VideoMgrUtil.sendMessage({
					id: 'wannaShare'
					, shareType: type.val()
					, fps: fps.val()
				});
			} else {
				VideoMgrUtil.sendMessage({
					id: 'pauseSharing'
					, uid: _getShareUid()
				});
			}
		});
		rbtn = sharer.find('.record-start-stop').off();
		if (Room.getOptions().allowRecording) {
			rbtn.show().click(function() {
				if (recState === SHARE_STOPPED) {
					_setRecState(SHARE_STARTING);
					VideoMgrUtil.sendMessage({
						id: 'wannaRecord'
						, shareType: type.val()
						, fps: fps.val()
					});
				} else {
					VideoMgrUtil.sendMessage({
						id: 'stopRecord'
						, uid: _getShareUid()
					});
				}
			});
		} else {
			rbtn.hide();
		}
	}
}
function _disable(e, state) {
	e.prop('disabled', state);
	if (state) {
		e.addClass('disabled');
	} else {
		e.removeClass('disabled');
	}
}
function _typeDisabled() {
	return VideoUtil.isEdge() || VideoUtil.isChrome() || VideoUtil.isEdgeChromium();
}
function _setBtnState(btn, state) {
	const dis = SHARE_STOPPED !== state
		, typeDis = _typeDisabled();
	_disable(type, dis);
	_disable(fps, dis || typeDis);
	btn.find('span').text(btn.data(dis ? 'stop' : 'start'));
	if (dis) {
		btn.addClass('stop');
	} else {
		btn.removeClass('stop');
	}
	_disable(btn, state === SHARE_STARTING);
	_disable(btn, state === SHARE_STARTING);
}
function _setShareState(state) {
	shareState = state;
	_setBtnState(sbtn, state);
}
function _setRecState(state) {
	recState = state;
	_setBtnState(rbtn, state);
}
function _getShareUid() {
	const v = $('div[data-client-uid="' + Room.getOptions().uid + '"][data-client-type="SCREEN"]');
	return v && v.data() && v.data().stream() ? v.data().stream().uid : '';
}

module.exports = {
	SHARE_STARTING: SHARE_STARTING
	, SHARE_STARTED: SHARE_STARTED
	, SHARE_STOPPED: SHARE_STOPPED

	, init: _init
	, open: function() {
		if (sharer && sharer.dialog('instance')) {
			sharer.dialog('open');
		}
	}
	, close: function() {
		if (sharer && sharer.dialog('instance')) {
			sharer.dialog('close');
		}
	}
	, setShareState: _setShareState
	, setRecState: _setRecState
	, baseConstraints: function(sd) {
		return {
			video: {
				frameRate: {
					ideal: sd.fps
				}
			}
			, audio: false
		};
	}
};
