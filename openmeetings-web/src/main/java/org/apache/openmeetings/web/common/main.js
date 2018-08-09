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
	let options, errs;
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

	self.confirmDlg = _confirmDlg;
	self.tmpl = _tmpl;
	self.debugEnabled = _debugEnabled;
	self.enableDebug = function() { if (!!options) { options.debug = true; } };
	self.sendMessage = function(m) {
		const msg = JSON.stringify(m || {});
		Wicket.WebSocket.send(msg);
	};
	self.initErrs = function(_e) { errs = _e; };
	self.error = _error;
	self.info = _info;
	self.log = _log;
	self.isIe = function() {
		return /Trident\/|MSIE/.test(window.navigator.userAgent);
	};
	return self;
})();
Wicket.BrowserInfo.collectExtraInfo = function(info) {
	const l = window.location;
	info.codebase = l.origin + l.pathname;
	info.settings = Settings.load();
};
//https://tc39.github.io/ecma262/#sec-array.prototype.includes
if (!Array.prototype.includes) {
	Object.defineProperty(Array.prototype, 'includes', {
		value: function(searchElement, fromIndex) {
			if (this == null) {
				throw new TypeError('"this" is null or not defined');
			}

			// 1. Let O be ? ToObject(this value).
			const o = Object(this);

			// 2. Let len be ? ToLength(? Get(O, "length")).
			const len = o.length >>> 0;

			// 3. If len is 0, return false.
			if (len === 0) {
				return false;
			}

			// 4. Let n be ? ToInteger(fromIndex).
			//    (If fromIndex is undefined, this step produces the value 0.)
			const n = fromIndex | 0;

			// 5. If n ≥ 0, then
			//  a. Let k be n.
			// 6. Else n < 0,
			//  a. Let k be len + n.
			//  b. If k < 0, let k be 0.
			let k = Math.max(n >= 0 ? n : len - Math.abs(n), 0);

			function sameValueZero(x, y) {
				return x === y || (typeof x === 'number' && typeof y === 'number' && isNaN(x) && isNaN(y));
			}

			// 7. Repeat, while k < len
			while (k < len) {
				// a. Let elementK be the result of ? Get(O, ! ToString(k)).
				// b. If SameValueZero(searchElement, elementK) is true, return true.
				if (sameValueZero(o[k], searchElement)) {
					return true;
				}
				// c. Increase k by 1.
				k++;
			}

			// 8. Return false
			return false;
		}
	});
}
