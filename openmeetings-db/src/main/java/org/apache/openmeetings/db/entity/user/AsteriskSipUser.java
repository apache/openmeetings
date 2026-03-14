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
package org.apache.openmeetings.db.entity.user;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.Serializable;
import java.util.HexFormat;

import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.bouncycastle.crypto.digests.SHA256Digest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "om_sipuser_auth")
@SecondaryTables({
	@SecondaryTable(name = "om_sipuser_aor", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id")),
	@SecondaryTable(name = "om_sipuser_endpoint", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
})
@XmlRootElement(name = "asterisksipuser")
@XmlAccessorType(XmlAccessType.FIELD)
public class AsteriskSipUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int HASH_LEN = 256;
	private static final String HASH_ALG = "SHA-256";//This one seems to be less supported "SHA-512-256";

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sip_db_id", unique = true, nullable = false)
	@XmlElement(name = "id", required = true)
	private long userId;

	@Id
	@Column(name = "id")
	@XmlTransient
	private String id;

	@Column(name = "max_contacts", table = "om_sipuser_aor")
	@XmlElement(name = "maxContacts", required = false)
	private int maxContacts = OpenmeetingsVariables.getDefaultSipMaxContacts();

	@Column(name = "remove_existing", table = "om_sipuser_aor", nullable = false)
	@XmlTransient
	private String removeExisting = "yes";

	@Column(name = "auth_type")
	@XmlTransient
	private String authType = "digest";

	@Column(name = "username")
	@XmlTransient
	private String username;

	@Column(name = "password_digest")
	@XmlElement(name = "md5secret", required = true) // historical name to comply with old format
	private String passwordDigest;

	@Column(name = "supported_algorithms_uac", nullable = false)
	private String supportedAlgorithmsUac = HASH_ALG;

	@Column(name = "supported_algorithms_uas", nullable = false)
	private String supportedAlgorithmsUas = HASH_ALG;

	@Column(name = "aors", table = "om_sipuser_endpoint")
	@XmlTransient
	private String aors;

	@Column(name = "auth", table = "om_sipuser_endpoint")
	@XmlTransient
	private String auth;

	@Column(name = "context", table = "om_sipuser_endpoint")
	@XmlElement(name = "context", required = false)
	private String context; // Varchar 128

	@Column(name = "transport", nullable = false, table = "om_sipuser_endpoint")
	@XmlElement(name = "transport", required = false)
	private String transport = OpenmeetingsVariables.getDefaultSipTransport();

	@Column(name = "allow", nullable = false, length = 100, table = "om_sipuser_endpoint")
	@XmlElement(name = "allow", required = false)
	private String allow = "!all,ulaw,opus,vp8";

	@Column(name = "mailboxes", table = "om_sipuser_endpoint")
	@XmlTransient
	private String mailboxes;

	public void setUsername(String username) {
		this.id = username;
		this.username = username;
		this.aors = username;
		this.auth = username;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void setPasswordDigest(String pass) {
		SHA256Digest digest = new SHA256Digest();
		byte[] bytes = pass.getBytes(UTF_8);
		digest.update(bytes, 0, bytes.length);
		byte[] enc = new byte[HASH_LEN / 8];
		digest.doFinal(enc, 0);
		this.passwordDigest = HASH_ALG + ":" + HexFormat.of().formatHex(enc);
	}
}
