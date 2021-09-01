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

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class InvitationDialog extends Modal<Invitation> {
	private static final long serialVersionUID = 1L;
	private BootstrapAjaxButton generate;
	private BootstrapAjaxButton send;
	private final InvitationForm form;

	public InvitationDialog(String id, final InvitationForm form) {
		super(id, form.getModel());
		setMarkupId(id);
		this.form = form;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("213"));
		add(form);

		addButton(send = new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("218"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				InvitationDialog.this.onError(target);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				InvitationDialog.this.onClick(target, InvitationForm.Action.SEND);
			}
		});
		addButton(generate = new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("1526"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target) {
				InvitationDialog.this.onError(target);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				InvitationDialog.this.onClick(target, InvitationForm.Action.GENERATE);
			}
		});
		addButton(OmModalCloseButton.of());
		super.onInitialize();
	}

	public void updateModel(AjaxRequestTarget target) {
		form.updateModel(target);
		target.add(
				send.setEnabled(false)
				, generate.setEnabled(false)
				);
	}

	protected void onError(AjaxRequestTarget target) {
		form.onError(target);
	}

	public void onClick(AjaxRequestTarget target, InvitationForm.Action action) {
		form.onClick(target, action);
	}

	public BootstrapAjaxButton getGenerate() {
		return generate;
	}

	public BootstrapAjaxButton getSend() {
		return send;
	}
}
