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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DEFAULT_LDAP_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowOAuthLogin;
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowRegister;
import static org.apache.openmeetings.web.pages.auth.SignInPage.showAuth;
import static org.apache.openmeetings.web.room.SwfPanel.SWF;
import static org.apache.openmeetings.web.room.SwfPanel.SWF_TYPE_NETWORK;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.OmAuthenticationStrategy;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmAjaxClientInfoBehavior;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.util.NonClosableDialog;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.effect.JQueryEffectBehavior;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class SignInDialog extends NonClosableDialog<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(SignInDialog.class, getWebAppRootKey());
	private Form<String> form;
	private DialogButton loginBtn;
	private DialogButton registerBtn;
	private String password;
	private String login;
	private boolean rememberMe = false;
	private RegisterDialog r;
	private ForgetPasswordDialog f;
	private LdapConfig domain;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));

	public SignInDialog(String id) {
		super(id, "");
		add(form = new SignInForm("signin"));
		add(new OmAjaxClientInfoBehavior());
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("108"));
		loginBtn = new DialogButton("login", getString("112")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isIndicating() {
				return true;
			}
		};
		registerBtn = new DialogButton("register", getString("123"));
		super.onInitialize();
	}

	public void setRegisterDialog(RegisterDialog r) {
		this.r = r;
	}

	public void setForgetPasswordDialog(ForgetPasswordDialog f) {
		this.f = f;
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", true);
		behavior.setOption("resizable", false);
	}

	@Override
	public boolean isDefaultCloseEventEnabled() {
		return false;
	}

	@Override
	public int getWidth() {
		return allowOAuthLogin()? 550: 450;
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		if (registerBtn.equals(button)) {
			r.setClientTimeZone();
			r.open(handler);
		}
	}

	@Override
	protected List<DialogButton> getButtons() {
		List<DialogButton> list = new ArrayList<>();
		if (allowRegister()) {
			list.add(registerBtn);
		}
		list.add(loginBtn);
		return list;
	}

	@Override
	public DialogButton getSubmitButton() {
		return loginBtn;
	}

	@Override
	public Form<String> getForm() {
		return form;
	}

	private void shake(AjaxRequestTarget target) {
		target.appendJavaScript(JQueryEffectBehavior.toString("#" + getMarkupId(), "shake"));
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		if (registerBtn.equals(button) || WebSession.get().isSignedIn()) {
			super.onClick(target, button);
		}
	}

	@Override
	protected void onError(AjaxRequestTarget target, DialogButton btn) {
		shake(target);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
		if (domain.getAddDomainToUserName()) {
			login = login + "@" + domain.getDomain();
		}
		OmAuthenticationStrategy strategy = getAuthenticationStrategy();
		WebSession ws = WebSession.get();
		Type type = domain.getId() > 0 ? Type.ldap : Type.user;
		boolean signIn = false;
		try {
			signIn = ws.signIn(login, password, type, domain.getId());
		} catch (OmException e) {
			error(getString(e.getKey()));
			target.add(feedback);
		}
		if (signIn) {
 			setResponsePage(Application.get().getHomePage());
			if (rememberMe) {
				strategy.save(login, password, type, domain.getId());
			} else {
				strategy.remove();
			}
		} else {
			if (!hasErrorMessage()) {
				error(getString("error.bad.credentials"));
				target.add(feedback);
			}
			// add random timeout
			try {
				Thread.sleep(6 + (long)(10 * Math.random() * 1000));
			} catch (InterruptedException e) {
				log.error("Unexpected exception while sleeping", e);
			}
			strategy.remove();
			shake(target);
		}
	}

	class SignInForm extends StatelessForm<String> {
		private static final long serialVersionUID = 1L;
		private PasswordTextField passField;
		private RequiredTextField<String> loginField;

		public SignInForm(String id) {
			super(id);

			if (WebSession.get().isSignedIn()) {
				alreadyLoggedIn();
			}
			add(feedback.setOutputMarkupId(true));
			add(loginField = new RequiredTextField<>("login", new PropertyModel<String>(SignInDialog.this, "login")));
			add(passField = new PasswordTextField("pass", new PropertyModel<String>(SignInDialog.this, "password")).setResetPassword(true));
			List<LdapConfig> ldaps = getBean(LdapConfigDao.class).get();
			int selectedLdap = getBean(ConfigurationDao.class).getInt(CONFIG_DEFAULT_LDAP_ID, 0);
			domain = ldaps.get(selectedLdap < ldaps.size() && selectedLdap > 0 ? selectedLdap : 0);
			add(new WebMarkupContainer("ldap")
				.add(new DropDownChoice<>("domain", new PropertyModel<LdapConfig>(SignInDialog.this, "domain")
						, ldaps, new ChoiceRenderer<LdapConfig>("name", "id"))).setVisible(ldaps.size() > 1));
			add(new CheckBox("rememberMe", new PropertyModel<Boolean>(SignInDialog.this, "rememberMe")).setOutputMarkupId(true));
			AjaxButton ab = new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					SignInDialog.this.onSubmit(target, loginBtn);
				}

				@Override
				protected void onError(AjaxRequestTarget target) {
					SignInDialog.this.onError(target, loginBtn);
				}
			};
			add(ab);
			setDefaultButton(ab);
			add(new AjaxLink<Void>("forget") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					SignInDialog.this.close(target, null);
					f.open(target);
				}
			});
			add(new Link<Void>("netTest") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					setResponsePage(HashPage.class, new PageParameters().add(SWF, SWF_TYPE_NETWORK));
				}
			});
			add(new WebMarkupContainer("oauthContainer").add(
				new ListView<OAuthServer>("oauthList", getBean(OAuth2Dao.class).getActive()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(final ListItem<OAuthServer> item) {
						final OAuthServer s = item.getModelObject();
						Button btn = new Button("oauthBtn") {
							private static final long serialVersionUID = 1L;

							@Override
							public void onSubmit() {
								showAuth(s);
							}
						};
						Component lbl = new Label("label", s.getName());
						if (!Strings.isEmpty(s.getIconUrl())) {
							lbl.add(AttributeModifier.replace("style", String.format("background-image: url(%s)", s.getIconUrl())));
						}
						btn.add(lbl);
						item.add(btn.setDefaultFormProcessing(false)); //skip all rules, go to redirect
					}
				}).setVisible(allowOAuthLogin()));
		}

		@Override
		protected void onInitialize() {
			loginField.setLabel(new ResourceModel("114"));
			passField.setLabel(new ResourceModel("110"));
			super.onInitialize();
		}

		private void alreadyLoggedIn() {
			// logon successful. Continue to the original destination
			continueToOriginalDestination();
			// Ups, no original destination. Go to the home page
			throw new RestartResponseException(Application.get().getHomePage());
		}
	}
}
