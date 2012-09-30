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
package org.apache.openmeetings.web.components.admin.labels;

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.ConfirmCallListener;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.time.Duration;

/**
 * Modify the language selection, add/delete {@link FieldLanguage}
 * 
 * @author swagner
 * 
 */
public class LangForm extends Form<Void> {

	private static final long serialVersionUID = 2837702941211636609L;
	private DropDownChoice<FieldLanguage> languages;

	public void updateLanguages(AjaxRequestTarget target) {
		FieldLanguageDaoImpl langDao = Application
				.getBean(FieldLanguageDaoImpl.class);
		languages.setChoices(langDao.getLanguages());
		// add(languages);
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
	public LangForm(String id, final WebMarkupContainer listContainer,
			final LangPanel langPanel) {
		super(id);
		setOutputMarkupId(true);

		FieldLanguageDaoImpl langDao = Application
				.getBean(FieldLanguageDaoImpl.class);
		
		languages = new DropDownChoice<FieldLanguage>("language"
				, new PropertyModel<FieldLanguage>(langPanel, "language")
				, langDao.getLanguages()
				, new ChoiceRenderer<FieldLanguage>("name", "language_id"));
				
		languages.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = -2055912815073387536L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
		add(languages);

		add(new WebMarkupContainer("deleteLangBtn").add(new AjaxEventBehavior("onclick"){
			private static final long serialVersionUID = -1650946343073068686L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
			}
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				langPanel.language.setDeleted(true);
				FieldLanguageDaoImpl langDao = Application.getBean(FieldLanguageDaoImpl.class);
				try {
					langDao.updateLanguage(langPanel.language);
				} catch (Exception e) {
					// TODO add feedback message
					e.printStackTrace();
				}
				languages.setChoices(langDao.getLanguages());
				target.add(languages);
				// FIXME need to force update list container
				target.add(listContainer);
			}
		})); 

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);
	}
}
