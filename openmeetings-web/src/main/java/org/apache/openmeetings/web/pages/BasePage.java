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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_GOOGLE_ANALYTICS_CODE;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.HashMap;
import java.util.Map;

import org.apache.directory.api.util.Strings;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.HeaderPanel;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.OmUrlFragment.AreaKeys;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.urlfragment.AsyncUrlFragmentAwarePage;

public abstract class BasePage extends AsyncUrlFragmentAwarePage {
	private static final long serialVersionUID = 1L;
	private final Map<String, String> options;
	private final HeaderPanel header;

	protected abstract boolean isRtl();
	protected abstract String getLanguageCode();
	protected abstract String getApplicationName();
	
	public BasePage() {
		options = new HashMap<String, String>();
		options.put("fragmentIdentifierSuffix", "");
		options.put("keyValueDelimiter", "/");
		String appName = getApplicationName();

		String code = getLanguageCode();
		add(new TransparentWebMarkupContainer("html")
	    	.add(new AttributeModifier("xml:lang", code))
	    	.add(new AttributeModifier("lang", code))
	    	.add(new AttributeModifier("dir", isRtl() ? "rtl" : "ltr"))); 
		add(new Label("pageTitle", appName));
		add(header = new HeaderPanel("header", appName));
	}
	
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

	protected String getGaCode() {
		return getBean(ConfigurationDao.class).getConfValue(CONFIG_GOOGLE_ANALYTICS_CODE, String.class, null);
	}
	
	protected boolean isMainPage() {
		return false;
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference())));
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forUrl(String.format("css/theme_om/jquery-ui.%scss"
				, RuntimeConfigurationType.DEVELOPMENT == getApplication().getConfigurationType() ? "" : "min.")));
		if (isRtl()) {
			response.render(CssHeaderItem.forUrl("css/theme-rtl.css"));
			response.render(CssHeaderItem.forUrl("css/admin-rtl.css"));
		}
		if (!Strings.isEmpty(getGaCode())) {
			response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BasePage.class, "om-ga.js"))));
			StringBuilder script = new StringBuilder("initGA('");
			script.append(getGaCode()).append("');").append(isMainPage() ? "initHash()" : "init()").append(';');
			response.render(OnDomReadyHeaderItem.forScript(script));
		}
	}
}
