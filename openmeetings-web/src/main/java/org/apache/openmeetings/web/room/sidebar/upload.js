/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function bindUpload(markupId, hiddenId) {
	const fi = $('#' + markupId + ' .fileinput');
	if (!fi.eventAdded) {
		$('#' + markupId + ' .fileinput').on('change.bs.fileinput', function(event) {
			event.stopPropagation();
			const th = $(this),
			fInput = th.find('input[type=file]'),
			fn = th.find('.fileinput-filename');
			if (fInput[0].files !== undefined && fInput[0].files.length > 1) {
				fn.text($.map(fInput[0].files, function(val) { return val.name; }).join(', '));
			}
			fInput.attr('title', fn.text());
			const hi = $('#' + hiddenId);
			hi.val(fn.text());
			hi.trigger('change');
			return false;
		});
		fi.eventAdded = true;
	}
}
