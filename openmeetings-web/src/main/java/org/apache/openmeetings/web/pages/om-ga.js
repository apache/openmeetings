/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function initGA(code, hash) {
	window.dataLayer = window.dataLayer || [];
	function gtag() {
		dataLayer.push(arguments);
	}
	function newPage() {
		const page = location.pathname + (location.search.indexOf('app=') > -1 ? location.search : location.hash);
		gtag('config', code, {'page_path': page});
	}
	gtag('js', new Date());

	gtag('config', code, {
		anonymize_ip: true
		, send_page_view: false
	});
	newPage();
	if (hash) {
		$(window).off('hashchange').on('hashchange', () => {
			if (location.hash.indexOf('/') > -1) {
				newPage();
			}
		});
	}
}
