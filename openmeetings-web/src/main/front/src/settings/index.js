/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
require('webrtc-adapter');

if (window.hasOwnProperty('isSecureContext') === false) {
	window.isSecureContext = window.location.protocol == 'https:' || ["localhost", "127.0.0.1"].indexOf(window.location.hostname) !== -1;
}

Object.assign(window, {
	VideoUtil: require('./video-util')
	, MicLevel: require('./mic-level')
	, WebRtcPeer: require('./WebRtcPeer')
	, VideoSettings: require('./settings')
});
