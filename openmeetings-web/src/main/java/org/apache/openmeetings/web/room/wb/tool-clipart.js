/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Clipart = function(wb, btn, s) {
	const art = Shape(wb);
	art.add2Canvas = function(canvas) {}
	art.createShape = function(canvas) {
		const imgSrc = btn.data('image');
		fabric.Image.fromURL(imgSrc, function(img) {
			art.orig.width = img.width;
			art.orig.height = img.height;
			art.obj = img.set({
				left: art.orig.x
				, top: art.orig.y
				, width: 0
				, height: 0
				, omType: 'Clipart'
				, _src: imgSrc
			});
			canvas.add(art.obj);
		});
	};
	art.updateShape = function(pointer) {
		if (!art.obj) {
			return; // not ready
		}
		const dx = pointer.x - art.orig.x, dy = pointer.y - art.orig.y
			, d = Math.sqrt(dx * dx + dy * dy);
		art.obj.set({
			width: d
			, height: art.orig.height * d / art.orig.width
			, angle: Math.atan2(dy, dx) * 180 / Math.PI
		});
	};
	art.internalActivate = function() {
		ToolUtil.disableAllProps(s);
	};
	return art;
};
