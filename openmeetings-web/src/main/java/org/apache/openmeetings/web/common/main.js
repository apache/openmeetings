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
	let options, errs
	const self = {};

	function _init(_options) {
		options = _options;
	}
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
	function _error(msg) {
		if (typeof(msg) === 'object') {
			msg = msg.name + ": " + msg.message;
		}
		if (!!errs && errs.length > 0) {
			errs.getKendoNotification().error(msg);
		}
		return console.error(msg);
	}
	function _debugEnabled() {
		return !!options && !!options.debug;
	}
	function _info() {
		if (_debugEnabled()) {
			console.info.apply(this, arguments);
		}
	}
	function _log() {
		if (_debugEnabled()) {
			console.log.apply(this, arguments);
		}
	}

	self.init = _init;
	self.confirmDlg = _confirmDlg;
	self.tmpl = _tmpl;
	self.debugEnabled = _debugEnabled;
	self.enableDebug = function() {
		if (!!options) {
			options.debug = true;
		}
	};
	self.sendMessage = function(_m, _base) {
		const base = _base || {}
			, m = _m || {}
			, msg = JSON.stringify($.extend({}, base, m));
		Wicket.WebSocket.send(msg);
	};
	self.initErrs = function(_e) { errs = _e; };
	self.error = _error;
	self.info = _info;
	self.log = _log;
	return self;
})();
Wicket.BrowserInfo.collectExtraInfo = function(info) {
	const l = window.location;
	info.codebase = l.origin + l.pathname;
	info.settings = Settings.load();
};
