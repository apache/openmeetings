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
package org.apache.openmeetings.web.user.dashboard.admin;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.wicketstuff.dashboard.Widget;
import org.wicketstuff.dashboard.web.WidgetView;

import com.googlecode.wicket.jquery.ui.form.button.IndicatingAjaxButton;

public class AdminWidgetView extends WidgetView {
	private static final long serialVersionUID = 1L;
	private final AdminCleanupInfoDialog cleanupDialog;
	final Form<Void> form = new Form<>("form");

	public AdminWidgetView(String id, Model<Widget> model) {
		super(id, model);
		add(form);
		form.add(cleanupDialog = new AdminCleanupInfoDialog("cleanup-dialog"));
		form.add(new IndicatingAjaxButton("show-cleanup-dialog") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected boolean isDisabledOnClick() {
				return true;
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				cleanupDialog.show(target);
			}
		});
	}
}
