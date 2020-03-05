/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var NONE = 'NONE';
var BaseWbArea = function() {
	const self = {};

	function _wbWsHandler(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = JSON.parse(msg);
			if (m && 'wb' === m.type && typeof(WbArea) !== 'undefined' && !!m.func) {
				WbArea[m.func](m.param);
			}
		} catch (err) {
			//no-op
		}
	}

	self.wbWsHandler = _wbWsHandler;
	return self;
};
