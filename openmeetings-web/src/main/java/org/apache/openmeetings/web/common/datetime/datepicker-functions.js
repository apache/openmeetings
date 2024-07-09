/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function omDateTimeInit(markupId, hiddenId) {
	const el = document.getElementById(markupId)
		, hidden = document.getElementById(hiddenId);
	if (!el) {
		return;
	}
	const picker = el.datetimepicker;
	let val = new tempusDominus.DateTime();
	try {
		const strDate = hidden.value;
		if (strDate) {
			val = new tempusDominus.DateTime(strDate);
		}
	} catch (e) {
		// no-op
	}
	picker.dates.setValue(val);

	picker.subscribe(tempusDominus.Namespace.events.change, (e) => {
		const pad = (n) => ('' + n).padStart(2, '0');
		const formatDate = (d) => d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate());
		const formatDateTime = (d) => formatDate(d) + 'T' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());// + 'Z';

		hidden.value = picker.optionsStore.options.display.components.clock ? formatDateTime(e.date) : formatDate(e.date);
		hidden.dispatchEvent(new Event('change'));
	});
}
