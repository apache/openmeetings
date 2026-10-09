/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const AjaxAdapter = $.fn.select2.amd.require('select2/data/ajax');
class OmWithDisabledAdapter extends AjaxAdapter {
	constructor($element, options) {
		super($element, options);
	}

	item($element) {
		const res = super.item($element);
		if ($element.data('disabled') === 'data-disabled') {
			res.disabled = true;
		}
		return res;
	}
}

function templateSelectionWithDisabled(data, container) {
	if (data.disabled === true) {
		container.addClass('select2-results__option--disabled');
		container.find('button').css('pointer-events', 'none');
	}
	return data.text;
}

function templateResultWithDisabled(data, container) {
	if (data.disabled === true && data.loading !== true) {
		container.classList.remove('select2-results__option--selectable');
		container.classList.add('select2-results__option--disabled');
		container.style.pointerEvents = 'none';
	}
	return data.text;
}
