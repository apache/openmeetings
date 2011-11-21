package org.openmeetings.test.config;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.openmeetings.test.calendar.TestAppointmentAddAppointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TestConfig extends AbstractOpenmeetingsSpringTest {
	
	private static final Logger log = LoggerFactory.getLogger(
			TestAppointmentAddAppointment.class);
	
	@Autowired
	private Configurationmanagement configurationmanagement;


	@Test
	public void getConfigKey() {
		
		try {
			String use_old_style_ffmpeg_map  = configurationmanagement.getConfValue("use.old.style.ffmpeg.map.option", String.class, "0");
			
			log.debug("use_old_style_ffmpeg_map "+use_old_style_ffmpeg_map);
			
			assertEquals(use_old_style_ffmpeg_map,"1");

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
		
	}
}
