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

function pedigreeChart() {
	var margin = {
			top: 20,
			right: 20,
			bottom: 30,
			left: 50
		},
		width = 960,
		height = 500,
		tooltip = function (d) {
			return null;
		},
		onClick = function (d) {
			// do nothing
		},
		zoom,
		tooltipStyle = "",
		nodeStyle = "",
		edgeStyle = "",
		nodeShape = "rect",
		tip,
		connections = null,
		interpolate = "basis";

	function chart(selection) {
		selection.each(function (data) {

			height = height - margin.bottom - margin.top;
			width = width - margin.left - margin.right;

			// Create a new directed graph
			var graph = new dagreD3.graphlib.Graph().setGraph({});

			function substring(str, len) {
				len = len || 15;

				if (!str || str.length <= len) {
					return str;
				} else {
					var regex = RegExp(".{" + len + "}\\w*", "g");
					return str.match(regex)[0] + "...";
				}
			}

			// Automatically label each of the nodes
			data.forEach(function (state) {
				graph.setNode(state.label, {label: substring(state.label), object: state, class: nodeStyle, shape: nodeShape});
			});

			if (connections !== null) {
				connections.forEach(function (edge) {
					graph.setEdge(edge.from, edge.to, {
						class: edgeStyle + " " + edge.style,
						lineInterpolate: interpolate
					});
				});
			}

			// Select the svg element, if it exists.
			var svg = d3.select(this).selectAll("svg").data([data]);
			// Otherwise, create the skeletal chart.
			var gEnter = svg.enter().append("svg").append("g");
			var inner = gEnter.append("g");

			// Update the outer dimensions.
			svg.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom);

			// Update the inner dimensions.
			var g = svg.select("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			// Set up zoom support
			zoom = d3.behavior.zoom()
				.scaleExtent([0.5, 3])
				.on("zoom", function () {
					inner.attr("transform", "translate(" + d3.event.translate + ")" + "scale(" + d3.event.scale + ")");
				});

			// Handle zooming only when the SVG has focus, otherwise don't zoom.
			svg.on("focus", function (e) {
				svg.call(zoom);
			});

			svg.on("blur", function (e) {
				svg.on('.zoom', null);
			});

			// Create the renderer
			var render = new dagreD3.render();

			// Run the renderer. This is what draws the final graph.
			render(inner, graph);

			svg.selectAll("g.node").on("click", onClick);

			if (d3.tip) {
				tip = d3.tip()
					.attr('class', 'gm8-d3js-tooltip ' + tooltipStyle)
					.offset([-10, 0])
					.html(function (d) {
						return tooltip(d);
					});

				g.call(tip);

				svg.selectAll("g.node")
					.on("mouseover", function (d) {
						tip.show(graph.node(d));
					})
					.on("mouseout", function (d) {
						tip.hide();
					});
			}

			// Center the graph
			var initialScale = 0.9;
			zoom
				.translate([(svg.attr("width") - graph.graph().width * initialScale) / 2, (svg.attr("height") - graph.graph().height) / 2])
				.scale(initialScale)
				.event(svg);
		});
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

	chart.connections = function (_) {
		if (!arguments.length) return connections;
		connections = Object.create(_);
		return chart;
	};

	chart.tooltip = function (_) {
		if (!arguments.length) return tooltip;
		tooltip = _;
		return chart;
	};

	chart.onClick = function (_) {
		if (!arguments.length) return onClick;
		onClick = _;
		return chart;
	};

	chart.interpolate = function (_) {
		if (!arguments.length) return interpolate;
		interpolate = _;
		return chart;
	};

	chart.nodeStyle = function (_) {
		if (!arguments.length) return nodeStyle;
		nodeStyle = _;
		return chart;
	};

	chart.edgeStyle = function (_) {
		if (!arguments.length) return edgeStyle;
		edgeStyle = _;
		return chart;
	};

	chart.nodeShape = function (_) {
		if (!arguments.length) return nodeShape;
		nodeShape = _;
		return chart;
	};

	chart.tooltipStyle = function (_) {
		if (!arguments.length) return tooltipStyle;
		tooltipStyle = _;
		return chart;
	};

	return chart;
}