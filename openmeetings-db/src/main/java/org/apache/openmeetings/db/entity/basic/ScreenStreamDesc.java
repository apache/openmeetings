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
package org.apache.openmeetings.db.entity.basic;

import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamType;

public class ScreenStreamDesc extends StreamDesc {
	public ScreenStreamDesc(ScreenStreamDesc sd) {
		super(sd);
	}

	public ScreenStreamDesc(final Client client, Activity toggle) {
		super(client, StreamType.SCREEN);
		add(toggle);
		setWidth(800);
		setHeight(600);
	}

	@Override
	protected boolean allowed(Activity a) {
		return Activity.SCREEN == a || Activity.RECORD == a;
	}
}
