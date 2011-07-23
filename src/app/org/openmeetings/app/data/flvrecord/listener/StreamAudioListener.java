package org.openmeetings.app.data.flvrecord.listener;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
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
	
	private long initialDelta = 0;
	
	private Integer lastTimeStamp = -1;
	
	private int lastStreamPacketTimeStamp = -1;
	
	private long byteCount = 0;

	private int duration = 0;
	
	private int purePacketLastTimeStamp = 0;
	
	private static final Logger log = Red5LoggerFactory.getLogger(StreamAudioListener.class, ScopeApplicationAdapter.webAppRootKey);

	public StreamAudioListener(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData, boolean isInterview) {
		// Auto-generated method stub
		super(streamName, scope, flvRecordingMetaDataId, isScreenData, isInterview);
	}

	
	
	public void streamResetEvent() {
		
		try {
		
			FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
			
			flvRecordingMetaDelta.setDeltaTime(0L);
			flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
			flvRecordingMetaDelta.setTimeStamp(0);
			flvRecordingMetaDelta.setDebugStatus("RESET TYPE :: ");
			flvRecordingMetaDelta.setIsStartPadding(true);
			flvRecordingMetaDelta.setIsEndPadding(false);
			flvRecordingMetaDelta.setDataLengthPacket(0);
			flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
			flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
			flvRecordingMetaDelta.setStreamCreationTime(null);
			flvRecordingMetaDelta.setStreamCreationTimeDate(null);
			flvRecordingMetaDelta.setPacketTimeStamp(lastTimeStamp);
			
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
			
			FlvRecordingMetaDeltaDaoImpl.getInstance().addFlvRecordingMetaDelta(flvRecordingMetaDelta);
			
			this.offset += lastTimeStamp;
			
		} catch (Exception e) {
			log.error("[packetReceived]",e);
		}
		
	}



	@Override
	public void packetReceived(IBroadcastStream broadcastStream, IStreamPacket streampacket) {
		try {
			
			
//			//Here we handle Reseted timestamps!
//			if (startTimeStamp != -1) {
//				
//				long deltaTimeBetweenTimeStampes = purePacketLastTimeStamp - streampacket.getTimestamp();
//				
//				System.out.println("deltaTimeBetweenTimeStampes "+deltaTimeBetweenTimeStampes);
//				
//				if (deltaTimeBetweenTimeStampes < -100) {
//					
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("We have detected a Reset in the TimeStamps!");
//					
//					System.out.println("streampacket.getTimestamp() :: "+streampacket.getTimestamp());
//					System.out.println("purePacketLastTimeStamp :: "+purePacketLastTimeStamp);
//					System.out.println("startTimeStamp :: "+startTimeStamp);
//					
//					int debugTimeStamp = purePacketLastTimeStamp - startTimeStamp;
//					
//					System.out.println("debugTimeStamp :: "+debugTimeStamp);
//					System.out.println("offset :: "+offset);
//					
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					log.debug("############################################");
//					
//					this.offset += debugTimeStamp;
//					
//				}
//				purePacketLastTimeStamp = streampacket.getTimestamp();
//			}
			
			//log.debug("streampacket.getTimestamp() :: "+streampacket.getTimestamp());
			
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
					
					this.initialDelta = new Date().getTime() - this.startedSessionTimeDate.getTime();
					
					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
					
					flvRecordingMetaDelta.setDeltaTime(this.initialDelta);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(0);
					flvRecordingMetaDelta.setDebugStatus("INIT AUDIO");
					flvRecordingMetaDelta.setOffset(this.offset);
					flvRecordingMetaDelta.setIsStartPadding(true);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
					flvRecordingMetaDelta.setStreamCreationTime(broadcastStream.getCreationTime());
					flvRecordingMetaDelta.setStreamCreationTimeDate(new Date(broadcastStream.getCreationTime()));
					flvRecordingMetaDelta.setPacketTimeStamp(streampacket.getTimestamp());
					
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
					
					FlvRecordingMetaDeltaDaoImpl.getInstance().addFlvRecordingMetaDelta(flvRecordingMetaDelta);
					
					//That will be not bigger then long value
					this.startTimeStamp = (int) (streampacket.getTimestamp());
					
					//We have to set that to bypass the initial delta
					//lastTimeStamp = startTimeStamp;
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
				
				this.lastStreamPacketTimeStamp = streampacket.getTimestamp();
				
				timeStamp -= this.startTimeStamp;
				
				timeStamp += this.offset;
				
				long deltaTime = 0;
				if (lastTimeStamp == -1) {
					deltaTime = 0; //Offset at the beginning is calculated above
				} else {
					deltaTime = timeStamp - lastTimeStamp; 
				}
				
				Long preLastTimeStamp = Long.parseLong(lastTimeStamp.toString());
				
				lastTimeStamp = timeStamp;
				
				if (deltaTime > 75){
					
					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
					
					flvRecordingMetaDelta.setDeltaTime(deltaTime);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(timeStamp);
					flvRecordingMetaDelta.setDebugStatus("RUN AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(false);
					flvRecordingMetaDelta.setLastTimeStamp(preLastTimeStamp);
					flvRecordingMetaDelta.setOffset(this.offset);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
					flvRecordingMetaDelta.setStreamCreationTime(broadcastStream.getCreationTime());
					flvRecordingMetaDelta.setStreamCreationTimeDate(new Date(broadcastStream.getCreationTime()));
					flvRecordingMetaDelta.setPacketTimeStamp(streampacket.getTimestamp());
					
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
					
					FlvRecordingMetaDeltaDaoImpl.getInstance().addFlvRecordingMetaDelta(flvRecordingMetaDelta);
					
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
				
				//We do not add any End Padding or count the gaps for the Screen Data, 
				//cause there is no!
				
				long deltaRecordingTime = new Date().getTime() - this.startedSessionTimeDate.getTime();
				 
				log.debug("lastTimeStamp :: "+this.lastTimeStamp);
				log.debug("lastStreamPacketTimeStamp :: "+this.lastStreamPacketTimeStamp);
				log.debug("deltaRecordingTime :: "+deltaRecordingTime);
				
				long deltaTimePaddingEnd = deltaRecordingTime - this.lastTimeStamp - this.initialDelta;
				
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
					
				FlvRecordingMetaDeltaDaoImpl.getInstance().addFlvRecordingMetaDelta(flvRecordingMetaDelta);
				
//				for (FlvRecordingMetaDelta flvRecordingMetaDeltaLoop : this.flvRecordingMetaDeltas) {
//					
//					FlvRecordingMetaDeltaDaoImpl.getInstance().addFlvRecordingMetaDelta(flvRecordingMetaDeltaLoop);
//					
//				}
				
			} catch (Exception err) {
				log.error("[closeStream]",err);
			}
			log.debug("#################### -end- closeStream ########################");
			log.debug("#################### -end- closeStream ########################");
		}
	}
	
	
}
