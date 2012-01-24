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
package de.medint.rtpsharer.datasource;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;



/**
 * A DataSource to read ScreenCaptures and
 * turn that into a stream of JMF buffers.
 * The DataSource is not seekable or positionable.
 */
public class ImageDataSource extends PushBufferDataSource {
	ImageSourceStream streams[];
	
	 protected boolean started = false;
	 protected boolean connected = false;

	public ImageDataSource(int width, int height, int frameRate) {
		streams = new ImageSourceStream[1];
		streams[0] = new ImageSourceStream(width, height, frameRate);
	}

	public void setLocator(MediaLocator source) {
	
	}

	public MediaLocator getLocator() {
		return null;
	}

	/**
	 * Content type is of RAW since we are sending buffers of video
	 * frames without a container format.
	 */	
	public String getContentType() {
		return ContentDescriptor.RAW;
	}

	public void connect() {
		if (connected)
            return;
	 connected = true;
	}

	public void disconnect() {
		if(started)
            stop();
        
        connected = false;
	}

	public void start() {
		System.out.println("start");
		
        if (started)
            return;
        
	started = true;
	streams[0].start(true);
	
	}

	
	
	public void stop() {
		if ((!connected) || (!started))
		    return;
		started = false;
		streams[0].start(false);
	}

	/**
	 * Return the ImageSourceStreams.
	 */
	public PushBufferStream[] getStreams() {
		return streams;
	}

	/**
	 * We could have derived the duration from the number of
	 * frames and frame rate.  But for the purpose of this program,
	 * it's not necessary.
	 */
	public Time getDuration() {
		return DURATION_UNKNOWN;
	}

	public Object[] getControls() {
		return new Object[0];
	}

	public Object getControl(String type) {
		return null;
	}
}
