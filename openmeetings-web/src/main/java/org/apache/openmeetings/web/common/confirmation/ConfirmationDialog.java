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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.ModalCloseButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;

public abstract class ConfirmationDialog extends TextContentModal {
	private static final long serialVersionUID = 1L;

	public ConfirmationDialog(String id, IModel<String> title, IModel<String> model) {
		super(id, model);
		header(title);
		setBackdrop(Backdrop.STATIC);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addButton(new BootstrapAjaxLink<>("button", null, Buttons.Type.Outline_Primary, new ResourceModel("54")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				close(target);
				onConfirm(target);
			}
		}); //send
		addButton(new ModalCloseButton(new ResourceModel("lbl.cancel")).type(Buttons.Type.Outline_Secondary));
	}

	protected abstract void onConfirm(AjaxRequestTarget target);
}
