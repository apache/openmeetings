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

import com.github.openjson.JSONObject;

public class WebcamStreamDesc extends StreamDesc {
	private boolean camEnabled = false;
	private boolean micEnabled = false;

	public WebcamStreamDesc(WebcamStreamDesc sd) {
		super(sd);
		this.camEnabled = sd.camEnabled;
		this.micEnabled = sd.micEnabled;
	}

	public WebcamStreamDesc(final Client client, Activity toggle) {
		super(client, StreamType.WEBCAM);
		setWidth(client.getWidth());
		setHeight(client.getHeight());
		// we will add all allowed activities here
		if (client.isAllowed(Activity.AUDIO)) {
			activities.add(Activity.AUDIO);
		}
		if (client.isAllowed(Activity.VIDEO)) {
			activities.add(Activity.VIDEO);
		}
		if (has(Activity.AUDIO) && has(Activity.VIDEO)) {
			activities.add(Activity.AUDIO_VIDEO);
		}
		switch (toggle) {
			case AUDIO:
				if (has(toggle)) {
					micEnabled = true;
				}
				break;
			case VIDEO:
				if (has(toggle)) {
					camEnabled = true;
				}
				break;
			case AUDIO_VIDEO:
				if (has(toggle)) {
					micEnabled = true;
					camEnabled = true;
				}
				break;
			default:
		}
	}

	@Override
	public StreamDesc remove(Activity a) {
		super.remove(a);
		switch (a) {
			case AUDIO:
				micEnabled = false;
				break;
			case VIDEO:
				camEnabled = false;
				break;
			case AUDIO_VIDEO:
				micEnabled = false;
				camEnabled = false;
				break;
			default:
		}
		return this;
	}

	public void toggle(Activity toggle) {
		switch (toggle) {
			case AUDIO:
				micEnabled = !micEnabled;
				break;
			case VIDEO:
				camEnabled = !camEnabled;
				break;
			default:
		}
	}

	public boolean isBroadcasting() {
		return !activities.isEmpty() && (camEnabled || micEnabled);
	}

	@Override
	protected boolean allowed(Activity a) {
		return Activity.AUDIO == a || Activity.VIDEO == a || Activity.AUDIO_VIDEO == a;
	}

	@Override
	public JSONObject toJson(boolean self) {
		return super.toJson(self)
				.put("camEnabled", camEnabled).put("micEnabled", micEnabled);
	}
}
