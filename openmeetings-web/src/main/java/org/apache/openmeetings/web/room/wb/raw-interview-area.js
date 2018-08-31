/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var InterviewWbArea = function() {
	const self = BaseWbArea();;
	let container, area, pArea, role = NONE, choose
		, _inited = false, rec;

	function _init() {
		Wicket.Event.subscribe("/websocket/message", self.wbWsHandler);
		container = $(".room.wb.area");
		area = container.find(".wb-area");
		pArea = area.find(".pod-area");
		rec = area.find('.rec-btn');
		rec.attr('title', rec.data('title-start'));
		rec.button({
			disabled: true
			, showLabel: false
			, icon: 'record'
		}).click(function() {
			wbAction($(this).data('mode') === 'rec' ? 'startRecording' : 'stopRecording', '');
		});
		pArea.find('.pod-big').droppable({
			accept: '.pod'
			, activeClass: 'ui-hightlight'
			, drop: function(event, ui) {
				const big = $(this)
					, vid = ui.draggable.find('.ui-dialog-content')
					, cvid = big.find('.ui-dialog-content');
				if (cvid.length === 0) {
					vid.dialog('option', 'appendTo', big);
					ui.draggable.remove();
				} else {
					cvid.dialog('option', 'appendTo', ui.draggable);
					vid.dialog('option', 'appendTo', big);
				}
				pArea.find('.ui-sortable-placeholder.pod').hide();
				_updateAreaClass();
			}
		});
		pArea.sortable({
			items: '.pod'
			, handle: '.ui-dialog-titlebar'
			, change: function(event, ui) {
				console.log('changed');
			}
		});
		_updateAreaClass();
		_inited = true;
	}
	function _setRole(_role) {
		if (!_inited) return;
		role = _role;
	}
	function _resizePod() {
		pArea.find('.ui-dialog-content').each(function() {
			$(this).data().resizePod();
		});
	}
	function _resize(sbW, chW, w, h) {
		if (!container || !_inited) return;
		const hh = h - 5 - 40;//rec button height
		container.width(w).height(h).css('left', (Settings.isRtl ? chW : sbW) + 'px');
		area.width(w).height(hh);
		pArea.width(w).height(hh);
		rec.css('right', w / 2)
		_resizePod();
	}
	function _setRecEnabled(en) {
		if (!_inited) return;
		rec.data('mode', 'rec').button('option', {disabled: !en, icon: 'record'});
	}
	function _setRecStarted(started) {
		if (!_inited) return;
		rec.data('mode', started ? 'stop' : 'rec')
			.attr('title', rec.data(started ? 'title-stop' : 'title-start'))
			.button('option', {icon: started ? 'stop' : 'record'});
	}
	function _updateAreaClass() {
		const count = pArea.find('.pod:not(.ui-helper,.ui-sortable-placeholder)').length
			, empt = pArea.find('.empty');
		if (count < 2) {
			empt.length == 0 && pArea.append($('<div class="empty"></div>'));
		} else {
			empt.remove();
		}
		let cls = 'pod-area ';
		if (count < 2) {
			cls +='max2';
		} else if (count < 3) {
			cls +='max3';
		} else if (count < 5) {
			cls +='max5';
		} else if (count < 9) {
			cls +='max9';
		} else if (count < 13) {
			cls +='max13';
		} else if (count < 17) {
			cls +='max17';
		} else if (count < 25) {
			cls +='max25';
		} else if (count < 33) {
			cls +='max33';
		}
		pArea.attr('class', cls);
		_resizePod();
	}

	self.init = _init;
	self.destroy = function() {
		Wicket.Event.unsubscribe("/websocket/message", self.wbWsHandler);
	};
	self.setRole = _setRole;
	self.resize = _resize;
	self.setRecEnabled = _setRecEnabled;
	self.setRecStarted = _setRecStarted;
	self.addDeleteHandler = function() {};
	self.removeDeleteHandler = function() {};
	self.updateAreaClass = _updateAreaClass;
	return self;
};
