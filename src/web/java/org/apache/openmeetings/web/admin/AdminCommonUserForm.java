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

package org.apache.openmeetings.web.admin;

import java.util.List;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public abstract class AdminCommonUserForm<T> extends AdminBaseForm<T> {
	private static final long serialVersionUID = -827309847606809118L;

	@SuppressWarnings("unchecked")
	public AdminCommonUserForm(String id, IModel<?> object) {
		super(id, (IModel<T>) object);
	}
	
	public abstract void updateView(AjaxRequestTarget target);

	public abstract void submitView(AjaxRequestTarget target, List<User> usersToAdd);

}
