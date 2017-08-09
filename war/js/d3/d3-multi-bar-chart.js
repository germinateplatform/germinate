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

function multiBarChart() {
    var margin = {
            top: 20,
            right: 20,
            bottom: 40,
            left: 50
        },
        width = 960,
        height = 500,
        x0Scale = d3.scale.ordinal()
        .rangeRoundBands([0, width], .1, .3),
        x1Scale = d3.scale.ordinal(),
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
        xAxisLabel = function (d) {
            return d.name;
        },
        xAxis = d3.svg.axis(),
        yAxis = d3.svg.axis(),
        yLabel = null,
        xLabel = null,
        showLegend = null,
        legendWidth = null,
        sortLegend = false,
        color = d3.scale.category10(),
        tip,
        ignoreIndices = [0];

    function chart(selection) {
        selection.each(function (data) {

            // Do we need to make room for a legend?
            margin.right += showLegend ? legendWidth : 0;

            height = height - margin.bottom - margin.top;
            width = width - margin.left - margin.right;

            color.domain(d3.keys(data[0]).filter(function (d, i) {
                return ignoreIndices.indexOf(i) === -1;
            }));

            xAxis.scale(x0Scale).orient("bottom");

            yAxis.scale(yScale).orient("left");

            data.forEach(function (d) {
                d.categories = color.domain().map(function (name) {
                    d[name] = yValue(d[name]);
                    return {
                        "orig": d,
                        "name": name,
                        "y": yValue(d[name]),
                        "x": xValue(d)
                    };
                });
            });

            var sections = data.map(function (d) {
                return xValue(d);
            });

            // Update the x-scales
            x0Scale.domain(sections).rangeRoundBands([0, width], .1, .3);
            x1Scale.domain(color.domain()).rangeRoundBands([0, x0Scale.rangeBand()], .1);

            // Update the y-scale.
            yScale.domain([Math.min(0, d3.min(data, function (c) {
                return d3.min(c.categories, function (v) {
                    return v.y;
                });
            })), d3.max(data, function (c) {
                return d3.max(c.categories, function (v) {
                    return v.y;
                });
            })]).range([height, 0]);

            // Select the svg element, if it exists.
            var svg = d3.select(this).selectAll("svg").data([data]);
            // Otherwise, create the skeletal chart.
            var gEnter = svg.enter().append("svg").append("g");

            gEnter.append("g").attr("class", "x axis");
            gEnter.append("g").attr("class", "y axis");

            // Update the outer dimensions.
            svg.attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom);

            // Update the inner dimensions.
            var g = svg.select("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

            var categories = gEnter.selectAll(".category")
                .data(data)
                .enter().append("g")
                .attr("class", "category")
                .attr("transform", function (d) {
                    return "translate(" + x0Scale(xValue(d)) + ",0)";
                });

            var bars = categories.selectAll("rect")
                .data(function (d) {
                    return d.categories;
                })
                .enter().append("rect")
                .attr("class", function (d) {
                    return "bar item-" + d.name.replace(/ /g, '-');
                })
                .attr("x", function (d) {
                    return x1Scale(d.name);
                })
                .attr("width", x1Scale.rangeBand())
                .attr("y", function (d) {
                    return isNaN(d.y) ? yScale(0) : yScale(Math.max(0, d.y));
                })
                .attr("height", function (d) {
                    return isNaN(d.y) ? 0 : Math.abs(yScale(d.y) - yScale(0));
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
                    .attr("transform", "translate(" + width / 2 + ", 0)")
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

            // Check if a legend is required
            if (showLegend) {
                var legendWrapper = gEnter.append("g")
                    .attr("transform", "translate(" + (width) + ", 0)");

                // Add the legend wrapper
                var legend = legendWrapper.selectAll(".legend")
                    .data(function () {
                        if (sortLegend)
                            return color.domain().sort();
                        else
                            return color.domain();
                    })
                    .enter()
                    .append("g")
                    .attr("class", "legend")
                    .attr("transform", function (d, i) {
                        return "translate(0," + i * 20 + ")";
                    })
                    .on("mouseenter", function (d) {
                        svg.selectAll(".bar")
                            .transition()
                            .duration(100)
                            .style("opacity", .2);
                        svg.selectAll(".item-" + d.replace(/ /g, '-'))
                            .transition()
                            .duration(100)
                            .style("opacity", 1);
                    })
                    .on("mouseleave", function (d) {
                        svg.selectAll(".bar")
                            .transition()
                            .duration(250)
                            .style("opacity", 1);
                    });

                // Add the legend
                legend.append("rect")
                    .attr("x", 20)
                    .attr("width", 18)
                    .attr("height", 18)
                    .attr("stroke", "black")
                    .attr("shape-rendering", "crispedges")
                    .style("fill", color);

                // Add the legend text (line name)
                legend.append("text")
                    .attr("x", 42)
                    .attr("y", 9)
                    .attr("dy", ".35em")
                    .style("text-anchor", "beginning")
                    .style("cursor", "default")
                    .text(function (d) {
                        return d;
                    });
            }

            if (d3.tip) {
                tip = d3.tip()
                    .attr('class', 'gm8-d3js-tooltip')
                    .offset([-10, 0])
                    .html(
                        function (d) {
                            return tooltip({
                                key: d.name,
                                data: d.orig
                            });
                        });

                gEnter.call(tip);

                // Add the tooltip
                bars.on('mouseover', tip.show)
                    .on('mouseout', tip.hide)
            }
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

    chart.ignoreIndices = function (_) {
        if (!arguments.length) return ignoreIndices;
        ignoreIndices = _;
        return chart;
    };

    chart.color = function (_) {
        if (!arguments.length) return color;
        color = _;
        return chart;
    };

    chart.sortLegend = function (_) {
        if (!arguments.length) return sortLegend;
        sortLegend = _;
        return chart;
    };

    return chart;
}