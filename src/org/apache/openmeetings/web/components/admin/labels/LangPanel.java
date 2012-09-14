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

import java.util.Date;
import java.util.Iterator;

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.data.basic.FieldLanguagesValuesDAO;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class LangPanel extends AdminPanel {
	private static final long serialVersionUID = 5904180813198016592L;
	private FieldLanguage language;
	private String newLanguageName;
	private String newLanguageISO;
	
	public LangPanel(String id) {
		super(id);
		FieldLanguageDaoImpl langDao = Application.getBean(FieldLanguageDaoImpl.class);
		language = langDao.getFieldLanguageById(1L);
		
		final AdminBaseForm<Fieldlanguagesvalues> form = new AdminBaseForm<Fieldlanguagesvalues>("form", new CompoundPropertyModel<Fieldlanguagesvalues>(new Fieldlanguagesvalues())) {
			private static final long serialVersionUID = -1309878909524329047L;

			@Override
			protected void onNewSubmit(AjaxRequestTarget target, Form<?> f) {
				this.setModelObject(new Fieldlanguagesvalues());
				target.add(this);
			}
			
			@Override
			protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
				Fieldlanguagesvalues flv = getModelObject();
				if (flv.getFieldlanguagesvalues_id() != null) {
					flv = Application.getBean(FieldLanguagesValuesDAO.class).get(getModelObject().getFieldlanguagesvalues_id());
				} else {
					flv = new Fieldlanguagesvalues();
				}
				this.setModelObject(flv);
				target.add(this);
			}
			
			@Override
			protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
				Application.getBean(FieldLanguagesValuesDAO.class).update(getModelObject());
				//FIXME reload
			}
			
			@Override
			protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
				Application.getBean(FieldLanguagesValuesDAO.class).delete(getModelObject());
				//FIXME reload
			}
		};
		form.add(new Label("fieldvalues.fieldvalues_id"));
		form.add(new TextField<String>("fieldvalues.name"));
		form.add(new TextArea<String>("value"));
        add(form);

		final DataView<Fieldlanguagesvalues> dataView = new DataView<Fieldlanguagesvalues>("langList"
				, new OmDataProvider<Fieldlanguagesvalues>(FieldLanguagesValuesDAO.class){
			private static final long serialVersionUID = -6822789354860988626L;

			public Iterator<? extends Fieldlanguagesvalues> iterator(long first, long count) {
				return Application.getBean(FieldLanguagesValuesDAO.class).get(language.getLanguage_id(), (int)first, (int)count).iterator();
			}
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Fieldlanguagesvalues> item) {
				final Fieldlanguagesvalues flv = item.getModelObject();
				item.add(new Label("lblId", "" + flv.getFieldvalues_id()));
				item.add(new Label("name", flv.getFieldvalues().getName()));
				item.add(new Label("value", flv.getValue()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(flv);
						form.hideNewRecord();
						target.add(form);
					}
				});
			}
		};
		
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dataView.modelChanging();
				target.add(listContainer);
			}
		});
		
		final Form<Void> f = new Form<Void>("langForm");
		final DropDownChoice<FieldLanguage> languages = new DropDownChoice<FieldLanguage>("language"
			, new PropertyModel<FieldLanguage>(this, "language")
			, langDao.getLanguages()
			, new ChoiceRenderer<FieldLanguage>("name", "language_id"));
		
		languages.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = -2055912815073387536L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					target.add(listContainer);
				}
			});
		f.add(languages.setOutputMarkupId(true));
		add(f.setOutputMarkupId(true));

		add(new WebMarkupContainer("deleteLngBtn").add(new AjaxEventBehavior("onclick"){
			private static final long serialVersionUID = -1650946343073068686L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				language.setDeleted(true);
				FieldLanguageDaoImpl langDao = Application.getBean(FieldLanguageDaoImpl.class);
				try {
					langDao.updateLanguage(language);
				} catch (Exception e) {
					// TODO add feedback message
					e.printStackTrace();
				}
				languages.setChoices(langDao.getLanguages());
				target.add(languages);
				//FIXME need to forse update list container
				target.add(listContainer);
			}
		})); 
		Form<Void> addLangForm = new Form<Void>("addLangForm");
		addLangForm.add(new AjaxButton("add", Model.of(WebSession.getString(366L)), addLangForm) {
			private static final long serialVersionUID = -552597041751688740L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FieldLanguageDaoImpl langDao = Application.getBean(FieldLanguageDaoImpl.class);
				
				FieldLanguage fl = new FieldLanguage();
				fl.setLanguage_id(langDao.getNextAvailableId());
				fl.setStarttime(new Date());
				fl.setDeleted(false);
				fl.setName(newLanguageName);
				fl.setRtl(false); //FIXME
				fl.setCode(newLanguageISO);
				
				try {
					langDao.updateLanguage(fl);
				} catch (Exception e) {
					// TODO add feedback message
					e.printStackTrace();
				}
				languages.setChoices(langDao.getLanguages());
				target.add(languages);
				target.appendJavaScript("$('#addLanguage').dialog('close');");
			}
		});
		add(addLangForm
			.add(new RequiredTextField<String>("name", new PropertyModel<String>(this, "newLanguageName")))
			.add(new RequiredTextField<String>("iso", new PropertyModel<String>(this, "newLanguageISO")))
		);
	}
}
