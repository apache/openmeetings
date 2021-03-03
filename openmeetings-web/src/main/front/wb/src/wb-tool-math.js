/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbShapeBase = require('./wb-tool-shape-base');
const ToolUtil = require('./wb-tool-util');
const Role = require('./wb-role');
const StaticTMath = require('./wb-tool-stat-math');

module.exports = class TMath extends WbShapeBase {
	static get TYPE() {
		return 'Math';
	}

	constructor(wb, settings, sBtn) {
		super();
		this.obj = null;

		const self = this;
		function _enableUpdate(upd, obj) {
			upd.data('uid', obj.uid);
			upd.data('slide', obj.slide);
			upd.button('enable');
		}
		function _updateDisabled(cnvs) {
			const canvas = cnvs || wb.getCanvas()
				, ao = canvas.getActiveObject()
				, fml = wb.getFormula()
				, ta = fml.find('textarea')
				, upd = fml.find('.update-btn');
			if (!!ao && ao.omType === TMath.TYPE) {
				_enableUpdate(upd, ao);
				ta.val(ao.formula);
				return false;
			} else {
				upd.button('disable');
				return true;
			}
		}
		function _mouseDown(o) {
			const canvas = this
				, pointer = canvas.getPointer(o.e)
				, fml = wb.getFormula()
				, ta = fml.find('textarea')
				, upd = fml.find('.update-btn');
			fml.show();
			if (_updateDisabled(canvas)) {
				const err = fml.find('.status');
				err.text('');
				if (ta.val().trim() === '') {
					StaticTMath.highlight(ta);
					return;
				}
				StaticTMath.create(
					{
						scaleX: 10
						, scaleY: 10
						, left: pointer.x
						, top: pointer.y
						, omType: TMath.TYPE
						, formula: ta.val()
					}
					, canvas
					, function(obj) {
						self.obj = obj;
						self.objectCreated.call(self, self.obj, canvas);
						if (wb.getRole() !== Role.NONE) {
							canvas.setActiveObject(self.obj);
						}
						_enableUpdate(upd, self.obj);
					}
					, function(msg) {
						err.text(msg);
						StaticTMath.highlight(err);
					}
				);
			}
		}
		this.activate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.on('mouse:down', _mouseDown);
				canvas.selection = true;
				canvas.forEachObject(function(o) {
					if (o.omType === TMath.TYPE) {
						o.selectable = true;
					}
				});
			});
			_updateDisabled();
			ToolUtil.disableAllProps(settings);
			sBtn.addClass('disabled');
		};
		this.deactivate = () => {
			wb.eachCanvas(function(canvas) {
				canvas.off('mouse:down', _mouseDown);
				canvas.selection = false;
				canvas.forEachObject(function(o) {
					if (o.omType === TMath.TYPE) {
						o.selectable = false;
					}
				});
			});
			wb.getFormula().find('.update-btn').button('disable');
		};
	}
};
