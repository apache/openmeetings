/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
export class ToolUtil {
	static enableLineProps(s, base) {
		const c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width'), o = this.enableOpacity(s, base);
		s.find('.wb-prop-fill').prop('disabled', true);
		s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
		c.val(base.stroke.color).prop('disabled', false);
		w.val(base.stroke.width).prop('disabled', false);
		return {c: c, w: w, o: o};
	}

	static enableAllProps(s, base) {
		const c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width')
			, f = s.find('.wb-prop-fill')
			, lc = s.find('.wb-prop-lock-color'), lf = s.find('.wb-prop-lock-fill');
		this.enableOpacity(s, base);
		s.find('.wb-prop-b, .wb-prop-i').button("disable");
		lc.button("enable").button('option', 'icon', base.stroke.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		lf.button("enable").button('option', 'icon', base.fill.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		c.val(base.stroke.color).prop('disabled', !base.stroke.enabled);
		w.val(base.stroke.width).prop('disabled', false);
		f.val(base.fill.color).prop('disabled', !base.fill.enabled);
	}

	static enableOpacity(s, base) {
		const o = s.find('.wb-prop-opacity')
		o.val(100 * base.opacity).prop('disabled', false);
		return o;
	}

	static disableAllProps(s) {
		s.find('[class^="wb-prop"]').prop('disabled', true);
		if (!!s.find('.wb-prop-b').button("instance")) {
			s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
		}
	}

	static addDeletedItem(canvas, o) {
		if ("Presentation" === o.fileType) {
			fabric.FabricImage.fromURL(o._src)
			.then(img => {
				const sz = img.getOriginalSize();
				img.width = sz.width;
				img.height = sz.height;
				img.scaleX = img.scaleY = canvas.width / (canvas.getZoom() * sz.width);
				canvas.setBackgroundImage(img, canvas.renderAll.bind(canvas));
			});
		} else {
			fabric.FabricImage.fromURL(o._src || o.src)
			.then(img => {
				const sz = img.getOriginalSize();
				img.width = sz.width;
				img.height = sz.height;
				img.scaleX = img.scaleY = (o.scaleX || 1.) * o.width / sz.width;
				img.type = 'image';
				img.videoStatus = function() {};
				canvas.add(img);
				canvas.requestRenderAll();
			}, o);
		}
	}

	static filter(_o, props) {
		return props.reduce(function(result, key) {
			result[key] = _o[key];
			return result;
		}, {});
	}

	static noColor = 'rgba(0,0,0,0)';
};
