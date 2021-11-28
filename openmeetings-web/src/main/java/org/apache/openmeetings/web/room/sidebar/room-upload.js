/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function roomUploadExtaBindFunc() {
	$('#room-upload-to-wb').off().click(function() {
		const clnBlock = $('#room-upload-clean-block');
		if ($(this).prop('checked')) {
			clnBlock.removeClass('d-none');
		} else {
			clnBlock.addClass('d-none');
		}
	});
}
function roomUploadOnComplete() {
	$('#omws-upload-form').parents('.modal').modal('hide');
}
