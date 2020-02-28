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

import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;

public abstract class ConfirmableAjaxBorder extends Border {
	private static final long serialVersionUID = 1L;
	private final IModel<String> title;
	private final IModel<String> message;
	private ConfirmationDialog dialog;

	public ConfirmableAjaxBorder(String id, IModel<String> title, IModel<String> message) {
		super(id);
		this.title = title;
		this.message = message;
		setOutputMarkupId(true);
	}

	private ConfirmationDialog getDialog() {
		if (dialog == null) {
			dialog = new ConfirmationDialog("dialog", title, message) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onConfirm(AjaxRequestTarget target) {
					ConfirmableAjaxBorder.this.onConfirm(target);
				}
			};
		}
		return dialog;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
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
					getDialog().show(target);
				}
			}
		});
		addToBorder(getDialog());
	}

	protected boolean isClickable() {
		return true;
	}

	public ConfirmableAjaxBorder setTitle(IModel<String> title) {
		getDialog().header(title);
		return this;
	}

	public ConfirmableAjaxBorder setMessage(IModel<String> message) {
		getDialog().setModel(message);
		return this;
	}

	/**
	 * Gives a chance to the specializations to modify the attributes.
	 *
	 * @param attributes - attributes
	 */
	protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
	}

	protected void onEvent(AjaxRequestTarget target) {
		getDialog().show(target);
	}

	/**
	 * Triggered when the form is submitted, and the validation succeed
	 *
	 * @param target - the {@link AjaxRequestTarget}
	 */
	protected abstract void onConfirm(AjaxRequestTarget target);

	@Override
	protected void detachModel() {
		super.detachModel();
		title.detach();
		message.detach();
	}

	public static ConfirmationBehavior newOkCancelDangerConfirm(Component c, String title) {
		return new ConfirmationBehavior(newOkCancelConfirmCfg(c, title)
				.withBtnOkClass("btn btn-sm btn-danger")
				.withBtnOkIconClass("fas fa-exclamation-triangle")
				);
	}

	public static ConfirmationBehavior newOkCancelConfirm(Component c, String title) {
		return new ConfirmationBehavior(newOkCancelConfirmCfg(c, title));
	}

	public static ConfirmationConfig newOkCancelConfirmCfg(Component c, String title) {
		return new ConfirmationConfig()
				.withBtnCancelLabel(c.getString("lbl.cancel"))
				.withBtnOkLabel(c.getString("54"))
				.withTitle(title);
	}
}
