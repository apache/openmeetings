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

import static org.apache.openmeetings.db.util.LocaleHelper.getCountries;
import static org.apache.openmeetings.db.util.LocaleHelper.getCountryName;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.model.IModel;

public class CountryDropDown extends DropDownChoice<String>  {
	private static final long serialVersionUID = 1L;

	public CountryDropDown(String id) {
		this(id, null);
	}

	public CountryDropDown(String id, IModel<String> model) {
		super(id);
		setDefaultModel(model);
		List<String> countries = new ArrayList<>(getCountries());
		countries.sort((c1, c2) -> getCountryName(c1, getLocale()).compareTo(getCountryName(c2, getLocale())));
		setChoices(countries);
		setChoiceRenderer(new LambdaChoiceRenderer<>(code -> getCountryName(code, getLocale()), code -> code));
	}
}
