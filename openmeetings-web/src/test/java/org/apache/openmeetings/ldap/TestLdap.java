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

import static org.apache.directory.server.constants.ServerDNConstants.ADMIN_SYSTEM_DN;
import static org.apache.directory.server.core.api.partition.PartitionNexus.ADMIN_PASSWORD_BYTES;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_ADMIN_DN;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_ADMIN_PASSWD;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_AUTH_TYPE;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_HOST;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_PORT;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_SEARCH_BASE;
import static org.apache.openmeetings.core.ldap.LdapOptions.CONFIGKEY_LDAP_SEARCH_SCOPE;
import static org.apache.openmeetings.util.OmFileHelper.getLdapConf;
import static org.apache.openmeetings.util.OmFileHelper.loadLdapConf;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.CreateLdapServerRule;
import org.apache.directory.server.protocol.shared.transport.Transport;
import org.apache.openmeetings.AbstractWicketTester;
import org.apache.openmeetings.core.ldap.LdapLoginManager;
import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.OmException;
import org.apache.openmeetings.web.app.WebSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@CreateDS(name = "omDS",
	partitions = {
		@CreatePartition(name = "test", suffix = "dc=test,dc=openmeetings,dc=apache,dc=org")
	})
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", address = "localhost")})
@ApplyLdifFiles({"schema/users.ldif"})
public class TestLdap extends AbstractWicketTester {
	private static final String CFG_SEARCH_BIND = UUID.randomUUID().toString();
	private static final String BAD_PASSWORD = "bad password";
	private static final String USER1 = "ldaptest1";
	private static final Map<String, LdapConfig> CFG_MAP = new HashMap<>();
	private static final Properties PROPS = new Properties();
	@Autowired
	private LdapConfigDao ldapDao;

	@ClassRule
	public static CreateLdapServerRule serverRule = new CreateLdapServerRule();

	@BeforeClass
	public static void prepare() {
		loadLdapConf("om_ldap.cfg", PROPS);
		Transport t = serverRule.getLdapServer().getTransports()[0];
		PROPS.put(CONFIGKEY_LDAP_HOST, t.getAddress());
		PROPS.put(CONFIGKEY_LDAP_PORT, String.valueOf(t.getPort()));
		PROPS.put(CONFIGKEY_LDAP_ADMIN_DN, ADMIN_SYSTEM_DN);
		PROPS.put(CONFIGKEY_LDAP_ADMIN_PASSWD, new String(ADMIN_PASSWORD_BYTES));
		PROPS.put(CONFIGKEY_LDAP_SEARCH_BASE, "dc=test,dc=openmeetings,dc=apache,dc=org");
		PROPS.put(CONFIGKEY_LDAP_SEARCH_SCOPE, SearchScope.SUBTREE.name());
	}

	private void createSbnd() throws FileNotFoundException, IOException {
		Properties pp = new Properties();
		pp.putAll(PROPS);
		pp.put(CONFIGKEY_LDAP_AUTH_TYPE, LdapLoginManager.AuthType.SEARCHANDBIND.name());
		try (OutputStream out = new FileOutputStream(getLdapConf(CFG_SEARCH_BIND))) {
			pp.store(out, "");
		}
		LdapConfig cfg = new LdapConfig();
		cfg.setName(CFG_SEARCH_BIND);
		cfg.setActive(true);
		cfg.setConfigFileName(CFG_SEARCH_BIND);
		ldapDao.update(cfg, null);
		CFG_MAP.put(CFG_SEARCH_BIND, cfg);
	}

	@Before
	public void clean() throws FileNotFoundException, IOException {
		if (CFG_MAP.isEmpty()) {
			createSbnd();
		}
		for (LdapConfig cfg : ldapDao.getActive()) {
			if (!CFG_MAP.containsKey(cfg.getName())) {
				cfg.setActive(false);
				ldapDao.update(cfg, null);
			} else {
				CFG_MAP.put(cfg.getName(), cfg);
			}
		}
	}

	@Test
	public void testSbndSessionLogin() throws OmException {
		LdapConfig cfg = CFG_MAP.get(CFG_SEARCH_BIND);
		assertTrue("Login should be successful", WebSession.get().signIn(USER1, userpass, User.Type.ldap, cfg.getId()));
	}

	@Test(expected = OmException.class)
	public void testSbndSessionLoginBadPassword() throws OmException {
		LdapConfig cfg = CFG_MAP.get(CFG_SEARCH_BIND);
		WebSession.get().signIn(USER1, BAD_PASSWORD, User.Type.ldap, cfg.getId());
	}
}
