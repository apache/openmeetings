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

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.label.StringLabel;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;


/**
 * Add/edit/delete {@link StringLabel}
 *
 * @author solomax, swagner
 *
 */
public class LabelsForm extends AdminBaseForm<StringLabel> {
	private static final long serialVersionUID = 1L;
	private LangPanel panel;
	private String key, value;

	public LabelsForm(String id, LangPanel panel, StringLabel label) {
		super(id, new CompoundPropertyModel<>(label));
		this.panel = panel;
		key = label.getKey();
		value = label.getValue();

		add(new RequiredTextField<String>("key"));
		add(new TextArea<String>("value"));
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> f) {
		key = null;
		value = null;
		setModelObject(new StringLabel(key, value));
		target.add(this);
		reinitJs(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new StringLabel(key, value));
		target.add(this);
		reinitJs(target);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		try {
			LabelDao.update(panel.language.getValue(), getModelObject());
		} catch (Exception e) {
			error("Unexpected error while saving label:" + e.getMessage());
		}
		setNewVisible(false);
		target.add(panel.listContainer);
		reinitJs(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		try {
			LabelDao.delete(panel.language.getValue(), getModelObject());
		} catch (Exception e) {
			error("Unexpected error while deleting label:" + e.getMessage());
		}
		target.add(panel.listContainer);
		reinitJs(target);
	}
}
