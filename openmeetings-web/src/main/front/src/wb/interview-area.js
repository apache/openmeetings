/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const WbAreaBase = require('./wb-area-base');

module.exports = class InterviewWbArea extends WbAreaBase {
	constructor() {
		super();
		let container, area, pArea, _inited = false, rec;

		function _updateAreaClass() {
			const count = pArea.find('.pod:not(.ui-helper,.ui-sortable-placeholder)').length
				, empt = pArea.find('.empty');
			if (count < 2) {
				empt.length === 0 && pArea.append($('<div class="empty"></div>'));
			} else {
				empt.remove();
			}
			const cls = 'pod-area max' +
				[2, 3, 5, 9, 13, 17, 25, 33].find((el) => {
					return count < el;
				});
			pArea.attr('class', cls);
		}
		this.init = () => {
			// it seems `super` can't be called from lambda
			this.wsinit();
			container = $(".room-block .wb-block");
			area = container.find(".wb-area");
			pArea = area.find(".pod-area");
			rec = area.find('.rec-btn');
			rec.attr('title', rec.data('title-start'));
			rec.button({
				disabled: true
				, showLabel: false
				, icon: 'record'
			}).click(function() {
				OmUtil.wbAction({action: $(this).data('mode') === 'rec' ? 'startRecording' : 'stopRecording'});
			});
			pArea.find('.pod-big').droppable({
				accept: '.pod'
				, activeClass: 'ui-hightlight'
				, drop: function(_, ui) {
					const big = $(this)
						, vid = ui.draggable.find('.ui-dialog-content')
						, cvid = big.find('.ui-dialog-content');
					if (cvid.length === 0) {
						vid.dialog('option', 'appendTo', big);
						ui.draggable.remove();
					} else {
						cvid.dialog('option', 'appendTo', ui.draggable);
						cvid.data().reattachStream();
						vid.dialog('option', 'appendTo', big);
					}
					vid.data().reattachStream();
					pArea.find('.ui-sortable-placeholder.pod').hide();
					_updateAreaClass();
				}
			});
			pArea.sortable({
				items: '.pod'
				, handle: '.ui-dialog-titlebar'
				, stop: function(_, ui) {
					const vid = ui.item.find('.ui-dialog-content');
					if (vid.length === 1) {
						vid.data().reattachStream();
					}
				}
			});
			_updateAreaClass();
			_inited = true;
		};
		this.destroy = () => {
			// it seems `super` can't be called from lambda
			this.wsdestroy();
		};
		this.setRecEnabled = (en) => {
			if (!_inited) {
				return;
			}
			rec.data('mode', 'rec').button('option', {disabled: !en, icon: 'record'});
		};
		this.setRecStarted = (started) => {
			if (!_inited) {
				return;
			}
			rec.data('mode', started ? 'stop' : 'rec')
				.attr('title', rec.data(started ? 'title-stop' : 'title-start'))
				.button('option', {icon: started ? 'stop' : 'record'});
		};
		this.updateAreaClass = _updateAreaClass;
	}
};
