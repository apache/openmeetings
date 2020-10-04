/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
window.dataLayer = window.dataLayer || [];
function gtag() {
	dataLayer.push(arguments);
}
function gtagEvent() {
	gtag('event', 'pageview', {page: location.pathname, hash: location.hash});
}
function initGA(code) {
	gtag('js', new Date());

	gtag('config', code);
}
function initHash() {
	gtagEvent();

	$(window).off('hashchange').on('hashchange', gtagEvent);
}
