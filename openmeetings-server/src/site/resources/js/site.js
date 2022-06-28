/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.

*/
$(document).ready(function() {
	// "New" markers
	["Call For Logo", "REST API Swagger"].forEach(
		topic => $('ul.nav li a[title="' + topic + '"').append('&nbsp;&nbsp;<span class="badge badge-success">New</span>')
	);
	// "ApacheCon" banner on the right
	$('.header .bannerRight').parent()
		.append($('<div class="float-right">')
			.append($('<a class="apachecon-banner bannerRight acevent" data-format="wide" data-width="250"></a>')));
	const banners = $('.acevent>img');
	if (banners.length > 0 && banners.length !== 2) {
		banners.remove();
		render_snippet();
	}
})
