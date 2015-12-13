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
package org.apache.openmeetings.web.user.record;

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import wicketdnd.theme.WindowsTheme;

public class RecordingPanel extends Panel {
	private static final long serialVersionUID = 1L;
	protected final WebMarkupContainer item = new WebMarkupContainer("item");

	public RecordingPanel(String id, final IModel<FlvRecording> model) {
		super(id, model);
		FlvRecording r = model.getObject();
		add(new WindowsTheme());
		item.add(r.isFolder() ? new AjaxEditableLabel<String>("name", Model.of(model.getObject().getFileName())) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String getLabelAjaxEvent() {
				return "dblClick";
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				FlvRecording r = model.getObject();
				r.setFileName(getEditor().getModelObject());
				getBean(FlvRecordingDao.class).update(r);
			}
			
			@Override
			public void onEdit(AjaxRequestTarget target) {
				super.onEdit(target);
			}
		} : new Label("name", r.getFileName()));
		add(item.setOutputMarkupId(true));
	}
}
