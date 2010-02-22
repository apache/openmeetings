package org.openmeetings.app.data.flvrecord.listener;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.io.ITag;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamPacket;
import org.slf4j.Logger;

public class StreamScreenListener extends ListenerAdapter {
	
	private int startTimeStamp = -1;
	
	private long byteCount = 0;
	
	private static final Logger log = Red5LoggerFactory.getLogger(StreamScreenListener.class, "openmeetings");

	public StreamScreenListener(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData,boolean isInterview) {
		super(streamName, scope, flvRecordingMetaDataId, isScreenData,isInterview);
		// Auto-generated constructor stub
	}
	
	@Override
	public void packetReceived(IBroadcastStream broadcastStream, IStreamPacket streampacket) {
		try {
			
			//We only are concerned about video at this moment
			if (streampacket.getDataType() == 9) {
			
				if (this.isClosed) {
					//Already closed this One
					return;
				}
				
				if (streampacket.getTimestamp() <= 0) {
					log.warn("Negative TimeStamp");
					return;
				}
				
				IoBuffer data = streampacket.getData().asReadOnlyBuffer();
				
				if (data.limit() == 0) {
					return;
				}
				
				this.byteCount += data.limit();
				
				if (startTimeStamp == -1) {
					
					//Calculate the delta between the initial start and the first audio-packet data
					
					long delta = new Date().getTime() - this.startedSessionTimeDate.getTime();
					
					//That will be not bigger then long value
					startTimeStamp = (int) (streampacket.getTimestamp() - delta);
				}
				
				if (writer == null) {
					
					File folder = new File(ScopeApplicationAdapter.webAppPath + File.separatorChar +
										"streams" + File.separatorChar +
										this.scope.getName());
					
					if (!folder.exists()) {
						folder.mkdir();
					}
					
					String flvName = ScopeApplicationAdapter.webAppPath + File.separatorChar +
											"streams" + File.separatorChar +
											this.scope.getName() + File.separatorChar +
											this.streamName+".flv";
					
					file = new File(flvName);
					init();
				}
				
				int timeStamp = streampacket.getTimestamp();
				
				timeStamp -= startTimeStamp;
				
	            ITag tag = new Tag();
	            tag.setDataType(streampacket.getDataType());
	            
	            //log.debug("data.limit() :: "+data.limit());
				tag.setBodySize(data.limit());
				tag.setTimestamp(timeStamp);
				tag.setBody(data);
				
				writer.writeTag(tag);
			
			}
			
		} catch (IOException e) {
			log.error("[packetReceived]",e);
		} catch (Exception e) {
			log.error("[packetReceived]",e);
		}
	}		
	
	@Override
	public void closeStream() throws Exception {
		if (writer != null && !this.isClosed) {
			try {
				
				writer.close();
				
				this.isClosed  = true;
				
			} catch (Exception err) {
				log.error("[closeStream]",err);
			}
		}
	}	
				
}
