/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const Settings = require('../main/settings');

const closedHeight = 20, timeout = 10000;
let activities, aclean, modArea, area, openedHeight = 345
	, openedHeightPx = openedHeight + 'px', inited = false
	, newActNotification;

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
function _updateHeightVar(h) {
	Room.setCssVar('--activities-height', h);
}
function _open() {
	if (isClosed()) {
		$('.control.block i', activities).removeClass('fa-angle-up').addClass('fa-angle-down');
		$('.control.block', activities).removeClass('bg-warning');
		activities.animate(
			{
				height: openedHeightPx
				, top: '-' + openedHeightPx
			}
			, 1000
			, function() {
				activities.css({'top': ''});
				_updateHeightVar(openedHeightPx);
				activities.resizable('option', 'disabled', false);
			}
		);
	}
}
function _close() {
	if (!isClosed()) {
		$('.control.block i', activities).removeClass('fa-angle-down').addClass('fa-angle-up');
		activities.animate(
			{
				height: closedHeight
				, top: (openedHeight - closedHeight) + 'px'
			}
			, 1000
			, function() {
				activities.css({'top': ''});
				_updateHeightVar(closedHeight + 'px');
			}
		);
		activities.resizable('option', 'disabled', true);
	}
}
function _findUser(uid) {
	const m = '5px', t = 50, u = $('#user' + uid);
	if (u.length === 1) {
		u[0].scrollIntoView();
		u.addClass('bg-warning');
		for(let i = 0; i < 10; i++) {
			u.animate({marginTop: '-='+m}, t)
				.animate({marginTop: '+='+m}, t);
		}
		u.removeClass('bg-warning', 1500);
	}
}
function _hightlight(notify) {
	if (!inited) {
		return;
	}
	if (isClosed()) {
		$('.control.block', activities).addClass('bg-warning');
		if (notify) {
			OmUtil.notify(newActNotification, 'new_aa_item');
		}
	}
}
function _getId(id) {
	return 'activity-' + id;
}
function _action(name, val) {
	activityAction($('.room-block .room-container').data('room-id'), name, val);
}
function _remove(ids) {
	for (let i = 0; i < ids.length; ++i) {
		$('#' + _getId(ids[i])).remove();
	}
	_updateCount();
}
function _clearItem(id) {
	if (aclean.prop('checked')) {
		_remove([id]);
	}
}
function _updateCount() {
	if (!inited) {
		return;
	}
	$('.control.block .badge', activities).text(modArea.find('.activity').length);
}

module.exports = {
	init: function() {
		if ($('#activities').resizable("instance") !== undefined) {
			return;
		}
		activities = $('#activities');
		const ctrlBlk = activities.find('.control.block');
		ctrlBlk.off().click(Activities.toggle);
		newActNotification = ctrlBlk.data('new-aa');
		activities.resizable({
			handles: 'n'
			, disabled: isClosed()
			, minHeight: 195
			, stop: function(_, ui) {
				openedHeight = ui.size.height;
				openedHeightPx = openedHeight + 'px';
				_updateHeightVar(openedHeightPx);
				activities.css({'top': ''});
			}
		});
		modArea = activities.find('.area .actions');
		area = activities.find('.area .activities');
		aclean = $('#activity-auto-clean');
		aclean.change(function() {
			const s = _load();
			s.activity.clean = $(this).prop('checked');
			Settings.save(s);
			_updateClean(s, $(this));
		});
		_updateClean(_load(), aclean);
		inited = true;
		_updateCount();
	}
	, toggle: function() {
		if (!inited) {
			return;
		}
		if (isClosed()) {
			_open();
		} else {
			_close();
		}
	}
	, findUser: _findUser
	, add: function(obj) {
		if (!inited) {
			return;
		}
		const _id = _getId(obj.id);
		(obj.action ? modArea : area).append(OmUtil.tmpl('#activity-stub', _id).data(obj));
		const a = $('#' + _id).addClass(obj.cssClass);
		const acpt = a.find('.activity-accept');
		if (obj.accept) {
			acpt.click(function() { _action('accept', obj.id); });
		} else {
			acpt.remove();
		}
		const dcln = a.find('.activity-decline');
		if (obj.decline) {
			dcln.click(function() { _action('decline', obj.id); });
		} else {
			dcln.remove();
		}
		const fnd = a.find('.activity-find');
		if (obj.find) {
			fnd.click(function() { _findUser(obj.uid); });
		} else {
			fnd.remove();
		}
		a.find('.activity-close').click(function() {
			_action('close', obj.id);
		});
		a.find('.activity-text').text(obj.text);
		_hightlight(obj.action);
		if (aclean.prop('checked') && a.hasClass('auto-clean')) {
			setTimeout(_clearItem.bind(null, obj.id), timeout);
		}
		_updateCount();
	}
	, remove: _remove
};
