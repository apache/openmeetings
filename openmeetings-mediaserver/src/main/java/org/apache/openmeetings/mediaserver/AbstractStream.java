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
package org.apache.openmeetings.mediaserver;

import org.kurento.client.CertificateKeyType;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.RtpEndpoint;
import org.kurento.client.WebRtcEndpoint;

public abstract class AbstractStream {
	protected final String sid;
	protected final String uid;

	protected AbstractStream(final String sid, final String uid) {
		this.sid = sid;
		this.uid = uid;
	}

	public String getSid() {
		return sid;
	}

	public String getUid() {
		return uid;
	}

	public void release() {
		release(true);
	}

	public abstract void release(boolean remove);

	public static WebRtcEndpoint createWebRtcEndpoint(MediaPipeline pipeline, Boolean recv,
			CertificateKeyType certificateType) {
		WebRtcEndpoint.Builder builder = new WebRtcEndpoint.Builder(pipeline);
		// See https://doc-kurento.readthedocs.io/en/latest/features/security.html#media-plane-security-dtls
		if (CertificateKeyType.RSA == certificateType) {
			builder.withCertificateKeyType(CertificateKeyType.RSA);
		} else if (CertificateKeyType.ECDSA == certificateType) {
			builder.withCertificateKeyType(CertificateKeyType.ECDSA);
		}
		if (recv != null) {
			if (recv) {
				builder.recvonly();
			} else {
				builder.sendonly();
			}
		}
		return builder.build();
	}

	public static RecorderEndpoint createRecorderEndpoint(MediaPipeline pipeline, String path, MediaProfileSpecType profile) {
		return new RecorderEndpoint.Builder(pipeline, path)
				.stopOnEndOfStream()
				.withMediaProfile(profile).build();
	}

	public static PlayerEndpoint createPlayerEndpoint(MediaPipeline pipeline, String path) {
		return new PlayerEndpoint.Builder(pipeline, path).build();
	}

	public static RtpEndpoint createRtpEndpoint(MediaPipeline pipeline) {
		return new RtpEndpoint.Builder(pipeline).build();
	}
}
