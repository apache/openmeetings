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

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageFormDialog;

public abstract class ConfirmableAjaxBorder extends Border {
	private static final long serialVersionUID = 1L;
	private final Form<?> form = new Form<>("form");
	private final Form<?> userForm;
	private final AbstractFormDialog<?> dialog;

	public ConfirmableAjaxBorder(String id, String title, String message) {
		this(id, title, message, null);
	}
	
	public ConfirmableAjaxBorder(String id, String title, String message, Form<?> form) {
		super(id, Model.of(message));
		dialog = newFormDialog("dialog", title, message);
		this.userForm = form;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				ConfirmableAjaxBorder.this.updateAjaxAttributes(attributes);
			}
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dialog.open(target);
			}
		});
		addToBorder(form.add(dialog));
	}

	/**
	 * Gives a chance to the specializations to modify the attributes.
	 * 
	 * @param attributes attributes
	 */
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
	}
	
	protected void onEvent(AjaxRequestTarget target) {
		dialog.open(target);
	}
	
	/**
	 * Triggered when the form is submitted, but the validation failed
	 *
	 * @param target the {@link AjaxRequestTarget}
	 * @param form the {@link Form}
	 */
	protected void onError(AjaxRequestTarget target, Form<?> form) {
	}

	/**
	 * Triggered when the form is submitted, and the validation succeed
	 *
	 * @param target the {@link AjaxRequestTarget}
	 * @param form the {@link Form}
	 */
	protected abstract void onSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * Create the dialog instance<br/>
	 * <b>Warning:</b> to be overridden with care!
	 *
	 * @param id the markupId
	 * @param title the title of the dialog
	 * @param message the message to be displayed
	 * @return the dialog instance
	 */
	protected AbstractFormDialog<?> newFormDialog(String id, String title, String message) {
		return new MessageFormDialog(id, title, message, DialogButtons.OK_CANCEL, DialogIcon.WARN) {
			private static final long serialVersionUID = 1L;

			@Override
			public DialogButton getSubmitButton() {
				return this.findButton(OK);
			}

			@Override
			public Form<?> getForm() {
				return userForm == null ? form : userForm;
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				super.close(target, null); // closes the dialog on error.
				ConfirmableAjaxBorder.this.onError(target, this.getForm());
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				ConfirmableAjaxBorder.this.onSubmit(target, this.getForm());
			}
		};
	}
}
