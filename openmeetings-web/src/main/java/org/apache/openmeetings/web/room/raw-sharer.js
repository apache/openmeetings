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
		type = sharer.find('select.type').selectmenu({
			width: 150
			, disabled: VideoUtil.isEdge()
		});
		fps = sharer.find('select.fps').selectmenu({
			width: 120
			, disabled: VideoUtil.isEdge()
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
				const cuid = Room.getOptions().uid
					, v = $('div[data-client-uid="' + cuid + '"][data-client-type="SCREEN"]')
					, uid = v.data().stream().uid;
				VideoManager.sendMessage({
					id: 'stopSharing'
					, uid: uid
				});
				VideoManager.close(uid, false);
				_setShareState(SHARE_STOPED);
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
				const cuid = Room.getOptions().uid
					, v = $('div[data-client-uid="' + cuid + '"][data-client-type="SCREEN"]')
					, uid = v.data().stream().uid;
				VideoManager.sendMessage({
					id: 'stopRecord'
					, uid: uid
				});
				VideoManager.close(uid, false);
				_setRecState(SHARE_STOPED);
			}
		});
	}
	function _setShareState(state) {
		shareState = state;
		const dis = SHARE_STOPED !== state;
		type.selectmenu('option', 'disabled', dis || VideoUtil.isEdge());
		fps.selectmenu('option', 'disabled', dis || VideoUtil.isEdge());
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
		const dis = SHARE_STOPED !== state;
		type.selectmenu('option', 'disabled', dis || VideoUtil.isEdge());
		fps.selectmenu('option', 'disabled', dis || VideoUtil.isEdge());
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
	return self;
})();
