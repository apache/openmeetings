/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function dragHelper() {
	var s = $(this);
	if (s.parents('a').hasClass('ui-state-active')) {
		s = $('.ui-state-active .ui-draggable.ui-draggable-handle');
	}
	var c = $('<div/>').attr('id', 'draggingContainer').width(80).height(36);
	var h = $('<div class="ui-corner-all ui-widget-header"/>').append(s.clone()).width(s.width());
	return c.append(h);
}
function treeRevert(dropped) {
	$('.file.tree .trees')[0].scrollTop = $(this).parent()[0].offsetTop - 32;
	return !dropped || (!!dropped && !!dropped.context && $(dropped.context).hasClass('wb', 'room'));
}
