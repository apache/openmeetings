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

import static java.text.DateFormat.SHORT;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import static org.apache.openmeetings.db.util.TimezoneUtil.getTimeZone;

import java.util.regex.Pattern;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.util.string.Strings;

public class FormatHelper {
	/**
	 * taken from BidiUtils
	 *
	 * A regular expression for matching right-to-left language codes. See
	 * {@link #isRtlLanguage} for the design.
	 */
	private static final Pattern RtlLocalesRe = Pattern.compile("^(ar|dv|he|iw|fa|nqo|ps|sd|ug|ur|yi|.*[-_](Arab|Hebr|Thaa|Nkoo|Tfng))"
					+ "(?!.*[-_](Latn|Cyrl)($|-|_))($|-|_)");

	private FormatHelper() {}

	/**
	 * Check if a BCP 47 / III language code indicates an RTL language, i.e.
	 * either: - a language code explicitly specifying one of the right-to-left
	 * scripts, e.g. "az-Arab", or
	 * <p>
	 * - a language code specifying one of the languages normally written in a
	 * right-to-left script, e.g. "fa" (Farsi), except ones explicitly
	 * specifying Latin or Cyrillic script (which are the usual LTR
	 * alternatives).
	 * <p>
	 * The list of right-to-left scripts appears in the 100-199 range in
	 * http://www.unicode.org/iso15924/iso15924-num.html, of which Arabic and
	 * Hebrew are by far the most widely used. We also recognize Thaana, N'Ko,
	 * and Tifinagh, which also have significant modern usage. The rest (Syriac,
	 * Samaritan, Mandaic, etc.) seem to have extremely limited or no modern
	 * usage and are not recognized. The languages usually written in a
	 * right-to-left script are taken as those with Suppress-Script:
	 * Hebr|Arab|Thaa|Nkoo|Tfng in
	 * http://www.iana.org/assignments/language-subtag-registry, as well as
	 * Sindhi (sd) and Uyghur (ug). The presence of other subtags of the
	 * language code, e.g. regions like EG (Egypt), is ignored.
	 *
	 * @param languageString - locale string
	 * @return <code>true</code> in case passed locale is right-to-left
	 */
	public static boolean isRtlLanguage(String languageString) {
		return languageString != null && RtlLocalesRe.matcher(languageString).find();
	}

	public static String formatUser(User u) {
		return formatUser(u, false);
	}

	public static String formatUser(User u, boolean isHTMLEscape) {
		String user = "";
		if (u != null) {
			String email = u.getAddress() == null ? "" : u.getAddress().getEmail();
			if (Strings.isEmpty(u.getFirstname()) && Strings.isEmpty(u.getLastname())) {
				user = email;
			} else {
				user = String.format("\"%s %s\" <%s>", u.getFirstname(), u.getLastname(), email);
			}
			user = isHTMLEscape ? escapeHtml4(user) : user;
		}
		return user;
	}

	public static FastDateFormat getDateFormat(User u) {
		return FastDateFormat.getDateInstance(SHORT, getTimeZone(u), LocaleHelper.getLocale(u));
	}

	public static FastDateFormat getTimeFormat(User u) {
		return FastDateFormat.getTimeInstance(SHORT, getTimeZone(u), LocaleHelper.getLocale(u));
	}

	public static FastDateFormat getDateTimeFormat(User u) {
		return FastDateFormat.getDateTimeInstance(SHORT, SHORT, getTimeZone(u), LocaleHelper.getLocale(u));
	}
}
