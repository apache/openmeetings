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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.IRequestParameters;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

@AuthorizeInstantiation({"Admin", "Dashboard", "Room"})
public class MainPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(MainPage.class, getWebAppRootKey());
	private static final String MAIN_PANEL_ID = "main";
	private final WebMarkupContainer mainContainer = new WebMarkupContainer("main-container");
	private final AbstractDefaultAjaxBehavior areaBehavior = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			loaded = true;
			log.debug("MainPage::areaBehavior");
			if (uf == null) {
				uf = WebSession.get().getArea();
			}
			main.updateContents(uf == null ? OmUrlFragment.get() : uf, target);
			WebSession.get().setArea(null);
		}
	};
	private final MainPanel main = new MainPanel(MAIN_PANEL_ID);
	private final AbstractDefaultAjaxBehavior delayedLoad = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			log.debug("MainPage::delayedLoad");
			mainContainer.replace(main);
			target.add(
				mainContainer.add(areaBehavior, new Behavior() {
					private static final long serialVersionUID = 1L;

					@Override
					public void renderHead(Component component, IHeaderResponse response) {
						internalRenderHead(response);
						response.render(OnDomReadyHeaderItem.forScript(areaBehavior.getCallbackScript()));
					}
				}));
		}
	};
	private OmUrlFragment uf = null;
	private boolean loaded = false;

	public MainPage() {
		super();
		getHeader().setVisible(false);
		add(mainContainer.add(new EmptyPanel(MAIN_PANEL_ID)).setOutputMarkupId(true));
		add(delayedLoad);
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
		log.debug("MainPage::onParameterArrival");
		OmUrlFragment _f = getUrlFragment(params);;
		if (_f != null) {
			uf = _f;
		}
		if (loaded && _f != null) {
			main.updateContents(_f, target, false);
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(OnDomReadyHeaderItem.forScript(delayedLoad.getCallbackScript()));
	}
}
