package org.openmeetings.app.data.flvrecord.listener.async;

import java.io.IOException;
import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.openmeetings.app.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.openmeetings.app.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.red5.io.ITag;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.IScope;
import org.slf4j.Logger;

public class StreamAudioWriter extends BaseStreamWriter {

	private static final Logger log = Red5LoggerFactory.getLogger(
			StreamAudioWriter.class, OpenmeetingsVariables.webAppRootKey);

	private int duration = 0;

	private int startTimeStamp = -1;

	private long initialDelta = 0;

	private Integer lastTimeStamp = -1;
	private Date lastcurrentTime = null;

	private int lastStreamPacketTimeStamp = -1;

	private long byteCount = 0;
	
	// Autowire is not possible
	protected final FlvRecordingMetaDeltaDaoImpl flvRecordingMetaDeltaDao;
	protected final FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDao;

	private boolean isInterview = false;
	
	public StreamAudioWriter(String streamName, IScope scope,
			Long flvRecordingMetaDataId, boolean isScreenData,
			boolean isInterview,
			FlvRecordingMetaDeltaDaoImpl flvRecordingMetaDeltaDao,
			FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDao) {
		super(streamName, scope, flvRecordingMetaDataId, isScreenData);
		
		this.flvRecordingMetaDeltaDao = flvRecordingMetaDeltaDao;
		this.flvRecordingMetaDataDao = flvRecordingMetaDataDao;
		this.isInterview  = isInterview;
		
		FlvRecordingMetaData flvRecordingMetaData = flvRecordingMetaDataDao.
								getFlvRecordingMetaDataById(flvRecordingMetaDataId);
		flvRecordingMetaData.setStreamReaderThreadComplete(false);
		flvRecordingMetaDataDao.updateFlvRecordingMetaData(flvRecordingMetaData);
		
	}

	@Override
	public void packetReceived(CachedEvent streampacket) {
		try {

			// We only care about audio at this moment
			if (this.isInterview || streampacket.getDataType() == 8) {

				if (streampacket.getTimestamp() <= 0) {
					log.warn("Negative TimeStamp");
					return;
				}

				IoBuffer data = streampacket.getData().asReadOnlyBuffer();

				if (data.limit() == 0) {
					return;
				}

				this.byteCount += data.limit();
				
				lastcurrentTime = streampacket.getCurrentTime();
				int timeStamp = streampacket.getTimestamp();
				Date virtualTime = streampacket.getCurrentTime();

				if (startTimeStamp == -1) {

					// Calculate the delta between the initial start and the
					// first audio-packet data

					this.initialDelta = virtualTime.getTime()
							- this.startedSessionTimeDate.getTime();

					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();

					flvRecordingMetaDelta.setDeltaTime(this.initialDelta);
					flvRecordingMetaDelta
							.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(0);
					flvRecordingMetaDelta.setDebugStatus("INIT AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(true);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta
							.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta
							.setStartTime(this.startedSessionTimeDate);
					flvRecordingMetaDelta.setPacketTimeStamp(streampacket
							.getTimestamp());

					Long deltaTimeStamp = virtualTime.getTime()
							- this.startedSessionTimeDate.getTime();

					// this.duration = Math.max(this.duration, 0 +
					// this.writer.getOffset());
					flvRecordingMetaDelta.setDuration(0);

					Long missingTime = deltaTimeStamp - 0;

					flvRecordingMetaDelta.setMissingTime(missingTime);

					flvRecordingMetaDelta.setCurrentTime(virtualTime);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);

					flvRecordingMetaDeltaDao
							.addFlvRecordingMetaDelta(flvRecordingMetaDelta);

					// That will be not bigger then long value
					this.startTimeStamp = (streampacket.getTimestamp());

					// We have to set that to bypass the initial delta
					// lastTimeStamp = startTimeStamp;
				}

				

				this.lastStreamPacketTimeStamp = streampacket.getTimestamp();

				timeStamp -= this.startTimeStamp;

				long deltaTime = 0;
				if (lastTimeStamp == -1) {
					deltaTime = 0; // Offset at the beginning is calculated
									// above
				} else {
					deltaTime = timeStamp - lastTimeStamp;
				}

				Long preLastTimeStamp = Long
						.parseLong(lastTimeStamp.toString());

				lastTimeStamp = timeStamp;

				if (deltaTime > 75) {

					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();

					flvRecordingMetaDelta.setDeltaTime(deltaTime);
					flvRecordingMetaDelta
							.setFlvRecordingMetaDataId(this.flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(timeStamp);
					flvRecordingMetaDelta.setDebugStatus("RUN AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(false);
					flvRecordingMetaDelta.setLastTimeStamp(preLastTimeStamp);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta
							.setReceivedAudioDataLength(this.byteCount);
					flvRecordingMetaDelta
							.setStartTime(this.startedSessionTimeDate);
					flvRecordingMetaDelta.setPacketTimeStamp(streampacket
							.getTimestamp());

					Date current_date = new Date();
					Long deltaTimeStamp = current_date.getTime()
							- this.startedSessionTimeDate.getTime();

					this.duration = Math.max(this.duration, timeStamp
							+ this.writer.getOffset());
					flvRecordingMetaDelta.setDuration(this.duration);

					Long missingTime = deltaTimeStamp - timeStamp;

					flvRecordingMetaDelta.setMissingTime(missingTime);

					flvRecordingMetaDelta.setCurrentTime(current_date);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);

					flvRecordingMetaDeltaDao
							.addFlvRecordingMetaDelta(flvRecordingMetaDelta);

				}

				ITag tag = new Tag();
				tag.setDataType(streampacket.getDataType());

				// log.debug("data.limit() :: "+data.limit());
				tag.setBodySize(data.limit());
				tag.setTimestamp(timeStamp);
				tag.setBody(data);

				writer.writeTag(tag);

			}

		} catch (IOException e) {
			log.error("[packetReceived]", e);
		} catch (Exception e) {
			log.error("[packetReceived]", e);
		}
	}

	@Override
	public void closeStream() {
		try {
			writer.close();
		} catch (Exception err) {
			log.error("[closeStream, close writer]", err);
		}

		try {
			// We do not add any End Padding or count the gaps for the
			// Screen Data, cause there is no!
			
			Date virtualTime = lastcurrentTime;
			log.debug("virtualTime: " + virtualTime);
			log.debug("startedSessionTimeDate: " + startedSessionTimeDate);
			
			long deltaRecordingTime = virtualTime == null ? 0 : virtualTime.getTime() - startedSessionTimeDate.getTime();

			log.debug("lastTimeStamp :closeStream: " + lastTimeStamp);
			log.debug("lastStreamPacketTimeStamp :closeStream: " + lastStreamPacketTimeStamp);
			log.debug("deltaRecordingTime :closeStream: " + deltaRecordingTime);

			long deltaTimePaddingEnd = deltaRecordingTime - lastTimeStamp - initialDelta;

			log.debug("deltaTimePaddingEnd :: " + deltaTimePaddingEnd);

			FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();

			flvRecordingMetaDelta.setDeltaTime(deltaTimePaddingEnd);
			flvRecordingMetaDelta
					.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
			flvRecordingMetaDelta.setTimeStamp(lastTimeStamp);
			flvRecordingMetaDelta.setDebugStatus("END AUDIO");
			flvRecordingMetaDelta.setIsStartPadding(false);
			flvRecordingMetaDelta.setIsEndPadding(true);
			flvRecordingMetaDelta.setDataLengthPacket(null);
			flvRecordingMetaDelta
					.setReceivedAudioDataLength(byteCount);
			flvRecordingMetaDelta.setStartTime(startedSessionTimeDate);
			flvRecordingMetaDelta.setCurrentTime(new Date());

			flvRecordingMetaDeltaDao
					.addFlvRecordingMetaDelta(flvRecordingMetaDelta);
			
			//Write the complete Bit to the meta data, the converter task will wait for this bit!
			FlvRecordingMetaData flvRecordingMetaData = flvRecordingMetaDataDao.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			flvRecordingMetaData.setStreamReaderThreadComplete(true);
			flvRecordingMetaDataDao.updateFlvRecordingMetaData(flvRecordingMetaData);

		} catch (Exception err) {
			log.error("[closeStream]", err);
		}
	}
}
