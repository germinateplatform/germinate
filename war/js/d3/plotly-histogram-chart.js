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

function plotlyHistogramChart() {
	var onPointsSelected = null,
		height = 600,
		xAxisTitle = '',
		yAxisTitle = '',
		colors = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"];

	function chart(selection) {
		selection.each(function (rows) {
			var data = [];
			var layout = {
				height: height,
				margin: {autoexpand: true},
				selectdirection: 'h',
				dragmode: 'select',
				legend: {
					orientation: 'h'
				},
				xaxis: {
					title: xAxisTitle
				},
				yaxis: {
					title: yAxisTitle
				}
			};

			var dims = Object.keys(rows[0]);

			dims.forEach(function (c, i) {
				data.push({
					x: unpack(rows, c),
					type: 'histogram',
					name: c,
					marker: {
						color: colors[i % colors.length]
					}
				});
			});

			var config = {
				modeBarButtonsToRemove: ['toImage'],
				displayModeBar: true,
				responsive: true,
				displaylogo: false
			};

			Plotly.newPlot(this, data, layout, config);

			var that = this;

			this.on('plotly_selected', function (eventData) {
				if (!eventData || (eventData.points.length < 1)) {
					Plotly.restyle(that, {selectedpoints: null});
				} else {
					if (onPointsSelected)
						onPointsSelected(eventData.points);
				}
			});
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

	chart.colors = function (_) {
		if (!arguments.length) return colors;
		colors = _;
		return chart;
	};

	chart.xAxisTitle = function (_) {
		if (!arguments.length) return xAxisTitle;
		xAxisTitle = _;
		return chart;
	};

	chart.yAxisTitle = function (_) {
		if (!arguments.length) return yAxisTitle;
		yAxisTitle = _;
		return chart;
	};

	chart.onPointsSelected = function (_) {
		if (!arguments.length) return onPointsSelected;
		onPointsSelected = _;
		return chart;
	};

	return chart;
}