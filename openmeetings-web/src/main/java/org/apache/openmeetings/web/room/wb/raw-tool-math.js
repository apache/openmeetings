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
				callback(mjOut);
			}
		});
	}
	function create(o, canvas, callback, errCallback) {
		tex2svg(o.formula, function(svg) {
			fabric.parseSVGDocument(svg, function(objects, options) {
				const opts = $.extend({}, o, options)
					, obj = objects.length === 1
						? new fabric.Group(objects, opts)
						: fabric.util.groupSVGElements(objects, opts);
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
var TMath = function(wb, s, sBtn) {
	const math = ShapeBase();
	math.obj = null;

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
		if (!!ao && ao.omType === 'Math') {
			_enableUpdate(upd, ao);
			ta.val(ao.formula);
			return false;
		} else {
			upd.button('disable');
			return true;
		}
	}
	math.mouseDown = function(o) {
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
					_enableUpdate(upd, math.obj);
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
		_updateDisabled();
		ToolUtil.disableAllProps(s);
		sBtn.addClass('disabled');
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
		wb.getFormula().find('.update-btn').button('disable');
	};

	return math;
};
