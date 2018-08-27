/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Text = function(wb, s) {
	const text = ShapeBase();
	text.obj = null;
	text.fabricType = 'i-text';
	text.fill.color = '#000000';
	text.stroke.enabled = false;
	text.stroke.width = 50; //fontSize
	text.stroke.color = '#000000';
	text.style = {bold: false, italic: false};

	text.createTextObj = function(canvas, pointer) {
		return new fabric.IText('', {
			left: pointer.x
			, top: pointer.y
			, padding: 7
			, fill: text.fill.enabled ? text.fill.color : 'rgba(0,0,0,0)'
			, stroke: text.stroke.enabled ? text.stroke.color : 'rgba(0,0,0,0)'
			, fontSize: text.stroke.width
			, fontFamily: text.fontFamily
			, opacity: text.opacity
		});
	};
	text._onMouseDown = function() {
		text.obj.enterEditing();
	};
	text._onActivate = function() {
		WbArea.removeDeleteHandler();
	};
	text._onDeactivate = function() {
		WbArea.addDeleteHandler();
	};
	text.mouseDown = function(o) {
		const canvas = this
			, pointer = canvas.getPointer(o.e)
			, ao = canvas.getActiveObject();
		if (!!ao && text.fabricType === ao.type) {
			text.obj = ao;
		} else {
			text.obj = text.createTextObj(canvas, pointer);
			if (text.style.bold) {
				text.obj.fontWeight = 'bold'
			}
			if (text.style.italic) {
				text.obj.fontStyle = 'italic'
			}
			canvas.add(text.obj).setActiveObject(text.obj);
		}
		text._onMouseDown();
	};
	text.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on('mouse:down', text.mouseDown);
			canvas.on('mouse:dblclick', text.doubleClick);
			canvas.selection = true;
			canvas.forEachObject(function(o) {
				if (text.fabricType === o.type) {
					o.selectable = true;
					o.editable = true;
				}
			});
		});
		text.fontFamily = $('#wb-text-style-block').css('font-family');
		ToolUtil.enableAllProps(s, text);
		const b = s.find('.wb-prop-b').button("enable");
		if (text.style.bold) {
			b.addClass('ui-state-active selected');
		} else {
			b.removeClass('ui-state-active selected');
		}
		const i = s.find('.wb-prop-i').button("enable");
		if (text.style.italic) {
			i.addClass('ui-state-active selected');
		} else {
			i.removeClass('ui-state-active selected');
		}
		text._onActivate();
	};
	text.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:down', text.mouseDown);
			canvas.off('mouse:dblclick', text.doubleClick);
			canvas.selection = false;
			canvas.forEachObject(function(o) {
				if (text.fabricType === o.type) {
					o.selectable = false;
					o.editable = false;
				}
			});
		});
		text._onDeactivate();
	};
	return text;
};
