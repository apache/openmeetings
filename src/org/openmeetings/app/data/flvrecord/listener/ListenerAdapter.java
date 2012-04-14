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
package org.openmeetings.app.data.flvrecord.listener;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.red5.io.IStreamableFile;
import org.red5.io.IStreamableFileFactory;
import org.red5.io.IStreamableFileService;
import org.red5.io.ITagWriter;
import org.red5.io.StreamableFileFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;

public abstract class ListenerAdapter implements IStreamListener {
	
	protected ITagWriter writer = null;
	
	protected Long flvRecordingMetaDataId = null;
	protected List<FlvRecordingMetaDelta> flvRecordingMetaDeltas;
	
	protected Date startedSessionTimeDate = null;
	
	protected File file;
	
	protected IScope scope;
	
	protected boolean isClosed = false;

	protected boolean isScreenData = false;
	
	protected Long offset = 0L;
	
	protected String streamName = "";

	protected boolean isInterview;
	
	public ListenerAdapter(String streamName, IScope scope, 
			Long flvRecordingMetaDataId, boolean isScreenData,
			boolean isInterview) {
		super();
		this.startedSessionTimeDate = new Date();
		this.isScreenData  = isScreenData;
		this.streamName  = streamName;
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
		this.flvRecordingMetaDeltas = new LinkedList<FlvRecordingMetaDelta>();
		this.scope = scope;
		this.isInterview = isInterview;
	}

	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}	
	
	public abstract void closeStream();
	
	/**
     * Initialization
     *
     * @throws IOException          I/O exception
     */
    protected void init() throws IOException {

		IStreamableFileFactory factory = (IStreamableFileFactory) ScopeUtils
				.getScopeService(this.scope, IStreamableFileFactory.class,
						StreamableFileFactory.class);
		
		File folder = file.getParentFile();

		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				throw new IOException("Could not create parent folder");
			}
		}

		if (!this.file.isFile()) {

			// Maybe the (previously existing) file has been deleted
			this.file.createNewFile();

		} else if (!file.canWrite()) {
			throw new IOException("The file is read-only");
		}

		IStreamableFileService service = factory.getService(this.file);
		IStreamableFile flv = service.getStreamableFile(this.file);
		this.writer = flv.getWriter();

	}

}
