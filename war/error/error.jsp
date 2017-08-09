<%@page isErrorPage="true" %>

<%
    String requestedPage = "";
    //String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    String baseUrl = request.getContextPath();
    String errorCode = "404";

    // Get the PageContext
    if (pageContext != null)
    {

        // Get the ErrorData
        ErrorData ed = null;
        try
        {
            ed = pageContext.getErrorData();
        }
        catch (NullPointerException e)
        {
        }

        // Display error details for the user
        if (ed != null)
        {
            // Output details about the HTTP error
            // (this should show error code 404, and the name of the missing page)
            requestedPage = ed.getRequestURI();

            errorCode = "" + ed.getStatusCode();
        }
    }
%>

<html>
<head>
    <script type="text/javascript" src="<%=baseUrl%>/error/d3.v3.js"></script>
    <script type="text/javascript" src="<%=baseUrl%>/error/jquery-1.8.2.min.js"></script>
    <script type="text/javascript" src="<%=baseUrl%>/error/constants.js"></script>

    <link href="http://netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css"
          rel="stylesheet">
    <!-- <script type="text/javascript" src="http://mbostock.github.com/d3/d3.geom.js"></script> -->


    <style type="text/css">
        body {
            border: 0;
            margin: 0;
            background-color: #444;
        }

        body#outer {
            background-image: url("<%=baseUrl%>/error/tile.jpg");
        }

        #popupWrapper {
            position: fixed;
            width: 100%;
            height: 100%;
            left: 0;
            top: 0;
            z-index: 1000;
            background: black;
            vertical-align: middle;
            opacity: 0.8;
        }

        #popupContent {
            position: absolute;
            width: 100%;
            top: 50%;
            z-index: 1001;
            vertical-align: middle;
            text-align: center;
            color: white;
            font-weight: bold;
            font-size: 16px;
        }

        #text {
            position: absolute;
            width: 600px;
            height: 300px;
            text-align: justify;
            left: 50%;
            margin-left: -300px;
            color: white;
        }

        .flat-button {
            display: inline-block;
            white-space: nowrap;
            cursor: pointer;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            position: relative;
            overflow: hidden;
            text-overflow: ellipsis;
            color: #fff;
            background-color: #00acef;
            border: 1px solid rgba(0, 0, 0, .5);
            border-bottom: 4px solid #0078a7;

            webkit-transition: opacity 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s;
            -moz-transition: opacity 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s;
            -ms-transition: opacity 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s;
            -o-transition: opacity 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s;
            transition: opacity 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s;

            margin-top: 2px;
        }

        .flat-button:active {
            top: 2px;
            border-bottom: 2px solid #00acef;
            outline: none;
            -webkit-box-shadow: none;
            box-shadow: none;
            margin-bottom: 2px;
        }

        .flat-button.disabled {
            background-color: #bbb;
            border-bottom: 4px solid #999;
            cursor: default;
        }

        .flat-button:hover {
            opacity: 0.7;
        }

        .flat-button.disabled:active {
            top: 0;
            border-bottom: 4px solid #999;
            margin-bottom: 0;
        }

        .flat-button.disabled:hover {
            opacity: 1;
        }

        .flat-button.disabled > .fa {
            text-align: center;
            border-right: 1px solid #999;
        }

        .flat-button > .text,
        .flat-button > .fa {
            display: inline-block;
            vertical-align: middle;
            padding: 2px 5px;
            line-height: 22px;
        }

        .flat-button > .text {
            padding-right: 10px;
            padding-left: 10px;
        }

        .flat-button > .fa {
            text-align: center;
            border-right: 1px solid #0078a7;
        }

        .flat-button.vertical > .text,
        .flat-button.vertical > .fa {
            display: block;
            vertical-align: middle;
            padding: 2px 5px;
            margin: 0;
            width: 100%;
            box-sizing: border-box;
        }

        .flat-button.vertical > .fa {
            text-align: center;
            border-right: 0;
        }

        #button-bar-wrapper {
            z-index: 1002;
            width: 100%;
            background: rgba(0, 0, 0, .3);
            position: fixed;
            bottom: 0;
            padding: 10px 0;
        }

        #button-bar {
            z-index: 1002;
            width: 400px;
            text-align: center;
            left: 50%;
            margin-left: -200px;
            position: relative;
        }

        .fallback-content {
            width: 50%;
            margin: 20px auto;
            color: white;
            padding-bottom: 40px;
            display: none;
        }
    </style>

    <script type="text/javascript">
        // Listen for resize events
        $(window).resize(function () {
            $("#chart").empty();
            $("body > #text").remove();
            doFancyStuff();
        });

        function doFancyStuff() {
            var w = window.innerWidth,
                    h = Math.max(800, window.innerHeight - 40);

            var textDivTop = 420;

            var aspect = h / w;

            // Get the error code and split it
            var errorCode = <%= errorCode %>;

            var digits = errorCode.toString().split('');

            var vertices = [];
            var i;

            // Get the appropriate numbers from the global array
            for (i = 0; i < digits.length; i++) {
                vertices[i] = window.myFancyCloneFunction(window.errorPageNumbers[digits[i]]);
            }

            // Add some variance (jitter) to the coordinates
            var variance = 6;

            // Move nodes to the center and add variance
            for (i = 0; i < vertices.length; i++) {
                for (var j = 0; j < vertices[i].length; j++) {
                    vertices[i][j].x += (w - 600) / 2 + (Math.random() * variance * 2 - variance) + (50 + i * 200);
                    vertices[i][j].y += (100) + (Math.random() * variance * 2 - variance) + 50;
                }
            }

            // Join the sub arrays together
            var temp = [];

            for (i = 0; i < vertices.length; i++) {
                temp = temp.concat(vertices[i]);
            }

            vertices = temp;

            // Keep track of the already colored items
            var selectedItems = d3.set();

            // Define a color scale with the JHI colors
            var color = d3.scale.ordinal().range(["#559CBF", "#872175", "#79A22E"]);

            // Keep track of the time point of the first interaction with the svg
            var startTime = null;

            var svg = d3.select("#chart")
                    .append("svg:svg")
                    .attr("width", w)
                    .attr("height", h)
                    .attr("viewBox", "0 0 " + w + " " + h)
                    .attr("preserveAspectRatio", "xMinYMin meet")
                    .on("mouseenter", function (d, i) {
                        // Set the start time
                        if (startTime === null)
                            startTime = new Date();
                    });

            // Append the parrot
            svg.append("svg:image")
                    .attr('x', (w - 497) / 2)
                    .attr('y', 130)
                    .attr('width', 497)
                    .attr('height', 274)
                    .attr("xlink:href", "<%=baseUrl%>/error/" + window.errorPageImages[errorCode]);

            // Append the germinate logo
            svg.append("svg:a")
                    .attr("xlink:href", "http://ics.hutton.ac.uk/germinate")
                    .attr("xlink:show", "new")
                    .append("svg:image")
                    .attr('x', (w + 600) / 2 - 242)
                    .attr('y', 25)
                    .attr('width', 242)
                    .attr('height', 56)
                    .attr("xlink:href", "<%=baseUrl%>/error/germinate.png");

            // And the jhi logo
            svg.append("svg:a")
                    .attr("xlink:href", "http://www.hutton.ac.uk")
                    .attr("xlink:show", "new")
                    .append("svg:image")
                    .attr('x', (w - 600) / 2)
                    .attr('y', 25)
                    .attr('width', 121)
                    .attr('height', 56)
                    .attr("xlink:href", "<%=baseUrl%>/error/jhi.png");

            var p = d3.select("body").append("div")
                    .attr("id", "text")
                    .style("top", textDivTop + "px")
                    .html(window.errorPageText[errorCode]);

            // Define the content g elements
            var clips = svg.append("svg:g").attr("id", "point-clips");
            var points = svg.append("svg:g").attr("id", "points");
            var paths = svg.append("svg:g").attr("id", "point-paths");

            // Create the circles around each point (used for the clipping of the voronoi cells)
            clips.selectAll("clipPath")
                // One for each node
                    .data(vertices)
                    .enter().append("svg:clipPath")
                    .attr("id", function (d, i) {
                        return "clip-" + i;
                    })
                    .append("svg:circle")
                    .attr('cx', function (d) {
                        return d.x;
                    })
                    .attr('cy', function (d) {
                        return d.y;
                    })
                    .attr('r', w)
                // Add an animation
                    .transition()
                    .delay(0)
                    .duration(1500)
                    .attr('r', 25);

            // Define the voronoi function
            var voronoi = d3.geom.voronoi()
                    .x(function (d) {
                        return d.x;
                    })
                    .y(function (d) {
                        return d.y;
                    });

            // Add the voronoi path elements
            paths.selectAll("path")
                    .data(voronoi(vertices))
                    .enter().append("svg:path")
                    .attr("d", function (d) {
                        return "M" + d.join(",") + "Z";
                    })
                    .attr("id", function (d, i) {
                        return "path-" + i;
                    })
                    .attr("clip-path", function (d, i) {
                        return "url(#clip-" + i + ")";
                    })
                    .style("fill", "#fff")
                    .style('fill-opacity', 0.4)
                    .style("stroke", "#999");

            var done = false;

            // Listen for mouseover events on the path elements
            paths.selectAll("path")
                    .on("mouseover", function (d, i) {
                        // Add the current item to the set (remember that it is already colored)
                        selectedItems.add(i);

                        // Color it
                        d3.select(this)
                                .style('fill-opacity', 0.4)
                                .style("fill", function (d, i) {
                                    return color(d);
                                });

                        // Check if all items are colored -> start win animation
                        if (selectedItems.size() == vertices.length && !done) {
                            // Remember to just show this once
                            done = true;

                            // Get the current time and calculate the time the user took to color everything
                            var endTime = new Date();
                            var seconds = (endTime.getTime() - startTime.getTime()) / 1000;

                            // Start the chaos animation
                            var counter = 0;
                            var myInterval = setInterval(function () {
                                // If we showed 10 animation cycles, cancel the animation and show the popup
                                if (counter++ == 10) {
                                    clearInterval(myInterval);
                                    var outer = d3.select("#content")
                                            .append("div")
                                            .attr("id", "popupWrapper")
                                            .on("click", function (d, i) {
                                                d3.select(this).remove();
                                            });
                                    outer.append("div")
                                            .attr("id", "popupContent")
                                            .text("Well, that was " + seconds + " seconds well spent.");
                                }
                                // Let the chaos begin
                                chaos();
                            }, 500);

                            // Start the initial chaos animation
                            chaos();
                        }
                    })
                    .on("mouseout", function (d, i) {
                        d3.select(this)
                                .style("fill-opacity", 0.7);
                    });

            // Add the dots of the data points
            points.selectAll("circle")
                    .data(vertices)
                    .enter().append("svg:circle")
                    .attr("id", function (d, i) {
                        return "point-" + i;
                    })
                    .attr("transform", function (d) {
                        return "translate(" + d.x + "," + d.y + ")";
                    })
                    .attr("r", 2)
                    .attr('stroke', 'none');

            // Color scale used for the chaos animation
            var z = d3.scale.category20b();

            // Chaos animation
            function chaos() {
                var swag = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
                for (var k = 0; k < swag.length; k++) {
                    svg.append("svg:circle")
                            .attr("cx", Math.floor(Math.random() * w))
                            .attr("cy", Math.floor(Math.random() * h))
                            .attr("r", 20)
                            .style("stroke", z(++i))
                            .style("stroke-opacity", 1e-6)
                            .style("fill", z(i))
                            .style("fill-opacity", 1e-6)
                            .transition()
                            .ease(Math.sqrt)
                            .duration(250)
                            .style("stroke-opacity", 1)
                            .style("fill-opacity", 1)
                            .transition()
                            .duration(500)
                            .ease(Math.sqrt)
                            .attr("transform", "translate(0,-100)")
                            .style("stroke-opacity", 1e-6)
                            .style("fill-opacity", 1e-6)
                            .remove();
                    svg.append("svg:circle")
                            .attr("cx", Math.floor(Math.random() * w))
                            .attr("cy", Math.floor(Math.random() * h))
                            .attr("r", 20)
                            .style("stroke", z(++i))
                            .style("stroke-opacity", 1e-6)
                            .style("fill", z(i))
                            .style("fill-opacity", 1e-6)
                            .transition()
                            .ease(Math.sqrt)
                            .duration(250)
                            .style("stroke-opacity", 1)
                            .style("fill-opacity", 1)
                            .transition()
                            .duration(500)
                            .ease(Math.sqrt)
                            .attr("transform", "translate(100,0)")
                            .style("stroke-opacity", 1e-6)
                            .style("fill-opacity", 1e-6)
                            .remove();
                    svg.append("svg:circle")
                            .attr("cx", Math.floor(Math.random() * w))
                            .attr("cy", Math.floor(Math.random() * h))
                            .attr("r", 20)
                            .style("stroke", z(++i))
                            .style("stroke-opacity", 1e-6)
                            .style("fill", z(i))
                            .style("fill-opacity", 1e-6)
                            .transition()
                            .ease(Math.sqrt)
                            .duration(250)
                            .style("stroke-opacity", 1)
                            .style("fill-opacity", 1)
                            .transition()
                            .duration(500)
                            .ease(Math.sqrt)
                            .attr("transform", "translate(0,100)")
                            .style("stroke-opacity", 1e-6)
                            .style("fill-opacity", 1e-6)
                            .remove();
                    svg.append("svg:circle")
                            .attr("cx", Math.floor(Math.random() * w))
                            .attr("cy", Math.floor(Math.random() * h))
                            .attr("r", 20)
                            .style("stroke", z(++i))
                            .style("stroke-opacity", 1e-6)
                            .style("fill", z(i))
                            .style("fill-opacity", 1e-6)
                            .transition()
                            .ease(Math.sqrt)
                            .duration(250)
                            .style("stroke-opacity", 1)
                            .style("fill-opacity", 1)
                            .transition()
                            .duration(500)
                            .ease(Math.sqrt)
                            .attr("transform", "translate(-100,0)")
                            .style("stroke-opacity", 1e-6)
                            .style("fill-opacity", 1e-6)
                            .remove();
                }
            }
        }

    </script>

</head>
<body id="outer">
<div id="content" style="border: 0; margin: 0; padding-bottom: 40px;">
    <div id="chart"></div>
</div>
<div id="button-bar-wrapper">
    <div id="button-bar">
        <button type="button" class="flat-button" title="Go back" style="float: left;"
                onclick="history.go(-1);">
            <div class="fa fa-arrow-circle-left fa-lg"></div>
            <div class="text">Go back one page</div>
        </button>
        <button type="button" class="flat-button" title="Go home" style="float: right;"
                onclick="window.location.href='<%=baseUrl%>'">
            <div class="fa fa-home fa-lg"></div>
            <div class="text">Go to Germinate home</div>
        </button>
    </div>
</div>

<!-- Fallback stuff for noscript and IE < 9 -->
<div id="fallback-404" class="fallback-content">
    <h2>all right then, if it's resting I'll wake it up. Hello Polly! I've got a nice cuttlefish for
        you when you wake up, Polly parrot!</h2>

    <div>
        <p>Like this parrot the link that you just clicked on '<%= requestedPage %>' is dead.</p>

        <p>It's not pining, it's passed on. This page is no more. It has ceased to be. It's expired
            and gone to meet its maker.
            This is a late web page. It's a stiff. Bereft of life, it rests in peace. If you hadn't
            nailed it to the server,
            it would be pushing up the daisies. It's rung down the curtain and joined the choir
            invisible. This is an ex-page.</p>

        <p>If you are feeling particularly helpful you could even let us know about this problem on
            our email address
            <a href="mailto:germinate@hutton.ac.uk" style="color: gray;">germinate@hutton.ac.uk</a>
            just let us know what you were doing when this happened.</p>

        <p>A nod's as good as a wink to a blind bat, so follow one of the links below and we won't
            say any more about this indiscretion.</p>

        <img src="<%=baseUrl%>/error/dead-parrot.png" style="margin: auto; display: block;">

        <p>ps. he's only sleeping...</p>

    </div>
</div>
<div id="fallback-403" class="fallback-content">
    <h2>Follow! But follow only if ye be men of valour for access to this file is guarded by a
        creature...</h2>

    <div>
        <p>Like this parrot the link that you just clicked on '<%= requestedPage %>' is dead.</p>

        <p>...so foul, so cruel that no man yet has tried to get it and lived.</p>

        <p>Bones of 50 men lie strewn about its lair. So, brave web user if you do doubt your
            courage or your strength, come no further for death awaits you all with nasty, big,
            pointy teeth!</p>

        <p>What an eccentric performance! Follow one of the links below and we wont say any more
            about this.</p>

        <img src="<%=baseUrl%>/error/bunny.png" style="margin: auto; display: block;">

    </div>
</div>
<div id="fallback-401" class="fallback-content">
    <h2>I didn't expect a kind of Spanish Inquisition...</h2>

    <div>
        <p>Nobody expects the Spanish Inquisition!</p>

        <p>Our chief weapon is surprise...surprise and fear...fear and surprise.... Our two weapons
            are fear and surprise...and ruthless efficiency.... Our *three* weapons are fear,
            surprise, and ruthless efficiency...and an almost fanatical devotion to
            bioinformatics.... Our *four*...no... *Amongst* our weapons.... Amongst our
            weaponry...are such elements as fear, surprise....</p>

        <p>I'll come in again.</p>

        <p>Confess and follow one of the links below and we wont say any more about this
            indiscretion.</p>

        <img src="<%=baseUrl%>/error/inquisition.png" style="margin: auto; display: block;">
    </div>
</div>

<!--[if gte IE 9]>
<script type="text/javascript">doFancyStuff();</script>
<![endif]-->
<!--[if !IE]><!-->
<script type="text/javascript">doFancyStuff();</script>
<!--<![endif]-->

<!--[if lt IE 9]>
<script type="text/javascript">
    $('#fallback-<%=errorCode%>').css('display', 'block');
</script>
<![endif]-->
<noscript>
    <style type="text/css">
        #fallback-<%=errorCode%> {
            display: block;
        }
    </style>
</noscript>
</body>
</html>