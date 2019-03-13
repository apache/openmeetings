/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Video = (function() {
	const self = {}
		, AudioCtx = window.AudioContext || window.webkitAudioContext;
	let sd, v, vc, t, f, size, vol, slider, handle, video, rtcPeer
		, lastVolume = 50, muted = false, aCtx, aSrc, aDest, gainNode, analyser
		, lm, level, userSpeaks = false, muteOthers;

	function _getExtra() {
		return t.height() + 2 + (f.is(':visible') ? f.height() : 0);
	}
	function _resizeDlgArea(_w, _h) {
		v.dialog('option', 'width', _w).dialog('option', 'height', _h);
		const h = _h - _getExtra();
		_resize(_w, h);
		if (Room.getOptions().interview) {
			VideoUtil.setPos(v, VideoUtil.getPos());
		}
	}
	function _resizePod() {
		const p = v.parents('.pod,.pod-big')
			, pw = p.width(), ph = p.height();
		_resizeDlgArea(pw, ph);
	}
	function _resizeLm(h) {
		if (!!lm) {
			lm.attr('height', h).height(h);
		}
		return lm;
	}
	function _resize(w, h) {
		vc.width(w).height(h);
		_resizeLm(h - 10);
		video.width(w).height(h);
	}
	function _micActivity(level) {
		const speaks = level > 5;
		if (speaks !== userSpeaks) {
			userSpeaks = speaks;
			OmUtil.sendMessage({type: 'mic', id: 'activity', active: speaks});
		}
	}
	function _getScreenStream(msg, callback) {
		function __handleScreenError(err) {
			VideoManager.sendMessage({id: 'errorSharing'});
			Sharer.setShareState(SHARE_STOPED);
			Sharer.setRecState(SHARE_STOPED);
			OmUtil.error(err);
		}
		const b = kurentoUtils.WebRtcPeer.browser;
		let promise, cnts;
		if (VideoUtil.isEdge(b) && b.major > 16) {
			cnts = {
				video: true
			};
			promise = navigator.getDisplayMedia(cnts);
		} else if (b.name === 'Firefox') {
			// https://mozilla.github.io/webrtc-landing/gum_test.html
			cnts = Sharer.baseConstraints(sd);
			cnts.video.mediaSource = sd.shareType;
			promise = navigator.mediaDevices.getUserMedia(cnts);
		} else if (VideoUtil.isChrome72(b)) {
			cnts = {
				video: true
			};
			promise = navigator.mediaDevices.getDisplayMedia(cnts);
		} else if (VideoUtil.isChrome(b)) {
			promise = Sharer.getChromeConstraints(sd).then((_cnts) => {
				cnts = _cnts;
				return navigator.mediaDevices.getUserMedia(_cnts);
			});
		} else {
			promise = new Promise(() => {
				Sharer.close();
				throw 'Screen-sharing is not supported in ' + b.name + '[' + b.major + ']';
			});
		}
		promise.then(function(stream) {
			callback(msg, cnts, stream);
		}).catch(__handleScreenError);
	}
	function _getVideoStream(msg, callback) {
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
				OmUtil.error('Requested devices are not available');
				VideoManager.close(sd.uid)
				return;
			}
			navigator.mediaDevices.getUserMedia(cnts)
				.then(function(stream) {
					let _stream = stream;
					if (stream.getAudioTracks().length !== 0) {
						vol.show();
						lm = vc.find('.level-meter');
						_resizeLm(vc.height() - 10).show();
						aCtx = new AudioCtx();
						gainNode = aCtx.createGain();
						analyser = aCtx.createAnalyser();
						aSrc = aCtx.createMediaStreamSource(stream);
						aSrc.connect(gainNode);
						gainNode.connect(analyser);
						if (VideoUtil.isEdge()) {
							analyser.connect(aCtx.destination);
						} else {
							aDest = aCtx.createMediaStreamDestination();
							analyser.connect(aDest);
							aSrc.origStream = stream;
							_stream = aDest.stream;
							stream.getVideoTracks().forEach(function(track) {
								_stream.addTrack(track);
							});
						}
						_handleVolume(lastVolume);
					}
					callback(msg, cnts, _stream);
				})
				.catch(function(err) {
					VideoManager.sendMessage({
						id : 'devicesAltered'
						, uid: sd.uid
						, audio: false
						, video: false
					});
					VideoManager.close(sd.uid);
					if ('NotReadableError' === err.name) {
						OmUtil.error('Camera/Microphone is busy and can\'t be used');
					} else {
						OmUtil.error(err);
					}
				});
		});
	}
	function __createSendPeer(msg, cnts, stream) {
		const options = {
			videoStream: stream
			, mediaConstraints: cnts
			, onicecandidate: self.onIceCandidate
		};
		if (!VideoUtil.isSharing(sd)) {
			options.localVideo = video[0];
		}
		rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
			VideoUtil.addIceServers(options, msg)
			, function (error) {
				if (error) {
					return OmUtil.error(error);
				}
				if (!!analyser) {
					level = MicLevel();
					level.meter(analyser, lm, _micActivity, OmUtil.error);
				}
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
					if (VideoUtil.isSharing(sd)) {
						Sharer.setShareState(SHARE_STARTED);
					}
					if (VideoUtil.isRecording(sd)) {
						Sharer.setRecState(SHARE_STARTED);
					}
				});
			});
	}
	function _createSendPeer(msg) {
		if (VideoUtil.isSharing(sd) || VideoUtil.isRecording(sd)) {
			_getScreenStream(msg, __createSendPeer);
		} else {
			_getVideoStream(msg, __createSendPeer);
		}
	}
	function _createResvPeer(msg) {
		const options = VideoUtil.addIceServers({
			remoteVideo : video[0]
			, onicecandidate : self.onIceCandidate
		}, msg);
		rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
			options
			, function(error) {
				if (!this.cleaned && error) {
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
		if (!f || !f.is(':visible')) {
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
		$(contSel).append(OmUtil.tmpl('#user-video', _id)
				.attr('title', name)
				.attr('data-client-uid', sd.cuid)
				.attr('data-client-type', sd.type)
				.data(self));
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
	function _initCamDialog() {
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
	function _init(msg) {
		sd = msg.stream;
		sd.activities = sd.activities.sort();
		size = {width: sd.width, height: sd.height};
		const _id = VideoUtil.getVid(sd.uid)
			, name = sd.user.displayName
			, _w = sd.width
			, _h = sd.height
			, isSharing = VideoUtil.isSharing(sd)
			, isRecording = VideoUtil.isRecording(sd)
			, opts = Room.getOptions();
		sd.self = sd.cuid === opts.uid;
		const contSel = _initContainer(_id, name, opts);
		v = $('#' + _id);
		f = v.find('.footer');
		if (!sd.self && isSharing) {
			Sharer.close();
		}
		if (sd.self && (isSharing || isRecording)) {
			v.hide();
		} else {
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
		}
		if (!isSharing && !isRecording) {
			_initCamDialog();
		}
		t = v.parent().find('.ui-dialog-titlebar').attr('title', name);
		v.on('remove', _cleanup);
		vc = v.find('.video');
		vc.width(_w).height(_h);
		muteOthers = vc.find('.mute-others');

		_refresh(msg);

		if (!isSharing && !isRecording) {
			VideoUtil.setPos(v, VideoUtil.getPos(VideoUtil.getRects(VID_SEL), sd.width, sd.height + 25));
		}
		return v;
	}
	function _update(_c) {
		const prevA = sd.activities;
		sd.activities = _c.activities.sort();
		sd.user.firstName = _c.user.firstName;
		sd.user.lastName = _c.user.lastName;
		const name = sd.user.displayName;
		v.dialog('option', 'title', name).parent().find('.ui-dialog-titlebar').attr('title', name);
		const same = prevA.length === sd.activities.length && prevA.every(function(value, index) { return value === sd.activities[index]})
		if (sd.self && !same) {
			_refresh();
		}
	}
	function _refresh(msg) {
		_cleanup();
		const _id = VideoUtil.getVid(sd.uid);
		const hasVideo = VideoUtil.hasVideo(sd) || VideoUtil.isSharing(sd) || VideoUtil.isRecording(sd);
		video = $(hasVideo ? '<video>' : '<audio>').attr('id', 'vid' + _id)
			.width(vc.width()).height(vc.height())
			.prop('autoplay', true).prop('controls', false);
		if (hasVideo) {
			video.attr('poster', sd.user.pictureUri);
		} else {
			vc.addClass('audio-only').css('background-image', 'url(' + sd.user.pictureUri + ')');
		}
		vc.append(video);
		const hasAudio = VideoUtil.hasAudio(sd);
		if (sd.self) {
			_createSendPeer(msg);
			_handleMicStatus(hasAudio);
		} else {
			_createResvPeer(msg);
		}
		if (vol) {
			if (hasAudio) {
				vol.show();
				_mute(muted);
			} else {
				vol.hide();
				v.parent().find('.dropdown-menu.video.volume').hide();
			}
		}
	}
	function _setRights() {
		if (Room.hasRight(['superModerator', 'moderator', 'muteOthers']) && VideoUtil.hasAudio(sd)) {
			muteOthers.addClass('enabled').click(function() {
				VideoManager.clickMuteOthers(sd.uid);
			});
		} else {
			muteOthers.removeClass('enabled').off();
		}
	}
	function _cleanup() {
		OmUtil.log('Disposing participant ' + sd.uid);
		if (!!analyser) {
			VideoUtil.disconnect(analyser);
			analyser = null;
		}
		if (!!gainNode) {
			VideoUtil.disconnect(gainNode);
			gainNode = null;
		}
		if (!!aSrc) {
			VideoUtil.cleanStream(aSrc.mediaStream);
			VideoUtil.cleanStream(aSrc.origStream);
			VideoUtil.disconnect(aSrc);
			aSrc = null;
		}
		if (!!aDest) {
			VideoUtil.disconnect(aDest);
			aDest = null;
		}
		if (!!aCtx) {
			if (!!aCtx.destination) {
				VideoUtil.disconnect(aCtx.destination);
			}
			aCtx.close();
			aCtx = null;
		}
		if (!!video && video.length > 0) {
			video.attr('id', 'dummy');
			const vidNode = video[0];
			VideoUtil.cleanStream(vidNode.srcObject);
			vidNode.srcObject = null;
			vidNode.parentNode.removeChild(vidNode);
			video = null;
		}
		if (!!lm && lm.length > 0) {
			_micActivity(0);
			lm.hide();
			muteOthers.removeClass('enabled').off();
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
		OmUtil.log('Local candidate ' + JSON.stringify(candidate));
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
