package org.openmeetings.app.data.flvrecord.listener;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingMetaDelta;
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

public class ListenerAdapter implements IStreamListener {
	
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
	
	public void packetReceived(IBroadcastStream arg0, IStreamPacket arg1) {
		// Auto-generated method stub
		
	}

	public void closeStream() throws Exception {
		// Auto-generated method stub
	}
	
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
