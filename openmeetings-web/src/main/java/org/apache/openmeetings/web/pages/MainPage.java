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

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.request.IRequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AuthorizeInstantiation({"ADMIN", "DASHBOARD", "ROOM"})
public class MainPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MainPage.class);
	private static final String MAIN_PANEL_ID = "main";
	private final WebMarkupContainer mainContainer = new WebMarkupContainer("main-container");
	private MainPanel main;
	private OmUrlFragment uf = null;
	private boolean inited = false;
	private boolean loaded = false;

	public void updateContents(OmUrlFragment f, IPartialPageRequestHandler handler) {
		main.updateContents(f, handler);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		getHeader().setVisible(false);
		final EmptyPanel temp = new EmptyPanel(MAIN_PANEL_ID);
		add(newDelayedLoad());
		add(mainContainer.add(temp).setOutputMarkupId(true));
	}

	private AbstractDefaultAjaxBehavior newDelayedLoad() {
		return new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				log.debug("MainPage::delayedLoad");
				main = new MainPanel(MAIN_PANEL_ID);
				target.add(mainContainer.replace(main).add(newAreaBehavior()));
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				if (!inited) {
					log.debug("renderHead:: newDelayedLoad");
					inited = true;
					super.renderHead(component, response);
					response.render(OnDomReadyHeaderItem.forScript(getCallbackScript()));
				}
			}
		};
	}

	private AbstractDefaultAjaxBehavior newAreaBehavior() {
		return new AbstractDefaultAjaxBehavior() {
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

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				if (!loaded) {
					log.debug("renderHead:: newAreaBehavior");
					super.renderHead(component, response);
					internalRenderHead(response);
					response.render(OnDomReadyHeaderItem.forScript(getCallbackScript()));
				}
			}
		};
	}

	@Override
	protected boolean isMainPage() {
		return true;
	}

	@Override
	protected void onParameterArrival(IRequestParameters params, AjaxRequestTarget target) {
		log.debug("MainPage::onParameterArrival");
		OmUrlFragment newf = getUrlFragment(params);
		if (newf != null) {
			uf = newf;
		}
		if (loaded && newf != null) {
			main.updateContents(newf, target, false);
		}
	}
}
