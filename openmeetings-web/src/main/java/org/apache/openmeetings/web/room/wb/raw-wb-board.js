/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Wb = function() {
	const ACTIVE = 'active', BUMPER = 100, wb = {id: -1, name: ''}, canvases = []
		, area = $('.room-block .wb-block .wb-area .tabs'), bar = area.find('.wb-tabbar')
		, extraProps = ['uid', 'fileId', 'fileType', 'count', 'slide', 'omType', '_src', 'formula'];
	let wbEl, tools, zoomBar, settings, math, mode, slide = 0, width = 0, height = 0
			, zoom = 1., zoomMode = 'PAGE_WIDTH', role = null, scrollTimeout = null;

	function _getBtn(m) {
		return !!tools ? tools.find('.om-icon.' + (m || mode) + ':not(.stub)') : null;
	}
	function _cleanActive() {
		!!tools && tools.find('.om-icon.' + ACTIVE).removeClass(ACTIVE);
	}
	function _setActive() {
		!!tools && tools.find('.om-icon.' + mode).addClass(ACTIVE);
	}
	function __validBtn(btn) {
		return !!btn && btn.length === 1
			&& typeof(btn.data) === 'function'
			&& typeof(btn.data()) === 'object'
			&& typeof(btn.data().deactivate) === 'function';
	}
	function _btnClick(toolType) {
		const b = _getBtn();
		if (__validBtn(b)) {
			b.data().deactivate();
		}
		_cleanActive();
		_getBtn('string' === typeof(toolType) && !!toolType ? toolType : $(this).data('toolType')).data().activate();
		_setActive();
	}
	function _initToolBtn(m, def, obj) {
		const btn = _getBtn(m);
		if (!btn || btn.length === 0) {
			return;
		}
		btn.data({
			obj: obj
			, toolType: m
			, activate: function() {
				if (!btn.hasClass(ACTIVE)) {
					mode = m;
					obj.activate();
				}
			}
			, deactivate: function() {
				obj.deactivate();
			}
		}).click(_btnClick);
		if (def) {
			btn.data().activate();
		}
	}
	function _setCurrent(c, _cur) {
		const hndl = c.find('a')
			, cur = _cur || c.find('div.om-icon.big:first')
		c.attr('title', cur.attr('title'));
		hndl.find('.om-icon').remove();
		hndl.prepend(cur.clone().addClass('stub').data('toolType', cur.data('toolType')));
	}
	function _initGroupHandle(c) {
		c.find('a').off().click(function(e) {
			e.stopImmediatePropagation()
			const stub = $(this).find('.stub');
			if (stub.hasClass(ACTIVE)) {
				$(this).dropdown('toggle')
			} else {
				_btnClick(stub.data('toolType'));
				stub.addClass(ACTIVE);
			}
		});
		_setCurrent(c);
	}
	function _initGroup(__id, e) {
		const c = OmUtil.tmpl(__id);
		e.after(c);
		c.find('.om-icon').each(function() {
			const cur = $(this);
			cur.click(function() {
				_setCurrent(c, cur);
			});
		});
		return c;
	}
	function _initTexts(sBtn) {
		const c = _initGroup('#wb-area-texts', _getBtn('apointer'));
		_initToolBtn('text', false, Text(wb, settings, sBtn));
		_initToolBtn('textbox', false, Textbox(wb, settings, sBtn));
		_initGroupHandle(c);
	}
	function _initDrawings(sBtn) {
		const c = _initGroup('#wb-area-drawings', tools.find('.texts'));
		_initToolBtn('eraser', false, Whiteout(wb, settings, sBtn));
		_initToolBtn('paint', false, Paint(wb, settings, sBtn));
		_initToolBtn('line', false, Line(wb, settings, sBtn));
		_initToolBtn('uline', false, ULine(wb, settings, sBtn));
		_initToolBtn('rect', false, Rect(wb, settings, sBtn));
		_initToolBtn('ellipse', false, Ellipse(wb, settings, sBtn));
		_initToolBtn('arrow', false, Arrow(wb, settings, sBtn));
		_initGroupHandle(c);
	}
	function _initCliparts(sBtn) {
		const c = OmUtil.tmpl('#wb-area-cliparts');
		tools.find('.drawings').after(c);
		c.find('.om-icon.clipart').each(function() {
			const cur = $(this);
			cur.css('background-image', 'url(' + cur.data('image') + ')')
				.click(function() {
					_setCurrent(c, cur);
				});
			_initToolBtn(cur.data('mode'), false, Clipart(wb, cur, settings, sBtn));
		});
		_initGroupHandle(c);
	}
	function _updateZoomPanel() {
		const ccount = canvases.length;
		if (ccount > 1 && role === PRESENTER) {
			zoomBar.find('.doc-group input').prop("disabled", false);
			zoomBar.find('.doc-group button').prop("disabled", false);
			zoomBar.find('.doc-group .curr-slide').removeClass("text-muted");
			zoomBar.find('.doc-group .curr-slide').addClass("text-dark");
			zoomBar.find('.doc-group .input-group-text').removeClass("text-muted");
			const ns = 1 * slide;
			zoomBar.find('.doc-group .curr-slide').val(ns + 1).attr('max', ccount);
			zoomBar.find('.doc-group .up').prop('disabled', ns < 1);
			zoomBar.find('.doc-group .down').prop('disabled', ns > ccount - 2);
			zoomBar.find('.doc-group .last-page').text(ccount);
		} else {
			zoomBar.find('.doc-group .curr-slide').val(0);
			zoomBar.find('.doc-group .last-page').text("-");
			zoomBar.find('.doc-group .curr-slide').addClass("text-muted");
			zoomBar.find('.doc-group .curr-slide').removeClass("text-dark");
			zoomBar.find('.doc-group .input-group-text').addClass("text-muted");
			zoomBar.find('.doc-group input').prop("disabled", true);
			zoomBar.find('.doc-group button').prop("disabled", true);
		}
	}
	function _setSlide(_sld) {
		const sld = 1 * _sld;
		if (sld < 0 || sld > canvases.length - 1) {
			return;
		}
		slide = _sld;
		OmUtil.wbAction({action: 'setSlide', data: {
			wbId: wb.id
			, slide: _sld
		}});
		_updateZoomPanel();
	}
	function _initSettings() {
		function setStyle(canvas, styleName, value) {
			const o = canvas.getActiveObject();
			if (o.setSelectionStyles && o.isEditing) {
				const style = {};
				style[styleName] = value;
				o.setSelectionStyles(style);
			} else {
				o[styleName] = value;
			}
			canvas.requestRenderAll();
		}
		settings.find('.wb-prop-b, .wb-prop-i')
			.button()
			.click(function() {
				$(this).toggleClass('ui-state-active selected');
				const btn = _getBtn()
					, isB = $(this).hasClass('wb-prop-b')
					, style = isB ? 'bold' : 'italic'
					, v = $(this).hasClass('selected')
					, val = v ? style : '';
				btn.data().obj.style[style] = v;
				wb.eachCanvas(function(canvas) {
					setStyle(canvas, isB ? 'fontWeight' : 'fontStyle', val)
				});
			});
		settings.find('.wb-prop-lock-color, .wb-prop-lock-fill')
			.button({icon: 'ui-icon-locked', showLabel: false})
			.click(function() {
				const btn = _getBtn()
					, isColor = $(this).hasClass('wb-prop-lock-color')
					, c = settings.find(isColor ? '.wb-prop-color' : '.wb-prop-fill')
					, enabled = $(this).button('option', 'icon') === 'ui-icon-locked';
				$(this).button('option', 'icon', enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
				c.prop('disabled', !enabled);
				btn.data().obj[isColor ? 'stroke' : 'fill'].enabled = enabled;
			});
		settings.find('.wb-prop-color').change(function() {
			const btn = _getBtn();
			if (btn.length === 1) {
				const v = $(this).val();
				btn.data().obj.stroke.color = v;
				wb.eachCanvas(function(canvas) {
					if ('paint' === mode) {
						canvas.freeDrawingBrush.color = v;
					} else {
						setStyle(canvas, 'stroke', v)
					}
				});
			}
		});
		settings.find('.wb-prop-width').change(function() {
			const btn = _getBtn();
			if (btn.length === 1) {
				const v = 1 * $(this).val();
				btn.data().obj.stroke.width = v;
				wb.eachCanvas(function(canvas) {
					if ('paint' === mode) {
						canvas.freeDrawingBrush.width = v;
					} else {
						setStyle(canvas, 'strokeWidth', v)
					}
				});
			}
		});
		settings.find('.wb-prop-fill').change(function() {
			const btn = _getBtn();
			if (btn.length === 1) {
				const v = $(this).val();
				btn.data().obj.fill.color = v;
				wb.eachCanvas(function(canvas) {
					setStyle(canvas, 'fill', v)
				});
			}
		});
		settings.find('.wb-prop-opacity').change(function() {
			const btn = _getBtn();
			if (btn.length === 1) {
				const v = (1 * $(this).val()) / 100;
				btn.data().obj.opacity = v;
				wb.eachCanvas(function(canvas) {
					if ('paint' === mode) {
						canvas.freeDrawingBrush.opacity = v;
					} else {
						setStyle(canvas, 'opacity', v)
					}
				});
			}
		});
		settings.find('.ui-dialog-titlebar-close').click(function() {
			settings.hide();
		});
		settings.draggable({
			scroll: false
			, handle: '.ui-dialog-titlebar'
			, containment: 'body'
			, start: function() {
				if (!!settings.css('bottom')) {
					settings.css('bottom', '').css(Settings.isRtl ? 'left' : 'right', '');
				}
			}
			, drag: function() {
				if (settings.position().x + settings.width() >= settings.parent().width()) {
					return false;
				}
			}
		});
	}
	function internalInit() {
		const clearAll = tools.find('.om-icon.clear-all')
			, sBtn = tools.find('.om-icon.settings');
		let _firstToolItem = true;
		switch (role) {
			case PRESENTER:
				clearAll.confirmation({
					confirmationEvent: 'bla'
					, onConfirm: function() {
						OmUtil.wbAction({action: 'clearAll', data: {wbId: wb.id}});
					}
				}).removeClass('disabled');
				zoomBar.find('.curr-slide').change(function() {
					_setSlide($(this).val() - 1);
					showCurrentSlide();
				});
				zoomBar.find('.doc-group .up').click(function () {
					_setSlide(1 * slide - 1);
					showCurrentSlide();
				});
				zoomBar.find('.doc-group .down').click(function () {
					_setSlide(1 * slide + 1);
					showCurrentSlide();
				});
				zoomBar.find('.settings-group').show().find('.settings').click(function () {
					const wbs = $('#wb-settings')
						, wbsw = wbs.find('.wbs-width').val(width)
						, wbsh = wbs.find('.wbs-height').val(height);
					function isNumeric(n) {
						return !isNaN(parseInt(n)) && isFinite(n);
					}
					wbs.modal('show');
					wbs.find('.btn-ok').off().click(function() {
						const __w = wbsw.val(), __h = wbsh.val();
						if (isNumeric(__w) && isNumeric(__h)) {
							width = parseInt(__w);
							height = parseInt(__h);
							_sendSetSize();
						}
						wbs.modal('hide');
					})
				});
			case WHITEBOARD:
				if (role === WHITEBOARD) {
					clearAll.addClass('disabled');
				}
				_initToolBtn('pointer', _firstToolItem, Pointer(wb, settings, sBtn));
				_firstToolItem = false;
				_initTexts(sBtn);
				_initDrawings(sBtn);
				_initToolBtn('math', _firstToolItem, TMath(wb, settings, sBtn));
				_initCliparts(sBtn);
				tools.find('.om-icon.settings').click(function() {
					settings.show();
				});
				tools.find('.om-icon.math').click(function() {
					math.show();
				});
				tools.find('.om-icon.clear-slide')
					.confirmation({
						confirmationEvent: 'bla'
						, onConfirm: function() {
							OmUtil.wbAction({action: 'clearSlide', data: {wbId: wb.id, slide: slide}});
						}
					});
				tools.find('.om-icon.save').click(function() {
					OmUtil.wbAction({action: 'save', data: {wbId: wb.id}});
				});
				tools.find('.om-icon.undo').click(function() {
					OmUtil.wbAction({action: 'undo', data: {wbId: wb.id}});
				});
				math.find('.ui-dialog-titlebar-close').click(function() {
					math.hide();
				});
				_initSettings();
				math.find('.update-btn').button().click(function() {
					const o = _findObject({
						uid: $(this).data('uid')
						, slide: $(this).data('slide')
					});
					const json = toOmJson(o);
					json.formula = math.find('textarea').val();
					const cnvs = canvases[o.slide];
					StaticTMath.create(json, cnvs
						, function(obj) {
							_removeHandler(o);
							cnvs.trigger('object:modified', {target: obj});
						}
						, function(msg) {
							const err = math.find('.status');
							err.text(msg);
							StaticTMath.highlight(err);
						});
				}).parent().css('text-align', Settings.isRtl ? 'left' : 'right');
				math.draggable({
					scroll: false
					, handle: '.ui-dialog-titlebar'
					, containment: 'body'
					, start: function() {
						if (!!math.css('bottom')) {
							math.css('bottom', '').css(Settings.isRtl ? 'left' : 'right', '');
						}
					}
					, drag: function() {
						if (math.position().x + math.width() >= math.parent().width()) {
							return false;
						}
					}
				});
				math.resizable({
					minHeight: 140
					, minWidth: 255
				});
			case NONE:
				_updateZoomPanel();
				zoomBar.find('.zoom-out').click(function() {
					zoom -= .2;
					if (zoom < .1) {
						zoom = .1;
					}
					zoomMode = 'ZOOM';
					_sendSetSize();
				});
				zoomBar.find('.zoom-in').click(function() {
					zoom += .2;
					zoomMode = 'ZOOM';
					_sendSetSize();
				});
				zoomBar.find('.zoom').change(function() {
					const zzz = $(this).val();
					zoomMode = 'ZOOM';
					if (isNaN(zzz)) {
						switch (zzz) {
							case 'FULL_FIT':
							case 'PAGE_WIDTH':
								zoomMode = zzz;
								break;
							case 'custom':
								zoom = 1. * $(this).data('custom-val');
								break;
							default:
								//no-op
						}
					} else {
						zoom = 1. * zzz;
					}
					_sendSetSize();
				});
				_setSize();
				_initToolBtn('apointer', _firstToolItem, APointer(wb, settings, sBtn));
			default:
				//no-op
		}
	}
	function _sendSetSize() {
		_setSize();
		OmUtil.wbAction({action: 'setSize', data: {
			wbId: wb.id
			, zoom: zoom
			, zoomMode: zoomMode
			, width: width
			, height: height
		}});
	}
	function _findObject(o) {
		let _o = null;
		const cnvs = canvases[o.slide];
		if (!!cnvs) {
			cnvs.forEachObject(function(__o) {
				if (!!__o && o.uid === __o.uid) {
					_o = __o;
					return false;
				}
			});
		}
		return _o;
	}
	function _removeHandler(o) {
		const __o = _findObject(o);
		if (!!__o) {
			const cnvs = canvases[o.slide];
			if (!!cnvs) {
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
			case 'VIDEO':
			case 'RECORDING':
				//no-op
				break;
			case 'PRESENTATION':
			{
				const ccount = canvases.length;
				for (let i = 0; i < _o.count; ++i) {
					if (canvases.length < i + 1) {
						addCanvas();
					}
					const canvas = canvases[i];
					if (_o.deleted) {
						ToolUtil.addDeletedItem(canvas, _o);
					} else {
						let scale = width / _o.width;
						scale = scale < 1 ? 1 : scale;
						canvas.setBackgroundImage(_o._src + '&slide=' + i, canvas.renderAll.bind(canvas)
								, {scaleX: scale, scaleY: scale});
					}
				}
				_updateZoomPanel();
				if (ccount !== canvases.length) {
					const b = _getBtn();
					if (__validBtn(b)) {
						b.data().deactivate();
						b.data().activate();
					}
					showCurrentSlide();
				}
			}
				break;
			default:
			{
				const canvas = canvases[_o.slide];
				if (!!canvas) {
					_o.selectable = canvas.selection;
					_o.editable = ('text' === mode || 'textbox' === mode);
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
				canvas.requestRenderAll();
			});
		});
	}

	function toOmJson(o) {
		const r = o.toJSON(extraProps);
		switch (o.omType) {
			case 'Video':
				delete r.objects;
				break;
			case 'Math':
				delete r.objects;
				break;
			default:
				//no-op
		}
		return r;
	}
	//events
	function objCreatedHandler(o) {
		if (role === NONE && o.omType !== 'pointer') {
			return;
		}
		let json;
		switch(o.omType) {
			case 'pointer':
				json = o;
				break;
			default:
				o.includeDefaultValues = false;
				json = toOmJson(o);
				break;
		}
		OmUtil.wbAction({action: 'createObj', data: {
			wbId: wb.id
			, obj: json
		}});
	}
	function objAddedHandler(e) {
		const o = e.target;
		if (o.loaded === true) {
			return;
		}
		switch(o.omType) {
			case 'textbox':
			case 'i-text':
				o.uid = uuidv4();
				o.slide = this.slide;
				objCreatedHandler(o);
				break;
			default:
				o.selectable = this.selection;
				break;
		}
	}
	function objModifiedHandler(e) {
		const o = e.target, items = [];
		if (role === NONE && o.omType !== 'pointer') {
			return;
		}
		o.includeDefaultValues = false;
		if ('activeSelection' === o.type) {
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
		OmUtil.wbAction({action: 'modifyObj', data: {
			wbId: wb.id
			, obj: items
		}});
	}
	function objSelectedHandler(e) {
		const o = e.target;
		settings.find('.wb-dim-x').val(o.left);
		settings.find('.wb-dim-y').val(o.top);
		settings.find('.wb-dim-w').val(o.width);
		settings.find('.wb-dim-h').val(o.height);
	}
	function selectionCleared(e) {
		const o = e.target;
		if (!o || '' !== o.text) {
			return;
		}
		if ('textbox' === o.omType || 'i-text' === o.omType) {
			OmUtil.wbAction({action: 'deleteObj', data: {
				wbId: wb.id
				, obj: [{
					uid: o.uid
					, slide: o.slide
				}]
			}});
		}
	}
	function pathCreatedHandler(o) {
		o.path.uid = uuidv4();
		o.path.slide = this.slide;
		o.path.omType = 'freeDraw';
		objCreatedHandler(o.path);
	}
	function scrollHandler() {
		if (scrollTimeout !== null) {
			clearTimeout(scrollTimeout);
		}
		scrollTimeout = setTimeout(function() {
			const sc = wbEl.find('.scroll-container')
				, canvases = sc.find('.canvas-container');
			if (Math.round(sc.height() + sc[0].scrollTop) === sc[0].scrollHeight) {
				if (slide !== canvases.length - 1) {
					_setSlide(canvases.length - 1);
				}
				return false;
			}
			canvases.each(function(idx) {
				const h = $(this).height(), pos = $(this).position();
				if (slide !== idx && pos.top > BUMPER - h && pos.top < BUMPER) {
					_setSlide(idx);
					return false;
				}
			});
		}, 100);
	}
	function showCurrentSlide() {
		wbEl.find('.scroll-container .canvas-container').each(function(idx) {
			if (role === PRESENTER) {
				$(this).show();
				const cclist = wbEl.find('.scroll-container .canvas-container');
				if (cclist.length > slide) {
					cclist[slide].scrollIntoView();
				}
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
		OmUtil.log('Text Edit Exit', obj);
	};
	var textChangedHandler = function (e) {
		var obj = e.target;
		OmUtil.log('Text Changed', obj);
	};*/
	function setHandlers(canvas) {
		// off everything first to prevent duplicates
		canvas.off({
			'wb:object:created': objCreatedHandler
			, 'object:modified': objModifiedHandler
			, 'object:added': objAddedHandler
			, 'object:selected': objSelectedHandler
			, 'path:created': pathCreatedHandler
			//, 'text:editing:exited': textEditedHandler
			//, 'text:changed': textChangedHandler
			, 'before:selection:cleared': selectionCleared
		});
		canvas.on({
			'wb:object:created': objCreatedHandler
			, 'object:modified': objModifiedHandler
		});
		if (role !== NONE) {
			canvas.on({
				'object:added': objAddedHandler
				, 'object:selected': objSelectedHandler
				, 'path:created': pathCreatedHandler
				, 'before:selection:cleared': selectionCleared
				//, 'text:editing:exited': textEditedHandler
				//, 'text:changed': textChangedHandler
			});
		}
	}
	function addCanvas() {
		const sl = canvases.length
			, cid = 'can-' + wb.id + '-slide-' + sl
			, c = $('<canvas></canvas>').attr('id', cid);
		wbEl.find('.canvases').append(c);
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
			case 'FULL_FIT':
				zoom = Math.min((area.width() - 30) / width, (area.height() - bar.height() - 30) / height);
				zoomBar.find('.zoom').val(zoomMode);
				break;
			case 'PAGE_WIDTH':
				zoom = (area.width() - 30 - 40) / width; // bumper + toolbar
				zoomBar.find('.zoom').val(zoomMode);
				break;
			default:
			{
				const oo = zoomBar.find('.zoom').find('option[value="' + zoom.toFixed(2) + '"]');
				if (oo.length === 1) {
					oo.prop('selected', true);
				} else {
					zoomBar.find('.zoom').data('custom-val', zoom).find('option[value=custom]')
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
	function __safeRemove(e) {
		if (typeof(e) === 'object') {
			e.remove();
		}
	}
	function __destroySettings() {
		$('#wb-settings').modal('dispose');
	}

	wb.setRole = function(_role) {
		if (role !== _role) {
			const btn = _getBtn();
			if (__validBtn(btn)) {
				btn.data().deactivate();
			}
			wbEl.find('.tools>div').remove();
			wbEl.find('.wb-tool-settings').remove();
			wbEl.find('.wb-zoom').remove();
			role = _role;
			const sc = wbEl.find('.scroll-container');
			zoomBar = OmUtil.tmpl('#wb-zoom');
			__safeRemove(tools);
			__safeRemove(settings);
			__safeRemove(math);
			if (role === NONE) {
				__destroySettings();
				tools = !!Room.getOptions().questions ? OmUtil.tmpl('#wb-tools-readonly') : wbEl.find('invalid-wb-element');
				sc.off('scroll', scrollHandler);
			} else {
				tools = OmUtil.tmpl('#wb-tools');
				settings = OmUtil.tmpl('#wb-tool-settings');
				settings[0].style.display = 'none';
				settings[0].style.bottom = '100px';
				settings[0].style[(Settings.isRtl ? 'left' : 'right')] = '100px';
				math = OmUtil.tmpl('#wb-formula');
				math[0].style.display = 'none';
				math[0].style.bottom = '100px';
				math[0].style[(Settings.isRtl ? 'left' : 'right')] = '100px';
				wbEl.append(settings, math);
				sc.on('scroll', scrollHandler);
			}
			wbEl.find('.tools').append(tools);
			wbEl.find('.wb-zoom-block').append(zoomBar);
			showCurrentSlide();
			tools = wbEl.find('.tools>div');
			settings = wbEl.find('.wb-tool-settings');
			wb.eachCanvas(function(canvas) {
				setHandlers(canvas);
				canvas.forEachObject(function(__o) {
					if (!!__o && __o.omType === 'Video') {
						__o.setPlayable(role);
					}
				});
			});
			internalInit();
		}
	};
	wb.init = function(wbo, tcid, _role) {
		wb.id = wbo.wbId;
		wb.name = wbo.name;
		width = wbo.width;
		height = wbo.height;
		zoom = wbo.zoom;
		zoomMode = wbo.zoomMode;
		wbEl = $('#' + tcid);
		addCanvas();
		wb.setRole(_role);
	};
	wb.setSize = function(wbo) {
		width = wbo.width;
		height = wbo.height;
		zoom = wbo.zoom;
		zoomMode = wbo.zoomMode;
		_setSize();
	};
	wb.resize = function() {
		if (zoomMode !== 'ZOOM') {
			_setSize();
		}
	};
	wb.setSlide = function(_sl) {
		slide = _sl;
		showCurrentSlide();
	};
	wb.createObj = function(obj) {
		const arr = [], del = [], _arr = Array.isArray(obj) ? obj : [obj];
		for (let i = 0; i < _arr.length; ++i) {
			const o = _arr[i];
			if (!!o.deleted && 'PRESENTATION' !== o.fileType) {
				del.push(o);
				continue;
			}
			switch(o.omType) {
				case 'pointer':
					APointer(wb).create(canvases[o.slide], o);
					break;
				case 'Video':
					Player.create(canvases[o.slide], o, wb);
					break;
				case 'Math':
					StaticTMath.create(o, canvases[o.slide]);
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
		for (let i = 0; i < del.length; ++i) {
			const o = del[i];
			ToolUtil.addDeletedItem(canvases[o.slide], o);
		}
	};
	wb.load = wb.createObj;
	wb.modifyObj = function(obj) { //TODO need to be unified
		const arr = [], _arr = Array.isArray(obj) ? obj : [obj];
		for (let i = 0; i < _arr.length; ++i) {
			const o = _arr[i];
			switch(o.omType) {
				case 'pointer':
					_modifyHandler(APointer(wb).create(canvases[o.slide], o));
					break;
				case 'Video':
				{
					const g = _findObject(o);
					if (!!g) {
						Player.modify(g, o);
					}
				}
					break;
				case 'Math':
				{
					_removeHandler(o);
					StaticTMath.create(o, canvases[o.slide]);
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
			const cc = $('#can-' + wb.id + '-slide-' + i).closest('.canvas-container');
			cc.remove();
			canvases[i].dispose();
		}
		$('.room-block .wb-block .wb-video').remove();
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
			$('.room-block .wb-block .wb-video.slide-' + _sl).remove();
			canvas.renderOnAddRemove = true;
			canvas.requestRenderAll();
		}
	};
	wb.getCanvas = function() {
		return canvases[slide];
	};
	wb.eachCanvas = function(func) {
		for (let i = 0; i < canvases.length; ++i) {
			func(canvases[i]);
		}
	};
	wb.videoStatus = _videoStatus;
	wb.getRole = function() {
		return role;
	};
	wb.getFormula = function() {
		return math;
	};
	wb.getZoom = function() {
		return zoom;
	};
	wb.destroy = function() {
		__destroySettings();
	};
	return wb;
};
