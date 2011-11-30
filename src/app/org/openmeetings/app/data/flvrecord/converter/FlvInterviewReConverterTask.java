package org.openmeetings.app.data.flvrecord.converter;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.task.TaskExecutor;

public class FlvInterviewReConverterTask {

	private static final Logger log = Red5LoggerFactory.getLogger(
			FlvInterviewReConverterTask.class,
			ScopeApplicationAdapter.webAppRootKey);

	// Spring loaded
	private TaskExecutor taskExecutor;
	private FlvInterviewConverter flvInterviewConverter;

	public FlvInterviewReConverterTask(TaskExecutor taskExecutor,
			FlvInterviewConverter flvInterviewConverter) {
		this.taskExecutor = taskExecutor;
		this.flvInterviewConverter = flvInterviewConverter;
	}

	public void startConversionThread(final Long flvRecordingId,
			final Integer leftSideLoud, final Integer rightSideLoud,
			final Integer leftSideTime, final Integer rightSideTime) {
		try {

			log.debug("[-1-]" + this.taskExecutor);

			this.taskExecutor.execute(new Runnable() {
				public void run() {
					flvInterviewConverter.startReConversion(flvRecordingId,
							leftSideLoud, rightSideLoud, leftSideTime,
							rightSideTime);
				}
			});

		} catch (Exception err) {
			log.error("[startConversionThread]", err);
		}
	}

}
