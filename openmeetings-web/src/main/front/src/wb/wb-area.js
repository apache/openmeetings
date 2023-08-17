/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const WbAreaBase = require('./wb-area-base');
const Role = require('./wb-role');
const Wb = require('./wb');
require('fabric'); // will produce `fabric` namespace

const arrowImg = new Image(), delImg = new Image();

arrowImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAICAYAAADqSp8ZAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAAygAAAMoBawMUsgAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAFsSURBVCiRrdI/SEJRFMfx37lPGxqKoGwxKJoaImhpCf8NEUFL9WgLUrPnIyEIa6reVEPQn0GeWDS4NDQETQ2JT4waojUoHBqCoJKWINB3720yIhGl+q7ncj5nuIQ6jWiaq1xmU4IwBACQ5GCAU5D8IECRAkUQzt8V++wmlSrX20e1BoFIrFdwHidIIQhH5O68sgzD/vnOF4m0QyijJGgMQIHZtJdJJ4oNg6qqNr20dKwBaOWKvZFPpZ7qXV3JH4wNSMbjJHGZ7XIlYRiiFkiBsL4CphwLwbck5E7uwMw3ClXD2iRImYYUq9lD886nLXZbyd2HL9AbXpglySOQeFVstpRJJ+5/i1UajkbbHCXahMS1ZAiS2+W1DMNmqqoqBLFMYIME1uxkvPRXDAAuTPMNhCwIGiT62eOzAQDkD+nbAjQDxudy+8mT/8C+FwjNjwuwdQnqY7b0kCesT7DC7allWVU/8D/zh3SdC/R8Aq9QhRc3h8LfAAAAAElFTkSuQmCC';
delImg.src = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAMAAAGgrv1cAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAADNQTFRFAAAA4j094j094j094j094j094j094j094j094j094j094j094j094j094j094j094j09hIdAxgAAABB0Uk5TABAgMEBQYHCAj5+vv8/f7yMagooAAADXSURBVBgZBcEBYoQgDACw1DJETmz//9olwGn6AAAbBxoiSACTpCTtJd02smg+MPoef7UgnpPQeVM42Vg02kl+qAPeE2B19wYAgO83xi6ggRMoBfuvsUSxp+vPjag98VqwC8oI9ozC5rMnUVbw5ITID94Fo4D4umsAwN/+urvfOwDg6d8FiFUnALPnkwCs6zvg+UKcSmD3ZBWyL4hTye4J3s16AXG6J+D+uD/A7vtUAutFT9g9EacSURNX33ZPQJzKqAW8lQCIXyWAVfUM5Hz7vQAAMcZIAP9DvgiOL2K6DwAAAABJRU5ErkJggg==';
;
function __getWbTabId(id) {
	return 'wb-tab-' + id;
}
function __getWbContentId(id) {
	return 'wb-content-' + id;
}
function _getWbTab(wbId) {
	return $('#' + __getWbTabId(wbId));
}
function _getWbContent(wbId) {
	return $('#' + __getWbContentId(wbId));
}
function _getWb(id) {
	return _getWbContent(id).data();
}
function _setTabName(link, name) {
	return link.attr('title', name)
		.find('span').text(name)
}
function _renameTab(obj) {
	_setTabName(_getWbTab(obj.wbId), obj.name);
}
function _getActive() {
	const tab = $('.room-block .wb-block .tabs .wb-tab-content .wb-tab.active');
	if (tab.length === 1) {
		return tab;
	}
	return null;
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
	_getWb(json.wbId).videoStatus(json);
}
function _initVideos(arr) {
	for (let i = 0; i < arr.length; ++i) {
		_videoStatus(arr[i]);
	}
}
function _actionActivateWb(_wbId) {
	OmUtil.wbAction({action: 'activateWb', data: {wbId: _wbId}});
}


module.exports = class DrawWbArea extends WbAreaBase {
	constructor() {
		super();
		const self = this;
		let scroll, role = Role.NONE, _inited = false;

		// Fabric overrides (should be kept up-to-date on fabric.js updates)
		if ('function' !== typeof (window.originalTr)) {
			window.originalTr = fabric.Object.prototype.controls.tr;
		}
		fabric.Object.prototype.controls.mtr.render = function(ctx, left, top, _, fabricObject) {
			ctx.save();
			ctx.translate(left, top);
			ctx.rotate(fabric.util.degreesToRadians(fabricObject.angle));
			ctx.drawImage(arrowImg, -arrowImg.width / 2, -arrowImg.height / 2);
			ctx.restore();
		};
		fabric.Object.prototype.controls.tr = new fabric.Control({
			x: 0.5
			, y: -0.5
			, cursorStyleHandler: function(eventData, control, fabricObject) {
				if (role === Role.PRESENTER) {
					return 'pointer';
				}
				return window.originalTr.cursorStyleHandler.call(this, eventData, control, fabricObject);
			}
			, getActionName: function(eventData, control, fabricObject) {
				if (role === Role.PRESENTER) {
					return 'click';
				}
				return window.originalTr.getActionName.call(this, eventData, control, fabricObject);
			}
			, mouseDownHandler: function(eventData, transformData, x, y) {
				if (role === Role.PRESENTER) {
					_performDelete();
					return true;
				}
				return window.originalTr.mouseDownHandler.call(this, eventData, transformData, x, y);
			}
			, render: function(ctx, left, top, styleOverride, fabricObject) {
				if (role === Role.PRESENTER) {
					const x = left - delImg.width / 2
						, y = top - delImg.height / 2;
					ctx.drawImage(delImg, x, y);
				} else {
					window.originalTr.render.call(this, ctx, left, top, styleOverride, fabricObject);
				}
			}
		});
		function _performDelete() {
			const wb = _getActive().data()
				, canvas = wb.getCanvas();
			if (role !== Role.PRESENTER || !canvas) {
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
				if (role !== Role.PRESENTER) {
					$('.wb-tabbar ul.nav-tabs a.nav-link').removeClass('active');
					link.addClass('active');
					$('.wb-tab-content .wb-tab').removeClass('active');
					$('#' + link.attr('aria-controls')).addClass('active');
				}
				link[0].scrollIntoView();
			}
		}
		function _addCloseBtn(li) {
			if (role !== Role.PRESENTER || li.find('button').length > 0) {
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
			const closeBtn = li.find('button');
			closeBtn.confirmation({
				title: closeBtn.attr('title')
				, confirmationEvent: 'bla'
				, container: '.room-block .wb-block .wb-area'
				, onConfirm: function() {
					const prevLi = li.prev()
						, prevWbId = prevLi.length > 0 ? prevLi.find('a').data('wb-id') : -1;
					OmUtil.wbAction({action: 'removeWb', data: {
						wbId: wbId
						, prevWbId: prevWbId
					}});
				}
			});
		}
		function __initTab(elems) {
			const links = elems.find('a');
			if (role === Role.PRESENTER) {
				elems.each(function() {
					_addCloseBtn($(this));
				});
				links.prop('disabled', false).removeClass('disabled');
			} else {
				links.prop('disabled', true).addClass('disabled');
				elems.find('button').remove();
			}
			__updateTabBarPreNextWhiteboardBTNs();
			links.off()
				.click(function(e) {
					e.preventDefault();
					if (role === Role.PRESENTER) {
						_actionActivateWb($(this).data('wb-id'));
					}
				});
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
		function __updateTabBarPreNextWhiteboardBTNs() {
			if (role === Role.PRESENTER) {
				const tabs = $('.room-block .wb-block .tabs');
				const tabsNav = tabs.find('ul.nav-tabs li');
				if (tabsNav.length > 1) {
					tabs.find('.prev.om-icon').prop('disabled', false).removeClass('disabled');
					tabs.find('.next.om-icon').prop('disabled', false).removeClass('disabled');
				} else {
					tabs.find('.prev.om-icon').prop('disabled', true).addClass('disabled');
					tabs.find('.next.om-icon').prop('disabled', true).addClass('disabled');
				}
			}
		}

		this.init = (callback) => {
			// it seems `super` can't be called from lambda
			this.wsinit();
			_doInit(callback);
		};
		this.setRole = (_role) => {
			if (!_inited) {
				return;
			}
			role = _role;
			const tabs = $('.room-block .wb-block .tabs');
			const tabsNav = tabs.find('ul.nav-tabs');
			tabsNav.sortable(role === Role.PRESENTER ? 'enable' : 'disable');
			const prev = tabs.find('.prev.om-icon'), next = tabs.find('.next.om-icon');
			if (role === Role.PRESENTER) {
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
					this.addDeleteHandler();
				}
			} else {
				if (prev.length > 0) {
					prev.parent().remove();
					next.parent().remove();
				}
				this.removeDeleteHandler();
			}
			__initTab(tabsNav.find('li'));
			tabs.find('.wb-tab-content .wb-tab').each(function() {
				$(this).data().setRole(role);
			});
		};
		this.destroy = () => {
			// it seems `super` can't be called from lambda
			this.wsdestroy();
			this.removeDeleteHandler();
		};
		this.create = (obj) => {
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
					if (role !== Role.PRESENTER) {
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

			const wbo = new Wb(obj, tcid, role);
			wb.on('remove', wbo.destroy);
			wb.data(wbo);
		};
		this.createWb = (obj) => {
			if (!_inited) {
				return;
			}
			this.create(obj);
			_activateTab(obj.wbId);
			_actionActivateWb(obj.wbId);
		};
		this.activateWb = (obj) => {
			if (!_inited) {
				return;
			}
			_activateTab(obj.wbId);
		};
		this.renameWb = (obj) => {
			if (!_inited) {
				return;
			}
			_renameTab(obj);
		};
		this.removeWb = (obj) => {
			if (!_inited) {
				return;
			}
			_getWbTab(obj.wbId).parent().remove();
			_getWbContent(obj.wbId).remove();
			_actionActivateWb(obj.prevWbId);
			__updateTabBarPreNextWhiteboardBTNs();
		};
		this.load = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).load(json.obj);
		};
		this.setSlide = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).setSlide(json.slide);
		};
		this.createObj = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).createObj(json.obj);
		};
		this.modifyObj = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).modifyObj(json.obj);
		};
		this.deleteObj = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).removeObj(json.obj);
		};
		this.clearAll = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).clearAll();
		};
		this.clearSlide = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).clearSlide(json.slide);
		};
		this.setSize = (json) => {
			if (!_inited) {
				return;
			}
			_getWb(json.wbId).setSize(json);
		};
		this.download = (fmt) => {
			if (!_inited) {
				return;
			}
			const wb = _getActive().data()
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
		};
		this.videoStatus = _videoStatus;
		this.loadVideos = () => {
			if (!_inited) {
				return;
			}
			OmUtil.wbAction({action: 'loadVideos'});
		};
		this.initVideos = _initVideos;
		this.addDeleteHandler = () => {
			if (role === Role.PRESENTER) {
				$(window).keyup(_deleteHandler);
			}
		};
		this.removeDeleteHandler = () => {
			$(window).off('keyup', _deleteHandler);
		};
		this.updateAreaClass = () => {};
		this.doCleanAll = () => {
			if (!_inited) {
				return;
			}
			$('.room-block .wb-block .tabs li a').each(function() {
				const wbId = $(this).data('wb-id');
				_getWbTab(wbId).remove();
				_getWbContent(wbId).remove();
			});
		};
		this.resize = () => {
			if (!_inited) {
				return;
			}
			const tabs = $('.room-block .wb-block .tabs');
			tabs.find('.wb-tab-content .wb-tab').each(function() {
				$(this).data().resize();
			});
		};
	}
};
