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
package org.openmeetings.client.screen;

import org.apache.log4j.Logger;
import org.openmeetings.screen.codec.CaptureScreenByMode;
 
/**
 * @author sebastianwagner
 *
 */
public class ClientSentScreen extends Thread {
	
	public static int mode = 1;
	
	public static boolean threadRunning = false;
	
	private static Logger log = Logger.getLogger(ClientSentScreen.class);
	
	@Override
	public void run() {
		try {
			
			log.debug("ClientSentScreen RUN");
			
			while (threadRunning) {
			
				if (mode == 1) {
					new ClientCaptureScreen(1);
				} else if (mode == 2) {
					new CaptureScreenByMode();
				}

				//Wait for 500 milliseconds for the next run
				ClientSentScreen.sleep(500);
			
			}
			
		} catch (Exception err) {
			log.error("[ClientSentScreen]",err);
		}
	}

}
