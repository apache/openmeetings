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

import org.apache.openmeetings.data.basic.FieldLanguageDaoImpl;
import org.apache.openmeetings.persistence.beans.lang.FieldLanguage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class AddLanguageForm extends Form<Void> {
	private static final long serialVersionUID = 8743289610974962636L;
	private String newLanguageName;
	private String newLanguageISO;
	
	public AddLanguageForm(String id) {
		super(id);
		
		add(new RequiredTextField<String>("name", new PropertyModel<String>(this, "newLanguageName")));
		add(new RequiredTextField<String>("iso", new PropertyModel<String>(this, "newLanguageISO")));
		
		add(new AjaxButton("add", Model.of(WebSession.getString(366L)), this) {
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
				/* FIXME
				languages.setChoices(langDao.getLanguages());
				target.add(languages);
				*/
				target.appendJavaScript("$('#addLanguage').dialog('close');");
			}
		});
	}
}
