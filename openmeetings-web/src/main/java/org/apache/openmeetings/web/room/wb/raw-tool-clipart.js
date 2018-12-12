/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
if (!String.prototype.endsWith) {
	String.prototype.endsWith = function(search, this_len) {
		if (this_len === undefined || this_len > this.length) {
			this_len = this.length;
		}
		return this.substring(this_len - search.length, this_len) === search;
	};
}
var Clipart = function(wb, btn, s) {
	const art = Shape(wb);
	art.add2Canvas = function(canvas) {}
	art.createShape = function(canvas) {
		const imgSrc = btn.data('image')
			, opts = {
				left: art.orig.x
				, top: art.orig.y
				, scaleX: 0.
				, scaleY: 0.
				, omType: 'Clipart'
				, _src: imgSrc
				, opacity: art.opacity
			};
		if (imgSrc.toLowerCase().endsWith('svg')) {
			fabric.loadSVGFromURL(imgSrc, function(elements) {
				art.orig.width = 32;
				art.orig.height = 32;
				art.obj = fabric.util.groupSVGElements(elements, opts);
				canvas.add(art.obj);
			});
		} else {
			fabric.Image.fromURL(imgSrc, function(img) {
				art.orig.width = img.width;
				art.orig.height = img.height;
				art.obj = img.set(opts);
				canvas.add(art.obj);
			});
		}
	};
	art.updateShape = function(pointer) {
		if (!art.obj) {
			return; // not ready
		}
		const dx = pointer.x - art.orig.x
			, dy = pointer.y - art.orig.y
			, d = Math.sqrt(dx * dx + dy * dy)
			, scale = d / art.obj.width;
		art.obj.set({
			scaleX: scale
			, scaleY: scale
			, angle: Math.atan2(dy, dx) * 180 / Math.PI
		});
	};
	art.internalActivate = function() {
		ToolUtil.disableAllProps(s);
		ToolUtil.enableOpacity(s, art);
	};
	return art;
};
