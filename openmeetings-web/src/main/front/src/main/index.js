/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
Wicket.BrowserInfo.collectExtraInfo = function(info) {
	const l = window.location;
	info.codebase = l.origin + l.pathname;
	info.settings = Settings.load();
};

function _updateResize() {
	const doc = document.documentElement;
	doc.style.setProperty('--app-height', `${window.innerHeight}px`)
}
$(window).on('resize', _updateResize);
//initial resize
_updateResize();

Object.assign(window, {
	Settings: require('./settings')
	, OmUtil: require('./omutils')
	//Fix to move the close icon on top of the .ui-dialog-titlebar cause otherwise
	// touch-events are broken and you won't be able to close the dialog
	, fixJQueryUIDialogTouch: function(dialog) {
		dialog.parent().find('.ui-dialog-titlebar-close').appendTo(dialog.parent());
	}
	, showBusyIndicator: function() {
		$('#busy-indicator').show();
	}
	, hideBusyIndicator: function() {
		$('#busy-indicator').hide();
	}
});
