/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmUtil = require('../main/omutils');
const Settings = require('../main/settings');

const Role = require('./wb-role');
const WbUtils = require('./wb-utils');
const APointer = require('./wb-tool-apointer');
const Pointer = require('./wb-tool-pointer');
const Text = require('./wb-tool-text');
const Textbox = require('./wb-tool-textbox');
const Whiteout = require('./wb-tool-whiteout');
const Paint = require('./wb-tool-paint');
const Line = require('./wb-tool-line');
const ULine = require('./wb-tool-uline');
const Rect = require('./wb-tool-rect');
const Ellipse = require('./wb-tool-ellipse');
const Arrow = require('./wb-tool-arrow');
const Clipart = require('./wb-tool-clipart');
const StaticTMath = require('./wb-tool-stat-math');
const TMath = require('./wb-tool-math');

const ACTIVE = 'active';

function __validBtn(btn) {
	return !!btn && btn.length === 1
		&& typeof(btn.data) === 'function'
		&& typeof(btn.data()) === 'object'
		&& typeof(btn.data().deactivate) === 'function';
}
function _setCurrent(c, _cur) {
	const hndl = c.find('a')
		, cur = _cur || c.find('div.om-icon.big:first')
	c.attr('title', cur.attr('title'));
	hndl.find('.om-icon').remove();
	hndl.prepend(cur.clone().addClass('stub').data('toolType', cur.data('toolType')));
}
function _initGroup(__id, e) {
	const c = OmUtil.tmpl(__id);
	e.after(c);
	c.find('.om-icon').each(function() {
		const cur = $(this);
		cur.click(function() {
			_setCurrent(c, cur);
		});
	});
	return c;
}
function __destroySettings() {
	$('#wb-settings').modal('dispose');
}

module.exports = class WbTools {
	constructor(wbEl, wb) {
		let mode, tools, settings, math;

		function _initGroupHandle(c) {
			c.find('a').off().click(function(e) {
				e.stopImmediatePropagation()
				//let's close all other dropdowns
				$(this).parents('.tools').find('.dropdown-toggle.show').toArray().forEach(menu => {
					const dd = bootstrap.Dropdown.getInstance(menu);
					if (menu !== this && dd) {
						dd.hide();
					}
				});
				const stub = $(this).find('.stub');
				if (!stub.hasClass(ACTIVE)) {
					_btnClick(stub.data('toolType'));
					stub.addClass(ACTIVE);
				}
			});
			_setCurrent(c);
		}
		function _getBtn(m) {
			return !!tools ? tools.find('.om-icon.' + (m || mode) + ':not(.stub)') : null;
		}
		function _cleanActive() {
			!!tools && tools.find('.om-icon.' + ACTIVE).removeClass(ACTIVE);
		}
		function _setActive() {
			!!tools && tools.find('.om-icon.' + mode).addClass(ACTIVE);
		}
		function _btnClick(toolType) {
			const b = _getBtn();
			if (__validBtn(b)) {
				b.data().deactivate();
			}
			_cleanActive();
			_getBtn('string' === typeof(toolType) && !!toolType ? toolType : $(this).data('toolType')).data().activate();
			_setActive();
		}
		function _initToolBtn(m, def, obj) {
			const btn = _getBtn(m);
			if (!btn || btn.length === 0) {
				return;
			}
			btn.data({
				obj: obj
				, toolType: m
				, activate: function() {
					if (!btn.hasClass(ACTIVE)) {
						mode = m;
						obj.activate();
					}
				}
				, deactivate: function() {
					obj.deactivate();
				}
			}).click(_btnClick);
			if (def) {
				btn.data().activate();
			}
		}
		function _initTexts(sBtn) {
			const c = _initGroup('#wb-area-texts', _getBtn('apointer'));
			_initToolBtn('text', false, new Text(wb, settings, sBtn));
			_initToolBtn('textbox', false, new Textbox(wb, settings, sBtn));
			_initGroupHandle(c, tools);
		}
		function _initDrawings(sBtn) {
			const c = _initGroup('#wb-area-drawings', tools.find('.texts'));
			_initToolBtn('eraser', false, new Whiteout(wb, settings, sBtn));
			_initToolBtn('paint', false, new Paint(wb, settings, sBtn));
			_initToolBtn('line', false, new Line(wb, settings, sBtn));
			_initToolBtn('uline', false, new ULine(wb, settings, sBtn));
			_initToolBtn('rect', false, new Rect(wb, settings, sBtn));
			_initToolBtn('ellipse', false, new Ellipse(wb, settings, sBtn));
			_initToolBtn('arrow', false, new Arrow(wb, settings, sBtn));
			_initGroupHandle(c, tools);
		}
		function _initCliparts(sBtn) {
			const c = OmUtil.tmpl('#wb-area-cliparts');
			tools.find('.drawings').after(c);
			c.find('.om-icon.clipart').each(function() {
				const cur = $(this);
				cur.css('background-image', 'url(' + cur.data('image') + ')')
					.click(function() {
						_setCurrent(c, cur);
					});
				_initToolBtn(cur.data('mode'), false, new Clipart(wb, cur, settings, sBtn));
			});
			_initGroupHandle(c, tools);
		}
		function _initSettings() {
			function setStyle(canvas, styleName, value) {
				const o = canvas.getActiveObject();
				if (!o) {
					return;
				}
				if (o.setSelectionStyles && o.isEditing) {
					const style = {};
					style[styleName] = value;
					o.setSelectionStyles(style);
				} else {
					o[styleName] = value;
				}
				canvas.requestRenderAll();
			}
			settings.find('.wb-prop-b, .wb-prop-i')
				.button()
				.click(function() {
					$(this).toggleClass('ui-state-active selected');
					const btn = _getBtn()
						, isB = $(this).hasClass('wb-prop-b')
						, style = isB ? 'bold' : 'italic'
						, v = $(this).hasClass('selected')
						, val = v ? style : '';
					btn.data().obj.style[style] = v;
					wb.eachCanvas(function(canvas) {
						setStyle(canvas, isB ? 'fontWeight' : 'fontStyle', val)
					});
				});
			settings.find('.wb-prop-lock-color, .wb-prop-lock-fill')
				.button({icon: 'ui-icon-locked', showLabel: false})
				.click(function() {
					const btn = _getBtn()
						, isColor = $(this).hasClass('wb-prop-lock-color')
						, c = settings.find(isColor ? '.wb-prop-color' : '.wb-prop-fill')
						, enabled = $(this).button('option', 'icon') === 'ui-icon-locked';
					$(this).button('option', 'icon', enabled ? 'ui-icon-unlocked' : 'ui-icon-locked');
					c.prop('disabled', !enabled);
					btn.data().obj[isColor ? 'stroke' : 'fill'].enabled = enabled;
				});
			settings.find('.wb-prop-color').change(function() {
				const btn = _getBtn();
				if (btn.length === 1) {
					const v = $(this).val();
					btn.data().obj.stroke.color = v;
					wb.eachCanvas(function(canvas) {
						if ('paint' === mode) {
							canvas.freeDrawingBrush.color = v;
						} else {
							setStyle(canvas, 'stroke', v)
						}
					});
				}
			});
			settings.find('.wb-prop-width').change(function() {
				const btn = _getBtn();
				if (btn.length === 1) {
					const v = 1 * $(this).val();
					btn.data().obj.stroke.width = v;
					wb.eachCanvas(function(canvas) {
						if ('paint' === mode) {
							canvas.freeDrawingBrush.width = v;
						} else {
							setStyle(canvas, 'strokeWidth', v)
						}
					});
				}
			});
			settings.find('.wb-prop-fill').change(function() {
				const btn = _getBtn();
				if (btn.length === 1) {
					const v = $(this).val();
					btn.data().obj.fill.color = v;
					wb.eachCanvas(function(canvas) {
						setStyle(canvas, 'fill', v)
					});
				}
			});
			settings.find('.wb-prop-opacity').change(function() {
				const btn = _getBtn();
				if (btn.length === 1) {
					const v = (1 * $(this).val()) / 100;
					btn.data().obj.opacity = v;
					wb.eachCanvas(function(canvas) {
						if ('paint' === mode) {
							canvas.freeDrawingBrush.opacity = v;
						} else {
							setStyle(canvas, 'opacity', v)
						}
					});
				}
			});
			settings.find('.ui-dialog-titlebar-close').click(function() {
				settings.hide();
			});
			settings.draggable({
				scroll: false
				, handle: '.ui-dialog-titlebar'
				, containment: 'body'
				, start: function() {
					if (!!settings.css('bottom')) {
						settings.css('bottom', '').css(Settings.isRtl ? 'left' : 'right', '');
					}
				}
				, drag: function() {
					if (settings.position().x + settings.width() >= settings.parent().width()) {
						return false;
					}
				}
			});
		}
		function _initMath() {
			math.find('.ui-dialog-titlebar-close').click(function() {
				math.hide();
			});
			math.find('.update-btn').button().click(function() {
				const o = wb._findObject({
					uid: $(this).data('uid')
					, slide: $(this).data('slide')
				});
				const json = wb._toOmJson(o);
				json.formula = math.find('textarea').val();
				const cnvs = wb.getCanvas(o.slide);
				StaticTMath.create(json, cnvs
					, function(obj) {
						wb.removeObj([o]);
						cnvs.fire('object:modified', {target: obj});
					}
					, function(msg) {
						const err = math.find('.status');
						err.text(msg);
						StaticTMath.highlight(err);
					});
			});
			math.draggable({
				scroll: false
				, handle: '.ui-dialog-titlebar'
				, containment: 'body'
				, start: function() {
					if (!!math.css('bottom')) {
						math.css('bottom', '').css(Settings.isRtl ? 'left' : 'right', '');
					}
				}
				, drag: function() {
					if (math.position().x + math.width() >= math.parent().width()) {
						return false;
					}
				}
			});
			math.resizable({
				minHeight: 140
				, minWidth: 255
			});
		}

		this.setRole = (role) => {
			const btn = _getBtn();
			if (__validBtn(btn)) {
				btn.data().deactivate();
			}
			wbEl.find('.tools>div').remove();
			wbEl.find('.wb-tool-settings').remove();
			WbUtils.safeRemove(tools);
			WbUtils.safeRemove(settings);
			WbUtils.safeRemove(math);
			if (role === Role.NONE) {
				__destroySettings();
				tools = !!Room.getOptions().questions ? OmUtil.tmpl('#wb-tools-readonly') : wbEl.find('invalid-wb-element');
			} else {
				tools = OmUtil.tmpl('#wb-tools');
				settings = OmUtil.tmpl('#wb-tool-settings');
				settings[0].style.display = 'none';
				settings[0].style.bottom = '100px';
				settings[0].style[(Settings.isRtl ? 'left' : 'right')] = '100px';
				math = OmUtil.tmpl('#wb-formula');
				math[0].style.display = 'none';
				math[0].style.bottom = '100px';
				math[0].style[(Settings.isRtl ? 'left' : 'right')] = '100px';
				wbEl.append(settings, math);
			}
			wbEl.find('.tools').append(tools);
			tools = wbEl.find('.tools>div');
			settings = wbEl.find('.wb-tool-settings');

			const clearAll = tools.find('.om-icon.clear-all')
				, sBtn = tools.find('.om-icon.settings');;
			clearAll.attr('data-bs-placement', Settings.isRtl ? 'right' : 'left');
			let _firstToolItem = true;
			switch (role) {
				case Role.PRESENTER:
					clearAll.confirmation({
						title: clearAll.attr('title')
						, confirmationEvent: 'om-clear-all'
						, container: wbEl[0]
						, onConfirm: () => OmUtil.wbAction({action: 'clearAll', data: {wbId: wb.getId()}})
					}).removeClass('disabled');
				case Role.WHITEBOARD:
					if (role === Role.WHITEBOARD) {
						clearAll.addClass('disabled');
					}
					_initToolBtn('pointer', _firstToolItem, new Pointer(wb, settings, sBtn));
					_firstToolItem = false;
					_initTexts(sBtn);
					_initDrawings(sBtn);
					_initToolBtn('math', _firstToolItem, new TMath(wb, settings, sBtn));
					_initCliparts(sBtn);
					tools.find('.om-icon.settings').click(function() {
						settings.show();
					});
					tools.find('.om-icon.math').click(function() {
						math.show();
					});
					const clearSlide = tools.find('.om-icon.clear-slide');
					clearSlide.confirmation({
						title: clearSlide.attr('title')
						, container: wbEl[0]
						, confirmationEvent: 'om-clear-slide'
						, onConfirm: () => OmUtil.wbAction({action: 'clearSlide', data: {wbId: wb.getId(), slide: wb.slide}})
					});
					tools.find('.om-icon.save').click(function() {
						OmUtil.wbAction({action: 'save', data: {wbId: wb.getId()}});
					});
					tools.find('.om-icon.undo').click(function() {
						OmUtil.wbAction({action: 'undo', data: {wbId: wb.getId()}});
					});
					tools.find('.om-icon.redo').click(function() {
						OmUtil.wbAction({action: 'redo', data: {wbId: wb.getId()}});
					});
					_initSettings();
					_initMath();
				case Role.NONE:
					_initToolBtn('apointer', _firstToolItem, new APointer(wb, settings, sBtn));
				default:
					//no-op
			}
		};
		this.reactivateBtn = () => {
			const b = _getBtn();
			if (__validBtn(b)) {
				b.data().deactivate();
				b.data().activate();
			}
		};
		this.updateCoordinates = (o) => {
			settings.find('.wb-dim-x').val(o.left);
			settings.find('.wb-dim-y').val(o.top);
			settings.find('.wb-dim-w').val(o.width);
			settings.find('.wb-dim-h').val(o.height);
		};
		this.destroy = () => {
			__destroySettings();
		};
		this.getMode = () => {
			return mode;
		};
		this.getMath = () => {
			return math;
		};
	}
};
