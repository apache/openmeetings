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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.util.OmFileHelper.getUserDashboard;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.slf4j.Logger;
import org.wicketstuff.dashboard.Dashboard;
import org.wicketstuff.dashboard.DashboardPersister;
import org.wicketstuff.dashboard.WidgetComparator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class UserDashboardPersister implements DashboardPersister {
	private static final Logger log = getLogger(UserDashboardPersister.class, webAppRootKey);
	
	@Override
	public Dashboard load() {
		return new XStreamDashboardPersister().load();
	}

	@Override
	public void save(Dashboard dashboard) {
		new XStreamDashboardPersister().save(dashboard);
	}

	public static class XStreamDashboardPersister implements DashboardPersister {
		private File file;
		private XStream xstream;
		
		public XStreamDashboardPersister() {
			this.file = getUserDashboard(getUserId());
			
	        xstream = new XStream(new DomDriver(StandardCharsets.UTF_8.name()));
	        xstream.setMode(XStream.NO_REFERENCES);
	        xstream.alias("dashboard", UserDashboard.class);
		}
		
		@Override
		public Dashboard load() {
			if (!file.exists() || !file.isFile()) {
				return null;
			}
			
			try {
				return (Dashboard) xstream.fromXML(new FileInputStream(file));
			} catch (Exception e) {
				log.error("Error while loading dashboard", e);
				return null;
			}
		}

		@Override
		public void save(Dashboard dashboard) {
			// sort widgets
			Collections.sort(dashboard.getWidgets(), new WidgetComparator());
			
			try {
				xstream.toXML(dashboard, new FileOutputStream(file));
			} catch (Exception e) {
				log.error("Error while saving dashboard", e);
			}
		}
	}
}
