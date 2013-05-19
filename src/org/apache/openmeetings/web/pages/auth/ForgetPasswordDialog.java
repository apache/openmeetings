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
package org.apache.openmeetings.web.pages.auth;

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class ForgetPasswordDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 8494008571497363018L;
	private DialogButton send = new DialogButton(WebSession.getString(317));
	private DialogButton cancel = new DialogButton(WebSession.getString(122));
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	private RequiredTextField<String> nameField;
	private Form<String> form;
	private SignInDialog s;
	private String name;
	private Type type = Type.email;
	
	enum Type {
		email
		, login
	}
	
	public ForgetPasswordDialog(String id) {
		super(id, WebSession.getString(312));
		add(form = new Form<String>("form") {
			private static final long serialVersionUID = 6340692639510268144L;
			private IModel<String> lblModel = Model.of(WebSession.getString(315));
			private Label label = new Label("label", lblModel);
			
			{
				add(feedback.setOutputMarkupId(true));
				add(label.setOutputMarkupId(true));
				RadioGroup<Type> rg = new RadioGroup<Type>("type", new PropertyModel<Type>(ForgetPasswordDialog.this, "type"));
				add(rg.add(new Radio<Type>("email", Model.of(Type.email)).setOutputMarkupId(true))
						.add(new Radio<Type>("login", Model.of(Type.login)).setOutputMarkupId(true))
						.setOutputMarkupId(true));
				rg.add(new AjaxFormChoiceComponentUpdatingBehavior() {
					private static final long serialVersionUID = 5814272716387415523L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						lblModel.setObject(WebSession.getString(type == Type.email ? 315 : 316));
						target.add(label);
					}
				});
				add(nameField = new RequiredTextField<String>("name", new PropertyModel<String>(ForgetPasswordDialog.this, "name")));
				add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
					private static final long serialVersionUID = 5257502637636428620L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						ForgetPasswordDialog.this.onSubmit(target);
					}
					
					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						// TODO Auto-generated method stub
						ForgetPasswordDialog.this.onError(target);
					}
				});
			}
			
			@Override
			protected void onValidate() {
				UsersDao dao = getBean(UsersDao.class);
				String n = nameField.getConvertedInput();
				if (type == Type.email && null == dao.getUserByEmail(n)) {
					error(WebSession.getString(318));
				}
				if (type == Type.login && null == dao.getUserByName(n)) {
					error(WebSession.getString(320));
				}
			}
		});
	}

	public void onClose(AjaxRequestTarget target, DialogButton button) {
		s.open(target);
	}

	public void setSignInDialog(SignInDialog s) {
		this.s = s;
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(send, cancel);
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return send;
	}

	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		//FIXME forgot password should be handled be Wicket
		getBean(UserManager.class).resetUser(type == Type.email ? name : "", type == Type.login ? name : ""
			, "" + getRequestCycle().urlFor(SwfPage.class, new PageParameters()));
	}
}
