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
var closeBlock = "<span class='ui-icon ui-icon-close' role='presentation'></span>"
	, closedHeight = "20px", openedHeight = "345px";
function openActivities() {
	if ($('#activitiesPanel').height() < 24) {
		$('#activitiesPanel .control.block .ui-icon').removeClass('ui-icon-carat-1-n').addClass('ui-icon-carat-1-s');
		$('#activitiesPanel').animate({height: openedHeight}, 1000);
	}
}
function closeActivities() {
	var activities = $('#activitiesPanel');
	if ($('#activitiesPanel').height() > 24) {
		$('#activitiesPanel .control.block .ui-icon').removeClass('ui-icon-carat-1-s').addClass('ui-icon-carat-1-n');
		activities.animate({height: closedHeight}, 1000);
	}
}
function toggleActivities() {
	if ($('#activitiesPanel').height() < 24) {
		openActivities();
	} else {
		closeActivities();
	}
}
