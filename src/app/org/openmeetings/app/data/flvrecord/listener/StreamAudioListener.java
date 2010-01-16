package org.openmeetings.app.data.flvrecord.listener;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingMetaDelta;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.io.IStreamableFile;
import org.red5.io.IStreamableFileFactory;
import org.red5.io.IStreamableFileService;
import org.red5.io.ITag;
import org.red5.io.StreamableFileFactory;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamPacket;
import org.slf4j.Logger;

public class StreamAudioListener extends ListenerAdapter {
	
	private int startTimeStamp = -1;
	
	private int lastTimeStamp = -1;
	
	private int lastStreamPacketTimeStamp = -1;
	
	private long byteCount = 0;

	private int duration = 0;
	
	private static final Logger log = Red5LoggerFactory.getLogger(StreamAudioListener.class, "openmeetings");

	public StreamAudioListener(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData) {
		// Auto-generated method stub
		super(streamName, scope, flvRecordingMetaDataId, isScreenData);
	}

	@Override
	public void packetReceived(IBroadcastStream broadcastStream, IStreamPacket streampacket) {
		try {
			
			//We only care about audio at this moment
			if (streampacket.getDataType() == 8) {
			
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
					
					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
					
					flvRecordingMetaDelta.setDeltaTime(delta);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(0);
					flvRecordingMetaDelta.setDebugStatus("INIT AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(true);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
					flvRecordingMetaDelta.setStreamCreationTime(broadcastStream.getCreationTime());
					flvRecordingMetaDelta.setStreamCreationTimeDate(new Date(broadcastStream.getCreationTime()));
					
					Date current_date = new Date();
					Long deltaTimeStamp = current_date.getTime() - this.startedSessionTimeDate.getTime();
					
					//this.duration = Math.max(this.duration, 0 + this.writer.getOffset());
					flvRecordingMetaDelta.setDuration(0);
					
					Long missingTime = deltaTimeStamp - 0;
					
					flvRecordingMetaDelta.setMissingTime(missingTime);
					
					flvRecordingMetaDelta.setCurrentTime(current_date);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);
					
					this.flvRecordingMetaDeltas.add(flvRecordingMetaDelta);
					
					//That will be not bigger then long value
					startTimeStamp = (int) (streampacket.getTimestamp() - delta);
					
					//We have to set that to bypass the initial delta
					lastTimeStamp = startTimeStamp;
				}
				
				long deltaTime = 0;
				
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
				
				this.lastStreamPacketTimeStamp = streampacket.getTimestamp();
				
				timeStamp -= startTimeStamp;
				
				if (lastTimeStamp == -1) {
					deltaTime = timeStamp; 
				} else {
					deltaTime = timeStamp - lastTimeStamp; 
				}
				
				lastTimeStamp = timeStamp;
				
				if (deltaTime > 55){
					
					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
					
					flvRecordingMetaDelta.setDeltaTime(deltaTime);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(timeStamp);
					flvRecordingMetaDelta.setDebugStatus("RUN AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(false);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
					
					
					Date current_date = new Date();
					Long deltaTimeStamp = current_date.getTime() - this.startedSessionTimeDate.getTime();
					
					this.duration = Math.max(this.duration, timeStamp + this.writer.getOffset());
					flvRecordingMetaDelta.setDuration(this.duration);
					
					Long missingTime = deltaTimeStamp - timeStamp;
					
					flvRecordingMetaDelta.setMissingTime(missingTime);
					
					flvRecordingMetaDelta.setCurrentTime(current_date);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);
					
					this.flvRecordingMetaDeltas.add(flvRecordingMetaDelta);
					
				}
				
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
				
				log.debug("#################### -start- closeStream ########################");
				log.debug("#################### -start- closeStream ########################");
				
				writer.close();
				
				this.isClosed  = true;
				
				if (!this.isScreenData) {
					
					//We do not add any End Padding or count the gaps for the Screen Data, 
					//cause there is no!
					
					long deltaRecordingTime = new Date().getTime() - this.startedSessionTimeDate.getTime();
					 
					log.debug("lastTimeStamp :: "+this.lastTimeStamp);
					log.debug("lastStreamPacketTimeStamp :: "+this.lastStreamPacketTimeStamp);
					log.debug("deltaRecordingTime :: "+deltaRecordingTime);
					
					long deltaTimePaddingEnd = deltaRecordingTime - this.lastTimeStamp;
					
					log.debug("deltaTimePaddingEnd :: "+deltaTimePaddingEnd);
					
					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
					
					flvRecordingMetaDelta.setDeltaTime(deltaTimePaddingEnd);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(this.lastTimeStamp);
					flvRecordingMetaDelta.setDebugStatus("END AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(false);
					flvRecordingMetaDelta.setIsEndPadding(true);
					flvRecordingMetaDelta.setDataLengthPacket(null);
					flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
					flvRecordingMetaDelta.setCurrentTime(new Date());
					
					this.flvRecordingMetaDeltas.add(flvRecordingMetaDelta);
					
				}
				
				for (FlvRecordingMetaDelta flvRecordingMetaDeltaLoop : this.flvRecordingMetaDeltas) {
					
					FlvRecordingMetaDeltaDaoImpl.getInstance().addFlvRecordingMetaDelta(flvRecordingMetaDeltaLoop);
					
				}
				
			} catch (Exception err) {
				log.error("[closeStream]",err);
			}
			log.debug("#################### -end- closeStream ########################");
			log.debug("#################### -end- closeStream ########################");
		}
	}
	
	
}
