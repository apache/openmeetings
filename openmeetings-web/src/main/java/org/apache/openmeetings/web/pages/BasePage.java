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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getGaCode;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.HeaderPanel;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.OmUrlFragment.AreaKeys;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.urlfragment.AsyncUrlFragmentAwarePage;

public abstract class BasePage extends AsyncUrlFragmentAwarePage {
	private static final long serialVersionUID = 1L;
	public static final String ALIGN_LEFT = "align-left ";
	public static final String ALIGN_RIGHT = "align-right ";
	private final Map<String, String> options;
	private final HeaderPanel header;
	private final WebMarkupContainer loader = new WebMarkupContainer("main-loader");

	public BasePage() {
		options = new HashMap<>();
		options.put("fragmentIdentifierSuffix", "");
		options.put("keyValueDelimiter", "/");
		String appName = getApplicationName();

		String code = getLanguageCode();
		add(new TransparentWebMarkupContainer("html")
				.add(AttributeModifier.replace("xml:lang", code))
				.add(AttributeModifier.replace("lang", code))
				.add(AttributeModifier.replace("dir", isRtl() ? "rtl" : "ltr")));
		add(new Label("pageTitle", appName));
		add(header = new HeaderPanel("header", appName));
		add(loader.setVisible(isMainPage()).setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
	}

	public abstract boolean isRtl();
	protected abstract String getLanguageCode();

	protected OmUrlFragment getUrlFragment(IRequestParameters params) {
		for (AreaKeys key : AreaKeys.values()) {
			StringValue type = params.getParameterValue(key.name());
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
		response.render(CssHeaderItem.forUrl(String.format("css/theme_om/jquery-ui%s.css", suffix)));
		response.render(CssHeaderItem.forUrl(String.format("css/theme%s.css", suffix)));
		if (isRtl()) {
			response.render(CssHeaderItem.forUrl(String.format("css/theme-rtl%s.css", suffix)));
		}
		if (!Strings.isEmpty(getGaCode())) {
			response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BasePage.class, "om-ga.js"))));
			StringBuilder script = new StringBuilder("initGA('");
			script.append(getGaCode()).append("');").append(isMainPage() ? "initHash()" : "init()").append(';');
			response.render(OnDomReadyHeaderItem.forScript(script));
		}
		response.render(CssHeaderItem.forUrl("css/custom.css"));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		internalRenderHead(response);
	}

	public WebMarkupContainer getLoader() {
		return loader;
	}
}
