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

function getUserId(uid) { return 'user' + uid; }

function addUser(u, uld) {
	var s = u.firstname + ' ' + u.lastname;
	var d = $('<div class="user ui-corner-all ui-widget-content"></div>').attr('id', getUserId(u.uid))
		.attr('data-id', u.id).text(s);
	if (u.current) {
		d.addClass('current');
	}
	uld.append(d);
	//TODO add activity
}

function removeUser(id) {
	$('#' + id).remove();//TODO replace with 'ends-with-id'
	//TODO add activity
}

function roomMessage(m) {
	if (m && m.type == "room") {
		//TODO add timestamp support
		switch (m.msg) {
			case "users":
				var uld = $('.user.list');
				var ulist = [];
				uld.children('[id^="user"]').each(function() {
					ulist.push(this.id); 
				});
				for (var i = 0; i < m.users.length; ++i) {
					var u = m.users[i];
					var id = getUserId(u.uid);
					if ($('#' + id).length == 0) {
						addUser(u, uld);
					} else {
						var idx = ulist.indexOf(id);
						if (idx > -1) {
							ulist.splice(idx, 1);
						}
					}
				}
				for (var i = 0; i < ulist.length; ++i) {
					removeUser(ulist[i]);
				}
				break;
			case "addUser":
				var id = getUserId(m.user.uid);
				if ($('#' + id).length == 0) {
					addUser(m.user, $('.user.list'));
				}
				break;
			case "removeUser":
				removeUser(getUserId(m.uid));
				break;
		}
	}
}

function setHeight() {
	var h = $(document).height() - $('#roomMenu').height();
	$(".room.sidebar.left").height(h);
	$(".room.wb.area").height(h);
}

$(document).ready(function() {
	setHeight();
	$(window).on('resize.openmeetings', function() {
		setHeight();
	});
});
