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

import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomType;

public class RoomDTO {
	private Long id;
	private String name;
	private String comment;
	private RoomType type;
	private Long numberOfPartizipants = new Long(4);
	private boolean appointment;
	private String confno;
	private boolean isPublic;
	private boolean demo;
	private Integer demoTime;
	private Long externalId;
	private String externalType;
	private String redirectUrl;
	private boolean moderated;
	private boolean allowUserQuestions;
	private boolean allowRecording;
	private boolean waitForRecording;
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
		id = r.getId();
		name = r.getName();
		comment = r.getComment();
		type = r.getRoomtype();
		numberOfPartizipants = r.getNumberOfPartizipants();
		appointment = r.isAppointment();
		confno = r.getConfno();
		isPublic = r.getIspublic();
		demo = r.getIsDemoRoom();
		demoTime = r.getDemoTime();
		externalId = r.getExternalId();
		externalType = r.getExternalType();
		redirectUrl = r.getRedirectURL();
		moderated = r.isModerated();
		allowUserQuestions = r.getAllowUserQuestions();
		allowRecording = r.isAllowRecording();
		waitForRecording = r.getWaitForRecording();
		audioOnly = r.isAudioOnly();
		topBarHidden = r.getHideTopBar();
		chatHidden = r.isChatHidden();
		activitiesHidden = r.isActivitiesHidden();
		filesExplorerHidden = r.getHideFilesExplorer();
		actionsMenuHidden = r.getHideActionsMenu();
		screenSharingHidden = r.getHideScreenSharing();
		whiteboardHidden = r.getHideWhiteboard();
	}

	public Room get(RoomTypeDao roomTypeDao) {
		Room r = new Room();
		r.setId(id);
		r.setName(name);
		r.setComment(comment);
		r.setRoomtype(roomTypeDao.get(type.getId()));
		r.setNumberOfPartizipants(numberOfPartizipants);
		r.setAppointment(appointment);
		r.setConfno(confno);
		r.setIspublic(isPublic);
		r.setIsDemoRoom(demo);
		r.setDemoTime(demoTime);
		r.setExternalId(externalId);
		r.setExternalType(externalType);
		r.setRedirectURL(redirectUrl);
		r.setModerated(moderated);
		r.setAllowUserQuestions(allowUserQuestions);
		r.setAllowRecording(allowRecording);
		r.setWaitForRecording(waitForRecording);
		r.setAudioOnly(audioOnly);
		r.setHideTopBar(topBarHidden);
		r.setChatHidden(chatHidden);
		r.setActivitiesHidden(activitiesHidden);
		r.setHideFilesExplorer(filesExplorerHidden);
		r.setHideActionsMenu(actionsMenuHidden);
		r.setHideScreenSharing(screenSharingHidden);
		r.setHideWhiteboard(whiteboardHidden);
		return r;
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

	public RoomType getType() {
		return type;
	}

	public void setType(RoomType type) {
		this.type = type;
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

	public boolean isAllowRecording() {
		return allowRecording;
	}

	public void setAllowRecording(boolean allowRecording) {
		this.allowRecording = allowRecording;
	}

	public boolean isWaitForRecording() {
		return waitForRecording;
	}

	public void setWaitForRecording(boolean waitForRecording) {
		this.waitForRecording = waitForRecording;
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

	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public static List<RoomDTO> list(List<Room> l) {
		List<RoomDTO> rList = new ArrayList<>();
		if (l != null) {
			for (Room r : l) {
				rList.add(new RoomDTO(r));
			}
		}
		return rList;
	}
}
