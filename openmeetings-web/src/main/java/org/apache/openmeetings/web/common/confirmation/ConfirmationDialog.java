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
package org.apache.openmeetings.web.common.confirmation;

import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;

public abstract class ConfirmationDialog extends TextContentModal {
	private static final long serialVersionUID = 1L;
	private BootstrapAjaxLink<String> okButton;

	protected ConfirmationDialog(String id, IModel<String> title, IModel<String> model) {
		super(id, model);
		header(title);
	}

	private BootstrapAjaxLink<String> getOkButton() {
		if (okButton == null) {
			okButton = new BootstrapAjaxLink<>(BUTTON_MARKUP_ID, null, Buttons.Type.Outline_Danger, new ResourceModel("54")) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					close(target);
					onConfirm(target);
				}
			};
			okButton.setIconType(FontAwesome5IconType.exclamation_triangle_s);
		}
		return okButton;
	}

	public ConfirmationDialog withOkType(Buttons.Type type) {
		getOkButton().setType(type);
		return this;
	}

	public ConfirmationDialog withOkIcon(IconType icon) {
		getOkButton().setIconType(icon);
		return this;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new CssClassNameAppender("om-confirm-dialog"));
		addButton(getOkButton());
		addButton(OmModalCloseButton.of());
	}

	protected abstract void onConfirm(AjaxRequestTarget target);
}
