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
package org.apache.openmeetings.db.entity.basic;

import java.io.IOException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.apache.openmeetings.db.entity.HistoricalEntity;
import org.apache.openmeetings.util.mail.IcalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.validate.ValidationException;

@Entity
@NamedQuery(name = "getMailMessageById", query = "SELECT m FROM MailMessage m WHERE m.id = :id")
@NamedQuery(name = "getMailMessages", query = "SELECT m FROM MailMessage m ORDER BY m.updated, m.inserted")
@NamedQuery(name = "getMailMessagesByStatus", query = "SELECT m FROM MailMessage m WHERE m.status = :status ORDER BY m.updated, m.inserted")
@NamedQuery(name = "countMailMessages", query = "SELECT COUNT(m) FROM MailMessage m")
@NamedQuery(name = "resetMailStatusByDate", query = "UPDATE MailMessage m SET m.status = :noneStatus WHERE m.status = :sendingStatus AND m.updated < :date")
@NamedQuery(name = "resetMailStatusById", query = "UPDATE MailMessage m SET m.errorCount = 0, m.status = :noneStatus WHERE m.id = :id")
@NamedQuery(name = "purgeMailMessages", query = "DELETE FROM MailMessage m WHERE m.recipients LIKE :email OR m.replyTo LIKE :email")
@Table(name = "email_queue")
public class MailMessage extends HistoricalEntity {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(MailMessage.class);

	public enum Status {
		NONE, SENDING, ERROR, DONE
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Lob
	@Column(name = "recipients")
	private String recipients;

	@Column(name = "replyTo")
	private String replyTo;

	@Column(name = "subject")
	private String subject;

	@Lob
	@Column(name = "body")
	private String body;

	@Lob
	@Column(name = "ics")
	private byte[] ics;

	@Column(name = "ics_method")
	private String icsMethod;

	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status = Status.NONE;

	@Column(name = "error_count", nullable = false)
	private int errorCount = 0;

	@Lob
	@Column(name = "last_error")
	private String lastError;

	public MailMessage() {
		this(null, null, null, null, null);
	}

	public MailMessage(String recipients, String replyTo, String subject, String body) {
		this(recipients, replyTo, subject, body, null);
	}

	public MailMessage(String recipients, String replyTo, String subject, String body, IcalHandler ical) {
		this.recipients = recipients;
		this.replyTo = replyTo;
		this.subject = subject;
		this.body = body;
		if (ical != null) {
			this.icsMethod = ical.getMethod().getValue();
			try {
				this.ics = ical.toByteArray();
			} catch (ValidationException|IOException e) {
				log.error("Unexpected error while getting ICS", e);
			}
		}
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public byte[] getIcs() {
		return ics;
	}

	public void setIcs(byte[] ics) {
		this.ics = ics;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public String getLastError() {
		return lastError;
	}

	public void setLastError(String lastError) {
		this.lastError = lastError;
	}

	public String getIcsMethod() {
		return icsMethod == null ? Method.VALUE_REQUEST : icsMethod;
	}
}
