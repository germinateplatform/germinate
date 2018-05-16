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

function histogram() {
    var margin = {
            top: 20,
            right: 20,
            bottom: 40,
            left: 50
        },
        width = 960,
        height = 500,
        xScale = d3.scale.linear(),
        yScale = d3.scale.linear(),
        xValue = function (d) {
            return d[0];
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
        nrOfBars = -1,
        tip;

    function chart(selection) {
        selection.each(function (data) {

            height = height - margin.bottom - margin.top;
            width = width - margin.left - margin.right;

            data = data.map(function(d) { return xValue(d); });

            // Update the x-scale.
            xScale.domain([Math.floor(d3.min(data, function(d) {
                return d;
            })), Math.ceil(d3.max(data, function(d) {
                return d;
            }))])
            .range([0, width]);

            if(nrOfBars == -1)
                data = d3.layout.histogram().bins(xScale.ticks())(data);
            else
                data = d3.layout.histogram().bins(xScale.ticks(nrOfBars))(data);

            // Update the y-scale.
            yScale.domain([0, d3.max(data, function(d) {
                return d.y;
            })])
            .range([height, 0]);

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
              .enter().append("g")
                .attr("class", "bar")
                .attr("transform", function(d) { 
                    return "translate(" + xScale(d.x) + "," + yScale(d.y) + ")";
                });

            bars.append("rect")
                .attr("class", "bar " + barStyle)
                .attr("x", 1)
                .attr("width", function(d) { return xScale(d.dx) - 1; })
                .attr("height", function(d) { return height - yScale(d.y); })
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

    chart.nrOfBars = function (_) {
        if (!arguments.length) return nrOfBars;
        nrOfBars = _;
        return chart;
    };

    return chart;
}