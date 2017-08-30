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
var NONE = 'none';
var WbArea = (function() {
	var container, area, role = NONE, self = {}, choose, btns, _inited = false;

	self.init = function() {
		container = $(".room.wb.area");
		area = container.find(".wb-area");
		btns = $('.pod-row .pod-container .pod a.choose-btn');
		btns.button()
			.click(function() {
				choose.dialog('open');
				let sel = choose.find('.users').html('');
				let users = $('.user.list .user.entry');
				for (let i = 0; i < users.length; ++i) {
					let u = $(users[i]);
					sel.append($('<option></option>').text(u.attr('title')).val(u.attr('id').substr(4)));
				}
				choose.find('.pod-name').val($(this).data('pod'));
				return false;
			});
		$('.pod-row .pod-container a.rec-btn.start').button({
			disabled: true
			, icon: "ui-icon-play"
		});
		$('.pod-row .pod-container a.rec-btn.stop').button({
			disabled: true
			, icon: "ui-icon-stop"
		});
		choose = $('#interview-choose-video');
		choose.dialog({
			modal: true
			, autoOpen: false
			, buttons: [
				{
					text: choose.data('btn-ok')
					, click: function() {
						toggleActivity('broadcastAV', choose.find('.users').val(), choose.find('.pod-name').val());
						$(this).dialog('close');
					}
				}
				, {
					text: choose.data('btn-cancel')
					, click: function() {
						$(this).dialog('close');
					}
				}
			]
		});
		_inited = true;
	};
	self.setRole = function(_role) {
		if (!_inited) return;
		role = _role;
		if (role !== NONE) {
			btns.show();
		} else {
			btns.hide();
		}
	}
	self.destroy = function() {
	};
	self.resize = function(posX, w, h) {
		if (!container || !_inited) return;
		var hh = h - 5;
		container.width(w).height(h).css('left', posX + "px");
		area.width(w).height(hh);
	}
	return self;
})();
