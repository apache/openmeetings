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
package org.apache.openmeetings.core.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getMinPasswdLength;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isPwdCheckDigit;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isPwdCheckSpecial;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isPwdCheckUpper;

import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrongPasswordValidator implements IValidator<String> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(StrongPasswordValidator.class);
	private final boolean web;
	private User u;

	public StrongPasswordValidator(final User u) {
		this(true, u);
	}

	public StrongPasswordValidator(final boolean web, final User u) {
		this.web = web;
		this.u = u;
	}

	private static boolean noDigit(String password) {
		return password == null || (isPwdCheckDigit() && !password.matches(".*\\d+.*"));
	}

	private static boolean noSymbol(String password) {
		return password == null || (isPwdCheckSpecial() && !password.matches(".*[!@#$%^&*\\]\\[]+.*"));
	}

	private static boolean noUpperCase(String password) {
		return password == null || (isPwdCheckUpper() && password.equals(password.toLowerCase(Locale.ROOT)));
	}

	private static boolean noLowerCase(String password) {
		return password == null || password.equals(password.toUpperCase(Locale.ROOT));
	}

	private static boolean badLength(String password) {
		return password == null || password.length() < getMinPasswdLength();
	}

	private static boolean checkWord(String password, String word) {
		if (Strings.isEmpty(password) || Strings.isEmpty(word) || word.length() < 3) {
			return false;
		}
		for (int i = 0; i < word.length() - 3; ++i) {
			String substr = word.toLowerCase(Locale.ROOT).substring(i, i + 3);
			if (password.toLowerCase(Locale.ROOT).indexOf(substr) > -1) {
				return true;
			}
		}
		return false;
	}

	private boolean hasStopWords(String password) {
		if (checkWord(password, u.getLogin())) {
			return true;
		}
		if (u.getAddress() != null) {
			String email = u.getAddress().getEmail();
			if (!Strings.isEmpty(email)) {
				for (String part : email.split("[.@]")) {
					if (checkWord(password, part)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void error(IValidatable<String> pass, String key) {
		error(pass, key, null);
	}

	private void error(IValidatable<String> pass, String key, Map<String, Object> params) {
		if (web) {
			ValidationError err = new ValidationError().addKey(key);
			if (params != null) {
				err.setVariables(params);
			}
			pass.error(err);
		} else {
			String msg = LabelDao.getString(key, 1L);
			if (params != null && !params.isEmpty() && !Strings.isEmpty(msg)) {
				for (Map.Entry<String, Object> e : params.entrySet()) {
					msg = msg.replace(String.format("${%s}", e.getKey()), "" + e.getValue());
				}
			}
			log.warn(msg);
			pass.error(new ValidationError(msg));
		}
	}

	@Override
	public void validate(IValidatable<String> pass) {
		if (badLength(pass.getValue())) {
			error(pass, "bad.password.short", Map.of("0", getMinPasswdLength()));
		}
		if (noLowerCase(pass.getValue())) {
			error(pass, "bad.password.lower");
		}
		if (noUpperCase(pass.getValue())) {
			error(pass, "bad.password.upper");
		}
		if (noDigit(pass.getValue())) {
			error(pass, "bad.password.digit");
		}
		if (noSymbol(pass.getValue())) {
			error(pass, "bad.password.special");
		}
		if (hasStopWords(pass.getValue())) {
			error(pass, "bad.password.stop");
		}
	}

	public void setUser(User u) {
		this.u = u;
	}
}
