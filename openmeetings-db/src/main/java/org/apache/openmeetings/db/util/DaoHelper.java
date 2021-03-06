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
package org.apache.openmeetings.db.util;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.wicket.util.string.Strings;

public class DaoHelper {
	public static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException("Should not be used");

	private DaoHelper() {}

	public static String getSearchQuery(String table, String alias, String search, boolean filterDeleted, boolean count, String sort, String... fields) {
		return getSearchQuery(table, alias, search, false, filterDeleted, count, sort, fields);
	}

	public static String getSearchQuery(String table, String alias, String search, boolean distinct, boolean filterDeleted, boolean count, String sort, String... fields) {
		return getSearchQuery(table, alias, null, search, distinct, filterDeleted, count, null, sort, fields);
	}

	public static String getSearchQuery(String table, String alias, String join, String search, boolean filterDeleted, boolean count, String additionalWhere, String sort, String... fields) {
		return getSearchQuery(table, alias, join, search, false, filterDeleted, count, additionalWhere, sort, fields);
	}

	public static String getSearchQuery(String table, String alias, String join, String search, boolean distinct, boolean filterDeleted, boolean count, String additionalWhere, String sort, String... fields) {
		StringBuilder sb = new StringBuilder("SELECT ");
		if (count) {
			sb.append("COUNT(");
		}
		if (distinct) {
			sb.append("DISTINCT ");
		}
		sb.append(alias);
		if (count) {
			sb.append(")");
		}
		sb.append(" FROM ").append(table).append(" ").append(alias);
		if (!Strings.isEmpty(join)) {
			sb.append(" ").append(join);
		}
		sb.append(" WHERE 1 = 1 ");
		if (filterDeleted) {
			sb.append("AND ").append(alias).append(".deleted = false ");
		}
		appendWhereClause(sb, search, alias, fields);
		if (!Strings.isEmpty(additionalWhere)) {
			sb.append("AND ").append(additionalWhere);
		}
		return appendSort(sb, alias, sort).toString();
	}

	public static StringBuilder appendWhereClause(StringBuilder sb, String search, String alias, String... fields) {
		if (!Strings.isEmpty(search) && fields != null) {
			boolean notEmpty = false;
			StringBuilder inSb = new StringBuilder();
			String[] searchItems = search.replace("\'", "").replace("\"", "").split(" ");
			for (int i = 0; i < searchItems.length; ++i) {
				if (searchItems[i].isEmpty()) {
					continue;
				}
				if (i == 0) {
					notEmpty = true;
					inSb.append(" (");
				} else {
					inSb.append(" OR ");
				}
				StringBuilder placeholder = new StringBuilder();
				placeholder.append("%").append(StringUtils.lowerCase(searchItems[i], Locale.ROOT)).append("%");

				inSb.append("(");
				for (int j = 0; j < fields.length; ++j) {
					if (j != 0) {
						inSb.append(" OR ");
					}
					inSb.append("lower(").append(alias).append(".").append(fields[j]).append(") LIKE '").append(placeholder).append("' ");
				}
				inSb.append(")");
			}
			if (notEmpty) {
				inSb.append(") ");
				sb.append(" AND").append(inSb);
			}
		}
		return sb;
	}

	public static StringBuilder appendSort(StringBuilder sb, String alias, String sort) {
		if (!Strings.isEmpty(sort)) {
			sb.append(" ORDER BY ").append(alias).append(".").append(sort);
		}
		return sb;
	}

	public static String getStringParam(String param) {
		return param == null ? "%" : "%" + StringUtils.lowerCase(param, Locale.ROOT) + "%";
	}

	public static <T> TypedQuery<T> setLimits(TypedQuery<T> q, Long first, Long max) {
		if (first != null) {
			q.setFirstResult(first.intValue());
		}
		if (max != null) {
			q.setMaxResults(max.intValue());
		}
		return q;
	}

	public static <T> List<T> fillLazy(EntityManager em, Function<OpenJPAEntityManager, TypedQuery<T>> func, String...groups) {
		OpenJPAEntityManager oem = OpenJPAPersistence.cast(em);
		boolean qrce = oem.getFetchPlan().getQueryResultCacheEnabled();
		try {
			oem.getFetchPlan().setQueryResultCacheEnabled(false); //update in cache during update
			TypedQuery<T> q = func.apply(oem);
			@SuppressWarnings("unchecked")
			OpenJPAQuery<T> kq = OpenJPAPersistence.cast(q);
			kq.getFetchPlan().addFetchGroups(groups);
			return kq.getResultList();
		} finally {
			oem.getFetchPlan().setQueryResultCacheEnabled(qrce);
		}
	}

	public static <T> T single(List<T> l) {
		return l.isEmpty() ? null : l.get(0);
	}
}
