/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const key = 'openmeetings';
function _load() {
	let s = {};
	try {
		s = JSON.parse(localStorage.getItem(key)) || s;
	} catch (e) {
		// no-op
	}
	return s;
}
function _save(s) {
	const _s = JSON.stringify(s);
	localStorage.setItem(key, _s);
	return _s;
}

module.exports = {
	isRtl: 'rtl' === document.querySelector('html').getAttribute('dir')
	, load: _load
	, save: _save
};
