/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */

// Based on this example https://github.com/mathjax/MathJax-demos-node/blob/4.1.0/mjs/direct/tex2svg
import {TeX} from '@mathjax/src/js/input/tex.js';
import {SVG} from '@mathjax/src/js/output/svg.js';
import {RegisterHTMLHandler} from '@mathjax/src/js/handlers/html.js';
import {liteAdaptor} from '@mathjax/src/js/adaptors/liteAdaptor.js';
import {mathjax} from '@mathjax/src/js/mathjax.js';
import {MathJaxNewcmFont} from '@mathjax/mathjax-newcm-font/js/chtml.js';
import {AmsConfiguration} from '@mathjax/src/mjs/input/tex/ams/AmsConfiguration.js';
import {NewcommandConfiguration} from '@mathjax/src/mjs/input/tex/newcommand/NewcommandConfiguration.js';
import {TextMacrosConfiguration} from '@mathjax/src/mjs/input/tex/textmacros/TextMacrosConfiguration.js';
import {NoUndefinedConfiguration} from '@mathjax/src/mjs/input/tex/noundefined/NoUndefinedConfiguration.js';
const FONT_SIZE = 16;

const tex = new TeX({
	packages: [
		"base",
		"ams",
		"newcommand",
		"textmacros",
		"noundefined"
	],
	formatError: (jax, err) => {
		return jax.formatError(err);
	},
});
const svg = new SVG({
	exFactor: 0.5,
	displayAlign: "center",
	displayIndent: "0em",
	displayOverflow: "overflow",
	linebreaks: {
		inline: false,
	},
	mathmlSpacing: false,
	fontCache: "local",
	useXlink: false,
});

RegisterHTMLHandler(liteAdaptor({fontSize: FONT_SIZE}));
const mathDoc = mathjax.document('', {
	InputJax: tex,
	OutputJax: svg,
});

import * as fabric from 'fabric';

export class StaticTMath {
	static create(o, canvas, callback, errCallback) {
		mathDoc.convertPromise(o.formula, {
			display: false,
			em: FONT_SIZE, // em-size in pixels
			ex: 8,         // ex-size in pixels
			containerWidth: 80 * FONT_SIZE,
		})
		.then(node => {
			const adaptor = mathDoc.adaptor;
			const svg = adaptor.getElement('svg', node);
			delete svg.styles;
			delete svg.attributes.style;
			return adaptor.serializeXML(svg);
		})
		.then(fabric.loadSVGFromString)
		.then(({ objects, options }) => {
			if (objects.length === 0) {
				return; // nothing to draw
			}
			const opts = Object.assign({}, o, options);
			const obj = objects.length === 1
					? new fabric.Group(objects, opts)
					: fabric.util.groupSVGElements(objects, opts);
			obj.selectable = canvas.selection;
			if (typeof(callback) === 'function') {
				callback(obj);
			}
			canvas.add(obj);
			canvas.requestRenderAll();
		})
		.catch(function (err) {
			errCallback(err.message);
		});
	}

	static highlight(el) {
		el.addClass('ui-state-highlight', 2000, function() {
			el.focus();
			el.removeClass('ui-state-highlight', 2000);
		});
	}
};
