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
var closedHeight = "20px", openedHeight = "345px";
function activitiesClosed(activities) {
	return activities.height() < 24;
}
function openActivities() {
	var activities = $('#activitiesPanel');
	if (activitiesClosed(activities)) {
		$('.control.block .ui-icon', activities).removeClass('ui-icon-carat-1-n').addClass('ui-icon-carat-1-s');
		$('.control.block', activities).removeClass('ui-state-highlight');
		activities.animate({height: openedHeight}, 1000);
	}
}
function closeActivities() {
	var activities = $('#activitiesPanel');
	if (!activitiesClosed(activities)) {
		$('.control.block .ui-icon', activities).removeClass('ui-icon-carat-1-s').addClass('ui-icon-carat-1-n');
		activities.animate({height: closedHeight}, 1000);
	}
}
function toggleActivities() {
	if (activitiesClosed($('#activitiesPanel'))) {
		openActivities();
	} else {
		closeActivities();
	}
}
function hightlightActivities() {
	var activities = $('#activitiesPanel');
	if (activitiesClosed(activities)) {
		$('.control.block', activities).addClass('ui-state-highlight');
	}
}
