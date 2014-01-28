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
package org.apache.openmeetings.web.user;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;
import com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.IWysiwygToolbar;

/**
 * Provides a custom implementation for com.googlecode.wicket.jquery.ui.plugins.wysiwyg.toolbar.IWysiwygToolbar suitable
 * for chat}
 */
public class ChatToolbar extends Panel implements IWysiwygToolbar {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer toolbar;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            the markup-id
	 */
	public ChatToolbar(String id) {
		this(id, null);
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 *            the markup-id
	 * @param model
	 *            the {@link org.apache.wicket.model.IModel}
	 */
	public ChatToolbar(String id, IModel<String> model) {
		super(id, model);

		toolbar = new WebMarkupContainer("toolbar");
		toolbar.setMarkupId("bToolbar");
		add(toolbar);

	}

	public void attachToEditor(Component editor) {
		toolbar.add(AttributeModifier.replace("data-target", JQueryWidget.getSelector(editor)));
	}
}
