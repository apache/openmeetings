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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public abstract class NameDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private DialogButton add;
	private DialogButton cancel;
	private final Form<String> form;
	protected final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final String name;
	private RequiredTextField<String> title;

	public NameDialog(String id) {
		this(id, null);
	}

	public NameDialog(String id, String name) {
		super(id, "", Model.of(name));
		this.name = name;
		form = new Form<>("form", getModel());
	}

	@Override
	protected void onInitialize() {
		setTitle(Model.of(getTitleStr()));
		add = new DialogButton("add", getAddStr());
		cancel = new DialogButton("cancel", getString("lbl.cancel"));
		form.add(new Label("label", getLabelStr())
				, title = new RequiredTextField<>("title", getModel())
				, feedback.setOutputMarkupId(true)
				, new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						NameDialog.this.onSubmit(target, add);
					}

					@Override
					protected void onError(AjaxRequestTarget target) {
						NameDialog.this.onError(target, add);
					}
				});
		title.setLabel(Model.of(getLabelStr()));
		add(form.setOutputMarkupId(true));
		super.onInitialize();
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		handler.add(form);
		setModelObject(name);
		getFeedbackMessages().clear();
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		close(target, getSubmitButton());
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(add, cancel);
	}

	@Override
	public DialogButton getSubmitButton() {
		return add;
	}

	@Override
	public Form<String> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		target.add(feedback);
	}

	protected String getTitleStr() {
		return getString("703");
	}

	protected String getLabelStr() {
		return getString("572");
	}

	protected String getAddStr() {
		return getString("1261");
	}
}
