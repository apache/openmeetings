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
			ToolUtil.disableAllProps(s);
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
