package org.openmeetings.app.data.flvrecord.converter;

import org.springframework.core.task.TaskExecutor;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvInterviewConverterTask {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvInterviewConverterTask.class);

	//Spring loaded
	private TaskExecutor taskExecutor;
	private FlvInterviewConverter flvInterviewConverter;
	
	public FlvInterviewConverterTask(TaskExecutor taskExecutor, FlvInterviewConverter flvInterviewConverter) {
		this.taskExecutor = taskExecutor;
		this.flvInterviewConverter = flvInterviewConverter;
	}

	public void startConversionThread(final Long flvRecordingId) {
		try {
			
			log.debug("[-1-]"+this.taskExecutor);
			
			this.taskExecutor.execute(new Runnable() {
				public void run() {
						flvInterviewConverter.startConversion( flvRecordingId );
	               }
	          });
			
		} catch (Exception err) {
			log.error("[startConversionThread]",err);
		}
	}

}
