/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var VideoManager = (function() {
	const self = {};
	let share, inited = false;

	function _onVideoResponse(m) {
		const w = $('#' + VideoUtil.getVid(m.uid))
			, v = w.data()

		v.getPeer().processAnswer(m.sdpAnswer, function (error) {
			if (error) {
				return OmUtil.error(error);
			}
		});
	}
	function _onBroadcast(msg) {
		const sd = msg.stream
			, uid = sd.uid;
		$('#' + VideoUtil.getVid(uid)).remove();
		if (sd.self && VideoUtil.isSharing(sd)) {
			return;
		}
		Video().init(msg);
		OmUtil.log(uid + ' registered in room');
	}
	function _onReceive(msg) {
		const uid = msg.stream.uid;
		$('#' + VideoUtil.getVid(uid)).remove();
		Video().init(msg);
		OmUtil.log(uid + ' receiving video');
	}
	function _onWsMessage(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = jQuery.parseJSON(msg);
			if (!m) {
				return;
			}
			if ('kurento' === m.type && 'test' !== m.mode) {
				OmUtil.info('Received message: ' + msg);
				switch (m.id) {
					case 'clientLeave':
						$(VID_SEL + ' div[data-client-uid="' + m.uid + '"]').each(function() {
							_closeV($(this));
						});
						if (share.data('cuid') === m.uid) {
							share.off('click').hide();
						}
						break;
					case 'broadcastStopped':
						_closeV($('#' + VideoUtil.getVid(m.uid)));
						break;
					case 'broadcast':
						_onBroadcast(m);
						break;
					case 'videoResponse':
						_onVideoResponse(m);
						break;
					case 'iceCandidate':
						{
							const w = $('#' + VideoUtil.getVid(m.uid))
								, v = w.data()

							v.getPeer().addIceCandidate(m.candidate, function (error) {
								if (error) {
									OmUtil.error('Error adding candidate: ' + error);
									return;
								}
							});
						}
						break;
					case 'newStream':
						_play([m.stream], m.iceServers);
						break;
					case 'error':
						OmUtil.error(m.message);
						break;
					default:
						//no-op
				}
			} else if ('mic' === m.type) {
				switch (m.id) {
					case 'activity':
						_userSpeaks(m.uid, m.active);
						break;
					default:
						//no-op
				}
			}
		} catch (err) {
			OmUtil.error(err);
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
		c.streams.forEach(function(sd) {
			sd.self = c.self;
			if (VideoUtil.isSharing(sd)) {
				return;
			}
			const _id = VideoUtil.getVid(sd.uid)
				, av = VideoUtil.hasAudio(sd) || VideoUtil.hasVideo(sd)
				, v = $('#' + _id);
			if (av && v.length === 1) {
				v.data().update(sd);
			} else if (!av && v.length === 1) {
				_closeV(v);
			}
		});
		if (c.uid === Room.getOptions().uid) {
			Room.setRights(c.rights);
			Room.setActivities(c.activities);
			const windows = $(VID_SEL + ' .ui-dialog-content');
			for (let i = 0; i < windows.length; ++i) {
				const w = $(windows[i]);
				w.data().setRights(c.rights);
			}
		}
		if (c.streams.length === 0) {
			// check for non inited video window
			const v = $('#' + VideoUtil.getVid(c.uid));
			if (v.length === 1) {
				_closeV(v);
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
	function _play(streams, iceServers) {
		if (!inited) {
			return;
		}
		streams.forEach(function(sd) {
			const m = {stream: sd, iceServers: iceServers};
			if (VideoUtil.isSharing(sd)) {
				_highlight(share
						.attr('title', share.data('user') + ' ' + sd.user.firstName + ' ' + sd.user.lastName + ' ' + share.data('text'))
						.data('uid', sd.uid)
						.data('cuid', sd.cuid)
						.show(), 10);
				share.tooltip().off('click').click(function() {
					let v = $('#' + VideoUtil.getVid(sd.uid))
					if (v.length === 1) {
						v.remove();
					}
					v = Video().init(m);
					VideoUtil.setPos(v, {left: 0, top: 35});
				});
			} else {
				_onReceive(m);
			}
		});
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
		return $(VID_SEL + ' div[data-client-uid="' + uid + '"][data-client-type="WEBCAM"]');
	}
	function _userSpeaks(uid, active) {
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
	function _toggleActivity(activity) {
		self.sendMessage({
			id: 'toggleActivity'
			, activity: activity
		});
	}

	self.init = _init;
	self.update = _update;
	self.play = _play;
	self.close = _close;
	self.refresh = _refresh;
	self.mute = _mute;
	self.clickExclusive = _clickExclusive;
	self.exclusive = _exclusive;
	self.toggleActivity = _toggleActivity;
	self.sendMessage = function(_m) {
		OmUtil.sendMessage(_m, {type: 'kurento'});
	}
	self.destroy = function() {
		Wicket.Event.unsubscribe('/websocket/message', _onWsMessage);
	}
	return self;
})();
