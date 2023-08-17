/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function __addSipText(v) {
	const txt = $('.sip-number');
	txt.val(txt.val() + v);
}
function __eraseSipText() {
	const txt = $('.sip-number')
		, t = txt.val();
	if (!!t) {
		txt.val(t.substring(0, t.length - 1));
	}
}
function _sipGetKey(evt) {
	let k = -1;
	if (evt.keyCode > 47 && evt.keyCode < 58) {
		k = evt.keyCode - 48;
	}
	if (evt.keyCode > 95 && evt.keyCode < 106) {
		k = evt.keyCode - 96;
	}
	return k;
}

module.exports = {
	init: function() {
		$('.sip .button-row button').off().click(function() {
			__addSipText($(this).data('value'));
		});
		$('#sip-dialer-btn-erase').off().click(__eraseSipText);
	}
	, keyDown: function(evt) {
		const k = _sipGetKey(evt);
		if (k > 0) {
			$('#sip-dialer-btn-' + k).addClass('bg-warning');
		}
	}
	, keyUp: function(evt) {
		if (evt.key === 'Backspace') {
			__eraseSipText();
		} else {
			const k = _sipGetKey(evt);
			if (k > 0) {
				$('#sip-dialer-btn-' + k).removeClass('bg-warning');
				__addSipText(k);
			}
		}
	}
};