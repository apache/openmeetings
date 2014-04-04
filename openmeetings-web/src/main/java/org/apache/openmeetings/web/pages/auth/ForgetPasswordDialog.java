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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getBaseUrl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.mail.MailHandler;
import org.apache.openmeetings.util.crypt.ManageCryptStyle;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.mail.template.ResetPasswordTemplate;
import org.apache.openmeetings.web.pages.ResetPage;
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
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class ForgetPasswordDialog extends AbstractFormDialog<String> {
	private static final Logger log = Red5LoggerFactory.getLogger(ForgetPasswordDialog.class, webAppRootKey);
	private static final long serialVersionUID = 8494008571497363018L;
	private String sendLbl = WebSession.getString(317);
	private DialogButton send = new DialogButton(sendLbl);
	private DialogButton cancel = new DialogButton(WebSession.getString(122));
	private FeedbackPanel feedback = new FeedbackPanel("feedback");
	private RequiredTextField<String> nameField;
	private Form<String> form;
	private SignInDialog s;
	private String name;
	private Type type = Type.email;
    final MessageDialog confirmDialog;
	
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
				add(nameField = new RequiredTextField<String>("name", new PropertyModel<String>(ForgetPasswordDialog.this, "name")));
				nameField.setLabel(Model.of(WebSession.getString(type == Type.email ? 315 : 316)));
				RadioGroup<Type> rg = new RadioGroup<Type>("type", new PropertyModel<Type>(ForgetPasswordDialog.this, "type"));
				add(rg.add(new Radio<Type>("email", Model.of(Type.email)).setOutputMarkupId(true))
						.add(new Radio<Type>("login", Model.of(Type.login)).setOutputMarkupId(true))
						.setOutputMarkupId(true));
				rg.add(new AjaxFormChoiceComponentUpdatingBehavior() {
					private static final long serialVersionUID = 5814272716387415523L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						lblModel.setObject(WebSession.getString(type == Type.email ? 315 : 316));
						nameField.setLabel(Model.of(WebSession.getString(type == Type.email ? 315 : 316)));
						target.add(label);
					}
				});
				add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
					private static final long serialVersionUID = 5257502637636428620L;

					@Override
					protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
						ForgetPasswordDialog.this.onSubmit(target);
					}
					
					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {
						ForgetPasswordDialog.this.onError(target);
					}
				});
			}
			
			@Override
			protected void onValidate() {
				AdminUserDao dao = getBean(AdminUserDao.class);
				String n = nameField.getConvertedInput();
				if (n != null) {
					if (type == Type.email && null == dao.getUserByEmail(n)) {
						error(WebSession.getString(318));
					}
					if (type == Type.login && null == dao.getUserByName(n)) {
						error(WebSession.getString(320));
					}
				}
			}
		});
		confirmDialog = new MessageDialog("confirmDialog", WebSession.getString(312), WebSession.getString(321), DialogButtons.OK, DialogIcon.INFO){
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
		        behavior.setOption("dialogClass", Options.asString("no-close"));
				behavior.setOption("closeOnEscape", false);
			}
			
			public void onClose(AjaxRequestTarget target, DialogButton button) {
				s.open(target);
			}
		};
		add(confirmDialog);
	}

	@Override
	public boolean isDefaultCloseEventEnabled()	{
		return true;
	}
	
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (send.equals(button)){
			confirmDialog.open(target);
		} else {
			s.open(target);
		}
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
		resetUser(type == Type.email ? name : "", type == Type.login ? name : ""
			, getBaseUrl() + getRequestCycle().urlFor(ResetPage.class, new PageParameters()).toString().substring(2));
	}

	/**
	 * reset a username by a given mail oder login by sending a mail to the
	 * registered EMail-Address
	 * 
	 * @param email
	 * @param username
	 * @param appLink
	 * @return
	 */
	private Long resetUser(String email, String username, String appLink) {
		try {
			AdminUserDao userDao = getBean(AdminUserDao.class);
			log.debug("resetUser " + email);

			// check if Mail given
			if (email.length() > 0) {
				// log.debug("getAdresses_id "+addr_e.getAdresses_id());
				User us = userDao.getUserByEmail(email);
				if (us != null) {
					sendHashByUser(us, appLink, userDao);
					return new Long(-4);
				} else {
					return new Long(-9);
				}
			} else if (username.length() > 0) {
				User us = userDao.getUserByName(username);
				if (us != null) {
					sendHashByUser(us, appLink, userDao);
					return new Long(-4);
				} else {
					return new Long(-3);
				}
			}
		} catch (Exception e) {
			log.error("[resetUser]", e);
			return new Long(-1);
		}
		return new Long(-2);
	}

	private void sendHashByUser(User us, String appLink, AdminUserDao userDao) throws Exception {
		String loginData = us.getLogin() + new Date();
		log.debug("User: " + us.getLogin());
		us.setResethash(ManageCryptStyle.getInstanceOfCrypt().createPassPhrase(loginData));
		userDao.update(us, -1L);
		String reset_link = appLink + "?hash=" + us.getResethash();

		String email = us.getAdresses().getEmail();

		String template = ResetPasswordTemplate.getEmail(reset_link);

		getBean(MailHandler.class).send(email, WebSession.getString(517), template);
	}

}
