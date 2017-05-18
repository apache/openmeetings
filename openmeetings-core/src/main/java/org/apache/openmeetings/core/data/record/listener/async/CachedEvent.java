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
package org.apache.openmeetings.core.data.record.listener.async;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.net.rtmp.event.VideoData.FrameType;

public class CachedEvent implements IStreamPacket {
	private byte dataType;
	private int timestamp; //this is the timeStamp, showing the time elapsed since the microphone was turned on
	private IoBuffer data;
	private Date currentTime; //this is the actually current timeStamp when the packet with audio data did enter the server
	private FrameType frameType = FrameType.UNKNOWN;

	public Date getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Date currentTime) {
		this.currentTime = currentTime;
	}

	public void setDataType(byte dataType) {
		this.dataType = dataType;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public void setData(IoBuffer data) {
		this.data = data;
	}

	@Override
	public byte getDataType() {
		return dataType;
	}

	@Override
	public int getTimestamp() {
		return timestamp;
	}

	@Override
	public IoBuffer getData() {
		return data;
	}

	public FrameType getFrameType() {
		return frameType;
	}

	public void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}
}
