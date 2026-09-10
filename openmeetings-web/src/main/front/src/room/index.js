/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */

// Let's re-style jquery-ui dialogs and buttons
Object.assign($.ui.dialog.prototype.options.classes, {
	'ui-dialog': 'modal-content'
	, 'ui-dialog-titlebar': 'modal-header'
	, 'ui-dialog-title': 'modal-title'
	, 'ui-dialog-titlebar-close': 'close'
	, 'ui-dialog-content': 'modal-body'
	, 'ui-dialog-buttonpane': 'modal-footer'
});
Object.assign($.ui.button.prototype.options.classes, {
	'ui-button': 'btn btn-outline-secondary'
});

Object.assign(window, {
	VideoManager: require('./video-manager')
	, Sharer: require('./sharer')
	, Room: require('./room')
	, Activities: require('./activities')
	, UserSettings: require('./user-settings')
});
