function mapCanvas() {
	var margin = {left: 100, top: 10, right: 150, bottom: 10},
		individualHeight = 40,
		excludePercentage = 0,
		tooltipStyle = "tip",
		preferredWidth = null,
		color = "#00acef",
		width,
		height;

	function chart(selection) {
		selection.each(function (data) {
			data.forEach(function (d) {
				d.position = +d.position;
			});

			var canvas = document.createElement("canvas");
			this.appendChild(canvas);

			// Get distinct chromosomes
			var chromosomes = data.map(function (obj) {
				return obj.chromosome;
			});
			chromosomes = chromosomes.filter(function (v, i) {
				return chromosomes.indexOf(v) === i;
			});

			// Add the tooltip
			var tip = document.createElement("div");
			tip.style.display = "none";
			tip.classList.add("gm8-d3js-tooltip");
			tip.classList.add(tooltipStyle);
			var tipContent = document.createElement("span");
			tip.appendChild(tipContent);
			document.body.appendChild(tip);

			// Remember to remove it
			canvas.onunload = function () {
				$(tip).remove();
			};

			var ctx = canvas.getContext("2d");

			var min = Number.MAX_VALUE;
			var mins = {};
			var max = 0;
			var maxs = {};
			var splitData = {};

			// For each chromosome
			chromosomes.forEach(function (d, i) {
				// Determine all markers on said chromosome
				var localData = data.filter(function (d) {
					return d.chromosome === chromosomes[i];
				});

				// Get the minimum on the chromosome
				mins[d] = localData.reduce(function (min, d) {
					return d.position < min ? d.position : min;
				}, Number.MAX_VALUE);

				// Get the maximum on the chromosome
				maxs[d] = localData.reduce(function (max, d) {
					return d.position > max ? d.position : max;
				}, 0);

				// Get overall minimum and maximum
				if (mins[d] < min)
					min = mins[d];
				if (maxs[d] > max)
					max = maxs[d];

				splitData[d] = localData;
			});

			chromosomes = chromosomes.filter(function (d) {
				return (maxs[d] - mins[d]) / (max - min) * 100 > excludePercentage;
			});

			// Set size
			canvas.width = preferredWidth || document.body.offsetWidth;
			canvas.height = (chromosomes.length + 1) * individualHeight + margin.top + margin.bottom;
			ctx.clearRect(0, 0, canvas.width, canvas.height);

			width = canvas.width - individualHeight;
			height = canvas.height;

			ctx.font = '15px Calibri';

			// For each chromosome
			chromosomes.forEach(function (c, i) {
				// Add the name
				ctx.globalAlpha = 1;
				ctx.textAlign = "end";
				ctx.strokeStyle = "#000000";
				ctx.fillText(c, margin.left - 10, i * individualHeight + 25 - 0.5 + margin.top);

				// Draw the outside rectangle
				var cx = (maxs[c] - mins[c]) / (maxs[c] - mins[c]) * ((width - margin.right) * maxs[c] / max);
				ctx.rect(margin.left - 0.5, i * individualHeight + 10 - 0.5 + margin.top, cx + 1, individualHeight / 2 + 1);
				ctx.stroke();
				ctx.globalAlpha = 0.6;

				// For each item in the chromosome
				splitData[c].forEach(function (d, j) {
					// Determine the x position by min-max normalization
					var x = margin.left + (d.position - mins[c]) / (maxs[c] - mins[c]) * ((width - margin.right) * maxs[c] / max);

					// Draw it
					ctx.strokeStyle = color;
					ctx.beginPath();
					ctx.moveTo(x + 0.5, i * individualHeight + 10 + margin.top);
					ctx.lineTo(x + 0.5, i * individualHeight + 10 + individualHeight / 2 + margin.top);
					ctx.stroke();
				});
			});

			ctx.globalAlpha = 1;
			ctx.strokeStyle = "#000000";

			// Add the axis
			ctx.beginPath();
			ctx.moveTo(margin.left - 0.5, height - individualHeight + 0.5 + 5 + margin.top - margin.bottom);
			ctx.lineTo(margin.left - 0.5, height - individualHeight + 0.5 + margin.top - margin.bottom);
			ctx.lineTo(margin.left + width - margin.right + 0.5, height - individualHeight + 0.5 + margin.top - margin.bottom);
			ctx.lineTo(margin.left + width - margin.right + 0.5, height - individualHeight + 0.5 + 5 + margin.top - margin.bottom);
			ctx.stroke();

			// Add the text
			ctx.textAlign = "center";
			ctx.fillText(min, margin.left - 0.5, height - individualHeight + 0.5 + 25 + margin.top - margin.bottom);
			ctx.fillText(max.toLocaleString(), margin.left + width - margin.right + 0.5, height - individualHeight + 0.5 + 25 + margin.top - margin.bottom);

			canvas.addEventListener("mouseout", function (e) {
				$(tip).hide();
			});

			canvas.addEventListener("mousemove", function (e) {
				var rect = this.getBoundingClientRect(),
					ex = e.clientX - rect.left,
					ey = e.clientY - rect.top;

				var minPoint = {
					dx: null,
					x: null,
					y: null,
					item: null
				};

				chromosomes.forEach(function (c, i) {
					ctx.beginPath();
					var cx = (maxs[c] - mins[c]) / (maxs[c] - mins[c]) * ((width - margin.right) * maxs[c] / max);
					ctx.rect(margin.left - 0.5, i * individualHeight + 10 - 0.5 + margin.top, cx + 1, individualHeight / 2 + 1);

					var isInPath = ctx.isPointInPath(ex, ey);

					// It's this chromosome we're hovering over
					if (isInPath) {
						splitData[c].forEach(function (d) {
							// Get closest data point
							var x = margin.left + (d.position - mins[c]) / (maxs[c] - mins[c]) * ((width - margin.right) * maxs[c] / max);
							var dex = Math.abs(x - ex);


							if (!minPoint.dx || dex < minPoint.dx) {
								minPoint.dx = dex;
								minPoint.x = x;
								minPoint.y = i * individualHeight + 10 - 0.5 + margin.top;
								minPoint.item = d;
							}
						});
					}
				});

				// Show tooltip
				if (minPoint.item) {
					$(tip).show();
					tipContent.innerHTML = minPoint.item.markerName + "<br/>" + minPoint.item.position;

					var tipHeight = tip.offsetHeight;
					var tipWidth = tip.offsetWidth;

					$(tip).css({
						"position": "fixed",
						"top": (minPoint.y + rect.top - tipHeight - 6) + "px",
						"left": (rect.left + minPoint.x - tipWidth / 2 + 0.5) + "px"
					});
				} else {
					$(tip).hide();
				}
			});
		});
	}

	chart.margin = function (_) {
		if (!arguments.length) return margin;
		margin = _;
		return chart;
	};

	chart.excludePercentage = function (_) {
		if (!arguments.length) return excludePercentage;
		excludePercentage = _;
		return chart;
	};

	chart.individualHeight = function (_) {
		if (!arguments.length) return individualHeight;
		individualHeight = _;
		return chart;
	};

	chart.tooltipStyle = function (_) {
		if (!arguments.length) return tooltipStyle;
		tooltipStyle = _;
		return chart;
	};

	chart.preferredWidth = function (_) {
		if (!arguments.length) return preferredWidth;
		preferredWidth = _;
		return chart;
	};

	chart.color = function (_) {
		if (!arguments.length) return color;
		color = _;
		return chart;
	};

	return chart;
}