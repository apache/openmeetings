/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var VideoManager = (function() {
	const self = {};
	let share, inited = false;

	function _onWsMessage(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = jQuery.parseJSON(msg);
			if (m && 'mic' === m.type) {
				switch (m.id) {
					case 'activity':
						_micActivity(m.uid, m.active);
						onBroadcast(m);
						break;
					default:
						//no-op
				}
			}
		} catch (err) {
			//no-op
		}
	}
	function _init() {
		Wicket.Event.subscribe('/websocket/message', _onWsMessage);
		VideoSettings.init(Room.getOptions());
		share = $('.room.box').find('.icon.shared.ui-button');
		inited = true;
	}
	function _update(c) {
		if (!inited) {
			return;
		}
		for (let i = 0; i < c.streams.length; ++i) {
			const cl = JSON.parse(JSON.stringify(c)), s = c.streams[i];
			delete cl.streams;
			$.extend(cl, s);
			if (cl.self && VideoUtil.isSharing(cl) || VideoUtil.isRecording(cl)) {
				continue;
			}
			const _id = VideoUtil.getVid(cl.uid)
				, av = VideoUtil.hasAudio(cl) || VideoUtil.hasVideo(cl)
				, v = $('#' + _id);
			if (av && v.length !== 1 && !!cl.self) {
				Video().init(cl, VideoUtil.getPos(VideoUtil.getRects(VID_SEL), cl.width, cl.height + 25));
			} else if (av && v.length === 1) {
				v.data().update(cl);
			} else if (!av && v.length === 1) {
				_closeV(v);
			}
		}
		if (c.uid === Room.getOptions().uid) {
			Room.setRights(c.rights);
			const windows = $(VID_SEL + ' .ui-dialog-content');
			for (let i = 0; i < windows.length; ++i) {
				const w = $(windows[i]);
				w.data().setRights(c.rights);
			}

		}
		if (c.streams.length === 0) {
			// check for non inited video window
			const vw = $('#' + VideoUtil.getVid(c.uid));
			if (vw.length === 1) {
				_closeV(vw);
			}
		}
	}
	function _closeV(v) {
		if (v.dialog('instance') !== undefined) {
			v.dialog('destroy');
		}
		v.parents('.pod').remove();
		v.remove();
		WbArea.updateAreaClass();
	}
	function _play(c) {
		if (!inited) {
			return;
		}
		if (VideoUtil.isSharing(c)) {
			_highlight(share
					.attr('title', share.data('user') + ' ' + c.user.firstName + ' ' + c.user.lastName + ' ' + share.data('text'))
					.data('uid', c.uid)
					.show(), 10);
			share.tooltip().off('click').click(function() {
				const v = $('#' + VideoUtil.getVid(c.uid))
				if (v.length !== 1) {
					Video().init(c, VideoUtil.container().offset());
				} else {
					v.dialog('open');
				}
			});
		} else if ('sharing' !== c.type) {
			Video().init(c, VideoUtil.getPos(VideoUtil.getRects(VID_SEL), c.width, c.height + 25));
		}
	}
	function _close(uid, showShareBtn) {
		const _id = VideoUtil.getVid(uid), v = $('#' + _id);
		if (v.length === 1) {
			_closeV(v);
		}
		if (!showShareBtn && uid === share.data('uid')) {
			share.off('click').hide();
		}
	}
	function _highlight(el, count) {
		if (count < 0) {
			return;
		}
		el.addClass('ui-state-highlight', 2000, function() {
			el.removeClass('ui-state-highlight', 2000, function() {
				_highlight(el, --count);
			});
		});
	}
	function _find(uid) {
		return $(VID_SEL + ' div[data-client-uid="room' + uid + '"]');
	}
	function _micActivity(uid, active) {
		const u = $('#user' + uid + ' .audio-activity.ui-icon')
			, v = _find(uid).parent();
		if (active) {
			u.addClass('speaking');
			v.addClass('user-speaks')
		} else {
			u.removeClass('speaking');
			v.removeClass('user-speaks')
		}
	}
	function _refresh(uid, opts) {
		const v = _find(uid);
		if (v.length > 0) {
			v.data().refresh(opts);
		}
	}
	function _mute(uid, mute) {
		const v = _find(uid);
		if (v.length > 0) {
			v.data().mute(mute);
		}
	}
	function _clickExclusive(uid) {
		const s = VideoSettings.load();
		if (false !== s.video.confirmExclusive) {
			const dlg = $('#exclusive-confirm');
			dlg.dialog({
				buttons: [
					{
						text: dlg.data('btn-ok')
						, click: function() {
							s.video.confirmExclusive = !$('#exclusive-confirm-dont-show').prop('checked');
							VideoSettings.save();
							roomAction('exclusive', uid);
							$(this).dialog('close');
						}
					}
					, {
						text: dlg.data('btn-cancel')
						, click: function() {
							$(this).dialog('close');
						}
					}
				]
			})
		}
	}
	function _exclusive(uid) {
		const windows = $(VID_SEL + ' .ui-dialog-content');
		for (let i = 0; i < windows.length; ++i) {
			const w = $(windows[i]);
			w.data().mute('room' + uid !== w.data('client-uid'));
		}
	}

	self.init = _init;
	self.update = _update;
	self.play = _play;
	self.close = _close;
	self.securityMode = function(uid) { $('#' + VideoUtil.getVid(uid)).data().securityMode(); };
	self.swfLoaded = function(uid) { $('#' + VideoUtil.getVid(uid)).data().swfLoaded(); };
	self.refresh = _refresh;
	self.mute = _mute;
	self.clickExclusive = _clickExclusive;
	self.exclusive = _exclusive;
	self.micActivity = function(active) {
		OmUtil.sendMessage({
			area: 'room'
			, type: 'mic'
			, id: 'activity'
			, active: active
		});
	};
	self.destroy = function() {
		Wicket.Event.unsubscribe('/websocket/message', _onWsMessage);
	}
	return self;
})();
