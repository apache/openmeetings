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
package org.openmeetings.app.backup;

import java.util.Map;

import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class OrganisationConverter extends OmConverter<Organisation> {
	private Organisationmanagement organisationmanagement;
	private Map<Long, Long> idMap;
	
	public OrganisationConverter() {
		//default constructor is for export
	}
	
	public OrganisationConverter(Organisationmanagement organisationmanagement, Map<Long, Long> idMap) {
		this.organisationmanagement = organisationmanagement;
		this.idMap = idMap;
	}
	
	public Organisation read(InputNode node) throws Exception {
		long oldOrgId = getlongValue(node);
		long newId = idMap.containsKey(oldOrgId) ? idMap.get(oldOrgId) : oldOrgId;
		
		return organisationmanagement.getOrganisationByIdBackup(newId);
	}

	public void write(OutputNode node, Organisation value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getOrganisation_id());
	}
}