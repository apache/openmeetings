/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Video = (function() {
	const self = {};
	let sd, v, vc, t, f, size, vol, slider, handle, video, rtcPeer
		, lastVolume = 50, muted = false, aCtx, aSrc, aDest, gainNode
		, lm, level, userSpeaks = false;

	function _getName() {
		return sd.user.firstName + ' ' + sd.user.lastName;
	}
	function _getExtra() {
		return t.height() + 2 + (f.is(':visible') ? f.height() : 0);
	}
	function _resizeDlg(_w, _h) {
		const h = _h + _getExtra();
		_resizeDlgArea(_w, h);
		return h;
	}
	function _resizeDlgArea(_w, _h) {
		v.dialog('option', 'width', _w).dialog('option', 'height', _h);
		const h = _h - _getExtra();
		_resize(_w, h);
		if (Room.getOptions().interview) {
			v.dialog('widget').css(VideoUtil.getPos());
		}
	}
	function _resizePod() {
		const p = v.parents('.pod,.pod-big')
			, pw = p.width(), ph = p.height();
		_resizeDlgArea(pw, ph);
	}
	function _resize(w, h) {
		vc.width(w).height(h);
		if (!!lm) {
			lm.height(h - 10);
		}
		video.width(w).height(h);
	}
	function _micActivity(level) {
		lm.getKendoProgressBar().value(140 * level); // magic number
		let speaks = level > .02;
		if (speaks !== userSpeaks) {
			userSpeaks = speaks;
			OmUtil.sendMessage({type: 'mic', id: 'activity', active: speaks});
		}
	}
	function _createSendPeer(msg) {
		VideoSettings.constraints(sd, function(cnts) {
			if ((VideoUtil.hasVideo(sd) && !cnts.video) || (VideoUtil.hasAudio(sd) && !cnts.audio)) {
				VideoManager.sendMessage({
					id : 'devicesAltered'
					, uid: sd.uid
					, audio: !!cnts.audio
					, video: !!cnts.video
				});
			}
			if (!cnts.audio && !cnts.video) {
				OmUtil.error("Requested devices are not available");
				VideoManager.close(sd.uid)
				return;
			}
			navigator.mediaDevices.getUserMedia(cnts)
				.then(function(stream) {
					let _stream = stream;
					if (stream.getAudioTracks().length !== 0) {
						vol.show();
						lm = vc.find('.level-meter')
							.kendoProgressBar({ value: 0, showStatus: false, orientation: 'vertical' });
						lm.height(vc.height() - 10);
						aCtx = new AudioContext();
						gainNode = aCtx.createGain();
						aSrc = aCtx.createMediaStreamSource(stream);
						aSrc.connect(gainNode);
						if (VideoUtil.isEdge()) {
							gainNode.connect(aCtx.destination);
						} else {
							aDest = aCtx.createMediaStreamDestination();
							gainNode.connect(aDest);
							_stream = aDest.stream;
							stream.getVideoTracks().forEach(function(track) {
								_stream.addTrack(track);
							});
						}
						_handleVolume(lastVolume);
					}
					const options = VideoUtil.addIceServers({
						localVideo: video[0]
						, videoStream: _stream
						, mediaConstraints: cnts
						, onicecandidate: self.onIceCandidate
					}, msg);
					rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
						options
						, function (error) {
							if (error) {
								return OmUtil.error(error);
							}
							level = MicLevel();
							level.meter(rtcPeer, _micActivity, OmUtil.error);
							this.generateOffer(function(error, offerSdp) {
								if (error) {
									return OmUtil.error('Sender sdp offer error ' + error);
								}
								OmUtil.log('Invoking Sender SDP offer callback function');
								VideoManager.sendMessage({
									id : 'broadcastStarted'
									, uid: sd.uid
									, sdpOffer: offerSdp
								});
							});
						});
				})
				.catch(function(err) {
					OmUtil.error(err);
				});
		});
	}
	function _createResvPeer(msg) {
		const options = VideoUtil.addIceServers({
			remoteVideo : video[0]
			, onicecandidate : self.onIceCandidate
		}, msg);
		rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
			options
			, function(error) {
				if (error) {
					return OmUtil.error(error);
				}
				this.generateOffer(function(error, offerSdp) {
					if (!this.cleaned && error) {
						return OmUtil.error('Receiver sdp offer error ' + error);
					}
					OmUtil.log('Invoking Receiver SDP offer callback function');
					VideoManager.sendMessage({
						id : 'addListener'
						, sender: sd.uid
						, sdpOffer: offerSdp
					});
				});
			});
	}
	function _handleMicStatus(state) {
		if (!f.is(':visible')) {
			return;
		}
		if (state) {
			f.find('.off').hide();
			f.find('.on').show();
			f.addClass('ui-state-highlight');
			t.addClass('ui-state-highlight');
		} else {
			f.find('.off').show();
			f.find('.on').hide();
			f.removeClass('ui-state-highlight');
			t.removeClass('ui-state-highlight');
		}
	}
	function _handleVolume(val) {
		handle.text(val);
		if (sd.self) {
			if (!!gainNode) {
				gainNode.gain.value = val / 100;
			}
		} else {
			video[0].volume = val / 100;
		}
		const ico = vol.find('.ui-icon');
		if (val > 0 && ico.hasClass('ui-icon-volume-off')) {
			ico.toggleClass('ui-icon-volume-off ui-icon-volume-on');
			vol.removeClass('ui-state-error');
			_handleMicStatus(true);
		} else if (val === 0 && ico.hasClass('ui-icon-volume-on')) {
			ico.toggleClass('ui-icon-volume-on ui-icon-volume-off');
			vol.addClass('ui-state-error');
			_handleMicStatus(false);
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
			_handleVolume(0);
		} else {
			slider.slider('option', 'value', lastVolume);
			_handleVolume(lastVolume);
		}
	}
	function _initContainer(_id, name, opts) {
		let contSel;
		if (opts.interview) {
			const area = $('.pod-area');
			const contId = UUID.v4();
			contSel = '#' + contId;
			area.append($('<div class="pod"></div>').attr('id', contId));
			WbArea.updateAreaClass();
		} else {
			contSel = '.room.box';
		}
		$(contSel).append(OmUtil.tmpl('#user-video', _id).attr('title', name)
				.attr('data-client-uid', sd.type + sd.cuid).data(self));
		return contSel;
	}
	function _initDialog(v, opts) {
		if (opts.interview) {
			v.dialog('option', 'draggable', false);
			v.dialog('option', 'resizable', false);
			v.dialogExtend({
				closable: false
				, collapsable: false
				, dblclick: false
			});
			$('.pod-area').sortable('refresh');
		} else {
			v.dialog('option', 'draggable', true);
			v.dialog('option', 'resizable', true);
			v.on('dialogresizestop', function(event, ui) {
				const w = ui.size.width - 2
					, h = ui.size.height - t.height() - 4 - (f.is(':visible') ? f.height() : 0);
				_resize(w, h);
			});
			if (VideoUtil.isSharing(sd)) {
				v.on('dialogclose', function() {
					VideoManager.close(sd.uid, true);
				});
			}
			v.dialogExtend({
				icons: {
					'collapse': 'ui-icon-minus'
				}
				, closable: VideoUtil.isSharing(sd)
				, collapsable: true
				, dblclick: 'collapse'
			});
		}
	}
	function _init(msg) {
		sd = msg.stream;
		sd.activities = sd.activities.sort();
		size = {width: sd.width, height: sd.height};
		const _id = VideoUtil.getVid(sd.uid)
			, name = _getName()
			, _w = sd.width
			, _h = sd.height
			, opts = Room.getOptions();
		sd.self = sd.cuid === opts.uid;
		const contSel = _initContainer(_id, name, opts);
		v = $('#' + _id);
		v.dialog({
			classes: {
				'ui-dialog': 'ui-corner-all video user-video' + (opts.showMicStatus ? ' mic-status' : '')
				, 'ui-dialog-titlebar': 'ui-corner-all' + (opts.showMicStatus ? ' ui-state-highlight' : '')
			}
			, width: _w
			, minWidth: 40
			, minHeight: 50
			, autoOpen: true
			, modal: false
			, appendTo: contSel
		});
		_initDialog(v, opts);
		t = v.parent().find('.ui-dialog-titlebar').attr('title', name);
		f = v.find('.footer');
		if (!VideoUtil.isSharing(sd)) {
			v.parent().find('.ui-dialog-titlebar-buttonpane')
				.append($('#video-volume-btn').children().clone())
				.append($('#video-refresh-btn').children().clone());
			const volume = v.parent().find('.dropdown-menu.video.volume');
			slider = v.parent().find('.slider');
			vol = v.parent().find('.ui-dialog-titlebar-volume')
				.on('mouseenter', function(e) {
					e.stopImmediatePropagation();
					volume.toggle();
				})
				.click(function(e) {
					e.stopImmediatePropagation();
					roomAction('mute', JSON.stringify({uid: sd.uid, mute: !muted}));
					_mute(!muted);
					volume.hide();
					return false;
				}).dblclick(function(e) {
					e.stopImmediatePropagation();
					return false;
				});
			v.parent().find('.ui-dialog-titlebar-refresh')
				.click(function(e) {
					e.stopImmediatePropagation();
					_refresh();
					return false;
				}).dblclick(function(e) {
					e.stopImmediatePropagation();
					return false;
				});
			volume.on('mouseleave', function() {
				$(this).hide();
			});
			handle = v.parent().find('.slider .handle');
			slider.slider({
				orientation: 'vertical'
				, range: 'min'
				, min: 0
				, max: 100
				, value: lastVolume
				, create: function() {
					handle.text($(this).slider('value'));
				}
				, slide: function(event, ui) {
					_handleVolume(ui.value);
				}
			});
			vol.hide();
		}
		v.on("remove", _cleanup);
		vc = v.find('.video');
		vc.width(_w).height(_h);

		_refresh(msg);

		v.dialog('widget').css(VideoUtil.getPos(VideoUtil.getRects(VID_SEL), sd.width, sd.height + 25));
		return v;
	}
	function _update(_c) {
		const prevA = sd.activities;
		sd.activities = _c.activities.sort();
		sd.user.firstName = _c.user.firstName;
		sd.user.lastName = _c.user.lastName;
		const name = _getName();
		v.dialog('option', 'title', name).parent().find('.ui-dialog-titlebar').attr('title', name);
		const same = prevA.length === sd.activities.length && prevA.every(function(value, index) { return value === sd.activities[index]})
		if (sd.self && !same) {
			_refresh();
		}
	}
	function _refresh(msg) {
		_cleanup();
		const _id = VideoUtil.getVid(sd.uid);
		const hasVideo = VideoUtil.hasVideo(sd)
			, imgUrl = 'profile/' + sd.user.id + '?anti=' + new Date().getTime();  //TODO add normal URL ????
		video = $(hasVideo ? '<video>' : '<audio>').attr('id', 'vid' + _id)
			.width(vc.width()).height(vc.height())
			.prop('autoplay', true).prop('controls', false);
		if (hasVideo) {
			video.attr('poster', imgUrl);
		} else {
			vc.addClass('audio-only').css('background-image', 'url(' + imgUrl + ')');
		}
		vc.append(video);
		const hasAudio = VideoUtil.hasAudio(sd);
		if (sd.self) {
			_createSendPeer(msg);
			_handleMicStatus(hasAudio);
		} else {
			_createResvPeer(msg);
		}
		if (hasAudio) {
			vol.show();
			_mute(muted);
		} else {
			vol.hide();
			v.parent().find('.dropdown-menu.video.volume').hide();
		}
	}
	function _setRights(_r) {
	}
	function _cleanup() {
		OmUtil.log('Disposing participant ' + sd.uid);
		if (!!gainNode) {
			gainNode.disconnect();
			gainNode = null;
		}
		if (!!aSrc) {
			VideoUtil.cleanStream(aSrc.mediaStream);
			aSrc.disconnect();
			aSrc = null;
		}
		if (!!aDest) {
			aDest.disconnect();
			aDest = null;
		}
		if (!!aCtx) {
			if (!!aCtx.destination) {
				aCtx.destination.disconnect();
			}
			aCtx.close();
			aCtx = null;
		}
		if (!!video && video.length > 0) {
			VideoUtil.cleanStream(video[0].srcObject);
			video[0].srcObject = null;
			video.remove();
			video = null;
		}
		if (!!lm && lm.length > 0) {
			_micActivity(0);
			lm.hide();
		}
		if (!!level) {
			level.dispose();
			level = null;
		}
		VideoUtil.cleanPeer(rtcPeer);
		vc.find('audio,video').remove();
	}
	function _reattachStream() {
		if (!!rtcPeer && !!video && video.length > 0) {
			video[0].srcObject = sd.self ? rtcPeer.getLocalStream() : rtcPeer.getRemoteStream();
		}
	}

	self.update = _update;
	self.refresh = _refresh;
	self.mute = _mute;
	self.isMuted = function() { return muted; };
	self.init = _init;
	self.stream = function() { return sd; };
	self.setRights = _setRights;
	self.getPeer = function() { return rtcPeer; };
	self.onIceCandidate = function(candidate) {
		const opts = Room.getOptions();
		OmUtil.log("Local candidate" + JSON.stringify(candidate));
		VideoManager.sendMessage({
			id: 'onIceCandidate'
			, candidate: candidate
			, uid: sd.uid
			, luid: sd.self ? sd.uid : opts.uid
		});
	};
	self.resizePod = _resizePod;
	self.reattachStream = _reattachStream;
	return self;
});
