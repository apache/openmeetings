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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Collector;
import io.prometheus.client.CounterMetricFamily;
import io.prometheus.client.GaugeMetricFamily;

/**
 * Exports Tomcat metrics applicable to most most applications:
 *
 * - http session metrics - request processor metrics - thread pool metrics
 *
 * Example usage:
 *
 * <pre>
 * {@code
 *   new TomcatGenericExports(false).register();
 * }
 * </pre>
 *
 * Example metrics being exported:
 *
 * <pre>
 * tomcat_info{version="7.0.61.0",build="Apr 29 2015 14:58:03 UTC",} 1.0
 * tomcat_session_active_total{context="/foo",host="default",} 877.0
 * tomcat_session_rejected_total{context="/foo",host="default",} 0.0
 * tomcat_session_created_total{context="/foo",host="default",} 24428.0
 * tomcat_session_expired_total{context="/foo",host="default",} 23832.0
 * tomcat_session_alivetime_seconds_avg{context="/foo",host="default",} 633.0
 * tomcat_session_alivetime_seconds_max{context="/foo",host="default",} 9883.0
 * tomcat_requestprocessor_received_bytes{name="http-bio-0.0.0.0-8080",} 0.0
 * tomcat_requestprocessor_sent_bytes{name="http-bio-0.0.0.0-8080",} 5056098.0
 * tomcat_requestprocessor_time_seconds{name="http-bio-0.0.0.0-8080",} 127386.0
 * tomcat_requestprocessor_error_count{name="http-bio-0.0.0.0-8080",} 0.0
 * tomcat_requestprocessor_request_count{name="http-bio-0.0.0.0-8080",} 33709.0
 * tomcat_threads_total{pool="http-bio-0.0.0.0-8080",} 10.0
 * tomcat_threads_active_total{pool="http-bio-0.0.0.0-8080",} 2.0
 * tomcat_threads_active_max{pool="http-bio-0.0.0.0-8080",} 200.0
 * </pre>
 */
public class TomcatGenericExports extends Collector {
	private static final Logger log = LoggerFactory.getLogger(TomcatGenericExports.class);
	private static final String LABEL_NAME = "name";
	private String jmxDomain = "Catalina";

	public TomcatGenericExports(boolean embedded) {
		if (embedded) {
			jmxDomain = "Tomcat";
		}
	}

	private void addRequestProcessorMetrics(List<MetricFamilySamples> mfs) {
		try {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName filterName = new ObjectName(jmxDomain + ":type=GlobalRequestProcessor,name=*");
			Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

			if (!mBeans.isEmpty()) {
				List<String> labelNameList = List.of(LABEL_NAME);

				GaugeMetricFamily requestProcessorBytesReceivedGauge = new GaugeMetricFamily(
						"tomcat_requestprocessor_received_bytes", "Number of bytes received by this request processor",
						labelNameList);

				GaugeMetricFamily requestProcessorBytesSentGauge = new GaugeMetricFamily(
						"tomcat_requestprocessor_sent_bytes", "Number of bytes sent by this request processor",
						labelNameList);

				GaugeMetricFamily requestProcessorProcessingTimeGauge = new GaugeMetricFamily(
						"tomcat_requestprocessor_time_seconds", "The total time spend by this request processor",
						labelNameList);

				CounterMetricFamily requestProcessorErrorCounter = new CounterMetricFamily(
						"tomcat_requestprocessor_error_count",
						"The number of error request served by this request processor", labelNameList);

				CounterMetricFamily requestProcessorRequestCounter = new CounterMetricFamily(
						"tomcat_requestprocessor_request_count",
						"The number of request served by this request processor", labelNameList);

				for (final ObjectInstance mBean : mBeans) {
					List<String> labelValueList = Collections
							.singletonList(mBean.getObjectName().getKeyProperty("name").replaceAll("[\"\\\\]", ""));

					requestProcessorBytesReceivedGauge.addMetric(labelValueList,
							((Long) server.getAttribute(mBean.getObjectName(), "bytesReceived")).doubleValue());

					requestProcessorBytesSentGauge.addMetric(labelValueList,
							((Long) server.getAttribute(mBean.getObjectName(), "bytesSent")).doubleValue());

					requestProcessorProcessingTimeGauge.addMetric(labelValueList,
							((Long) server.getAttribute(mBean.getObjectName(), "processingTime")).doubleValue()
									/ 1000.0);

					requestProcessorErrorCounter.addMetric(labelValueList,
							((Integer) server.getAttribute(mBean.getObjectName(), "errorCount")).doubleValue());

					requestProcessorRequestCounter.addMetric(labelValueList,
							((Integer) server.getAttribute(mBean.getObjectName(), "requestCount")).doubleValue());
				}

				mfs.add(requestProcessorBytesReceivedGauge);
				mfs.add(requestProcessorBytesSentGauge);
				mfs.add(requestProcessorProcessingTimeGauge);
				mfs.add(requestProcessorRequestCounter);
				mfs.add(requestProcessorErrorCounter);
			}
		} catch (Exception e) {
			log.error("Error retrieving metric.", e);
		}
	}

	private void addSessionMetrics(List<MetricFamilySamples> mfs) {
		try {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName filterName = new ObjectName(jmxDomain + ":type=Manager,context=*,host=*");
			Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

			if (!mBeans.isEmpty()) {
				List<String> labelNameList = List.of("host", "context");

				GaugeMetricFamily activeSessionCountGauge = new GaugeMetricFamily("tomcat_session_active_total",
						"Number of active sessions", labelNameList);

				GaugeMetricFamily rejectedSessionCountGauge = new GaugeMetricFamily("tomcat_session_rejected_total",
						"Number of sessions rejected due to maxActive being reached", labelNameList);

				GaugeMetricFamily createdSessionCountGauge = new GaugeMetricFamily("tomcat_session_created_total",
						"Number of sessions created", labelNameList);

				GaugeMetricFamily expiredSessionCountGauge = new GaugeMetricFamily("tomcat_session_expired_total",
						"Number of sessions that expired", labelNameList);

				GaugeMetricFamily sessionAvgAliveTimeGauge = new GaugeMetricFamily(
						"tomcat_session_alivetime_seconds_avg", "Average time an expired session had been alive",
						labelNameList);

				GaugeMetricFamily sessionMaxAliveTimeGauge = new GaugeMetricFamily(
						"tomcat_session_alivetime_seconds_max", "Maximum time an expired session had been alive",
						labelNameList);

				GaugeMetricFamily contextStateGauge = new GaugeMetricFamily("tomcat_context_state_started",
						"Indication if the lifecycle state of this context is STARTED", labelNameList);

				for (final ObjectInstance mBean : mBeans) {
					List<String> labelValueList = List.of(mBean.getObjectName().getKeyProperty("host"),
							mBean.getObjectName().getKeyProperty("context"));

					activeSessionCountGauge.addMetric(labelValueList,
							((Integer) server.getAttribute(mBean.getObjectName(), "activeSessions")).doubleValue());

					rejectedSessionCountGauge.addMetric(labelValueList,
							((Integer) server.getAttribute(mBean.getObjectName(), "rejectedSessions")).doubleValue());

					createdSessionCountGauge.addMetric(labelValueList,
							((Long) server.getAttribute(mBean.getObjectName(), "sessionCounter")).doubleValue());

					expiredSessionCountGauge.addMetric(labelValueList,
							((Long) server.getAttribute(mBean.getObjectName(), "expiredSessions")).doubleValue());

					sessionAvgAliveTimeGauge.addMetric(labelValueList,
							((Integer) server.getAttribute(mBean.getObjectName(), "sessionAverageAliveTime"))
									.doubleValue());

					sessionMaxAliveTimeGauge.addMetric(labelValueList,
							((Integer) server.getAttribute(mBean.getObjectName(), "sessionMaxAliveTime"))
									.doubleValue());

					if (server.getAttribute(mBean.getObjectName(), "stateName").equals("STARTED")) {
						contextStateGauge.addMetric(labelValueList, 1.0);
					} else {
						contextStateGauge.addMetric(labelValueList, 0.0);
					}
				}

				mfs.add(activeSessionCountGauge);
				mfs.add(rejectedSessionCountGauge);
				mfs.add(createdSessionCountGauge);
				mfs.add(expiredSessionCountGauge);
				mfs.add(sessionAvgAliveTimeGauge);
				mfs.add(sessionMaxAliveTimeGauge);
				mfs.add(contextStateGauge);
			}
		} catch (Exception e) {
			log.error("Error retrieving metric.", e);
		}
	}

	private void addThreadPoolMetrics(List<MetricFamilySamples> mfs) {
		try {
			final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			ObjectName filterName = new ObjectName(jmxDomain + ":type=ThreadPool,name=*");
			Set<ObjectInstance> mBeans = server.queryMBeans(filterName, null);

			if (!mBeans.isEmpty()) {
				List<String> labelList = List.of(LABEL_NAME);

				GaugeMetricFamily threadPoolCurrentCountGauge = new GaugeMetricFamily("tomcat_threads_total",
						"Number threads in this pool.", labelList);

				GaugeMetricFamily threadPoolActiveCountGauge = new GaugeMetricFamily("tomcat_threads_active_total",
						"Number of active threads in this pool.", labelList);

				GaugeMetricFamily threadPoolMaxThreadsGauge = new GaugeMetricFamily("tomcat_threads_max",
						"Maximum number of threads allowed in this pool.", labelList);

				GaugeMetricFamily threadPoolConnectionCountGauge = new GaugeMetricFamily(
						"tomcat_connections_active_total", "Number of connections served by this pool.", labelList);

				GaugeMetricFamily threadPoolMaxConnectionGauge = new GaugeMetricFamily("tomcat_connections_active_max",
						"Maximum number of concurrent connections served by this pool.", labelList);

				String[] genericAttributes = new String[] { "currentThreadCount", "currentThreadsBusy", "maxThreads",
						"connectionCount", "maxConnections" };

				for (final ObjectInstance mBean : mBeans) {
					List<String> labelValueList = Collections
							.singletonList(mBean.getObjectName().getKeyProperty("name").replaceAll("[\"\\\\]", ""));
					AttributeList attributeList = server.getAttributes(mBean.getObjectName(), genericAttributes);
					for (Attribute attribute : attributeList.asList()) {
						switch (attribute.getName()) {
							case "currentThreadCount":
								threadPoolCurrentCountGauge.addMetric(labelValueList,
										((Integer) attribute.getValue()).doubleValue());
								break;
							case "currentThreadsBusy":
								threadPoolActiveCountGauge.addMetric(labelValueList,
										((Integer) attribute.getValue()).doubleValue());
								break;
							case "maxThreads":
								threadPoolMaxThreadsGauge.addMetric(labelValueList,
										((Integer) attribute.getValue()).doubleValue());
								break;
							case "connectionCount":
								threadPoolConnectionCountGauge.addMetric(labelValueList,
										((Long) attribute.getValue()).doubleValue());
								break;
							case "maxConnections":
								threadPoolMaxConnectionGauge.addMetric(labelValueList,
										((Integer) attribute.getValue()).doubleValue());
								break;
							default:
								log.warn("Unexpected attribute {}", attribute);
								break;
						}
					}
				}

				addNonEmptyMetricFamily(mfs, threadPoolCurrentCountGauge);
				addNonEmptyMetricFamily(mfs, threadPoolActiveCountGauge);
				addNonEmptyMetricFamily(mfs, threadPoolMaxThreadsGauge);
				addNonEmptyMetricFamily(mfs, threadPoolConnectionCountGauge);
				addNonEmptyMetricFamily(mfs, threadPoolMaxConnectionGauge);
			}
		} catch (Exception e) {
			log.error("Error retrieving metric: {}", e.getMessage());
		}
	}

	private void addVersionInfo(List<MetricFamilySamples> mfs) {
		GaugeMetricFamily tomcatInfo = new GaugeMetricFamily("tomcat_info", "tomcat version info",
				List.of("version", "build"));
		try (InputStream is = getClass().getResourceAsStream("/org/apache/catalina/util/ServerInfo.properties")) {
			Properties props = new Properties();
			props.load(is);
			//server info can be get as props.getProperty("server.info");
			tomcatInfo.addMetric(List.of(props.getProperty("server.number"), props.getProperty("server.built")), 1);
		} catch (Throwable t) {
			log.warn("Unable to read Tomcat version: ", t);
		}
		mfs.add(tomcatInfo);
	}

	private void addNonEmptyMetricFamily(List<MetricFamilySamples> mfs, GaugeMetricFamily metricFamily) {
		if (!metricFamily.samples.isEmpty()) {
			mfs.add(metricFamily);
		}
	}

	@Override
	public List<MetricFamilySamples> collect() {
		List<MetricFamilySamples> mfs = new ArrayList<>();
		addSessionMetrics(mfs);
		addThreadPoolMetrics(mfs);
		addRequestProcessorMetrics(mfs);
		addVersionInfo(mfs);
		return mfs;

	}
}
