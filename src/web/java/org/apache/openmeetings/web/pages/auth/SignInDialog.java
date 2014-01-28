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

import static org.apache.openmeetings.web.app.Application.getAuthenticationStrategy;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowOAuthLogin;
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowRegister;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ErrorDao;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.basic.ErrorValue;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.OmAuthenticationStrategy;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.util.BaseUrlAjaxBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.effect.JQueryEffectBehavior;
import com.googlecode.wicket.jquery.ui.form.button.Button;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class SignInDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 7746996016261051947L;
	private Form<String> form;
	private DialogButton loginBtn = new DialogButton(WebSession.getString(112));
	private String registerLbl = WebSession.getString(123);
	private DialogButton registerBtn = new DialogButton(registerLbl);
    private String password;
    private String login;
    private boolean rememberMe = false;
    private RegisterDialog r;
    private ForgetPasswordDialog f;
    private LdapConfig domain;
    private String ldapConfigFileName;
    private FeedbackPanel feedback = new FeedbackPanel("feedback");
    
	public SignInDialog(String id) {
		super(id, WebSession.getString(108));
		add(form = new SignInForm("signin"));
		add(new BaseUrlAjaxBehavior());
		add(new AjaxClientInfoBehavior());
	}

	public void setRegisterDialog(RegisterDialog r) {
		this.r = r;
	}

	public void setForgetPasswordDialog(ForgetPasswordDialog f) {
		this.f = f;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new JQueryBehavior(JQueryWidget.getSelector(this), "dialog") {
			private static final long serialVersionUID = -249782023133645704L;

			@Override
            protected String $()
            {
                return this.$(Options.asString("open"));
            }
        });
	}
	
	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		//behavior.setOption("autoOpen", true); //TODO need to be updated as soon as API will be added
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
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (registerBtn.equals(button)) {
			r.setClientTimeZone();
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
	// TODO Auto-generated method stub
	
	private void shake(AjaxRequestTarget target) {
		JQueryEffectBehavior shake = new JQueryEffectBehavior("#" + getMarkupId(), "shake");
		target.appendJavaScript(shake.toString());
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
		ldapConfigFileName = domain.getConfigFileName() == null ? "" : domain.getConfigFileName();
		if (domain.getAddDomainToUserName()) {
			login = login + "@" + domain.getDomain();
		}
		OmAuthenticationStrategy strategy = getAuthenticationStrategy();
		WebSession ws = WebSession.get();
		if (ws.signIn(login, password, ldapConfigFileName)) {
 			setResponsePage(Application.get().getHomePage());
			if (rememberMe) {
				strategy.save(login, password, ldapConfigFileName);
			} else {
				strategy.remove();
			}
		} else {
			strategy.remove();
			if (ws.getLoginError() != null) {
				ErrorValue eValue = getBean(ErrorDao.class).get(-1 * ws.getLoginError());
				if (eValue != null) {
					error(WebSession.getString(eValue.getFieldvalues_id()));
					target.add(feedback);
				}
			}
			shake(target);
		}
	}
	
	class SignInForm extends StatelessForm<String> {
		private static final long serialVersionUID = 4079939497154278822L;
		private PasswordTextField passField;
		private RequiredTextField<String> loginField;

		public SignInForm(String id) {
			super(id);
			
			if (WebSession.get().isSignedIn()) {
				alreadyLoggedIn();
			}
			add(feedback.setOutputMarkupId(true));
			add(loginField = new RequiredTextField<String>("login", new PropertyModel<String>(SignInDialog.this, "login")));
			loginField.setLabel(Model.of(WebSession.getString(114)));
			add(passField = new PasswordTextField("pass", new PropertyModel<String>(SignInDialog.this, "password")).setResetPassword(true));
			passField.setLabel(Model.of(WebSession.getString(115)));
			List<LdapConfig> ldaps = getBean(LdapConfigDao.class).getLdapConfigs();
			domain = ldaps.get(0);
			add(new WebMarkupContainer("ldap")
				.add(new DropDownChoice<LdapConfig>("domain", new PropertyModel<LdapConfig>(SignInDialog.this, "domain")
						, ldaps, new ChoiceRenderer<LdapConfig>("name", "ldapConfigId"))).setVisible(ldaps.size() > 1));
			add(new CheckBox("rememberMe", new PropertyModel<Boolean>(SignInDialog.this, "rememberMe")).setOutputMarkupId(true));
			add(new AjaxButton("submit") { //FAKE button so "submit-on-enter" works as expected
				private static final long serialVersionUID = -3612671587183668912L;

				@Override
				protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
					SignInDialog.this.onSubmit(target);
				}
				
				@Override
				protected void onError(AjaxRequestTarget target, Form<?> form) {
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
			add(new Link<Void>("netTest") {
				private static final long serialVersionUID = -9055312659797800331L;

				@Override
				public void onClick() {
					PageParameters pp = new PageParameters();
					pp.add("swf", "networktesting.swf10.swf");
					setResponsePage(SwfPage.class, pp);
				}
			});
			add(new WebMarkupContainer("oauthContainer").add(
				new ListView<OAuthServer>("oauthList", getBean(OAuth2Dao.class).getEnabledOAuthServers()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void populateItem(final ListItem<OAuthServer> item) {
						Button btn = new Button("oauthBtn");
						Image icon = new Image("icon", new Model<String>());
						icon.setVisible(item.getModelObject().getIconUrl() != null && 
								!"".equals(item.getModelObject().getIconUrl()));
						icon.add(new AttributeModifier("src", new AbstractReadOnlyModel<String>() {

							private static final long serialVersionUID = 7257002837120721882L;

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
