/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const VideoUtil = require('./video-util');

if (window.hasOwnProperty('isSecureContext') === false) {
	window.isSecureContext = window.location.protocol == 'https:' || ["localhost", "127.0.0.1"].indexOf(window.location.hostname) !== -1;
}

Object.assign(window, {
	VideoUtil: VideoUtil
	, VIDWIN_SEL: VideoUtil.VIDWIN_SEL
	, VID_SEL: VideoUtil.VID_SEL
	, MicLevel: require('./mic-level')
	, VideoSettings: require('./settings')

	// AdapterJS is not added for now
	, kurentoUtils: require('kurento-utils')
	, uuidv4: require('uuid/v4')
});
