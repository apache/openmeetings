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
package org.apache.openmeetings.web.common;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

public abstract class ImagePanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	protected final WebMarkupContainer profile = new TransparentWebMarkupContainer("profile");

	public ImagePanel(String id) {
		super(id);
		add(profile.setOutputMarkupId(true));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		update();
	}

	protected abstract String getImageUrl();

	protected String getTitle() {
		return getString("5");
	}

	public void update() {
		profile.addOrReplace(new WebMarkupContainer("img").add(
				AttributeModifier.append("alt", getTitle())
				, AttributeModifier.append("title", getTitle())
				, AttributeModifier.append("src", getImageUrl())));
	}
}
