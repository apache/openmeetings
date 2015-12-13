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
package org.apache.openmeetings.web.app;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class OmAuthenticationStrategy extends DefaultAuthenticationStrategy {
	/** The separator used to concatenate the username and password */
	private static final String VALUE_SEPARATOR = "-sep-";
	private static final String COOKIE_KEY = "LoggedIn";
	private static final Logger log = Red5LoggerFactory.getLogger(OmAuthenticationStrategy.class, webAppRootKey);
	/** The cookie name to store the username/password/domain */
	private final String cookieKey;
	
	public OmAuthenticationStrategy() {
		super(COOKIE_KEY);
		cookieKey = COOKIE_KEY;
	}
	
	/**
	 * @see org.apache.wicket.authentication.IAuthenticationStrategy#load()
	 */
	@Override
	public String[] load()
	{
		String value = getCookieUtils().load(cookieKey);
		if (Strings.isEmpty(value) == false)
		{
			try
			{
				value = getCrypt().decryptUrlSafe(value);
			}
			catch (RuntimeException e)
			{
				log.info(
					"Error decrypting login cookie: {}. The cookie will be deleted. Possible cause is that a session-relative encryption key was used to encrypt this cookie while this decryption attempt is happening in a different session, eg user coming back to the application after session expiration",
					cookieKey);
				getCookieUtils().remove(cookieKey);
				value = null;
			}
			if (Strings.isEmpty(value) == false)
			{
				String username = null;
				String password = null;
				String domain = null;

				String[] values = value.split(VALUE_SEPARATOR);
				if ((values.length > 0) && (Strings.isEmpty(values[0]) == false))
				{
					username = values[0];
				}
				if ((values.length > 1) && (Strings.isEmpty(values[1]) == false))
				{
					password = values[1];
				}
				if ((values.length > 2) && (Strings.isEmpty(values[2]) == false))
				{
					domain = values[2];
				}

				return new String[] { username, password, domain };
			}
		}

		return null;
	}

	public void save(final String username, final String password, final String domain) {
		String value = username + VALUE_SEPARATOR + password + VALUE_SEPARATOR + domain;

		String encryptedValue = getCrypt().encryptUrlSafe(value);

		getCookieUtils().save(cookieKey, encryptedValue);

	}
}
