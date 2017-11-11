/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var PRESENTER = 'presenter';
var WHITEBOARD = 'whiteBoard';
var DrawWbArea = function() {
	const self = {};
	let container, area, tabs, scroll, role = NONE, _inited = false;

	function refreshTabs() {
		tabs.tabs("refresh").find('ul').removeClass('ui-corner-all').removeClass('ui-widget-header');
	}
	function getActive() {
		const idx = tabs.tabs("option", 'active');
		if (idx > -1) {
			const href = tabs.find('a')[idx];
			if (!!href) {
				return $($(href).attr('href'));
			}
		}
		return null;
	}
	function _performDelete() {
		const wb = getActive().data()
			, canvas = wb.getCanvas();
		if (role !== PRESENTER || !canvas) {
			return true;
		}
		const arr = [];
		if (!!canvas.getActiveGroup()) {
			canvas.getActiveGroup().forEachObject(function(o) {
				arr.push({
					uid: o.uid
					, slide: o.slide
				});
			});
		} else {
			const o = canvas.getActiveObject();
			if (!!o) {
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
	function _deleteHandler(e) {
		switch (e.which) {
			case 8:  // backspace
			case 46: // delete
				return _performDelete();
		}
	}
	function _getWbTab(wbId) {
		return tabs.find('li[data-wb-id="' + wbId + '"]');
	}
	function _activateTab(wbId) {
		container.find('.wb-tabbar li').each(function(idx) {
			if (wbId === 1 * $(this).data('wb-id')) {
				tabs.tabs("option", "active", idx);
				$(this)[0].scrollIntoView();
				return false;
			}
		});
	}
	function _setTabName(li, name) {
		return li.find('a').attr('title', name)
			.find('span').text(name)
	}
	function _renameTab(obj) {
		_setTabName(_getWbTab(obj.wbId), obj.name);
	}
	function _resizeWbs() {
		const w = area.width(), hh = area.height()
			, wbTabs = area.find(".tabs.ui-tabs")
			, tabPanels = wbTabs.find(".ui-tabs-panel")
			, wbah = hh - 5 - wbTabs.find("ul.ui-tabs-nav").height();
		tabPanels.height(wbah);
		tabPanels.each(function() {
			$(this).data().resize(w - 25, wbah - 20);
		});
		wbTabs.find(".ui-tabs-panel .scroll-container").height(wbah);
	}
	function _addCloseBtn(li) {
		if (role !== PRESENTER) {
			return;
		}
		li.append($('#wb-tab-close').clone().attr('id', ''));
		li.find('button').click(function() {
			RoomUtil.confirmDlg('wb-confirm-remove', function() { wbAction('removeWb', JSON.stringify({wbId: li.data().wbId})); });
		});
	}
	function _getImage(cnv, fmt) {
		return cnv.toDataURL({
			format: fmt
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

	self.getWbTabId = function(id) {
		return "wb-tab-" + id;
	};
	self.getWb = function(id) {
		return $('#' + self.getWbTabId(id)).data();
	};
	self.getCanvas = function(id) {
		return self.getWb(id).getCanvas();
	};
	self.setRole = function(_role) {
		if (!_inited) return;
		role = _role;
		const tabsNav = tabs.find(".ui-tabs-nav");
		tabsNav.sortable(role === PRESENTER ? "enable" : "disable");
		const prev = tabs.find('.prev.om-icon'), next = tabs.find('.next.om-icon');
		if (role === PRESENTER) {
			if (prev.length === 0) {
				const cc = tabs.find('.wb-tabbar .scroll-container')
					, left = $('#wb-tabbar-ctrls-left').clone().attr('id', '')
					, right = $('#wb-tabbar-ctrls-right').clone().attr('id', '');
				cc.before(left).after(right);
				tabs.find('.add.om-icon').click(function() {
					wbAction('createWb');
				});
				tabs.find('.prev.om-icon').click(function() {
					scroll.scrollLeft(scroll.scrollLeft() - 30);
				});
				tabs.find('.next.om-icon').click(function() {
					scroll.scrollLeft(scroll.scrollLeft() + 30);
				});
				tabsNav.find('li').each(function() {
					const li = $(this);
					_addCloseBtn(li);
				});
				self.addDeleteHandler();
			}
		} else {
			if (prev.length > 0) {
				prev.parent().remove();
				next.parent().remove();
				tabsNav.find('li button').remove();
			}
			self.removeDeleteHandler();
		}
		tabs.find(".ui-tabs-panel").each(function() {
			$(this).data().setRole(role);
		});
	}
	self.init = function() {
		container = $(".room.wb.area");
		tabs = container.find('.tabs');
		if (tabs.length === 0) return;
		tabs.tabs({
			beforeActivate: function(e) {
				let res = true;
				if (e.originalEvent && e.originalEvent.type === 'click') {
					res = role === PRESENTER;
				}
				return res;
			}
			, activate: function(e, ui) {
				wbAction('activateWb', JSON.stringify({wbId: ui.newTab.data('wb-id')}));
			}
		});
		scroll = tabs.find('.scroll-container');
		area = container.find(".wb-area");
		tabs.find(".ui-tabs-nav").sortable({
			axis: "x"
			, stop: function() {
				refreshTabs();
			}
		});
		_inited = true;
		self.setRole(role);
		$('#wb-rename-menu').menu().find('.wb-rename').click(function() {
			_getWbTab($(this).parent().data('wb-id')).find('a span').trigger("dblclick");
		});
	};
	self.destroy = function() {
		self.removeDeleteHandler();
	};
	self.create = function(obj) {
		if (!_inited) return;
		const tid = self.getWbTabId(obj.wbId)
			, wb = $('#wb-area').clone().attr('id', tid)
			, li = $('#wb-area-tab').clone().attr('id', '').data('wb-id', obj.wbId).attr('data-wb-id', obj.wbId)
				.contextmenu(function(e) {
					if (role !== PRESENTER) {
						return;
					}
					e.preventDefault();
					$('#wb-rename-menu').show().data('wb-id', obj.wbId)
						.position({my: 'left top', collision: 'none', of: _getWbTab(obj.wbId)});
				});
		li.find('a').attr('href', "#" + tid);
		_setTabName(li, obj.name)
			.dblclick(function() {
				if (role !== PRESENTER) {
					return;
				}
				const editor = $('<input name="newName" type="text" style="color: black;"/>')
					, name = $(this).hide().after(editor.val(obj.name));
				editor.focus().blur(function() {
					const newName = $(this).val();
					if (newName !== "") {
						wbAction('renameWb', JSON.stringify({wbId: obj.wbId, name: newName}));
					}
					$(this).remove();
					name.show();
				});
			});

		tabs.find(".ui-tabs-nav").append(li);
		tabs.append(wb);
		refreshTabs();
		_addCloseBtn(li);

		const wbo = Wb();
		wbo.init(obj, tid, role);
		wb.data(wbo);
		_resizeWbs();
	}
	self.createWb = function(obj) {
		if (!_inited) return;
		self.create(obj);
		self.setRole(role);
		_activateTab(obj.wbId);
	};
	self.activateWb = function(obj) {
		if (!_inited) return;
		_activateTab(obj.wbId);
	}
	self.renameWb = function(obj) {
		if (!_inited) return;
		_renameTab(obj);
	}
	self.load = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).load(json.obj);
	};
	self.setSlide = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).setSlide(json.slide);
	};
	self.createObj = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).createObj(json.obj);
	};
	self.modifyObj = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).modifyObj(json.obj);
	};
	self.deleteObj = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).removeObj(json.obj);
	};
	self.clearAll = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).clearAll();
		Room.setSize();
	};
	self.clearSlide = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).clearSlide(json.slide);
	};
	self.removeWb = function(obj) {
		if (!_inited) return;
		const tabId = self.getWbTabId(obj.wbId);
		_getWbTab(obj.wbId).remove();
		$("#" + tabId).remove();
		refreshTabs();
	};
	self.resize = function(sbW, chW, w, h) {
		const hh = h - 5;
		container.width(w).height(h).css('left', (isRtl ? chW : sbW) + "px");
		if (!container || !_inited) return;
		area.width(w).height(hh);

		const wbTabs = area.find(".tabs.ui-tabs");
		wbTabs.height(hh);
		_resizeWbs();
	}
	self.setSize = function(json) {
		if (!_inited) return;
		self.getWb(json.wbId).setSize(json);
	}
	self.download = function(fmt) {
		if (!_inited) return;
		const wb = getActive().data();
		if ('pdf' === fmt) {
			const arr = [];
			wb.eachCanvas(function(cnv) {
				arr.push(_getImage(cnv, 'image/png'));
			});
			wbAction('downloadPdf', JSON.stringify({
				slides: arr
			}));
		} else {
			const cnv = wb.getCanvas()
				, a = document.createElement('a');
			a.setAttribute('target', '_blank')
			a.setAttribute('download', wb.name + '.' + fmt);
			a.setAttribute('href', _getImage(cnv, fmt));
			a.dispatchEvent(new MouseEvent('click', {view: window, bubbles: false, cancelable: true}));
		}
	}
	self.videoStatus = _videoStatus;
	self.loadVideos = function() {
		if (!_inited) return;
		wbAction('loadVideos');
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
	return self;
};
$(function() {
	Wicket.Event.subscribe("/websocket/message", function(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = jQuery.parseJSON(msg);
			if (m && 'wb' === m.type && typeof WbArea !== 'undefined' && !!m.func) {
				WbArea[m.func](m.param);
			}
		} catch (err) {
			//no-op
		}
	});
});
