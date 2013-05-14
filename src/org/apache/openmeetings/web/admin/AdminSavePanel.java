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

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ConfirmCallListener;
import org.apache.openmeetings.web.common.FormSaveRefreshPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public abstract class AdminSavePanel<T> extends FormSaveRefreshPanel<T> {
	private static final long serialVersionUID = -8916631148087019924L;
	private Label newRecord;
	
	public AdminSavePanel(String id, final Form<T> form) {
		super(id, form);
		
		newRecord = new Label("newRecord", Model.of(WebSession.getString(344L)));
		add(newRecord.setVisible(false).setOutputMarkupId(true));
		
		// add a new button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-new-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				newRecord.setVisible(true);
				target.add(newRecord);
				onNewSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				onNewError(target, form);
			}
		});

		// add a cancel button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-cancel-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				hideNewRecord();
				onDeleteSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				hideNewRecord();
				onDeleteError(target, form);
			}
		});
	}
	
	/**
	 * Hide the new record text
	 */
	public void hideNewRecord() {
		newRecord.setVisible(false);
	}
	
	/**
	 * Hide the new record text
	 */
	public void showNewRecord() {
		newRecord.setVisible(true);
	}

	protected abstract void onNewSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onNewError(AjaxRequestTarget target, Form<?> form);

	protected abstract void onDeleteSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onDeleteError(AjaxRequestTarget target, Form<?> form);
}
