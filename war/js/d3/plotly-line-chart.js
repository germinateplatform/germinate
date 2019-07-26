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

function plotlyLineChart() {
	var colors = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"],
		xaxisTickText = null,
		xaxisTickVals = null,
		columnsToIgnore= [],
		yaxisTitle = '',
		yaxisRangeMode = 'normal',
		getText = function (rows, dim) {
			return '';
		},
		x = '',
		height = 700;

	function unpack(rows, key) {
		return rows.map(function (row) {
			return row[key];
		});
	}

	function chart(selection) {
		selection.each(function (rows) {
			var dims = Object.keys(rows[0]);
			dims = dims.filter(function (d) {
				return columnsToIgnore.indexOf(d) < 0;
			});

			var data = [];

			for (var i = 0; i < dims.length; i++) {
				data.push({
					x: unpack(rows, x),
					y: unpack(rows, dims[i]),
					name: dims[i],
					mode: 'lines',
					text: getText(rows, dims[i]),
					hovertemplate: '%{y} - %{text}',
					line: {
						shape: 'spline',
						color: colors[i % colors.length]
					},
					type: 'scatter'
				});
			}

			var layout = {
				autosize: true,
				height: height,
				hovermode: 'x',
				legend: {
					orientation: 'h'
				},
				xaxis: {
					ticktext: xaxisTickText,
					tickvals: xaxisTickVals
				},
				yaxis: {
					title: yaxisTitle,
					rangemode: yaxisRangeMode
				}
			};

			var config = {
				modeBarButtonsToRemove: ['toImage'],
				displayModeBar: true,
				responsive: true,
				displaylogo: false
			};

			Plotly.plot(this, data, layout, config);
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

	chart.xaxisTickText = function (_) {
		if (!arguments.length) return xaxisTickText;
		xaxisTickText = _;
		return chart;
	};

	chart.xaxisTickVals = function (_) {
		if (!arguments.length) return xaxisTickVals;
		xaxisTickVals = _;
		return chart;
	};

	chart.columnsToIgnore = function (_) {
		if (!arguments.length) return columnsToIgnore;
		columnsToIgnore = _;
		return chart;
	};

	chart.yaxisTitle = function (_) {
		if (!arguments.length) return yaxisTitle;
		yaxisTitle = _;
		return chart;
	};

	chart.yaxisRangeMode = function (_) {
		if (!arguments.length) return yaxisRangeMode;
		yaxisRangeMode = _;
		return chart;
	};

	chart.getText = function (_) {
		if (!arguments.length) return getText;
		getText = _;
		return chart;
	};

	chart.x = function (_) {
		if (!arguments.length) return x;
		x = _;
		return chart;
	};

	return chart;
}