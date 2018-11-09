/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var NetTest = (function() {
	const self = {}, PINGS = 10, LIMIT = 2000, URL = './services/networktest/';
	let output, lbls, net, tests, testName, testLabel, testNext, bulk = true;

	// Based on
	// https://github.com/nesk/network.js/blob/master/example/main.js
	function _init(_lbls) {
		lbls = _lbls;
		output = $('.nettest output');
		$('.nettest .test-container').each(function() {
			const cont = $(this)
				, d = cont.find('.test');
			d.dialog({
				closeOnEscape: false
				, classes: {
					'ui-dialog': 'ui-corner-all nettest-dialog'
					, 'ui-dialog-titlebar': 'ui-corner-all no-close'
				}
				, autoOpen: true
				, resizable: false
				, draggable: false
				, modal: false
				, appendTo: '#' + cont.attr('id')
				, position: {my: "left top", at: "left top", of: cont}
				, minWidth: 190
				, width: 190
				, maxWidth: 190
				, height: 100
			});
			d.parent().find('.ui-dialog-titlebar .ui-dialog-title')
				.prepend($('<span class="ui-icon"></span>').addClass(d.data('icon')));
		});
		$('.nettest button')
			.button()
			.click(function() {
				const btn = $(this);
				btn.removeClass('complete').removeClass('not-started').addClass('started');
				testLabel = btn.data('lbl');
				testName = btn.data('measure');
				tests[testName].start();
				btn.find('.value').html('');
			});

		net = new Network();
		_initTests()
		// progress can be added
		net.upload
			.on('start', _start)
			.on('restart', _restart)
			.on('end', _end);
		net.download
			.on('start', _start)
			.on('restart', _restart)
			.on('end', _end);
		net.latency.on('start', _start);
		$('.nettest button[data-start="true"]').click()
	}
	function _initTests() {
		tests = {
			ping: {
				start: function() {
					const t = net.latency;
					t.settings({
						endpoint: URL + '?type=ping'
						, measures: PINGS
						, attempts: 1
					});
					t.off('end').on('end', _pingEnd);
					t.start();
					t.trigger('start');
				}
			}
			, jitter: {
				start: function() {
					const t = net.latency;
					t.settings({
						endpoint: URL + '?type=jitter'
						, measures: 5
						, attempts: 3
					});
					t.off('end').on('end', _jitterEnd);
					t.start();
					t.trigger('start');
				}
			}
			, upload: {
				start: function() {
					const t = net.upload;
					t.settings({
						endpoint: URL + '?type=upload'
						, delay: LIMIT
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
						endpoint: URL + '?type=download'
						, delay: LIMIT
						, data: {
							size: 1 * 1024 * 1024
							, multiplier: 2
						}
					});
					t.start();
				}
			}
		};
	}
	function __start(size, newSection) {
		const msg = $('<span></span>').append(lbls['report.start']);
		let upDown = false;
		if (testName === 'upload') {
			msg.append(lbls['upl.bytes']);
			upDown = true;
		} else if (testName === 'download') {
			msg.append(lbls['dwn.bytes']);
			upDown = true;
		}
		if (upDown) {
			msg.append(_value(size / 1024 / 1024, lbls['mb']));
		}
		msg.append('...');
		_log(_delimiter(msg), newSection);
	}
	function _start(size) {
		__start(size, true);
	}
	function _jitterEnd(avg, _all) {
		const all = $('<span></span>').append('[');
		let delim = '';
		let max = 0, min = Number.MAX_VALUE;
		for (let i = 0; i < _all.length; ++i) {
			const v = _all[i];
			max = Math.max(max, v);
			min = Math.min(min, v);
			all.append(delim).append(_value(v, lbls['ms']));
			delim = ',';
		}
		all.append(']');
		_log(all);
		_log($('<span></span>').append(lbls['jitter.avg']).append(_value(avg, lbls['ms'])));
		_log($('<span></span>').append(lbls['jitter.min']).append(_value(min, lbls['ms'])));
		_log($('<span></span>').append(lbls['jitter.max']).append(_value(max, lbls['ms'])));
		_log($('<span></span>').append(lbls['jitter'])
				.append(':').append(_value(max - avg, lbls['ms']))
				.append(';').append(_value(min - avg, lbls['ms'])));
		_setResult('')
	}
	function _pingEnd(avg, _all) {
		_log($('<span></span>').append(lbls['ping.avg']).append(_value(avg, lbls['ms'])));
		_log($('<span></span>').append(lbls['ping.rcv']).append(_value(_all.length, '')));
		_log($('<span></span>').append(lbls['ping.lost']).append(_value(PINGS - _all.length, '')));
		_setResult(_value(avg, lbls['ms']))
	}
	function _restart(size) {
		__start(size, false);
	}
	function _mbps() {
		return lbls['mb'] + '/' + lbls['sec'];
	}
	function _btn() {
		return $('#test-' + testName + ' button.test-btn');
	}
	function _setResult(val) {
		const btn = _btn();
		btn.addClass('complete').removeClass('started');
		btn.find('.value').html(val);
		testNext = btn.data('next');
		if (!testNext) {
			bulk = false;
			return;
		}
		if (bulk) {
			testName = testNext;
			_btn().click();
		}
	}
	function _end(avg) {
		const val = _value(avg / 1024 / 1024, _mbps())
			, msg = $('<span></span>')
				.append(lbls[testName === 'upload' ? 'upl.speed' : 'dwn.speed'])
				.append(val);
		_log(msg);
		_setResult(val)
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
		output.find('span').last()[0].scrollIntoView(false);
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
