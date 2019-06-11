/*
 *  Copyright 2019 Information and Computational Sciences,
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

package jhi.germinate.client.widget.table.pagination.cell;

import com.google.gwt.cell.client.*;
import com.google.gwt.safehtml.shared.*;

/**
 * This {@link AbstractCell} renders a help icon which, on hover, displays a tooltip with help information.
 *
 * @author Sebastian Raubach
 */
public class HelpCell extends AbstractCell<String>
{
	private String help;

	/**
	 * Creates a new {@link HelpCell}. Uses the given help text to display it in a tooltip.
	 *
	 * @param help The text to display in the help tooltip.
	 */
	public HelpCell(String help)
	{
		super();

		this.help = help;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb)
	{
		// Create a bootstrap tooltip on a span using MDI's 'help-circle' and bootstraps 'text-muted' color.
		sb.append(SafeHtmlUtils.fromSafeConstant("<span>" + value + "</span> <span class='mdi mdi-help-circle text-muted' data-toggle='tooltip' data-trigger='click' data-placement='bottom' title='" + help + "'></span>"));
	}
}
