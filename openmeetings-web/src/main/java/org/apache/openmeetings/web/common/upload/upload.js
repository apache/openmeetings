/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Upload = (function() {
	let progress, progressBar, progressTitle, curUid;

	function _setProgress(prg) {
		const progressP = prg + '%';
		progressBar.css('width', progressP)
			.attr('aria-valuenow', prg)
			.text(progressP);
	}
	function _onWsMessage(_, msg) {
		try {
			if (msg instanceof Blob) {
				return; //ping
			}
			const m = JSON.parse(msg);
			if (m && 'omws-upload' === m.type && curUid === m.uuid) {
				switch (m.status) {
					case 'ERROR':
						OmUtil.error(m.message);
						break;
					case 'PROGRESS':
						_setProgress(m.progress)
						if (m.progress === 100) {
							$('#omws-upload-form').parents('.modal').modal('hide');
							_cleanup();
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
	function _bindUpload(extaBindFunc) {
		const form = $('#omws-upload-form')
				, fi = form.find('.fileinput')
		progress = form.parent().find('.progress-block');
		progressTitle = progress.find('.progress-title');
		progressBar = progress.find('.progress-bar');
		_cleanup();
		form.show();
		progress.addClass('d-none');
		let uploadBtn = $('#omws-upload-btn');
		if (uploadBtn.length === 0) {
			uploadBtn = $('<button id="omws-upload-btn" class="btn btn-outline-primary"></button>')
				.text(form.data('upload-lbl'));
			form.parents('.modal-content').find('.modal-footer').prepend(uploadBtn);
			uploadBtn.click(function() {
				const cform = $('#omws-upload-form');
				$.ajax({
					url: cform.attr('action')
					, type: 'POST'
					, data: new FormData($('#omws-upload-form')[0])
					, processData: false
					, contentType: false
					, xhr: function () {
						const xhr = new XMLHttpRequest();
						xhr.upload.addEventListener('progress', evt => _setProgress((100 * evt.loaded / evt.total).toFixed(2)), false);
						xhr.upload.addEventListener('loadstart', () => {
							uploadBtn.attr('disabled', 'disabled');
							cform.hide();
							progressTitle.text(progressTitle.data('upload-lbl'));
							progress.removeClass('d-none');
						}, false);
						return xhr;
					}
				}).done(function(data) {
					curUid = data.uuid;
					progressTitle.text(progressTitle.data('converting-lbl'));
					_setProgress(0);
					Wicket.Event.subscribe('/websocket/message', _onWsMessage);
				}).fail(function(e) {
					OmUtil.error(e.message);
				});
			});
		}
		uploadBtn.attr('disabled', 'disabled');
		fi.off().on('change.bs.fileinput', function(event) {
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
				$('#omws-upload-file').val('');
			}
			fInput.attr('title', fn.text());
			return false;
		});
		if ('function' === typeof(extaBindFunc)) {
			extaBindFunc();
		}
	}

	return {
		bindUpload: _bindUpload
	};
})();
