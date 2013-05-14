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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * provides basic functionality to insert, update, remove, refresh record in
 * admin section
 * 
 * @author swagner
 * 
 * @param <T>
 */
public class AdminBaseForm<T> extends Form<T> {
	private static final long serialVersionUID = 1L;
	private AdminSavePanel<T> savePanel;

	public AdminBaseForm(String id, IModel<T> object) {
		super(id, object);
		
		savePanel = new AdminSavePanel<T>("buttons", this) {
			private static final long serialVersionUID = -5833647470067891270L;

			@Override
			protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onSaveSubmit(target, form);
			}

			@Override
			protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onSaveError(target, form);
			}

			@Override
			protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onNewSubmit(target, form);
			}

			@Override
			protected void onNewError(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onNewError(target, form);
			}

			@Override
			protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onRefreshSubmit(target, form);
			}

			@Override
			protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onRefreshError(target, form);
			}

			@Override
			protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onDeleteSubmit(target, form);
			}

			@Override
			protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
				AdminBaseForm.this.onDeleteError(target, form);
			}
			
		};
		add(savePanel);


	}

	/**
	 * @see AdminBaseForm#hideNewRecord()
	 */
	public void hideNewRecord() {
		savePanel.hideNewRecord();
	}
	
	/**
	 * @see AdminBaseForm#showNewRecord()
	 */
	public void showNewRecord() {
		savePanel.showNewRecord();
	}

	/**
	 * invoked when user press save button
	 * 
	 * @param target
	 * @param form
	 */
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked when save has error
	 * 
	 * @param target
	 * @param form
	 */
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked when new button is pressed
	 * 
	 * @param target
	 * @param form
	 */
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked if new has error
	 * 
	 * @param target
	 * @param form
	 */
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked when refresh button is pressed
	 * 
	 * @param target
	 * @param form
	 */
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked when refresh has error
	 * 
	 * @param target
	 * @param form
	 */
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked when delete button is pressed
	 * 
	 * @param target
	 * @param form
	 */
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
	}
	
	/**
	 * invoked when delete has error
	 * 
	 * @param target
	 * @param form
	 */
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
	}
	
}
