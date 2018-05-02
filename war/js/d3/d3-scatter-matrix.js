/*
 *  Copyright 2018 Information and Computational Sciences,
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

function scatterMatrix() {
	var margin = {
			top: 20,
			right: 30,
			bottom: 30,
			left: 50
		},
		width = 960,
		height = width,
		size = 1,
		padding = 15,
		xScale = d3.scale.linear(),
		yScale = d3.scale.linear(),
		tooltip = function (d) {
			return null;
		},
		xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(4),
		yAxis = d3.svg.axis().scale(yScale).orient("left").ticks(4),
		axisStyle = "",
		dotStyle = "",
		frameStyle = "",
		hiddenStyle = hiddenStyle,
		tooltipStyle = "",
		legendItemStyle = "",
		xTickFormat = d3.format("s"),
		yTickFormat = d3.format("s"),
		tip,
		showLegend = false,
		legendWidth = 50,
		color = d3.scale.category10(),
		colorKey = function (d) {
			return d.xValue;
		},
		idColumn = "",
		ignoreColumns = [];

	function chart(selection) {
		selection.each(function (data) {

			// Do we need to make room for a legend?
			margin.right += showLegend ? legendWidth : 0;

			height = width - margin.bottom - margin.top - (showLegend ? legendWidth : 0);
			width = width - margin.left - margin.right;

			var domainByTrait = {},
				traits = d3.keys(data[0]).filter(function (d) {
					return d !== idColumn && $.inArray(d, ignoreColumns) == -1;
				}),
				n = traits.length,
				dots;

			traits.forEach(function (trait) {
				// Make sure we convert strings to numbers
				data.forEach(function (point) {
					point[trait] = parseFloat(point[trait]);
					point.colorKey = colorKey(point);
					point.color = color(point.colorKey);
				});

				domainByTrait[trait] = d3.extent(data, function (d) {
					return parseFloat(d[trait]);
				});
			});

			size = width / n;

			if (xTickFormat)
				xAxis.tickFormat(xTickFormat);
			if (yTickFormat)
				yAxis.tickFormat(yTickFormat);

			xScale.range([padding / 2, size - padding / 2]);
			yScale.range([size - padding / 2, padding / 2]);

			xAxis.tickSize(size * n);
			yAxis.tickSize(-size * n);

			var brush = d3.svg.brush()
				.x(xScale)
				.y(yScale)
				.on("brushstart", brushstart)
				.on("brush", brushmove)
				.on("brushend", brushend);

			// Select the svg element, if it exists.
			var svg = d3.select(this).selectAll("svg").data([data]);
			// Otherwise, create the skeletal chart.
			var gEnter = svg.enter().append("svg").append("g");

			// Update the outer dimensions.
			svg.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom);

			// Update the inner dimensions.
			var g = svg.select("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			// Update the x-axis.
			g.selectAll(".x.axis")
				.data(traits)
				.enter()
				.append("g")
				.attr("class", "x axis " + axisStyle)
				.attr("transform", function (d, i) {
					return "translate(" + (n - i - 1) * size + ",0)";
				})
				.each(function (d) {
					xScale.domain(domainByTrait[d]);
					d3.select(this).call(xAxis);
				});

			g.selectAll(".y.axis")
				.data(traits)
				.enter().append("g")
				.attr("class", "y axis " + axisStyle)
				.attr("transform", function (d, i) {
					return "translate(0," + i * size + ")";
				})
				.each(function (d) {
					yScale.domain(domainByTrait[d]);
					d3.select(this).call(yAxis);
				});

			var cell = g.selectAll(".cell")
				.data(cross(traits, traits))
				.enter().append("g")
				.attr("class", "cell")
				.attr("transform", function (d) {
					return "translate(" + (n - d.i - 1) * size + "," + d.j * size + ")";
				})
				.each(plot);

			// Titles for the diagonal.
			cell.filter(function (d) {
				return d.i === d.j;
			}).append("text")
				.attr("x", padding)
				.attr("y", padding)
				.attr("dy", ".71em")
				.text(function (d) {
					return d.x;
				});

			cell.call(brush);

			// Check if a legend is required
			if (showLegend) {
				d3.legend(this, svg, color, margin, width, height, legendWidth, legendItemStyle, true);
			}

			function plot(p) {
				var cell = d3.select(this);

				xScale.domain(domainByTrait[p.x]);
				yScale.domain(domainByTrait[p.y]);

				cell.append("rect")
					.attr("class", "frame " + frameStyle)
					.attr("x", padding / 2)
					.attr("y", padding / 2)
					.attr("width", size - padding)
					.attr("height", size - padding);

				dots = cell.selectAll("circle")
					.data(data)
					.enter().append("circle")
					// Don't show items where either of the dimensions is NaN
					.filter(function (d) {
						return !isNaN(d[p.x]) && !isNaN(d[p.y]);
					})
					.attr("class", function (d) {
						if (d.colorKey)
							return "dot item " + dotStyle + " item-" + d3.makeSafeForCSS(d.colorKey);
						else
							return "dot item " + dotStyle;
					})
					.attr("cx", function (d) {
						return xScale(d[p.x]);
					})
					.attr("cy", function (d) {
						return yScale(d[p.y]);
					})
					.attr("r", 3)
					.attr("id", function (d) {
						return "item-" + d[idColumn];
					})
					.style("fill", function (d) {
						return d.color;
					});
			}

			var brushCell;

			// Clear the previously-active brush, if any.
			function brushstart(p) {
				if (brushCell !== this) {
					d3.select(brushCell).call(brush.clear());
					xScale.domain(domainByTrait[p.x]);
					yScale.domain(domainByTrait[p.y]);
					brushCell = this;
				}
			}

			// Highlight the selected circles.
			function brushmove(p) {
				var e = brush.extent();

				// First, get the items in THIS cell that should be selected
				var selected = d3.select(brushCell).selectAll("circle").filter(function (d) {
					return !isNaN(d[p.x]) && !isNaN(d[p.y]) && e[0][0] <= d[p.x] && d[p.x] <= e[1][0] && e[0][1] <= d[p.y] && d[p.y] <= e[1][1];
				});

				// Then hide all circles
				svg.selectAll("circle").attr("class", "dot item " + dotStyle + " " + hiddenStyle);

				// And then select all items with the same id as a selected item and select them as well
				selected.each(function (d) {
					var temp = svg.selectAll("#item-" + d[idColumn]);
					temp.classed(hiddenStyle, false);
					temp.attr("class", "dot item " + dotStyle + " selected");
				});

				//svg.selectAll("circle").classed(hiddenStyle, function (d) {
				//    return e[0][0] > d[p.x] || d[p.x] > e[1][0] || e[0][1] > d[p.y] || d[p.y] > e[1][1];
				//});
				//// Mark the selected items so we can extract them easily on request
				//svg.selectAll("circle").classed("selected", function (d) {
				//    return e[0][0] <= d[p.x] && d[p.x] <= e[1][0] && e[0][1] <= d[p.y] && d[p.y] <= e[1][1];
				//});
			}

			// If the brush is empty, select all circles.
			function brushend() {
				if (brush.empty()) {
					svg.selectAll("." + hiddenStyle).classed(hiddenStyle, false);
					svg.selectAll(".selected").classed("selected", false);
				}
			}

			function cross(a, b) {
				var c = [],
					n = a.length,
					m = b.length,
					i, j;
				for (i = -1; ++i < n;)
					for (j = -1; ++j < m;) c.push({
						x: a[i],
						i: i,
						y: b[j],
						j: j
					});
				return c;
			}

			if (d3.tip) {
				tip = d3.tip()
					.attr('class', 'gm8-d3js-tooltip ' + tooltipStyle)
					.offset([-10, 0])
					.html(
						function (d) {
							return tooltip(d);
						});

				gEnter.call(tip);

				dots
					.on("mouseenter", function (d) {
						d3.select(this)
							.transition()
							.duration(100)
							.attr("r", 4.5);
						tip.show(d);
					})
					.on("mouseleave", function (d) {
						d3.select(this)
							.transition()
							.duration(200)
							.attr("r", 2.5);
						tip.hide();
					});
			}

		});
	}

	// The x-accessor for the path generator; xScale ∘ xValue.
	function X(d) {
		return xScale(d.date);
	}

	// The x-accessor for the path generator; yScale ∘ yValue.
	function Y(d) {
		return yScale(d.value);
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

	chart.padding = function (_) {
		if (!arguments.length) return padding;
		padding = _;
		return chart;
	};

	chart.size = function (_) {
		if (!arguments.length) return size;
		size = _;
		return chart;
	};

	chart.height = function (_) {
		if (!arguments.length) return height;
		height = _;
		return chart;
	};

	chart.tooltip = function (_) {
		if (!arguments.length) return tooltip;
		tooltip = _;
		return chart;
	};

	chart.color = function (_) {
		if (!arguments.length) return color;
		color = _;
		return chart;
	};

	chart.colorKey = function (_) {
		if (!arguments.length) return colorKey;
		colorKey = _;
		return chart;
	};

	chart.idColumn = function (_) {
		if (!arguments.length) return idColumn;
		idColumn = _;
		return chart;
	};

	chart.ignoreColumns = function (_) {
		if (!arguments.length) return ignoreColumns;
		ignoreColumns = _;
		return chart;
	};

	chart.axisStyle = function (_) {
		if (!arguments.length) return axisStyle;
		axisStyle = _;
		return chart;
	};

	chart.dotStyle = function (_) {
		if (!arguments.length) return dotStyle;
		dotStyle = _;
		return chart;
	};

	chart.hiddenStyle = function (_) {
		if (!arguments.length) return hiddenStyle;
		hiddenStyle = _;
		return chart;
	};

	chart.frameStyle = function (_) {
		if (!arguments.length) return frameStyle;
		frameStyle = _;
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

	chart.xTickFormat = function (_) {
		if (!arguments.length) return xTickFormat;
		xTickFormat = _;
		return chart;
	};

	chart.yTickFormat = function (_) {
		if (!arguments.length) return yTickFormat;
		yTickFormat = _;
		return chart;
	};

	return chart;
}