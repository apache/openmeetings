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

import java.util.Date;

import org.apache.openmeetings.db.dao.label.FieldLanguageDao;
import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
/**
 * 
 * @author solomax, swagner
 * 
 */
public class AddLanguageForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(AddLanguageForm.class, webAppRootKey);
	private String newLanguageName;
	private String newLanguageISO;
	
	public AddLanguageForm(String id, final LangPanel langPanel) {
		super(id);
		
		add(new RequiredTextField<String>("name", new PropertyModel<String>(this, "newLanguageName")));
		add(new RequiredTextField<String>("iso", new PropertyModel<String>(this, "newLanguageISO")));
		
		add(new AjaxButton("add", Model.of(WebSession.getString(366L)), this) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FieldLanguageDao langDao = Application.getBean(FieldLanguageDao.class);
				
				FieldLanguage fl = new FieldLanguage();
				fl.setId(langDao.getNextAvailableId());
				fl.setStarttime(new Date());
				fl.setDeleted(false);
				fl.setName(newLanguageName);
				fl.setRtl(false); //FIXME
				fl.setCode(newLanguageISO);
				
				try {
					langDao.update(fl);
				} catch (Exception e) {
					// TODO add feedback message
					log.error("Error while adding language", e);
				}

				langPanel.getLangForm().updateLanguages(target);
				/* FIXME
				languages.setChoices(langDao.getLanguages());
				target.add(languages);
				*/
				target.appendJavaScript("$('#addLanguage').dialog('close');");
			}
		});
	}
}
