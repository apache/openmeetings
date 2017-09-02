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
