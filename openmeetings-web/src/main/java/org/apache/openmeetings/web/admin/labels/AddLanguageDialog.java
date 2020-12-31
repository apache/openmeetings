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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;

public class AddLanguageDialog extends Modal<String> {
	private static final long serialVersionUID = 1L;
	private final NotificationPanel feedback = new NotificationPanel("feedback");
	private final Form<Void> form = new Form<>("addLangForm");
	private final RequiredTextField<String> iso = new RequiredTextField<>("iso", Model.of(""));
	private final LangPanel langPanel;

	public AddLanguageDialog(String id, final LangPanel langPanel) {
		super(id);
		this.langPanel = langPanel;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("362"));

		addButton(new BootstrapAjaxButton(BUTTON_MARKUP_ID, new ResourceModel("366"), form, Buttons.Type.Outline_Primary) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				try {
					LabelDao.add(Locale.forLanguageTag(iso.getModelObject()));
					langPanel.getLangForm().updateLanguages(target);
					AddLanguageDialog.this.close(target);
				} catch (Exception e) {
					error("Failed to add, " + e.getMessage());
					target.add(feedback);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				target.add(feedback);
			}
		});

		add(form.add(feedback.setOutputMarkupId(true), iso.setOutputMarkupId(true)));
		iso.add(new IValidator<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(IValidatable<String> s) {
				try {
					new Locale.Builder().setLanguageTag(s.getValue()).build();
				} catch (IllformedLocaleException e) {
					s.error(new ValidationError("Invalid code, please specify valid ISO code"));
					return;
				}
				Locale l = Locale.forLanguageTag(s.getValue());
				for (Map.Entry<Long, Locale> e : LabelDao.getLanguages()) {
					if (e.getValue().equals(l)) {
						s.error(new ValidationError("This code already added"));
						break;
					}
				}
			}
		});
		super.onInitialize();
	}

	@Override
	public Modal<String> show(IPartialPageRequestHandler handler) {
		iso.setModelObject("");
		handler.add(iso);
		return super.show(handler);
	}
}
