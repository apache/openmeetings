/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Activities = function() {
	const closedHeight = 20, timeout = 10000;
	let activities, aclean, area, openedHeight = 345, openedHeightPx = openedHeight + 'px', inited = false;

	function _load() {
		const s = Settings.load();
		if (typeof(s.activity) === 'undefined') {
			s.activity = {};
		}
		return s;
	}
	function _updateClean(s, a) {
		const clean = s.activity.clean === true;
		a.prop('checked', clean);
		if (clean) {
			activities.find('.auto-clean').each(function() {
				setTimeout(_clearItem.bind(null, $(this).data().id), timeout);
			});
		}
	}
	function isClosed() {
		return activities.height() < 24;
	}
	function _open() {
		if (isClosed()) {
			$('.control.block .ui-icon', activities).removeClass('ui-icon-caret-1-n').addClass('ui-icon-caret-1-s');
			$('.control.block', activities).removeClass('ui-state-highlight');
			activities.animate(
				{
					height: openedHeightPx
					, top: '-' + openedHeightPx
				}
				, 1000
				, function() {
					activities.css({'top': ''});
					Room.setSize();
				}
			);
			activities.resizable('option', 'disabled', false);
		}
	}
	function _close() {
		if (!isClosed()) {
			$('.control.block .ui-icon', activities).removeClass('ui-icon-caret-1-s').addClass('ui-icon-caret-1-n');
			activities.animate(
				{
					height: closedHeight
					, top: (openedHeight - closedHeight) + 'px'
				}
				, 1000
				, function() {
					activities.css({'top': ''});
					Room.setSize();
				}
			);
			activities.resizable('option', 'disabled', true);
		}
	}
	function _findUser(uid) {
		const m = '5px', t = 50, u = $('#user' + uid);
		if (u.length === 1) {
			u[0].scrollIntoView();
			u.addClass('ui-state-highlight');
			for(let i = 0; i < 10; i++) {
				u.animate({marginTop: '-='+m}, t)
					.animate({marginTop: '+='+m}, t);
			}
			u.removeClass('ui-state-highlight', 1500);
		}
	}
	function _hightlight() {
		if (!inited) return;
		if (isClosed()) {
			$('.control.block', activities).addClass('ui-state-highlight');
		}
	}
	function _getId(id) {
		return 'activity-' + id;
	}
	function _action(name, val) {
		activityAction($('.room.box').data('room-id'), name, val);
	}
	function _remove(id) {
		$('#' + _getId(id)).remove();
	}
	function _clearItem(id) {
		if (aclean.prop('checked')) {
			_remove(id);
		}
	}

	return {
		init: function() {
			if ($('#activities').resizable("instance") !== undefined) {
				return;
			}
			activities = $('#activities');
			activities.resizable({
				handles: 'n'
				, disabled: isClosed()
				, alsoResize: '#activities .area'
				, minHeight: 195
				, resize: function(event, ui) {
					activities.css({'top': ''});
				}
				, stop: function(event, ui) {
					openedHeight = ui.size.height;
					openedHeightPx = openedHeight + 'px';
				}
			});
			area = activities.find('.area');
			aclean = $('#activity-auto-clean');
			aclean.change(function() {
				const s = _load();
				s.activity.clean = $(this).prop('checked');
				Settings.save(s);
				_updateClean(s, $(this));
			});
			_updateClean(_load(), aclean);
			inited = true;
		}
		, toggle: function() {
			if (!inited) return;
			if (isClosed()) {
				_open();
			} else {
				_close();
			}
		}
		, findUser: _findUser
		, add: function(obj) {
			if (!inited) return;
			const _id = _getId(obj.id);
			area.append(OmUtil.tmpl('#activity-stub', _id).data(obj));
			const a = $('#' + _id).addClass(obj.cssClass);
			a.find('.activity-close,.activity-accept,.activity-decline,.activity-find').addClass(Settings.isRtl ? 'align-left' : 'align-right');
			const acpt = a.find('.activity-accept');
			if (obj.accept) {
				acpt.click(function() { _action('accept', obj.id); });
			} else {
				acpt.hide();
			}
			const dcln = a.find('.activity-decline');
			if (obj.decline) {
				dcln.click(function() { _action('decline', obj.id); });
			} else {
				dcln.hide();
			}
			const fnd = a.find('.activity-find');
			if (obj.find) {
				fnd.click(function() { _findUser(obj.uid); });
			} else {
				fnd.hide();
			}
			a.find('.activity-close').click(function() { a.remove(); _action('close', obj.id); });
			a.find('.activity-text').text(obj.text);
			_hightlight();
			if (aclean.prop('checked') && a.hasClass('auto-clean')) {
				setTimeout(_clearItem.bind(null, obj.id), timeout);
			}
		}
		, remove: _remove
	};
}();
