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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addOnlineUser;
import static org.apache.openmeetings.web.app.Application.removeOnlineUser;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_EDIT;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;
import static org.apache.openmeetings.web.util.OmUrlFragment.getPanel;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.ConfirmableAjaxLink;
import org.apache.openmeetings.web.common.MenuPanel;
import org.apache.openmeetings.web.user.AboutDialog;
import org.apache.openmeetings.web.user.ChatPanel;
import org.apache.openmeetings.web.util.BaseUrlAjaxBehavior;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.message.ClosedMessage;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.urlfragment.UrlFragment;

@AuthorizeInstantiation("USER")
public class MainPage extends BaseInitedPage {
	private static final long serialVersionUID = 6421960759218157999L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPage.class, webAppRootKey);
	private final MenuPanel menu;
	private final MarkupContainer contents;
	private final AbstractAjaxTimerBehavior areaBehavior;
	private DebugBar dev = null;
	
	public MainPage(PageParameters pp) {
		super();
		contents = new WebMarkupContainer("contents");
		add(contents.add(new WebMarkupContainer(CHILD_ID)).setOutputMarkupId(true).setMarkupId("contents"));
		menu = new MenuPanel("menu");
		add(menu);
		add(new AjaxLink<Void>("messages") {
			private static final long serialVersionUID = 4065339709905366840L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_MESSAGES, target);
			}
		});
		add(new ConfirmableAjaxLink("logout", 634L) {
			private static final long serialVersionUID = -2994610981053570537L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		add(new AjaxLink<Void>("profile") {
			private static final long serialVersionUID = 4065339709905366840L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				updateContents(PROFILE_EDIT, target);
			}
		});
		final AboutDialog about = new AboutDialog("aboutDialog");
		add(new AjaxLink<Void>("about") {
			private static final long serialVersionUID = 4065339709905366840L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				about.open(target);
			}
		});
		add(about);
		if (getApplication().getDebugSettings().isDevelopmentUtilitiesEnabled()) {
		    add(dev = new DebugBar("dev"));
		    dev.setOutputMarkupId(true);
		} else {
		    add(new EmptyPanel("dev").setVisible(false));
		}		
		add(new ExternalLink("bug", "https://issues.apache.org/jira/browse/OPENMEETINGS"));//FIXME hardcoded
		
		add(new ChatPanel("chatPanel"));
		add(new WebSocketBehavior() {
			private static final long serialVersionUID = -3311970325911992958L;

			@Override
			protected void onConnect(ConnectedMessage message) {
				super.onConnect(message);
				addOnlineUser(getUserId(), WebSession.get().getId());
				log.debug("WebSocketBehavior::onConnect");
			}
			
			@Override
			protected void onClose(ClosedMessage message) {
				super.onClose(message);
				removeOnlineUser(getUserId(), WebSession.get().getId());
				log.debug("WebSocketBehavior::onClose");
			}
		});
		//load preselected content
		add(areaBehavior = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND) {
			private static final long serialVersionUID = -1551197896975384329L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				OmUrlFragment area = WebSession.get().getArea();
				updateContents(area == null ? OmUrlFragment.get() : area, target);
				stop(target);
				WebSession.get().setArea(null);
			}
		});
		
		add(new BaseUrlAjaxBehavior());
	}
	
	public void updateContents(OmUrlFragment f, AjaxRequestTarget target) {
		BasePanel panel = getPanel(f.getArea(), f.getType());
		if (panel != null) {
			Component prev = contents.get(CHILD_ID);
			if (prev != null && prev instanceof BasePanel) {
				((BasePanel)prev).cleanup(target);
			}
			target.add(contents.replace(panel));
			UrlFragment uf = new UrlFragment(target);
			uf.set(f.getArea().name(), f.getType());
			panel.onMenuPanelLoad(target);
		}
		if (dev != null){
			target.add(dev);
		}
	}
	
	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget target) {
		OmUrlFragment uf = getUrlFragment(params);
		if (uf != null) {
			areaBehavior.stop(target);
			updateContents(uf, target);
		}
	}
}
