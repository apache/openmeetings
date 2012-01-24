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
package org.openmeetings.screen.codec;

import org.apache.log4j.Logger;

public class CaptureScreenByDelta {
	
	private static Logger log = Logger.getLogger(CaptureScreenByDelta.class);

	/**
	 * 
	 * 
	 * 1) Fill Buffer
	 * 2) Check Buffer Full
	 * 3) Do comparison => switch buffer index
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * each Buffer Pool is one second 
	 * so the PRE buffer is one second
	 * => we buffer always 1 second, analyze 
	 * that data and then send it
	 * 
	 * Buffer 1 
	 * Buffer 2
	 * Buffer 3
	 * Buffer 4
	 * Buffer 5
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3 && buffer1 != buffer4 && buffer1 != buffer5
	 * => send buffer1 with 0.1 quality
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3 && buffer1 != buffer4
	 * => send buffer1 with 0.2 quality
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3
	 * => send buffer1 with 0.4 quality
	 * 
	 * buffer1 != buffer2 
	 * => send buffer1 with 0.6 quality
	 * 
	 * buffer1 == buffer2
	 * => send nothing
	 * 
	 * => switch buffers in one direction
	 * 
	 */
	
	/**
	 * 
	 *
	 * 24.09.2009 10:24:43
	 * sebastianwagner
	 * 
	 *
	 */
	public CaptureScreenByDelta() {
		try {
			
		} catch (Exception err) {
			log.error("[ClientCaptureScreen]",err);
		}
	}
}
