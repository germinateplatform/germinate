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

d3.makeSafeForCSS = function (name) {
    if(name) {
    	if(name === "N/A") {
			name = "n__a";
		} else {
			name = name.replace(/[^a-z0-9]/g, function (s) {
				var c = s.charCodeAt(0);
				if (c == 32) return '-';
				if (c >= 65 && c <= 90) return s.toLowerCase();
				return '__' + ('000' + c.toString(16)).slice(-4);
			});
		}
    }
    else {
    	name = "n__a";
	}

    return name;
};

function isOverflowed(element) {
    return element.scrollHeight > element.clientHeight;
}

d3.legend = function (parent, svg, scale, margin, width, height, legendWidth, legendItemStyle, sortLegend) {
    d3.select(parent)
        .style("position", "relative");

    var offset = $(svg.node()).offset();
    var parentOffset = $(parent).offset();

    var legendDiv = d3.select(parent).append("div")
        .attr("class", "gm8-d3js-legend")
        .style("position", "absolute")
        .style("top", margin.top + (offset.top - parentOffset.top) + "px")
        .style("left", (width + (margin.right - legendWidth) + 10 + (offset.left - parentOffset.left)) + "px")
        .style("width", legendWidth + "px")
        .style("max-height", height + "px")
        .style("max-width", legendWidth + "px")
        .style("overflow-y", "auto")
        .style("overflow-x", "hidden");

    var domain = scale.domain();

    if (sortLegend)
        domain = domain.sort(d3.ascending);

    var hidden = {};
    domain.forEach(function (d) {
        hidden[d] = false;
    });

    // Add a new div for each item
    var legendItems = legendDiv.selectAll("div")
        .data(domain)
        .enter()
        .append("div")
        .attr("id", function (d) {
            return "legend-item-" + d3.makeSafeForCSS(d);
        })
        .attr("class", "legend-item " + legendItemStyle)
        .on("click", function (d) {
            hidden[d] = !hidden[d];

            var opacity = hidden[d] ? 0.05 : 1.0;

			svg.selectAll(".item-" + d3.makeSafeForCSS(d))
				.style("opacity", opacity);

			legendDiv.selectAll("#legend-item-" + d3.makeSafeForCSS(d) + " i")
				.style("opacity", opacity);
        });

    legendItems.append("i")
        .attr("class", "mdi mdi-lg mdi-circle")
        .style("vertical-align", "middle")
        .style("color", function (d) {
            return scale(d);
        });

    var spans = legendItems.append("span")
        .style("padding-left", "5px")
        .style("width", (legendWidth - 20) + "px")
        .text(function (d) {
            if(d)
                return d;
            else
                return "N/A";
        })
        .attr("title", function (d) {
            if(d)
                return d;
            else
                return "N/A";
        });

    // Add buttons to hide/show all items at the same time
    var p = $.parseHTML("<div class='btn-group' role='group'></div>");
	$(legendDiv.node()).append(p);
    addButton(p, "mdi-eye", false);
    addButton(p, "mdi-eye-off-outline", true);

    /* Reduce the width to accommodate space for an eventual scrollbar */
    if (isOverflowed(legendDiv.node())) {
        spans.style("width", (legendWidth - 40) + "px");
    }

    function addButton(div, style, hide) {
        var element = $.parseHTML("<button type='button' class='btn btn-xs mdi mdi-lg " + style + "'></button>");
        $(div).append(element);

		var opacity = hide ? 0.05 : 1.0;

        $(element).on("click", function() {
			svg.selectAll(".item")
				.style("opacity", opacity);

			legendDiv.selectAll(".legend-item i")
				.style("opacity", opacity);

			for (var property in hidden) {
				if (hidden.hasOwnProperty(property)) {
					hidden[property] = hide;
				}
			}
        });
    }
};