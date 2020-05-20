/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Settings = (function() {
	const key = 'openmeetings';
	function _load() {
		let s = {};
		try {
			s = JSON.parse(localStorage.getItem(key)) || s;
		} catch (e) {
			// no-op
		}
		return s;
	}
	function _save(s) {
		const _s = JSON.stringify(s);
		localStorage.setItem(key, _s);
		return _s;
	}

	return {
		isRtl: 'rtl' === $('html').attr('dir')
		, load: _load
		, save: _save
	};
})();
var OmUtil = (function() {
	let options, errs, alertId = 0;
	const self = {};

	function _init(_options) {
		options = _options;
	}
	function _confirmDlg(_id, okHandler) {
		const confirm = $('#' + _id);
		confirm.dialog({
			modal: true
			, buttons: [{
				text: confirm.data('btn-ok'),
				click: function() {
					okHandler();
					$(this).dialog('close');
				}
			}, {
				text: confirm.data('btn-cancel'),
				click: function() {
					$(this).dialog('close');
				}
			}]
		});
		return confirm;
	}
	function _tmpl(tmplId, newId) {
		return $(tmplId).clone().attr('id', newId || '');
	}
	function __alert(level, msg, autohideAfter) {
		const holder = $('#alert-holder');
		const curId = 'om-alert' + alertId++;
		holder.append($(`<div id="${curId}" class="alert alert-${level} alert-dismissible fade show m-0" role="alert">${msg}
				<button type="button" class="close" data-dismiss="alert" aria-label="${holder.data('lbl-close')}">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>`));
		if (autohideAfter > 0) {
			setTimeout(() => { $(`#${curId}`).alert('close');}, autohideAfter);
		}
	}
	function _error(msg) {
		if (typeof(msg) === 'object') {
			msg = msg.name + ': ' + msg.message;
		}
		__alert('danger', msg, 20000);
		return console.error(msg);
	}
	function _debugEnabled() {
		return !!options && !!options.debug;
	}
	function _info() {
		if (_debugEnabled()) {
			console.info.apply(this, arguments);
		}
	}
	function _log() {
		if (_debugEnabled()) {
			console.log.apply(this, arguments);
		}
	}

	self.init = _init;
	self.confirmDlg = _confirmDlg;
	self.tmpl = _tmpl;
	self.debugEnabled = _debugEnabled;
	self.enableDebug = function() {
		if (!!options) {
			options.debug = true;
		}
	};
	self.sendMessage = function(_m, _base) {
		const base = _base || {}
			, m = _m || {}
			, msg = JSON.stringify($.extend({}, base, m));
		Wicket.WebSocket.send(msg);
	};
	self.alert = __alert;
	self.error = _error;
	self.info = _info;
	self.log = _log;
	self.wbAction = function(_m) {
		self.sendMessage(_m, {area: 'room', type: 'wb'});
	};
	self.roomAction = function(_m) {
		self.sendMessage(_m, {area: 'room', type: 'room'});
	};
	self.getKeyCode = function(evt) {
		let code = evt.code;
		if (typeof (code) === 'undefined') {
			const codeInfo = window.keyCodeToInfoTable[evt.keyCode];
			if (typeof(codeInfo) === 'object') {
				code = codeInfo.code;
			}
		}
		return code;
	};
	self.setCssVar = function(key, val) {
		($('body')[0]).style.setProperty(key, val);
	};
	self.ping = function(callback) {
		setTimeout(callback, 30000);
	} ;
	return self;
})();
Wicket.BrowserInfo.collectExtraInfo = function(info) {
	const l = window.location;
	info.codebase = l.origin + l.pathname;
	info.settings = Settings.load();
};
//Fix to move the close icon on top of the .ui-dialog-titlebar cause otherwise 
// touch-events are broken and you won't be able to close the dialog
function fixJQueryUIDialogTouch (dialog) {
    dialog.parent().find('.ui-dialog-titlebar-close').appendTo(dialog.parent());
}
function showBusyIndicator() {
	$('#busy-indicator').show();
}
function hideBusyIndicator() {
	$('#busy-indicator').hide();
}
(function() {
	// https://github.com/inexorabletash/polyfill/blob/master/LICENSE.md
	// Licensed MIT
	// the below helper table is taken from here https://inexorabletash.github.io/polyfill/demos/keyboard.html
	var STANDARD = KeyboardEvent.DOM_KEY_LOCATION_STANDARD, LEFT = KeyboardEvent.DOM_KEY_LOCATION_LEFT, RIGHT = KeyboardEvent.DOM_KEY_LOCATION_RIGHT, NUMPAD = KeyboardEvent.DOM_KEY_LOCATION_NUMPAD;

	window.keyCodeToInfoTable = {
		// 0x01 - VK_LBUTTON
		// 0x02 - VK_RBUTTON
		0x03: {
			code: 'Cancel'
		}, // [USB: 0x9b] char \x0018 ??? (Not in D3E)
		// 0x04 - VK_MBUTTON
		// 0x05 - VK_XBUTTON1
		// 0x06 - VK_XBUTTON2
		0x06: {
			code: 'Help'
		}, // [USB: 0x75] ???
		// 0x07 - undefined
		0x08: {
			code: 'Backspace'
		}, // [USB: 0x2a] Labelled Delete on Macintosh keyboards.
		0x09: {
			code: 'Tab'
		}, // [USB: 0x2b]
		// 0x0A-0x0B - reserved
		0X0C: {
			code: 'Clear'
		}, // [USB: 0x9c] NumPad Center (Not in D3E)
		0X0D: {
			code: 'Enter'
		}, // [USB: 0x28]
		// 0x0E-0x0F - undefined

		0x10: {
			code: 'Shift'
		},
		0x11: {
			code: 'Control'
		},
		0x12: {
			code: 'Alt'
		},
		0x13: {
			code: 'Pause'
		}, // [USB: 0x48]
		0x14: {
			code: 'CapsLock'
		}, // [USB: 0x39]
		0x15: {
			code: 'KanaMode'
		}, // [USB: 0x88]
		0x16: {
			code: 'Lang1'
		}, // [USB: 0x90]
		// 0x17: VK_JUNJA
		// 0x18: VK_FINAL
		0x19: {
			code: 'Lang2'
		}, // [USB: 0x91]
		// 0x1A - undefined
		0x1B: {
			code: 'Escape'
		}, // [USB: 0x29]
		0x1C: {
			code: 'Convert'
		}, // [USB: 0x8a]
		0x1D: {
			code: 'NonConvert'
		}, // [USB: 0x8b]
		0x1E: {
			code: 'Accept'
		}, // [USB: ????]
		0x1F: {
			code: 'ModeChange'
		}, // [USB: ????]

		0x20: {
			code: 'Space'
		}, // [USB: 0x2c]
		0x21: {
			code: 'PageUp'
		}, // [USB: 0x4b]
		0x22: {
			code: 'PageDown'
		}, // [USB: 0x4e]
		0x23: {
			code: 'End'
		}, // [USB: 0x4d]
		0x24: {
			code: 'Home'
		}, // [USB: 0x4a]
		0x25: {
			code: 'ArrowLeft'
		}, // [USB: 0x50]
		0x26: {
			code: 'ArrowUp'
		}, // [USB: 0x52]
		0x27: {
			code: 'ArrowRight'
		}, // [USB: 0x4f]
		0x28: {
			code: 'ArrowDown'
		}, // [USB: 0x51]
		0x29: {
			code: 'Select'
		}, // (Not in D3E)
		0x2A: {
			code: 'Print'
		}, // (Not in D3E)
		0x2B: {
			code: 'Execute'
		}, // [USB: 0x74] (Not in D3E)
		0x2C: {
			code: 'PrintScreen'
		}, // [USB: 0x46]
		0x2D: {
			code: 'Insert'
		}, // [USB: 0x49]
		0x2E: {
			code: 'Delete'
		}, // [USB: 0x4c]
		0x2F: {
			code: 'Help'
		}, // [USB: 0x75] ???

		0x30: {
			code: 'Digit0',
			keyCap: '0'
		}, // [USB: 0x27] 0)
		0x31: {
			code: 'Digit1',
			keyCap: '1'
		}, // [USB: 0x1e] 1!
		0x32: {
			code: 'Digit2',
			keyCap: '2'
		}, // [USB: 0x1f] 2@
		0x33: {
			code: 'Digit3',
			keyCap: '3'
		}, // [USB: 0x20] 3#
		0x34: {
			code: 'Digit4',
			keyCap: '4'
		}, // [USB: 0x21] 4$
		0x35: {
			code: 'Digit5',
			keyCap: '5'
		}, // [USB: 0x22] 5%
		0x36: {
			code: 'Digit6',
			keyCap: '6'
		}, // [USB: 0x23] 6^
		0x37: {
			code: 'Digit7',
			keyCap: '7'
		}, // [USB: 0x24] 7&
		0x38: {
			code: 'Digit8',
			keyCap: '8'
		}, // [USB: 0x25] 8*
		0x39: {
			code: 'Digit9',
			keyCap: '9'
		}, // [USB: 0x26] 9(
		// 0x3A-0x40 - undefined

		0x41: {
			code: 'KeyA',
			keyCap: 'a'
		}, // [USB: 0x04]
		0x42: {
			code: 'KeyB',
			keyCap: 'b'
		}, // [USB: 0x05]
		0x43: {
			code: 'KeyC',
			keyCap: 'c'
		}, // [USB: 0x06]
		0x44: {
			code: 'KeyD',
			keyCap: 'd'
		}, // [USB: 0x07]
		0x45: {
			code: 'KeyE',
			keyCap: 'e'
		}, // [USB: 0x08]
		0x46: {
			code: 'KeyF',
			keyCap: 'f'
		}, // [USB: 0x09]
		0x47: {
			code: 'KeyG',
			keyCap: 'g'
		}, // [USB: 0x0a]
		0x48: {
			code: 'KeyH',
			keyCap: 'h'
		}, // [USB: 0x0b]
		0x49: {
			code: 'KeyI',
			keyCap: 'i'
		}, // [USB: 0x0c]
		0x4A: {
			code: 'KeyJ',
			keyCap: 'j'
		}, // [USB: 0x0d]
		0x4B: {
			code: 'KeyK',
			keyCap: 'k'
		}, // [USB: 0x0e]
		0x4C: {
			code: 'KeyL',
			keyCap: 'l'
		}, // [USB: 0x0f]
		0x4D: {
			code: 'KeyM',
			keyCap: 'm'
		}, // [USB: 0x10]
		0x4E: {
			code: 'KeyN',
			keyCap: 'n'
		}, // [USB: 0x11]
		0x4F: {
			code: 'KeyO',
			keyCap: 'o'
		}, // [USB: 0x12]

		0x50: {
			code: 'KeyP',
			keyCap: 'p'
		}, // [USB: 0x13]
		0x51: {
			code: 'KeyQ',
			keyCap: 'q'
		}, // [USB: 0x14]
		0x52: {
			code: 'KeyR',
			keyCap: 'r'
		}, // [USB: 0x15]
		0x53: {
			code: 'KeyS',
			keyCap: 's'
		}, // [USB: 0x16]
		0x54: {
			code: 'KeyT',
			keyCap: 't'
		}, // [USB: 0x17]
		0x55: {
			code: 'KeyU',
			keyCap: 'u'
		}, // [USB: 0x18]
		0x56: {
			code: 'KeyV',
			keyCap: 'v'
		}, // [USB: 0x19]
		0x57: {
			code: 'KeyW',
			keyCap: 'w'
		}, // [USB: 0x1a]
		0x58: {
			code: 'KeyX',
			keyCap: 'x'
		}, // [USB: 0x1b]
		0x59: {
			code: 'KeyY',
			keyCap: 'y'
		}, // [USB: 0x1c]
		0x5A: {
			code: 'KeyZ',
			keyCap: 'z'
		}, // [USB: 0x1d]
		0x5B: {
			code: 'MetaLeft',
			location: LEFT
		}, // [USB: 0xe3]
		0x5C: {
			code: 'MetaRight',
			location: RIGHT
		}, // [USB: 0xe7]
		0x5D: {
			code: 'ContextMenu'
		}, // [USB: 0x65] Context Menu
		// 0x5E - reserved
		0x5F: {
			code: 'Standby'
		}, // [USB: 0x82] Sleep

		0x60: {
			code: 'Numpad0',
			keyCap: '0',
			location: NUMPAD
		}, // [USB: 0x62]
		0x61: {
			code: 'Numpad1',
			keyCap: '1',
			location: NUMPAD
		}, // [USB: 0x59]
		0x62: {
			code: 'Numpad2',
			keyCap: '2',
			location: NUMPAD
		}, // [USB: 0x5a]
		0x63: {
			code: 'Numpad3',
			keyCap: '3',
			location: NUMPAD
		}, // [USB: 0x5b]
		0x64: {
			code: 'Numpad4',
			keyCap: '4',
			location: NUMPAD
		}, // [USB: 0x5c]
		0x65: {
			code: 'Numpad5',
			keyCap: '5',
			location: NUMPAD
		}, // [USB: 0x5d]
		0x66: {
			code: 'Numpad6',
			keyCap: '6',
			location: NUMPAD
		}, // [USB: 0x5e]
		0x67: {
			code: 'Numpad7',
			keyCap: '7',
			location: NUMPAD
		}, // [USB: 0x5f]
		0x68: {
			code: 'Numpad8',
			keyCap: '8',
			location: NUMPAD
		}, // [USB: 0x60]
		0x69: {
			code: 'Numpad9',
			keyCap: '9',
			location: NUMPAD
		}, // [USB: 0x61]
		0x6A: {
			code: 'NumpadMultiply',
			keyCap: '*',
			location: NUMPAD
		}, // [USB: 0x55]
		0x6B: {
			code: 'NumpadAdd',
			keyCap: '+',
			location: NUMPAD
		}, // [USB: 0x57]
		0x6C: {
			code: 'NumpadComma',
			keyCap: ',',
			location: NUMPAD
		}, // [USB: 0x85]
		0x6D: {
			code: 'NumpadSubtract',
			keyCap: '-',
			location: NUMPAD
		}, // [USB: 0x56]
		0x6E: {
			code: 'NumpadDecimal',
			keyCap: '.',
			location: NUMPAD
		}, // [USB: 0x63]
		0x6F: {
			code: 'NumpadDivide',
			keyCap: '/',
			location: NUMPAD
		}, // [USB: 0x54]

		0x70: {
			code: 'F1'
		}, // [USB: 0x3a]
		0x71: {
			code: 'F2'
		}, // [USB: 0x3b]
		0x72: {
			code: 'F3'
		}, // [USB: 0x3c]
		0x73: {
			code: 'F4'
		}, // [USB: 0x3d]
		0x74: {
			code: 'F5'
		}, // [USB: 0x3e]
		0x75: {
			code: 'F6'
		}, // [USB: 0x3f]
		0x76: {
			code: 'F7'
		}, // [USB: 0x40]
		0x77: {
			code: 'F8'
		}, // [USB: 0x41]
		0x78: {
			code: 'F9'
		}, // [USB: 0x42]
		0x79: {
			code: 'F10'
		}, // [USB: 0x43]
		0x7A: {
			code: 'F11'
		}, // [USB: 0x44]
		0x7B: {
			code: 'F12'
		}, // [USB: 0x45]
		0x7C: {
			code: 'F13'
		}, // [USB: 0x68]
		0x7D: {
			code: 'F14'
		}, // [USB: 0x69]
		0x7E: {
			code: 'F15'
		}, // [USB: 0x6a]
		0x7F: {
			code: 'F16'
		}, // [USB: 0x6b]

		0x80: {
			code: 'F17'
		}, // [USB: 0x6c]
		0x81: {
			code: 'F18'
		}, // [USB: 0x6d]
		0x82: {
			code: 'F19'
		}, // [USB: 0x6e]
		0x83: {
			code: 'F20'
		}, // [USB: 0x6f]
		0x84: {
			code: 'F21'
		}, // [USB: 0x70]
		0x85: {
			code: 'F22'
		}, // [USB: 0x71]
		0x86: {
			code: 'F23'
		}, // [USB: 0x72]
		0x87: {
			code: 'F24'
		}, // [USB: 0x73]
		// 0x88-0x8F - unassigned

		0x90: {
			code: 'NumLock',
			location: NUMPAD
		}, // [USB: 0x53]
		0x91: {
			code: 'ScrollLock'
		}, // [USB: 0x47]
		// 0x92-0x96 - OEM specific
		// 0x97-0x9F - unassigned

		// NOTE: 0xA0-0xA5 usually mapped to 0x10-0x12 in browsers
		0xA0: {
			code: 'ShiftLeft',
			location: LEFT
		}, // [USB: 0xe1]
		0xA1: {
			code: 'ShiftRight',
			location: RIGHT
		}, // [USB: 0xe5]
		0xA2: {
			code: 'ControlLeft',
			location: LEFT
		}, // [USB: 0xe0]
		0xA3: {
			code: 'ControlRight',
			location: RIGHT
		}, // [USB: 0xe4]
		0xA4: {
			code: 'AltLeft',
			location: LEFT
		}, // [USB: 0xe2]
		0xA5: {
			code: 'AltRight',
			location: RIGHT
		}, // [USB: 0xe6]

		0xA6: {
			code: 'BrowserBack'
		}, // [USB: 0x0c/0x0224]
		0xA7: {
			code: 'BrowserForward'
		}, // [USB: 0x0c/0x0225]
		0xA8: {
			code: 'BrowserRefresh'
		}, // [USB: 0x0c/0x0227]
		0xA9: {
			code: 'BrowserStop'
		}, // [USB: 0x0c/0x0226]
		0xAA: {
			code: 'BrowserSearch'
		}, // [USB: 0x0c/0x0221]
		0xAB: {
			code: 'BrowserFavorites'
		}, // [USB: 0x0c/0x0228]
		0xAC: {
			code: 'BrowserHome'
		}, // [USB: 0x0c/0x0222]
		0xAD: {
			code: 'AudioVolumeMute'
		}, // [USB: 0x7f]
		0xAE: {
			code: 'AudioVolumeDown'
		}, // [USB: 0x81]
		0xAF: {
			code: 'AudioVolumeUp'
		}, // [USB: 0x80]

		0xB0: {
			code: 'MediaTrackNext'
		}, // [USB: 0x0c/0x00b5]
		0xB1: {
			code: 'MediaTrackPrevious'
		}, // [USB: 0x0c/0x00b6]
		0xB2: {
			code: 'MediaStop'
		}, // [USB: 0x0c/0x00b7]
		0xB3: {
			code: 'MediaPlayPause'
		}, // [USB: 0x0c/0x00cd]
		0xB4: {
			code: 'LaunchMail'
		}, // [USB: 0x0c/0x018a]
		0xB5: {
			code: 'MediaSelect'
		},
		0xB6: {
			code: 'LaunchApp1'
		},
		0xB7: {
			code: 'LaunchApp2'
		},
		// 0xB8-0xB9 - reserved
		0xBA: {
			code: 'Semicolon',
			keyCap: ';'
		}, // [USB: 0x33] ;: (US Standard 101)
		0xBB: {
			code: 'Equal',
			keyCap: '='
		}, // [USB: 0x2e] =+
		0xBC: {
			code: 'Comma',
			keyCap: ','
		}, // [USB: 0x36] ,<
		0xBD: {
			code: 'Minus',
			keyCap: '-'
		}, // [USB: 0x2d] -_
		0xBE: {
			code: 'Period',
			keyCap: '.'
		}, // [USB: 0x37] .>
		0xBF: {
			code: 'Slash',
			keyCap: '/'
		}, // [USB: 0x38] /? (US Standard 101)

		0xC0: {
			code: 'Backquote',
			keyCap: '`'
		}, // [USB: 0x35] `~ (US Standard 101)
		// 0xC1-0xCF - reserved

		// 0xD0-0xD7 - reserved
		// 0xD8-0xDA - unassigned
		0xDB: {
			code: 'BracketLeft',
			keyCap: '['
		}, // [USB: 0x2f] [{ (US Standard 101)
		0xDC: {
			code: 'Backslash',
			keyCap: '\\'
		}, // [USB: 0x31] \| (US Standard 101)
		0xDD: {
			code: 'BracketRight',
			keyCap: ']'
		}, // [USB: 0x30] ]} (US Standard 101)
		0xDE: {
			code: 'Quote',
			keyCap: '\''
		}, // [USB: 0x34] '" (US Standard 101)
		// 0xDF - miscellaneous/varies

		// 0xE0 - reserved
		// 0xE1 - OEM specific
		0xE2: {
			code: 'IntlBackslash',
			keyCap: '\\'
		}, // [USB: 0x64] \| (UK Standard 102)
		// 0xE3-0xE4 - OEM specific
		0xE5: {
			code: 'Process'
		}, // (Not in D3E)
		// 0xE6 - OEM specific
		// 0xE7 - VK_PACKET
		// 0xE8 - unassigned
		// 0xE9-0xEF - OEM specific

		// 0xF0-0xF5 - OEM specific
		0xF6: {
			code: 'Attn'
		}, // [USB: 0x9a] (Not in D3E)
		0xF7: {
			code: 'CrSel'
		}, // [USB: 0xa3] (Not in D3E)
		0xF8: {
			code: 'ExSel'
		}, // [USB: 0xa4] (Not in D3E)
		0xF9: {
			code: 'EraseEof'
		}, // (Not in D3E)
		0xFA: {
			code: 'Play'
		}, // (Not in D3E)
		0xFB: {
			code: 'ZoomToggle'
		}, // (Not in D3E)
		// 0xFC - VK_NONAME - reserved
		// 0xFD - VK_PA1
		0xFE: {
			code: 'Clear'
		}
	// [USB: 0x9c] (Not in D3E)
	};
})();
