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

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.util.string.StringValue;

public class SwfPage extends WebPage {
	private static final long serialVersionUID = 6492618860620779445L;
	private final String swf;
	private final Url url;

	public SwfPage() {
		this(new PageParameters());
	}

	public SwfPage(PageParameters pp) {
		add(new Label("titleAppName", getBean(ConfigurationDao.class).getAppName()));
		StringValue swfVal = pp.get("swf");
		swf = swfVal.isEmpty() ? "main.as3.swf11.swf" : swfVal.toString();
		url = new PageParametersEncoder().encodePageParameters(pp);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forScript(String.format("var swfurl = \"%1$s?%2$s\";", swf, url), "swfurl"));
	}
}
