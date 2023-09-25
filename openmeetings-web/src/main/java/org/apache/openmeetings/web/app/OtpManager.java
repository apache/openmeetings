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
package org.apache.openmeetings.web.app;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;
import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;

import java.net.UnknownHostException;

import jakarta.annotation.PostConstruct;

import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.NtpTimeProvider;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

@Service
public class OtpManager {
	private static final Logger log = LoggerFactory.getLogger(OtpManager.class);
	// these properties are hardcoded into Google Authenticator :(
	private static final int DIGITS = 6;
	private static final int PERIOD = 30;
	private static final HashingAlgorithm ALGORITHM = HashingAlgorithm.SHA1;

	private final SecretGenerator secretGenerator = new DefaultSecretGenerator(128);
	private CodeVerifier codeVerifier;
	@Value("${otp.issuer}")
	private String issuer = "";
	@Value("${otp.ntp.server}")
	private String ntpServer = "pool.ntp.org";
	@Value("${otp.ntp.timeout}")
	private int ntpTimeout = 6;

	@PostConstruct
	public void init() throws UnknownHostException {
		CodeGenerator codeGenerator = new DefaultCodeGenerator(ALGORITHM, DIGITS);
		TimeProvider timeProvider;
		try {
			timeProvider = new NtpTimeProvider(ntpServer, ntpTimeout);
		} catch (UnknownHostException e) {
			log.error("Unable to create NTP time provider, fallback to system, Please CHECK your setup", e);
			timeProvider = new SystemTimeProvider();
		}
		final DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
		verifier.setTimePeriod(PERIOD);
		codeVerifier = verifier;
	}

	public String generateSecret() {
		return secretGenerator.generate();
	}

	public String getQr(String userEmail, String secret) {
		QrData data = new QrData.Builder()
			.label(userEmail)
			.secret(secret)
			.issuer(Strings.isEmpty(issuer) ? getApplicationName() : issuer)
			.algorithm(ALGORITHM)
			.digits(DIGITS)
			.period(PERIOD)
			.build();
		QrGenerator generator = new ZxingPngQrGenerator();
		try {
			byte[] imageData = generator.generate(data);
			return getDataUriForImage(imageData, PNG_MIME_TYPE);
		} catch (QrGenerationException e) {
			log.error("Unexpected exception while generating QR", e);
		}
		return "";
	}

	public String[] getRecoveryCodes() {
		RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
		return recoveryCodes.generateCodes(16);
	}

	public boolean verify(String secret, String code) {
		return codeVerifier.isValidCode(secret, code);
	}
}
