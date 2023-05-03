/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const NetTest = (function() {
	const self = {}, PINGS = 10, LIMIT = 2000, URL = './services/networktest/'
		, DELAY = 3000, KB = 1024, MB = KB * KB;
	let output, lbls, tests, testName, testLabel, testNext, bulk = true;

	const average = (array) => array.reduce((a, b) => a + b) / array.length;

	function _init(_lbls) {
		lbls = _lbls;
		output = $('.nettest output');
		$('.nettest button')
			.click(function() {
				const btn = $(this);
				btn.removeClass('complete').removeClass('not-started').addClass('started');
				testLabel = btn.data('lbl');
				testName = btn.data('measure');
				tests[testName].start();
				btn.parent().find('.value').html('');
			});

		_initTests();
		$('.nettest button[data-start="true"]').click()
	}
	function __cleanOptions(options) {
		if (isNaN(options.attempts)) {
			options.attempts = 0;
		}
		if (isNaN(options.step)) {
			options.step = options.measures;
			options.astep = options.attempts;
		}
		if (!Array.isArray(options.results)) {
			options.results = [];
			options.lresults = [];
		}
	}
	function __stepsDone(options) {
		if (--options.astep > 0) {
			options.step = options.measures;
			options.results.push(options.lresults);
			options.lresults = [];
			__repeat(options);
		} else {
			if (options.attempts > 0) {
				options.results.push(options.lresults);
			} else {
				options.results = options.lresults;
			}
			options.onend(options.results);
		}
	}
	function __repeat(options) {
		__cleanOptions(options);
		if (options.step < 0) {
			return; //might happen in case of error
		}
		if (options.step === 0) {
			__stepsDone(options);
		} else {
			options.action(options.params, res => {
				if (res.ok) {
					let val = res.time;
					if (options.params.size) {
						_logSize(options.params.curSize);
						val = 1000 * options.params.curSize / res.time;
					}
					options.lresults.push(val);
				}
				if (options.maxTime && res.time > options.maxTime) {
					options.step = 0;
				} else {
					options.step--;
				}
				__repeat(options);
			});
		}
	}
	function __netTest(params, callback) {
		let tail = '';
		if (params.size) {
			params.curSize = params.size + (params.curSize || 0);
			tail = '&size=' + params.curSize;
		}
		setTimeout(() => {
			const fopts = {cache: 'no-cache'};
			if (params.mode === 'upload') {
				fopts.method = 'POST'
				const arr = Uint8Array.from({length: params.curSize}, () => Math.floor(Math.random() * 255));
				fopts.body = new Blob([arr.buffer], {type: 'application/octet-stream'});
			}
			let t = Date.now();
			fetch(URL + params.url + tail, fopts)
				.then(resp => resp.ok ? resp.arrayBuffer() : Promise.resolve(null))
				.then(buf => {
					callback({ok: !!buf, time: Date.now() - t});
				})
				.catch(err => {
					OmUtil.log(err);
					callback({ok: false, time: -1});
				});
		}, params.delay || 0);
	}
	function _initTests() {
		tests = {
			ping: {
				start: () => {
					_start();
					__repeat({
						action: __netTest
						, params: {
							url: '?type=ping'
						}
						, measures: PINGS
						, onend: _pingEnd
					});
				}
			}
			, jitter: {
				start: () => {
					_start();
					__repeat({
						action: __netTest
						, params: {
							url: '?type=jitter'
						}
						, measures: 5
						, attempts: 3
						, onend: _jitterEnd
					});
				}
			}
			, upload: {
				start: function() {
					_start();
					__repeat({
						action: __netTest
						, params: {
							url: '?type=upload'
							, mode: 'upload'
							, size: 512 * KB
							, delay: DELAY
						}
						, maxTime: LIMIT
						, measures: 5
						, onend: _end
					});
				}
			}
			, download: {
				start: () => {
					_start();
					__repeat({
						action: __netTest
						, params: {
							url: '?type=download'
							, size: 1 * MB
							, delay: DELAY
						}
						, maxTime: LIMIT
						, measures: 5
						, onend: _end
					});
				}
			}
		};
	}
	function _start() {
		const msg = $('<span></span>').append(lbls['report.start']);
		_log(_delimiter(msg), true);
	}
	function _logSize(size) {
		const msg = $('<span></span>');
		if (testName === 'upload') {
			msg.append(lbls['upl.bytes']);
		} else if (testName === 'download') {
			msg.append(lbls['dwn.bytes']);
		}
		msg.append(_value(size / MB, lbls['mb']));
		msg.append('...');
		_log(_delimiter(msg));
	}
	function _jitterEnd(times) {
		const all = $('<span></span>').append('[')
			, atimes = times.map(arr => average(arr))
			, avg = average(atimes);
		let delim = '';
		let max = 0, min = Number.MAX_VALUE;
		atimes.forEach(v => {
			max = Math.max(max, v);
			min = Math.min(min, v);
			all.append(delim).append(_value(v, lbls['ms']));
			delim = ',';
		});
		all.append(']');
		_log(all);
		_log($('<span></span>').append(lbls['jitter.avg']).append(_value(avg, lbls['ms'])));
		_log($('<span></span>').append(lbls['jitter.min']).append(_value(min, lbls['ms'])));
		_log($('<span></span>').append(lbls['jitter.max']).append(_value(max, lbls['ms'])));
		_log($('<span></span>').append(lbls['jitter'])
			.append(':').append(_value(max - avg, lbls['ms']))
			.append(';').append(_value(min - avg, lbls['ms'])));
		_setResult($('<div></div>')
			.append($('<div class="line"></div>').append(lbls['jitter.avgAbbr'] + '&nbsp;').append(_value(avg, lbls['ms'])))
			.append($('<div class="line"></div>').append(lbls['jitter'] + '&nbsp;').append(_value(max - avg, lbls['ms']))));
	}
	function _pingEnd(times) {
		const avg = average(times);
		_log($('<span></span>').append(lbls['ping.avg']).append(_value(avg, lbls['ms'])));
		_log($('<span></span>').append(lbls['ping.rcv']).append(_value(times.length, '')));
		_log($('<span></span>').append(lbls['ping.lost']).append(_value(PINGS - times.length, '')));
		_setResult(_value(avg, lbls['ms']));
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
		btn.parent().parent().find('.value').html(val);
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
	function _end(speeds) {
		const avg = average(speeds)
			, val = _value(avg / MB, _mbps())
			, msg = $('<span></span>')
				.append(lbls[testName === 'upload' ? 'upl.speed' : 'dwn.speed'])
				.append(val);
		_log(msg);
		_setResult(val.clone());
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
		return $('<span class="value"></span>').append(value == null ? 'null' : value.toFixed(1) + ' ' + unit);
	}

	self.init = _init;
	return self;
})();
