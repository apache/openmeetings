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
package org.apache.openmeetings.core.remote;

import java.util.Collection;
import java.util.stream.Stream;

import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.basic.Client.Activity;
import org.apache.openmeetings.db.entity.basic.Client.StreamDesc;
import org.apache.openmeetings.db.entity.record.Recording;

import com.github.openjson.JSONObject;

public interface IStreamProcessor {

	void toggleActivity(Client c, Activity a);

	void rightsUpdated(Client c);

	// Sharing
	boolean hasRightsToShare(Client c);

	Stream<KStream> getByRoom(Long roomId);

	KStream getByUid(String uid);

	void onMessage(Client c, final String cmdId, JSONObject msg);

	void startBroadcast(KStream stream, StreamDesc sd, String sdpOffer, Runnable then);

	boolean startConvertion(Recording rec);

	void pauseSharing(Client c, String uid);

	StreamDesc doStopSharing(String sid, String uid);

	Client getBySid(String sid);

	boolean screenShareAllowed(Client c);

	boolean isSharing(Long roomId);

	void addStream(KStream stream);

	boolean hasRightsToRecord(Client c);

	boolean recordingAllowed(Client c);

	void startRecording(Client c);

	void stopRecording(Client c);

	boolean isRecording(Long roomId);

	Collection<KStream> getStreams();

	boolean hasStream(String uid);

	void remove(Client c);

	void release(AbstractStream stream, boolean releaseStream);

	void destroy();

	JSONObject newStoppedMsg(StreamDesc sd);

}
