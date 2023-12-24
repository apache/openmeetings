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
package org.apache.openmeetings.db.mapper;

import static java.util.UUID.randomUUID;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.room.InvitationDTO;
import org.apache.openmeetings.db.dto.room.RoomDTO;
import org.apache.openmeetings.db.dto.room.RoomFileDTO;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.user.User.Type;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.inject.Inject;

@Component
public class RoomMapper {
	private static final Logger log = LoggerFactory.getLogger(RoomMapper.class);
	private static final FastDateFormat SDF = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	@Inject
	private RoomDao roomDao;
	@Inject
	private UserDao userDao;
	@Inject
	private GroupDao groupDao;
	@Inject
	private FileItemDao fileDao;

	public Room get(RoomDTO dto) {
		Room r = dto.getId() == null ? new Room() : roomDao.get(dto.getId());
		r.setId(dto.getId());
		r.setName(dto.getName());
		r.setTag(dto.getTag());
		r.setComment(dto.getComment());
		r.setType(dto.getType());
		r.setCapacity(dto.getCapacity());
		r.setAppointment(dto.isAppointment());
		r.setConfno(dto.getConfno());
		r.setIspublic(dto.isPublic());
		r.setDemoRoom(dto.isDemo());
		r.setClosed(dto.isClosed());
		r.setDemoTime(dto.getDemoTime());
		r.setExternalId(dto.getExternalId());
		String externalType = dto.getExternalType();
		if (!Strings.isEmpty(externalType)
				&& r.getGroups().stream().filter(gu -> gu.getGroup().isExternal() && gu.getGroup().getName().equals(externalType)).count() == 0)
		{
			r.addGroup(groupDao.getExternal(externalType));
		}
		r.setRedirectURL(dto.getRedirectUrl());
		r.setModerated(dto.isModerated());
		r.setWaitModerator(dto.isWaitModerator());
		r.setAllowUserQuestions(dto.isAllowUserQuestions());
		r.setAllowRecording(dto.isAllowRecording());
		r.setWaitRecording(dto.isWaitRecording());
		r.setAudioOnly(dto.isAudioOnly());
		r.setHiddenElements(dto.getHiddenElements());
		r.setFiles(get(dto.getId(), dto.getFiles()));
		return r;
	}

	public RoomFile get(RoomFileDTO dto, Long roomId) {
		RoomFile f = new RoomFile();
		f.setId(dto.getId());
		f.setRoomId(roomId);
		f.setFile(fileDao.getBase(dto.getFileId()));
		f.setWbIdx(dto.getWbIdx());
		return f;
	}

	public List<RoomFile> get(Long roomId, List<RoomFileDTO> rfl) {
		List<RoomFile> r = new ArrayList<>();
		if (rfl != null) {
			for (RoomFileDTO rf : rfl) {
				RoomFile f = get(rf, roomId);
				if (f.getFile() == null) {
					continue;
				}
				r.add(f);
			}
		}
		return r;
	}


	public Invitation get(InvitationDTO dto, Long userId) {
		Invitation i = new Invitation();
		i.setHash(randomUUID().toString());
		i.setPasswordProtected(dto.isPasswordProtected());
		if (dto.isPasswordProtected()) {
			i.setPassword(CryptProvider.get().hash(dto.getPassword()));
		}

		i.setUsed(false);
		i.setValid(dto.getValid());

		try {
			// valid period of Invitation
			switch (dto.getValid()) {
				case PERIOD:
					i.setValidFrom(new Date(SDF.parse(dto.getValidFrom()).getTime() - (5 * 60 * 1000)));
					i.setValidTo(SDF.parse(dto.getValidTo()));
					break;
				case ENDLESS, ONE_TIME:
				default:
					break;
			}
		} catch (ParseException e) {
			log.error("Unexpected error while creating invitation", e);
			throw new RuntimeException(e);
		}

		i.setDeleted(false);

		i.setInvitedBy(userDao.get(userId));
		i.setInvitee(userDao.getContact(dto.getEmail(), dto.getFirstname(), dto.getLastname(), userId));
		if (Type.CONTACT == i.getInvitee().getType()) {
			i.getInvitee().setLanguageId(dto.getLanguageId());
		}
		i.setRoom(roomDao.get(dto.getRoomId()));
		i.setInserted(new Date());
		i.setAppointment(null);
		return i;
	}
}
