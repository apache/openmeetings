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
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_PORT;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_FLASH_PROTOCOL;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_REDIRECT_URL_FOR_EXTERNAL_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_QUALITY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_SCREENSHARING_FPS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.addUserToRoom;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.getRoomUsers;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;
import static org.apache.openmeetings.web.app.WebSession.getSid;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.OmUrlFragment.ROOMS_PUBLIC;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.label.FieldLanguagesValuesDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.Client;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.menu.MenuItem;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.openmeetings.web.common.tree.FileItemTree;
import org.apache.openmeetings.web.common.tree.FileTreePanel;
import org.apache.openmeetings.web.common.tree.MyRecordingTreeProvider;
import org.apache.openmeetings.web.common.tree.PublicRecordingTreeProvider;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.IWebSocketSettings;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.whiteboard.WhiteboardBehavior;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.form.button.Button;

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
	
	private String getLabels(int ... ids) {
		StringBuilder result = new StringBuilder();
		boolean delim = false;
		FieldLanguagesValuesDao labelDao = getBean(FieldLanguagesValuesDao.class);
		for (int id : ids) {
			if (delim) {
				result.append(';');
			}
			result.append(labelDao.getString(id, getLanguage()));
			delim = true;
		}
		return result.toString();
	}
	
	public RoomPanel(String id, long _roomId) {
		this(id, getBean(RoomDao.class).get(_roomId));
	}
	
	public RoomPanel(String id, Room r) {
		super(id);
		this.roomId = r.getId();
		add(new MenuPanel("roomMenu", getMenu()).setVisible(!r.getHideTopBar()));
		WebMarkupContainer wb = new WebMarkupContainer("whiteboard");
		add(wb.setOutputMarkupId(true));
		add(new WhiteboardBehavior("1", wb.getMarkupId(), null, null, null));
		add(aab, AttributeAppender.append("style", "height: 100%;"));
		boolean showFiles = !r.getHideFilesExplorer();
		add(new WebMarkupContainer("flink").setVisible(showFiles));
		add(new WebMarkupContainer("ftab").add(new FileTreePanel("tree") {
			private static final long serialVersionUID = 1L;

			@Override
			public void updateSizes() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void update(AjaxRequestTarget target, FileItem f) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void defineTrees() {
				FileExplorerItem f = new FileExplorerItem();
				f.setOwnerId(getUserId());
				selectedFile.setObject(f);
				treesView.add(selected = new FileItemTree<FileExplorerItem>(treesView.newChildId(), this, new FilesTreeProvider(null)));
				treesView.add(new FileItemTree<FileExplorerItem>(treesView.newChildId(), this, new FilesTreeProvider(roomId)));
				treesView.add(new FileItemTree<FlvRecording>(treesView.newChildId(), this, new MyRecordingTreeProvider()));
				treesView.add(new FileItemTree<FlvRecording>(treesView.newChildId(), this, new PublicRecordingTreeProvider(null, null)));
				for (Organisation_Users ou : getBean(UserDao.class).get(getUserId()).getOrganisation_users()) {
					Organisation o = ou.getOrganisation();
					treesView.add(new FileItemTree<FlvRecording>(treesView.newChildId(), this, new PublicRecordingTreeProvider(o.getId(), o.getName())));
				}
			}
			
			@Override
			public void createFolder(String name) {
				if (selectedFile.getObject() instanceof FlvRecording) {
					createRecordingFolder(name);
				} else {
					FileExplorerItem f = new FileExplorerItem();
					f.setFileName(name);
					f.setInsertedBy(getUserId());
					f.setInserted(new Date());
					f.setType(Type.Folder);;
					FileItem p = selectedFile.getObject();
					long parentId = p.getId();
					f.setParentItemId(Type.Folder == p.getType() && parentId > 0 ? parentId : null);
					f.setOwnerId(p.getOwnerId());
					f.setRoomId(p.getRoomId());
					getBean(FileExplorerItemDao.class).update(f);
				}
			}
		}).setVisible(showFiles));
		add(new JQueryBehavior(".room.sidebar.left .tabs", "tabs", new Options("active", showFiles && r.isFilesOpened() ? "ftab" : "utab")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void renderScript(JavaScriptHeaderItem script, IHeaderResponse response) {
				response.render(new PriorityHeaderItem(script));
			}
		});
		add(new Label("roomName", r.getName()));
		add(new Label("recording", "Recording started").setVisible(false)); //FIXME add/remove
		add(new Button("ask")); //FIXME add/remove
		final AjaxDownload download = new AjaxDownload(true) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getFileName() {
				return "public_" + roomId + ".jnlp";
			}
		};
		add(download);
		add(new Button("share").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				String app = "";
				try {
					ConfigurationDao cfgDao = getBean(ConfigurationDao.class);
					app = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("APPLICATION.jnlp"), "UTF-8");
					String baseUrl = cfgDao.getBaseUrl();
					URL url = new URL(baseUrl);
					String path = url.getPath();
					path = path.substring(1, path.indexOf('/', 2) + 1);
					app = app.replace("$codebase", baseUrl + "screenshare")
							.replace("$applicationName", cfgDao.getAppName())
							.replace("$protocol", cfgDao.getConfValue(CONFIG_FLASH_PROTOCOL, String.class, ""))
							.replace("$port", cfgDao.getConfValue(CONFIG_FLASH_PORT, String.class, ""))
							.replace("$host", url.getHost())
							.replace("$app", path + roomId)
							.replace("$userId", "" + getUserId())
							.replace("$publicSid", getSid())
							.replace("$labels", "<![CDATA[" + getLabels(730,  731,  732,  733,  734
									,  735,  737,  738,  739,  740
									,  741,  742,  844,  869,  870
									,  871,  872,  878, 1089, 1090
									, 1091, 1092, 1093, 1465, 1466
									, 1467, 1468, 1469, 1470, 1471
									, 1472, 1473, 1474, 1475, 1476
									, 1477, 1589) + "]]>")
							.replace("$defaultQuality", cfgDao.getConfValue(CONFIG_SCREENSHARING_QUALITY, String.class, ""))
							.replace("$defaultFps", cfgDao.getConfValue(CONFIG_SCREENSHARING_FPS, String.class, ""))
							.replace("$allowRecording", "true") //FIXME add/remove
							.replace("$allowPublishing", "true") //FIXME add/remove
							.replace("$keystore", "<![CDATA[]]>") //FIXME add/remove
							.replace("$password", "<![CDATA[]]>") //FIXME add/remove
							;
				} catch (Exception e) {
					log.error("Unexpected error while creating jnlp file", e);
				}
				download.setResourceStream(new StringResourceStream(app, "application/x-java-jnlp-file"));
				download.initiate(target);
			}
		})); //FIXME add/remove
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

	class FilesTreeProvider implements ITreeProvider<FileExplorerItem> {
		private static final long serialVersionUID = 1L;
		Long roomId = null;

		FilesTreeProvider(Long roomId) {
			this.roomId = roomId;
		}
		
		public void detach() {
			// TODO LDM should be used
		}

		public boolean hasChildren(FileExplorerItem node) {
			return node.getId() <= 0 || Type.Folder == node.getType();
		}

		public Iterator<? extends FileExplorerItem> getChildren(FileExplorerItem node) {
			FileExplorerItemDao dao = getBean(FileExplorerItemDao.class);
			List<FileExplorerItem> list = null;
			if (node.getId() == 0) {
				list = dao.getByOwner(node.getOwnerId());
			} else if (node.getId() < 0) {
				list = dao.getByRoom(roomId);
			} else {
				list = dao.getByParent(node.getId());
			}
			return list.iterator();
		}

		public IModel<FileExplorerItem> model(FileExplorerItem object) {
			// TODO LDM should be used
			return Model.of(object);
		}

		@Override
		public Iterator<? extends FileExplorerItem> getRoots() {
			FileExplorerItem f = new FileExplorerItem();
			f.setRoomId(roomId);
			if (roomId == null) {
				f.setId(0L);
				f.setOwnerId(getUserId());
				f.setFileName(WebSession.getString(706));
			} else {
				f.setId(-roomId);
				f.setFileName(WebSession.getString(707));
			}
			return Arrays.asList(f).iterator();
		}
	}		
}
