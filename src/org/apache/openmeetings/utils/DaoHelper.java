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
package org.apache.openmeetings.utils;

import org.apache.commons.lang3.StringUtils;

public class DaoHelper {

	public static String getSearchQuery(String table, String alias, String search, boolean filterDeleted, boolean count, String sort, String... fields) {
		return getSearchQuery(table, alias, search, filterDeleted, count, null, sort, fields);
	}
	public static String getSearchQuery(String table, String alias, String search, boolean filterDeleted, boolean count, String additionalWhere, String sort, String... fields) {
		StringBuilder sb = new StringBuilder("SELECT ");
		if (count) {
			sb.append("COUNT(").append(alias).append(")");
		} else {
			sb.append(alias);
		}
		sb.append(" FROM ").append(table).append(" ").append(alias);
		boolean whereAdded = false;
		if (filterDeleted) {
			whereAdded = true;
			sb.append(" WHERE ").append(alias).append(".deleted = false ");
		}
		StringBuilder where = DaoHelper.getWhereClause(search, alias, fields);
		if (where.length() > 0) {
			if (!whereAdded) {
				whereAdded = true;
				sb.append(" WHERE ");
			} else {
				sb.append("AND ");
			}
			sb.append(where);
		}
		if (additionalWhere != null && !additionalWhere.trim().isEmpty()) {
			if (!whereAdded) {
				whereAdded = true;
				sb.append(" WHERE ");
			} else {
				sb.append("AND ");
			}
			sb.append(additionalWhere);
		}
		if (sort != null && !sort.trim().isEmpty()) {
			sb.append(" ORDER BY ").append(alias).append(".").append(sort);
		}
		return sb.toString();
	}
	
	public static StringBuilder getWhereClause(String search, String alias, String... fields) {
		StringBuilder sb = new StringBuilder();
		getWhereClause(sb, search, alias, fields);
		return sb;
	}
	
	public static void getWhereClause(StringBuilder sb, String search, String alias, String... fields) {
		if (search != null) {
			boolean notEmpty = false;
			String[] searchItems = search.split(" ");
			for (int i = 0; i < searchItems.length; ++i) {
				if (searchItems[i].isEmpty()) {
					continue;
				}
				if (i == 0) {
					notEmpty = true;
					sb.append(" (");
				} else {
					sb.append(" OR ");
				}
				StringBuilder placeholder = new StringBuilder();
				placeholder.append("%").append(StringUtils.lowerCase(searchItems[i])).append("%");
	
				sb.append("(");
				for (int j = 0; j < fields.length; ++j) {
					if (j != 0) {
						sb.append(" OR ");
					}
					sb.append("lower(").append(alias).append(".").append(fields[j]).append(") LIKE '").append(placeholder).append("' ");
				}
				sb.append(")");
			}
			if (notEmpty) {
				sb.append(") ");
			}
		}
	}
}
