/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function addOmAdminConfigHandlers() {
	$('.hotkey-input')
		.off()
		.on('keydown', function(evt) {
			const inp = $(evt.target);
			evt.preventDefault();
			let val = '';
			if (evt.ctrlKey) {
				val += 'Ctrl+';
			}
			if (evt.altKey) {
				val += 'Alt+';
			}
			if (evt.shiftKey) {
				val += 'Shift+';
			}
			const code = evt.code;
			if (typeof(code) === 'undefined') {
				return;
			}
			val += code;
			if (evt.keyCode !== 16 && evt.keyCode !== 17 && evt.keyCode !== 18 && inp.length == 1) {
				inp.val(val);
			}
		})
		.on('paste', function(e) {
			e.preventDefault();
			e.stopImmediatePropagation();
			return false;
		});
}
