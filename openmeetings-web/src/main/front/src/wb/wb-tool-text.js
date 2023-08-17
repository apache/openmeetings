/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const VideoUtil = require('../settings/video-util');

const ShapeBase = require('./wb-tool-shape-base');
const ToolUtil = require('./wb-tool-util');
require('fabric');

module.exports = class Text extends ShapeBase {
	constructor(wb, settings, sBtn) {
		super();
		Object.assign(this, {
			obj: null
			, omType: 'i-text'
			, style: {
				bold: false
				, italic: false
			}
		});
		this.fill.color = '#000000';
		Object.assign(this.stroke, {
			enabled: false
			, width: 50 //fontSize
			, color: '#000000'
		});

		const self = this;
		function _mouseDown(o) {
			const canvas = this
				, pointer = canvas.getPointer(o.e)
				, ao = self.__getObj.call(self, canvas, o.target);
			if (!!ao) {
				self.obj = ao;
			} else {
				self.obj = self.createTextObj.call(self, canvas, pointer);
				if (self.style.bold) {
					self.obj.fontWeight = 'bold'
				}
				if (self.style.italic) {
					self.obj.fontStyle = 'italic'
				}
				canvas.add(self.obj).setActiveObject(self.obj);
			}
			self._onMouseDown.call(self);
		};
		this.activate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.on('mouse:down', _mouseDown);
				canvas.on('mouse:dblclick', self._doubleClick);
				canvas.selection = true;
				canvas.forEachObject(function(o) {
					if (self._editable(o)) {
						o.selectable = true;
						o.editable = true;
					}
				});
			});
			this.fontFamily = $('#wb-text-style-block').css('font-family');
			ToolUtil.enableAllProps(settings, this);
			const b = settings.find('.wb-prop-b').button("enable");
			if (this.style.bold) {
				b.addClass('ui-state-active selected');
			} else {
				b.removeClass('ui-state-active selected');
			}
			const i = settings.find('.wb-prop-i').button("enable");
			if (this.style.italic) {
				i.addClass('ui-state-active selected');
			} else {
				i.removeClass('ui-state-active selected');
			}
			this._onActivate();
			VideoUtil.highlight(sBtn.removeClass('disabled'), 'bg-warning', 5);
		};
		this.deactivate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.off('mouse:down', _mouseDown);
				canvas.off('mouse:dblclick', self._doubleClick);
				canvas.selection = false;
				canvas.forEachObject(function(o) {
					if (self.omType === o.omType) {
						o.selectable = false;
						o.editable = false;
					}
				});
			});
			this._onDeactivate();
		};
	}

	_doubleClick() {}

	__valid(o) {
		return !!o && this.omType === o.omType;
	}

	__getObj(canvas, o) {
		if (this.__valid(o)) {
			return o;
		} else {
			const _o = canvas.getActiveObject();
			return this.__valid(_o) ? _o : null;
		}
	}

	createTextObj(_, pointer) {
		return new fabric.IText('', {
			left: pointer.x
			, top: pointer.y
			, padding: 7
			, omType: this.omType
			, fill: this.fill.enabled ? this.fill.color : ToolUtil.noColor
			, stroke: this.stroke.enabled ? this.stroke.color : ToolUtil.noColor
			, fontSize: this.stroke.width
			, fontFamily: this.fontFamily
			, opacity: this.opacity
		});
	}

	_onMouseDown() {
		this.obj.enterEditing();
	}

	_onActivate() {
		WbArea.removeDeleteHandler();
	}

	_onDeactivate() {
		WbArea.addDeleteHandler();
	}

	_editable(o) {
		return this.omType === o.omType;
	}
};
