/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var StaticTMath = (function() {
	function tex2svg(tex, callback, _errCallback) {
		const errCallback = _errCallback || function() {};
		let error = false;
		const wrapper = $('<div>').html('\\[' + tex + '\\]');
		MathJax.Hub.Register.MessageHook('TeX Jax - parse error', function(message) {
			errCallback(message[1]);
			error = true;
		});
		MathJax.Hub.Register.MessageHook('Math Processing Error', function(message) {
			errCallback(message[2]);
			error = true;
		});
		MathJax.Hub.Queue(['Typeset', MathJax.Hub, wrapper[0]]);
		MathJax.Hub.Queue(function() {
			if (!error) {
				const mjOut = wrapper[0].getElementsByTagName('svg')[0];
				$(mjOut).attr('xmlns', 'http://www.w3.org/2000/svg');
				callback(mjOut.outerHTML);
			}
		});
	}
	function create(o, canvas, callback, errCallback) {
		tex2svg(o.formula, function(svg) {
			fabric.loadSVGFromString(svg, function(objects, options) {
				const obj = fabric.util.groupSVGElements(objects, $.extend({}, o, options));
				obj.selectable = canvas.selection;
				obj.type = 'group';
				if (typeof(callback) === 'function') {
					callback(obj);
				}
				canvas.add(obj).requestRenderAll();
			});
		}, errCallback);
	}
	function highlight(el) {
		el.addClass('ui-state-highlight', 2000, function() {
			el.focus();
			el.removeClass('ui-state-highlight', 2000);
		});
	}

	return {
		tex2svg: tex2svg
		, create: create
		, highlight: highlight
	}
})();
var TMath = function(wb, s) {
	const math = ShapeBase();
	math.obj = null;

	math.mouseDown = function(o) {
		const canvas = this
			, pointer = canvas.getPointer(o.e)
			, ao = canvas.getActiveObject()
			, fml = wb.getFormula()
			, ta = fml.find('textarea')
			, upd = fml.find('.update-btn');
		fml.show();
		if (!!ao && ao.omType === 'Math') {
			upd.data('uid', ao.uid);
			upd.data('slide', ao.slide);
			ta.val(ao.formula);
		} else {
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
					, omType: 'Math'
					, formula: ta.val()
				}
				, canvas
				, function(obj) {
					math.obj = obj;
					math.objectCreated(math.obj, canvas);
					if (wb.getRole() !== NONE) {
						canvas.setActiveObject(math.obj);
					}
					upd.data('uid', math.obj.uid);
					upd.data('slide', math.obj.slide);
				}
				, function(msg) {
					err.text(msg);
					StaticTMath.highlight(err);
				}
			);
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
