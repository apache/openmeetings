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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Iterator;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

/**
 * provides function to fill paging tables
 * 
 * @author swagner
 * 
 * @param <T>
 */
public class SearchableDataProvider<T extends IDataProviderEntity> extends SortableDataProvider<T, String> {
	private static final long serialVersionUID = 4325721185888905204L;
	protected Class<? extends IDataProviderDao<T>> clazz;
	protected String search = null;
	
	public SearchableDataProvider(Class<? extends IDataProviderDao<T>> c) {
		this.clazz = c;
	}
	
	public void detach() {
		// does nothing
	}

	protected IDataProviderDao<T> getDao() {
		return getBean(clazz);
	}
	
	protected String getSortStr() {
		String result = null;
		if (getSort() != null) {
			result = getSort().getProperty() + " " + (getSort().isAscending() ? "ASC" : "DESC");
		}
		return result;
	}
	
	public Iterator<? extends T> iterator(long first, long count) {
		return (search == null && getSort() == null
			? getDao().get((int)first, (int)count)
			: getDao().get(search, (int)first, (int)count, getSortStr())).iterator();
	}

	public long size() {
		return search == null ? getDao().count() : getDao().count(search);
	}

	public IModel<T> model(T object) {
		return new CompoundPropertyModel<T>(object);
	}

	public void setSearch(String search) {
		if (search != null && !search.trim().isEmpty()) {
			this.search = search.trim();
		} else {
			this.search = null;
		}
	}
	
	public String getSearch() {
		return search;
	}
}
