/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var VideoManager = (function() {
	const self = {};
	let share, inited = false;

	function _onVideoResponse(m) {
		const w = $('#' + VideoUtil.getVid(m.uid))
			, v = w.data()
			, peer = v && v.getPeer();

		if (peer) {
			peer.processAnswer(m.sdpAnswer, function (error) {
				if (error) {
					if (true === this.cleaned) {
						return;
					}
					return OmUtil.error(error);
				}
				const vidEls = w.find('audio, video')
					, vidEl = vidEls.length === 1 ? vidEls[0] : null;
				if (vidEl && vidEl.paused) {
					vidEl.play().catch(function (err) {
						if ('NotAllowedError' === err.name) {
							VideoUtil.askPermission(function () {
								vidEl.play();
							});
						}
					});
				}
			});
		}
	}
	function _onBroadcast(msg) {
		const sd = msg.stream
			, uid = sd.uid;
		if (Array.isArray(msg.cleanup)) {
			msg.cleanup.forEach(function(_cuid) {
				_close(_cuid);
			});
		}
		$('#' + VideoUtil.getVid(uid)).remove();
		Video().init(msg);
		OmUtil.log(uid + ' registered in room');
	}
	function _onShareUpdated(msg) {
		const sd = msg.stream
			, uid = sd.uid
			, w = $('#' + VideoUtil.getVid(uid))
			, v = w.data();
		if (v && (VideoUtil.isSharing(sd) || VideoUtil.isRecording(sd))) {
			// Update activities in the current data object
			v.stream().activities = sd.activities;
		}
		Sharer.setShareState(VideoUtil.isSharing(sd) ? SHARE_STARTED : SHARE_STOPPED);
		Sharer.setRecState(VideoUtil.isRecording(sd) ? SHARE_STARTED : SHARE_STOPPED);
	}
	function _onReceive(msg) {
		const uid = msg.stream.uid;
		$('#' + VideoUtil.getVid(uid)).remove();
		Video().init(msg);
		OmUtil.log(uid + ' receiving video');
	}
	function _onKMessage(m) {
		switch (m.id) {
			case 'clientLeave':
				$(VID_SEL + '[data-client-uid="' + m.uid + '"]').each(function() {
					_closeV($(this));
				});
				if (share.data('cuid') === m.uid) {
					share.off().hide();
				}
				break;
			case 'broadcastStopped':
				_close(m.uid, false);
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
						, peer = v && v.getPeer();

					if (peer) {
						peer.addIceCandidate(m.candidate, function (error) {
							if (error) {
								if (true === this.cleaned) {
									return;
								}
								OmUtil.error('Error adding candidate: ' + error);
								return;
							}
						});
					}
				}
				break;
			case 'newStream':
				_play([m.stream], m.iceServers);
				break;
			case 'shareUpdated':
				_onShareUpdated(m);
				break;
			case 'error':
				OmUtil.error(m.message);
				break;
			default:
				//no-op
		}
	}
	function _onWsMessage(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = JSON.parse(msg);
			if (!m) {
				return;
			}
			if ('kurento' === m.type && 'test' !== m.mode) {
				OmUtil.info('Received message: ' + msg);
				_onKMessage(m);
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
		share = $('.room-block .room-container').find('.btn.shared');
		inited = true;
	}
	function _update(c) {
		if (!inited) {
			return;
		}
		const streamMap = {};
		c.streams.forEach(function(sd) {
			streamMap[sd.uid] = sd.uid;
			sd.self = c.self;
			sd.cam = c.cam;
			sd.mic = c.mic;
			if (VideoUtil.isSharing(sd) || VideoUtil.isRecording(sd)) {
				return;
			}
			const _id = VideoUtil.getVid(sd.uid)
				, av = VideoUtil.hasMic(sd) || VideoUtil.hasCam(sd)
				, v = $('#' + _id);
			if (av && v.length === 1) {
				v.data().update(sd);
			} else if (!av && v.length === 1) {
				_closeV(v);
			}
		});
		if (c.uid === Room.getOptions().uid) {
			$(VID_SEL).each(function() {
				$(this).data().setRights(c.rights);
			});
		}
		$('[data-client-uid="' + c.cuid + '"]').each(function() {
			const sd = $(this).data().stream();
			if (!streamMap[sd.uid]) {
				//not-inited/invalid video window
				_closeV($(this));
			}
		});
	}
	function _closeV(v) {
		if (v.dialog('instance') !== undefined) {
			v.dialog('destroy');
		}
		v.parents('.pod').remove();
		v.remove();
		WbArea.updateAreaClass();
	}
	function _playSharing(sd, iceServers) {
		const m = {stream: sd, iceServers: iceServers};
		let v = $('#' + VideoUtil.getVid(sd.uid))
		if (v.length === 1) {
			v.remove();
		}
		v = Video().init(m);
		VideoUtil.setPos(v, {left: 0, top: 35});
	}
	function _play(streams, iceServers) {
		if (!inited) {
			return;
		}
		streams.forEach(function(sd) {
			const m = {stream: sd, iceServers: iceServers};
			if (VideoUtil.isSharing(sd)) {
				VideoUtil.highlight(share
						.attr('title', share.data('user') + ' ' + sd.user.firstName + ' ' + sd.user.lastName + ' ' + share.data('text'))
						.data('uid', sd.uid)
						.data('cuid', sd.cuid)
						.show()
					, 'btn-outline-warning', 10);
				share.tooltip().off().click(function() {
					_playSharing(sd, iceServers);
				});
				if (Room.getOptions().autoOpenSharing === true) {
					_playSharing(sd, iceServers);
				}
			} else if (VideoUtil.isRecording(sd)) {
				return;
			} else {
				_onReceive(m);
			}
		});
	}
	function _close(uid, showShareBtn) {
		const v = $('#' + VideoUtil.getVid(uid));
		if (v.length === 1) {
			_closeV(v);
		}
		if (!showShareBtn && uid === share.data('uid')) {
			share.off().hide();
		}
	}
	function _find(uid) {
		return $(VID_SEL + '[data-client-uid="' + uid + '"][data-client-type="WEBCAM"]');
	}
	function _userSpeaks(uid, active) {
		const u = $('#user' + uid + ' .audio-activity')
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
	function _clickMuteOthers(uid) {
		const s = VideoSettings.load();
		if (false !== s.video.confirmMuteOthers) {
			const dlg = $('#muteothers-confirm');
			dlg.dialog({
				buttons: [
					{
						text: dlg.data('btn-ok')
						, click: function() {
							s.video.confirmMuteOthers = !$('#muteothers-confirm-dont-show').prop('checked');
							VideoSettings.save();
							OmUtil.roomAction({action: 'muteOthers', uid: uid});
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
	function _muteOthers(uid) {
		$(VID_SEL).each(function() {
			const w= $(this), v = w.data(), v2 = w.data('client-uid');
			if (v && v2) {
				v.mute('room' + uid !== v2);
			}
		});
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
	self.clickMuteOthers = _clickMuteOthers;
	self.muteOthers = _muteOthers;
	self.toggleActivity = _toggleActivity;
	self.sendMessage = function(_m) {
		OmUtil.sendMessage(_m, {type: 'kurento'});
	}
	self.destroy = function() {
		Wicket.Event.unsubscribe('/websocket/message', _onWsMessage);
	}
	return self;
})();
