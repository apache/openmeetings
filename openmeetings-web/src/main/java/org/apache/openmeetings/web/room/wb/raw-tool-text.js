/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Text = function(wb, s) {
	const text = ShapeBase();
	text.obj = null;
	text.fill.color = '#000000';
	text.stroke.width = 50; //fontSize
	text.stroke.color = '#000000';
	text.style = {bold: false, italic: false};

	text.mouseDown = function(o) {
		const canvas = this
			, pointer = canvas.getPointer(o.e)
			, ao = canvas.getActiveObject();
		if (!!ao && ao.type === 'i-text') {
			text.obj = ao;
		} else {
			text.obj = new fabric.IText('', {
				left: pointer.x
				, top: pointer.y
				, padding: 7
				, fill: text.fill.enabled ? text.fill.color : 'rgba(0,0,0,0)'
				, stroke: text.stroke.enabled ? text.stroke.color : 'rgba(0,0,0,0)'
				//, strokeWidth: text.stroke.width
				, fontSize: text.stroke.width
				, fontFamily: text.fontFamily
				, opacity: text.opacity
			});
			if (text.style.bold) {
				text.obj.fontWeight = 'bold'
			}
			if (text.style.italic) {
				text.obj.fontStyle = 'italic'
			}
			canvas.add(text.obj).setActiveObject(text.obj);
		}
		text.obj.enterEditing();
	};
	text.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on('mouse:down', text.mouseDown);
			canvas.selection = true;
			canvas.forEachObject(function(o) {
				if (o.type === 'i-text') {
					o.selectable = true;
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
		WbArea.removeDeleteHandler();
	};
	text.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:down', text.mouseDown);
			canvas.selection = false;
			canvas.forEachObject(function(o) {
				if (o.type === 'i-text') {
					o.selectable = false;
				}
			});
		});
		WbArea.addDeleteHandler();
	};
	return text;
};
