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
package org.apache.openmeetings.web.user.profile;

import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.GeneralUserForm;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class UserForm extends GeneralUserForm {
	private static final long serialVersionUID = 1305752513494262480L;
	private final PasswordTextField confirmPassword;
	
	public UserForm(String id, IModel<User> model) {
		super(id, model, false);
		add(confirmPassword = new PasswordTextField("confirmPassword", new Model<String>()));
		confirmPassword.setRequired(false);
	}
	
	@Override
	protected void onValidate() {
		String pass = getPasswordField().getConvertedInput();
		if (pass != null && !pass.isEmpty() && !pass.equals(confirmPassword.getConvertedInput())) {
			error(WebSession.getString(232));
		}
		super.onValidate();
	}
}
