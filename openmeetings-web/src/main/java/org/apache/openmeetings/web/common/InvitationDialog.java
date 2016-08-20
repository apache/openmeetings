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

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class InvitationDialog extends AbstractFormDialog<Invitation> {
	private static final long serialVersionUID = 1L;
	public final DialogButton generate = new DialogButton("generate", Application.getString(1526));
	public final DialogButton send = new DialogButton("send", Application.getString(218));
	public final DialogButton cancel = new DialogButton("cancel", Application.getString(219));
	private final InvitationForm form;

	public InvitationDialog(String id, final InvitationForm _form) {
		super(id, Application.getString(214),_form.getModel());
		add(form = _form);
	}

	@Override
	public int getWidth() {
		return 500;
	}

	public void updateModel(AjaxRequestTarget target) {
		form.updateModel(target);
		send.setEnabled(false, target);
		generate.setEnabled(false, target);
	}

	@Override
	protected Form<?> getForm(DialogButton button) {
		if (button.equals(generate) || button.equals(send)) {
			return form;
		}
		return super.getForm(button);
	}
	
	@Override
	public InvitationForm getForm() {
		return form;
	}

	@Override
	public DialogButton getSubmitButton() {
		return send;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(generate, send, cancel);
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		form.onError(target);
	}

	public void onSuperClick(AjaxRequestTarget target, DialogButton button) {
		super.onClick(target, button);
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		form.onClick(target, button);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		//designed to be empty because of multiple submit buttons
	}
}
