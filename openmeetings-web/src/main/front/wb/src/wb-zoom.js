/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const Role = require('./wb-role');

const area = $('.room-block .wb-block .wb-area .tabs')
	, bar = area.find('.wb-tabbar')

module.exports = class WbZoom {
	constructor(wbEl, wbObj) {
		this.zoom = 1.;
		this.zoomMode = 'PAGE_WIDTH';

		let zoomBar;
		function _sendSetSize() {
			wbObj.doSetSize();
			OmUtil.wbAction({action: 'setSize', data: {
				wbId: wbObj.getId()
				, zoom: this.zoom
				, zoomMode: this.zoomMode
				, width: wbObj.getWidth()
				, height: wbObj.getHeight()
			}});
		}

		this.init = (wbo) => {
			this.zoom = wbo.zoom;
			this.zoomMode = wbo.zoomMode;
		};
		this.update = (role, ccount) => {
			if (ccount > 1 && role === Role.PRESENTER) {
				zoomBar.find('.doc-group input').prop("disabled", false);
				zoomBar.find('.doc-group button').prop("disabled", false);
				zoomBar.find('.doc-group .curr-slide').removeClass("text-muted");
				zoomBar.find('.doc-group .curr-slide').addClass("text-dark");
				zoomBar.find('.doc-group .input-group-text').removeClass("text-muted");
				const ns = 1 * slide;
				zoomBar.find('.doc-group .curr-slide').val(ns + 1).attr('max', ccount);
				zoomBar.find('.doc-group .up').prop('disabled', ns < 1);
				zoomBar.find('.doc-group .down').prop('disabled', ns > ccount - 2);
				zoomBar.find('.doc-group .last-page').text(ccount);
			} else {
				zoomBar.find('.doc-group .curr-slide').val(0);
				zoomBar.find('.doc-group .last-page').text("-");
				zoomBar.find('.doc-group .curr-slide').addClass("text-muted");
				zoomBar.find('.doc-group .curr-slide').removeClass("text-dark");
				zoomBar.find('.doc-group .input-group-text').addClass("text-muted");
				zoomBar.find('.doc-group input').prop("disabled", true);
				zoomBar.find('.doc-group button').prop("disabled", true);
			}
		};
		this.setRole = (role) => {
			wbEl.find('.wb-zoom').remove();
			zoomBar = OmUtil.tmpl('#wb-zoom');
			wbEl.find('.wb-zoom-block').append(zoomBar);

			switch (role) {
				case Role.PRESENTER:
					zoomBar.find('.curr-slide').change(function() {
						_setSlide($(this).val() - 1);
						showCurrentSlide();
					});
					zoomBar.find('.doc-group .up').click(function () {
						_setSlide(1 * slide - 1);
						showCurrentSlide();
					});
					zoomBar.find('.doc-group .down').click(function () {
						_setSlide(1 * slide + 1);
						showCurrentSlide();
					});
					zoomBar.find('.settings-group').show().find('.settings').click(function () {
						const wbs = $('#wb-settings')
							, wbsw = wbs.find('.wbs-width').val(width)
							, wbsh = wbs.find('.wbs-height').val(height);
						function isNumeric(n) {
							return !isNaN(parseInt(n)) && isFinite(n);
						}
						wbs.modal('show');
						wbs.find('.btn-ok').off().click(function() {
							const __w = wbsw.val(), __h = wbsh.val();
							if (isNumeric(__w) && isNumeric(__h)) {
								width = parseInt(__w);
								height = parseInt(__h);
								_sendSetSize();
							}
							wbs.modal('hide');
						})
					});
				case Role.WHITEBOARD:
					// fallthrough
				case Role.NONE:
					zoomBar.find('.zoom-out').click(function() {
						this.zoom -= .2;
						if (this.zoom < .1) {
							this.zoom = .1;
						}
						this.zoomMode = 'ZOOM';
						_sendSetSize();
					});
					zoomBar.find('.zoom-in').click(function() {
						this.zoom += .2;
						this.zoomMode = 'ZOOM';
						_sendSetSize();
					});
					zoomBar.find('.zoom').change(function() {
						const zzz = $(this).val();
						this.zoomMode = 'ZOOM';
						if (isNaN(zzz)) {
							switch (zzz) {
								case 'FULL_FIT':
								case 'PAGE_WIDTH':
									this.zoomMode = zzz;
									break;
								case 'custom':
									this.zoom = 1. * $(this).data('custom-val');
									break;
								default:
									//no-op
							}
						} else {
							this.zoom = 1. * zzz;
						}
						_sendSetSize();
					});
				default:
					//no-op
			}
		};
		this.setSize = () => {
			switch (this.zoomMode) {
				case 'FULL_FIT':
					this.zoom = Math.min((area.width() - 30) / wbObj.getWidth(), (area.height() - bar.height() - 30) / wbObj.getHeight());
					zoomBar.find('.zoom').val(this.zoomMode);
					break;
				case 'PAGE_WIDTH':
					this.zoom = (area.width() - 30 - 40) / wbObj.getWidth(); // bumper + toolbar
					zoomBar.find('.zoom').val(this.zoomMode);
					break;
				default:
				{
					const oo = zoomBar.find('.zoom').find('option[value="' + this.zoom.toFixed(2) + '"]');
					if (oo.length === 1) {
						oo.prop('selected', true);
					} else {
						zoomBar
							.find('.zoom')
								.data('custom-val', this.zoom)
							.find('option[value=custom]')
								.text((100. * this.zoom).toFixed(0) + '%')
								.prop('selected', true);
					}
				}
					break;
			}
		};
	}

	getZoom() {
		return this.zoom;
	}

	getMode() {
		return this.zoomMode;
	}
};
