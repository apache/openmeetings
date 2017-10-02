/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var Activities = function() {
	const closedHeight = "20px";
	let activities, openedHeight = "345px", inited = false;

	function isInited() {
		if (!inited) {
			activities = $('#activities');
			if (!!activities.resizable("instance")) {
				inited = true;
			}
		}
		return inited;
	}
	function isClosed() {
		return activities.height() < 24;
	}
	function open() {
		if (!isInited()) {
			activities.resizable({
				handles: "n, e"
				, disabled: isClosed(activities)
				, alsoResize: "#activities .area"
				, minHeight: 195
				, minWidth: 260
				, stop: function(event, ui) {
					activities.css({'top': '', 'right': ''});
					openedHeight = ui.size.height + "px";
				}
			});
		}
		if (isClosed()) {
			$('.control.block .ui-icon', activities).removeClass('ui-icon-caret-1-n').addClass('ui-icon-caret-1-s');
			$('.control.block', activities).removeClass('ui-state-highlight');
			activities.animate({height: openedHeight}, 1000);
			activities.resizable("option", "disabled", false);
		}
	}
	function close() {
		if (!isClosed()) {
			$('.control.block .ui-icon', activities).removeClass('ui-icon-caret-1-s').addClass('ui-icon-caret-1-n');
			activities.animate({height: closedHeight}, 1000);
			activities.resizable("option", "disabled", false);
		}
	}

	return {
		hightlight: function() {
			if (isClosed()) {
				$('.control.block', activities).addClass('ui-state-highlight');
			}
		}
		, toggle: function() {
			if (isClosed()) {
				open();
			} else {
				close();
			}
		}
		, findUser: function(uid) {
			const m = '5px', t = 50, u = $('#user' + uid);
			if (u.length === 1) {
				u[0].scrollIntoView();
				u.addClass('ui-state-highlight');
				for(let i = 0; i < 10; i++) {
					u.animate({marginTop: '-='+m}, t)
						.animate({marginTop: '+='+m}, t);
				}
				u.removeClass('ui-state-highlight', 1500);
			}
		}
	};
}();
