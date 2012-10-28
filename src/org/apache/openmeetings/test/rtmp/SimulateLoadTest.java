package org.apache.openmeetings.test.rtmp;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SimulateLoadTest {

	private int numberOfClients = 10;
	private List<SimulateLoad> simulateLoadList = new ArrayList<SimulateLoad>();

	private final String host = "192.168.1.7";
	private final int port = 1935;
	private final String applicationContext = "openmeetings/1";

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
