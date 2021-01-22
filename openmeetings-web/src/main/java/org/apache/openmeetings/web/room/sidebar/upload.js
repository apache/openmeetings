/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Upload = (function() {
	let progress, progressBar, curUid;

	function _onWsMessage(jqEvent, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = JSON.parse(msg);
			if (m && 'room-upload' === m.type && curUid === m.uuid) {
				switch (m.status) {
					case 'ERROR':
						OmUtil.error(m.message);
						break;
					case 'PROGRESS': {
							const progressP = m.progress + '%';
							progressBar.css('width', progressP).attr('aria-valuenow', m.progress).text(progressP);
							if (m.progress === 100) {
								$('#room-upload-form').parents('.modal').modal('hide');
								_cleanup();
							}
						}
						break;
					default:
						//no-op
				}
			}
		} catch (err) {
			OmUtil.error(err);
		}
	}
	function _cleanup() {
		Wicket.Event.unsubscribe('/websocket/message', _onWsMessage);
	}
	function _bindUpload() {
		const form = $('#room-upload-form')
				, fi = form.find('.fileinput')
		progress = form.parent().find('.progress');
		progressBar = form.parent().find('.progress-bar');
		_cleanup();
		form.show();
		progress.addClass('d-none');
		let uploadBtn = $('#room-upload-btn');
		if (uploadBtn.length === 0) {
			uploadBtn = $('<button id="room-upload-btn" class="btn btn-outline-primary"></button>')
				.text(form.data('upload-lbl'));
			form.parents('.modal-content').find('.modal-footer').prepend(uploadBtn);
			uploadBtn.click(function() {
				const cform = $('#room-upload-form');
				$.ajax({
					url: cform.attr('action')
					, type: 'POST'
					, data: new FormData($('#room-upload-form')[0])
					, processData: false
					, contentType: false
				}).done(function(data) {
					curUid = data.uuid;
					uploadBtn.attr('disabled', 'disabled');
					cform.hide();
					progress.removeClass('d-none');
					Wicket.Event.subscribe('/websocket/message', _onWsMessage);
				}).fail(function(e) {
					OmUtil.error(e.message);
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

	return {
		bindUpload: _bindUpload
	};
})();
