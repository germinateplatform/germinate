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

function multiLineChart() {
	var margin = {
			top: 20,
			right: 20,
			bottom: 30,
			left: 50
		},
		width = 960,
		height = 500,
		xScale = d3.scale.linear(),
		yScale = d3.scale.linear(),
		xValue = function (d) {
			return d[0];
		},
		yValue = function (d) {
			return parseFloat(d);
		},
		tooltip = function (d) {
			return null;
		},
		xAxis = d3.svg.axis(),
		yAxis = d3.svg.axis(),
		yLabel = null,
		xLabel = null,
		axisStyle = "",
		tooltipStyle = "",
		legendItemStyle = "",
		lineStyle = "",
		dotStyle = "",
		xAxisStart = 0,
		xTickFormat = null,
		yTickFormat = d3.format(".2s"),
		tip,
		color = d3.scale.category10(),
		line = d3.svg.line()
		.defined(function (d) {
			return !isNaN(d.y);
		})
		.x(X)
		.y(Y),
		interpolate = "cardinal",
		minimum = null,
		ignoreIndices = [0],
		showLegend = false,
		sortLegend = false,
		xTicks = null,
		xTicksValue = null,
		legendWidth = 50;

	function chart(selection) {
		selection.each(function (data) {

			// Do we need to make room for a legend?
			margin.right += showLegend ? legendWidth : 0;

			height = height - margin.bottom - margin.top;
			width = width - margin.left - margin.right;

			// Get the individual series
			console.log(data);
			color.domain(d3.keys(data[0]).filter(function (d, i) {
				return ignoreIndices.indexOf(i) === -1;
			}));

			// Set the interpolation
			line.interpolate(interpolate);

			// Apply the scales to the axes
			xAxis.scale(xScale).orient("bottom");

			if (xTicks && xTicksValue)
				xAxis.ticks(xTicks, xTicksValue);

			if (xTickFormat)
				xAxis.tickFormat(xTickFormat);

			yAxis.scale(yScale).orient("left");

			if (yTickFormat)
				yAxis.tickFormat(yTickFormat);

			// Extract the data
			var lineData = color.domain().map(function (name) {
				return {
					name: name,
					values: data.map(function (d) {
						d[name] = yValue(d[name]);
						return {
							x: xValue(d),
							y: d[name]
						};
					})
				};
			});

			// Set scale domains and ranges
			xScale.domain(d3.extent(data, function (d) {
				return xValue(d);
			})).range([xAxisStart, width]);

			yScale.domain([
				(typeof minimum === 'undefined') ? minimum : d3.min(lineData, function (c) {
					return d3.min(c.values, function (v) {
						return v.y
					})
				}),
                d3.max(lineData, function (c) {
					return d3.max(c.values, function (v) {
						return v.y;
					});
				})
            ]).range([height, 0]);

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

			var lines = g.selectAll(".line")
				.data(lineData)
				.enter().append("g")
				.attr("class", "line");

			// Update the line path.
			lines.append("path")
				.attr("class", function (d) {
					return "line item item-" + d3.makeSafeForCSS(d.name) + " " + lineStyle;
				})
				.attr("d", function (d, i) {
					return line(d.values);
				})
				.attr("id", function (d, i) {
					return "item-" + d3.makeSafeForCSS(d.name);
				})
				.style("stroke", function (d) {
					return color(d.name);
				});


			// Update the dots
			g.selectAll("dot")
				.data(lineData)
				.enter().append("g")
				.attr("class", "dots")
				.selectAll("circle")
				.data(function (d) {
					return d.values.filter(function (e, i) {
						// Store the name in the object as well (used to get the color)
						e.name = d.name;
						if (isNaN(e.y))
							return false;

						var before = d.values[i - 1];
						var after = d.values[i + 1];

						if (before === undefined && after === undefined) {
							return true;
						} else if (before === undefined) {
							if (isNaN(after.y))
								return true;
						} else if (after === undefined) {
							if (isNaN(before.y))
								return true;
						} else return isNaN(before.y) && isNaN(after.y);
					});
				})
				.enter().append("circle")
				.attr("class", "dot " + dotStyle)
				.attr("r", 3.5)
				.attr("cx", function (d) {
					return xScale(d.x);
				})
				.attr("cy", function (d) {
					return yScale(d.y);
				})
				.style("fill", function (d) {
					return color(d.name);
				});

			// Update the x-axis.
			var xAxisElement = g.select(".x.axis")
				.attr("transform", "translate(0," + yScale.range()[0] + ")")
				.call(xAxis);

			if (xLabel) {
				xAxisElement.append("text")
					.attr("x", width)
					.attr("dy", "-.71em")
					.style("text-anchor", "end")
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

			// Check if d3-tip is loaded, if so, attach the tooltip code
			if (d3.tip) {
				tip = d3.tip()
					.attr('class', 'gm8-d3js-tooltip ' + tooltipStyle)
					.offset([-10, 0])
					.html(
						function (d) {
							return tooltip(d);
						});

				gEnter.call(tip);

				// Add an overlay that will catch mouse events to show the tooltip
				gEnter.append("rect").attr("width", width)
					.attr("height", height)
					.style("fill", "none")
					.style("pointer-events", "all")
					.attr("transform", "translate(0, 0)")
					.on("mouseleave", function () {
						tip.hide();
					})
					.on("mousemove", mousemove);
			}

			// Check if a legend is required
			if (showLegend) {
				d3.legend(this, svg, color, margin, width, height, legendWidth, legendItemStyle, false);
			}

			// Positions and fills the tooltip
			function mousemove() {
				// Gather some necessary data
				var x0 = xScale.invert(d3.mouse(this)[0]);
				var y0 = yScale.invert(d3.mouse(this)[1]);

				var bisectDate = d3.bisector(function (d) {
					return xValue(d);
				}).left;

				var i = bisectDate(data, x0, 1);

				var d0 = data[i - 1];
				var d1 = data[i];
				var d;

				if (!d0)
					d = d1;
				else if (!d1)
					d = d0;
				else
					d = x0 - xValue(d0) > xValue(d1) - x0 ? d1 : d0;

				if (!d || (d3.mouse(this)[0] > width)) {
					tip.hide();
					return;
				}

				var minDist = Number.MAX_VALUE;
				var minItem = null;

				for (var j = 0; j < color.domain().length; j++) {
					var value = Math.abs(y0 - d[color.domain()[j]]);
					if (value <= minDist) {
						minDist = value;
						minItem = color.domain()[j];
					}
				}

				// Determine the closest point
				var xVal = xScale(xValue(d));
				var yVal = yScale(d[minItem]);

				if ((typeof xVal !== 'undefined') && (typeof yVal !== 'undefined') && (minItem !== null)) {
					var xPos = xVal - width / 2;
					var yPos = yVal - 10;

					tip.offset([yPos, xPos]);
					tip.show({
						data: d,
						key: minItem
					}, svg);

				} else {
					// Hide the tooltip
					tip.hide();
				}
			}
		});
	}

	// The x-accessor for the path generator; xScale ∘ xValue.
	function X(d) {
		return xScale(d.x);
	}

	// The x-accessor for the path generator; yScale ∘ yValue.
	function Y(d) {
		return yScale(d.y);
	}

	chart.margin = function (_) {
		if (!arguments.length) return margin;
		margin = Object.create(_);
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

	chart.xScale = function (_) {
		if (!arguments.length) return xScale;
		xScale = _;
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

	chart.xAxisStart = function (_) {
		if (!arguments.length) return xAxisStart;
		xAxisStart = _;
		return chart;
	};

	chart.color = function (_) {
		if (!arguments.length) return color;
		color = _;
		return chart;
	};

	chart.interpolate = function (_) {
		if (!arguments.length) return interpolate;
		interpolate = _;
		return chart;
	};

	chart.ignoreIndices = function (_) {
		if (!arguments.length) return ignoreIndices;
		ignoreIndices = _;
		return chart;
	};

	chart.minimum = function (_) {
		if (!arguments.length) return minimum;
		minimum = _;
		return chart;
	};

	chart.showLegend = function (_) {
		if (!arguments.length) return showLegend;
		showLegend = _;
		return chart;
	};

	chart.legendWidth = function (_) {
		if (!arguments.length) return legendWidth;
		legendWidth = _;
		return chart;
	};

	chart.axisStyle = function (_) {
		if (!arguments.length) return axisStyle;
		axisStyle = _;
		return chart;
	};

	chart.tooltipStyle = function (_) {
		if (!arguments.length) return tooltipStyle;
		tooltipStyle = _;
		return chart;
	};

	chart.legendItemStyle = function (_) {
		if (!arguments.length) return legendItemStyle;
		legendItemStyle = _;
		return chart;
	};

	chart.lineStyle = function (_) {
		if (!arguments.length) return lineStyle;
		lineStyle = _;
		return chart;
	};

	chart.dotStyle = function (_) {
		if (!arguments.length) return dotStyle;
		dotStyle = _;
		return chart;
	};

	chart.xTickFormat = function (_) {
		if (!arguments.length) return xTickFormat;
		xTickFormat = _;
		return chart;
	};

	chart.xTicks = function (_) {
		if (!arguments.length) return xTicks;
		xTicks = _;
		return chart;
	};

	chart.xTicksValue = function (_) {
		if (!arguments.length) return xTicksValue;
		xTicksValue = _;
		return chart;
	};

	chart.yTickFormat = function (_) {
		if (!arguments.length) return yTickFormat;
		yTickFormat = _;
		return chart;
	};

	chart.sortLegend = function (_) {
		if (!arguments.length) return sortLegend;
		sortLegend = _;
		return chart;
	};

	return chart;
}