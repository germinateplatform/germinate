/*
 *  Copyright 2019 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

function plotlyPieChart() {
	var height = 800,
		labels = function (rows) {
			return unpack(rows, 'labels')
		},
		custom = function (rows) {
			return null;
		},
		onSliceClicked = null,
		colors = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"];

	function chart(selection) {
		selection.each(function (rows) {
			var values = unpack(rows, "count");
			var c = [];

			values.forEach(function (v, i) {
				c.push(colors[i % colors.length]);
			});

			var data = [{
				values: unpack(rows, "count"),
				labels: labels(rows),
				custom: custom(rows),
				type: 'pie',
				textinfo: 'percent',
				textposition: 'inside',
				marker: {
					colors: c
				}
			}];

			var config = {
				modeBarButtonsToRemove: ['toImage'],
				displayModeBar: false,
				responsive: true,
				displaylogo: false
			};

			var layout = {
				height: height,
				xaxis: {
					automargin: true
				},
				yaxis: {
					automargin: true
				},
				barmode: 'group',
				legend: {
					orientation: 'h'
				}
			};

			Plotly.newPlot(this, data, layout, config);

			if (onSliceClicked) {
				var currentClass = window.$(this).find('.surface').attr('class');
				window.$(this).find('.surface').attr('class', currentClass + ' gm8-cursor-pointer');

				this.on('plotly_click', function (data) {
					if (data && data.points && data.points.length > 0 && data.event && data.event.button === 0) {
						onSliceClicked(data);
					}
				});
			}
		});
	}

	function unpack(rows, key) {
		return rows.map(function (row) {
			return row[key];
		});
	}

	chart.height = function (_) {
		if (!arguments.length) return height;
		height = _;
		return chart;
	};

	chart.labels = function (_) {
		if (!arguments.length) return labels;
		labels = _;
		return chart;
	};

	chart.colors = function (_) {
		if (!arguments.length) return colors;
		colors = _;
		return chart;
	};

	chart.onSliceClicked = function (_) {
		if (!arguments.length) return onSliceClicked;
		onSliceClicked = _;
		return chart;
	};

	chart.custom = function (_) {
		if (!arguments.length) return custom;
		custom = _;
		return chart;
	};

	return chart;
}