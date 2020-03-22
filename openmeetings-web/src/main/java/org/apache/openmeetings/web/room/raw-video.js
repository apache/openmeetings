/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Video = (function() {
	const self = {}
		, AudioCtx = window.AudioContext || window.webkitAudioContext;
	let sd, v, vc, t, footer, size, vol, video, iceServers
		, lm, level, userSpeaks = false, muteOthers;

	function _resizeDlgArea(_w, _h) {
		if (Room.getOptions().interview) {
			VideoUtil.setPos(v, VideoUtil.getPos());
		} else if (v.dialog('instance')) {
			v.dialog('option', 'width', _w).dialog('option', 'height', _h);
		}
	}
	function _micActivity(speaks) {
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
		} else if (VideoUtil.isChrome(b)) {
			cnts = {
				video: true
			};
			promise = navigator.mediaDevices.getDisplayMedia(cnts);
		} else {
			promise = new Promise(() => {
				Sharer.close();
				throw 'Screen-sharing is not supported in ' + b.name + '[' + b.major + ']';
			});
		}
		promise.then(function(stream) {
			__createVideo();
			callback(msg, cnts, stream);
		}).catch(__handleScreenError);
	}
	function _getVideoStream(msg, callback) {
		VideoSettings.constraints(sd, function(cnts) {
			if ((VideoUtil.hasCam(sd) && !cnts.video) || (VideoUtil.hasMic(sd) && !cnts.audio)) {
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
					const data = {};
					if (stream.getAudioTracks().length !== 0) {
						lm = vc.find('.level-meter');
						lm.show();
						data.aCtx = new AudioCtx();
						data.gainNode = data.aCtx.createGain();
						data.analyser = data.aCtx.createAnalyser();
						data.aSrc = data.aCtx.createMediaStreamSource(stream);
						data.aSrc.connect(data.gainNode);
						data.gainNode.connect(data.analyser);
						if (VideoUtil.isEdge()) {
							data.analyser.connect(data.aCtx.destination);
						} else {
							data.aDest = data.aCtx.createMediaStreamDestination();
							data.analyser.connect(data.aDest);
							data.aSrc.origStream = stream;
							_stream = data.aDest.stream;
							stream.getVideoTracks().forEach(function(track) {
								_stream.addTrack(track);
							});
						}
					}
					__createVideo(data);
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
		const data = video.data();
		data.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(
			VideoUtil.addIceServers(options, msg)
			, function (error) {
				if (error) {
					return OmUtil.error(error);
				}
				if (data.analyser) {
					level = MicLevel();
					level.meter(data.analyser, lm, _micActivity, OmUtil.error);
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
		__createVideo();
		const options = VideoUtil.addIceServers({
			remoteVideo : video[0]
			, onicecandidate : self.onIceCandidate
		}, msg);
		const data = video.data();
		data.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(
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
		if (!footer || !footer.is(':visible')) {
			return;
		}
		if (state) {
			footer.text(footer.data('on'));
			footer.addClass('mic-on');
			t.addClass('mic-on');
		} else {
			footer.text(footer.data('off'));
			footer.removeClass('mic-on');
			t.removeClass('mic-on');
		}
	}
	function _initContainer(_id, name, opts) {
		const hasVideo = VideoUtil.hasVideo(sd);
		let contSel;
		if (hasVideo) {
			if (opts.interview) {
				const area = $('.pod-area');
				const contId = uuidv4();
				contSel = '#' + contId;
				area.append($('<div class="pod"></div>').attr('id', contId));
				WbArea.updateAreaClass();
			} else {
				contSel = '.room-block .room-container';
			}
		} else {
			contSel = '#user' + sd.cuid;
		}
		$(contSel).append(OmUtil.tmpl('#user-video', _id)
				.attr('title', name)
				.attr('data-client-uid', sd.cuid)
				.attr('data-client-type', sd.type)
				.data(self));
		v = $('#' + _id);
		vc = v.find('.video');
		muteOthers = vc.find('.mute-others');
		_setRights();
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
			.append($('#video-refresh-btn').children().clone());
		v.parent().find('.ui-dialog-titlebar-refresh')
			.click(function(e) {
				e.stopImmediatePropagation();
				_refresh();
				return false;
			}).dblclick(function(e) {
				e.stopImmediatePropagation();
				return false;
			});
	}
	function _init(msg) {
		sd = msg.stream;
		vol = Volume();
		iceServers = msg.iceServers;
		sd.activities = sd.activities.sort();
		size = {width: sd.width, height: sd.height};
		const _id = VideoUtil.getVid(sd.uid)
			, name = sd.user.displayName
			, _w = sd.width
			, _h = sd.height
			, isSharing = VideoUtil.isSharing(sd)
			, isRecording = VideoUtil.isRecording(sd)
			, hasVideo = VideoUtil.hasVideo(sd)
			, opts = Room.getOptions();
		sd.self = sd.cuid === opts.uid;
		const contSel = _initContainer(_id, name, opts);
		footer = v.find('.footer');
		if (!opts.showMicStatus) {
			footer.hide();
		}
		if (!sd.self && isSharing) {
			Sharer.close();
		}
		if (sd.self && (isSharing || isRecording)) {
			v.hide();
		} else if (hasVideo) {
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
		if (hasVideo && !isSharing && !isRecording) {
			_initCamDialog();
		}
		t = v.parent().find('.ui-dialog-titlebar').attr('title', name);
		v.on('remove', _cleanup);
		if (hasVideo) {
			vc.width(_w).height(_h);
		}

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
		const name = sd.user.displayName
			, hasVideo = VideoUtil.hasVideo(sd);
		if (hasVideo) {
			v.dialog('option', 'title', name).parent().find('.ui-dialog-titlebar').attr('title', name);
		}
		const same = prevA.length === sd.activities.length && prevA.every(function(value, index) { return value === sd.activities[index]})
		if (sd.self && !same) {
			_refresh();
		}
	}
	function __createVideo(data) {
		const _id = VideoUtil.getVid(sd.uid);
		const hasVideo = VideoUtil.hasVideo(sd);
		_resizeDlgArea(hasVideo ? size.width : 120
			, hasVideo ? size.height : 90);
		video = $(hasVideo ? '<video>' : '<audio>').attr('id', 'vid' + _id)
			.width(vc.width()).height(vc.height())
			.prop('autoplay', true).prop('controls', false);
		if (data) {
			video.data(data);
		}
		if (hasVideo) {
			vc.removeClass('audio-only').css('background-image', '');;
			vc.parents('.ui-dialog').removeClass('audio-only');
			video.attr('poster', sd.user.pictureUri);
		} else {
			vc.addClass('audio-only');
		}
		vc.append(video);
		if (VideoUtil.hasMic(sd)) {
			const volIco = vol.create(self)
			if (hasVideo) {
				v.parent().find('.ui-dialog-titlebar-buttonpane').append(volIco);
			} else {
				volIco.addClass('ulist-small');
				volIco.insertAfter('#user' + sd.cuid + ' .typing-activity');
			}
		} else {
			vol.destroy();
		}
	}
	function _refresh(_msg) {
		const msg = _msg || {iceServers: iceServers};
		_cleanup();
		const hasAudio = VideoUtil.hasMic(sd);
		if (sd.self) {
			_createSendPeer(msg);
			_handleMicStatus(hasAudio);
		} else {
			_createResvPeer(msg);
		}
	}
	function _setRights() {
		if (Room.hasRight(['MUTE_OTHERS']) && VideoUtil.hasMic(sd)) {
			muteOthers.addClass('enabled').click(function() {
				VideoManager.clickMuteOthers(sd.uid);
			});
		} else {
			muteOthers.removeClass('enabled').off();
		}
	}
	function _cleanup() {
		OmUtil.log('Disposing participant ' + sd.uid);
		if (video && video.length > 0) {
			const data = video.data();
			if (data.analyser) {
				VideoUtil.disconnect(data.analyser);
				data.analyser = null;
			}
			if (data.gainNode) {
				VideoUtil.disconnect(data.gainNode);
				data.gainNode = null;
			}
			if (data.aSrc) {
				VideoUtil.cleanStream(data.aSrc.mediaStream);
				VideoUtil.cleanStream(data.aSrc.origStream);
				VideoUtil.disconnect(data.aSrc);
				data.aSrc = null;
			}
			if (data.aDest) {
				VideoUtil.disconnect(data.aDest);
				data.aDest = null;
			}
			if (data.aCtx) {
				if (data.aCtx.destination) {
					VideoUtil.disconnect(data.aCtx.destination);
				}
				data.aCtx.close();
				data.aCtx = null;
			}
			video.attr('id', 'dummy');
			const vidNode = video[0];
			VideoUtil.cleanStream(vidNode.srcObject);
			vidNode.srcObject = null;
			vidNode.parentNode.removeChild(vidNode);

			VideoUtil.cleanPeer(data.rtcPeer);
			video = null;
		}
		if (lm && lm.length > 0) {
			_micActivity(false);
			lm.hide();
			muteOthers.removeClass('enabled').off();
		}
		if (level) {
			level.dispose();
			level = null;
		}
		vc.find('audio,video').remove();
	}
	function _reattachStream() {
		if (video && video.length > 0) {
			const data = video.data();
			if (data.rtcPeer) {
				video[0].srcObject = sd.self ? data.rtcPeer.getLocalStream() : data.rtcPeer.getRemoteStream();
			}
		}
	}

	self.update = _update;
	self.refresh = _refresh;
	self.mute = function(_mute) {
		vol.mute(_mute);
	};
	self.isMuted = function() {
		return vol.muted();
	};
	self.init = _init;
	self.stream = function() { return sd; };
	self.setRights = _setRights;
	self.getPeer = function() { return video ? video.data().rtcPeer : null; };
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
	self.reattachStream = _reattachStream;
	self.video = function() {
		return video;
	};
	self.handleMicStatus = _handleMicStatus;
	return self;
});
