/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Wb = function() {
	const ACTIVE = 'active', BUMPER = 100
		, wb = {id: -1, name: ''}, canvases = []
		, extraProps = ['uid', 'fileId', 'fileType', 'count', 'slide', 'omType', '_src'];
	let a, t, z, s, mode, slide = 0, width = 0, height = 0
			, zoom = 1., zoomMode = 'fullFit', role = null;

	function getBtn(m) {
		return !!t ? t.find(".om-icon." + (m || mode)) : null;
	}
	function initToolBtn(m, def, obj) {
		const btn = getBtn(m);
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
			const b = getBtn();
			if (b.length && b.hasClass(ACTIVE)) {
				b.data().deactivate();
			}
			btn.data().activate();
		});
		if (def) {
			btn.data().activate();
		}
	}
	function initCliparts() {
		const c = $('#wb-area-cliparts').clone().attr('id', '');
		getBtn('arrow').after(c);
		c.find('a').prepend(c.find('div.om-icon.big:first'));
		c.find('.om-icon.clipart').each(function() {
			const cur = $(this);
			cur.css('background-image', 'url(' + cur.data('image') + ')')
				.click(function() {
					const old = c.find('a .om-icon.clipart');
					c.find('ul li').prepend(old);
					c.find('a').prepend(cur);
				});
			initToolBtn(cur.data('mode'), false, Clipart(wb, cur, s));
		});
	}
	function _updateZoomPanel() {
		const ccount = canvases.length;
		if (ccount > 1 && role === PRESENTER) {
			z.find('.doc-group').show();
			const ns = 1 * slide;
			z.find('.doc-group .curr-slide').val(ns + 1).attr('max', ccount);
			z.find('.doc-group .up').prop('disabled', ns < 1);
			z.find('.doc-group .down').prop('disabled', ns > ccount - 2);
			z.find('.doc-group .last-page').text(ccount);
		} else {
			z.find('.doc-group').hide();
		}
	}
	function _setSlide(_sld) {
		slide = _sld;
		wbAction('setSlide', JSON.stringify({
			wbId: wb.id
			, slide: _sld
		}));
		_updateZoomPanel();
	}
	function internalInit() {
		t.draggable({
			snap: "parent"
			, containment: "parent"
			, scroll: false
			, stop: function(event, ui) {
				const pos = ui.helper.position();
				if (pos.left === 0 || pos.left + ui.helper.width() === ui.helper.parent().width()) {
					ui.helper.removeClass('horisontal').addClass('vertical');
				} else if (pos.top === 0 || pos.top + ui.helper.height() === ui.helper.parent().height()) {
					ui.helper.removeClass('vertical').addClass('horisontal');
				}
			}
		});
		z.draggable({
			snap: "parent"
			, containment: "parent"
			, scroll: false
		});
		const clearAll = t.find('.om-icon.clear-all');
		let _firstToolItem = true;
		switch (role) {
			case PRESENTER:
				clearAll.click(function() {
					RoomUtil.confirmDlg('clear-all-confirm', function() { wbAction('clearAll', JSON.stringify({wbId: wb.id})); });
				}).removeClass('disabled');
				z.find('.curr-slide').change(function() {
					_setSlide($(this).val() - 1);
					showCurrentSlide();
				});
				z.find('.doc-group .up').click(function () {
					_setSlide(1 * slide - 1);
					showCurrentSlide();
				});
				z.find('.doc-group .down').click(function () {
					_setSlide(1 * slide + 1);
					showCurrentSlide();
				});
			case WHITEBOARD:
				if (role === WHITEBOARD) {
					clearAll.addClass('disabled');
				}
				initToolBtn('pointer', _firstToolItem, Pointer(wb, s));
				_firstToolItem = false;
				initToolBtn('text', _firstToolItem, Text(wb, s));
				initToolBtn('paint', _firstToolItem, Paint(wb, s));
				initToolBtn('line', _firstToolItem, Line(wb, s));
				initToolBtn('uline', _firstToolItem, ULine(wb, s));
				initToolBtn('rect', _firstToolItem, Rect(wb, s));
				initToolBtn('ellipse', _firstToolItem, Ellipse(wb, s));
				initToolBtn('arrow', _firstToolItem, Arrow(wb, s));
				initCliparts();
				t.find(".om-icon.settings").click(function() {
					s.show();
				});
				t.find('.om-icon.clear-slide').click(function() {
					RoomUtil.confirmDlg('clear-slide-confirm', function() { wbAction('clearSlide', JSON.stringify({wbId: wb.id, slide: slide})); });
				});
				t.find('.om-icon.save').click(function() {
					wbAction('save', JSON.stringify({wbId: wb.id}));
				});
				t.find('.om-icon.undo').click(function() {
					wbAction('undo', JSON.stringify({wbId: wb.id}));
				});
				s.find('.wb-prop-b, .wb-prop-i')
					.button()
					.click(function() {
						$(this).toggleClass('ui-state-active selected');
						const btn = getBtn()
							, isB = $(this).hasClass('wb-prop-b');
						btn.data().obj.style[isB ? 'bold' : 'italic'] = $(this).hasClass('selected');
					});
				s.find('.wb-prop-lock-color, .wb-prop-lock-fill')
					.button({icon: 'ui-icon-locked', showLabel: false})
					.click(function() {
						const btn = getBtn()
							, isColor = $(this).hasClass('wb-prop-lock-color')
							, c = s.find(isColor ? '.wb-prop-color' : '.wb-prop-fill')
							, enabled = $(this).button('option', 'icon') === 'ui-icon-locked';
						$(this).button('option', 'icon', enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
						c.prop('disabled', !enabled);
						btn.data().obj[isColor ? 'stroke' : 'fill'].enabled = enabled;
					});
				s.find('.wb-prop-color').change(function() {
					const btn = getBtn();
					if (btn.length === 1) {
						const v = $(this).val();
						btn.data().obj.stroke.color = v;
						if ('paint' === mode) {
							wb.eachCanvas(function(canvas) {
								canvas.freeDrawingBrush.color = v;
							});
						}
					}
				});
				s.find('.wb-prop-width').change(function() {
					const btn = getBtn();
					if (btn.length === 1) {
						const v = 1 * $(this).val();
						btn.data().obj.stroke.width = v;
						if ('paint' === mode) {
							wb.eachCanvas(function(canvas) {
								canvas.freeDrawingBrush.width = v;
							});
						}
					}
				});
				s.find('.wb-prop-fill').change(function() {
					const btn = getBtn();
					if (btn.length === 1) {
						const v = $(this).val();
						btn.data().obj.fill.color = v;
					}
				});
				s.find('.wb-prop-opacity').change(function() {
					const btn = getBtn();
					if (btn.length === 1) {
						const v = (1 * $(this).val()) / 100;
						btn.data().obj.opacity = v;
						if ('paint' === mode) {
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
					, containment: 'body'
					, start: function() {
						if (!!s.css('bottom')) {
							s.css('bottom', '').css(Settings.isRtl ? 'left' : 'right', '');
						}
					}
					, drag: function() {
						if (s.position().x + s.width() >= s.parent().width()) {
							return false;
						}
					}
				});
			case NONE:
				_updateZoomPanel();
				z.find('.zoom-out').click(function() {
					zoom -= .2;
					if (zoom < .1) {
						zoom = .1;
					}
					zoomMode = 'zoom';
					_setSize();
					wbAction('setSize', JSON.stringify({
						wbId: wb.id
						, zoom: zoom
						, zoomMode: zoomMode
					}));
				});
				z.find('.zoom-in').click(function() {
					zoom += .2;
					zoomMode = 'zoom';
					_setSize();
					wbAction('setSize', JSON.stringify({
						wbId: wb.id
						, zoom: zoom
						, zoomMode: zoomMode
					}));
				});
				z.find('.zoom').change(function() {
					const zzz = $(this).val();
					zoomMode = 'zoom';
					if (isNaN(zzz)) {
						switch (zzz) {
							case 'fullFit':
							case 'pageWidth':
								zoomMode = zzz;
								break;
							case 'custom':
								zoom = 1. * $(this).data('custom-val');
								break;
						}
					} else {
						zoom = 1. * zzz;
					}
					_setSize();
					wbAction('setSize', JSON.stringify({
						wbId: wb.id
						, zoom: zoom
						, zoomMode: zoomMode
					}));
				});
				_setSize();
				initToolBtn('apointer', _firstToolItem, APointer(wb, s));
		}
	}
	function _findObject(o) {
		let _o = null;
		canvases[o.slide].forEachObject(function(__o) {
			if (!!__o && o.uid === __o.uid) {
				_o = __o;
				return false;
			}
		});
		return _o;
	}
	function _removeHandler(o) {
		const __o = _findObject(o);
		if (!!__o) {
			const cnvs = canvases[o.slide];
			if (!!cnvs) {
				cnvs.discardActiveGroup();
				if ('Video' === __o.omType) {
					$('#wb-video-' + __o.uid).remove();
				}
				cnvs.remove(__o);
			}
		}
	}
	function _modifyHandler(_o) {
		_removeHandler(_o);
		_createHandler(_o);
	}
	function _createHandler(_o) {
		switch (_o.fileType) {
			case 'Video':
			case 'Recording':
				//no-op
				break;
			case 'Presentation':
			{
				const ccount = canvases.length
					, count = _o.deleted ? 1 : _o.count;
				for (let i = 0; i < count; ++i) {
					if (canvases.length < i + 1) {
						addCanvas();
					}
					const canvas = canvases[i];
					canvas.setBackgroundImage(_o._src + "&slide=" + i, canvas.renderAll.bind(canvas), {});
				}
				_updateZoomPanel();
				if (ccount !== canvases.length) {
					const b = getBtn();
					if (b.length && b.hasClass(ACTIVE)) {
						b.data().deactivate();
						b.data().activate();
					}
				}
			}
				break;
			default:
			{
				const canvas = canvases[_o.slide];
				if (!!canvas) {
					_o.selectable = canvas.selection;
					canvas.add(_o);
				}
			}
				break;
		}
	}
	function _createObject(arr, handler) {
		fabric.util.enlivenObjects(arr, function(objects) {
			wb.eachCanvas(function(canvas) {
				canvas.renderOnAddRemove = false;
			});

			for (let i = 0; i < objects.length; ++i) {
				const _o = objects[i];
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
		const r = o.toJSON(extraProps);
		if (o.omType === 'Video') {
			r.type = 'video';
			delete r.objects;
			return r;
		}
		return r;
	}
	//events
	function wbObjCreatedHandler(o) {
		if (role === NONE && o.type !== 'pointer') return;

		let json;
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
		const o = e.target;
		if (!!o.loaded) return;
		switch(o.type) {
			case 'i-text':
				o.uid = UUID.generate();
				o.slide = this.slide;
				wbObjCreatedHandler(o);
				break;
			default:
				o.selectable = this.selection;
				break;
		}
	};
	function objModifiedHandler(e) {
		const o = e.target, items = [];
		if (role === NONE && o.type !== 'pointer') return;

		o.includeDefaultValues = false;
		if ("group" === o.type && o.omType !== 'Video') {
			o.clone(function(_o) {
				// ungrouping
				_o.includeDefaultValues = false;
				const _items = _o.destroy().getObjects();
				for (let i = 0; i < _items.length; ++i) {
					items.push(toOmJson(_items[i]));
				}
			}, extraProps);
		} else {
			items.push(toOmJson(o));
		}
		wbAction('modifyObj', JSON.stringify({
			wbId: wb.id
			, obj: items
		}));
	};
	function objSelectedHandler(e) {
		const o = e.target;
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
	function scrollHandler() {
		$(this).find('.canvas-container').each(function(idx) {
			const h = $(this).height(), pos = $(this).position();
			if (slide !== idx && pos.top > BUMPER - h && pos.top < BUMPER) {
				//TODO might be done without iterating
				_setSlide(idx);
				return false;
			}
		});
	}
	function showCurrentSlide() {
		a.find('.scroll-container .canvas-container').each(function(idx) {
			if (role === PRESENTER) {
				$(this).show();
				a.find('.scroll-container .canvas-container')[slide].scrollIntoView();
			} else {
				if (idx === slide) {
					$(this).show();
				} else {
					$(this).hide();
				}
			}
		});
	}
	/*TODO interactive text change
	var textEditedHandler = function (e) {
		var obj = e.target;
		console.log('Text Edit Exit', obj);
	};
	var textChangedHandler = function (e) {
		var obj = e.target;
		console.log('Text Changed', obj);
	};*/
	function setHandlers(canvas) {
		// off everything first to prevent duplicates
		canvas.off({
			'wb:object:created': wbObjCreatedHandler
			, 'object:modified': objModifiedHandler
			, 'object:added': objAddedHandler
			, 'object:selected': objSelectedHandler
			, 'path:created': pathCreatedHandler
			//, 'text:editing:exited': textEditedHandler
			//, 'text:changed': textChangedHandler
		});
		canvas.on({
			'wb:object:created': wbObjCreatedHandler
			, 'object:modified': objModifiedHandler
		});
		if (role !== NONE) {
			canvas.on({
				'object:added': objAddedHandler
				, 'object:selected': objSelectedHandler
				, 'path:created': pathCreatedHandler
				//, 'text:editing:exited': textEditedHandler
				//, 'text:changed': textChangedHandler
			});
		}
	}
	function addCanvas() {
		const sl = canvases.length
			, cid = 'can-' + a.attr('id') + '-slide-' + sl
			, c = $('<canvas></canvas>').attr('id', cid);
		a.find('.canvases').append(c);
		const canvas = new fabric.Canvas(c.attr('id'), {
			preserveObjectStacking: true
		});
		canvas.wbId = wb.id;
		canvas.slide = sl;
		canvases.push(canvas);
		const cc = $('#' + cid).closest('.canvas-container');
		if (role === NONE) {
			if (sl === slide) {
				cc.show();
			} else {
				cc.hide();
			}
		}
		__setSize(canvas);
		setHandlers(canvas);
	}
	function __setSize(_cnv) {
		_cnv.setWidth(zoom * width).setHeight(zoom * height).setZoom(zoom);
	}
	function _setSize() {
		switch (zoomMode) {
			case 'fullFit':
				zoom = Math.min((a.width() - 10) / width, (a.height() - 10) / height);
				z.find('.zoom').val(zoomMode);
				break;
			case 'pageWidth':
				zoom = (a.width() - 10) / width;
				z.find('.zoom').val(zoomMode);
				break;
			default:
			{
				const oo = z.find('.zoom').find('option[value="' + zoom.toFixed(2) + '"]');
				if (oo.length === 1) {
					oo.prop('selected', true);
				} else {
					z.find('.zoom').data('custom-val', zoom).find('option[value=custom]')
						.text((100. * zoom).toFixed(0) + '%')
						.prop('selected', true);
				}
			}
				break;
		}
		wb.eachCanvas(function(canvas) {
			__setSize(canvas);
		});
		_setSlide(slide);
	}
	function _videoStatus(json) {
		const g = _findObject(json);
		if (!!g) {
			g.videoStatus(json.status);
		}
	}
	wb.setRole = function(_role) {
		if (role !== _role) {
			const btn = getBtn();
			if (!!btn && btn.length === 1) {
				btn.data().deactivate();
			}
			a.find('.tools').remove();
			a.find('.wb-settings').remove();
			a.find('.wb-zoom').remove();
			role = _role;
			const sc = a.find('.scroll-container');
			z = $('#wb-zoom').clone().attr('id', '')
				.attr('style', 'position: absolute; top: 0px; ' + (Settings.isRtl ? 'right' : 'left') + ': 80px;');
			if (role === NONE) {
				t = $('#wb-tools-readonly').clone().attr('id', '');
				sc.off('scroll', scrollHandler);
			} else {
				t = $('#wb-tools').clone().attr('id', '');
				s = $("#wb-settings").clone().attr('id', '')
					.attr('style', 'display: none; bottom: 100px; ' + (Settings.isRtl ? 'left' : 'right') + ': 100px;');
				a.append(s);
				sc.on('scroll', scrollHandler);
			}
			t.attr('style', 'position: absolute; top: 20px; ' + (Settings.isRtl ? 'left' : 'right') + ': 20px;');
			a.append(t).append(z);
			showCurrentSlide();
			t = a.find('.tools'), s = a.find(".wb-settings");
			wb.eachCanvas(function(canvas) {
				setHandlers(canvas);
				canvas.forEachObject(function(__o) { //TODO reduce iterations
					if (!!__o && __o.omType === 'Video') {
						__o.setPlayable(role);
					}
				});
			});
			internalInit();
		}
	};
	wb.init = function(wbo, tid, _role) {
		wb.id = wbo.wbId;
		wb.name = wbo.name;
		width = wbo.width;
		height = wbo.height;
		zoom = wbo.zoom;
		zoomMode = wbo.zoomMode;
		a = $('#' + tid);
		addCanvas();
		wb.setRole(_role);
	};
	wb.setSize = function(wbo) {
		width = wbo.width;
		height = wbo.height;
		zoom = wbo.zoom;
		zoomMode = wbo.zoomMode;
		_setSize();
	}
	wb.resize = function() {
		if (t.position().left + t.width() > a.width()) {
			t.position({
				my: (Settings.isRtl ? 'left' : 'right')
				, at: (Settings.isRtl ? 'left' : 'right') + '-20'
				, of: '#' + a[0].id
				, collision: "fit"
			});
		}
		if (z.position().left + z.width() > a.width()) {
			z.position({
				my: (Settings.isRtl ? 'right' : 'left') + ' top'
				, at: "center top"
				, of: '#' + a[0].id
				, collision: "fit"
			});
		}
		if (zoomMode !== 'zoom') {
			_setSize();
		}
	};
	wb.setSlide = function(_sl) {
		slide = _sl;
		showCurrentSlide();
	};
	wb.createObj = function(obj) {
		const arr = [], _arr = Array.isArray(obj) ? obj : [obj];
		for (let i = 0; i < _arr.length; ++i) {
			const o = _arr[i];
			switch(o.type) {
				case 'pointer':
					APointer().create(canvases[o.slide], o);
					break;
				case 'video':
					Player.create(canvases[o.slide], o, role);
					break;
				default:
				{
					const __o = _findObject(o);
					if (!__o) {
						arr.push(o);
					}
				}
					break;
			}
		}
		if (arr.length > 0) {
			_createObject(arr, _createHandler);
		}
	};
	wb.load = wb.createObj;
	wb.modifyObj = function(obj) { //TODO need to be unified
		const arr = [], _arr = Array.isArray(obj) ? obj : [obj];
		for (let i = 0; i < _arr.length; ++i) {
			const o = _arr[i];
			switch(o.type) {
				case 'pointer':
					_modifyHandler(APointer().create(canvases[o.slide], o))
					break;
				case 'video':
				{
					const g = _findObject(o);
					if (!!g) {
						Player.modify(g, o);
					}
				}
					break;
				default:
					arr.push(o);
					break;
			}
		}
		if (arr.length > 0) {
			_createObject(arr, _modifyHandler);
		}
	};
	wb.removeObj = function(arr) {
		for (let i = 0; i < arr.length; ++i) {
			_removeHandler(arr[i]);
		}
	};
	wb.clearAll = function() {
		for (let i = 1; i < canvases.length; ++i) {
			const cc = $('#can-wb-tab-0-slide-' + i).closest('.canvas-container');
			cc.remove();
			canvases[i].dispose();
		}
		$('.room.wb.area .wb-video').remove();
		canvases.splice(1);
		canvases[0].clear();
		_updateZoomPanel();
	};
	wb.clearSlide = function(_sl) {
		if (canvases.length > _sl) {
			const canvas = canvases[_sl];
			canvas.renderOnAddRemove = false;
			let arr = canvas.getObjects();
			while (arr.length > 0) {
				canvas.remove(arr[arr.length - 1]);
				arr = canvas.getObjects();
			}
			$('.room.wb.area .wb-video.slide-' + _sl).remove();
			canvas.renderOnAddRemove = true;
			canvas.renderAll();
		}
	};
	wb.getCanvas = function() {
		return canvases[slide];
	};
	wb.eachCanvas = function(func) {
		for (let i = 0; i < canvases.length; ++i) {
			func(canvases[i]);
		}
	}
	wb.videoStatus = _videoStatus;
	return wb;
};
