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

import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageFormDialog;

public abstract class ConfirmableAjaxBorder extends Border {
	private static final long serialVersionUID = 1L;
	private static final String DIALOG_ID = "dialog";
	protected final Form<?> form = new Form<>("form");
	protected final Form<?> userForm;
	private final ConfirmableBorderDialog dialog;
	private boolean validate = false;

	public ConfirmableAjaxBorder(String id, String title, String message) {
		this(id, title, message, null, null);
	}

	public ConfirmableAjaxBorder(String id, String title, String message, Form<?> form) {
		this(id, title, message, form, null);
	}

	public ConfirmableAjaxBorder(String id, String title, String message, ConfirmableBorderDialog dialog) {
		this(id, title, message, null, dialog);
	}

	public ConfirmableAjaxBorder(String id, String title, String message, Form<?> userForm, ConfirmableBorderDialog dialog) {
		this(id, title, message, userForm, dialog, false);
	}

	public ConfirmableAjaxBorder(String id, String title, String message, Form<?> userForm, ConfirmableBorderDialog dialog, boolean validate) {
		super(id, Model.of(message));
		if (dialog == null) {
			this.dialog = new ConfirmableBorderDialog(DIALOG_ID, title, message, userForm == null ? form : userForm);
			form.add(this.dialog);
		} else {
			this.dialog = dialog;
			form.add(new EmptyPanel(DIALOG_ID));
		}
		this.userForm = userForm;
		this.validate = validate;
		this.dialog.setSubmitHandler((SerializableConsumer<AjaxRequestTarget>)t->onSubmit(t));
		this.dialog.setErrorHandler((SerializableConsumer<AjaxRequestTarget>)t->onError(t));
		setOutputMarkupId(true);
	}

	public AbstractFormDialog<?> getDialog() {
		return dialog;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		if (validate) {
			add(new AjaxFormSubmitBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					dialog.open(target);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					ConfirmableAjaxBorder.this.onError(target);
				}
			});
		} else {
			add(new AjaxEventBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
					super.updateAjaxAttributes(attributes);
					ConfirmableAjaxBorder.this.updateAjaxAttributes(attributes);
				}

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					if (isClickable()) {
						dialog.open(target);
					}
				}
			});
		}
		addToBorder(form);
	}

	protected boolean isClickable() {
		return true;
	}

	/**
	 * Gives a chance to the specializations to modify the attributes.
	 *
	 * @param attributes - attributes
	 */
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
	}

	protected void onEvent(AjaxRequestTarget target) {
		dialog.open(target);
	}

	/**
	 * Triggered when the form is submitted, but the validation failed
	 *
	 * @param target - the {@link AjaxRequestTarget}
	 */
	protected void onError(AjaxRequestTarget target) {
	}

	/**
	 * Triggered when the form is submitted, and the validation succeed
	 *
	 * @param target - the {@link AjaxRequestTarget}
	 */
	protected abstract void onSubmit(AjaxRequestTarget target);

	public static class ConfirmableBorderDialog extends MessageFormDialog {
		private static final long serialVersionUID = 1L;
		private Form<?> form;
		private SerializableConsumer<AjaxRequestTarget> submitHandler = null;
		private SerializableConsumer<AjaxRequestTarget> errorHandler = null;

		public ConfirmableBorderDialog(String id, String title, String message) {
			this(id, title, message, null);
		}

		public ConfirmableBorderDialog(String id, String title, String message, Form<?> form) {
			super(id, title, message, DialogButtons.OK_CANCEL, DialogIcon.WARN);
			this.form = form;
		}

		public void setSubmitHandler(SerializableConsumer<AjaxRequestTarget> submitHandler) {
			this.submitHandler = submitHandler;
		}

		public void setErrorHandler(SerializableConsumer<AjaxRequestTarget> errorHandler) {
			this.errorHandler = errorHandler;
		}

		@Override
		public DialogButton getSubmitButton() {
			return this.findButton(OK);
		}

		@Override
		public Form<?> getForm() {
			return this.form;
		}

		@Override
		protected void onError(AjaxRequestTarget target, DialogButton btn) {
			super.close(target, null); // closes the dialog on error.
			if (errorHandler != null) {
				errorHandler.accept(target);
			}
		}

		@Override
		protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
			if (submitHandler != null) {
				submitHandler.accept(target);
			}
		}
	}
}
