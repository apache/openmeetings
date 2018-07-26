/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var NONE = 'none';
function wbWsHandler(jqEvent, msg) {
	try {
		if (msg instanceof Blob) {
			return; //ping
		}
		const m = jQuery.parseJSON(msg);
		if (m && 'wb' === m.type && typeof(WbArea) !== 'undefined' && !!m.func) {
			WbArea[m.func](m.param);
		}
	} catch (err) {
		//no-op
	}
}
