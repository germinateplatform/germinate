<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="jhi.germinate.server.watcher.*" %>
<%@ page import="jhi.germinate.shared.*" %>
<%@ page import="jhi.germinate.shared.datastructure.*" %>
<%@ page import="jhi.germinate.shared.enums.*" %>

<%--
  ~  Copyright 2017 Information and Computational Sciences,
  ~  The James Hutton Institute.
  ~
  ~  Licensed under the Apache License, Version 2.0 (the "License");
  ~  you may not use this file except in compliance with the License.
  ~  You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~  Unless required by applicable law or agreed to in writing, software
  ~  distributed under the License is distributed on an "AS IS" BASIS,
  ~  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~  See the License for the specific language governing permissions and
  ~  limitations under the License.
  --%>

<%
	String pageTitle = PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_DATABASE_NAME);
	boolean debugModeEnabled = PropertyWatcher.getBoolean(ServerProperty.GERMINATE_DEBUG);
	boolean readOnlyModeEnebaled = PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY);
	String title = PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_TITLE);
	boolean logoContainsLink = PropertyWatcher.getBoolean(ServerProperty.GERMINATE_TEMPLATE_LOGO_CONTAINS_LINK);
	boolean useGoogleAnalytics = PropertyWatcher.getBoolean(ServerProperty.GOOGLE_ANALYTICS_ENABLED);
	String googleAnalyticsTrackingId = "'" + PropertyWatcher.get(ServerProperty.GOOGLE_ANALYTICS_TRACKING_ID) + "'";
	String contact = PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_EMAIL_ADDRESS);


	String version = "v3.19.4RC";
%>

<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<meta name="description" content="">
	<meta name="author" content="">

	<title><%= title %>
	</title>
	<link rel="shortcut icon" type="image/x-icon" href="favicon.ico"/>

	<!-- Bootstrap Core CSS -->
	<link href="css/bootstrap-germinate.css" rel="stylesheet">

	<!-- MetisMenu CSS -->
	<link href="css/metisMenu.min.css" rel="stylesheet">

	<!-- jQuery -->
	<script type="text/javascript" src="js/jquery-2.1.4.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui.min.js"></script>

	<!-- Lightbox -->
	<link type="text/css" rel="stylesheet" href="css/baguetteBox.css" media="screen"/>
	<script type="text/javascript" src="js/baguetteBox.js"></script>

	<!-- Peity -->
	<script type="text/javascript" src="js/jquery.peity.js"></script>

	<!-- Cookie policy notification -->
	<script type="text/javascript" src="js/jquery.cookie.js"></script>
	<script type="text/javascript" src="js/jquery.cookiecuttr.js"></script>
	<link type="text/css" rel="stylesheet" href="css/cookiecuttr.css"/>

	<!-- Custom Fonts -->
	<link href="css/materialdesignicons.min.css" rel="stylesheet" type="text/css">

	<!-- Intro.js -->
	<link rel="stylesheet" href="css/introjs.min.css"/>
	<script src="js/intro.min.js"></script>

	<% if (debugModeEnabled) { %>
		<!-- Code prettify -->
		<script type="text/javascript" src="js/prettify.js" defer="defer"></script>
		<script type="text/javascript" src="js/lang-sql.js" defer="defer"></script>
		<link rel="stylesheet" href="css/prettify.css"/>
	<% } %>

	<!-- Germinate -->
	<script type="text/javascript" src="germinate/germinate.nocache.js"></script>
	<link href="css/germinate-css.jsp" rel="stylesheet" type="text/css">

	<!-- Login screen -->
	<script type="text/javascript" src="js/hexagon-js.jsp"></script>
	<link href="css/hexagon.css" rel="stylesheet" type="text/css">

	<!-- Custom CSS -->
	<link type="text/css" rel="stylesheet" href="css/custom.css"/>

	<!-- Additional bootstrap column sizes -->
	<link href="css/bootstrap-xxs.css" rel="stylesheet">
	<!-- The base template -->
	<link href="css/template-css.jsp" rel="stylesheet">

	<% if (useGoogleAnalytics) { %>
		<!-- Google Analytics -->
		<script>
			(function (i, s, o, g, r, a, m) {
				i['GoogleAnalyticsObject'] = r;
				i[r] = i[r] || function () {
					(i[r].q = i[r].q || []).push(arguments)
				}, i[r].l = 1 * new Date();
				a = s.createElement(o),
					m = s.getElementsByTagName(o)[0];
				a.async = 1;
				a.src = g;
				m.parentNode.insertBefore(a, m)
			})(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

			ga('create', <%= googleAnalyticsTrackingId %>, 'auto');
			ga('send', 'pageview');
		</script>
	<% } %>

	<%
		// Custom html goes here, we just include it straight into the page
		String customHtml = request.getServletContext().getRealPath("/") + "custom.html";
		File file = new File(customHtml);

		if (file.exists())
		{
			try
			{
				BufferedReader br = new BufferedReader(new FileReader(file));
				for (String line = br.readLine(); line != null; line = br.readLine())
				{
					out.write(line);
					out.newLine();
				}
				br.close();
			}
			catch (IOException e)
			{
			}
		}
	%>
</head>

<body>

<!-- GWT history support -->
<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position: absolute; width: 0; height: 0; border: 0"></iframe>

<div id="<%= Id.STRUCTURE_PAGE %>" style="display: none;">
	<!-- Navigation -->
	<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a href='#<%= Page.HOME.name() %>' class='navbar-brand'>
				<img src='img/germinate.svg' class='logo'/>
			</a>
		</div>

		<ul class="nav navbar-top-links navbar-right">
			<!-- GM8 Language Selector -->
			<li class="dropdown" id="<%= Id.STRUCTURE_LANGUAGE_SELECTOR_UL %>" style="display: none;">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-translate fa-fw fa-lg"></i> <i class="mdi mdi-chevron-down"></i>
				</a>
			</li>
			<!-- /GM8 Language Selector -->
			<!-- GM8 Share Widget -->
			<li class="dropdown" id="<%= Id.STRUCTURE_SHARE_UL %>" style="display: none;">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-share-variant fa-fw fa-lg"></i> <i class="mdi mdi-chevron-down"></i>
				</a>
			</li>
			<!-- /GM8 Share Widget -->
			<!-- GM8 Shopping Cart -->
			<li class="dropdown" id="<%= Id.STRUCTURE_MARKED_ITEM_UL %>" style="display: none;">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-bookmark-check fa-fw fa-lg"></i> <i class="mdi mdi-chevron-down"></i>
				</a>
			</li>
			<!-- /GM8 Shopping Cart -->
			<!-- GM8 Account Settings -->
			<li class="dropdown" id="<%= Id.STRUCTURE_ACCOUNT_SETTINGS_UL %>" style="display: none;">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-account fa-fw fa-lg"></i> <i class="mdi mdi-chevron-down"></i>
				</a>
			</li>
			<!-- /GM8 Account Settings -->

			<% if (!StringUtils.isEmpty(contact)) { %>
				<!-- GM8 Contact -->
				<li>
					<a href="mailto:<%= contact %>" id="<%= Id.STRUCTURE_CONTACT_A %>">
						<i class="mdi mdi-email fa-fw fa-lg"></i>
					</a>
				</li>
				<!-- /GM8 Contact -->
			<% } %>
			<!-- GM8 Help -->
			<li>
				<a href="#" id="<%= Id.STRUCTURE_HELP_A %>" style="display: none;">
					<i class="mdi mdi-help-circle-outline fa-fw fa-lg"></i>
				</a>
			</li>
			<!-- /GM8 Help -->
		</ul>

		<div class="navbar-default sidebar" role="navigation">
			<div class="sidebar-nav navbar-collapse">
				<ul class="nav" id="<%= Id.STRUCTURE_MAIN_MENU_UL %>">
					<li id="<%= Id.STRUCTURE_VERSION_NUMBER %>"><%= version %>
					</li>
					<li class="sidebar-search" id="<%= Id.STRUCTURE_SEARCH_PANEL %>" style="display: none"></li>
				</ul>
				<ul class="nav <%= Style.LAYOUT_LOGO_SECTION %>">
					<li><a>
						<% if (logoContainsLink) { %>
							<object data="img/logo.svg" type="image/svg+xml"></object>
						<% } else { %>
							<img src="img/logo.svg"/>
						<% } %>
					</a></li>
				</ul>
			</div>
			<!-- /.sidebar-collapse -->
		</div>
		<!-- /.navbar-static-side -->
	</nav>

	<div id="content-wrapper">

		<%
			// Add a big header in read only mode
			if (readOnlyModeEnebaled)
			{
		%>
		<div id="<%= Id.STRUCTURE_READ_ONLY_BANNER %>" class="bg-primary"></div>
		<%
			}
		%>


		<div class="container-fluid">
			<div class="well well-sm row">
				<div class="col-xxs-12 col-xs-10 text-center-xxs text-left-xs">
					<h4><%= pageTitle %>
					</h4>
				</div>
				<div class="col-xxs-12 col-xs-2 text-center-xxs text-right-xs">
					<img src="img/crop.svg">
				</div>
			</div>
		</div>

		<!-- The div containing the debug information (if the mode is enabled) -->
		<div id="<%= Id.STRUCTURE_DEBUG_INFO %>"></div>
		<!-- The parallax page header -->
		<div id="<%= Id.STRUCTURE_PARALLAX %>"></div>
		<!-- The main page content -->
		<div id="<%= Id.STRUCTURE_MAIN_CONTENT %>"></div>
	</div>
	<!-- /#page-wrapper -->
</div>
<!-- /#wrapper -->

<div id="<%= Id.STRUCTURE_LOGIN %>" style="display: none;" class="<%= Style.NO_POINTER_EVENTS %>"></div>

<footer class="footer" id="<%= Id.STRUCTURE_FOOTER %>"></footer>

<!-- login fields used for auto completion of the browser (saved passwords) -->
<div style="display: none;">
	<form id="<%= Id.LOGIN_FORM %>" method="post" action="">
		<input type="text" id="<%= Id.LOGIN_USERNAME_INPUT %>" name="germinate-username" class="form-control" required="" autofocus=""/>
		<input type="password" id="<%= Id.LOGIN_PASSWORD_INPUT %>" name="germinate-password" class="form-control" required=""/>
	</form>
</div>

<!-- Bootstrap Core JavaScript -->
<script src="js/bootstrap.min.js"></script>

<!-- Metis Menu Plugin JavaScript -->
<script src="js/metisMenu.min.js"></script>

<!-- Custom Theme JavaScript -->
<script src="js/template-js.jsp"></script>

</body>

</html>
