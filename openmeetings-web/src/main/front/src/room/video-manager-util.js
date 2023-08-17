/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const VideoUtil = require('../settings/video-util');
const VideoSettings = require('../settings/settings');

let share;

function __savePod(v) {
	const opts = Room.getOptions();
	if (!opts.interview && v && v.find('video').length > 0 && v.data('clientType') === 'WEBCAM' && v.dialog('instance')) {
		const userUI = $(`#user${v.data('clientUid')}`)
			, widget = v.dialog('widget');
		userUI.data('video-pod', {
			x: widget.css('left')
			, y: widget.css('top')
			, w: widget.css('width')
			, h: widget.css('height')
		});
	}
}
function _closeV(v) {
	if (!v || v.length < 1) {
		return;
	}
	__savePod(v);
	if (v.dialog('instance') !== undefined) {
		v.dialog('destroy');
	}
	v.parents('.pod').remove();
	v.remove();
	WbArea.updateAreaClass();
}
function _clickMuteOthers(uid) {
	const s = VideoSettings.load();
	if (false !== s.video.confirmMuteOthers) {
		const dlg = $('#muteothers-confirm');
		dlg.dialog({
			appendTo: ".room-container"
			, buttons: [
				{
					text: dlg.data('btn-ok')
					, click: function() {
						s.video.confirmMuteOthers = !$('#muteothers-confirm-dont-show').prop('checked');
						VideoSettings.save();
						OmUtil.roomAction({action: 'muteOthers', uid: uid});
						$(this).dialog('close');
					}
				}
				, {
					text: dlg.data('btn-cancel')
					, click: function() {
						$(this).dialog('close');
					}
				}
			]
		})
	} else {
		OmUtil.roomAction({action: 'muteOthers', uid: uid});
	}
}

module.exports = {
	init: (_share) => {
		share = _share;
	}
	, sendMessage: (_m) => {
		OmUtil.sendMessage(_m, {type: 'kurento'});
	}
	, savePod: __savePod
	, closeV: _closeV
	, close: (uid, showShareBtn) => {
		const v = $('#' + VideoUtil.getVid(uid));
		if (v.length === 1) {
			_closeV(v);
		}
		if (!showShareBtn && uid === share.data('uid')) {
			share.off().hide();
		}
	}
	, clickMuteOthers: _clickMuteOthers
};
