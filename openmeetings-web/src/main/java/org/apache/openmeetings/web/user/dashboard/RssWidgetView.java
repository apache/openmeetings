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

import static org.apache.openmeetings.core.rss.LoadAtomRssFeed.getFeedConnection;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED1;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED2;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.web.WidgetView;

public class RssWidgetView extends WidgetView {
	private static final long serialVersionUID = 1L;
	private final RSSFeedBehavior feed1;
	private final RSSFeedBehavior feed2;

	public RssWidgetView(String id, Model<Widget> model) {
		super(id, model);
		ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
		add(feed1 = new RSSFeedBehavior(cfgDao.getString(CONFIG_DASHBOARD_RSS_FEED1, "")));
		add(feed2 = new RSSFeedBehavior(cfgDao.getString(CONFIG_DASHBOARD_RSS_FEED2, "")));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(RssWidgetView.class, "rss.js")));
		response.render(OnDomReadyHeaderItem.forScript(String.format("loadRssTab('%s', '%s');", feed1.getCallbackUrl(), feed2.getCallbackUrl()))) ;
	}

	static class RSSFeedBehavior extends AbstractAjaxBehavior {
		private static final long serialVersionUID = 1L;
		private String url;

		RSSFeedBehavior(String url) {
			this.url = url;
		}

		@Override
		public void onRequest() {
			ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(new AbstractResourceStream() {
				private static final long serialVersionUID = 1L;
				transient HttpURLConnection con;

				@Override
				public InputStream getInputStream() throws ResourceStreamNotFoundException {
					try {
						con = getFeedConnection(url);
						return con.getInputStream();
					} catch (IOException e) {
						throw new ResourceStreamNotFoundException();
					}
				}

				@Override
				public void close() throws IOException {
					if (con != null) {
						con.disconnect();
					}
				}
			}, "feed");
			handler.setContentDisposition(ContentDisposition.INLINE);
			getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
		}
	}
}
