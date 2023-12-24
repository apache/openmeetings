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

import static org.apache.openmeetings.web.app.Application.isInstalled;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getGaCode;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.HeaderPanel;
import org.apache.openmeetings.web.pages.install.InstallWizardPage;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.OmUrlFragment.AreaKeys;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.head.filter.FilteredHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.urlfragment.AsyncUrlFragmentAwarePage;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapResourcesBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6CssReference;

public abstract class BasePage extends AsyncUrlFragmentAwarePage {
	private static final long serialVersionUID = 1L;
	public static final String ALIGN_LEFT = "float-start ";
	public static final String ALIGN_RIGHT = "float-end ";
	public static final String CUSTOM_CSS_FILTER = "customCSS";
	private final Map<String, String> options = new HashMap<>();
	private HeaderPanel header;
	private final WebMarkupContainer loader = new WebMarkupContainer("main-loader") {
		private static final long serialVersionUID = 1L;

		@Override
		public void renderHead(IHeaderResponse response) {
			response.render(CssHeaderItem.forReference(new CssResourceReference(BasePage.class, "loader.css")));
		}
	};

	protected BasePage() {
		if (isInitComplete()) {
			if (!isInstalled() && ! (this instanceof InstallWizardPage)) {
				throw new RestartResponseException(InstallWizardPage.class);
			}
		} else if (!(this instanceof NotInitedPage)) {
			throw new RestartResponseException(NotInitedPage.class);
		}
		options.put("fragmentIdentifierSuffix", "");
		options.put("keyValueDelimiter", "/");
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		String appName = getApplicationName();

		String code = getLanguageCode();
		add(new TransparentWebMarkupContainer("html")
				.add(AttributeModifier.replace("xml:lang", code))
				.add(AttributeModifier.replace("lang", code))
				.add(AttributeModifier.replace("dir", isRtl() ? "rtl" : "ltr")));
		add(new Label("pageTitle", appName));
		add(header = new HeaderPanel("header", appName));
		add(loader.setVisible(isMainPage()).setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
		add(new HeaderResponseContainer(CUSTOM_CSS_FILTER, CUSTOM_CSS_FILTER));
	}

	public abstract boolean isRtl();
	protected abstract String getLanguageCode();

	protected OmUrlFragment getUrlFragment(IRequestParameters params) {
		for (AreaKeys key : AreaKeys.values()) {
			StringValue type = params.getParameterValue(key.zone());
			if (!type.isEmpty()) {
				return new OmUrlFragment(key, type.toString());
			}
		}
		return null;
	}

	public HeaderPanel getHeader() {
		return header;
	}

	@Override
	protected Map<String, String> getOptions() {
		return options;
	}

	protected boolean isMainPage() {
		return false;
	}

	protected void internalRenderHead(IHeaderResponse response) {
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference())));
		super.renderHead(response);
		final String suffix = DEVELOPMENT == getApplication().getConfigurationType() ? "" : ".min";
		response.render(CssHeaderItem.forUrl("css/theme_om/jquery-ui" + suffix + ".css"));
		response.render(CssHeaderItem.forUrl("css/theme" + suffix + ".css"));
		if (!Strings.isEmpty(getGaCode())) {
			response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BasePage.class, "om-ga.js") {
				private static final long serialVersionUID = 1L;

				@Override
				public List<HeaderItem> getDependencies() {
					return List.of(new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("https://www.googletagmanager.com/gtag/js?id=" + getGaCode()).setAsync(true)));
				}
			})));
			StringBuilder script = new StringBuilder("initGA('")
					.append(getGaCode()).append("', ").append(isMainPage()).append(");");
			response.render(OnDomReadyHeaderItem.forScript(script));
		}
		response.render(CssHeaderItem.forReference(FontAwesome6CssReference.instance()));
		BootstrapResourcesBehavior.instance().renderHead(Bootstrap.getSettings(getApplication()), response);
		response.render(new FilteredHeaderItem(CssHeaderItem.forUrl("css/custom.css"), CUSTOM_CSS_FILTER));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		internalRenderHead(response);
	}

	public WebMarkupContainer getLoader() {
		return loader;
	}
}
