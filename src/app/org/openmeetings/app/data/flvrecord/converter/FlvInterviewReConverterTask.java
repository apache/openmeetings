package org.openmeetings.app.data.flvrecord.converter;

import org.springframework.core.task.TaskExecutor;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvInterviewReConverterTask {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvInterviewReConverterTask.class);

	//Spring loaded
	private TaskExecutor taskExecutor;
	private FlvInterviewReConverter flvInterviewReConverter;
	
	public FlvInterviewReConverterTask(TaskExecutor taskExecutor, FlvInterviewReConverter flvInterviewReConverter) {
		this.taskExecutor = taskExecutor;
		this.flvInterviewReConverter = flvInterviewReConverter;
	}

	public void startConversionThread(final Long flvRecordingId, final Integer leftSideLoud, final Integer rightSideLoud, final Integer leftSideTime, final Integer rightSideTime) {
		try {
			
			log.debug("[-1-]"+this.taskExecutor);
			
			this.taskExecutor.execute(new Runnable() {
				public void run() {
						flvInterviewReConverter.startConversion( flvRecordingId, leftSideLoud, rightSideLoud, leftSideTime, rightSideTime );
	               }
	          });
			
		} catch (Exception err) {
			log.error("[startConversionThread]",err);
		}
	}

}
