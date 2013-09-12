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

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.web.user.rooms.RoomPanel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SwfPage extends BaseNotInitedPage {
	private static final long serialVersionUID = 6492618860620779445L;

	public SwfPage() {
		this(new PageParameters());
	}

	public SwfPage(PageParameters pp) {
		add(new Label("titleAppName", getBean(ConfigurationDao.class).getAppName()));
		add(new RoomPanel("room", pp));
	}
	
}
