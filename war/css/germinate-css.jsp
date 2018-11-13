<%@ page  contentType ="text/css; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jhi.germinate.server.service.*"%>
<%@ page import="jhi.germinate.server.watcher.*"%>
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
	String highlightColor = CommonServiceImpl.getColors(PropertyWatcher.get(ServerProperty.GERMINATE_TEMPLATE_CATEGORICAL_COLORS)).get(0);
%>

/* This is a hack to counteract the padding that is added when you look at the data statistics page. No idea how it's added, but it happens... */
body {
	padding-right: 0 !important;
}

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
#content-wrapper .well img {
	height: 50px;
}

.<%= Style.LAYOUT_TABLE_CELL_PADDING_PADDING %> {
	padding: 8px;
}

.<%= Style.LAYOUT_MARGIN_MAP %> {
	margin: 15px 0;
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

.<%= Style.CURSER_POINTER %> {
	cursor: pointer;
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

.<%= Style.COUNTRY_FLAG %> {
	background-size: contain;
	background-position: 50%;
	background-repeat: no-repeat;
	position: relative;
	display: inline-block;
	width: 1.733333em;
	line-height: 1.3em;
	margin-right: 5px;
}
.<%= Style.COUNTRY_FLAG %>:before {
	content: "\00a0";
}
.flag-icon.flag-icon-squared {
	width: 1em;
}

.<%= Style.COUNTRY_FLAG %>.su,
.<%= Style.COUNTRY_FLAG %>.unknown,
.<%= Style.COUNTRY_FLAG %>.un {
	background-image: url(../img/flags/un.svg);
}

.<%= Style.COUNTRY_FLAG %>.ad {
	background-image: url(../img/flags/ad.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_AE,
.<%= Style.COUNTRY_FLAG %>.ae {
	background-image: url(../img/flags/ae.svg);
}

.<%= Style.COUNTRY_FLAG %>.af {
	background-image: url(../img/flags/af.svg);
}

.<%= Style.COUNTRY_FLAG %>.ag {
	background-image: url(../img/flags/ag.svg);
}

.<%= Style.COUNTRY_FLAG %>.ai {
	background-image: url(../img/flags/ai.svg);
}

.<%= Style.COUNTRY_FLAG %>.sq_AL,
.<%= Style.COUNTRY_FLAG %>.al {
	background-image: url(../img/flags/al.svg);
}

.<%= Style.COUNTRY_FLAG %>.am {
	background-image: url(../img/flags/am.svg);
}

.<%= Style.COUNTRY_FLAG %>.an {
	background-image: url(../img/flags/an.svg);
}

.<%= Style.COUNTRY_FLAG %>.ao {
	background-image: url(../img/flags/ao.svg);
}

.<%= Style.COUNTRY_FLAG %>.aq {
	background-image: url(../img/flags/aq.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_AR,
.<%= Style.COUNTRY_FLAG %>.ar {
	background-image: url(../img/flags/ar.svg);
}

.<%= Style.COUNTRY_FLAG %>.as {
	background-image: url(../img/flags/as.svg);
}

.<%= Style.COUNTRY_FLAG %>.de_AT,
.<%= Style.COUNTRY_FLAG %>.at {
	background-image: url(../img/flags/at.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_AU,
.<%= Style.COUNTRY_FLAG %>.au {
	background-image: url(../img/flags/au.svg);
}

.<%= Style.COUNTRY_FLAG %>.aw {
	background-image: url(../img/flags/aw.svg);
}

.<%= Style.COUNTRY_FLAG %>.ax {
	background-image: url(../img/flags/ax.svg);
}

.<%= Style.COUNTRY_FLAG %>.az {
	background-image: url(../img/flags/az.svg);
}

.<%= Style.COUNTRY_FLAG %>.sr_BA,
.<%= Style.COUNTRY_FLAG %>.ba {
	background-image: url(../img/flags/ba.svg);
}

.<%= Style.COUNTRY_FLAG %>.bb {
	background-image: url(../img/flags/bb.svg);
}

.<%= Style.COUNTRY_FLAG %>.bd {
	background-image: url(../img/flags/bd.svg);
}

.<%= Style.COUNTRY_FLAG %>.nl_BE,
.<%= Style.COUNTRY_FLAG %>.fr_BE,
.<%= Style.COUNTRY_FLAG %>.be {
	background-image: url(../img/flags/be.svg);
}

.<%= Style.COUNTRY_FLAG %>.bf {
	background-image: url(../img/flags/bf.svg);
}

.<%= Style.COUNTRY_FLAG %>.bg_BG,
.<%= Style.COUNTRY_FLAG %>.bg {
	background-image: url(../img/flags/bg.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_BH,
.<%= Style.COUNTRY_FLAG %>.bh {
	background-image: url(../img/flags/bh.svg);
}

.<%= Style.COUNTRY_FLAG %>.bi {
	background-image: url(../img/flags/bi.svg);
}

.<%= Style.COUNTRY_FLAG %>.bj {
	background-image: url(../img/flags/bj.svg);
}

.<%= Style.COUNTRY_FLAG %>.bl {
	background-image: url(../img/flags/bl.svg);
}

.<%= Style.COUNTRY_FLAG %>.bm {
	background-image: url(../img/flags/mb.svg);
}

.<%= Style.COUNTRY_FLAG %>.bn {
	background-image: url(../img/flags/bn.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_BO,
.<%= Style.COUNTRY_FLAG %>.bo {
	background-image: url(../img/flags/bo.svg);
}

.<%= Style.COUNTRY_FLAG %>.pt_BR,
.<%= Style.COUNTRY_FLAG %>.br {
	background-image: url(../img/flags/br.svg);
}

.<%= Style.COUNTRY_FLAG %>.bs {
	background-image: url(../img/flags/bs.svg);
}

.<%= Style.COUNTRY_FLAG %>.bt {
	background-image: url(../img/flags/bt.svg);
}

.<%= Style.COUNTRY_FLAG %>.bw {
	background-image: url(../img/flags/bw.svg);
}

.<%= Style.COUNTRY_FLAG %>.be_BY,
.<%= Style.COUNTRY_FLAG %>.by {
	background-image: url(../img/flags/by.svg);
}

.<%= Style.COUNTRY_FLAG %>.bz {
	background-image: url(../img/flags/bz.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_CA,
.<%= Style.COUNTRY_FLAG %>.fr_CA,
.<%= Style.COUNTRY_FLAG %>.ca {
	background-image: url(../img/flags/ca.svg);
}

.<%= Style.COUNTRY_FLAG %>.cc {
	background-image: url(../img/flags/cc.svg);
}

.<%= Style.COUNTRY_FLAG %>.cd {
	background-image: url(../img/flags/cd.svg);
}

.<%= Style.COUNTRY_FLAG %>.cf {
	background-image: url(../img/flags/cf.svg);
}

.<%= Style.COUNTRY_FLAG %>.cg {
	background-image: url(../img/flags/cg.svg);
}

.<%= Style.COUNTRY_FLAG %>.fr_CH,
.<%= Style.COUNTRY_FLAG %>.de_CH,
.<%= Style.COUNTRY_FLAG %>.it_CH,
.<%= Style.COUNTRY_FLAG %>.ch {
	background-image: url(../img/flags/ch.svg);
}

.<%= Style.COUNTRY_FLAG %>.ci {
	background-image: url(../img/flags/ci.svg);
}

.<%= Style.COUNTRY_FLAG %>.ck {
	background-image: url(../img/flags/ck.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_CL,
.<%= Style.COUNTRY_FLAG %>.cl {
	background-image: url(../img/flags/cl.svg);
}

.<%= Style.COUNTRY_FLAG %>.cm {
	background-image: url(../img/flags/cm.svg);
}

.<%= Style.COUNTRY_FLAG %>.zh_CN,
.<%= Style.COUNTRY_FLAG %>.cn {
	background-image: url(../img/flags/cn.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_CO,
.<%= Style.COUNTRY_FLAG %>.co {
	background-image: url(../img/flags/co.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_CR,
.<%= Style.COUNTRY_FLAG %>.cr {
	background-image: url(../img/flags/cr.svg);
}

.<%= Style.COUNTRY_FLAG %>.cu {
	background-image: url(../img/flags/cu.svg);
}

.<%= Style.COUNTRY_FLAG %>.cv {
	background-image: url(../img/flags/cv.svg);
}

.<%= Style.COUNTRY_FLAG %>.cw {
	background-image: url(../img/flags/cw.svg);
}

.<%= Style.COUNTRY_FLAG %>.cx {
	background-image: url(../img/flags/cx.svg);
}

.<%= Style.COUNTRY_FLAG %>.el_CY,
.<%= Style.COUNTRY_FLAG %>.cy {
	background-image: url(../img/flags/cy.svg);
}

.<%= Style.COUNTRY_FLAG %>.cs_CZ,
.<%= Style.COUNTRY_FLAG %>.cz {
	background-image: url(../img/flags/cz.svg);
}

.<%= Style.COUNTRY_FLAG %>.de_DE,
.<%= Style.COUNTRY_FLAG %>.de {
	background-image: url(../img/flags/de.svg);
}

.<%= Style.COUNTRY_FLAG %>.dj {
	background-image: url(../img/flags/dj.svg);
}

.<%= Style.COUNTRY_FLAG %>.da_DK,
.<%= Style.COUNTRY_FLAG %>.dk {
	background-image: url(../img/flags/dk.svg);
}

.<%= Style.COUNTRY_FLAG %>.dm {
	background-image: url(../img/flags/dm.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_DO,
.<%= Style.COUNTRY_FLAG %>.do {
	background-image: url(../img/flags/do.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_DZ,
.<%= Style.COUNTRY_FLAG %>.dz {
	background-image: url(../img/flags/dz.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_EC,
.<%= Style.COUNTRY_FLAG %>.ec {
	background-image: url(../img/flags/ec.svg);
}

.<%= Style.COUNTRY_FLAG %>.et_EE,
.<%= Style.COUNTRY_FLAG %>.ee {
	background-image: url(../img/flags/ee.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_EG,
.<%= Style.COUNTRY_FLAG %>.eg {
	background-image: url(../img/flags/eg.svg);
}

.<%= Style.COUNTRY_FLAG %>.eh {
	background-image: url(../img/flags/eh.svg);
}

.<%= Style.COUNTRY_FLAG %>.er {
	background-image: url(../img/flags/er.svg);
}

.<%= Style.COUNTRY_FLAG %>.ca_ES,
.<%= Style.COUNTRY_FLAG %>.es_ES,
.<%= Style.COUNTRY_FLAG %>.es {
	background-image: url(../img/flags/es.svg);
}

.<%= Style.COUNTRY_FLAG %>.et {
	background-image: url(../img/flags/et.svg);
}

.<%= Style.COUNTRY_FLAG %>.eu {
	background-image: url(../img/flags/eu.svg);
}

.<%= Style.COUNTRY_FLAG %>.fi_FI,
.<%= Style.COUNTRY_FLAG %>.fi {
	background-image: url(../img/flags/fi.svg);
}

.<%= Style.COUNTRY_FLAG %>.fj {
	background-image: url(../img/flags/fj.svg);
}

.<%= Style.COUNTRY_FLAG %>.fk {
	background-image: url(../img/flags/fk.svg);
}

.<%= Style.COUNTRY_FLAG %>.fm {
	background-image: url(../img/flags/fm.svg);
}

.<%= Style.COUNTRY_FLAG %>.fo {
	background-image: url(../img/flags/fo.svg);
}

.<%= Style.COUNTRY_FLAG %>.fr_FR,
.<%= Style.COUNTRY_FLAG %>.fr {
	background-image: url(../img/flags/fr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ga {
	background-image: url(../img/flags/ga.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_GB,
.<%= Style.COUNTRY_FLAG %>.en,
.<%= Style.COUNTRY_FLAG %>.gb {
	background-image: url(../img/flags/gb.svg);
}

.<%= Style.COUNTRY_FLAG %>.gd {
	background-image: url(../img/flags/gd.svg);
}

.<%= Style.COUNTRY_FLAG %>.ge {
	background-image: url(../img/flags/ge.svg);
}

.<%= Style.COUNTRY_FLAG %>.gg {
	background-image: url(../img/flags/gg.svg);
}

.<%= Style.COUNTRY_FLAG %>.gh {
	background-image: url(../img/flags/gh.svg);
}

.<%= Style.COUNTRY_FLAG %>.gi {
	background-image: url(../img/flags/gi.svg);
}

.<%= Style.COUNTRY_FLAG %>.gl {
	background-image: url(../img/flags/gl.svg);
}

.<%= Style.COUNTRY_FLAG %>.gm {
	background-image: url(../img/flags/gm.svg);
}

.<%= Style.COUNTRY_FLAG %>.gn {
	background-image: url(../img/flags/gn.svg);
}

.<%= Style.COUNTRY_FLAG %>.gq {
	background-image: url(../img/flags/gq.svg);
}

.<%= Style.COUNTRY_FLAG %>.el_GR,
.<%= Style.COUNTRY_FLAG %>.gr {
	background-image: url(../img/flags/gr.svg);
}

.<%= Style.COUNTRY_FLAG %>.gs {
	background-image: url(../img/flags/gs.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_GT,
.<%= Style.COUNTRY_FLAG %>.gt {
	background-image: url(../img/flags/gt.svg);
}

.<%= Style.COUNTRY_FLAG %>.gu {
	background-image: url(../img/flags/gu.svg);
}

.<%= Style.COUNTRY_FLAG %>.gw {
	background-image: url(../img/flags/gw.svg);
}

.<%= Style.COUNTRY_FLAG %>.gy {
	background-image: url(../img/flags/gy.svg);
}

.<%= Style.COUNTRY_FLAG %>.zh_HK,
.<%= Style.COUNTRY_FLAG %>.hk {
	background-image: url(../img/flags/hk.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_HN,
.<%= Style.COUNTRY_FLAG %>.hn {
	background-image: url(../img/flags/hn.svg);
}

.<%= Style.COUNTRY_FLAG %>.hr_HR,
.<%= Style.COUNTRY_FLAG %>.hr {
	background-image: url(../img/flags/hr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ht {
	background-image: url(../img/flags/ht.svg);
}

.<%= Style.COUNTRY_FLAG %>.hu_HU,
.<%= Style.COUNTRY_FLAG %>.hu {
	background-image: url(../img/flags/hu.svg);
}

.<%= Style.COUNTRY_FLAG %>.ic {
	background-image: url(../img/flags/ic.svg);
}

.<%= Style.COUNTRY_FLAG %>.in_ID,
.<%= Style.COUNTRY_FLAG %>.id {
	background-image: url(../img/flags/id.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_IE,
.<%= Style.COUNTRY_FLAG %>.ga_IE,
.<%= Style.COUNTRY_FLAG %>.ie {
	background-image: url(../img/flags/ie.svg);
}

.<%= Style.COUNTRY_FLAG %>.iw_IL,
.<%= Style.COUNTRY_FLAG %>.il {
	background-image: url(../img/flags/il.svg);
}

.<%= Style.COUNTRY_FLAG %>.im {
	background-image: url(../img/flags/im.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_IN,
.<%= Style.COUNTRY_FLAG %>.hi_IN,
.<%= Style.COUNTRY_FLAG %>.in {
	background-image: url(../img/flags/in.svg);
}
.<%= Style.COUNTRY_FLAG %>.ar_IQ,
.<%= Style.COUNTRY_FLAG %>.iq {
	background-image: url(../img/flags/iq.svg);
}

.<%= Style.COUNTRY_FLAG %>.ir {
	background-image: url(../img/flags/ir.svg);
}

.<%= Style.COUNTRY_FLAG %>.is_IS,
.<%= Style.COUNTRY_FLAG %>.is {
	background-image: url(../img/flags/is.svg);
}

.<%= Style.COUNTRY_FLAG %>.it_IT,
.<%= Style.COUNTRY_FLAG %>.it {
	background-image: url(../img/flags/it.svg);
}

.<%= Style.COUNTRY_FLAG %>.je {
	background-image: url(../img/flags/je.svg);
}

.<%= Style.COUNTRY_FLAG %>.jm {
	background-image: url(../img/flags/jm.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_JO,
.<%= Style.COUNTRY_FLAG %>.jo {
	background-image: url(../img/flags/jo.svg);
}

.<%= Style.COUNTRY_FLAG %>.ja_JP,
.<%= Style.COUNTRY_FLAG %>.jp {
	background-image: url(../img/flags/jp.svg);
}

.<%= Style.COUNTRY_FLAG %>.ke {
	background-image: url(../img/flags/ke.svg);
}

.<%= Style.COUNTRY_FLAG %>.kg {
	background-image: url(../img/flags/kg.svg);
}

.<%= Style.COUNTRY_FLAG %>.kh {
	background-image: url(../img/flags/kh.svg);
}

.<%= Style.COUNTRY_FLAG %>.ki {
	background-image: url(../img/flags/ki.svg);
}

.<%= Style.COUNTRY_FLAG %>.km {
	background-image: url(../img/flags/km.svg);
}

.<%= Style.COUNTRY_FLAG %>.kn {
	background-image: url(../img/flags/kn.svg);
}

.<%= Style.COUNTRY_FLAG %>.kp {
	background-image: url(../img/flags/kp.svg);
}

.<%= Style.COUNTRY_FLAG %>.ko_KR,
.<%= Style.COUNTRY_FLAG %>.kr {
	background-image: url(../img/flags/kr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_KW,
.<%= Style.COUNTRY_FLAG %>.kw {
	background-image: url(../img/flags/kw.svg);
}

.<%= Style.COUNTRY_FLAG %>.ky {
	background-image: url(../img/flags/ky.svg);
}

.<%= Style.COUNTRY_FLAG %>.kz {
	background-image: url(../img/flags/kz.svg);
}

.<%= Style.COUNTRY_FLAG %>.la {
	background-image: url(../img/flags/la.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_LB,
.<%= Style.COUNTRY_FLAG %>.lb {
	background-image: url(../img/flags/lb.svg);
}

.<%= Style.COUNTRY_FLAG %>.lc {
	background-image: url(../img/flags/lc.svg);
}

.<%= Style.COUNTRY_FLAG %>.li {
	background-image: url(../img/flags/li.svg);
}

.<%= Style.COUNTRY_FLAG %>.lk {
	background-image: url(../img/flags/lk.svg);
}

.<%= Style.COUNTRY_FLAG %>.lr {
	background-image: url(../img/flags/lr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ls {
	background-image: url(../img/flags/ls.svg);
}

.<%= Style.COUNTRY_FLAG %>.lt_LT,
.<%= Style.COUNTRY_FLAG %>.lt {
	background-image: url(../img/flags/lt.svg);
}

.<%= Style.COUNTRY_FLAG %>.fr_LU,
.<%= Style.COUNTRY_FLAG %>.de_LU,
.<%= Style.COUNTRY_FLAG %>.lu {
	background-image: url(../img/flags/lu.svg);
}

.<%= Style.COUNTRY_FLAG %>.lv_LV,
.<%= Style.COUNTRY_FLAG %>.lv {
	background-image: url(../img/flags/lv.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_LY,
.<%= Style.COUNTRY_FLAG %>.ly {
	background-image: url(../img/flags/ly.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_MA,
.<%= Style.COUNTRY_FLAG %>.ma {
	background-image: url(../img/flags/ma.svg);
}

.<%= Style.COUNTRY_FLAG %>.mc {
	background-image: url(../img/flags/mc.svg);
}

.<%= Style.COUNTRY_FLAG %>.md {
	background-image: url(../img/flags/md.svg);
}

.<%= Style.COUNTRY_FLAG %>.sr_ME,
.<%= Style.COUNTRY_FLAG %>.me {
	background-image: url(../img/flags/me.svg);
}

.<%= Style.COUNTRY_FLAG %>.mf {
	background-image: url(../img/flags/mf.svg);
}

.<%= Style.COUNTRY_FLAG %>.mg {
	background-image: url(../img/flags/mg.svg);
}

.<%= Style.COUNTRY_FLAG %>.mh {
	background-image: url(../img/flags/mh.svg);
}

.<%= Style.COUNTRY_FLAG %>.mk_MK,
.<%= Style.COUNTRY_FLAG %>.mk {
	background-image: url(../img/flags/mk.svg);
}

.<%= Style.COUNTRY_FLAG %>.ml {
	background-image: url(../img/flags/ml.svg);
}

.<%= Style.COUNTRY_FLAG %>.mm {
	background-image: url(../img/flags/mm.svg);
}

.<%= Style.COUNTRY_FLAG %>.mn {
	background-image: url(../img/flags/mn.svg);
}

.<%= Style.COUNTRY_FLAG %>.mo {
	background-image: url(../img/flags/mo.svg);
}

.<%= Style.COUNTRY_FLAG %>.mp {
	background-image: url(../img/flags/mp.svg);
}

.<%= Style.COUNTRY_FLAG %>.mq {
	background-image: url(../img/flags/mq.svg);
}

.<%= Style.COUNTRY_FLAG %>.mr {
	background-image: url(../img/flags/mr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ms {
	background-image: url(../img/flags/ms.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_MT,
.<%= Style.COUNTRY_FLAG %>.mt_MT,
.<%= Style.COUNTRY_FLAG %>.mt {
	background-image: url(../img/flags/mt.svg);
}

.<%= Style.COUNTRY_FLAG %>.mu {
	background-image: url(../img/flags/mu.svg);
}

.<%= Style.COUNTRY_FLAG %>.mv {
	background-image: url(../img/flags/mv.svg);
}

.<%= Style.COUNTRY_FLAG %>.mw {
	background-image: url(../img/flags/mw.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_MX,
.<%= Style.COUNTRY_FLAG %>.mx {
	background-image: url(../img/flags/mx.svg);
}

.<%= Style.COUNTRY_FLAG %>.ms_MY,
.<%= Style.COUNTRY_FLAG %>.my {
	background-image: url(../img/flags/my.svg);
}

.<%= Style.COUNTRY_FLAG %>.mz {
	background-image: url(../img/flags/mz.svg);
}

.<%= Style.COUNTRY_FLAG %>.na {
	background-image: url(../img/flags/na.svg);
}

.<%= Style.COUNTRY_FLAG %>.nc {
	background-image: url(../img/flags/nc.svg);
}

.<%= Style.COUNTRY_FLAG %>.ne {
	background-image: url(../img/flags/ne.svg);
}

.<%= Style.COUNTRY_FLAG %>.nf {
	background-image: url(../img/flags/nf.svg);
}

.<%= Style.COUNTRY_FLAG %>.ng {
	background-image: url(../img/flags/ng.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_NI,
.<%= Style.COUNTRY_FLAG %>.ni {
	background-image: url(../img/flags/ni.svg);
}

.<%= Style.COUNTRY_FLAG %>.nl_NL,
.<%= Style.COUNTRY_FLAG %>.nl {
	background-image: url(../img/flags/nl.svg);
}

.<%= Style.COUNTRY_FLAG %>.no_NO,
.<%= Style.COUNTRY_FLAG %>.nb_NO,
.<%= Style.COUNTRY_FLAG %>.nn_NO,
.<%= Style.COUNTRY_FLAG %>.no {
	background-image: url(../img/flags/no.svg);
}

.<%= Style.COUNTRY_FLAG %>.np {
	background-image: url(../img/flags/np.svg);
}

.<%= Style.COUNTRY_FLAG %>.nr {
	background-image: url(../img/flags/nr.svg);
}

.<%= Style.COUNTRY_FLAG %>.nu {
	background-image: url(../img/flags/nu.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_NZ,
.<%= Style.COUNTRY_FLAG %>.nz {
	background-image: url(../img/flags/nz.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_OM,
.<%= Style.COUNTRY_FLAG %>.om {
	background-image: url(../img/flags/om.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_PA,
.<%= Style.COUNTRY_FLAG %>.pa {
	background-image: url(../img/flags/pa.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_PE,
.<%= Style.COUNTRY_FLAG %>.pe {
	background-image: url(../img/flags/pe.svg);
}

.<%= Style.COUNTRY_FLAG %>.pf {
	background-image: url(../img/flags/pf.svg);
}

.<%= Style.COUNTRY_FLAG %>.pg {
	background-image: url(../img/flags/pg.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_PH,
.<%= Style.COUNTRY_FLAG %>.ph {
	background-image: url(../img/flags/ph.svg);
}

.<%= Style.COUNTRY_FLAG %>.pk {
	background-image: url(../img/flags/pk.svg);
}

.<%= Style.COUNTRY_FLAG %>.pl_PL,
.<%= Style.COUNTRY_FLAG %>.pl {
	background-image: url(../img/flags/pl.svg);
}

.<%= Style.COUNTRY_FLAG %>.pn {
	background-image: url(../img/flags/pn.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_PR,
.<%= Style.COUNTRY_FLAG %>.pr {
	background-image: url(../img/flags/pr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ps {
	background-image: url(../img/flags/ps.svg);
}

.<%= Style.COUNTRY_FLAG %>.pt_PT,
.<%= Style.COUNTRY_FLAG %>.pt {
	background-image: url(../img/flags/pt.svg);
}

.<%= Style.COUNTRY_FLAG %>.pw {
	background-image: url(../img/flags/pw.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_PY,
.<%= Style.COUNTRY_FLAG %>.py {
	background-image: url(../img/flags/py.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_QA,
.<%= Style.COUNTRY_FLAG %>.qa {
	background-image: url(../img/flags/qa.svg);
}

.<%= Style.COUNTRY_FLAG %>.ro_RO,
.<%= Style.COUNTRY_FLAG %>.ro {
	background-image: url(../img/flags/ro.svg);
}

.<%= Style.COUNTRY_FLAG %>.sr_RS,
.<%= Style.COUNTRY_FLAG %>.rs {
	background-image: url(../img/flags/rs.svg);
}

.<%= Style.COUNTRY_FLAG %>.ru_RU,
.<%= Style.COUNTRY_FLAG %>.ru {
	background-image: url(../img/flags/ru.svg);
}

.<%= Style.COUNTRY_FLAG %>.rw {
	background-image: url(../img/flags/rw.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_SA,
.<%= Style.COUNTRY_FLAG %>.sa {
	background-image: url(../img/flags/sa.svg);
}

.<%= Style.COUNTRY_FLAG %>.sb {
	background-image: url(../img/flags/sb.svg);
}

.<%= Style.COUNTRY_FLAG %>.sc {
	background-image: url(../img/flags/sc.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_SD,
.<%= Style.COUNTRY_FLAG %>.sd {
	background-image: url(../img/flags/sd.svg);
}

.<%= Style.COUNTRY_FLAG %>.sv_SE,
.<%= Style.COUNTRY_FLAG %>.se {
	background-image: url(../img/flags/se.svg);
}

.<%= Style.COUNTRY_FLAG %>.zh_SG,
.<%= Style.COUNTRY_FLAG %>.en_SG,
.<%= Style.COUNTRY_FLAG %>.sg {
	background-image: url(../img/flags/sg.svg);
}

.<%= Style.COUNTRY_FLAG %>.sh {
	background-image: url(../img/flags/sh.svg);
}

.<%= Style.COUNTRY_FLAG %>.sl_SI,
.<%= Style.COUNTRY_FLAG %>.si {
	background-image: url(../img/flags/si.svg);
}

.<%= Style.COUNTRY_FLAG %>.sk_SK,
.<%= Style.COUNTRY_FLAG %>.sk {
	background-image: url(../img/flags/sk.svg);
}

.<%= Style.COUNTRY_FLAG %>.sl {
	background-image: url(../img/flags/sl.svg);
}

.<%= Style.COUNTRY_FLAG %>.sm {
	background-image: url(../img/flags/sm.svg);
}

.<%= Style.COUNTRY_FLAG %>.sn {
	background-image: url(../img/flags/sn.svg);
}

.<%= Style.COUNTRY_FLAG %>.so {
	background-image: url(../img/flags/so.svg);
}

.<%= Style.COUNTRY_FLAG %>.sr {
	background-image: url(../img/flags/sr.svg);
}

.<%= Style.COUNTRY_FLAG %>.ss {
	background-image: url(../img/flags/ss.svg);
}

.<%= Style.COUNTRY_FLAG %>.st {
	background-image: url(../img/flags/st.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_SV,
.<%= Style.COUNTRY_FLAG %>.sv {
	background-image: url(../img/flags/sv.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_SY,
.<%= Style.COUNTRY_FLAG %>.sy {
	background-image: url(../img/flags/sy.svg);
}

.<%= Style.COUNTRY_FLAG %>.sz {
	background-image: url(../img/flags/sz.svg);
}

.<%= Style.COUNTRY_FLAG %>.tc {
	background-image: url(../img/flags/tc.svg);
}

.<%= Style.COUNTRY_FLAG %>.td {
	background-image: url(../img/flags/td.svg);
}

.<%= Style.COUNTRY_FLAG %>.tf {
	background-image: url(../img/flags/tf.svg);
}

.<%= Style.COUNTRY_FLAG %>.tg {
	background-image: url(../img/flags/tg.svg);
}

.<%= Style.COUNTRY_FLAG %>.th_TH,
.<%= Style.COUNTRY_FLAG %>.th {
	background-image: url(../img/flags/th.svg);
}

.<%= Style.COUNTRY_FLAG %>.tj {
	background-image: url(../img/flags/tj.svg);
}

.<%= Style.COUNTRY_FLAG %>.tk {
	background-image: url(../img/flags/tk.svg);
}

.<%= Style.COUNTRY_FLAG %>.tl {
	background-image: url(../img/flags/tl.svg);
}

.<%= Style.COUNTRY_FLAG %>.tm {
	background-image: url(../img/flags/tm.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_TN,
.<%= Style.COUNTRY_FLAG %>.tn {
	background-image: url(../img/flags/tn.svg);
}

.<%= Style.COUNTRY_FLAG %>.to {
	background-image: url(../img/flags/to.svg);
}

.<%= Style.COUNTRY_FLAG %>.tr_TR,
.<%= Style.COUNTRY_FLAG %>.tr {
	background-image: url(../img/flags/tr.svg);
}

.<%= Style.COUNTRY_FLAG %>.tt {
	background-image: url(../img/flags/tt.svg);
}

.<%= Style.COUNTRY_FLAG %>.tv {
	background-image: url(../img/flags/tv.svg);
}

.<%= Style.COUNTRY_FLAG %>.zh_TW,
.<%= Style.COUNTRY_FLAG %>.tw {
	background-image: url(../img/flags/tw.svg);
}

.<%= Style.COUNTRY_FLAG %>.tz {
	background-image: url(../img/flags/tz.svg);
}

.<%= Style.COUNTRY_FLAG %>.uk_UA,
.<%= Style.COUNTRY_FLAG %>.ua {
	background-image: url(../img/flags/ua.svg);
}

.<%= Style.COUNTRY_FLAG %>.ug {
	background-image: url(../img/flags/ug.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_US,
.<%= Style.COUNTRY_FLAG %>.es_US,
.<%= Style.COUNTRY_FLAG %>.us {
	background-image: url(../img/flags/us.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_UY,
.<%= Style.COUNTRY_FLAG %>.uy {
	background-image: url(../img/flags/uy.svg);
}

.<%= Style.COUNTRY_FLAG %>.uz {
	background-image: url(../img/flags/uz.svg);
}

.<%= Style.COUNTRY_FLAG %>.va {
	background-image: url(../img/flags/va.svg);
}

.<%= Style.COUNTRY_FLAG %>.vc {
	background-image: url(../img/flags/vc.svg);
}

.<%= Style.COUNTRY_FLAG %>.es_VE,
.<%= Style.COUNTRY_FLAG %>.ve {
	background-image: url(../img/flags/ve.svg);
}

.<%= Style.COUNTRY_FLAG %>.vg {
	background-image: url(../img/flags/vg.svg);
}

.<%= Style.COUNTRY_FLAG %>.vi {
	background-image: url(../img/flags/vi.svg);
}

.<%= Style.COUNTRY_FLAG %>.vi_VN,
.<%= Style.COUNTRY_FLAG %>.vn {
	background-image: url(../img/flags/vn.svg);
}

.<%= Style.COUNTRY_FLAG %>.vu {
	background-image: url(../img/flags/vu.svg);
}

.<%= Style.COUNTRY_FLAG %>.wf {
	background-image: url(../img/flags/wf.svg);
}

.<%= Style.COUNTRY_FLAG %>.ws {
	background-image: url(../img/flags/ws.svg);
}

.<%= Style.COUNTRY_FLAG %>.ar_YE,
.<%= Style.COUNTRY_FLAG %>.ye {
	background-image: url(../img/flags/ye.svg);
}

.<%= Style.COUNTRY_FLAG %>.yt {
	background-image: url(../img/flags/yt.svg);
}

.<%= Style.COUNTRY_FLAG %>.en_ZA,
.<%= Style.COUNTRY_FLAG %>.za {
	background-image: url(../img/flags/za.svg);
}

.<%= Style.COUNTRY_FLAG %>.zm {
	background-image: url(../img/flags/zm.svg);
}

.<%= Style.COUNTRY_FLAG %>.zw {
	background-image: url(../img/flags/zw.svg);
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




/* Make sure printing works ok */
@media print {
	body * {
		visibility: hidden !important;
	}
	.<%= Style.PRINT_SECTION %>, .<%= Style.PRINT_SECTION %> * {
		visibility: visible !important;
		width: auto !important;
		height: auto !important;
		overflow: visible !important;
		max-height: unset !important;
		max-width: unset !important;
	}
	.<%= Style.PRINT_SECTION %> {
		position: absolute !important;
		left: 0 !important;
		top: 0 !important;
	}
}