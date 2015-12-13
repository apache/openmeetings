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

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public abstract class AddFolderDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private final DialogButton add = new DialogButton(WebSession.getString(1261));
	private final DialogButton cancel = new DialogButton(WebSession.getString(219));
	private final Form<String> form;
	private final FeedbackPanel feedback = new FeedbackPanel("feedback");
	private final String name;
	private RequiredTextField<String> title;

	public AddFolderDialog(String id) {
		this(id, null);
	}
	
	public AddFolderDialog(String id, String name) {
		super(id, WebSession.getString(1260), Model.of(name));
		this.name = name;
		form = new Form<String>("form", getModel()) {
			private static final long serialVersionUID = 1L;
			{
				add(title = new RequiredTextField<String>("title", getModel()));
				title.setLabel(Model.of(WebSession.getString(572)));
				add(feedback.setOutputMarkupId(true));
				add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
					private static final long serialVersionUID = -3612671587183668912L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						AddFolderDialog.this.onSubmit(target);
					}
					
					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						AddFolderDialog.this.onError(target);
					}
				});
			}
		};
		add(form);
	}

	@Override
	protected void onOpen(AjaxRequestTarget target) {
		super.onOpen(target);
		
		setModelObject(name);
		getFeedbackMessages().clear();
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		close(target, getSubmitButton());
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(add, cancel);
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return add;
	}

	@Override
	public Form<String> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}
}
