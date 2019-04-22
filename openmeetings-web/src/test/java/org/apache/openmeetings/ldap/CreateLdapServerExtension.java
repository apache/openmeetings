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
package org.apache.openmeetings.ldap;

import java.lang.reflect.AnnotatedElement;

import org.apache.directory.api.util.FileUtils;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.factory.DSAnnotationProcessor;
import org.apache.directory.server.factory.ServerAnnotationProcessor;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateLdapServerExtension implements BeforeAllCallback, AfterAllCallback {
	private static final Logger log = LoggerFactory.getLogger(CreateLdapServerExtension.class);
	private DirectoryService directoryService;
	private LdapServer ldapServer;

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		log.trace("Stopping ldap server");
		ldapServer.stop();

		log.trace("Shutting down directory service");
		directoryService.shutdown();
		FileUtils.deleteDirectory(directoryService.getInstanceLayout().getInstanceDirectory());
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		if (context.getElement().isPresent()) {
			AnnotatedElement e = context.getElement().get();
			Description description = Description.createSuiteDescription("LDAP", e.getAnnotations());
			log.trace("Creating directory service");
			directoryService = DSAnnotationProcessor.getDirectoryService(description);
			DSAnnotationProcessor.applyLdifs(description, directoryService);

			log.trace("Creating ldap server");
			ldapServer = ServerAnnotationProcessor.createLdapServer(description, directoryService);
		}
	}

	public LdapServer getLdapServer() {
		return ldapServer;
	}
}
