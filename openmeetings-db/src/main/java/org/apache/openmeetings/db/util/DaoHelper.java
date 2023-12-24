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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoHelper {
	private static final Logger log = LoggerFactory.getLogger(DaoHelper.class);
	public static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException("Should not be used");

	private DaoHelper() {}

	public static String getStringParam(String param) {
		return "%" + StringUtils.lowerCase(param, Locale.ROOT) + "%";
	}

	public static <T> long count(EntityManager em
			, Class<T> clazz
			, String search
			, List<String> searchFields
			, boolean noDeleted
			, BiFunction<CriteriaBuilder, CriteriaQuery<?>, Predicate> filter)
	{
		return count(em, clazz, CriteriaBuilder::count, search, searchFields, noDeleted, filter);
	}

	public static <T> long count(EntityManager em
			, Class<T> clazz
			, BiFunction<CriteriaBuilder, Root<T>, Expression<Long>> queuePath
			, String search
			, List<String> searchFields
			, boolean noDeleted
			, BiFunction<CriteriaBuilder, CriteriaQuery<?>, Predicate> filter)
	{
		CriteriaQuery<Long> query = query(em, clazz, Long.class, queuePath, false, search, searchFields, noDeleted, filter, null);
		return em.createQuery(query).getSingleResult();
	}

	public static <T> List<T> get(
			EntityManager em
			, Class<T> clazz
			, boolean distinct
			, String search
			, List<String> searchFields
			, boolean noDeleted
			, BiFunction<CriteriaBuilder, CriteriaQuery<?>, Predicate> filter
			, SortParam<String> sort
			, long start
			, long count)
	{
		return get(em, clazz, clazz, (builder, root) -> root
				, distinct, search, searchFields, noDeleted, filter, sort, start, count);
	}

	public static <T, R> List<T> get(
			EntityManager em
			, Class<R> rootClazz
			, Class<T> clazz
			, BiFunction<CriteriaBuilder, Root<R>, Expression<T>> queuePath
			, boolean distinct
			, String search
			, List<String> searchFields
			, boolean noDeleted
			, BiFunction<CriteriaBuilder, CriteriaQuery<?>, Predicate> filter
			, SortParam<String> sort
			, long start
			, long count)
	{
		CriteriaQuery<T> query = query(em, rootClazz, clazz, queuePath, distinct, search, searchFields, noDeleted, filter, sort);
		return setLimits(em.createQuery(query), start, count).getResultList();
	}

	public static <T, R> CriteriaQuery<T> query(
			EntityManager em
			, Class<R> rootClazz
			, Class<T> clazz
			, BiFunction<CriteriaBuilder, Root<R>, Expression<T>> queuePath
			, boolean distinct
			, String search
			, List<String> searchFields
			, boolean noDeleted
			, BiFunction<CriteriaBuilder, CriteriaQuery<?>, Predicate> filter
			, SortParam<String> sort)
	{
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<R> root = query.from(rootClazz);
		query.select(queuePath.apply(builder, root));
		if (distinct) {
			query.distinct(distinct);
		}

		query.where(search(search, searchFields, noDeleted, filter, builder, root, query));
		sort(sort, builder, root, query);
		return query;
	}

	public static <T, Q> Predicate search(String search
			, List<String> searchFields
			, boolean noDeleted
			, BiFunction<CriteriaBuilder, CriteriaQuery<?>, Predicate> filter
			, CriteriaBuilder builder
			, Root<T> root
			, CriteriaQuery<Q> query)
	{
		Predicate result = builder.isNull(null);
		if (noDeleted) {
			result = builder.and(result, builder.equal(root.get("deleted"), false));
		}
		if (filter != null) {
			result = builder.and(result, filter.apply(builder, query));
		}
		if (!Strings.isEmpty(search)) {
			Predicate[] criterias = Stream.of(search.replace("\'", "").replace("\"", "").split(" "))
					.filter(searchItem -> !searchItem.isEmpty())
					.map(DaoHelper::getStringParam)
					.flatMap(searchItem -> searchFields.stream().map(col -> like(col, searchItem, builder, root)))
					.toArray(Predicate[]::new);
			result = builder.and(result, builder.or(criterias));
		}
		return result;
	}

	public static <T> Predicate like(String col, String searchItem, CriteriaBuilder builder, Path<T> root) {
		Path<String> colPath = null;
		String[] cols = col.split("[.]");
		for(String s : cols) {
			colPath = colPath == null ? root.get(s) : colPath.get(s);
		}
		return builder.like(builder.lower(colPath), "%" + searchItem + "%");
	}

	public static <T, Q> void sort(SortParam<String> sort, CriteriaBuilder builder, Root<T> root, CriteriaQuery<Q> query) {
		if (sort != null && !Strings.isEmpty(sort.getProperty())) {
			query.orderBy(sort.isAscending()
					? builder.asc(root.get(sort.getProperty()))
					: builder.desc(root.get(sort.getProperty())));
		}
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

	public static <T> T only(List<T> l) {
		if (l.size() > 1) {
			log.error("More than one ({}) record found", l.size());
		}
		return l.isEmpty() ? null : l.get(0);
	}

	@SuppressWarnings("unchecked")
	public static <T> Root<T> getRoot(CriteriaQuery<?> query, Class<T> clazz) {
		return query.getRoots().stream()
				.filter(r -> clazz.equals(r.getModel().getJavaType()))
				.map(r -> (Root<T>)r)
				.findAny()
				.orElseThrow();
	}

	public static Subquery<Long> groupAdminQuery(Long userId, CriteriaBuilder builder, AbstractQuery<?> parentQ) {
		Subquery<Long> query = parentQ.subquery(Long.class);
		Root<GroupUser> root = query.from(GroupUser.class);
		query.select(root.get("group").get("id"));
		query.where(builder.and(builder.isTrue(root.get("moderator")), builder.equal(root.get("user").get("id"), userId)));
		return query;
	}
}
