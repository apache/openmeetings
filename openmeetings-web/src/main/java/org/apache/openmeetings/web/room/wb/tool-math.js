/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var TMath = function(wb, s) {
	const math = ShapeBase();
	math.obj = null;

	math.mouseDown = function(o) {
		const canvas = this
			, pointer = canvas.getPointer(o.e)
			, ao = canvas.getActiveObject();
		if (!!ao && ao.omType === 'Math') {
			math.obj = ao;
		} else {
			function tex2svg(tex, callback) {
				const wrapper = $("<div>").html(tex);
				MathJax.Hub.Queue(["Typeset", MathJax.Hub, wrapper[0]]);
				MathJax.Hub.Queue(function() {
					const mjOut = wrapper[0].getElementsByTagName("svg")[0];
					$(mjOut).attr("xmlns", "http://www.w3.org/2000/svg");
					callback(mjOut.outerHTML);
				});
			}
			tex2svg('\\[f(a) = \\frac{1}{2\\pi i} \\oint\\frac{f(z)}{z-a}dz;   \\sqrt{x}   \\]', function(svg) {
				fabric.loadSVGFromString(svg, function(objects, options) {
					math.obj = fabric.util.groupSVGElements(objects, options);
					//this have to be checked ....
					canvas.add(math.obj.set({
						scaleX: 10
						, scaleY: 10
						, left: pointer.x
						, top: pointer.y
						, omType: 'Math'
					})).requestRenderAll();
					math.objectCreated(math.obj, canvas);
				});
			});
		}
	};
	math.activate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.on('mouse:down', math.mouseDown);
			canvas.selection = true;
			canvas.forEachObject(function(o) {
				if (o.omType === 'Math') {
					o.selectable = true;
				}
			});
		});
		ToolUtil.disableAllProps(s);
	};
	math.deactivate = function() {
		wb.eachCanvas(function(canvas) {
			canvas.off('mouse:down', math.mouseDown);
			canvas.selection = false;
			canvas.forEachObject(function(o) {
				if (o.omType === 'Math') {
					o.selectable = false;
				}
			});
		});
	};

	return math;
};
