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
package org.apache.openmeetings.db.dto.room;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomType;

public class RoomDTO {
	private Long id;
	private String name;
	private String comment;
	private RoomType roomtype;
	private Long numberOfPartizipants = new Long(4);
	private boolean appointment;
	private String confno;
	private boolean isPublic;
	private boolean demo;
	private Integer demoTime;
	private boolean moderated;
	private boolean allowUserQuestions;
	private boolean audioOnly;
	private boolean topBarHidden;
	private boolean chatHidden;
	private boolean activitiesHidden;
	private boolean filesExplorerHidden;
	private boolean actionsMenuHidden;
	private boolean screenSharingHidden;
	private boolean whiteboardHidden;

	public RoomDTO() {
	}
	
	public RoomDTO(Room r) {
		id = r.getRooms_id();
		name = r.getName();
		comment = r.getComment();
		roomtype = r.getRoomtype();
		numberOfPartizipants = r.getNumberOfPartizipants();
		appointment = r.getAppointment();
		confno = r.getConfno();
		isPublic = Boolean.TRUE.equals(r.getIspublic());
		demo = Boolean.TRUE.equals(r.getIsDemoRoom());
		demoTime = r.getDemoTime();
		moderated = Boolean.TRUE.equals(r.getIsModeratedRoom());
		allowUserQuestions = Boolean.TRUE.equals(r.getAllowUserQuestions());
		audioOnly = Boolean.TRUE.equals(r.getIsAudioOnly());
		topBarHidden = Boolean.TRUE.equals(r.getHideTopBar());
		chatHidden = Boolean.TRUE.equals(r.getHideChat());
		activitiesHidden = Boolean.TRUE.equals(r.getHideActivitiesAndActions());
		filesExplorerHidden = Boolean.TRUE.equals(r.getHideFilesExplorer());
		actionsMenuHidden = Boolean.TRUE.equals(r.getHideActionsMenu());
		screenSharingHidden = Boolean.TRUE.equals(r.getHideScreenSharing());
		whiteboardHidden = Boolean.TRUE.equals(r.getHideWhiteboard());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public RoomType getRoomtype() {
		return roomtype;
	}

	public void setRoomtype(RoomType roomtype) {
		this.roomtype = roomtype;
	}

	public Long getNumberOfPartizipants() {
		return numberOfPartizipants;
	}

	public void setNumberOfPartizipants(Long numberOfPartizipants) {
		this.numberOfPartizipants = numberOfPartizipants;
	}

	public boolean isAppointment() {
		return appointment;
	}

	public void setAppointment(boolean appointment) {
		this.appointment = appointment;
	}

	public String getConfno() {
		return confno;
	}

	public void setConfno(String confno) {
		this.confno = confno;
	}

	public static List<RoomDTO> list(List<Room> l) {
		List<RoomDTO> rList = new ArrayList<RoomDTO>();
		if (l != null) {
			for (Room r : l) {
				rList.add(new RoomDTO(r));
			}
		}
		return rList;
	}

	public boolean isDemo() {
		return demo;
	}

	public void setDemo(boolean demo) {
		this.demo = demo;
	}

	public Integer getDemoTime() {
		return demoTime;
	}

	public void setDemoTime(Integer demoTime) {
		this.demoTime = demoTime;
	}

	public boolean isModerated() {
		return moderated;
	}

	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	public boolean isAllowUserQuestions() {
		return allowUserQuestions;
	}

	public void setAllowUserQuestions(boolean allowUserQuestions) {
		this.allowUserQuestions = allowUserQuestions;
	}

	public boolean isAudioOnly() {
		return audioOnly;
	}

	public void setAudioOnly(boolean audioOnly) {
		this.audioOnly = audioOnly;
	}

	public boolean isTopBarHidden() {
		return topBarHidden;
	}

	public void setTopBarHidden(boolean topBarHidden) {
		this.topBarHidden = topBarHidden;
	}

	public boolean isChatHidden() {
		return chatHidden;
	}

	public void setChatHidden(boolean chatHidden) {
		this.chatHidden = chatHidden;
	}

	public boolean isActivitiesHidden() {
		return activitiesHidden;
	}

	public void setActivitiesHidden(boolean activitiesHidden) {
		this.activitiesHidden = activitiesHidden;
	}

	public boolean isFilesExplorerHidden() {
		return filesExplorerHidden;
	}

	public void setFilesExplorerHidden(boolean filesExplorerHidden) {
		this.filesExplorerHidden = filesExplorerHidden;
	}

	public boolean isActionsMenuHidden() {
		return actionsMenuHidden;
	}

	public void setActionsMenuHidden(boolean actionsMenuHidden) {
		this.actionsMenuHidden = actionsMenuHidden;
	}

	public boolean isScreenSharingHidden() {
		return screenSharingHidden;
	}

	public void setScreenSharingHidden(boolean screenSharingHidden) {
		this.screenSharingHidden = screenSharingHidden;
	}

	public boolean isWhiteboardHidden() {
		return whiteboardHidden;
	}

	public void setWhiteboardHidden(boolean whiteboardHidden) {
		this.whiteboardHidden = whiteboardHidden;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
}
