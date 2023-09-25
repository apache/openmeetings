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

import static org.apache.openmeetings.util.OpenmeetingsVariables.isAllowRegisterFrontend;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.UserManager;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.OmModalCloseButton;
import org.apache.openmeetings.web.pages.BaseInitedPage;
import org.apache.openmeetings.web.room.IconTextModal;
import org.apache.openmeetings.util.OmException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONException;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.TextContentModal;
import jakarta.inject.Inject;

public class SignInPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(SignInPage.class);
	public static final String TOKEN_PARAM = "token";
	private SignInDialog signin;
	private final Modal<String> kick = new IconTextModal("kick") {
		private static final long serialVersionUID = 1L;
		{
			withLabel(new ResourceModel("606"));
			withErrorIcon();
			setCloseOnEscapeKey(false);
			show(true);
			setUseCloseHandler(true);
			addButton(OmModalCloseButton.of("54"));
		}

		@Override
		protected void onClose(IPartialPageRequestHandler target) {
			WebSession.setKickedByAdmin(false);
			Application.get().restartResponseAtSignInPage();
		}
	};
	private final Modal<String> forgetInfoDialog = new TextContentModal("forgetInfo", new ResourceModel("321")) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onClose(IPartialPageRequestHandler handler) {
			signin.show(handler);
		}
	};
	private final ForgetPasswordDialog forget = new ForgetPasswordDialog("forget");
	private final Modal<String> registerInfoDialog = new TextContentModal("registerInfo", Model.of("")) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onInitialize() {
			super.onInitialize();
			setModelObject(getString("warn.notverified"));
			get("content").setOutputMarkupId(true);
		}

		@Override
		public Modal<String> setModelObject(String obj) {
			super.setModelObject(obj);
			get("content").setDefaultModelObject(obj);
			return this;
		}

		@Override
		protected void onClose(IPartialPageRequestHandler handler) {
			signin.show(handler);
		}
	};
	RegisterDialog r = new RegisterDialog("register");
	private final OtpDialog otpDialog = new OtpDialog("otpDialog", Model.of());

	@Inject
	private UserManager userManager;
	@Inject
	private OAuth2Dao oauthDao;

	public SignInPage() throws InterruptedException {
		this(new PageParameters());
	}

	public SignInPage(PageParameters p) throws InterruptedException {
		super();
		WebSession.get().checkToken(p.get(TOKEN_PARAM));
		if (WebSession.get().isSignedIn()) {
			setResponsePage(Application.get().getHomePage());
		}
		StringValue oauthid = p.get("oauthid");
		if (!oauthid.isEmpty()) { // oauth2 login
			try {
				long serverId = oauthid.toLong(-1);
				OAuthServer server = oauthDao.get(serverId);
				log.debug("OAuthServer={}", server);
				if (server == null) {
					log.warn("OAuth server id={} not found", serverId);
					return;
				}

				User u = userManager.loginOAuth(p.get("code").toOptionalString(), server);

				if (u != null && WebSession.get().signIn(u)) {
					setResponsePage(Application.get().getHomePage());
				} else {
					log.error("Failed to login via OAuth2!");
				}
			} catch (IOException|NoSuchAlgorithmException|JSONException e) {
				log.error("OAuth2 login error", e);
			}
		}
		//will try to login directly using parameters sent by POST
		IRequestParameters pp = RequestCycle.get().getRequest().getPostParameters();
		StringValue login = pp.getParameterValue("login"), password = pp.getParameterValue("password");
		if (!login.isEmpty() && !password.isEmpty()) {
			try {
				if (WebSession.get().signIn(login.toString(), password.toString(), Type.USER, null)) {
					setResponsePage(Application.get().getHomePage());
				} else {
					log.error("Failed to login using POST parameters passed");
				}
			} catch (OmException e) {
				log.error("Exception while login with POST parameters passed", e);
			}
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		signin = new SignInDialog("signin");
		add(signin.setVisible(!WebSession.get().isKickedByAdmin()),
				r.setVisible(allowRegister()), forget, kick.setVisible(WebSession.get().isKickedByAdmin()));
		add(forgetInfoDialog
				.header(new ResourceModel("312"))
				.addButton(OmModalCloseButton.of("54"))
				.setUseCloseHandler(true)
		);
		add(registerInfoDialog
				.header(new ResourceModel("235"))
				.addButton(OmModalCloseButton.of("54"))
				.setUseCloseHandler(true)
		);
		add(otpDialog);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(OnDomReadyHeaderItem.forScript(
				"""
				$('#signin-dialog, #register-dialog, #forget-dialog').on('shown.bs.modal', function () {
					$(this).find('.auto-focus').trigger('focus');
				})
				"""));
	}

	boolean allowRegister() {
		return isAllowRegisterFrontend();
	}

	boolean allowOAuthLogin() {
		return !oauthDao.getActive().isEmpty();
	}

	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget arg1) {
		WebSession.get().setArea(getUrlFragment(params));
	}
}
