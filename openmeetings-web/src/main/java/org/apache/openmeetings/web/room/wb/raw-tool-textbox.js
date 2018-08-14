/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Textbox = function(wb, s) {
	const text = Text(wb, s);
	text.fabricType = 'textbox';

	text.createTextObj = function(canvas, pointer) {
		return new fabric.Textbox('', {
			left: pointer.x
			, top: pointer.y
			, padding: 7
			, fill: text.fill.enabled ? text.fill.color : 'rgba(0,0,0,0)'
			, stroke: text.stroke.enabled ? text.stroke.color : 'rgba(0,0,0,0)'
			//, strokeWidth: text.stroke.width
			, fontSize: text.stroke.width
			, fontFamily: text.fontFamily
			, opacity: text.opacity
			, breakWords: true
			, width: canvas.width / 4
			, lockScalingX: false
			, lockScalingY: true
		});
	};
	text.doubleClick = function(e) {
		const canvas = this
			, ao = e.target;
		if (!!ao && text.fabricType === ao.type) {
			text.obj = ao;
			text.obj.enterEditing();
			WbArea.removeDeleteHandler();
		}
	};
	text._onMouseDown = function() {
		WbArea.addDeleteHandler();
	};
	text._onActivate = function() {
		WbArea.addDeleteHandler();
	};
	text._onDeactivate = function() {
		WbArea.addDeleteHandler();
	};
	return text;
};
