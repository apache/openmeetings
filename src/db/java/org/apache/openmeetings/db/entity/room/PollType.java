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
package org.apache.openmeetings.db.entity.room;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
	@NamedQuery(name = "getPollTypes", query = "SELECT pt FROM PollType pt"),
	@NamedQuery(name = "getPollType", query = "SELECT pt FROM PollType pt " +
			"WHERE pt.pollTypesId = :pollTypesId")		
})
@Table(name = "poll_types")
public class PollType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long pollTypesId;
	
	@Column(name = "fieldvalues_id")
	private Long label;
	
	@Column(name = "numeric_answer")
	private Boolean isNumericAnswer;
	
	/**
	 * @return the pollTypesId
	 */
	public Long getPollTypesId() {
		return pollTypesId;
	}
	/**
	 * @param pollTypesId the pollTypesId to set
	 */
	public void setPollTypesId(Long pollTypesId) {
		this.pollTypesId = pollTypesId;
	}
	/**
	 * @return the isNumericAnswer
	 */
	public Boolean getIsNumericAnswer() {
		return isNumericAnswer;
	}
	/**
	 * @param isNumericAnswer the isNumericAnswer to set
	 */
	public void setIsNumericAnswer(Boolean isNumericAnswer) {
		this.isNumericAnswer = isNumericAnswer;
	}
	/**
	 * @return the pollTypeLabelid
	 */
	public Long getLabel() {
		return label;
	}
	/**
	 * @param pollTypeLabelid the pollTypeLabelid to set
	 */
	public void setLabel(Long label) {
		this.label = label;
	}
}
