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

import java.util.Iterator;

import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.persistence.beans.OmEntity;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class OmDataProvider<T extends OmEntity> implements IDataProvider<T> {
	private static final long serialVersionUID = 4325721185888905204L;
	protected Class<? extends OmDAO<T>> clazz;
	
	public OmDataProvider(Class<? extends OmDAO<T>> c) {
		this.clazz = c;
	}
	
	public void detach() {
		// does nothing
	}

	public Iterator<? extends T> iterator(long first, long count) {
		return Application.getBean(clazz).get((int)first, (int)count).iterator();
	}

	public long size() {
		return Application.getBean(clazz).count();
	}

	public IModel<T> model(T object) {
		return new CompoundPropertyModel<T>(object);
	}

}
