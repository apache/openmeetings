/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Text = function(wb, s) {
	let text = ShapeBase();
	text.obj = null;
	text.fill.color = '#000000';
	text.stroke.width = 1;
	text.stroke.color = '#000000';
	text.style = {bold: false, italic: false};
	//TODO font size, background color

	text.mouseDown = function(o) {
		var canvas = this;
		var pointer = canvas.getPointer(o.e);
		var ao = canvas.getActiveObject();
		if (!!ao && ao.type === 'i-text') {
			text.obj = ao;
		} else {
			text.obj = new fabric.IText('', {
				left: pointer.x
				, top: pointer.y
				, padding: 7
				, fill: text.fill.enabled ? text.fill.color : 'rgba(0,0,0,0)'
				, stroke: text.stroke.enabled ? text.stroke.color : 'rgba(0,0,0,0)'
				, strokeWidth: text.stroke.width
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
		text.enableAllProps(s);
		var b = s.find('.wb-prop-b').button("enable");
		if (text.style.bold) {
			b.addClass('ui-state-active selected');
		} else {
			b.removeClass('ui-state-active selected');
		}
		var i = s.find('.wb-prop-i').button("enable");
		if (text.style.italic) {
			i.addClass('ui-state-active selected');
		} else {
			i.removeClass('ui-state-active selected');
		}
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
	};
	return text;
};
