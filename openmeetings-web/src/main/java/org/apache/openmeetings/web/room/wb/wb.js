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
var APointer = function(canvas, user) {
	var pointer = {};
	pointer.mouseUp = function(o) {
		var ptr = canvas.getPointer(o.e);
		fabric.Image.fromURL('./css/images/menupointer.png', function(img) {
			img.set({
				left:15
				, originX: 'right'
				, originY: 'top'
			});
			var circle1 = new fabric.Circle({
				radius: 20
				, stroke: '#ff6600'
				, strokeWidth: 2
				, fill: 'rgba(0,0,0,0)'
				, originX: 'center'
				, originY: 'center'
			});
			var circle2 = new fabric.Circle({
				radius: 6
				, stroke: '#ff6600'
				, strokeWidth: 2
				, fill: 'rgba(0,0,0,0)'
				, originX: 'center'
				, originY: 'center'
			});
			var text = new fabric.Text(user, {
				fontSize: 12
				, left: 10
				, originX: 'left'
				, originY: 'bottom'
			});
			var group = new fabric.Group([circle1, circle2, img, text], {
				left: ptr.x - 20
				, top: ptr.y - 20
			});
			canvas.add(group);

			var count = 3;
			function go(_cnt) {
				if (_cnt < 0) {
					canvas.remove(group);
				}
				circle1.set({radius: 3});
				circle2.set({radius: 6});
				circle1.animate(
					'radius', '20'
					, {
						onChange: canvas.renderAll.bind(canvas)
						, duration: 1000
						, onComplete: function() {go(_cnt - 1);}
					});
				circle2.animate(
					'radius', '20'
					, {
						onChange: canvas.renderAll.bind(canvas)
						, duration: 1000
					});
			}
			go(count);
		});
	}
	pointer.activate = function() {
		canvas.on('mouse:up', pointer.mouseUp);
	}
	pointer.deactivate = function() {
		canvas.off('mouse:up', pointer.mouseUp);
	};
	return pointer;
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
		arrow.obj = new fabric.Polygon([
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0},
			{x: 0, y: 0}]
			, {
				left: arrow.orig.x
				, top: arrow.orig.y
				, angle: 0
				, strokeWidth: 2
				, fill: arrow.fill.enabled ? arrow.fill.color : 'rgba(0,0,0,0)'
				, stroke: arrow.stroke.enabled ? arrow.stroke.color : 'rgba(0,0,0,0)'
			});

		return arrow.obj;
	};
	arrow.updateShape = function(pointer) {
		var dx = pointer.x - arrow.orig.x
		, dy = pointer.y - arrow.orig.y
		, d = Math.sqrt(dx * dx + dy * dy)
		, sw = arrow.stroke.width
		, hl = sw * 3
		, h = 1.5 * sw
		, points = [
			{x: 0, y: sw},
			{x: Math.max(0, d - hl), y: sw},
			{x: Math.max(0, d - hl), y: h},
			{x: d, y: 3 * sw / 4},
			{x: Math.max(0, d - hl), y: 0},
			{x: Math.max(0, d - hl), y: sw / 2},
			{x: 0, y: sw / 2}];
		arrow.obj.set({
			points: points
			, angle: Math.atan2(dy, dx) * 180 / Math.PI
			, width: d
			, height: h
			, maxX: d
			, maxY: h
			, pathOffset: {
				x: d / 2,
				y: h / 2
			}
		});
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
	var wbId, a, t, s, canvas, mode;

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
		var c = $('#wb-area-cliparts').clone().attr('id', '');
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
	}
	function internalInit(t) {
		t.draggable({
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
		initToolBtn('apointer', false, APointer(canvas, 'TEST USER')); //FIXME TODO
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
	}

	//events
	var objAddedHandler = function (e) {
		var obj = e.target;
		console.log('Object Added', obj);
	};
	var objModifiedHandler = function (e) {
		var obj = e.target;
		console.log('Object Modified', obj);
	};
	var objRemovedHandler = function (e) {
		var obj = e.target;
		console.log('Object Removed', obj);
	};
	var pathCreatedHandler = function (e) {
		var obj = e.target;
		console.log('Path Created', obj);
	};
	var textEditedHandler = function (e) {
		var obj = e.target;
		console.log('Text Edit Exit', obj);
	};
	var textChangedHandler = function (e) {
		var obj = e.target;
		console.log('Text Changed', obj);
	};
	return {
		init: function(_wbId, tid) {
			wbId = _wbId;
			a = $('#' + tid);
			t = a.find('.tools'), c = a.find('canvas'), s = a.find(".wb-settings");
			c.attr('id', 'can-' + tid);
			canvas = new fabric.Canvas(c.attr('id'));
			//TODO create via WS canvas:cleared
			canvas.on('object:added', objAddedHandler);
			canvas.on('object:modified', objModifiedHandler);
			canvas.on('object:removed', objRemovedHandler);
			canvas.on('path:created', pathCreatedHandler);
			canvas.on('text:editing:exited', textEditedHandler);
			canvas.on('text:changed', textChangedHandler);
			internalInit(t);
			setRoomSizes();
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
		, getCanvas: function() {
			return canvas;
		}
	};
};
var WbArea = (function() {
	var container, area, tabs, self = {};

	function getWbTabId(id) {
		return "wb-tab-" + id;
	}
	function refreshTabs() {
		tabs.tabs("refresh").find('ul').removeClass('ui-corner-all').removeClass('ui-widget-header');
	}
	function getActive() {
		var idx = tabs.tabs("option", 'active');
		if (idx > -1) {
			var href = tabs.find('a')[idx];
			if (!!href) {
				var wb = $($(href).attr('href'));
				return wb.data('getCanvas')();
			}
		}
		return null;
	}
	function deleteHandler(e) {
		var canvas = getActive();
		switch (e.which) {
			case 8:  // backspace
			case 46: // delete
				if (!!canvas) {
					if (canvas.getActiveGroup()) {
						canvas.getActiveGroup().forEachObject(function(o){ canvas.remove(o) });
						canvas.discardActiveGroup().renderAll();
					} else {
						var obj = canvas.getActiveObject();
						if (!!obj) {
							canvas.remove(obj).renderAll();
						}
					}
					return false;
				}
		}
	}
	self.init = function() {
		tabs = $('.room.wb.area .tabs').tabs();
		tabs.find(".ui-tabs-nav").sortable({
			axis: "x"
			, stop: function() {
				refreshTabs();
			}
		});
		tabs.find('.add.om-icon').click(function() {
			wbAction('createWb');
		});
		container = $(".room.wb.area");
		area = container.find(".wb-area");
		$(window).keyup(deleteHandler);
	}
	self.destroy = function() {
		$(window).off('keyup', deleteHandler);
	};
	self.add = function(obj) {
		var tid = getWbTabId(obj.id)
			, li = $('#wb-area-tab').clone().attr('id', '').data('wb-id', obj.id)
			, wb = $('#wb-area').clone().attr('id', tid);
		li.find('a').text(obj.name).attr('title', obj.name).attr('href', "#" + tid);
		li.find('button').click(function() {
			wbAction('removeWb', JSON.stringify({id: obj.id}));
		});

		tabs.find(".ui-tabs-nav").append(li);
		tabs.append(wb);
		refreshTabs();

		$('.room.wb.area .wb-tabbar li').each(function(idx) {
			if (obj.id == 1 * $(this).data('wb-id')) {
				tabs.tabs("option", "active", idx);
				return false;
			}
		});
		wb.data(Wb()).data('init')(obj.id, tid);
	};
	self.remove = function(id) {
		var tabId = getWbTabId(id);
		tabs.find('li[aria-controls="' + tabId + '"]').remove();
		$("#" + tabId).remove();
		refreshTabs();
	};
	self.resize = function(posX, w, h) {
		if (!container) return;
		var hh = h - 5;
		container.width(w).css('left', posX	 + "px");
		area.width(w);

		container.height(h);
		area.height(hh);
		var wbTabs = area.find(".tabs.ui-tabs");
		wbTabs.height(hh);
		var tabPanels = wbTabs.find(".ui-tabs-panel");
		var wbah = hh - 5 - wbTabs.find("ul.ui-tabs-nav").height();
		tabPanels.height(wbah);
		tabPanels.each(function(idx) {
			$(this).data('resize')(w, wbah);
		});
	}
	return self;
})();
$(function() {
	Wicket.Event.subscribe("/websocket/message", function(jqEvent, msg) {
		try {
			var m = jQuery.parseJSON(msg);
			if (m) {
				switch(m.type) {
					case "wb":
						eval(m.func);
						break;
				}
			}
		} catch (err) {
			//no-op
		}
	});
});
