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
package org.apache.openmeetings.web.components.user;

import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;

public class ChatPanel extends UserPanel {
	private static final long serialVersionUID = -9144707674886211557L;

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		setMarkupId(id);
		
		add(new WebMarkupContainer("messages"));
		add(new Form<Void>("sendForm") {
			private static final long serialVersionUID = -6367566664201921428L;

			{
				add(new TextArea<String>("message").setOutputMarkupId(true));
				add(new Button("send").add(new AjaxFormSubmitBehavior("onclick"){
					private static final long serialVersionUID = -3746739738826501331L;
					
				}));
			}
		});
	}

}
