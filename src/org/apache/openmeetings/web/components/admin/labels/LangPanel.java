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

import java.util.Iterator;

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.data.basic.FieldLanguagesValuesDAO;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 * Language Editor, add/insert/update {@link Fieldlanguagesvalues} and
 * add/delete {@link FieldLanguage} contains several Forms and one list
 * 
 * @author solomax, swagner
 * 
 */
public class LangPanel extends AdminPanel {
	private static final long serialVersionUID = 5904180813198016592L;

	private FieldLanguage language;
	private String newLanguageName;
	private String newLanguageISO;

	public FieldLanguage getLanguage() {
		return language;
	}

	public void setLanguage(FieldLanguage language) {
		this.language = language;
	}

	public String getNewLanguageName() {
		return newLanguageName;
	}

	public void setNewLanguageName(String newLanguageName) {
		this.newLanguageName = newLanguageName;
	}

	public String getNewLanguageISO() {
		return newLanguageISO;
	}

	public void setNewLanguageISO(String newLanguageISO) {
		this.newLanguageISO = newLanguageISO;
	}

	public LangPanel(String id) {
		super(id);
		FieldLanguageDaoImpl langDao = Application
				.getBean(FieldLanguageDaoImpl.class);
		language = langDao.getFieldLanguageById(1L);

		final LabelsForm form = new LabelsForm("form",
				new Fieldlanguagesvalues());
		add(form);

		final DataView<Fieldlanguagesvalues> dataView = new DataView<Fieldlanguagesvalues>(
				"langList", new OmDataProvider<Fieldlanguagesvalues>(
						FieldLanguagesValuesDAO.class) {
					private static final long serialVersionUID = -6822789354860988626L;

					public Iterator<? extends Fieldlanguagesvalues> iterator(
							long first, long count) {
						return Application
								.getBean(FieldLanguagesValuesDAO.class)
								.get(language.getLanguage_id(), (int) first,
										(int) count).iterator();
					}
				}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<Fieldlanguagesvalues> item) {
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
				item.add(AttributeModifier.append("class", "clickable "
						+ ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};

		final WebMarkupContainer listContainer = new WebMarkupContainer(
				"listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dataView.modelChanging();
				target.add(listContainer);
			}
		});
		add(new LangForm("langForm", listContainer, language, this));

	}

}
