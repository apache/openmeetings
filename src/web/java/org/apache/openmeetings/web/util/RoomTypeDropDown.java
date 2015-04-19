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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getLanguage;

import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

public class RoomTypeDropDown extends DropDownChoice<RoomType> {
	private static final long serialVersionUID = 1L;
	
	public static List<RoomType> getRoomTypes() {
		return getBean(RoomTypeDao.class).getAll(getLanguage());
	}
	
	public RoomTypeDropDown(String id) {
		super(id);
		setChoices(getRoomTypes());
		setChoiceRenderer(new IChoiceRenderer<RoomType>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(RoomType object) {
				return getString("" + object.getFieldvalues_id());
			}

			@Override
			public String getIdValue(RoomType object, int index) {
				return "" + object.getRoomtypes_id();
			}
		});
	}
}
