package org.openmeetings.test.rdc;

import java.awt.event.KeyEvent;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestKeyCodesNumber extends TestCase {

	private static final Logger log = Logger.getLogger(TestKeyCodesNumber.class);

	@Test
	public void testKeyCodes() {
		try {

			for (int i = 1; i < 600; i++) {

				String charText = KeyEvent.getKeyText(i);

				log.debug("ERROR " + i + " " + charText);

			}

		} catch (Exception err) {
			log.error("[testKeyCodes]", err);
		}

	}

}