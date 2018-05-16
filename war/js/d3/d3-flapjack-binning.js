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

function d3BinningDiv() {
    var id = "",
        min = 0,
        max = 1,
        splitPoint = null,
        height = 18,
        widths = [],
        colors = [],
        data = [],
        tooltipStyle = "",
        rectStyle = "",
        separatorStyle = "",
        hoverCallback = function (isHover, start, end) {
            // Do nothing here
        };

    function chart() {
        var temp = 0;

        // Reformat the data
        for (var i = 0; i < Math.min(colors.length, widths.length); i++) {
            data.push({
                index: i,
                color: colors[i],
                width: widths[i],
                range: [(temp / 100 * (max - min) + min).toFixed(2), ((temp + widths[i]) / 100 * (max - min) + min).toFixed(2)]
            });

            temp += widths[i];
        }

        $('#' + id).empty();

        $('#gm8-d3js-tooltip-' + id).remove();

        var div = d3.select('#' + id);

        // Add the tooltip div
        var tooltip = d3.select('body')
            .append('div')
            .attr('id', 'gm8-d3js-tooltip-' + id)
            .attr('class', 'gm8-d3js-tooltip ' + tooltipStyle)
            .style('display', 'none');

        // Add the actual rectangles
        div.selectAll('.' + rectStyle)
            .data(data)
            .enter()
            .append('div')
            .attr('class', rectStyle)
            .style('background', function (d) {
                return d.color;
            })
            .style('width', function (d) {
                return d.width + "%";
            })
            .style('height', height + "px").on('mouseenter', function (d) {
                // Position the tooltip
                var that = d3.select(this)[0][0];
                var width = that.offsetWidth;
                var height = that.offsetHeight;
                var left = that.offsetLeft;
                var top = that.offsetTop;

                var x = left + width / 2;

                tooltip.style('display', 'initial')
                    .style('top', top + height + 'px')
                    .style('left', x + 'px')
                    .text('[' + d.range + ']');

                x -= tooltip[0][0].offsetWidth / 2;

                tooltip.style('left', x + 'px');

                hoverCallback(true, d.range[0], d.range[1]);
            })
            .on('mouseleave', function () {
                tooltip.style('display', 'none');
            });

        div.on('mouseleave', function () {
            hoverCallback(false, 0, 1);
        });

        // Add the separator if required
        if (typeof splitPoint !== 'undefined' && splitPoint !== null) {

            // Make sure the split point has a valid position
            splitPoint = Math.min(Math.max(0, splitPoint), Math.min(widths.length, colors.length));

            div.append('div')
                .attr('class', separatorStyle)
                .style('margin-left', function (d) {
                    var separatorWidth = d3.select(this)[0][0].offsetWidth / 2;
                    if (separatorWidth % 1 === 0)
                        separatorWidth += 0.25;
                    return -separatorWidth + "px";
                })
                .style('padding-left', (splitPoint / (max + min)) * 100 + '%');
        }
    }

    chart.widths = function (_) {
        if (!arguments.length) return widths;
        widths = _;
        return chart;
    };

    chart.colors = function (_) {
        if (!arguments.length) return colors;
        colors = _;
        return chart;
    };

    chart.id = function (_) {
        if (!arguments.length) return id;
        id = _;
        return chart;
    };

    chart.min = function (_) {
        if (!arguments.length) return min;
        min = _;
        return chart;
    };

    chart.max = function (_) {
        if (!arguments.length) return max;
        max = _;
        return chart;
    };

    chart.splitPoint = function (_) {
        if (!arguments.length) return splitPoint;
        splitPoint = _;
        return chart;
    };

    chart.tooltipStyle = function (_) {
        if (!arguments.length) return tooltipStyle;
        tooltipStyle = _;
        return chart;
    };

    chart.rectStyle = function (_) {
        if (!arguments.length) return rectStyle;
        rectStyle = _;
        return chart;
    };

    chart.separatorStyle = function (_) {
        if (!arguments.length) return separatorStyle;
        separatorStyle = _;
        return chart;
    };

	chart.height = function (_) {
		if (!arguments.length) return height;
		height = _;
		return chart;
	};

    chart.hoverCallback = function (_) {
        if (!arguments.length) return hoverCallback;
        hoverCallback = _;
        return chart;
    };

    return chart;
}