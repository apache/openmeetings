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

import static org.apache.openmeetings.web.app.WebSession.getDateFormat;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class EventDetailDialog extends AbstractDialog<Appointment> {
	private static final long serialVersionUID = 1L;

	public EventDetailDialog(String id, final Appointment a) {
		super(id, "", new CompoundPropertyModel<>(a), false);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		setTitle(new ResourceModel("815"));
		add(new Label("title"));
		add(new Label("description"));
		add(new Label("owner.timeZoneId"));
		add(new Label("start", getDateFormat().format(getModelObject().getStart())));
		add(new Label("end", getDateFormat().format(getModelObject().getEnd())));
		add(new Label("owner.firstname"));
		add(new Label("owner.lastname"));
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", true);
		behavior.setOption("position", "{my: 'right top', at: 'right bottom', of: '.room.menu' }");
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		//no-op
	}

	@Override
	protected List<DialogButton> getButtons() {
		return Arrays.asList(new DialogButton("cancel", new ResourceModel("lbl.cancel")));
	}
}
