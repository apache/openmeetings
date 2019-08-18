/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var SHARE_STARTING = 'starting';
var SHARE_STARTED = 'started';
var SHARE_STOPED = 'stoped';
var Sharer = (function() {
	const self = {};
	let sharer, type, fps, sbtn, rbtn, width, height
		, shareState = SHARE_STOPED, recState = SHARE_STOPED;

	function _init() {
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
		return VideoUtil.isEdge(b) || VideoUtil.isChrome(b);
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
	return self;
})();
