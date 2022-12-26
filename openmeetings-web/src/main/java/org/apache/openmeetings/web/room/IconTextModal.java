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
package org.apache.openmeetings.web.room;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.ColorBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconTypeBuilder;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconTypeBuilder.FontAwesome6Solid;

public class IconTextModal extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final Icon icon = new Icon("icon", (IconType) null);
	private final Label label = new Label("label", Model.of(""));

	public IconTextModal(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(icon, label);
	}

	public IconTextModal withIcon(IconType icon) {
		this.icon.setType(icon);
		return this;
	}

	public IconTextModal withLabel(IModel<String> model) {
		label.setDefaultModel(model);
		return this;
	}

	public IconTextModal withLabel(String label) {
		this.label.setDefaultModelObject(label);
		return this;
	}

	public Label getLabel() {
		return label;
	}

	public IconTextModal withErrorIcon() {
		return withErrorIcon(ColorBehavior.Color.Danger);
	}

	public IconTextModal withErrorIcon(ColorBehavior.Color color) {
		add(new ColorBehavior(color));
		return withIcon(FontAwesome6IconTypeBuilder.on(FontAwesome6Solid.triangle_exclamation)
			.size(FontAwesome6IconTypeBuilder.Size.three)
			.build());
	}
}
