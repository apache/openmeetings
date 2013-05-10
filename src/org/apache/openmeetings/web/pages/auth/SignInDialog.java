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

import static org.apache.openmeetings.web.pages.auth.SignInPage.allowRegister;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.effect.JQueryEffectBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class SignInDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 7746996016261051947L;
	private Form<String> form;
	private DialogButton loginBtn = new DialogButton(WebSession.getString(112));
	private DialogButton registerBtn = new DialogButton(WebSession.getString(123));
    private String password;
    private String login;
    private String area = "";
    private boolean rememberMe = false;
    private RegisterDialog r;
    private ForgetPasswordDialog f;
	
	public SignInDialog(String id) {
		super(id, WebSession.getString(108));
		add(form = new SignInForm("signin"));
	}

	public void setRegisterDialog(RegisterDialog r) {
		this.r = r;
	}

	public void setForgetPasswordDialog(ForgetPasswordDialog f) {
		this.f = f;
	}
	
	//TODO need to be removed
	@Override
	public DialogBehavior newWidgetBehavior(String selector) {
		DialogBehavior db = super.newWidgetBehavior(selector);
		db.setOption("autoOpen", true);
		db.setOption("closeOnEscape", false);
		db.setOption("resizable", false);
		db.setOption("open", "function(event, ui) {$('.ui-dialog-titlebar-close').hide();}");
		return db;
	}
	
	@Override
	public boolean isDefaultCloseEventEnabled() {
		return false;
	}
	
	@Override
	public int getWidth() {
		return 450;
	}
	
	@Override
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (registerBtn.equals(button)) {
			r.open(target);
		}
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		List<DialogButton> list = new ArrayList<DialogButton>();
		if (allowRegister()) {
			list.add(registerBtn);
		}
		list.add(loginBtn);
		return list;
	}
	
	@Override
	protected DialogButton getSubmitButton() {
		return loginBtn;
	}

	@Override
	public Form<String> getForm() {
		return form;
	}

	private void shake(AjaxRequestTarget target) {
		JQueryEffectBehavior shake = new JQueryEffectBehavior("#" + getMarkupId(), "shake");
		target.appendJavaScript(shake.toString());
	}
	
	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		if (button.equals(registerBtn) || WebSession.get().isSignedIn()) {
			super.onClick(target, button);
		}
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		shake(target);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
		if (WebSession.get().signIn(login, password)) {
			WebSession.get().setArea(area);
 			setResponsePage(Application.get().getHomePage());
			if (rememberMe) {
				strategy.save(login, password);
			} else {
				strategy.remove();
			}
		} else {
			strategy.remove();
			shake(target);
		}
	}
	
	class SignInForm extends StatelessForm<String> {
		private static final long serialVersionUID = 4079939497154278822L;

		public SignInForm(String id) {
			super(id);
			
			if (WebSession.get().isSignedIn()) {
				alreadyLoggedIn();
			} else {
				IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
				// get username and password from persistence store
				String[] data = strategy.load();

				if ((data != null) && (data.length > 1)) {
					// try to sign in the user
					if (WebSession.get().signIn(data[0], data[1])) {
						login = data[0];
						password = data[1];

						alreadyLoggedIn();
					} else {
						// the loaded credentials are wrong. erase them.
						strategy.remove();
					}
				}
			}
			add(new FeedbackPanel("feedback"));
			add(new RequiredTextField<String>("login", new PropertyModel<String>(SignInDialog.this, "login")));
			add(new PasswordTextField("pass", new PropertyModel<String>(SignInDialog.this, "password")).setResetPassword(true));
			add(new CheckBox("rememberMe", new PropertyModel<Boolean>(SignInDialog.this, "rememberMe")).setOutputMarkupId(true));
			add(new HiddenField<String>("area", new PropertyModel<String>(SignInDialog.this, "area"))
					.setMarkupId("area")
					.setOutputMarkupId(true));
			add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = -3612671587183668912L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					SignInDialog.this.onSubmit(target);
				}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					// TODO Auto-generated method stub
					SignInDialog.this.onError(target);
				}
			});
			add(new AjaxLink<Void>("forget") {
				private static final long serialVersionUID = -7497568829491287604L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					SignInDialog.this.close(target, null);
					f.open(target);
				}
			});
		}

		private void alreadyLoggedIn() {
			// logon successful. Continue to the original destination
			continueToOriginalDestination();
			// Ups, no original destination. Go to the home page
			throw new RestartResponseException(Application.get().getHomePage());
		}
	}
}
