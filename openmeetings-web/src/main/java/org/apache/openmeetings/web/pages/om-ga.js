/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function initGA(code, hash) {
	window.dataLayer = window.dataLayer || [];
	const gtag = () => dataLayer.push(arguments)
		, newPage = () => gtag('config', code, {'page_path': location.pathname + location.hash});
	gtag('js', new Date());

	newPage();
	if (hash) {
		$(window).off('hashchange').on('hashchange', newPage);
	}
}
