/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
let options, alertId = 0;

function _init(_options) {
	options = _options;
}
function _tmpl(tmplId, newId) {
	return $(tmplId).clone().attr('id', newId || '');
}
function __alert(level, msg, autohideAfter) {
	const holder = $('#alert-holder');
	const curId = 'om-alert' + alertId++;
	holder.append($(`<div id="${curId}" class="alert alert-${level} alert-dismissible fade show m-0" role="alert">${msg}
			<button type="button" class="close" data-dismiss="alert" aria-label="${holder.data('lbl-close')}">
				<span aria-hidden="true">&times;</span>
			</button>
		</div>`));
	if (autohideAfter > 0) {
		setTimeout(() => { $(`#${curId}`).alert('close');}, autohideAfter);
	}
}
function _error(msg, noAlert) {
	if (typeof(msg) === 'object') {
		msg = msg.name + ': ' + msg.message;
	}
	if (noAlert !== true) {
		__alert('danger', msg, 20000);
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
function _sendMessage(_m, _base) {
	const base = _base || {}
		, m = _m || {}
		, msg = JSON.stringify($.extend({}, base, m));
	Wicket.WebSocket.send(msg);
}
function _requestNotifyPermission(callback, elseCallback) {
	if (Notification.permission !== 'granted' && Notification.permission !== 'denied') {
		Notification.requestPermission().then(permission => {
			if (permission === 'granted') {
				callback();
			}
		});
	} else if (typeof(elseCallback) === 'function') {
		elseCallback();
	}
}
function _notify(msg, tag, elseCallback) {
	if (window === window.parent) {
		function _newMessage() {
			const opts = {
					tag: tag
				};
			new Notification(msg, opts);
		}
		if (Notification.permission === 'granted') {
			_newMessage();
		} else {
			_requestNotifyPermission(() => _newMessage());
		}
	} else if (typeof(elseCallback) === 'function') {
		elseCallback();
	}
}

module.exports = {
	init: _init
	, tmpl: _tmpl
	, debugEnabled: _debugEnabled
	, enableDebug: function() {
		if (!!options) {
			options.debug = true;
		}
	}
	, sendMessage: _sendMessage
	, alert: __alert
	, error: _error
	, info: _info
	, log: _log
	, wbAction: function(_m) {
		_sendMessage(_m, {area: 'room', type: 'wb'});
	}
	, roomAction: function(_m) {
		_sendMessage(_m, {area: 'room', type: 'room'});
	}
	, setCssVar: function(key, val) {
		($('body')[0]).style.setProperty(key, val);
	}
	, ping: function() {
		setTimeout(() => _sendMessage({type: 'ping'}), 30000);
	}
	, notify: _notify
	, requestNotifyPermission: _requestNotifyPermission
};
