package org.openmeetings.app.data.flvrecord.listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;
//import org.openmeetings.app.data.flvrecord.FlvRecordingHelperDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
//import org.openmeetings.app.hibernate.beans.flvrecord.FlvRecordingHelper;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.io.IStreamableFile;
import org.red5.io.IStreamableFileFactory;
import org.red5.io.IStreamableFileService;
import org.red5.io.ITag;
import org.red5.io.ITagWriter;
import org.red5.io.StreamableFileFactory;
import org.red5.io.flv.impl.FLVWriter;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.red5.server.api.ScopeUtils;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.stream.IStreamListener;
import org.red5.server.api.stream.IStreamPacket;
import org.red5.server.net.rtmp.event.SerializeUtils;
import org.slf4j.Logger;

public class StreamTranscodingListener implements IStreamListener {
	
	private ITagWriter writer = null;
	
	private File file;
	
	private boolean debug = false;
	
	private IScope scope;
	
	private int startTimeStamp = -1;
	
	private int lastTimeStamp = -1;
	
	private int lastStreamPacketTimeStamp = -1;
	private Date startedSessionTimeDate = null;
	
	private long byteCount = 0;

	private String folderPath = null;

	private Long flvRecordingMetaDataId = null;
	private List<FlvRecordingMetaDelta> flvRecordingMetaDeltas;
	//private Long flvRecordingId = null;

	private String streamName = "";

	private boolean isClosed = false;

	private boolean isScreenData = false;

	private int duration;
	
	private static final Logger log = Red5LoggerFactory.getLogger(StreamTranscodingListener.class, ScopeApplicationAdapter.webAppRootKey);

	
	public StreamTranscodingListener(String streamName, IScope scope, 
			Long flvRecordingMetaDataId, boolean isScreenData) {
		super();
		this.startedSessionTimeDate = new Date();
		this.isScreenData  = isScreenData;
		this.streamName  = streamName;
		this.flvRecordingMetaDataId = flvRecordingMetaDataId;
		this.flvRecordingMetaDeltas = new LinkedList<FlvRecordingMetaDelta>();
		//this.flvRecordingId = flvRecordingId;
		this.scope = scope;
	}

	public Long getFlvRecordingMetaDataId() {
		return flvRecordingMetaDataId;
	}

	public void packetReceived(IBroadcastStream broadcastStream, IStreamPacket streampacket) {
		// TODO Auto-generated method stub
		try {
			
			if (this.isClosed) {
				//Already closed this One
				return;
			}
			
			if (this.isScreenData) {
				//Screen Recorder has No Audio
				if (streampacket.getDataType() == 8) {
					return;
				}
			} else {
				//Audio Recorder needs no Video
				if (streampacket.getDataType() == 9) {
					return;
				}
			}
			
			if (startTimeStamp == -1) {
				startTimeStamp = streampacket.getTimestamp();
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

			long deltaTime = 0;
			
			//log.debug("hasAudio :: "+streampacket.getDataType()); //8 == audio data
			
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
				
				if (debug) {
					
					this.folderPath  = ScopeApplicationAdapter.webAppPath + File.separatorChar + file.getName().substring(0, file.getName().length()-4);
					
					File folder_debug = new File (folderPath);
					if (!folder_debug.exists()) {
						folder_debug.mkdir();
					}
				}
				
				init();
				//writer = new FLVWriter(,false);
				//writer.writeHeader();
			}
			
			if (streampacket.getDataType() == 8) {
				
				int timeStamp = streampacket.getTimestamp();
				
				this.lastStreamPacketTimeStamp = streampacket.getTimestamp();
				
				timeStamp -= startTimeStamp;
				
				if (lastTimeStamp == -1) {
					deltaTime = timeStamp; 
				} else {
					deltaTime = timeStamp - lastTimeStamp; 
				}
				
				lastTimeStamp = timeStamp;
				
				//log.debug("---- Is Audio Data :: "+streampacket.getTimestamp());
				//log.debug("---- Is Audio Length :: "+streampacket.getData().array().length);
				
				//log.debug("hasAudio :: "+streampacket.getTimestamp());
				//audioTs += streampacket.getTimestamp();
	
				//IoBuffer audioData = streampacket.getData().asReadOnlyBuffer();
				//byte[] data = SerializeUtils.ByteBufferToByteArray( audioData );
				
				
				//Try to fix missing packets, Standard packet Sizes are 46/47 ms long
				
				if (deltaTime > 55){
					
					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();
					
					flvRecordingMetaDelta.setDeltaTime(deltaTime);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(timeStamp);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta.setStartTime(this.startedSessionTimeDate);
					
					
					Date current_date = new Date();
					Long deltaTimeStamp = this.startedSessionTimeDate.getTime() - current_date.getTime();
					
					this.duration = Math.max(this.duration, timeStamp + this.writer.getOffset());
					flvRecordingMetaDelta.setDuration(this.duration);
					
					Long missingTime = deltaTimeStamp - timeStamp;
					
					flvRecordingMetaDelta.setMissingTime(missingTime);
					
					flvRecordingMetaDelta.setCurrentTime(current_date);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);
					
					
					this.flvRecordingMetaDeltas.add(flvRecordingMetaDelta);
					
					//Trying to simulate Packets, does not work, Packets must contains some decoded header Information
//					int numberOfMissingPackets = (int) Math.round(deltaTime/47);
//					
//					log.debug("deltaTime :: numberOfMissingPackets "+deltaTime+" :: "+numberOfMissingPackets);
//					
//					for (int i=0;i<numberOfMissingPackets;i++){
//						
//						ITag tag = new Tag();
//			            
//			            tag.setDataType(streampacket.getDataType());
//			            
//			            IoBuffer data = FLVWriterHelper.getAudioBuffer();
//			
//			            log.debug("write packet to :: "+timeStamp + (numberOfMissingPackets * 47));
//			            
//						tag.setBodySize(data.limit());
//						tag.setTimestamp(timeStamp + (numberOfMissingPackets * 47));
//						tag.setBody(data);
//						
//						//writer.writeTag(tag);
//						
//					}
					
				}
	            
	            ITag tag = new Tag();
	            tag.setDataType(streampacket.getDataType());
	            
	            //log.debug("data.limit() :: "+data.limit());
				tag.setBodySize(data.limit());
				tag.setTimestamp(timeStamp);
				tag.setBody(data);
				
				//log.debug("duration: "+flvWriter.getDuration());
				
				if (debug) {
					
					String filePath = folderPath + File.separatorChar 
							+ "Packet"
							+"_t_"+tag.getTimestamp()
							+"_d_"+deltaTime
							+"_s_"+tag.getBodySize()
							+".flv";
					
					//File out = new File();
					
					FLVDebugWriter flvWriterDebug = new FLVDebugWriter( new File( filePath ), false);
					
					flvWriterDebug.writeBytesFromTag(streampacket.getData().asReadOnlyBuffer());

					flvWriterDebug.closeBytesFile();
					
				}

				writer.writeTag(tag);
	
			} else if (streampacket.getDataType() == 9) {
				
				//Video data is handled just without calculating deltas
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
			
			//log.debug("hasAudio :: "+broadcastStream.getCodecInfo().hasAudio());
			
			//log.debug("Audio codec Name :: "+broadcastStream.getCodecInfo().getAudioCodecName());
			//log.debug("Data Length "+streampacket.getData()
			//streampacket.getData()
			
			//broadcastStream.getProvider()
			
		} catch (IOException e) {
			log.error("[packetReceived]",e);
		} catch (Exception e) {
			log.error("[packetReceived]",e);
		}
	}
	
	/**

     * Initialization

     *

     * @throws IOException          I/O exception

     */

    private void init() throws IOException {

		
		IStreamableFileFactory factory = (IStreamableFileFactory) ScopeUtils

				.getScopeService(scope, IStreamableFileFactory.class,

						StreamableFileFactory.class);

		File folder = file.getParentFile();

		if (!folder.exists()) {

			if (!folder.mkdirs()) {

				throw new IOException("Could not create parent folder");

			}

		}

		if (!file.isFile()) {

			// Maybe the (previously existing) file has been deleted

			file.createNewFile();

		} else if (!file.canWrite()) {

			throw new IOException("The file is read-only");

		}

		IStreamableFileService service = factory.getService(file);

		IStreamableFile flv = service.getStreamableFile(file);

		writer = flv.getWriter();

	}
    
	public void closeStream() throws Exception {
		if (writer != null && !this.isClosed) {
			try {
				
				log.debug("#################### -start- closeStream ########################");
				log.debug("#################### -start- closeStream ########################");
				FLVWriter flvWriter = (FLVWriter) writer;
				//log.debug("duration: "+flvWriter.getDuration());
				//log.debug(writer.getClass().getName());
				
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

	public void streamResetEvent() {
		// TODO Auto-generated method stub
		
	}
	
}
