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
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import ro.fortsoft.wicket.dashboard.Dashboard;
import ro.fortsoft.wicket.dashboard.DashboardPersister;
import ro.fortsoft.wicket.dashboard.XStreamDashboardPersister;

public class UserDashboardPersister implements DashboardPersister {
	public Dashboard load() {
		return new XStreamDashboardPersister(getUserDashboard(getUserId())).load();
	}

	public void save(Dashboard dashboard) {
		new XStreamDashboardPersister(getUserDashboard(getUserId())).save(dashboard);
	}

}
