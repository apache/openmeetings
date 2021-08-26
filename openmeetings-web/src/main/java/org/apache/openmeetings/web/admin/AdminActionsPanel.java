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
package org.apache.openmeetings.web.admin;

import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import org.apache.openmeetings.web.common.FormActionsPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public abstract class AdminActionsPanel<T> extends FormActionsPanel<T> {
	private static final long serialVersionUID = 1L;
	private final Label newRecord = new Label("newRecord", Model.of(""));
	private final Form<T> form;
	private AjaxButton newBtn;
	private AjaxLink<Void> delBtn;
	private AjaxLink<Void> restoreBtn;

	protected AdminActionsPanel(String id, final Form<T> form) {
		super(id, form);
		this.form = form;
	}

	@Override
	protected void onInitialize() {
		newRecord.setDefaultModelObject(getString("155"));
		add(newRecord.setVisible(false).setOutputMarkupId(true));

		newBtn = new AjaxButton("btn-new", form) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				newRecord.setVisible(true);
				target.add(newRecord);
				onNewSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				AdminActionsPanel.this.onError(target, form);
			}
		};
		// add a cancel button that can be used to submit the form via ajax
		final Form<?> cForm = new Form<>("form");
		cForm.setMultiPart(form.isMultiPart());
		add(cForm);
		delBtn = new AjaxLink<>("btn-delete") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				setNewRecordVisible(false);
				onDeleteSubmit(target, form);
			}
		};
		delBtn.add(newOkCancelDangerConfirm(this, getString("833")));
		restoreBtn = new AjaxLink<>("btn-restore") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				setNewRecordVisible(false);
				onRestoreSubmit(target, form);
			}
		};
		add(newBtn, delBtn
				, restoreBtn.setOutputMarkupPlaceholderTag(true).setVisible(false));
		super.onInitialize();
	}

	public void setNewVisible(boolean visible) {
		newBtn.setVisible(visible);
	}

	@Override
	public void setNewRecordVisible(boolean visible) {
		newRecord.setVisible(visible);
	}

	public void setDelVisible(boolean visible) {
		delBtn.setVisible(visible);
	}

	public void setRestoreVisible(boolean visible) {
		restoreBtn.setVisible(visible);
	}

	protected abstract void onNewSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onDeleteSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onRestoreSubmit(AjaxRequestTarget target, Form<?> form);
}
