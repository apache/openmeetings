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

import org.apache.openmeetings.screenshare.CaptureScreen;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.red5.server.net.rtmp.event.VideoData;
import org.slf4j.Logger;

public class SendJob implements Job {
	private static final Logger log = getLogger(SendJob.class);
	public static final String CAPTURE_KEY = "capture";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		CaptureScreen capture = (CaptureScreen)data.get(CAPTURE_KEY);
		capture.setSendFrameGuard(true);
		if (log.isTraceEnabled()) {
			long real = System.currentTimeMillis() - capture.getStartTime();
			log.trace(String.format("send: Enter method, timestamp: %s, real: %s, diff: %s", capture.getTimestamp(), real, real - capture.getTimestamp().get()));
		}
		VideoData f = capture.getFrames().poll();
		if (log.isTraceEnabled()) {
			log.trace(String.format("send: Getting %s image", f == null ? "DUMMY" : "CAPTURED"));
		}
		f = f == null ? capture.getEncoder().getUnalteredFrame() : f;
		if (f != null) {
			capture.pushVideo(f, capture.getTimestamp().get());
			if (log.isTraceEnabled()) {
				long real = System.currentTimeMillis() - capture.getStartTime();
				log.trace(String.format("send: Sending video %sk, timestamp: %s, real: %s, diff: %s", f.getData().capacity() / 1024, capture.getTimestamp(), real, real - capture.getTimestamp().get()));
			}
			capture.getTimestamp().addAndGet(capture.getTimestampDelta());
			if (log.isTraceEnabled()) {
				log.trace(String.format("send: new timestamp: %s", capture.getTimestamp()));
			}
		} else if (log.isTraceEnabled()) {
			log.trace("send: nothing to send");
		}
		capture.setSendFrameGuard(false);
	}
}
