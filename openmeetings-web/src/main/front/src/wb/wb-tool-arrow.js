/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const Line = require('./wb-tool-line');
const ToolUtil = require('./wb-tool-util');

module.exports = class Arrow extends Line {
	constructor(wb, settings, sBtn) {
		super(wb, settings, sBtn)
		this.stroke.width = 20;

		this.internalActivate = () => {
			ToolUtil.enableAllProps(settings, this);
		};
	}

	createShape() {
		this.obj = new fabric.Polygon([
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0}]
			, {
				left: this.orig.x
				, top: this.orig.y
				, angle: 0
				, strokeWidth: 2
				, fill: this.fill.enabled ? this.fill.color : ToolUtil.noColor
				, stroke: this.stroke.enabled ? this.stroke.color : ToolUtil.noColor
				, opacity: this.opacity
				, omType: 'arrow'
			});
	}

	updateShape(pointer) {
		const dx = pointer.x - this.orig.x
			, dy = pointer.y - this.orig.y
			, d = Math.sqrt(dx * dx + dy * dy)
			, sw = this.stroke.width
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
		this.obj.set({
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
	}
};
