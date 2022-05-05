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
package org.apache.openmeetings.db.entity.label;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.Session;

public class OmLanguage implements Serializable {
	private static final long serialVersionUID = 1L;
	private final long id;
	private final Locale locale;
	private final boolean rtl;
	private String tip;
	private char rangeStart = 'A';
	private char rangeEnd = 'Z';

	public OmLanguage(Long id, Locale locale) {
		this.id = id;
		this.locale = locale;
		this.rtl = Session.isRtlLanguage(locale);
	}

	public long getId() {
		return id;
	}

	public Locale getLocale() {
		return locale;
	}

	public boolean isRtl() {
		return rtl;
	}

	public String getTip() {
		return tip;
	}

	public OmLanguage setTip(String tip) {
		this.tip = tip;
		return this;
	}

	public char getRangeStart() {
		return rangeStart;
	}

	public OmLanguage setRangeStart(char rangeStart) {
		this.rangeStart = rangeStart;
		return this;
	}

	public char getRangeEnd() {
		return rangeEnd;
	}

	public OmLanguage setRangeEnd(char rangeEnd) {
		this.rangeEnd = rangeEnd;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OmLanguage [id=");
		builder.append(id);
		builder.append(", locale=");
		builder.append(locale);
		builder.append(", rtl=");
		builder.append(rtl);
		builder.append(", rangeStart=");
		builder.append(rangeStart);
		builder.append(", rangeEnd=");
		builder.append(rangeEnd);
		builder.append("]");
		return builder.toString();
	}
}
