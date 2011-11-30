package org.openmeetings.app.data.flvrecord.converter;

import org.springframework.core.task.TaskExecutor;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvRecorderConverterTask {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecorderConverterTask.class, ScopeApplicationAdapter.webAppRootKey);

	//Spring loaded
	private TaskExecutor taskExecutor;
	private FlvRecorderConverter flvRecorderConverter;
	
	public FlvRecorderConverterTask(TaskExecutor taskExecutor, FlvRecorderConverter flvRecorderConverter) {
		this.taskExecutor = taskExecutor;
		this.flvRecorderConverter = flvRecorderConverter;
	}

	public void startConversionThread(final Long flvRecordingId) {
		try {
			
			log.debug("[-1-]"+this.taskExecutor);
			
			this.taskExecutor.execute(new Runnable() {
				public void run() {
	            	   flvRecorderConverter.startConversion( flvRecordingId );
	               }
	          });
			
		} catch (Exception err) {
			log.error("[startConversionThread]",err);
		}
	}

}
