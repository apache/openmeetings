/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var ShapeBase = function() {
	const base = Base();
	base.fill = {enabled: true, color: '#FFFF33'};
	base.stroke = {enabled: true, color: '#FF6600', width: 5};
	base.opacity = 1;
	base.enableLineProps = function(s) {
		const c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width'), o = s.find('.wb-prop-opacity');
		s.find('.wb-prop-fill').prop('disabled', true);
		s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
		c.val(base.stroke.color).prop('disabled', false);
		w.val(base.stroke.width).prop('disabled', false);
		o.val(100 * base.opacity).prop('disabled', false);
		return {c: c, w: w, o: o};
	};
	base.enableAllProps = function(s) {
		const c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width')
			, o = s.find('.wb-prop-opacity'), f = s.find('.wb-prop-fill')
			, lc = s.find('.wb-prop-lock-color'), lf = s.find('.wb-prop-lock-fill');
		s.find('.wb-prop-b, .wb-prop-i').button("disable");
		lc.button("enable").button('option', 'icon', base.stroke.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		lf.button("enable").button('option', 'icon', base.fill.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		c.val(base.stroke.color).prop('disabled', !base.stroke.enabled);
		w.val(base.stroke.width).prop('disabled', false);
		o.val(100 * base.opacity).prop('disabled', false);
		f.val(base.fill.color).prop('disabled', !base.fill.enabled);
	};
	return base;
};
