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

function scatterPlot() {
    var margin = {
            top: 20,
            right: 30,
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
        itemName = null,
        xAxis = d3.svg.axis().scale(xScale).orient("bottom"),
        yAxis = d3.svg.axis().scale(yScale).orient("left"),
        yLabel = null,
        xLabel = null,
        axisStyle = "",
        dotStyle = "",
        tooltipStyle = "",
        legendItemStyle = "",
        xTickFormat = d3.format(".2s"),
        yTickFormat = d3.format(".2s"),
        tip,
        id = function (d) {
            return d.id;
        },
        highlightColor = null,
        color = d3.scale.category10(),
        colorKey = function (d) {
            return d.xValue;
        },
        lassoConfig = null,
        onClick = function (d) {
            // do nothing
        },
        showGrid = false,
        showLegend = false,
        showDistribution = false,
        legendWidth = 50;

    function chart(selection) {
        selection.each(function (data) {

            // Do we need to make room for a legend?
            margin.right += showLegend ? legendWidth : 0;

            height = height - margin.bottom - margin.top;
            width = width - margin.left - margin.right;

            data.forEach(function (d) {
                d.xValue = xValue(d);
                d.yValue = yValue(d);
                if(itemName)
                    d.itemName = itemName(d);
                else
                    d.itemName = null;
                d.colorKey = colorKey(d);
                d.color = color(d.colorKey);
                d.id = id(d);
            });

            data = $.map(data, function (d, key) {
                if(!isNaN(d.xValue) && !isNaN(d.yValue))
                    return d;
			});

            data = data.filter(function (d) {
                return !isNaN(d.xValue) && !isNaN(d.yValue);
            });

            if (xTickFormat)
                xAxis.tickFormat(xTickFormat);
            if (yTickFormat)
                yAxis.tickFormat(yTickFormat);

            xScale.domain(d3.extent(data, function (d) {
                return d.xValue;
            })).nice();

            xScale.range([0, width]);

            yScale.domain(d3.extent(data, function (d) {
                return d.yValue;
            })).nice();

            yScale.range([height, 0]);

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

            if (showGrid) {
                // Add the vertical grid lines
                gEnter.append("g")
                    .attr("class", "grid")
                    .attr("transform", "translate(0," + yScale.range()[0] + ")")
                    .style("stroke", "lightgray")
                    .style("stroke-width", "1px")
                    .call(makeXAxis().tickSize(-height, 0, 0).tickFormat(""));

                // Add the horizontal grid lines
                gEnter.append("g")
                    .attr("class", "grid")
                    .style("stroke", "lightgray")
                    .style("stroke-width", "1px")
                    .call(makeYAxis().tickSize(-width, 0, 0).tickFormat(""));
            }

            var dots = g.selectAll(".dot")
                .data(data)
                .enter()
                .append("circle")
                .attr("class", function (d) {
                    if (d.colorKey)
                        return "dot item " + dotStyle + " item-" + d3.makeSafeForCSS(d.colorKey);
                    else
                        return "dot item " + dotStyle + " item-n__a";
                })
                .attr("id", function (d) {
                    return "item-" + d.id;
                })
                .attr("itemname", function (d) {
                    if(d.itemName)
                        return d.itemName;
                    else
                        return "";
                })
                .attr("r", 2.5)
                .attr("cx", function (d) {
                    return xScale(d.xValue);
                })
                .attr("cy", function (d) {
                    return yScale(d.yValue);
                })
                .style("fill", function (d) {
                    return d.color;
                });

            if (showDistribution) {
                g.selectAll(".histo-x")
                    .data(data)
                    .enter()
                    .append("line")
                    .attr("class", function (d) {
						if (d.colorKey)
							return "histo-x item item-" + d3.makeSafeForCSS(d.colorKey);
                        else
                            return "histo-x item item-n__a";
                    })
                    .attr("r", 2.5)
                    .attr("x1", function (d) {
                        return xScale(d.xValue);
                    })
                    .attr("x2", function (d) {
                        return xScale(d.xValue);
                    })
                    .attr("y1", 0)
                    .attr("y2", 0 - 10)
                    .style("stroke-width", 1)
                    .style("stroke", function (d) {
                        return d.color;
                    });

                g.selectAll(".histo-y")
                    .data(data)
                    .enter()
                    .append("line")
                    .attr("class", function (d) {
						if (d.colorKey)
							return "histo-y item item-" + d3.makeSafeForCSS(d.colorKey);
						else
						    return "histo-y item item-n__a";
                    })
                    .attr("r", 2.5)
                    .attr("y1", function (d) {
                        return yScale(d.yValue);
                    })
                    .attr("y2", function (d) {
                        return yScale(d.yValue);
                    })
                    .attr("x1", width)
                    .attr("x2", width + 10)
                    .style("stroke-width", 1)
                    .style("stroke", function (d) {
                        return d.color;
                    });
            }

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
                    .on("click", onClick)
                    .on("mouseenter", function (d) {
                        if (!lassoConfig || !d3.select(this).classed(lassoConfig.selectedStyle)) {

                            svg.selectAll("#item-" + d.id)
                                .transition()
                                .duration(100)
                                .style("fill", highlightColor ? highlightColor : null)
                                .attr("r", 4.5);
                        }

                        tip.show(d);
                    })
                    .on("mouseleave", function (d) {
                        if (!lassoConfig || !d3.select(this).classed(lassoConfig.selectedStyle)) {

                            svg.selectAll("#item-" + d.id)
                                .transition()
                                .duration(200)
                                .style("fill", function (d) {
                                    return d.color;
                                })
                                .attr("r", 2.5);
                        }
                        tip.hide();
                    });
            }

            // Check if a legend is required
            if (showLegend) {
                d3.legend(this, svg, color, margin, width, height, legendWidth, legendItemStyle, true);
            }

            if (lassoConfig) {
                // Lasso functions to execute while lassoing
                var lasso_start = function () {
                    lasso.items()
                        .attr("r", 2.5) // reset size
                        .style("fill", null) // clear all of the fills
                        .classed(lassoConfig.selectedStyle, false)
                        .classed(lassoConfig.notPossibleStyle, true) // style as not possible
                        .classed(lassoConfig.possibleStyle, false);
                };

                var lasso_draw = function () {
                    // Style the possible dots
                    lasso.items().filter(function (d) {
                            return d.possible === true && svg.selectAll("#item-" + d.id).style("opacity") != "0"
                        })
                        .classed(lassoConfig.selectedStyle, false)
                        .classed(lassoConfig.notPossibleStyle, false)
                        .classed(lassoConfig.possibleStyle, true);

                    // Style the not possible dot
                    lasso.items().filter(function (d) {
                            return d.possible === false || svg.selectAll("#item-" + d.id).style("opacity") == "0"
                        })
                        .classed(lassoConfig.selectedStyle, false)
                        .classed(lassoConfig.notPossibleStyle, true)
                        .classed(lassoConfig.possibleStyle, false);
                };

                var lasso_end = function () {
                    // Reset the color of all dots
                    lasso.items()
                        .style("fill", function (d) {
                            return d.color;
                        });

                    // Style the selected dots
                    lasso.items().filter(function (d) {
                            return d.selected === true && svg.selectAll("#item-" + d.id).style("opacity") != "0"
                        })
                        .classed(lassoConfig.selectedStyle, true)
                        .attr("r", 5);


                    // Reset the style of the not selected dots
                    lasso.items().filter(function (d) {
                            return d.selected === false
                        })
                        .classed(lassoConfig.selectedStyle, false)
                        .classed(lassoConfig.notPossibleStyle, false)
                        .classed(lassoConfig.possibleStyle, false)
                        .attr("r", 2.5);

                };

                // Define the lasso
                var lasso = d3.lasso()
                    .items(svg.selectAll(".dot"))
                    //.closePathDistance(Number.MAX_VALUE) // max distance for the lasso loop to be closed
                    .closePathDistance(75)
                    .closePathSelect(true) // can items be selected by closing the path?
                    .hoverSelect(true) // can items by selected by hovering over them?
                    .area(svg) // area where the lasso can be started
                    .on("start", lasso_start) // lasso start function
                    .on("draw", lasso_draw) // lasso draw function
                    .on("end", lasso_end); // lasso end function

                // Init the lasso on the svg:g that contains the dots
                svg.call(lasso);
            }

            function makeXAxis() {
                return d3.svg.axis().scale(xScale).orient("bottom").ticks(10);
            }

            function makeYAxis() {
                return d3.svg.axis().scale(yScale).orient("left").ticks(10);
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

    chart.highlightColor = function (_) {
        if (!arguments.length) return highlightColor;
        highlightColor = _;
        return chart;
    };

    chart.colorKey = function (_) {
        if (!arguments.length) return colorKey;
        colorKey = _;
        return chart;
    };

    chart.id = function (_) {
        if (!arguments.length) return id;
        id = _;
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

    chart.legendItemStyle = function (_) {
        if (!arguments.length) return legendItemStyle;
        legendItemStyle = _;
        return chart;
    };

    chart.tooltipStyle = function (_) {
        if (!arguments.length) return tooltipStyle;
        tooltipStyle = _;
        return chart;
    };

    chart.showGrid = function (_) {
        if (!arguments.length) return showGrid;
        showGrid = _;
        return chart;
    };

    chart.showLegend = function (_) {
        if (!arguments.length) return showLegend;
        showLegend = _;
        return chart;
    };

    chart.showDistribution = function (_) {
        if (!arguments.length) return showDistribution;
        showDistribution = _;
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

    chart.lassoConfig = function (_) {
        if (!arguments.length) return lassoConfig;
        lassoConfig = _;
        return chart;
    };

    chart.itemName = function (_) {
        if (!arguments.length) return itemName;
        itemName = _;
        return chart;
    };

    return chart;
}