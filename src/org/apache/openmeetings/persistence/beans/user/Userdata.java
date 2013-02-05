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
package org.apache.openmeetings.persistence.beans.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "user_data")
public class Userdata implements Serializable {
	private static final long serialVersionUID = 8365799229794940172L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
    private Long data_id;
	
	@Column(name="user_id")
    private Long user_id;
	
	@Column(name="data_key")
    private String data_key;
	
	@Column(name="data")
    private String data;
	
	@Column(name = "starttime")
	private Date starttime;
	
	@Column(name = "updatetime")
	private Date updatetime;
	
	@Lob
	@Column(name = "comment_field")
    private String comment;
	
	@Column(name = "deleted")
    private boolean deleted;

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public String getData_key() {
		return data_key;
	}
	public void setData_key(String data_key) {
		this.data_key = data_key;
	}
    
	public Long getData_id() {
		return data_id;
	}
	public void setData_id(Long data_id) {
		this.data_id = data_id;
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

	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}



}
