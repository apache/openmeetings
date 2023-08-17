/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const VideoUtil = require('../settings/video-util');

const WbShapeBase = require('./wb-tool-shape-base');

module.exports = class WbShape extends WbShapeBase {
	constructor(wb, sBtn) {
		super();
		Object.assign(this, {
			obj: null
			, isDown: false
			, orig: {x: 0, y: 0}
		});

		const self = this;
		function _mouseDown(o) {
			const canvas = this
				, pointer = canvas.getPointer(o.e);
			self.isDown = true;
			self.orig = {x: pointer.x, y: pointer.y};
			self.createShape.call(self, canvas);
			self.add2Canvas.call(self, canvas);
		};
		function _mouseMove(o) {
			const canvas = this;
			if (!self.isDown) {
				return;
			}
			const pointer = canvas.getPointer(o.e);
			self.updateShape.call(self, pointer);
			canvas.requestRenderAll();
		};
		function _mouseUp() {
			const canvas = this;
			self.isDown = false;
			self.obj.setCoords();
			self.obj.selectable = false;
			canvas.requestRenderAll();
			self.objectCreated(self.obj, canvas);
		};

		this.activate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.on({
					'mouse:down': _mouseDown
					, 'mouse:move': _mouseMove
					, 'mouse:up': _mouseUp
				});
			});
			this.internalActivate();
			VideoUtil.highlight(sBtn.removeClass('disabled'), 'bg-warning', 5);
		};
		this.deactivate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.off({
					'mouse:down': _mouseDown
					, 'mouse:move': _mouseMove
					, 'mouse:up': _mouseUp
				});
			});
		};
	}

	add2Canvas(canvas) {
		canvas.add(this.obj);
	}

	createShape() {}
	updateShape() {}
	internalActivate() {}
};
