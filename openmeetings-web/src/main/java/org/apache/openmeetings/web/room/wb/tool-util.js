/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var ToolUtil = (function() {
	return {
		enableLineProps: function(s, base) {
			const c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width'), o = this.enableOpacity(s, base);
			s.find('.wb-prop-fill').prop('disabled', true);
			s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
			c.val(base.stroke.color).prop('disabled', false);
			w.val(base.stroke.width).prop('disabled', false);
			return {c: c, w: w, o: o};
		}
		, enableAllProps: function(s, base) {
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
		, enableOpacity: function(s, base) {
			const o = s.find('.wb-prop-opacity')
			o.val(100 * base.opacity).prop('disabled', false);
			return o;
		}
		, disableAllProps: function(s) {
			s.find('[class^="wb-prop"]').prop('disabled', true);
			if (!!s.find('.wb-prop-b').button("instance")) {
				s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
			}
		}
	};
})();
