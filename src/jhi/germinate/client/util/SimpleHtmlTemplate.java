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

package jhi.germinate.client.util;

import com.google.gwt.core.client.*;
import com.google.gwt.safecss.shared.*;
import com.google.gwt.safehtml.client.*;
import com.google.gwt.safehtml.shared.*;

import jhi.germinate.shared.*;

/**
 * {@link SimpleHtmlTemplate} is a class containing methods to create various HTML contents.
 *
 * @author Sebastian Raubach
 */
public interface SimpleHtmlTemplate extends SafeHtmlTemplates
{
	SimpleHtmlTemplate INSTANCE = GWT.create(SimpleHtmlTemplate.class);

	/**
	 * Creates an anchor with the given url and text (opens in new tab)
	 *
	 * @param href The link url
	 * @param name The link text
	 * @return An anchor with the given url and text (opens in new tab)
	 */
	@Template("<a href='{0}' target='_blank' style='word-break: break-word;'>{1}</a>")
	SafeHtml anchorNewTab(SafeUri href, String name);

	/**
	 * Creates an anchor with the given url and text (opens in new tab)
	 *
	 * @param href The link url
	 * @param name The link text
	 * @return An anchor with the given url and text (opens in new tab)
	 */
	@Template("<a href='{0}' target='_blank' style='word-break: break-word;' title='{1}'>{2}</a>")
	SafeHtml anchorNewTabTruncated(SafeUri href, String text, String truncated);

	/**
	 * Creates an anchor with the given url and text
	 *
	 * @param href The link url
	 * @param name The link text
	 * @return An anchor with the given url and text
	 */
	@Template("<a href='{0}' style='word-break: break-word;'>{1}</a>")
	SafeHtml anchor(SafeUri href, String name);

	/**
	 * Creates an anchor with the given url and text
	 *
	 * @param href The link url
	 * @param name The link text
	 * @return An anchor with the given url and text
	 */
	@Template("<a href='{0}' title='{1}' style='word-break: break-word;'>{2}</a>")
	SafeHtml anchorTruncated(SafeUri href, String text, String truncated);

	/**
	 * Creates an anchor with the given url and text
	 *
	 * @param href The link url
	 * @param name The link text
	 * @return An anchor with the given url and text
	 */
	@Template("<span><a href='{0}' style='word-break: break-word;'>{1}</a><span class='{1}'></span></span>")
	SafeHtml anchorWithIcon(SafeUri href, String name, String style);

	/**
	 * Creates an anchor with the given url and text
	 *
	 * @param href The link url
	 * @param name The link text
	 * @return An anchor with the given url and text
	 */
	@Template("<div class='" + Style.LAYOUT_TABLE_CELL_PADDING_PADDING + "'><a href='{0}' style='word-break: break-word;'>{1}</a></div>")
	SafeHtml anchorPadded(SafeUri href, String name);

	/**
	 * Creates a simple text
	 *
	 * @param name The text to display
	 * @return A simple text
	 */
	@Template("{0}")
	SafeHtml text(String name);

	/**
	 * Creates a simple text
	 *
	 * @param name The text to display
	 * @return A simple text
	 */
	@Template("<span title='{0}'>{1}</span>")
	SafeHtml textTruncated(String text, String truncated);

	/**
	 * Creates a simple text
	 *
	 * @param name The text to display
	 * @return A simple text
	 */
	@Template("<span><span>{0}</span><span class='{1}'></span></span>")
	SafeHtml textWithIcon(String name, String style);

	/**
	 * Creates a simple text
	 *
	 * @param name The text to display
	 * @return A simple text
	 */
	@Template("<div class='" + Style.LAYOUT_TABLE_CELL_PADDING_PADDING + "'><span>{0}</span></div>")
	SafeHtml textPadded(String name);

	/**
	 * Creates an empty <code>&lt;span&gt;</code> element
	 *
	 * @return An empty span
	 */
	@Template("<span>&nbsp;</span>")
	SafeHtml empty();

	/**
	 * Creates a <code>&lt;span&gt;</code> element with the given Material Design Icon style and the given title attribute
	 *
	 * @param mdi   The MaterialDesignIcon style
	 * @param title The title attribute
	 * @return A <code>&lt;span&gt;</code> element with the given Material Design Icon style and the given title attribute
	 */
	@Template("<span class='" + Style.MDI + " " + Style.MDI_LG + " " + Style.FA_FIXED_WIDTH + " {0}' style='vertical-align: middle' title='{1}'></span></a>")
	SafeHtml materialIconFixedWidth(String mdi, String title);

	/**
	 * Creates an anchor with the given link, target, title and Material Design Icon style
	 *
	 * @param mdi    The MaterialDesignIcon style
	 * @param title  The title attribuge
	 * @param href   The link url
	 * @param target The target
	 * @return An anchor with the given link, target, title and Material Design Icon style
	 */
	@Template("<a href='{2}' target ='{3}' style='word-break: break-word;'><span class='" + Style.MDI + " " + Style.MDI_LG + " " + Style.FA_FIXED_WIDTH + " {0}' style='vertical-align: middle' title='{1}'></span></a>")
	SafeHtml materialIconAnchor(String mdi, String title, SafeUri href, String target);

	/**
	 * Creates an anchor with the given link, target, title and Material Design Icon style
	 *
	 * @param mdi    The MaterialDesignIcon style
	 * @param text   The title attribuge
	 * @param href   The link url
	 * @param target The target
	 * @return An anchor with the given link, target, title and Material Design Icon style
	 */
	@Template("<span><a href='{3}' target ='{4}' style='word-break: break-word;'>{1}</a><a href='#'><span class='" + Style.MDI + " " + Style.MDI_LG + " " + Style.FA_FIXED_WIDTH + " {0}' style='margin-left: 5px; vertical-align: middle' title='{2}'></span></a></span>")
	SafeHtml materialIconAnchorWithText(String mdi, String text, String iconTitle, SafeUri href, String target);

	/**
	 * Creates an anchor with the given link, target, title and Material Design Icon style
	 *
	 * @param mdi  The MaterialDesignIcon style
	 * @param text The title attribuge
	 * @return An anchor with the given link, target, title and Material Design Icon style
	 */
	@Template("<span><span>{1}</span><a href='#'><span class='" + Style.MDI + " " + Style.MDI_LG + " " + Style.FA_FIXED_WIDTH + " {0}' style='margin-left: 5px; vertical-align: middle' title='{2}'></span></a></span>")
	SafeHtml materialIconWithText(String mdi, String text, String iconTitle);

	/**
	 * Creates a div with the given background color, text color and content
	 *
	 * @param backgroundColor The background color
	 * @param textColor       The text color
	 * @param content         The actual content
	 * @return A div with the given background color, text color and content
	 */
	@Template("<div class='" + Style.LAYOUT_TABLE_CELL_PADDING_PADDING + " right-align' style='{0}{1}'>{2}</div>")
	SafeHtml color(SafeStyles backgroundColor, SafeStyles textColor, String content);

	/**
	 * Creates a div with the given background color, text color and content
	 *
	 * @param backgroundColor The background color
	 * @param textColor       The text color
	 * @param content         The actual content
	 * @return A div with the given background color, text color and content
	 */
	@Template("<div class='" + Style.LAYOUT_TABLE_CELL_PADDING_PADDING + " right-align' style='{0}{1}'>{2}</div>")
	SafeHtml color(SafeStyles backgroundColor, SafeStyles textColor, SafeHtml content);

	/**
	 * Creates a div with the given background color and a space symbol as its content
	 *
	 * @param backgroundColor The background color
	 * @return A div with the given background color and a space symbol as its content
	 */
	@Template("<div class='gradient-legend' style='{0}'>&nbsp;</div>")
	SafeHtml gradientLegendHorizontal(SafeStyles backgroundColor);

	/**
	 * Creates a span pretending to be a link (color and cursor)
	 *
	 * @param name The anchor text
	 * @return A span pretending to be a link (color and cursor)
	 */
	@Template("<span class='" + Style.TAG_DUMMY_ANCHOR + " text-primary'>{0}</span>")
	SafeHtml dummyAnchor(String name);

	/**
	 * Creates a span pretending to be a link (color and cursor)
	 *
	 * @param name The anchor text
	 * @return A span pretending to be a link (color and cursor)
	 */
	@Template("<span><span class='" + Style.TAG_DUMMY_ANCHOR + " text-primary'>{0}</span><span class='{1}'></span></span>")
	SafeHtml dummyAnchorWithIcon(String name, String iconStyle);

	/**
	 * Creates a span element with the given text and Material Design Icon styling
	 *
	 * @param mdi The Material Design Icon style
	 * @param text        The text
	 * @return A span element with the given text and Material Design Icon styling
	 */
	@Template("<span class='text-primary " + Style.MDI + " " + Style.FA_FIXED_WIDTH + " " + Style.MDI_LG + " " + Style.LAYOUT_V_ALIGN_MIDDLE
			+ " {0}'></span><span class='" + Style.LAYOUT_V_ALIGN_MIDDLE + "' style='margin-left: 5px; white-space: nowrap;'>{1}</span>")
	SafeHtml contextMenuItemMaterialIcon(String mdi, String text);

	/**
	 * Creates a 'mailto' anchor with the given email address.
	 *
	 * @param email The email address
	 * @return A 'mailto' anchor with the given email address.
	 */
	@Template("<a href='mailto:{0}'>{0}</a>")
	SafeHtml mailto(String email);

	@Template("<span class=" + Style.TABLE_PEITY_DONUT + "><span class='donut' style='display: none;'>{1}/{2}</span><span>&nbsp;{0}</span></span>")
	SafeHtml peityDonut(String formatted, double value, int outOf);
}
