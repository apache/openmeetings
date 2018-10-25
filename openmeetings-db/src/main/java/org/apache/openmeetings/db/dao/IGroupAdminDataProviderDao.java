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

public interface IGroupAdminDataProviderDao<T extends IDataProviderEntity> extends IDataProviderDao<T> {
	/**
	 * Get a list of instances of {@link T}
	 *
	 * @param search - string search criteria to filter entities
	 * @param adminId - id of group admin user
	 * @param start - the start to range to retrieve
	 * @param count - maximum instance count to retrieve
	 * @param order - column and sort order
	 * @return list of instances in the range specified
	 */
	List<T> adminGet(String search, Long adminId, long start, long count, String order);

	/**
	 * Get a list of instances of {@link T}
	 *
	 * @param search - string search criteria to filter entities
	 * @param start - the start to range to retrieve
	 * @param count - maximum instance count to retrieve
	 * @param order - column and sort order
	 * @return list of instances in the range specified
	 */
	default List<T> adminGet(String search, long start, long count, String order) {
		return get(search, start, count, order);
	}

	/**
	 * Count the number of instances of {@link T}
	 *
	 * @param search - string search criteria to filter entities
	 * @param adminId - id of group admin user
	 * @return count of instances satisfying given search criteria
	 */
	long adminCount(String search, Long adminId);

	/**
	 * Count the number of instances of {@link T}
	 *
	 * @param search - string search criteria to filter entities
	 * @return count of instances satisfying given search criteria
	 */
	default long adminCount(String search) {
		return count(search);
	}
}
