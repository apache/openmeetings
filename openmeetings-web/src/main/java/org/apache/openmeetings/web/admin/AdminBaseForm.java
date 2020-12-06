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

import java.time.Duration;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * provides basic functionality to insert, update, remove, refresh record in
 * admin section
 *
 * @author swagner
 *
 * @param <T> - Entity class being used by this Admin Form
 */
public abstract class AdminBaseForm<T> extends Form<T> {
	private static final long serialVersionUID = 1L;
	private AdminActionsPanel<T> savePanel;
	protected final AjaxFormValidatingBehavior validationBehavior
			= new AjaxFormValidatingBehavior("keydown", Duration.ofSeconds(1));

	protected AdminBaseForm(String id, IModel<T> object) {
		super(id, object);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		savePanel = new AdminActionsPanel<>("buttons", this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onSaveSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onError(target, form);
			}

			@Override
			protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onNewSubmit(target, form);
			}

			@Override
			protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onRefreshSubmit(target, form);
			}

			@Override
			protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onDeleteSubmit(target, form);
			}

			@Override
			protected void onPurgeSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onPurgeSubmit(target, form);
			}

			@Override
			protected void onRestoreSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onRestoreSubmit(target, form);
			}
		};
		add(savePanel);
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(validationBehavior);
	}

	public void setNewVisible(boolean visible) {
		savePanel.setNewVisible(visible);
	}

	public void setNewRecordVisible(boolean visible) {
		savePanel.setNewRecordVisible(visible);
	}

	public void setDelVisible(boolean visible) {
		savePanel.setDelVisible(visible);
	}

	public void setSaveVisible(boolean visible) {
		savePanel.setSaveVisible(visible);
	}

	public void setPurgeVisible(boolean visible) {
		savePanel.setPurgeVisible(visible);
	}

	public void setRestoreVisible(boolean visible) {
		savePanel.setRestoreVisible(visible);
	}

	/**
	 * invoked when user press save button
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected abstract void onSaveSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * invoked when save has error
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected void onError(AjaxRequestTarget target, Form<?> form) {
		//no-op
	}

	/**
	 * invoked when new button is pressed
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected abstract void onNewSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * invoked when refresh button is pressed
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected abstract void onRefreshSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * invoked when delete button is pressed
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected abstract void onDeleteSubmit(AjaxRequestTarget target, Form<?> form);

	/**
	 * invoked when purge button is pressed
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected void onPurgeSubmit(AjaxRequestTarget target, Form<?> form) {
		//no-op
	}

	/**
	 * invoked when restore button is pressed
	 *
	 * @param target - ajax target to update form component
	 * @param form - Form being processed
	 */
	protected void onRestoreSubmit(AjaxRequestTarget target, Form<?> form) {
		//no-op
	}
}
