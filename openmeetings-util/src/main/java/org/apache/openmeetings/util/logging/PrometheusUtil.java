package org.apache.openmeetings.util.logging;

import io.prometheus.client.Histogram;

public class PrometheusUtil {

	private static Histogram histogram = Histogram.build() //
			.help("OpenMeetings Application Metrics") //
			.name("org_openmeetings_metrics") //
			.labelNames("class", "method", "type", "message") //
			.register();

	public static Histogram getHistogram() {
		return histogram;
	}
}
