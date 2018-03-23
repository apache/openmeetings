/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var NetTest = (function() {
	const self = {}, LIMIT = 2000;
	let output, lbls, net, tests, testName, testLabel;

	// Based on
	// https://github.com/nesk/network.js/blob/master/example/main.js
	function _init(_lbls) {
		lbls = _lbls;
		output = $('.nettest output');
		$('.nettest button')
			.button()
			.click(function() {
				const btn = $(this);
				testLabel = btn.data('lbl');
				testName = btn.data('measure');
				tests[testName].start();
			});

		net = new Network({
			endpoint: './services/networktest/'
		});
		tests = {
			latency: {
				start: function() {
					const t = net.latency;
					t.settings({
						measures: 5
						, attempts: 3
					});
					t.start();
					t.trigger('start');
				}
			}
			, upload: {
				start: function() {
					const t = net.upload;
					t.settings({
						delay: LIMIT
						, measures: 5
						, data: {
							size: 1 * 1024 * 1024
							, multiplier: 2
						}
					});
					t.start();
				}
			}
			, download: {
				start: function() {
					const t = net.download;
					t.settings({
						delay: LIMIT
						, measures: 5
						, data: {
							size: 1 * 1024 * 1024
							, multiplier: 2
						}
					});
					t.start();
				}
			}
		};
		net.upload
			.on('start', _start)
			.on('progress', _progress)
			.on('restart', _restart)
			.on('end', _end);
		net.download
			.on('start', _start)
			.on('progress', _progress)
			.on('restart', _restart)
			.on('end', _end);
		net.latency
			.on('start', _start)
			.on('end', function(avg, _all) {
				const all = $('<span></span>').append('[');
				let delim = '';
				for (let i = 0; i < _all.length; ++i) {
					all.append(delim).append(_value(_all[i], lbls['ms']));
					delim = ',';
				}
				all.append(']');
				_log(all);
				_log($('<span></span>').append(lbls['jitter.avg']).append(_value(avg, lbls['ms'])));
				_stop();
			});
	}
	function _start(size) {
		const msg = $('<span></span>').append(lbls['report.start']);
		if (testName === 'upload') {
			msg.append(lbls['upl.bytes']);
		} else if (testName === 'download') {
			msg.append(lbls['dwn.bytes']);
		}
		if (testName !== 'latency') {
			msg.append(_value(size / 1024 / 1024, lbls['mb']));
		}
		msg.append('...');
		_log(_delimiter(msg), true);
	}
	function _mbps() {
		return lbls['mb'] + '/' + lbls['sec'];
	}
	function _progress(avg, instant) {
		const output = 'Instant speed: ' + _value(instant / 1024 / 1024, _mbps())
			+ ' // Average speed: ' + _value(avg / 1024 / 1024, _mbps());
		_log(output);
	}
	function _restart(size) {
		_log(_delimiter(
			'The minimum delay of ' + _value(LIMIT / 1000, lbls['sec']) + ' has not been reached'
		));
		_log(_delimiter(
			'Restarting measures with '
			+ _value(size / 1024 / 1024, lbls['mb'])
			+ ' of data...'
		));
	}
	function _end(avg) {
		_log('Final average speed: ' + _value(avg / 1024 / 1024, _mbps()));
		_stop();
	}
	function _stop() {
		_log(_delimiter('Finished measures'));
	}
	function _delimiter(text) {
		return $('<span class="delim"></span>').html(text);
	}
	function _log(text, newSection) {
		output.append('<br/>');
		if (newSection) {
			output.append('<br/>');
		}
		output.append($('<span class="module"></span>').text(testLabel)).append(text);
	}
	function _value(value, unit) {
		if (value != null) {
			return $('<span class="value">' + value.toFixed(3) + ' ' + unit + '</span>');
		} else {
			return $('<span class="value">null</span>');
		}
	}

	self.init = _init;
	return self;
})();
