/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openmeetings.web.util;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.string.Strings;

public class ThreadHelper {
	private ThreadHelper() {}

	public static void startRunnable(Runnable r) {
		startRunnable(r, null);
	}

	public static void startRunnable(Runnable r, String name) {
		final Application app = Application.get();
		final WebSession session = WebSession.get();
		final RequestCycle rc = RequestCycle.get();
		Thread t = new Thread(() -> {
			try {
				ThreadContext.setApplication(app);
				ThreadContext.setSession(session);
				ThreadContext.setRequestCycle(rc);
				r.run();
			} finally {
				ThreadContext.detach();
			}
		});
		if (!Strings.isEmpty(name)) {
			t.setName(name);
		}
		t.start();
	}
}
