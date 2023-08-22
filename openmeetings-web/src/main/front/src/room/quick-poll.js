/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const UserListUtil = require('./user-list-util');

function _setQuickPollRights() {
	const close = $('#quick-vote .close-btn');
	if (close.length === 1) {
		if (UserListUtil.hasRight(['PRESENTER'])) {
			close.show();
			if (typeof(close.data('bs.confirmation')) === 'object') {
				return;
			}
			close
				.confirmation({
					confirmationEvent: 'bla'
					, onConfirm: () => quickPollAction('close')
				});
		} else {
			close.hide();
		}
	}
}
function _update(obj) {
	if (obj.started) {
		let qv = $('#quick-vote');
		if (qv.length === 0) {
			const wbArea = $('.room-block .wb-block');
			qv = OmUtil.tmpl('#quick-vote-template', 'quick-vote');
			qv.attr('class', 'end-0');
			wbArea.append(qv);
		}
		const pro = qv.find('.control.pro')
			, con = qv.find('.control.con');
		if (obj.voted) {
			pro.removeClass('clickable').off();
			con.removeClass('clickable').off();
		} else {
			pro.addClass('clickable').off().click(function() {
				quickPollAction('vote', true);
			});
			con.addClass('clickable').off().click(function() {
				quickPollAction('vote', false);
			});
		}
		pro.find('.badge').text(obj.pros);
		con.find('.badge').text(obj.cons);
		_setQuickPollRights();
	} else {
		const qv = $('#quick-vote');
		if (qv.length === 1) {
			qv.remove();
		}
	}
	OmUtil.tmpl('#quick-vote-template', 'quick-vote');
}

module.exports = {
	update: _update
	, setRights: _setQuickPollRights
};