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
package org.apache.openmeetings.util.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import io.prometheus.metrics.core.metrics.Histogram;
import io.prometheus.metrics.model.snapshots.Unit;

@Aspect
@Component
public class PrometheusAspect {
	private static final Histogram histogram = Histogram.builder()
			.help("OpenMeetings Application Metrics")
			.name("org_openmeetings_metrics")
			.labelNames("class", "method", "type", "message")
			.register();

	private Object logExecutionTime(ProceedingJoinPoint joinPoint, String logType) throws Throwable {
		String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		long start = System.nanoTime();
		try {
			return joinPoint.proceed();
		} finally {
			histogram.labelValues(className, methodName, logType, "default")
					.observe(Unit.nanosToSeconds(System.nanoTime() - start));
		}
	}

	@Around("@annotation(TimedDatabase)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		return logExecutionTime(joinPoint, "database");
	}

	@Around("@annotation(TimedApplication)")
	public Object logExecutionTimedApplication(ProceedingJoinPoint joinPoint) throws Throwable {
		return logExecutionTime(joinPoint, "application");
	}
}
