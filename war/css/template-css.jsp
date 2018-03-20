<%@ page  contentType ="text/css; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jhi.germinate.shared.*"%><%@ page import="jhi.germinate.shared.enums.*"%>

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

html {
  min-height: 100vh;
  position: relative;
}

body {
  background-color: #f8f8f8;
  min-height: calc(100vh - 40px);
  margin-bottom: 40px;
}

footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 40px;
  background-color: #eee;
  font-size: 12px;
}

footer > div.container,
footer > div.container-fluid {
  line-height: 40px;
}

button.mdi:empty:before {
  margin-right: 0;
}

.mdi.<%= Style.MDI_LG %>:before {
  font-size: 1.15em;
  line-height: .4em;
  vertical-align: -15%;
}

button.mdi:before {
  margin-right: 5px;
}

.navbar-toggle {
	height: 40px;
	margin: 10px;
	display: inline-block !important;
}

.<%= Style.TABLE_BORDER %> {
  border: 1px solid #ddd;
}

#<%= Id.STRUCTURE_LOGIN %> .navbar > .container .navbar-brand .logo,
#<%= Id.STRUCTURE_PAGE %> .navbar > .container .navbar-brand .logo {
 height: 50px;
 max-width: 100%;
 width: auto;
}

#<%= Id.STRUCTURE_LOGIN %> .navbar-brand,
#<%= Id.STRUCTURE_PAGE %> .navbar-brand {
	padding: 5px 15px;
    height: 60px;
}

#<%= Id.STRUCTURE_LOGIN %> {
  min-height: calc(100vh - 40px);
}

#<%= Id.STRUCTURE_READ_ONLY_BANNER %> {
  min-height: 50px;
  margin: -15px -15px 15px;
}

#<%= Id.STRUCTURE_READ_ONLY_BANNER %> p {
  text-align: center;
  line-height: 50px;
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

<%--#<%= Id.STRUCTURE_PAGE %>,--%>
<%--#<%= Id.STRUCTURE_LOGIN %> {--%>
  <%--width: 100%;--%>
  <%--height: 100%;--%>
<%--}--%>
#content-wrapper {
  padding: 15px;
  min-height: 568px;
  background-color: white;
}

<%--.<%= Style.MAPS_PANEL %> {--%>
  <%--margin-left: -15px;--%>
  <%--margin-right: -15px;--%>
<%--}--%>
@media (min-width: 992px) {

	.<%= Style.LAYOUT_SIDEBAR_TOGGLED %> .navbar-default.sidebar {
		width: 0;
		overflow-x: hidden;
	}

  #content-wrapper {
    position: inherit;
    margin: 0 0 0 250px;
    padding: 30px;
    border-left: 1px solid #e7e7e7;
  }

	.<%= Style.LAYOUT_SIDEBAR_TOGGLED %> #content-wrapper {
		margin: 0;
	}

  <%--.<%= Style.MAPS_PANEL %> {--%>
    <%--margin-left: -30px;--%>
    <%--margin-right: -30px;--%>
  <%--}--%>
}
.page-header {
  margin-top: 0;
}
.navbar-top-links {
  margin-right: 0;
}
.navbar-top-links li {
  display: inline-block;
}
#<%= Id.STRUCTURE_PAGE %> .navbar-top-links li:last-child {
  margin-right: 15px;
}
.navbar-top-links li a {
  padding: 15px;
  min-height: 60px;
  line-height: 30px;
}
.navbar-top-links .dropdown-menu li {
  display: block;
}
#<%= Id.STRUCTURE_PAGE %> .navbar-top-links .dropdown-menu li:last-child {
  margin-right: 0;
}
.navbar-top-links .dropdown-menu li a {
  padding: 3px 20px;
  min-height: 0;
}
.navbar-top-links .dropdown-menu li a div {
  white-space: normal;
}
.navbar-top-links .dropdown-messages,
.navbar-top-links .dropdown-tasks,
.navbar-top-links .dropdown-alerts {
  min-width: 0;
}
.navbar-top-links .dropdown-messages {
  margin-left: 5px;
}
.navbar-top-links .dropdown-tasks {
  margin-left: -59px;
}
.navbar-top-links .dropdown-user {
  right: 0;
  left: auto;
}
.sidebar .sidebar-nav.navbar-collapse {
  padding-left: 0;
  padding-right: 0;
}
.sidebar .sidebar-search {
  padding: 15px;
}
.sidebar ul li {
  border-bottom: 1px solid #e7e7e7;
}
.sidebar ul li a.active {
  background-color: #eeeeee;
}
.sidebar .arrow {
  float: right;
  line-height: 21px;
}
.sidebar .nav-second-level li,
.sidebar .nav-third-level li {
  border-bottom: none !important;
}
.sidebar .nav-second-level li a {
  padding-left: 37px;
}
.sidebar .nav-third-level li a {
  padding-left: 52px;
}
@media (min-width: 992px) {
  .navbar-top-static {
    min-height: 60px;
  }
  .sidebar {
    z-index: 1;
    position: absolute;
    width: 250px;
    margin-top: 61px;
  }
  .navbar-top-links .dropdown-messages,
  .navbar-top-links .dropdown-tasks,
  .navbar-top-links .dropdown-alerts {
    margin-left: auto;
  }
}
.btn-outline {
  color: inherit;
  background-color: transparent;
  transition: all .5s;
}
.btn-primary.btn-outline {
  color: #428bca;
}
.btn-success.btn-outline {
  color: #5cb85c;
}
.btn-info.btn-outline {
  color: #5bc0de;
}
.btn-warning.btn-outline {
  color: #f0ad4e;
}
.btn-danger.btn-outline {
  color: #d9534f;
}
.btn-primary.btn-outline:hover,
.btn-success.btn-outline:hover,
.btn-info.btn-outline:hover,
.btn-warning.btn-outline:hover,
.btn-danger.btn-outline:hover {
  color: white;
}

.bootstrap-switch {
  display: block;
}
.<%= Style.TABLE_CONTROL_PANEL %> .bootstrap-switch {
  display: inline-block;
}

.<%= Style.LAYOUT_NEWS_LIST %> {
  margin: 0;
  padding: 0;
  list-style: none;
}
.<%= Style.LAYOUT_NEWS_LIST %> li {
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px dotted #999999;
}
.<%= Style.LAYOUT_NEWS_LIST %> li li {
  margin-bottom: 0;
  border-bottom: 0;
}
.<%= Style.LAYOUT_NEWS_LIST %> li.left .<%= Style.LAYOUT_NEWS_LIST_ITEM_BODY %> {
  margin-left: 60px;
}
.<%= Style.LAYOUT_NEWS_LIST %> li.right .<%= Style.LAYOUT_NEWS_LIST_ITEM_BODY %> {
  margin-right: 60px;
}
.<%= Style.LAYOUT_NEWS_LIST %> li .<%= Style.LAYOUT_NEWS_LIST_ITEM_BODY %> p {
  margin: 0;
}
.panel .slidedown .glyphicon,
.<%= Style.LAYOUT_NEWS_LIST %> .glyphicon {
  margin-right: 5px;
}
.<%= Style.LAYOUT_NEWS_PANEL %> .<%= Style.LAYOUT_NEWS_LIST_ITEM_BODY %> {
  height: 350px;
  overflow-y: scroll;
}
.login-panel {
  margin-top: 25%;
}
.flot-chart {
  display: block;
  height: 400px;
}
.flot-chart-content {
  width: 100%;
  height: 100%;
}
table.dataTable thead .sorting,
table.dataTable thead .sorting_asc,
table.dataTable thead .sorting_desc,
table.dataTable thead .sorting_asc_disabled,
table.dataTable thead .sorting_desc_disabled {
  background: transparent;
}

.btn-circle {
  width: 30px;
  height: 30px;
  padding: 6px 0;
  border-radius: 15px;
  text-align: center;
  font-size: 12px;
  line-height: 1.428571429;
}
.btn-circle.btn-lg {
  width: 50px;
  height: 50px;
  padding: 10px 16px;
  border-radius: 25px;
  font-size: 18px;
  line-height: 1.33;
}
.btn-circle.btn-xl {
  width: 70px;
  height: 70px;
  padding: 10px 16px;
  border-radius: 35px;
  font-size: 24px;
  line-height: 1.33;
}
.show-grid [class^="col-"] {
  padding-top: 10px;
  padding-bottom: 10px;
  border: 1px solid #ddd;
  background-color: #eee !important;
}
.show-grid {
  margin: 15px 0;
}
.gm8-text-format-huge {
  font-size: 40px;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> {
  list-style: none;
  margin: 0;
  padding: 0 10px;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li {
  padding: 3px 0 3px 20px;
  margin: .4em 0;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.earth {
  background: url(../img/mime/earth.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.empty {
  background: url(../img/mime/empty.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.helium.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/helium.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.txt.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/txt.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.pdf.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/pdf.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.unknown.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/unknown.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.flapjack.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/flapjack.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.xlsx.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/xlsx.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.strudel.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/strudel.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.<%= FileType.mct.getStyle(FileType.IconStyle.IMAGE) %> {
  background: url(../img/mime/mct.png) no-repeat 0 50%;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.windows {
  background: url(../img/mime/windows.png) no-repeat 0 50%;
  padding: 3px 0 3px 35px;
  line-height: 2em;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.osx {
  background: url(../img/mime/osx.png) no-repeat 0 50%;
  padding: 3px 0 3px 35px;
  line-height: 2em;
}

ul.<%= Style.WIDGET_UL_ICON_LIST %> li.linux {
  background: url(../img/mime/linux.png) no-repeat 0 50%;
  padding: 3px 0 3px 35px;
  line-height: 2em;
}

@media (min-width:992px) {
  #<%= Id.STRUCTURE_READ_ONLY_BANNER %> {
  margin: -30px -30px 30px;
}

  #<%= Id.STRUCTURE_READ_ONLY_BANNER %> p {
    font-size: 20px;
  }
}

@media (min-width: 768px) {
  .navbar-left {
    float: left !important;
  }
  .navbar-right {
    float: initial !important;
    margin-right: 0px;
  }
  .navbar-right ~ .navbar-right {
    margin-right: 0;
  }
}

@media (min-width: 992px) {
  .navbar-left {
    float: left !important;
  }
  .navbar-right {
    float: right !important;
    margin-right: 0px;
  }
  .navbar-right ~ .navbar-right {
    margin-right: 0;
  }
}

@media (min-width: 768px) and (max-width: 991px) {
  .navbar-collapse.collapse {
    display: none !important;
  }
  .navbar-collapse.collapse.in {
    display: block !important;
  }
  .navbar-header .collapse, .navbar-toggle {
    display:block !important;
  }
  .navbar-header {
    float:none;
  }
}

@media (max-width: 479px) {
  .text-left-not-xxs, .text-center-not-xxs, .text-right-not-xxs, .text-justify-not-xxs {
    text-align: inherit;
  }
  .text-left-xxs {
    text-align: left;
  }
  .text-center-xxs {
    text-align: center;
  }
  .text-right-xxs {
    text-align: right;
  }
  .text-justify-xxs {
    text-align: justify;
  }
}

@media (min-width: 480px) {
  .text-left-not-xs, .text-center-not-xs, .text-right-not-xs, .text-justify-not-xs {
    text-align: inherit;
  }
  .text-left-xs {
    text-align: left;
  }
  .text-center-xs {
    text-align: center;
  }
  .text-right-xs {
    text-align: right;
  }
  .text-justify-xs {
    text-align: justify;
  }
}
@media (min-width: 768px) {
  .text-left-not-sm, .text-center-not-sm, .text-right-not-sm, .text-justify-not-sm {
    text-align: inherit;
  }
  .text-left-sm {
    text-align: left;
  }
  .text-center-sm {
    text-align: center;
  }
  .text-right-sm {
    text-align: right;
  }
  .text-justify-sm {
    text-align: justify;
  }
}
@media (min-width: 992px) {
  .text-left-not-md, .text-center-not-md, .text-right-not-md, .text-justify-not-md {
    text-align: inherit;
  }
  .text-left-md {
    text-align: left;
  }
  .text-center-md {
    text-align: center;
  }
  .text-right-md {
    text-align: right;
  }
  .text-justify-md {
    text-align: justify;
  }
}
@media (min-width: 1200px) {
  .text-left-not-lg, .text-center-not-lg, .text-right-not-lg, .text-justify-not-lg {
    text-align: inherit;
  }
  .text-left-lg {
    text-align: left;
  }
  .text-center-lg {
    text-align: center;
  }
  .text-right-lg {
    text-align: right;
  }
  .text-justify-lg {
    text-align: justify;
  }
}