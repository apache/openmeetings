/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function omDateTimeInputHasChanged(e, hiddenId, dateOnly) {
	const fmt = dateOnly ? 'YYYY-MM-DD' : 'YYYY-MM-DDTHH:mm:ss';
	let val = e.target.value
		, date = e.date;
	if (!date) {
		const mmnt = $(e.target).datetimepicker('date');
		if (moment.isMoment(mmnt)) {
			date = moment(val, mmnt.creationData().format);
		}
	}
	if (date) {
		val = date.isValid() ? date.clone().locale('en').format(fmt) : date.creationData().input;
	}
	$('#' + hiddenId).val(val).trigger('change');
}
