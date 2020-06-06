/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var PRESENTER = 'PRESENTER';
var WHITEBOARD = 'WHITEBOARD';
var DrawWbArea = function() {
	const self = BaseWbArea()
		, arrowImg = new Image(), delImg = new Image();
	arrowImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAICAYAAADqSp8ZAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAygAAAMoBawMUsgAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAFsSURBVCiRrdI/SEJRFMfx37lPGxqKoGwxKJoaImhpCf8NEUFL9WgLUrPnIyEIa6reVEPQn0GeWDS4NDQETQ2JT4waojUoHBqCoJKWINB3720yIhGl+q7ncj5nuIQ6jWiaq1xmU4IwBACQ5GCAU5D8IECRAkUQzt8V++wmlSrX20e1BoFIrFdwHidIIQhH5O68sgzD/vnOF4m0QyijJGgMQIHZtJdJJ4oNg6qqNr20dKwBaOWKvZFPpZ7qXV3JH4wNSMbjJHGZ7XIlYRiiFkiBsL4CphwLwbck5E7uwMw3ClXD2iRImYYUq9lD886nLXZbyd2HL9AbXpglySOQeFVstpRJJ+5/i1UajkbbHCXahMS1ZAiS2+W1DMNmqqoqBLFMYIME1uxkvPRXDAAuTPMNhCwIGiT62eOzAQDkD+nbAjQDxudy+8mT/8C+FwjNjwuwdQnqY7b0kCesT7DC7allWVU/8D/zh3SdC/R8Aq9QhRc3h8LfAAAAAElFTkSuQmCC';
	delImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAMAAAGgrv1cAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAADNQTFRFAAAA4j094j094j094j094j094j094j094j094j094j094j094j094j094j094j094j09hIdAxgAAABB0Uk5TABAgMEBQYHCAj5+vv8/f7yMagooAAADXSURBVBgZBcEBYoQgDACw1DJETmz//9olwGn6AAAbBxoiSACTpCTtJd02smg+MPoef7UgnpPQeVM42Vg02kl+qAPeE2B19wYAgO83xi6ggRMoBfuvsUSxp+vPjag98VqwC8oI9ozC5rMnUVbw5ITID94Fo4D4umsAwN/+urvfOwDg6d8FiFUnALPnkwCs6zvg+UKcSmD3ZBWyL4hTye4J3s16AXG6J+D+uD/A7vtUAutFT9g9EacSURNX33ZPQJzKqAW8lQCIXyWAVfUM5Hz7vQAAMcZIAP9DvgiOL2K6DwAAAABJRU5ErkJggg==';
	let scroll, role = NONE, _inited = false;

	// Fabric overrides (should be kept up-to-date on fabric.js updates)
	if ('function' !== typeof(window.originalDrawControl)) {
		window.originalDrawControl = fabric.Object.prototype._drawControl;
		window.originalGetRotatedCornerCursor = fabric.Canvas.prototype._getRotatedCornerCursor;
		window.originalGetActionFromCorner = fabric.Canvas.prototype._getActionFromCorner;
		window.originalGetCornerCursor = fabric.Canvas.prototype.getCornerCursor;
		fabric.Object.prototype._drawControl = function(control, ctx, methodName, left, top, styleOverride) {
			switch (control) {
				case 'mtr':
				{
					const x = left + (this.cornerSize - arrowImg.width) / 2
						, y = top + (this.cornerSize - arrowImg.height) / 2;
					ctx.drawImage(arrowImg, x, y);
				}
					break;
				case 'tr':
				{
					if (role === PRESENTER) {
						const x = left + (this.cornerSize - delImg.width) / 2
							, y = top + (this.cornerSize - delImg.height) / 2;
						ctx.drawImage(delImg, x, y);
					} else {
						window.originalDrawControl.call(this, control, ctx, methodName, left, top, styleOverride);
					}
				}
					break;
				default:
					window.originalDrawControl.call(this, control, ctx, methodName, left, top, styleOverride);
					break;
			}
		};
		fabric.Canvas.prototype._getRotatedCornerCursor = function(corner, target, e) {
			if (role === PRESENTER && 'tr' === corner) {
				return 'pointer';
			}
			return window.originalGetRotatedCornerCursor.call(this, corner, target, e);
		};
		fabric.Canvas.prototype._getActionFromCorner = function(alreadySelected, corner, e) {
			if (role === PRESENTER && 'tr' === corner) {
				_performDelete();
				return 'none';
			}
			return window.originalGetActionFromCorner.call(this, alreadySelected, corner, e);
		};
		fabric.Canvas.prototype.getCornerCursor = function(corner, target, e) {
			if (role === PRESENTER && 'tr' === corner) {
				return 'pointer';
			}
			return window.originalGetCornerCursor.call(this, corner, target, e);
		}
	}

	function getActive() {
		const tab = $('.room-block .wb-block .tabs .wb-tab-content .wb-tab.active');
		if (tab.length === 1) {
			return tab;
		}
		return null;
	}
	function _performDelete() {
		const wb = getActive().data()
			, canvas = wb.getCanvas();
		if (role !== PRESENTER || !canvas) {
			return true;
		}
		const arr = [], objs = canvas.getActiveObjects();
		for (let i = 0; i < objs.length; ++i) {
			arr.push({
				uid: objs[i].uid
				, slide: objs[i].slide
			});
		}
		OmUtil.wbAction({action: 'deleteObj', data: {
			wbId: wb.id
			, obj: arr
		}});
		return false;
	}
	function _deleteHandler(e) {
		if ('BODY' === e.target.tagName) {
			switch (e.which) {
				case 8:  // backspace
				case 46: // delete
					e.preventDefault();
					e.stopImmediatePropagation();
					return _performDelete();
				default:
					//no-op
			}
		}
	}
	function _activateTab(wbId) {
		const link = _getWbTab(wbId);
		if (link.length > 0) {
			link.tab('show');
			if (role !== PRESENTER) {
				$('.wb-tabbar ul.nav-tabs a.nav-link').removeClass('active');
				link.addClass('active');
				$('.wb-tab-content .wb-tab').removeClass('active');
				$('#' + link.attr('aria-controls')).addClass('active');
			}
			link[0].scrollIntoView();
		}
	}
	function _setTabName(link, name) {
		return link.attr('title', name)
			.find('span').text(name)
	}
	function _getWbTab(wbId) {
		return $('#' + __getWbTabId(wbId));
	}
	function _getWbContent(wbId) {
		return $('#' + __getWbContentId(wbId));
	};
	function _renameTab(obj) {
		_setTabName(_getWbTab(obj.wbId), obj.name);
	}
	function _addCloseBtn(li) {
		if (role !== PRESENTER || li.find('button').length > 0) {
			return;
		}
		const link = li.find('a')
			, wbId = link.data('wb-id');
		// Apply right click menu only to the text item
		li.find(".nav-link").find("span").first().contextmenu(
			function(e) {
				e.preventDefault();
				$('#wb-rename-menu').show().data('wb-id', wbId)
					.position({my: 'left top', collision: 'none', of: _getWbTab(wbId)});
			});
		
		link.append(OmUtil.tmpl('#wb-tab-close'));
		li.find('button')
			.confirmation({
				confirmationEvent: 'bla'
				, onConfirm: function() {
					OmUtil.wbAction({action: 'removeWb', data: {wbId: wbId}});
				}
			});
	}
	function _getImage(cnv) {
		return cnv.toDataURL({
			format: 'image/png'
			, width: cnv.width
			, height: cnv.height
			, multiplier: 1. / cnv.getZoom()
			, left: 0
			, top: 0
		});
	}
	function _videoStatus(json) {
		self.getWb(json.wbId).videoStatus(json);
	}
	function _initVideos(arr) {
		for (let i = 0; i < arr.length; ++i) {
			 _videoStatus(arr[i]);
		}
	}
	function __getWbTabId(id) {
		return 'wb-tab-' + id;
	};
	function __getWbContentId(id) {
		return 'wb-content-' + id;
	};
	function __initTab(elems) {
		const links = elems.find('a');
		if (role === PRESENTER) {
			elems.each(function() {
				_addCloseBtn($(this));
			});
			links.prop('disabled', false).removeClass('disabled');
		} else {
			links.prop('disabled', true).addClass('disabled');
			elems.find('button').remove();
		}
		links.off()
			.click(function(e) {
				e.preventDefault();
				if (role === PRESENTER) {
					_actionActivateWb($(this).data('wb-id'));
				}
			});
	};

	self.getWb = function(id) {
		return _getWbContent(id).data();
	};
	self.getCanvas = function(id) {
		return self.getWb(id).getCanvas();
	};
	self.setRole = function(_role) {
		if (!_inited) {
			return;
		}
		role = _role;
		const tabs = $('.room-block .wb-block .tabs');
		const tabsNav = tabs.find('ul.nav-tabs');
		tabsNav.sortable(role === PRESENTER ? 'enable' : 'disable');
		const prev = tabs.find('.prev.om-icon'), next = tabs.find('.next.om-icon');
		if (role === PRESENTER) {
			if (prev.length === 0) {
				const cc = tabs.find('.wb-tabbar .scroll-container')
					, left = OmUtil.tmpl('#wb-tabbar-ctrls-left')
					, right = OmUtil.tmpl('#wb-tabbar-ctrls-right');
				cc.before(left).after(right);
				tabs.find('.add.om-icon').click(function() {
					OmUtil.wbAction({action: 'createWb'});
				});
				tabs.find('.prev.om-icon').click(function() {
					scroll.scrollLeft(scroll.scrollLeft() - 30);
				});
				tabs.find('.next.om-icon').click(function() {
					scroll.scrollLeft(scroll.scrollLeft() + 30);
				});
				self.addDeleteHandler();
			}
		} else {
			if (prev.length > 0) {
				prev.parent().remove();
				next.parent().remove();
			}
			self.removeDeleteHandler();
		}
		__initTab(tabsNav.find('li'));
		tabs.find('.wb-tab-content .wb-tab').each(function() {
			$(this).data().setRole(role);
		});
	}
	function _actionActivateWb(_wbId) {
		OmUtil.wbAction({action: 'activateWb', data: {wbId: _wbId}});
	}
	function _doInit(callback) {
		const tabs = $('.room-block .wb-block .tabs');
		if (tabs.length === 0) {
			setTimeout(_doInit, 100, callback);
			return;
		}
		scroll = tabs.find('.scroll-container');
		$('.room-block .wb-block .tabs ul.nav-tabs').sortable({
			axis: 'x'
		});
		_inited = true;
		self.setRole(role);
		if (typeof(callback) === 'function') {
			callback();
		}
		$('#wb-rename-menu').menu().find('.wb-rename').click(function() {
			const textSpan = _getWbTab($(this).parent().data('wb-id')).find('.wb-nav-tab-text').first();
			textSpan.trigger('dblclick');
		});
	}

	self.init = function(callback) {
		Wicket.Event.subscribe('/websocket/message', self.wbWsHandler);
		_doInit(callback);
	};
	self.destroy = function() {
		self.removeDeleteHandler();
		Wicket.Event.unsubscribe('/websocket/message', self.wbWsHandler);
	};
	self.create = function(obj) {
		if (!_inited) {
			return;
		}
		const tid = __getWbTabId(obj.wbId)
			, tcid = __getWbContentId(obj.wbId)
			, wb = OmUtil.tmpl('#wb-area', tcid).attr('aria-labelledby', tid)
			, li = OmUtil.tmpl('#wb-area-tab')
			, link = li.find('a');
		link.attr('id', tid).attr('data-wb-id', obj.wbId).attr('href', '#' + tcid).attr('aria-controls', tcid);
		_setTabName(link, obj.name)
			.dblclick(function() {
				if (role !== PRESENTER) {
					return;
				}
				const editor = $('<input class="newName" name="newName" type="text"/>')
					, name = $(this).hide().after(editor.val(obj.name))
					, renameWbTab = function() {
						const newName = editor.val();
						if (newName !== '') {
							OmUtil.wbAction({action: 'renameWb', data: {wbId: obj.wbId, name: newName}});
						}
						editor.remove();
						name.show();
					};
				editor.focus()
					.blur(renameWbTab)
					.keyup(function(evt) {
						if (evt.which === 13) {
							renameWbTab();
						}
					});
			});

		const tabs = $('.room-block .wb-block .tabs');
		tabs.find('ul.nav-tabs').append(li);
		tabs.find('.wb-tab-content').append(wb);
		__initTab(li);

		const wbo = Wb();
		wbo.init(obj, tcid, role);
		wb.on('remove', wbo.destroy);
		wb.data(wbo);
	}
	self.createWb = function(obj) {
		if (!_inited) {
			return;
		}
		self.create(obj);
		_activateTab(obj.wbId);
		_actionActivateWb(obj.wbId);
	};
	self.activateWb = function(obj) {
		if (!_inited) {
			return;
		}
		_activateTab(obj.wbId);
	}
	self.renameWb = function(obj) {
		if (!_inited) {
			return;
		}
		_renameTab(obj);
	}
	self.removeWb = function(obj) {
		if (!_inited) {
			return;
		}
		_getWbTab(obj.wbId).parent().remove();
		_getWbContent(obj.wbId).remove();
		_actionActivateWb(getActive().data().id);
	};
	self.load = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).load(json.obj);
	};
	self.setSlide = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).setSlide(json.slide);
	};
	self.createObj = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).createObj(json.obj);
	};
	self.modifyObj = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).modifyObj(json.obj);
	};
	self.deleteObj = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).removeObj(json.obj);
	};
	self.clearAll = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).clearAll();
	};
	self.clearSlide = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).clearSlide(json.slide);
	};
	self.setSize = function(json) {
		if (!_inited) {
			return;
		}
		self.getWb(json.wbId).setSize(json);
	}
	self.download = function(fmt) {
		if (!_inited) {
			return;
		}
		const wb = getActive().data()
			, url = location.pathname.substring(0, location.pathname.indexOf('/', 1)) + '/services/wb/uploadwb/'
			, arr = [];
		let type;
		if ('pdf' === fmt) {
			wb.eachCanvas(function(cnv) {
				arr.push(_getImage(cnv));
			});
			type = 'pdf';
		} else {
			const cnv = wb.getCanvas();
			arr.push(_getImage(cnv));
			type = 'png';
		}
		$.ajax({
			type: "POST"
			, url: url + type + '?sid=' + Room.getOptions().sid
			, data: {data: JSON.stringify(arr)}
			, dataType: 'json'
			, cache: false
		}).done(function(res) {
			if ('SUCCESS' === res.serviceResult.type) {
				OmUtil.wbAction({action: 'download', data: {
					type: type
					, fuid: res.serviceResult.message
				}});
			} else {
				OmUtil.error(res.serviceResult.message);
			}
		}).fail(function(err) {
			OmUtil.error(err);
		});
	}
	self.videoStatus = _videoStatus;
	self.loadVideos = function() {
		if (!_inited) {
			return;
		}
		OmUtil.wbAction({action: 'loadVideos'});
	};
	self.initVideos = _initVideos;
	self.addDeleteHandler = function() {
		if (role === PRESENTER) {
			$(window).keyup(_deleteHandler);
		}
	};
	self.removeDeleteHandler = function() {
		$(window).off('keyup', _deleteHandler);
	};
	self.updateAreaClass = function() {};
	self.doCleanAll = function() {
		if (!_inited) {
			return;
		}
		$('.room-block .wb-block .tabs li a').each(function() {
			const wbId = $(this).data('wb-id');
			_getWbTab(wbId).remove();
			_getWbContent(wbId).remove();
		});
	};
	return self;
};
