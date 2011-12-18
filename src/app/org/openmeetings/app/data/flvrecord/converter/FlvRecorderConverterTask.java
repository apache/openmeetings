package org.openmeetings.app.data.flvrecord.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class FlvRecorderConverterTask {

	private static final Logger log = Red5LoggerFactory.getLogger(FlvRecorderConverterTask.class, ScopeApplicationAdapter.webAppRootKey);

	@Autowired
	@Qualifier("openmeetings.TaskExecutor")
	private TaskExecutor taskExecutor;
	@Autowired
	private FlvRecorderConverter flvRecorderConverter;

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
