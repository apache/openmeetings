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
package org.apache.openmeetings.core.rss;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.openjson.JSONArray;

public class LoadAtomRssFeed {
	private static final Logger log = LoggerFactory.getLogger(LoadAtomRssFeed.class);
	private static JSONArray rss = new JSONArray();

	private LoadAtomRssFeed() {}

	public static HttpURLConnection getFeedConnection(String urlStr) throws IOException {
		log.trace("getFeedConnection:: {}", urlStr);

		URL url = new URL(urlStr);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.1.4322)");
		conn.setRequestProperty("Referer", "https://openmeetings.apache.org/");
		conn.setUseCaches(false);
		conn.connect();
		return conn;
	}

	public static JSONArray getRss() {
		return rss;
	}

	public static void setRss(JSONArray rss) {
		LoadAtomRssFeed.rss = rss;
	}
}
