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
package org.apache.openmeetings.db.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.user.User;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class LocaleHelper {
	private static final Logger log = Red5LoggerFactory.getLogger(LocaleHelper.class, getWebAppRootKey());

	private LocaleHelper() {}

	public static List<String> getCountries() {
		return Arrays.asList(Locale.getISOCountries());
	}

	public static String getCountryName(String code, Locale l) {
		return new Locale.Builder().setRegion(code).build().getDisplayCountry(l);
	}

	public static String getCountryName(String code) {
		return new Locale.Builder().setRegion(code).build().getDisplayCountry();
	}

	public static String validateCountry(String _code) {
		List<String> list = getCountries();
		Set<String> countries = new HashSet<>(list);
		String code = _code == null ? "" : _code.toUpperCase(Locale.ROOT);
		if (!countries.contains(code)) {
			String newCountry = list.get(0);
			log.warn("Invalid country found: {}, will be replaced with: {}", code, newCountry);
			code = newCountry;
		}
		return code;
	}

	public static Locale getLocale(Long langId) {
		return langId == 3 ? Locale.GERMANY : LabelDao.getLocale(langId);
	}

	public static Locale getLocale(User u) {
		Locale locale = getLocale(u.getLanguageId());
		try {
			Locale.Builder builder = new Locale.Builder().setLanguage(locale.getLanguage());
			if (u.getAddress() != null && u.getAddress().getCountry() != null) {
				builder.setRegion(u.getAddress().getCountry());
			}
			locale = builder.build();
		} catch (Exception e) {
			log.error("Unexpected Error while constructing locale for the user", e.getMessage());
		}
		return locale;
	}
}
