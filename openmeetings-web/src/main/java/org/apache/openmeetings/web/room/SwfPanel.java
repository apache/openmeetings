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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.WICKET_ROOM_ID;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getSid;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.openmeetings.core.session.SessionManager;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.mapper.parameter.PageParametersEncoder;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

public class SwfPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(SwfPanel.class, webAppRootKey);
	public static final String SWF = "swf";
	public static final String PARAM_PUBLIC_SID = "publicSid";
	public static final String PARAM_URL = "url";
	public static final String SWF_TYPE_NETWORK = "network";
	public static final String SWF_TYPE_SETTINGS = "settings";
	private Long roomId = null;

	public SwfPanel(String id) {
		this(id, new PageParameters());
	}

	public SwfPanel(String id, Long roomId, String uid) {
		this(id, addServer(roomId, true).add("uid", uid));
	}

	public SwfPanel(String id, PageParameters pp) {
		super(id);
		//OK let's find the room
		try {
			StringValue room = pp.get(WICKET_ROOM_ID);
			if (!room.isEmpty()) {
				roomId = room.toLongObject();
			}
		} catch (Exception e) {
			//no-op
		}
		PageParameters spp = new PageParameters(pp);
		if (roomId != null) {
			spp.mergeWith(new PageParameters().add(WICKET_ROOM_ID, roomId));
		}
		add(new Label("init", getInitFunction(spp)).setEscapeModelStrings(false));
		add(new AbstractAjaxTimerBehavior(Duration.minutes(5)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTimer(AjaxRequestTarget target) {
				getBean(SessiondataDao.class).check(WebSession.getSid()); //keep SID alive
			}
		});
	}

	private static ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(SwfPanel.class, "swf-functions.js");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forUrl("js/openmeetings_functions.js")));
		//FIXME TODO ugly HACK
		if (WebSession.get().getClientInfo().getProperties().isBrowserMozillaFirefox()) {
			response.render(new PriorityHeaderItem(CssHeaderItem.forCSS(".ui-widget-overlay{opacity: 1 !important;}", "ff-veil-hack")));
		}
	}

	public String getInitFunction(PageParameters pp) {
		String initStr = null;
		StringValue type = pp.get(SWF);
		String swf = getFlashFile(type);
		if (!Strings.isEmpty(swf)) {
			String lbls = null;
			if (SWF_TYPE_NETWORK.equals(type.toString())) {
				lbls = getStringLabels(
						"network.test.ms", "network.test.mb", "network.test.sec"
						, "network.test.click.play", "network.test.copy.log"
						, "network.test.report", "network.test.report.start", "network.test.report.error"
						, "network.test.report.con.err"
						, "network.test.ping", "network.test.ping.avg", "network.test.ping.rcv"
						, "network.test.ping.lost", "network.test.ping.load"
						, "network.test.port", "network.test.port.avail", "network.test.port.stopped"
						, "network.test.jitter", "network.test.jitter.avg", "network.test.jitter.min"
						, "network.test.jitter.max"
						, "network.test.dwn", "network.test.dwn.bytes", "network.test.dwn.time"
						, "network.test.dwn.speed"
						, "network.test.upl", "network.test.upl.bytes", "network.test.upl.time"
						, "network.test.upl.speed"
						);
			} else if (SWF_TYPE_SETTINGS.equals(type.toString())) {
				lbls = getStringLabels("448", "449", "450", "451", "758", "447", "52", "53", "1429", "1430"
						, "775", "452", "767", "764", "765", "918", "54", "761", "762", "144", "203", "642"
						, "save.success");
			}
			initStr = String.format("var labels = %s; initSwf(%s);", lbls
					, new JSONObject().put("src", swf + new PageParametersEncoder().encodePageParameters(pp)).toString());
		}
		return initStr;
	}

	private String getFlashFile(StringValue type) {
		String fmt = "main%s.swf11.swf";
		if (SWF_TYPE_NETWORK.equals(type.toString())) {
			fmt = "networktesting%s.swf10.swf";
		}
		return String.format(fmt, DEVELOPMENT == getApplication().getConfigurationType() ? "debug" : "");
	}

	public static String getStringLabels(String... ids) {
		JSONArray arr = new JSONArray();
		for (String id : ids) {
			arr.put(new JSONObject().put("id", id).put("value", Application.getString(id)));
		}
		return arr.toString();
	}

	private static PageParameters addServer(PageParameters pp, Server s) {
		return pp.add("protocol", s.getProtocol()).add("host", s.getAddress()).add("port", s.getPort()).add("context", s.getWebapp());
	}

	public static PageParameters addServer(Long roomId, boolean addBasic) {
		PageParameters pp = new PageParameters();
		if (addBasic) {
			pp.add("wicketsid", getSid()).add(WICKET_ROOM_ID, roomId).add("language", getLanguage());
		}
		List<Server> serverList = getBean(ServerDao.class).getActiveServers();

		long minimum = -1;
		Server result = null;
		Map<Server, List<Long>> activeRoomsMap = new HashMap<>();
		for (Server server : serverList) {
			List<Long> roomIds = getBean(SessionManager.class).getActiveRoomIdsByServer(server);
			if (roomIds.contains(roomId)) {
				// if the room is already opened on a server, redirect the user to that one,
				log.debug("Room is already opened on a server " + server.getAddress());
				return addServer(pp, server);
			}
			activeRoomsMap.put(server, roomIds);
		}
		for (Map.Entry<Server, List<Long>> entry : activeRoomsMap.entrySet()) {
			List<Long> roomIds = entry.getValue();
			long capacity = getBean(RoomDao.class).getRoomsCapacityByIds(roomIds);
			if (minimum < 0 || capacity < minimum) {
				minimum = capacity;
				result = entry.getKey();
			}
			log.debug("Checking server: " + entry.getKey() + " Number of rooms " + roomIds.size() + " RoomIds: "
					+ roomIds + " max(Sum): " + capacity);
		}
		return result == null ? pp : addServer(pp, result);
	}
}
