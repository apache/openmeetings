/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const ToolUtil = require('./wb-tool-util');

module.exports = class Pointer {
	constructor(wb, s, sBtn) {
		this.activate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.selection = true;
				canvas.forEachObject(function(o) {
					o.selectable = true;
				});
			});
			ToolUtil.disableAllProps(s);
			sBtn.addClass('disabled');
		};
		this.deactivate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.selection = false;
				canvas.forEachObject(function(o) {
					o.selectable = false;
				});
			});
		}
	}
};
