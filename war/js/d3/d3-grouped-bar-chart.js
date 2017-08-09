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

function groupedBarChart() {
    var margin = {
            top: 20,
            right: 20,
            bottom: 30,
            left: 50
        },
        width = 960,
        height = 500,
        xScale = d3.scale.ordinal(),
        x2Scale = d3.scale.ordinal(),
        yScale = d3.scale.linear(),
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
        barStyle = "",
        yTickFormat = d3.format(".2s"),
        color = d3.scale.category10(),
        rowIdentifier = "ExperimentType",
        showLegend = false,
        sortLegend = false,
        legendWidth = 50;

    function chart(selection) {
        selection.each(function (data) {

            // Do we need to make room for a legend?
            margin.right += showLegend ? legendWidth : 0;

            height = height - margin.bottom - margin.top;
            width = width - margin.left - margin.right;

            var rows = d3.keys(data[0]).filter(function (key) {
                return key !== rowIdentifier;
            });

            data.forEach(function (d) {
                d.row = rows.map(function (name) {
                    return {
                        name: name,
                        value: +d[name]
                    };
                });
            });

            // Apply the scales to the axes
            xAxis.scale(xScale).orient("bottom");

            yAxis.scale(yScale).orient("left");

            if (yTickFormat)
                yAxis.tickFormat(yTickFormat);

            xScale.rangeRoundBands([0, width], .1)
            yScale.range([height, 0]);

            // Set scale domains and ranges
            xScale.domain(data.map(function (d) {
                return d[rowIdentifier];
            }));
            x2Scale.domain(rows).rangeRoundBands([0, xScale.rangeBand()]);
            yScale.domain([0, d3.max(data, function (d) {
                return d3.max(d.row, function (d) {
                    return d.value;
                });
            })]);

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

            var categories = g.selectAll(".category")
                .data(data)
                .enter().append("g")
                .attr("class", "category")
                .attr("transform", function (d) {
                    return "translate(" + xScale(d[rowIdentifier]) + ",0)";
                });

            var rects = categories.selectAll("rect")
                .data(function (d) {
                    return d.row;
                })
                .enter().append("rect")
                .attr("class", function (d) {
                    return "bar item item-" + d3.makeSafeForCSS(d.name);
                })
                .attr("width", x2Scale.rangeBand())
                .attr("x", function (d) {
                    return x2Scale(d.name);
                })
                .attr("y", function (d) {
                    return yScale(d.value);
                })
                .attr("height", function (d) {
                    return height - yScale(d.value);
                })
                .style("fill", function (d) {
                    return color(d.name);
                });

            // Update the x-axis.
            var xAxisElement = gEnter.select(".x.axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);

            if (xLabel) {
                xAxisElement.append("text")
                    .attr("x", width)
                    .attr("dy", "-.71em")
                    .style("text-anchor", "end")
                    .text(xLabel);
            }

            var yAxisElement = gEnter.select(".y.axis")
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
                d3.legend(this, svg, color, margin, width, height, legendWidth, legendItemStyle, true);
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

                rects.on('mouseover', tip.show)
                    .on('mouseout', tip.hide);
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

    chart.color = function (_) {
        if (!arguments.length) return color;
        color = _;
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

    chart.barStyle = function (_) {
        if (!arguments.length) return barStyle;
        barStyle = _;
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

    chart.rowIdentifier = function (_) {
        if (!arguments.length) return rowIdentifier;
        rowIdentifier = _;
        return chart;
    };

    return chart;
}