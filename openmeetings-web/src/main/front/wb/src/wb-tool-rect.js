/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbShape = require('./wb-tool-shape');
const ToolUtil = require('./wb-tool-util');

module.exports = class Rect extends WbShape {
	constructor(wb, settings, sBtn) {
		super(wb, sBtn);

		this.internalActivate = () => {
			ToolUtil.enableAllProps(settings, this);
		};
	}

	createShape() {
		this.obj = new fabric.Rect({
			strokeWidth: this.stroke.width
			, fill: this.fill.enabled ? this.fill.color : ToolUtil.noColor
			, stroke: this.stroke.enabled ? this.stroke.color : ToolUtil.noColor
			, opacity: this.opacity
			, left: this.orig.x
			, top: this.orig.y
			, width: 0
			, height: 0
			, omType: 'rect'
		});
	}

	updateShape(pointer) {
		if (this.orig.x > pointer.x) {
			this.obj.set({ left: pointer.x });
		}
		if (this.orig.y > pointer.y) {
			this.obj.set({ top: pointer.y });
		}
		this.obj.set({
			width: Math.abs(this.orig.x - pointer.x)
			, height: Math.abs(this.orig.y - pointer.y)
		});
	}
};
