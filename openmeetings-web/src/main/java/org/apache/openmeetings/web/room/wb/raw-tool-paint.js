/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Paint = function(wb, s, sBtn) {
	const paint = ShapeBase(wb);
	paint.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.isDrawingMode = true;
			canvas.freeDrawingBrush.width = paint.stroke.width;
			canvas.freeDrawingBrush.color = paint.stroke.color;
			canvas.freeDrawingBrush.opacity = paint.opacity;
		});
		ToolUtil.enableLineProps(s, paint).o.prop('disabled', true);
		VideoUtil.highlight(sBtn.removeClass('disabled'), 5);
	};
	paint.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.isDrawingMode = false;
		});
	};
	return paint;
};
