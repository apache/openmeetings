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
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.web.app.WebSession;

public abstract class BaseInitedPage extends BasePage {
	private static final long serialVersionUID = 5716753033219700254L;

	@Override
	protected String getApplicationName() {
		return getBean(ConfigurationDao.class).getAppName();
	}
	
	@Override
	protected FieldLanguage getLanguage() {
		return WebSession.getLanguageObj();
	}
}
