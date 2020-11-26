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
package org.apache.openmeetings.web.room;

import java.time.Duration;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

public abstract class OmTimerBehavior extends AbstractAjaxTimerBehavior {
	private static final long serialVersionUID = 1L;
	private final long clock;
	private final int delay;
	private final String labelId;

	protected OmTimerBehavior(int delay, String labelId) {
		super(Duration.ofSeconds(1));
		clock = System.currentTimeMillis();
		this.delay = delay;
		this.labelId = labelId;
	}

	protected static String getTime(int remain) {
		Duration d = Duration.ofSeconds(remain);
		return String.format("%d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart());
	}

	public static String getText(String text, int remain) {
		return text + ": " + getTime(remain);
	}

	protected String getText(int remain) {
		return getText(getComponent().getString(labelId), remain);
	}

	private int remain(long now) {
		return (int)(delay - (now - clock) / 1000);
	}

	@Override
	protected void onBind() {
		super.onBind();
		getComponent().setDefaultModelObject(getText(delay));
		getComponent().setOutputMarkupId(true);
		onTimer(delay);
	}

	/**
	 * @param remain - time in seconds until finish
	 */
	protected void onTimer(int remain) {
	}

	@Override
	protected void onTimer(AjaxRequestTarget target) {
		int remain = remain(System.currentTimeMillis());
		if (remain > -1) {
			getComponent().setDefaultModelObject(getText(remain));
			onTimer(remain);
			target.add(getComponent());
		} else {
			stop(target);
			onFinish(target);
		}
	}

	protected abstract void onFinish(AjaxRequestTarget target);
}
