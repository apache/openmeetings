/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const OmFileTree = (function() {
	return {
		dragHelper: function() {
			let s = $(this);
			if (s.hasClass('active')) {
				s = $('.file.active.ui-draggable.ui-draggable-handle, .recording.active.ui-draggable.ui-draggable-handle');
			}
			const c = $('<div/>').attr('id', 'draggingContainer').addClass('drag-container').width(80).height(36)
				, h = $('<div class="border"/>').append(s.clone()).width(s.width());
			return c.append(h);
		}
		, treeRevert: function(dropped) {
			$('.file-tree .trees')[0].scrollTop = $(this).parent()[0].offsetTop - 32;
			return !dropped || (!!dropped.context && $(dropped.context).hasClass('wb', 'room'));
		}
		, confirmTrash: function(drop, ui, callback) {
			$('.trash-toolbar').confirmation({
				trigger: 'manual'
				, onConfirm: () => {
					drop.append(ui.draggable);
					callback();
				}
			});
			$('.trash-toolbar').confirmation('show');
		}
	};
})();
