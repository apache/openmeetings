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
package org.apache.openmeetings.web.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public abstract class FormSaveRefreshPanel<T> extends BasePanel {
	private static final long serialVersionUID = 6133794730008996211L;
	protected final FeedbackPanel feedback;
	
	public FormSaveRefreshPanel(String id, Form<T> form) {
		super(id);
		setOutputMarkupId(true);

		feedback = new FeedbackPanel("feedback");
		add(feedback.setOutputMarkupId(true));

		// add a save button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-save-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				onSaveSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				onSaveError(target, form);
			}
		});

		// add a refresh button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-refresh-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				hideNewRecord();
				onRefreshSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				hideNewRecord();
				onRefreshError(target, form);
			}
		});

	}
	/**
	 * Hide the new record text
	 */
	public void hideNewRecord() {
		// for admin only, will be implemented in admin
	}
	
	protected abstract void onSaveSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onSaveError(AjaxRequestTarget target, Form<?> form);

	protected abstract void onRefreshSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onRefreshError(AjaxRequestTarget target, Form<?> form);
}
