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
package org.apache.openmeetings.web.admin.labels;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
import org.apache.wicket.model.PropertyModel;

/**
 * Modify the language selection, add/delete language
 *
 * @author swagner
 *
 */
public class LangForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private DropDownChoice<Map.Entry<Long, Locale>> languages;

	/**
	 * Render Main
	 *
	 * @param id - id of this form
	 * @param listContainer - container holds list of labels
	 * @param langPanel - language panel
	 */
	public LangForm(String id, final WebMarkupContainer listContainer, final LangPanel langPanel) {
		super(id);
		setOutputMarkupId(true);

		languages = new DropDownChoice<>("language"
				, new PropertyModel<>(langPanel, "language")
				, getLanguages()
				, new LambdaChoiceRenderer<>(e -> e.getValue().getDisplayName(), e -> "" + e.getKey()));

		languages.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
		add(languages);
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ofSeconds(1)));
	}

	static List<Map.Entry<Long, Locale>> getLanguages() {
		List<Map.Entry<Long, Locale>> list = new ArrayList<>();
		for (Map.Entry<Long, Locale> e : LabelDao.getLanguages()) {
			list.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
		}
		return list;
	}

	public void updateLanguages(AjaxRequestTarget target) {
		languages.setChoices(getLanguages());
		target.add(languages);
	}
}
