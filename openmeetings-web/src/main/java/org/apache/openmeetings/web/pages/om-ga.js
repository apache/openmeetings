/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function initGA(code) {
	window.dataLayer = window.dataLayer || [];
	function gtag() {
		dataLayer.push(arguments);
	}
	gtag('js', new Date());

	gtag('config', code);
}
function init() {
	gtag('event', 'pageview');
}
function initHash() {
	gtag('event', 'pageview', window.location.hash);

	$(window).bind('hashchange', function() {
		gtag('event', 'pageview', window.location.hash);
	});
}
