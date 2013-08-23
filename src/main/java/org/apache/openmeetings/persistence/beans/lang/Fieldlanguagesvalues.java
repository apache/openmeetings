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
package org.apache.openmeetings.persistence.beans.lang;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openmeetings.persistence.beans.IDataProviderEntity;

@Entity
@NamedQueries({
	@NamedQuery(name = "allFieldLanguageValues", query = "SELECT flv FROM Fieldlanguagesvalues flv "
		+ "WHERE flv.deleted = false "
		+ "		AND flv.fieldvalues.deleted = false "
		+ "		AND flv.language_id = :language_id ORDER BY flv.fieldvalues_id ASC")
	, @NamedQuery(name="allFieldValuesIds", query = "SELECT flv.fieldvalues_id FROM Fieldlanguagesvalues flv "
		+ "WHERE flv.deleted = false "
		+ "		AND flv.fieldvalues.deleted = false "
		+ "		AND flv.language_id = :language_id")
	, @NamedQuery(name="allNotTranslatedValues", query = "SELECT flv FROM Fieldlanguagesvalues flv "
		+ "WHERE flv.deleted = false "
		+ "		AND flv.fieldvalues.deleted = false "
		+ "		AND flv.language_id = 1 AND flv.fieldvalues_id NOT IN (:id_list)")
	, @NamedQuery(name="getFieldLanguagesValuesById"
		, query = "SELECT f FROM Fieldlanguagesvalues f WHERE f.fieldlanguagesvalues_id = :id")
	, @NamedQuery(name="getFieldLanguagesValuesByValueAndLang"
		, query = "SELECT f FROM Fieldlanguagesvalues f WHERE f.fieldvalues_id = :fieldValuesId AND f.language_id = :lang AND f.deleted = false")
})
@Table(name = "fieldlanguagesvalues")
public class Fieldlanguagesvalues implements Serializable, IDataProviderEntity {
	private static final long serialVersionUID = 1965055047163639210L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long fieldlanguagesvalues_id;
	
	@Column(name="fieldvalues_id")
	private Long fieldvalues_id;
	
	@Column(name="language_id")
	private Long language_id;
	
	@Column(name="starttime")
	private Date starttime;
	
	@Column(name="updatetime")
	private Date updatetime;
	
	@Column(name="deleted")
	private boolean deleted;
	
	@Lob
	@Column(name="value")
	private String value;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "fieldvalues_id")
	@ForeignKey(enabled = true)
	private Fieldvalues fieldvalues;
	
	public Fieldlanguagesvalues() {
		super();
	}
	
	public Long getFieldlanguagesvalues_id() {
		return fieldlanguagesvalues_id;
	}
	public void setFieldlanguagesvalues_id(Long fieldlanguagesvalues_id) {
		this.fieldlanguagesvalues_id = fieldlanguagesvalues_id;
	}
	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
    
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	
	public boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public Long getFieldvalues_id() {
		return fieldvalues_id;
	}
	public void setFieldvalues_id(Long fieldvalues_id) {
		this.fieldvalues_id = fieldvalues_id;
	}

	public Long getLanguage_id() {
		return language_id;
	}
	public void setLanguage_id(Long language_id) {
		this.language_id = language_id;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public Fieldvalues getFieldvalues() {
		return fieldvalues;
	}

	public void setFieldvalues(Fieldvalues fieldvalues) {
		this.fieldvalues = fieldvalues;
	}
	
}
