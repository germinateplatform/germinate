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

function horizontalGroupedBarChart() {
    var margin = {
            top: 40,
            right: 40,
            bottom: 60,
            left: 40
        },
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom,
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
        xAxis = d3.svg.axis().scale(x0Scale).orient("left"),
        yAxis = d3.svg.axis().scale(yScale).orient("top"),
        yLabel = null,
        xLabel = null,
        axisStyle = "",
        tooltipStyle = "",
        color = d3.scale.category10(),
        tip,
        ignoreIndices = [0];

    function chart(selection) {
        selection.each(function (data) {

            color.domain(d3.keys(data[0]).filter(function (d, i) {
                return ignoreIndices.indexOf(i) === -1;
            }));

            data.forEach(function (d) {
                d.categories = color.domain().map(function (name) {
                    return {
                        "orig": d,
                        "name": name,
                        "y": yValue(d[name]),
                        "x": xValue(d)
                    };
                });
            });

            var maxValue = d3.max(data, function (d) {
                return d3.max(d.categories, function (d) {
                    return d.y;
                });
            });
            var minValue = Math.min(0, d3.min(data, function (d) {
                return d3.min(d.categories, function (d) {
                    return d.y;
                });
            }));

            var sections = data.map(function (d) {
                return xValue(d);
            });

            // Select the svg element, if it exists.
            var svg = d3.select(this).selectAll("svg").data([data]);

            // Calculate the width and height based on the parent width and the number of sites and data entries
            var individualBarHeight = color.domain().length == 1 ? 20 : 10;
            var barGroupHeight = individualBarHeight * color.domain().length;
            height = barGroupHeight * data.length + margin.bottom + margin.top;

            // Update the x-scales
            x0Scale.domain(sections).rangeRoundBands([0, height - margin.top - margin.bottom], .1, .3);
            x1Scale.domain(color.domain()).rangeRoundBands([0, x0Scale.rangeBand()], .1);

            // Update the y-scale.
            yScale.domain([d3.max(data, function (c) {
                return d3.max(c.categories, function (v) {
                    return v.y;
                });
            }), Math.min(0, d3.min(data, function (c) {
                return d3.min(c.categories, function (v) {
                    return v.y;
                });
            }))]).range([width - margin.left - margin.right, 0]);

            // Otherwise, create the skeletal chart.
            var gEnter = svg.enter().append("svg").append("g");

            gEnter.append("g").attr("class", "x axis " + axisStyle);
            gEnter.append("g").attr("class", "y axis " + axisStyle);

            // Update the outer dimensions.
            svg.attr("width", width)
                .attr("height", height);

            var g = svg.select("g");

            // Update the x-axis.
            var xAxisElement = g.select(".x.axis")
                .call(xAxis);

            // Calculate the maximal width of all the y axis labels
            // Update the svg margin accordingly
            margin.left += d3.max(svg.selectAll(".x.axis text")[0], function (d, i) {
                return d.getComputedTextLength();
            });

            // Update the values again, since they changed because of the y label margin change
            x0Scale.rangeRoundBands([0, height - margin.top - margin.bottom], .1, .3);
            yScale.range([width - margin.left - margin.right, 0]);

            // Update the inner dimensions.
            g.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

            // Calculate the mean per site
            var means = [];
            color.domain().forEach(function (d) {
                var json = {};
                json['category'] = d;
                json['mean'] = d3.mean(data, function (e) {
                    return +e[d];
                });
                means.push(json);
            });

            // Add mean rules
            var meanLines = gEnter.selectAll("g.means")
                .data(means)
                .enter().append("g")
                .attr("id", "test")
                .attr("class", "rule means")
                .attr("transform", function (d) {
                    return "translate(" + yScale(d.mean) + ", 0)";
                });

            meanLines.append("line")
                .attr("y2", height - margin.bottom - margin.top)
                .style("stroke", function (d) {
                    return color(d.category);
                })
                .style("stroke-width", "2px")
                .style("stroke-opacity", "1");

            meanLines.append("text")
                .attr("dy", ".3em")
                .attr("transform", function (d) {
                    return "translate(0, " + (height - margin.top - margin.bottom + individualBarHeight) + "),rotate(-20)"
                })
                .style("text-anchor", "end")
                .text(function (d) {
                    return "AVG: " + d.mean.toFixed(2);
                });



            var categories = gEnter.selectAll(".category")
                .data(data)
                .enter().append("g")
                .attr("class", "category")
                .attr("transform", function (d) {
                    return "translate(0, " + x0Scale(xValue(d)) + ")";
                });

            var bars = categories.selectAll("rect")
                .data(function (d) {
                    return d.categories;
                })
                .enter().append("rect")
                .attr("class", "bar")
                .attr("y", function (d) {
                    return x1Scale(d.name);
                })
                .attr("height", x1Scale.rangeBand())
                .attr("x", function (d) {
                    return x0Scale(Math.max(0, d.x));
                })
                .attr("width", function (d) {
                    return 0;
                    //                    return isNaN(d.y) ? 0 : Math.abs(yScale(d.y) - yScale(0));
                })
                .style("fill", function (d) {
                    return color(d.name);
                });
            bars.transition()
                .duration(1000)
                .delay(function (d) {
                    return ((d.y - minValue) / (maxValue - minValue)) * 1000;
                })
                .attr("width", function (d) {
                    return isNaN(d.y) ? 0 : Math.abs(yScale(d.y) - yScale(0));
                });

            if (xLabel) {
                //                xAxisElement.append("text")
                //                    .attr("transform", "translate(" + width / 2 + ", 0)")
                //                    .attr("dy", "3em")
                //                    .style("text-anchor", "middle")
                //                    .text(xLabel);
            }

            var yAxisElement = svg.select(".y.axis")
                .call(yAxis);

            if (yLabel) {
                yAxisElement.append("text")
                    .attr("transform", "translate(" + (width - margin.left - margin.right) + ", 0)")
                    .attr("dy", "1em")
                    .style("text-anchor", "end")
                    .text(yLabel)
            }


            if (d3.tip) {
                tip = d3.tip()
                    .attr('class', 'gm8-d3js-tooltip ' + tooltipStyle)
                    .direction('e')
                    .offset([-1, 10])
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

    return chart;
}