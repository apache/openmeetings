/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Line = function(wb, s) {
	const line = Shape(wb);
	line.createShape = function() {
		line.obj = new fabric.Line([line.orig.x, line.orig.y, line.orig.x, line.orig.y], {
			strokeWidth: line.stroke.width
			, fill: line.stroke.color
			, stroke: line.stroke.color
			, opacity: line.opacity
		});
		return line.obj;
	};
	line.internalActivate = function() {
		ToolUtil.enableLineProps(s, line);
	};
	line.updateShape = function(pointer) {
		line.obj.set({ x2: pointer.x, y2: pointer.y });
	};
	return line;
};
