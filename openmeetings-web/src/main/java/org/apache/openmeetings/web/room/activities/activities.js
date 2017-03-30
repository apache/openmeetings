/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var Activities = function() {
	var closedHeight = "20px", openedHeight = "345px";

	function isInited(activities) {
		return !!activities.resizable("instance");
	}
	function isClosed(activities) {
		return activities.height() < 24;
	}
	function open() {
		var activities = $('#activities');
		if (!isInited(activities)) {
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
		if (isClosed(activities)) {
			$('.control.block .ui-icon', activities).removeClass('ui-icon-caret-1-n').addClass('ui-icon-caret-1-s');
			$('.control.block', activities).removeClass('ui-state-highlight');
			activities.animate({height: openedHeight}, 1000);
			activities.resizable("option", "disabled", false);
		}
	}
	function close() {
		var activities = $('#activities');
		if (!isClosed(activities)) {
			$('.control.block .ui-icon', activities).removeClass('ui-icon-caret-1-s').addClass('ui-icon-caret-1-n');
			activities.animate({height: closedHeight}, 1000);
			activities.resizable("option", "disabled", false);
		}
	}

	return {
		hightlight: function() {
			var activities = $('#activities');
			if (isClosed(activities)) {
				$('.control.block', activities).addClass('ui-state-highlight');
			}
		}
		, toggle: function() {
			if (isClosed($('#activities'))) {
				open();
			} else {
				close();
			}
		}
		, findUser: function(uid) {
			var m = '5px', t = 50, u = $('#user'+uid);
			if (u.length === 1) {
				u[0].scrollIntoView();
				u.addClass('ui-state-highlight');
				for(i = 0; i < 10; i++) {
					u.animate({marginTop: '-='+m}, t)
						.animate({marginTop: '+='+m}, t);
				}
				u.removeClass('ui-state-highlight', 1500);
			}
		}
	};
}();
