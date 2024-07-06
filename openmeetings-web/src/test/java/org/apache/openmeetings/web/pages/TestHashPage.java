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
package org.apache.openmeetings.web.pages;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.common.OmWebSocketPanel.CONNECTED_MSG;
import static org.apache.openmeetings.web.pages.HashPage.HASH;
import static org.apache.openmeetings.web.pages.HashPage.INVITATION_HASH;
import static org.apache.openmeetings.web.pages.HashPage.PANEL_MAIN;
import static org.apache.openmeetings.web.pages.HashPage.PANEL_RECORDING;
import static org.apache.openmeetings.web.util.OmUrlFragment.CHILD_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.apache.openmeetings.web.AbstractWicketTesterTest;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.Valid;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.openmeetings.web.user.record.VideoInfo;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.ws.tester.WebSocketTester;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

class TestHashPage extends AbstractWicketTesterTest {
	private static final Logger log = LoggerFactory.getLogger(TestHashPage.class);

	@Inject
	protected RoomDao roomDao;
	@Inject
	protected RecordingDao recDao;
	@Inject
	protected InvitationDao inviteDao;

	private void checkAccessDenied(boolean visible) {
		@SuppressWarnings("unchecked")
		Modal<String> dlg = (Modal<String>)tester.getComponentFromLastRenderedPage("access-denied");
		assertEquals(visible, tester.getLastResponseAsString().contains("new bootstrap.Modal(document.getElementById('" + dlg.getMarkupId() + "')).show();"));
	}

	private void checkAccessDenied(PageParameters pp) {
		tester.startPage(HashPage.class, pp);
		tester.assertRenderedPage(HashPage.class);
		checkAccessDenied(true);
		tester.assertInvisible(PANEL_MAIN);
	}

	private HashPage commonCheck(PageParameters pp) {
		HashPage page = tester.startPage(HashPage.class, pp);
		tester.assertRenderedPage(HashPage.class);
		checkAccessDenied(false);
		return page;
	}

	private void checkMainPanel(PageParameters pp, Class<? extends Panel> clazz) {
		HashPage page = commonCheck(pp);
		tester.assertComponent(PANEL_MAIN, MainPanel.class);
		tester.assertInvisible("header");

		WebSocketTester webSocketTester = new WebSocketTester(tester, page);
		webSocketTester.sendMessage(CONNECTED_MSG);
		tester.assertComponent(String.format("%s:contents:%s", PANEL_MAIN, CHILD_ID), clazz);
	}

	private void checkRecordingPanel(PageParameters pp, Class<? extends Panel> clazz) {
		HashPage page = commonCheck(pp);
		tester.assertComponent(PANEL_RECORDING, WebMarkupContainer.class);
		tester.assertVisible(PANEL_RECORDING);
		tester.assertVisible("header");

		WebSocketTester webSocketTester = new WebSocketTester(tester, page);
		webSocketTester.sendMessage(CONNECTED_MSG);
		tester.assertComponent(String.format("%s:%s", PANEL_RECORDING, "info"), clazz);
	}

	private Invitation get(Long userId, Long roomId, Long apptId, Long recId, Valid valid, String passwd, Date from, Date to) {
		Invitation i = new Invitation();
		i.setHash(randomUUID().toString());
		if (!Strings.isEmpty(passwd)) {
			i.setPasswordProtected(true);
			i.setPassword(CryptProvider.get().hash(passwd));
		}

		i.setUsed(false);
		i.setValid(valid);

		// valid period of Invitation
		switch (valid) {
			case PERIOD:
				i.setValidFrom(new Date(from.getTime() - (5 * 60 * 1000)));
				i.setValidTo(to);
				break;
			case ENDLESS, ONE_TIME:
			default:
				break;
		}

		i.setDeleted(false);

		i.setInvitedBy(userDao.get(userId));
		i.setInvitee(getContact(UUID.randomUUID().toString(), userId));
		if (roomId != null) {
			i.setRoom(roomDao.get(roomId));
		} else if (recId != null) {
			i.setRecording(recDao.get(recId));
		} else if (apptId != null) {
			Appointment a = appointmentDao.get(apptId);
			i.setAppointment(a);
			i.setRoom(a.getRoom());
		}
		inviteDao.update(i);
		return i;
	}

	@Test
	void testNoParams() {
		log.debug("No params");
		checkAccessDenied(null);
	}

	@Test
	void testBadParam() {
		log.debug("bad params");
		checkAccessDenied(new PageParameters().add("aaa", "bbb").add(HashPage.LANG, 777));
	}

	@Test
	void testBadSecure() {
		log.debug("bad secure");
		checkAccessDenied(new PageParameters().add(HASH, "bbb").add(HashPage.LANG, 3));
	}

	@Test
	void testBadInvitation() {
		log.debug("bad invitation");
		checkAccessDenied(new PageParameters().add(INVITATION_HASH, "bbb").add(HashPage.LANG, 5));
	}

	@Test
	void testValidOneTimeRoom() {
		Invitation i = get(1L, 2L, null, null, Valid.ONE_TIME, null, null, null);
		checkMainPanel(new PageParameters().add(INVITATION_HASH, i.getHash()), RoomPanel.class);
	}

	@Test
	void testValidOneTimeRecording() {
		// panel-recording
		Recording rec = new Recording();
		rec.setOwnerId(1L);
		recDao.update(rec);
		Invitation i = get(1L, null, null, rec.getId(), Valid.ONE_TIME, null, null, null);
		checkRecordingPanel(new PageParameters().add(INVITATION_HASH, i.getHash()), VideoInfo.class);
	}
}
