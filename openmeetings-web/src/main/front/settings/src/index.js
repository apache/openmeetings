/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
require('webrtc-adapter');
const VideoUtil = require('./video-util');
const {WebRtcPeerRecvonly, WebRtcPeerSendonly} = require('./WebRtcPeer');

if (window.hasOwnProperty('isSecureContext') === false) {
	window.isSecureContext = window.location.protocol == 'https:' || ["localhost", "127.0.0.1"].indexOf(window.location.hostname) !== -1;
}

Object.assign(window, {
	VideoUtil: VideoUtil
	, VIDWIN_SEL: VideoUtil.VIDWIN_SEL
	, VID_SEL: VideoUtil.VID_SEL
	, MicLevel: require('./mic-level')
	, WebRtcPeerRecvonly: WebRtcPeerRecvonly
	, WebRtcPeerSendonly: WebRtcPeerSendonly
	, VideoSettings: require('./settings')
});
