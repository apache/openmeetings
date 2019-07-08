/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Wb = function() {
	const ACTIVE = 'active', BUMPER = 100, wb = {id: -1, name: ''}, canvases = []
		, area = $('.room.wb.area .wb-area .tabs.ui-tabs'), bar = area.find('.wb-tabbar')
		, extraProps = ['uid', 'fileId', 'fileType', 'count', 'slide', 'omType', '_src', 'formula'];
	let a, t, z, s, f, mode, slide = 0, width = 0, height = 0
			, zoom = 1., zoomMode = 'pageWidth', role = null, scrollTimeout = null;

	function _getBtn(m) {
		return !!t ? t.find('.om-icon.' + (m || mode) + ':not(.stub)') : null;
	}
	function _cleanActive() {
		!!t && t.find('.om-icon.' + ACTIVE).removeClass(ACTIVE);
	}
	function _setActive() {
		!!t && t.find('.om-icon.' + mode).addClass(ACTIVE);
	}
	function _btnClick(toolType) {
		const b = _getBtn();
		if (b.length && b.hasClass(ACTIVE)) {
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
		_initToolBtn('text', false, Text(wb, s, sBtn));
		_initToolBtn('textbox', false, Textbox(wb, s, sBtn));
		_initGroupHandle(c);
	}
	function _initDrawings(sBtn) {
		const c = _initGroup('#wb-area-drawings', t.find('.texts'));
		_initToolBtn('eraser', false, Whiteout(wb, s, sBtn));
		_initToolBtn('paint', false, Paint(wb, s, sBtn));
		_initToolBtn('line', false, Line(wb, s, sBtn));
		_initToolBtn('uline', false, ULine(wb, s, sBtn));
		_initToolBtn('rect', false, Rect(wb, s, sBtn));
		_initToolBtn('ellipse', false, Ellipse(wb, s, sBtn));
		_initToolBtn('arrow', false, Arrow(wb, s, sBtn));
		_initGroupHandle(c);
	}
	function _initCliparts(sBtn) {
		const c = OmUtil.tmpl('#wb-area-cliparts');
		t.find('.drawings').after(c);
		c.find('.om-icon.clipart').each(function() {
			const cur = $(this);
			cur.css('background-image', 'url(' + cur.data('image') + ')')
				.click(function() {
					_setCurrent(c, cur);
				});
			_initToolBtn(cur.data('mode'), false, Clipart(wb, cur, s, sBtn));
		});
		_initGroupHandle(c);
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
	function _initSettings() {
		function setStyle(canvas, styleName, value) {
			const o = canvas.getActiveObject();
			if (o.setSelectionStyles && o.isEditing) {
				let style = {};
				style[styleName] = value;
				o.setSelectionStyles(style);
			} else {
				o[styleName] = value;
			}
			canvas.requestRenderAll();
		}
		s.find('.wb-prop-b, .wb-prop-i')
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
		s.find('.wb-prop-lock-color, .wb-prop-lock-fill')
			.button({icon: 'ui-icon-locked', showLabel: false})
			.click(function() {
				const btn = _getBtn()
					, isColor = $(this).hasClass('wb-prop-lock-color')
					, c = s.find(isColor ? '.wb-prop-color' : '.wb-prop-fill')
					, enabled = $(this).button('option', 'icon') === 'ui-icon-locked';
				$(this).button('option', 'icon', enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
				c.prop('disabled', !enabled);
				btn.data().obj[isColor ? 'stroke' : 'fill'].enabled = enabled;
			});
		s.find('.wb-prop-color').change(function() {
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
		s.find('.wb-prop-width').change(function() {
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
		s.find('.wb-prop-fill').change(function() {
			const btn = _getBtn();
			if (btn.length === 1) {
				const v = $(this).val();
				btn.data().obj.fill.color = v;
				wb.eachCanvas(function(canvas) {
					setStyle(canvas, 'fill', v)
				});
			}
		});
		s.find('.wb-prop-opacity').change(function() {
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
	}
	function internalInit() {
		t.draggable({
			snap: 'parent'
			, containment: 'parent'
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
			snap: 'parent'
			, containment: 'parent'
			, scroll: false
		});
		const clearAll = t.find('.om-icon.clear-all')
			, sBtn = t.find('.om-icon.settings');
		let _firstToolItem = true;
		switch (role) {
			case PRESENTER:
				clearAll.click(function() {
					OmUtil.confirmDlg('clear-all-confirm', function() { wbAction('clearAll', JSON.stringify({wbId: wb.id})); });
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
				z.find('.settings-group').show().find('.settings').click(function () {
					const wbs = $('#wb-settings')
						, wbsw = wbs.find('.wbs-width').val(width)
						, wbsh = wbs.find('.wbs-height').val(height);
					function isNumeric(n) {
						return !isNaN(parseInt(n)) && isFinite(n);
					}
					wbs.dialog({
						buttons: [
							{
								text: wbs.data('btn-ok')
								, click: function() {
									const __w = wbsw.val(), __h = wbsh.val();
									if (isNumeric(__w) && isNumeric(__h)) {
										width = parseInt(__w);
										height = parseInt(__h);
										_sendSetSize();
									}
									$(this).dialog("close");
								}
							}
							, {
								text: wbs.data('btn-cancel')
								, click: function() {
									$(this).dialog("close");
								}
							}
						]
					});
				});
			case WHITEBOARD:
				if (role === WHITEBOARD) {
					clearAll.addClass('disabled');
				}
				_initToolBtn('pointer', _firstToolItem, Pointer(wb, s, sBtn));
				_firstToolItem = false;
				_initTexts(sBtn);
				_initDrawings(sBtn);
				_initToolBtn('math', _firstToolItem, TMath(wb, s, sBtn));
				_initCliparts(sBtn);
				t.find('.om-icon.settings').click(function() {
					s.show();
				});
				t.find('.om-icon.math').click(function() {
					f.show();
				});
				t.find('.om-icon.clear-slide').click(function() {
					OmUtil.confirmDlg('clear-slide-confirm', function() { wbAction('clearSlide', JSON.stringify({wbId: wb.id, slide: slide})); });
				});
				t.find('.om-icon.save').click(function() {
					wbAction('save', JSON.stringify({wbId: wb.id}));
				});
				t.find('.om-icon.undo').click(function() {
					wbAction('undo', JSON.stringify({wbId: wb.id}));
				});
				f.find('.ui-dialog-titlebar-close').click(function() {
					f.hide();
				});
				_initSettings();
				f.find('.update-btn').button().click(function() {
					const o = _findObject({
						uid: $(this).data('uid')
						, slide: $(this).data('slide')
					});
					const json = toOmJson(o);
					json.formula = f.find('textarea').val();
					const cnvs = canvases[o.slide];
					StaticTMath.create(json, cnvs
						, function(obj) {
							_removeHandler(o);
							cnvs.trigger('object:modified', {target: obj});
						}
						, function(msg) {
							const err = f.find('.status');
							err.text(msg);
							StaticTMath.highlight(err);
						});
				}).parent().css('text-align', Settings.isRtl ? 'left' : 'right');
				f.draggable({
					scroll: false
					, containment: 'body'
					, start: function() {
						if (!!f.css('bottom')) {
							f.css('bottom', '').css(Settings.isRtl ? 'left' : 'right', '');
						}
					}
					, drag: function() {
						if (f.position().x + f.width() >= f.parent().width()) {
							return false;
						}
					}
				}).resizable({
					alsoResize: f.find('.text-container')
				});
			case NONE:
				_updateZoomPanel();
				z.find('.zoom-out').click(function() {
					zoom -= .2;
					if (zoom < .1) {
						zoom = .1;
					}
					zoomMode = 'zoom';
					_sendSetSize();
				});
				z.find('.zoom-in').click(function() {
					zoom += .2;
					zoomMode = 'zoom';
					_sendSetSize();
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
					_sendSetSize();
				});
				_setSize();
				_initToolBtn('apointer', _firstToolItem, APointer(wb, s, sBtn));
			default:
				//no-op
		}
	}
	function _sendSetSize() {
		_setSize();
		wbAction('setSize', JSON.stringify({
			wbId: wb.id
			, zoom: zoom
			, zoomMode: zoomMode
			, width: width
			, height: height
		}));
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
			case 'Video':
			case 'Recording':
				//no-op
				break;
			case 'Presentation':
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
					if (b.length && b.hasClass(ACTIVE)) {
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
	};

	function toOmJson(o) {
		const r = o.toJSON(extraProps);
		switch (o.omType) {
			case 'Video':
				r.type = 'video';
				delete r.objects;
				break;
			case 'Math':
				r.type = 'math';
				delete r.objects;
				break;
		}
		return r;
	}
	//events
	function objCreatedHandler(o) {
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
			case 'textbox':
			case 'i-text':
				o.uid = UUID.generate();
				o.slide = this.slide;
				objCreatedHandler(o);
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
	function selectionCleared(e) {
		const o = e.target;
		if (!o || '' !== o.text) {
			return;
		}
		if ('textbox' === o.type || 'i-text' === o.type) {
			wbAction('deleteObj', JSON.stringify({
				wbId: wb.id
				, obj: [{
					uid: o.uid
					, slide: o.slide
				}]
			}));
		}
	}
	function pathCreatedHandler(o) {
		o.path.uid = UUID.generate();
		o.path.slide = this.slide;
		objCreatedHandler(o.path);
	};
	function scrollHandler() {
		if (scrollTimeout !== null) {
			clearTimeout(scrollTimeout);
		}
		scrollTimeout = setTimeout(function() {
			const sc = a.find('.scroll-container')
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
		a.find('.scroll-container .canvas-container').each(function(idx) {
			if (role === PRESENTER) {
				$(this).show();
				const cclist = a.find('.scroll-container .canvas-container');
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
		console.log('Text Edit Exit', obj);
	};
	var textChangedHandler = function (e) {
		var obj = e.target;
		console.log('Text Changed', obj);
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
				zoom = Math.min((area.width() - 10) / width, (area.height() - bar.height() - 10) / height);
				z.find('.zoom').val(zoomMode);
				break;
			case 'pageWidth':
				zoom = (area.width() - 10) / width;
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
	function __safeRemove(e) {
		if (typeof(e) === 'object') {
			e.remove();
		}
	}
	function __destroySettings() {
		const wbs = $('#wb-settings');
		if (wbs.dialog('instance')) {
			wbs.dialog('destroy');
		}
	}

	wb.setRole = function(_role) {
		if (role !== _role) {
			const btn = _getBtn();
			if (!!btn && btn.length === 1) {
				btn.data().deactivate();
			}
			a.find('.tools').remove();
			a.find('.wb-tool-settings').remove();
			a.find('.wb-zoom').remove();
			role = _role;
			const sc = a.find('.scroll-container');
			z = OmUtil.tmpl('#wb-zoom')
				.attr('style', 'position: absolute; top: 0px; ' + (Settings.isRtl ? 'right' : 'left') + ': 80px;');
			__safeRemove(t);
			__safeRemove(s);
			__safeRemove(f);
			if (role === NONE) {
				__destroySettings();
				t = !!Room.getOptions().questions ? OmUtil.tmpl('#wb-tools-readonly') : a.find('invalid-wb-element');
				sc.off('scroll', scrollHandler);
			} else {
				t = OmUtil.tmpl('#wb-tools');
				s = OmUtil.tmpl('#wb-tool-settings')
					.attr('style', 'display: none; bottom: 100px; ' + (Settings.isRtl ? 'left' : 'right') + ': 100px;');
				f = OmUtil.tmpl('#wb-formula')
					.attr('style', 'display: none; bottom: 100px; ' + (Settings.isRtl ? 'left' : 'right') + ': 100px;');
				a.append(s, f);
				sc.on('scroll', scrollHandler);
			}
			t.attr('style', 'position: absolute; top: 20px; ' + (Settings.isRtl ? 'left' : 'right') + ': 20px;');
			a.append(t).append(z);
			showCurrentSlide();
			t = a.find('.tools'), s = a.find('.wb-tool-settings');
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
		if (t.length === 1 && t.position().left + t.width() > a.width()) {
			t.position({
				my: (Settings.isRtl ? 'left' : 'right')
				, at: (Settings.isRtl ? 'left' : 'right') + '-20'
				, of: '#' + a[0].id
				, collision: 'fit'
			});
		}
		if (z.position().left + z.width() > a.width()) {
			z.position({
				my: (Settings.isRtl ? 'right' : 'left') + ' top'
				, at: 'center top'
				, of: '#' + a[0].id
				, collision: 'fit'
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
		const arr = [], del = [], _arr = Array.isArray(obj) ? obj : [obj];
		for (let i = 0; i < _arr.length; ++i) {
			const o = _arr[i];
			if (!!o.deleted && 'Presentation' !== o.fileType) {
				del.push(o);
				continue;
			}
			switch(o.type) {
				case 'pointer':
					APointer(wb).create(canvases[o.slide], o);
					break;
				case 'video':
					Player.create(canvases[o.slide], o, wb);
					break;
				case 'math':
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
			switch(o.type) {
				case 'pointer':
					_modifyHandler(APointer(wb).create(canvases[o.slide], o))
					break;
				case 'video':
				{
					const g = _findObject(o);
					if (!!g) {
						Player.modify(g, o);
					}
				}
					break;
				case 'math':
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
			const cc = $('#can-wb-tab-' + wb.id + '-slide-' + i).closest('.canvas-container');
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
		return f;
	};
	wb.getZoom = function() {
		return zoom;
	}
	wb.destroy = function() {
		__destroySettings();
	}
	return wb;
};
