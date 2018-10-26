/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Sharer = (function() {
	const self = {};
	let sharer, type, fps, sbtn, rbtn, width, height;

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
		});
		width = sharer.find('.width');
		height = sharer.find('.height');
		sbtn.click(function() {
			_setShareState(true);
			VideoManager.sendMessage({
				id: 'wannaShare'
				, shareType: type.val()
				, fps: fps.val()
				, width: width.val()
				, height: height.val()
			});
		});
		rbtn = sharer.find('.record-start-stop').button({
			icon: 'ui-icon-bullet'
		});
	}
	function _setShareState(state) {
		type.selectmenu('option', 'disabled', state || VideoUtil.isEdge());
		fps.selectmenu('option', 'disabled', state || VideoUtil.isEdge());
		width.prop('disabled', state);
		height.prop('disabled', state);
		sbtn.button('option', 'icon', state ? 'ui-icon-stop' : 'ui-icon-image');
		if (state) {
			sbtn.button('disable');
		} else {
			sbtn.button('enable');
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
	return self;
})();
