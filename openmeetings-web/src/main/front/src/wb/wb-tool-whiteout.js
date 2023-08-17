/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbShapeBase = require('./wb-tool-shape-base');
const ToolUtil = require('./wb-tool-util');

module.exports = class Whiteout extends WbShapeBase {
	constructor(wb, s, sBtn) {
		super();
		this.fill.color = '#FFFFFF';
		Object.assign(this.stroke, {
			color: '#FFFFFF'
			, width: 25
		});

		const self = this;
		this.activate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.isDrawingMode = true;
				canvas.freeDrawingBrush.width = self.stroke.width;
				canvas.freeDrawingBrush.color = self.stroke.color;
				canvas.freeDrawingBrush.opacity = self.opacity;
			});
			ToolUtil.disableAllProps(s);
			sBtn.addClass('disabled');
		};
		this.deactivate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.isDrawingMode = false;
			});
		};
	}
};
