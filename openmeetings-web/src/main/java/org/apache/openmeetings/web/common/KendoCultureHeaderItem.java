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
package org.apache.openmeetings.web.common;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import com.googlecode.wicket.kendo.ui.KendoCulture;
import com.googlecode.wicket.kendo.ui.resource.KendoCultureResourceReference;

public class KendoCultureHeaderItem {
	private static Set<String> CULTURES = new HashSet<>();

	public static JavaScriptHeaderItem get() {
		if (CULTURES.isEmpty()) {
			for (KendoCulture c : KendoCulture.values()) {
				CULTURES.add(c.toString());
			}
		}
		Locale l = WebSession.get().getLocale();
		String culture = l.toLanguageTag();
		if (!CULTURES.contains(culture)) {
			culture = l.getLanguage();
			if (!CULTURES.contains(culture)) {
				culture = Locale.ENGLISH.getLanguage();
			}
		}
		return JavaScriptHeaderItem.forReference(new KendoCultureResourceReference(culture));
	}
}
