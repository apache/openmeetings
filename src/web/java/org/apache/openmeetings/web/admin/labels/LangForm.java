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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.web.common.ConfirmCallListener;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

/**
 * Modify the language selection, add/delete language
 * 
 * @author solomax, swagner
 * 
 */
public class LangForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private DropDownChoice<Map.Entry<Long, Locale>> languages;

	private List<Map.Entry<Long, Locale>> getLanguages() {
		List<Map.Entry<Long, Locale>> list = new ArrayList<Map.Entry<Long, Locale>>();
		for (Map.Entry<Long, Locale> e : LabelDao.languages.entrySet()) {
			list.add(new AbstractMap.SimpleEntry<Long,Locale>(e.getKey(), e.getValue()));
		}
		return list;
	}
	
	public void updateLanguages(AjaxRequestTarget target) {
		languages.setChoices(getLanguages());
		target.add(languages);
	}

	/**
	 * Render Main
	 * 
	 * @param id
	 * @param listContainer
	 * @param language
	 * @param langPanel
	 */
	public LangForm(String id, final WebMarkupContainer listContainer, final LangPanel langPanel) {
		super(id);
		setOutputMarkupId(true);

		languages = new DropDownChoice<Map.Entry<Long, Locale>>("language"
				, new PropertyModel<Map.Entry<Long, Locale>>(langPanel, "language")
				, getLanguages()
				, new IChoiceRenderer<Map.Entry<Long, Locale>>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(Map.Entry<Long, Locale> object) {
						return object.getValue().getDisplayName();
					}

					@Override
					public String getIdValue(Map.Entry<Long, Locale> object, int index) {
						return "" + object.getKey();
					}
				});
				
		languages.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
		add(languages);

		add(new WebMarkupContainer("deleteLangBtn").add(new AjaxEventBehavior("onclick"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
			}
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				LabelDao.delete(langPanel.language.getValue());
				List<Map.Entry<Long, Locale>> langs = getLanguages();
				langPanel.language = langs.isEmpty() ? null : langs.get(0);
				languages.setChoices(langs);
				target.add(languages, listContainer);
			}
		})); 

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
	}
}
