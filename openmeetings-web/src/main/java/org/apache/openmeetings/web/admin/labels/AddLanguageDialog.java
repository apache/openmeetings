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

import java.util.Arrays;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.kendo.ui.panel.KendoFeedbackPanel;

public class AddLanguageDialog extends AbstractFormDialog<String> {
	private static final long serialVersionUID = 1L;
	private final KendoFeedbackPanel feedback = new KendoFeedbackPanel("feedback", new Options("button", true));
	private final DialogButton add = new DialogButton("add", Application.getString(366L));
	private final Form<Void> form = new Form<>("addLangForm");
	private final RequiredTextField<String> iso = new RequiredTextField<String>("iso", Model.of(""));
	private final LangPanel langPanel;

	public AddLanguageDialog(String id, final LangPanel langPanel) {
		super(id, Application.getString(364));
		this.langPanel = langPanel;
		add(form.add(feedback, iso));
		iso.add(new IValidator<String>() {
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
		});
	}
	
	@Override
	public Form<?> getForm() {
		return form;
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(add);
	}
	
	@Override
	public DialogButton getSubmitButton() {
		return add;
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		super.onOpen(handler);
		iso.setModelObject("");
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		target.add(feedback);
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		try {
			LabelDao.add(Locale.forLanguageTag(iso.getModelObject()));
			langPanel.getLangForm().updateLanguages(target);
			target.appendJavaScript("$('#addLanguage').dialog('close');");
		} catch (Exception e) {
			error("Failed to add, " + e.getMessage()); //FIXME TODO add proper key
			target.add(feedback);
		}
	}
}
