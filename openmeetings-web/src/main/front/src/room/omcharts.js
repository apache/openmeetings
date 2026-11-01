/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
import Chart from 'chart.js/auto'
const Settings = require('../main/settings');

export class OmCharts {
	static updateChart(id, type, setLabel, labels, values) {
		const canvas = document.getElementById(id);
		let chart = canvas.chart;
		if (chart && canvas.chart.options.type != type) {
			chart.destroy();
			chart = null;
		}
		if (chart) {
			const data = canvas.chart.data;
			data.labels = labels;
			const ds = data.datasets[0];
			ds.label = setLabel;
			ds.data = values;
			chart.update();
		} else {
			const opts = {
				indexAxis: 'y',
				elements: {
					bar: {
						borderWidth: 2,
					}
				},
				responsive: true,
				plugins: {
					legend: {
						position: Settings.isRtl ? 'left' : 'right',
					},
					title: {
						display: true,
						text: setLabel
					}
				}
			};
			if ('bar' === type) {
				opts.scales = {
					y: {
						beginAtZero: true
					}
				};
				opts.plugins.legend.display = false;
			}
			canvas.chart = new Chart(canvas, {
				type: type,
				data: {
					labels: labels,
					datasets: [{
						label: setLabel,
						data: values,
						borderWidth: 1
					}]
				},
				options: opts
			});
		}
	}
};
