/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');

module.exports = class Volume {
	constructor() {
		const self = this;
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

		this.create = (_video) => {
			video = _video;
			this.destroy();
			const uid = video.stream().uid
				, cuid = video.stream().cuid
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
					OmUtil.roomAction({action: 'mute', uid: cuid, mute: !muted});
					self.mute(!muted);
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
					self.handle(ui.value);
				}
			});
			this.handle(lastVolume);
			this.mute(muted);
			return vol;
		};
		this.handle = (val) => {
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
		};
		this.mute = (mute) => {
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
				this.handle(0);
			} else {
				slider.slider('option', 'value', lastVolume);
				this.handle(lastVolume);
			}
		};
		this.destroy = () => {
			if (vol) {
				vol.remove();
				vol = null;
			}
		};
		this.muted = () => {
			return muted;
		};
	}
};
