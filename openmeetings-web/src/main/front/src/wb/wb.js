/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const Role = require('./wb-role');
const WbTools = require('./wb-tools');
const WbZoom = require('./wb-zoom');
const APointer = require('./wb-tool-apointer');
const Player = require('./wb-player');
const TMath = require('./wb-tool-math');
const StaticTMath = require('./wb-tool-stat-math');
require('fabric'); // will produce `fabric` namespace

const BUMPER = 100
	, extraProps = ['uid', 'fileId', 'fileType', 'count', 'slide', 'omType', '_src', 'formula'];


module.exports = class Wb {
	constructor(wbo, tcid, _role) {
		this.id = wbo.wbId;
		this.title = wbo.name;
		this.width = wbo.width;
		this.height = wbo.height;
		this.slide = 0;

		const canvases = [], self = this;
		let wbEl, tools, zoomBar
			, role = null, scrollTimeout = null;

		function _removeHandler(o) {
			const __o = self._findObject(o);
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
							let scale = self.width / _o.width;
							scale = scale < 1 ? 1 : scale;
							canvas.setBackgroundImage(_o._src + '&slide=' + i, canvas.renderAll.bind(canvas)
									, {scaleX: scale, scaleY: scale});
						}
					}
					zoomBar.update(role, canvases.length);
					if (ccount !== canvases.length) {
						tools.reactivateBtn();
						self._showCurrentSlide();
					}
				}
					break;
				default:
				{
					const canvas = canvases[_o.slide];
					if (!!canvas) {
						_o.selectable = canvas.selection;
						_o.editable = ('text' === tools.getMode() || 'textbox' === tools.getMode());
						canvas.add(_o);
					}
				}
					break;
			}
		}
		function _createObject(arr, handler) {
			fabric.util.enlivenObjects(arr, function(objects) {
				for (let i = 0; i < objects.length; ++i) {
					const _o = objects[i];
					_o.loaded = true;
					handler(_o);
				}

				self.eachCanvas(function(canvas) {
					canvas.requestRenderAll();
				});
			});
		}

		//events
		function objCreatedHandler(o) {
			if (role === Role.NONE && o.omType !== 'pointer') {
				return;
			}
			let json;
			switch(o.omType) {
				case 'pointer':
					json = o;
					break;
				default:
					o.includeDefaultValues = false;
					json = self._toOmJson(o);
					break;
			}
			OmUtil.wbAction({action: 'createObj', data: {
				wbId: self.id
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
					o.uid = crypto.randomUUID();
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
			if (role === Role.NONE && o.omType !== 'pointer') {
				return;
			}
			function modifiedAction(items) {
				OmUtil.wbAction({action: 'modifyObj', data: {
					wbId: self.id
					, obj: items
				}});
			}
			o.includeDefaultValues = false;
			if ('activeSelection' === o.type) {
				o.clone(function(_o) {
					// ungrouping
					_o.includeDefaultValues = false;
					const _items = _o.destroy().getObjects();
					for (let i = 0; i < _items.length; ++i) {
						items.push(self._toOmJson(_items[i]));
					}
					modifiedAction(items);
				}, extraProps);
			} else {
				items.push(self._toOmJson(o));
				modifiedAction(items);
			}
		}
		function objSelectedHandler(e) {
			tools.updateCoordinates(e.target);
		}
		function selectionCleared(e) {
			const o = e.target;
			if (!o || '' !== o.text) {
				return;
			}
			if ('textbox' === o.omType || 'i-text' === o.omType) {
				OmUtil.wbAction({action: 'deleteObj', data: {
					wbId: self.id
					, obj: [{
						uid: o.uid
						, slide: o.slide
					}]
				}});
			}
		}
		function pathCreatedHandler(o) {
			o.path.uid = crypto.randomUUID();
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
					if (self.slide !== canvases.length - 1) {
						self._doSetSlide(canvases.length - 1);
					}
					return false;
				}
				canvases.each(function(idx) {
					const h = $(this).height(), pos = $(this).position();
					if (self.slide !== idx && pos.top > BUMPER - h && pos.top < BUMPER) {
						self._doSetSlide(idx);
						return false;
					}
				});
			}, 100);
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
			if (role !== Role.NONE) {
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
				, cid = 'can-' + self.id + '-slide-' + sl
				, c = $('<canvas></canvas>').attr('id', cid);
			wbEl.find('.canvases').append(c);
			const canvas = new fabric.Canvas(c.attr('id'), {
				preserveObjectStacking: true
			});
			canvas.wbId = self.id;
			canvas.slide = sl;
			canvases.push(canvas);
			const cc = $('#' + cid).closest('.canvas-container');
			if (role === Role.NONE) {
				if (sl === self.slide) {
					cc.show();
				} else {
					cc.hide();
				}
			}
			__setSize(canvas);
			setHandlers(canvas);
		}
		function __setSize(_cnv) {
			_cnv.setWidth(zoomBar.getZoom() * self.width)
				.setHeight(zoomBar.getZoom() * self.height)
				.setZoom(zoomBar.getZoom());
		}
		function _setSize(skipSendWsMsg) {
			zoomBar.setSize();
			self.eachCanvas(function(canvas) {
				__setSize(canvas);
			});
			if (!skipSendWsMsg) {
				self._doSetSlide(self.slide);
			}
		}
		function _videoStatus(json) {
			const g = self._findObject(json);
			if (!!g) {
				g.videoStatus(json.status);
			}
		}

		this._toOmJson = (o) => {
			const r = o.toJSON(extraProps);
			switch (o.omType) {
				case 'Video':
					delete r.objects;
					break;
				case TMath.TYPE:
					delete r.objects;
					break;
				default:
					//no-op
			}
			return r;
		};
		this._findObject = (o) => {
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
		};
		this.setRole = (_role) => {
			if (role !== _role) {
				role = _role;
				const sc = wbEl.find('.scroll-container');
				if (role === Role.NONE) {
					sc.off('scroll', scrollHandler);
				} else {
					sc.on('scroll', scrollHandler);
				}
				self._showCurrentSlide();
				this.eachCanvas(function(canvas) {
					setHandlers(canvas);
					canvas.forEachObject(function(__o) {
						if (!!__o && __o.omType === 'Video') {
							__o.setPlayable(role);
						}
					});
				});
				tools.setRole(role);
				zoomBar.setRole(role);
				zoomBar.update(role, canvases.length);
				_setSize();
			}
		};
		this.setSize = (wbo) => {
			this.width = wbo.width;
			this.height = wbo.height;
			zoomBar.init(wbo);
			_setSize();
		};
		this.doSetSize = _setSize;
		this.resize = () => {
			if (zoomBar.getMode() !== 'ZOOM') {
				_setSize(true);
			}
		};
		this._showCurrentSlide = () => {
			wbEl.find('.scroll-container .canvas-container').each(function(idx) {
				if (role === Role.PRESENTER) {
					$(this).show();
					const cclist = wbEl.find('.scroll-container .canvas-container');
					if (cclist.length > self.slide) {
						cclist[self.slide].scrollIntoView();
					}
				} else {
					if (idx === self.slide) {
						$(this).show();
					} else {
						$(this).hide();
					}
				}
			});
		};
		this._doSetSlide = (_sld) => {
			const sld = 1 * _sld;
			if (sld < 0 || sld > canvases.length - 1) {
				return;
			}
			self.slide = _sld;
			OmUtil.wbAction({action: 'setSlide', data: {
				wbId: self.id
				, slide: _sld
			}});
			zoomBar.update(role, canvases.length);
		};
		this.setSlide = (_sl) => {
			self.slide = _sl;
			self._showCurrentSlide();
		};
		this.createObj = (obj) => {
			const arr = [], del = [], _arr = Array.isArray(obj) ? obj : [obj];
			for (let i = 0; i < _arr.length; ++i) {
				const o = _arr[i];
				if (!!o.deleted && 'PRESENTATION' !== o.fileType) {
					del.push(o);
					continue;
				}
				switch(o.omType) {
					case 'pointer':
						new APointer(this).create(canvases[o.slide], o);
						break;
					case 'Video':
						Player.create(canvases[o.slide], o, self);
						break;
					case TMath.TYPE:
						StaticTMath.create(o, canvases[o.slide]);
						break;
					default:
					{
						const __o = self._findObject(o);
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
		this.load = this.createObj;
		this.modifyObj = (obj) => { //TODO need to be unified
			const arr = [], _arr = Array.isArray(obj) ? obj : [obj];
			for (let i = 0; i < _arr.length; ++i) {
				const o = _arr[i];
				switch(o.omType) {
					case 'pointer':
						_modifyHandler(new APointer(this).create(canvases[o.slide], o));
						break;
					case 'Video':
					{
						const g = self._findObject(o);
						if (!!g) {
							Player.modify(g, o);
						}
					}
						break;
					case TMath.TYPE:
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
		this.removeObj = (arr) => {
			for (let i = 0; i < arr.length; ++i) {
				_removeHandler(arr[i]);
			}
		};
		this.clearAll = () => {
			for (let i = 1; i < canvases.length; ++i) {
				const cc = $('#can-' + this.id + '-slide-' + i).closest('.canvas-container');
				cc.remove();
				canvases[i].dispose();
			}
			$('.room-block .wb-block .wb-video').remove();
			canvases.splice(1);
			canvases[0].clear();
			zoomBar.update(role, canvases.length);
		};
		this.clearSlide = (_sl) => {
			if (canvases.length > _sl) {
				const canvas = canvases[_sl];
				let arr = canvas.getObjects();
				while (arr.length > 0) {
					canvas.remove(arr[arr.length - 1]);
					arr = canvas.getObjects();
				}
				$('.room-block .wb-block .wb-video.slide-' + _sl).remove();
				canvas.requestRenderAll();
			}
		};
		this.getCanvas = (_slide) => {
			return canvases[typeof(_slide) === 'number' ? _slide : self.slide];
		};
		this.eachCanvas = (func) => {
			for (let i = 0; i < canvases.length; ++i) {
				func(canvases[i]);
			}
		};
		this.videoStatus = _videoStatus;
		this.getRole = () => {
			return role;
		};
		this.getFormula = () => {
			return tools.getMath();
		};
		this.getZoom = () => {
			return zoomBar.getZoom();
		};
		this.destroy = () => {
			tools.destroy();
		};

		wbEl = $('#' + tcid);
		tools = new WbTools(wbEl, this);
		zoomBar = new WbZoom(wbEl, this);
		zoomBar.init(wbo);
		addCanvas();
		this.setRole(_role);
	}

	getId() {
		return this.id;
	}
};
