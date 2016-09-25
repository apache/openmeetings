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
package org.apache.openmeetings.web.pages;

import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getNamedFunction;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.getParam;
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;
import static org.apache.openmeetings.web.common.MainPanel.PARAM_USER_ID;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.user.InviteUserToRoomDialog;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.util.time.Duration;

@AuthorizeInstantiation({"Admin", "Dashboard", "Room"})
public class MainPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private final AbstractAjaxTimerBehavior areaBehavior;
	private final MainPanel main = new MainPanel("main");
	private final InviteUserToRoomDialog inviteUser;

	public MainPage() {
		super();
		getHeader().setVisible(false);
		add(main, inviteUser = new InviteUserToRoomDialog("invite-to-room"));
		//load preselected content
		add(areaBehavior = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				OmUrlFragment area = WebSession.get().getArea();
				main.updateContents(area == null ? OmUrlFragment.get() : area, target);
				stop(target);
				WebSession.get().setArea(null);
			}
		});
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				inviteUser.open(target, getParam(getComponent(), PARAM_USER_ID).toLong());
			}
			
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forScript(getNamedFunction("inviteUser", this, explicit(PARAM_USER_ID)), "inviteUser")));
			}
		});
	}

	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler) {
		main.updateContents(f, handler);
	}

	@Override
	protected boolean isMainPage() {
		return true;
	}

	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget target) {
		OmUrlFragment uf = getUrlFragment(params);
		if (uf != null) {
			areaBehavior.stop(target);
			main.updateContents(uf, target, false);
		}
	}
}
