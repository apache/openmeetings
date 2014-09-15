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

import org.apache.openmeetings.db.dao.room.PollDao;
import org.apache.openmeetings.db.entity.room.PollType;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class PollTypeConverter extends OmConverter<PollType> {
	private PollDao pollDao;
	
	public PollTypeConverter() {
		//default constructor is for export
	}
	
	public PollTypeConverter(PollDao pollDao) {
		this.pollDao = pollDao;
	}
	
	public PollType read(InputNode node) throws Exception {
		return pollDao.getType(getlongValue(node));
	}

	public void write(OutputNode node, PollType value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getId());
	}
}