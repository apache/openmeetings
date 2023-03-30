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

import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;

public abstract class FormActionsPanel<T> extends Panel {
	private static final long serialVersionUID = 1L;
	private final Form<T> form;
	protected final NotificationPanel feedback = new NotificationPanel("feedback");
	private AjaxButton saveBtn;
	private AjaxLink<Void> purgeBtn;

	protected FormActionsPanel(String id, Form<T> form) {
		super(id);
		this.form = form;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		add(feedback.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));

		// add a save button that can be used to submit the form via ajax
		add(saveBtn = new AjaxButton("btn-save", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				onSaveSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				FormActionsPanel.this.onError(target, form);
			}
		});

		// add a refresh button that can be used to submit the form via ajax
		add(new AjaxLink<Void>("btn-refresh") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				setNewRecordVisible(false);
				onRefreshSubmit(target, form);
			}
		});
		purgeBtn = new AjaxLink<>("btn-purge") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				setNewRecordVisible(false);
				onPurgeSubmit(target, form);
			}
		};
		purgeBtn.add(newOkCancelDangerConfirm(this, getString("admin.purge.desc")));
		add(purgeBtn.setOutputMarkupPlaceholderTag(true).setVisible(false));
		super.onInitialize();
	}

	public void setSaveVisible(boolean visible) {
		saveBtn.setVisible(visible);
	}

	/**
	 * Change visibility the new record text
	 *
	 * @param visible - new visibility
	 */
	public void setNewRecordVisible(boolean visible) {
		// for admin only, will be implemented in admin
	}

	public void setPurgeVisible(boolean visible) {
		purgeBtn.setVisible(visible);
	}

	protected abstract void onSaveSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onRefreshSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onPurgeSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * Save error handler
	 *
	 * @param target Ajax target
	 * @param form form object
	 */
	protected void onError(AjaxRequestTarget target, Form<?> form) {
		//no-op
	}
}
