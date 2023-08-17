/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbShape = require('./wb-tool-shape');
const ToolUtil = require('./wb-tool-util');

module.exports = class Line extends WbShape {
	constructor(wb, settings, sBtn) {
		super(wb, sBtn);

		this.internalActivate = () => {
			ToolUtil.enableLineProps(settings, this);
		};
	}

	createShape() {
		this.obj = new fabric.Line([this.orig.x, this.orig.y, this.orig.x, this.orig.y], {
			strokeWidth: this.stroke.width
			, fill: this.stroke.color
			, stroke: this.stroke.color
			, opacity: this.opacity
			, omType: 'line'
		});
	}

	updateShape(pointer) {
		this.obj.set({ x2: pointer.x, y2: pointer.y });
	}
};
