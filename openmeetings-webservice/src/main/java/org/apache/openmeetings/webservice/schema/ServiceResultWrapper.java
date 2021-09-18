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
package org.apache.openmeetings.webservice.schema;

import org.apache.openmeetings.db.dto.basic.ServiceResult;

/**
 *
 * @author Sebastian.wagner
 *
 * This is required for generating the right schema documentation.
 *
 * Because of the way how CXF-RS generates root elements into the response it is required to
 * generate a wrapper element to match the schema of the actual response.
 *
 * A better way would be to drop the root element in CXF, but that would be a breaking change
 * to existing clients. See https://issues.apache.org/jira/browse/OPENMEETINGS-2667
 *
 */
public class ServiceResultWrapper {
	private ServiceResult serviceResult;

	public ServiceResult getServiceResult() {
		return serviceResult;
	}

	public void setServiceResult(ServiceResult serviceResult) {
		this.serviceResult = serviceResult;
	}
}
