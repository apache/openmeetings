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
package org.apache.openmeetings.web.components.admin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class AdminBaseFormPanel<T> extends AdminPanel {
	private static final long serialVersionUID = 8361623159373532543L;

	public AdminBaseFormPanel(String id, final AdminBaseForm<T> form) {
		super(id);

		setOutputMarkupId(true);

		final FeedbackPanel feedback = new FeedbackPanel("feedback");
		feedback.setOutputMarkupId(true);
		add(feedback);

		// add a button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-save-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});

		add(new AjaxButton("ajax-new-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});

		add(new AjaxButton("ajax-refresh-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});

		add(new AjaxButton("ajax-cancel-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
			}
		});
	}
}
