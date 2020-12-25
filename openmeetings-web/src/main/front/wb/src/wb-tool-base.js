/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
module.exports = class WbToolBase {
	objectCreated(o, canvas) {
		this.uid = uuidv4();
		this.slide = canvas.slide;
		canvas.trigger("wb:object:created", o);
		return o.uid;
	}
};
