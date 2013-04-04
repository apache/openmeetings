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
package org.apache.openmeetings.web.components.admin.rooms;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomOrganisation;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import edu.emory.mathcs.backport.java.util.Arrays;

public class RoomForm extends AdminBaseForm<Room> {

	private static final long serialVersionUID = 1L;

	Object[] array = { 2L, 4L, 6L, 8L, 10L, 12L, 14L, 16L, 20L, 25L, 32L, 50L,
			100L, 150L, 200L, 500L, 1000L };
	@SuppressWarnings("unchecked")
	List<Long> DROPDOWN_NUMBER_OF_PARTICIPANTS = Arrays.asList(array);

	public RoomForm(String id, final Room room) {
		super(id, new CompoundPropertyModel<Room>(room));
		setOutputMarkupId(true);
		RequiredTextField<String> name = new RequiredTextField<String>("name");
		name.setLabel(new Model<String>(WebSession.getString(193)));
		add(name);

		add(new DropDownChoice<Long>("numberOfPartizipants", //
				DROPDOWN_NUMBER_OF_PARTICIPANTS, //
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;
					public Object getDisplayValue(Long id) {
						return id;
					}
					public String getIdValue(Long id, int index) {
						return "" + id;
					}
				}));

		add(new DropDownChoice<RoomType>("roomtype", Application.getBean(
				RoomManager.class).getAllRoomTypes(WebSession.getLanguage()),
				new ChoiceRenderer<RoomType>("label", "roomtypes_id")));

		add(new TextArea<String>("comment"));

		add(new CheckBox("appointment"));
		add(new CheckBox("ispublic"));

		List<Organisation> orgList = Application.getBean(
				OrganisationManager.class).getOrganisations(3L);
		List<RoomOrganisation> orgRooms = new ArrayList<RoomOrganisation>(
				orgList.size());
		for (Organisation org : orgList) {
			orgRooms.add(new RoomOrganisation(org));
		}
		ListMultipleChoice<RoomOrganisation> orgChoiceList = new ListMultipleChoice<RoomOrganisation>(
				"roomOrganisations", orgRooms,
				new ChoiceRenderer<RoomOrganisation>("organisation.name",
						"organisation.organisation_id"));
		orgChoiceList.setMaxRows(6);
		add(orgChoiceList);

		add(new CheckBox("isDemoRoom"));
		TextField<Integer> demoTime = new TextField<Integer>("demoTime");
		demoTime.setLabel(new Model<String>(WebSession.getString(637)));
		add(demoTime);
		add(new CheckBox("allowUserQuestions"));
		add(new CheckBox("isAudioOnly"));
		add(new CheckBox("isClosed"));
		add(new TextField<String>("redirectURL"));
		add(new CheckBox("waitForRecording"));
		add(new CheckBox("allowRecording"));

		add(new CheckBox("hideTopBar"));
		add(new CheckBox("hideChat"));
		add(new CheckBox("hideActivitiesAndActions"));
		add(new CheckBox("hideFilesExplorer"));
		add(new CheckBox("hideActionsMenu"));
		add(new CheckBox("hideScreenSharing"));
		add(new CheckBox("hideWhiteboard"));
		add(new CheckBox("showMicrophoneStatus"));
		add(new CheckBox("chatModerated"));
		add(new CheckBox("chatOpened"));
		add(new CheckBox("filesOpened"));
		add(new CheckBox("autoVideoSelect"));
		
		// FIXME: Room user moderator list

		add(new CheckBox("isModeratedRoom"));
		

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);
	}
}
