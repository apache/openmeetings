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
package org.apache.openmeetings.web.user.dashboard;

import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;
import static org.apache.openmeetings.web.pages.HashPage.APP_KEY;
import static org.apache.openmeetings.web.pages.HashPage.APP_TYPE_SETTINGS;
import static org.apache.openmeetings.web.util.OmUrlFragment.CALENDAR;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_PUBLIC;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.pages.HashPage;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.web.WidgetView;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;

public class StartWidgetView extends WidgetView {
	private static final long serialVersionUID = 1L;

	public StartWidgetView(String id, Model<Widget> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		add(new WebMarkupContainer("step1").add(new PublicRoomsEventBehavior()));
		add(new WebMarkupContainer("step2").add(new PublicRoomsEventBehavior()));
		add(new WebMarkupContainer("step3").add(new WebMarkupContainer("avTest").add(AttributeModifier.append("href"
				, RequestCycle.get().urlFor(HashPage.class, new PageParameters().add(APP_KEY, APP_TYPE_SETTINGS)).toString()))));

		add(new WebMarkupContainer("step4").add(new PublicRoomsEventBehavior()));
		add(new Label("123msg", Application.getString("widget.start.desc")) //Application here is used to substitute {0}
				.setEscapeModelStrings(false));
		add(new BootstrapButton("start", new ResourceModel("773"), Buttons.Type.Outline_Primary).add(new PublicRoomsEventBehavior()));
		add(new BootstrapButton("calendar", new ResourceModel("291"), Buttons.Type.Outline_Primary).add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				((MainPage)getPage()).updateContents(CALENDAR, target);
			}
		}));
		super.onInitialize();
	}

	private class PublicRoomsEventBehavior extends AjaxEventBehavior {
		private static final long serialVersionUID = 1L;

		public PublicRoomsEventBehavior() {
			super(EVT_CLICK);
		}

		@Override
		protected void onEvent(AjaxRequestTarget target) {
			((MainPage)getPage()).updateContents(ROOMS_PUBLIC, target);
		}
	}
}
