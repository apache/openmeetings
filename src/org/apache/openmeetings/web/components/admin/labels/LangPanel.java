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

import org.apache.openmeetings.data.basic.FieldLanguageDao;
import org.apache.openmeetings.data.basic.FieldValueDao;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.SearchableDataView;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.data.OrderByBorder;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

/**
 * Language Editor, add/insert/update {@link Fieldlanguagesvalues} and
 * add/delete {@link FieldLanguage} contains several Forms and one list
 * 
 * @author solomax, swagner
 * 
 */
public class LangPanel extends AdminPanel {
	private static final long serialVersionUID = 5904180813198016592L;

	FieldLanguage language;
	final WebMarkupContainer listContainer;
	private LangForm langForm;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("labelsInit();");
	}

	@SuppressWarnings("unchecked")
	public LangPanel(String id) {
		super(id);
		FieldLanguageDao langDao = Application
				.getBean(FieldLanguageDao.class);
		language = langDao.getFieldLanguageById(1L);

		Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
		flv.setLanguage_id(language.getLanguage_id());
		final LabelsForm form = new LabelsForm("form", this, flv);
		form.showNewRecord();
		add(form);

		final SearchableDataView<Fieldvalues> dataView = new SearchableDataView<Fieldvalues>(
				"langList"
				, new SearchableDataProvider<Fieldvalues>(FieldValueDao.class) {
					private static final long serialVersionUID = -6822789354860988626L;

					@Override
					public long size() {
						return search == null
								? Application.getBean(FieldValueDao.class).count()
								: Application.getBean(FieldValueDao.class).count(language.getLanguage_id(), search);
					}
					
					public Iterator<? extends Fieldvalues> iterator(long first, long count) {
						return (search == null && getSort() == null
								? Application.getBean(FieldValueDao.class).get(language.getLanguage_id(), (int)first, (int)count)
								: Application.getBean(FieldValueDao.class).get(language.getLanguage_id(), search, (int)first, (int)count, getSortStr())).iterator();
					}
				}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<Fieldvalues> item) {
				final Fieldvalues fv = item.getModelObject();
				item.add(new Label("lblId", "" + fv.getFieldvalues_id()));
				item.add(new Label("name", fv.getName()));
				item.add(new Label("value", fv.getFieldlanguagesvalue() != null ? fv.getFieldlanguagesvalue().getValue() : null));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(fv.getFieldlanguagesvalue());
						form.hideNewRecord();
						target.add(form);
						target.appendJavaScript("labelsInit();");
					}
				});
				item.add(AttributeModifier.append("class", "clickable "
						+ ((item.getIndex() % 2 == 1) ? "even" : "odd")));
			}
		};

		listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		DataViewContainer<Fieldvalues> container = new DataViewContainer<Fieldvalues>(listContainer, dataView);
		container.setLinks(new OrderByBorder<Fieldvalues>("orderById", "fieldvalues.fieldvalues_id", container)
				, new OrderByBorder<Fieldvalues>("orderByName", "fieldvalues.name", container)
				, new OrderByBorder<Fieldvalues>("orderByValue", "value", container));
		add(container.orderLinks);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				dataView.modelChanging();
				target.add(listContainer);
			}
		});
		langForm = new LangForm("langForm", listContainer, this);
		add(langForm);
		add(new AddLanguageForm("addLangForm", this));
	}

	public LangForm getLangForm() {
		return langForm;
	}
}
