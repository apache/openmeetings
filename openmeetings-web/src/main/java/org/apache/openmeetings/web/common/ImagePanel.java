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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.PARAM_SRC;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class ImagePanel extends Panel {
	private static final long serialVersionUID = 1L;
	protected final WebMarkupContainer profile = new TransparentWebMarkupContainer("profile");

	protected ImagePanel(String id) {
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

	protected Component getImage() {
		return new WebMarkupContainer("img").add(
				AttributeModifier.append("alt", getTitle())
				, AttributeModifier.append(ATTR_TITLE, getTitle())
				, AttributeModifier.append(PARAM_SRC, getImageUrl()));
	}

	public void update() {
		profile.addOrReplace(getImage());
	}
}
