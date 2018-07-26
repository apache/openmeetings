/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Arrow = function(wb, s) {
	const arrow = Line(wb, s);
	arrow.stroke.width = 20;
	arrow.createShape = function(canvas) {
		arrow.obj = new fabric.Polygon([
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0}]
			, {
				left: arrow.orig.x
				, top: arrow.orig.y
				, angle: 0
				, strokeWidth: 2
				, fill: arrow.fill.enabled ? arrow.fill.color : 'rgba(0,0,0,0)'
				, stroke: arrow.stroke.enabled ? arrow.stroke.color : 'rgba(0,0,0,0)'
				, opacity: arrow.opacity
			});

		return arrow.obj;
	};
	arrow.updateShape = function(pointer) {
		const dx = pointer.x - arrow.orig.x
			, dy = pointer.y - arrow.orig.y
			, d = Math.sqrt(dx * dx + dy * dy)
			, sw = arrow.stroke.width
			, hl = sw * 3
			, h = 1.5 * sw
			, points = [
				{x: 0, y: sw},
				{x: Math.max(0, d - hl), y: sw},
				{x: Math.max(0, d - hl), y: h},
				{x: d, y: 3 * sw / 4},
				{x: Math.max(0, d - hl), y: 0},
				{x: Math.max(0, d - hl), y: sw / 2},
				{x: 0, y: sw / 2}];
		arrow.obj.set({
			points: points
			, angle: Math.atan2(dy, dx) * 180 / Math.PI
			, width: d
			, height: h
			, maxX: d
			, maxY: h
			, pathOffset: {
				x: d / 2,
				y: h / 2
			}
		});
	};
	arrow.internalActivate = function() {
		ToolUtil.enableAllProps(s, arrow);
	};
	return arrow;
};
