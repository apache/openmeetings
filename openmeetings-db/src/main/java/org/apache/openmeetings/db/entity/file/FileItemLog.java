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
package org.apache.openmeetings.db.entity.file;

import static org.apache.openmeetings.util.process.ProcessResult.ZERO;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.apache.openmeetings.db.entity.IDataProviderEntity;

@Entity
@NamedQuery(name = "getFileLogsByFile", query = "SELECT fl FROM FileItemLog fl WHERE fl.fileId = :fileId")
@NamedQuery(name = "countErrorFileLogsByFile", query = "SELECT COUNT(fl) FROM FileItemLog fl WHERE fl.fileId = :fileId AND fl.optional = false AND fl.exitCode <> 0")
@NamedQuery(name = "deleteErrorFileLogsByFile", query = "DELETE FROM FileItemLog fl WHERE fl.fileId = :fileId")
@Table(name = "file_log")
public class FileItemLog implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;
	public static final int MAX_LOG_SIZE = 1 * 1024 * 1024;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "file_id")
	private Long fileId;

	@Column(name = "inserted")
	private Date inserted;

	@Column(name = "name")
	private String name;

	@Lob
	@Column(name = "message", length = MAX_LOG_SIZE)
	private String message;

	@Column(name = "exit_code")
	private Integer exitCode;

	@Column(name = "optional", nullable = false)
	private boolean optional;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Long getFileId() {
		return fileId;
	}

	public FileItemLog setFileId(Long fileId) {
		this.fileId = fileId;
		return this;
	}

	public Date getInserted() {
		return inserted;
	}

	public FileItemLog setInserted(Date inserted) {
		this.inserted = inserted;
		return this;
	}

	public String getName() {
		return name;
	}

	public FileItemLog setName(String name) {
		this.name = name;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public FileItemLog setMessage(String message) {
		this.message = message == null || message.length() < MAX_LOG_SIZE ? message : message.substring(0, MAX_LOG_SIZE);
		return this;
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public FileItemLog setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
		return this;
	}

	public boolean isOk() {
		return optional || !isWarn();
	}

	public boolean isWarn() {
		return !ZERO.equals(exitCode);
	}

	public boolean isOptional() {
		return optional;
	}

	public FileItemLog setOptional(boolean optional) {
		this.optional = optional;
		return this;
	}
}
