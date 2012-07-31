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

import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class OmTimeZoneConverter implements Converter<OmTimeZone> {
	private OmTimeZoneDaoImpl omTimeZoneDaoImpl;
	
	public OmTimeZoneConverter() {
		//default constructor is for export
	}
	
	public OmTimeZoneConverter(OmTimeZoneDaoImpl omTimeZoneDaoImpl) {
		this.omTimeZoneDaoImpl = omTimeZoneDaoImpl;
	}
	
	public OmTimeZone read(InputNode node) throws Exception {
		OmTimeZone omTimeZone;
		try {
			omTimeZone = omTimeZoneDaoImpl.getOmTimeZone(node.getValue());
		} catch (Exception e) {
			omTimeZone = new OmTimeZone();
		}

		return omTimeZone;
	}

	public void write(OutputNode node, OmTimeZone value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getJname());
	}
}