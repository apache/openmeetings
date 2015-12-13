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

import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;
/**
 * 
 * @author solomax, swagner
 * 
 */
public class AddLanguageForm extends Form<Void> {
	private static final long serialVersionUID = 1L;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private String newLanguageISO;
	
	public AddLanguageForm(String id, final LangPanel langPanel) {
		super(id);
		add(feedback);
		add(new RequiredTextField<String>("iso", new PropertyModel<String>(this, "newLanguageISO")).add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> s) {
				try {
					new Locale.Builder().setLanguageTag(s.getValue());
				} catch (IllformedLocaleException e) {
					s.error(new ValidationError("Invalid code, please use ")); //FIXME TODO add proper key
					return;
				}
				Locale l = Locale.forLanguageTag(s.getValue());
				for (Map.Entry<Long, Locale> e : LabelDao.languages.entrySet()) {
					if (e.getValue().equals(l)) {
						s.error(new ValidationError("This code already added")); //FIXME TODO add proper key
						break;
					}
				}
			}
		}));
		add(new AjaxButton("add", Model.of(Application.getString(366L)), this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.add(feedback);
			}
			
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				try {
					LabelDao.add(Locale.forLanguageTag(newLanguageISO));
					langPanel.getLangForm().updateLanguages(target);
					target.appendJavaScript("$('#addLanguage').dialog('close');");
				} catch (Exception e) {
					error("Failed to add, " + e.getMessage()); //FIXME TODO add proper key
					target.add(feedback);
				}
			}
		});
	}
}
