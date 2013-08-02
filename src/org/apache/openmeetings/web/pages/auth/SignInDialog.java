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
import static org.apache.openmeetings.web.pages.auth.SignInPage.allowRegister;
import static org.apache.wicket.ajax.attributes.CallbackParameter.resolved;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.remote.LdapConfigService;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.OmAuthenticationStrategy;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.openmeetings.web.util.BaseUrlAjaxBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.effect.JQueryEffectBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class SignInDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 7746996016261051947L;
	private Form<String> form;
	private DialogButton loginBtn = new DialogButton(WebSession.getString(112));
	private DialogButton registerBtn = new DialogButton(WebSession.getString(123));
    private String password;
    private String login;
    private boolean rememberMe = false;
    private RegisterDialog r;
    private ForgetPasswordDialog f;
    private LdapConfig domain;
    private String ldapConfigFileName;
	private HiddenField<Integer> browserTZOffset;
	
	public SignInDialog(String id) {
		super(id, WebSession.getString(108));
		add(form = new SignInForm("signin"));
		browserTZOffset = new HiddenField<Integer>("tzOffset", Model.of(new Integer(0)));
		add(browserTZOffset);

		// This code is required to detect time zone offset
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forScript(getCallbackFunctionBody(resolved("tzOffset", "getTimeZoneOffsetMinutes()")), "getTzOffset"));
			}
			
			@Override
			protected void respond(AjaxRequestTarget target) {
				StringValue offset = getRequestCycle().getRequest().getRequestParameters().getParameterValue("tzOffset");
				try {
					browserTZOffset.setModelObject(offset.toInteger());
				} catch (NumberFormatException ex) { }
			}
		});
		add(new BaseUrlAjaxBehavior());
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
	protected void onConfigure(JQueryBehavior behavior) {
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
		return 450;
	}
	
	@Override
	public void onClose(AjaxRequestTarget target, DialogButton button) {
		if (registerBtn.equals(button)) {
			r.setBrowserTZOffset(target, browserTZOffset.getModelObject());
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
		ldapConfigFileName = domain.getConfigFileName() == null ? "" : domain.getConfigFileName();
		if (domain.getAddDomainToUserName()) {
			login = login + "@" + domain.getDomain();
		}
		OmAuthenticationStrategy strategy = getAuthenticationStrategy();
		if (WebSession.get().signIn(login, password, ldapConfigFileName)) {
 			setResponsePage(Application.get().getHomePage());
			if (rememberMe) {
				strategy.save(login, password, ldapConfigFileName);
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
		private PasswordTextField passField;
		private RequiredTextField<String> loginField;

		public SignInForm(String id) {
			super(id);
			
			if (WebSession.get().isSignedIn()) {
				alreadyLoggedIn();
			}
			add(new FeedbackPanel("feedback"));
			add(loginField = new RequiredTextField<String>("login", new PropertyModel<String>(SignInDialog.this, "login")));
			loginField.setLabel(Model.of(WebSession.getString(114)));
			add(passField = new PasswordTextField("pass", new PropertyModel<String>(SignInDialog.this, "password")).setResetPassword(true));
			passField.setLabel(Model.of(WebSession.getString(115)));
			List<LdapConfig> ldaps = getBean(LdapConfigService.class).getActiveLdapConfigs();
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
		}

		private void alreadyLoggedIn() {
			// logon successful. Continue to the original destination
			continueToOriginalDestination();
			// Ups, no original destination. Go to the home page
			throw new RestartResponseException(Application.get().getHomePage());
		}
	}
}
