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
package org.openmeetings.app.quartz.scheduler;

import java.io.File;
import java.io.FileFilter;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.utils.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class TestSetupCleanupJob {
	private static Logger log = Red5LoggerFactory.getLogger(TestSetupCleanupJob.class, OpenmeetingsVariables.webAppRootKey);
	private long expirationInterval = 60 * 60 * 1000; // 1 hour

	public void doIt() {
		log.debug("TestSetupClearJob.execute");
		try {
			//FIXME need to move all these staff to helper
			File[] folders = OmFileHelper.getStreamsFilesDir().listFiles();
			if (folders != null) {
				for (File folder : folders) {
					if (folder.isDirectory()) {
						//TODO need to rework this and remove hardcodings
						for (File file : folder.listFiles(new FileFilter() {
							public boolean accept(File file) {
								return file.getName().startsWith("TEST_SETUP_");
							}
						}))
						{
							if (file.isFile() && file.lastModified() + expirationInterval < System.currentTimeMillis()) {
								log.debug("expired TEST SETUP found: " + file.getCanonicalPath());
								file.delete();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Unexpected exception while processing tests setup videous.", e);
		}
	}
}
