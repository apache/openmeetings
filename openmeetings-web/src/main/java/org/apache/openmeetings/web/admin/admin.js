/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function adminPanelInit() {
	if (typeof(extAdminPanelInit) === 'function') {
		extAdminPanelInit();
	}
	const f = $('#adminForm'), t = $('#adminTable')
		, h = window.innerHeight - 5;
	if (f.length === 1 && t.length === 1) {
		f.height(h - f.position().top);
		t.height(h - t.position().top);
	}
}
