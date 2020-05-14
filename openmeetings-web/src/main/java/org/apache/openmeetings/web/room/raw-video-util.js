/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WB_AREA_SEL = '.room-block .wb-block';
const WBA_WB_SEL = '.room-block .wb-block .wb-tab-content';
const VIDWIN_SEL = '.video.user-video';
const VID_SEL = '.video-container[id!=user-video]';
const CAM_ACTIVITY = 'VIDEO';
const MIC_ACTIVITY = 'AUDIO';
const SCREEN_ACTIVITY = 'SCREEN';
const REC_ACTIVITY = 'RECORD';
var VideoUtil = (function() {
	const self = {};
	function _getVid(uid) {
		return 'video' + uid;
	}
	function _isSharing(sd) {
		return !!sd && 'SCREEN' === sd.type && sd.activities.includes(SCREEN_ACTIVITY);
	}
	function _isRecording(sd) {
		return !!sd && 'SCREEN' === sd.type && sd.activities.includes(REC_ACTIVITY);
	}
	function _hasMic(sd) {
		return !sd || sd.activities.includes(MIC_ACTIVITY);
	}
	function _hasCam(sd) {
		return !sd || sd.activities.includes(CAM_ACTIVITY);
	}
	function _hasVideo(sd) {
		return _hasCam(sd) || _isSharing(sd) || _isRecording(sd);
	}
	function _getRects(sel, excl) {
		const list = [], elems = $(sel);
		for (let i = 0; i < elems.length; ++i) {
			if (excl !== $(elems[i]).attr('aria-describedby')) {
				list.push(_getRect(elems[i]));
			}
		}
		return list;
	}
	function _getRect(e) {
		const win = $(e), winoff = win.offset();
		return {left: winoff.left
			, top: winoff.top
			, right: winoff.left + win.width()
			, bottom: winoff.top + win.height()};
	}
	function _container() {
		const a = $(WB_AREA_SEL);
		const c = a.find('.wb-area .tabs .wb-tab-content');
		return c.length > 0 ? $(WBA_WB_SEL) : a;
	}
	function __processTopToBottom(area, rectNew, list) {
		const offsetX = 20
			, offsetY = 10;

		let minY = area.bottom, posFound;
		do {
			posFound = true;
			for (let i = 0; i < list.length; ++i) {
				const rect = list[i];
				minY = Math.min(minY, rect.bottom);

				if (rectNew.left < rect.right && rectNew.right > rect.left && rectNew.top < rect.bottom && rectNew.bottom > rect.top) {
					rectNew.left = rect.right + offsetX;
					posFound = false;
				}
				if (rectNew.right >= area.right) {
					rectNew.left = area.left;
					rectNew.top = Math.max(minY, rectNew.top) + offsetY;
					posFound = false;
				}
				if (rectNew.bottom >= area.bottom) {
					rectNew.top = area.top;
					posFound = true;
					break;
				}
			}
		} while (!posFound);
		return {left: rectNew.left, top: rectNew.top};
	}
	function __processEqualsBottomToTop(area, rectNew, list) {
		const offsetX = 20
			, offsetY = 10;

		rectNew.bottom = area.bottom;
		let minY = area.bottom, posFound;
		do {
			posFound = true;
			for (let i = 0; i < list.length; ++i) {
				const rect = list[i];
				minY = Math.min(minY, rect.top);

				if (rectNew.left < rect.right && rectNew.right > rect.left && rectNew.top < rect.bottom && rectNew.bottom > rect.top) {
					rectNew.left = rect.right + offsetX;
					posFound = false;
				}
				if (rectNew.right >= area.right) {
					rectNew.left = area.left;
					rectNew.bottom = Math.min(minY, rectNew.top) - offsetY;
					posFound = false;
				}
				if (rectNew.top <= area.top) {
					rectNew.top = area.top;
					posFound = true;
					break;
				}
			}
		} while (!posFound);
		return {left: rectNew.left, top: rectNew.top};
	}
	function _getPos(list, w, h, _processor) {
		if (Room.getOptions().interview) {
			return {left: 0, top: 0};
		}
		const wba = _container()
			, woffset = wba.offset()
			, area = {left: woffset.left, top: woffset.top, right: woffset.left + wba.width(), bottom: woffset.top + wba.height()}
			, rectNew = {
				_left: area.left
				, _top: area.top
				, _right: area.left + w
				, _bottom: area.top + h
				, get left() {
					return this._left;
				}
				, set left(l) {
					this._left = l;
					this._right = l + w;
				}
				, get right() {
					return this._right;
				}
				, get top() {
					return this._top;
				}
				, set top(t) {
					this._top = t;
					this._bottom = t + h;
				}
				, set bottom(b) {
					this._bottom = b;
					this._top = b - h;
				}
				, get bottom() {
					return this._bottom;
				}
			};
		const processor = _processor || __processTopToBottom;
		return processor(area, rectNew, list);
	}
	function _arrange() {
		const list = [];
		$(VIDWIN_SEL).each(function() {
			const v = $(this);
			v.css(_getPos(list, v.width(), v.height()));
			list.push(_getRect(v));
		});
	}
	function _arrangeResize() {
		const list = [];
		function __getDialog(_v) {
			return $(_v).find('.video-container.ui-dialog-content');
		}
		$(VIDWIN_SEL).toArray().sort((v1, v2) => {
			const c1 = __getDialog(v1).data().stream()
				, c2 = __getDialog(v2).data().stream();
			return c2.level - c1.level || c1.user.displayName.localeCompare(c2.user.displayName);
		}).forEach(_v => {
			const v = $(_v);
			__getDialog(v)
				.dialog('option', 'width', 120)
				.dialog('option', 'height', 90);
			v.css(_getPos(list, v.width(), v.height(), __processEqualsBottomToTop));
			list.push(_getRect(v));
		});
	}
	function _cleanStream(stream) {
		if (!!stream) {
			stream.getTracks().forEach(function(track) {
				try {
					track.stop();
				} catch(e) {
					//no-op
				}
			});
		}
	}
	function _cleanPeer(peer) {
		if (!!peer) {
			peer.cleaned = true;
			const pc = peer.peerConnection;
			try {
				if (!!pc && !!pc.getLocalStreams()) {
					pc.getLocalStreams().forEach(function(stream) {
						_cleanStream(stream);
					});
				}
			} catch(e) {
				OmUtil.log('Failed to clean peer' + e);
			}
			try {
				peer.dispose();
			} catch(e) {
				//no-op
			}
			peer = null;
		}
	}
	function _isChrome(_b) {
		const b = _b || kurentoUtils.WebRtcPeer.browser;
		return b.name === 'Chrome' || b.name === 'Chromium';
	}
	function _isEdge(_b) {
		const b = _b || kurentoUtils.WebRtcPeer.browser;
		return b.name === 'Edge' && "MSGestureEvent" in window;
	}
	function _isEdgeChromium(_b) {
		const b = _b || kurentoUtils.WebRtcPeer.browser;
		return b.name === 'Edge' && !("MSGestureEvent" in window);
	}
	function _setPos(v, pos) {
		if (v.dialog('instance')) {
			v.dialog('widget').css(pos);
		}
	}
	function _askPermission(callback) {
		const perm = $('#ask-permission');
		if (undefined === perm.dialog('instance')) {
			perm.data('callbacks', []).dialog({
				appendTo: '.room-block .room-container'
				, autoOpen: true
				, buttons: [
					{
						text: perm.data('btn-ok')
						, click: function() {
							while (perm.data('callbacks').length > 0) {
								perm.data('callbacks').pop()();
							}
							$(this).dialog('close');
						}
					}
				]
			});
		} else if (!perm.dialog('isOpen')) {
			perm.dialog('open')
		}
		perm.data('callbacks').push(callback);
	}
	function _disconnect(node) {
		try {
			node.disconnect(); //this one can throw
		} catch (e) {
			//no-op
		}
	}
	function _sharingSupported() {
		const b = kurentoUtils.WebRtcPeer.browser;
		return (b.name === 'Edge' && b.major > 16)
			|| (b.name === 'Firefox')
			|| (b.name === 'Chrome')
			|| (b.name === 'Chromium');
	}
	function _highlight(el, clazz, count) {
		if (!el || el.length < 1 || el.hasClass('disabled') || count < 0) {
			return;
		}
		el.addClass(clazz).delay(2000).queue(function(next) {
			el.removeClass(clazz).delay(2000).queue(function(next1) {
				_highlight(el, clazz, --count);
				next1();
			});
			next();
		});
	}

	self.getVid = _getVid;
	self.isSharing = _isSharing;
	self.isRecording = _isRecording;
	self.hasMic = _hasMic;
	self.hasCam = _hasCam;
	self.hasVideo = _hasVideo;
	self.getRects = _getRects;
	self.getPos = _getPos;
	self.container = _container;
	self.arrange = _arrange;
	self.arrangeResize = _arrangeResize;
	self.cleanStream = _cleanStream;
	self.cleanPeer = _cleanPeer;
	self.addIceServers = function(opts, m) {
		if (m && m.iceServers && m.iceServers.length > 0) {
			opts.configuration = {iceServers: m.iceServers};
		}
		return opts;
	};
	self.isEdge = _isEdge;
	self.isEdgeChromium = _isEdgeChromium;
	self.isChrome = _isChrome;
	self.setPos = _setPos;
	self.askPermission = _askPermission;
	self.disconnect = _disconnect;
	self.sharingSupported = _sharingSupported;
	self.highlight = _highlight;
	return self;
})();
var Volume = (function() {
	let video, vol, drop, slider, handleEl, hideTimer = null
		, lastVolume = 50, muted = false;

	function __cancelHide() {
		if (hideTimer) {
			clearTimeout(hideTimer);
			hideTimer = null;
		}
	}
	function __hideDrop() {
		__cancelHide();
		hideTimer = setTimeout(() => {
			drop.hide();
			hideTimer = null;
		}, 3000);
	}

	function _create(_video) {
		video = _video;
		_destroy();
		const uid = video.stream().uid
			, volId = 'volume-' + uid;
		vol = OmUtil.tmpl('#volume-control-stub', volId)
		slider = vol.find('.slider');
		drop = vol.find('.dropdown-menu');
		vol.on('mouseenter', function(e) {
				e.stopImmediatePropagation();
				drop.show();
				__hideDrop()
			})
			.click(function(e) {
				e.stopImmediatePropagation();
				OmUtil.roomAction({action: 'mute', uid: uid, mute: !muted});
				_mute(!muted);
				drop.hide();
				return false;
			}).dblclick(function(e) {
				e.stopImmediatePropagation();
				return false;
			});
		drop.on('mouseenter', function() {
			__cancelHide();
		});
		drop.on('mouseleave', function() {
			__hideDrop();
		});
		handleEl = vol.find('.handle');
		slider.slider({
			orientation: 'vertical'
			, range: 'min'
			, min: 0
			, max: 100
			, value: lastVolume
			, create: function() {
				handleEl.text($(this).slider('value'));
			}
			, slide: function(event, ui) {
				_handle(ui.value);
			}
		});
		_handle(lastVolume);
		_mute(muted);
		return vol;
	}
	function _handle(val) {
		handleEl.text(val);
		const vidEl = video.video()
			, data = vidEl.data();
		if (video.stream().self) {
			if (data.gainNode) {
				data.gainNode.gain.value = val / 100;
			}
		} else {
			vidEl[0].volume = val / 100;
		}
		const ico = vol.find('a');
		if (val > 0 && ico.hasClass('volume-off')) {
			ico.toggleClass('volume-off volume-on');
			video.handleMicStatus(true);
		} else if (val === 0 && ico.hasClass('volume-on')) {
			ico.toggleClass('volume-on volume-off');
			video.handleMicStatus(false);
		}
	}
	function _mute(mute) {
		if (!slider) {
			return;
		}
		muted = mute;
		if (mute) {
			const val = slider.slider('option', 'value');
			if (val > 0) {
				lastVolume = val;
			}
			slider.slider('option', 'value', 0);
			_handle(0);
		} else {
			slider.slider('option', 'value', lastVolume);
			_handle(lastVolume);
		}
	}
	function _destroy() {
		if (vol) {
			vol.remove();
			vol = null;
		}
	}

	return {
		create: _create
		, handle: _handle
		, mute: _mute
		, muted: function() {
			return muted;
		}
		, destroy: _destroy
	};
});
