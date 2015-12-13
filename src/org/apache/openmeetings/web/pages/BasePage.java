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

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.HeaderPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public abstract class BasePage extends WebPage {
	private static final long serialVersionUID = -6237917782433412496L;

	public BasePage() {
		String appName = Application.getBean(ConfigurationDao.class).getAppName();

		FieldLanguage lang = WebSession.getLanguageObj();
		String code = lang.getCode();
		add(new TransparentWebMarkupContainer("html")
	    	.add(new AttributeModifier("xml:lang", code))
	    	.add(new AttributeModifier("lang", code))
	    	.add(new AttributeModifier("dir", Boolean.TRUE.equals(lang.getRtl()) ? "rtl" : "ltr"))); 
		add(new Label("pageTitle", appName));
		add(new HeaderPanel("header", appName));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		response.render(JavaScriptHeaderItem.forReference(Application.get()
				.getJavaScriptLibrarySettings().getJQueryReference()));
		response.render(JavaScriptHeaderItem.forUrl("js/jquery-ui-1.9.0.custom.min.js", "jquery-ui"));
		super.renderHead(response);
	}
}
