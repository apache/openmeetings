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
package org.apache.openmeetings.webservice;

import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OmFileHelper.getOmHome;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Files;

import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.openmeetings.web.AbstractOmServerTest;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CreateTomcatExtension implements BeforeAllCallback {
	private String host;
	private String context;
	private int port = 8080;
	private Tomcat tomcat;

	public CreateTomcatExtension(String host, String context) {
		this.host = host;
		this.context = context;
	}

	@Override
	public void beforeAll(ExtensionContext extContext) throws Exception {
		if (tomcat == null) {
			extContext.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put("my_report", new ExtensionContext.Store.CloseableResource() {
				@Override
				public void close() throws Throwable {
					if (tomcat.getServer() != null && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
						if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
							tomcat.stop();
						}
						tomcat.destroy();
					}
				}
			});
			AbstractOmServerTest.init();
			tomcat = new Tomcat();
			Connector connector = new Connector("HTTP/1.1");
			connector.setProperty("address", InetAddress.getByName(host).getHostAddress());
			connector.setPort(0);
			tomcat.getService().addConnector(connector);
			tomcat.setConnector(connector);
			File wd = Files.createTempDirectory("om" + randomUUID().toString()).toFile();
			tomcat.setBaseDir(wd.getCanonicalPath());
			tomcat.getHost().setAppBase(wd.getCanonicalPath());
			tomcat.getHost().setAutoDeploy(false);
			tomcat.getHost().setDeployOnStartup(false);
			tomcat.addWebapp(context, getOmHome().getAbsolutePath());
			tomcat.getConnector(); // to init the connector
			tomcat.start();
			port = tomcat.getConnector().getLocalPort();
		}
	}

	public int getPort() {
		return port;
	}
}
