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
/**
 * Fast UUID generator, RFC4122 version 4 compliant.
 * @author Jeff Ward (jcward.com).
 * @license MIT license
 * @link http://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid-in-javascript/21963136#21963136
 **/
var UUID = (function() {
	var self = {};
	var lut = [];
	for (var i = 0; i < 256; i++) {
		lut[i] = (i < 16 ? '0' : '') + (i).toString(16);
	}
	self.generate = function() {
		var d0 = Math.random() * 0xffffffff | 0;
		var d1 = Math.random() * 0xffffffff | 0;
		var d2 = Math.random() * 0xffffffff | 0;
		var d3 = Math.random() * 0xffffffff | 0;
		return lut[d0 & 0xff] + lut[d0 >> 8 & 0xff] + lut[d0 >> 16 & 0xff] + lut[d0 >> 24 & 0xff] + '-' +
			lut[d1 & 0xff] + lut[d1 >> 8 & 0xff] + '-' + lut[d1 >> 16 & 0x0f | 0x40] + lut[d1 >> 24 & 0xff] + '-' +
			lut[d2 & 0x3f | 0x80] + lut[d2 >> 8 & 0xff] + '-' + lut[d2 >> 16 & 0xff] + lut[d2 >> 24 & 0xff] +
			lut[d3 & 0xff] + lut[d3 >> 8 & 0xff] + lut[d3 >> 16 & 0xff] + lut[d3 >> 24 & 0xff];
	}
	return self;
})();
// Based on https://groups.google.com/d/msg/fabricjs/TSwLHzLsP_w/-sE8WBDq6QIJ
fabric.LineArrow = fabric.util.createClass(fabric.Line, {
	type: 'lineArrow'
	, initialize: function(element, options) {
		options || (options = {});
		this.callSuper('initialize', element, options);
	}
	, toObject: function() {
		return fabric.util.object.extend(this.callSuper('toObject'));
	}
	, _render: function(ctx) {
		this.callSuper('_render', ctx);

		// do not render if width/height are zeros or object is not visible
		if (this.width === 0 || this.height === 0 || !this.visible) return;

		ctx.save();
		var xDiff = this.x2 - this.x1;
		var yDiff = this.y2 - this.y1;
		var angle = Math.atan2(yDiff, xDiff);
		ctx.translate((this.x2 - this.x1) / 2, (this.y2 - this.y1) / 2);
		ctx.rotate(angle);
		ctx.beginPath();
		// move 10px in front of line to start the arrow so it does not have the
		// square line end showing in front (0,0)
		ctx.moveTo(10, 0);
		ctx.lineTo(-20, 15);
		ctx.lineTo(-20, -15);
		ctx.closePath();
		this._renderFill(ctx);
		this._renderStroke(ctx);
		ctx.restore();
	}
});
fabric.LineArrow.fromObject = function (object, callback) {
	callback && callback(new fabric.LineArrow([object.x1, object.y1, object.x2, object.y2],object));
};
fabric.LineArrow.async = true;

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
	var base = {
		fill: {enabled: true, color: '#FFFF33'}
		, stroke: {enabled: true, color: '#FF6600', width: 5}
		, opacity: 1
	};
	base.enableLineProps = function(s) {
		var c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width'), o = s.find('.wb-prop-opacity');
		s.find('.wb-prop-fill').prop('disabled', true);
		s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
		c.val(base.stroke.color).prop('disabled', false);
		w.val(base.stroke.width).prop('disabled', false);
		o.val(100 * base.opacity).prop('disabled', false);
		return {c: c, w: w, o: o};
	};
	base.enableAllProps = function(s) {
		var c = s.find('.wb-prop-color'), w = s.find('.wb-prop-width')
			, o = s.find('.wb-prop-opacity'), f = s.find('.wb-prop-fill')
			, lc = s.find('.wb-prop-lock-color'), lf = s.find('.wb-prop-lock-fill');
		s.find('.wb-prop-b, .wb-prop-i').button("disable");
		lc.button("enable").button('option', 'icon', base.stroke.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		lf.button("enable").button('option', 'icon', base.fill.enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
		c.val(base.stroke.color).prop('disabled', !base.stroke.enabled);
		w.val(base.stroke.width).prop('disabled', false);
		o.val(100 * base.opacity).prop('disabled', false);
		f.val(base.fill.color).prop('disabled', !base.fill.enabled);
	};
	return base;
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
				, fill: text.fill.color
				, opacity: text.opacity
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
		text.enableAllProps(s);
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

		paint.enableLineProps(s).o.prop('disabled', true); //TODO not working
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

	shape.add2Canvas = function() {
		canvas.add(shape.obj);
	}
	shape.mouseDown = function(o) {
		shape.isDown = true;
		var pointer = canvas.getPointer(o.e);
		shape.orig = {x: pointer.x, y: pointer.y};
		shape.createShape();
		shape.add2Canvas();
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
		line.enableLineProps(s);
	};
	line.updateShape = function(pointer) {
		line.obj.set({ x2: pointer.x, y2: pointer.y });
	};
	return line;
}
var ULine = function(canvas, s) {
	var uline = Line(canvas, s);
	uline.stroke.width = 20;
	uline.opacity = .5;
	return uline;
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
		rect.enableAllProps(s);
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
var Ellipse = function(canvas, s) {
	var ellipse = Rect(canvas, s);
	ellipse.createShape = function() {
		ellipse.obj = new fabric.Ellipse({
			strokeWidth: ellipse.stroke.width
			, fill: ellipse.fill.enabled ? ellipse.fill.color : 'rgba(0,0,0,0)'
			, stroke: ellipse.stroke.enabled ? ellipse.stroke.color : 'rgba(0,0,0,0)'
			, left: ellipse.orig.x
			, top: ellipse.orig.y
			, rx: 0
			, ry: 0
			, originX: 'center'
			, originY: 'center'
		});
		return ellipse.obj;
	};
	ellipse.updateShape = function(pointer) {
		ellipse.obj.set({
			rx: Math.abs(ellipse.orig.x - pointer.x)
			, ry: Math.abs(ellipse.orig.y - pointer.y)
		});
	};
	return ellipse;
}
var Arrow = function(canvas, s) {
	var arrow = Line(canvas, s);
	arrow.createShape = function() {
		arrow.obj = new fabric.LineArrow([arrow.orig.x, arrow.orig.y, arrow.orig.x, arrow.orig.y], {
			strokeWidth: arrow.stroke.width
			, fill: arrow.fill.enabled ? arrow.fill.color : 'rgba(0,0,0,0)'
			, stroke: arrow.stroke.enabled ? arrow.stroke.color : 'rgba(0,0,0,0)'
			, opacity: arrow.opacity
		});
		return arrow.obj;
	};
	arrow.internalActivate = function() {
		arrow.enableAllProps(s);
	};
	return arrow;
}
var Clipart = function(canvas, btn) {
	var art = Shape(canvas);
	art.add2Canvas = function() {}
	art.createShape = function() {
		fabric.Image.fromURL(btn.data('image'), function(img) {
			art.orig.width = img.width;
			art.orig.height = img.height;
			art.obj = img.set({
				left: art.orig.x
				, top: art.orig.y
				, width: 0
				, height: 0
			});
			canvas.add(art.obj);
		});
	}
	art.updateShape = function(pointer) {
		if (!art.obj) {
			return; // not ready
		}
		//TODO height == width
		var dx = pointer.x - art.orig.x, dy = pointer.y - art.orig.y;
		var d = Math.sqrt(dx * dx + dy * dy);
		art.obj.set({
			width: d
			, height: art.orig.height * d / art.orig.width
			, angle: Math.atan2(dy, dx) * 180 / Math.PI
		});
	}
	return art;
}
var Wb = function() {
	const ACTIVE = 'active';
	var a, t, s, canvas, mode;

	function getBtn(m) {
		return t.find(".om-icon." + (m || mode));
	}
	function initToolBtn(m, def, obj) {
		var btn = getBtn(m);
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
	function initCliparts() {
		var c = $('#wb-area-clip').clone().attr('id', '');
		getBtn('arrow').after(c);
		c.find('a').prepend(c.find('div.om-icon.big:first'));
		c.find('.om-icon.clipart').each(function(idx) {
			var cur = $(this);
			cur.css('background-image', 'url(' + cur.data('image') + ')')
				.click(function() {
					var old = c.find('a .om-icon.clipart');
					c.find('ul li').prepend(old);
					c.find('a').prepend(cur);
				});
			initToolBtn(cur.data('mode'), false, Clipart(canvas, cur));
		});
		c.show();
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
		initToolBtn('uline', false, ULine(canvas, s));
		initToolBtn('rect', false, Rect(canvas, s));
		initToolBtn('ellipse', false, Ellipse(canvas, s));
		initToolBtn('arrow', false, Arrow(canvas, s));
		initCliparts();
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
