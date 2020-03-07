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
package org.apache.openmeetings.backup;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sun.xml.bind.marshaller.CharacterEscapeHandler;

public class TestExport extends AbstractJUnitDefaults {
	@Test
	public void exportMain() throws Exception {
		BackupExport.main(new String[] {File.createTempFile("gereral", "cfg").getCanonicalPath()});
	}

	@Test
	public void exportUser() throws Exception {
		User u = createUser();
		u.setAge(LocalDate.of(1977, 11, 13));
		Group g = groupDao.get(1L);
		u.addGroup(g);
		Class<User> eClazz = User.class;
		JAXBContext jc = JAXBContext.newInstance(eClazz);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
			@Override
			public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException {
				writer.write(ac, i, j);
			}
		});
		StringWriter writer = new StringWriter();
		marshaller.marshal(u, writer);
		Assertions.assertNotNull(writer.getBuffer());
	}
}
