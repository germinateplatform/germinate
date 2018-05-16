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

function barChartFakeX() {
    var margin = {
            top: 20,
            right: 20,
            bottom: 40,
            left: 50
        },
        width = 960,
        height = 500,
        xScale = d3.scale.ordinal(),
        fakeXScale = d3.scale.linear(),
        yScale = d3.scale.linear(),
        xValue = function (d) {
            return d[0];
        },
        yValue = function (d) {
            return d[1];
        },
        tooltip = function (d) {
            return null;
        },
        onClick = function (d) {
            // do nothing
        },
        xAxis = d3.svg.axis().scale(xScale).orient("bottom"),
        fakeXAxis = d3.svg.axis(fakeXScale).orient("bottom").tickFormat(d3.format("d")),
        yAxis = d3.svg.axis().scale(yScale).orient("left"),
        yLabel = null,
        xLabel = null,
        tip,
        axisStyle = "",
        tooltipStyle = "",
        barStyle = "",
        minimum = 0,
        maximum = 1,
        color = "#0099cc",
        barPositionCallback = null;

    function chart(selection) {
        selection.each(function (data) {

            width = width - margin.left - margin.right;
            height = height - margin.top - margin.bottom;

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
            })).rangeBands([0, width], .1, 4);

            // Update the y-scale.
            yScale.domain([Math.min(0, d3.min(data, function (d) {
                return d.y;
            })), d3.max(data, function (d) {
                return d.y;
            })]).range([height, 0]);

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

            // Get the actual chart area. We need this to align the flapjack histogram images
            var chartArea = gEnter.append("rect")
                .attr("width", width + "px")
                .attr("height", height + "px")
                .style("fill", "transparent");

            var bars = g.selectAll(".bar")
                .data(data)
                .enter().append("rect")
                .attr("class", "bar " + barStyle)
                .attr("id", function (d, i) {
                    return "bar-" + i;
                })
                .attr("x", function (d) {
                    return xScale(d.x);
                })
                .attr("width", xScale.rangeBand())
                .attr("y", function (d) {
                    //                    return yScale(Math.max(0, d.y));
                    return height;
                })
                .attr("height", function (d) {
                    //                    return Math.abs(yScale(d.y) - yScale(0));
                    return 0;
                })
                .style("fill", function (d) {
                    return color;
                });

            var minValue = d3.min(data, function (d) {
                return d.y;
            });
            var maxValue = d3.max(data, function (d) {
                return d.y;
            });

            bars.transition()
                .delay(function (d) {
                    return ((d.y - minValue) / (maxValue - minValue)) * 1000;
                })
                .duration(1000)
                .attr("y", function (d) {
                    return (isNaN(d.y) ? 0 : yScale(d.y));
                })
                .attr("height", function (d) {
                    return height - (isNaN(d.y) ? height : yScale(d.y));
                });

            bars.on("click", onClick);

            var firstBar = d3.select("#bar-0")[0][0];
            var lastBar = d3.select("#bar-" + (data.length - 1))[0][0];
            var overallWidth = (lastBar.x.baseVal.value + lastBar.width.baseVal.value) - firstBar.x.baseVal.value;

            // Update the fake x-scale
            fakeXScale.domain([minimum, maximum]);
            fakeXAxis.tickValues([firstBar.x.baseVal.value, (lastBar.x.baseVal.value + lastBar.width.baseVal.value)])
                .tickFormat(function (d) {
                    return d === firstBar.x.baseVal.value ? minimum : maximum;
                });
            fakeXScale.range([firstBar.x.baseVal.value, (lastBar.x.baseVal.value + lastBar.width.baseVal.value)]);

            // Update the x-axis.
            var xAxisElement = g.select(".x.axis")
                .attr("transform", "translate(0," + yScale.range()[0] + ")")
                .call(fakeXAxis);

            if (xLabel) {
                xAxisElement.append("text")
                    .attr("transform", "translate(" + (width) / 2 + ", 0)")
                    .attr("dy", "2em")
                    .attr("text-anchor", "middle")
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
                            if (d instanceof Array)
                                return tooltip(d[d.length - 1].orig);
                            else
                                return tooltip(d.orig);
                        });

                gEnter.call(tip);

                // Add the tooltip
                bars.on('mouseover', tip.show)
                    .on('mouseout', tip.hide)
            }

            var x00 = fakeXScale(minimum);
            var x11 = fakeXScale(maximum);

            var firstSelection = g.append('rect')
                .attr('x', x00)
                .attr('y', 0)
                .attr('width', 0)
                .attr('height', height)
                .style('fill', 'black')
                .style('opacity', 0);

            var secondSelection = g.append('rect')
                .attr('x', x11)
                .attr('y', 0)
                .attr('width', 0)
                .attr('height', height)
                .style('fill', 'black')
                .style('opacity', 0);

            if (barPositionCallback) {
                barPositionCallback(firstBar.x.baseVal.value + chartArea[0][0].getBoundingClientRect().left, overallWidth);
            }

            function showHighlight(start, end) {
                var x01 = fakeXScale(start);
                var x10 = fakeXScale(end);

                var duration = 100;

                // Check width of both rectangles. If 0, then slow the animation down
                if (firstSelection.attr('width') == 0 && secondSelection.attr('width') == 0)
                    duration = 450;

                firstSelection
                    .transition()
                    .duration(duration)
                    .attr('width', x01 - x00)
                    .style('opacity', .5);

                secondSelection
                    .transition()
                    .duration(duration)
                    .attr('width', x11 - x10)
                    .attr('x', x10)
                    .style('opacity', .5)
            }

            function hideHighlight() {
                firstSelection
                    .transition()
                    .duration(450)
                    .attr('width', 0)
                    .style('opacity', 0);

                secondSelection
                    .transition()
                    .duration(450)
                    .attr('width', 0)
                    .attr('x', x11)
                    .style('opacity', 0)
            }

            barChartFakeX.showHighlight = showHighlight;

            barChartFakeX.hideHighlight = hideHighlight;
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

    chart.minimum = function (_) {
        if (!arguments.length) return minimum;
        minimum = _;
        return chart;
    };

    chart.maximum = function (_) {
        if (!arguments.length) return maximum;
        maximum = _;
        return chart;
    };

    chart.barPositionCallback = function (_) {
        if (!arguments.length) return barPositionCallback;
        barPositionCallback = _;
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

    chart.barStyle = function (_) {
        if (!arguments.length) return barStyle;
        barStyle = _;
        return chart;
    };

    chart.onClick = function (_) {
        if (!arguments.length) return onClick;
        onClick = _;
        return chart;
    };

    chart.color = function (_) {
        if (!arguments.length) return color;
        color = _;
        return chart;
    };

    return chart;
}