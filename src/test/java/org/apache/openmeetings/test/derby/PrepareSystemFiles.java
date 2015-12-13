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
package org.apache.openmeetings.test.derby;

import java.io.File;

import org.apache.openmeetings.cli.ConnectionProperties;
import org.apache.openmeetings.cli.ConnectionPropertiesPatcher;
import org.apache.openmeetings.util.OmFileHelper;

/**
 * This file is called from command line to patch the derby configuration 
 * during the automated run of Selenium
 * 
 * @author swagner
 *
 */
public class PrepareSystemFiles {
	
	public static void main(String... args) {
		try {
			OmFileHelper.setOmHome(args[0]);
			
			String databaseHomeDirectory = args[1];
			
			String persistanceFileToPatch = args[2];
			
			ConnectionProperties connectionProperties = new ConnectionProperties();
			
			File conf = new File(persistanceFileToPatch);
			
			if (conf.exists()) {
				conf.delete();
			}
			
			String dbType = "derby";
			File srcConf = new File(OmFileHelper.getWebinfDir(), "classes/META-INF/" + dbType + "_persistence.xml");
			ConnectionPropertiesPatcher.getPatcher(dbType, connectionProperties).patch(
					srcConf
					, conf
					, "localhost"
					, "1527"
					, databaseHomeDirectory + "openmeetings"
					, "user"
					, "secret"
					);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}
