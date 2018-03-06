/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Settings = (function() {
	const key = 'openmeetings';
	function _load() {
		let s = {};
		try {
			s = JSON.parse(localStorage.getItem(key)) || s;
		} catch (e) {
			//no-op
		}
		return s;
	}
	function _save(s) {
		const _s = JSON.stringify(s);
		localStorage.setItem(key, _s);
		return _s;
	}

	return {
		isRtl: "rtl" === $('html').attr('dir')
		, load: _load
		, save: _save
	};
})();
var OmUtil = (function() {
	const self = {};
	function _confirmDlg(_id, okHandler) {
		const confirm = $('#' + _id);
		confirm.dialog({
			modal: true
			, buttons: [
				{
					text: confirm.data('btn-ok')
					, click: function() {
						okHandler();
						$(this).dialog('close');
					}
				}
				, {
					text: confirm.data('btn-cancel')
					, click: function() {
						$(this).dialog('close');
					}
				}
			]
		});
		return confirm;
	}
	function _tmpl(tmplId, newId) {
		return $(tmplId).clone().attr('id', newId || '');
	}

	self.confirmDlg = _confirmDlg;
	self.tmpl = _tmpl;
	self.sendMessage = function(m) {
		const msg = JSON.stringify(m || {});
		Wicket.WebSocket.send(msg);
	};
	return self;
})();
Wicket.BrowserInfo.collectExtraInfo = function(info) {
	const l = window.location;
	info.codebase = l.origin + l.pathname;
	info.settings = Settings.load();
};
