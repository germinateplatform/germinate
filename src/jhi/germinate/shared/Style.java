/*
 *  Copyright 2017 Information and Computational Sciences,
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

package jhi.germinate.shared;

import java.util.*;
import java.util.stream.*;

/**
 * @author Sebastian Raubach
 */
public class Style
{
	public static final String COUNTRY_FLAG = "gm8-country-flag";

	public static final String LAYOUT_NEWS_LIST                  = "gm8-news-list";
	public static final String LAYOUT_NEWS_PANEL                 = "gm8-news-panel";
	public static final String LAYOUT_NEWS_LIST_ITEM_BODY        = "gm8-news-list-item-body";
	public static final String LAYOUT_V_ALIGN_MIDDLE             = "gm8-v-align-middle";
	public static final String LAYOUT_WHITE_SPACE_NO_WRAP        = "gm8-white-space-no-wrap";
	public static final String LAYOUT_NO_PADDING                 = "gm8-layout-no-padding";
	public static final String LAYOUT_NO_MARGIN                  = "gm8-layout-no-margin";
	public static final String LAYOUT_NO_MARGIN_TOP              = "gm8-layout-no-margin-top";
	public static final String LAYOUT_OVERFLOW_X_AUTO            = "gm8-overflow-x-auto";
	public static final String LAYOUT_CLEAR_BOTH                 = "gm8-clear-both";
	public static final String LAYOUT_DISPLAY_INLINE_BLOCK       = "gm8-display-inline-block";
	public static final String LAYOUT_DISPLAY_NONE               = "gm8-display-none";
	public static final String LAYOUT_LOGO_SECTION               = "gm8-logo-section";
	public static final String LAYOUT_BUTTON_MARGIN              = "gm8-button-margin";
	public static final String LAYOUT_TABLE_CELL_PADDING_PADDING = "gm8-table-cell-padding";
	public static final String LAYOUT_SELECT_BUTTON_COMBO        = "gm8-select-button-combo";
	public static final String LAYOUT_FLOAT_INITIAL              = "gm8-float-initial";
	public static final String LAYOUT_NO_BORDER_LEFT             = "gm8-no-border-left";
	public static final String LAYOUT_SIDEBAR_TOGGLED            = "gm8-sidebar-toggled";

	public static final String FORM_REQUIRED = "form-required";

	public static final String TAG_DUMMY_ANCHOR = "gm8-tag-dummy-anchor";

	public static final String WIDGET_BUSY_INDICATOR = "gm8-widget-busy-indicator";

	public static final String WIDGET_ICON_BUTTON = "gm8-widget-icon-button";

	public static final String WIDGET_MAP_STATIC_OVERLAY = "gm8-map-static-overlay";

	public static final String WIDGET_UL_ICON_LIST = "gm8-ul-icon-list";

	public static final String MAPS_PANEL = "gm8-maps-panel";

	public static final String TABLE_BORDER               = "gm8-table-border";
	public static final String TABLE_CONTROL_PANEL        = "gm8-table-control-panel";
	public static final String TABLE_CONTROL_PANEL_TOP    = TABLE_CONTROL_PANEL + " top";
	public static final String TABLE_CONTROL_PANEL_BOTTOM = TABLE_CONTROL_PANEL + " bottom";

	public static final String LAYOUT_VERTICAL_INPUT_GROUP = "gm8-vertical-input-group";

	public static final String TEXT_BOLD         = "gm8-text-bold";
	public static final String TEXT_ITALIC       = "gm8-text-italic";
	public static final String TEXT_RIGHT_ALIGN  = "gm8-text-right-align";
	public static final String TEXT_CENTER_ALIGN = "gm8-text-center-align";

	public static final String CURSOR_DEFAULT = "gm8-cursor-default";

	public static final String NO_POINTER_EVENTS = "gm8-no-pointer-events";

	public static final String STATE_ACTIVE = "active";

	public static final String PRETTY_PRINT     = "prettyprint";
	public static final String PRETTY_PRINT_SQL = "lang-sql";

	public static final String TEXT_FORMAT_HUGE = "gm8-text-format-huge";

	public static final String FA                                  = "fa";
	public static final String FA_FIXED_WIDTH                      = "fa-fw";
	public static final String FA_LG                               = "fa-lg";
	public static final String MDI_24PX                            = "mdi-24px";
	public static final String MDI_48PX                            = "mdi-48px";
	public static final String MDI                                 = "mdi";
	public static final String MDI_HOME                            = "mdi-home";
	public static final String MDI_FLOWER                          = "mdi-flower";
	public static final String MDI_DATABASE                        = "mdi-database";
	public static final String MDI_CHART_AREASPLINE                = "mdi-chart-areaspline";
	public static final String MDI_EARTH                           = "mdi-earth";
	public static final String MDI_WEATHER_SNOWY_RAIN              = "mdi-weather-snowy-rainy";
	public static final String MDI_GROUP                           = "mdi-group";
	public static final String MDI_IMAGE_MULTIPLE                  = "mdi-image-multiple";
	public static final String MDI_HARDDISK                        = "mdi-harddisk";
	public static final String MDI_DNA                             = "mdi-dna";
	public static final String MDI_MAP_MARKER                      = "mdi-map-marker";
	public static final String MDI_TWITTER_BOX                     = "mdi-twitter-box";
	public static final String MDI_FACEBOOK_BOX                    = "mdi-facebook-box";
	public static final String MDI_GOOGLE_PLUS_BOX                 = "mdi-google-plus-box";
	public static final String MDI_NATURE_PEOPLE                   = "mdi-nature-people";
	public static final String MDI_INFORMATION                     = "mdi-information";
	public static final String MDI_INFORMATION_OUTLINE             = "mdi-information-outline";
	public static final String MDI_TAG_MULTIPLE                    = "mdi-tag-multiple";
	public static final String MDI_FLASK                           = "mdi-flask";
	public static final String MDI_SHOVEL                          = "mdi-shovel";
	public static final String MDI_PULSE                           = "mdi-pulse";
	public static final String MDI_ATOM                            = "mdi-atom";
	public static final String MDI_REORDER_VERTICAL                = "mdi-reorder-vertical";
	public static final String MDI_GOOGLE_MAPS                     = "mdi-google-maps";
	public static final String MDI_VIEW_QUILT                      = "mdi-view-quilt";
	public static final String MDI_VIEW_LIST                       = "mdi-view-list";
	public static final String MDI_CROSSHAIRS_GPS                  = "mdi-crosshairs-gps";
	public static final String MDI_BULLETIN_BOARD                  = "mdi-bulletin-board";
	public static final String MDI_NEWSPAPER                       = "mdi-newspaper";
	public static final String MDI_REFRESH                         = "mdi-refresh";
	public static final String MDI_LOGOUT                          = "mdi-logout";
	public static final String MDI_LOGIN_VARIANT                   = "mdi-login-variant";
	public static final String MDI_LOGOUT_VARIANT                  = "mdi-logout-variant";
	public static final String MDI_SETTINGS                        = "mdi-settings";
	public static final String MDI_EYE_OFF                         = "mdi-eye-off";
	public static final String MDI_LOCK_OPEN                       = "mdi-lock-open";
	public static final String MDI_LOCK                            = "mdi-lock";
	public static final String MDI_DOWNLOAD                        = "mdi-download";
	public static final String MDI_CHECKBOX_BLANK_OUTLINE          = "mdi-checkbox-blank-outline";
	public static final String MDI_CHECKBOX_MULTIPLE_BLANK_OUTLINE = "mdi-checkbox-multiple-blank-outline";
	public static final String MDI_CHECKBOX_MARKED                 = "mdi-checkbox-marked";
	public static final String MDI_CHECKBOX_MULTIPLE_MARKED        = "mdi-checkbox-multiple-marked";
	public static final String MDI_FILE_IMAGE                      = "mdi-file-image";
	public static final String MDI_FILE_XML                        = "mdi-file-xml";
	public static final String MDI_FILE_DOCUMENT                   = "mdi-file-document";
	public static final String MDI_SITEMAP                         = "mdi-sitemap";
	public static final String MDI_ACCOUNT                         = "mdi-account";
	public static final String MDI_ACCOUNT_SETTINGS_VARIANT        = "mdi-account-settings-variant";
	public static final String MDI_ACCOUNT_MULTIPLE                = "mdi-account-multiple";
	public static final String MDI_MAGNIFY                         = "mdi-magnify";
	public static final String MDI_LINK_VARIANT                    = "mdi-link-variant";
	public static final String MDI_NEW_BOX                         = "mdi-new-box";
	public static final String MDI_CHECK                           = "mdi-check";
	public static final String MDI_DELETE                          = "mdi-delete";
	public static final String MDI_FILE_PLUS                       = "mdi-file-plus";
	public static final String MDI_RENAME_BOX                      = "mdi-rename-box";
	public static final String MDI_FILE_EXCEL                      = "mdi-file-excel";
	public static final String MDI_FILE_PDF                        = "mdi-file-pdf";
	public static final String MDI_GOOGLE_EARTH                    = "mdi-google-earth";
	public static final String MDI_PLUS_BOX                        = "mdi-plus-box";
	public static final String MDI_HELP_CIRCLE_OUTLINE             = "mdi-help-circle-outline";
	public static final String MDI_FILTER                          = "mdi-filter";
	public static final String MDI_ARROW_RIGHT_BOLD_CIRCLE         = "mdi-arrow-right-bold-circle";
	public static final String MDI_ARROW_LEFT_BOLD_CIRCLE          = "mdi-arrow-left-bold-circle";
	public static final String MDI_CLOCK                           = "mdi-clock";
	public static final String MDI_TRANSLATE                       = "mdi-translate";
	public static final String MDI_EMAIL                           = "mdi-email";
	public static final String MDI_EYE                             = "mdi-eye";
	public static final String MDI_CHART_SCATTERPLOT_HEXBIN        = "mdi-chart-scatterplot-hexbin";
	public static final String MDI_VIEW_GRID                       = "mdi-view-grid";
	public static final String MDI_WEB                             = "mdi-web";
	public static final String MDI_GITHUB_CIRCLE                   = "mdi-github-circle";
	public static final String MDI_PLAY                            = "mdi-play";
	public static final String MDI_GLASSES                         = "mdi-glasses";
	public static final String MDI_CANCEL                          = "mdi-cancel";
	public static final String MDI_CHECK_CIRCLE                    = "mdi-check-circle";
	public static final String MDI_UPLOAD                          = "mdi-upload";
	public static final String MDI_ALERT                           = "mdi-alert";
	public static final String MDI_PLUS_BOX_OUTLINE                = "mdi-plus-box-outline";
	public static final String MDI_CHEVRON_LEFT                    = "mdi-chevron-left";
	public static final String MDI_CHEVRON_DOUBLE_LEFT             = "mdi-chevron-double-left";
	public static final String MDI_CHEVRON_RIGHT                   = "mdi-chevron-right";
	public static final String MDI_CHEVRON_DOUBLE_RIGHT            = "mdi-chevron-double-right";
	public static final String CHECKBOX_MULTIPLE_MARKED_OUTLINE    = "mdi-checkbox-multiple-marked-outline";
	public static final String MDI_PENCIL_BOX_OUTLINE              = "mdi-pencil-box-outline";
	public static final String MDI_ARROW_LEFT_BOLD                 = "mdi-arrow-left-bold";
	public static final String MDI_ARROW_RIGHT_BOLD                = "mdi-arrow-right-bold";
	public static final String MDI_VIEW_COLUMN                     = "mdi-view-column";
	public static final String MDI_CONTENT_SAVE                    = "mdi-content-save";
	public static final String MDI_CONTENT_PASTE                   = "mdi-content-paste";
	public static final String MDI_FORMAT_ALIGN_JUSTIFY            = "mdi-format-align-justify";
	public static final String MDI_FORMAT_ALIGN_LEFT               = "mdi-format-align-left";
	public static final String MDI_ROTATE_90                       = "mdi-rotate-90";
	public static final String MDI_ROTATE_270                      = "mdi-rotate-270";
	public static final String MDI_FORMAT_INDENT_INCREASE          = "mdi-format-indent-increase";
	public static final String MDI_VECTOR_POLYGON                  = "mdi-vector-polygon";
	public static final String MDI_TIMER_SAND                      = "mdi-timer-sand";
	public static final String MDI_PASSPORT                        = "mdi-passport";
	public static final String MDI_CITY                            = "mdi-city";
	public static final String MDI_SELECTION                       = "mdi-selection";
	public static final String MDI_COMMENT_TEXT_OUTLINE            = "mdi-comment-text-outline";
	public static final String MDI_LG                              = "mdi-lg";
	public static final String MDI_PLAYLIST_PLUS                   = "mdi-playlist-plus";
	public static final String BOOTSTRAP_DROPDOWN_ALERT            = "dropdown-alerts";
	public static final String COL_XXS_12                          = "col-xxs-12";

	/**
	 * Returns the combined style string
	 *
	 * @param styles The styles to combine
	 * @return The combined string
	 */
	public static String combine(String... styles)
	{
		if (styles.length == 0)
			return "";
		else
		{
			return Arrays.stream(styles)
						 .filter(s -> !StringUtils.isEmpty(s))
						 .collect(Collectors.joining(" "));
		}
	}

	public static String mdi(String style)
	{
		return combine(MDI, style);
	}

	public static String mdiLg(String style)
	{
		return combine(MDI, style, MDI_LG);
	}
}
