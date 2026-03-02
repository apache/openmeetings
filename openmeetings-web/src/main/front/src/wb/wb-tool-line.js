/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
import { WbShape } from './wb-tool-shape';
import { ToolUtil } from './wb-tool-util';

import * as fabric from 'fabric';

export class Line extends WbShape {
	constructor(wb, settings, sBtn) {
		super(wb, sBtn);

		this.internalActivate = () => {
			ToolUtil.enableLineProps(settings, this);
		};
	}

	createShape() {
		// FIXME TODO `Polyline` doesn't work
		this.obj = new fabric.Line([this.orig.x, this.orig.y, this.orig.x, this.orig.y], {
					//new fabric.Polyline([{x: this.orig.x, y: this.orig.y}, {x: this.orig.x, y: this.orig.y}], {
			strokeWidth: this.stroke.width
			, fill: this.stroke.color
			, stroke: this.stroke.color
			, opacity: this.opacity
			, omType: 'line'
		});
	}

	updateShape(pointer) {
		this.obj.set({ x2: pointer.x, y2: pointer.y });
		//this.obj.points[1] = {x: pointer.x, y: pointer.y};
	}
};
