/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var InterviewWbArea = function() {
	const self = {};
	let container, area, pArea, role = NONE, choose
		, _inited = false, rec;

	function _init() {
		Wicket.Event.subscribe("/websocket/message", wbWsHandler);
		container = $(".room.wb.area");
		area = container.find(".wb-area");
		pArea = area.find(".pod-area");
		rec = area.find('.rec-btn');
		rec.attr('title', rec.data('title-start'));
		rec.button({
			disabled: true
			, showLabel: false
			, icon: 'ui-icon-play'
		}).click(function() {
			wbAction($(this).data('mode') === 'rec' ? 'startRecording' : 'stopRecording', '');
		});
		pArea.find('.pod-big').droppable({
			accept: '.pod'
			, activeClass: 'ui-hightlight'
			, drop: function(event, ui) {
				const vid = ui.draggable.find('.ui-dialog-content');
				vid.dialog('option', 'appendTo', $(this));
				ui.draggable.remove();
				_resizePod($(this));
			}
		});
		pArea.sortable({
			items: '.pod'
			, handle: '.ui-dialog-titlebar'
			, change: function(event, ui) {
				console.log('changed');
			}
		});
		_inited = true;
	}
	function _setRole(_role) {
		if (!_inited) return;
		role = _role;
	}
	function _resizePod(el) {
		(el || pArea).find('.ui-dialog-content').each(function() {
			$(this).data().resizePod();
		});
	}
	function _resize(sbW, chW, w, h) {
		if (!container || !_inited) return;
		const hh = h - 5;
		container.width(w).height(h).css('left', (Settings.isRtl ? chW : sbW) + 'px');
		area.width(w).height(hh);
		pArea.width(w).height(hh);
		_resizePod();
	}
	function _setRecEnabled(en) {
		if (!_inited) return;
		rec.data('mode', 'rec').button('option', {disabled: !en, icon: 'ui-icon-play'});
	}
	function _setRecStarted(started) {
		if (!_inited) return;
		rec.data('mode', started ? 'stop' : 'rec')
			.attr('title', rec.data(started ? 'title-stop' : 'title-start'))
			.button('option', {icon: started ? 'ui-icon-stop' : 'ui-icon-play'});
	}

	self.init = _init;
	self.destroy = function() {
		Wicket.Event.unsubscribe("/websocket/message", wbWsHandler);
	};
	self.setRole = _setRole;
	self.resize = _resize;
	self.setRecEnabled = _setRecEnabled;
	self.setRecStarted = _setRecStarted;
	self.addDeleteHandler = function() {};
	self.removeDeleteHandler = function() {};
	return self;
};
