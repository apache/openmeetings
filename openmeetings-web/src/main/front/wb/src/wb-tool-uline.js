/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const Line = require('./wb-tool-line');

module.exports = class ULine extends Line {
	constructor(wb, settings, sBtn) {
		super(wb, settings, sBtn);
		this.stroke.width = 20;
		this.opacity = .5;
	}
};
