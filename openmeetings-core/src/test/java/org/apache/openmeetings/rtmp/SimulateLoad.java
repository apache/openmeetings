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
package org.apache.openmeetings.rtmp;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SimulateLoad extends Thread {
	private static final Logger log = Red5LoggerFactory.getLogger(SimulateLoad.class, getWebAppRootKey());

	private LoadTestRtmpClient loadTestRtmpClient;
	private boolean testRunning = true;

	public double getAverageTime() {
		return loadTestRtmpClient.getAverageTime();
	}

	public boolean isTestRunning() {
		return testRunning;
	}

	public static void main(String... args) {
		try {
			if (args.length != 4) {
				throw new RuntimeException("4 args needed, host, port, context, instanceId");
			}
			for (String arg : args) {
				System.err.println("arg: " + arg);
			}
			SimulateLoad simulateLoad = new SimulateLoad(args[0], Integer
					.valueOf(args[1]).intValue(), args[2], Integer.valueOf(
					args[3]).intValue());
			simulateLoad.start();
			System.err.println("started ");

		} catch (Exception er) {
			log.error("Error", er);
		}
	}

	public SimulateLoad(String host, int port, String applicationContext,
			int instanceId) {
		super();

		loadTestRtmpClient = new LoadTestRtmpClient(instanceId);
		loadTestRtmpClient.connect(host, port, applicationContext,
				loadTestRtmpClient);
	}

	@Override
	public void run() {
		try {

			System.err.println("######### start client");

			while (testRunning) {
				testRunning = !loadTestRtmpClient.performCall();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					log.error("Error", e);
				}
			}

		} catch (Exception e) {
			log.error("Error", e);
		}
	}

	public int getNumberOfCalls() {
		return loadTestRtmpClient.getNumberOfCalls();
	}

}
