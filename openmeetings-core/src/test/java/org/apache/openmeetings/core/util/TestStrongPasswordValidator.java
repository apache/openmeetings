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
package org.apache.openmeetings.core.util;

import static org.apache.openmeetings.util.OpenmeetingsVariables.setPwdCheckDigit;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setPwdCheckSpecial;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setPwdCheckUpper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.wicket.validation.Validatable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

class TestStrongPasswordValidator {
	private static User getUser(String login, String email) {
		User u = new User();
		u.setLogin(login);
		u.setAddress(new Address());
		u.getAddress().setEmail(email);
		return u;
	}

	private static User getUser3() {
		return getUser("2222", "2222@local");
	}

	private static Stream<Arguments> provideTestArgs() {
		List<Arguments> args = new ArrayList<>();
		for (boolean web : new boolean[] {true, false}) {
			args.add(Arguments.of(null, web, getUser(null, null), 5));
			User u1 = getUser("1", null);
			args.add(Arguments.of(null, web, u1, 5));
			User u2 = getUser("2222", null);
			args.add(Arguments.of("1", web, u2, 4));
			User u3 = getUser3();
			args.add(Arguments.of("password", web, u3, 3));
			args.add(Arguments.of("passWord", web, u3, 2));
			args.add(Arguments.of("passWord222", web, u3, 2));
			args.add(Arguments.of("passWord2!", web, u3, 0));
		}
		return args.stream();
	}

	void runWrapped(Runnable task) {
		try (MockedStatic<LabelDao> labelMock = mockStatic(LabelDao.class)) {
			labelMock.when(() -> LabelDao.getString(any(String.class), any(Long.class))).then(new Answer<String>() {
				@Override
				public String answer(InvocationOnMock invocation) throws Throwable {
					return invocation.getArgument(0);
				}
			});
			task.run();
		}
	}

	@Test
	void testDefCtr() {
		runWrapped(() -> {
			Validatable<String> pass = new Validatable<>(null);
			StrongPasswordValidator validator = new StrongPasswordValidator(new User());
			validator.validate(pass);
			assertEquals(5, pass.getErrors().size());
		});
	}

	@Test
	void testSetUser() {
		runWrapped(() -> {
			Validatable<String> pass = new Validatable<>(null);
			StrongPasswordValidator validator = new StrongPasswordValidator(null);
			validator.setUser(new User());
			validator.validate(pass);
			assertEquals(5, pass.getErrors().size());
		});
	}

	@Test
	void testNoUpper() {
		try {
			setPwdCheckUpper(false);
			test("password", false, getUser3(), 2);
		} finally {
			setPwdCheckUpper(true);
		}
	}

	@Test
	void testNoDigit() {
		try {
			setPwdCheckDigit(false);
			test("password", false, getUser3(), 2);
		} finally {
			setPwdCheckDigit(true);
		}
	}

	@Test
	void testNoSpecial() {
		try {
			setPwdCheckSpecial(false);
			test("password", false, getUser3(), 2);
		} finally {
			setPwdCheckSpecial(true);
		}
	}

	@ParameterizedTest
	@MethodSource("provideTestArgs")
	void test(String pwd, boolean web, User u, int expectedErrors) {
		runWrapped(() -> {
			Validatable<String> pass = new Validatable<>(pwd);
			StrongPasswordValidator validator = new StrongPasswordValidator(web, u);
			validator.validate(pass);
			assertEquals(expectedErrors, pass.getErrors().size(), "Expected exactly " + expectedErrors + " errors, pass: '" + pwd + "', user: " + u);
		});
	}
}
