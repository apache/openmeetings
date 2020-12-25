/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbShape = require('./wb-tool-shape');
const ToolUtil = require('./wb-tool-util');
require('fabric');

module.exports = class Clipart extends WbShape {
	constructor(wb, btn, settings, sBtn) {
		super(wb, sBtn);

		const self = this;
		this.createShape = (canvas) => {
			const imgSrc = btn.data('image')
				, opts = {
					left: self.orig.x
					, top: self.orig.y
					, scaleX: 0.
					, scaleY: 0.
					, omType: 'Clipart'
					, _src: imgSrc
					, opacity: self.opacity
				};
			if (imgSrc.toLowerCase().endsWith('svg')) {
				fabric.loadSVGFromURL(imgSrc, function(elements) {
					self.orig.width = 32;
					self.orig.height = 32;
					self.obj = fabric.util.groupSVGElements(elements, opts);
					self.obj.set(opts);
					canvas.add(self.obj);
				});
			} else {
				fabric.Image.fromURL(imgSrc, function(img) {
					self.orig.width = img.width;
					self.orig.height = img.height;
					self.obj = img.set(opts);
					canvas.add(self.obj);
				});
			}
		};
		this.internalActivate = () => {
			ToolUtil.disableAllProps(settings);
			ToolUtil.enableOpacity(settings, this);
		};
	}

	add2Canvas() {}

	updateShape(pointer) {
		if (!this.obj) {
			return; // not ready
		}
		const dx = pointer.x - this.orig.x
			, dy = pointer.y - this.orig.y
			, d = Math.sqrt(dx * dx + dy * dy)
			, scale = d / this.obj.width;
		this.obj.set({
			scaleX: scale
			, scaleY: scale
			, angle: Math.atan2(dy, dx) * 180 / Math.PI
		});
	};
};
