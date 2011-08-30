package org.openmeetings.test.rdc;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestReadKeyCodesNumber extends TestCase {

	private static final Logger log = Logger
			.getLogger(TestReadKeyCodesNumber.class);

	@Test
	public void testTestKeyCodesNumber() {
		try {

			this.testKeyCodes();

		} catch (Exception er) {
			log.debug("ERROR ", er);
		}
	}

	@Test
	public void testKeyCodes() {
		try {

		} catch (Exception err) {
			log.error("[testKeyCodes]", err);
		}

	}

}