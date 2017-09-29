/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Base = function() {
	const base = {};
	base.objectCreated = function(o, canvas) {
		o.uid = UUID.generate();
		o.slide = canvas.slide;
		canvas.trigger("wb:object:created", o);
		return o.uid;
	}
	return base;
};
