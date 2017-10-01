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
package org.apache.openmeetings.derby;

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;

import org.apache.openmeetings.cli.ConnectionPropertiesPatcher;
import org.apache.openmeetings.util.ConnectionProperties.DbType;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * This file is called from command line to patch the derby configuration
 * during the automated run of Selenium
 *
 * @author swagner
 *
 */
public class PrepareSystemFiles {
	private static final Logger log = Red5LoggerFactory.getLogger(PrepareSystemFiles.class, getWebAppRootKey());

	public static void main(String... args) {
		try {
			OmFileHelper.setOmHome(args[0]);

			String databaseHomeDirectory = args[1];

			String persistanceFileToPatch = args[2];

			File conf = new File(persistanceFileToPatch);

			if (conf.exists()) {
				conf.delete();
			}

			ConnectionPropertiesPatcher.patch(DbType.derby.name()
					, "localhost"
					, "1527"
					, databaseHomeDirectory + "openmeetings"
					, "user"
					, "secret"
					);
		} catch (Exception err) {
			log.error("Error", err);
		}
	}

}
