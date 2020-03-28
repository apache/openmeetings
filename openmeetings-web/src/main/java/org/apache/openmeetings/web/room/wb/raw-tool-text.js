/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Text = function(wb, s, sBtn) {
	const text = ShapeBase();
	text.obj = null;
	text.omType = 'i-text';
	text.fill.color = '#000000';
	text.stroke.enabled = false;
	text.stroke.width = 50; //fontSize
	text.stroke.color = '#000000';
	text.style = {bold: false, italic: false};

	function __valid(o) {
		return !!o && text.omType === o.omType;
	}
	function __getObj(canvas, o) {
		if (__valid(o)) {
			return o;
		} else {
			const _o = canvas.getActiveObject();
			return __valid(_o) ? _o : null;
		}
	}
	text.createTextObj = function(canvas, pointer) {
		return new fabric.IText('', {
			left: pointer.x
			, top: pointer.y
			, padding: 7
			, omType: text.omType
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
			, ao = __getObj(canvas, o.target);
		if (!!ao) {
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
	text._editable = function(o) {
		return text.omType === o.omType;
	}
	text.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on('mouse:down', text.mouseDown);
			canvas.on('mouse:dblclick', text.doubleClick);
			canvas.selection = true;
			canvas.forEachObject(function(o) {
				if (text._editable(o)) {
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
		VideoUtil.highlight(sBtn.removeClass('disabled'), 'bg-warning', 5);
	};
	text.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:down', text.mouseDown);
			canvas.off('mouse:dblclick', text.doubleClick);
			canvas.selection = false;
			canvas.forEachObject(function(o) {
				if (text.omType === o.omType) {
					o.selectable = false;
					o.editable = false;
				}
			});
		});
		text._onDeactivate();
	};
	return text;
};
