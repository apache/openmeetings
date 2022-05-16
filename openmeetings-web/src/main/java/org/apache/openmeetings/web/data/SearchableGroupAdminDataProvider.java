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
package org.apache.openmeetings.web.data;

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Iterator;

import org.apache.openmeetings.db.dao.IGroupAdminDataProviderDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;

public class SearchableGroupAdminDataProvider<T extends IDataProviderEntity> extends SearchableDataProvider<T> {
	private static final long serialVersionUID = 1L;

	public SearchableGroupAdminDataProvider(Class<? extends IGroupAdminDataProviderDao<T>> c) {
		super(c);
	}

	@Override
	protected IGroupAdminDataProviderDao<T> getDao() {
		return (IGroupAdminDataProviderDao<T>)super.getDao();
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		return (hasGroupAdminLevel(getRights())
				? getDao().adminGet(search, getUserId(), first, count, getSort())
				: getDao().adminGet(search, first, count, getSort())).iterator();
	}

	@Override
	public long size() {
		return hasGroupAdminLevel(getRights())
				? getDao().adminCount(search, getUserId())
				: getDao().adminCount(search);
	}
}
