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
package org.openmeetings.app.templates;

import java.io.File;

import org.apache.velocity.app.Velocity;
import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author swagner
 * 
 */

public abstract class VelocityLoader {

	private static final Logger log = Red5LoggerFactory.getLogger(
			VelocityLoader.class, OpenmeetingsVariables.webAppRootKey);

	public VelocityLoader() {

	}

	/**
	 * Loads the Path from the Red5-Scope
	 * 
	 */
	public void init() {
		try {
			String current_dir = ScopeApplicationAdapter.webAppPath
					+ File.separatorChar;
			
			File fInit = new File(current_dir + "WEB-INF" + File.separatorChar
					+ "velocity.properties");
			
			if (fInit.exists()) {
				Velocity.init(current_dir + "WEB-INF" + File.separatorChar
						+ "velocity.properties");
			} else {
				Velocity.init("WeBContent"+File.separatorChar+"WEB-INF" + File.separatorChar
						+ "velocity.properties");
			}

			
		} catch (Exception e) {
			log.error("Problem initializing Velocity : ", e);
			System.out.println("Problem initializing Velocity : " + e);
		}
	}

	/**
	 * http://$server:$port/openmeetings/?param=value&param2=value2 Loads the
	 * path by a given string, this is necessary cause if invoked by Servlet
	 * there is no Red5-Scope available
	 * 
	 * @param path
	 */
	public VelocityLoader(String path) {
		try {
			Velocity.init(path + "WEB-INF/velocity.properties");
		} catch (Exception e) {
			log.error("Problem initializing Velocity : " + e);
			System.out.println("Problem initializing Velocity : " + e);
		}
	}

}
