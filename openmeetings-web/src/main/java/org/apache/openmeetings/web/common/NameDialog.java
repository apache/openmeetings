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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public abstract class NameDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final Form<String> form;
	protected final NotificationPanel feedback = new NotificationPanel("feedback");
	private final String name;

	protected NameDialog(String id) {
		this(id, null);
	}

	protected NameDialog(String id, String name) {
		super(id, Model.of(name));
		this.name = name;
		form = new Form<>("form", getModel());
	}

	@Override
	protected void onInitialize() {
		header(getTitle());

		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, getAddBtnLabel(), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				NameDialog.this.onSubmit(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				NameDialog.this.onError(target);
			}
		}); // add
		addButton(OmModalCloseButton.of());
		RequiredTextField<String> title = new RequiredTextField<>("title", getModel());
		title.setLabel(getLabel());
		form.add(new Label("label", getLabel())
				, title
				, feedback.setOutputMarkupId(true)
				, new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						NameDialog.this.onSubmit(target);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						NameDialog.this.onError(target);
					}
				});
		add(form.setOutputMarkupId(true));
		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		handler.add(form);
		setModelObject(name);
		getFeedbackMessages().clear();
		return super.show(handler);
	}

	protected void onSubmit(AjaxRequestTarget target) {
		close(target);
	}

	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	protected IModel<String> getTitle() {
		return new ResourceModel("703");
	}

	protected IModel<String> getLabel() {
		return new ResourceModel("572");
	}

	protected IModel<String> getAddBtnLabel() {
		return new ResourceModel("1261");
	}
}
