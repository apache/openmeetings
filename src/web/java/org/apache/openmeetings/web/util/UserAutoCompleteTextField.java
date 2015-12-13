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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.List;
import java.util.Locale;

import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;

import com.googlecode.wicket.jquery.core.renderer.ITextRenderer;
import com.googlecode.wicket.jquery.ui.form.autocomplete.AutoCompleteTextField;

public class UserAutoCompleteTextField extends AutoCompleteTextField<User> {
	private static final long serialVersionUID = 1L;
	private UserTextRenderer renderer;
	private String inputValue = null;
	private IConverter<User> converter = new IConverter<User>() {
		private static final long serialVersionUID = 1L;

		public User convertToObject(String value, Locale locale) {
			if (value != null && value.equals(UserAutoCompleteTextField.this.getModelValue()))  {
				return UserAutoCompleteTextField.this.getModelObject();
			} else {
				return UserChoiceProvider.getUser(value);
			}
		}

		public String convertToString(User value, Locale locale) {
			return UserAutoCompleteTextField.this.renderer.getText(value);
		}

	};

	public UserAutoCompleteTextField(String id, IModel<User> model) {
		super(id, model, new UserTextRenderer());
		this.renderer = (UserTextRenderer)getRenderer();
	}
	
	public UserAutoCompleteTextField(String id) {
		this(id, null);
	}

	public String inputToString() {
		return inputValue;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C> IConverter<C> getConverter(Class<C> type) {
		if (type.isAssignableFrom(User.class)) {
			return (IConverter<C>)converter;
		} else {
			return super.getConverter(type);
		}
	}
	
	@Override
	protected List<User> getChoices(String input) {
		inputValue = input;
		return getBean(UserDao.class).get(input, 0, 10, null, getUserId());
	}
	
	private static class UserTextRenderer implements ITextRenderer<User> {
		private static final long serialVersionUID = 1L;

		public String getText(User u) {
			return u == null ? "" : String.format("%s %s <%s>", u.getFirstname(), u.getLastname(), u.getAdresses().getEmail());
		}
		
		public String getText(User u, String expression) {
			return getText(u);
		}
	}
}
