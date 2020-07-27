/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function bindUpload() {
	const form = $('#room-upload-form')
			, fi = form.find('.fileinput')
	let uploadBtn = $('#room-upload-btn');
	if (uploadBtn.length === 0) {
		uploadBtn = $('<button id="room-upload-btn" class="btn btn-outline-primary"></button>')
			.text(form.data('upload-lbl'));
		form.parents('.modal-content').find('.modal-footer').prepend(uploadBtn);
		uploadBtn.click(function() {
			$.ajax({
				url: form.attr('action')
				, type: 'POST'
				, data: new FormData($('#room-upload-form')[0])
				, processData: false
				, contentType: false
			}).done(function(data) {
				let i = 0;
			}).fail(function(e) {
				let i = 0;
			});
		});
	}
	uploadBtn.attr('disabled', 'disabled');
	if (!fi.eventAdded) {
		fi.on('change.bs.fileinput', function(event) {
			event.stopPropagation();
			const th = $(this)
				, fInput = th.find('input[type=file]')
				, fn = th.find('.fileinput-filename')
				, files = fInput[0].files
				, errors = form.find('.error');
			errors.html('');
			let	valid = files !== undefined && files.length > 0;
			if (valid) {
				const size = Array.from(files).map(f => f.size).reduce((a, b) => a + b, 0);
				valid = size < +form.data('max-size');
				if (!valid) {
					errors.html($('<div class="alert alert-danger" role="alert"></div>').text(form.data('max-upload-lbl') + ' ' + form.data('max-size-lbl')));
				}
			}
			if (valid) {
				fn.text(Array.from(files).map(f => f.name).join(', '));
				uploadBtn.removeAttr('disabled');
			} else {
				fn.text('');
				uploadBtn.attr('disabled', 'disabled');
				$('#room-upload-file').val('');
			}
			fInput.attr('title', fn.text());
			return false;
		});
		$('#room-upload-to-wb').click(function() {
			const clnBlock = $('#room-upload-clean-block');
			if ($(this).prop('checked')) {
				clnBlock.removeClass('d-none');
			} else {
				clnBlock.addClass('d-none');
			}
		});
		fi.eventAdded = true;
	}
}
