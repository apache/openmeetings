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
window.addEventListener("load", (event) => {
	// "New" markers
	["Call For Logo", "REST API Swagger"].forEach(
		topic => document.querySelector(`ul.nav li a[title="${topic}"`)
					.insertAdjacentHTML('beforeend', '&nbsp;&nbsp;<span class="badge badge-success">New</span>')
	);
	// "ApacheCon" banner on the right
	document.querySelector('.header--banner .header--banner-right.banner-right')
		.insertAdjacentHTML('beforeend',
			`<div class="header--banner-content">
				<a class="apachecon-banner bannerRight acevent" data-format="wide" data-width="250"></a>
			</div>`)
	const banners = document.getElementsByClassName('acevent');
	render_snippet();
});
