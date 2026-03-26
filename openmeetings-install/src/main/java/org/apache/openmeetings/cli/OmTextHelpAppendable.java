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
package org.apache.openmeetings.cli;

import static org.apache.openmeetings.cli.OmHelpFormatter.DELIMITER;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextHelpAppendable;

public class OmTextHelpAppendable extends TextHelpAppendable {
	OmTextHelpAppendable(int maxWidth) {
		super(System.out);
		setMaxWidth(maxWidth);
	}

	@Override
	protected TableDefinition adjustTableFormat(TableDefinition table) {
		TableDefinition def = super.adjustTableFormat(table);
		int firstWidth = def.columnTextStyles().get(0).getMaxWidth();
		String first = "-".repeat(firstWidth);
		String second = "-".repeat(getMaxWidth() - firstWidth);
		List<List<String>> rows = new ArrayList<>();
		def.rows().forEach(list -> {
			if (list.size() == 2 && DELIMITER.equals(list.get(0)) && DELIMITER.equals(list.get(1))) {
				rows.add(List.of(first, second));
			} else {
				rows.add(list);
			}
		});
		return TableDefinition.from(def.caption(), def.columnTextStyles(), def.headers(), rows);
	}
}
