/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbToolBase = require('./wb-tool-base');
const ToolUtil = require('./wb-tool-util');
require('fabric');

module.exports = class APointer extends WbToolBase {
	constructor(wb, settings, sBtn) {
		super();
		this.user = '';
		this.wb = wb;

		const self = this;
		function _mouseUp(o) {
			const canvas = this
				, ptr = canvas.getPointer(o.e);
			if (self.user === '') {
				self.user = $('.room-block .sidebar .user-list .current .name').text();
			}
			const obj = {
				omType: 'pointer'
				, x: ptr.x
				, y: ptr.y
				, user: self.user
			};
			obj.uid = self.objectCreated.call(self, obj, canvas);
			self.create.call(self, canvas, obj);
		}

		this.activate = () => {
			this.wb.eachCanvas(function(canvas) {
				canvas.selection = false;
				canvas.on('mouse:up', _mouseUp);
			});
			ToolUtil.disableAllProps(settings);
			sBtn.addClass('disabled');
		}

		this.deactivate = () => {
			this.wb.eachCanvas(function(canvas) {
				canvas.off('mouse:up', _mouseUp);
			});
		};
	}

	create(canvas, o) {
		const zoom = this.wb.getZoom();
		fabric.Image.fromURL('./css/images/pointer.png', function(img) {
			const scale = 1. / zoom;
			img.set({
				left:15
				, originX: 'right'
				, originY: 'top'
			});
			const circle1 = new fabric.Circle({
				radius: 20
				, stroke: '#ff6600'
				, strokeWidth: 2
				, fill: ToolUtil.noColor
				, originX: 'center'
				, originY: 'center'
			});
			const circle2 = new fabric.Circle({
				radius: 6
				, stroke: '#ff6600'
				, strokeWidth: 2
				, fill: ToolUtil.noColor
				, originX: 'center'
				, originY: 'center'
			});
			const text = new fabric.Text(o.user, {
				fontSize: 12
				, left: 10
				, originX: 'left'
				, originY: 'bottom'
			});
			const group = new fabric.Group([circle1, circle2, img, text], {
				left: o.x - 20
				, top: o.y - 20
				, scaleX: scale
				, scaleY: scale
			});

			canvas.add(group);
			group.uid = o.uid;
			group.loaded = !!o.loaded;

			const count = 3;
			function go(_cnt) {
				if (_cnt < 0) {
					canvas.remove(group);
					return;
				}
				circle1.set({radius: 3});
				circle2.set({radius: 6});
				circle1.animate(
					'radius', '20'
					, {
						onChange: canvas.renderAll.bind(canvas)
						, duration: 1000
						, onComplete: function() {go(_cnt - 1);}
					});
				circle2.animate(
					'radius', '20'
					, {
						onChange: canvas.renderAll.bind(canvas)
						, duration: 1000
					});
			}
			go(count);
		});
	}
};
