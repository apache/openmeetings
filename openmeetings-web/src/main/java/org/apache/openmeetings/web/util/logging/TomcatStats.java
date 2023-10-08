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
package org.apache.openmeetings.web.util.logging;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.metrics.core.metrics.Gauge;

/**
 * Exports Tomcat metrics applicable to most applications:
 *
 * - http session metrics - request processor metrics - thread pool metrics
 *
 * Example usage:
 *
 * <pre>
 * {@code
 *   TomcatStats stats = new TomcatStats(false);
 *   stats.refresh();
 * }
 * </pre>
 *
 * Example metrics being exported:
 *
 * <pre>
 * tomcat_info{version="7.0.61.0",build="Apr 29 2015 14:58:03 UTC",} 1.0
 * tomcat_session_active{context="/foo",host="default",} 877.0
 * tomcat_session_rejected{context="/foo",host="default",} 0.0
 * tomcat_session_constructed{context="/foo",host="default",} 24428.0
 * tomcat_session_expired{context="/foo",host="default",} 23832.0
 * tomcat_session_alivetime_seconds_avg{context="/foo",host="default",} 633.0
 * tomcat_session_alivetime_seconds_max{context="/foo",host="default",} 9883.0
 * tomcat_requestprocessor_received_bytes{name="http-bio-0.0.0.0-8080",} 0.0
 * tomcat_requestprocessor_sent_bytes{name="http-bio-0.0.0.0-8080",} 5056098.0
 * tomcat_requestprocessor_time_seconds{name="http-bio-0.0.0.0-8080",} 127386.0
 * tomcat_requestprocessor_error_count{name="http-bio-0.0.0.0-8080",} 0.0
 * tomcat_requestprocessor_request_count{name="http-bio-0.0.0.0-8080",} 33709.0
 * tomcat_threads{pool="http-bio-0.0.0.0-8080",} 10.0
 * tomcat_threads_active{pool="http-bio-0.0.0.0-8080",} 2.0
 * tomcat_threads_active_max{pool="http-bio-0.0.0.0-8080",} 200.0
 * </pre>
 */

public class TomcatStats {
	private static final Logger log = LoggerFactory.getLogger(TomcatStats.class);

	private static final String[] LBL_NAMES = new String[]{"name"};
	private static final Gauge rqBytesReceived = Gauge.builder()
			.name("tomcat_requestprocessor_received_bytes")
			.help("Number of bytes received by this request processor")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge rqBytesSent = Gauge.builder()
			.name("tomcat_requestprocessor_sent_bytes")
			.help("Number of bytes sent by this request processor")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge rqTime = Gauge.builder()
			.name("tomcat_requestprocessor_time_seconds")
			.help("The total time spend by this request processor")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge rqErrors = Gauge.builder()
			.name("tomcat_requestprocessor_error_count")
			.help("The number of error request served by this request processor")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge rqRequests = Gauge.builder()
			.name("tomcat_requestprocessor_request_count")
			.help("The number of request served by this request processor")
			.labelNames(LBL_NAMES)
			.register();

	private static final Gauge threads = Gauge.builder()
			.name("tomcat_threads")
			.help("Number threads in this pool.")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge threadsActive = Gauge.builder()
			.name("tomcat_threads_active")
			.help("Number of active threads in this pool.")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge threadsMax = Gauge.builder()
			.name("tomcat_threads_max")
			.help("Maximum number of threads allowed in this pool.")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge connsActive = Gauge.builder()
			.name("tomcat_connections_active")
			.help("Number of connections served by this pool.")
			.labelNames(LBL_NAMES)
			.register();
	private static final Gauge connsActiveMax = Gauge.builder()
			.name("tomcat_connections_active_max")
			.help("Maximum number of concurrent connections served by this pool.")
			.labelNames(LBL_NAMES)
			.register();

	private static final String[] SESSION_LBL_NAMES = new String[]{"host", "context"};
	private static final Gauge sessionActive = Gauge.builder()
			.name("tomcat_session_active")
			.help("Number of active sessions")
			.labelNames(SESSION_LBL_NAMES)
			.register();
	private static final Gauge sessionRejected = Gauge.builder()
			.name("tomcat_session_rejected")
			.help("Number of sessions rejected due to maxActive being reached")
			.labelNames(SESSION_LBL_NAMES)
			.register();
	private static final Gauge sessionCreated = Gauge.builder()
			.name("tomcat_session_constructed")
			.help("Number of sessions created")
			.labelNames(SESSION_LBL_NAMES)
			.register();
	private static final Gauge sessionExpired = Gauge.builder()
			.name("tomcat_session_expired")
			.help("Number of sessions that expired")
			.labelNames(SESSION_LBL_NAMES)
			.register();
	private static final Gauge sessionAvgAlive = Gauge.builder()
			.name("tomcat_session_alivetime_seconds_avg")
			.help("Average time an expired session had been alive")
			.labelNames(SESSION_LBL_NAMES)
			.register();
	private static final Gauge sessionMaxAlive = Gauge.builder()
			.name("tomcat_session_alivetime_seconds_max")
			.help("Maximum time an expired session had been alive")
			.labelNames(SESSION_LBL_NAMES)
			.register();
	private static final Gauge ctxState = Gauge.builder()
			.name("tomcat_context_state_started")
			.help("Indication if the lifecycle state of this context is STARTED")
			.labelNames(SESSION_LBL_NAMES)
			.register();

	private static final Gauge version = Gauge.builder()
			.name("tomcat_details")
			.help("tomcat version info")
			.labelNames("version", "build")
			.register();

	private final String jmxDomain;

	public TomcatStats(boolean embedded) {
		jmxDomain = embedded ? "Tomcat" : "Catalina";
	}

	private double getAttr(Attribute attribute) {
		return ((Number)attribute.getValue()).doubleValue();
	}

	private double getAttr(final MBeanServer server, final ObjectInstance mBean, String attr) throws InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException {
		return ((Number)server.getAttribute(mBean.getObjectName(), attr)).doubleValue();
	}

	private void updateRequestProcessorMetrics() {
		try {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName filterName = new ObjectName(jmxDomain + ":type=GlobalRequestProcessor,name=*");
			Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

			for (final ObjectInstance mBean : mBeans) {
				String labels = mBean.getObjectName().getKeyProperty("name").replaceAll("[\"\\\\]", "");

				rqBytesReceived.labelValues(labels)
						.set(getAttr(server, mBean, "bytesReceived"));

				rqBytesSent.labelValues(labels)
						.set(getAttr(server, mBean, "bytesSent"));

				rqTime.labelValues(labels)
						.set(getAttr(server, mBean, "processingTime") / 1000.0);

				rqErrors.labelValues(labels)
						.set(getAttr(server, mBean, "errorCount"));

				rqRequests.labelValues(labels)
						.set(getAttr(server, mBean, "requestCount"));
			}
		} catch (Exception e) {
			log.error("Error retrieving RequestProcessor metric.", e);
		}
	}

	private void updateSessionMetrics() {
		try {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName filterName = new ObjectName(jmxDomain + ":type=Manager,context=*,host=*");
			Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

			for (final ObjectInstance mBean : mBeans) {
				String[] labelValues = new String[] {
						mBean.getObjectName().getKeyProperty("host")
						, mBean.getObjectName().getKeyProperty("context")};

				sessionActive.labelValues(labelValues)
						.set(getAttr(server, mBean, "activeSessions"));

				sessionRejected.labelValues(labelValues)
						.set(getAttr(server, mBean, "rejectedSessions"));

				sessionCreated.labelValues(labelValues)
						.set(getAttr(server, mBean, "sessionCounter"));

				sessionExpired.labelValues(labelValues)
						.set(getAttr(server, mBean, "expiredSessions"));

				sessionAvgAlive.labelValues(labelValues)
						.set(getAttr(server, mBean, "sessionAverageAliveTime"));

				sessionMaxAlive.labelValues(labelValues)
						.set(getAttr(server, mBean, "sessionMaxAliveTime"));

				ctxState.labelValues(labelValues)
						.set("STARTED".equals(server.getAttribute(mBean.getObjectName(), "stateName")) ? 1. : 0.);
			}
		} catch (Exception e) {
			log.error("Error retrieving Session metric.", e);
		}
	}

	private void updateThreadPoolMetrics() {
		try {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName filterName = new ObjectName(jmxDomain + ":type=ThreadPool,name=*");
			Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

			if (!mBeans.isEmpty()) {
				String[] genericAttributes = new String[] { "currentThreadCount", "currentThreadsBusy", "maxThreads",
						"connectionCount", "maxConnections" };

				for (final ObjectInstance mBean : mBeans) {
					String label = mBean.getObjectName().getKeyProperty("name").replaceAll("[\"\\\\]", "");
					AttributeList attributeList = server.getAttributes(mBean.getObjectName(), genericAttributes);
					for (Attribute attribute : attributeList.asList()) {
						switch (attribute.getName()) {
							case "currentThreadCount":
								threads.labelValues(label).set(getAttr(attribute));
								break;
							case "currentThreadsBusy":
								threadsActive.labelValues(label).set(getAttr(attribute));
								break;
							case "maxThreads":
								threadsMax.labelValues(label).set(getAttr(attribute));
								break;
							case "connectionCount":
								connsActive.labelValues(label).set(getAttr(attribute));
								break;
							case "maxConnections":
								connsActiveMax.labelValues(label).set(getAttr(attribute));
								break;
							default:
								log.warn("Unexpected attribute {}", attribute);
								break;
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("Error retrieving ThreadPool metric: {}", e.getMessage());
		}
	}

	private void updateVersionInfo() {
		try (InputStream is = getClass().getResourceAsStream("/org/apache/catalina/util/ServerInfo.properties")) {
			Properties props = new Properties();
			props.load(is);
			//server info can be get as props.getProperty("server.info");
			version.labelValues(props.getProperty("server.number"), props.getProperty("server.built")).set(1.);
		} catch (Exception t) {
			log.warn("Unable to read Tomcat version: ", t);
		}
	}

	public void refresh() {
		updateRequestProcessorMetrics();
		updateSessionMetrics();
		updateThreadPoolMetrics();
		updateVersionInfo();
	}
}
