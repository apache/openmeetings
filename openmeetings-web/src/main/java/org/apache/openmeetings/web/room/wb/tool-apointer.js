/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var APointer = function(wb, s) {
	const pointer = Base();
	pointer.user = '';
	pointer.create = function(canvas, o) {
		fabric.Image.fromURL('./css/images/pointer.png', function(img) {
			img.set({
				left:15
				, originX: 'right'
				, originY: 'top'
			});
			const circle1 = new fabric.Circle({
				radius: 20
				, stroke: '#ff6600'
				, strokeWidth: 2
				, fill: 'rgba(0,0,0,0)'
				, originX: 'center'
				, originY: 'center'
			});
			const circle2 = new fabric.Circle({
				radius: 6
				, stroke: '#ff6600'
				, strokeWidth: 2
				, fill: 'rgba(0,0,0,0)'
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
	pointer.mouseUp = function(o) {
		const canvas = this
			, ptr = canvas.getPointer(o.e);
		if (pointer.user === '') {
			pointer.user = $('.room.sidebar .user.list .current .name').text();
		}
		const obj = {
			type: 'pointer'
			, x: ptr.x
			, y: ptr.y
			, user: pointer.user
		};
		obj.uid = pointer.objectCreated(obj, canvas);
		pointer.create(canvas, obj);
	}
	pointer.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.selection = false;
			canvas.on('mouse:up', pointer.mouseUp);
		});
		ToolUtil.disableAllProps(s);
	}
	pointer.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:up', pointer.mouseUp);
		});
	};
	return pointer;
};
