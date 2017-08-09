/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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

function barChart() {
	var margin = {
			top: 20,
			right: 20,
			bottom: 40,
			left: 50
		},
		width = 960,
		height = 500,
		xAxisStart = 0,
		xScale = d3.scale.ordinal(),
		yScale = d3.scale.linear(),
		yTickFormat = d3.format(".2s"),
		xValue = function (d) {
			return d[0];
		},
		yValue = function (d) {
			return d[1];
		},
		tooltip = function (d) {
			return null;
		},
		color = "steelblue",
		xAxis = d3.svg.axis().scale(xScale).orient("bottom"),
		yAxis = d3.svg.axis().scale(yScale).orient("left"),
		yLabel = null,
		xLabel = null,
		axisStyle = "",
		barStyle = "",
		tooltipStyle = "",
		outerPadding = 0.1,
		tip,
		sort = null,
		removeXAxisTicks = false;

	function chart(selection) {
		selection.each(function (data) {

			height = height - margin.bottom - margin.top;
			width = width - margin.left - margin.right;

			// Convert data to standard representation greedily;
			// this is needed for nondeterministic accessors.
			data = data.map(function (d, i) {
				return {
					orig: d,
					x: xValue.call(data, d, i),
					y: yValue.call(data, d, i)
				};
			});

			// Update the x-scale.
			xScale.domain(data.map(function (d) {
				return d.x;
			}));

			if(outerPadding === 0)
				xScale.rangeBands([xAxisStart, width]);
			else
				xScale.rangeRoundBands([xAxisStart, width], outerPadding);

			// Update the y-scale.
			yScale.domain([Math.min(0, d3.min(data, function (d) {
				return d.y;
			})), d3.max(data, function (d) {
				return d.y;
			})]).range([height, 0]);

			if (removeXAxisTicks)
				xAxis.tickValues(0);

			if (yTickFormat)
				yAxis.tickFormat(yTickFormat);

			// Select the svg element, if it exists.
			var svg = d3.select(this).selectAll("svg").data([data]);
			// Otherwise, create the skeletal chart.
			var gEnter = svg.enter().append("svg").append("g");

			gEnter.append("g").attr("class", "x axis " + axisStyle);
			gEnter.append("g").attr("class", "y axis " + axisStyle);

			// Update the outer dimensions.
			svg.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom);

			// Update the inner dimensions.
			var g = svg.select("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			var bars = g.selectAll(".bar")
				.data(data)
				.enter().append("rect")
				.attr("class", "bar " + barStyle)
				.attr("x", function (d) {
					return xScale(d.x);
				})
				.attr("width", xScale.rangeBand())
				.attr("y", function (d) {
					return yScale(Math.max(0, d.y));
				})
				.attr("height", function (d) {
					return Math.abs(yScale(d.y) - yScale(0));
				})
				.style("shape-rendering", "crispEdges")
				.style("fill", color);

			// Update the x-axis.
			var xAxisElement = g.select(".x.axis")
				.attr("transform", "translate(0," + (height) + ")")
				.call(xAxis);

			if (xLabel) {
				xAxisElement.append("text")
					.attr("transform", "translate(" + (width) / 2 + ", 0)")
					.attr("dy", "3em")
					.style("text-anchor", "middle")
					.text(xLabel);
			}

			var yAxisElement = svg.select(".y.axis")
				.call(yAxis);

			if (yLabel) {
				yAxisElement.append("text")
					.attr("transform", "rotate(-90)")
					.attr("y", 6)
					.attr("dy", ".71em")
					.style("text-anchor", "end")
					.text(yLabel);
			}


			if (d3.tip) {
				tip = d3.tip()
					.attr('class', 'gm8-d3js-tooltip ' + tooltipStyle)
					.offset([-10, 0])
					.html(
						function (d) {
							return tooltip(d.orig);
						});

				gEnter.call(tip);

				// Add the tooltip
				bars.on('mouseover', tip.show)
					.on('mouseout', tip.hide)
			}

			sort = function change(byValue) {
				// Copy-on-write since tweens are evaluated after a delay.
				var x0 = xScale.domain(data.sort(byValue ? function (a, b) {
							return b.y - a.y;
						} : function (a, b) {
							return d3.ascending(a.x, b.x);
						})
						.map(function (d) {
							return d.x;
						}))
					.copy();

				svg.selectAll(".bar")
					.sort(function (a, b) {
						return x0(a.x) - x0(b.x);
					});

				var transition = svg.transition().duration(750),
					delay = function (d, i) {
						return i * 1000 / data.length;
					};

				transition.selectAll(".bar")
					.delay(delay)
					.attr("x", function (d) {
						return x0(d.x);
					});

				transition.select(".x.axis")
					.call(xAxis)
					.selectAll("g")
					.delay(delay);
			}
		});
	}

	chart.margin = function (_) {
		if (!arguments.length) return margin;
		margin = _;
		return chart;
	};

	chart.width = function (_) {
		if (!arguments.length) return width;
		width = _;
		return chart;
	};

	chart.height = function (_) {
		if (!arguments.length) return height;
		height = _;
		return chart;
	};

	chart.x = function (_) {
		if (!arguments.length) return xValue;
		xValue = _;
		return chart;
	};

	chart.y = function (_) {
		if (!arguments.length) return yValue;
		yValue = _;
		return chart;
	};

	chart.tooltip = function (_) {
		if (!arguments.length) return tooltip;
		tooltip = _;
		return chart;
	};

	chart.xLabel = function (_) {
		if (!arguments.length) return xLabel;
		xLabel = _;
		return chart;
	};

	chart.yLabel = function (_) {
		if (!arguments.length) return yLabel;
		yLabel = _;
		return chart;
	};

	chart.axisStyle = function (_) {
		if (!arguments.length) return axisStyle;
		axisStyle = _;
		return chart;
	};

	chart.barStyle = function (_) {
		if (!arguments.length) return barStyle;
		barStyle = _;
		return chart;
	};

	chart.tooltipStyle = function (_) {
		if (!arguments.length) return tooltipStyle;
		tooltipStyle = _;
		return chart;
	};

	chart.color = function (_) {
		if (!arguments.length) return color;
		color = _;
		return chart;
	};

	chart.xAxisStart = function (_) {
		if (!arguments.length) return xAxisStart;
		xAxisStart = _;
		return chart;
	};

	chart.removeXAxisTicks = function (_) {
		if (!arguments.length) return removeXAxisTicks;
		removeXAxisTicks = _;
		return chart;
	};

	chart.yTickFormat = function (_) {
		if (!arguments.length) return yTickFormat;
		yTickFormat = _;
		return chart;
	};

	chart.outerPadding = function (_) {
		if (!arguments.length) return outerPadding;
		outerPadding = _;
		return chart;
	};

	chart.bindSort = function (inputElement) {
		if (inputElement) {
			d3.select(inputElement).on("change", function () {
				sort(inputElement.checked);
			});
		}
		return chart;
	};

	return chart;
}