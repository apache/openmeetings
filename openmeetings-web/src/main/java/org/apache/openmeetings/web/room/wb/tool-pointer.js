/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Pointer = function(wb, s) {
	return {
		activate: function() {
			wb.eachCanvas(function(canvas) {
				canvas.selection = true;
				canvas.forEachObject(function(o) {
					o.selectable = true;
				});
			});
			s.find('[class^="wb-prop"]').prop('disabled', true);
			if (!!s.find('.wb-prop-b').button("instance")) {
				s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
			}
		}
		, deactivate: function() {
			wb.eachCanvas(function(canvas) {
				canvas.selection = false;
				canvas.forEachObject(function(o) {
					o.selectable = false;
				});
			});
		}
	};
};
