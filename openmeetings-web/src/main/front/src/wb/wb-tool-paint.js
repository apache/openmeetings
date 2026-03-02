/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const VideoUtil = require('../settings/video-util');

import { WbShapeBase } from './wb-tool-shape-base';
import { ToolUtil } from './wb-tool-util';

export class Paint extends WbShapeBase {
	constructor(wb, s, sBtn) {
		super();

		const self = this;
		this.activate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.isDrawingMode = true;
				canvas.freeDrawingBrush.width = self.stroke.width;
				canvas.freeDrawingBrush.color = self.stroke.color;
				canvas.freeDrawingBrush.opacity = self.opacity;
			});
			ToolUtil.enableLineProps(s, self).o.prop('disabled', true);
			VideoUtil.highlight(sBtn.removeClass('disabled'), 'bg-warning', 5);
		};
		this.deactivate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.isDrawingMode = false;
			});
		};
	}
};
