/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var NetTest = (function() {
	const self = {};
	let output, lbls, net, testName, testLabel;

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
				net[testName].start();
				if (testName === 'latency') {
					net[testName].trigger('start');
				}
			});

		net = new Network({
			endpoint: './services/networktest/'
		});
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
			.on('end', function(avg, all) {
				all = all.map(function(latency) {
					return _value(latency, lbls['ms']);
				});
				all = '[ ' + all.join(' , ') + ' ]';
				_log('Instant latencies: ' + all);
				_log('Average latency: ' + _value(avg, lbls['ms']));
				_stop();
			});
	}
	function _start(size) {
		_log(_delimiter(
			'Starting ' + testLabel + ' measures'
			+ (testName != 'latency' ? (' with ' + _value(size / 1024 / 1024, lbls['mb']) + ' of data') : '')
			+ '...'
		), true);
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
			'The minimum delay of ' + _value(8, lbls['sec']) + ' has not been reached'
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
		return $('<span class="delim"></span>').text(text);
	}
	function _log(text, newSection) {
		output.append('<br/>');
		if (newSection) {
			output.append('<br/>');
		}
		output.append($('<span class="module"></span>&nbsp;').text(testLabel)).append(text);
	}
	function _value(value, unit) {
		if (value != null) {
			return '<span class="value">' + value.toFixed(3) + ' ' + unit + '</span>';
		} else {
			return '<span class="value">null</span>';
		}
	}

	self.init = _init;
	return self;
})();
