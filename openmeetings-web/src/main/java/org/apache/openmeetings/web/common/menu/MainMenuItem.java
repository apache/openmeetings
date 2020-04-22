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
package org.apache.openmeetings.web.common.menu;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.util.OmUrlFragment;
import org.apache.openmeetings.web.util.OmUrlFragment.MenuActions;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class MainMenuItem extends OmMenuItem {
	private static final long serialVersionUID = 1L;
	private MenuActions action;

	public MainMenuItem(String lbl, String title, MenuActions action) {
		super(Application.getString(lbl), Application.getString(title));
		this.action = action;
	}

	public void onClick(MainPanel main, AjaxRequestTarget target) {
		main.updateContents(new OmUrlFragment(action), target);
	}
}
