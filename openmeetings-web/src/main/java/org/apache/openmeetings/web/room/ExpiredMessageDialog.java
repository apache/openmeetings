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
package org.apache.openmeetings.web.room;

import org.apache.openmeetings.web.room.menu.RoomMenuPanel;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;
import com.googlecode.wicket.jquery.ui.widget.dialog.MessageDialog;

public class ExpiredMessageDialog extends MessageDialog {
	private static final long serialVersionUID = 1L;
	private final RoomMenuPanel menu;

	public ExpiredMessageDialog(String id, String message, RoomMenuPanel menu) {
		super(id, "", message, DialogButtons.OK, DialogIcon.ERROR);
		this.menu = menu;
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("204"));
		super.onInitialize();
	}

	@Override
	public boolean isModal() {
		return true;
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", true);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		menu.exit(handler);
	}
}
