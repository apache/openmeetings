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
package org.apache.openmeetings.web.components.user.dashboard;

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class RssWidgetView extends WidgetView {
	private static final long serialVersionUID = -6257866996099503210L;

	public RssWidgetView(String id, Model<Widget> model) {
		super(id, model);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		//FIXME refactor this link to XML resoursce should be generated here
		/*
		response.render(OnDomReadyHeaderItem.forScript("getNews('"
				+ cfgDao.getConfValue(RSS_FEED1_KEY, String.class, "") + ","
				+ cfgDao.getConfValue(RSS_FEED2_KEY, String.class, "") + "');"
				)) ;
				*/
	}
}
