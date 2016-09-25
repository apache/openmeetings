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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.process.ConverterProcessResult.ZERO;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.file.FileItem.Type;

@Entity
@NamedQueries({
		@NamedQuery(name = "getFileLogsByFile", query = "SELECT fl FROM FileItemLog fl WHERE fl.fileId = :fileId AND fl.fileType = :type"),
		@NamedQuery(name = "countErrorFileLogsByFile", query = "SELECT COUNT(fl) FROM FileItemLog fl WHERE fl.fileId = :fileId AND fl.fileType = :type AND fl.exitCode <> 0"),
		@NamedQuery(name = "deleteErrorFileLogsByFile", query = "DELETE FROM FileItemLog fl WHERE fl.fileId = :fileId AND fl.fileType = :type") })
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

	@Column(name = "file_type")
	@Enumerated(EnumType.STRING)
	private Type fileType;

	@Column(name = "inserted")
	private Date inserted;

	@Column(name = "name")
	private String name;

	@Lob
	@Column(name = "message", length = MAX_LOG_SIZE)
	private byte[] bytes;

	@Column(name = "exit_code")
	private Integer exitCode;

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

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Type getFileType() {
		return fileType;
	}

	public void setFileType(Type fileType) {
		this.fileType = fileType;
	}

	public Date getInserted() {
		return inserted;
	}

	public void setInserted(Date inserted) {
		this.inserted = inserted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return bytes == null ? null : new String(bytes, UTF_8);
	}

	public void setMessage(String message) {
		setBytes(message.getBytes(UTF_8));
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public void setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] a) {
		this.bytes = a == null || a.length < MAX_LOG_SIZE ? a : Arrays.copyOf(a, MAX_LOG_SIZE);
	}

	public boolean isOk() {
		return ZERO.equals(exitCode);
	}
}
