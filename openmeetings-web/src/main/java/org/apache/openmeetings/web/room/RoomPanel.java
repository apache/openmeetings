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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_APPLICATION_BASE_URL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addUserToRoom;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getRoomUsers;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_PUBLIC;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.menu.MenuItem;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.protocol.ws.IWebSocketSettings;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.whiteboard.WhiteboardBehavior;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;

@AuthorizeInstantiation("Room")
public class RoomPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomPanel.class, webAppRootKey);
	private long roomId;
	private Client c;
	private AbstractDefaultAjaxBehavior aab = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			target.appendJavaScript("roomMessage(" + userList(roomId, c) + ");");
			target.appendJavaScript("setHeight();");
			//TODO SID etc
			Room r = getBean(RoomDao.class).get(roomId);
			target.appendJavaScript(String.format("initVideo('%s', %s, %s, %s, %s);", "sid"
					, r.getId()
					, r.getIsAudioOnly()
					, 4L == r.getRoomtype().getRoomtypes_id()
					, getStringLabels(448, 449, 450, 451, 758, 447, 52, 53, 1429, 1430, 775, 452, 767, 764, 765, 918, 54, 761, 762).toString()
					));
			
		}
	};
	
	public RoomPanel(String id, long _roomId) {
		super(id);
		this.roomId = _roomId;
		Room r = getBean(RoomDao.class).get(roomId);
		add(new MenuPanel("roomMenu", getMenu()).setVisible(!r.getHideTopBar()));
		WebMarkupContainer wb = new WebMarkupContainer("whiteboard");
		add(wb.setOutputMarkupId(true));
		add(new WhiteboardBehavior("1", wb.getMarkupId(), null, null, null));
		add(aab, AttributeAppender.append("style", "height: 100%;"));
		boolean showFiles = !r.getHideFilesExplorer();
		add(new WebMarkupContainer("flink").setVisible(showFiles));
		add(new WebMarkupContainer("ftab").setVisible(showFiles));
		add(new JQueryBehavior(".room.sidebar.left .tabs", "tabs", new Options("active", showFiles && r.isFilesOpened() ? "ftab" : "utab")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void renderScript(JavaScriptHeaderItem script, IHeaderResponse response) {
				response.render(new PriorityHeaderItem(script));
			}
		});
	}

	private JSONArray getStringLabels(long... ids) {
		JSONArray arr = new JSONArray();
		try {
		for (long id : ids) {
			arr.put(new JSONObject().put("id", id).put("value", WebSession.getString(id)));
		}
		} catch (JSONException e) {
			log.error("", e);
		}
		return arr;
	}
	
	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		c = addUserToRoom(roomId, getPage().getPageId());
		sendRoom(roomId, addUser(c));
	}
	
	public static JSONObject getUser(Client c, boolean current) throws JSONException {
		User u = getBean(UserDao.class).get(c.getUserId());
		return new JSONObject()
			.put("uid", c.getUid())
			.put("id", u.getId())
			.put("firstname", u.getFirstname())
			.put("lastname", u.getLastname())
			.put("email", u.getAdresses() == null ? null : u.getAdresses().getEmail())
			.put("current", current);
	}
	
	public static String userList(long roomId, Client cur) {
		try {
			JSONArray ul = new JSONArray();
			for (Client c : getRoomUsers(roomId)) {
				ul.put(getUser(c, c.equals(cur)));
			}
			return new JSONObject()
				.put("type", "room")
				.put("msg", "users")
				.put("timestamp", System.currentTimeMillis())
				.put("users", ul).toString();
		} catch (Exception e) {
			log.error("Error while creating message", e);
		}
		return "{}";
	}
	
	public static String addUser(Client c) {
		try {
			return new JSONObject()
				.put("type", "room")
				.put("msg", "addUser")
				.put("user", getUser(c, false)).toString();
		} catch (Exception e) {
			log.error("Error while creating message", e);
		}
		return "{}";
	}
	
	public static String removeUser(Client c) {
		try {
			return new JSONObject()
				.put("type", "room")
				.put("msg", "removeUser")
				.put("uid", c.getUid()).toString();
		} catch (Exception e) {
			log.error("Error while creating message", e);
		}
		return "{}";
	}
	
	public static void sendRoom(long roomId, String msg) {
		IWebSocketConnectionRegistry reg = IWebSocketSettings.Holder.get(Application.get()).getConnectionRegistry();
		for (Client c : getRoomUsers(roomId)) {
			try {
				reg.getConnection(Application.get(), c.getSessionId(), new PageIdKey(c.getPageId())).sendMessage(msg);
			} catch (Exception e) {
				log.error("Error while sending message", e);
			}
		}
	}
	
	private List<MenuItem> getMenu() {
		//TODO hide/show
		List<MenuItem> menu = new ArrayList<MenuItem>();
		menu.add(new RoomMenuItem(WebSession.getString(308), WebSession.getString(309), "room menu exit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(MainPage page, AjaxRequestTarget target) {
				if (WebSession.getRights().contains(Right.Dashboard)) {
					page.updateContents(ROOMS_PUBLIC, target);
				} else {
					String url = getBean(ConfigurationDao.class).getConfValue(CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY, String.class, "");
					if (Strings.isEmpty(url)) {
						url = getBean(ConfigurationDao.class).getConfValue(CONFIG_APPLICATION_BASE_URL, String.class, "");
					}
					throw new RedirectToUrlException(url);
				}
			}
		});
		MenuItem files = new RoomMenuItem(WebSession.getString(245));
		List<RoomMenuItem> fileItems = new ArrayList<RoomMenuItem>();
		fileItems.add(new RoomMenuItem(WebSession.getString(15)));
		files.setChildren(fileItems);
		menu.add(files);
		
		MenuItem actions = new RoomMenuItem(WebSession.getString(635));
		List<RoomMenuItem> actionItems = new ArrayList<RoomMenuItem>();
		actionItems.add(new RoomMenuItem(WebSession.getString(213)));
		actionItems.add(new RoomMenuItem(WebSession.getString(239)));
		actionItems.add(new RoomMenuItem(WebSession.getString(784)));
		actionItems.add(new RoomMenuItem(WebSession.getString(785)));
		actionItems.add(new RoomMenuItem(WebSession.getString(786)));
		actionItems.add(new RoomMenuItem(WebSession.getString(24)));
		actionItems.add(new RoomMenuItem(WebSession.getString(37)));
		actionItems.add(new RoomMenuItem(WebSession.getString(42)));
		actionItems.add(new RoomMenuItem(WebSession.getString(1447)));
		actionItems.add(new RoomMenuItem(WebSession.getString(1126)));
		actions.setChildren(actionItems);
		menu.add(actions);
		return menu;
	}
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.add(getMainPage().getHeader().setVisible(false), getMainPage().getMenu().setVisible(false)
				, getMainPage().getTopLinks().setVisible(false));
		target.appendJavaScript("roomLoad();");
	}
	
	@Override
	public void cleanup(AjaxRequestTarget target) {
		target.add(getMainPage().getHeader().setVisible(true), getMainPage().getMenu().setVisible(true)
				, getMainPage().getTopLinks().setVisible(true));
		target.appendJavaScript("$(window).off('resize.openmeetings');");
		sendRoom(roomId, removeUser(c));
	}

	private ResourceReference newResourceReference() {
		return new JavaScriptResourceReference(RoomPanel.class, "room.js");
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(newResourceReference())));
		response.render(OnDomReadyHeaderItem.forScript(aab.getCallbackScript()));
	}
}
