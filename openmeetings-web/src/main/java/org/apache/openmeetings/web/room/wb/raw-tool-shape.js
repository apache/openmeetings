/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Shape = function(wb) {
	const shape = ShapeBase(wb);
	shape.obj = null;
	shape.isDown = false;
	shape.orig = {x: 0, y: 0};

	shape.add2Canvas = function(canvas) {
		canvas.add(shape.obj);
	}
	shape.mouseDown = function(o) {
		const canvas = this
			, pointer = canvas.getPointer(o.e);
		shape.isDown = true;
		shape.orig = {x: pointer.x, y: pointer.y};
		shape.createShape(canvas);
		shape.add2Canvas(canvas);
	};
	shape.mouseMove = function(o) {
		const canvas = this;
		if (!shape.isDown) return;
		const pointer = canvas.getPointer(o.e);
		shape.updateShape(pointer);
		canvas.requestRenderAll();
	};
	shape.updateCreated = function(o) {
		return o;
	};
	shape.mouseUp = function(o) {
		const canvas = this;
		shape.isDown = false;
		shape.obj.setCoords();
		shape.obj.selectable = false;
		canvas.requestRenderAll();
		shape.objectCreated(shape.obj, canvas);
	};
	shape.internalActivate = function() {};
	shape.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on({
				'mouse:down': shape.mouseDown
				, 'mouse:move': shape.mouseMove
				, 'mouse:up': shape.mouseUp
			});
		});
		shape.internalActivate();
	};
	shape.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off({
				'mouse:down': shape.mouseDown
				, 'mouse:move': shape.mouseMove
				, 'mouse:up': shape.mouseUp
			});
		});
	};
	return shape;
};
