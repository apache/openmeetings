/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
module.exports = class WbAreaBase {
	constructor() {
		function _wbWsHandler(_, msg) {
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

		this.wsinit = () => {
			// it seems `super` can't be called from lambda
			Wicket.Event.subscribe("/websocket/message", _wbWsHandler);
		};
		this.wsdestroy = () => {
			// it seems `super` can't be called from lambda
			Wicket.Event.unsubscribe("/websocket/message", _wbWsHandler);
		};
		this.setRole = () => {};
		this.addDeleteHandler = () => {};
		this.removeDeleteHandler = () => {};
		this.resize = () => {};
	}
};
