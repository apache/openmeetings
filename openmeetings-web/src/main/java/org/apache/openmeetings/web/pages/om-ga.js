/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function initGA(code, hash) {
	const dataLayer = globalThis.dataLayer || [];
	const  gtag = () => {
		dataLayer.push(arguments);
	}
	const newPage = () => {
		const page = location.pathname + (location.search.includes('app=') ? location.search : location.hash);
		gtag('config', code, {'page_path': page});
	}
	gtag('js', new Date());

	gtag('config', code, {
		anonymize_ip: true
		, send_page_view: false
	});
	newPage();
	if (hash) {
		$(globalThis).off('hashchange').on('hashchange', () => {
			if (location.hash.includes('/')) {
				newPage();
			}
		});
	}
}
