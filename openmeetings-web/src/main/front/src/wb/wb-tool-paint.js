/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const VideoUtil = require('../settings/video-util');

const WbShapeBase = require('./wb-tool-shape-base');
const ToolUtil = require('./wb-tool-util');

module.exports = class Paint extends WbShapeBase {
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
