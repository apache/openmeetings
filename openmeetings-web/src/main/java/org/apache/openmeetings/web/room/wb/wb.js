/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var Pointer = function(canvas, s) {
	return {
		activate: function() {
			canvas.selection = true;
			canvas.forEachObject(function(o) {
				o.selectable = true;
			});
			s.find('[class^="wb-prop"]').prop('disabled', true);
			if (!!s.find('.wb-prop-b').button("instance")) {
				s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
			}
		}
		, deactivate: function() {
			canvas.selection = false;
			canvas.forEachObject(function(o) {
				o.selectable = false;
			});
		}
	};
}
var Base = function() {
	return {
		fill: {enabled: true, color: '#FFFF33'}
		, stroke: {enabled: true, color: '#FF6600', width: 5}
		, opacity: 1
	};
}
var Text = function(canvas, s) {
	var text = Base();
	text.obj = null;

	text.mouseDown = function(o) {
		var pointer = canvas.getPointer(o.e);
		var ao = canvas.getActiveObject();
		if (!!ao && ao.type == 'i-text') {
			text.obj = ao;
		} else {
			text.obj = new fabric.IText('', {
				left: pointer.x
				, top: pointer.y
				, padding: 7
				, stroke: text.stroke.color
			});
			canvas.add(text.obj).setActiveObject(text.obj);
		}
		text.obj.enterEditing();
	};
	text.activate = function() {
		canvas.on('mouse:down', text.mouseDown);
		canvas.selection = true;
		canvas.forEachObject(function(o) {
			if (o.type == 'i-text') {
				o.selectable = true;
			}
		});
	};
	text.deactivate = function() {
		canvas.off('mouse:down', text.mouseDown);
		canvas.selection = false;
		canvas.forEachObject(function(o) {
			if (o.type == 'i-text') {
				o.selectable = false;
			}
		});
	};
	return text;
}
var Paint = function(canvas, s) {
	var paint = Base();
	paint.activate = function() {
		canvas.isDrawingMode = true;
		canvas.freeDrawingBrush.width = paint.stroke.width;
		canvas.freeDrawingBrush.color = paint.stroke.color;
		canvas.freeDrawingBrush.opacity = paint.opacity; //TODO not working

		var c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width'), o = s.find('.wb-prop-opacity');
		s.find('.wb-prop-fill').prop('disabled', true);
		s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
		c.val(paint.stroke.color).prop('disabled', false);
		w.val(paint.stroke.width).prop('disabled', false);
		o.val(100 * paint.opacity).prop('disabled', true); //TODO not working
	};
	paint.deactivate = function() {
		canvas.isDrawingMode = false;
	};
	return paint;
}
var Shape = function(canvas) {
	var shape = Base();
	shape.obj = null;
	shape.isDown = false;
	shape.orig = {x: 0, y: 0};

	shape.mouseDown = function(o) {
		shape.isDown = true;
		var pointer = canvas.getPointer(o.e);
		shape.orig = {x: pointer.x, y: pointer.y};
		canvas.add(shape.createShape());
	};
	shape.mouseMove = function(o) {
		if (!shape.isDown) return;
		var pointer = canvas.getPointer(o.e);
		shape.updateShape(pointer);
		canvas.renderAll();
	};
	shape.mouseUp = function(o) {
		shape.isDown = false;
		shape.obj.setCoords();
		shape.obj.selectable = false;
	};
	shape.internalActivate = function() {};
	shape.activate = function() {
		canvas.on('mouse:down', shape.mouseDown);
		canvas.on('mouse:move', shape.mouseMove);
		canvas.on('mouse:up', shape.mouseUp);
		shape.internalActivate();
	};
	shape.deactivate = function() {
		canvas.off('mouse:down', shape.mouseDown);
		canvas.off('mouse:move', shape.mouseMove);
		canvas.off('mouse:up', shape.mouseUup);
	};
	return shape;
};
var Line = function(canvas, s) {
	var line = Shape(canvas);
	line.createShape = function() {
		line.obj = new fabric.Line([line.orig.x, line.orig.y, line.orig.x, line.orig.y], {
			strokeWidth: line.stroke.width
			, fill: line.stroke.color
			, stroke: line.stroke.color
			, opacity: line.opacity
		});
		return line.obj;
	};
	line.internalActivate = function() {
		var c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width'), o = s.find('.wb-prop-opacity');
		s.find('.wb-prop-fill').prop('disabled', true);
		s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
		c.val(line.stroke.color).prop('disabled', false);
		w.val(line.stroke.width).prop('disabled', false);
		o.val(100 * line.opacity).prop('disabled', false);
	};
	line.updateShape = function(pointer) {
		line.obj.set({ x2: pointer.x, y2: pointer.y });
	};
	return line;
}
var Rect = function(canvas, s) {
	var rect = Shape(canvas);
	rect.createShape = function() {
		rect.obj = new fabric.Rect({
			strokeWidth: rect.stroke.width
			, fill: rect.fill.enabled ? rect.fill.color : 'rgba(0,0,0,0)'
			, stroke: rect.stroke.enabled ? rect.stroke.color : 'rgba(0,0,0,0)'
			, left: rect.orig.x
			, top: rect.orig.y
			, width: 0
			, height: 0
		});
		return rect.obj;
	};
	rect.internalActivate = function() {
		var c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width')
			, o = s.find('.wb-prop-opacity'), f = s.find('.wb-prop-fill')
			, lc = s.find('.wb-prop-lock-color'), lf = s.find('.wb-prop-lock-fill');
		s.find('.wb-prop-b, .wb-prop-i').button("disable");
		lc.button("enable").button('option', 'icon', rect.stroke.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		lf.button("enable").button('option', 'icon', rect.fill.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		c.val(rect.stroke.color).prop('disabled', !rect.stroke.enabled);
		w.val(rect.stroke.width).prop('disabled', false);
		o.val(100 * rect.opacity).prop('disabled', false);
		f.val(rect.fill.color).prop('disabled', !rect.fill.enabled);
	};
	rect.updateShape = function(pointer) {
		if (rect.orig.x > pointer.x) {
			rect.obj.set({ left: pointer.x });
		}
		if (rect.orig.y > pointer.y) {
			rect.obj.set({ top: pointer.y });
		}
		rect.obj.set({
			width: Math.abs(rect.orig.x - pointer.x)
			, height: Math.abs(rect.orig.y - pointer.y)
		});
	};
	return rect;
}
var Wb = function() {
	const ACTIVE = 'active';
	var a, t, s, canvas, mode;

	function getBtn() {
		return t.find(".om-icon." + mode);
	}
	function initToolBtn(m, def, obj) {
		var btn = t.find(".om-icon." + m);
		btn.data({
			obj: obj
			, activate: function() {
				if (!btn.hasClass(ACTIVE)) {
					mode = m;
					btn.addClass(ACTIVE);
					obj.activate();
				}
			}
			, deactivate: function() {
				btn.removeClass(ACTIVE);
				obj.deactivate();
			}
		}).click(function() {
			var b = getBtn();
			if (b.length && b.hasClass(ACTIVE)) {
				b.data('deactivate')();
			}
			btn.data('activate')();
		});
		if (def) {
			btn.data('activate')();
		}
	}
	function internalInit(t) {
		t.show().draggable({
			snap: "parent"
			, containment: "parent"
			, scroll: false
			, stop: function(event, ui) {
				var pos = ui.helper.position();
				if (pos.left == 0 || pos.left + ui.helper.width() == ui.helper.parent().width()) {
					ui.helper.removeClass('horisontal').addClass('vertical');
				} else if (pos.top == 0 || pos.top + ui.helper.height() == ui.helper.parent().height()) {
					ui.helper.removeClass('vertical').addClass('horisontal');
				}
				setRoomSizes(); // TODO should be better option
			}
		});
		initToolBtn('pointer', true, Pointer(canvas, s));
		initToolBtn('text', false, Text(canvas, s));
		initToolBtn('paint', false, Paint(canvas, s));
		initToolBtn('line', false, Line(canvas, s));
		initToolBtn('rect', false, Rect(canvas, s));
		t.find(".om-icon.settings").click(function() {
			s.show();
		});
		s.find('.wb-prop-b, .wb-prop-i')
			.button()
			.click(function() {
				$(this).toggleClass('ui-state-active selected');
			});
		s.find('.wb-prop-lock-color, .wb-prop-lock-fill')
			.button({icon: 'ui-icon-locked', showLabel: false})
			.click(function() {
				var btn = getBtn();
				var isColor = $(this).hasClass('wb-prop-lock-color');
				var c = s.find(isColor ? '.wb-prop-color' : '.wb-prop-fill');
				var enabled = $(this).button('option', 'icon') == 'ui-icon-locked';
				$(this).button('option', 'icon', enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
				c.prop('disabled', !enabled);
				btn.data('obj')[isColor ? 'stroke' : 'fill'].enabled = enabled;
			});
		s.find('.wb-prop-color').change(function() {
			var btn = getBtn();
			if (btn.length == 1) {
				var v = $(this).val();
				btn.data('obj').stroke.color = v;
				if ('paint' == mode) {
					canvas.freeDrawingBrush.color = v;
				}
			}
		});
		s.find('.wb-prop-width').change(function() {
			var btn = getBtn();
			if (btn.length == 1) {
				var v = 1 * $(this).val();
				btn.data('obj').stroke.width = v;
				if ('paint' == mode) {
					canvas.freeDrawingBrush.width = v;
				}
			}
		});
		s.find('.wb-prop-opacity').change(function() {
			var btn = getBtn();
			if (btn.length == 1) {
				var v = (1 * $(this).val()) / 100;
				btn.data('obj').opacity = v;
				if ('paint' == mode) {
					canvas.freeDrawingBrush.opacity = v;
				}
			}
		});
		s.find('.ui-dialog-titlebar-close').click(function() {
			s.hide();
		});
		s.draggable({
			scroll: false
			, containment: "body"
			, start: function(event, ui) {
				if (!!s.css("bottom")) {
					s.css("bottom", "").css("right", "");
					setRoomSizes(); // TODO should be better option
				}
			}
			, drag: function(event, ui) {
				if (s.position().x + s.width() >= s.parent().width()) {
					return false;
				}
			}
		});
		/* TODO
		$(window).keydown(function(e) {
			switch (e.keyCode) {
				case 46: // delete
					var obj = canvas.getActiveObject(); //TODO iterate canvas.getActiveGroup()
					canvas.remove(obj);
					canvas.renderAll();
					return false;
			}
			return; //using "return" other attached events will execute
		});
		*/
	}

	return {
		fabric: true
		, init: function(id) {
			a = $('#' + id);
			t = a.find('.tools'), c = a.find('canvas'), s = a.find(".wb-settings");
			c.attr('id', 'can-' + id);
			canvas = new fabric.Canvas(c.attr('id'));
			internalInit(t);
		}
		, resize: function(w, h) {
			if (t.position().left + t.width() > a.width()) {
				t.position({
					my: "right"
					, at: "right"
					, of: a.selector
					, collision: "fit"
				});
			}
			canvas.setWidth(w);
			canvas.setHeight(h);
		}
	};
};
function initArea(id) {
	var a = $('#' + id);
	a.data(Wb());
	a.data('init')(id);
}
