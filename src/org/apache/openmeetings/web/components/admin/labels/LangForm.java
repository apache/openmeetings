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

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.ConfirmCallListener;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.time.Duration;

/**
 * Modify the language selection, add/delete {@link FieldLanguage}
 * 
 * @author swagner
 * 
 */
public class LangForm extends AdminBaseForm<FieldLanguage> {

	private static final long serialVersionUID = 2837702941211636609L;
	private final WebMarkupContainer listContainer;
	private final LangPanel langPanel;
	private DropDownChoice<Long> languages;

	private List<FieldLanguage> fieldLanguages;

	/**
	 * Get list of language ids for drow down
	 * 
	 * @return
	 */
	private List<Long> getFieldLanguageIds() {
		List<Long> idsList = new ArrayList<Long>();
		for (FieldLanguage fieldLanguage : fieldLanguages) {
			idsList.add(fieldLanguage.getLanguage_id());
		}
		return idsList;
	}

	/**
	 * get name for id for dropdown renderer
	 * 
	 * @param id
	 * @return
	 */
	private String getFieldLanguageLabelById(Long id) {
		for (FieldLanguage language : fieldLanguages) {
			if (id.equals(language.getLanguage_id())) {
				return language.getName();
			}
		}
		throw new RuntimeException("Could not find FieldLanguage for id " + id);
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
			FieldLanguage language, LangPanel langPanel) {

		super(id, new CompoundPropertyModel<FieldLanguage>(language));
		this.listContainer = listContainer;
		this.langPanel = langPanel;
		setOutputMarkupId(true);

		addLanguageDropDown();

		addSaveAndDeleteButtons();

		// addNewLanguagePopUp(langPanel);

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);
	}

	/**
	 * Adds the drop down menu to choose more languages
	 */
	private void addLanguageDropDown() {

		// prepare the list of languages
		FieldLanguageDaoImpl langDao = Application
				.getBean(FieldLanguageDaoImpl.class);
		fieldLanguages = langDao.getLanguages();

		languages = new DropDownChoice<Long>("language_id",
				getFieldLanguageIds(), new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;

					public Object getDisplayValue(Long id) {
						return getFieldLanguageLabelById(id);
					}

					public String getIdValue(Long id, int index) {
						return "" + id;
					}

				});

		languages.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = -2055912815073387536L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});

		add(languages);
	}

	/**
	 * adds the pop up to enter a new language
	 * 
	 * @param langPanel
	 */
	// private void addNewLanguagePopUp(LangPanel langPanel) {
	// langPanel.add(addLangForm.add(
	// new RequiredTextField<String>("name",
	// new PropertyModel<String>(this, "newLanguageName")))
	// .add(new RequiredTextField<String>("iso",
	// new PropertyModel<String>(this, "newLanguageISO"))));
	// }

	/**
	 * Add save and remove buttons and trigger add/delete in DAO
	 */
	private void addSaveAndDeleteButtons() {
		
		add(new AjaxButton("newLangBtn") {
			private static final long serialVersionUID = 5570057276994987132L;
		});

		add(new AjaxButton("deleteLangBtn") {
			private static final long serialVersionUID = -1650946343073068686L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(
						new ConfirmCallListener(833L));
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FieldLanguageDaoImpl langDao = Application
						.getBean(FieldLanguageDaoImpl.class);
				langDao.delete(langPanel.getLanguage());
				fieldLanguages = langDao.getLanguages();
				languages.setChoices(getFieldLanguageIds());
				target.add(languages);
				// FIXME need to force update list container
				target.add(listContainer);
			}
		});

		// add(new AjaxButton("add", this) {
		// private static final long serialVersionUID = -552597041751688740L;
		//
		// @Override
		// public void onSubmit(AjaxRequestTarget target, Form<?> form) {
		// FieldLanguageDaoImpl langDao = Application
		// .getBean(FieldLanguageDaoImpl.class);
		//
		// FieldLanguage fl = new FieldLanguage();
		// fl.setLanguage_id(langDao.getNextAvailableId());
		// fl.setStarttime(new Date());
		// fl.setDeleted(false);
		// fl.setName(langPanel.getNewLanguageName());
		// fl.setRtl(false); // FIXME
		// fl.setCode(langPanel.getNewLanguageISO());
		//
		// try {
		// langDao.updateLanguage(fl);
		// } catch (Exception e) {
		// // TODO add feedback message
		// e.printStackTrace();
		// }
		// fieldLanguages = langDao.getLanguages();
		// languages.setChoices(getFieldLanguageIds());
		// target.add(languages);
		// target.appendJavaScript("$('#addLanguage').dialog('close');");
		// }
		// });
	}

}
