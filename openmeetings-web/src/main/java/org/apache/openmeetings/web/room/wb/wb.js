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
var Base = function() {
	var base = {};
	base.objectCreated = function(o, canvas) {
		o.uid = UUID.generate();
		o.slide = canvas.slide;
		canvas.trigger("wb:object:created", o);
		return o.uid;
	}
	return base;
}
var Pointer = function(wb, s) {
	return {
		activate: function() {
			wb.eachCanvas(function(canvas) {
				canvas.selection = true;
				canvas.forEachObject(function(o) {
					o.selectable = true;
				});
			});
			s.find('[class^="wb-prop"]').prop('disabled', true);
			if (!!s.find('.wb-prop-b').button("instance")) {
				s.find('.wb-prop-b, .wb-prop-i, .wb-prop-lock-color, .wb-prop-lock-fill').button("disable");
			}
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
}
var APointer = function(wb) {
	var pointer = Base(wb);
	pointer.user = '';
	pointer.create = function(canvas, o) {
		fabric.Image.fromURL('./css/images/pointer.png', function(img) {
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
			var text = new fabric.Text(o.user, {
				fontSize: 12
				, left: 10
				, originX: 'left'
				, originY: 'bottom'
			});
			var group = new fabric.Group([circle1, circle2, img, text], {
				left: o.x - 20
				, top: o.y - 20
			});
			
			canvas.add(group);
			group.uid = o.uid;
			group.loaded = !!o.loaded;

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
	pointer.mouseUp = function(o) {
		var canvas = this;
		var ptr = canvas.getPointer(o.e);
		var obj = {
			type: 'pointer'
			, x: ptr.x
			, y: ptr.y
			, user: pointer.user
		};
		obj.uid = uid = pointer.objectCreated(obj, canvas);
		pointer.create(canvas, obj);
	}
	pointer.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on('mouse:up', pointer.mouseUp);
		});
		pointer.user = $('.room.sidebar.left .user.list .current .name').text();
	}
	pointer.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:up', pointer.mouseUp);
		});
	};
	return pointer;
}
var ShapeBase = function(wb) {
	var base = Base(wb);
	base.fill = {enabled: true, color: '#FFFF33'};
	base.stroke = {enabled: true, color: '#FF6600', width: 5};
	base.opacity = 1;
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
var Text = function(wb, s) {
	var text = ShapeBase(wb);
	text.obj = null;

	text.mouseDown = function(o) {
		var canvas = this;
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
		wb.eachCanvas(function(canvas) {
			canvas.on('mouse:down', text.mouseDown);
			canvas.selection = true;
			canvas.forEachObject(function(o) {
				if (o.type == 'i-text') {
					o.selectable = true;
				}
			});
		});
		text.enableAllProps(s);
	};
	text.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:down', text.mouseDown);
			canvas.selection = false;
			canvas.forEachObject(function(o) {
				if (o.type == 'i-text') {
					o.selectable = false;
				}
			});
		});
	};
	return text;
}
var Paint = function(wb, s) {
	var paint = ShapeBase(wb);
	paint.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.isDrawingMode = true;
			canvas.freeDrawingBrush.width = paint.stroke.width;
			canvas.freeDrawingBrush.color = paint.stroke.color;
			canvas.freeDrawingBrush.opacity = paint.opacity; //TODO not working
		});
		paint.enableLineProps(s).o.prop('disabled', true); //TODO not working
	};
	paint.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.isDrawingMode = false;
		});
	};
	return paint;
}
var Shape = function(wb) {
	var shape = ShapeBase(wb);
	shape.obj = null;
	shape.isDown = false;
	shape.orig = {x: 0, y: 0};

	shape.add2Canvas = function(canvas) {
		canvas.add(shape.obj);
	}
	shape.mouseDown = function(o) {
		var canvas = this;
		shape.isDown = true;
		var pointer = canvas.getPointer(o.e);
		shape.orig = {x: pointer.x, y: pointer.y};
		shape.createShape(canvas);
		shape.add2Canvas(canvas);
	};
	shape.mouseMove = function(o) {
		var canvas = this;
		if (!shape.isDown) return;
		var pointer = canvas.getPointer(o.e);
		shape.updateShape(pointer);
		canvas.renderAll();
	};
	shape.updateCreated = function(o) {
		return o;
	};
	shape.mouseUp = function(o) {
		var canvas = this;
		shape.isDown = false;
		shape.obj.setCoords();
		shape.obj.selectable = false;
		canvas.renderAll();
		shape.objectCreated(shape.obj, canvas);
	};
	shape.internalActivate = function() {};
	shape.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on({
				'mouse:down': shape.mouseDown
				, 'mouse:move': shape.mouseMove
				, 'mouse:up': shape.mouseUp
			});
		});
		shape.internalActivate();
	};
	shape.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off({
				'mouse:down': shape.mouseDown
				, 'mouse:move': shape.mouseMove
				, 'mouse:up': shape.mouseUp
			});
		});
	};
	return shape;
};
var Line = function(wb, s) {
	var line = Shape(wb);
	line.createShape = function(canvas) {
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
var ULine = function(wb, s) {
	var uline = Line(wb, s);
	uline.stroke.width = 20;
	uline.opacity = .5;
	return uline;
}
var Rect = function(wb, s) {
	var rect = Shape(wb);
	rect.createShape = function(canvas) {
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
var Ellipse = function(wb, s) {
	var ellipse = Rect(wb, s);
	ellipse.createShape = function(canvas) {
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
var Arrow = function(wb, s) {
	var arrow = Line(wb, s);
	arrow.createShape = function(canvas) {
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
var Clipart = function(wb, btn) {
	var art = Shape(wb);
	art.add2Canvas = function(canvas) {}
	art.createShape = function(canvas) {
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
	var wb = {id: -1}, a, t, s, canvases = [], mode, slide = 0, resizable = true;

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
			initToolBtn(cur.data('mode'), false, Clipart(wb, cur));
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
			}
		});
		initToolBtn('pointer', true, Pointer(wb, s));
		initToolBtn('apointer', false, APointer(wb));
		initToolBtn('text', false, Text(wb, s));
		initToolBtn('paint', false, Paint(wb, s));
		initToolBtn('line', false, Line(wb, s));
		initToolBtn('uline', false, ULine(wb, s));
		initToolBtn('rect', false, Rect(wb, s));
		initToolBtn('ellipse', false, Ellipse(wb, s));
		initToolBtn('arrow', false, Arrow(wb, s));
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
					wb.eachCanvas(function(canvas) {
						canvas.freeDrawingBrush.color = v;
					});
				}
			}
		});
		s.find('.wb-prop-width').change(function() {
			var btn = getBtn();
			if (btn.length == 1) {
				var v = 1 * $(this).val();
				btn.data('obj').stroke.width = v;
				if ('paint' == mode) {
					wb.eachCanvas(function(canvas) {
						canvas.freeDrawingBrush.width = v;
					});
				}
			}
		});
		s.find('.wb-prop-opacity').change(function() {
			var btn = getBtn();
			if (btn.length == 1) {
				var v = (1 * $(this).val()) / 100;
				btn.data('obj').opacity = v;
				if ('paint' == mode) {
					wb.eachCanvas(function(canvas) {
						canvas.freeDrawingBrush.opacity = v;
					});
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
				}
			}
			, drag: function(event, ui) {
				if (s.position().x + s.width() >= s.parent().width()) {
					return false;
				}
			}
		});
	}
	function _findObject(o) {
		var _o = {};
		canvases[o.slide].forEachObject(function(__o) {
			if (!!__o && o.uid === __o.uid) {
				_o = __o;
				return false;
			}
		});
		return _o;
	}
	function _removeHandler(o) {
		var __o = _findObject(o);
		if (!!__o) {
			canvases[o.slide].remove(__o);
		}
	}
	function _modifyHandler(_o) {
		_removeHandler(_o);
		canvases[_o.slide].add(_o);
	}
	function _createHandler(_o) {
		switch (_o.fileType) {
			case 'Video':
			case 'Recording':
			{
				var vid = $('<video>').hide().attr('id', 'video-' + _o.uid).attr('poster', _o._poster + '&preview=true')
					.attr("width", _o.width).attr("height", _o.height)
					.append($('<source>').attr('type', 'video/mp4').attr('src', _o._src))
				$('#wb-tab-' + canvas.wbId).append(vid);
				var vImg = new fabric.Image(vid[0], {
					left: _o.left
					, top: _o.top
				});
				canvases[_o.slide].add(vImg);
				//console.log(vImg.toJSON(['uid', 'fileId', 'fileType']));
			}
				break;
			case 'Presentation':
			{
				if (resizable && !_o.deleted) {
					resizable = false;
				}
				var count = _o.deleted ? 1 : _o.count;
				for (var i = 0; i < count; ++i) {
					if (canvases.length < i + 1) {
						addCanvas();
					}
					var canvas = canvases[i];
					/*
					 * TODO block resizing
					*/
					canvas.setBackgroundImage(_o._src + "&slide=" + i, canvas.renderAll.bind(canvas), {
						/*backgroundImageOpacity: 0.5
						, */backgroundImageStretch: false
					}).setWidth(Math.max(canvas.width, _o.width)).setHeight(Math.max(canvas.height, _o.height));
				}
			}
				break;
			default:
				canvases[_o.slide].add(_o);
				break;
		}
	}
	function _createObject(arr, handler) {
		fabric.util.enlivenObjects(arr, function(objects) {
			wb.eachCanvas(function(canvas) {
				canvas.renderOnAddRemove = false;
			});

			for (var i = 0; i < objects.length; ++i) {
				var _o = objects[i];
				_o.loaded = true;
				handler(_o);
			}

			wb.eachCanvas(function(canvas) {
				canvas.renderOnAddRemove = true;
				canvas.renderAll();
			});
		});
	};

	function toOmJson(o) {
		return o.toJSON(['uid', 'fileId', 'fileType', 'count', 'slide']);
	}
	//events
	function wbObjCreatedHandler(o) {
		var json = {};
		switch(o.type) {
			case 'pointer':
				json = o;
				break;
			default:
				o.includeDefaultValues = false;
				json = toOmJson(o);
				break;
		}
		wbAction('createObj', JSON.stringify({
			wbId: wb.id
			, obj: json
		}));
	};
	function objAddedHandler(e) {
		var o = e.target;
		if (!!o.loaded) return;
		switch(o.type) {
			case 'i-text':
				o.uid = UUID.generate();
				o.slide = this.slide;
				wbObjCreatedHandler(o);
				break;
		}
	};
	function objModifiedHandler(e) {
		var o = e.target;
		o.includeDefaultValues = false;
		wbAction('modifyObj', JSON.stringify({
			wbId: wb.id
			, obj: toOmJson(o)
		}));
	};
	function objSelectedHandler(e) {
		var o = e.target;
		s.find('.wb-dim-x').val(o.left);
		s.find('.wb-dim-y').val(o.top);
		s.find('.wb-dim-w').val(o.width);
		s.find('.wb-dim-h').val(o.height);
	}
	function pathCreatedHandler(o) {
		o.path.uid = UUID.generate();
		o.path.slide = this.slide;
		wbObjCreatedHandler(o.path);
	};
	/*TODO interactive text chage
	var textEditedHandler = function (e) {
		var obj = e.target;
		console.log('Text Edit Exit', obj);
	};
	var textChangedHandler = function (e) {
		var obj = e.target;
		console.log('Text Changed', obj);
	};*/
	function addCanvas() {
		var slide = canvases.length;
		var c = $('<canvas></canvas>').attr('id', 'can-' + a.attr('id') + '-slide-' + slide);
		a.find('.canvases').append(c);
		var canvas = new fabric.Canvas(c.attr('id'));
		canvas.wbId = wb.id;
		canvas.slide = slide;
		//TODO create via WS canvas:cleared
		canvas.on({
			'object:added': objAddedHandler
			, 'object:modified': objModifiedHandler
			, 'object:selected': objSelectedHandler
			, 'path:created': pathCreatedHandler
			//, 'text:editing:exited': textEditedHandler
			//, 'text:changed': textChangedHandler
			, 'wb:object:created': wbObjCreatedHandler
		});
		canvases.push(canvas);
	}
	wb.init = function(_wbId, tid) {
		wb.id = _wbId;
		a = $('#' + tid);
		t = a.find('.tools'), s = a.find(".wb-settings");
		addCanvas();
		internalInit(t);
	};
	wb.resize = function(w, h) {
		if (t.position().left + t.width() > a.width()) {
			t.position({
				my: "right"
				, at: "right"
				, of: a.selector
				, collision: "fit"
			});
		}
		if (resizable) {
			//TODO FIXME need to be checked
			wb.eachCanvas(function(canvas) {
				canvas.setWidth(w).setHeight(h);
			});
		}
	};
	wb.load = function(arr) {
		_createObject(arr, _createHandler);
	};
	wb.createObj = function(o) {
		switch(o.type) {
			case 'pointer':
				APointer().create(canvases[o.slide], o);
				break;
			default:
				_createObject([o], _createHandler);
				/*
				 * https://jsfiddle.net/l2aelba/kro7h6rv/2/
				if ('Video' === o.fileType || 'Recording' === o.fileType) {
					fabric.util.requestAnimFrame(function render() {
						canvas.renderAll();
						fabric.util.requestAnimFrame(render);
					});
				}
				*/
				break;
		}
	};
	wb.modifyObj = function(o) { //TODO need to be unified
		switch(o.type) {
			case 'pointer':
				_modifyHandler(APointer().create(canvases[o.slide], o))
				break;
			default:
				var arr = [o];
				if (!!o.objects) {
					arr = o.objects;
					for (var i = 0; i < arr.length; ++i) {
						var _o = arr[i];
						_o.left += o.left;
						_o.top += o.top;
					}
				}
				_createObject(o.objects || [o], _modifyHandler);
				break;
		}
	};
	wb.removeObj = function(arr) {
		for (var i = 0; i < arr.length; ++i) {
			_removeHandler(arr[i]);
		}
	};
	wb.getCanvas = function() {
		return canvases[slide];
	};
	wb.eachCanvas = function(func) {
		for (var i = 0; i < canvases.length; ++i) {
			func(canvases[i]);
		}
	}
	return wb;
};
var WbArea = (function() {
	var container, area, tabs, scroll, self = {};

	function refreshTabs() {
		tabs.tabs("refresh").find('ul').removeClass('ui-corner-all').removeClass('ui-widget-header');
	}
	function getActive() {
		var idx = tabs.tabs("option", 'active');
		if (idx > -1) {
			var href = tabs.find('a')[idx];
			if (!!href) {
				return $($(href).attr('href'));
			}
		}
		return null;
	}
	function deleteHandler(e) {
		switch (e.which) {
			case 8:  // backspace
			case 46: // delete
				{
					var wb = getActive().data();
					var canvas = wb.getCanvas();
					if (!!canvas) {
						var arr = [];
						if (canvas.getActiveGroup()) {
							canvas.getActiveGroup().forEachObject(function(o){
								arr.push({
									uid: o.uid
									, slide: o.slide
								});
							});
						} else {
							var obj = canvas.getActiveObject();
							if (!!obj) {
								arr.push({
									uid: o.uid
									, slide: o.slide
								});
							}
						}
						wbAction('deleteObj', JSON.stringify({
							wbId: wb.id
							, obj: arr
						}));
						return false;
					}
				}
				break;
		}
	}
	function _activateTab(wbId) {
		container.find('.wb-tabbar li').each(function(idx) {
			if (wbId == 1 * $(this).data('wb-id')) {
				tabs.tabs("option", "active", idx);
				$(this)[0].scrollIntoView();
				return false;
			}
		});
	}
	self.getWbTabId = function(id) {
		return "wb-tab-" + id;
	};
	self.getWb = function(id) {
		return $('#' + self.getWbTabId(id)).data();
	};
	self.getCanvas = function(id) {
		return self.getWb(id).getCanvas();
	};
	self.init = function() {
		container = $(".room.wb.area");
		tabs = container.find('.tabs').tabs({
			activate: function(event, ui) {
				wbAction('activeWb', JSON.stringify({id: ui.newTab.data('wb-id')}));
			}
		});
		scroll = tabs.find('.scroll-container');
		tabs.find(".ui-tabs-nav").sortable({
			axis: "x"
			, stop: function() {
				refreshTabs();
			}
		});
		tabs.find('.add.om-icon').click(function() {
			wbAction('createWb');
		});
		tabs.find('.prev.om-icon').click(function() {
			scroll.scrollLeft(scroll.scrollLeft() - 30);
		});
		tabs.find('.next.om-icon').click(function() {
			scroll.scrollLeft(scroll.scrollLeft() + 30);
		});
		area = container.find(".wb-area");
		$(window).keyup(deleteHandler);
	};
	self.destroy = function() {
		$(window).off('keyup', deleteHandler);
	};
	self.create = function(obj) {
		var tid = self.getWbTabId(obj.id)
			, li = $('#wb-area-tab').clone().attr('id', '').data('wb-id', obj.id)
			, wb = $('#wb-area').clone().attr('id', tid);
		li.find('a').text(obj.name).attr('title', obj.name).attr('href', "#" + tid);
		li.find('button').click(function() {
			wbAction('removeWb', JSON.stringify({id: obj.id}));
		});
	
		tabs.find(".ui-tabs-nav").append(li);
		tabs.append(wb);
		refreshTabs();
	
		var wbo = Wb();
		wb.data(wbo);
		wbo.init(obj.id, tid);
	}
	self.add = function(obj) {
		self.create(obj);
		_activateTab(obj.id);
	};
	self.activate = function(obj) {
		_activateTab(obj.id);
	}
	self.load = function(json) {
		self.getWb(json.wbId).load(json.obj);
	};
	self.createObj = function(json) {
		self.getWb(json.wbId).createObj(json.obj);
	};
	self.modifyObj = function(json) {
		self.getWb(json.wbId).modifyObj(json.obj);
	};
	self.removeObj = function(json) {
		self.getWb(json.wbId).removeObj(json.obj);
	};
	self.remove = function(obj) {
		var tabId = self.getWbTabId(obj.id);
		tabs.find('li[aria-controls="' + tabId + '"]').remove();
		$("#" + tabId).remove();
		refreshTabs();
	};
	self.resize = function(posX, w, h) {
		if (!container) return;
		var hh = h - 5;
		container.width(w).height(h).css('left', posX + "px");
		area.width(w).height(hh);

		var wbTabs = area.find(".tabs.ui-tabs");
		wbTabs.height(hh);
		var tabPanels = wbTabs.find(".ui-tabs-panel");
		var wbah = hh - 5 - wbTabs.find("ul.ui-tabs-nav").height();
		tabPanels.height(wbah);
		tabPanels.each(function(idx) {
			$(this).data('resize')(w - 20, wbah);
		});
		wbTabs.find(".ui-tabs-panel .scroll-container").height(wbah);
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
			//console.log(err);
			//no-op
		}
	});
});
