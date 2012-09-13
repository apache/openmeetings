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

import org.apache.openmeetings.data.basic.Fieldmanagment;
import org.apache.openmeetings.persistence.beans.lang.Fieldlanguagesvalues;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class LangPanel extends AdminPanel {
	private static final long serialVersionUID = 5904180813198016592L;
	long language = 1;
	
	public LangPanel(String id) {
		super(id);

		DataView<Fieldlanguagesvalues> dataView = new DataView<Fieldlanguagesvalues>("langList", new IDataProvider<Fieldlanguagesvalues>(){
			private static final long serialVersionUID = -6822789354860988626L;

			public void detach() {
				//empty
			}

			public Iterator<? extends Fieldlanguagesvalues> iterator(long first, long count) {
				return Application.getBean(Fieldmanagment.class).getMixedFieldValuesList(language, (int)first, (int)count).iterator();
			}

			public long size() {
				return Application.getBean(Fieldmanagment.class).getNextFieldvaluesId() - 1; //FIXME need to be generalized
			}

			public IModel<Fieldlanguagesvalues> model(Fieldlanguagesvalues object) {
				return new CompoundPropertyModel<Fieldlanguagesvalues>(object);
			}
			
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Fieldlanguagesvalues> item) {
				final Fieldlanguagesvalues flv = item.getModelObject();
				item.add(new Label("lblId", "" + flv.getFieldlanguagesvalues_id()));
				item.add(new Label("name", flv.getFieldvalues().getName()));
				item.add(new Label("value", flv.getValue()));
				/*
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						//form.setModelObject(u);
						//target.add(form);
					}
				});
				*/
			}
		};
		
		final WebMarkupContainer langListContainer = new WebMarkupContainer("langListContainer");
		add(langListContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(langListContainer);
			}
		});
		
	}
}
