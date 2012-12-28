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
package org.apache.openmeetings.backup;

import org.apache.openmeetings.data.conference.Roommanagement;
import org.apache.openmeetings.persistence.beans.rooms.RoomTypes;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class RoomTypeConverter extends OmConverter<RoomTypes> {
	private Roommanagement roommanagement;
	
	public RoomTypeConverter() {
		//default constructor is for export
	}
	
	public RoomTypeConverter(Roommanagement roommanagement) {
		this.roommanagement = roommanagement;
	}
	
	public RoomTypes read(InputNode node) throws Exception {
		RoomTypes rt = roommanagement.getRoomTypesById(getlongValue(node));
		return rt != null ? rt : roommanagement.getRoomTypesById(1); // conference type will be used in case of bad type
	}

	public void write(OutputNode node, RoomTypes value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getRoomtypes_id());
	}
}