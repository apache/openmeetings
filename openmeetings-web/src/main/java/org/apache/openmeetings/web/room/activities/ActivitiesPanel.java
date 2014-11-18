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
package org.apache.openmeetings.web.room.activities;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.room.RoomPanel.isModerator;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.room.activities.Activity.Type;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ActivitiesPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(ActivitiesPanel.class, webAppRootKey);
	private static final String PARAM_UID = "uid";
	private static final String ACTION = "action";
	private static final String PARAM_ROOM_ID = "roomid";
	private enum Action {
		accept, decline, close
	};
	private final Queue<Activity> activities = new ConcurrentLinkedQueue<Activity>();
	private final long roomId;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final AbstractDefaultAjaxBehavior action = new AbstractDefaultAjaxBehavior() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void respond(AjaxRequestTarget target) {
			try {
				long uid = getRequest().getRequestParameters().getParameterValue(PARAM_UID).toLong(); 
				long roomId = getRequest().getRequestParameters().getParameterValue(PARAM_ROOM_ID).toLong();
				assert(ActivitiesPanel.this.roomId == roomId);
				Action action = Action.valueOf(getRequest().getRequestParameters().getParameterValue(ACTION).toString());
				Activity a = null;//activities.get(uid);
				if (a != null) {
					if (action == Action.close && (a.getType() == Type.roomEnter || a.getType() == Type.roomExit)) {
						activities.remove(uid);
					} else if (isModerator(getUserId(), roomId)) {
						switch (a.getType()) {
							case askModeration:
								break;
							default:
								break;	
						}
					}
				} else {
					log.error("It seems like we are being hacked!!!!");
				}
			} catch (Exception e) {
				log.error("Unexpected exception while processing activity action", e);
			}
		}
	};/*
	private ListView<Activity> lv = new ListView<Activity>("activities", activities) {
		@Override
		protected void populateItem(ListItem<Activity> arg0) {
			// TODO Auto-generated method stub
			
		}
	};*/

	public void addActivity(Long userId, Activity.Type type, AjaxRequestTarget target) {
		if (getUserId() != userId) {
			Activity a = new Activity(userId,  type);
			//activities.put(a.getUid(), a);
			target.add(container);
		}
	}
	
	public ActivitiesPanel(String id, long roomId) {
		super(id);
		this.roomId = roomId;
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		add(container.setOutputMarkupId(true));
		add(action);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ActivitiesPanel.class, "activities.js"))));
		response.render(CssHeaderItem.forUrl("css/activities.css"));
	}
}
