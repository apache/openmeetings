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
package org.openmeetings.test.server;


import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.client.gui.ClientViewerScreen;

/**
 * @author sebastianwagner
 *
 */
public class TestWebStartViewerGui extends TestCase {
	
	private static Logger log = Logger.getLogger(TestWebStartViewerGui.class);
	
	@Test
	public void testTestWebStartGui(){
		try {
			
			String url = "192.168.0.3";
			String SID = "ff27d722821cb1c9928764128cee2e85";
			String room = "1";
			String domain = "";
			String publicSID = "testw";
			String record = "no";
			
			new ClientViewerScreen(url,"4445", SID, room, domain, publicSID, record,"");
		   
			try {
	            Thread.sleep(100000000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	            throw new InterruptedException(e.getMessage());
	        }
		} catch (Exception err) {
			log.error("[TestSocket] ",err);
		}
		
	}

}
