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
package org.apache.openmeetings.axis.services;

import java.util.Date;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomType;

public class RoomWebServiceFacade extends BaseWebService {

	// TODO: Not implemented yet
	// public List<Rooms_Organisation> getRoomsByOrganisationAndType(String SID,
	// long organisation_id, long roomtypes_id) {
	// return conferenceService.getRoomsByOrganisationAndType(SID,
	// organisation_id, roomtypes_id);
	// }

	public Room[] getRoomsPublic(String SID, Long roomtypes_id)
			throws AxisFault {
		return getBean(RoomWebService.class).getRoomsPublic(SID, roomtypes_id);
	}

	/**
	 * Deletes a flv recording
	 * 
	 * @param SID
	 *            The SID of the User. This SID must be marked as Loggedin
	 * @param flvRecordingId
	 *            the id of the recording
	 * @return
	 * @throws AxisFault
	 */
	public boolean deleteFlvRecording(String SID, Long flvRecordingId)
			throws AxisFault {
		return getBean(RoomWebService.class).deleteFlvRecording(SID,
				flvRecordingId);
	}

	public FLVRecordingReturn[] getFlvRecordingByExternalUserId(String SID,
			String externalUserId) throws AxisFault {
		return getBean(RoomWebService.class).getFlvRecordingByExternalUserId(SID,
				externalUserId);
	}

	public FLVRecordingReturn[] getFlvRecordingByExternalRoomTypeAndCreator(
			String SID, String externalRoomType, Long insertedBy)
			throws AxisFault {
		return getBean(RoomWebService.class)
				.getFlvRecordingByExternalRoomTypeAndCreator(SID,
						externalRoomType, insertedBy);
	}

	public List<FlvRecording> getFlvRecordingByExternalRoomTypeByList(
			String SID, String externalRoomType) throws AxisFault {
		return getBean(RoomWebService.class)
				.getFlvRecordingByExternalRoomTypeByList(SID, externalRoomType);
	}

	public FlvRecording[] getFlvRecordingByExternalRoomType(String SID,
			String externalRoomType) throws AxisFault {
		return getBean(RoomWebService.class).getFlvRecordingByExternalRoomType(
				SID, externalRoomType);
	}

	public FlvRecording[] getFlvRecordingByRoomId(String SID, Long roomId)
			throws AxisFault {
		return getBean(RoomWebService.class).getFlvRecordingByRoomId(SID, roomId);
	}

	public RoomType[] getRoomTypes(String SID) throws AxisFault {
		return getBean(RoomWebService.class).getRoomTypes(SID);
	}

	public RoomCountBean[] getRoomCounters(String SID, Integer roomId1,
			Integer roomId2, Integer roomId3, Integer roomId4, Integer roomId5,
			Integer roomId6, Integer roomId7, Integer roomId8, Integer roomId9,
			Integer roomId10) throws AxisFault {
		return getBean(RoomWebService.class).getRoomCounters(SID, roomId1,
				roomId2, roomId3, roomId4, roomId5, roomId6, roomId7, roomId8,
				roomId9, roomId10);
	}

	public Room getRoomById(String SID, long rooms_id) throws AxisFault {
		return getBean(RoomWebService.class).getRoomById(SID, rooms_id);
	}

	/**
	 * @deprecated this function is intend to be deleted
	 * @param SID
	 * @param rooms_id
	 * @return
	 */
	@Deprecated
	public Room getRoomWithCurrentUsersById(String SID, long rooms_id)
			throws AxisFault {
		return getBean(RoomWebService.class).getRoomWithCurrentUsersById(SID,
				rooms_id);
	}

	public RoomReturn getRoomWithClientObjectsById(String SID, long rooms_id)
			throws AxisFault {
		return getBean(RoomWebService.class).getRoomWithClientObjectsById(SID,
				rooms_id);
	}

	public SearchResult<Room> getRooms(String SID, int start, int max,
			String orderby, boolean asc) throws AxisFault {
		return getBean(RoomWebService.class).getRooms(SID, start, max, orderby,
				asc);
	}

	public SearchResult<Room> getRoomsWithCurrentUsers(String SID, int start,
			int max, String orderby, boolean asc) throws AxisFault {
		return getBean(RoomWebService.class).getRoomsWithCurrentUsers(SID, start,
				max, orderby, asc);
	}

	/**
	 * TODO: Fix Organization Issue
	 * 
	 * @deprecated use addRoomWithModeration instead
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param videoPodWidth
	 * @param videoPodHeight
	 * @param videoPodXPosition
	 * @param videoPodYPosition
	 * @param moderationPanelXPosition
	 * @param showWhiteBoard
	 * @param whiteBoardPanelXPosition
	 * @param whiteBoardPanelYPosition
	 * @param whiteBoardPanelHeight
	 * @param whiteBoardPanelWidth
	 * @param showFilesPanel
	 * @param filesPanelXPosition
	 * @param filesPanelYPosition
	 * @param filesPanelHeight
	 * @param filesPanelWidth
	 * @return
	 */
	@Deprecated
	public Long addRoom(String SID, String name, Long roomtypes_id,
			String comment, Long numberOfPartizipants, Boolean ispublic,
			Integer videoPodWidth, Integer videoPodHeight,
			Integer videoPodXPosition, Integer videoPodYPosition,
			Integer moderationPanelXPosition, Boolean showWhiteBoard,
			Integer whiteBoardPanelXPosition, Integer whiteBoardPanelYPosition,
			Integer whiteBoardPanelHeight, Integer whiteBoardPanelWidth,
			Boolean showFilesPanel, Integer filesPanelXPosition,
			Integer filesPanelYPosition, Integer filesPanelHeight,
			Integer filesPanelWidth) throws AxisFault {
		return getBean(RoomWebService.class).addRoom(SID, name, roomtypes_id,
				comment, numberOfPartizipants, ispublic, videoPodWidth,
				videoPodHeight, videoPodXPosition, videoPodYPosition,
				moderationPanelXPosition, showWhiteBoard,
				whiteBoardPanelXPosition, whiteBoardPanelYPosition,
				whiteBoardPanelHeight, whiteBoardPanelWidth, showFilesPanel,
				filesPanelXPosition, filesPanelYPosition, filesPanelHeight,
				filesPanelWidth);
	}

	public Long addRoomWithModeration(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom) throws AxisFault {
		return getBean(RoomWebService.class).addRoomWithModeration(SID, name,
				roomtypes_id, comment, numberOfPartizipants, ispublic,
				appointment, isDemoRoom, demoTime, isModeratedRoom);
	}

	/**
	 * this SOAP Method has an additional param to enable or disable the buttons
	 * to apply for moderation this does only work in combination with the
	 * room-type restricted
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param appointment
	 * @param isDemoRoom
	 * @param demoTime
	 * @param isModeratedRoom
	 * @param allowUserQuestions
	 * @return
	 */
	public Long addRoomWithModerationAndQuestions(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions) throws AxisFault {
		return getBean(RoomWebService.class).addRoomWithModerationAndQuestions(
				SID, name, roomtypes_id, comment, numberOfPartizipants,
				ispublic, appointment, isDemoRoom, demoTime, isModeratedRoom,
				allowUserQuestions);
	}

	public Long addRoomWithModerationQuestionsAndAudioType(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions, Boolean isAudioOnly) throws AxisFault {
		return getBean(RoomWebService.class)
				.addRoomWithModerationQuestionsAndAudioType(SID, name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						allowUserQuestions, isAudioOnly);
	}

	/**
	 * 
	 * @param SID
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param appointment
	 * @param isDemoRoom
	 * @param demoTime
	 * @param isModeratedRoom
	 * @param externalRoomId
	 * @param externalUserType
	 * @return
	 */
	public Long getRoomIdByExternalId(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, Long externalRoomId,
			String externalRoomType) throws AxisFault {
		return getBean(RoomWebService.class).getRoomIdByExternalId(SID, name,
				roomtypes_id, comment, numberOfPartizipants, ispublic,
				appointment, isDemoRoom, demoTime, isModeratedRoom,
				externalRoomId, externalRoomType);
	}

	/**
	 * TODO: Fix Organization Issue
	 * 
	 * @deprecated use updateRoomWithModeration
	 * 
	 * @param SID
	 * @param rooms_id
	 * @param name
	 * @param roomtypes_id
	 * @param comment
	 * @param numberOfPartizipants
	 * @param ispublic
	 * @param videoPodWidth
	 * @param videoPodHeight
	 * @param videoPodXPosition
	 * @param videoPodYPosition
	 * @param moderationPanelXPosition
	 * @param showWhiteBoard
	 * @param whiteBoardPanelXPosition
	 * @param whiteBoardPanelYPosition
	 * @param whiteBoardPanelHeight
	 * @param whiteBoardPanelWidth
	 * @param showFilesPanel
	 * @param filesPanelXPosition
	 * @param filesPanelYPosition
	 * @param filesPanelHeight
	 * @param filesPanelWidth
	 * @return
	 */
	@Deprecated
	public Long updateRoom(String SID, Long rooms_id, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Integer videoPodWidth, Integer videoPodHeight,
			Integer videoPodXPosition, Integer videoPodYPosition,
			Integer moderationPanelXPosition, Boolean showWhiteBoard,
			Integer whiteBoardPanelXPosition, Integer whiteBoardPanelYPosition,
			Integer whiteBoardPanelHeight, Integer whiteBoardPanelWidth,
			Boolean showFilesPanel, Integer filesPanelXPosition,
			Integer filesPanelYPosition, Integer filesPanelHeight,
			Integer filesPanelWidth, Boolean appointment) throws AxisFault {
		return getBean(RoomWebService.class).updateRoom(SID, rooms_id, name,
				roomtypes_id, comment, numberOfPartizipants, ispublic,
				videoPodWidth, videoPodHeight, videoPodXPosition,
				videoPodYPosition, moderationPanelXPosition, showWhiteBoard,
				whiteBoardPanelXPosition, whiteBoardPanelYPosition,
				whiteBoardPanelHeight, whiteBoardPanelWidth, showFilesPanel,
				filesPanelXPosition, filesPanelYPosition, filesPanelHeight,
				filesPanelWidth, appointment);
	}

	public Long updateRoomWithModeration(String SID, Long room_id, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom) throws AxisFault {
		return getBean(RoomWebService.class).updateRoomWithModeration(SID,
				room_id, name, roomtypes_id, comment, numberOfPartizipants,
				ispublic, appointment, isDemoRoom, demoTime, isModeratedRoom);
	}

	public Long updateRoomWithModerationAndQuestions(String SID, Long room_id,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions) throws AxisFault {
		return getBean(RoomWebService.class).updateRoomWithModerationAndQuestions(
				SID, room_id, name, roomtypes_id, comment,
				numberOfPartizipants, ispublic, appointment, isDemoRoom,
				demoTime, isModeratedRoom, allowUserQuestions);
	}

	public Long updateRoomWithModerationQuestionsAudioTypeAndHideOptions(
			String SID, Long room_id, String name, Long roomtypes_id,
			String comment, Long numberOfPartizipants, Boolean ispublic,
			Boolean appointment, Boolean isDemoRoom, Integer demoTime,
			Boolean isModeratedRoom, Boolean allowUserQuestions,
			Boolean isAudioOnly, Boolean hideTopBar, Boolean hideChat,
			Boolean hideActivitiesAndActions, Boolean hideFilesExplorer,
			Boolean hideActionsMenu, Boolean hideScreenSharing,
			Boolean hideWhiteboard) throws AxisFault {
		return getBean(RoomWebService.class)
				.updateRoomWithModerationQuestionsAudioTypeAndHideOptions(SID,
						room_id, name, roomtypes_id, comment,
						numberOfPartizipants, ispublic, appointment,
						isDemoRoom, demoTime, isModeratedRoom,
						allowUserQuestions, isAudioOnly, hideTopBar, hideChat,
						hideActivitiesAndActions, hideFilesExplorer,
						hideActionsMenu, hideScreenSharing, hideWhiteboard);

	}

	public Long deleteRoom(String SID, long rooms_id) throws AxisFault {
		return getBean(RoomWebService.class).deleteRoom(SID, rooms_id);
	}

	public Boolean kickUser(String SID_Admin, Long room_id) throws AxisFault {
		return getBean(RoomWebService.class).kickUser(SID_Admin, room_id);
	}

	public Long addRoomWithModerationAndExternalType(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType)
			throws AxisFault {
		return getBean(RoomWebService.class).addRoomWithModerationAndExternalType(
				SID, name, roomtypes_id, comment, numberOfPartizipants,
				ispublic, appointment, isDemoRoom, demoTime, isModeratedRoom,
				externalRoomType);
	}

	public Long addRoomWithModerationExternalTypeAndAudioType(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, Boolean allowUserQuestions,
			Boolean isAudioOnly) throws AxisFault {
		return getBean(RoomWebService.class)
				.addRoomWithModerationExternalTypeAndAudioType(SID, name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						externalRoomType, allowUserQuestions, isAudioOnly);
	}

	public Long addRoomWithModerationAndRecordingFlags(String SID, String name,
			Long roomtypes_id, String comment, Long numberOfPartizipants,
			Boolean ispublic, Boolean appointment, Boolean isDemoRoom,
			Integer demoTime, Boolean isModeratedRoom, String externalRoomType,
			Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean waitForRecording, Boolean allowRecording) throws AxisFault {
		return getBean(RoomWebService.class)
				.addRoomWithModerationAndRecordingFlags(SID, name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						externalRoomType, allowUserQuestions, isAudioOnly,
						waitForRecording, allowRecording);
	}

	public Long addRoomWithModerationExternalTypeAndTopBarOption(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, Boolean allowUserQuestions,
			Boolean isAudioOnly, Boolean waitForRecording,
			Boolean allowRecording, Boolean hideTopBar) throws AxisFault {
		return getBean(RoomWebService.class)
				.addRoomWithModerationExternalTypeAndTopBarOption(SID, name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						externalRoomType, allowUserQuestions, isAudioOnly,
						waitForRecording, allowRecording, hideTopBar);
	}

	public Long addRoomWithModerationQuestionsAudioTypeAndHideOptions(
			String SID, String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			Boolean allowUserQuestions, Boolean isAudioOnly,
			Boolean hideTopBar, Boolean hideChat,
			Boolean hideActivitiesAndActions, Boolean hideFilesExplorer,
			Boolean hideActionsMenu, Boolean hideScreenSharing,
			Boolean hideWhiteboard) throws AxisFault {
		return getBean(RoomWebService.class).addRoomWithModerationQuestionsAudioTypeAndHideOptions(SID, name, roomtypes_id, comment, numberOfPartizipants, ispublic, appointment, isDemoRoom, demoTime, isModeratedRoom, allowUserQuestions, isAudioOnly, hideTopBar, hideChat, hideActivitiesAndActions, hideFilesExplorer, hideActionsMenu, hideScreenSharing, hideWhiteboard);
	}
	
	/**
	 * 
	 * Create a Invitation hash and optionally send it by mail the From to Date
	 * is as String as some SOAP libraries do not accept Date Objects in SOAP
	 * Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the
	 * leading zero's)
	 * 
	 * @param SID
	 *            a valid Session Token
	 * @param username
	 *            the username of the User that he will get
	 * @param room_id
	 *            the conference room id of the invitation
	 * @param isPasswordProtected
	 *            if the invitation is password protected
	 * @param invitationpass
	 *            the password for accessing the conference room via the
	 *            invitation hash
	 * @param valid
	 *            the type of validation for the hash 1: endless, 2: from-to
	 *            period, 3: one-time
	 * @param validFromDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validFromTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @param validToDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validToTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String getInvitationHash(String SID, String username, Long room_id,
			Boolean isPasswordProtected, String invitationpass, Integer valid,
			String validFromDate, String validFromTime, String validToDate,
			String validToTime) throws AxisFault {
		return getBean(RoomWebService.class).getInvitationHash(SID, username,
				room_id, isPasswordProtected, invitationpass, valid,
				validFromDate, validFromTime, validToDate, validToTime);

	}

	/**
	 * Create a Invitation hash and optionally send it by mail the From to Date
	 * is as String as some SOAP libraries do not accept Date Objects in SOAP
	 * Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the
	 * leading zero's)
	 * 
	 * @param SID
	 *            a valid Session Token
	 * @param username
	 *            the Username of the User that he will get
	 * @param message
	 *            the Message in the Email Body send with the invitation if
	 *            sendMail is true
	 * @param baseurl
	 *            the baseURL for the Infivations link in the Mail Body if
	 *            sendMail is true
	 * @param email
	 *            the Email to send the invitation to if sendMail is true
	 * @param subject
	 *            the subject of the Email send with the invitation if sendMail
	 *            is true
	 * @param room_id
	 *            the conference room id of the invitation
	 * @param conferencedomain
	 *            the domain of the room (keep empty)
	 * @param isPasswordProtected
	 *            if the invitation is password protected
	 * @param invitationpass
	 *            the password for accessing the conference room via the
	 *            invitation hash
	 * @param valid
	 *            the type of validation for the hash 1: endless, 2: from-to
	 *            period, 3: one-time
	 * @param validFromDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validFromTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @param validToDate
	 *            Date in Format of dd.mm.yyyy only of interest if valid is type
	 *            2
	 * @param validToTime
	 *            time in Format of hh:mm only of interest if valid is type 2
	 * @param language_id
	 *            the language id of the EMail that is send with the invitation
	 *            if sendMail is true
	 * @param sendMail
	 *            if sendMail is true then the RPC-Call will send the invitation
	 *            to the email
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String sendInvitationHash(String SID, String username,
			String message, String baseurl, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Long language_id, Boolean sendMail) throws AxisFault {
		return getBean(RoomWebService.class).sendInvitationHash(SID, username,
				message, baseurl, email, subject, room_id, conferencedomain,
				isPasswordProtected, invitationpass, valid, validFromDate,
				validFromTime, validToDate, validToTime, language_id, sendMail);
	}

	/**
	 * Create a Invitation hash and optionally send it by mail the From to Date
	 * is as String as some SOAP libraries do not accept Date Objects in SOAP
	 * Calls Date is parsed as dd.mm.yyyy, time as hh:mm (don't forget the
	 * leading zero's)
	 * 
	 * @param SID
	 *            a valid Session Token
	 * @param username
	 *            the Username of the User that he will get
	 * @param message
	 *            the Message in the Email Body send with the invitation if
	 *            sendMail is true
	 * @param baseurl
	 *            the baseURL for the Infivations link in the Mail Body if
	 *            sendMail is true
	 * @param email
	 *            the Email to send the invitation to if sendMail is true
	 * @param subject
	 *            the subject of the Email send with the invitation if sendMail
	 *            is true
	 * @param room_id
	 *            the conference room id of the invitation
	 * @param conferencedomain
	 *            the domain of the room (keep empty)
	 * @param isPasswordProtected
	 *            if the invitation is password protected
	 * @param invitationpass
	 *            the password for accessing the conference room via the
	 *            invitation hash
	 * @param valid
	 *            the type of validation for the hash 1: endless, 2: from-to
	 *            period, 3: one-time
	 * @param fromDate
	 *            Date as Date Object only of interest if valid is type 2
	 * @param toDate
	 *            Date as Date Object only of interest if valid is type 2
	 * @param language_id
	 *            the language id of the EMail that is send with the invitation
	 *            if sendMail is true
	 * @param sendMail
	 *            if sendMail is true then the RPC-Call will send the invitation
	 *            to the email
	 * @return a HASH value that can be made into a URL with
	 *         http://$OPENMEETINGS_HOST
	 *         :$PORT/openmeetings/?invitationHash="+invitationsHash;
	 * @throws AxisFault
	 */
	public String sendInvitationHashWithDateObject(String SID, String username,
			String message, String baseurl, String email, String subject,
			Long room_id, String conferencedomain, Boolean isPasswordProtected,
			String invitationpass, Integer valid, Date fromDate, Date toDate,
			Long language_id, Boolean sendMail) throws AxisFault {
		return getBean(RoomWebService.class).sendInvitationHashWithDateObject(SID,
				username, message, baseurl, email, subject, room_id,
				conferencedomain, isPasswordProtected, invitationpass, valid,
				fromDate, toDate, language_id, sendMail);
	}

	public List<RoomReturn> getRoomsWithCurrentUsersByList(String SID,
			int start, int max, String orderby, boolean asc) throws AxisFault {
		return getBean(RoomWebService.class).getRoomsWithCurrentUsersByList(SID,
				start, max, orderby, asc);
	}

	public List<RoomReturn> getRoomsWithCurrentUsersByListAndType(String SID,
			int start, int max, String orderby, boolean asc,
			String externalRoomType) throws AxisFault {
		return getBean(RoomWebService.class)
				.getRoomsWithCurrentUsersByListAndType(SID, start, max,
						orderby, asc, externalRoomType);
	}

	public Long addRoomWithModerationAndExternalTypeAndStartEnd(String SID,
			String name, Long roomtypes_id, String comment,
			Long numberOfPartizipants, Boolean ispublic, Boolean appointment,
			Boolean isDemoRoom, Integer demoTime, Boolean isModeratedRoom,
			String externalRoomType, String validFromDate,
			String validFromTime, String validToDate, String validToTime,
			Boolean isPasswordProtected, String password, Long reminderTypeId,
			String redirectURL) throws AxisFault {
		return getBean(RoomWebService.class)
				.addRoomWithModerationAndExternalTypeAndStartEnd(SID, name,
						roomtypes_id, comment, numberOfPartizipants, ispublic,
						appointment, isDemoRoom, demoTime, isModeratedRoom,
						externalRoomType, validFromDate, validFromTime,
						validToDate, validToTime, isPasswordProtected,
						password, reminderTypeId, redirectURL);
	}

	public Long addMeetingMemberRemindToRoom(String SID, Long room_id,
			String firstname, String lastname, String email, String phone,
			String baseUrl, Long language_id) throws AxisFault {
		return getBean(RoomWebService.class).addMeetingMemberRemindToRoom(SID,
				room_id, firstname, lastname, email, phone, language_id);
	}

	public Long addExternalMeetingMemberRemindToRoom(String SID, Long room_id,
			String firstname, String lastname, String email, String baseUrl,
			Long language_id, String jNameTimeZone, String invitorName)
			throws AxisFault {
		return getBean(RoomWebService.class).addExternalMeetingMemberRemindToRoom(
				SID, room_id, firstname, lastname, email, baseUrl, language_id,
				jNameTimeZone, invitorName);
	}

	public int closeRoom(String SID, Long room_id, Boolean status)
			throws AxisFault {
		return getBean(RoomWebService.class).closeRoom(SID, room_id, status);
	}

	public int modifyRoomParameter(String SID, Long room_id, String paramName,
			String paramValue) throws AxisFault {
		return getBean(RoomWebService.class).modifyRoomParameter(SID, room_id,
				paramName, paramValue);
	}

}
