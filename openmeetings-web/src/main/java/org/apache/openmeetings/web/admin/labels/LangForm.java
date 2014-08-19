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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.web.common.ConfirmCallListener;
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
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * Modify the language selection, add/delete {@link FieldLanguage}
 * 
 * @author swagner
 * 
 */
public class LangForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(LangForm.class, webAppRootKey);
	private DropDownChoice<FieldLanguage> languages;

	public void updateLanguages(AjaxRequestTarget target) {
		FieldLanguageDao langDao = getBean(FieldLanguageDao.class);
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
	public LangForm(String id, final WebMarkupContainer listContainer, final LangPanel langPanel) {
		super(id);
		setOutputMarkupId(true);

		FieldLanguageDao langDao = getBean(FieldLanguageDao.class);
		
		languages = new DropDownChoice<FieldLanguage>("language"
				, new PropertyModel<FieldLanguage>(langPanel, "language")
				, langDao.getLanguages()
				, new ChoiceRenderer<FieldLanguage>("name", "id"));
				
		languages.add(new AjaxFormComponentUpdatingBehavior("onchange") {
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
				langPanel.language.setDeleted(true);
				FieldLanguageDao langDao = getBean(FieldLanguageDao.class);
				try {
					langDao.update(langPanel.language);
				} catch (Exception e) {
					// TODO add feedback message
					log.error("Error", e);
				}
				languages.setChoices(langDao.getLanguages());
				target.add(languages);
				// FIXME need to force update list container
				target.add(listContainer);
			}
		})); 

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}
}
