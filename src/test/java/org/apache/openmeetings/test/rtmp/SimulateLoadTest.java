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
package org.apache.openmeetings.test.rtmp;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SimulateLoadTest {

	private int numberOfClients = 25;
	private List<SimulateLoad> simulateLoadList = new ArrayList<SimulateLoad>();

	private String host = "192.168.1.7";
	private final int port = 1935;
	private final String applicationContext = "openmeetings/1";

	public SimulateLoadTest(String host, int numberOfClients) {
		this.host = host;
		this.numberOfClients = numberOfClients;
	}

	public static void main(String... args) {
		if (args.length != 2) {
			return;
		}
		SimulateLoadTest simulateLoadTest = new SimulateLoadTest(args[0],
				Integer.valueOf(args[1]).intValue());
		simulateLoadTest.test();
	}


	@Test
	public void test() {
		try {
			for (int i = 0; i < numberOfClients; i++) {
				SimulateLoad simulateLoad = new SimulateLoad(host, port,
						applicationContext, i);
				simulateLoadList.add(simulateLoad);
				Thread.sleep(100);
			}

			System.err.println("Clients initialized");

			for (SimulateLoad simulateLoad : simulateLoadList) {
				simulateLoad.start();
				Thread.sleep(50);
			}

			System.err.println("Clients started");

			boolean running = true;
			while (running) {
				boolean doRunStill = false;
				for (SimulateLoad simulateLoad : simulateLoadList) {
					if (simulateLoad.isTestRunning()) {
						doRunStill = true;
						break;
					}
				}
				running = doRunStill;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			double overallTime = 0;

			for (SimulateLoad simulateLoad : simulateLoadList) {
				overallTime += simulateLoad.getAverageTime();
				System.err.println("Number of calls: "
						+ simulateLoad.getNumberOfCalls() + "overallTime: "
						+ overallTime + " averageTime"
						+ simulateLoad.getAverageTime());
			}

			double deltaAllClients = overallTime
					/ Integer.valueOf(simulateLoadList.size()).doubleValue();

			System.err.println("Average time per call: " + deltaAllClients);

		} catch (Exception err) {
			err.printStackTrace();
		}

	}

}
