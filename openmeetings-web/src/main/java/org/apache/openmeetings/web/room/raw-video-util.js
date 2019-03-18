/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WB_AREA_SEL = '.room.wb.area';
const WBA_WB_SEL = '.room.wb.area .ui-tabs-panel.ui-corner-bottom.ui-widget-content:visible';
const VID_SEL = '.video.user-video';
var VideoUtil = (function() {
	const self = {};
	function _getVid(uid) {
		return 'video' + uid;
	}
	function _isSharing(c) {
		return 'sharing' === c.type && c.screenActivities.includes('sharing');
	}
	function _isRecording(c) {
		return 'sharing' === c.type
			&& c.screenActivities.includes('recording')
			&& !c.screenActivities.includes('sharing');
	}
	function _hasAudio(c) {
		return c.activities.includes('broadcastA');
	}
	function _hasVideo(c) {
		return c.activities.includes('broadcastV');
	}
	function _getRects(sel, excl) {
		const list = [], elems = $(sel);
		for (let i = 0; i < elems.length; ++i) {
			if (excl !== $(elems[i]).attr('aria-describedby')) {
				list.push(_getRect(elems[i]));
			}
		}
		return list;
	}
	function _getRect(e) {
		const win = $(e), winoff = win.offset();
		return {left: winoff.left
			, top: winoff.top
			, right: winoff.left + win.width()
			, bottom: winoff.top + win.height()};
	}
	function _container() {
		const a = $(WB_AREA_SEL);
		const c = a.find('.wb-area .tabs .ui-tabs-panel');
		return c.length > 0 ? $(WBA_WB_SEL) : a;
	}
	function _getPos(list, w, h) {
		if (Room.getOptions().interview) {
			return {left: 0, top: 0};
		}
		const wba = _container(), woffset = wba.offset()
			, offsetX = 20, offsetY = 10
			, area = {left: woffset.left, top: woffset.top, right: woffset.left + wba.width(), bottom: woffset.top + wba.height()};
		const rectNew = {
				_left: area.left
				, _top: area.top
				, right: area.left + w
				, bottom: area.top + h
				, get left() {
					return this._left
				}
				, set left(l) {
					this._left = l;
					this.right = l + w;
				}
				, get top() {
					return this._top
				}
				, set top(t) {
					this._top = t;
					this.bottom = t + h;
				}
			};
		let minY = area.bottom, posFound;
		do {
			posFound = true
			for (let i = 0; i < list.length; ++i) {
				const rect = list[i];
				minY = Math.min(minY, rect.bottom);

				if (rectNew.left < rect.right && rectNew.right > rect.left && rectNew.top < rect.bottom && rectNew.bottom > rect.top) {
					rectNew.left = rect.right + offsetX;
					posFound = false;
				}
				if (rectNew.right >= area.right) {
					rectNew.left = area.left;
					rectNew.top = Math.max(minY, rectNew.top) + offsetY;
					posFound = false;
				}
				if (rectNew.bottom >= area.bottom) {
					rectNew.top = area.top;
					posFound = true;
					break;
				}
			}
		} while (!posFound);
		return {left: rectNew.left, top: rectNew.top};
	}
	function _arrange() {
		const list = [], elems = $(VID_SEL);
		for (let i = 0; i < elems.length; ++i) {
			const v = $(elems[i]);
			v.css(_getPos(list, v.width(), v.height()));
			list.push(_getRect(v));
		}
	}
	function _askPermission(callback) {
		const perm = $('#ask-permission');
		if (undefined === perm.dialog('instance')) {
			perm.data('callbacks', []).dialog({
				appendTo: '.room.holder .room.box'
				, autoOpen: true
				, buttons: [
					{
						text: perm.data('btn-ok')
						, click: function() {
							while (perm.data('callbacks').length > 0) {
								perm.data('callbacks').pop()();
							}
							$(this).dialog('close');
						}
					}
				]
			});
		} else if (!perm.dialog('isOpen')) {
			perm.dialog('open')
		}
		perm.data('callbacks').push(callback);
	}
	function _highlight(el, count) {
		if (!el || el.length < 1 || el.hasClass('disabled') || count < 0) {
			return;
		}
		el.addClass('ui-state-highlight', 2000, function() {
			el.removeClass('ui-state-highlight', 2000, function() {
				_highlight(el, --count);
			});
		});
	}

	self.getVid = _getVid;
	self.isSharing = _isSharing;
	self.isRecording = _isRecording;
	self.hasAudio = _hasAudio;
	self.hasVideo = _hasVideo;
	self.getRects = _getRects;
	self.getPos = _getPos;
	self.container = _container;
	self.arrange = _arrange;
	self.askPermission = _askPermission;
	self.highlight = _highlight;
	return self;
})();
