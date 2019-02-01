/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Whiteout = function(wb, s) {
	const wout = ShapeBase(wb);
	wout.fill.color = '#FFFFFF';
	wout.stroke.color = '#FFFFFF';
	wout.stroke.width = 25;
	wout.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.isDrawingMode = true;
			canvas.freeDrawingBrush.width = wout.stroke.width;
			canvas.freeDrawingBrush.color = wout.stroke.color;
			canvas.freeDrawingBrush.opacity = wout.opacity; //TODO not working
		});
		ToolUtil.disableAllProps(s);
	};
	wout.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.isDrawingMode = false;
		});
	};
	return wout;
};
