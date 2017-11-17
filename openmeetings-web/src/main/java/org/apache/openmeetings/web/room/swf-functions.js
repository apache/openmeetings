/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var labels;
function initSwf(el, swf, id, options) {
	const type = 'application/x-shockwave-flash'
		, src = 'public/' + swf + '?cache' + new Date().getTime()
		, o = $('<object>').attr('id', id).attr('type', type).attr('data', src).attr('width', options.width).attr('height', options.height);
	o.append($('<param>').attr('name', 'quality').attr('value', 'best'))
		.append($('<param>').attr('name', 'wmode').attr('value', options.wmode))
		.append($('<param>').attr('name', 'allowscriptaccess').attr('value', 'sameDomain'))
		.append($('<param>').attr('name', 'allowfullscreen').attr('value', 'false'))
		.append($('<param>').attr('name', 'flashvars').attr('value', $.param(options)));
	el.append(o);
	return o;
}
function getStringLabels() {
	return labels;
}
