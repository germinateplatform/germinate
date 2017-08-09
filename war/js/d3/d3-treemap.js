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

function treemap() {
	var margin = {
			top: 24,
			right: 0,
			bottom: 0,
			left: 0
		},
		width = 960,
		height = 500,
		size = function (d) {
			return d.value;
		},
		displayName = function (d) {
			return d.key;
		},
		tooltip = function (d) {
			return null;
		},
		tip,
		color = d3.scale.category20b(),
		grandparentStyle = "grandparent",
		parentStyle = "parent",
		childrenStyle = "children",
		childStyle = "child",
		rectStyle = "rect",
		textStyle = "text",
		tooltipStyle = "",
		onClick = function (d) {
			// do nothing
		},
		overallHeaderName = "TEST";

	function chart(selection) {
		selection.each(function (input) {

			var data = d3.nest()
				.key(function (d) {
					return d.country;
				})
				.key(function (d) {
					return d.site;
				})
				.entries(input);

			var root,
				formatNumber = d3.format(",d"),
				rname = overallHeaderName,
				theight = 36 + 16;

			var svg = d3.select(this).selectAll("svg").data([data]);

			width = width - margin.left - margin.right;
			height = height - margin.top - margin.bottom - theight;
			var transitioning;

			var x = d3.scale.linear()
				.domain([0, width])
				.range([0, width]);

			var y = d3.scale.linear()
				.domain([0, height])
				.range([0, height]);

			var treemap = d3.layout.treemap()
				.children(function (d, depth) {
					return depth ? null : d._children;
				})
				.sort(function (a, b) {
					return a.value - b.value;
				})
				.ratio(height / width * 0.5 * (1 + Math.sqrt(5)))
				.round(false);

			svg.enter().append("svg");

			var gEnter = svg.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.bottom + margin.top)
				.style("margin-left", -margin.left + "px")
				.style("margin.right", -margin.right + "px")
				.append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
				.style("shape-rendering", "crispEdges");

			var grandparent = gEnter.append("g")
				.attr("class", grandparentStyle);

			grandparent.append("rect")
				.attr("class", rectStyle)
				.attr("y", -margin.top)
				.attr("width", width)
				.attr("height", margin.top);

			grandparent.append("text")
				.attr("class", textStyle)
				.attr("x", 6)
				.attr("y", 6 - margin.top)
				.attr("dy", ".75em");

			root = {
				key: rname,
				values: data
			};

			initialize(root);
			accumulate(root);
			layout(root);
			display(root);

			if (window.parent !== window) {
				var myheight = document.documentElement.scrollHeight || document.body.scrollHeight;
				window.parent.postMessage({
					height: myheight
				}, '*');
			}

			function initialize(root) {
				root.x = root.y = 0;
				root.dx = width;
				root.dy = height;
				root.depth = 0;
			}

			// Aggregate the values for internal nodes. This is normally done by the
			// treemap layout, but not here because of our custom implementation.
			// We also take a snapshot of the original children (_children) to avoid
			// the children being overwritten when when layout is computed.
			function accumulate(d) {
				return (d._children = d.values) ? d.value = d.values.reduce(function (p, v) {
					return p + accumulate(v);
				}, 0) : d.value;
			}

			// Compute the treemap layout recursively such that each group of siblings
			// uses the same size (1×1) rather than the dimensions of the parent cell.
			// This optimizes the layout for the current zoom state. Note that a wrapper
			// object is created for the parent node for each group of siblings so that
			// the parent’s dimensions are not discarded as we recurse. Since each group
			// of sibling was laid out in 1×1, we must rescale to fit using absolute
			// coordinates. This lets us use a viewport to zoom.
			function layout(d) {
				if (d._children) {
					treemap.nodes({
						_children: d._children
					});
					d._children.forEach(function (c) {
						c.x = d.x + c.x * d.dx;
						c.y = d.y + c.y * d.dy;
						c.dx *= d.dx;
						c.dy *= d.dy;
						c.parent = d;
						layout(c);
					});
				}
			}

			function display(d) {
				grandparent
					.datum(d.parent)
					.on("click", transition)
					.select("text")
					.text(name(d));

				var g1 = gEnter.insert("g", "." + grandparentStyle)
					.datum(d)
					.attr("class", "depth");

				var g = g1.selectAll("g")
					.data(d._children)
					.enter().append("g");

				g.filter(function (d) {
						return d._children;
					})
					.classed(childrenStyle, true)
					.on("click", transition);

				var children = g.selectAll("." + childStyle)
					.data(function (d) {
						return d._children || [d];
					})
					.enter().append("g");

				children.append("rect")
					.attr("class", rectStyle + " " + childStyle)
					.call(rect);

				var rects = g.append("rect")
					.attr("class", rectStyle + " " + parentStyle)
					.call(rect);

				if (d3.tip) {

					d3.select(".gm8-d3js-tooltip-treemap").remove();

					var array = [].concat.apply([], rects);

					var tip = d3.tip()
						.attr('class', 'gm8-d3js-tooltip gm8-d3js-tooltip-treemap ' + tooltipStyle)
						.offset([-10, 0])
						.html(
							function (d) {
								return tooltip(d);
							});

					gEnter.call(tip);

					// Add the tooltip
					d3.selectAll(array)
						.on('mouseover', tip.show)
						.on('mouseout', tip.hide)
				}

				var t = g.append("text")
					.attr("class", "ptext " + textStyle)
					.attr("dy", ".75em")

				t.append("tspan")
					.text(function (d) {
						return d.key;
					});
				t.append("tspan")
					.attr("dy", "1.0em")
					.text(function (d) {
						return formatNumber(d.value);
					});
				t.call(text);

				g.selectAll("rect")
					.style("fill", function (d) {
						return color(d.key);
					});

				function transition(d) {
					if (transitioning || !d) return;

					// If this is a leaf node, then invoke the onClick method
					if (d.parent !== undefined && d.parent.depth > 0) {
						onClick(d);
						return;
					}

					transitioning = true;

					var g2 = display(d),
						t1 = g1.transition().duration(750),
						t2 = g2.transition().duration(750);

					// Update the domain only after entering new elements.
					x.domain([d.x, d.x + d.dx]);
					y.domain([d.y, d.y + d.dy]);

					// Enable anti-aliasing during the transition.
					g.style("shape-rendering", null);

					// Draw child nodes on top of parent nodes.
					g.selectAll(".depth").sort(function (a, b) {
						return a.depth - b.depth;
					});

					// Fade-in entering text.
					g2.selectAll("text").style("fill-opacity", 0);

					// Transition to the new view.
					t1.selectAll(".ptext").call(text).style("fill-opacity", 0);
					t2.selectAll(".ptext").call(text).style("fill-opacity", 1);
					t1.selectAll("rect").call(rect);
					t2.selectAll("rect").call(rect);

					// Remove the old node when the transition is finished.
					t1.remove().each("end", function () {
						g.style("shape-rendering", "crispEdges");
						transitioning = false;
					});
				}

				return g;
			}

			function text(text) {
				text.selectAll("tspan")
					.attr("x", function (d) {
						return x(d.x) + 6;
					})
				text.attr("x", function (d) {
						return x(d.x) + 6;
					})
					.attr("y", function (d) {
						return y(d.y) + 6;
					})
					.style("opacity", function (d) {
						return ((this.getBBox().width + 6 < x(d.x + d.dx) - x(d.x)) && (this.getBBox().height + 6 < y(d.y + d.dy) - y(d.y))) ? 1 : 0;
					});
			}

			function rect(rect) {
				rect.attr("x", function (d) {
						return x(d.x);
					})
					.attr("y", function (d) {
						return y(d.y);
					})
					.attr("width", function (d) {
						return x(d.x + d.dx) - x(d.x);
					})
					.attr("height", function (d) {
						return y(d.y + d.dy) - y(d.y);
					});
			}

			function name(d) {
				return d.parent ? name(d.parent) + " / " + d.key + " (" + formatNumber(d.value) + ")" : d.key + " (" + formatNumber(d.value) + ")";
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

	chart.size = function (_) {
		if (!arguments.length) return size;
		size = _;
		return chart;
	};

	chart.displayName = function (_) {
		if (!arguments.length) return displayName;
		displayName = _;
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

	chart.onClick = function (_) {
		if (!arguments.length) return onClick;
		onClick = _;
		return chart;
	};

	chart.overallHeaderName = function (_) {
		if (!arguments.length) return overallHeaderName;
		overallHeaderName = _;
		return chart;
	};

	chart.grandparentStyle = function (_) {
		if (!arguments.length) return grandparentStyle;
		grandparentStyle = _;
		return chart;
	};

	chart.parentStyle = function (_) {
		if (!arguments.length) return parentStyle;
		parentStyle = _;
		return chart;
	};

	chart.childrenStyle = function (_) {
		if (!arguments.length) return childrenStyle;
		childrenStyle = _;
		return chart;
	};

	chart.childStyle = function (_) {
		if (!arguments.length) return childStyle;
		childStyle = _;
		return chart;
	};

	chart.textStyle = function (_) {
		if (!arguments.length) return textStyle;
		textStyle = _;
		return chart;
	};

	chart.rectStyle = function (_) {
		if (!arguments.length) return rectStyle;
		rectStyle = _;
		return chart;
	};

	chart.tooltipStyle = function (_) {
		if (!arguments.length) return tooltipStyle;
		tooltipStyle = _;
		return chart;
	};

	return chart;
}
