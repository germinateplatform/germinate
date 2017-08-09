<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="jhi.germinate.server.config.*" %>
<%@ page import="jhi.germinate.shared.*" %>
<%@ page import="jhi.germinate.shared.enums.*" %>

<%--
  ~  Copyright 2017 Sebastian Raubach and Paul Shaw from the
  ~  Information and Computational Sciences Group at JHI Dundee
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
	String pageTitle = PropertyReader.get(ServerProperty.GERMINATE_TEMPLATE_DATABASE_NAME);
	boolean debugModeEnabled = PropertyReader.getBoolean(ServerProperty.GERMINATE_DEBUG);
	boolean readOnlyModeEnebaled = PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY);
	String title = PropertyReader.get(ServerProperty.GERMINATE_TEMPLATE_TITLE);
%>

<!DOCTYPE html>
<html lang="en">

<head>

	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
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

	<!-- Fancybox -->
	<link type="text/css" rel="stylesheet" href="css/jquery.fancybox.min.css" media="screen"/>
	<script type="text/javascript" src="js/jquery.fancybox.min.js"></script>

	<!-- Cookie policy notification -->
	<script type="text/javascript" src="js/jquery.cookie.js"></script>
	<script type="text/javascript" src="js/jquery.cookiecuttr.js"></script>
	<link type="text/css" rel="stylesheet" href="css/cookiecuttr.css"/>

	<!-- Custom Fonts -->
	<link href="css/font-awesome.min.css" rel="stylesheet" type="text/css">
	<link href="css/materialdesignicons.min.css" rel="stylesheet" type="text/css">

	<!-- Intro.js -->
	<link rel="stylesheet" href="css/introjs.min.css"/>
	<script src="js/intro.min.js"></script>

	<%
		if (debugModeEnabled)
		{
	%>
	<!-- Code prettify -->
	<script type="text/javascript" src="js/prettify.js" defer="defer"></script>
	<script type="text/javascript" src="js/lang-sql.js" defer="defer"></script>
	<link rel="stylesheet" href="css/prettify.css"/>
	<%
		}
	%>

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
	<script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
	<![endif]-->

	<script type="text/javascript" src="germinate/germinate.nocache.js"></script>
	<link href="css/germinate-css.jsp" rel="stylesheet" type="text/css">

	<link type="text/css" rel="stylesheet" href="css/custom.css"/>

	<!-- Custom CSS -->
	<link href="css/template-css.jsp" rel="stylesheet">

	<%
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

	<script type="text/javascript" src="js/hexagon-js.jsp"></script>
	<link href="css/hexagon.css" rel="stylesheet" type="text/css">
</head>

<body>

<!-- OPTIONAL: include this if you want history support -->
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
			<a href='#' class='navbar-brand'>
				<img src='img/germinate.svg' class='logo'/>
			</a>
		</div>

		<ul class="nav navbar-top-links navbar-right">
			<!-- GM8 Language Selector -->
			<li class="dropdown" id="<%= Id.STRUCTURE_LANGUAGE_SELECTOR_UL %>">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-translate fa-fw fa-lg"></i> <i class="fa fa-caret-down"></i>
				</a>
			</li>
			<!-- /GM8 Language Selector -->
			<!-- GM8 Share Widget -->
			<li class="dropdown" id="<%= Id.STRUCTURE_SHARE_UL %>">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-share-variant fa-fw fa-lg"></i> <i class="fa fa-caret-down"></i>
				</a>
			</li>
			<!-- /GM8 Share Widget -->
			<!-- GM8 Shopping Cart -->
			<li class="dropdown" id="<%= Id.STRUCTURE_SHOPPING_CART_UL %>">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-cart fa-fw fa-lg"></i> <i class="fa fa-caret-down"></i>
				</a>
			</li>
			<!-- /GM8 Shopping Cart -->
			<!-- GM8 Account Settings -->
			<li class="dropdown" id="<%= Id.STRUCTURE_ACCOUNT_SETTINGS_UL %>">
				<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="mdi mdi-account fa-fw fa-lg"></i> <i class="fa fa-caret-down"></i>
				</a>
			</li>
			<!-- /GM8 Account Settings -->
			<!-- GM8 Help -->
			<li>
				<a href="#" id="<%= Id.STRUCTURE_HELP_UL %>">
					<i class="mdi mdi-help-circle-outline fa-fw fa-lg"></i>
				</a>
			</li>
			<!-- /GM8 Help -->
		</ul>

		<div class="navbar-default sidebar" role="navigation">
			<div class="sidebar-nav navbar-collapse">
				<ul class="nav" id="<%= Id.STRUCTURE_MAIN_MENU_UL %>">
					<li class="sidebar-search" id="<%= Id.STRUCTURE_SEARCH_PANEL %>" style="display: none"></li>
				</ul>
				<ul class="nav <%= Style.LAYOUT_LOGO_SECTION %>">
					<li><a><img src="img/logo.svg"/></a></li>
				</ul>
			</div>
			<!-- /.sidebar-collapse -->
		</div>
		<!-- /.navbar-static-side -->
	</nav>

	<div id="content-wrapper">

		<%
			if (readOnlyModeEnebaled)
			{
		%>
		<div id="<%= Id.STRUCTURE_READ_ONLY_BANNER %>" class="bg-primary"></div>
		<%
			}
		%>

		<div id="<%= Id.STRUCTURE_BANNER %>" class="well well-sm clearfix"><h4 class="pull-left"><%= pageTitle %>
		</h4><img src="img/crop.svg" class="pull-right"/></div>
		<div id="<%= Id.STRUCTURE_DEBUG_INFO %>"></div>
		<div id="<%= Id.STRUCTURE_PARALLAX %>"></div>
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
