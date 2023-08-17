/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const Settings = require('../main/settings');
const Chat = require('../chat/chat');
const VideoSettings = require('../settings/settings');

function __initMuteOthers() {
	let s = VideoSettings.load();
	$('#muteOthersAsk')
		.prop('checked', s.video.confirmMuteOthers)
		.off().click(function() {
			s = VideoSettings.load();
			s.video.confirmMuteOthers = !$('#muteOthersAsk').prop('checked');
			VideoSettings.save();
		});
}
function __initChat() {
	let s = Settings.load();
	$('#chatNotify')
		.prop('checked', s.chat.muted)
		.off().click(function() {
			s = Settings.load();
			s.chat.muted = !$('#chatNotify').prop('checked');
			Settings.save(s);
			Chat.reload();
		});
	$('#sendOnCtrlEnter')
		.prop('checked', s.chat.sendOn === Chat.SEND_CTRL)
		.off().click(function() {
			s = Settings.load();
			s.chat.sendOn = $('#sendOnCtrlEnter').prop('checked') ? Chat.SEND_CTRL : Chat.SEND_ENTER;
			Settings.save(s);
			Chat.reload();
		});
}
function __initVideo() {
	const resolutions = $('#video-settings .cam-resolution').clone();
	let s = VideoSettings.load();
	resolutions
		.change(function() {
			const o = resolutions.find('option:selected').data();
			s.fixed.width = o.width;
			s.fixed.height = o.height;
			VideoSettings.save();
		})
		.find('option').each(function() {
			const o = $(this).data();
			if (o.width === s.fixed.width && o.height === s.fixed.height) {
				$(this).prop('selected', true);
				return false;
			}
		});
	$('#video-sizes-container')
		.html('')
		.append(resolutions.prop('disabled', !s.fixed.enabled));
	$('#fixedVideoPod')
		.prop('checked', s.fixed.enabled)
		.off().click(function() {
			s = VideoSettings.load();
			s.fixed.enabled = $('#fixedVideoPod').prop('checked');
			resolutions.prop('disabled', !s.fixed.enabled)
			VideoSettings.save();
		});
}
function _open() {
	__initMuteOthers();
	__initChat();
	__initVideo();
	$('#room-local-settings').modal('show');
}

module.exports = {
	open: _open
};
