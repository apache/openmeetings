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
package org.apache.openmeetings.persistence.beans.poll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.persistence.beans.lang.Fieldvalues;

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
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fieldvalues_id")
	@ForeignKey(enabled = true)
	private Fieldvalues label;
	
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
	public Fieldvalues getLabel() {
		return label;
	}
	/**
	 * @param pollTypeLabelid the pollTypeLabelid to set
	 */
	public void setLabel(Fieldvalues label) {
		this.label = label;
	}
}
