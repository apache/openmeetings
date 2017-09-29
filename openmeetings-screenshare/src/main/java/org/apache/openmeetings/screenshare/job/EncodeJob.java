/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.screenshare.job;

import static org.slf4j.LoggerFactory.getLogger;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;

import org.apache.openmeetings.screenshare.CaptureScreen;
import org.apache.openmeetings.screenshare.ScreenV1Encoder;
import org.apache.openmeetings.screenshare.gui.ScreenDimensions;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.red5.server.net.rtmp.event.VideoData;
import org.slf4j.Logger;

@DisallowConcurrentExecution
public class EncodeJob implements Job {
	private static final Logger log = getLogger(EncodeJob.class);
	public static final String CAPTURE_KEY = "capture";
	private Robot robot;
	private ScreenDimensions dim;
	private Rectangle screen = null;
	private int[][] image = null;

	public EncodeJob() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			log.error("encode: Unexpected Error while creating robot", e);
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		CaptureScreen capture = (CaptureScreen)data.get(CAPTURE_KEY);
		if (screen == null) {
			dim = capture.getDim();
			screen = new Rectangle(dim.getSpinnerX(), dim.getSpinnerY()
					, dim.getSpinnerWidth(), dim.getSpinnerHeight());
		}

		long start = 0;
		if (log.isTraceEnabled()) {
			start = System.currentTimeMillis();
		}
		image = ScreenV1Encoder.getImage(dim, screen, robot);
		if (log.isTraceEnabled()) {
			log.trace(String.format("encode: Image was captured in %s ms, size %sk", System.currentTimeMillis() - start, 4 * image.length * image[0].length / 1024));
			start = System.currentTimeMillis();
		}
		try {
			VideoData vData = capture.getEncoder().encode(image);
			if (log.isTraceEnabled()) {
				long now = System.currentTimeMillis();
				log.trace(String.format("encode: Image was encoded in %s ms, timestamp is %s", now - start, now - capture.getStartTime()));
			}
			capture.getFrames().offer(vData);
			capture.getEncoder().createUnalteredFrame();
		} catch (Exception e) {
			log.error("Error while encoding: ", e);
		}
	}
}
