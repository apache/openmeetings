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
import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowOAuthLogin;
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowRegister;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.basic.ErrorValue;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.OmAuthenticationStrategy;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
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
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.effect.JQueryEffectBehavior;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class SignInDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private Form<String> form;
	private DialogButton loginBtn = new DialogButton("login", Application.getString(112));
	private DialogButton registerBtn = new DialogButton("register", Application.getString(123));
	private String password;
	private String login;
	private boolean rememberMe = false;
	private RegisterDialog r;
	private ForgetPasswordDialog f;
	private LdapConfig domain;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));

	public SignInDialog(String id) {
		super(id, Application.getString(108));
		add(form = new SignInForm("signin"));
		add(new AjaxClientInfoBehavior());
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
		behavior.setOption("closeOnEscape", false);
		behavior.setOption("dialogClass", Options.asString("no-close"));
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
		List<DialogButton> list = new ArrayList<DialogButton>();
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
	protected void onError(AjaxRequestTarget target) {
		shake(target);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		if (domain.getAddDomainToUserName()) {
			login = login + "@" + domain.getDomain();
		}
		OmAuthenticationStrategy strategy = getAuthenticationStrategy();
		WebSession ws = WebSession.get();
		Type type = domain.getId() > 0 ? Type.ldap : Type.user;
		if (ws.signIn(login, password, type, domain.getId())) {
 			setResponsePage(Application.get().getHomePage());
			if (rememberMe) {
				strategy.save(login, password, type, domain.getId());
			} else {
				strategy.remove();
			}
		} else {
			strategy.remove();
			if (ws.getLoginError() != null) {
				ErrorValue eValue = getBean(ErrorDao.class).get(-1 * ws.getLoginError());
				if (eValue != null) {
					error(Application.getString(eValue.getLabelId()));
					target.add(feedback);
				}
			}
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
			add(loginField = new RequiredTextField<String>("login", new PropertyModel<String>(SignInDialog.this, "login")));
			loginField.setLabel(Model.of(Application.getString(114)));
			add(passField = new PasswordTextField("pass", new PropertyModel<String>(SignInDialog.this, "password")).setResetPassword(true));
			passField.setLabel(Model.of(Application.getString(115)));
			List<LdapConfig> ldaps = getBean(LdapConfigDao.class).get();
			int selectedLdap = getBean(ConfigurationDao.class).getConfValue(CONFIG_DEFAULT_LDAP_ID, Integer.class, "0");
			domain = ldaps.get(selectedLdap < ldaps.size() && selectedLdap > 0 ? selectedLdap : 0);
			add(new WebMarkupContainer("ldap")
				.add(new DropDownChoice<LdapConfig>("domain", new PropertyModel<LdapConfig>(SignInDialog.this, "domain")
						, ldaps, new ChoiceRenderer<LdapConfig>("name", "id"))).setVisible(ldaps.size() > 1));
			add(new CheckBox("rememberMe", new PropertyModel<Boolean>(SignInDialog.this, "rememberMe")).setOutputMarkupId(true));
			AjaxButton ab = new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					SignInDialog.this.onSubmit(target);
				}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
					SignInDialog.this.onError(target);
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
					setResponsePage(SwfPage.class, new PageParameters().add("swf", RoomPanel.SWF_TYPE_NETWORK));
				}
			});
			add(new WebMarkupContainer("oauthContainer").add(
				new ListView<OAuthServer>("oauthList", getBean(OAuth2Dao.class).getActive()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(final ListItem<OAuthServer> item) {
						Button btn = new Button("oauthBtn");
						Image icon = new Image("icon", new Model<String>());
						icon.setVisible(!Strings.isEmpty(item.getModelObject().getIconUrl()));
						icon.add(new AttributeModifier("src", new AbstractReadOnlyModel<String>() {
							private static final long serialVersionUID = 1L;

							@Override
							public String getObject() {
								return item.getModelObject().getIconUrl();
							}
							
						}));
						btn.add(icon);
						btn.add(new Label("label", item.getModelObject().getName()))
							.add(new AjaxEventBehavior("click") {
								private static final long serialVersionUID = 1L;
								
								@Override
								protected void onEvent(AjaxRequestTarget target) {
									PageParameters parameters = new PageParameters();
									parameters.add("oauthid", item.getModelObject().getId());
									setResponsePage(SignInPage.class, parameters);
								}
							});
						item.add(btn);
					}
				}).setVisible(allowOAuthLogin()));
		}

		private void alreadyLoggedIn() {
			// logon successful. Continue to the original destination
			continueToOriginalDestination();
			// Ups, no original destination. Go to the home page
			throw new RestartResponseException(Application.get().getHomePage());
		}
	}
}
