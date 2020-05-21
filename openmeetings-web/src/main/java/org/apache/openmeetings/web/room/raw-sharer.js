/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var SHARE_STARTING = 'starting';
var SHARE_STARTED = 'started';
var SHARE_STOPPED = 'stopped';
var Sharer = (function() {
	const self = {};
	let sharer, type, fps, sbtn, rbtn, width, height
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
			classes: {
				'ui-dialog': 'sharer'
				, 'ui-dialog-titlebar': ''
			}
			, width: 450
			, autoOpen: false
			, resizable: false
		});
		fixJQueryUIDialogTouch(sharer);
		
		if (!VideoUtil.sharingSupported()) {
			sharer.find('.container').remove();
			sharer.find('.alert').show();
		} else {
			type = sharer.find('select.type');
			const b = kurentoUtils.WebRtcPeer.browser;
			fps = sharer.find('select.fps');
			_disable(fps, VideoUtil.isEdge(b));
			sbtn = sharer.find('.share-start-stop').off().click(function() {
				if (shareState === SHARE_STOPPED) {
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
			rbtn = sharer.find('.record-start-stop').off();
			if (Room.getOptions().allowRecording) {
				rbtn.show().click(function() {
					if (recState === SHARE_STOPPED) {
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
	function _typeDisabled(_b) {
		const b = _b || kurentoUtils.WebRtcPeer.browser;
		return VideoUtil.isEdge(b) || VideoUtil.isChrome(b) || VideoUtil.isEdgeChromium(b);
	}
	function _setBtnState(btn, state) {
		const dis = SHARE_STOPPED !== state
			, typeDis = _typeDisabled();
		_disable(type, dis);
		_disable(fps, dis || typeDis);
		_disable(width, dis);
		_disable(height, dis);
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
			}
			, audio: false
		};
	};
	return self;
})();
