/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
require('fabric');

// Based on this example: https://github.com/mathjax/MathJax-demos-node/blob/master/preload/tex2svg
const packages = 'base, autoload, require, ams, newcommand, noundefined'.split(/\s*,\s*/);

MathJax = {
	options: {}
	, tex: {
		packages: packages
		, noundefined: {
			color: 'red'
			, background: ''
			, size: ''
		}
		, formatError(_, error) {
			throw error;
		}
	}
	, svg: {
		fontCache: 'local'
	}
	, startup: {
		typeset: false
	}
};

require('mathjax-full/components/src/startup/lib/startup.js');
require('mathjax-full/components/src/core/core.js');
require('mathjax-full/js/adaptors/browserAdaptor');
require('mathjax-full/components/src/input/tex-base/tex-base.js');
require('mathjax-full/components/src/input/tex/extensions/all-packages/all-packages.js');
require('mathjax-full/components/src/input/tex/extensions/noundefined/noundefined');
require('mathjax-full/components/src/output/svg/svg.js');
require('mathjax-full/components/src/output/svg/fonts/tex/tex.js');
require('mathjax-full/components/src/startup/startup.js');

MathJax.loader.preLoad(
	'core'
	, 'adaptors/browserAdaptor'
	, 'input/tex-base'
	, '[tex]/all-packages'
	, '[tex]/noundefined'
	, 'output/svg'
	, 'output/svg/fonts/tex'
);

MathJax.config.startup.ready();

function _tex2svg(tex, callback, _errCallback) {
	MathJax.tex2svgPromise(tex, {
		display: false
		, em: 16 // em-size in pixels
		, ex: 8 // ex-size in pixels
		, containerWidth: 80 * 16
	}).then(function (node) {
		callback(node.firstElementChild);
	}).catch(function (err) {
		_errCallback(err.message);
	});
}
function create(o, canvas, callback, errCallback) {
	_tex2svg(o.formula, function(svg) {
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

module.exports = {
	create: create
	, highlight: highlight
};
