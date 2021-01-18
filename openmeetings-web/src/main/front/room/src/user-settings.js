/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */

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
	let s = VideoSettings.load()
		, resolutions = $('#video-settings .cam-resolution').clone();
	if (typeof(s.fixed) === 'undefined') {
		s.fixed = {
			enabled: false
			, width: 120
			, height: 90
		};
		VideoSettings.save()
	}
	$('#video-sizes-container').html('')
		.append(resolutions);
	$('#sendOnCtrlEnter')
		.prop('checked', s.fixed.enabled)
		.off().click(function() {
			s = Settings.load();
			s.chat.sendOn = $('#sendOnCtrlEnter').prop('checked') ? Chat.SEND_CTRL : Chat.SEND_ENTER;
			Settings.save(s);
			Chat.reload();
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
