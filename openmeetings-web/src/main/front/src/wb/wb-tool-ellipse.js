/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
import { OmWbRect } from './wb-tool-rect';
import { ToolUtil } from './wb-tool-util';

import * as fabric from 'fabric';

export class Ellipse extends OmWbRect {
	constructor(wb, settings, sBtn) {
		super(wb, settings, sBtn);
	}

	createShape() {
		this.obj = new fabric.Ellipse({
			strokeWidth: this.stroke.width
			, fill: this.fill.enabled ? this.fill.color : ToolUtil.noColor
			, stroke: this.stroke.enabled ? this.stroke.color : ToolUtil.noColor
			, opacity: this.opacity
			, left: this.orig.x
			, top: this.orig.y
			, rx: 0
			, ry: 0
			, originX: 'center'
			, originY: 'center'
			, omType: 'ellipse'
		});
	}

	updateShape(pointer) {
		this.obj.set({
			rx: Math.abs(this.orig.x - pointer.x)
			, ry: Math.abs(this.orig.y - pointer.y)
		});
	}
};
