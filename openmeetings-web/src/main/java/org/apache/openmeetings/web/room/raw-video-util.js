/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WB_AREA_SEL = '.room.wb.area';
const WBA_WB_SEL = '.room.wb.area .ui-tabs-panel.ui-corner-bottom.ui-widget-content:visible';
const VID_SEL = '.video.user-video';
const CAM_ACTIVITY = 'VIDEO';
const MIC_ACTIVITY = 'AUDIO';
var VideoUtil = (function() {
	const self = {};
	function _getVid(uid) {
		return 'video' + uid;
	}
	function _isSharing(sd) {
		return 'SCREEN' === sd.type;
	}
	function _hasAudio(sd) {
		return !sd || sd.activities.includes(MIC_ACTIVITY);
	}
	function _hasVideo(sd) {
		return !sd || sd.activities.includes(CAM_ACTIVITY);
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
		const c = a.find('.wb-area .tabs .ui-tabs-panel');
		return c.length > 0 ? $(WBA_WB_SEL) : a;
	}
	function _getPos(list, w, h) {
		if (Room.getOptions().interview) {
			return {left: 0, top: 0};
		}
		const wba = _container(), woffset = wba.offset()
			, offsetX = 20, offsetY = 10
			, area = {left: woffset.left, top: woffset.top, right: woffset.left + wba.width(), bottom: woffset.top + wba.height()};
		const rectNew = {
				_left: area.left
				, _top: area.top
				, right: area.left + w
				, bottom: area.top + h
				, get left() {
					return this._left
				}
				, set left(l) {
					this._left = l;
					this.right = l + w;
				}
				, get top() {
					return this._top
				}
				, set top(t) {
					this._top = t;
					this.bottom = t + h;
				}
			};
		let minY = area.bottom, posFound;
		do {
			posFound = true
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
	function _arrange() {
		const list = [], elems = $(VID_SEL);
		for (let i = 0; i < elems.length; ++i) {
			const v = $(elems[i]);
			v.css(_getPos(list, v.width(), v.height()));
			list.push(_getRect(v));
		}
	}
	function _cleanStream(stream) {
		if (!!stream) {
			stream.getTracks().forEach(function(track) {
				track.stop();
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
			peer.dispose();
			peer = null;
		}
	}
	function _isEdge() {
		const b = kurentoUtils.WebRtcPeer.browser;
		return b.name === 'Edge';
	}
	function _setPos(v, pos) {
		v.dialog('widget').css(pos);
	}

	self.getVid = _getVid;
	self.isSharing = _isSharing;
	self.hasAudio = _hasAudio;
	self.hasVideo = _hasVideo;
	self.getRects = _getRects;
	self.getPos = _getPos;
	self.container = _container;
	self.arrange = _arrange;
	self.cleanStream = _cleanStream;
	self.cleanPeer = _cleanPeer;
	self.addIceServers = function(opts, m) {
		if (m && m.iceServers && m.iceServers.length > 0) {
			opts.configuration = {iceServers: m.iceServers};
		}
		return opts;
	};
	self.isEdge = _isEdge;
	self.setPos = _setPos;
	return self;
})();
