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
package org.apache.openmeetings.web.components.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * provides basic functionality to insert, update, remove, refresh record in
 * admin section
 * 
 * @author swagner
 * 
 * @param <T>
 */
public class AdminBaseForm<T> extends Form<T> {

	private static final long serialVersionUID = 1L;

	public AdminBaseForm(String id, IModel<T> object) {
		super(id, object);
		add(new AdminBaseFormPanel<T>("buttons", this));
	}

}
