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
package org.apache.openmeetings.db.dao;

import java.util.List;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.springframework.transaction.annotation.Transactional;

/**
 * General interface to perform CRUD operations on entities
 *
 * @author swagner
 *
 * @param <T> entity type for this provider
 */
@Transactional
public interface IDataProviderDao<T extends IDataProviderEntity> {
	/**
	 * Get an instance of an {@link T}
	 *
	 * @param id - id of instance to retrieve
	 * @return instance with the id gived
	 */
	T get(Long id);

	default T get(long id) {
		return get(Long.valueOf(id));
	}

	/**
	 * Get a list of instances of {@link T}
	 *
	 * @param start - the start to range to retrieve
	 * @param count - maximum instance count to retrieve
	 * @return list of instances in the range specified
	 */
	List<T> get(long start, long count);

	/**
	 * Get a list of instances of {@link T}
	 *
	 * @param search - string search criteria to filter entities
	 * @param start - the start to range to retrieve
	 * @param count - maximum instance count to retrieve
	 * @param order - column and sort order
	 * @return list of instances in the range specified
	 */
	List<T> get(String search, long start, long count, SortParam<String> order);

	/**
	 * Count the number of instances of {@link T}
	 *
	 * @return count of instances
	 */
	long count();

	/**
	 * Count the number of instances of {@link T}
	 *
	 * @param search - string search criteria to filter entities
	 * @return count of instances satisfying given search criteria
	 */
	long count(String search);

	/**
	 * Update an instance of {@link T}
	 *
	 * @param entity - entity to be updated
	 * @param userId - user performed update
	 * @return - updated entity
	 */
	T update(T entity, Long userId);

	/**
	 * Delete an instance of {@link T}
	 *
	 * @param entity - entity to be deleted
	 * @param userId - user performed delete
	 */
	void delete(T entity, Long userId);
}
