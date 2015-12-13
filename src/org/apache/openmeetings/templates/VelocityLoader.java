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
package org.apache.openmeetings.templates;

import java.io.File;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.velocity.app.Velocity;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author swagner
 * 
 */
public abstract class VelocityLoader {
	private static final Logger log = Red5LoggerFactory.getLogger(VelocityLoader.class, OpenmeetingsVariables.webAppRootKey);

	/**
	 * Loads the Path from the Red5-Scope
	 * 
	 */
	public void init() {
		try {
			File fInit = new File(OmFileHelper.getWebinfDir(), "velocity.properties");
			
			if (fInit.exists()) {
				Velocity.init(fInit.getCanonicalPath());
			}
		} catch (Exception e) {
			log.error("Problem initializing Velocity : ", e);
			System.out.println("Problem initializing Velocity : " + e);
		}
	}
}
