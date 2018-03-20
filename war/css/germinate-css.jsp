<%@ page  contentType ="text/css; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jhi.germinate.server.config.*"%>
<%@ page import="jhi.germinate.server.service.*"%>
<%@ page import="jhi.germinate.shared.*"%>
<%@ page import="jhi.germinate.shared.enums.*"%>

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
	String highlightColor = CommonServiceImpl.getColors(PropertyReader.get(ServerProperty.GERMINATE_TEMPLATE_CATEGORICAL_COLORS)).get(0);
%>

.fa-lg {
	font-size: 1.33333333em;
	line-height: .75em;
	vertical-align: -15%;
}

.fa-fw {
	width: 1.28571429em;
	text-align: center;
}

.fa-4x {
	font-size: 4em;
}

input:-webkit-autofill, input:-webkit-autofill:focus, input:-webkit-autofill:active {
	-webkit-box-shadow: 0 0 0 1000px white inset;
}

html, body, .<%= Style.NO_POINTER_EVENTS %>{
	pointer-events: none;
}

body * {
	pointer-events: all;
}

td p {
	margin: 0;
}

td span.mdi:nth-child(2) {
	margin-left: 5px;
}

.dropdown-menu {
	z-index: 2000;
}

.modal-body dl {
	margin-bottom: 10px;
}

#<%= Id.STRUCTURE_LOGIN_BACKGROUND_SVG %> {
	position: fixed;
	top: 0;
	left: 0;
	z-index: -1;
}

.<%= Style.WIDGET_MAP_STATIC_OVERLAY %> {
	position: absolute;
	right: 0;
	top: 0;
	background-color: rgba(255, 255, 255, .7);
	border: 1px solid #6f7277;
	padding: 5px;
	pointer-events: none;
	z-index: 1000;
}

.<%= Style.WIDGET_MAP_STATIC_OVERLAY %> > img {
	vertical-align: middle;
}

.<%= Style.TAG_DUMMY_ANCHOR %> {
	cursor: pointer;
}

.<%= Style.TAG_DUMMY_ANCHOR %>:hover {
	text-decoration: underline;
}

.<%= Style.WIDGET_ICON_BUTTON %> {
	-webkit-transition: all 0.1s ease-in-out;
	-moz-transition: all 0.1s ease-in-out;
	-ms-transition: all 0.1s ease-in-out;
	-o-transition: all 0.1s ease-in-out;
	outline: none;
	line-height: 1 !important;
	/*font-size: 12pt !important;*/
	background: none;
	color: #727272;
	cursor: pointer;
	margin: 0 4px;
	border: 0;
	vertical-align: middle;
	text-align: center;
}

.<%= Style.WIDGET_ICON_BUTTON %>:hover {
	color: <%= highlightColor %>
}

#content-wrapper .well h4,
#<%= Id.STRUCTURE_LOGIN %> .well h4 {
	line-height: 30px
}
#content-wrapper .well img,
#<%= Id.STRUCTURE_LOGIN %> .well img{
	height: 50px;
}

.<%= Style.LAYOUT_TABLE_CELL_PADDING_PADDING %> {
	padding: 8px;
}

.<%= Style.LAYOUT_BUTTON_MARGIN %> {
	margin: 20px 0;
}

.<%= Style.LAYOUT_LOGO_SECTION %> {
	text-align: center;
}

.<%= Style.LAYOUT_LOGO_SECTION %> img,
.<%= Style.LAYOUT_LOGO_SECTION %> object {
	max-width: 100%;
	width: 300px;
}

.<%= Style.LAYOUT_V_ALIGN_MIDDLE %> {
	vertical-align: middle;
}

.<%= Style.LAYOUT_DISPLAY_INLINE_BLOCK %> {
	display: inline-block !important;
}

.<%= Style.LAYOUT_DISPLAY_NONE %> {
	display: none;
}

.<%= Style.CURSOR_DEFAULT %> {
	cursor: default;
}

.<%= Style.TEXT_BOLD %> {
	font-weight: bold;
}

.<%= Style.TEXT_ITALIC %> {
	font-style: italic;
}

.<%= Style.LAYOUT_WHITE_SPACE_NO_WRAP %> {
	white-space: nowrap;
}

.<%= Style.LAYOUT_NO_PADDING %> {
	padding: 0 !important;
}

.<%= Style.LAYOUT_NO_MARGIN %> {
	margin: 0 !important;
}

.<%= Style.TEXT_CENTER_ALIGN %> {
	text-align: center;
}

.<%= Style.TEXT_RIGHT_ALIGN %> {
	text-align: right !important;
}

.<%= Style.TEXT_RIGHT_ALIGN %> input {
	text-align: right !important;
}

.<%= Style.LAYOUT_NO_MARGIN_TOP %> {
	margin-top: 0;
}

.<%= Style.LAYOUT_FLOAT_INITIAL %> {
	float: initial !important;
}

.<%= Style.LAYOUT_NO_BORDER_LEFT %> {
	border-left: 0;
}

.<%= Style.COUNTRY_FLAG %> {
	background: url(../img/flags24.png) no-repeat 50% 50%;
	height: 24px;
	width: 24px;
	display: inline-block;
	vertical-align: middle;
	margin: 0 5px;
}

.<%= Style.TABLE_CONTROL_PANEL %> .badge {
	background-color: #777;
}

.<%= Style.TABLE_CONTROL_PANEL %>.top .pagination a,
.<%= Style.TABLE_CONTROL_PANEL %>.top .pagination span,
.<%= Style.TABLE_CONTROL_PANEL %>.top .btn {
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
	border-bottom: 0;
}

.<%= Style.TABLE_CONTROL_PANEL %>.bottom .pagination a,
.<%= Style.TABLE_CONTROL_PANEL %>.bottom .pagination span,
.<%= Style.TABLE_CONTROL_PANEL %>.bottom .btn {
	border-top-left-radius: 0;
	border-top-right-radius: 0;
	border-top: 0;
}

#<%= Id.STRUCTURE_VERSION_NUMBER %> {
	padding: 5px;
	text-align: center;
}

#<%= Id.STRUCTURE_MAIN_MENU_UL%> li a {
	text-transform: capitalize;
}

table th input:not([type=checkbox]) {
	min-width: 60px;
}

th {
	background-color: #f5f5f5;
}

.<%= Style.LAYOUT_OVERFLOW_X_AUTO%> {
	overflow-x: auto;
}

.<%= Style.LAYOUT_CLEAR_BOTH %> {
	clear: both;
}

.<%= Style.LAYOUT_VERTICAL_INPUT_GROUP %>:first-child {
	padding-bottom: 0;
}
.<%= Style.LAYOUT_VERTICAL_INPUT_GROUP %>:first-child * {
	border-bottom-left-radius: 0;
	border-bottom-right-radius: 0;
}
.<%= Style.LAYOUT_VERTICAL_INPUT_GROUP %>:last-child {
	padding-top: 0;
}
.<%= Style.LAYOUT_VERTICAL_INPUT_GROUP %>:last-child * {
	border-top-left-radius: 0;
	border-top-right-radius: 0;
}
.<%= Style.LAYOUT_VERTICAL_INPUT_GROUP %>:not(:last-child):not(:first-child) {
	padding-top: 0;
	padding-bottom: 0;
}
.<%= Style.LAYOUT_VERTICAL_INPUT_GROUP %>:not(:last-child):not(:first-child) * {
	border-radius: 0;
}

.<%= Style.LAYOUT_SELECT_BUTTON_COMBO %> select {
	border-bottom-left-radius: 0;
}

.<%= Style.LAYOUT_SELECT_BUTTON_COMBO %> .btn,
.<%= Style.TABLE_CONTROL_PANEL_BOTTOM %> .btn {
	border-top-left-radius: 0;
	border-top-right-radius: 0;
	border-top: 0;
}

.navbar-default li a > i:first-child,
.navbar-top-links li a > span:first-child,
.navbar-top-links li a > i:first-child {
	margin-right: 6px;
}

.navbar-default li a > i:only-child,
.navbar-top-links li a > span:only-child,
.navbar-top-links li a > i:only-child {
	margin-right: 0;
}

/* Loading indicator */
.<%= Style.WIDGET_BUSY_INDICATOR %> {
	box-sizing: border-box;
	display: inline-block;
	width: 100px;
	height: 100px;
	border: solid 4px transparent;
	border-top-color: <%= highlightColor %>;
	border-left-color: <%= highlightColor %>;
	border-radius: 100px;
	-webkit-animation: nprogress-spinner 1000ms linear infinite;
	-moz-animation: nprogress-spinner 1000ms linear infinite;
	-ms-animation: nprogress-spinner 1000ms linear infinite;
	-o-animation: nprogress-spinner 1000ms linear infinite;
	animation: nprogress-spinner 1000ms linear infinite;
	position: fixed;
	top: 50%;
	left: 50%;
	margin: -50px 0 0 -50px;
}

.relative {
	position: relative;
}

@-webkit-keyframes nprogress-spinner {
	0% {
		-webkit-transform: rotate(0deg);
		transform: rotate(0deg);
	}
	100% {
		-webkit-transform: rotate(360deg);
		transform: rotate(360deg);
	}
}

@-moz-keyframes nprogress-spinner {
	0% {
		-moz-transform: rotate(0deg);
		transform: rotate(0deg);
	}
	100% {
		-moz-transform: rotate(360deg);
		transform: rotate(360deg);
	}
}

@-o-keyframes nprogress-spinner {
	0% {
		-o-transform: rotate(0deg);
		transform: rotate(0deg);
	}
	100% {
		-o-transform: rotate(360deg);
		transform: rotate(360deg);
	}
}

@-ms-keyframes nprogress-spinner {
	0% {
		-ms-transform: rotate(0deg);
		transform: rotate(0deg);
	}
	100% {
		-ms-transform: rotate(360deg);
		transform: rotate(360deg);
	}
}

@keyframes nprogress-spinner {
	0% {
		-ms-transform: rotate(0deg);
		transform: rotate(0deg);
	}
	100% {
		-ms-transform: rotate(360deg);
		transform: rotate(360deg);
	}
}

/* Flags */

.<%= Style.COUNTRY_FLAG %>.su,
.<%= Style.COUNTRY_FLAG %>.unknown,
.<%= Style.COUNTRY_FLAG %>.un {
	background-position: 0 -408px;
}

.<%= Style.COUNTRY_FLAG %>.ad {
	background-position: 0 -456px;
}

.<%= Style.COUNTRY_FLAG %>.ar_AE,
.<%= Style.COUNTRY_FLAG %>.ae {
	background-position: 0 -480px;
}

.<%= Style.COUNTRY_FLAG %>.af {
	background-position: 0 -504px;
}

.<%= Style.COUNTRY_FLAG %>.ag {
	background-position: 0 -528px;
}

.<%= Style.COUNTRY_FLAG %>.ai {
	background-position: 0 -552px;
}

.<%= Style.COUNTRY_FLAG %>.sq_AL,
.<%= Style.COUNTRY_FLAG %>.al {
	background-position: 0 -576px;
}

.<%= Style.COUNTRY_FLAG %>.am {
	background-position: 0 -600px;
}

.<%= Style.COUNTRY_FLAG %>.an {
	background-position: 0 -624px;
}

.<%= Style.COUNTRY_FLAG %>.ao {
	background-position: 0 -648px;
}

.<%= Style.COUNTRY_FLAG %>.aq {
	background-position: 0 -672px;
}

.<%= Style.COUNTRY_FLAG %>.es_AR,
.<%= Style.COUNTRY_FLAG %>.ar {
	background-position: 0 -696px;
}

.<%= Style.COUNTRY_FLAG %>.as {
	background-position: 0 -720px;
}

.<%= Style.COUNTRY_FLAG %>.de_AT,
.<%= Style.COUNTRY_FLAG %>.at {
	background-position: 0 -744px;
}

.<%= Style.COUNTRY_FLAG %>.en_AU,
.<%= Style.COUNTRY_FLAG %>.au {
	background-position: 0 -768px;
}

.<%= Style.COUNTRY_FLAG %>.aw {
	background-position: 0 -792px;
}

.<%= Style.COUNTRY_FLAG %>.ax {
	background-position: 0 -816px;
}

.<%= Style.COUNTRY_FLAG %>.az {
	background-position: 0 -840px;
}

.<%= Style.COUNTRY_FLAG %>.sr_BA,
.<%= Style.COUNTRY_FLAG %>.ba {
	background-position: 0 -864px;
}

.<%= Style.COUNTRY_FLAG %>.bb {
	background-position: 0 -888px;
}

.<%= Style.COUNTRY_FLAG %>.bd {
	background-position: 0 -912px;
}

.<%= Style.COUNTRY_FLAG %>.nl_BE,
.<%= Style.COUNTRY_FLAG %>.fr_BE,
.<%= Style.COUNTRY_FLAG %>.be {
	background-position: 0 -936px;
}

.<%= Style.COUNTRY_FLAG %>.bf {
	background-position: 0 -960px;
}

.<%= Style.COUNTRY_FLAG %>.bg_BG,
.<%= Style.COUNTRY_FLAG %>.bg {
	background-position: 0 -984px;
}

.<%= Style.COUNTRY_FLAG %>.ar_BH,
.<%= Style.COUNTRY_FLAG %>.bh {
	background-position: 0 -1008px;
}

.<%= Style.COUNTRY_FLAG %>.bi {
	background-position: 0 -1032px;
}

.<%= Style.COUNTRY_FLAG %>.bj {
	background-position: 0 -1056px;
}

.<%= Style.COUNTRY_FLAG %>.bl {
	background-position: 0 -1080px;
}

.<%= Style.COUNTRY_FLAG %>.bm {
	background-position: 0 -1104px;
}

.<%= Style.COUNTRY_FLAG %>.bn {
	background-position: 0 -1128px;
}

.<%= Style.COUNTRY_FLAG %>.es_BO,
.<%= Style.COUNTRY_FLAG %>.bo {
	background-position: 0 -1152px;
}

.<%= Style.COUNTRY_FLAG %>.pt_BR,
.<%= Style.COUNTRY_FLAG %>.br {
	background-position: 0 -1176px;
}

.<%= Style.COUNTRY_FLAG %>.bs {
	background-position: 0 -1200px;
}

.<%= Style.COUNTRY_FLAG %>.bt {
	background-position: 0 -1224px;
}

.<%= Style.COUNTRY_FLAG %>.bw {
	background-position: 0 -1248px;
}

.<%= Style.COUNTRY_FLAG %>.be_BY,
.<%= Style.COUNTRY_FLAG %>.by {
	background-position: 0 -1272px;
}

.<%= Style.COUNTRY_FLAG %>.bz {
	background-position: 0 -1296px;
}

.<%= Style.COUNTRY_FLAG %>.en_CA,
.<%= Style.COUNTRY_FLAG %>.fr_CA,
.<%= Style.COUNTRY_FLAG %>.ca {
	background-position: 0 -1320px;
}

.<%= Style.COUNTRY_FLAG %>.cc {
	background-position: 0 -1344px;
}

.<%= Style.COUNTRY_FLAG %>.cd {
	background-position: 0 -1368px;
}

.<%= Style.COUNTRY_FLAG %>.cf {
	background-position: 0 -1392px;
}

.<%= Style.COUNTRY_FLAG %>.cg {
	background-position: 0 -1416px;
}

.<%= Style.COUNTRY_FLAG %>.fr_CH,
.<%= Style.COUNTRY_FLAG %>.de_CH,
.<%= Style.COUNTRY_FLAG %>.it_CH,
.<%= Style.COUNTRY_FLAG %>.ch {
	background-position: 0 -1440px;
}

.<%= Style.COUNTRY_FLAG %>.ci {
	background-position: 0 -1464px;
}

.<%= Style.COUNTRY_FLAG %>.ck {
	background-position: 0 -1488px;
}

.<%= Style.COUNTRY_FLAG %>.es_CL,
.<%= Style.COUNTRY_FLAG %>.cl {
	background-position: 0 -1512px;
}

.<%= Style.COUNTRY_FLAG %>.cm {
	background-position: 0 -1536px;
}

.<%= Style.COUNTRY_FLAG %>.zh_CN,
.<%= Style.COUNTRY_FLAG %>.cn {
	background-position: 0 -1560px;
}

.<%= Style.COUNTRY_FLAG %>.es_CO,
.<%= Style.COUNTRY_FLAG %>.co {
	background-position: 0 -1584px;
}

.<%= Style.COUNTRY_FLAG %>.es_CR,
.<%= Style.COUNTRY_FLAG %>.cr {
	background-position: 0 -1608px;
}

.<%= Style.COUNTRY_FLAG %>.cu {
	background-position: 0 -1632px;
}

.<%= Style.COUNTRY_FLAG %>.cv {
	background-position: 0 -1656px;
}

.<%= Style.COUNTRY_FLAG %>.cw {
	background-position: 0 -1680px;
}

.<%= Style.COUNTRY_FLAG %>.cx {
	background-position: 0 -1704px;
}

.<%= Style.COUNTRY_FLAG %>.el_CY,
.<%= Style.COUNTRY_FLAG %>.cy {
	background-position: 0 -1728px;
}

.<%= Style.COUNTRY_FLAG %>.cs_CZ,
.<%= Style.COUNTRY_FLAG %>.cz {
	background-position: 0 -1752px;
}

.<%= Style.COUNTRY_FLAG %>.de_DE,
.<%= Style.COUNTRY_FLAG %>.de {
	background-position: 0 -1776px;
}

.<%= Style.COUNTRY_FLAG %>.dj {
	background-position: 0 -1800px;
}

.<%= Style.COUNTRY_FLAG %>.da_DK,
.<%= Style.COUNTRY_FLAG %>.dk {
	background-position: 0 -1824px;
}

.<%= Style.COUNTRY_FLAG %>.dm {
	background-position: 0 -1848px;
}

.<%= Style.COUNTRY_FLAG %>.es_DO,
.<%= Style.COUNTRY_FLAG %>.do {
	background-position: 0 -1872px;
}

.<%= Style.COUNTRY_FLAG %>.ar_DZ,
.<%= Style.COUNTRY_FLAG %>.dz {
	background-position: 0 -1896px;
}

.<%= Style.COUNTRY_FLAG %>.es_EC,
.<%= Style.COUNTRY_FLAG %>.ec {
	background-position: 0 -1920px;
}

.<%= Style.COUNTRY_FLAG %>.et_EE,
.<%= Style.COUNTRY_FLAG %>.ee {
	background-position: 0 -1944px;
}

.<%= Style.COUNTRY_FLAG %>.ar_EG,
.<%= Style.COUNTRY_FLAG %>.eg {
	background-position: 0 -1968px;
}

.<%= Style.COUNTRY_FLAG %>.eh {
	background-position: 0 -1992px;
}

.<%= Style.COUNTRY_FLAG %>.er {
	background-position: 0 -2016px;
}

.<%= Style.COUNTRY_FLAG %>.ca_ES,
.<%= Style.COUNTRY_FLAG %>.es_ES,
.<%= Style.COUNTRY_FLAG %>.es {
	background-position: 0 -2040px;
}

.<%= Style.COUNTRY_FLAG %>.et {
	background-position: 0 -2064px;
}

.<%= Style.COUNTRY_FLAG %>.eu {
	background-position: 0 -2088px;
}

.<%= Style.COUNTRY_FLAG %>.fi_FI,
.<%= Style.COUNTRY_FLAG %>.fi {
	background-position: 0 -2112px;
}

.<%= Style.COUNTRY_FLAG %>.fj {
	background-position: 0 -2136px;
}

.<%= Style.COUNTRY_FLAG %>.fk {
	background-position: 0 -2160px;
}

.<%= Style.COUNTRY_FLAG %>.fm {
	background-position: 0 -2184px;
}

.<%= Style.COUNTRY_FLAG %>.fo {
	background-position: 0 -2208px;
}

.<%= Style.COUNTRY_FLAG %>.fr_FR,
.<%= Style.COUNTRY_FLAG %>.fr {
	background-position: 0 -2232px;
}

.<%= Style.COUNTRY_FLAG %>.ga {
	background-position: 0 -2256px;
}

.<%= Style.COUNTRY_FLAG %>.en_GB,
.<%= Style.COUNTRY_FLAG %>.en,
.<%= Style.COUNTRY_FLAG %>.gb {
	background-position: 0 -2280px;
}

.<%= Style.COUNTRY_FLAG %>.gd {
	background-position: 0 -2304px;
}

.<%= Style.COUNTRY_FLAG %>.ge {
	background-position: 0 -2328px;
}

.<%= Style.COUNTRY_FLAG %>.gg {
	background-position: 0 -2352px;
}

.<%= Style.COUNTRY_FLAG %>.gh {
	background-position: 0 -2376px;
}

.<%= Style.COUNTRY_FLAG %>.gi {
	background-position: 0 -2400px;
}

.<%= Style.COUNTRY_FLAG %>.gl {
	background-position: 0 -2424px;
}

.<%= Style.COUNTRY_FLAG %>.gm {
	background-position: 0 -2448px;
}

.<%= Style.COUNTRY_FLAG %>.gn {
	background-position: 0 -2472px;
}

.<%= Style.COUNTRY_FLAG %>.gq {
	background-position: 0 -2496px;
}

.<%= Style.COUNTRY_FLAG %>.el_GR,
.<%= Style.COUNTRY_FLAG %>.gr {
	background-position: 0 -2520px;
}

.<%= Style.COUNTRY_FLAG %>.gs {
	background-position: 0 -2544px;
}

.<%= Style.COUNTRY_FLAG %>.es_GT,
.<%= Style.COUNTRY_FLAG %>.gt {
	background-position: 0 -2568px;
}

.<%= Style.COUNTRY_FLAG %>.gu {
	background-position: 0 -2592px;
}

.<%= Style.COUNTRY_FLAG %>.gw {
	background-position: 0 -2616px;
}

.<%= Style.COUNTRY_FLAG %>.gy {
	background-position: 0 -2640px;
}

.<%= Style.COUNTRY_FLAG %>.zh_HK,
.<%= Style.COUNTRY_FLAG %>.hk {
	background-position: 0 -2664px;
}

.<%= Style.COUNTRY_FLAG %>.es_HN,
.<%= Style.COUNTRY_FLAG %>.hn {
	background-position: 0 -2688px;
}

.<%= Style.COUNTRY_FLAG %>.hr_HR,
.<%= Style.COUNTRY_FLAG %>.hr {
	background-position: 0 -2712px;
}

.<%= Style.COUNTRY_FLAG %>.ht {
	background-position: 0 -2736px;
}

.<%= Style.COUNTRY_FLAG %>.hu_HU,
.<%= Style.COUNTRY_FLAG %>.hu {
	background-position: 0 -2760px;
}

.<%= Style.COUNTRY_FLAG %>.ic {
	background-position: 0 -2784px;
}

.<%= Style.COUNTRY_FLAG %>.in_ID,
.<%= Style.COUNTRY_FLAG %>.id {
	background-position: 0 -2808px;
}

.<%= Style.COUNTRY_FLAG %>.en_IE,
.<%= Style.COUNTRY_FLAG %>.ga_IE,
.<%= Style.COUNTRY_FLAG %>.ie {
	background-position: 0 -2832px;
}

.<%= Style.COUNTRY_FLAG %>.iw_IL,
.<%= Style.COUNTRY_FLAG %>.il {
	background-position: 0 -2856px;
}

.<%= Style.COUNTRY_FLAG %>.im {
	background-position: 0 -2880px;
}

.<%= Style.COUNTRY_FLAG %>.en_IN,
.<%= Style.COUNTRY_FLAG %>.hi_IN,
.<%= Style.COUNTRY_FLAG %>.in {
	background-position: 0 -2904px;
}
.<%= Style.COUNTRY_FLAG %>.ar_IQ,
.<%= Style.COUNTRY_FLAG %>.iq {
	background-position: 0 -2928px;
}

.<%= Style.COUNTRY_FLAG %>.ir {
	background-position: 0 -2952px;
}

.<%= Style.COUNTRY_FLAG %>.is_IS,
.<%= Style.COUNTRY_FLAG %>.is {
	background-position: 0 -2976px;
}

.<%= Style.COUNTRY_FLAG %>.it_IT,
.<%= Style.COUNTRY_FLAG %>.it {
	background-position: 0 -3000px;
}

.<%= Style.COUNTRY_FLAG %>.je {
	background-position: 0 -3024px;
}

.<%= Style.COUNTRY_FLAG %>.jm {
	background-position: 0 -3048px;
}

.<%= Style.COUNTRY_FLAG %>.ar_JO,
.<%= Style.COUNTRY_FLAG %>.jo {
	background-position: 0 -3072px;
}

.<%= Style.COUNTRY_FLAG %>.ja_JP,
.<%= Style.COUNTRY_FLAG %>.jp {
	background-position: 0 -3096px;
}

.<%= Style.COUNTRY_FLAG %>.ke {
	background-position: 0 -3120px;
}

.<%= Style.COUNTRY_FLAG %>.kg {
	background-position: 0 -3144px;
}

.<%= Style.COUNTRY_FLAG %>.kh {
	background-position: 0 -3168px;
}

.<%= Style.COUNTRY_FLAG %>.ki {
	background-position: 0 -3192px;
}

.<%= Style.COUNTRY_FLAG %>.km {
	background-position: 0 -3216px;
}

.<%= Style.COUNTRY_FLAG %>.kn {
	background-position: 0 -3240px;
}

.<%= Style.COUNTRY_FLAG %>.kp {
	background-position: 0 -3264px;
}

.<%= Style.COUNTRY_FLAG %>.ko_KR,
.<%= Style.COUNTRY_FLAG %>.kr {
	background-position: 0 -3288px;
}

.<%= Style.COUNTRY_FLAG %>.ar_KW,
.<%= Style.COUNTRY_FLAG %>.kw {
	background-position: 0 -3312px;
}

.<%= Style.COUNTRY_FLAG %>.ky {
	background-position: 0 -3336px;
}

.<%= Style.COUNTRY_FLAG %>.kz {
	background-position: 0 -3360px;
}

.<%= Style.COUNTRY_FLAG %>.la {
	background-position: 0 -3384px;
}

.<%= Style.COUNTRY_FLAG %>.ar_LB,
.<%= Style.COUNTRY_FLAG %>.lb {
	background-position: 0 -3408px;
}

.<%= Style.COUNTRY_FLAG %>.lc {
	background-position: 0 -3432px;
}

.<%= Style.COUNTRY_FLAG %>.li {
	background-position: 0 -3456px;
}

.<%= Style.COUNTRY_FLAG %>.lk {
	background-position: 0 -3480px;
}

.<%= Style.COUNTRY_FLAG %>.lr {
	background-position: 0 -3504px;
}

.<%= Style.COUNTRY_FLAG %>.ls {
	background-position: 0 -3528px;
}

.<%= Style.COUNTRY_FLAG %>.lt_LT,
.<%= Style.COUNTRY_FLAG %>.lt {
	background-position: 0 -3552px;
}

.<%= Style.COUNTRY_FLAG %>.fr_LU,
.<%= Style.COUNTRY_FLAG %>.de_LU,
.<%= Style.COUNTRY_FLAG %>.lu {
	background-position: 0 -3576px;
}

.<%= Style.COUNTRY_FLAG %>.lv_LV,
.<%= Style.COUNTRY_FLAG %>.lv {
	background-position: 0 -3600px;
}

.<%= Style.COUNTRY_FLAG %>.ar_LY,
.<%= Style.COUNTRY_FLAG %>.ly {
	background-position: 0 -3624px;
}

.<%= Style.COUNTRY_FLAG %>.ar_MA,
.<%= Style.COUNTRY_FLAG %>.ma {
	background-position: 0 -3648px;
}

.<%= Style.COUNTRY_FLAG %>.mc {
	background-position: 0 -3672px;
}

.<%= Style.COUNTRY_FLAG %>.md {
	background-position: 0 -3696px;
}

.<%= Style.COUNTRY_FLAG %>.sr_ME,
.<%= Style.COUNTRY_FLAG %>.me {
	background-position: 0 -3720px;
}

.<%= Style.COUNTRY_FLAG %>.mf {
	background-position: 0 -3744px;
}

.<%= Style.COUNTRY_FLAG %>.mg {
	background-position: 0 -3768px;
}

.<%= Style.COUNTRY_FLAG %>.mh {
	background-position: 0 -3792px;
}

.<%= Style.COUNTRY_FLAG %>.mk_MK,
.<%= Style.COUNTRY_FLAG %>.mk {
	background-position: 0 -3816px;
}

.<%= Style.COUNTRY_FLAG %>.ml {
	background-position: 0 -3840px;
}

.<%= Style.COUNTRY_FLAG %>.mm {
	background-position: 0 -3864px;
}

.<%= Style.COUNTRY_FLAG %>.mn {
	background-position: 0 -3888px;
}

.<%= Style.COUNTRY_FLAG %>.mo {
	background-position: 0 -3912px;
}

.<%= Style.COUNTRY_FLAG %>.mp {
	background-position: 0 -3936px;
}

.<%= Style.COUNTRY_FLAG %>.mq {
	background-position: 0 -3960px;
}

.<%= Style.COUNTRY_FLAG %>.mr {
	background-position: 0 -3984px;
}

.<%= Style.COUNTRY_FLAG %>.ms {
	background-position: 0 -4008px;
}

.<%= Style.COUNTRY_FLAG %>.en_MT,
.<%= Style.COUNTRY_FLAG %>.mt_MT,
.<%= Style.COUNTRY_FLAG %>.mt {
	background-position: 0 -4032px;
}

.<%= Style.COUNTRY_FLAG %>.mu {
	background-position: 0 -4056px;
}

.<%= Style.COUNTRY_FLAG %>.mv {
	background-position: 0 -4080px;
}

.<%= Style.COUNTRY_FLAG %>.mw {
	background-position: 0 -4104px;
}

.<%= Style.COUNTRY_FLAG %>.es_MX,
.<%= Style.COUNTRY_FLAG %>.mx {
	background-position: 0 -4128px;
}

.<%= Style.COUNTRY_FLAG %>.ms_MY,
.<%= Style.COUNTRY_FLAG %>.my {
	background-position: 0 -4152px;
}

.<%= Style.COUNTRY_FLAG %>.mz {
	background-position: 0 -4176px;
}

.<%= Style.COUNTRY_FLAG %>.na {
	background-position: 0 -4200px;
}

.<%= Style.COUNTRY_FLAG %>.nc {
	background-position: 0 -4224px;
}

.<%= Style.COUNTRY_FLAG %>.ne {
	background-position: 0 -4248px;
}

.<%= Style.COUNTRY_FLAG %>.nf {
	background-position: 0 -4272px;
}

.<%= Style.COUNTRY_FLAG %>.ng {
	background-position: 0 -4296px;
}

.<%= Style.COUNTRY_FLAG %>.es_NI,
.<%= Style.COUNTRY_FLAG %>.ni {
	background-position: 0 -4320px;
}

.<%= Style.COUNTRY_FLAG %>.nl_NL,
.<%= Style.COUNTRY_FLAG %>.nl {
	background-position: 0 -4344px;
}

.<%= Style.COUNTRY_FLAG %>.no_NO,
.<%= Style.COUNTRY_FLAG %>.nb_NO,
.<%= Style.COUNTRY_FLAG %>.nn_NO,
.<%= Style.COUNTRY_FLAG %>.no {
	background-position: 0 -4368px;
}

.<%= Style.COUNTRY_FLAG %>.np {
	background-position: 0 -4392px;
}

.<%= Style.COUNTRY_FLAG %>.nr {
	background-position: 0 -4416px;
}

.<%= Style.COUNTRY_FLAG %>.nu {
	background-position: 0 -4440px;
}

.<%= Style.COUNTRY_FLAG %>.en_NZ,
.<%= Style.COUNTRY_FLAG %>.nz {
	background-position: 0 -4464px;
}

.<%= Style.COUNTRY_FLAG %>.ar_OM,
.<%= Style.COUNTRY_FLAG %>.om {
	background-position: 0 -4488px;
}

.<%= Style.COUNTRY_FLAG %>.es_PA,
.<%= Style.COUNTRY_FLAG %>.pa {
	background-position: 0 -4512px;
}

.<%= Style.COUNTRY_FLAG %>.es_PE,
.<%= Style.COUNTRY_FLAG %>.pe {
	background-position: 0 -4536px;
}

.<%= Style.COUNTRY_FLAG %>.pf {
	background-position: 0 -4560px;
}

.<%= Style.COUNTRY_FLAG %>.pg {
	background-position: 0 -4584px;
}

.<%= Style.COUNTRY_FLAG %>.en_PH,
.<%= Style.COUNTRY_FLAG %>.ph {
	background-position: 0 -4608px;
}

.<%= Style.COUNTRY_FLAG %>.pk {
	background-position: 0 -4632px;
}

.<%= Style.COUNTRY_FLAG %>.pl_PL,
.<%= Style.COUNTRY_FLAG %>.pl {
	background-position: 0 -4656px;
}

.<%= Style.COUNTRY_FLAG %>.pn {
	background-position: 0 -4680px;
}

.<%= Style.COUNTRY_FLAG %>.es_PR,
.<%= Style.COUNTRY_FLAG %>.pr {
	background-position: 0 -4704px;
}

.<%= Style.COUNTRY_FLAG %>.ps {
	background-position: 0 -4728px;
}

.<%= Style.COUNTRY_FLAG %>.pt_PT,
.<%= Style.COUNTRY_FLAG %>.pt {
	background-position: 0 -4752px;
}

.<%= Style.COUNTRY_FLAG %>.pw {
	background-position: 0 -4776px;
}

.<%= Style.COUNTRY_FLAG %>.es_PY,
.<%= Style.COUNTRY_FLAG %>.py {
	background-position: 0 -4800px;
}

.<%= Style.COUNTRY_FLAG %>.ar_QA,
.<%= Style.COUNTRY_FLAG %>.qa {
	background-position: 0 -4824px;
}

.<%= Style.COUNTRY_FLAG %>.ro_RO,
.<%= Style.COUNTRY_FLAG %>.ro {
	background-position: 0 -4848px;
}

.<%= Style.COUNTRY_FLAG %>.sr_RS,
.<%= Style.COUNTRY_FLAG %>.rs {
	background-position: 0 -4872px;
}

.<%= Style.COUNTRY_FLAG %>.ru_RU,
.<%= Style.COUNTRY_FLAG %>.ru {
	background-position: 0 -4896px;
}

.<%= Style.COUNTRY_FLAG %>.rw {
	background-position: 0 -4920px;
}

.<%= Style.COUNTRY_FLAG %>.ar_SA,
.<%= Style.COUNTRY_FLAG %>.sa {
	background-position: 0 -4944px;
}

.<%= Style.COUNTRY_FLAG %>.sb {
	background-position: 0 -4968px;
}

.<%= Style.COUNTRY_FLAG %>.sc {
	background-position: 0 -4992px;
}

.<%= Style.COUNTRY_FLAG %>.ar_SD,
.<%= Style.COUNTRY_FLAG %>.sd {
	background-position: 0 -5016px;
}

.<%= Style.COUNTRY_FLAG %>.sv_SE,
.<%= Style.COUNTRY_FLAG %>.se {
	background-position: 0 -5040px;
}

.<%= Style.COUNTRY_FLAG %>.zh_SG,
.<%= Style.COUNTRY_FLAG %>.en_SG,
.<%= Style.COUNTRY_FLAG %>.sg {
	background-position: 0 -5064px;
}

.<%= Style.COUNTRY_FLAG %>.sh {
	background-position: 0 -5088px;
}

.<%= Style.COUNTRY_FLAG %>.sl_SI,
.<%= Style.COUNTRY_FLAG %>.si {
	background-position: 0 -5112px;
}

.<%= Style.COUNTRY_FLAG %>.sk_SK,
.<%= Style.COUNTRY_FLAG %>.sk {
	background-position: 0 -5136px;
}

.<%= Style.COUNTRY_FLAG %>.sl {
	background-position: 0 -5160px;
}

.<%= Style.COUNTRY_FLAG %>.sm {
	background-position: 0 -5184px;
}

.<%= Style.COUNTRY_FLAG %>.sn {
	background-position: 0 -5208px;
}

.<%= Style.COUNTRY_FLAG %>.so {
	background-position: 0 -5232px;
}

.<%= Style.COUNTRY_FLAG %>.sr {
	background-position: 0 -5256px;
}

.<%= Style.COUNTRY_FLAG %>.ss {
	background-position: 0 -5280px;
}

.<%= Style.COUNTRY_FLAG %>.st {
	background-position: 0 -5304px;
}

.<%= Style.COUNTRY_FLAG %>.es_SV,
.<%= Style.COUNTRY_FLAG %>.sv {
	background-position: 0 -5328px;
}

.<%= Style.COUNTRY_FLAG %>.ar_SY,
.<%= Style.COUNTRY_FLAG %>.sy {
	background-position: 0 -5352px;
}

.<%= Style.COUNTRY_FLAG %>.sz {
	background-position: 0 -5376px;
}

.<%= Style.COUNTRY_FLAG %>.tc {
	background-position: 0 -5400px;
}

.<%= Style.COUNTRY_FLAG %>.td {
	background-position: 0 -5424px;
}

.<%= Style.COUNTRY_FLAG %>.tf {
	background-position: 0 -5448px;
}

.<%= Style.COUNTRY_FLAG %>.tg {
	background-position: 0 -5472px;
}

.<%= Style.COUNTRY_FLAG %>.th_TH,
.<%= Style.COUNTRY_FLAG %>.th {
	background-position: 0 -5496px;
}

.<%= Style.COUNTRY_FLAG %>.tj {
	background-position: 0 -5520px;
}

.<%= Style.COUNTRY_FLAG %>.tk {
	background-position: 0 -5544px;
}

.<%= Style.COUNTRY_FLAG %>.tl {
	background-position: 0 -5568px;
}

.<%= Style.COUNTRY_FLAG %>.tm {
	background-position: 0 -5592px;
}

.<%= Style.COUNTRY_FLAG %>.ar_TN,
.<%= Style.COUNTRY_FLAG %>.tn {
	background-position: 0 -5616px;
}

.<%= Style.COUNTRY_FLAG %>.to {
	background-position: 0 -5640px;
}

.<%= Style.COUNTRY_FLAG %>.tr_TR,
.<%= Style.COUNTRY_FLAG %>.tr {
	background-position: 0 -5664px;
}

.<%= Style.COUNTRY_FLAG %>.tt {
	background-position: 0 -5688px;
}

.<%= Style.COUNTRY_FLAG %>.tv {
	background-position: 0 -5712px;
}

.<%= Style.COUNTRY_FLAG %>.zh_TW,
.<%= Style.COUNTRY_FLAG %>.tw {
	background-position: 0 -5736px;
}

.<%= Style.COUNTRY_FLAG %>.tz {
	background-position: 0 -5760px;
}

.<%= Style.COUNTRY_FLAG %>.uk_UA,
.<%= Style.COUNTRY_FLAG %>.ua {
	background-position: 0 -5784px;
}

.<%= Style.COUNTRY_FLAG %>.ug {
	background-position: 0 -5808px;
}

.<%= Style.COUNTRY_FLAG %>.en_US,
.<%= Style.COUNTRY_FLAG %>.es_US,
.<%= Style.COUNTRY_FLAG %>.us {
	background-position: 0 -5832px;
}

.<%= Style.COUNTRY_FLAG %>.es_UY,
.<%= Style.COUNTRY_FLAG %>.uy {
	background-position: 0 -5856px;
}

.<%= Style.COUNTRY_FLAG %>.uz {
	background-position: 0 -5880px;
}

.<%= Style.COUNTRY_FLAG %>.va {
	background-position: 0 -5904px;
}

.<%= Style.COUNTRY_FLAG %>.vc {
	background-position: 0 -5928px;
}

.<%= Style.COUNTRY_FLAG %>.es_VE,
.<%= Style.COUNTRY_FLAG %>.ve {
	background-position: 0 -5952px;
}

.<%= Style.COUNTRY_FLAG %>.vg {
	background-position: 0 -5976px;
}

.<%= Style.COUNTRY_FLAG %>.vi {
	background-position: 0 -6000px;
}

.<%= Style.COUNTRY_FLAG %>.vi_VN,
.<%= Style.COUNTRY_FLAG %>.vn {
	background-position: 0 -6024px;
}

.<%= Style.COUNTRY_FLAG %>.vu {
	background-position: 0 -6048px;
}

.<%= Style.COUNTRY_FLAG %>.wf {
	background-position: 0 -6072px;
}

.<%= Style.COUNTRY_FLAG %>.ws {
	background-position: 0 -6096px;
}

.<%= Style.COUNTRY_FLAG %>.ar_YE,
.<%= Style.COUNTRY_FLAG %>.ye {
	background-position: 0 -6120px;
}

.<%= Style.COUNTRY_FLAG %>.yt {
	background-position: 0 -6144px;
}

.<%= Style.COUNTRY_FLAG %>.en_ZA,
.<%= Style.COUNTRY_FLAG %>.za {
	background-position: 0 -6168px;
}

.<%= Style.COUNTRY_FLAG %>.zm {
	background-position: 0 -6192px;
}

.<%= Style.COUNTRY_FLAG %>.zw {
	background-position: 0 -6216px;
}

/* Leaflet customization */
.marker-cluster, .prunecluster {
	color: white;
}

.marker-cluster-small, .prunecluster-small {
	background-color: rgba(124, 179, 218, 0.6);
}
.marker-cluster-small div, .prunecluster-small div {
	background-color: rgba(124, 179, 218, 0.8);
}

.marker-cluster-medium, .prunecluster-medium {
	background-color: rgba(62, 139, 193, 0.6);
}
.marker-cluster-medium div, .prunecluster-medium div {
	background-color: rgba(62, 139, 193, 0.8);
}

.marker-cluster-large, .prunecluster-large {
	background-color: rgba(15, 112, 182, 0.6);
}
.marker-cluster-large div, .prunecluster-large div {
	background-color: rgba(15, 112, 182, 0.8);
}

/* Responsive layout */
@media (min-width: 992px) {
	.<%= Style.LAYOUT_LOGO_SECTION %> img,
	.<%= Style.LAYOUT_LOGO_SECTION %> object {
		width: 100%;
	}
}

/* GWT customization */
.gwt-DatePicker {
	border: 1px solid #ccc;
	border-top:1px solid #999;
	cursor: default;
}
.gwt-DatePicker td,
.datePickerMonthSelector td:focus {
	outline: none;
}
.datePickerMonthSelector td:focus {
	outline: none;
}
.datePickerDays {
	width: 100%;
	background: white;
}
.datePickerDay,
.datePickerWeekdayLabel,
.datePickerWeekendLabel {
	font-size: 85%;
	text-align: center;
	padding: 4px;
	outline: none;
	font-weight:bold;
	color:#333;
	border-right: 1px solid #EDEDED;
	border-bottom: 1px solid #EDEDED;
}
.datePickerWeekdayLabel,
.datePickerWeekendLabel {
	background: #fff;
	padding: 0 4px 2px;
	cursor: default;
	color:#666;
	font-size:70%;
	font-weight:normal;
}
.datePickerDay {
	padding: 4px 7px;
	cursor: pointer;
}
.datePickerDayIsWeekend {
	background: #f7f7f7;
}
.datePickerDayIsFiller {
	color: #999;
	font-weight:normal;
}
.datePickerDayIsValue {
	background: <%= highlightColor %>;
}
.datePickerDayIsDisabled {
	color: #AAAAAA;
	font-style: italic;
}
.datePickerDayIsHighlighted {
	background: <%= highlightColor %>;
}
.datePickerDayIsValueAndHighlighted {
	background: #d7dfe8;
}
.datePickerDayIsToday {
	background: #d7dfe8;
}
.datePickerMonthSelector {
	width: 100%;
	padding: 1px 0 5px 0;
	background: #fff;
}
.datePickerPreviousButton,
.datePickerNextButton,
.datePickerPreviousYearButton,
.datePickerNextYearButton {
	font-size: 120%;
	line-height: 1em;
	color: #3a6aad;
	cursor: pointer;
	font-weight: bold;
	padding: 0 4px;
	outline: none;
}
td.datePickerMonth,
td.datePickerYear {
	text-align: center;
	vertical-align: middle;
	white-space: nowrap;
	font-size: 100%;
	font-weight: bold;
	color: #333;
}
.gwt-DateBox {
	padding: 5px 4px;
	border: 1px solid #ccc;
	border-top: 1px solid #999;
	font-size: 100%;
}
.gwt-DateBox input {
	width: 8em;
}
.dateBoxFormatError {
	background: #ffcccc;
}
.dateBoxPopup {
}

/* Add the styling for the separator */
.gwt-MenuItemSeparator {
	padding: 2px 0;
	cursor: default;
}
.gwt-MenuItemSeparator .menuSeparatorInner {
	height: 1px;
	padding: 0;
	border: 0;
	border-top: 1px solid #ccc;
	overflow: hidden;
}