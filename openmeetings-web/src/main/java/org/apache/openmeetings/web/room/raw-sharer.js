/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var SHARE_STARTING = 'starting';
var SHARE_STARTED = 'started';
var SHARE_STOPED = 'stoped';
var Sharer = (function() {
	const self = {};
	let sharer, type, fps, sbtn, rbtn, width, height
		, shareState = SHARE_STOPED, recState = SHARE_STOPED
		, iframe, frameUrl = 'https://www.webrtc-experiment.com/getSourceId/';

	function _init(url) {
		frameUrl = url;
		sharer = $('#sharer').dialog({
			width: 450
			, autoOpen: false
		});
		if (!VideoUtil.sharingSupported()) {
			sharer.find('.container').remove();
			sharer.find('.alert').show();
		} else {
			type = sharer.find('select.type');
			const b = kurentoUtils.WebRtcPeer.browser;
			type.find('option[value="' + (VideoUtil.isChrome() ? 'application' : 'tab') + '"]').remove();
			type.selectmenu({
				width: 150
				, disabled: _typeDisabled(b)
			});
			fps = sharer.find('select.fps').selectmenu({
				width: 120
				, disabled: VideoUtil.isEdge(b)
			});
			sbtn = sharer.find('.share-start-stop').button({
				icon: 'ui-icon-image'
			}).off().click(function() {
				if (shareState === SHARE_STOPED) {
					_setShareState(SHARE_STARTING);
					VideoManager.sendMessage({
						id: 'wannaShare'
						, shareType: type.val()
						, fps: fps.val()
						, width: width.val()
						, height: height.val()
					});
				} else {
					VideoManager.sendMessage({
						id: 'pauseSharing'
						, uid: _getShareUid()
					});
				}
			});
			width = sharer.find('.width');
			height = sharer.find('.height');
			rbtn = sharer.find('.record-start-stop').button({
				icon: 'ui-icon-bullet'
			}).off().click(function() {
				if (recState === SHARE_STOPED) {
					_setRecState(SHARE_STARTING);
					VideoManager.sendMessage({
						id: 'wannaRecord'
						, shareType: type.val()
						, fps: fps.val()
						, width: width.val()
						, height: height.val()
					});
				} else {
					VideoManager.sendMessage({
						id: 'stopRecord'
						, uid: _getShareUid()
					});
				}
			});
		}
	}
	function _typeDisabled(_b) {
		const b = _b || kurentoUtils.WebRtcPeer.browser;
		return VideoUtil.isEdge(b) || VideoUtil.isChrome72(b);
	}
	function _setShareState(state) {
		shareState = state;
		const dis = SHARE_STOPED !== state
			, typeDis = _typeDisabled();
		type.selectmenu('option', 'disabled', dis || typeDis);
		fps.selectmenu('option', 'disabled', dis || typeDis);
		width.prop('disabled', dis);
		height.prop('disabled', dis);
		sbtn.text(sbtn.data(dis ? 'stop' : 'start'));
		sbtn.button('option', 'icon', dis ? 'ui-icon-stop' : 'ui-icon-image');
		if (state === SHARE_STARTING) {
			sbtn.button('disable');
			rbtn.button('disable');
		} else {
			sbtn.button('enable');
			rbtn.button('enable');
		}
	}
	function _setRecState(state) {
		recState = state;
		const dis = SHARE_STOPED !== state
			, typeDis = _typeDisabled();
		type.selectmenu('option', 'disabled', dis || typeDis);
		fps.selectmenu('option', 'disabled', dis || typeDis);
		width.prop('disabled', dis);
		height.prop('disabled', dis);
		rbtn.text(rbtn.data(dis ? 'stop' : 'start'));
		rbtn.button('option', 'icon', dis ? 'ui-icon-stop' : 'ui-icon-image');
		if (state === SHARE_STARTING) {
			sbtn.button('disable');
			rbtn.button('disable');
		} else {
			sbtn.button('enable');
			rbtn.button('enable');
		}
	}
	// Following methods are based on
	// Licensed MIT
	// Last time updated on June 08, 2018
	// Latest file can be found here: https://cdn.webrtc-experiment.com/getScreenId.js
	// Muaz Khan         - www.MuazKhan.com
	function _getChromeConstraints(sd) {
		return new Promise((resolve) => {
			if (iframe) {
				iframe.remove();
			}
			iframe = $('<iframe>')
				.on('load', function() {
					resolve();
				})
				.attr('src', frameUrl)
				.hide();
			$(document.body || document.documentElement).append(iframe);
		}).then(() => {
			return new Promise((resolve, reject) => {
				window.addEventListener('message', _onIFrameCallback);

				function _onIFrameCallback(event) {
					if (!event.data) {
						return;
					}
					if (event.data.chromeMediaSourceId) {
						if (event.data.chromeMediaSourceId === 'PermissionDeniedError') {
							reject('permission-denied');
						} else {
							resolve(_getScreenConstraints(sd, event.data.chromeMediaSourceId));
						}
						// this event listener is no more needed
						window.removeEventListener('message', _onIFrameCallback);
					}
					if (event.data.chromeExtensionStatus) {
						reject(event.data.chromeExtensionStatus);
						// this event listener is no more needed
						window.removeEventListener('message', _onIFrameCallback);
					}
				}

				iframe[0].contentWindow.postMessage({
					captureCustomSourceId: [sd.shareType]
				}, '*');
			});
		});
	};
	function _getScreenConstraints(sd, sourceId) {
		//Chrome screen constraints requires old school definition
		const cnts = {
			audio: false
			, video: {
				mandatory: {
					maxWidth: sd.width
					, maxHeight: sd.height
				}
				, optional: []
			}
		};
		if (sourceId) {
			cnts.video.mandatory = {
				chromeMediaSourceId: sourceId
				, chromeMediaSource: 'desktop'
			};
		}
		return cnts;
	}
	function _getShareUid() {
		const v = $('div[data-client-uid="' + Room.getOptions().uid + '"][data-client-type="SCREEN"]');
		return v && v.data() && v.data().stream() ? v.data().stream().uid : '';
	}

	self.init = _init;
	self.open = function() {
		if (sharer && sharer.dialog('instance')) {
			sharer.dialog('open');
		}
	};
	self.close = function() {
		if (sharer && sharer.dialog('instance')) {
			sharer.dialog('close');
		}
	};
	self.setShareState = _setShareState;
	self.setRecState = _setRecState;
	self.baseConstraints = function(sd) {
		return {
			video: {
				frameRate: {
					ideal: sd.fps
				}
				, width: {
					ideal: sd.width
				}
				, height: {
					ideal: sd.height
				}
			}
			, audio: false
		};
	};
	self.getChromeConstraints = _getChromeConstraints;
	return self;
})();
