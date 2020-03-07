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
package org.apache.openmeetings.util;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.util.string.Strings;

public class ConnectionProperties implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum DbType {
		DB2
		, H2
		, MSSQL
		, MYSQL
		, ORACLE
		, POSTGRESQL;

		public static DbType of(String val) {
			if (Strings.isEmpty(val)) {
				return DbType.H2;
			}
			return "sqlserver".equals(val) ? DbType.MSSQL : DbType.valueOf(val.toUpperCase(Locale.ROOT));
		}

		public String dbName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	private String driver = "org.h2.Driver";
	private String url = "jdbc:h2:./omdb";
	private String login = "user";
	private String password = "secret";
	private DbType dbType = DbType.H2;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driverName) {
		this.driver = driverName;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String connectionURL) {
		this.url = connectionURL;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String connectionLogin) {
		this.login = connectionLogin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String connectionPass) {
		this.password = connectionPass;
	}

	public DbType getDbType() {
		return dbType;
	}

	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}

	@Override
	public String toString() {
		return "ConnectionProperties [type=" + dbType + ", driver=" + driver + ", url=" + url + ", login=" + login + "]";
	}
}
