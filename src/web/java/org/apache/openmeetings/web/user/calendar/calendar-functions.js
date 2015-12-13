/*
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
/**
 * @author Sebastien Briquet
 */
function getCalendarHeight() {
    return $(window).height() - $('#${markupId}').position().top - 20;
}

function setCalendarHeight() {
	var cal = $('#${markupId}');
	if (cal.length) {
    	cal.fullCalendar('option', 'height', getCalendarHeight());
	}
}

$(function() {
    $(window).load(function() { setCalendarHeight(); } );
});

function addCalButton(where, text, id) {
	var my_button = '<span class="fc-header-space"></span>' +
		'<span class="fc-button fc-button-nextYear fc-state-default fc-corner-right" unselectable="on">' +
		'<input type="text" id="' + id + '" value="' + text +'" /></span>';
	$("td.fc-header-" + where).append(my_button);
	 
	var dp = $("#"+id);
	dp.datepicker({
		showOn: "button",
		buttonImage: "images/calendar.gif",
		buttonImageOnly: true,
		changeMonth: true,
		changeYear: true,
		changeDay: true,
		onChangeMonthYear: function(year, month, inst) {
		     var date = new Date();
		     $('#${markupId}').fullCalendar('gotoDate', year, month-1, date.getDate());
		},
		onSelect: function(dateText, inst) {
		     var date = new Date(dateText);
		     $('#${markupId}').fullCalendar('gotoDate', date.getFullYear(), date.getMonth(), date.getDate());
		}
	});
	
	dp.datepicker("option", "dayNames", $('#${markupId}').fullCalendar("option","dayNames"));
	dp.datepicker("option", "dayNamesShort", $('#${markupId}').fullCalendar("option","dayNamesShort"));
	dp.datepicker("option", "dayNamesMin", $('#${markupId}').fullCalendar("option","dayNamesShort"));
	dp.datepicker("option", "monthNames", $('#${markupId}').fullCalendar("option","monthNames"));
	dp.datepicker("option", "monthNamesShort", $('#${markupId}').fullCalendar("option","monthNamesShort"));
	
	dp.hide();
}
 
function setDatepickerDate(id, date) {
	$("#"+id).datepicker('setDate', date);
}
