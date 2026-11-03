/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const Settings = require('../main/settings');

const UAParser = require('ua-parser-js')
	, ua = (typeof window !== 'undefined' && window.navigator) ? window.navigator.userAgent : ''
	, parser = new UAParser(ua)
	, browser = parser.getBrowser();

const WB_AREA_SEL = '.room-block .wb-block';
const WBA_WB_SEL = '.room-block .wb-block .wb-tab-content';
const VIDWIN_SEL = '.video.user-video';
const VID_SEL = '.video-container[id!=user-video]';
const CAM_ACTIVITY = 'VIDEO';
const MIC_ACTIVITY = 'AUDIO';
const SCREEN_ACTIVITY = 'SCREEN';
const REC_ACTIVITY = 'RECORD';

function _isSafari() {
	return browser.name === 'Safari';
}
function _isChrome() {
	return browser.name === 'Chrome' || browser.name === 'Chromium';
}
function _isEdge() {
	return browser.name === 'Edge' && "MSGestureEvent" in window;
}
function _isEdgeChromium() {
	return browser.name === 'Edge' && !("MSGestureEvent" in window);
}

function _getVid(uid) {
	return 'video' + uid;
}
function _isSharing(sd) {
	return !!sd && 'SCREEN' === sd.type && sd.activities.includes(SCREEN_ACTIVITY);
}
function _isRecording(sd) {
	return !!sd && 'SCREEN' === sd.type && sd.activities.includes(REC_ACTIVITY);
}
function _hasActivity(sd, act) {
	return !!sd && sd.activities.includes(act);
}
function _hasMic(sd) {
	if (!sd) {
		return true;
	}
	const enabled = sd.micEnabled !== false;
	return sd.activities.includes(MIC_ACTIVITY) && enabled;
}
function _hasCam(sd) {
	if (!sd) {
		return true;
	}
	const enabled = sd.camEnabled !== false;
	return sd.activities.includes(CAM_ACTIVITY) && enabled;
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
function _arrangeResize(vSettings) {
	const list = []
		, size = {width: 120, height: 90};
	if (vSettings.fixed.enabled) {
		size.width = vSettings.fixed.width;
		size.height = vSettings.fixed.height;
	}

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
			.dialog('option', 'width', size.width)
			.dialog('option', 'height', size.height);
		v.css(_getPos(list, v.width(), v.height(), __processEqualsBottomToTop));
		list.push(_getRect(v));
	});
}
function _cleanStream(stream) {
	if (!!stream) {
		stream.getTracks().forEach(track => track.stop());
	}
}
function _cleanPeer(rtcPeer) {
	if (!!rtcPeer) {
		try {
			const pc = rtcPeer.pc;
			if (!!pc) {
				pc.getSenders().forEach(sender => {
					try {
						if (sender.track) {
							sender.track.stop();
						}
					} catch(e) {
						OmUtil.log('Failed to clean sender' + e);
					}
				});
				pc.getReceivers().forEach(receiver => {
					try {
						if (receiver.track) {
							receiver.track.stop();
						}
					} catch(e) {
						OmUtil.log('Failed to clean receiver' + e);
					}
				});
			}
			rtcPeer.dispose();
		} catch(e) {
			//no-op
		}
	}
}
function _setPos(v, pos) {
	if (v.dialog('instance')) {
		v.dialog('widget').css(pos);
	}
}
function _askPermission(callback) {
	const perm = $('#ask-permission');
	$('.sidebar').confirmation({
		title: perm.attr('title')
		, placement: Settings.isRtl ? 'right' : 'left'
		, singleton: true
		, rootSelector: '.sidebar'
		, html: true
		, content: perm.html()
		, buttons: [{
			class: 'btn btn-sm btn-warning'
			, label: perm.data('btn-ok')
			, value: perm.data('btn-ok')
			, onClick: function() {
				callback();
				$('.sidebar').confirmation('dispose');
			}
		}]
	});
	$('.sidebar').confirmation('show');
}
function _disconnect(node) {
	try {
		node.disconnect(); //this one can throw
	} catch (e) {
		//no-op
	}
}
function _sharingSupported() {
	return (browser.name === 'Edge' && browser.major > 16)
		|| (typeof(navigator.mediaDevices.getDisplayMedia) === 'function'
			&& (browser.name === 'Firefox'
				|| browser.name === 'Opera'
				|| browser.name === 'Yandex'
				|| _isSafari()
				|| _isChrome()
				|| _isEdgeChromium()
				|| (browser.name === 'Mozilla' && browser.major > 4)
			));
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
function _playSrc(_video, _stream, mute) {
	if (_stream && _video) {
		_video.srcObject = _stream;
		if (_video.paused) {
			_video.play().then(() => _video.muted = mute).catch(err => {
				if ('NotAllowedError' === err.name) {
					_askPermission(() => _video.play().then(() => _video.muted = mute));
				}
			});
		}
	}
}

module.exports = {
	VIDWIN_SEL: VIDWIN_SEL
	, VID_SEL: VID_SEL
	, CAM_ACTIVITY: CAM_ACTIVITY
	, MIC_ACTIVITY: MIC_ACTIVITY

	, getVid: _getVid
	, isSharing: _isSharing
	, isRecording: _isRecording
	, hasMic: _hasMic
	, hasCam: _hasCam
	, hasVideo: _hasVideo
	, hasActivity: _hasActivity
	, getRects: _getRects
	, getPos: _getPos
	, container: _container
	, arrange: _arrange
	, arrangeResize: _arrangeResize
	, cleanStream: _cleanStream
	, cleanPeer: _cleanPeer
	, addIceServers: function(opts, m) {
		if (m && m.iceServers && m.iceServers.length > 0) {
			opts.iceServers = m.iceServers;
		}
		return opts;
	}
	, setPos: _setPos
	, askPermission: _askPermission
	, disconnect: _disconnect
	, sharingSupported: _sharingSupported
	, highlight: _highlight
	, playSrc: _playSrc

	, browser: browser
	, isEdge: _isEdge
	, isEdgeChromium: _isEdgeChromium
	, isChrome: _isChrome
	, isSafari: _isSafari
};
