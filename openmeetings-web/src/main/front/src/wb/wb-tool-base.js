/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
module.exports = class WbToolBase {
	objectCreated(o, canvas) {
		o.uid = crypto.randomUUID();
		o.slide = canvas.slide;
		canvas.fire("wb:object:created", o);
		return o.uid;
	}
};
