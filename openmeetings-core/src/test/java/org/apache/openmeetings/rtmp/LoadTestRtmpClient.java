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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.red5.client.net.rtmp.ClientExceptionHandler;
import org.red5.client.net.rtmp.INetStreamEventHandler;
import org.red5.client.net.rtmp.RTMPClient;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;
import org.red5.server.net.rtmp.event.Notify;

public class LoadTestRtmpClient extends RTMPClient implements IPendingServiceCallback, INetStreamEventHandler, ClientExceptionHandler {
	private static class CallObject {
		Date started;
		Date ended;

		public CallObject(Date started) {
			super();
			this.started = started;
		}

		public Date getStarted() {
			return started;
		}

		public Date getEnded() {
			return ended;
		}

		public void setEnded(Date ended) {
			this.ended = ended;
		}
	}

	private int counterCalls = 0; // a call is always 2 steps
	private Map<Integer, CallObject> calls = new HashMap<>();
	private boolean isConnected = false;
	private final int instanceId;

	public LoadTestRtmpClient(int instanceId) {
		this.instanceId = instanceId;
	}

	public boolean performCall() {
		// System.err.println("performCall " + isConnected);

		if (!isConnected) {
			return false;
		}

		if (counterCalls % 2 == 0) {

			if (counterCalls > 10) {

				return true;

			}

			System.err.println("Rest o do new call " + counterCalls);
			counterCalls++;

			Map<String, Integer> map = new HashMap<>();
			map.put("instanceId", instanceId);
			map.put("count", counterCalls);
			calls.put(counterCalls, new CallObject(new Date()));
			invoke("sendMessageToCurrentScope", new Object[] {"syncMessageToCurrentScopeResult", map, true, false }, this);

		} else {
			System.err.println("Call running " + counterCalls);
		}
		return false;
	}

	public double getAverageTime() {
		long overallTime = 0L;

		for (Entry<Integer, CallObject> tCallObjectEntry : calls.entrySet()) {

			long deltaTime = tCallObjectEntry.getValue().getEnded().getTime()
					- tCallObjectEntry.getValue().getStarted().getTime();

			// System.err.println("Key " + tCallObjectEntry.getKey()
			// + "deltaTime " + deltaTime);

			overallTime += deltaTime;

		}

		double averageTime = Long.valueOf(overallTime).doubleValue()
				/ Integer.valueOf(calls.size()).doubleValue();

		return averageTime;
	}

	@Override
	public void resultReceived(IPendingServiceCall call) {
		String method = call == null ? null : call.getServiceMethodName();
		System.err.println("method "+method);
		if (method == null) {
			return;
		}
		if ("connect".equals(method)) {
			isConnected = true;
		}

		if ("loadTestSyncMessage".equals(method)) {

			CallObject tCallObject = calls.get(counterCalls);
			if (tCallObject == null) {

				for (Entry<Integer, CallObject> tCallObjectEntry : calls.entrySet()) {

					System.err.println("Key " + tCallObjectEntry.getKey()
							+ "tCallObjectEntry "
							+ tCallObjectEntry.getValue().getStarted());

				}

				throw new RuntimeException(
						"tCallObject is null currentCountReturn "
								+ counterCalls + " list size "
								+ calls.size());
			}
			tCallObject.setEnded(new Date());
			calls.put(counterCalls, tCallObject);

			System.err.println("Call received " + counterCalls
					+ " instanceId: " + instanceId);

			counterCalls++;
		}
	}

	@Override
	public void onStreamEvent(Notify notify) {
		// no-op
	}

	public int getNumberOfCalls() {
		return calls.size();
	}
}
